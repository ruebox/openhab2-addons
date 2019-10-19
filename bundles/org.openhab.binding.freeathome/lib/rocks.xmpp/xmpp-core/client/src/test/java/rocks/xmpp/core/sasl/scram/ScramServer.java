/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Christian Schudt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package rocks.xmpp.core.sasl.scram;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The server implementation of the SCRAM-SHA-1 SASL mechanism.
 * <p>
 * This class is not thread-safe.
 *
 * @author Christian Schudt
 * @see <a href="http://tools.ietf.org/html/rfc5802">Salted Challenge Response Authentication Mechanism (SCRAM)</a>
 */
final class ScramServer extends ScramBase implements SaslServer {

    private static final int ITERATION_COUNT = 4096;

    private static final Pattern USER_VALIDATION = Pattern.compile("=(?!2C|3D)");

    private char[] password;

    private byte[] salt;

    private String authorizationId;

    public ScramServer(String hashAlgorithm, CallbackHandler callbackHandler) {
        super(hashAlgorithm, callbackHandler);
    }

    static String validateAndGetUsername(String userAttribute) throws SaslException {
        // If the server receives a username that
        // contains '=' not followed by either '2C' or '3D', then the
        // server MUST fail the authentication.

        Matcher matcher = USER_VALIDATION.matcher(userAttribute);
        if (matcher.find()) {
            throw new SaslException("Username must not contain '=' not followed by '2C' or '3D'.");
        }
        return userAttribute.replaceAll("=3D", "=").replaceAll("=2C", ",");
    }

    @Override
    public final byte[] evaluateResponse(byte[] response) throws SaslException {

        String clientMessage = new String(response, StandardCharsets.UTF_8);
        Map<Character, String> attributes = getAttributes(clientMessage);
        String cnonce = attributes.get('r');

        if (cnonce == null) {
            throw new SaslException("SCRAM: No nonce found in client message.");
        }

        // If no client message has been received yet.
        if (clientFirstMessageBare == null) {

            // Note that the client's first message will always start with "n", "y",
            // or "p"; otherwise, the message is invalid and authentication MUST fail.
            if (!clientMessage.startsWith("n") && !clientMessage.startsWith("y") && !clientMessage.startsWith("p")) {
                throw new SaslException("SCRAM: Client first message must start with n, y or p.");
            }

            String user = attributes.get('n');

            if (user == null) {
                throw new SaslException("SCRAM: No user found in client first message.");
            }

            // Upon receipt of the username by the server, the server MUST
            // either prepare it using the "SASLprep" profile [RFC4013] of the
            // "stringprep" algorithm [RFC3454] treating it as a query string
            // (i.e., unassigned Unicode codepoints are allowed) or otherwise
            // be prepared to do SASLprep-aware string comparisons and/or
            // index lookups.  If the preparation of the username fails or
            // results in an empty string, the server SHOULD abort the authentication exchange.

            user = validateAndGetUsername(SaslPrep.prepare(user));

            // The syntax of this field is the same as that of the "n" field
            // with respect to quoting of '=' and ','.
            authorizationId = validateAndGetUsername(attributes.get('a'));


            // Retrieve the password for the user.
            NameCallback ncb = new NameCallback("SCRAM username: ", user);
            ncb.setName(user);
            PasswordCallback pcb = new PasswordCallback("SCRAM password: ", false);
            try {
                callbackHandler.handle(new Callback[]{ncb, pcb});
            } catch (IOException | UnsupportedCallbackException e) {
                throw new SaslException("SCRAM: Error retrieving password.");
            }
            password = pcb.getPassword();
            pcb.clearPassword();

            // Generate salt.
            final Random r = new SecureRandom();
            salt = new byte[32];
            r.nextBytes(salt);

            try {
                nonce = (cnonce + generateNonce());
            } catch (NoSuchAlgorithmException e) {
                throw new SaslException();
            }

            clientFirstMessageBare = createClientFirstMessageBare(user, cnonce);

            // In response, the server sends a "server-first-message" containing the
            // user's iteration count i and the user's salt, and appends its own
            // nonce to the client-specified one.
            serverFirstMessage = "r=" + nonce + ",s=" + DatatypeConverter.printBase64Binary(salt) + ",i=" + ITERATION_COUNT;
            return serverFirstMessage.getBytes(StandardCharsets.UTF_8);

        } else {

            // The server verifies the nonce and the proof
            if (!cnonce.equals(nonce)) {
                throw new SaslException("SCRAM: Client provided invalid nonce.");
            }

            String clientProofBase64 = attributes.get('p');
            if (clientProofBase64 == null) {
                throw new SaslException("SCRAM: Client provided no client proof.");
            }

            channelBinding = attributes.get('c');
            if (channelBinding == null) {
                throw new SaslException("SCRAM: Client provided no channel-binding.");
            }

            // The server authenticates the client by computing the ClientSignature,
            // exclusive-ORing that with the ClientProof to recover the ClientKey
            // and verifying the correctness of the ClientKey by applying the hash
            // function and comparing the result to the StoredKey.  If the ClientKey
            // is correct, this proves that the client has access to the user's
            // password.
            try {
                byte[] saltedPassword = computeSaltedPassword(password, salt, ITERATION_COUNT);
                byte[] clientKey = computeClientKey(saltedPassword);
                String authMessage = computeAuthMessage();
                byte[] clientSignature = computeClientSignature(clientKey, authMessage);
                byte[] clientProof = DatatypeConverter.parseBase64Binary(clientProofBase64);
                byte[] recoveredClientKey = xor(clientSignature, clientProof);
                if (Arrays.equals(h(recoveredClientKey), computeStoredKey(clientKey))) {
                    complete = true;
                    byte[] serverKey = hmac(saltedPassword, "Server Key".getBytes(StandardCharsets.UTF_8));
                    // return ServerSignature
                    String serverFinalMessage = "v=" + DatatypeConverter.printBase64Binary(hmac(serverKey, authMessage.getBytes(StandardCharsets.UTF_8)));
                    return serverFinalMessage.getBytes(StandardCharsets.UTF_8);
                } else {
                    // On failed authentication, the entire server-
                    // final-message is OPTIONAL; specifically, a server implementation
                    // MAY conclude the SASL exchange with a failure without sending the
                    // server-final-message.  This results in an application-level error
                    // response without an extra round-trip.

                    // In XMPP we don't want to return a server-final-message, but instead return a XMPP SASL failure.
                    // Therefore throw an exception.
                    throw new SaslException("SCRAM authentication failed.");
                }

            } catch (InvalidKeyException | NoSuchAlgorithmException e) {
                throw new SaslException("SCRAM", e);
            }
        }
    }

    @Override
    public final String getAuthorizationID() {
        return authorizationId;
    }
}
