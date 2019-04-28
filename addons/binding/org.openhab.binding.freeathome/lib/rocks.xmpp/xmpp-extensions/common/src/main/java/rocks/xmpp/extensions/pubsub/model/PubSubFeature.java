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

package rocks.xmpp.extensions.pubsub.model;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Represents the pubsub features.
 *
 * @author Christian Schudt
 */
public enum PubSubFeature {
    /**
     * The default node access model is authorize.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#accessmodels">4.5 Node Access Models</a>
     */
    @XmlEnumValue("access-authorize")
    ACCESS_AUTHORIZE,
    /**
     * The default node access model is open.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#accessmodels">4.5 Node Access Models</a>
     */
    @XmlEnumValue("access-open")
    ACCESS_OPEN,
    /**
     * TThe default node access model is presence.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#accessmodels">4.5 Node Access Models</a>
     */
    @XmlEnumValue("access-presence")
    ACCESS_PRESENCE,
    /**
     * The default node access model is roster.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#accessmodels">4.5 Node Access Models</a>
     */
    @XmlEnumValue("access-roster")
    ACCESS_ROSTER,
    /**
     * The default node access model is whitelist.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#accessmodels">4.5 Node Access Models</a>
     */
    @XmlEnumValue("access-whitelist")
    ACCESS_WHITELIST,
    /**
     * The service supports automatic creation of nodes on first publish.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#publisher-publish-autocreate">7.1.4 Automatic Node Creation</a>
     */
    @XmlEnumValue("auto-create")
    AUTO_CREATE,
    /**
     * The service supports automatic subscription to a nodes based on presence subscription.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#auto-subscribe">9.1 Auto-Subscribe</a>
     */
    @XmlEnumValue("auto-subscribe")
    AUTO_SUBSCRIBE,
    /**
     * Collection nodes are supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0248.html">XEP-0248: PubSub Collection Nodes</a>
     */
    @XmlEnumValue("collections")
    COLLECTIONS,
    /**
     * Configuration of node options is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-configure">8.2 Configure a Node</a>
     */
    @XmlEnumValue("config-node")
    CONFIG_NODE,
    /**
     * Simultaneous creation and configuration of nodes is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-create-and-configure">8.1.3 Create and Configure a Node</a>
     */
    @XmlEnumValue("create-and-configure")
    CREATE_AND_CONFIGURE,
    /**
     * Creation of nodes is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-create">8.1 Create a Node</a>
     */
    @XmlEnumValue("create-nodes")
    CREATE_NODES,
    /**
     * Deletion of items is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#publisher-delete">7.2 Delete an Item from a Node</a>
     */
    @XmlEnumValue("delete-items")
    DELETE_ITEMS,
    /**
     * Deletion of nodes is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-delete">8.4 Delete a Node</a>
     */
    @XmlEnumValue("delete-nodes")
    DELETE_NODES,
    /**
     * The service supports filtering of notifications based on Entity Capabilities.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#filtered-notifications">9.2 Filtered Notifications</a>
     */
    @XmlEnumValue("filtered-notifications")
    FILTERED_NOTIFICATIONS,
    /**
     * Retrieval of pending subscription approvals is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-subreq">8.6 Manage Subscription Requests</a>
     */
    @XmlEnumValue("get-pending")
    GET_PENDING,
    /**
     * Creation of instant nodes is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-create">8.1 Create a Node</a>
     */
    @XmlEnumValue("instant-nodes")
    INSTANT_NODES,
    /**
     * Publishers may specify item identifiers.
     */
    @XmlEnumValue("item-ids")
    ITEM_IDS,
    /**
     * The service supports sending of the last published item to new
     * subscribers and to newly available resources.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#events">4.3 Event Types</a>
     */
    @XmlEnumValue("last-published")
    LAST_PUBLISHED,
    /**
     * Time-based subscriptions are supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#impl-leases">12.18 Time-Based Subscriptions (Leases)</a>
     */
    @XmlEnumValue("leased-subscription")
    LEASED_SUBSCRIPTION,
    /**
     * Node owners may manage subscriptions.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-subscriptions">8.8 Manage Subscriptions</a>
     */
    @XmlEnumValue("manage-subscriptions")
    MANAGE_SUBSCRIPTIONS,
    /**
     * The member affiliation is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#affiliations">4.1 Affiliations</a>
     */
    @XmlEnumValue("member-affiliation")
    MEMBER_AFFILIATION,
    /**
     * Node meta-data is supported.
     */
    @XmlEnumValue("meta-data")
    META_DATA,
    /**
     * Node owners may modify affiliations.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-affiliations">8.9 Manage Affiliations</a>
     */
    @XmlEnumValue("modify-affiliations")
    MODIFY_AFFILIATIONS,
    /**
     * A single leaf node can be associated with multiple collections.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0248.html">XEP-0248: PubSub Collection Nodes</a>
     */
    @XmlEnumValue("multi-collection")
    MULTI_COLLECTION,
    /**
     * The service supports the storage of multiple items per node.
     */
    @XmlEnumValue("multi-items")
    MULTI_ITEMS,
    /**
     * A single entity may subscribe to a node multiple times.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-subscribe-multi">6.1.6 Multiple Subscriptions</a>
     */
    @XmlEnumValue("multi-subscribe")
    MULTI_SUBSCRIBE,
    /**
     * The outcast affiliation is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#affiliations">4.1 Affiliations</a>
     */
    @XmlEnumValue("outcast-affiliation")
    OUTCAST_AFFILIATION,
    /**
     * Persistent items are supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-retrieve-error-persistent">6.5.9.4 Persistent Items Not Supported</a>
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#publisher-delete-error-persistent">7.2.3.5 Persistent Items Not Supported</a>
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-purge-error-nopersist">8.5.3.3 Node Does Not Persist Items</a>
     */
    @XmlEnumValue("persistent-items")
    PERSISTENT_ITEMS,
    /**
     * Presence-based delivery of event notifications is supported.
     */
    @XmlEnumValue("presence-notifications")
    PRESENCE_NOTIFICATIONS,
    /**
     * Implicit presence-based subscriptions are supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#auto-subscribe">9.1 Auto-Subscribe</a>
     */
    @XmlEnumValue("presence-subscribe")
    PRESENCE_SUBSCRIBE,
    /**
     * Publishing items is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#publisher-publish">7.1 Publish an Item to a Node</a>
     */
    @XmlEnumValue("publish")
    PUBLISH,
    /**
     * Publication with publish options is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#publisher-publish-options">7.1.5 Publishing Options</a>
     */
    @XmlEnumValue("publish-options")
    PUBLISH_OPTIONS,
    /**
     * The publish-only affiliation is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#affiliations">4.1 Affiliations</a>
     */
    @XmlEnumValue("publish-only-affiliation")
    PUBLISH_ONLY_AFFILIATION,
    /**
     * The publisher affiliation is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#affiliations">4.1 Affiliations</a>
     */
    @XmlEnumValue("publisher-affiliation")
    PUBLISHER_AFFILIATION,
    /**
     * Purging of nodes is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-purge">8.5 Purge All Node Items</a>
     */
    @XmlEnumValue("purge-nodes")
    PURGE_NODES,
    /**
     * Item retraction is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#publisher-delete">7.2 Delete an Item from a Node</a>
     */
    @XmlEnumValue("retract-items")
    RETRACT_ITEMS,
    /**
     * Retrieval of current affiliations is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#entity-affiliations">5.7 Retrieve Affiliations</a>
     */
    @XmlEnumValue("retrieve-affiliations")
    RETRIEVE_AFFILIATIONS,
    /**
     * Retrieval of default node configuration is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-default">8.3 Request Default Node Configuration Options</a>
     */
    @XmlEnumValue("retrieve-default")
    RETRIEVE_DEFAULT,
    /**
     * Retrieval of default subscription configuration is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscribe-default">6.4 Request Default Subscription Configuration Options</a>
     */
    @XmlEnumValue("retrieve-default-sub")
    RETRIEVE_DEFAULT_SUB,
    /**
     * Item retrieval is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-retrieve">6.5 Retrieve Items from a Node</a>
     */
    @XmlEnumValue("retrieve-items")
    RETRIEVE_ITEMS,
    /**
     * Retrieval of current subscriptions is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#entity-subscriptions">5.6 Retrieve Subscriptions</a>
     */
    @XmlEnumValue("retrieve-subscriptions")
    RETRIEVE_SUBSCRIPTIONS,
    /**
     * Subscribing and unsubscribing are supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-subscribe">6.1 Subscribe to a Node</a>
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-unsubscribe">6.2 Unsubscribe from a Node</a>
     */
    @XmlEnumValue("subscribe")
    SUBSCRIBE,
    /**
     * Configuration of subscription options is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-configure">6.3 Configure Subscription Options</a>
     */
    @XmlEnumValue("subscription-options")
    SUBSCRIPTION_OPTIONS,
    /**
     * Notification of subscription state changes is supported.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#impl-subchange">12.13 Notification of Subscription State Changes</a>
     */
    @XmlEnumValue("subscription-notifications")
    SUBSCRIPTION_NOTIFICATIONS
}
