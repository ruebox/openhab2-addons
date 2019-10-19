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

package rocks.xmpp.core.stream.model.errors;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The implementation of the {@code <connection-timeout/>} stream error.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#streams-error-conditions-connection-timeout">4.9.3.4.  connection-timeout</a></cite></p>
 * <p>One party is closing the stream because it has reason to believe that the other party has permanently lost the ability to communicate over the stream. The lack of ability to communicate can be discovered using various methods, such as whitespace keepalives as specified under Section 4.4, XMPP-level pings as defined in [XEP-0199], and XMPP Stream Management as defined in [XEP-0198].</p>
 * </blockquote>
 * This class is a singleton.
 *
 * @see #CONNECTION_TIMEOUT
 */
@XmlRootElement(name = "connection-timeout")
@XmlType(factoryMethod = "create")
final class ConnectionTimeout extends Condition {

    ConnectionTimeout() {
    }

    private static ConnectionTimeout create() {
        return (ConnectionTimeout) CONNECTION_TIMEOUT;
    }
}