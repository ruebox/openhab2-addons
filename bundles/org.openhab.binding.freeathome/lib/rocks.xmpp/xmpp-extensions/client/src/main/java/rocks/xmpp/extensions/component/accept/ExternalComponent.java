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

package rocks.xmpp.extensions.component.accept;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.net.client.ClientConnectionConfiguration;
import rocks.xmpp.core.net.client.SocketConnectionConfiguration;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.session.XmppSessionConfiguration;
import rocks.xmpp.core.session.model.SessionOpen;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.core.stanza.model.Stanza;
import rocks.xmpp.core.stream.model.StreamElement;
import rocks.xmpp.extensions.component.accept.model.ComponentIQ;
import rocks.xmpp.extensions.component.accept.model.ComponentMessage;
import rocks.xmpp.extensions.component.accept.model.ComponentPresence;
import rocks.xmpp.extensions.component.accept.model.Handshake;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * An external component session which connects to an XMPP server using the "Jabber Component Protocol".
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0114.html">XEP-0114: Jabber Component Protocol</a>
 */
public final class ExternalComponent extends XmppSession {

    private static final Logger logger = Logger.getLogger(ExternalComponent.class.getName());

    private volatile CompletableFuture<SessionOpen> streamOpened;

    private volatile CompletableFuture<Void> handshakeReceived;

    private final String sharedSecret;

    private volatile Jid connectedResource;

    private ExternalComponent(String componentName, String sharedSecret, XmppSessionConfiguration configuration, ClientConnectionConfiguration connectionConfiguration) {
        super(componentName, configuration, connectionConfiguration);
        this.sharedSecret = sharedSecret;
    }

    /**
     * Creates a new external component using a default configuration. Any registered {@link #addCreationListener(Consumer) creation listeners} are triggered.
     *
     * @param componentName The component name.
     * @param sharedSecret  The shared secret (password).
     * @param hostname      The hostname to connect to.
     * @param port          The port to connect to.
     * @return The external component.
     */
    public static ExternalComponent create(String componentName, String sharedSecret, String hostname, int port) {
        return create(componentName, sharedSecret, XmppSessionConfiguration.getDefault(), hostname, port);
    }

    /**
     * Creates a new external component. Any registered {@link #addCreationListener(Consumer) creation listeners} are triggered.
     *
     * @param componentName The component name.
     * @param sharedSecret  The shared secret (password).
     * @param configuration The configuration.
     * @param hostname      The hostname to connect to.
     * @param port          The port to connect to.
     * @return The external component.
     */
    public static ExternalComponent create(String componentName, String sharedSecret, XmppSessionConfiguration configuration, String hostname, int port) {
        return create(componentName, sharedSecret, configuration, SocketConnectionConfiguration.builder().hostname(hostname).port(port).build());
    }

    /**
     * Creates a new external component using a default configuration. Any registered {@link #addCreationListener(Consumer) creation listeners} are triggered.
     *
     * @param componentName            The component name.
     * @param sharedSecret             The shared secret (password).
     * @param xmppSessionConfiguration The XMPP configuration.
     * @param connectionConfiguration  The connection configuration.
     * @return The external component.
     */
    public static ExternalComponent create(String componentName, String sharedSecret, XmppSessionConfiguration xmppSessionConfiguration, ClientConnectionConfiguration connectionConfiguration) {
        ExternalComponent component = new ExternalComponent(componentName, sharedSecret, xmppSessionConfiguration, connectionConfiguration);
        notifyCreationListeners(component);
        return component;
    }

