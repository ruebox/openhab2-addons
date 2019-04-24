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

package rocks.xmpp.core.stream.client;

import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stream.StreamFeatureNegotiator;
import rocks.xmpp.core.stream.model.StreamFeature;

/**
 * A base class to negotiate features.
 * <p>
 * Classes which extend from this class are responsible to negotiate a single feature.
 * </p>
 * Feature negotiators are registered with the {@link StreamFeaturesManager#addFeatureNegotiator(StreamFeatureNegotiator)} method.
 *
 * @author Christian Schudt
 */
public abstract class ClientStreamFeatureNegotiator<T extends StreamFeature> extends Manager implements StreamFeatureNegotiator<T> {

    private final Class<T> featureClass;

    /**
     * Constructs a feature negotiator.
     *
     * @param xmppSession  The XMPP session.
     * @param featureClass The feature class, which represents the feature, which will be negotiated.
     */
    public ClientStreamFeatureNegotiator(XmppSession xmppSession, Class<T> featureClass) {
        super(xmppSession, false);
        this.featureClass = featureClass;
    }

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
