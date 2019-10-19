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
import rocks.xmpp.core.session.model.SessionOpen;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Locale;

/**
 * The implementation of the {@code <open/>} element in the {@code urn:ietf:params:xml:ns:xmpp-framing} namespace.
 *
 * @author Christian Schudt
 * @since 0.7.0
 */
@XmlRootElement
public final class Open extends Frame implements SessionOpen {

    @SuppressWarnings("unused")
    private Open() {
        this(null, null);
    }

    /**
     * Creates an {@code <open/>} element with a 'to' and 'lang' attribute.
     *
     * @param to       The 'to' attribute.
     * @param language The 'lang' attribute.
     */
    public Open(Jid to, Locale language) {
        this(to, null, null, language);
    }

    /**
     * Creates an {@code <open/>} element with a 'to', 'from', 'id' and 'lang' attribute.
     *
     * @param to       The 'to' attribute.
     * @param from     The 'from' attribute.
     * @param id       The 'id' attribute.
     * @param language The 'lang' attribute.
     */
    public Open(Jid to, Jid from, String id, Locale language) {
        this(to, from, id, language, "1.0");
    }

    /**
     * Creates an {@code <open/>} element with a 'to', 'from', 'id' and 'lang' attribute.
     *
     * @param to       The 'to' attribute.
     * @param from     The 'from' attribute.
     * @param id       The 'id' attribute.
     * @param language The 'lang' attribute.
     * @param version  The 'version' attribute.
     */
    public Open(Jid to, Jid from, String id, Locale language, String version) {
        super(to, from, id, language, version);
    }

    @Override
    public final String toString() {
        return "WebSocket Stream Open";
    }
}
