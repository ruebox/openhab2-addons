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

package rocks.xmpp.extensions.hashes.model;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlValue;
import java.util.Arrays;
import java.util.Objects;

/**
 * This class represents a hash value in conjunction with its algorithm.
 * <p>
 * This class overrides {@link #equals} and {@link #hashCode()}, two instances equal each other, if their hash algorithm and value are equal.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0300.html">XEP-0300: Use of Cryptographic Hash Functions in XMPP</a>
 * @see <a href="https://xmpp.org/extensions/xep-0300.html#schema">XML Schema</a>
 */
@XmlRootElement
@XmlSeeAlso(HashUsed.class)
public final class Hash implements Hashed {

    /**
     * urn:xmpp:hashes:2
     */
    public static final String NAMESPACE = "urn:xmpp:hashes:2";

    @XmlValue
    private final byte[] value;

    @XmlAttribute
    private final String algo;

    private Hash() {
        this.value = null;
        this.algo = null;
    }

    /**
     * Creates hash value.
     *
     * @param value     The hash value.
     * @param algorithm The hash algorithm.
     */
    public Hash(byte[] value, String algorithm) {
        this.value = Objects.requireNonNull(value).clone();
        this.algo = Objects.requireNonNull(algorithm);
    }

    public static Hash from(final Hashed hashed) {
        if (hashed instanceof Hash) {
            return (Hash) hashed;
        }
        return new Hash(hashed.getHashValue(), hashed.getHashAlgorithm());
    }

    /**
     * Gets the hash algorithm.
     *
     * @return The hash algorithm.
     */
    @Override
    public final String getHashAlgorithm() {
        return algo;
    }

    /**
     * Gets the hash value.
     *
     * @return The hash value.
     */
    @Override
    public final byte[] getHashValue() {
        return value.clone();
    }

    @Override
    public final boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Hash)) {
            return false;
        }
        Hash other = (Hash) o;

        return Objects.equals(algo, other.algo)
                && Arrays.equals(value, other.value);

    }

    @Override
    public final int hashCode() {
        return Objects.hash(algo, Arrays.hashCode(value));
    }

    @Override
    public final String toString() {
        return "Hash (" + algo + "): " + DatatypeConverter.printBase64Binary(value);
    }
}
