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

package rocks.xmpp.extensions.disco.model.info;

import rocks.xmpp.extensions.data.model.DataForm;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * The implementation of the {@code <query/>} element in the {@code http://jabber.org/protocol/disco#info} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0030.html">XEP-0030: Service Discovery</a>
 * @see <a href="https://xmpp.org/extensions/xep-0030.html#schemas-info">XML Schema</a>
 * @see <a href="https://xmpp.org/extensions/xep-0128.html">XEP-0128: Service Discovery Extensions</a>
 */
@XmlRootElement(name = "query")
public final class InfoDiscovery implements InfoNode {

    /**
     * http://jabber.org/protocol/disco#info
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/disco#info";

    private final Set<Identity> identity = new TreeSet<>();

    private final Set<Feature> feature = new TreeSet<>();

    @XmlElementRef
    private final List<DataForm> extensions = new ArrayList<>();

    @XmlAttribute
    private final String node;

    /**
     * Creates an empty element, used for info discovery requests.
     */
    public InfoDiscovery() {
        this(null);
    }

    /**
     * Creates an info discovery element with a node attribute.
     *
     * @param node The node.
     */
    public InfoDiscovery(String node) {
        this.node = node;
    }

    /**
     * Creates an info discovery element, used in discovery info responses.
     *
     * @param identities The identities
     * @param features   The features.
     */
    public InfoDiscovery(Collection<Identity> identities, Collection<String> features) {
        this(null, identities, features, null);
    }

    /**
     * Creates an info discovery element, used in discovery info responses.
     *
     * @param identities The identities
     * @param features   The features.
     * @param extensions The extensions.
     */
    public InfoDiscovery(Collection<Identity> identities, Collection<String> features, Collection<DataForm> extensions) {
        this(null, identities, features, extensions);
    }

    /**
     * Creates an info discovery element, used in discovery info responses.
     *
     * @param node       The node.
     * @param identities The identities
     * @param features   The features.
     * @param extensions The extensions.
     */
    public InfoDiscovery(String node, Collection<Identity> identities, Collection<String> features, Collection<DataForm> extensions) {
        this.node = node;
        if (identities != null) {
            this.identity.addAll(identities);
        }
        if (features != null) {
            this.feature.addAll(features.stream().map(Feature::new).collect(Collectors.toList()));
        }
        if (extensions != null) {
            this.extensions.addAll(extensions);
        }
    }

    @Override
    public final Set<Identity> getIdentities() {
        return Collections.unmodifiableSet(identity);
    }

    @Override
    public final Set<String> getFeatures() {
        Set<String> set = feature.stream().filter(f -> f.getVar() != null).map(Feature::getVar).collect(Collectors.toCollection(TreeSet::new));
        return Collections.unmodifiableSet(set);
    }

    @Override
    public final String getNode() {
        return node;
    }

    @Override
    public final List<DataForm> getExtensions() {
        return Collections.unmodifiableList(extensions);
    }

    @Override
    public final String toString() {
        return "Identity: " + identity + "; Features: " + feature;
    }
}
