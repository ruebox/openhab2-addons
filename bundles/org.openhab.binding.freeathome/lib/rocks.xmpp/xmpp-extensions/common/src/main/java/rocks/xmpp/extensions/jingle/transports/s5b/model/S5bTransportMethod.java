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

package rocks.xmpp.extensions.jingle.transports.s5b.model;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.extensions.bytestreams.s5b.model.Socks5ByteStream;
import rocks.xmpp.extensions.bytestreams.s5b.model.Socks5StreamHost;
import rocks.xmpp.extensions.jingle.transports.model.TransportMethod;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The implementation of the {@code <transport/>} element in the {@code urn:xmpp:jingle:transports:s5b:1} namespace.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0260.html">XEP-0260: Jingle SOCKS5 Bytestreams Transport Method</a>
 * @see <a href="https://xmpp.org/extensions/xep-0260.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "transport")
public final class S5bTransportMethod extends TransportMethod {

    /**
     * urn:xmpp:jingle:transports:s5b:1
     */
    public static final String NAMESPACE = "urn:xmpp:jingle:transports:s5b:1";

    @XmlAttribute
    private final String sid;

    @XmlAttribute
    private final String dstaddr;

    @XmlAttribute
    private final Socks5ByteStream.Mode mode;

    private final List<Candidate> candidate = new ArrayList<>();

    @XmlElement(name = "candidate-used")
    private final CandidateUsed candidateUsed;

    private final Activated activated;

    @XmlElement(name = "candidate-error")
    private final String candidateError;

    @XmlElement(name = "proxy-error")
    private final String proxyError;

    private S5bTransportMethod() {
        this(null, null, null, Collections.emptyList());
    }

    public S5bTransportMethod(String sessionId, String dstaddr, Socks5ByteStream.Mode mode, Collection<Candidate> candidates) {
        this(sessionId, dstaddr, mode, candidates, null, null, false, false);
    }

    private S5bTransportMethod(String sessionId, String dstaddr, Socks5ByteStream.Mode mode, Collection<Candidate> candidates, CandidateUsed candidateUsed, Activated activated, boolean candidateError, boolean proxyError) {
        this.sid = sessionId;
        this.dstaddr = dstaddr;
        this.mode = mode;
        this.candidate.addAll(candidates);
        this.candidateUsed = candidateUsed;
        this.activated = activated;
        this.candidateError = candidateError ? "" : null;
        this.proxyError = proxyError ? "" : null;
    }

    /**
     * Creates a transport method with a {@code <candidate-used/>} element.
     *
     * @param sid The session id.
     * @param cid The candidate id.
     * @return The transport method.
     */
    public static S5bTransportMethod candidateUsed(String sid, String cid) {
        return new S5bTransportMethod(sid, null, null, Collections.emptyList(), new CandidateUsed(cid), null, false, false);
    }

    /**
     * Creates a transport method with a {@code <candidate-error/>} element.
     *
     * @param sid The session id.
     * @return The transport method.
     */
    public static S5bTransportMethod candidateError(String sid) {
        return new S5bTransportMethod(sid, null, null, Collections.emptyList(), null, null, true, false);
    }

    /**
     * Creates a transport method with a {@code <proxy-error/>} element.
     *
     * @param sid The session id.
     * @return The transport method.
     */
    public static S5bTransportMethod proxyError(String sid) {
        return new S5bTransportMethod(sid, null, null, Collections.emptyList(), null, null, false, true);
    }

    /**
     * Creates a transport method with a {@code <activated/>} element.
     *
     * @param sid The session id.
     * @param cid The id of the activated candidate.
     * @return The transport method.
     */
    public static S5bTransportMethod activated(String sid, String cid) {
        return new S5bTransportMethod(sid, null, null, Collections.emptyList(), null, new Activated(cid), false, false);
    }

    /**
     * Gets the preferred priority. Note that the calculated priority is only a recommendation.
     *
     * @param type            The type.
     * @param localPreference The local preference, should be between 0 and 65535.
     * @return The calculated preferred priority.
     */
    public static int calculatePriority(S5bTransportMethod.Candidate.Type type, int localPreference) {
        // (2^16)*(type preference) + (local preference)
        return 0xFFFF * (type == null ? Candidate.Type.DIRECT.getPreferenceValue() : type.getPreferenceValue()) + localPreference;
    }

