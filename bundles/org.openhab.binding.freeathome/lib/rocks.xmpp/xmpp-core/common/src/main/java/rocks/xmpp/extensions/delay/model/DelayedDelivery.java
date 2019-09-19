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

package rocks.xmpp.extensions.delay.model;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.stanza.model.Stanza;
import rocks.xmpp.util.adapters.InstantAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.Instant;
import java.util.Objects;

/**
 * The implementation of the {@code <delay/>} element in the {@code urn:xmpp:delay} namespace.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/extensions/xep-0203.html#protocol">2. Protocol Definition</a></cite></p>
 * <p>The XML namespace defined herein is used to provide timestamp information about data stored for later delivery. The most common uses of this namespace are to stamp:</p>
 * <ul>
 * <li>A message that is sent to an offline entity and stored for later delivery (see <a href="https://xmpp.org/extensions/xep-0160.html">Best Practices for Handling Offline Messages</a>).</li>
 * <li>The last available presence stanza sent by a connected client to a server.</li>
 * <li>Messages cached by a <a href="https://xmpp.org/extensions/xep-0045.html">Multi-User Chat</a> room for delivery to new participants when they join the room.</li>
 * </ul>
 * </blockquote>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0203.html">XEP-0203: Delayed Delivery</a>
 * @see <a href="https://xmpp.org/extensions/xep-0203.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "delay")
public final class DelayedDelivery {

    /**
     * urn:xmpp:delay
     */
    public static final String NAMESPACE = "urn:xmpp:delay";

    @XmlAttribute
    private final Jid from;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantAdapter.class)
    private final Instant stamp;

    @XmlValue
    private final String reason;

    /**
     * Private default constructor for unmarshalling.
     */
    private DelayedDelivery() {
        this.stamp = null;
        this.from = null;
        this.reason = null;
    }

    /**
     * Creates a element with only a timestamp attribute.
     *
     * @param timestamp The timestamp.
     */
    public DelayedDelivery(Instant timestamp) {
        this(timestamp, null, null);
    }

    /**
     * Creates a delayed delivery element with all attributes.
     *
     * @param timestamp The timestamp.
     * @param from      The sender.
     * @param reason    The reason.
     */
    public DelayedDelivery(Instant timestamp, Jid from, String reason) {
        this.stamp = Objects.requireNonNull(timestamp);
        this.from = from;
        this.reason = reason;
    }

    /**
     * Gets the original send date of a stanza, i.e. <code>Instant.now()</code>, if no delayed deliver information is available or the timestamp of delayed delivery.
     *
     * @param stanza The stanza.
     * @return The original send date of a stanza or <code>Instant.now()</code>.
     */
    public static Instant sendDate(Stanza stanza) {
        DelayedDelivery delayedDelivery = stanza.getExtension(DelayedDelivery.class);
        if (delayedDelivery != null) {
            return delayedDelivery.getTimeStamp();
        } else {
            return Instant.now();
        }
    }

    /**
     * Gets the Jabber ID of the entity that originally sent the XML stanza or that delayed the delivery of the stanza (e.g., the address of a multi-user chat room).
     *
     * @return The entity who originally sent the XML stanza.
     */
    public final Jid getFrom() {
        return from;
    }

    /**
     * Gets the time when the XML stanza was originally sent.
     *
     * @return The time when the XML stanza was originally sent.
     */
    public final Instant getTimeStamp() {
        return stamp;
    }

    /**
     * Gets the natural-language description of the reason for the delay.
     *
     * @return The natural-language description of the reason for the delay.
     */
    public final String getReason() {
        return reason;
    }

    @Override
    public final String toString() {
        return "Send date: " + stamp;
    }
}
