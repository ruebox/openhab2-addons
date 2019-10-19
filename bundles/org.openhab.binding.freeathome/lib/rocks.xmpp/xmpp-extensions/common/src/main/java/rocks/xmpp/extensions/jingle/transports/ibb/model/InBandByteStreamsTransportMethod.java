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

package rocks.xmpp.extensions.jingle.transports.ibb.model;

import rocks.xmpp.extensions.bytestreams.ibb.model.InBandByteStream;
import rocks.xmpp.extensions.jingle.transports.model.TransportMethod;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

/**
 * The implementation of the {@code <transport/>} element in the {@code urn:xmpp:jingle:transports:ibb:1} namespace.
 *
 * @author Christian Schudt
 */
@XmlRootElement(name = "transport")
public final class InBandByteStreamsTransportMethod extends TransportMethod {

    /**
     * urn:xmpp:jingle:transports:ibb:1
     */
    public static final String NAMESPACE = "urn:xmpp:jingle:transports:ibb:1";

    @XmlAttribute(name = "block-size")
    private final int blockSize;

    @XmlAttribute
    private final String sid;

    @XmlAttribute
    private final InBandByteStream.Open.StanzaType stanza;

    private InBandByteStreamsTransportMethod() {
        this.blockSize = 0;
        this.sid = null;
        this.stanza = null;
    }

    public InBandByteStreamsTransportMethod(int blockSize, String sessionId) {
        this(blockSize, sessionId, null);
    }

    public InBandByteStreamsTransportMethod(int blockSize, String sessionId, InBandByteStream.Open.StanzaType stanzaType) {
        this.sid = Objects.requireNonNull(sessionId);
        this.blockSize = blockSize;
        this.stanza = stanzaType;
    }

    /**
     * Gets the block size of a data chunk.
     *
     * @return The block size.
     */
    public final int getBlockSize() {
        return blockSize;
    }

    /**
     * Gets the IBB session id.
     *
     * @return The session id.
     */
    public final String getSessionId() {
        return sid;
    }

    /**
     * Gets the stanza type used to transfer data.
     *
     * @return The stanza type or null (which means IQ stanzas are used).
     */
    public final InBandByteStream.Open.StanzaType getStanzaType() {
        return stanza;
    }

    @Override
    public final String toString() {
        return "IBB Transport (" + sid + "), block-size: " + blockSize;
    }
}
