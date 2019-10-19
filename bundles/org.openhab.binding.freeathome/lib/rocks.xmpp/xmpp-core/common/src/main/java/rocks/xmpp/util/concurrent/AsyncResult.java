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

package rocks.xmpp.util.concurrent;

import rocks.xmpp.core.XmppException;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents the result of an asynchronous operation.
 * <p>
 * It implements both {@link Future} and {@link CompletionStage} and is therefore similar to {@link CompletableFuture}, but read-only, i.e. it cannot be completed.
 *
 * @author Christian Schudt
 * @see CompletableFuture
 */
public class AsyncResult<T> implements Future<T>, CompletionStage<T> {

    private final CompletableFuture<T> completableFuture;

    public AsyncResult(CompletionStage<T> completionStage) {
        this.completableFuture = completionStage.toCompletableFuture();
    }

    @Override
    public <U> AsyncResult<U> thenApply(Function<? super T, ? extends U> fn) {
        return new AsyncResult<>(completableFuture.thenApply(fn));
    }

    @Override
    public <U> AsyncResult<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
        return new AsyncResult<>(completableFuture.thenApplyAsync(fn));
    }

    @Override
    public <U> AsyncResult<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
        return new AsyncResult<>(completableFuture.thenApplyAsync(fn, executor));
    }

    @Override
    public AsyncResult<Void> thenAccept(Consumer<? super T> action) {
        return new AsyncResult<>(completableFuture.thenAccept(action));
    }

    @Override
    public AsyncResult<Void> thenAcceptAsync(Consumer<? super T> action) {
        return new AsyncResult<>(completableFuture.thenAcceptAsync(action));
    }

    @Override
    public AsyncResult<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
        return new AsyncResult<>(completableFuture.thenAcceptAsync(action, executor));
    }

    @Override
    public AsyncResult<Void> thenRun(Runnable action) {
        return new AsyncResult<>(completableFuture.thenRun(action));
    }

    @Override
    public AsyncResult<Void> thenRunAsync(Runnable action) {
        return new AsyncResult<>(completableFuture.thenRunAsync(action));
    }

    @Override
    public AsyncResult<Void> thenRunAsync(Runnable action, Executor executor) {
        return new AsyncResult<>(completableFuture.thenRunAsync(action, executor));
    }

    @Override
    public <U, V> AsyncResult<V> thenCombine(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return new AsyncResult<>(completableFuture.thenCombine(other, fn));
    }

    @Override
    public <U, V> AsyncResult<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return new AsyncResult<>(completableFuture.thenCombineAsync(other, fn));
    }

    @Override
    public <U, V> AsyncResult<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
        return new AsyncResult<>(completableFuture.thenCombineAsync(other, fn, executor));
    }

    @Override
    public <U> AsyncResult<Void> thenAcceptBoth(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        return new AsyncResult<>(completableFuture.thenAcceptBoth(other, action));
    }

    @Override
    public <U> AsyncResult<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        return new AsyncResult<>(completableFuture.thenAcceptBothAsync(other, action));
    }

    @Override
    public <U> AsyncResult<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action, Executor executor) {
        return new AsyncResult<>(completableFuture.thenAcceptBothAsync(other, action, executor));
    }

    @Override
    public AsyncResult<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
        return new AsyncResult<>(completableFuture.runAfterBoth(other, action));
    }

    @Override
    public AsyncResult<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
        return new AsyncResult<>(completableFuture.runAfterBothAsync(other, action));
    }

    @Override
    public AsyncResult<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return new AsyncResult<>(completableFuture.runAfterBothAsync(other, action, executor));
    }

    @Override
    public <U> AsyncResult<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return new AsyncResult<>(completableFuture.applyToEither(other, fn));
    }

    @Override
    public <U> AsyncResult<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return new AsyncResult<>(completableFuture.applyToEitherAsync(other, fn));
    }

    @Override
    public <U> AsyncResult<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn, Executor executor) {
        return new AsyncResult<>(completableFuture.applyToEitherAsync(other, fn, executor));
    }

    @Override
    public AsyncResult<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action) {
        return new AsyncResult<>(completableFuture.acceptEither(other, action));
    }

    @Override
    public AsyncResult<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action) {
        return new AsyncResult<>(completableFuture.acceptEitherAsync(other, action));
    }

    @Override
    public AsyncResult<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action, Executor executor) {
        return new AsyncResult<>(completableFuture.acceptEitherAsync(other, action, executor));
    }

    @Override
    public AsyncResult<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
        return new AsyncResult<>(completableFuture.runAfterEither(other, action));
    }

    @Override
    public AsyncResult<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
        return new AsyncResult<>(completableFuture.runAfterEitherAsync(other, action));
    }

    @Override
    public AsyncResult<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return new AsyncResult<>(completableFuture.runAfterEitherAsync(other, action, executor));
    }

    @Override
    public <U> AsyncResult<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
        return new AsyncResult<>(completableFuture.thenCompose(fn));
    }

    @Override
    public <U> AsyncResult<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {
        return new AsyncResult<>(completableFuture.thenComposeAsync(fn));
    }

    @Override
    public <U> AsyncResult<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn, Executor executor) {
        return new AsyncResult<>(completableFuture.thenComposeAsync(fn, executor));
    }

    @Override
    public AsyncResult<T> exceptionally(Function<Throwable, ? extends T> fn) {
        return new AsyncResult<>(completableFuture.exceptionally(fn));
    }

    @Override
    public AsyncResult<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
        return new AsyncResult<>(completableFuture.whenComplete(action));
    }

    @Override
    public AsyncResult<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
        return new AsyncResult<>(completableFuture.whenCompleteAsync(action));
    }

    @Override
    public AsyncResult<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        return new AsyncResult<>(completableFuture.whenCompleteAsync(action, executor));
    }

    @Override
    public <U> AsyncResult<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
        return new AsyncResult<>(completableFuture.handle(fn));
    }

    @Override
    public <U> AsyncResult<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
        return new AsyncResult<>(completableFuture.handleAsync(fn));
    }

    @Override
    public <U> AsyncResult<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
        return new AsyncResult<>(completableFuture.handleAsync(fn, executor));
    }

    @Override
    public final CompletableFuture<T> toCompletableFuture() {
        return completableFuture.whenComplete((result, throwable) -> {
        });
    }

    /**
     * Cancels the processing of this {@link Future}, i.e. completes it with a {@link CancellationException}.
     *
     * @param mayInterruptIfRunning this value has no effect in this
     *                              implementation because interrupts are not used to control
     *                              processing.
     * @return If this {@link Future} is now cancelled.
     * @see #cancel()
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return completableFuture.cancel(mayInterruptIfRunning);
    }

    /**
     * Cancels the processing of this {@link Future}, i.e. completes it with a {@link CancellationException}.
     * <p>
     * This method is a shortcut to {@link #cancel(boolean)}, because the boolean parameter has no effect in this implementation.
     *
     * @return If this {@link Future} is now cancelled.
     */
    public boolean cancel() {
        return cancel(false);
    }

    @Override
    public boolean isCancelled() {
        return completableFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        return completableFuture.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return completableFuture.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return completableFuture.get(timeout, unit);
    }

    /**
     * Waits uninterruptibly on the result of the query and returns it.
     * <p>
     * If the thread, waiting on the result is interrupted, this method continues to block until the result is available.
     *
     * @return The result.
     * @throws XmppException If the response threw an exception.
     */
    public final T getResult() throws XmppException {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    return get();
                } catch (InterruptedException e) {
                    interrupted = true;
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof XmppException) {
                        throw (XmppException) cause;
                    } else if (cause instanceof RuntimeException) {
                        throw (RuntimeException) cause;
                    } else {
                        throw new XmppException(cause);
                    }
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Waits uninterruptibly on the result of the query (with a timeout) and returns it.
     * <p>
     * If the thread, waiting on the result is interrupted, this method continues to block until the result is available or the timeout elapses.
     *
     * @param timeout The timeout
     * @param unit    The time unit.
     * @return The result.
     * @throws XmppException    If the response threw an exception.
     * @throws TimeoutException If the result didn't receive in time.
     */
    public final T getResult(long timeout, TimeUnit unit) throws XmppException, TimeoutException {
        boolean interrupted = false;
        try {
            long remainingNanos = unit.toNanos(timeout);
            long end = System.nanoTime() + remainingNanos;
            while (true) {
                try {
                    return get(remainingNanos, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    interrupted = true;
                    remainingNanos = end - System.nanoTime();
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof XmppException) {
                        throw (XmppException) cause;
                    } else if (cause instanceof RuntimeException) {
                        throw (RuntimeException) cause;
                    } else {
                        throw new XmppException(cause);
                    }
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
