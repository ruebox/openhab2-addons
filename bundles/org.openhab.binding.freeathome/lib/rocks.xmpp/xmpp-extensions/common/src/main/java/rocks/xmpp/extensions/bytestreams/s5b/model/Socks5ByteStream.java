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

package rocks.xmpp.extensions.bytestreams.s5b.model;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.util.XmppUtils;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The implementation of the {@code <query/>} element in the {@code http://jabber.org/protocol/bytestreams} namespace.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0065.html">XEP-0065: SOCKS5 Bytestreams</a>
 * @see <a href="https://xmpp.org/extensions/xep-0065.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "query")
public final class Socks5ByteStream {

    /**
     * http://jabber.org/protocol/bytestreams
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/bytestreams";

    private final List<StreamHost> streamhost = new ArrayList<>();

    @XmlElement(name = "streamhost-used")
    private StreamHostUsed streamHostUsed;

    @XmlAttribute
    private String dstaddr;

    private Jid activate;

    @XmlAttribute
    private Mode mode;

    @XmlAttribute
    private String sid;

    /**
     * Creates an empty {@code <query/>} element.
     */
    public Socks5ByteStream() {
    }

    /**
     * Creates a {@code <query/>} element with an {@code <streamhost/>} child elements.
     *
     * @param sessionId   The session id.
     * @param streamHosts The stream hosts.
     * @param dstaddr     The destination address (i.e. the hash).
     */
    public Socks5ByteStream(String sessionId, Collection<StreamHost> streamHosts, String dstaddr) {
        this.sid = Objects.requireNonNull(sessionId);
        this.streamhost.addAll(streamHosts);
        this.dstaddr = dstaddr;
    }

    /**
     * Creates a {@code <query/>} element with an {@code <activate/>} child element.
     *
     * @param sessionId The session id.
     * @param jid       The JID.
     * @return The query element.
     */
    public static Socks5ByteStream activate(String sessionId, Jid jid) {
        Socks5ByteStream socks5ByteStream = new Socks5ByteStream();
        socks5ByteStream.sid = Objects.requireNonNull(sessionId);
        socks5ByteStream.activate = jid;
        return socks5ByteStream;
    }

    /**
     * Creates a {@code <query/>} element with an {@code <streamhost-used/>} child element.
     *
     * @param sessionId session id.
     * @param jid       The JID.
     * @return The query element.
     */
    public static Socks5ByteStream streamHostUsed(String sessionId, Jid jid) {
        Socks5ByteStream socks5ByteStream = new Socks5ByteStream();
        socks5ByteStream.sid = Objects.requireNonNull(sessionId);
        socks5ByteStream.streamHostUsed = new StreamHostUsed(jid);
        return socks5ByteStream;
    }

    /**
     * Creates the hexadecimal-encoded SHA-1 hash for usage in SOCKS5 negotiation.
     *
     * @param sessionId    The session id
     * @param requesterJid The requester JID
     * @param targetJid    The target JID.
     * @return The hexadecimal-encoded SHA-1 hash.
     * @see <a href="https://xmpp.org/extensions/xep-0065.html#mediated-proto-establish">6.3.2 Target Establishes SOCKS5 Connection with Proxy</a>
     */
    public static String hash(String sessionId, Jid requesterJid, Jid targetJid) {
        return XmppUtils.hash((sessionId + requesterJid.toEscapedString() + targetJid.toEscapedString()).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Gets the stream hosts.
     *
     * @return The stream hosts.
     */
    public final List<StreamHost> getStreamHosts() {
        return Collections.unmodifiableList(streamhost);
    }

    /**
     * Gets the session id.
     *
     * @return The session id.
     */
    public final String getSessionId() {
        return sid;
    }

    /**
     * Gets the used stream host.
     *
     * @return The used stream host.
     */
    public final Jid getStreamHostUsed() {
        return streamHostUsed != null ? streamHostUsed.jid : null;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        if (streamHostUsed != null) {
            sb.append(streamHostUsed);
        } else if (activate != null) {
            sb.append("Activate ").append(activate);
        } else {
            sb.append("Stream hosts: ").append(streamhost);
        }
        if (sid != null) {
            sb.append(" (").append(sid).append(')');
        }
        return sb.toString();
    }

    /**
     * The transport mode.
     */
    public enum Mode {
        /**
         * TCP transport (default).
         */
        @XmlEnumValue("tcp")
        TCP,
        /**
         * UDP transport.
         *
         * @see <a href="https://xmpp.org/extensions/xep-0065.html#udp">Optional UDP Support</a>
         */
        @XmlEnumValue("udp")
        UDP
    }
}
