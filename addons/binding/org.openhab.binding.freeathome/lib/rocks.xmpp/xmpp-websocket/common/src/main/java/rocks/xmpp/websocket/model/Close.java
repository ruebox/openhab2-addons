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

package rocks.xmpp.websocket.model;

import rocks.xmpp.addr.Jid;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Locale;

/**
 * The implementation of the {@code <close/>} element in the {@code urn:ietf:params:xml:ns:xmpp-framing} namespace.
 *
 * @author Christian Schudt
 * @since 0.7.0
 */
@XmlRootElement
public final class Close extends Frame {

    @XmlAttribute(name = "see-other-uri")
    private final URI uri;

    /**
     * Creates an empty {@code <close/>}.
     */
    public Close() {
        this(null);
    }

    /**
     * Creates an {@code <close/>} with an "see-other-uri" attribute.
     *
     * @param uri The 'see-other-uri' attribute.
     */
    public Close(URI uri) {
        this(null, null, null, null, null, uri);
    }

    /**
     * Creates an {@code <close/>} element with a 'to', 'from', 'id', 'lang' and 'see-other-uri' attribute.
     *
     * @param to       The 'to' attribute.
     * @param from     The 'from' attribute.
     * @param id       The 'id' attribute.
     * @param language The 'lang' attribute.
     * @param version  The 'version' attribute.
     * @param uri      The 'see-other-uri' attribute.
     */
    public Close(Jid to, Jid from, String id, Locale language, String version, URI uri) {
        super(to, from, id, language, version);
        this.uri = uri;
    }

    /**
     * The "see-other-uri" attribute.
     *
     * @return The URI.
     * @see <a href="https://tools.ietf.org/html/rfc7395#section-3.6.1">3.6.1.  see-other-uri</a>
     */
    public final URI getUri() {
        return uri;
    }

    @Override
    public final String toString() {
        return "WebSocket Stream Close";
    }
}
