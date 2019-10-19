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

import rocks.xmpp.addr.Jid;

import java.time.Instant;

/**
 * Represents the {@code <subscription/>} element, which is used in 'pubsub', 'pubsub#event' and 'pubsub#owner' namespace.
 *
 * @author Christian Schudt
 */
public interface Subscription {

    /**
     * Gets the subscriber.
     *
     * @return The subscriber.
     */
    Jid getJid();

    /**
     * Gets the node.
     *
     * @return The node.
     */
    String getNode();

    /**
     * Gets the subscription id, which is used to differentiate between multiple subscriptions for the same entity.
     *
     * @return The subscription id.
     */
    String getSubId();

    /**
     * Gets the subscription state to a node.
     *
     * @return The subscription state.
     */
    SubscriptionState getSubscriptionState();

    /**
     * Gets the expiry of the subscription.
     *
     * @return The expiry.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#impl-leases">12.18 Time-Based Subscriptions (Leases)</a>
     */
    Instant getExpiry();

    /**
     * Indicates if subscription configuration is required.
     *
     * @return True, if subscription configuration is required.
     */
    boolean isConfigurationRequired();

    /**
     * Indicates if subscription options are supported (but not necessarily required).
     *
     * @return True, if subscription options are supported (but not necessarily required).
     */
    boolean isConfigurationSupported();
}
