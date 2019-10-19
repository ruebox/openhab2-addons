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

package rocks.xmpp.extensions.jingle.model;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.extensions.jingle.apps.model.ApplicationFormat;
import rocks.xmpp.extensions.jingle.transports.model.TransportMethod;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Schudt
 */
@XmlRootElement
public final class Jingle {

    /**
     * urn:xmpp:jingle:1
     */
    public static final String NAMESPACE = "urn:xmpp:jingle:1";

    private final List<Content> content = new ArrayList<>();

    @XmlAttribute
    private Action action;

    @XmlAttribute
    private Jid initiator;

    @XmlAttribute
    private Jid responder;

    @XmlAttribute
    private String sid;

    private Reason reason;

    @XmlAnyElement(lax = true)
    private Object payload;

    private Jingle() {
    }

    public Jingle(String sessionId, Action action, Reason reason) {
        this.sid = sessionId;
        this.action = action;
        this.reason = reason;
    }

    public Jingle(String sessionId, Action action, Object payload) {
        this.sid = sessionId;
        this.action = action;
        this.payload = payload;
    }

    public static Jingle initiator(Jid initiator, String sessionId, Action action, List<Content> contents) {
        Jingle jingle = new Jingle();
        jingle.sid = sessionId;
        jingle.action = action;
        jingle.initiator = initiator;
        jingle.content.addAll(contents);
        return jingle;
    }

    public static Jingle responder(Jid responder, String sessionId, Action action, List<Content> contents) {
        Jingle jingle = new Jingle();
        jingle.sid = sessionId;
        jingle.action = action;
        jingle.responder = responder;
        jingle.content.addAll(contents);
        return jingle;
    }

    /**
     * Gets the Jingle action.
     *
     * @return The Jingle action.
     * @see Jingle.Action
     */
    public Action getAction() {
        return action;
    }

    /**
     * Gets the initiator.
     * <blockquote>
     * <p>The full JID of the entity that has initiated the session flow. When the Jingle action is "session-initiate", the {@code <jingle/>} element SHOULD possess an 'initiator' attribute that explicitly specifies the full JID of the initiating entity; for all other actions, the {@code <jingle/>} element SHOULD NOT possess an 'initiator' attribute and the recipient of the message SHOULD ignore the value if provided. The value of the 'initiator' attribute MAY be different from the 'from' address on the IQ-set of the session-initiate message (e.g., to handle certain interactions involving call managers, soft switches, and media relays). This usage shall be defined in other specifications, for example, in <a href="https://xmpp.org/extensions/xep-0251.html">Jingle Session Transfer (XEP-0251)</a>. However, in all cases if the 'initiator' and 'from' values differ then the responder MUST NOT interact with the 'initiator' JID unless it trusts the 'initiator' JID or trusts that the 'from' JID is allowed to authorize the 'initiator' JID to act on the 'from' JID's behalf. In the absence of explicit rules for handling this case, the responder SHOULD simply ignore the 'initiator' attribute and treat the 'from' JID as the initiating entity. After sending acknowledgement of the session-initiate message, the responder MUST send all future commmunications about the Jingle session to the initiator (whether the initiator is considered the 'from' JID or the 'initiator' JID).</p>
     * </blockquote>
     *
     * @return The initiator.
     */
    public Jid getInitiator() {
        return initiator;
    }

    /**
     * Gets the responder.
     * <blockquote>
     * <p>The full JID of the entity that has replied to the initiation, which can be different from the 'to' address on the IQ-set. When the Jingle action is "session-accept", the {@code <jingle/>} element SHOULD possess a 'responder' attribute that explicitly specifies the full JID of the responding entity; for all other actions, the {@code <jingle/>} element SHOULD NOT possess a 'responder' attribute and the recipient of the message SHOULD ignore the value if provided. The value of the 'responder' attribute MAY be different from the 'from' address on the IQ-set of the session-accept message, where the logic for handling any difference between the 'responder' JID and the 'from' JID follows the same logic as for session-initiate messages (see above). After sending acknowledgement of the session-accept message, the initiator MUST send all future commmunications about this Jingle session to the responder (whether the responder is considered the 'from' JID or the 'responder' JID).</p>
     * </blockquote>
     *
     * @return The responder.
     */
    public Jid getResponder() {
        return responder;
    }

