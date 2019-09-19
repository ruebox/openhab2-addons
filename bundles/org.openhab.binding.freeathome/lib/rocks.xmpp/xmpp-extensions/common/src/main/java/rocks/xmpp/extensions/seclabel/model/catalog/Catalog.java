/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Christian Schudt
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

package rocks.xmpp.extensions.seclabel.model.catalog;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.extensions.seclabel.model.SecurityLabel;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The implementation of the {@code <catalog/>} element in the {@code urn:xmpp:sec-label:catalog:2} namespace.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0258.html">XEP-0258: Security Labels in XMPP</a>
 * @see <a href="https://xmpp.org/extensions/xep-0258.html#schema-catalog">XML Schema</a>
 */
@XmlRootElement(name = "catalog")
public final class Catalog {

    @XmlAttribute(name = "to")
    private final Jid to;

    @XmlAttribute(name = "from")
    private final Jid from;

    @XmlAttribute(name = "name")
    private final String name;

    @XmlAttribute(name = "desc")
    private final String description;

    @XmlAttribute(name = "id")
    private final String id;

    @XmlAttribute(name = "size")
    private final Integer size;

    @XmlAttribute(name = "restrict")
    private final Boolean restrict;

    @XmlElement(name = "item")
    private final List<Item> items = new ArrayList<>();

    public Catalog() {
        this(null, null, null, null, null, null, null, null);
    }

    public Catalog(Jid to, Jid from, String name, String description, String id, Integer size, Boolean restrict, Collection<Item> items) {
        this.to = to;
        this.from = from;
        this.name = name;
        this.description = description;
        this.id = id;
        this.size = size;
        this.restrict = restrict;
        if (items != null) {
            this.items.addAll(items);
        }
    }

    /**
     * Gets the 'to' attribute.
     *
     * @return The 'to' attribute.
     */
    public final Jid getTo() {
        return to;
    }

    /**
     * Gets the 'from' attribute.
     *
     * @return The 'from' attribute.
     */
    public final Jid getFrom() {
        return from;
    }

    /**
     * Gets the catalog name.
     *
     * @return The catalog name.
     */
    public final String getName() {
        return name;
    }

    /**
     * Gets the description.
     *
     * @return The description.
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Identifier for current revision, commonly a hash.
     *
     * @return The id.
     */
    public final String getId() {
        return id;
    }

    /**
     * If catalog is restrictive, the client SHOULD restrict the user to choosing one of the items from the catalog and use the label of that item (or no label if the selected item is empty).
     *
     * @return True, if restrictive.
     */
    public final boolean isRestrictive() {
        return restrict != null && restrict;
    }

    /**
     * Gets the number of items.
     *
     * @return The number of items.
     */
    public final Integer getSize() {
        return size;
    }

    /**
     * Gets the catalog items.
     *
     * @return The catalog items.
     */
    public final List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * A catalog item.
     * <p>
     * An item may have no security label. Such an item explicitly offers a choice of sending a stanza without a label. A non-restrictive catalog implicitly offers this choice when it does not contain an empty item.
     */
    public static final class Item {

        @XmlAttribute(name = "selector")
        private final String selector;

        @XmlAttribute(name = "default")
        private final Boolean aDefault;

        @XmlElementRef
        private final SecurityLabel securityLabel;

        public Item() {
            this.selector = null;
            this.aDefault = null;
            this.securityLabel = null;
        }

        public Item(String selector, SecurityLabel securityLabel, Boolean isDefault) {
            this.selector = selector;
            this.aDefault = isDefault;
            this.securityLabel = securityLabel;
        }

        /**
         * Gets the security label or null.
         *
         * @return The security label or null.
         */
        public final SecurityLabel getSecurityLabel() {
            return securityLabel;
        }

        /**
         * Gets the selector.
         * <blockquote>
         * <p>Items in the catalog may contain a selector= attribute. The value of this attribute represents the item's placement in a hierarchical organization of the items. If one item has a selector= attribute, all items should have a selector= attribute. The value of the selector= attribute conforms to the selector-value ABNF production:</p>
         * <p>{@code selector-value = (<item>"|")*<item>}</p>
         * <p>where {@code <item/>} is a sequence of characters not including "|".</p>
         * <p>A value of "X|Y|Z" indicates that this item is "Z" in the the "Y" subset of the "X" subset of items. This information may be used, for instance, in generating label selection menus in graphical user interfaces.</p>
         * </blockquote>
         *
         * @return The selector.
         */
        public final String getSelector() {
            return selector;
        }

        /**
         * Whether this item is the default item.
         * <blockquote>
         * <p>The client should default the label selection to this item in cases where the user has not selected an item.</p>
         * </blockquote>
         *
         * @return If this item is the default.
         */
        public final boolean isDefault() {
            return aDefault != null && aDefault;
        }
    }
}
