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

package rocks.xmpp.extensions.pubsub.model.owner;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.pubsub.model.Affiliation;
import rocks.xmpp.extensions.pubsub.model.AffiliationState;
import rocks.xmpp.extensions.pubsub.model.Subscription;
import rocks.xmpp.extensions.pubsub.model.SubscriptionState;
import rocks.xmpp.util.adapters.InstantAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The implementation of the {@code <pubsub/>} element in the {@code http://jabber.org/protocol/pubsub#owner} namespace.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0060.html">XEP-0060: Publish-Subscribe</a>
 * @see <a href="https://xmpp.org/extensions/xep-0060.html#schemas-owner">XML Schema</a>
 */
@XmlRootElement(name = "pubsub")
public final class PubSubOwner {

    @XmlElements({
            @XmlElement(name = "affiliations", type = Affiliations.class),
            @XmlElement(name = "configure", type = Configure.class),
            @XmlElement(name = "default", type = Default.class),
            @XmlElement(name = "delete", type = Delete.class),
            @XmlElement(name = "purge", type = Purge.class),
            @XmlElement(name = "subscriptions", type = Subscriptions.class)
    })
    private final PubSubOwnerChildElement type;

    private PubSubOwner() {
        this.type = null;
    }

    private PubSubOwner(PubSubOwnerChildElement type) {
        this.type = type;
    }

    /**
     * Creates a pubsub element with an {@code <configure/>} child element and a 'node' attribute.
     * <p><b>Sample:</b></p>
     * ```xml
     * <pubsub xmlns='http://jabber.org/protocol/pubsub#owner'>
     *     <configure node='princely_musings'/>
     * </pubsub>
     * ```
     *
     * @param node The node.
     * @return The pubsub instance.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-configure">8.2 Configure a Node</a>
     */
    public static PubSubOwner withConfigure(String node) {
        return new PubSubOwner(new Configure(node));
    }

    /**
     * Creates a pubsub element with an {@code <configure/>} child element and a 'node' attribute and a configuration form.
     * <p><b>Sample:</b></p>
     * ```xml
     * <pubsub xmlns='http://jabber.org/protocol/pubsub#owner'>
     *     <configure node='princely_musings'/>
     *        <x xmlns='jabber:x:data' type='submit'>
     *            <field var='FORM_TYPE' type='hidden'>
     *        ...
     * </pubsub>
     * ```
     *
     * @param dataForm The configuration form.
     * @param node     The node.
     * @return The pubsub instance.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-configure">8.2.4 Form Submission</a>
     */
    public static PubSubOwner withConfigure(String node, DataForm dataForm) {
        return new PubSubOwner(new Configure(node, dataForm));
    }

    /**
     * Creates a pubsub element with a {@code <default/>} child element.
     * <p><b>Sample:</b></p>
     * ```xml
     * <pubsub xmlns='http://jabber.org/protocol/pubsub#owner'>
     *     <default/>
     * </pubsub>
     * ```
     *
     * @return The pubsub instance.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-default">8.3 Request Default Node Configuration Options</a>
     */
    public static PubSubOwner withDefault() {
        return new PubSubOwner(new Default());
    }

    /**
     * Creates a pubsub element with a {@code <delete/>} child element and a 'node' attribute.
     * <p><b>Sample:</b></p>
     * ```xml
     * <pubsub xmlns='http://jabber.org/protocol/pubsub#owner'>
     *     <delete node='princely_musings'/>
     * </pubsub>
     * ```
     *
     * @param node The node.
     * @return The pubsub instance.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-delete">8.4 Delete a Node</a>
     */
    public static PubSubOwner withDelete(String node) {
        return new PubSubOwner(new Delete(node));
    }

    /**
     * Creates a pubsub element with a {@code <delete/>} child element, a 'node' attribute and a replacement node.
     * <p><b>Sample:</b></p>
     * ```xml
     * <pubsub xmlns='http://jabber.org/protocol/pubsub#owner'>
     *     <delete node='princely_musings'>
     *         <redirect uri='xmpp:hamlet@denmark.lit?;node=blog'/>
     *     </delete>
     * </pubsub>
     * ```
     *
     * @param node            The node.
     * @param replacementNode The replacement node.
     * @return The pubsub instance.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-delete">8.4 Delete a Node</a>
     */
    public static PubSubOwner withDelete(String node, URI replacementNode) {
        return new PubSubOwner(new Delete(node, new Delete.Redirect(replacementNode)));
    }

    /**
     * Creates a pubsub element with a {@code <purge/>} child element and a 'node' attribute.
     * <p><b>Sample:</b></p>
     * ```xml
     * <pubsub xmlns='http://jabber.org/protocol/pubsub#owner'>
     *     <purge node='princely_musings'/>
     * </pubsub>
     * ```
     *
     * @param node The node.
     * @return The pubsub instance.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-purge">8.5 Purge All Node Items</a>
     */
    public static PubSubOwner withPurge(String node) {
        return new PubSubOwner(new Purge(node));
    }

    /**
     * Creates a pubsub element with a {@code <subscriptions/>} child element with {@code <subscription/>} elements.
     * <p><b>Sample:</b></p>
     * ```xml
     * <pubsub xmlns='http://jabber.org/protocol/pubsub#owner'>
     *     <subscriptions node='princely_musings'>
     *         <subscription jid='bard@shakespeare.lit' subscription='subscribed'/>
     *     </subscriptions>
     * </pubsub>
     * ```
     *
     * @param node          The node.
     * @param subscriptions The subscriptions.
     * @return The pubsub instance.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-subscriptions-modify">8.8.2 Modify Subscriptions</a>
     */
    public static PubSubOwner withSubscriptions(String node, Subscription... subscriptions) {
        return new PubSubOwner(new Subscriptions(node, subscriptions));
    }

    /**
     * Creates a pubsub element with a {@code <affiliations/>} child element with {@code <affiliation/>} elements.
     * <p><b>Sample:</b></p>
     * ```xml
     * <pubsub xmlns='http://jabber.org/protocol/pubsub#owner'>
     *     <affiliations node='princely_musings'>
     *         <affiliation jid='bard@shakespeare.lit' affiliation='publisher'/>
     *     </affiliations>
     * </pubsub>
     * ```
     *
     * @param node             The node.
     * @param affiliationNodes The affiliations.
     * @return The pubsub instance.
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-affiliations-modify">8.9.2 Modify Affiliation</a>
     */
    public static PubSubOwner withAffiliations(String node, Affiliation... affiliationNodes) {
        return new PubSubOwner((new Affiliations(node, affiliationNodes)));
    }

    /**
     * Gets the configuration form if the pubsub element contains either a {@code <configure/>} or a {@code <default/>} element.
     *
     * @return The configuration form or null.
     * @see #isConfigure()
     * @see #isDefault()
     */
    public final DataForm getConfigurationForm() {
        if (type instanceof Configure) {
            return ((Configure) type).getDataForm();
        } else if (type instanceof Default) {
            return ((Default) type).dataForm;
        }
        return null;
    }

    /**
     * Gets the node of the child element.
     *
     * @return The node.
     */
    public final String getNode() {
        return type != null ? type.node : null;
    }

    /**
     * Indicates, whether this pubsub element contains a 'configure' child element.
     *
     * @return True, if the pubsub element contains a 'configure' child element.
     * @see #getConfigurationForm()
     */
    public final boolean isConfigure() {
        return type instanceof Configure;
    }

    /**
     * Indicates, whether this pubsub element contains a 'default' child element.
     *
     * @return True, if the pubsub element contains a 'default' child element.
     * @see #getConfigurationForm()
     */
    public final boolean isDefault() {
        return type instanceof Default;
    }

    /**
     * Indicates, whether this pubsub element contains a 'delete' child element.
     *
     * @return True, if the pubsub element contains a 'delete' child element.
     */
    public final boolean isDelete() {
        return type instanceof Delete;
    }

    /**
     * Indicates, whether this pubsub element contains a 'purge' child element.
     *
     * @return True, if the pubsub element contains a 'purge' child element.
     */
    public final boolean isPurge() {
        return type instanceof Purge;
    }

    /**
     * Indicates, whether this pubsub element contains a 'subscriptions' child element.
     *
     * @return True, if the pubsub element contains a 'subscriptions' child element.
     * @see #getSubscriptions()
     */
    public final boolean isSubscriptions() {
        return type instanceof Subscriptions;
    }

    /**
     * Indicates, whether this pubsub element contains a 'subscriptions' child element.
     *
     * @return True, if the pubsub element contains a 'subscriptions' child element.
     * @see #getAffiliations()
     */
    public final boolean isAffiliations() {
        return type instanceof Affiliations;
    }

    /**
     * Gets the subscriptions, if this pubsub element contains 'subscriptions' element.
     *
     * @return The subscriptions, if the pubsub element contains a 'subscriptions' child element; otherwise an empty list.
     */
    public final List<Subscription> getSubscriptions() {
        if (type instanceof Subscriptions) {
            return Collections.unmodifiableList(new ArrayList<>(((Subscriptions) type).subscription));
        }
        return Collections.emptyList();
    }

    /**
     * Gets the affiliations, if this pubsub element contains 'affiliations' element.
     *
     * @return The affiliations, if the pubsub element contains a 'affiliations' child element; otherwise an empty list.
     */
    public final List<Affiliation> getAffiliations() {
        if (type instanceof Affiliations) {
            return Collections.unmodifiableList(new ArrayList<>(((Affiliations) type).affiliation));
        }
        return Collections.emptyList();
    }

    /**
     * Gets the redirect URI, if this pubsub element contains a 'delete' element.
     *
     * @return The redirect URI, if this pubsub element contains a 'delete' element; otherwise null.
     */
    public final URI getRedirectUri() {
        if (type instanceof Delete && ((Delete) type).getRedirect() != null) {
            return ((Delete) type).getRedirect().uri;
        }
        return null;
    }

    private static final class Affiliations extends PubSubOwnerChildElement {

        private final List<AffiliationNodeOwner> affiliation = new ArrayList<>();

        private Affiliations() {
            this(null);
        }

        private Affiliations(String node, Affiliation... affiliations) {
            super(node);
            for (Affiliation affiliation : affiliations) {
                this.affiliation.add(new AffiliationNodeOwner(affiliation.getNode(), affiliation.getAffiliationState(), affiliation.getJid()));
            }
        }

        private static final class AffiliationNodeOwner implements Affiliation {

            @XmlAttribute
            private final String node;

            @XmlAttribute
            private final AffiliationState affiliation;

            @XmlAttribute
            private final Jid jid;

            private AffiliationNodeOwner() {
                this(null, null, null);
            }

            private AffiliationNodeOwner(String node, AffiliationState affiliation, Jid jid) {
                this.node = node;
                this.affiliation = affiliation;
                this.jid = jid;
            }

            @Override
            public final Jid getJid() {
                return jid;
            }

            @Override
            public final AffiliationState getAffiliationState() {
                return affiliation;
            }

            @Override
            public final String getNode() {
                return node;
            }
        }
    }

    private static final class Configure extends PubSubOwnerChildElement {

        @XmlElementRef
        private final DataForm dataForm;

        private Configure() {
            this(null);
        }

        private Configure(String node) {
            this(node, null);
        }

        private Configure(String node, DataForm dataForm) {
            super(node);
            this.dataForm = dataForm;
        }

        private DataForm getDataForm() {
            return dataForm;
        }
    }

    private static final class Default extends PubSubOwnerChildElement {

        @XmlElementRef
        private final DataForm dataForm;

        private Default() {
            this.dataForm = null;
        }
    }

    private static final class Delete extends PubSubOwnerChildElement {

        private final Redirect redirect;

        private Delete() {
            this(null);
        }

        private Delete(String node) {
            this(node, null);
        }

        private Delete(String node, Redirect redirect) {
            super(node);
            this.redirect = redirect;
        }

        private Redirect getRedirect() {
            return redirect;
        }

        private static final class Redirect {
            @XmlAttribute
            private final URI uri;

            private Redirect() {
                this(null);
            }

            private Redirect(URI uri) {
                this.uri = uri;
            }
        }
    }

    private static final class Purge extends PubSubOwnerChildElement {

        private Purge() {
        }

        private Purge(String node) {
            super(node);
        }
    }

    private static final class Subscriptions extends PubSubOwnerChildElement {

        private final List<SubscriptionOwner> subscription = new ArrayList<>();

        private Subscriptions() {
        }

        private Subscriptions(String node, Subscription... subscriptions) {
            super(node);
            for (Subscription subscription : subscriptions) {
                this.subscription.add(new SubscriptionOwner(subscription.getNode(), subscription.getJid(), subscription.getSubId(), subscription.getSubscriptionState(), subscription.getExpiry(), subscription.isConfigurationSupported() ? new SubscriptionOwner.Options(subscription.isConfigurationRequired()) : null));
            }
        }

        private static final class SubscriptionOwner extends PubSubOwnerChildElement implements Subscription {
            @XmlAttribute
            private final Jid jid;

            @XmlAttribute
            private final String subid;

            @XmlAttribute
            private final SubscriptionState subscription;

            @XmlAttribute
            @XmlJavaTypeAdapter(InstantAdapter.class)
            private final Instant expiry;

            @XmlElement(name = "subscribe-options")
            private final Options options;

            private SubscriptionOwner() {
                this(null, null, null, null, null, null);
            }

            private SubscriptionOwner(String node, Jid jid, String subid, SubscriptionState subscription, Instant expiry, Options options) {
                super(node);
                this.jid = jid;
                this.subid = subid;
                this.subscription = subscription;
                this.expiry = expiry;
                this.options = options;
            }

            @Override
            public final SubscriptionState getSubscriptionState() {
                return subscription;
            }

            @Override
            public final Jid getJid() {
                return jid;
            }

            @Override
            public final String getSubId() {
                return subid;
            }

            @Override
            public final Instant getExpiry() {
                return expiry;
            }

            @Override
            public final boolean isConfigurationRequired() {
                return options != null && options.isRequired();
            }

            @Override
            public final boolean isConfigurationSupported() {
                return options != null;
            }

            private static final class Options {

                private final String required;

                private Options() {
                    this.required = null;
                }

                private Options(boolean required) {
                    this.required = required ? "" : null;
                }

                private boolean isRequired() {
                    return required != null;
                }
            }
        }
    }

    @XmlTransient
    private abstract static class PubSubOwnerChildElement {

        @XmlAttribute
        private final String node;

        private PubSubOwnerChildElement() {
            this(null);
        }

        private PubSubOwnerChildElement(String node) {
            this.node = node;
        }

        public final String getNode() {
            return node;
        }
    }
}
