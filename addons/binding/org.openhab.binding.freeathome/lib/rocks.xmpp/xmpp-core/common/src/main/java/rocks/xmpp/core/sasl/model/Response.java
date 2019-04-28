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

/**
 * The implementation of the {@code <response/>} element, which is sent during the SASL negotiation.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-process-neg-challengeresponse">6.4.3.  Challenge-Response Sequence</a></cite></p>
 * <p>The initiating entity responds to the challenge by sending a {@code <response/>} element qualified by the 'urn:ietf:params:xml:ns:xmpp-sasl' namespace; this element MAY contain XML character data (which MUST be generated in accordance with the definition of the SASL mechanism chosen by the initiating entity).</p>
 * </blockquote>
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see Challenge
 */
@XmlRootElement
public final class Response implements StreamElement {

    @XmlValue
    private final byte[] value;

    /**
     * Default constructor, needed for unmarshalling.
     */
    private Response() {
        this.value = null;
    }

    /**
     * Creates a response to a {@link rocks.xmpp.core.sasl.model.Challenge}.
     *
     * @param value The value.
     */
    public Response(byte[] value) {
        this.value = value != null ? value.clone() : null;
    }

    /**
     * Gets the value.
     *
     * @return The value.
     */
    public final byte[] getValue() {
        return value != null ? value.clone() : null;
    }

    @Override
    public final String toString() {
        return "SASL response";
    }
}
