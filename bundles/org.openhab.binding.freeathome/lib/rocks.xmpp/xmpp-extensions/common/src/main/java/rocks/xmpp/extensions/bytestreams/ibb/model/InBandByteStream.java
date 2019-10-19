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

package rocks.xmpp.extensions.bytestreams.ibb.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import java.util.Objects;

/**
 * This class is a container for the three different In-Band ByteStream elements and provides the namespace for IBB, so that it can be used by other protocols such as XEP-0095.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0047.html">XEP-0047: In-Band Bytestreams</a>
 */
@XmlTransient
@XmlSeeAlso({InBandByteStream.Open.class, InBandByteStream.Data.class, InBandByteStream.Close.class})
public abstract class InBandByteStream {

    /**
     * http://jabber.org/protocol/ibb.
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/ibb";

    @XmlAttribute
    private final String sid;

    private InBandByteStream(String sid) {
        this.sid = Objects.requireNonNull(sid);
    }

    private InBandByteStream() {
        this.sid = null;
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
     * The implementation of the {@code <open/>} element in the {@code http://jabber.org/protocol/ibb} namespace.
     * <p>
     * This class is immutable.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0047.html">XEP-0047: In-Band Bytestreams</a>
     * @see <a href="https://xmpp.org/extensions/xep-0047.html#schema">XML Schema</a>
     */
    @XmlRootElement
    public static final class Open extends InBandByteStream {
        @XmlAttribute(name = "block-size")
        private final int blockSize;

        @XmlAttribute
        private final StanzaType stanza;

        private Open() {
            this.blockSize = 0;
            this.stanza = null;
        }

        /**
         * Creates the {@code <open/>} element.
         *
         * @param blockSize The block size.
         * @param sessionId The session id.
         */
        public Open(int blockSize, String sessionId) {
            this(blockSize, sessionId, null);
        }

        /**
         * Creates the {@code <open/>} element.
         *
         * @param blockSize  The block size.
         * @param sessionId  The session id.
         * @param stanzaType The stanza type.
         */
        public Open(int blockSize, String sessionId, StanzaType stanzaType) {
            super(sessionId);
            this.blockSize = blockSize;
            this.stanza = stanzaType;
        }

        /**
         * Gets the block size;
         *
         * @return The block size.
         */
        public final int getBlockSize() {
            return blockSize;
        }

        /**
         * Gets the stanza type.
         *
         * @return The stanza type.
         */
        public final StanzaType getStanzaType() {
            return stanza;
        }

        @Override
        public final String toString() {
            return "IBB open (" + getSessionId() + "), block-size: " + blockSize;
        }

        /**
         * The stanza type to be used for IBB.
         */
        public enum StanzaType {
            /**
             * IQ stanzas are used (default)
             */
            @XmlEnumValue("iq")
            IQ,
            /**
             * Message stanzas are used.
             */
            @XmlEnumValue("message")
            MESSAGE
        }
    }

    /**
     * The implementation of the {@code <data/>} element in the {@code http://jabber.org/protocol/ibb} namespace.
     * <p>
     * This class is immutable.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0047.html">XEP-0047: In-Band Bytestreams</a>
     * @see <a href="https://xmpp.org/extensions/xep-0047.html#schema">XML Schema</a>
     */
    @XmlRootElement
    public static final class Data extends InBandByteStream {

        @XmlAttribute
        private final int seq;

        @XmlValue
        private final byte[] bytes;

        /**
         * Private constructor for unmarshalling.
         */
        private Data() {
            this.seq = 0;
            this.bytes = null;
        }

        /**
         * Creates the {@code <data/>} element.
         *
         * @param bytes The bytes.
         * @param sid   The session id.
         * @param seq   The sequence number.
         */
        public Data(byte[] bytes, String sid, int seq) {
            super(sid);
            this.bytes = Objects.requireNonNull(bytes).clone();
            this.seq = seq;
        }

        /**
         * Gets the sequence number.
         *
         * @return The sequence number.
         */
        public final int getSequence() {
            return seq;
        }

        /**
         * Gets the bytes.
         *
         * @return The bytes.
         */
        public final byte[] getBytes() {
            return bytes.clone();
        }

        @Override
        public final String toString() {
            return "IBB data (" + getSessionId() + "), " + bytes.length + " bytes, seq: " + seq;
        }
    }

    /**
     * The implementation of the {@code <close/>} element in the {@code http://jabber.org/protocol/ibb} namespace.
     * <p>
     * This class is immutable.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0047.html">XEP-0047: In-Band Bytestreams</a>
     * @see <a href="https://xmpp.org/extensions/xep-0047.html#schema">XML Schema</a>
     */
    @XmlRootElement
    public static final class Close extends InBandByteStream {

        private Close() {
        }

        /**
         * Creates the {@code <close/>} element.
         *
         * @param sessionId The session id.
         */
        public Close(String sessionId) {
            super(sessionId);
        }

        @Override
        public final String toString() {
            return "IBB close (" + getSessionId() + ')';
        }
    }
}
