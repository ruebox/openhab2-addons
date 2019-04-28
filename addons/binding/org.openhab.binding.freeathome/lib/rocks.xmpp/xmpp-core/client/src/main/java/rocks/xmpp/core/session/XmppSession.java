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

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.net.Connection;
import rocks.xmpp.core.net.client.ClientConnectionConfiguration;
import rocks.xmpp.core.net.client.SocketConnectionConfiguration;
import rocks.xmpp.core.sasl.AuthenticationException;
import rocks.xmpp.core.session.debug.XmppDebugger;
import rocks.xmpp.core.session.model.SessionOpen;
import rocks.xmpp.core.stanza.IQEvent;
import rocks.xmpp.core.stanza.IQHandler;
import rocks.xmpp.core.stanza.MessageEvent;
import rocks.xmpp.core.stanza.PresenceEvent;
import rocks.xmpp.core.stanza.model.ExtensibleStanza;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.IQ.Type;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.core.stanza.model.Stanza;
import rocks.xmpp.core.stanza.model.StanzaErrorException;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.core.stream.StreamHandler;
import rocks.xmpp.core.stream.StreamNegotiationException;
import rocks.xmpp.core.stream.client.StreamFeaturesManager;
import rocks.xmpp.core.stream.model.StreamElement;
import rocks.xmpp.core.stream.model.StreamError;
import rocks.xmpp.core.stream.model.StreamErrorException;
import rocks.xmpp.core.stream.model.StreamFeatures;
import rocks.xmpp.core.stream.model.StreamHeader;
import rocks.xmpp.extensions.caps.EntityCapabilitiesManager;
import rocks.xmpp.extensions.delay.model.DelayedDelivery;
import rocks.xmpp.extensions.disco.ServiceDiscoveryManager;
import rocks.xmpp.extensions.httpbind.BoshConnectionConfiguration;
import rocks.xmpp.extensions.sm.StreamManager;
import rocks.xmpp.util.XmppUtils;
import rocks.xmpp.util.concurrent.AsyncResult;
import rocks.xmpp.util.concurrent.CompletionStages;
import rocks.xmpp.util.concurrent.QueuedExecutorService;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The base class for different kinds of XMPP sessions.
 * <p>
 * To date there are three kinds of sessions:
 * </p>
 * <ul>
 * <li>A normal client-to-server session. This is the default and most used XMPP session. It's concrete implementation is the {@link XmppClient}.</li>
 * <li>An external component session (<a href="https://xmpp.org/extensions/xep-0114.html">XEP-0114: Jabber Component Protocol</a>).</li>
 * <li>A client-to-client session (<a href="https://xmpp.org/extensions/xep-0174.html">XEP-0174: Serverless Messaging</a>) (no implementation yet).</li>
 * </ul>
 * <p>
 * This class provides the common functionality and abstract methods for connection establishment, sending and receiving XML stanzas, closing the session, etc.
 * </p>
 * Concrete implementations may have different concepts for authentication, e.g. normal C2S sessions use SASL, while the Jabber Component Protocol uses a different kind of handshake for authenticating.
 *
 * @author Christian Schudt
 * @see XmppClient
 * @see <a href="https://xmpp.org/extensions/xep-0114.html">XEP-0114: Jabber Component Protocol</a>
 * @see <a href="https://xmpp.org/extensions/xep-0174.html">XEP-0174: Serverless Messaging</a>
 */
public abstract class XmppSession implements StreamHandler, AutoCloseable {

    protected static final Collection<Consumer<XmppSession>> creationListeners = new CopyOnWriteArraySet<>();

    private static final Logger logger = Logger.getLogger(XmppSession.class.getName());

    private static final EnumSet<Status> IS_CONNECTED = EnumSet.of(Status.CONNECTED, Status.AUTHENTICATED, Status.AUTHENTICATING);

    private static final ExecutorService IQ_HANDLER_EXECUTOR = Executors.newCachedThreadPool(XmppUtils.createNamedThreadFactory("IQ Handler Thread"));

    private static final ExecutorService STANZA_LISTENER_EXECUTOR = Executors.newCachedThreadPool(XmppUtils.createNamedThreadFactory("Stanza Listener Thread"));

    protected final XmppSessionConfiguration configuration;

    protected final ServiceDiscoveryManager serviceDiscoveryManager;

    protected final StreamFeaturesManager streamFeaturesManager;

    final Set<Consumer<ConnectionEvent>> connectionListeners = new CopyOnWriteArraySet<>();

    /**
     * The XMPP domain.
     */
    private final Jid xmppServiceDomain;

    private final List<ClientConnectionConfiguration> connectionConfigurations = new ArrayList<>();

    private final Set<Consumer<MessageEvent>> inboundMessageListeners = new CopyOnWriteArraySet<>();

    private final Set<Consumer<MessageEvent>> outboundMessageListeners = new CopyOnWriteArraySet<>();

    private final Set<Consumer<PresenceEvent>> inboundPresenceListeners = new CopyOnWriteArraySet<>();

    private final Set<Consumer<PresenceEvent>> outboundPresenceListeners = new CopyOnWriteArraySet<>();

    private final Set<Consumer<IQEvent>> inboundIQListeners = new CopyOnWriteArraySet<>();

    private final Set<Consumer<IQEvent>> outboundIQListeners = new CopyOnWriteArraySet<>();

    private final Map<Class<?>, IQHandler> iqHandlerMap = new HashMap<>();

    private final Map<Class<?>, Boolean> iqHandlerInvocationModes = new HashMap<>();

    private final Set<Consumer<SessionStatusEvent>> sessionStatusListeners = new CopyOnWriteArraySet<>();

    private final Map<Class<? extends Manager>, Manager> instances = new ConcurrentHashMap<>();

    private final Set<Consumer<MessageEvent>> messageAcknowledgedListeners = new CopyOnWriteArraySet<>();

    private final Set<Consumer<StreamElement>> sendSucceededListeners = new CopyOnWriteArraySet<>();

    private final Set<BiConsumer<StreamElement, Throwable>> sendFailedListeners = new CopyOnWriteArraySet<>();

    /**
     * The unacknowledged stanzas.
     */
    private final Queue<Stanza> unacknowledgedStanzas = new ConcurrentLinkedDeque<>();

    /**
     * Maps a stanza to its send date. This is used when resending unacknowledged stanzas during reconnection (to have the original send date).
     */
    private final Map<Stanza, Instant> stanzaSendDate = new ConcurrentHashMap<>();

