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

package rocks.xmpp.extensions.pubsub.model.event;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.pubsub.model.Item;
import rocks.xmpp.extensions.pubsub.model.Subscription;
import rocks.xmpp.extensions.pubsub.model.SubscriptionState;
import rocks.xmpp.util.adapters.InstantAdapter;

import javax.xml.bind.annotation.XmlAnyElement;
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
 * The implementation of the {@code <event/>} element in the {@code http://jabber.org/protocol/pubsub#event} namespace.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0060.html">XEP-0060: Publish-Subscribe</a>
 * @see <a href="https://xmpp.org/extensions/xep-0060.html#schemas-event">XML Schema</a>
 */
@XmlRootElement(name = "event")
public final class Event {

    @XmlElements({
            @XmlElement(name = "configuration", type = Configuration.class),
            @XmlElement(name = "delete", type = Delete.class),
            @XmlElement(name = "items", type = Items.class),
            @XmlElement(name = "purge", type = Purge.class),
            @XmlElement(name = "subscription", type = SubscriptionInfo.class)
    })
    private final PubSubEventChildElement type;

    private Event() {
        this.type = null;
    }

    /**
     * Gets the 'node' attribute of the child element.
     *
     * @return The node.
     */
    public final String getNode() {
        return type != null ? type.getNode() : null;
    }

    /**
     * Indicates, whether the event is a configuration change event.
     *
     * @return True, if the configuration has changed.
     */
    public final boolean isConfiguration() {
        return type instanceof Configuration;
    }

    /**
     * Indicates, whether the event is a delete event.
     *
     * @return True, if a node has been deleted.
     */
    public final boolean isDelete() {
        return type instanceof Delete;
    }

    /**
     * Indicates, whether the event is purge event.
     *
     * @return True, if a node has been purged.
     */
    public final boolean isPurge() {
        return type instanceof Purge;
    }

    /**
     * Gets the subscription approval.
     *
     * @return The subscription approval or null, if the event did not include a subscription.
     */
    public final Subscription getSubscription() {
        if (type instanceof SubscriptionInfo) {
            return ((SubscriptionInfo) type);
        }
        return null;
    }

    /**
     * Gets the items of the event.
     *
     * @return The items of the event or an empty list, if the event did not include any items.
     */
    public final List<Item> getItems() {
        if (type instanceof Items) {
            return Collections.unmodifiableList(((Items) type).item);
        }
        return Collections.emptyList();
    }

    /**
     * Gets the configuration form.
     *
     * @return The configuration form or null, if the configuration form isn't included.
     * @see #isConfiguration()
     */
    public final DataForm getConfigurationForm() {
        if (type instanceof Configuration) {
            return ((Configuration) type).dataForm;
        }
        return null;
    }

    /**
     * Gets the redirect URI in case the event is a delete event.
     *
     * @return The redirect URI or null, if a redirect URI isn't included.
     * @see #isDelete()
     */
    public final URI getRedirectUri() {
        if (type instanceof Delete && ((Delete) type).redirect != null) {
            return ((Delete) type).redirect.uri;
        }
        return null;
    }

    private static final class Configuration extends PubSubEventChildElement {

        @XmlElementRef
        private final DataForm dataForm;

        private Configuration() {
            this.dataForm = null;
        }
    }

    private static final class Delete extends PubSubEventChildElement {

        private final Redirect redirect;

        private Delete() {
            this.redirect = null;
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

    private static final class Items extends PubSubEventChildElement {

        private final List<ItemElement> item = new ArrayList<>();

        @XmlAttribute(name = "max_items")
        private final Long maxItems;

        @XmlAttribute
        private final String subid;

        private final Retract retract;

        private Items() {
            this(null, null);
        }

        private Items(String node, Long maxItems) {
            super(node);
            this.maxItems = maxItems;
            this.subid = null;
            this.retract = null;
        }
    }

    private static final class Purge extends PubSubEventChildElement {

        private Purge() {
        }
    }

    private static final class Retract {
        @XmlAttribute
        private final String node;

        @XmlAttribute
        private final Boolean notify;

        private final ItemElement item;

        @XmlAttribute
        private final String id;

        private Retract() {
            this(null, null, null);
        }

        private Retract(String node, ItemElement item, Boolean notify) {
            this.node = node;
            this.item = item;
            this.notify = notify;
            this.id = null;
        }
    }

    private static final class SubscriptionInfo extends PubSubEventChildElement implements Subscription {

        @XmlAttribute
        @XmlJavaTypeAdapter(InstantAdapter.class)
        private final Instant expiry;

        @XmlAttribute
        private final Jid jid;

        @XmlAttribute
        private final String subid;

        @XmlAttribute
        private final SubscriptionState subscription;

        private SubscriptionInfo() {
            this.expiry = null;
            this.jid = null;
            this.subid = null;
            this.subscription = null;
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
        public final SubscriptionState getSubscriptionState() {
            return subscription;
        }

        @Override
        public final Instant getExpiry() {
            return expiry;
        }

        @Override
        public final boolean isConfigurationRequired() {
            return false;
        }

        @Override
        public final boolean isConfigurationSupported() {
            return false;
        }
    }

    private static final class ItemElement implements Item {

        @XmlAnyElement(lax = true)
        private final Object object;

        @XmlAttribute
        private final String id;

        @XmlAttribute
        private final String node;

        @XmlAttribute
        private final String publisher;

        private ItemElement() {
            this.object = this.id = this.node = this.publisher = null;
        }

        @Override
        public final Object getPayload() {
            return object;
        }

        @Override
        public final String getId() {
            return id;
        }

        @Override
        public final String getNode() {
            return node;
        }

        @Override
        public final String getPublisher() {
            return publisher;
        }
    }

    @XmlTransient
    private abstract static class PubSubEventChildElement {

        @XmlAttribute
        private final String node;

        private PubSubEventChildElement() {
            this(null);
        }

        private PubSubEventChildElement(String node) {
            this.node = node;
        }

        public final String getNode() {
            return node;
        }
    }
}
