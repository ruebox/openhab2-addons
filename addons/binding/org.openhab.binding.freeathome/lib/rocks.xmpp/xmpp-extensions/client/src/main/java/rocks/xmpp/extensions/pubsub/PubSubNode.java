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

package rocks.xmpp.extensions.pubsub;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.Extension;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.disco.ServiceDiscoveryManager;
import rocks.xmpp.extensions.disco.model.info.Identity;
import rocks.xmpp.extensions.pubsub.model.Affiliation;
import rocks.xmpp.extensions.pubsub.model.Item;
import rocks.xmpp.extensions.pubsub.model.NodeConfiguration;
import rocks.xmpp.extensions.pubsub.model.NodeMetaData;
import rocks.xmpp.extensions.pubsub.model.NodeType;
import rocks.xmpp.extensions.pubsub.model.PubSub;
import rocks.xmpp.extensions.pubsub.model.PublishOptions;
import rocks.xmpp.extensions.pubsub.model.SubscribeOptions;
import rocks.xmpp.extensions.pubsub.model.Subscription;
import rocks.xmpp.extensions.pubsub.model.owner.PubSubOwner;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class represents a single pubsub node.
 * <p>
 * It allows you to create the node on the pubsub service, subscribe or unsubscribe from the node, retrieve items from it, publish items to it, etc.
 *
 * @author Christian Schudt
 */
public final class PubSubNode {

    private final Jid pubSubServiceAddress;

    private final XmppSession xmppSession;

    private final ServiceDiscoveryManager serviceDiscoveryManager;

    private volatile NodeType type;

    private volatile String nodeId;

    PubSubNode(String nodeId, Jid pubSubServiceAddress, XmppSession xmppSession) {
        // If a node is a leaf node rather than a collection node and items have been published to the node, the service MAY return one <item/> element for each published item as described in the Discover Items for a Node section of this document, however such items MUST NOT include a 'node' attribute (since they are published items, not nodes).
        this(nodeId, nodeId == null ? NodeType.LEAF : NodeType.COLLECTION, pubSubServiceAddress, xmppSession);
    }

    PubSubNode(String nodeId, NodeType type, Jid pubSubServiceAddress, XmppSession xmppSession) {
        this.nodeId = nodeId;
        this.type = type;
        this.pubSubServiceAddress = pubSubServiceAddress;
        this.xmppSession = xmppSession;
        this.serviceDiscoveryManager = xmppSession.getManager(ServiceDiscoveryManager.class);
    }

    /**
     * Discovers the node info, which consists of a node name, type and meta data.
     *
     * @return The async result with the node info.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#entity-info">5.3 Discover Node Information</a>
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#entity-metadata">5.4 Discover Node Metadata</a>
     */
    public AsyncResult<NodeMetaData> discoverNodeMetaData() {
        if (nodeId == null) {
            throw new IllegalStateException("nodeId must not be null.");
        }
        return serviceDiscoveryManager.discoverInformation(pubSubServiceAddress, nodeId).thenApply(infoNode -> {
            Identity identity = null;
            NodeMetaData metaDataForm = null;

            if (infoNode != null) {
                Set<Identity> identities = infoNode.getIdentities();
                Iterator<Identity> iterator = identities.iterator();
                if (iterator.hasNext()) {
                    identity = iterator.next();
                }
                for (DataForm dataForm : infoNode.getExtensions()) {
                    String formType = dataForm.getFormType();
                    if (NodeMetaData.FORM_TYPE.equals(formType)) {
                        metaDataForm = new NodeMetaData(dataForm);
                        break;
                    }
                }
            }
            // Assign the node type.
            type = identity != null ? "collection".equals(identity.getType()) ? NodeType.COLLECTION : NodeType.LEAF : NodeType.LEAF;
            return metaDataForm;
        });
    }

    /**
     * Discovers the items for this node.
     *
     * @return The async result with the items.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#entity-discoveritems">5.5 Discover Items for a Node</a>
     */
    public AsyncResult<List<Item>> discoverItems() {
        return serviceDiscoveryManager.discoverItems(pubSubServiceAddress, nodeId).thenApply(itemNode -> {
            List<Item> result = new ArrayList<>();
            for (final rocks.xmpp.extensions.disco.model.items.Item item : itemNode.getItems()) {
                // The 'name' attribute of each Service Discovery item MUST contain its ItemID
                result.add(new Item() {
                    @Override
                    public Object getPayload() {
                        return null;
                    }

                    @Override
                    public String getId() {
                        return item.getName();
                    }

                    @Override
                    public String getNode() {
                        return null;
                    }

                    @Override
                    public String getPublisher() {
                        return null;
                    }
                });
            }
            return result;
        });
    }

