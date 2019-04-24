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

package rocks.xmpp.extensions.idle.model;

import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.extensions.delay.model.DelayedDelivery;
import rocks.xmpp.extensions.last.model.LastActivity;
import rocks.xmpp.util.adapters.OffsetDateTimeAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.Objects;

/**
 * The implementation of the {@code <idle/>} element in the {@code urn:xmpp:idle:1} namespace.
 * <p>
 * It also provides a {@linkplain #timeFromPresence(Presence) convenient method}, which gets the idle time from a presence with respect to <a href="https://xmpp.org/extensions/xep-0319.html">XEP-0319: Last User Interaction in Presence</a> and the superseded <a href="https://xmpp.org/extensions/xep-0256.html">XEP-0256: Last Activity in Presence</a>.
 * </p>
 * <h3>Adding Last User Interaction to Presence</h3>
 * ```java
 * presence.addExtension(Idle.since(OffsetDateTime.now()));
 * ```
 * <h3>Getting Last User Interaction from Presence</h3>
 * The following method takes the very similar <a href="https://xmpp.org/extensions/xep-0256.html">XEP-0256: Last Activity in Presence</a> extension into account as well.
 * Delayed Delivery information on the presence is respected, too.
 * ```java
 * Instant idleSince = Idle.timeFromPresence(presence);
 * ```
 *
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0319.html">XEP-0319: Last User Interaction in Presence</a>
 */
@XmlRootElement
public final class Idle {

    /**
     * urn:xmpp:idle:1
     */
    public static final String NAMESPACE = "urn:xmpp:idle:1";

    @XmlAttribute
    @XmlJavaTypeAdapter(OffsetDateTimeAdapter.class)
    private final OffsetDateTime since;

    private Idle() {
        this.since = null;
    }

    /**
     * @param since The date/time since the entity is idle.
     */
    private Idle(OffsetDateTime since) {
        this.since = since;
    }

    /**
     * Creates an idle element with the date since when the user is idle.
     *
     * @param since The date.
     * @return The idle element.
     */
    public static Idle since(OffsetDateTime since) {
        return new Idle(Objects.requireNonNull(since));
    }

    /**
     * Extracts the idle time from a presence.
     * <p>
     * This methods first checks for a <a href="https://xmpp.org/extensions/xep-0319.html">XEP-0319: Last User Interaction in Presence</a> extension and uses <a href="https://xmpp.org/extensions/xep-0256.html">XEP-0256: Last Activity in Presence</a> semantics as fallback to determine the idle time.
     * <p>
     * Delayed delivery information is respected.
     *
     * @param presence The presence.
     * @return The idle time or null if the presence contains no idle information.
     */
    public static Instant timeFromPresence(Presence presence) {
        // Check XEP-0319: Last User Interaction in Presence
        Idle idle = presence.getExtension(Idle.class);
        if (idle != null) {
            return idle.getSince().toInstant();
        }
        LastActivity lastActivity = presence.getExtension(LastActivity.class);
        // Check XEP-0256: Last Activity in Presence
        // When a client automatically sets the user's <show/> value to "away" or "xa" (extended away), it can indicate when that particular was last active during the current presence session.
        if (lastActivity != null && EnumSet.of(Presence.Show.AWAY, Presence.Show.XA).contains(presence.getShow())) {
            return DelayedDelivery.sendDate(presence).minusSeconds(lastActivity.getSeconds());
        }
        return null;
    }

    /**
     * Gets the date/time since the entity is idle.
     *
     * @return The date/time since the entity is idle.
     */
    public final OffsetDateTime getSince() {
        return since;
    }

    @Override
    public final String toString() {
        return "Idle since " + since;
    }
}
