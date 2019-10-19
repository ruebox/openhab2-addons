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

package rocks.xmpp.core.stanza.model.errors;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The implementation of the {@code <remote-server-timeout/>} stanza error.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-remote-server-timeout">8.3.3.17.  remote-server-timeout</a></cite></p>
 * <p>A remote server or service specified as part or all of the JID of the intended recipient (or needed to fulfill a request) was resolved but communications could not be established within a reasonable amount of time (e.g., an XML stream cannot be established at the resolved IP address and port, or an XML stream can be established but stream negotiation fails because of problems with TLS, SASL, Server Dialback, etc.); the associated error type SHOULD be "wait" (unless the error is of a more permanent nature, e.g., the remote server is found but it cannot be authenticated or it violates security policies).</p>
 * </blockquote>
 * This class is a singleton.
 *
 * @see #REMOTE_SERVER_TIMEOUT
 */
@XmlRootElement(name = "remote-server-timeout")
@XmlType(factoryMethod = "create")
final class RemoteServerTimeout extends Condition {

    RemoteServerTimeout() {
    }

    private static RemoteServerTimeout create() {
        return (RemoteServerTimeout) REMOTE_SERVER_TIMEOUT;
    }
}