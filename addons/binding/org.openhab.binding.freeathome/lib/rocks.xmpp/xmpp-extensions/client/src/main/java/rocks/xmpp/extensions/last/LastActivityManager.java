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

package rocks.xmpp.extensions.last;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.AbstractIQHandler;
import rocks.xmpp.core.stanza.IQHandler;
import rocks.xmpp.core.stanza.MessageEvent;
import rocks.xmpp.core.stanza.PresenceEvent;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.core.stanza.model.Presence.Show;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.extensions.idle.model.Idle;
import rocks.xmpp.extensions.last.model.LastActivity;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The implementation of <a href="https://xmpp.org/extensions/xep-0012.html">XEP-0012: Last Activity</a> and <a href="https://xmpp.org/extensions/xep-0256.html">XEP-0256: Last Activity in Presence</a>.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/extensions/xep-0012.html#intro">1. Introduction</a></cite></p>
 * <p>It is often helpful to know the time of the last activity associated with a entity. The canonical usage is to discover when a disconnected user last accessed its server. The 'jabber:iq:last' namespace provides a method for retrieving that information. The 'jabber:iq:last' namespace can also be used to discover or publicize when a connected user was last active on the server (i.e., the user's idle time) or to query servers and components about their current uptime.</p>
 * </blockquote>
 * This class also takes care about the following use case, by automatically appending last activity information to 'away' and 'xa' presences:
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/extensions/xep-0256.html#away">1.2 Away and Extended Away</a></cite></p>
 * <p>When a client automatically sets the user's {@code <show/>} value to "away" or "xa" (extended away), it can indicate when that particular was last active during the current presence session.</p>
 * </blockquote>
 * This manager also automatically adds an {@code <idle/>} extension to outbound presences, if the presence is of type {@linkplain Show#AWAY away} or {@linkplain Show#XA xa}.
 * However, sending such presences is still the responsibility of the application developer, i.e. no presences are sent automatically.
 * <p>
 * By default, idle time is determined by outbound messages and non-away, non-xa presences. E.g. whenever a message is sent, the idle time is reset to the current time.
 * Then, when a 'away' or 'xa' presence is sent, the {@code <idle/>} extension is added with the date of the last sent message.
 * <p>
 * The strategy for determining last user interaction can be changed by {@linkplain #setIdleStrategy(Supplier) setting a supplier} which returns the timestamp of last user interaction.
 * Possible alternative strategies is to track mouse movement or keyboard interaction for which cases you would set a supplier which gets the date of the last mouse movement.
 * <p>
 * Automatic inclusion of last activity information in presence stanzas and support for this protocol can be {@linkplain #setEnabled(boolean)} enabled or disabled}.
 * </p>
 * <h3>Code sample</h3>
 * ```java
 * LastActivityManager lastActivityManager = xmppSession.getManager(LastActivityManager.class);
 * LastActivity lastActivity = lastActivityManager.getLastActivity(Jid.of("juliet@example.com/balcony")).getResult();
 * ```
 *
 * @author Christian Schudt
 */
public final class LastActivityManager extends Manager {

    private final Consumer<PresenceEvent> outboundPresenceListener;

    private final IQHandler iqHandler;

    private Supplier<Instant> idleStrategy;

    private Instant lastSentStanza = Instant.now();

    private final Consumer<MessageEvent> outboundMessageListener;

    private LastActivityManager(final XmppSession xmppSession) {
        super(xmppSession);
        this.idleStrategy = this::getLastSentStanzaTime;
        this.outboundPresenceListener = e -> {
            Presence presence = e.getPresence();
            if (presence.getTo() == null) {
                synchronized (this) {
                    // If an available presence with <show/> value 'away' or 'xa' is sent, append last activity information.
                    if (idleStrategy != null && presence.isAvailable() && EnumSet.of(Presence.Show.AWAY, Presence.Show.XA).contains(presence.getShow())) {
                        Instant idleSince = idleStrategy.get();
                        if (idleSince != null) {
                            // XEP-0319: Last User Interaction in Presence
                            if (!presence.hasExtension(Idle.class)) {
                                presence.addExtension(Idle.since(OffsetDateTime.ofInstant(idleSince, ZoneOffset.UTC)));
                            }
                            // XEP-0256: Last Activity in Presence
                            if (!presence.hasExtension(LastActivity.class)) {
                                presence.addExtension(new LastActivity(getSecondsSince(idleSince), presence.getStatus()));
                            }
                        }
                    } else {
                        lastSentStanza = Instant.now();
                    }
                }
            }
        };
        this.iqHandler = new AbstractIQHandler(IQ.Type.GET) {
            @Override
            protected IQ processRequest(IQ iq) {
                // If someone asks me to get my last activity, reply.
                synchronized (this) {
                    Instant idleSince = idleStrategy != null ? idleStrategy.get() : null;
                    if (idleSince != null) {
                        return iq.createResult(new LastActivity(getSecondsSince(idleSince), null));
                    } else {
                        // A client that does not support the protocol, or that does not wish to divulge this information, MUST return a <service-unavailable/> error.
                        return iq.createError(Condition.SERVICE_UNAVAILABLE);
                    }
                }
            }
        };

        this.outboundMessageListener = e -> {
            synchronized (this) {
                lastSentStanza = Instant.now();
            }
        };
    }

    static long getSecondsSince(Instant date) {
        return Math.max(0, Duration.between(date, Instant.now()).getSeconds());
    }

    /**
     * Gets the time of the last sent message or non-away, non-xa presence.
     * <p>
     * This is the default strategy for determining last user interaction.
     *
     * @return The time of the last sent stanza.
     */
    public synchronized Instant getLastSentStanzaTime() {
        return lastSentStanza;
    }

    /**
     * Sets an idle strategy, i.e. a supplier for last user interaction.
     *
     * @param idleStrategy The strategy.
     * @see #getLastSentStanzaTime()
     */
    public synchronized void setIdleStrategy(Supplier<Instant> idleStrategy) {
        this.idleStrategy = idleStrategy;
    }

    /**
     * Gets the current idle strategy, i.e. a supplier for last user interaction.
     *
     * @return The strategy or null if no strategy is set.
     */
    public synchronized Supplier<Instant> getIdleStrategy() {
        return idleStrategy;
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        xmppSession.addOutboundPresenceListener(outboundPresenceListener);
        xmppSession.addOutboundMessageListener(outboundMessageListener);
        xmppSession.addIQHandler(LastActivity.class, iqHandler);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        xmppSession.removeOutboundPresenceListener(outboundPresenceListener);
        xmppSession.removeOutboundMessageListener(outboundMessageListener);
        xmppSession.removeIQHandler(LastActivity.class);
    }

    /**
     * Gets the last activity of the specified user.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/extensions/xep-0012.html#impl">7. Implementation Notes</a></cite></p>
     * <p>The information contained in an IQ reply for this namespace is inherently ambiguous. Specifically, for a bare JID {@code <localpart@domain.tld>} the information is the time since the JID was last connected to its server; for a full JID {@code <localpart@domain.tld/resource>} the information is the time since the resource was last active in the context of an existing session; and for a bare domain the information is the uptime for the server or component. An application MUST take these differences into account when presenting the information to a human user (if any).</p>
     * </blockquote>
     *
     * @param jid The JID for which the last activity is requested.
     * @return The async result with the last activity of the requested JID or null if the feature is not implemented or a time out has occurred.
     */
    public AsyncResult<LastActivity> getLastActivity(Jid jid) {
        return xmppSession.query(IQ.get(jid, new LastActivity()), LastActivity.class);
    }
}
