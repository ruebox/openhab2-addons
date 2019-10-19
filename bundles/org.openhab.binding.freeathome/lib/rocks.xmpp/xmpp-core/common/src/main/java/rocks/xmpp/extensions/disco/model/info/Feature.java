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

package rocks.xmpp.extensions.disco.model.info;

import rocks.xmpp.util.Strings;

import javax.xml.bind.annotation.XmlAttribute;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Represents a feature offered or protocol supported by an XMPP entity.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/extensions/xep-0030.html#info">3. Discovering Information About a Jabber Entity</a></cite></p>
 * <p>This information helps requesting entities determine what actions are possible with regard to this entity (registration, search, join, etc.), what protocols the entity supports, and specific feature types of interest, if any (e.g., for the purpose of feature negotiation).</p>
 * </blockquote>
 * This class is immutable.
 *
 * @author Christian Schudt
 */
final class Feature implements Comparable<Feature> {

    /**
     * Each {@code <feature/>} element MUST possess a 'var' attribute whose value is a protocol namespace or other feature offered by the entity.
     */
    @XmlAttribute
    private final String var;

    private Feature() {
        this.var = null;
    }

    /**
     * Creates a feature.
     *
     * @param var A protocol namespace or other feature offered by the entity.
     */
    Feature(String var) {
        this.var = Objects.requireNonNull(var);
    }

    /**
     * Gets the 'var' attribute, whose value is either a protocol namespace or other feature offered by the entity.
     *
     * @return A protocol namespace or other feature offered by the entity.
     */
    final String getVar() {
        return var;
    }

    /**
     * A feature is considered equal to another one, if their 'var' attribute are equal.
     *
     * @param o The other entity.
     * @return True, if the both features are equal.
     */
    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Feature)) {
            return false;
        }
        Feature other = (Feature) o;
        return Objects.equals(var, other.var);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(var);
    }

    /**
     * Implements a natural ordering of a feature, as suggested and required by <a href="https://xmpp.org/extensions/xep-0115.html">XEP-0115: Entity Capabilities</a>.
     * (The 'var' attributes are compared to each other).
     *
     * @param o The other feature.
     * @return The result of the comparison.
     */
    @Override
    public final int compareTo(Feature o) {
        if (o == null) {
            return 1;
        } else {
            if (var == null && o.var == null) {
                return 0;
            } else if (var == null) {
                return -1;
            } else if (o.var == null) {
                return 1;
            } else {
                return Strings.compareUnsignedBytes(var, o.var, StandardCharsets.UTF_8);
            }
        }
    }

    @Override
    public final String toString() {
        return var;
    }
}
