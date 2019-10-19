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

package rocks.xmpp.extensions.disco;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.Extension;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.AbstractIQHandler;
import rocks.xmpp.core.stanza.IQHandler;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.disco.model.info.Identity;
import rocks.xmpp.extensions.disco.model.info.InfoDiscovery;
import rocks.xmpp.extensions.disco.model.info.InfoNode;
import rocks.xmpp.extensions.disco.model.items.Item;
import rocks.xmpp.extensions.disco.model.items.ItemDiscovery;
import rocks.xmpp.extensions.disco.model.items.ItemNode;
import rocks.xmpp.extensions.rsm.ResultSet;
import rocks.xmpp.extensions.rsm.ResultSetProvider;
import rocks.xmpp.extensions.rsm.model.ResultSetManagement;
import rocks.xmpp.util.XmppUtils;
import rocks.xmpp.util.concurrent.AsyncResult;
import rocks.xmpp.util.concurrent.CompletionStages;

import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Manages <a href="https://xmpp.org/extensions/xep-0030.html">XEP-0030: Service Discovery</a>.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/extensions/xep-0030.html#intro">1. Introduction</a></cite></p>
 * <p>The ability to discover information about entities on the Jabber network is extremely valuable. Such information might include features offered or protocols supported by the entity, the entity's type or identity, and additional entities that are associated with the original entity in some way (often thought of as "children" of the "parent" entity).</p>
 * </blockquote>
 * <p>Enabled extensions are automatically added to the list of features by their respective manager class.
 * Disabled extensions are removed.</p>
 * <p>
 * This class automatically manages inbound service discovery requests by responding with a list of enabled extensions (features).
 * </p>
 *
 * @author Christian Schudt
 */
public final class ServiceDiscoveryManager extends Manager {

    private static final Set<Identity> DEFAULT_IDENTITY = Collections.singleton(Identity.clientPc());

    private final Set<Identity> identities = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final Set<String> features = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final CopyOnWriteArrayList<DataForm> extensions = new CopyOnWriteArrayList<>();

    private final Map<String, InfoNode> infoNodeMap = new ConcurrentHashMap<>();

    private final Map<String, ResultSetProvider<Item>> itemProviders = new ConcurrentHashMap<>();

    private final Set<Consumer<EventObject>> capabilitiesChangeListeners = new CopyOnWriteArraySet<>();

    private final IQHandler discoInfoHandler;

    private final IQHandler discoItemHandler;

    private final Map<String, Extension> featureToExtension = new HashMap<>();

    private final Map<Class<? extends Manager>, Set<Extension>> managersToExtensions = new HashMap<>();