    /**
     * Gets the session id.
     * <blockquote>
     * <p>A random session identifier generated by the initiator, which effectively maps to the local-part of a SIP "Call-ID" parameter; this SHOULD match the XML Nmtoken production so that XML character escaping is not needed for characters such as '&amp;'. In some situations the Jingle session identifier might have security implications. See <a href="http://tools.ietf.org/html/rfc4086">RFC 4086</a> regarding requirements for randomness.</p>
     * </blockquote>
     *
     * @return The session id.
     */
    public String getSessionId() {
        return sid;
    }

    /**
     * Gets the contents.
     *
     * @return The contents.
     */
    public List<Content> getContents() {
        return content;
    }

    /**
     * Gets the reason.
     *
     * @return The reason.
     */
    public Reason getReason() {
        return reason;
    }

    /**
     * Sets the reason.
     *
     * @param reason The reason.
     */
    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public Object getPayload() {
        return payload;
    }

    /**
     * The action.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0166.html#def-action">7.2 Action Attribute</a>
     */
    public enum Action {
        /**
         * Accept a content-add action received from another party.
         */
        @XmlEnumValue("content-accept")
        CONTENT_ACCEPT,
        /**
         * Add one or more new content definitions to the session.
         */
        @XmlEnumValue("content-add")
        CONTENT_ADD,
        /**
         * Change the directionality of media sending.
         */
        @XmlEnumValue("content-modify")
        CONTENT_MODIFY,
        /**
         * Reject a content-add action received from another party.
         */
        @XmlEnumValue("content-reject")
        CONTENT_REJECT,
        /**
         * Remove one or more content definitions from the session.
         */
        @XmlEnumValue("content-remove")
        CONTENT_REMOVE,
        /**
         * Exchange information about parameters for an application type.
         */
        @XmlEnumValue("description-info")
        DESCRIPTION_INFO,
        /**
         * Send information related to establishment or maintenance of security preconditions.
         */
        @XmlEnumValue("security-info")
        SECURITY_INFO,
        /**
         * Definitively accept a session negotiation.
         */
        @XmlEnumValue("session-accept")
        SESSION_ACCEPT,
        /**
         * Send session-level information, such as a ping or a ringing message.
         */
        @XmlEnumValue("session-info")
        SESSION_INFO,
        /**
         * Request negotiation of a new Jingle session.
         */
        @XmlEnumValue("session-initiate")
        SESSION_INITIATE,
        /**
         * End an existing session.
         */
        @XmlEnumValue("session-terminate")
        SESSION_TERMINATE,
        /**
         * Accept a transport-replace action received from another party.
         */
        @XmlEnumValue("session-terminate")
        TRANSPORT_ACCEPT,
        /**
         * Exchange transport candidates.
         */
        @XmlEnumValue("transport-info")
        TRANSPORT_INFO,
        /**
         * Reject a transport-replace action received from another party.
         */
        @XmlEnumValue("transport-reject")
        TRANSPORT_REJECT,
        /**
         * Redefine a transport method or replace it with a different method.
         */
        @XmlEnumValue("transport-replace")
        TRANSPORT_REPLACE
    }

    /**
     * The implementation of the {@code <content/>} element.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0166.html#def-content">7.3 Content Element</a>
     */
    public static final class Content {

        @XmlElementRef
        private ApplicationFormat applicationFormat;

        @XmlElementRef
        private TransportMethod transportMethod;

        @XmlAttribute
        private Creator creator;

        @XmlAttribute
        private String disposition;

        @XmlAttribute
        private String name;

        @XmlAttribute
        private Senders senders;

        private Content() {
        }

        /**
         * Creates a content element.
         *
         * @param name              The name.
         * @param creator           The creator.
         * @param applicationFormat The application type.
         * @param transportMethod   The transport method.
         */
        public Content(String name, Creator creator, ApplicationFormat applicationFormat, TransportMethod transportMethod) {
            this(name, creator, applicationFormat, transportMethod, null, null);
        }

        /**
         * Creates a content element.
         *
         * @param name              The name.
         * @param creator           The creator.
         * @param applicationFormat The application type.
         * @param transportMethod   The transport method.
         * @param disposition       The content disposition.
         * @param senders           The senders.
         */
        public Content(String name, Creator creator, ApplicationFormat applicationFormat, TransportMethod transportMethod, String disposition, Senders senders) {
            this.name = name;
            this.creator = creator;
            this.applicationFormat = applicationFormat;
            this.transportMethod = transportMethod;
            this.disposition = disposition;
            this.senders = senders;
        }