    /**
     * Gets the subscriptions for this node.
     *
     * @return The async result with the subscriptions for the node.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#entity-subscriptions">5.6 Retrieve Subscriptions</a>
     */
    public AsyncResult<List<Subscription>> getSubscriptions() {
        return xmppSession.query(IQ.get(pubSubServiceAddress, PubSub.withSubscriptions(nodeId))).thenApply(result ->
                result.getExtension(PubSub.class).getSubscriptions());
    }


    /**
     * Gets the affiliations for this node.
     *
     * @return The async result with the affiliations for all nodes.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#entity-affiliations">5.7 Retrieve Affiliations</a>
     */
    public AsyncResult<List<Affiliation>> getAffiliations() {
        return xmppSession.query(IQ.get(pubSubServiceAddress, PubSub.withAffiliations(nodeId))).thenApply(result ->
                result.getExtension(PubSub.class).getAffiliations());
    }

    /**
     * Subscribes to this node.
     *
     * @return The async result with the subscription.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-subscribe">6.1 Subscribe to a Node</a>
     */
    public AsyncResult<Subscription> subscribe() {
        return subscribe(xmppSession.getConnectedResource().asBareJid(), null);
    }

    /**
     * Subscribes to and configures this node.
     *
     * @param subscribeOptions The configuration form.
     * @return The async result with the subscription.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-configure-subandconfig">6.3.7 Subscribe and Configure</a>
     */
    public AsyncResult<Subscription> subscribe(SubscribeOptions subscribeOptions) {
        return subscribe(xmppSession.getConnectedResource().asBareJid(), subscribeOptions);
    }

    /**
     * Subscribes to this node.
     *
     * @param jid The JID which is subscribed.
     * @return The async result with the subscription.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-subscribe">6.1 Subscribe to a Node</a>
     */
    public final AsyncResult<Subscription> subscribe(final Jid jid) {
        return subscribe(jid, null);
    }

    /**
     * Subscribes to and configures this node.
     *
     * @param jid              The JID which is subscribed.
     * @param subscribeOptions The configuration form.
     * @return The async result with the subscription.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-configure-subandconfig">6.3.7 Subscribe and Configure</a>
     */
    public final AsyncResult<Subscription> subscribe(final Jid jid, final SubscribeOptions subscribeOptions) {
        return xmppSession.query(IQ.set(pubSubServiceAddress, PubSub.withSubscribe(Objects.requireNonNull(nodeId, "nodeId must not be null"), Objects.requireNonNull(jid), subscribeOptions != null ? subscribeOptions.getDataForm() : null))).thenApply(result ->
                result.getExtension(PubSub.class).getSubscription());
    }

    /**
     * Unsubscribes from this node.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-unsubscribe">6.2 Unsubscribe from a Node</a>
     */
    public void unsubscribe() {
        this.unsubscribe(null);
    }

    /**
     * Unsubscribes from this node.
     *
     * @param subscriptionId The subscription id.
     * @return The async result.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-unsubscribe">6.2 Unsubscribe from a Node</a>
     */
    public AsyncResult<IQ> unsubscribe(String subscriptionId) {
        return xmppSession.query(IQ.set(pubSubServiceAddress, PubSub.withUnsubscribe(nodeId, xmppSession.getConnectedResource().asBareJid(), subscriptionId)));
    }

    /**
     * Gets the (default) subscription options for this node.
     *
     * @param defaultOptions Whether to get the default options or not.
     * @return The async result with the data form.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-configure-request">6.3.2 Request</a>
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-configure-submit">6.4 Request Default Subscription Configuration Options</a>
     * @see #configureSubscription(rocks.xmpp.extensions.pubsub.model.SubscribeOptions)
     */
    public AsyncResult<SubscribeOptions> getSubscriptionOptions(boolean defaultOptions) {
        if (defaultOptions) {
            return xmppSession.query(IQ.get(pubSubServiceAddress, PubSub.withDefault(nodeId))).thenApply(result ->
                    new SubscribeOptions(result.getExtension(PubSub.class).getDefault().getDataForm()));
        } else {
            return getSubscriptionOptions();
        }
    }

    /**
     * Gets the subscription options for this node.
     *
     * @return The async result with the data form.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-configure-request">6.3.2 Request</a>
     * @see #configureSubscription(rocks.xmpp.extensions.pubsub.model.SubscribeOptions)
     */
    public AsyncResult<SubscribeOptions> getSubscriptionOptions() {
        return getSubscriptionOptions(null);
    }

