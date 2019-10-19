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

package rocks.xmpp.extensions.httpbind.model;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.model.SessionOpen;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * The implementation of the {@code <body/>} element in the {@code http://jabber.org/protocol/httpbind} namespace.
 * <p>
 * The natural ordering of this class is ordering by request ids (RID).
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0124.html">XEP-0124: Bidirectional-streams Over Synchronous HTTP (BOSH)</a>
 * @see <a href="https://xmpp.org/extensions/xep-0206.html">XEP-0206: XMPP Over BOSH</a>
 * @see <a href="https://xmpp.org/extensions/xep-0124.html#schema">XML Schema</a>
 */
@XmlRootElement
public final class Body implements SessionOpen, Comparable<Body> {

    /**
     * http://jabber.org/protocol/httpbind
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/httpbind";

    /**
     * urn:xmpp:xbosh
     */
    public static final String XBOSH_NAMESPACE = "urn:xmpp:xbosh";

    @XmlAnyElement(lax = true)
    private final List<Object> wrappedObjects = new ArrayList<>();

    private final URI uri;

    /**
     * The connection manager MAY include an 'accept' attribute in the session creation response element, to specify a space-separated list of the content encodings it can decompress. After receiving a session creation response with an 'accept' attribute, clients MAY include an HTTP Content-Encoding header in subsequent requests (indicating one of the encodings specified in the 'accept' attribute) and compress the bodies of the requests accordingly.
     */
    @XmlAttribute
    private final String accept;

    /**
     * A connection manager MAY include an 'ack' attribute (set to the value of the 'rid' attribute of the session creation request) to indicate that it will be using acknowledgements throughout the session and that the absence of an 'ack' attribute in any response is meaningful (see Acknowledgements).
     */
    @XmlAttribute
    private final Long ack;

    @XmlAttribute(name = "authid")
    private final String authId;

    @XmlAttribute
    @XmlJavaTypeAdapter(CharsetAdapter.class)
    private final List<Charset> charsets;

    @XmlAttribute
    private final Condition condition;

    @XmlAttribute
    private final String content;

    @XmlAttribute
    private final Jid from;

    /**
     * This attribute informs the client about the maximum number of requests the connection manager will keep waiting at any one time during the session. This value MUST NOT be greater than the value specified by the client in the session request.
     */
    @XmlAttribute
    private final Short hold;

    /**
     * This attribute specifies the longest allowable inactivity period (in seconds). This enables the client to ensure that the periods with no requests pending are never too long (see Polling Sessions and Inactivity).
     */
    @XmlAttribute
    @XmlJavaTypeAdapter(SecondsAdapter.class)
    private final Duration inactivity;

    @XmlAttribute
    private final String key;

    /**
     * If the connection manager supports session pausing (see Inactivity) then it SHOULD advertise that to the client by including a 'maxpause' attribute in the session creation response element.
     * The value of the attribute indicates the maximum length of a temporary session pause (in seconds) that a client can request.
     */
    @XmlAttribute(name = "maxpause")
    @XmlJavaTypeAdapter(SecondsAdapter.class)
    private final Duration maxPause;

    @XmlAttribute(name = "newkey")
    private final String newKey;

    @XmlAttribute
    @XmlJavaTypeAdapter(SecondsAdapter.class)
    private final Duration pause;

    /**
     * This attribute specifies the shortest allowable polling interval (in seconds). This enables the client to not send empty request elements more often than desired (see Polling Sessions and Overactivity).
     */
    @XmlAttribute
    @XmlJavaTypeAdapter(SecondsAdapter.class)
    private final Duration polling;

    @XmlAttribute
    private final Long report;

    /**
     * This attribute enables the connection manager to limit the number of simultaneous requests the client makes (see Overactivity and Polling Sessions). The RECOMMENDED values are either "2" or one more than the value of the 'hold' attribute specified in the session request.
     */
    @XmlAttribute
    private final Short requests;

    @XmlAttribute
    private final Long rid;

    @XmlAttribute
    private final String route;

    @XmlAttribute
    private final String sid;

    @XmlAttribute
    private final String stream;

    @XmlAttribute
    @XmlJavaTypeAdapter(MillisecondsAdapter.class)
    private final Duration time;

    /**
     * This attribute communicates the identity of the backend server to which the client is attempting to connect.
     */
    @XmlAttribute
    private final Jid to;

    @XmlAttribute
    private final Type type;

    /**
     * This attribute specifies the highest version of the BOSH protocol that the connection manager supports, or the version specified by the client in its request, whichever is lower.
     */
    @XmlAttribute
    private final String ver;

    @XmlAttribute
    @XmlJavaTypeAdapter(SecondsAdapter.class)
    private final Duration wait;

    @XmlAttribute(namespace = XMLConstants.XML_NS_URI)
    private final Locale lang;

    @XmlAttribute(namespace = XBOSH_NAMESPACE)
    private final String version;

    @XmlAttribute(namespace = XBOSH_NAMESPACE)
    private final Boolean restartlogic;

    @XmlAttribute(namespace = XBOSH_NAMESPACE)
    private final Boolean restart;

    private Body() {
        this.uri = null;
        this.accept = null;
        this.ack = null;
        this.authId = null;
        this.charsets = null;
        this.condition = null;
        this.content = null;
        this.from = null;
        this.hold = null;
        this.inactivity = null;
        this.key = null;
        this.maxPause = null;
        this.newKey = null;
        this.pause = null;
        this.polling = null;
        this.report = null;
        this.requests = null;
        this.rid = null;
        this.route = null;
        this.sid = null;
        this.stream = null;
        this.time = null;
        this.to = null;
        this.type = null;
        this.ver = null;
        this.wait = null;
        this.lang = null;

        this.restart = null;
        this.restartlogic = null;
        this.version = null;
    }

    private Body(Builder builder) {
        this.uri = builder.uri;
        this.accept = builder.accept;
        this.ack = builder.ack;
        this.authId = builder.authId;
        this.charsets = builder.charsets != null ? Arrays.asList(builder.charsets) : null;
        this.condition = builder.condition;
        this.content = builder.content;
        this.from = builder.from;
        this.hold = builder.hold;
        this.inactivity = builder.inactivity;
        this.key = builder.key;
        this.maxPause = builder.maxPause;
        this.newKey = builder.newKey;
        this.pause = builder.pause;
        this.polling = builder.polling;
        this.report = builder.report;
        this.requests = builder.requests;
        this.rid = builder.requestId;
        this.route = builder.route;
        this.sid = builder.sessionId;
        this.stream = builder.stream;
        this.time = builder.time;
        this.to = builder.to;
        this.type = builder.type;
        this.ver = builder.version;
        this.wait = builder.wait;
        this.lang = builder.language;
        this.restart = builder.restart;

        this.restartlogic = builder.restartlogic;
        this.version = builder.xmppVersion;
        this.wrappedObjects.addAll(builder.wrappedObjects);
    }

    /**
     * Creates a builder for the body element.
     *
     * @return The body.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets an unmodifiable list of wrapped objects.
     *
     * @return The wrapped objects.
     */
    public final List<Object> getWrappedObjects() {
        return Collections.unmodifiableList(wrappedObjects);
    }

    /**
     * If the connection manager reports a {@link Condition#SEE_OTHER_URI} error condition, this method returns the URI.
     *
     * @return The URI.
     * @see Condition#SEE_OTHER_URI
     */
    public final URI getUri() {
        return uri;
    }

    /**
     * The connection manager MAY include an 'accept' attribute in the session creation response element, to specify a comma-separated list of the content encodings it can decompress.
     * After receiving a session creation response with an 'accept' attribute, clients MAY include an HTTP Content-Encoding header in subsequent requests (indicating one of the encodings specified in the 'accept' attribute) and compress the bodies of the requests accordingly.
     *
     * @return The comma-separated list of the content encodings.
     */
    public final String getAccept() {
        return accept;
    }

    /**
     * A client MAY include an 'ack' attribute (set to "1") to indicate that it will be using acknowledgements throughout the session and that the absence of an 'ack' attribute in any request is meaningful.
     * When responding to a request that it has been holding, if the connection manager finds it has already received another request with a higher 'rid' attribute (typically while it was holding the first request), then it MAY acknowledge the reception to the client. The connection manager MAY set the 'ack' attribute of any response to the value of the highest 'rid' attribute it has received in the case where it has also received all requests with lower 'rid' values.
     *
     * @return The acknowledged request.
     * @see #getReport()
     * @see #getTime()
     */
    public final Long getAck() {
        return ack;
    }

    /**
     * Gets the 'authid' attribute, which contains the value of the XMPP stream ID generated by the XMPP server.
     * This value is needed only by legacy XMPP clients in order to complete digest authentication using the obsolete Non-SASL Authentication (XEP-0078) protocol.
     *
     * @return The 'authId' attribute.
     */
    public final String getAuthId() {
        return authId;
    }

    /**
     * The connection manager MAY inform the client which encodings it can convert by setting the optional 'charsets' attribute in the session creation response element to a space-separated list of encodings.
     *
     * @return The available charsets.
     */
    public final List<Charset> getCharsets() {
        return charsets != null ? charsets : Collections.emptyList();
    }

    /**
     * Gets a terminal binding condition.
     *
     * @return The condition.
     * @see #getType()
     * @see <a href="https://xmpp.org/extensions/xep-0124.html#errorstatus-terminal">17.2 Terminal Binding Conditions</a>
     */
    public final Condition getCondition() {
        return condition;
    }

    /**
     * Some clients are constrained to only accept HTTP responses with specific Content-Types (e.g., "text/html"). The {@code <body/>} element of the first request MAY possess a 'content' attribute. This specifies the value of the HTTP Content-Type header that MUST appear in all the connection manager's responses during the session. If the client request does not possess a 'content' attribute, then the HTTP Content-Type header of responses MUST be "text/xml; charset=utf-8".
     *
     * @return The content type.
     */
    public final String getContent() {
        return content;
    }

    /**
     * The {@code <body/>} element of the first request MAY also possess a 'from' attribute, which specifies the originator of the first stream and which enables the connection manager to forward the originating entity's identity to the application server.
     *
     * @return The 'from' attribute.
     */
    @Override
    public final Jid getFrom() {
        return from;
    }

    /**
     * This attribute informs the client about the maximum number of requests the connection manager will keep waiting at any one time during the session. This value MUST NOT be greater than the value specified by the client in the session request.
     * The client SHOULD set the 'hold' attribute to a value of "1".
     *
     * @return The 'hold' attribute.
     * @see #getRequests()
     */
    public final Short getHold() {
        return hold;
    }

    /**
     * After receiving a response from the connection manager, if none of the client's requests are still being held by the connection manager
     * (and if the session is not a Polling Session), the client SHOULD make a new request as soon as possible.
     * In any case, if no requests are being held, the client MUST make a new request before the maximum inactivity period has expired.
     * The length of this period (in seconds) is specified by the 'inactivity' attribute in the session creation response.
     *
     * @return The inactivity period or null.
     * @see #getPause()
     * @see #getMaxPause()
     * @see <a href="https://xmpp.org/extensions/xep-0124.html#inactive">10. Inactivity</a>
     */
    public final Duration getInactivity() {
        return inactivity;
    }

    /**
     * Gets the key used to protect insecure connections.
     *
     * @return The key.
     * @see #getNewKey()
     * @see <a href="https://xmpp.org/extensions/xep-0124.html#keys">15. Protecting Insecure Sessions</a>
     */
    public final String getKey() {
        return key;
    }

    /**
     * If the connection manager supports session pausing (see Inactivity)
     * then it SHOULD advertise that to the client by including a 'maxpause' attribute in the session creation response element.
     * The value of the attribute indicates the maximum length of a temporary session pause (in seconds) that a client can request.
     *
     * @return The maximal pause or null.
     * @see #getPause()
     * @see #getInactivity()
     */
    public final Duration getMaxPause() {
        return maxPause;
    }

    /**
     * Gets the new key used to protect insecure connections.
     *
     * @return The new key.
     * @see #getKey()
     */
    public final String getNewKey() {
        return newKey;
    }

    /**
     * If a client encounters an exceptional temporary situation during which it will be unable to send requests to the connection manager for a period of time greater than the maximum inactivity period (e.g., while a runtime environment changes from one web page to another), and if the connection manager included a 'maxpause' attribute in its Session Creation Response, then the client MAY request a temporary increase to the maximum inactivity period by including a 'pause' attribute in a request.
     *
     * @return The 'pause' attribute value.
     * @see #getMaxPause()
     * @see #getInactivity()
     */
    public final Duration getPause() {
        return pause;
    }

    /**
     * This attribute specifies the shortest allowable polling interval (in seconds). This enables the client to not send empty request elements more often than desired.
     *
     * @return The 'polling' attribute value or null.
     */
    public final Duration getPolling() {
        return polling;
    }

    /**
     * After receiving a request with an 'ack' value less than the 'rid' of the last request that it has already responded to, the connection manager MAY inform the client of the situation by sending its next response immediately instead of waiting until it has payloads to send to the client (e.g., if some time has passed since it responded). In this case it SHOULD include a 'report' attribute set to one greater than the 'ack' attribute it received from the client, and a 'time' attribute set to the number of milliseconds since it sent the response associated with the 'report' attribute.
     * <p>
     * Upon reception of a response with 'report' and 'time' attributes, if the client has still not received the response associated with the request identifier specified by the 'report' attribute, then it MAY choose to resend the request associated with the missing response.
     *
     * @return The 'report' attribute value.
     * @see #getTime()
     * @see #getAck()
     */
    public final Long getReport() {
        return report;
    }

    /**
     * This attribute enables the connection manager to limit the number of simultaneous requests the client makes (see Overactivity and Polling Sessions). The RECOMMENDED values are either "2" or one more than the value of the 'hold' attribute specified in the session request.
     *
     * @return The 'requests' attribute value.
     * @see #getHold()
     */
    public final Short getRequests() {
        return requests;
    }

    /**
     * The {@code <body/>} element of every client request MUST possess a sequential request ID encapsulated via the 'rid' attribute.
     *
     * @return The 'rid' attribute value.
     */
    public final Long getRid() {
        return rid;
    }

    /**
     * A connection manager MAY be configured to enable sessions with more than one server in different domains. When requesting a session with such a "proxy" connection manager, a client SHOULD include a 'route' attribute that specifies the protocol, hostname, and port of the server with which it wants to communicate, formatted as "proto:host:port" (e.g., "xmpp:example.com:9999").
     *
     * @return The 'route' attribute value.
     */
    public final String getRoute() {
        return route;
    }

    /**
     * All requests after the first one MUST include a valid 'sid' attribute (provided by the connection manager in the Session Creation Response). The initialization request is unique in that the {@code <body/>} element MUST NOT possess a 'sid' attribute.
     *
     * @return The 'sid' attribute value.
     */
    public final String getSid() {
        return sid;
    }

    /**
     * Gets the session id.
     *
     * @return The session id.
     * @see #getSid()
     */
    @Override
    public final String getId() {
        return sid;
    }

    /**
     * If a connection manager supports the multi-streams feature, it MUST include a 'stream' attribute in its Session Creation Response. If a client does not receive the 'stream' attribute then it MUST assume that the connection manager does not support the feature.
     *
     * @return The 'stream' attribute value.
     */
    public final String getStream() {
        return stream;
    }

    /**
     * After receiving a request with an 'ack' value less than the 'rid' of the last request that it has already responded to, the connection manager MAY inform the client of the situation by sending its next response immediately instead of waiting until it has payloads to send to the client (e.g., if some time has passed since it responded). In this case it SHOULD include a 'report' attribute set to one greater than the 'ack' attribute it received from the client, and a 'time' attribute set to the number of milliseconds since it sent the response associated with the 'report' attribute.
     *
     * @return The 'time' attribute value.
     * @see #getReport()
     * @see #getAck()
     */
    public final Duration getTime() {
        return time;
    }

    @Override
    public final Jid getTo() {
        return to;
    }

    /**
     * The type of the body.
     *
     * @return The type.
     * @see #getCondition()
     */
    public final Type getType() {
        return type;
    }

    /**
     * This attribute specifies the highest version of the BOSH protocol that the client supports. The numbering scheme is "&lt;major&gt;.&lt;minor&gt;" (where the minor number MAY be incremented higher than a single digit, so it MUST be treated as a separate integer). Note: The 'ver' attribute should not be confused with the version of any protocol being transported.
     *
     * @return The 'version' attribute value.
     */
    public final String getBoshVersion() {
        return ver;
    }

    /**
     * This attribute specifies the longest time (in seconds) that the connection manager is allowed to wait before responding to any request during the session. This enables the client to limit the delay before it discovers any network failure, and to prevent its HTTP/TCP connection from expiring due to inactivity.
     *
     * @return The 'wait' attribute value.
     */
    public final Duration getWait() {
        return wait;
    }

    @Override
    public final Locale getLanguage() {
        return lang;
    }

    /**
     * Gets the XMPP version.
     *
     * @return The XMPP version.
     */
    @Override
    public final String getVersion() {
        return version;
    }

    /**
     * If the client requests a stream restart.
     *
     * @return The 'restart' attribute value.
     */
    public final boolean isRestart() {
        return restart != null && restart;
    }

    /**
     * If the connection manager supports stream restarts, it MUST advertise that fact by including a 'restartlogic' attribute (qualified by the 'urn:xmpp:xbosh' namespace) whose value is set to "true".
     *
     * @return The 'restartlogic' attribute value.
     */
    public final boolean isRestartLogic() {
        return restartlogic != null && restartlogic;
    }

    /**
     * Compares this body with another body by using the {@linkplain #getRid() request id}.
     * Null bodies are sorted last, while null RIDs are sorted first (as it may indicate a session creation request).
     *
     * @param o The body.
     * @return The comparison result.
     */
    @Override
    public final int compareTo(final Body o) {
        if (o == null) {
            return -1;
        }
        if (rid != null) {
            if (o.rid != null) {
                return rid.compareTo(o.rid);
            } else {
                return 1;
            }
        } else {
            if (o.rid != null) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        if (rid != null) {
            sb.append("RID: ").append(rid).append("; ");
        }
        if (sid != null) {
            sb.append("SID: ").append(sid).append("; ");
        }
        if (wait != null) {
            sb.append("Wait: ").append(wait);
        }
        return sb.toString();
    }

    /**
     * The implementation of the 'condition' attribute of the {@code <body/>} wrapper element, which indicates an error reported by the connection manager.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0124.html#errorstatus-terminal">17.2 Terminal Binding Conditions</a>
     */
    @XmlEnum
    public enum Condition {
        /**
         * The format of an HTTP header or binding element received from the client is unacceptable (e.g., syntax error).
         */
        @XmlEnumValue(value = "bad-request")
        BAD_REQUEST,
        /**
         * The target domain specified in the 'to' attribute or the target host or port specified in the 'route' attribute is no longer serviced by the connection manager.
         */
        @XmlEnumValue(value = "host-gone")
        HOST_GONE,
        /**
         * The target domain specified in the 'to' attribute or the target host or port specified in the 'route' attribute is unknown to the connection manager.
         */
        @XmlEnumValue(value = "host-unknown")
        HOST_UNKNOWN,
        /**
         * The initialization element lacks a 'to' or 'route' attribute (or the attribute has no value) but the connection manager requires one.
         */
        @XmlEnumValue(value = "improper-addressing")
        IMPROPER_ADDRESSING,
        /**
         * The connection manager has experienced an internal error that prevents it from servicing the request.
         */
        @XmlEnumValue(value = "internal-server-error")
        INTERNAL_SERVER_ERROR,
        /**
         * (1) 'sid' is not valid, (2) 'stream' is not valid, (3) 'rid' is larger than the upper limit of the expected window, (4) connection manager is unable to resend response, (5) 'key' sequence is invalid.
         */
        @XmlEnumValue(value = "item-not-found")
        ITEM_NOT_FOUND,
        /**
         * Another request being processed at the same time as this request caused the session to terminate.
         */
        @XmlEnumValue(value = "other-request")
        OTHER_REQUEST,
        /**
         * The client has broken the session rules (polling too frequently, requesting too frequently, sending too many simultaneous requests).
         */
        @XmlEnumValue(value = "policy-violation")
        POLICY_VIOLATION,
        /**
         * The connection manager was unable to connect to, or unable to connect securely to, or has lost its connection to, the server.
         */
        @XmlEnumValue(value = "remote-connection-failed")
        REMOTE_CONNECTION_FAILED,
        /**
         * Encapsulates an error in the protocol being transported.
         */
        @XmlEnumValue(value = "remote-stream-error")
        REMOTE_STREAM_ERROR,
        /**
         * The connection manager does not operate at this URI (e.g., the connection manager accepts only SSL or TLS connections at some https: URI rather than the http: URI requested by the client). The client can try POSTing to the URI in the content of the {@code <uri/>} child element.
         */
        @XmlEnumValue(value = "see-other-uri")
        SEE_OTHER_URI,
        /**
         * The connection manager is being shut down. All active HTTP sessions are being terminated. No new sessions can be created.
         */
        @XmlEnumValue(value = "system-shutdown")
        SYSTEM_SHUTDOWN,
        /**
         * The error is not one of those defined herein; the connection manager SHOULD include application-specific information in the content of the {@code <body/>} wrapper.
         */
        @XmlEnumValue(value = "undefined-condition")
        UNDEFINED_CONDITION;

        @Override
        public String toString() {
            return this.name().toLowerCase().replace("_", "-");
        }
    }

    /**
     * The implementation of the 'type' attribute of the {@code <body/>} element.
     */
    @XmlEnum
    public enum Type {
        /**
         * The connection manager has reported a recoverable binding error.
         */
        @XmlEnumValue(value = "error")
        ERROR,
        /**
         * The connection manager or the client has terminated the connection.
         */
        @XmlEnumValue(value = "terminate")
        TERMINATE
    }

    /**
     * A builder for the body element.
     */
    public static final class Builder {

        private URI uri;

        private final List<Object> wrappedObjects = new ArrayList<>();

        private String accept;

        private Long ack;

        private String authId;

        private Charset[] charsets;

        private Condition condition;

        private String content;

        private Jid from;

        private Short hold;

        private Duration inactivity;

        private String key;

        private Duration maxPause;

        private String newKey;

        private Duration pause;

        private Duration polling;

        private Long report;

        private Short requests;

        private Long requestId;

        private String route;

        private String sessionId;

        private String stream;

        private Duration time;

        private Jid to;

        private Type type;

        private String version;

        private Duration wait;

        private Locale language;

        private String xmppVersion;

        private Boolean restart;

        private Boolean restartlogic;

        private Builder() {
        }

        /**
         * Sets the 'uri' attribute of the body.
         *
         * @param uri The 'uri' attribute.
         * @return The builder.
         */
        public final Builder uri(URI uri) {
            this.uri = uri;
            return this;
        }

        /**
         * Sets the 'accept' attribute of the body.
         *
         * @param accept The 'accept' attribute.
         * @return The builder.
         */
        public final Builder accept(String accept) {
            this.accept = accept;
            return this;
        }

        /**
         * Sets the 'ack' attribute of the body.
         *
         * @param ack The 'ack' attribute.
         * @return The builder.
         */
        public final Builder ack(long ack) {
            this.ack = ack;
            return this;
        }

        /**
         * Sets the 'authid' attribute of the body.
         *
         * @param authId The 'authid' attribute.
         * @return The builder.
         */
        public final Builder authId(String authId) {
            this.authId = authId;
            return this;
        }

        /**
         * Sets the 'charsets' attribute of the body.
         *
         * @param charsets The 'charsets' attribute.
         * @return The builder.
         */
        public final Builder charsets(Charset... charsets) {
            this.charsets = charsets;
            return this;
        }

        /**
         * Sets the 'condition' attribute of the body.
         *
         * @param condition The 'condition' attribute.
         * @return The builder.
         */
        public final Builder condition(Condition condition) {
            this.condition = condition;
            return this;
        }

        /**
         * Sets the 'content' attribute of the body.
         *
         * @param content The 'content' attribute.
         * @return The builder.
         */
        public final Builder content(String content) {
            this.content = content;
            return this;
        }

        /**
         * Sets the 'from' attribute of the body.
         *
         * @param from The 'from' attribute.
         * @return The builder.
         */
        public final Builder from(Jid from) {
            this.from = from;
            return this;
        }

        /**
         * Sets the 'hold' attribute of the body.
         *
         * @param hold The 'hold' attribute.
         * @return The builder.
         */
        public final Builder hold(short hold) {
            this.hold = hold;
            return this;
        }

        /**
         * Sets the 'inactivity' attribute of the body.
         *
         * @param inactivity The 'inactivity' attribute.
         * @return The builder.
         */
        public final Builder inactivity(Duration inactivity) {
            this.inactivity = inactivity;
            return this;
        }

        /**
         * Sets the 'key' attribute of the body.
         *
         * @param key The 'key' attribute.
         * @return The builder.
         */
        public final Builder key(String key) {
            this.key = key;
            return this;
        }

        /**
         * Sets the 'maxpause' attribute of the body.
         *
         * @param maxPause The 'maxpause' attribute.
         * @return The builder.
         */
        public final Builder maxPause(Duration maxPause) {
            this.maxPause = maxPause;
            return this;
        }

        /**
         * Sets the 'newkey' attribute of the body.
         *
         * @param newKey The 'newkey' attribute.
         * @return The builder.
         */
        public final Builder newKey(String newKey) {
            this.newKey = newKey;
            return this;
        }

        /**
         * Sets the 'pause' attribute of the body.
         *
         * @param pause The 'pause' attribute.
         * @return The builder.
         */
        public final Builder pause(Duration pause) {
            this.pause = pause;
            return this;
        }

        /**
         * Sets the 'polling' attribute of the body.
         *
         * @param polling The 'polling' attribute.
         * @return The builder.
         */
        public final Builder polling(Duration polling) {
            this.polling = polling;
            return this;
        }

        /**
         * Sets the 'report' attribute of the body.
         *
         * @param report The 'report' attribute.
         * @return The builder.
         */
        public final Builder report(long report) {
            this.report = report;
            return this;
        }

        /**
         * Sets the 'requests' attribute of the body.
         *
         * @param requests The 'requests' attribute.
         * @return The builder.
         */
        public final Builder requests(short requests) {
            this.requests = requests;
            return this;
        }

        /**
         * Sets the 'rid' attribute of the body.
         *
         * @param requestId The 'rid' attribute.
         * @return The builder.
         */
        public final Builder requestId(long requestId) {
            this.requestId = requestId;
            return this;
        }

        /**
         * Sets the 'route' attribute of the body.
         *
         * @param route The 'route' attribute.
         * @return The builder.
         */
        public final Builder route(String route) {
            this.route = route;
            return this;
        }

        /**
         * Sets the 'sid' attribute of the body.
         *
         * @param sessionId The 'sid' attribute.
         * @return The builder.
         */
        public final Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        /**
         * Sets the 'stream' attribute of the body.
         *
         * @param stream The 'stream' attribute.
         * @return The builder.
         */
        public final Builder stream(String stream) {
            this.stream = stream;
            return this;
        }

        /**
         * Sets the 'time' attribute of the body.
         *
         * @param time The 'time' attribute.
         * @return The builder.
         */
        public final Builder time(Duration time) {
            this.time = time;
            return this;
        }

        /**
         * Sets the 'to' attribute of the body.
         *
         * @param to The 'to' attribute.
         * @return The builder.
         */
        public final Builder to(Jid to) {
            this.to = to;
            return this;
        }

        /**
         * Sets the 'type' attribute of the body.
         *
         * @param type The 'type' attribute.
         * @return The builder.
         */
        public final Builder type(Type type) {
            this.type = type;
            return this;
        }

        /**
         * Sets the 'ver' attribute of the body.
         *
         * @param version The 'ver' attribute.
         * @return The builder.
         */
        public final Builder version(String version) {
            this.version = version;
            return this;
        }

        /**
         * Sets the 'wait' attribute of the body.
         *
         * @param wait The 'wait' attribute.
         * @return The builder.
         */
        public final Builder wait(Duration wait) {
            this.wait = wait;
            return this;
        }

        /**
         * Sets the 'lang' attribute of the body.
         *
         * @param language The 'lang' attribute.
         * @return The builder.
         */
        public final Builder language(Locale language) {
            this.language = language;
            return this;
        }

        /**
         * Sets the 'version' attribute of the body.
         *
         * @param xmppVersion The 'version' attribute.
         * @return The builder.
         */
        public final Builder xmppVersion(String xmppVersion) {
            this.xmppVersion = xmppVersion;
            return this;
        }

        /**
         * Sets the 'restart' attribute of the body.
         *
         * @param restart The 'restart' attribute.
         * @return The builder.
         */
        public final Builder restart(boolean restart) {
            this.restart = restart;
            return this;
        }

        /**
         * Sets the 'restartlogic' attribute of the body.
         *
         * @param restartlogic The 'restartlogic' attribute.
         * @return The builder.
         */
        public final Builder restartLogic(boolean restartlogic) {
            this.restartlogic = restartlogic;
            return this;
        }

        /**
         * Sets the wrapped objects of the body, i.e. the payload.
         *
         * @param objects The wrapped objects.
         * @return The builder.
         */
        public final Builder wrappedObjects(Collection<Object> objects) {
            this.wrappedObjects.clear();
            this.wrappedObjects.addAll(objects);
            return this;
        }

        /**
         * Builds the body.
         *
         * @return The body.
         */
        public final Body build() {
            return new Body(this);
        }
    }

    private static final class CharsetAdapter extends XmlAdapter<String, List<Charset>> {

        @Override
        public final List<Charset> unmarshal(final String charsets) {
            if (charsets != null) {
                return Collections.unmodifiableList(Arrays.stream(charsets.split(" ")).map(Charset::forName).collect(Collectors.toList()));
            }
            return null;
        }

        @Override
        public final String marshal(final List<Charset> charsets) {
            if (charsets != null && !charsets.isEmpty()) {
                return String.join(" ", charsets.stream().map(c -> (CharSequence) c.name())::iterator);
            }
            return null;
        }
    }

    private static final class SecondsAdapter extends XmlAdapter<Integer, Duration> {

        @Override
        public final Duration unmarshal(final Integer v) {
            if (v != null) {
                return Duration.ofSeconds(v);
            }
            return null;
        }

        @Override
        public final Integer marshal(final Duration v) {
            if (v != null) {
                return (int) Math.min(v.getSeconds(), Integer.MAX_VALUE);
            }
            return null;
        }
    }

    private static final class MillisecondsAdapter extends XmlAdapter<Integer, Duration> {

        @Override
        public final Duration unmarshal(final Integer v) {
            if (v != null) {
                return Duration.ofMillis(v);
            }
            return null;
        }

        @Override
        public final Integer marshal(final Duration v) {
            if (v != null) {
                return (int) Math.min(v.toMillis(), Integer.MAX_VALUE);
            }
            return null;
        }
    }
}