    /**
     * Maps a stanza to its send task.
     */
    private final Map<Stanza, SendTask<? extends Stanza>> stanzaTrackingMap = new ConcurrentHashMap<>();

    /**
     * Holds the connection state.
     */
    private final AtomicReference<Status> status = new AtomicReference<>(Status.INITIAL);

    /**
     * guarded by "connections"
     */
    protected Connection activeConnection;

    /**
     * Any exception that occurred during stream negotiation ({@link #connect()}))
     */
    protected volatile Throwable exception;

    protected volatile boolean wasLoggedIn;

    ExecutorService stanzaListenerExecutor;

    /**
     * The shutdown hook for JVM shutdown, which will disconnect each open connection before the JVM is halted.
     */
    private volatile Thread shutdownHook;

    private volatile XmppDebugger debugger;

    protected XmppSession(String xmppServiceDomain, XmppSessionConfiguration configuration, ClientConnectionConfiguration... connectionConfigurations) {
        this.xmppServiceDomain = Jid.of(Objects.requireNonNull(xmppServiceDomain, "The XMPP service domain must not be null. It's a required attribute in the stream header"));
        this.configuration = configuration;
        this.stanzaListenerExecutor = new QueuedExecutorService(STANZA_LISTENER_EXECUTOR);
        this.serviceDiscoveryManager = getManager(ServiceDiscoveryManager.class);
        this.streamFeaturesManager = getManager(StreamFeaturesManager.class);

        // Add a shutdown hook, which will gracefully close the connection, when the JVM is halted.
        if (configuration.isCloseOnShutdown()) {
            shutdownHook = configuration.getThreadFactory("Shutdown Hook").newThread(() -> {
                shutdownHook = null;
                try {
                    close();
                } catch (XmppException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }
            });
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        }

        if (configuration.getDebugger() != null) {
            try {
                this.debugger = configuration.getDebugger().getConstructor().newInstance();
                this.debugger.initialize(this);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        if (connectionConfigurations.length == 0) {
            // Add two fallback connections. Host and port will be determined by the XMPP domain via SRV lookup.
            this.connectionConfigurations.add(SocketConnectionConfiguration.getDefault());
            this.connectionConfigurations.add(BoshConnectionConfiguration.getDefault());
        } else {
            this.connectionConfigurations.addAll(Arrays.asList(connectionConfigurations));
        }

        configuration.getExtensions().forEach(serviceDiscoveryManager::registerFeature);
    }

    protected static void throwAsXmppExceptionIfNotNull(Throwable e) throws XmppException {
        if (e != null) {
            if (e instanceof XmppException) {
                throw (XmppException) e;
            } else if (e.getCause() instanceof XmppException) {
                throw (XmppException) e.getCause();
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e instanceof Error) {
                throw (Error) e;
            } else {
                throw new XmppException(e);
            }
        }
    }

    /**
     * Adds a listener, which is triggered, whenever a new session is created.
     *
     * @param listener The listener.
     * @see #removeCreationListener(Consumer)
     */
    public static void addCreationListener(Consumer<XmppSession> listener) {
        creationListeners.add(listener);
    }

    /**
     * Removes a previously added creation listener.
     *
     * @param listener The listener.
     * @see #addCreationListener(Consumer)
     */
    public static void removeCreationListener(Consumer<XmppSession> listener) {
        creationListeners.remove(listener);
    }

    protected static void notifyCreationListeners(XmppSession xmppSession) {
        creationListeners.forEach(xmppSessionConsumer -> {
            try {
                xmppSessionConsumer.accept(xmppSession);
            } catch (Exception e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        });
    }

    /**
     * Connects to the XMPP server.
     *
     * @throws ConnectionException        If a connection error occurred on the transport layer, e.g. the socket could not connect.
     * @throws StreamErrorException       If the server returned a stream error.
     * @throws StreamNegotiationException If any exception occurred during stream feature negotiation.
     * @throws NoResponseException        If the server didn't return a response during stream establishment.
     * @throws XmppException              If any other XMPP exception occurs.
     * @throws IllegalStateException      If the session is in a wrong state, e.g. closed or already connected.
     */
    public final void connect() throws XmppException {
        connect(null);
    }

    public abstract void connect(Jid from) throws XmppException;

    /**
     * Called after successful login.
     */
    protected final void afterLogin() {
        if (wasLoggedIn) {
            XmppUtils.notifyEventListeners(connectionListeners, new ConnectionEvent(this, ConnectionEvent.Type.RECONNECTION_SUCCEEDED, null, Duration.ZERO));
        }
        wasLoggedIn = true;
        // Copy the unacknowledged stanzas.
        Queue<Stanza> toBeResent = new ArrayDeque<>(unacknowledgedStanzas);
        // Then clear the queue.
        unacknowledgedStanzas.clear();

        // Then resend everything, which the server didn't acknowledge.
        toBeResent.forEach(stanza -> {
            Instant originalSendDate = stanzaSendDate.remove(stanza);
            if (originalSendDate != null) {
                DelayedDelivery delayedDelivery = new DelayedDelivery(originalSendDate);
                if (stanza instanceof ExtensibleStanza && !stanza.hasExtension(DelayedDelivery.class)) {
                    ((ExtensibleStanza) stanza).addExtension(delayedDelivery);
                }
            }
            this.sendInternal(stanza, true);
        });
    }

    protected final void tryConnect(Jid from, String namespace, String version) throws XmppException {

        // Close any previous connection, which might still be open.
        try {
            closeAndNullifyConnection();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failure during closing previous connection.", e);
        }

        synchronized (connectionConfigurations) {
            Iterator<ClientConnectionConfiguration> connectionIterator = getConnections().iterator();
            while (connectionIterator.hasNext()) {
                ClientConnectionConfiguration connectionConfiguration = connectionIterator.next();
                Connection connection = null;
                try {
                    connection = connectionConfiguration.createConnection(this);
                    connection.open(StreamHeader.create(from, xmppServiceDomain, null, version, configuration.getLanguage(), namespace));
                    activeConnection = connection;
                    break;
                } catch (Exception e) {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (Exception e1) {
                            e.addSuppressed(e1);
                        }
                    }
                    if (connectionIterator.hasNext()) {
                        logger.log(Level.WARNING, "{0} failed to connect. Trying alternative connection.", connectionConfiguration);
                        logger.log(Level.FINE, e.getMessage(), e);
                    } else {
                        throw new ConnectionException("Failed to connect to " + connectionConfiguration, e);
                    }
                }
            }
            logger.log(Level.FINE, "Connected via {0}", activeConnection);
        }
    }

    /**
     * Checks, if the session is already connected and if so, logs it.
     *
     * @return True, if the session is already connected.
     */
    protected boolean checkConnected() {
        if (isConnected()) {
            // Silently return, when we are already connected.
            logger.fine("Already connected. Return silently.");
            return true;
        }
        return false;
    }

    /**
     * Checks, if the session is closed and throws an {@link IllegalStateException} if so.
     *
     * @return The current status of session (which will never be {@link Status#CLOSED}.
     */
    protected Status preConnect() {
        Status previousStatus = getStatus();
        if (previousStatus == Status.CLOSED) {
            throw new IllegalStateException("Session is already closed. Create a new one.");
        }
        return previousStatus;
    }

    /**
     * Called, when the connection has failed. It closes the connection and reverts the session status to the previous status.
     *
     * @param previousStatus The previous status.
     * @param e              The exception. Any exception during closing the connection will be added as suppressed exception to this one.
     * @throws XmppException The exception, which will be rethrown.
     */
    protected final void onConnectionFailed(Status previousStatus, Throwable e) throws XmppException {
        try {
            closeAndNullifyConnection();
        } catch (Exception e1) {
            e.addSuppressed(e1);
        }
        updateStatus(previousStatus, e);
        throwAsXmppExceptionIfNotNull(e);
    }

    /**
     * Checks several preconditions before login.
     *
     * @return The previous status.
     */
    protected final Status preLogin() {
        Status previousStatus = getStatus();
        if (!IS_CONNECTED.contains(previousStatus)) {
            throw new IllegalStateException("You must be connected to the server before trying to login. Status is " + previousStatus);
        }
        if (getDomain() == null) {
            throw new IllegalStateException("The XMPP domain must not be null.");
        }
        exception = null;
        return previousStatus;
    }

    /**
     * Checks, if the session is already authenticated and if so, logs it.
     *
     * @return True, if the session is already authenticated.
     */
    protected boolean checkAuthenticated() {
        if (getStatus() == Status.AUTHENTICATED) {
            // Silently return, when we are already logged in.
            logger.fine("Already logged in. Return silently.");
            return true;
        }
        return false;
    }

    /**
     * Adds an inbound message listener to the session, which will get notified, whenever a message is received.
     *
     * @param messageListener The message listener.
     * @see #removeInboundMessageListener(Consumer)
     */
    public final void addInboundMessageListener(Consumer<MessageEvent> messageListener) {
        inboundMessageListeners.add(messageListener);
    }

    /**
     * Removes a previously added inbound message listener from the session.
     *
     * @param messageListener The message listener.
     * @see #addInboundMessageListener(Consumer)
     */
    public final void removeInboundMessageListener(Consumer<MessageEvent> messageListener) {
        inboundMessageListeners.remove(messageListener);
    }

    /**
     * Adds an outbound message listener to the session, which will get notified, whenever a message is sent.
     *
     * @param messageListener The message listener.
     * @see #removeOutboundMessageListener(Consumer)
     */
    public final void addOutboundMessageListener(Consumer<MessageEvent> messageListener) {
        outboundMessageListeners.add(messageListener);
    }

    /**
     * Removes a previously added outbound message listener from the session.
     *
     * @param messageListener The message listener.
     * @see #addOutboundMessageListener(Consumer)
     */
    public final void removeOutboundMessageListener(Consumer<MessageEvent> messageListener) {
        outboundMessageListeners.remove(messageListener);
    }

    /**
     * Adds an inbound presence listener to the session, which will get notified, whenever a presence is received.
     *
     * @param presenceListener The presence listener.
     * @see #removeInboundPresenceListener(Consumer)
     */
    public final void addInboundPresenceListener(Consumer<PresenceEvent> presenceListener) {
        inboundPresenceListeners.add(presenceListener);
    }

    /**
     * Removes a previously added inbound presence listener from the session.
     *
     * @param presenceListener The presence listener.
     * @see #addInboundPresenceListener(Consumer)
     */
    public final void removeInboundPresenceListener(Consumer<PresenceEvent> presenceListener) {
        inboundPresenceListeners.remove(presenceListener);
    }

    /**
     * Adds an outbound presence listener to the session, which will get notified, whenever a presence is sent.
     *
     * @param presenceListener The presence listener.
     * @see #removeOutboundPresenceListener(Consumer)
     */
    public final void addOutboundPresenceListener(Consumer<PresenceEvent> presenceListener) {
        outboundPresenceListeners.add(presenceListener);
    }

    /**
     * Removes a previously added outbound presence listener from the session.
     *
     * @param presenceListener The presence listener.
     * @see #addOutboundPresenceListener(Consumer)
     */
    public final void removeOutboundPresenceListener(Consumer<PresenceEvent> presenceListener) {
        outboundPresenceListeners.remove(presenceListener);
    }

    /**
     * Adds an inbound IQ listener to the session, which will get notified, whenever an IQ stanza is received.
     *
     * @param iqListener The IQ listener.
     * @see #removeInboundIQListener(Consumer)
     */
    public final void addInboundIQListener(Consumer<IQEvent> iqListener) {
        inboundIQListeners.add(iqListener);
    }

    /**
     * Removes a previously added inbound IQ listener from the session.
     *
     * @param iqListener The IQ listener.
     * @see #addInboundIQListener(Consumer)
     */
    public final void removeInboundIQListener(Consumer<IQEvent> iqListener) {
        inboundIQListeners.remove(iqListener);
    }

    /**
     * Adds an outbound IQ listener to the session, which will get notified, whenever an IQ stanza is sent.
     *
     * @param iqListener The IQ listener.
     * @see #removeOutboundIQListener(Consumer)
     */
    public final void addOutboundIQListener(Consumer<IQEvent> iqListener) {
        outboundIQListeners.add(iqListener);
    }

    /**
     * Removes a previously added outbound IQ listener from the session.
     *
     * @param iqListener The IQ listener.
     * @see #addOutboundIQListener(Consumer)
     */
    public final void removeOutboundIQListener(Consumer<IQEvent> iqListener) {
        outboundIQListeners.remove(iqListener);
    }

    /**
     * Adds a listener, which gets called, when the server acknowledges the receipt of a message.
     *
     * @param messageListener The message listener.
     * @see #removeMessageAcknowledgedListener(Consumer)
     */
    public final void addMessageAcknowledgedListener(Consumer<MessageEvent> messageListener) {
        messageAcknowledgedListeners.add(messageListener);
    }

    /**
     * Removes a previously added message acknowledge listener.
     *
     * @param messageListener The message listener.
     * @see #addMessageAcknowledgedListener(Consumer)
     */
    public final void removeMessageAcknowledgedListener(Consumer<MessageEvent> messageListener) {
        messageAcknowledgedListeners.remove(messageListener);
    }

    /**
     * Adds a listener, which gets called, whenever a stream element (e.g. message) has been sent successfully.
     *
     * @param sendSucceededListener The listener.
     * @see #removeSendSucceededListener(Consumer)
     */
    public final void addSendSucceededListener(Consumer<StreamElement> sendSucceededListener) {
        sendSucceededListeners.add(sendSucceededListener);
    }

    /**
     * Removes a previously added send succeeded listener.
     *
     * @param sendSucceededListener The listener.
     * @see #addSendSucceededListener(Consumer)
     */
    public final void removeSendSucceededListener(Consumer<StreamElement> sendSucceededListener) {
        sendSucceededListeners.remove(sendSucceededListener);
    }

    /**
     * Adds a listener, which gets called, whenever a stream element (e.g. message) has been sent unsuccessfully.
     *
     * @param sendFailedListener The listener.
     * @see #removeSendFailedListener(BiConsumer)
     */
    public final void addSendFailedListener(BiConsumer<StreamElement, Throwable> sendFailedListener) {
        sendFailedListeners.add(sendFailedListener);
    }

    /**
     * Removes a previously added send failed listener.
     *
     * @param sendFailedListener The listener.
     * @see #addSendFailedListener(BiConsumer)
     * @see #addSendSucceededListener(Consumer)
     */
    public final void removeSendFailedListener(BiConsumer<StreamElement, Throwable> sendFailedListener) {
        sendFailedListeners.remove(sendFailedListener);
    }

    /**
     * Adds an IQ handler for a given payload type. The handler will be processed asynchronously, which means it won't block the inbound stanza processing queue.
     *
     * @param type      The payload type.
     * @param iqHandler The IQ handler.
     * @see #removeIQHandler(Class)
     * @see #addIQHandler(Class, IQHandler, boolean)
     */
    public final void addIQHandler(Class<?> type, IQHandler iqHandler) {
        addIQHandler(type, iqHandler, true);
    }

    /**
     * Adds an IQ handler for a given payload type. The handler can either be processed asynchronously (which means it won't block the inbound stanza processing queue),
     * or synchronously, which means IQ requests are processed on the same thread as other stanzas.
     * In other words synchronous processing means, the IQ requests are processed in the same order as they arrive and no other stanzas can be
     * processed until the handler has returned.
     *
     * @param type        The payload type.
     * @param iqHandler   The IQ handler.
     * @param invokeAsync True, if the handler should be processed asynchronously; false, if the handler should be processed asynchronously.
     * @see #removeIQHandler(Class)
     */
    public final void addIQHandler(Class<?> type, IQHandler iqHandler, boolean invokeAsync) {
        synchronized (iqHandlerMap) {
            iqHandlerMap.put(type, iqHandler);
            iqHandlerInvocationModes.put(type, invokeAsync);
        }
    }

    /**
     * Removes an IQ handler.
     *
     * @param type The payload type.
     * @see #addIQHandler(Class, IQHandler)
     */
    public final void removeIQHandler(Class<?> type) {
        synchronized (iqHandlerMap) {
            iqHandlerMap.remove(type);
            iqHandlerInvocationModes.remove(type);
        }
    }

    /**
     * Adds a session listener, which listens for session status changes.
     * Each time the {@linkplain Status session status} changes, all listeners will be notified.
     *
     * @param sessionStatusListener The session listener.
     * @see #removeSessionStatusListener(Consumer)
     */
    public final void addSessionStatusListener(Consumer<SessionStatusEvent> sessionStatusListener) {
        sessionStatusListeners.add(sessionStatusListener);
    }

    /**
     * Removes a previously added session listener.
     *
     * @param sessionStatusListener The session listener.
     * @see #addSessionStatusListener(Consumer)
     */
    public final void removeSessionStatusListener(Consumer<SessionStatusEvent> sessionStatusListener) {
        sessionStatusListeners.remove(sessionStatusListener);
    }

    /**
     * Adds a connection listener, which is triggered, when the connection used by this session is disconnected or reconnected.
     *
     * @param connectionListener The connection listener.
     * @see #removeConnectionListener(Consumer)
     */
    public final void addConnectionListener(Consumer<ConnectionEvent> connectionListener) {
        connectionListeners.add(connectionListener);
    }

    /**
     * Removes a previously added connection listener.
     *
     * @param connectionListener The connection listener.
     * @see #addConnectionListener(Consumer)
     */
    public final void removeConnectionListener(Consumer<ConnectionEvent> connectionListener) {
        connectionListeners.remove(connectionListener);
    }

    /**
     * Sends an {@code <iq/>} stanza and returns an async result, which can be used to wait for the response. The result is completed if the response IQ has arrived or the default timeout has exceeded, in which case the result completes with a {@link NoResponseException}.
     * <p>
     *
     * @param iq The {@code <iq/>} stanza, which must be of type {@linkplain Type#GET get} or {@linkplain Type#SET set}.
     * @return The async IQ result.
     */
    public final AsyncResult<IQ> query(IQ iq) {
        return query(iq, configuration.getDefaultResponseTimeout());
    }

    /**
     * Sends an {@code <iq/>} stanza and returns an async result, which can be used to wait for the response. The result is completed if the response IQ has arrived or the timeout has exceeded, in which case the result completes with a {@link NoResponseException}.
     *
     * @param iq      The {@code <iq/>} stanza, which must be of type {@linkplain Type#GET get} or {@linkplain Type#SET set}.
     * @param timeout The timeout.
     * @return The async IQ result.
     */
    public final AsyncResult<IQ> query(IQ iq, Duration timeout) {

        if (!iq.isRequest()) {
            throw new IllegalArgumentException("IQ must be of type 'get' or 'set'");
        }
        return sendAndAwait(iq,
                IQEvent::getIQ,
                new IQResponsePredicate(iq, getConnectedResource()),
                this::sendIQ,
                this::addInboundIQListener,
                this::removeInboundIQListener,
                timeout);
    }

    /**
     * Sends an {@code <iq/>} stanza and returns an async result, which can be used to wait for the response. The result is completed if the response IQ has arrived or the default timeout has exceeded, in which case the result completes with a {@link NoResponseException}.
     * The payload of the response IQ is returned in the async result class.
     *
     * @param <T>   The type.
     * @param iq    The {@code <iq/>} stanza, which must be of type {@linkplain rocks.xmpp.core.stanza.model.IQ.Type#GET get} or {@linkplain rocks.xmpp.core.stanza.model.IQ.Type#SET set}.
     * @param clazz The class which is IQ response's payload.
     * @return The async result with the IQ response's payload.
     */
    public final <T> AsyncResult<T> query(IQ iq, Class<T> clazz) {
        return query(iq).thenApply(
                result -> result.getExtension(clazz));
    }

    /**
     * Sends a stanza and returns an async result which can wait for the presence stanza, which matches the predicate, to arrive.
     *
     * @param stanza The stanza, which is sent.
     * @param filter The presence filter.
     * @return The async presence result.
     */
    public final AsyncResult<Presence> sendAndAwaitPresence(Presence stanza, final Predicate<Presence> filter) {
        return sendAndAwait(stanza,
                PresenceEvent::getPresence,
                filter,
                this::sendPresence,
                this::addInboundPresenceListener,
                this::removeInboundPresenceListener,
                configuration.getDefaultResponseTimeout()
        );
    }

    /**
     * Sends a stanza and returns an async result which can wait for the message stanza, which matches the predicate, to arrive.
     *
     * @param stanza The stanza, which is sent.
     * @param filter The message filter.
     * @return The async message result.
     */
    public final AsyncResult<Message> sendAndAwaitMessage(Message stanza, final Predicate<Message> filter) {
        return sendAndAwait(stanza,
                MessageEvent::getMessage,
                filter.and(new MessageResponsePredicate(stanza, getConnectedResource())),
                this::sendMessage,
                this::addInboundMessageListener,
                this::removeInboundMessageListener,
                configuration.getDefaultResponseTimeout()
        );
    }

    private <S extends Stanza, E extends EventObject> AsyncResult<S> sendAndAwait(S stanza, Function<E, S> stanzaMapper, final Predicate<S> filter, Function<S, SendTask<S>> sendFunction, Consumer<Consumer<E>> addListener, Consumer<Consumer<E>> removeListener, Duration timeout) {
        CompletableFuture<S> completableFuture = new CompletableFuture<>();

        final Consumer<E> listener = e -> {
            S st = stanzaMapper.apply(e);
            if (filter.test(st)) {
                if (st.getError() != null) {
                    completableFuture.completeExceptionally(new StanzaErrorException(st));
                }
                completableFuture.complete(st);
            }
        };

        addListener.accept(listener);

        SendTask<S> sendTask = sendFunction.apply(stanza);
        // When the sending failed, immediately complete the future with the exception.
        sendTask.onFailed((throwable, s) -> completableFuture.completeExceptionally(throwable));
        return new AsyncResult<>(completableFuture
                // When a response has received, mark the requesting stanza as acknowledged.
                // This is especially important for Bind and Roster IQs, so that they won't be resend after login.
                .whenComplete((result, e) -> removeFromQueue(sendTask.getStanza()))
                .applyToEither(CompletionStages.timeoutAfter(timeout.toMillis(), TimeUnit.MILLISECONDS, () -> new NoResponseException("Timeout reached, while waiting on a response for request: " + stanza)), Function.identity()))
                // When either a timeout happened or response has received, remove the listener.
                .whenComplete((result, e) -> removeListener.accept(listener));

    }

    /**
     * Gets the actively used connection.
     *
     * @return The actively used connection.
     */
    public final Connection getActiveConnection() {
        synchronized (connectionConfigurations) {
            return activeConnection;
        }
    }

    /**
     * Sends an XML element to the server, usually a stanza, i.e. a message, presence or IQ.
     *
     * @param element The XML element.
     * @return The sent stream element, which is usually the same as the parameter, but may differ in case a stanza is sent, e.g. a {@link Message} is translated to a {@link rocks.xmpp.core.stanza.model.client.ClientMessage}.
     */
    public Future<Void> send(StreamElement element) {
        return sendInternal(prepareElement(element), true);
    }

    /**
     * Sends an XML element to the server, usually a stanza, i.e. a message, presence or IQ.
     *
     * @param element The XML element.
     * @param queue   If the element should be queued for later resending.
     * @return The sent stream element, which is usually the same as the parameter, but may differ in case a stanza is sent, e.g. a {@link Message} is translated to a {@link rocks.xmpp.core.stanza.model.client.ClientMessage}.
     */
    final Future<Void> send(StreamElement element, boolean queue) {
        return sendInternal(prepareElement(element), queue);
    }

    private CompletableFuture<Void> sendInternal(StreamElement element, boolean queue) {

        CompletableFuture<Void> sendFuture;
        Stanza stanza = null;
        try {
            if (element instanceof Stanza) {
                stanza = (Stanza) element;
                if (queue) {
                    // Put the stanzas in an unacknowledged queue.
                    // They will be removed if either the stanza has been sent without error or if it has been acknowledged by the server (if the connection supports acknowledgements).
                    // In case of IQ queries, they will be removed, when the IQ response arrives.
                    unacknowledgedStanzas.offer(stanza);
                    stanzaSendDate.put(stanza, Instant.now());
                }
                // If resource binding has not completed and it's tried to send a stanza which doesn't serve the purpose
                // of resource binding, throw an exception, because otherwise the server will terminate the connection with a stream error.
                if (!EnumSet.of(Status.AUTHENTICATED, Status.CLOSING, Status.DISCONNECTED).contains(getStatus())
                        && !Stanza.isToItselfOrServer(stanza, getDomain(), getConnectedResource())) {
                    throw new IllegalStateException("Cannot send stanzas before resource binding has completed.");
                }
                if (stanza instanceof Message) {
                    MessageEvent messageEvent = new MessageEvent(this, (Message) stanza, false);
                    XmppUtils.notifyEventListeners(outboundMessageListeners, messageEvent);
                    if (messageEvent.isConsumed()) {
                        throw new IllegalStateException("Message event has been consumed.");
                    }
                } else if (stanza instanceof Presence) {
                    PresenceEvent presenceEvent = new PresenceEvent(this, (Presence) stanza, false);
                    XmppUtils.notifyEventListeners(outboundPresenceListeners, presenceEvent);
                    if (presenceEvent.isConsumed()) {
                        throw new IllegalStateException("Presence event has been consumed.");
                    }
                } else if (stanza instanceof IQ) {
                    IQEvent iqEvent = new IQEvent(this, (IQ) stanza, false);
                    XmppUtils.notifyEventListeners(outboundIQListeners, iqEvent);
                    if (iqEvent.isConsumed()) {
                        throw new IllegalStateException("IQ event has been consumed.");
                    }
                }
            }

            Connection connection = getActiveConnection();
            if (connection == null) {
                IllegalStateException ise = new IllegalStateException("Session is not connected to server (status: " + getStatus() + ')');
                Throwable cause = exception;
                if (cause != null) {
                    ise.initCause(cause);
                }
                throw ise;
            } else {
                sendFuture = connection.send(element).toCompletableFuture();
            }
        } catch (Exception e) {
            sendFuture = new CompletableFuture<>();
            sendFuture.completeExceptionally(e);
        }
        // This is for resending. If this stanza has been already sent previously, but failed,
        // there might be a associated send task. Update it with the new send future.
        if (stanza != null) {
            SendTask<?> sendTask = stanzaTrackingMap.get(stanza);
            if (sendTask != null) {
                sendTask.updateSendFuture(sendFuture);
            }
        }
        return sendFuture.whenComplete(((aVoid, throwable) -> {
            if (throwable == null) {
                sendSucceededListeners.forEach(listener -> {
                    try {
                        listener.accept(element);
                    } catch (Exception e) {
                        logger.log(Level.WARNING, e.getMessage(), e);
                    }
                });
                // The stanza has been successfully sent. Don't track it any longer, unless the connection supports acknowledgements.
                if (element instanceof Stanza) {
                    Connection connection = getActiveConnection();
                    if (connection == null || !connection.isUsingAcknowledgements()) {
                        Stanza st = (Stanza) element;
                        removeFromQueue(st);
                        stanzaTrackingMap.remove(st);
                    }
                }
            } else {
                sendFailedListeners.forEach(listener -> {
                    try {
                        listener.accept(element, throwable);
                    } catch (Exception e) {
                        logger.log(Level.WARNING, e.getMessage(), e);
                    }
                });
            }
        }));
    }

    private void removeFromQueue(Stanza stanza) {
        stanzaSendDate.remove(stanza);
        unacknowledgedStanzas.remove(stanza);
    }

    /**
     * Sends an IQ.
     *
     * @param iq The IQ.
     * @return The send task, which allows to track the stanza.
     */
    public SendTask<IQ> sendIQ(final IQ iq) {
        return trackAndSend(iq);
    }

    /**
     * Sends a message.
     *
     * @param message The message.
     * @return The send task, which allows to track the stanza.
     */
    public SendTask<Message> sendMessage(final Message message) {
        return trackAndSend(message);
    }

    /**
     * Sends a presence.
     *
     * @param presence The presence.
     * @return The send task, which allows to track the stanza.
     */
    public SendTask<Presence> sendPresence(final Presence presence) {
        return trackAndSend(presence);
    }

    @SuppressWarnings("unchecked")
    protected final <S extends Stanza> SendTask<S> trackAndSend(S stanza) {
        Stanza s = (S) prepareElement(stanza);
        SendTask<S> sendTask = (SendTask<S>) stanzaTrackingMap.computeIfAbsent(s, key -> new SendTask<>(s));
        sendTask.updateSendFuture(sendInternal(s, true));
        return sendTask;
    }

    /**
     * Prepares a stream element for sending.
     * Usually only stanzas need to be prepared, which usually means converting it to the correct type (e.g. {@link Message} to {@link rocks.xmpp.core.stanza.model.client.ClientMessage}.so that it's in the correct namespace).
     * Preparing could also be used to add the 'from' attribute (as required for external components).
     *
     * @param element The element.
     * @return The prepared stanza.
     */
    protected StreamElement prepareElement(StreamElement element) {
        return element;
    }

    /**
     * Gets the status of the session.
     *
     * @return The status.
     */
    public final Status getStatus() {
        return status.get();
    }

    /**
     * Updates the status and notifies the session listeners.
     *
     * @param status The new status.
     * @return True, if the status has changed; otherwise false.
     */
    protected final boolean updateStatus(Status status) {
        return updateStatus(status, (Throwable) null);
    }

    /**
     * Updates the status and notifies the session listeners.
     *
     * @param status The new status.
     * @param e      The exception.
     * @return True, if the status has changed; otherwise false.
     */
    protected final boolean updateStatus(Status status, Throwable e) {
        Status oldStatus = this.status.getAndSet(status);
        if (status != oldStatus) {
            // Make sure to not call listeners from within synchronized region.
            XmppUtils.notifyEventListeners(sessionStatusListeners, new SessionStatusEvent(this, status, oldStatus, e));
        }
        return status != oldStatus;
    }

    /**
     * Updates the status and notifies the session listeners.
     *
     * @param expected The expected status.
     * @param status   The new status.
     * @return True, if the status has changed; otherwise false.
     */
    protected final boolean updateStatus(Status expected, Status status) {
        boolean hasChanged = this.status.compareAndSet(expected, status);
        if (hasChanged) {
            // Make sure to not call listeners from within synchronized region.
            XmppUtils.notifyEventListeners(sessionStatusListeners, new SessionStatusEvent(this, status, expected, null));
        }
        return hasChanged;
    }

    /**
     * Gets an unmodifiable list of connections, which this session will try during connecting.
     *
     * @return The connections.
     */
    public final List<ClientConnectionConfiguration> getConnections() {
        return Collections.unmodifiableList(connectionConfigurations);
    }

    /**
     * Creates a new unmarshaller, which can be used to unmarshal XML to objects.
     * <p>
     * Note that the returned unmarshaller is not thread-safe.
     * </p>
     *
     * @return The unmarshaller.
     * @see #createMarshaller()
     */
    public final Unmarshaller createUnmarshaller() {
        try {
            return configuration.getJAXBContext().createUnmarshaller();
        } catch (JAXBException e) {
            throw new DataBindingException(e);
        }
    }

    /**
     * Creates a marshaller, which can be used to create XML from objects.
     * <p>
     * The returned marshaller is configured with {@code Marshaller.JAXB_FRAGMENT = true}, so that no XML header is written
     * (which is usually what we want in XMPP when writing stanzas).
     * </p>
     * Note that the returned unmarshaller is not thread-safe.
     *
     * @return The marshaller.
     * @see #createUnmarshaller()
     */
    public final Marshaller createMarshaller() {
        try {
            Marshaller marshaller = configuration.getJAXBContext().createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            return marshaller;
        } catch (JAXBException e) {
            throw new DataBindingException(e);
        }
    }

    /**
     * Indicates, whether the session is connected.
     *
     * @return True, if the status is {@link Status#CONNECTED}, {@link Status#AUTHENTICATED} or {@link Status#AUTHENTICATING}.
     * @see #getStatus()
     */
    public final boolean isConnected() {
        return IS_CONNECTED.contains(getStatus());
    }

    /**
     * Returns true, if the session is authenticated. For a normal client-to-server session this means, when the user has logged in (successfully completed SASL negotiation and resource binding).
     *
     * @return True, if authenticated.
     * @since 0.7.0
     */
    public final boolean isAuthenticated() {
        return getStatus() == Status.AUTHENTICATED;
    }

    /**
     * Handles an XMPP element.
     * <p>
     * This method should be called on the reader thread.
     * </p>
     *
     * @param element The XMPP element.
     * @return True, if the stream needs to be restarted; otherwise false.
     * @throws StreamErrorException       If the element is a stream error.
     * @throws StreamNegotiationException If any exception occurred during stream feature negotiation.
     * @throws XmppException              If any other XMPP exception occurs.
     */
    @Override
    public boolean handleElement(final Object element) throws XmppException {

        StreamManager streamManager = getManager(StreamManager.class);

        if (element instanceof IQ) {
            final IQ iq = (IQ) element;

            if (iq.getType() == null) {
                // return <bad-request/> if the <iq/> has no type.
                send(iq.createError(Condition.BAD_REQUEST));
            } else if (iq.isRequest()) {
                Object payload = iq.getExtension(Object.class);
                if (payload == null) {
                    // return <bad-request/> if the <iq/> has no payload.
                    send(iq.createError(Condition.BAD_REQUEST));
                } else {
                    Executor executor;
                    final IQHandler iqHandler;
                    synchronized (iqHandlerMap) {
                        iqHandler = iqHandlerMap.get(payload.getClass());
                        // If the handler is to be invoked asynchronously, get the iqHandlerExecutor, otherwise use stanzaListenerExecutor (which is a single thread executor).
                        executor = iqHandler != null ? (iqHandlerInvocationModes.get(payload.getClass()) ? getIqHandlerExecutor() : getStanzaListenerExecutor()) : null;
                    }

                    if (iqHandler != null) {
                        executor.execute(() -> {
                            try {
                                IQ response = iqHandler.handleRequest(iq);
                                if (response != null) {
                                    send(response);
                                }
                            } catch (Exception e) {
                                logger.log(Level.WARNING, e, () -> "Failed to handle IQ request: " + e.getMessage());
                                // If any exception occurs during processing the IQ, return <service-unavailable/>.
                                send(iq.createError(Condition.SERVICE_UNAVAILABLE));
                            }
                        });
                    } else {
                        // return <service-unavailable/> if the <iq/> is not understood.
                        send(iq.createError(Condition.SERVICE_UNAVAILABLE));
                    }
                }
            }
            getIqHandlerExecutor().execute(() -> {
                XmppUtils.notifyEventListeners(inboundIQListeners, new IQEvent(this, iq, true));
                streamManager.incrementInboundStanzaCount();
            });
        } else if (element instanceof Message) {
            getStanzaListenerExecutor().execute(() -> {
                XmppUtils.notifyEventListeners(inboundMessageListeners, new MessageEvent(this, (Message) element, true));
                streamManager.incrementInboundStanzaCount();
            });
        } else if (element instanceof Presence) {
            getStanzaListenerExecutor().execute(() -> {
                XmppUtils.notifyEventListeners(inboundPresenceListeners, new PresenceEvent(this, (Presence) element, true));
                streamManager.incrementInboundStanzaCount();
            });
        } else if (element instanceof StreamFeatures) {
            streamFeaturesManager.processFeatures((StreamFeatures) element);
        } else if (element instanceof StreamError) {
            throw new StreamErrorException((StreamError) element);
        } else if (!(element instanceof SessionOpen)) {
            // Let's see, if the element is known to any feature negotiator.
            return streamFeaturesManager.handleElement(element);
        }
        return false;
    }

    /**
     * Gets an instance of the specified manager class. The class MUST have a constructor which takes a single parameter, whose type is {@link rocks.xmpp.core.session.XmppSession}.
     *
     * @param clazz The class of the manager.
     * @param <T>   The type.
     * @return An instance of the specified manager.
     */
    @SuppressWarnings("unchecked")
    public final <T extends Manager> T getManager(Class<T> clazz) {
        T instance;
        if ((instance = (T) instances.get(clazz)) == null) {
            synchronized (instances) {
                if ((instance = (T) instances.get(clazz)) == null) {
                    try {
                        Constructor<T> constructor = clazz.getDeclaredConstructor(XmppSession.class);
                        constructor.setAccessible(true);
                        instance = constructor.newInstance(this);
                        instance.initialize();
                        instances.put(clazz, instance);
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        throw new IllegalArgumentException("Can't instantiate the provided class:" + clazz, e);
                    }
                }
            }
        }
        return instance;
    }

    /**
     * Explicitly closes the session and performs a clean up of all listeners. Calling this method, if the session is already closing or closed has no effect.
     *
     * @throws XmppException If an exception occurs while closing the connection, e.g. the underlying socket connection.
     */
    @Override
    public final void close() throws XmppException {
        if (getStatus() == Status.CLOSED || !updateStatus(Status.CLOSING)) {
            return;
        }
        // The following code should only be called once, no matter how many threads concurrently call this method.
        try {
            closeAndNullifyConnection();
        } catch (Exception e) {
            throwAsXmppExceptionIfNotNull(e);
        } finally {
            // Clear everything.
            inboundMessageListeners.clear();
            outboundMessageListeners.clear();
            inboundPresenceListeners.clear();
            outboundPresenceListeners.clear();
            inboundIQListeners.clear();
            outboundIQListeners.clear();
            messageAcknowledgedListeners.clear();
            sendSucceededListeners.clear();
            sendFailedListeners.clear();
            stanzaListenerExecutor.shutdown();
            if (shutdownHook != null) {
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
            }
            updateStatus(Status.CLOSED);
            sessionStatusListeners.clear();
        }
    }

    private void closeAndNullifyConnection() throws Exception {

        try {
            Connection connection = getActiveConnection();
            if (connection != null) {
                connection.close();
            }
        } finally {
            synchronized (connectionConfigurations) {
                activeConnection = null;
            }
        }
    }

    /**
     * Called if any unhandled exception is thrown during reading or writing.
     * <p>
     * This method will close the stream.
     * </p>
     *
     * @param e The exception. If an unrecoverable XMPP stream error occurred, the exception is a {@link rocks.xmpp.core.stream.model.StreamError}.
     */
    public void notifyException(Throwable e) {
        // If the exception occurred during stream negotiation, i.e. before the connect() method has finished, the exception will be thrown.
        exception = Objects.requireNonNull(e, "exception must not be null");
        // Release a potential waiting thread.
        streamFeaturesManager.cancelNegotiation();
        if (EnumSet.of(Status.AUTHENTICATED, Status.AUTHENTICATING, Status.CONNECTED, Status.CONNECTING).contains(getStatus()) && !(e instanceof AuthenticationException)) {
            try {
                closeAndNullifyConnection();
            } catch (Exception e1) {
                e.addSuppressed(e1);
            }
            updateStatus(Status.DISCONNECTED, e);
        }
    }

    /**
     * Gets the XMPP domain of the connected server. This variable is set after server has responded with a stream header. The domain is the stream header's 'from' attribute.
     *
     * @return The XMPP domain.
     */
    public Jid getDomain() {
        return xmppServiceDomain;
    }

    /**
     * Gets the configuration for this session.
     *
     * @return The configuration.
     */
    public final XmppSessionConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Gets the debugger or null if no debugger class was specified in the configuration.
     *
     * @return The debugger.
     * @see XmppSessionConfiguration#getDebugger()
     */
    public final XmppDebugger getDebugger() {
        return debugger;
    }

    /**
     * Enables a feature by its name, usually a protocol namespace.
     *
     * @param name The associated manager class.
     */
    public final void enableFeature(String name) {
        serviceDiscoveryManager.addFeature(name);
    }

    /**
     * Disables a feature by its name, usually a protocol namespace.
     *
     * @param name The associated manager class.
     */
    public final void disableFeature(String name) {
        serviceDiscoveryManager.removeFeature(name);
    }

    /**
     * Enables a feature by its manager class.
     *
     * @param managerClass The associated manager class.
     */
    public final void enableFeature(Class<? extends Manager> managerClass) {
        serviceDiscoveryManager.addFeature(managerClass);
    }

    /**
     * Disables a feature by its manager class.
     *
     * @param managerClass The associated manager class.
     */
    public final void disableFeature(Class<? extends Manager> managerClass) {
        serviceDiscoveryManager.removeFeature(managerClass);
    }

    /**
     * Gets the enabled features.
     *
     * @return The enabled features.
     */
    public final Set<String> getEnabledFeatures() {
        return serviceDiscoveryManager.getFeatures();
    }

    /**
     * The connected resource, which is assigned by the server.
     *
     * @return The connected resource.
     */
    public abstract Jid getConnectedResource();

    public final Queue<Stanza> getUnacknowledgedStanzas() {
        return unacknowledgedStanzas;
    }

    /**
     * Marks a stanza as acknowledged.
     * This method removes a stanza from the unacknowledged queue, so that it won't be resent during reconnection
     * and notifies the {@linkplain #addMessageAcknowledgedListener(Consumer) acknowledged listeners}.
     *
     * @param acknowledgedStanza The acknowledged stanza.
     * @see #addMessageAcknowledgedListener(Consumer)
     */
    public final void markAcknowledged(Stanza acknowledgedStanza) {
        if (acknowledgedStanza != null) {
            removeFromQueue(acknowledgedStanza);
            if (acknowledgedStanza instanceof Message) {
                XmppUtils.notifyEventListeners(messageAcknowledgedListeners, new MessageEvent(this, (Message) acknowledgedStanza, false));
            }
            SendTask<?> sendTask = stanzaTrackingMap.remove(acknowledgedStanza);
            if (sendTask != null) {
                sendTask.receivedByServer();
            }
        }
    }

    protected ExecutorService getIqHandlerExecutor() {
        return IQ_HANDLER_EXECUTOR;
    }

    protected ExecutorService getStanzaListenerExecutor() {
        return stanzaListenerExecutor;
    }

    /**
     * Determines support of another XMPP entity for a given feature.
     * <p>
     * Note that if you want to determine support of another client, you have to provide that client's full JID (user@domain/resource).
     * If you want to determine the server's capabilities provide only the domain JID of the server.
     * </p>
     * This method uses cached information and the presence based entity capabilities (XEP-0115) to determine support. Only if no information is available an explicit service discovery request is made.
     *
     * @param feature The feature, usually defined by an XMPP Extension Protocol, e.g. "urn:xmpp:ping".
     * @param jid     The XMPP entity.
     * @return True, if the XMPP entity supports the given feature; otherwise false.
     */
    public final AsyncResult<Boolean> isSupported(String feature, Jid jid) {
        return getManager(EntityCapabilitiesManager.class).isSupported(feature, jid);
    }

    /**
     * Represents the session status.
     * <p>
     * The following chart illustrates the valid status transitions:
     * </p>
     * <pre>
     * ┌────── INITIAL
     * │          │
     * │          ▼
     * │      CONNECTING ◄────────┐
     * │          │               │
     * │          ▼               │
     * │      CONNECTED ───► DISCONNECTED
     * │          │               ▲
     * │          ▼               │
     * │    AUTHENTICATING        │
     * │          │               │
     * │          ▼               │
     * │    AUTHENTICATED ────────┘
     * │          │
     * │          ▼
     * └─────► CLOSING
     *            │
     *            ▼
     *         CLOSED
     * </pre>
     */
    public enum Status {
        /**
         * The session is in initial state.
         */
        INITIAL,
        /**
         * The session is currently negotiating features.
         */
        CONNECTING,
        /**
         * The session is established with the server, features are negotiated, but we are not yet authenticated.
         */
        CONNECTED,
        /**
         * The session is currently authenticating with the server.
         */
        AUTHENTICATING,
        /**
         * The session has authenticated.
         */
        AUTHENTICATED,
        /**
         * The session has been temporarily disconnected by an exception.
         */
        DISCONNECTED,
        /**
         * The session is closing.
         */
        CLOSING,
        /**
         * The session is closed.
         */
        CLOSED
    }
}