    /**
     * Gets the subscription options for this node.
     *
     * @param subId The subscription id.
     * @return The async result with the data form.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-configure-request">6.3.2 Request</a>
     * @see #configureSubscription(rocks.xmpp.extensions.pubsub.model.SubscribeOptions)
     */
    public AsyncResult<SubscribeOptions> getSubscriptionOptions(String subId) {
        return xmppSession.query(IQ.get(pubSubServiceAddress, PubSub.withOptions(nodeId, xmppSession.getConnectedResource().asBareJid(), subId, null))).thenApply(result ->
                new SubscribeOptions(result.getExtension(PubSub.class).getOptions().getDataForm()));
    }

    /**
     * Configures the subscription options for this node.
     *
     * @param subscribeOptions The subscription options form.
     * @return The async result.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-configure-submit">6.3.5 Form Submission</a>
     */
    public AsyncResult<IQ> configureSubscription(SubscribeOptions subscribeOptions) {
        return xmppSession.query(IQ.set(pubSubServiceAddress, PubSub.withOptions(nodeId, xmppSession.getConnectedResource().asBareJid(), null, subscribeOptions != null ? subscribeOptions.getDataForm() : null)));
    }

    /**
     * Gets all items for this node.
     *
     * @return The async result with the items.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-retrieve-requestall">6.5.2 Requesting All Items</a>
     */
    public AsyncResult<List<Item>> getItems() {
        return xmppSession.query(IQ.get(pubSubServiceAddress, PubSub.withItems(nodeId))).thenApply(result ->
                result.getExtension(PubSub.class).getItems());
    }

    /**
     * Gets one or more items with a given item id for a specific node.
     *
     * @param ids The item ids.
     * @return The async result with the items.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-retrieve-returnnotify">6.5.6 Returning Notifications Only</a>
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-retrieve-requestone">6.5.8 Requesting a Particular Item</a>
     */
    public AsyncResult<List<Item>> getItems(String... ids) {
        return xmppSession.query(IQ.get(pubSubServiceAddress, PubSub.withItems(nodeId, ids))).thenApply(result ->
                result.getExtension(PubSub.class).getItems());
    }

    /**
     * Gets the most recent items.
     *
     * @param maxItems The maximal number of items.
     * @return The async result with the items.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-retrieve-requestrecent">6.5.7 Requesting the Most Recent Items</a>
     */
    public AsyncResult<List<Item>> getItems(int maxItems) {
        return xmppSession.query(IQ.get(pubSubServiceAddress, PubSub.withItems(nodeId, maxItems))).thenApply(result ->
                result.getExtension(PubSub.class).getItems());
    }

    /**
     * Publishes an item to this node.
     *
     * @param item The item to be published. Note that this item must be known to the session, so that it can be marshalled into XML.
     * @return The async result with the item id, generated by the pubsub service.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#publisher-publish">7.1 Publish an Item to a Node</a>
     * @see rocks.xmpp.core.session.XmppSessionConfiguration.Builder#extensions(Extension...)
     */
    public AsyncResult<String> publish(Object item) {
        return publish(null, item);
    }

    /**
     * Publishes an item to this node.
     *
     * @param item           The item to be published. Note that this item must be known to the session, so that it can be marshalled into XML.
     * @param publishOptions The optional publish options.
     * @return The async result with the item id, generated by the pubsub service.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#publisher-publish">7.1 Publish an Item to a Node</a>
     * @see rocks.xmpp.core.session.XmppSessionConfiguration.Builder#extensions(Extension...)
     */
    public AsyncResult<String> publish(Object item, PublishOptions publishOptions) {
        return publish(null, item, publishOptions);
    }


    /**
     * Publishes an item to this node.
     *
     * @param id   The item's id.
     * @param item The item to be published. Note that this item must be known to the session, so that it can be marshalled into XML.
     * @return The async result with the item id.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#publisher-publish">7.1 Publish an Item to a Node</a>
     * @see rocks.xmpp.core.session.XmppSessionConfiguration.Builder#extensions(Extension...)
     */
    public AsyncResult<String> publish(String id, Object item) {
        return publish(id, item, null);
    }

    /**
     * Publishes an item to this node.
     *
     * @param id             The item's id.
     * @param item           The item to be published. Note that this item must be known to the session, so that it can be marshalled into XML.
     * @param publishOptions The optional publish options.
     * @return The async result with the item id.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#publisher-publish-options">7.1.5 Publishing Options</a>
     * @see rocks.xmpp.core.session.XmppSessionConfiguration.Builder#extensions(Extension...)
     */
    public AsyncResult<String> publish(String id, Object item, PublishOptions publishOptions) {
        return xmppSession.query(IQ.set(pubSubServiceAddress, PubSub.withPublish(nodeId, id, item, publishOptions != null ? publishOptions.getDataForm() : null))).thenApply(result -> {
            PubSub pubSub = result.getExtension(PubSub.class);
            if (pubSub != null && pubSub.getPublish() != null && pubSub.getPublish().getItem() != null) {
                return pubSub.getPublish().getItem().getId();
            }
            return id;
        });
    }