        /**
         * Gets the creator of the content.
         * <blockquote>
         * <p>Which party originally generated the content type (used to prevent race conditions regarding modifications); the defined values are "initiator" and "responder" (where the default is "initiator"). The value of the 'creator' attribute for a given content type MUST always match the party that originally generated the content type, even for Jingle actions that are sent by the other party in relation to that content type (e.g., subsequent content-modify or transport-info messages). The combination of the 'creator' attribute and the 'name' attribute is unique among both parties to a Jingle session.</p>
         * </blockquote>
         *
         * @return The creator.
         */
        public Creator getCreator() {
            return creator;
        }

        /**
         * Gets the content disposition.
         * <blockquote>
         * <p>How the content definition is to be interpreted by the recipient. The meaning of this attribute matches the "Content-Disposition" header as defined in <a href="http://tools.ietf.org/html/rfc2183">RFC 2183</a> and applied to SIP by <a href="http://tools.ietf.org/html/rfc3261">RFC 3261</a>. The value of this attribute SHOULD be one of the values registered in the <a href="http://www.iana.org/assignments/mail-cont-disp">IANA Mail Content Disposition Values and Parameters Registry</a>. The default value of this attribute is "session".</p>
         * </blockquote>
         *
         * @return The disposition.
         */
        public String getDisposition() {
            return disposition;
        }

        /**
         * Gets the name of the content.
         * <blockquote>
         * <p>A unique name or identifier for the content type according to the creator, which MAY have meaning to a human user in order to differentiate this content type from other content types (e.g., two content types containing video media could differentiate between "room-pan" and "slides"). If there are two content types with the same value for the 'name' attribute, they shall understood as alternative definitions for the same purpose (e.g., a legacy method and a standards-based method for establishing a voice call), typically to smooth the transition from an older technology to Jingle.</p>
         * </blockquote>
         *
         * @return The name.
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the senders.
         * <blockquote>
         * <p>Which parties in the session will be generating content (i.e., the direction in which a Jingle session is active); the allowable values are "both", "initiator", "none", and "responder" (where the default is "both"). Note that the defined values of the 'senders' attribute in Jingle correspond to the SDP attributes of "sendrecv", "sendonly", "inactive", and "recvonly" defined in RFC <a href="http://tools.ietf.org/html/rfc4566">4566</a> and used in the offer-answer model <a href="http://tools.ietf.org/html/rfc3264">RFC 3264</a>.</p>
         * </blockquote>
         *
         * @return The senders.
         */
        public Senders getSenders() {
            return senders;
        }

        /**
         * Gets the application type.
         *
         * @return The application type.
         */
        public ApplicationFormat getApplicationFormat() {
            return applicationFormat;
        }

        /**
         * Gets the transport method.
         *
         * @return The transport method.
         */
        public TransportMethod getTransportMethod() {
            return transportMethod;
        }

        /**
         * The creator.
         *
         * @see #getCreator()
         */
        public enum Creator {
            @XmlEnumValue("initiator")
            INITIATOR,
            @XmlEnumValue("responder")
            RESPONDER
        }

        /**
         * The senders.
         *
         * @see #getSenders()
         */
        public enum Senders {
            @XmlEnumValue("both")
            BOTH,
            @XmlEnumValue("initiator")
            INITIATOR,
            @XmlEnumValue("none")
            NONE,
            @XmlEnumValue("responder")
            RESPONDER
        }
    }

    /**
     * The implementation of the {@code <reason/>} element.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0166.html#def-reason">7.4 Reason Element</a>
     */
    public static final class Reason {

        @XmlElements({
                @XmlElement(name = "alternative-session", type = AlternativeSession.class),
                @XmlElement(name = "busy", type = Busy.class),
                @XmlElement(name = "cancel", type = Cancel.class),
                @XmlElement(name = "connectivity-error", type = ConnectivityError.class),
                @XmlElement(name = "decline", type = Decline.class),
                @XmlElement(name = "expired", type = Expired.class),
                @XmlElement(name = "failed-application", type = FailedApplication.class),
                @XmlElement(name = "failed-transport", type = FailedTransport.class),
                @XmlElement(name = "general-error", type = GeneralError.class),
                @XmlElement(name = "gone", type = Gone.class),
                @XmlElement(name = "incompatible-parameters", type = IncompatibleParameters.class),
                @XmlElement(name = "media-error", type = MediaError.class),
                @XmlElement(name = "security-error", type = SecurityError.class),
                @XmlElement(name = "success", type = Success.class),
                @XmlElement(name = "timeout", type = Timeout.class),
                @XmlElement(name = "unsupported-applications", type = UnsupportedApplications.class),
                @XmlElement(name = "unsupported-transports", type = UnsupportedTransports.class)
        })
        private Condition condition;

