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

package rocks.xmpp.core.session;

import rocks.xmpp.core.sasl.AuthenticationException;
import rocks.xmpp.core.sasl.XmppSaslClientFactory;
import rocks.xmpp.core.sasl.model.Auth;
import rocks.xmpp.core.sasl.model.Challenge;
import rocks.xmpp.core.sasl.model.Failure;
import rocks.xmpp.core.sasl.model.Mechanisms;
import rocks.xmpp.core.sasl.model.Response;
import rocks.xmpp.core.sasl.model.Success;
import rocks.xmpp.core.stream.StreamNegotiationException;
import rocks.xmpp.core.stream.StreamNegotiationResult;
import rocks.xmpp.core.stream.client.ClientStreamFeatureNegotiator;

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages SASL authentication as described in <a href="https://xmpp.org/rfcs/rfc6120.html#sasl">SASL Negotiation</a>.
 * <p>
 * This class is thread-safe.
 *
 * @author Christian Schudt
 */
final class AuthenticationManager extends ClientStreamFeatureNegotiator<Mechanisms> {

    private static final Logger logger = Logger.getLogger(AuthenticationManager.class.getName());

    static {
        // The SunSASL Provider only supports: "PLAIN", "CRAM-MD5", "DIGEST-MD5", "GSSAPI", "EXTERNAL".
        // http://download.java.net/jdk8/docs/technotes/guides/security/sasl/sasl-refguide.html

        // Add the "ANONYMOUS" and "SCRAM-SHA-1" SASL mechanism.
        Security.addProvider(new XmppProvider());
    }

    /**
     * Stores the supported and preferred SASL mechanisms of the server.
     */
    private final List<String> supportedMechanisms;

    /**
     * The SASL client which is used during an authentication process.
     * Guarded by "this".
     */
    private SaslClient saslClient;

    /**
     * The additional data returned by the {@code <success/>} element.
     * Guarded by "this".
     */
    private byte[] successData;

    /**
     * Creates the authentication manager. Usually only the {@link rocks.xmpp.core.session.XmppSession} should create it implicitly.
     *
     * @param xmppSession The session.
     */
    AuthenticationManager(final XmppSession xmppSession) {
        super(xmppSession, Mechanisms.class);
        this.supportedMechanisms = new ArrayList<>();
    }

    /**
     * @param mechanisms      The mechanisms to use.
     * @param authorizationId The authorization identity.
     * @param callbackHandler The callback handler.
     * @throws StreamNegotiationException If an exception occurred, while starting authentication.
     */
    final void startAuthentication(Collection<String> mechanisms, String authorizationId, CallbackHandler callbackHandler) throws StreamNegotiationException {
        synchronized (this) {
            try {
                Collection<String> clientMechanisms = new ArrayDeque<>(mechanisms);

                // Retain only the server-supported mechanisms.
                clientMechanisms.retainAll(supportedMechanisms);

                if (clientMechanisms.isEmpty()) {
                    throw new StreamNegotiationException("Server doesn't support any of the requested SASL mechanisms: " + mechanisms + ".");
                }
                successData = null;
                saslClient = Sasl.createSaslClient(clientMechanisms.toArray(new String[clientMechanisms.size()]), authorizationId, "xmpp", xmppSession.getDomain().toString(), Collections.emptyMap(), callbackHandler);

                if (saslClient == null) {
                    throw new SaslException("No SASL client found for mechanisms: " + clientMechanisms);
                }

                byte[] initialResponse = null;
                if (saslClient.hasInitialResponse()) {
                    initialResponse = saslClient.evaluateChallenge(new byte[0]);
                }

                xmppSession.send(new Auth(saslClient.getMechanismName(), initialResponse));
            } catch (SaslException e) {
                throw new StreamNegotiationException(e);
            }
        }
    }

    @Override
    public final synchronized StreamNegotiationResult processNegotiation(Object element) throws StreamNegotiationException {
        try {
            if (element instanceof Mechanisms) {
                supportedMechanisms.clear();
                supportedMechanisms.addAll(((Mechanisms) element).getMechanisms());
            } else if (element instanceof Challenge) {
                xmppSession.send(new Response(saslClient.evaluateChallenge(((Challenge) element).getValue())));
            } else if (element instanceof Failure) {
                Failure authenticationFailure = (Failure) element;
                String failureText = saslClient.getMechanismName() + " authentication failed with condition " + authenticationFailure.toString();
                if (authenticationFailure.getText() != null) {
                    failureText += " (" + authenticationFailure.getText() + ')';
                }
                throw new AuthenticationException(failureText, authenticationFailure);
            } else if (element instanceof Success) {
                successData = ((Success) element).getAdditionalData();
                if (!saslClient.isComplete()) {
                    saslClient.evaluateChallenge(successData);
                }
                try {
                    saslClient.dispose();
                    saslClient = null;
                } catch (SaslException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }
                return StreamNegotiationResult.RESTART;
            }
        } catch (SaslException e) {
            AuthenticationException authenticationException = new AuthenticationException(e.getMessage());
            try {
                if (saslClient != null) {
                    saslClient.dispose();
                    saslClient = null;
                }
            } catch (SaslException disposeException) {
                authenticationException.addSuppressed(disposeException);
            }
            throw authenticationException;
        }

        return StreamNegotiationResult.INCOMPLETE;
    }

    @Override
    public final boolean canProcess(Object element) {
        return element instanceof Challenge || element instanceof Failure || element instanceof Success;
    }

    synchronized byte[] getSuccessData() {
        return successData;
    }

    private static final class XmppProvider extends Provider {

        XmppProvider() {
            super("XMPP Sasl Provider", 1.0, "Provides additional SASL mechanisms, which are required for XMPP.");
            put("SaslClientFactory.ANONYMOUS", XmppSaslClientFactory.class.getName());
            put("SaslClientFactory.SCRAM-SHA-1", XmppSaslClientFactory.class.getName());
        }
    }
}
