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

import java.time.Duration;
import java.util.function.BiPredicate;

/**
 * Combines two reconnection strategies. The primary strategy is used, if {@link ReconnectionStrategy#mayReconnect(int, Throwable)} returns true, otherwise the secondary reconnection strategy is used.
 *
 * @author Christian Schudt
 */
final class HybridReconnectionStrategy implements ReconnectionStrategy {

    private final ReconnectionStrategy primaryStrategy;

    private final ReconnectionStrategy secondaryStrategy;

    private final BiPredicate<Integer, Throwable> predicate;

    public HybridReconnectionStrategy(ReconnectionStrategy primaryStrategy, ReconnectionStrategy secondaryStrategy, BiPredicate<Integer, Throwable> predicate) {
        this.primaryStrategy = primaryStrategy;
        this.secondaryStrategy = secondaryStrategy;
        this.predicate = predicate;
    }

    @Override
    public final Duration getNextReconnectionAttempt(final int attempt, final Throwable cause) {
        return (predicate.test(attempt, cause) ? primaryStrategy : secondaryStrategy).getNextReconnectionAttempt(attempt, cause);
    }
}
