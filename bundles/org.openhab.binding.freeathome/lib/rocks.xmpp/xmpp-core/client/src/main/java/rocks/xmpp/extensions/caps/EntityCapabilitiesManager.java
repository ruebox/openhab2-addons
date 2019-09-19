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

package rocks.xmpp.extensions.caps;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.PresenceEvent;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.core.stanza.model.StanzaErrorException;
import rocks.xmpp.core.stream.client.StreamFeaturesManager;
import rocks.xmpp.extensions.caps.model.EntityCapabilities;
import rocks.xmpp.extensions.caps.model.EntityCapabilities1;
import rocks.xmpp.extensions.caps2.model.EntityCapabilities2;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.disco.ServiceDiscoveryManager;
import rocks.xmpp.extensions.disco.model.info.Identity;
import rocks.xmpp.extensions.disco.model.info.InfoDiscovery;
import rocks.xmpp.extensions.disco.model.info.InfoNode;
import rocks.xmpp.extensions.hashes.model.Hash;
import rocks.xmpp.extensions.hashes.model.Hashed;
import rocks.xmpp.im.subscription.PresenceManager;
import rocks.xmpp.util.XmppUtils;
import rocks.xmpp.util.cache.DirectoryCache;
import rocks.xmpp.util.cache.LruCache;
import rocks.xmpp.util.concurrent.AsyncResult;

