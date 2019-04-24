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

import rocks.xmpp.util.XmppUtils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Utility class for creating different kinds of {@link java.util.concurrent.CompletionStage}.
 *
 * @author Christian Schudt
 */
public final class CompletionStages {

    private static final ScheduledExecutorService TIMEOUT_EXECUTOR = Executors.newSingleThreadScheduledExecutor(XmppUtils.createNamedThreadFactory("timeout-scheduler"));

    private CompletionStages() {
    }

    /**
     * Creates a new completion stage which uses a fallback stage in case the primary stage completes exceptionally.
     *
     * @param stage    The primary stage.
     * @param fallback A function, which returns a fallback stage, in case the primary one fails exceptionally.
     * @param <T>      The type.
     * @return The completion stage with a fallback.
     */
    public static <T> CompletionStage<T> withFallback(final CompletionStage<T> stage,
                                                      final BiFunction<CompletionStage<T>, Throwable, ? extends CompletionStage<T>> fallback) {
        return stage.handle((response, error) -> error)
                .thenCompose(error -> error != null ? fallback.apply(stage, error) : stage);
    }

    /**
     * Returns a completion stage, which is complete when all the completion stages are complete.
     * The lists of each stage are flat mapped into one list, so that the returned stage has one accumulated list.
     *
     * @param stages The stages.
     * @param <T>    The type.
     * @return The future.
     */
    public static <T> CompletionStage<List<T>> allOf(final Collection<? extends CompletionStage<List<T>>> stages) {
        // First convert the list of stages to an array of CompletableFuture.
        // Then use CompletableFuture.allOf to combine them all.
        return CompletableFuture.allOf(stages.stream().map(CompletionStage::toCompletableFuture).collect(Collectors.toList()).toArray(new CompletableFuture[stages.size()]))
                .thenApply(result ->
                        stages.stream()
                                // Get the result of each future (List<T>)
                                .map(stage -> stage.toCompletableFuture().join())
                                        // Map the List<List<T>> to one stream
                                .flatMap(Collection::stream)
                                        // Collect all items into one list.
                                .collect(Collectors.toList()));
    }

    /**
     * Creates a completion stage, which times out after the specified time, i.e. it completes exceptionally with a {@link TimeoutException}.
     *
     * @param delay The delay.
     * @param unit  The time unit.
     * @param <T>   The type.
     * @return The stage.
     */
    public static <T> CompletionStage<T> timeoutAfter(final long delay, final TimeUnit unit) {
        return timeoutAfter(delay, unit, () -> new TimeoutException("Timeout after " + delay + ' ' + unit));
    }

    /**
     * Creates a completion stage, which times out after the specified time, i.e. it completes exceptionally with the supplied exception.
     *
     * @param delay             The delay.
     * @param unit              The time unit.
     * @param throwableSupplier The supplier for an exception which occurs on timeout.
     * @param <T>               The type.
     * @return The stage.
     */
    public static <T> CompletionStage<T> timeoutAfter(final long delay, final TimeUnit unit, final Supplier<Throwable> throwableSupplier) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        TIMEOUT_EXECUTOR.schedule(() ->
                completableFuture.completeExceptionally(throwableSupplier.get())
                , delay, unit);
        return completableFuture;
    }
}
