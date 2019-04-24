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

package rocks.xmpp.extensions.featureneg.model;

import rocks.xmpp.extensions.data.model.DataForm;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

/**
 * The implementation of the {@code <feature/>} element in the {@code http://jabber.org/protocol/feature-neg} namespace.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/extensions/xep-0020.html#intro">1. Introduction</a></cite></p>
 * <p>The protocol defined herein enables Jabber entities to negotiate options for specific features. These features could be negotiated between any two endpoints on the Jabber network, such as two clients, a client and a component, two components, a client and a server, or two servers. The protocol is generic enough that it can be used whenever options need to be negotiated between two Jabber entities. For examples, Stream Initiation (XEP-0095) [2], SI File Transfer (XEP-0096) [3] or Stanza Session Negotiation (XEP-0155) [4].</p>
 * </blockquote>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0020.html">XEP-0020: Feature Negotiation</a>
 * @see <a href="https://xmpp.org/extensions/xep-0020.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "feature")
public final class FeatureNegotiation {

    /**
     * http://jabber.org/protocol/feature-neg
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/feature-neg";

    @XmlElementRef
    private final DataForm dataForm;

    private FeatureNegotiation() {
        this.dataForm = null;
    }

    /**
     * Creates a feature negotiation with structured data.
     *
     * @param dataForm The structured data form, which contains feature negotiation details.
     */
    public FeatureNegotiation(DataForm dataForm) {
        this.dataForm = Objects.requireNonNull(dataForm);
    }

    /**
     * Gets the data form, which contains feature negotiation details.
     *
     * @return The data form.
     */
    public final DataForm getDataForm() {
        return dataForm;
    }
}
