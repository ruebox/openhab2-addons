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

package rocks.xmpp.extensions.time.model;

import rocks.xmpp.util.adapters.InstantAdapter;
import rocks.xmpp.util.adapters.ZoneOffsetAdapter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * The implementation of the {@code <time/>} element in the {@code urn:xmpp:time} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0202.html">XEP-0202: Entity Time</a>
 * @see <a href="https://xmpp.org/extensions/xep-0202.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "time")
public final class EntityTime {

    /**
     * urn:xmpp:time
     */
    public static final String NAMESPACE = "urn:xmpp:time";

    @XmlJavaTypeAdapter(ZoneOffsetAdapter.class)
    private final ZoneOffset tzo;

    @XmlJavaTypeAdapter(InstantAdapter.class)
    private final Instant utc;

    /**
     * Creates a empty entity time element for requesting entity time.
     */
    public EntityTime() {
        this.tzo = null;
        this.utc = null;
    }

    public EntityTime(OffsetDateTime dateTime) {
        this.tzo = Objects.requireNonNull(dateTime).getOffset();
        this.utc = dateTime.toInstant();
    }

    /**
     * Gets the entity's date.
     *
     * @return The date.
     */
    public final OffsetDateTime getDateTime() {
        return utc != null ? OffsetDateTime.ofInstant(utc, tzo != null ? tzo : ZoneOffset.UTC) : null;
    }

    @Override
    public final String toString() {
        OffsetDateTime dateTime = getDateTime();
        if (dateTime != null) {
            return dateTime.toString();
        }
        return super.toString();
    }
}
