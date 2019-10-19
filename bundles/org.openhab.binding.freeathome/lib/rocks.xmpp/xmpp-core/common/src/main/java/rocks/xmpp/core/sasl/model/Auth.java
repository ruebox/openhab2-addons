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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * The implementation of the {@code <auth/> } element to initialize the SASL authentication process.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-process-neg-initiate">6.4.2.  Initiation</a></cite></p>
 * <p>In order to begin the SASL negotiation, the initiating entity sends an {@code <auth/>} element qualified by the 'urn:ietf:params:xml:ns:xmpp-sasl' namespace and includes an appropriate value for the 'mechanism' attribute, thus starting the handshake for that particular authentication mechanism. This element MAY contain XML character data (in SASL terminology, the "initial response") if the mechanism supports or requires it. If the initiating entity needs to send a zero-length initial response, it MUST transmit the response as a single equals sign character ("="), which indicates that the response is present but contains no data.</p>
 * <p>If the initiating entity subsequently sends another {@code <auth/>} element and the ongoing authentication handshake has not yet completed, the receiving entity MUST discard the ongoing handshake and MUST process a new handshake for the subsequently requested SASL mechanism.</p>
 * </blockquote>
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 */
@XmlRootElement
public final class Auth implements StreamElement {

    @XmlValue
    @XmlJavaTypeAdapter(EmptyResponseAdapter.class)
    private final byte[] initialResponse;

    @XmlAttribute
    private final String mechanism;

    /**
     * Private default constructor, needed for unmarshalling.
     */
    private Auth() {
        this(null, null);
    }

    /**
     * @param mechanism       The SASL mechanism.
     * @param initialResponse The initial response.
     */
    public Auth(String mechanism, byte[] initialResponse) {
        this.mechanism = mechanism;
        this.initialResponse = initialResponse != null ? initialResponse.clone() : null;
    }

    /**
     * Gets the mechanism.
     *
     * @return The mechanism.
     */
    public final String getMechanism() {
        return mechanism;
    }

    /**
     * Gets the initial response.
     *
     * @return The initial response.
     */
    public final byte[] getInitialResponse() {
        return initialResponse != null ? initialResponse.clone() : null;
    }

    @Override
    public final String toString() {
        return "SASL auth mechanism: " + mechanism;
    }
}
