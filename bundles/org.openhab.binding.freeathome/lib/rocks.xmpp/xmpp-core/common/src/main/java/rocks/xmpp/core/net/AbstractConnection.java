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

package rocks.xmpp.core.net;

import rocks.xmpp.core.session.model.SessionOpen;
import rocks.xmpp.core.stream.model.StreamError;
import rocks.xmpp.util.concurrent.CompletionStages;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * An abstract connection implementation.
 *
 * @author Christian Schudt
 */
public abstract class AbstractConnection implements Connection {

    private final AtomicBoolean closed = new AtomicBoolean();

    private final CompletableFuture<Void> closedByPeer = new CompletableFuture<>();

    private final ConnectionConfiguration connectionConfiguration;

    private String streamId;

    protected AbstractConnection(ConnectionConfiguration connectionConfiguration) {
        this.connectionConfiguration = connectionConfiguration;
    }

    @Override
    public final ConnectionConfiguration getConfiguration() {
        return connectionConfiguration;
    }

    @Override
    public boolean isUsingAcknowledgements() {
        return false;
    }

    /**
     * Restarts the stream.
     *
     * @see <a href="https://tools.ietf.org/html/rfc6120#section-4.3.3">RFC 6120 § 4.3.3.  Restarts</a>
     * @see <a href="https://xmpp.org/extensions/xep-0206.html#preconditions-sasl">XEP-0206 Authentication and Resource Binding</a>
     * @see <a href="https://tools.ietf.org/html/rfc7395#section-3.7">RFC 7395 § 3.7.  Stream Restarts</a>
     */
    protected abstract void restartStream();

    /**
     * Closes the XMPP layer stream, e.g. by sending {@code </stream:stream>}.
     *
     * @return The future which is complete, when the stream is closed.
     */
    protected abstract CompletionStage<Void> closeStream();

    /**
     * Closes the underlying physical connection, e.g. a TCP connection.
     *
     * @return The future which is complete, when the connection is closed.
     */
    protected abstract CompletionStage<Void> closeConnection();

    /**
     * @param sessionOpen The stream header.
     */
    protected final synchronized void openedByPeer(SessionOpen sessionOpen) {
        streamId = sessionOpen.getId();
    }

    protected void closedByPeer() {
        closedByPeer.complete(null);
        closeAsync();
    }

    @Override
    public final synchronized String getStreamId() {
        return streamId;
    }

    @Override
    public final CompletionStage<Void> closeAsync() {
        if (closed.compareAndSet(false, true)) {
            // First close XMPP layer stream
            return closeStream()
                    // Then wait for the reception of the peer's closing element or timeout.
                    .thenCompose(v -> closedByPeer.applyToEither(CompletionStages.timeoutAfter(500, TimeUnit.MILLISECONDS), Function.identity()))
                    .handle((aVoid, exc) -> closeConnection())
                    // Then compose this future with the returned channel future, kind of flat mapping it.
                    .thenCompose(Function.identity());
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public final CompletionStage<Void> closeAsync(StreamError streamError) {
        write(streamError);
        return closeAsync();
    }

    /**
     * Closes the connection. This method blocks until everything is closed (max. 1s).
     *
     * @throws Exception If the close failed.
     * @see #closeAsync()
     */
    @Override
    public final void close() throws Exception {
        try {
            closeAsync().toCompletableFuture().get(2, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            } else {
                throw e;
            }
        } catch (InterruptedException e) {
            // Implementers of AutoCloseable are strongly advised to not have the close method throw InterruptedException.
            Thread.currentThread().interrupt();
        }
    }

    /**
     * If the connection is closed.
     *
     * @return True, if closed.
     */
    protected final boolean isClosed() {
        return closed.get();
    }
}
