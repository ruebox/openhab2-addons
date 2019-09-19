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

package rocks.xmpp.extensions.offline.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The implementation of the {@code <offline/>} element in the {@code http://jabber.org/protocol/offline} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0013.html">XEP-0013: Flexible Offline Message Retrieval</a>
 * @see <a href="https://xmpp.org/extensions/xep-0013.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "offline")
public final class OfflineMessage {

    /**
     * http://jabber.org/protocol/offline
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/offline";

    private final List<Item> item = new ArrayList<>();

    private final String fetch;

    private final String purge;

    private OfflineMessage() {
        this.fetch = null;
        this.purge = null;
    }

    public OfflineMessage(Item... item) {
        this(Arrays.asList(item));
    }

    public OfflineMessage(Collection<Item> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("items must not be empty");
        }
        this.item.addAll(items);
        this.fetch = null;
        this.purge = null;
    }

    public OfflineMessage(boolean fetch, boolean purge) {
        this.fetch = fetch ? "" : null;
        this.purge = purge ? "" : null;
    }

    /**
     * Gets the offline message id.
     *
     * @return The offline message id.
     */
    public String getId() {
        if (!item.isEmpty()) {
            return item.get(0).getId();
        }
        return null;
    }

    /**
     * Gets the items.
     *
     * @return The items.
     */
    public List<Item> getItems() {
        return Collections.unmodifiableList(item);
    }

    public static final class Item {
        @XmlAttribute
        private final String node;

        @XmlAttribute
        private final Action action;

        private Item() {
            this.node = null;
            this.action = null;
        }

        public Item(String id, Action action) {
            this.node = Objects.requireNonNull(id);
            this.action = action;
        }

        /**
         * Gets the offline message id.
         *
         * @return The id.
         */
        public final String getId() {
            return node;
        }

        @XmlEnum
        public enum Action {
            @XmlEnumValue("remove")
            REMOVE,
            @XmlEnumValue("view")
            VIEW
        }
    }
}
