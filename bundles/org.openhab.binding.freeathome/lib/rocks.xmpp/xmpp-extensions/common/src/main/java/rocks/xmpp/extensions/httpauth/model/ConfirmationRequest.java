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

package rocks.xmpp.extensions.httpauth.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URL;

/**
 * The implementation of the {@code <confirm/>} element in the {@code http://jabber.org/protocol/http-auth} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0070.html">XEP-0070: Verifying HTTP Requests via XMPP</a>
 * @see <a href="https://xmpp.org/extensions/xep-0070.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "confirm")
public final class ConfirmationRequest {

    /**
     * http://jabber.org/protocol/http-auth
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/http-auth";

    @XmlAttribute
    private final String id;

    @XmlAttribute
    private final String method;

    @XmlAttribute
    private final URL url;

    private ConfirmationRequest() {
        this(null, null, null);
    }

    /**
     * Creates a confirmation request element.
     *
     * @param id     The transaction identifier provided in the HTTP Authorization Request
     * @param method The method of HTTP request.
     * @param url    The URL that was requested.
     */
    public ConfirmationRequest(String id, String method, URL url) {
        this.id = id;
        this.method = method;
        this.url = url;
    }

    /**
     * Gets the transaction identifier provided in the HTTP Authorization Request.
     *
     * @return The id.
     */
    public final String getId() {
        return id;
    }

    /**
     * Gets the HTTP method.
     *
     * @return The HTTP method.
     */
    public final String getMethod() {
        return method;
    }

    /**
     * Gets the URL to confirm.
     *
     * @return The URL.
     */
    public final URL getUrl() {
        return url;
    }
}