    /**
     * Gets the DST.ADDR field for the SOCKS5 protocol.
     * In XMPP this is SHA-1 hash of <i>session id</i> + <i>requester JID</i> + <i>receiver JID</i>
     *
     * @return The DST.ADDR field.
     */
    public final String getDstAddr() {
        return dstaddr;
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
     * Gets the mode.
     *
     * @return The mode.
     */
    public final Socks5ByteStream.Mode getMode() {
        return mode;
    }

    /**
     * Gets the candidates, i.e. stream hosts for the transport.
     *
     * @return The candidates.
     */
    public final List<Candidate> getCandidates() {
        return Collections.unmodifiableList(candidate);
    }

    /**
     * Gets the id of the used candidate.
     *
     * @return The id or null.
     */
    public final String getCandidateUsed() {
        return candidateUsed != null ? candidateUsed.cid : null;
    }

    /**
     * Gets the id of the activated candidate.
     *
     * @return The id or null.
     */
    public final String getActivated() {
        return activated != null ? activated.cid : null;
    }

    /**
     * Indicates, if it's a candidate error.
     *
     * @return If it's a candidate error.
     */
    public final boolean isCandidateError() {
        return candidateError != null;
    }

    /**
     * Indicates, if it's a proxy error.
     *
     * @return If it's a proxy error.
     */
    public final boolean isProxyError() {
        return proxyError != null;
    }

    /**
     * The implementation of the {@code <candidate/>} element in the {@code urn:xmpp:jingle:transports:s5b:1} namespace.
     * <p>
     * Candidates are possible stream hosts for the transport.
     * <p>
     * The best (preferred) candidate is the one, with the highest priority.
     * Multiple candidates are naturally sorted by their priority (highest first).
     */
    public static final class Candidate implements Socks5StreamHost, Comparable<Candidate> {

        @XmlAttribute
        private final String cid;

        @XmlAttribute
        private final String host;

        @XmlAttribute
        private final Jid jid;

        @XmlAttribute
        private final int port;

        @XmlAttribute
        private final int priority;

        @XmlAttribute
        private final Type type;

        private Candidate() {
            this(null, null, 0, null, null, 0);
        }

        /**
         * @param cid      The candidate id.
         * @param hostname The hostname.
         * @param port     The port.
         * @param jid      The JID.
         * @param type     The type.
         * @param priority The priority, see {@code calculatePriority(Type, int)}.
         */
        public Candidate(String cid, String hostname, int port, Jid jid, Type type, int priority) {
            this.cid = cid;
            this.host = hostname;
            this.jid = jid;
            this.type = type;
            this.port = port;
            this.priority = priority;
        }

        /**
         * Gets the candidate id.
         *
         * @return The candidate id.
         */
        public final String getCid() {
            return cid;
        }

        /**
         * Gets the candidate's hostname.
         *
         * @return The candidate's hostname.
         */
        @Override
        public final String getHostname() {
            return host;
        }

        /**
         * Gets the candidate's JID.
         *
         * @return The candidate's JID.
         */
        @Override
        public final Jid getJid() {
            return jid;
        }

        /**
         * Gets the port.
         *
         * @return The port.
         */
        @Override
        public final int getPort() {
            return port;
        }

        /**
         * Gets the priority of this candidate.
         *
         * @return The priority.
         */
        public final int getPriority() {
            return priority;
        }

        /**
         * Gets the transport type.
         *
         * @return The transport type.
         */
        public final Type getType() {
            return type;
        }

        @Override
        public final int compareTo(Candidate o) {
            return Integer.compare(o.priority, priority);
        }

        /**
         * The transport type.
         */
        public enum Type {
            /**
             * Direct connection using the given interface.
             */
            @XmlEnumValue("direct")
            DIRECT(126),
            /**
             * Direct connection using NAT assisting technologies like NAT-PMP or UPnP-IGD.
             */
            @XmlEnumValue("assisted")
            ASSISTED(120),
            /**
             * Tunnel protocols such as Teredo.
             */
            @XmlEnumValue("tunnel")
            TUNNEL(110),
            /**
             * SOCKS5 Relay.
             */
            @XmlEnumValue("proxy")
            PROXY(10);

            private final int preferenceValue;

            Type(int preferenceValue) {
                this.preferenceValue = preferenceValue;
            }

            /**
             * The preference value.
             *
             * @return The preference value.
             */
            public final int getPreferenceValue() {
                return preferenceValue;
            }
        }
    }

    private static final class CandidateUsed {
        @XmlAttribute
        private final String cid;

        private CandidateUsed() {
            this.cid = null;
        }

        public CandidateUsed(String cid) {
            this.cid = Objects.requireNonNull(cid);
        }
    }

    private static final class Activated {
        @XmlAttribute
        private final String cid;

        private Activated() {
            this.cid = null;
        }

        public Activated(String cid) {
            this.cid = Objects.requireNonNull(cid);
        }
    }
}
