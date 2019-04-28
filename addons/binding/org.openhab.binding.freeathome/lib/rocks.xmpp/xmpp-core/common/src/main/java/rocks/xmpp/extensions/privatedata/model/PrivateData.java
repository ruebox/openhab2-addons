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

package rocks.xmpp.extensions.privatedata.model;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The implementation of the {@code <query/>} element in the {@code jabber:iq:private} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0049.html">XEP-0049: Private XML Storage</a>
 * @see <a href="https://xmpp.org/extensions/xep-0049.html#sect-idp1528656">XML Schema</a>
 */
@XmlRootElement(name = "query")
public final class PrivateData {

    /**
     * jabber:iq:private
     */
    public static final String NAMESPACE = "jabber:iq:private";

    @XmlAnyElement(lax = true)
    private final Object privateData;

    private PrivateData() {
        this.privateData = null;
    }

    /**
     * Creates private data.
     *
     * @param privateData The private data.
     */
    public PrivateData(Object privateData) {
        this.privateData = privateData;
    }

    /**
     * Gets the private data items.
     *
     * @return The items.
     */
    public final Object getData() {
        return privateData;
    }

    @Override
    public final String toString() {
        return privateData != null ? privateData.toString() : super.toString();
    }
}
