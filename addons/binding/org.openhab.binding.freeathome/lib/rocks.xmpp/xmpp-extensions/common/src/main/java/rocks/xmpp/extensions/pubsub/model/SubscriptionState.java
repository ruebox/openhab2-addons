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

package rocks.xmpp.extensions.pubsub.model;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Represents the subscription state to a node.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0060.html#substates">4.2 Subscription States</a>
 */
public enum SubscriptionState {
    /**
     * The node MUST NOT send event notifications or payloads to the Entity.
     */
    @XmlEnumValue("none")
    NONE,
    /**
     * An entity has requested to subscribe to a node and the request has not yet been approved by a node owner. The node MUST NOT send event notifications or payloads to the entity while it is in this state.
     */
    @XmlEnumValue("pending")
    PENDING,
    /**
     * An entity is subscribed to a node. The node MUST send all event notifications (and, if configured, payloads) to the entity while it is in this state (subject to subscriber configuration and content filtering).
     */
    @XmlEnumValue("subscribed")
    SUBSCRIBED,
    /**
     * An entity has subscribed but its subscription options have not yet been configured. The node MAY send event notifications or payloads to the entity while it is in this state. The service MAY timeout unconfigured subscriptions.
     */
    @XmlEnumValue("unconfigured")
    UNCONFIGURED,
}