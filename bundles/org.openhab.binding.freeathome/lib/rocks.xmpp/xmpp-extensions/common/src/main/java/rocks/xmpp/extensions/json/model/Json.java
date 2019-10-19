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

package rocks.xmpp.extensions.json.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.util.Objects;

/**
 * The implementation of the {@code <json/>} element in the {@code urn:xmpp:json:0} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0335.html">XEP-0335: JSON Containers</a>
 * @see <a href="https://xmpp.org/extensions/xep-0335.html#schema">XML Schema</a>
 */
@XmlRootElement
public final class Json {

    /**
     * urn:xmpp:json:0
     */
    public static final String NAMESPACE = "urn:xmpp:json:0";

    @XmlValue
    private final String json;

    private Json() {
        this.json = null;
    }

    public Json(String json) {
        this.json = Objects.requireNonNull(json);
    }

    /**
     * Gets the JSON value.
     *
     * @return The JSON value.
     */
    public final String getValue() {
        return json;
    }

    @Override
    public final String toString() {
        return "Json: " + json;
    }
}
