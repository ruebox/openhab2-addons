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
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.disco.ServiceDiscoveryManager;
import rocks.xmpp.extensions.disco.model.info.InfoNode;
import rocks.xmpp.extensions.pubsub.model.Affiliation;
import rocks.xmpp.extensions.pubsub.model.NodeType;
import rocks.xmpp.extensions.pubsub.model.PubSub;
import rocks.xmpp.extensions.pubsub.model.PubSubFeature;
import rocks.xmpp.extensions.pubsub.model.Subscription;
import rocks.xmpp.extensions.pubsub.model.owner.PubSubOwner;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * This class acts a facade to deal with a remote pubsub service.
 * <p>
 * E.g. it lets you get all your subscriptions on the service, let's you work with nodes (e.g. subscribe to nodes)
 * or let's you discover the features provided by the remote service.
 * <p>
 * To work with pubsub nodes, {@linkplain #node(String) create a local node instance}, which can be used to work the remote node (e.g. subscribe to the node).
 *
 * @author Christian Schudt
 */
public final class PubSubService {

    private static final Logger logger = Logger.getLogger(PubSubService.class.getName());

    private final Jid service;

    private final String name;

    private final ServiceDiscoveryManager serviceDiscoveryManager;

    private final XmppSession xmppSession;

    PubSubService(Jid service, String name, XmppSession xmppSession, ServiceDiscoveryManager serviceDiscoveryManager) {
        this.service = service;
        this.name = name;
        this.serviceDiscoveryManager = serviceDiscoveryManager;
        this.xmppSession = xmppSession;
    }

    /**
     * Discovers the features, which are supported by the pubsub service.
     *
     * @return The async result with the set of supported features.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#entity-features">5.1 Discover Features</a>
     */
    public AsyncResult<Collection<PubSubFeature>> discoverFeatures() {
        return serviceDiscoveryManager.discoverInformation(service).thenApply(this::getFeatures);
    }

    Collection<PubSubFeature> getFeatures(InfoNode infoNode) {
        Collection<PubSubFeature> features = EnumSet.noneOf(PubSubFeature.class);
        infoNode.getFeatures().stream().filter(feature -> feature.startsWith(PubSub.NAMESPACE + '#')).forEach(feature -> {
            String f = feature.substring(feature.indexOf('#') + 1);
            try {
                PubSubFeature pubSubFeature = PubSubFeature.valueOf(f.toUpperCase().replace('-', '_'));
                if (pubSubFeature != null) {
                    features.add(pubSubFeature);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Server advertised unknown pubsub feature: {0}", f);
            }
        });
        return features;
    }

    /**
     * Discovers the first-level nodes of this pubsub service.
     *
     * @return The async result with the list of nodes.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#entity-nodes">5.2 Discover Nodes</a>
     */
    public AsyncResult<List<PubSubNode>> discoverNodes() {
        return serviceDiscoveryManager.discoverItems(service).thenApply(itemNode ->
                itemNode.getItems()
                        .stream()
                        .map(item -> new PubSubNode(item.getNode(), service, xmppSession))
                        .collect(Collectors.toList())
        );
    }

    /**
     * Creates a pubsub node locally, which can be used to work with a node at the pubsub service.
     *
     * @param node The node.
     * @return The node.
     */
    public PubSubNode node(String node) {
        return new PubSubNode(node, NodeType.LEAF, service, xmppSession);
    }

    /**
     * Gets the subscriptions for all nodes.
     *
     * @return The async result with the subscriptions for all nodes.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#entity-subscriptions">5.6 Retrieve Subscriptions</a>
     */
    public AsyncResult<List<Subscription>> getSubscriptions() {
        return xmppSession.query(IQ.get(service, PubSub.withSubscriptions())).thenApply(result -> {
            PubSub pubSub = result.getExtension(PubSub.class);
            return pubSub.getSubscriptions();
        });
    }

    /**
     * Gets the affiliations for all nodes.
     *
     * @return The async result with the affiliations for all nodes.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#entity-affiliations">5.7 Retrieve Affiliations</a>
     */
    public AsyncResult<List<Affiliation>> getAffiliations() {
        return xmppSession.query(IQ.get(service, PubSub.withAffiliations())).thenApply(result -> {
            PubSub pubSub = result.getExtension(PubSub.class);
            return pubSub.getAffiliations();
        });
    }

    /**
     * Gets the default subscription options for this pubsub service.
     *
     * @return The async result with the default subscription options.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#subscriber-configure-submit">6.4 Request Default Subscription Configuration Options</a>
     */
    public AsyncResult<DataForm> getDefaultSubscriptionOptions() {
        return xmppSession.query(IQ.get(service, PubSub.withDefault())).thenApply(result -> {
            PubSub pubSub = result.getExtension(PubSub.class);
            return pubSub.getDefault().getDataForm();
        });
    }

    /**
     * Gets the default node configuration form for this pubsub service.
     *
     * @return The async result with the configuration form.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-default">8.3 Request Default Node Configuration Options</a>
     */
    public AsyncResult<DataForm> getDefaultNodeConfiguration() {
        return xmppSession.query(IQ.get(service, PubSubOwner.withDefault())).thenApply(result -> {
            PubSubOwner pubSubOwner = result.getExtension(PubSubOwner.class);
            return pubSubOwner.getConfigurationForm();
        });
    }

    /**
     * Gets the service address.
     *
     * @return The service address.
     */
    public Jid getAddress() {
        return service;
    }

    /**
     * Returns the service address.
     *
     * @return The service address.
     */
    @Override
    public String toString() {
        return service.toString();
    }

    /**
     * Gets the name of this service.
     *
     * @return The name or null, if the name is unknown.
     */
    public String getName() {
        return name;
    }
}
