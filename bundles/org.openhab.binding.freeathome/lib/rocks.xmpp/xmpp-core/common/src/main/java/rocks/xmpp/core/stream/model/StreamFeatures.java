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

package rocks.xmpp.core.stream.model;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The implementation of the {@code <stream:features/>} element.
 * <p>
 * This class is immutable (if all features are immutable, too).
 *
 * @author Christian Schudt
 */
@XmlRootElement(name = "features")
public final class StreamFeatures implements StreamElement {

    /**
     * http://etherx.jabber.org/streams
     */
    @Deprecated
    public static final String NAMESPACE = "http://etherx.jabber.org/streams";

    @XmlAnyElement(lax = true)
    private final List<Object> features = new ArrayList<>();

    private StreamFeatures() {
    }

    public StreamFeatures(Collection<StreamFeature> features) {
        this.features.addAll(features);
    }

    /**
     * Gets the features, i.e. the child elements of the {@code <stream:features/>} element.
     *
     * @return The features.
     */
    public final List<Object> getFeatures() {
        return Collections.unmodifiableList(features);
    }

    @Override
    public final String toString() {
        return "Stream features: " + features;
    }
}