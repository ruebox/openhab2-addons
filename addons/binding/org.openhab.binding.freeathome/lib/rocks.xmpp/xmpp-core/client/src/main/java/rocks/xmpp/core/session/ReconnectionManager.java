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

import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.stream.model.StreamErrorException;
import rocks.xmpp.core.stream.model.errors.Condition;
import rocks.xmpp.util.XmppUtils;
import rocks.xmpp.util.concurrent.QueuedScheduledExecutorService;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import static rocks.xmpp.core.session.ReconnectionStrategy.onSystemShutdownFirstOrElseSecond;
import static rocks.xmpp.core.session.ReconnectionStrategy.truncatedBinaryExponentialBackoffStrategy;

/**
 * If the connection goes down, this class automatically reconnects, if the user was authenticated.
 * <p>
 * Reconnection is not performed, if the user got disconnected due to an {@code <conflict/>} stream error.
 * </p>
 * The default reconnection strategy is a so called truncated binary exponential back off (as proposed by the XMPP specification),
 * which means that the first reconnection attempt is performed X seconds after the disconnect, where X is between 0 and 60.<br>
 * The second attempt chooses a random number between 0 and 180.<br>
 * The third attempt chooses a random number between 0 and 420.<br>
 * The forth attempt chooses a random number between 0 and 900.<br>
 * The fifth attempt chooses a random number between 0 and 1860.<br>
 * <p>
 * Generally speaking it is <code>2^attempt * 60</code> seconds.
 * This class is unconditionally thread-safe.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/rfcs/rfc6120.html#tcp-reconnect">3.3.  Reconnection</a>
 */
final class ReconnectionManager extends Manager {

    private static final Logger logger = Logger.getLogger(ReconnectionManager.class.getName());

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(XmppUtils.createNamedThreadFactory("XMPP Reconnection Thread"));

    private final ScheduledExecutorService scheduledExecutorService;

    private final ReconnectionStrategy reconnectionStrategy;

    private ScheduledFuture<?> scheduledReconnectingInterval;

    private Instant nextReconnectionAttempt;

    private ReconnectionManager(final XmppSession xmppSession) {
        super(xmppSession, false);
        ReconnectionStrategy configuredStrategy = xmppSession.getConfiguration().getReconnectionStrategy();
        if (configuredStrategy != null) {
            this.reconnectionStrategy = configuredStrategy;
        } else {
            this.reconnectionStrategy = onSystemShutdownFirstOrElseSecond(
                    // on first attempt: 0-60 seconds    (2^1-1 * 60)
                    // on second attempt: 0-180 seconds  (2^2-1 * 60)
                    // on third attempt: 0-420 seconds   (2^3-1 * 60)
                    // on fourth attempt: 0-900 seconds  (2^4-1 * 60)
                    // -> max. 15 minutes
                    truncatedBinaryExponentialBackoffStrategy(60, 4),

                    // on first attempt: 0-10 seconds   (2^1-1 * 10)
                    // on second attempt: 0-30 seconds  (2^2-1 * 10)
                    // on third attempt: 0-70 seconds   (2^3-1 * 10)
                    // on fourth attempt: 0-150 seconds (2^4-1 * 10)
                    // on fifth attempt: 0-310 seconds  (2^5-1 * 10)
                    // -> max. ~ 5 minutes
                    truncatedBinaryExponentialBackoffStrategy(10, 5));
        }
        scheduledExecutorService = new QueuedScheduledExecutorService(EXECUTOR_SERVICE);
    }

    @Override
    protected final void initialize() {
        xmppSession.addSessionStatusListener(e -> {
            switch (e.getStatus()) {
                case DISCONNECTED:
                    // Reconnect if we were connected or logged in and an exception has occurred, that is not a <conflict/> stream error.
                    if (e.getOldStatus() == XmppSession.Status.AUTHENTICATED) {
                        XmppUtils.notifyEventListeners(xmppSession.connectionListeners, new ConnectionEvent(xmppSession, ConnectionEvent.Type.DISCONNECTED, e.getThrowable(), Duration.ZERO));
                        if (reconnectionStrategy.mayReconnect(0, e.getThrowable())) {
                            scheduleReconnection(0, e.getThrowable());
                        }
                    }
                    break;
                case CONNECTED:
                    cancel();
                    break;
                case CLOSED:
                    cancel();
                    scheduledExecutorService.shutdown();
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * Cancels the next reconnection attempt.
     */
    private synchronized void cancel() {
        nextReconnectionAttempt = null;
        if (scheduledReconnectingInterval != null) {
            // Cancel the scheduled timer.
            scheduledReconnectingInterval.cancel(false);
        }
    }

    private synchronized void scheduleReconnection(final int attempt, Throwable throwable) {
        if (isEnabled()) {
            Duration duration = reconnectionStrategy.getNextReconnectionAttempt(attempt, throwable);
            if (attempt == 0) {
                logger.log(Level.FINE, "Disconnect detected. Next reconnection attempt in {0} seconds.", duration.getSeconds());
            } else {
                logger.log(Level.FINE, "Still disconnected after {0} retries. Next reconnection attempt in {1} seconds.", new Object[]{attempt, duration.getSeconds()});
            }

            nextReconnectionAttempt = Instant.now().plus(duration);

            scheduledReconnectingInterval = scheduledExecutorService.scheduleAtFixedRate(() -> {
                Duration remainingDuration;
                synchronized (this) {
                    remainingDuration = Duration.between(Instant.now(), nextReconnectionAttempt);
                }
                if (!remainingDuration.isNegative()) {
                    XmppUtils.notifyEventListeners(xmppSession.connectionListeners, new ConnectionEvent(xmppSession, ConnectionEvent.Type.RECONNECTION_PENDING, throwable, remainingDuration));
                } else {
                    synchronized (this) {
                        scheduledReconnectingInterval.cancel(false);
                    }
                    try {
                        xmppSession.connect();
                        logger.log(Level.FINE, "Reconnection successful.");
                    } catch (XmppException e) {
                        logger.log(Level.FINE, "Reconnection failed.", e);
                        XmppUtils.notifyEventListeners(xmppSession.connectionListeners, new ConnectionEvent(xmppSession, ConnectionEvent.Type.RECONNECTION_FAILED, e, Duration.ZERO));
                        scheduleReconnection(attempt + 1, e);
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
    }

    @Override
    protected final void onDisable() {
        super.onDisable();
        cancel();
    }

    /**
     * A predicate which returns true as soon a system-shutdown stream error has occurred.
     */
    static final class SystemShutdownPredicate implements BiPredicate<Integer, Throwable> {
        private boolean systemShutdown;

        @Override
        public final boolean test(Integer attempt, Throwable cause) {
            if (!systemShutdown || attempt == 0) {
                systemShutdown = cause instanceof StreamErrorException && ((StreamErrorException) cause).getCondition() == Condition.SYSTEM_SHUTDOWN;
            }
            return systemShutdown;
        }
    }
}
