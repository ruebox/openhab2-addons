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

import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.Stanza;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A send task is the result of a send action and allows to keep track of the sent stanza.
 * <p>
 * This class implements {@link Future}, which {@linkplain Future#isDone()} is done}, when the stanza has been sent to the server.
 *
 * @author Christian Schudt
 * @see XmppSession#sendMessage(Message)
 */
public final class SendTask<S extends Stanza> implements Future<Void> {

    private final S stanza;

    /**
     * Guarded by this.
     */
    private Consumer<S> onAcknowledge;

    private Consumer<S> onSent;

    private BiConsumer<Throwable, S> onFailure;

    /**
     * Guarded by this.
     */
    private boolean receivedByServer;

    private CompletableFuture<Void> sendFuture;

    SendTask(S stanza) {
        this.stanza = stanza;
    }

    /**
     * Gets the stanza, which has been sent.
     *
     * @return The stanza.
     */
    public final S getStanza() {
        return stanza;
    }

    /**
     * Called when the sent stanza has been acknowledged by the server.
     *
     * @param onAcknowledge The consumer.
     */
    public final void onAcknowledge(Consumer<S> onAcknowledge) {
        boolean received;
        synchronized (this) {
            received = receivedByServer;
            this.onAcknowledge = onAcknowledge;
        }
        if (received) {
            onAcknowledge.accept(stanza);
        }
    }

    /**
     * Called, when a stanza has been sent to the server. Note, that this does not mean, that the server received it.
     *
     * @param onSent The callback.
     */
    public final synchronized void onSent(Consumer<S> onSent) {
        this.onSent = Objects.requireNonNull(onSent);
        sendFuture.thenRun(() -> onSent.accept(stanza));
    }

    /**
     * Called, when a send operation failed.
     *
     * @param onFailure The callback.
     */
    public final synchronized void onFailed(BiConsumer<Throwable, S> onFailure) {
        this.onFailure = Objects.requireNonNull(onFailure);
        sendFuture.whenComplete((aVoid, throwable) -> {
            if (throwable != null) {
                onFailure.accept(throwable, stanza);
            }
        });
    }

    synchronized void updateSendFuture(CompletableFuture<Void> sendFuture) {
        this.sendFuture = sendFuture;
        Consumer<S> consumerSent = onSent;
        BiConsumer<Throwable, S> consumerFailed = onFailure;
        if (consumerSent != null) {
            this.sendFuture.thenRun(() -> consumerSent.accept(stanza));
        }
        if (consumerFailed != null) {
            sendFuture.whenComplete((aVoid, throwable) -> {
                if (throwable != null) {
                    consumerFailed.accept(throwable, stanza);
                }
            });
        }
    }

    void receivedByServer() {
        Consumer<S> consumer;
        synchronized (this) {
            receivedByServer = true;
            consumer = onAcknowledge;
        }
        if (consumer != null) {
            consumer.accept(stanza);
        }
    }

    @Override
    public final boolean cancel(boolean mayInterruptIfRunning) {
        return sendFuture.cancel(mayInterruptIfRunning);
    }

    @Override
    public final boolean isCancelled() {
        return sendFuture.isCancelled();
    }

    @Override
    public final boolean isDone() {
        return sendFuture.isDone();
    }

    @Override
    public final Void get() throws InterruptedException, ExecutionException {
        return sendFuture.get();
    }

    @Override
    public final Void get(long timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return sendFuture.get(timeout, timeUnit);
    }
}
