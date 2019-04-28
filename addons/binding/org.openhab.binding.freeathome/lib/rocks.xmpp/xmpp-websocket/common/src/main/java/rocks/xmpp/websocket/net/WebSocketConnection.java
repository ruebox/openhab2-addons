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

package rocks.xmpp.websocket.net;

import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.net.AbstractConnection;
import rocks.xmpp.core.net.ConnectionConfiguration;
import rocks.xmpp.core.session.model.SessionOpen;
import rocks.xmpp.core.stream.StreamHandler;
import rocks.xmpp.core.stream.model.StreamElement;
import rocks.xmpp.core.net.ChannelEncryption;
import rocks.xmpp.websocket.model.Close;
import rocks.xmpp.websocket.model.Open;

import javax.websocket.Session;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

/**
 * An XMPP WebSocket connection.
 *
 * @author Christian Schudt
 * @see <a href="https://tools.ietf.org/html/rfc7395">XMPP Subprotocol for WebSocket</a>
 * @since 0.7.0
 */
public class WebSocketConnection extends AbstractConnection {

    protected final Session session;

    private final CompletionStage<Void> closeFuture;

    private final StreamHandler streamHandler;

    private final Consumer<Throwable> onException;

    protected SessionOpen sessionOpen;

    public WebSocketConnection(Session session, StreamHandler streamHandler, Consumer<Throwable> onException, CompletableFuture<Void> closeFuture, ConnectionConfiguration connectionConfiguration) {
        super(connectionConfiguration);
        this.closeFuture = closeFuture;
        this.session = session;
        this.streamHandler = streamHandler;
        this.onException = onException;
        session.addMessageHandler(StreamElement.class, this::onRead);
    }

    private void onRead(final StreamElement streamElement) {
        if (streamElement instanceof Open) {
            openedByPeer((Open) streamElement);
        } else if (streamElement instanceof Close) {
            closedByPeer();
        }
        try {
            if (streamHandler.handleElement(streamElement)) {
                restartStream();
            }
        } catch (XmppException e) {
            onException.accept(e);
        }
    }

    @Override
    protected void restartStream() {
    }

    @Override
    protected final CompletionStage<Void> closeStream() {
        return send(new Close());
    }

    @Override
    protected CompletionStage<Void> closeConnection() {
        return CompletableFuture.runAsync(() -> {
            try {
                session.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    @Override
    public final CompletionStage<Void> closeFuture() {
        return closeFuture;
    }

    @Override
    public CompletableFuture<Void> send(final StreamElement streamElement) {
        final CompletableFuture<Void> future = write(streamElement);
        flush();
        return future;
    }

    @Override
    public final CompletableFuture<Void> write(final StreamElement streamElement) {
        final CompletableFuture<Void> sendFuture = new CompletableFuture<>();
        session.getAsyncRemote().sendObject(streamElement, result -> {
            if (result.isOK()) {
                sendFuture.complete(null);
            } else {
                sendFuture.completeExceptionally(result.getException());
            }
        });
        return sendFuture;
    }

    @Override
    public final void flush() {
        try {
            session.getAsyncRemote().flushBatch();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public final InetSocketAddress getRemoteAddress() {
        return InetSocketAddress.createUnresolved(session.getRequestURI().getHost(), session.getRequestURI().getPort());
    }

    @Override
    public final CompletionStage<Void> open(final SessionOpen sessionOpen) {
        this.sessionOpen = sessionOpen;
        // Opens the stream
        return send(new Open(sessionOpen.getTo(), sessionOpen.getFrom(), sessionOpen.getId(), sessionOpen.getLanguage()));
    }

    @Override
    public final boolean isSecure() {
        // session.isSecure() does always return false for client connections, also use the configuration.
        return session.isSecure() || getConfiguration().getChannelEncryption() == ChannelEncryption.DIRECT;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder("WebSocket connection at ").append(session.getRequestURI());
        final String streamId = getStreamId();
        if (streamId != null) {
            sb.append(" (").append(streamId).append(')');
        }
        return sb.toString();
    }
}