    private ServiceDiscoveryManager(final XmppSession xmppSession) {
        super(xmppSession, true);

        this.discoInfoHandler = new AbstractIQHandler(IQ.Type.GET) {
            @Override
            protected IQ processRequest(IQ iq) {
                InfoDiscovery infoDiscovery = iq.getExtension(InfoDiscovery.class);
                if (infoDiscovery.getNode() == null) {
                    return iq.createResult(new InfoDiscovery(getIdentities(), getFeatures(), getExtensions()));
                } else {
                    InfoNode infoNode = infoNodeMap.get(infoDiscovery.getNode());
                    if (infoNode != null) {
                        return iq.createResult(new InfoDiscovery(infoNode.getNode(), infoNode.getIdentities(), infoNode.getFeatures(), infoNode.getExtensions()));
                    } else {
                        // Returns <feature-not-implemented/> here.
                        // XEP-0030 is not clear on that, but XEP-0045 and XEP-0079 specify to return a <feature-not-implemented/> on unknown nodes.
                        return iq.createError(Condition.FEATURE_NOT_IMPLEMENTED);
                    }
                }
            }
        };
        this.discoItemHandler = new AbstractIQHandler(IQ.Type.GET) {
            @Override
            protected IQ processRequest(IQ iq) {
                ItemDiscovery itemDiscovery = iq.getExtension(ItemDiscovery.class);
                ResultSetProvider<Item> itemProvider = itemProviders.get(itemDiscovery.getNode() == null ? "" : itemDiscovery.getNode());
                if (itemProvider != null) {
                    ResultSet<Item> resultSet = ResultSet.create(itemProvider, itemDiscovery.getResultSetManagement());
                    return iq.createResult(new ItemDiscovery(itemDiscovery.getNode(), resultSet.getItems(), resultSet.getResultSetManagement()));
                } else {
                    if (itemDiscovery.getNode() == null) {
                        // If there are no items associated with an entity (or if those items are not publicly available), the target entity MUST return an empty query element to the requesting entity.
                        return iq.createResult(new ItemDiscovery(itemDiscovery.getNode()));
                    } else {
                        // <item-not-found/>: The JID or JID+NodeID of the specified target entity does not exist.
                        return iq.createError(Condition.ITEM_NOT_FOUND);
                    }
                }
            }
        };
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        xmppSession.addIQHandler(InfoDiscovery.class, discoInfoHandler);
        xmppSession.addIQHandler(ItemDiscovery.class, discoItemHandler);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        xmppSession.removeIQHandler(InfoDiscovery.class);
        xmppSession.removeIQHandler(ItemDiscovery.class);
    }

    /**
     * Adds a property change listener, which listens for changes in the {@linkplain #getIdentities() identities}, {@linkplain #getFeatures() features}, {@linkplain #getExtensions() extensions} and {@linkplain #getItems() items} collections.
     *
     * @param listener The listener.
     * @see #removeCapabilitiesChangeListener(Consumer)
     */
    public final void addCapabilitiesChangeListener(Consumer<EventObject> listener) {
        this.capabilitiesChangeListeners.add(listener);
    }

    /**
     * Removes a property change listener.
     *
     * @param listener The listener.
     * @see #addCapabilitiesChangeListener(Consumer)
     */
    public final void removeCapabilitiesChangeListener(Consumer<EventObject> listener) {
        this.capabilitiesChangeListeners.remove(listener);
    }

