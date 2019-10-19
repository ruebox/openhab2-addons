/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Christian Schudt
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

package rocks.xmpp.core;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.session.ReconnectionStrategy;
import rocks.xmpp.core.stream.model.StreamErrorException;
import rocks.xmpp.core.stream.model.StreamError;
import rocks.xmpp.core.stream.model.errors.Condition;

import java.time.Duration;

/**
 * @author Christian Schudt
 */
public class ReconnectionStrategyTest {

    @Test
    public void testTruncatedBinaryExponentialBackoff() {
        ReconnectionStrategy truncatedBinaryExponentialBackoffStrategy = ReconnectionStrategy.truncatedBinaryExponentialBackoffStrategy(60, 4);
        shouldBackoffBinaryExponentially(truncatedBinaryExponentialBackoffStrategy, null);
    }

    @Test
    public void testAlwaysAfter() {
        ReconnectionStrategy alwaysAfter = ReconnectionStrategy.alwaysAfter(Duration.ofSeconds(15));
        Duration first = alwaysAfter.getNextReconnectionAttempt(0, null);

        Assert.assertEquals(first.getSeconds(), 15);
        Duration second = alwaysAfter.getNextReconnectionAttempt(1, null);
        Assert.assertEquals(second.getSeconds(), 15);
    }

    @Test
    public void testAlwaysAfterRandom() {
        ReconnectionStrategy alwaysRandomlyAfter = ReconnectionStrategy.alwaysRandomlyAfter(Duration.ofSeconds(5), Duration.ofSeconds(7));
        Duration first = alwaysRandomlyAfter.getNextReconnectionAttempt(0, null);

        Assert.assertTrue(first.getSeconds() >= 5 && first.getSeconds() < 7);
        Duration second = alwaysRandomlyAfter.getNextReconnectionAttempt(1, null);
        Assert.assertTrue(second.getSeconds() >= 5 && second.getSeconds() < 7);
    }

    @Test
    public void testAlwaysAfterDurationUnlessSystemShutdown() {
        Throwable cause = new StreamErrorException(new StreamError(Condition.SYSTEM_SHUTDOWN));
        ReconnectionStrategy strategy = ReconnectionStrategy.onSystemShutdownFirstOrElseSecond(
                ReconnectionStrategy.truncatedBinaryExponentialBackoffStrategy(60, 4),
                ReconnectionStrategy.alwaysAfter(Duration.ofSeconds(5)));
        shouldBackoffBinaryExponentially(strategy, cause);

        Assert.assertEquals(strategy.getNextReconnectionAttempt(0, null), Duration.ofSeconds(5));
        Assert.assertEquals(strategy.getNextReconnectionAttempt(1, null), Duration.ofSeconds(5));
    }

    private static void shouldBackoffBinaryExponentially(ReconnectionStrategy strategy, Throwable cause) {
        Duration first = strategy.getNextReconnectionAttempt(0, cause);
        Assert.assertTrue(first.getSeconds() >= 0 && first.getSeconds() < 60);
        Duration second = strategy.getNextReconnectionAttempt(1, null);
        Assert.assertTrue(second.getSeconds() >= 0 && second.getSeconds() < 180);
        Duration third = strategy.getNextReconnectionAttempt(2, null);
        Assert.assertTrue(third.getSeconds() >= 0 && third.getSeconds() < 420);
        Duration fourth = strategy.getNextReconnectionAttempt(3, null);
        Assert.assertTrue(fourth.getSeconds() >= 0 && fourth.getSeconds() < 900);
        Duration fifth = strategy.getNextReconnectionAttempt(4, null);
        Assert.assertTrue(fifth.getSeconds() >= 0 && fifth.getSeconds() < 1860);
        Duration sixth = strategy.getNextReconnectionAttempt(5, null);
        Assert.assertTrue(sixth.getSeconds() >= 0 && sixth.getSeconds() < 1860);
        Duration seventh = strategy.getNextReconnectionAttempt(6, null);
        Assert.assertTrue(seventh.getSeconds() >= 0 && seventh.getSeconds() < 1860);
    }
}
