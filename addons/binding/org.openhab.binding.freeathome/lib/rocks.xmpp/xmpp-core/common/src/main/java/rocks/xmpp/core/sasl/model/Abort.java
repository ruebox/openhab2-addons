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
import javax.xml.bind.annotation.XmlType;

/**
 * The implementation of the {@code <abort/>} element to abort SASL negotiation.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-process-neg-abort">6.4.4.  Abort</a></cite></p>
 * <p>The initiating entity aborts the handshake for this authentication mechanism by sending an {@code <abort/>} element qualified by the 'urn:ietf:params:xml:ns:xmpp-sasl' namespace.</p>
 * <p>Upon receiving an {@code <abort/>} element, the receiving entity MUST return a {@code <failure/>} element qualified by the 'urn:ietf:params:xml:ns:xmpp-sasl' namespace and containing an {@code <aborted/>} child element.</p>
 * </blockquote>
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 */
@XmlRootElement
@XmlType(factoryMethod = "create")
public final class Abort implements StreamElement {

    /**
     * The implementation of the {@code <abort/>} element.
     */
    public static final Abort INSTANCE = new Abort();

    private Abort() {
    }

    private static Abort create() {
        return INSTANCE;
    }

    @Override
    public final String toString() {
        return "SASL abort";
    }
}
