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

package rocks.xmpp.core.sasl.model;

import rocks.xmpp.core.stream.model.StreamElement;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * The implementation of the {@code <success/>} element, which indicates success during SASL negotiation.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-process-neg-success">6.4.6.  SASL Success</a></cite></p>
 * <p>The receiving entity reports success of the handshake by sending a {@code <success/>} element qualified by the 'urn:ietf:params:xml:ns:xmpp-sasl' namespace; this element MAY contain XML character data (in SASL terminology, "additional data with success") if the chosen SASL mechanism supports or requires it.</p>
 * </blockquote>
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 */
@XmlRootElement
public final class Success implements StreamElement {

    @XmlValue
    @XmlJavaTypeAdapter(EmptyResponseAdapter.class)
    private final byte[] additionalData;

    public Success() {
        this(null);
    }

    public Success(byte[] additionalData) {
        this.additionalData = additionalData != null ? additionalData.clone() : null;
    }

    /**
     * Gets additional data.
     * <blockquote><p>This element MAY contain XML character data (in SASL terminology, "additional data with success") if the chosen SASL mechanism supports or requires it.</p></blockquote>
     *
     * @return Additional data.
     */
    public final byte[] getAdditionalData() {
        return additionalData != null ? additionalData.clone() : null;
    }

    @Override
    public final String toString() {
        return "SASL success";
    }
}