    @Override
    public final void connect(Jid from) throws XmppException {
        Status previousStatus = preConnect();

        try {
            if (!checkConnected()) {
                // Don't call listeners from within synchronized blocks to avoid possible deadlocks.

                updateStatus(Status.CONNECTING);
                synchronized (this) {
                    streamOpened = new CompletableFuture<>();
                    // Double-checked locking: Recheck connected status. In a multi-threaded environment multiple threads could have passed the first check.
                    if (!checkConnected()) {
                        // Reset
                        exception = null;

                        tryConnect(from, "jabber:component:accept", "1.0");
                        logger.fine("Negotiating stream, waiting until handshake is ready to be negotiated.");
                        SessionOpen sessionOpen = streamOpened.get(configuration.getDefaultResponseTimeout().toMillis(), TimeUnit.MILLISECONDS);

                        // Check if the server returned a stream error, e.g. conflict.
                        throwAsXmppExceptionIfNotNull(exception);

                        if (sessionOpen != null && sessionOpen.getVersion() != null) {
                            streamFeaturesManager.completeNegotiation().get(configuration.getDefaultResponseTimeout().toMillis() * 2, TimeUnit.MILLISECONDS);
                        } else {
                            // Wait shortly to see if the server will respond with a <conflict/>, <host-unknown/> or other stream error.
                            Thread.sleep(50);
                        }

                        connectedResource = getDomain();
                    }
                }
            }
            throwAsXmppExceptionIfNotNull(exception);
            // Don't call listeners from within synchronized blocks to avoid possible deadlocks.
            updateStatus(Status.CONNECTING, Status.CONNECTED);
            login(sharedSecret);
        } catch (Throwable e) {
            onConnectionFailed(previousStatus, e);
        }
    }

    /**
     * Authenticates with the server using a shared secret.
     *
     * @param sharedSecret The shared secret.
     * @throws XmppException If authentication failed.
     */
    private void login(String sharedSecret) throws XmppException {
        Status previousStatus = preLogin();

        try {
            if (checkAuthenticated()) {
                // Silently return, when we are already authenticated.
                return;
            }
            updateStatus(Status.AUTHENTICATING);
            synchronized (this) {
                handshakeReceived = new CompletableFuture<>();
                if (checkAuthenticated()) {
                    // Silently return, when we are already authenticated.
                    return;
                }
                // Send the <handshake/> element.
                send(Handshake.create(getActiveConnection().getStreamId(), sharedSecret));
                // Wait for the <handshake/> element to be received from the server.
                handshakeReceived.get(configuration.getDefaultResponseTimeout().toMillis(), TimeUnit.MILLISECONDS);
            }
            // Authentication succeeded, update the status.
            updateStatus(Status.AUTHENTICATED);
            // Check if the server returned a stream error, e.g. not-authorized and throw it.
            throwAsXmppExceptionIfNotNull(exception);
            afterLogin();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Revert status
            updateStatus(previousStatus, e);
            throwAsXmppExceptionIfNotNull(e);
        } catch (Throwable e) {
            // Revert status
            updateStatus(previousStatus, e);
            throwAsXmppExceptionIfNotNull(e);
        }

    }

    @Override
    public final boolean handleElement(Object element) throws XmppException {
        boolean doRestart = false;
        if (element instanceof Handshake) {
            releaseLock();
        } else {
            doRestart = super.handleElement(element);
        }
        if (element instanceof SessionOpen) {
            CompletableFuture<SessionOpen> future = streamOpened;
            if (future != null) {
                future.complete((SessionOpen) element);
                streamOpened = null;
            }
        }
        return doRestart;
    }

    @Override
    public final void notifyException(Throwable e) {
        releaseLock();
        super.notifyException(e);
    }

    private void releaseLock() {
        CompletableFuture<SessionOpen> future = streamOpened;
        if (future != null) {
            future.complete(null);
            streamOpened = null;
        }
        CompletableFuture<Void> future2 = handshakeReceived;
        if (future2 != null) {
            future2.complete(null);
            handshakeReceived = null;
        }
    }

    @Override
    public final Jid getConnectedResource() {
        return connectedResource;
    }

    @Override
    protected final StreamElement prepareElement(StreamElement element) {

        if (element instanceof Stanza && ((Stanza) element).getFrom() == null) {
            ((Stanza) element).setFrom(connectedResource);
        }
        if (element instanceof Message) {
            element = ComponentMessage.from((Message) element);
        } else if (element instanceof Presence) {
            element = ComponentPresence.from((Presence) element);
        } else if (element instanceof IQ) {
            element = ComponentIQ.from((IQ) element);
        }

        return element;
    }
}
