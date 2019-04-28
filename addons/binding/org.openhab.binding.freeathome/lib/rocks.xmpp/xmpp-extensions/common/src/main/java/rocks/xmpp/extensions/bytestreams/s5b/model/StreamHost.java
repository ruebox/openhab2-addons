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

import javax.xml.bind.annotation.XmlAttribute;
import java.util.Objects;

/**
 * The {@code <streamhost/>} element.
 *
 * @author Christian Schudt
 */
public final class StreamHost implements Socks5StreamHost {

    @XmlAttribute
    private final Jid jid;

    @XmlAttribute
    private final String host;

    @XmlAttribute
    private final int port;

    private StreamHost() {
        this.jid = null;
        this.host = null;
        this.port = 1080;
    }

    /**
     * Creates a {@code <streamhost/>} element.
     *
     * @param jid  The JID.
     * @param host The host address.
     * @param port The port.
     */
    public StreamHost(Jid jid, String host, int port) {
        this.jid = Objects.requireNonNull(jid);
        this.host = Objects.requireNonNull(host);
        this.port = port;
    }

    /**
     * Gets the IP address or DNS domain name of the StreamHost for SOCKS5 communication over TCP.
     *
     * @return The host.
     */
    @Override
    public final String getHostname() {
        return host;
    }

    /**
     * Gets the JabberID of the StreamHost for communication over XMPP.
     *
     * @return The JID.
     */
    @Override
    public final Jid getJid() {
        return jid;
    }

    /**
     * Get the port on which to connect for SOCKS5 communication over TCP.
     *
     * @return The port.
     */
    @Override
    public final int getPort() {
        return port;
    }

    @Override
    public final String toString() {
        return jid + " (" + host + ":" + port + ')';
    }
}
