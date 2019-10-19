/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Christian Schudt
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

package rocks.xmpp.websocket.net.client;

import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.Stanza;
import rocks.xmpp.core.stream.client.StreamFeaturesManager;
import rocks.xmpp.core.stream.model.StreamElement;
import rocks.xmpp.extensions.sm.StreamManager;
import rocks.xmpp.extensions.sm.model.StreamManagement;
import rocks.xmpp.util.XmppUtils;
import rocks.xmpp.util.concurrent.QueuedScheduledExecutorService;
import rocks.xmpp.websocket.net.WebSocketConnection;

import javax.websocket.MessageHandler;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A WebSocket connection initiated by a client.
 *
 * @author Christian Schudt
 */
public final class WebSocketClientConnection extends WebSocketConnection {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(XmppUtils.createNamedThreadFactory("WebSocket Ping Scheduler"));

    private final StreamFeaturesManager streamFeaturesManager;

    private final StreamManager streamManager;

    private final Set<String> pings = new CopyOnWriteArraySet<>();

    private ScheduledExecutorService executorService;

    /**
     * Guarded by "this".
     */
    private Future<?> pingFuture;

    /**
     * Guarded by "this".
     */
    private Future<?> pongFuture;

    WebSocketClientConnection(Session session, CompletableFuture<Void> closeFuture, XmppSession xmppSession, WebSocketConnectionConfiguration connectionConfiguration) {
        super(session, xmppSession, xmppSession::notifyException, closeFuture, connectionConfiguration);
        this.streamFeaturesManager = xmppSession.getManager(StreamFeaturesManager.class);
        this.streamManager = xmppSession.getManager(StreamManager.class);
        this.streamFeaturesManager.addFeatureNegotiator(streamManager);
        this.streamManager.reset();
        this.executorService = new QueuedScheduledExecutorService(EXECUTOR_SERVICE);
        session.addMessageHandler(new PongHandler());
        if (connectionConfiguration.getPingInterval() != null && !connectionConfiguration.getPingInterval().isNegative() && !connectionConfiguration.getPingInterval().isZero()) {
            pingFuture = this.executorService.scheduleAtFixedRate(() -> {
                // Send a WebSocket ping in an interval.
                synchronized (this) {
                    try {
                        if (this.session.isOpen()) {
                            String uuid = UUID.randomUUID().toString();
                            if (pings.add(uuid)) {
                                // Send the ping with the UUID as application data, so that we can match it to the pong.
                                this.session.getBasicRemote().sendPing(ByteBuffer.wrap(uuid.getBytes(StandardCharsets.UTF_8)));
                                // Later check if the ping has been answered by a pong.
                                pongFuture = this.executorService.schedule(() -> {
                                    if (pings.remove(uuid)) {
                                        // Ping has not been removed by a corresponding pong (still unanswered).
                                        // Notify the session with an exception.
                                        xmppSession.notifyException(new XmppException("No WebSocket pong received in time."));
                                    }
                                }, xmppSession.getConfiguration().getDefaultResponseTimeout().toMillis(), TimeUnit.MILLISECONDS);
                            }
                        }
                    } catch (IOException e) {
                        xmppSession.notifyException(e);
                    }
                }
            }, 0, connectionConfiguration.getPingInterval().toMillis(), TimeUnit.MILLISECONDS);
        }
        closeFuture().whenComplete(((aVoid, throwable) -> {
            if (throwable != null) {
                xmppSession.notifyException(throwable);
            }
        }));
    }

    @Override
    public final CompletableFuture<Void> send(final StreamElement streamElement) {
        if (streamElement instanceof Stanza) {
            // When about to send a stanza, first put the stanza (paired with the current value of X) in an "unacknowledged" queue.
            this.streamManager.markUnacknowledged((Stanza) streamElement);
        }
        return write(streamElement)
                .thenRun(() -> {
                    if (streamElement instanceof Stanza && streamManager.isActive() && streamManager.getRequestStrategy().test((Stanza) streamElement)) {
                        write(StreamManagement.REQUEST);
                    }
                })
                .thenRun(this::flush);
    }

    @Override
    protected final void restartStream() {
        open(sessionOpen);
    }

    @Override
    public final boolean isUsingAcknowledgements() {
        return streamManager.isActive();
    }

    @Override
    protected CompletionStage<Void> closeConnection() {
        return super.closeConnection().thenRun(() -> {
            streamFeaturesManager.removeFeatureNegotiator(streamManager);

            pings.clear();
            synchronized (this) {
                if (pingFuture != null) {
                    pingFuture.cancel(false);
                    pingFuture = null;
                }
                if (pongFuture != null) {
                    pongFuture.cancel(false);
                    pongFuture = null;
                }
                if (executorService != null) {
                    executorService.shutdown();
                    try {
                        if (!executorService.awaitTermination(50, TimeUnit.MILLISECONDS)) {
                            executorService.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        // (Re-)Cancel if current thread also interrupted
                        executorService.shutdownNow();
                        Thread.currentThread().interrupt();
                    }
                    executorService = null;
                }
            }
        });
    }

    private final class PongHandler implements MessageHandler.Whole<PongMessage> {

        @Override
        public final void onMessage(final PongMessage message) {
            // We received a pong from the server.
            // We can now remove the corresponding ping.
            final byte[] bytes = new byte[message.getApplicationData().limit()];
            message.getApplicationData().get(bytes);
            pings.remove(new String(bytes, StandardCharsets.UTF_8));
        }
    }
}
