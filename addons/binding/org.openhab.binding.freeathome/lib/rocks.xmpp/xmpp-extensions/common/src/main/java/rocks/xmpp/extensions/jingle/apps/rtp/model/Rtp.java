/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Christian Schudt
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

package rocks.xmpp.extensions.jingle.apps.rtp.model;

import rocks.xmpp.extensions.jingle.apps.model.ApplicationFormat;
import rocks.xmpp.extensions.jingle.apps.rtp.model.errors.CryptoRequired;
import rocks.xmpp.extensions.jingle.apps.rtp.model.errors.InvalidCrypto;
import rocks.xmpp.extensions.jingle.apps.rtp.model.info.Active;
import rocks.xmpp.extensions.jingle.apps.rtp.model.info.Hold;
import rocks.xmpp.extensions.jingle.apps.rtp.model.info.Mute;
import rocks.xmpp.extensions.jingle.apps.rtp.model.info.Ringing;
import rocks.xmpp.extensions.jingle.apps.rtp.model.info.Unhold;
import rocks.xmpp.extensions.jingle.apps.rtp.model.info.Unmute;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of the {@code <description/>} element in the {@code urn:xmpp:jingle:apps:rtp:1} namespace.
 *
 * @author Christian Schudt
 */
@XmlRootElement(name = "description")
@XmlSeeAlso({InvalidCrypto.class, CryptoRequired.class, Active.class, Hold.class, Unhold.class, Mute.class, Unmute.class, Ringing.class})
public final class Rtp extends ApplicationFormat {

    /**
     * urn:xmpp:jingle:apps:rtp:1
     */
    public static final String NAMESPACE = "urn:xmpp:jingle:apps:rtp:1";

    @XmlAttribute
    private String media;

    @XmlAttribute
    private String ssrc;

    @XmlElement(name = "payload-type")
    private final List<PayloadType> payloadTypes = new ArrayList<>();

    private Encryption encryption;

    private Bandwidth bandwidth;

    private Rtp() {
    }

    /**
     * @param media The media type, such as "audio" or "video", where the media type SHOULD be as registered at <a href="www.iana.org/assignments/media-types">IANA MIME Media Types Registry</a>.
     */
    public Rtp(String media) {
        this.media = media;
    }

    /**
     * @param media      The media type, such as "audio" or "video", where the media type SHOULD be as registered at <a href="www.iana.org/assignments/media-types">IANA MIME Media Types Registry</a>.
     * @param ssrc       Specifies the 32-bit synchronization source for this media stream, as defined in RFC 3550.
     * @param bandwidth  The allowable or preferred bandwidth for use by this application type.
     * @param encryption The encryption.
     */
    public Rtp(String media, String ssrc, Bandwidth bandwidth, Encryption encryption) {
        this.media = media;
        this.ssrc = ssrc;
        this.bandwidth = bandwidth;
        this.encryption = encryption;
    }

    /**
     * Gets the media, such as "audio" or "video".
     *
     * @return The media.
     */
    public String getMedia() {
        return media;
    }

    /**
     * Gets the payload types.
     *
     * @return The payload types.
     */
    public List<PayloadType> getPayloadTypes() {
        return payloadTypes;
    }

    /**
     * Gets the encryption element.
     *
     * @return The encryption.
     */
    public Encryption getEncryption() {
        return encryption;
    }

    /**
     * Gets the 32-bit synchronization source for this media stream, as defined in RFC 3550.
     *
     * @return The 32-bit synchronization source for this media stream, as defined in RFC 3550.
     */
    public String getSynchronizationSource() {
        return ssrc;
    }

    /**
     * Gets the band width.
     *
     * @return The band width.
     */
    public Bandwidth getBandwidth() {
        return bandwidth;
    }

    /**
     * The encryption element, which is used for the Secure Real-time Transport Protocol.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0167.html#srtp">7. Negotiation of SRTP</a>
     */
    public static final class Encryption {

        private final List<Crypto> crypto = new ArrayList<>();

        public List<Crypto> getCrypto() {
            return crypto;
        }
    }

    /**
     * The crypto element, which is used for the Secure Real-time Transport Protocol.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0167.html#srtp">7. Negotiation of SRTP</a>
     */
    public static final class Crypto {

