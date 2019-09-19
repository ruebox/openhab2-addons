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

package rocks.xmpp.extensions.ping;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.IQEvent;
import rocks.xmpp.core.stanza.IQHandler;
import rocks.xmpp.core.stanza.MessageEvent;
import rocks.xmpp.core.stanza.PresenceEvent;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.StanzaErrorException;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.extensions.ping.handler.PingHandler;
import rocks.xmpp.extensions.ping.model.Ping;
import rocks.xmpp.util.XmppUtils;
import rocks.xmpp.util.concurrent.AsyncResult;
import rocks.xmpp.util.concurrent.QueuedScheduledExecutorService;

import java.time.Duration;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * This class implements the application-level ping mechanism as specified in <a href="https://xmpp.org/extensions/xep-0199.html">XEP-0199: XMPP Ping</a>.
 * <p>
 * If enabled, it periodically pings the server to ensure a stable connection. These pings are not sent as long as other stanzas are sent, because they serve the same purpose (telling the server, that we are still available).
 * <p>
 * For <a href="https://xmpp.org/extensions/xep-0199.html#s2c">Server-To-Client Pings</a> it automatically responds with a result (pong), if enabled.
 * <p>
 * It also allows to ping the server manually (<a href="https://xmpp.org/extensions/xep-0199.html#c2s">Client-To-Server Pings</a>) or to ping other XMPP entities (<a href="https://xmpp.org/extensions/xep-0199.html#e2e">Client-to-Client Pings</a>).
 *
 * @author Christian Schudt
 */
public final class PingManager extends Manager {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(XmppUtils.createNamedThreadFactory("XMPP Scheduled Ping Thread"));

    private final QueuedScheduledExecutorService scheduledExecutorService;

    /**
     * guarded by "this"
     */
    private ScheduledFuture<?> nextPing;

    /**
     * guarded by "this"
     */
    private Duration pingInterval = Duration.ofMinutes(15);

    private final IQHandler iqHandler;

    private final Consumer<MessageEvent> inboundMessageListener;

    private final Consumer<PresenceEvent> inboundPresenceListener;

    private final Consumer<IQEvent> inboundIQListener;

    /**
     * Creates the ping manager.
     *
     * @param xmppSession The underlying XMPP session.
     */
    private PingManager(final XmppSession xmppSession) {
        super(xmppSession, true);
        scheduledExecutorService = new QueuedScheduledExecutorService(EXECUTOR_SERVICE);
        scheduledExecutorService.setRemoveOnCancelPolicy(true);

        this.iqHandler = new PingHandler();
        inboundMessageListener = e -> rescheduleNextPing();
        inboundPresenceListener = e -> rescheduleNextPing();
        inboundIQListener = e -> rescheduleNextPing();

    }

    @Override
    protected final void onEnable() {
        super.onEnable();
        xmppSession.addIQHandler(Ping.class, iqHandler);

        // Reschedule server pings whenever we receive a stanza from the server.
        // When we receive a stanza, we are obviously connected.
        // Pinging should be deferred in this case.
        xmppSession.addInboundMessageListener(inboundMessageListener);
        xmppSession.addInboundPresenceListener(inboundPresenceListener);
        xmppSession.addInboundIQListener(inboundIQListener);
        rescheduleNextPing();
    }

    @Override
    protected final void onDisable() {
        super.onDisable();
        xmppSession.removeIQHandler(Ping.class);
        xmppSession.removeInboundMessageListener(inboundMessageListener);
        xmppSession.removeInboundPresenceListener(inboundPresenceListener);
        xmppSession.removeInboundIQListener(inboundIQListener);
        cancelNextPing();
    }

    /**
     * Pings the given XMPP entity.
     *
     * @param jid The JID to ping.
     * @return The async result with true if a response has been received within the timeout and the recipient is available, false otherwise.
     */
    public final AsyncResult<Boolean> ping(Jid jid) {
        IQ request = IQ.get(jid, Ping.INSTANCE);
        return xmppSession.query(request).handle((iq, e) -> {
            if (e != null) {
                Throwable cause = e instanceof CompletionException ? e.getCause() : e;
                if (cause instanceof RuntimeException) {
                    // Rethrow any RuntimeException, mainly CancellationException
                    throw (RuntimeException) e;
                }
                if (cause instanceof StanzaErrorException) {

                    if (jid != null && jid.isFullJid()) {
                        Jid from = ((StanzaErrorException) cause).getStanza().getFrom();
                        // If we pinged a full JID and the resource if offline, the server will respond on behalf of the user with <service-unavailable/>.
                        // In this case we want to return false, because the intended recipient is unavailable.
                        // If the response came from the full JID, the recipient is online, even if it returned an error.
                        return from != null && from.isFullJid() && from.equals(jid);
                    } else {
                        // If we pinged a bare JID, the server will respond. If it returned a <service-unavailable/> or <feature-not-implemented/> error, it just means it doesn't understand the ping protocol.
                        // Nonetheless an error response is still a valid pong, hence always return true in this case.
                        // If any other error is returned, most likely <remote-server-not-found/>, <remote-server-timeout/>, <gone/> return false.
                        Condition condition = ((StanzaErrorException) cause).getCondition();
                        return condition == Condition.SERVICE_UNAVAILABLE || condition == Condition.FEATURE_NOT_IMPLEMENTED;
                    }
                }
            }
            return true;
        });
    }

    /**
     * Pings the connected server.
     *
     * @return The async result with true if a response has been received, false otherwise.
     */
    public final AsyncResult<Boolean> pingServer() {
        return ping(xmppSession.getDomain());
    }

    /**
     * Gets the ping interval. The default ping interval is 900 seconds (15 minutes).
     *
     * @return The ping interval in seconds.
     * @see #setPingInterval(Duration)
     */
    public final synchronized Duration getPingInterval() {
        return pingInterval;
    }

    /**
     * Sets the automatic ping interval. Any scheduled future ping is canceled and a new ping is scheduled after the specified interval.
     *
     * @param pingInterval The ping interval in seconds.
     * @see #getPingInterval()
     */
    public final synchronized void setPingInterval(Duration pingInterval) {
        this.pingInterval = pingInterval;
        rescheduleNextPing();
    }

    private synchronized void rescheduleNextPing() {
        // Reschedule in a separate thread, so that it won't interrupt the "pinging" thread due to the cancel, which then causes the ping to fail.
        if (pingInterval != null && !pingInterval.isNegative() && !scheduledExecutorService.isShutdown()) {
            cancelNextPing();
            nextPing = scheduledExecutorService.schedule(() -> {
                if (isEnabled() && xmppSession.getStatus() == XmppSession.Status.AUTHENTICATED) {
                    pingServer().thenAccept(result -> {
                        if (!result) {
                            try {
                                throw new XmppException("Server ping failed.");
                            } catch (XmppException e) {
                                xmppSession.notifyException(e);
                            }
                        }
                    });
                }
                // Rescheduling of the next ping is already done by the IQ response of the ping.
            }, pingInterval.getSeconds(), TimeUnit.SECONDS);
        }
    }

    /**
     * If a ping has been scheduled, it will be canceled.
     */
    private synchronized void cancelNextPing() {
        if (nextPing != null) {
            nextPing.cancel(false);
            nextPing = null;
        }
    }

    @Override
    protected void dispose() {
        // Shutdown the ping executor service and cancel the next ping.
        synchronized (this) {
            cancelNextPing();
            scheduledExecutorService.shutdownNow();
        }
    }
}
