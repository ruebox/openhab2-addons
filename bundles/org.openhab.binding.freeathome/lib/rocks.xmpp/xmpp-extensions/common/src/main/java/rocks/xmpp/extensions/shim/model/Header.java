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

package rocks.xmpp.extensions.shim.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * A header element which hold stanza header information or internet metadata.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0131.html">XEP-0131: Stanza Headers and Internet Metadata</a>
 * @see <a href="https://xmpp.org/extensions/xep-0131.html#schema">XML Schema</a>
 * @see Headers
 */
public final class Header {

    @XmlAttribute
    private final String name;

    @XmlValue
    private final String value;

    private Header() {
        this(null, null);
    }

    private Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Creates a header.
     *
     * @param name  The name of the header. See <a href="https://xmpp.org/extensions/xep-0131.html#registrar-shim">9.3 SHIM Headers Registry</a> for registered headers.
     * @param value The header value.
     * @return The header.
     * @see <a href="https://xmpp.org/extensions/xep-0131.html#registrar-shim">9.3 SHIM Headers Registry</a>
     */
    public static Header of(String name, String value) {
        return new Header(Objects.requireNonNull(name), value);
    }

    /**
     * Creates a header with a start date.
     *
     * @param dateTime The start date.
     * @return The header.
     * @see <a href="https://xmpp.org/extensions/xep-0149.html">XEP-0149: Time Periods</a>
     */
    public static Header ofStartDate(OffsetDateTime dateTime) {
        return new Header("Start", dateTime.toString());
    }

    /**
     * Creates a header with a stop date.
     *
     * @param dateTime The stop date.
     * @return The header.
     * @see <a href="https://xmpp.org/extensions/xep-0149.html">XEP-0149: Time Periods</a>
     */
    public static Header ofStopDate(OffsetDateTime dateTime) {
        return new Header("Stop", dateTime.toString());
    }

    /**
     * Gets the name of the header.
     *
     * @return The header.
     */
    public final String getName() {
        return name;
    }

    /**
     * Gets the value of the header.
     *
     * @return The header.
     */
    public final String getValue() {
        return value;
    }

    @Override
    public final String toString() {
        return name + ": " + value;
    }
}