        @XmlAttribute(name = "crypto-suite")
        private String cryptoSuite;

        @XmlAttribute(name = "key-params")
        private String keyParams;

        @XmlAttribute(name = "session-params")
        private String sessionParams;

        @XmlAttribute
        private String tag;

        private Crypto() {
        }

        public Crypto(String cryptoSuite, String keyParams, String tag) {
            this.cryptoSuite = cryptoSuite;
            this.keyParams = keyParams;
            this.tag = tag;
        }

        public String getCryptoSuite() {
            return cryptoSuite;
        }

        public String getKeyParameters() {
            return keyParams;
        }

        public String getSessionParameters() {
            return sessionParams;
        }

        public String getTag() {
            return tag;
        }
    }

    /**
     * Specifies the allowable or preferred bandwidth for use by this application type.
     */
    public static final class Bandwidth {

        @XmlAttribute
        private String type;

        private Bandwidth() {
        }

        /**
         * Creates a bandwidth object.
         *
         * @param type Should be a value for the SDP "bwtype" parameter as listed in the IANA Session Description Protocol Parameters Registry.
         */
        public Bandwidth(String type) {
            this.type = type;
        }

        /**
         * Gets the type.
         *
         * @return The type.
         */
        public String getType() {
            return type;
        }
    }

    /**
     * The payload type which specifies an encoding that can be used for the RTP stream.
     */
    public static final class PayloadType {

        private final List<Parameter> parameter = new ArrayList<>();

        @XmlAttribute
        private int channels = 1;

        @XmlAttribute(name = "clockrate")
        private long clockRate;

        @XmlAttribute
        private int id;

        @XmlAttribute
        private long maxptime;

        @XmlAttribute
        private String name;

        @XmlAttribute
        private long ptime;

        private PayloadType() {
        }

        /**
         * Creates a payload type. The id is the only required attribute.
         *
         * @param id The id.
         */
        public PayloadType(int id) {
            this.id = id;
        }

        /**
         * Creates a payload type with all possible attributes.
         *
         * @param id            The id.
         * @param channels      The number of channels.
         * @param clockRate     The sampling frequency in Hertz.
         * @param name          The name.
         * @param packetTime    The packet time.
         * @param maxPacketTime The maximum packet time.
         */
        public PayloadType(int id, int channels, long clockRate, String name, long packetTime, long maxPacketTime) {
            this.id = id;
            this.channels = channels;
            this.clockRate = clockRate;
            this.ptime = packetTime;
            this.maxptime = maxPacketTime;
            this.name = name;
        }

        /**
         * Gets the appropriate subtype of the MIME type.
         *
         * @return The name.
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the sampling frequency in Hertz.
         *
         * @return The sampling frequency.
         */
        public long getClockRate() {
            return clockRate;
        }

        /**
         * Gets the number of channels.
         *
         * @return The number of channels.
         */
        public int getChannels() {
            return channels;
        }

        /**
         * Gets the parameters. For example, as described in RFC 5574, the "cng", "mode", and "vbr" parameters can be specified in relation to usage of the Speex codec.
         *
         * @return The parameters.
         */
        public List<Parameter> getParameters() {
            return parameter;
        }

        /**
         * Gets the payload identifier.
         *
         * @return The payload identifier.
         */
        public int getId() {
            return id;
        }

        /**
         * Gets the maximum packet time as specified in RFC 4566
         *
         * @return The maximum packet time.
         */
        public long getMaxPacketTime() {
            return maxptime;
        }

        /**
         * Gets the packet time as specified in RFC 4566.
         *
         * @return The packet time.
         */
        public long getPacketTime() {
            return ptime;
        }

        /**
         * The parameter for a payload.
         */
        public static final class Parameter {

            @XmlAttribute
            private String name;

            @XmlAttribute
            private String value;

            private Parameter() {
            }

            /**
             * Constructs a parameter with name and value.
             *
             * @param name  The name.
             * @param value The value.
             */
            public Parameter(String name, String value) {
                this.name = name;
                this.value = value;
            }

            /**
             * Gets the parameter name.
             *
             * @return The parameter name.
             */
            public String getName() {
                return name;
            }

            /**
             * Gets the parameter value.
             *
             * @return The parameter value.
             */
            public String getValue() {
                return value;
            }
        }
    }
}