import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class manages <a href="https://xmpp.org/extensions/xep-0115.html">XEP-0115: Entity Capabilities</a>.
 * <p>
 * If this manager is enabled (default), entity capabilities are automatically included in every presence notification being sent.
 * </p>
 * <p>
 * You can check for an entity's capabilities by using {@link #discoverCapabilities(Jid)}, which will either return cached capabilities or ask the entity.
 * </p>
 * Similarly you can ask if an entity supports a particular feature via {@link #isSupported(String, Jid)}.
 * <p>
 * This class is thread-safe.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0115.html">XEP-0115: Entity Capabilities</a>
 */
public final class EntityCapabilitiesManager extends Manager {

    private static final Logger logger = Logger.getLogger(EntityCapabilitiesManager.class.getName());

    private static final String DEFAULT_NODE = "http://xmpp.rocks";

    // Cache up to 100 capability hashes in memory.
    private static final Map<Hash, InfoNode> CAPS_CACHE = new LruCache<>(100);

    // Cache the capabilities of an entity.
    private static final Map<Jid, InfoNode> ENTITY_CAPABILITIES = new ConcurrentHashMap<>();

    private static final Map<Jid, AsyncResult<InfoNode>> REQUESTS = new ConcurrentHashMap<>();

    private final ServiceDiscoveryManager serviceDiscoveryManager;

    private final Map<InfoNode, Collection<EntityCapabilities>> publishedNodes;

    private final DirectoryCache directoryCapsCache;

    private final Consumer<PresenceEvent> inboundPresenceListener;

    private final Consumer<PresenceEvent> outboundPresenceListener;

    /**
     * Guarded by "serviceDiscoveryManager".
     */
    private boolean capsSent;

    /**
     * Guarded by "this".
     */
    private String node;

    private EntityCapabilitiesManager(final XmppSession xmppSession) {
        super(xmppSession);
        serviceDiscoveryManager = xmppSession.getManager(ServiceDiscoveryManager.class);

        directoryCapsCache = xmppSession.getConfiguration().getCacheDirectory() != null ? new DirectoryCache(xmppSession.getConfiguration().getCacheDirectory().resolve("caps")) : null;
        // no need for a synchronized map, since access to this is already synchronized by this class.

        publishedNodes = new LinkedHashMap<InfoNode, Collection<EntityCapabilities>>(10, 0.75F, false) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<InfoNode, Collection<EntityCapabilities>> eldest) {
                if (size() > 10) {
                    // Remove old published nodes as well, they are no longer needed.
                    serviceDiscoveryManager.removeInfoNode(eldest.getKey().getNode());
                    return true;
                }
                return false;
            }
        };

        this.inboundPresenceListener = e -> {
            final Presence presence = e.getPresence();
            if (!presence.getFrom().equals(xmppSession.getConnectedResource())) {
                final List<EntityCapabilities> entityCapabilities = presence.getExtensions(EntityCapabilities.class);
                processNextEntityCaps(entityCapabilities.iterator(), presence.getFrom());
            }
        };

        this.outboundPresenceListener = e -> {
            final Presence presence = e.getPresence();
            if (presence.isAvailable()) {
                // Synchronize on sdm, to make sure no features/identities are added removed, while computing the hash.
                synchronized (publishedNodes) {
                    if (publishedNodes.isEmpty()) {
                        publishCapsNode();
                    }
                    // a client SHOULD include entity capabilities with every presence notification it sends.
                    // Get the last generated verification string here.
                    Deque<Collection<EntityCapabilities>> publishedEntityCaps = new ArrayDeque<>(publishedNodes.values());
                    Collection<EntityCapabilities> lastPublishedEntityCaps = publishedEntityCaps.getLast();
                    for (EntityCapabilities entityCapabilities : lastPublishedEntityCaps) {
                        presence.putExtension(entityCapabilities);
                    }
                    capsSent = true;
                }
            }
        };
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        xmppSession.addInboundPresenceListener(inboundPresenceListener);
        xmppSession.addOutboundPresenceListener(outboundPresenceListener);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        xmppSession.removeInboundPresenceListener(inboundPresenceListener);
        xmppSession.removeOutboundPresenceListener(outboundPresenceListener);
    }

    @Override
    protected void initialize() {
        serviceDiscoveryManager.addCapabilitiesChangeListener(evt -> {
            // If we haven't established a presence session yet, don't care about changes in service discovery.
            // If we change features during a presence session, update the verification string and resend presence.

            // https://xmpp.org/extensions/xep-0115.html#advertise:
            // "If the supported features change during a generating entity's presence session (e.g., a user installs an updated version of a client plugin), the application MUST recompute the verification string and SHOULD send a new presence broadcast."
            Presence lastPresence = null;
            synchronized (publishedNodes) {
                if (capsSent) {
                    // Whenever the verification string has changed, publish the info node.
                    publishCapsNode();

                    // Resend presence. This manager will add the caps extension later.
                    PresenceManager presenceManager = xmppSession.getManager(PresenceManager.class);
                    lastPresence = presenceManager.getLastSentPresence();
                }
            }
            if (lastPresence != null) {
                xmppSession.send(new Presence(null, lastPresence.getType(), lastPresence.getShow(), lastPresence.getStatuses(), lastPresence.getPriority(), null, null, lastPresence.getLanguage(), null, null));
            }
        });
        xmppSession.addSessionStatusListener(e -> {
            switch (e.getStatus()) {
                case AUTHENTICATED:
                    // As soon as we are authenticated, check if the server has advertised Entity Capabilities in its stream features.
                    List<EntityCapabilities> serverCapabilities = xmppSession.getManager(StreamFeaturesManager.class).getFeatures(EntityCapabilities.class);
                    // If yes, treat it as other caps.
                    processNextEntityCaps(serverCapabilities.iterator(), xmppSession.getDomain());
                    break;
                default:
                    break;
            }
        });
    }

    private void publishCapsNode() {

        final InfoDiscovery infoDiscovery = new InfoDiscovery(serviceDiscoveryManager.getIdentities(), serviceDiscoveryManager.getFeatures(), serviceDiscoveryManager.getExtensions());
        final Collection<EntityCapabilities> caps = new ArrayList<>();

        try {
            EntityCapabilities entityCapabilities1 = new EntityCapabilities1(getNode(), infoDiscovery, MessageDigest.getInstance("sha-1"));
            EntityCapabilities entityCapabilities2 = new EntityCapabilities2(infoDiscovery, MessageDigest.getInstance("sha-256"));
            caps.add(entityCapabilities1);
            caps.add(entityCapabilities2);

            publishedNodes.put(infoDiscovery, caps);
            for (EntityCapabilities entityCapabilities : caps) {

                Set<Hashed> capabilityHashSet = entityCapabilities.getCapabilityHashSet();
                for (Hashed hashed : capabilityHashSet) {

                    // Cache our own capabilities.
                    writeToCache(Hash.from(hashed), infoDiscovery);

                    final String node = entityCapabilities.createCapabilityHashNode(hashed);

                    serviceDiscoveryManager.addInfoNode(new InfoNode() {
                        @Override
                        public String getNode() {
                            return node;
                        }

                        @Override
                        public Set<Identity> getIdentities() {
                            return infoDiscovery.getIdentities();
                        }

                        @Override
                        public Set<String> getFeatures() {
                            return infoDiscovery.getFeatures();
                        }

                        @Override
                        public List<DataForm> getExtensions() {
                            return infoDiscovery.getExtensions();
                        }
                    });
                }
            }
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    /**
     * Gets the node. If no node was set, a default node is returned.
     *
     * @return The node.
     * @see #setNode(String)
     */
    public synchronized String getNode() {
        return node != null ? node : DEFAULT_NODE;
    }

    /**
     * Sets the node.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/extensions/xep-0115.html#protocol">4. Protocol</a></cite></p>
     * <p>It is RECOMMENDED for the value of the 'node' attribute to be an HTTP URL at which a user could find further information about the software product, such as "http://psi-im.org" for the Psi client;</p>
     * </blockquote>
     *
     * @param node The node.
     * @see #getNode()
     */
    public synchronized void setNode(String node) {
        this.node = node;
    }

    /**
     * Discovers the capabilities of another XMPP entity.
     *
     * @param jid The JID, which should usually be a full JID.
     * @return The async result with the capabilities in form of a info node, which contains the identities, the features and service discovery extensions.
     * @see <a href="https://xmpp.org/extensions/xep-0115.html#discover">6.2 Discovering Capabilities</a>
     */
    public final AsyncResult<InfoNode> discoverCapabilities(Jid jid) {
        InfoNode infoNode = ENTITY_CAPABILITIES.get(jid);
        if (infoNode == null) {
            // Make sure, that for the same JID no multiple concurrent queries are sent. One is enough.
            return REQUESTS.computeIfAbsent(jid, key -> serviceDiscoveryManager.discoverInformation(jid)
                    .whenComplete((result, e) -> {
                        if (result != null) {
                            ENTITY_CAPABILITIES.put(jid, result);
                        }
                        REQUESTS.remove(jid);
                    }));
        }
        return new AsyncResult<>(CompletableFuture.completedFuture(infoNode));
    }

    /**
     * Checks whether the entity supports the given feature. If the features are already known and cached
     *
     * @param feature The feature.
     * @param jid     The JID, which should usually be a full JID.
     * @return The async result with true, if this entity supports the feature.
     */
    public final AsyncResult<Boolean> isSupported(String feature, Jid jid) {
        return discoverCapabilities(jid)
                .handle((infoNode, e) -> {
                    if (e == null) {
                        return infoNode.getFeatures().contains(feature);
                    } else {
                        if (e.getCause() instanceof StanzaErrorException) {
                            return false;
                        }
                        throw (CompletionException) e;
                    }
                });
    }

    private void writeToCache(Hash hash, InfoNode infoNode) {
        if (directoryCapsCache != null) {
            // Write to in-memory cache.
            CAPS_CACHE.put(hash, infoNode);

            // Write to persistent cache.
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                XMLStreamWriter xmppStreamWriter = null;
                try {
                    xmppStreamWriter = XmppUtils.createXmppStreamWriter(xmppSession.getConfiguration().getXmlOutputFactory().createXMLStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8.name()));
                    xmppSession.createMarshaller().marshal(infoNode, xmppStreamWriter);
                    xmppStreamWriter.flush();
                } finally {
                    if (xmppStreamWriter != null) {
                        xmppStreamWriter.close();
                    }
                }
                directoryCapsCache.put(XmppUtils.hash(hash.toString().getBytes(StandardCharsets.UTF_8)) + ".caps", byteArrayOutputStream.toByteArray());
            } catch (Exception e) {
                logger.log(Level.WARNING, e, () -> "Could not write entity capabilities to persistent cache. Reason: " + e.getMessage());
            }
        }
    }

    private InfoNode readFromCache(Hash hash) {
        if (directoryCapsCache != null) {
            // First check the in-memory cache.
            InfoNode infoNode = CAPS_CACHE.get(hash);
            if (infoNode != null) {
                return infoNode;
            }
            // If it's not present, check the persistent cache.
            String fileName = XmppUtils.hash(hash.toString().getBytes(StandardCharsets.UTF_8)) + ".caps";
            try {
                byte[] bytes = directoryCapsCache.get(fileName);
                if (bytes != null) {
                    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
                        infoNode = (InfoNode) xmppSession.createUnmarshaller().unmarshal(byteArrayInputStream);
                        CAPS_CACHE.put(hash, infoNode);
                        return infoNode;
                    }
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, e, () -> "Could not read entity capabilities from persistent cache (file: " + fileName + ')');
            }
        }
        // The verification string is unknown, Service Discovery needs to be done.
        return null;
    }

    private void processNextEntityCaps(final Iterator<EntityCapabilities> entityCapabilities, final Jid entity) {
        if (entityCapabilities.hasNext()) {
            final EntityCapabilities caps = entityCapabilities.next();
            logger.log(Level.FINE, "Processing {0}", caps);
            processNextHash(entityCapabilities, caps.getCapabilityHashSet().iterator(), entity, caps);
        }
    }

    private void processNextHash(final Iterator<EntityCapabilities> entityCapabilities, final Iterator<Hashed> hashedIterator, final Jid entity, final EntityCapabilities caps) {
        if (hashedIterator.hasNext()) {
            // 1. Verify that the <c/> element includes a 'hash' attribute. If it does not, ignore the 'ver'
            final Hashed hashed = hashedIterator.next();
            if (hashed.getHashAlgorithm() == null) {
                return;
            }
            final Hash hash = Hash.from(hashed);
            // Check if the hash is already known.
            final InfoNode infoNode = readFromCache(hash);

            if (infoNode != null) {
                // If its known, just update the information for this entity.
                ENTITY_CAPABILITIES.put(entity, infoNode);
            } else {
                final String nodeToDiscover = caps.createCapabilityHashNode(hash);
                try {
                    // 3. If the value of the 'hash' attribute matches one of the processing application's supported hash functions, validate the verification string by doing the following:
                    final MessageDigest messageDigest = MessageDigest.getInstance(hash.getHashAlgorithm());

                    // 3.1 Send a service discovery information request to the generating entity.
                    // 3.2 Receive a service discovery information response from the generating entity.
                    logger.log(Level.FINE, "Discovering capabilities for '{0}' at node {1}", new Object[]{entity, nodeToDiscover});
                    serviceDiscoveryManager.discoverInformation(entity, nodeToDiscover).whenComplete((infoDiscovery, e1) -> {
                        if (e1 != null) {
                            processNextHash(entityCapabilities, hashedIterator, entity, caps);
                            logger.log(Level.WARNING, e1, () -> "Failed to discover information for entity '" + entity + "' for node '" + nodeToDiscover + "'");
                        } else {
                            // 3.3 If the response includes more than one service discovery identity with the same category/type/lang/name, consider the entire response to be ill-formed.
                            // 3.4 If the response includes more than one service discovery feature with the same XML character data, consider the entire response to be ill-formed.
                            // => not possible due to java.util.Set semantics and equals method.
                            // If the response had duplicates, just check the hash.

                            // 3.5 If the response includes more than one extended service discovery information form with the same FORM_TYPE or the FORM_TYPE field contains more than one <value/> element with different XML character data, consider the entire response to be ill-formed.
                            Collection<String> ftValues = new ArrayDeque<>();
                            for (DataForm dataForm : infoDiscovery.getExtensions()) {
                                DataForm.Field formType = dataForm.findField(DataForm.FORM_TYPE);
                                // 3.6 If the response includes an extended service discovery information form where the FORM_TYPE field is not of type "hidden" or the form does not include a FORM_TYPE field, ignore the form but continue processing.
                                if (formType != null && formType.getType() == DataForm.Field.Type.HIDDEN && !formType.getValues().isEmpty()) {
                                    Collection<String> values = new ArrayDeque<>();
                                    for (String value : formType.getValues()) {
                                        if (values.contains(value)) {
                                            // ill-formed
                                            return;
                                        }
                                        values.add(value);
                                    }
                                    String value = formType.getValues().get(0);
                                    if (ftValues.contains(value)) {
                                        // ill-formed
                                        return;
                                    }
                                    ftValues.add(value);
                                }
                            }

                            // 3.7 If the response is considered well-formed, reconstruct the hash by using the service discovery information response to generate a local hash in accordance with the Generation Method).
                            final byte[] verificationString = caps.createVerificationString(infoDiscovery);
                            final byte[] computedHash = messageDigest.digest(verificationString);
                            // 3.8 If the values of the received and reconstructed hashes match, the processing application MUST consider the result to be valid and SHOULD globally cache the result for all JabberIDs with which it communicates.
                            if (Arrays.equals(computedHash, hash.getHashValue())) {
                                writeToCache(hash, infoDiscovery);
                            } else {
                                processNextHash(entityCapabilities, hashedIterator, entity, caps);
                            }
                            ENTITY_CAPABILITIES.put(entity, infoDiscovery);
                        }
                    });
                    // 3.9 If the values of the received and reconstructed hashes do not match, the processing application MUST consider the result to be invalid and MUST NOT globally cache the verification string;
                } catch (NoSuchAlgorithmException e1) {
                    // 2. If the value of the 'hash' attribute does not match one of the processing application's supported hash functions, do the following:
                    // 2.1 Send a service discovery information request to the generating entity.
                    // 2.2 Receive a service discovery information response from the generating entity.
                    // 2.3 Do not validate or globally cache the verification string as described below; instead, the processing application SHOULD associate the discovered identity+features only with the JabberID of the generating entity.
                    serviceDiscoveryManager.discoverInformation(entity, nodeToDiscover).whenComplete((result, e2) -> {
                        if (e2 != null) {
                            logger.log(Level.WARNING, "Failed to discover information for entity '{0}' for node '{1}'", new Object[]{entity, nodeToDiscover});
                        } else {
                            ENTITY_CAPABILITIES.put(entity, result);
                        }
                    });

                    // Additionally try next hash.
                    processNextHash(entityCapabilities, hashedIterator, entity, caps);
                }
            }
        } else {
            // No more hashes, try next entity capabilities, if present.
            processNextEntityCaps(entityCapabilities, entity);
        }
    }
}
