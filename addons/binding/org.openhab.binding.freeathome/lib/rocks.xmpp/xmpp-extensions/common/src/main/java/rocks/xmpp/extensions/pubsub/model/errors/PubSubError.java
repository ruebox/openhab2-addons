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

package rocks.xmpp.extensions.pubsub.model.errors;

import rocks.xmpp.extensions.pubsub.model.PubSubFeature;
import rocks.xmpp.util.Strings;

import javax.xml.bind.annotation.XmlTransient;

/**
 * An enclosing class for pubsub errors.
 *
 * @author Christian Schudt
 */
@XmlTransient
public abstract class PubSubError {
    /**
     * The {@code <closed-node/>} pubsub error.
     */
    public static final ClosedNode CLOSED_NODE = new ClosedNode();

    /**
     * The {@code <configuration-required/>} pubsub error.
     */
    public static final ConfigurationRequired CONFIGURATION_REQUIRED = new ConfigurationRequired();

    /**
     * The {@code <invalid-jid/>} pubsub error.
     */
    public static final InvalidJid INVALID_JID = new InvalidJid();

    /**
     * The {@code <invalid-options/>} pubsub error.
     */
    public static final InvalidOptions INVALID_OPTIONS = new InvalidOptions();

    /**
     * The {@code <invalid-payload/>} pubsub error.
     */
    public static final InvalidPayload INVALID_PAYLOAD = new InvalidPayload();

    /**
     * The {@code <invalid-subid/>} pubsub error.
     */
    public static final InvalidSubId INVALID_SUB_ID = new InvalidSubId();

    /**
     * The {@code <item-forbidden/>} pubsub error.
     */
    public static final ItemForbidden ITEM_FORBIDDEN = new ItemForbidden();

    /**
     * The {@code <item-required/>} pubsub error.
     */
    public static final ItemRequired ITEM_REQUIRED = new ItemRequired();

    /**
     * The {@code <jid-required/>} pubsub error.
     */
    public static final JidRequired JID_REQUIRED = new JidRequired();

    /**
     * The {@code <max-items-exceeded/>} pubsub error.
     */
    public static final MaxItemsExceeded MAX_ITEMS_EXCEEDED = new MaxItemsExceeded();

    /**
     * The {@code <max-nodes-exceeded/>} pubsub error.
     */
    public static final MaxNodesExceeded MAX_NODES_EXCEEDED = new MaxNodesExceeded();

    /**
     * The {@code <nodeid-required/>} pubsub error.
     */
    public static final NodeIdRequired NODE_ID_REQUIRED = new NodeIdRequired();

    /**
     * The {@code <not-in-roster-group/>} pubsub error.
     */
    public static final NotInRosterGroup NOT_IN_ROSTER_GROUP = new NotInRosterGroup();

    /**
     * The {@code <not-subscribed/>} pubsub error.
     */
    public static final NotSubscribed NOT_SUBSCRIBED = new NotSubscribed();

    /**
     * The {@code <payload-required/>} pubsub error.
     */
    public static final PayloadRequired PAYLOAD_REQUIRED = new PayloadRequired();

    /**
     * The {@code <payload-too-big/>} pubsub error.
     */
    public static final PayloadTooBig PAYLOAD_TOO_BIG = new PayloadTooBig();

    /**
     * The {@code <pending-subscription/>} pubsub error.
     */
    public static final PendingSubscription PENDING_SUBSCRIPTION = new PendingSubscription();

    /**
     * The {@code <presence-subscription-required/>} pubsub error.
     */
    public static final PresenceSubscriptionRequired PRESENCE_SUBSCRIPTION_REQUIRED = new PresenceSubscriptionRequired();

    /**
     * The {@code <subid-required/>} pubsub error.
     */
    public static final SubIdRequired SUB_ID_REQUIRED = new SubIdRequired();

    /**
     * The {@code <too-many-subscriptions/>} pubsub error.
     */
    public static final TooManySubscriptions TOO_MANY_SUBSCRIPTIONS = new TooManySubscriptions();

    PubSubError() {
    }

    /**
     * Creates an {@code <unsupported/>} pubsub error.
     *
     * @param pubSubFeature The unsupported feature.
     * @return The error.
     */
    public static Unsupported unsupported(PubSubFeature pubSubFeature) {
        return new Unsupported(pubSubFeature);
    }

    @Override
    public String toString() {
        return "PubSub error: " + Strings.toDash(getClass().getSimpleName());
    }
}
