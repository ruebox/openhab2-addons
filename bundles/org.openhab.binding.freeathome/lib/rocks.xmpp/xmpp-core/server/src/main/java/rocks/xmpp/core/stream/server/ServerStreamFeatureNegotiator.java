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

package rocks.xmpp.core.stream.server;

import rocks.xmpp.core.stream.StreamFeatureNegotiator;
import rocks.xmpp.core.stream.StreamNegotiationResult;
import rocks.xmpp.core.stream.model.StreamFeature;

/**
 * An abstract stream feature negotiator from a server perspective.
 *
 * @author Christian Schudt
 * @see ServerStreamFeaturesManager
 */
public abstract class ServerStreamFeatureNegotiator<T extends StreamFeature> implements StreamFeatureNegotiator<T> {

    private final Class<T> featureClass;

    /**
     * Constructs the negotiator.
     *
     * @param featureClass The feature class.
     */
    protected ServerStreamFeatureNegotiator(final Class<T> featureClass) {
        this.featureClass = featureClass;
    }

    /**
     * Creates a concrete stream feature for inclusion in the stream features element after sending the response stream header.
     *
     * @return The stream feature.
     */
    public abstract T createStreamFeature();

    /**
     * Processes an element.
     *
     * @param element The element.
     * @return The stream feature negotiation result.
     */
    @Override
    public abstract StreamNegotiationResult processNegotiation(Object element);

    /**
     * Gets the feature class, this negotiator is responsible for.
     *
     * @return The feature class.
     */
    @Override
    public final Class<T> getFeatureClass() {
        return featureClass;
    }
}
