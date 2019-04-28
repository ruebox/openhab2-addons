/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Christian Schudt
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

package rocks.xmpp.core.stream;

import rocks.xmpp.core.stream.model.StreamFeature;

/**
 * An interface for various stream negotiators, either from client perspective or server perspective.
 * <p>
 * A stream negotiator receives an element from the stream and tries to negotiate the stream feature.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/rfcs/rfc6120.html#streams-negotiation">4.3.  Stream Negotiation</a>
 */
public interface StreamFeatureNegotiator<T extends StreamFeature> {

    /**
     * Processes a feature protocol element or the feature element itself.
     *
     * @param element The XML element, which belongs to the feature negotiation, e.g. {@code <challenge/>} for SASL negotiation or the feature element itself, e.g. {@code <mechanisms/>}.
     * @return The result of the feature negotiation.
     * @throws StreamNegotiationException Any exception which might be thrown during a feature negotiation.
     */
    StreamNegotiationResult processNegotiation(Object element) throws StreamNegotiationException;

    /**
     * Checks, whether the element can be processed by the feature negotiator.
     *
     * @param element The feature protocol element, e.g. {@code <challenge/>}. The element is never the feature element itself, e.g. {@code <mechanisms/>}, which is advertised in the {@code <stream:features/>} element.
     * @return True, if the element can be processed by the feature negotiator.
     */
    boolean canProcess(Object element);

    /**
     * Gets the feature class, this negotiator is responsible for.
     *
     * @return The feature class.
     */
    Class<T> getFeatureClass();
}
