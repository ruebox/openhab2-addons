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

package rocks.xmpp.core.tls.model;

import rocks.xmpp.core.stream.model.StreamElement;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the TLS proceed case.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#tls-process-initiate-proceed">5.4.2.3.  Proceed Case</a></cite></p>
 * <p>If the proceed case occurs, the receiving entity MUST return a {@code <proceed/>} element qualified by the 'urn:ietf:params:xml:ns:xmpp-tls' namespace.</p>
 * </blockquote>
 */
@XmlRootElement
@XmlType(factoryMethod = "create")
public final class Proceed implements StreamElement {

    /**
     * The {@code <proceed xmlns="urn:ietf:params:xml:ns:xmpp-tls"/>} element.
     */
    public static final Proceed INSTANCE = new Proceed();

    private Proceed() {
    }

    private static Proceed create() {
        return INSTANCE;
    }

    @Override
    public final String toString() {
        return "StartTLS proceed";
    }
}