    /**
     * Gets an unmodifiable list of items.
     *
     * @return The items.
     */
    public final List<Item> getItems() {
        ResultSetProvider<Item> rootItemProvider = itemProviders.get("");
        if (rootItemProvider != null) {
            return Collections.unmodifiableList(rootItemProvider.getItems());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Gets an unmodifiable set of identities.
     *
     * @return The identities.
     * @see #addIdentity(rocks.xmpp.extensions.disco.model.info.Identity)
     * @see #removeIdentity(rocks.xmpp.extensions.disco.model.info.Identity)
     */
    public final Set<Identity> getIdentities() {
        return identities.isEmpty() ? DEFAULT_IDENTITY : Collections.unmodifiableSet(new HashSet<>(identities));
    }

    /**
     * Gets an unmodifiable set of features.
     *
     * @return The features.
     * @see #addFeature(String)
     * @see #removeFeature(String)
     */
    public final Set<String> getFeatures() {
        return Collections.unmodifiableSet(new HashSet<>(features));
    }

    /**
     * Gets an unmodifiable list of extensions.
     *
     * @return The extensions.
     * @see #addExtension(rocks.xmpp.extensions.data.model.DataForm)
     * @see #removeExtension(rocks.xmpp.extensions.data.model.DataForm)
     * @see <a href="https://xmpp.org/extensions/xep-0128.html">XEP-0128: Service Discovery Extensions</a>
     */
    @SuppressWarnings("unchecked")
    public final List<DataForm> getExtensions() {
        return Collections.unmodifiableList((List<DataForm>) extensions.clone());
    }

    /**
     * Adds an identity.
     *
     * @param identity The identity.
     * @see #removeIdentity(rocks.xmpp.extensions.disco.model.info.Identity)
     * @see #getIdentities()
     */
    public final void addIdentity(Identity identity) {
        if (identities.add(identity) && xmppSession.isConnected()) {
            XmppUtils.notifyEventListeners(capabilitiesChangeListeners, new EventObject(this));
        }
    }

    /**
     * Removes an identity.
     *
     * @param identity The identity.
     * @see #addIdentity(rocks.xmpp.extensions.disco.model.info.Identity)
     * @see #getIdentities()
     */
    public final void removeIdentity(Identity identity) {
        if (identities.remove(identity) && xmppSession.isConnected()) {
            XmppUtils.notifyEventListeners(capabilitiesChangeListeners, new EventObject(this));
        }
    }

    /**
     * Adds a feature. Features should not be added or removed directly. Instead enable or disable the respective extension manager, which will then add or remove the feature.
     * That way, supported features are consistent with enabled extension managers and service discovery won't reveal features, that are in fact not supported.
     *
     * @param feature The feature.
     * @see #removeFeature(String)
     * @see #getFeatures()
     */
    public final void addFeature(String feature) {
        if (features.add(feature)) {
            Extension extension = featureToExtension.get(feature);
            setEnabled(extension != null ? Collections.singleton(extension) : null, feature, true);
            if (xmppSession.isConnected()) {
                XmppUtils.notifyEventListeners(capabilitiesChangeListeners, new EventObject(this));
            }
        }
    }

    /**
     * Removes a feature.
     *
     * @param feature The feature.
     * @see #addFeature(String)
     * @see #getFeatures()
     */
    public final void removeFeature(String feature) {
        if (features.remove(feature)) {
            Extension extension = featureToExtension.get(feature);
            setEnabled(extension != null ? Collections.singleton(extension) : null, feature, false);
            if (xmppSession.isConnected()) {
                XmppUtils.notifyEventListeners(capabilitiesChangeListeners, new EventObject(this));
            }
        }
    }

    /**
     * Adds a feature by its manager class.
     *
     * @param managerClass The manager class.
     * @see #addFeature(String)
     * @see #getFeatures()
     */
    public final void addFeature(Class<? extends Manager> managerClass) {
        // This will eventually call addFeature(String)
        setEnabled(managersToExtensions.get(managerClass), null, true);
    }

    /**
     * Removes a feature by its manager class.
     *
     * @param managerClass The feature.
     * @see #addFeature(String)
     * @see #getFeatures()
     */
    public final void removeFeature(Class<? extends Manager> managerClass) {
        // This will eventually call addFeature(String)
        setEnabled(managersToExtensions.get(managerClass), null, false);
    }

    /**
     * Adds an extension.
     *
     * @param extension The extension.
     * @see #removeExtension(rocks.xmpp.extensions.data.model.DataForm)
     * @see #getExtensions()
     * @see <a href="https://xmpp.org/extensions/xep-0128.html">XEP-0128: Service Discovery Extensions</a>
     */
    public final void addExtension(DataForm extension) {
        if (extensions.add(extension) && xmppSession.isConnected()) {
            XmppUtils.notifyEventListeners(capabilitiesChangeListeners, new EventObject(this));
        }
    }

    /**
     * Removes an extension.
     *
     * @param extension The extension.
     * @see #addExtension(rocks.xmpp.extensions.data.model.DataForm)
     * @see #getExtensions()
     * @see <a href="https://xmpp.org/extensions/xep-0128.html">XEP-0128: Service Discovery Extensions</a>
     */
    public final void removeExtension(DataForm extension) {
        if (extensions.remove(extension) && xmppSession.isConnected()) {
            XmppUtils.notifyEventListeners(capabilitiesChangeListeners, new EventObject(this));
        }
    }

    /**
     * Discovers information about another XMPP entity.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/extensions/xep-0030.html#info">3. Discovering Information About a Jabber Entity</a></cite></p>
     * <p>A requesting entity may want to discover information about another entity on the network. The information desired generally is of two kinds:</p>
     * <ol>
     * <li>The target entity's identity.</li>
     * <li>The features offered and protocols supported by the target entity.</li>
     * </ol>
     * </blockquote>
     *
     * @param jid The entity's JID.
     * @return The async service discovery result.
     */
    public final AsyncResult<InfoNode> discoverInformation(Jid jid) {
        return discoverInformation(jid, null);
    }

    /**
     * Discovers information about another XMPP entity targeted at a specific node.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/extensions/xep-0030.html#info-nodes">3.2 Info Nodes</a></cite></p>
     * <p>A disco#info query MAY also be directed to a specific node identifier associated with a JID.</p>
     * </blockquote>
     *
     * @param jid  The entity's JID.
     * @param node The node.
     * @return The async service discovery result.
     * @see #discoverInformation(Jid)
     */
    public final AsyncResult<InfoNode> discoverInformation(Jid jid, String node) {
        return xmppSession.query(IQ.get(jid, new InfoDiscovery(node)), InfoNode.class);
    }

    /**
     * Discovers item associated with another XMPP entity.
     *
     * @param jid The JID.
     * @return The async result with the discovered items.
     */
    public final AsyncResult<ItemNode> discoverItems(Jid jid) {
        return discoverItems(jid, null, null);
    }

    /**
     * Discovers item associated with another XMPP entity.
     *
     * @param jid       The JID.
     * @param resultSet The result set management.
     * @return The async result with the discovered items.
     */
    public final AsyncResult<ItemNode> discoverItems(Jid jid, ResultSetManagement resultSet) {
        return discoverItems(jid, null, resultSet);
    }

    /**
     * Discovers item associated with another XMPP entity.
     *
     * @param jid  The JID.
     * @param node The node.
     * @return The async result with the discovered items.
     */
    public final AsyncResult<ItemNode> discoverItems(Jid jid, String node) {
        return discoverItems(jid, node, null);
    }

    /**
     * Discovers item associated with another XMPP entity.
     *
     * @param jid                 The JID.
     * @param node                The node.
     * @param resultSetManagement The result set management.
     * @return The async result with the discovered items.
     */
    public final AsyncResult<ItemNode> discoverItems(Jid jid, String node, ResultSetManagement resultSetManagement) {
        return xmppSession.query(IQ.get(jid, new ItemDiscovery(node, resultSetManagement)), ItemNode.class);
    }

    /**
     * Discovers a service on the connected server by its identity.
     * <p>
     * E.g. to discover MUC services you could call this method with {@link Identity#conferenceText()};
     * <p>
     * This method is generally preferred over {@link #discoverServices(String)}.
     *
     * @param identity The identity.
     * @return The services, that belong to the namespace.
     */
    public final AsyncResult<List<Item>> discoverServices(Identity identity) {
        return discoverServices(infoNode -> {
            for (Identity id : infoNode.getIdentities()) {
                if (id.getCategory().equals(identity.getCategory()) && id.getType().equals(identity.getType())) {
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * Discovers a service on the connected server by its feature namespace.
     *
     * @param feature The feature namespace.
     * @return The async result with the services, that belong to the namespace.
     */
    public final AsyncResult<List<Item>> discoverServices(String feature) {
        return discoverServices(infoNode -> infoNode.getFeatures().contains(feature));
    }

    private AsyncResult<List<Item>> discoverServices(Predicate<InfoNode> predicate) {
        // First discover the items of the server.
        // Then, for each item, discover the features of the item, but ignore any exceptions.
        return discoverItems(xmppSession.getDomain()).thenCompose(itemDiscovery -> {
            Collection<CompletionStage<List<Item>>> stages = itemDiscovery.getItems().stream()
                    .map(item -> discoverInformation(item.getJid()).thenApply(infoDiscovery -> {
                        if (predicate.test(infoDiscovery)) {
                            return Collections.singletonList(item);
                        }
                        return Collections.<Item>emptyList();
                    }).handle((items, throwable) -> {
                        // If one disco#info fails, don't let the whole discoverServices() method fail.
                        // Instead of failing, return an empty list, other services can hopefully be discovered successfully.
                        if (throwable != null) {
                            return Collections.<Item>emptyList();
                        } else {
                            return items;
                        }
                    }))
                    .collect(Collectors.toList());
            return CompletionStages.allOf(stages);
        });
    }

    /**
     * Adds an info node.
     *
     * @param infoNode The info node.
     */
    public final void addInfoNode(InfoNode infoNode) {
        infoNodeMap.put(infoNode.getNode(), infoNode);
    }

    /**
     * Removes an info node.
     *
     * @param node The node name.
     */
    public final void removeInfoNode(String node) {
        infoNodeMap.remove(node);
    }

    /**
     * Sets an item provider for the root node.
     * <p>
     * If you want to manage items in memory, you can use {@link ResultSetProvider#forItems(Collection)}}.
     *
     * @param itemProvider The item provider.
     */
    public final void setItemProvider(ResultSetProvider<Item> itemProvider) {
        if (itemProvider == null) {
            itemProviders.remove("");
        } else {
            itemProviders.put("", itemProvider);
        }
    }

    /**
     * Sets an item provider for a node.
     * <p>
     * If you want to manage items in memory, you can use {@link ResultSetProvider#forItems(Collection)}}.
     *
     * @param node         The node name.
     * @param itemProvider The item provider.
     */
    public final void setItemProvider(String node, ResultSetProvider<Item> itemProvider) {
        if (itemProvider == null) {
            itemProviders.remove(node);
        } else {
            itemProviders.put(node, itemProvider);
        }
    }

    @Override
    protected void dispose() {
        capabilitiesChangeListeners.clear();
        infoNodeMap.clear();
        itemProviders.clear();
    }

    private void setEnabled(Iterable<Extension> extensions, String feature, boolean enabled) {
        if (extensions != null) {
            for (Extension extension : extensions) {
                // Check if the extension has an associated manager class, which we need to enable/disable.
                Class<? extends Manager> managerClass = extension.getManager();
                if (managerClass != null) {
                    Manager manager = xmppSession.getManager(managerClass);
                    // A manager can manage multiple features (e.g. ServiceDiscoveryManager manages disco#items and disco#info feature)
                    // If we disable one feature, but not the other one, the manager should still be enabled.
                    boolean mayDisable = true;
                    if (feature != null && !enabled) {
                        Set<Extension> ex = managersToExtensions.get(managerClass);
                        for (Extension e : ex) {
                            if (features.contains(e.getNamespace())) {
                                mayDisable = false;
                                break;
                            }
                        }
                    }
                    if (mayDisable && enabled != manager.isEnabled()) {
                        manager.setEnabled(enabled);
                    }
                }

                // Enable the feature (by adding it to the Service Discovery list)
                enableFeature(extension.getNamespace(), enabled);

                // Do the same for each sub-feature.
                for (String subFeature : extension.getFeatures()) {
                    enableFeature(subFeature, enabled);
                }
            }
        } else {
            enableFeature(feature, enabled);
        }
    }

    private void enableFeature(String feature, boolean enabled) {
        if (feature != null) {
            if (enabled) {
                addFeature(feature);
            } else {
                removeFeature(feature);
            }
        }
    }

    /**
     * Registers a feature / extension.
     *
     * @param extension The extension.
     */
    public final void registerFeature(Extension extension) {
        if (extension.getNamespace() != null) {
            featureToExtension.put(extension.getNamespace(), extension);
        }
        if (extension.getManager() != null) {
            Set<Extension> extensions = managersToExtensions.computeIfAbsent(extension.getManager(), key -> new HashSet<>());
            extensions.add(extension);
        }
        if (extension.isEnabled()) {
            setEnabled(Collections.singleton(extension), null, true);
        }
    }
}
