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

package rocks.xmpp.extensions.disco.model.items;

import rocks.xmpp.extensions.rsm.model.ResultSetManagement;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The implementation of the {@code <query/>} element in the {@code http://jabber.org/protocol/disco#item} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0030.html">XEP-0030: Service Discovery</a>
 * @see <a href="https://xmpp.org/extensions/xep-0030.html#schemas-items">XML Schema</a>
 */
@XmlRootElement(name = "query")
public final class ItemDiscovery implements ItemNode {

    /**
     * http://jabber.org/protocol/disco#items
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/disco#items";

    private final List<Item> item = new ArrayList<>();

    @XmlAttribute
    private final String node;

    @XmlElementRef
    private final ResultSetManagement resultSetManagement;

    /**
     * Creates an empty element, used for item discovery requests.
     */
    public ItemDiscovery() {
        this(null, null, null);
    }

    /**
     * Creates an item discovery element with a node attribute.
     *
     * @param node The node.
     */
    public ItemDiscovery(String node) {
        this(node, null, null);
    }

    /**
     * Creates an item discovery element with a node attribute.
     *
     * @param node                The node.
     * @param resultSetManagement The result set management extension.
     */
    public ItemDiscovery(String node, ResultSetManagement resultSetManagement) {
        this(node, null, resultSetManagement);
    }

    /**
     * Creates an item discovery element with nodes.
     *
     * @param items The items.
     */
    public ItemDiscovery(Collection<Item> items) {
        this(null, items);
    }

    /**
     * Creates an item discovery element with nodes and result set management.
     *
     * @param items               The items.
     * @param resultSetManagement The result set management extension.
     */
    public ItemDiscovery(Collection<Item> items, ResultSetManagement resultSetManagement) {
        this(null, items, resultSetManagement);
    }

    /**
     * Creates an item discovery element with a node attribute.
     *
     * @param node  The node.
     * @param items The items.
     */
    public ItemDiscovery(String node, Collection<Item> items) {
        this(node, items, null);
    }

    /**
     * Creates an item discovery element with a node attribute and result set management.
     *
     * @param node                The node.
     * @param items               The items.
     * @param resultSetManagement The result set management extension.
     */
    public ItemDiscovery(String node, Collection<Item> items, ResultSetManagement resultSetManagement) {
        this.node = node;
        if (items != null) {
            this.item.addAll(items);
        }
        this.resultSetManagement = resultSetManagement;
    }

    @Override
    public final List<Item> getItems() {
        return Collections.unmodifiableList(item);
    }

    @Override
    public final ResultSetManagement getResultSetManagement() {
        return resultSetManagement;
    }

    @Override
    public final String getNode() {
        return node;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        if (node != null) {
            sb.append(node);
        }
        if (!item.isEmpty()) {
            if (!sb.toString().isEmpty()) {
                sb.append(": ");
            }
            sb.append(item.toString());
        }
        return sb.toString();
    }
}