    /**
     * Deletes an item from this node.
     *
     * @param id     The item id.
     * @param notify If the pubsub service shall notify the subscribers about the deletion.
     * @return The async result.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#publisher-delete">7.2 Delete an Item from a Node</a>
     */
    public AsyncResult<IQ> deleteItem(String id, boolean notify) {
        return xmppSession.query(IQ.set(pubSubServiceAddress, PubSub.withRetract(nodeId, id, notify)));
    }

    /**
     * Creates the node on the remote pubsub service.
     *
     * @return The async result with the node id, if it wasn't already set.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-create">8.1 Create a Node</a>
     */
    public AsyncResult<String> create() {
        return create(null);
    }

    /**
     * Creates and configures this node on the remote pubsub service.
     *
     * @param nodeConfiguration The configuration form.
     * @return The async result with the node id.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-create-and-configure">8.1.3 Create and Configure a Node</a>
     */
    public AsyncResult<String> create(NodeConfiguration nodeConfiguration) {
        return xmppSession.query(IQ.set(pubSubServiceAddress, PubSub.withCreate(nodeId, nodeConfiguration != null ? nodeConfiguration.getDataForm() : null))).thenApply(result -> {
            if (nodeId != null) {
                return nodeId;
            }
            PubSub pubSub = result.getExtension(PubSub.class);
            if (pubSub != null) {
                String generatedNodeId = pubSub.getNode();
                if (generatedNodeId != null) {
                    this.nodeId = generatedNodeId;
                }
            }
            return nodeId;
        });
    }

    /**
     * Gets the node configuration form.
     *
     * @return The async result with the configuration form.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-configure-request">8.2.1 Request</a>
     */
    public AsyncResult<NodeConfiguration> getNodeConfiguration() {
        return xmppSession.query(IQ.get(pubSubServiceAddress, PubSubOwner.withConfigure(nodeId))).thenApply(result -> {
            PubSubOwner pubSubOwner = result.getExtension(PubSubOwner.class);
            return new NodeConfiguration(pubSubOwner.getConfigurationForm());
        });
    }

    /**
     * Configures the node by submitting the configuration form.
     *
     * @param nodeConfiguration The configuration form.
     * @return The async result.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-configure-submit">8.2.4 Form Submission</a>
     */
    public AsyncResult<IQ> configureNode(NodeConfiguration nodeConfiguration) {
        return xmppSession.query(IQ.set(pubSubServiceAddress, PubSubOwner.withConfigure(nodeId, nodeConfiguration.getDataForm())));
    }

    /**
     * Deletes this node on the pubsub service.
     *
     * @return The async result.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-delete">8.4 Delete a Node</a>
     */
    public AsyncResult<IQ> delete() {
        return xmppSession.query(IQ.set(pubSubServiceAddress, PubSubOwner.withDelete(nodeId)));
    }

    /**
     * Deletes this node and specifies a replacement node.
     *
     * @param uri The replacement node.
     * @return The async result.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-delete">8.4 Delete a Node</a>
     */
    public AsyncResult<IQ> delete(URI uri) {
        return xmppSession.query(IQ.set(pubSubServiceAddress, PubSubOwner.withDelete(nodeId, uri)));
    }

    /**
     * Purges this node of all published items.
     *
     * @return The async result.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-purge">8.5 Purge All Node Items</a>
     */
    public AsyncResult<IQ> purge() {
        return xmppSession.query(IQ.set(pubSubServiceAddress, PubSubOwner.withPurge(nodeId)));
    }

    /**
     * Discovers the (sub-)nodes, which hierarchically reside under this node, e.g. the "second-level" nodes.
     *
     * @return The async result with the discovered pubsub nodes.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#entity-nodes">5.2 Discover Nodes</a>
     */
    public AsyncResult<List<PubSubNode>> discoverNodes() {
        return serviceDiscoveryManager.discoverItems(pubSubServiceAddress, nodeId).thenApply(itemNode ->
                itemNode.getItems().stream()
                        .map(item -> new PubSubNode(item.getNode(), pubSubServiceAddress, xmppSession))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets the node id.
     *
     * @return The node id.
     */
    public String getId() {
        return nodeId;
    }

    /**
     * Gets the type of this node.
     *
     * @return The type.
     */
    public NodeType getType() {
        return type;
    }

    /**
     * The node id.
     *
     * @return The node id.
     */
    @Override
    public String toString() {
        return nodeId;
    }
}
