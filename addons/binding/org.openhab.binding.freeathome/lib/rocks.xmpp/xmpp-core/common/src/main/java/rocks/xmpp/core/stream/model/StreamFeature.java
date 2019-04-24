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

import javax.xml.bind.annotation.XmlTransient;

/**
 * A base class for a stream feature, which is advertised by the server in its {@code <stream:features/>} element.
 *
 * @author Christian Schudt
 */
@XmlTransient
public abstract class StreamFeature implements Comparable<StreamFeature> {

    /**
     * Indicates, whether this feature is mandatory to negotiate.
     * By default a feature is not mandatory.
     * Override this method for mandatory-to-negotiate features.
     *
     * @return True, if the feature is mandatory.
     */
    public boolean isMandatory() {
        return false;
    }

    /**
     * Gets the priority of the feature, i.e. when it will be negotiated during feature negotiation.
     *
     * @return The priority.
     */
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    /**
     * If this feature requires a stream restart after it has been negotiated.
     *
     * @return True, if a stream restart is required.
     */
    public boolean requiresRestart() {
        return false;
    }

    /**
     * Compares two features by their priority and mandatory-to-negotiate flag.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#streams-negotiation-features">4.3.2.  Stream Features Format</a></cite></p>
     * <p>A {@code <features/>} element that contains both mandatory-to-negotiate and voluntary-to-negotiate features
     * indicates that the negotiation is not complete but that the initiating entity MAY complete
     * the voluntary-to-negotiate feature(s) before it attempts to negotiate the mandatory-to-negotiate feature(s).
     * </p>
     * </blockquote>
     *
     * @param o The other feature.
     * @return The comparison result.
     * @see <a href="https://xmpp.org/extensions/xep-0170.html">XEP-0170: Recommended Order of Stream Feature Negotiation</a>
     */
    @Override
    public final int compareTo(StreamFeature o) {
        // First compare by their priority.
        int result = Integer.compare(getPriority(), o.getPriority());
        // If this is equal, put volunteer-to-negotiate features before mandatory ones.
        if (result == 0) {
            return Boolean.compare(isMandatory(), o.isMandatory());
        }
        return result;
    }
}
