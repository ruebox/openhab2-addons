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

package rocks.xmpp.extensions.reach.model;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * The implementation of the {@code <address/>} element in the {@code urn:xmpp:reach:0} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0152.html">XEP-0152: Reachability Addresses</a>
 * @see <a href="https://xmpp.org/extensions/xep-0152.html#schema">XML Schema</a>
 */
public final class Address {

    private final List<Description> desc = new ArrayList<>();

    @XmlAttribute
    private final URI uri;

    private Address() {
        this.uri = null;
    }

    public Address(URI uri) {
        this.uri = Objects.requireNonNull(uri);
    }

    public Address(URI uri, Description... descriptions) {
        this.uri = Objects.requireNonNull(uri);
        this.desc.addAll(Arrays.asList(descriptions));
    }

    public final URI getUri() {
        return uri;
    }

    public final List<Description> getDescriptions() {
        return Collections.unmodifiableList(desc);
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Address)) {
            return false;
        }
        Address other = (Address) o;

        return Objects.equals(desc, other.desc)
                && Objects.equals(uri, other.uri);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(desc, uri);
    }

    @Override
    public final String toString() {
        return uri != null ? uri.toString() : super.toString();
    }

    /**
     * The description of the address.
     */
    public static final class Description {

        @XmlValue
        private final String value;

        @XmlAttribute(namespace = XMLConstants.XML_NS_URI)
        private final Locale lang;

        private Description() {
            this(null, null);
        }

        /**
         * @param value    The actual description.
         * @param language The language of the description.
         */
        public Description(String value, Locale language) {
            this.value = value;
            this.lang = language;
        }

        /**
         * Gets the description.
         *
         * @return The description.
         */
        public final String getValue() {
            return value;
        }

        /**
         * Gets the language.
         *
         * @return The language.
         */
        public final Locale getLanguage() {
            return lang;
        }

        @Override
        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Description)) {
                return false;
            }
            Description other = (Description) o;

            return Objects.equals(value, other.value)
                    && Objects.equals(lang, other.lang);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(value, lang);
        }

        @Override
        public final String toString() {
            return value;
        }
    }
}