        private String text;

        @XmlAnyElement(lax = true)
        private Object extension;

        private Reason() {
        }

        /**
         * Creates a reason element with a defined condition.
         *
         * @param condition The condition.
         */
        public Reason(Condition condition) {
            this.condition = condition;
        }

        /**
         * Creates a reason element with a defined condition and a text.
         *
         * @param condition The condition.
         * @param text      The optional text for the reason.
         */
        public Reason(Condition condition, String text) {
            this.condition = condition;
            this.text = text;
        }

        /**
         * Creates a reason element with a defined condition and a text.
         *
         * @param condition The condition.
         * @param text      The optional text for the reason.
         * @param extension An object to provide more detailed information about the reason.
         */
        public Reason(Condition condition, String text, Object extension) {
            this.condition = condition;
            this.text = text;
            this.extension = extension;
        }

        /**
         * Gets the type.
         *
         * @return The type.
         */
        public Condition getType() {
            return condition;
        }

        /**
         * Gets human-readable information about the reason for the action.
         *
         * @return The information.
         */
        public String getText() {
            return text;
        }

        /**
         * Gets the extension, which defines more detailed information about the reason.
         *
         * @return The reason.
         */
        public Object getExtension() {
            return extension;
        }

        /**
         * The base class for the defined conditions for a reason.
         */
        @XmlTransient
        public abstract static class Condition {
            private Condition() {
            }
        }

        /**
         * The party prefers to use an existing session with the peer rather than initiate a new session; the Jingle session ID of the alternative session SHOULD be provided as the XML character data of the {@code <sid/>} child.
         */
        public static final class AlternativeSession extends Condition {

            private String sid;

            private AlternativeSession() {
            }

            public AlternativeSession(String sessionId) {
                this.sid = sessionId;
            }

            /**
             * Gets the alternative session id.
             *
             * @return The alternative session id.
             */
            public String getSessionId() {
                return sid;
            }
        }

        /**
         * The party is busy and cannot accept a session.
         */
        public static final class Busy extends Condition {
        }

        /**
         * The initiator wishes to formally cancel the session initiation request.
         */
        public static final class Cancel extends Condition {
        }

        /**
         * The action is related to connectivity problems.
         */
        public static final class ConnectivityError extends Condition {
        }

        /**
         * The party wishes to formally decline the session.
         */
        public static final class Decline extends Condition {
        }

        /**
         * The session length has exceeded a pre-defined time limit (e.g., a meeting hosted at a conference service).
         */
        public static final class Expired extends Condition {
        }

        /**
         * The party has been unable to initialize processing related to the application type.
         */
        public static final class FailedApplication extends Condition {
        }

        /**
         * The party has been unable to establish connectivity for the transport method.
         */
        public static final class FailedTransport extends Condition {
        }

        /**
         * The action is related to a non-specific application error.
         */
        public static final class GeneralError extends Condition {
        }

        /**
         * The entity is going offline or is no longer available.
         */
        public static final class Gone extends Condition {
        }

        /**
         * The party supports the offered application type but does not support the offered or negotiated parameters.
         */
        public static final class IncompatibleParameters extends Condition {
        }

        /**
         * The action is related to media processing problems.
         */
        public static final class MediaError extends Condition {
        }

        /**
         * The action is related to a violation of local security policies.
         */
        public static final class SecurityError extends Condition {
        }

        /**
         * The action is generated during the normal course of state management and does not reflect any error.
         */
        public static final class Success extends Condition {
        }

        /**
         * A request has not been answered so the sender is timing out the request.
         */
        public static final class Timeout extends Condition {
        }

        /**
         * The party supports none of the offered application types.
         */
        public static final class UnsupportedApplications extends Condition {
        }

        /**
         * The party supports none of the offered transport methods.
         */
        public static final class UnsupportedTransports extends Condition {
        }
    }
}
