/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 Christian Schudt
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
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

/**
 * The client implementation of the SCRAM-SHA-1 SASL mechanism.
 * <p>
 * This class is not thread-safe.
 *
 * @author Christian Schudt
 * @see <a href="http://tools.ietf.org/html/rfc5802">Salted Challenge Response Authentication Mechanism (SCRAM)</a>
 */
public final class ScramClient extends ScramBase implements SaslClient {

    private static final String GS2_CBIND_FLAG = "n";

    private final String gs2Header;

    String username;

    private String authorizationId;

    private char[] passwd;

    private byte[] serverSignature;

    public ScramClient(String hashAlgorithm, String authorizationId, CallbackHandler callbackHandler) {
        super(hashAlgorithm, callbackHandler);

        // authzID can only be encoded in UTF8 - RFC 2222
        if (authorizationId != null) {
            this.authorizationId = new String(authorizationId.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        }
        this.gs2Header = GS2_CBIND_FLAG + ',' + (authorizationId != null ? "a=" + authorizationId : "") + ',';
    }

    /**
     * The characters ',' or '=' in usernames are sent as '=2C' and
     * '=3D' respectively.
     *
     * @param username The username.
     * @return The replaced username.
     */
    static String replaceUsername(String username) {
        if (username != null) {
            return username.replace("=", "=3D").replace(",", "=2C");
        }
        return null;
    }

    @Override
    public final boolean hasInitialResponse() {
        // Nothing in SCRAM prevents either sending
        // the client-first message with the SASL authentication request defined
        // by an application protocol ("initial client response")
        return true;
    }

    @Override
    public final byte[] evaluateChallenge(byte[] challenge) throws SaslException {

        // Initial response
        if (challenge.length == 0) {
            NameCallback ncb = authorizationId == null ?
                    new NameCallback("SCRAM username: ") :
                    new NameCallback("SCRAM username: ", authorizationId);
            PasswordCallback pcb = new PasswordCallback("SCRAM-SHA-1 password: ", false);
            try {
                callbackHandler.handle(new Callback[]{ncb, pcb});
                passwd = pcb.getPassword();
                pcb.clearPassword();
                username = ncb.getName();
                if (passwd == null || username == null) {
                    throw new SaslException("SCRAM: Username and password must not be null.");
                }

                // Before sending the username to the server, the client SHOULD
                // prepare the username using the "SASLprep" profile [RFC4013] of
                // the "stringprep" algorithm [RFC3454] treating it as a query
                // string (i.e., unassigned Unicode code points are allowed).
                username = SaslPrep.prepare(username);

                // If the preparation of the username fails or results in an empty
                // string, the client SHOULD abort the authentication exchange.
                if ("".equals(username)) {
                    throw new SaslException("SCRAM: Username must not be empty.");
                }
                username = replaceUsername(username);
                String cnonce = generateNonce();
                clientFirstMessageBare = createClientFirstMessageBare(username, cnonce);

                // First, the client sends the "client-first-message"
                String clientFirstMessage = gs2Header + clientFirstMessageBare;
                return clientFirstMessage.getBytes(StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new SaslException("SCRAM: Error acquiring user name or password.", e);
            } catch (UnsupportedCallbackException e) {
                throw new SaslException("SCRAM: Cannot perform callback to acquire username or password", e);
            } catch (NoSuchAlgorithmException e) {
                throw new SaslException("SCRAM: Failed to generate nonce.", e);
            }
        } else {

            try {
                // The server sends the salt and the iteration count to the client, which then computes
                // the following values and sends a ClientProof to the server
                String serverMessage = new String(challenge, StandardCharsets.UTF_8);
                Map<Character, String> attributes = getAttributes(serverMessage);

                // e: This attribute specifies an error that occurred during
                // authentication exchange.  It is sent by the server in its final
                // message and can help diagnose the reason for the authentication
                // exchange failure.
                String error = attributes.get('e');
                if (error != null) {
                    throw new SaslException(error);
                }

                // v: This attribute specifies a base64-encoded ServerSignature.  It
                // is sent by the server in its final message, and is used by the
                // client to verify that the server has access to the user's
                // authentication information.
                String verifier = attributes.get('v');
                if (verifier != null) {
                    // The client then authenticates the server by computing the
                    // ServerSignature and comparing it to the value sent by the server.  If
                    // the two are different, the client MUST consider the authentication
                    // exchange to be unsuccessful, and it might have to drop the
                    // connection.
                    if (!Arrays.equals(serverSignature, DatatypeConverter.parseBase64Binary(verifier))) {
                        throw new SaslException("SCRAM: Verification failed");
                    }
                    complete = true;
                    return null;
                }

                serverFirstMessage = serverMessage;
                nonce = attributes.get('r');

                String saltBase64 = attributes.get('s');
                int iterationCount;
                try {
                    iterationCount = Integer.parseInt(attributes.get('i'));
                } catch (NumberFormatException e) {
                    throw new SaslException("iterationCount could not be parsed.");
                }

                if (nonce == null) {
                    throw new SaslException("SCRAM: nonce was null in the server response.");
                }
                if (saltBase64 == null) {
                    throw new SaslException("SCRAM: salt was null in the server response.");
                }

                byte[] salt = DatatypeConverter.parseBase64Binary(saltBase64);

                try {
                    channelBinding = DatatypeConverter.printBase64Binary(gs2Header.getBytes(StandardCharsets.UTF_8));
                    byte[] saltedPassword = computeSaltedPassword(passwd, salt, iterationCount);
                    String authMessage = computeAuthMessage();
                    byte[] clientKey = computeClientKey(saltedPassword);
                    byte[] clientSignature = computeClientSignature(clientKey, computeAuthMessage());
                    // ClientProof     := ClientKey XOR ClientSignature
                    byte[] clientProof = xor(clientKey, clientSignature);
                    byte[] serverKey = computeServerKey(saltedPassword);
                    serverSignature = hmac(serverKey, authMessage.getBytes(StandardCharsets.UTF_8));
                    String clientFinalMessageWithoutProof = "c=" + channelBinding + ",r=" + nonce;
                    // The client then responds by sending a "client-final-message" with the
                    // same nonce and a ClientProof computed using the selected hash
                    // function as explained earlier.
                    String clientFinalMessage = clientFinalMessageWithoutProof + ",p=" + DatatypeConverter.printBase64Binary(clientProof);
                    return clientFinalMessage.getBytes(StandardCharsets.UTF_8);

                } catch (GeneralSecurityException e) {
                    throw new SaslException(e.getMessage(), e);
                }
            } catch (SaslException e) {
                complete = true;
                throw e;
            }
        }
    }
}
