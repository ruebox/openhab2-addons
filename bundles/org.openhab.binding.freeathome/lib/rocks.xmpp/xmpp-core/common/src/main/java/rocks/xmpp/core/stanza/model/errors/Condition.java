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

package rocks.xmpp.core.stanza.model.errors;

import rocks.xmpp.core.stanza.model.StanzaError;
import rocks.xmpp.util.Strings;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlValue;
import java.util.HashMap;
import java.util.Map;

/**
 * An abstract implementation of a defined stanza error condition.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions">8.3.3.  Defined Conditions</a>
 */
@XmlSeeAlso({BadRequest.class, Conflict.class, FeatureNotImplemented.class, Conflict.class, Forbidden.class, Gone.class, InternalServerError.class, ItemNotFound.class, JidMalformed.class, NotAcceptable.class, NotAllowed.class, NotAuthorized.class, PolicyViolation.class, RecipientUnavailable.class, Redirect.class, RegistrationRequired.class, RemoteServerNotFound.class, RemoteServerTimeout.class, ResourceConstraint.class, ServiceUnavailable.class, SubscriptionRequired.class, UndefinedCondition.class, UnexpectedRequest.class})
public abstract class Condition {

    /**
     * The implementation of the {@code <bad-request/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-bad-request">8.3.3.1.  bad-request</a></cite></p>
     * <p>The sender has sent a stanza containing XML that does not conform to the appropriate schema or that cannot be processed (e.g., an IQ stanza that includes an unrecognized value of the 'type' attribute, or an element that is qualified by a recognized namespace but that violates the defined syntax for the element); the associated error type SHOULD be "modify".</p>
     * </blockquote>
     */
    public static final Condition BAD_REQUEST = new BadRequest();

    /**
     * The implementation of the {@code <conflict/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-conflict">8.3.3.2.  conflict</a></cite></p>
     * <p>Access cannot be granted because an existing resource exists with the same name or address; the associated error type SHOULD be "cancel".</p>
     * </blockquote>
     */
    public static final Condition CONFLICT = new Conflict();

    /**
     * The implementation of the {@code <feature-not-implemented/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-feature-not-implemented">8.3.3.3.  feature-not-implemented</a></cite></p>
     * <p>The feature represented in the XML stanza is not implemented by the intended recipient or an intermediate server and therefore the stanza cannot be processed (e.g., the entity understands the namespace but does not recognize the element name); the associated error type SHOULD be "cancel" or "modify".</p>
     * </blockquote>
     */
    public static final Condition FEATURE_NOT_IMPLEMENTED = new FeatureNotImplemented();

    /**
     * The implementation of the {@code <forbidden/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-forbidden">8.3.3.4.  forbidden</a></cite></p>
     * <p>The requesting entity does not possess the necessary permissions to perform an action that only certain authorized roles or individuals are allowed to complete (i.e., it typically relates to authorization rather than authentication); the associated error type SHOULD be "auth".</p>
     * </blockquote>
     */
    public static final Condition FORBIDDEN = new Forbidden();

    /**
     * The implementation of the {@code <internal-server-error/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-internal-server-error">8.3.3.6.  internal-server-error</a></cite></p>
     * <p>The server has experienced a misconfiguration or other internal error that prevents it from processing the stanza; the associated error type SHOULD be "cancel".</p>
     * </blockquote>
     */
    public static final Condition INTERNAL_SERVER_ERROR = new InternalServerError();

    /**
     * The implementation of the {@code <item-not-found/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-item-not-found">8.3.3.7.  item-not-found</a></cite></p>
     * <p>The addressed JID or item requested cannot be found; the associated error type SHOULD be "cancel".</p>
     * </blockquote>
     */
    public static final Condition ITEM_NOT_FOUND = new ItemNotFound();

    /**
     * The implementation of the {@code <jid-malformed/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-jid-malformed">8.3.3.8.  jid-malformed</a></cite></p>
     * <p>The sending entity has provided (e.g., during resource binding) or communicated (e.g., in the 'to' address of a stanza) an XMPP address or aspect thereof that violates the rules defined in [XMPP-ADDR]; the associated error type SHOULD be "modify".</p>
     * </blockquote>
     */
    public static final Condition JID_MALFORMED = new JidMalformed();

    /**
     * The implementation of the {@code <not-acceptable/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-not-acceptable">8.3.3.9.  not-acceptable</a></cite></p>
     * <p>The recipient or server understands the request but cannot process it because the request does not meet criteria defined by the recipient or server (e.g., a request to subscribe to information that does not simultaneously include configuration parameters needed by the recipient); the associated error type SHOULD be "modify".</p>
     * </blockquote>
     */
    public static final Condition NOT_ACCEPTABLE = new NotAcceptable();

    /**
     * The implementation of the {@code <not-allowed/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-not-allowed">8.3.3.10.  not-allowed</a></cite></p>
     * <p>The recipient or server does not allow any entity to perform the action (e.g., sending to entities at a blacklisted domain); the associated error type SHOULD be "cancel".</p>
     * </blockquote>
     */
    public static final Condition NOT_ALLOWED = new NotAllowed();

    /**
     * The implementation of the {@code <not-authorized/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-not-authorized">8.3.3.11.  not-authorized</a></cite></p>
     * <p>The sender needs to provide credentials before being allowed to perform the action, or has provided improper credentials (the name "not-authorized", which was borrowed from the "401 Unauthorized" error of [HTTP], might lead the reader to think that this condition relates to authorization, but instead it is typically used in relation to authentication); the associated error type SHOULD be "auth".</p>
     * </blockquote>
     */
    public static final Condition NOT_AUTHORIZED = new NotAuthorized();

    /**
     * The implementation of the {@code <policy-violation/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-policy-violation">8.3.3.12.  policy-violation</a></cite></p>
     * <p>The entity has violated some local service policy (e.g., a message contains words that are prohibited by the service) and the server MAY choose to specify the policy in the {@code <text/>} element or in an application-specific condition element; the associated error type SHOULD be "modify" or "wait" depending on the policy being violated.</p>
     * </blockquote>
     */
    public static final Condition POLICY_VIOLATION = new PolicyViolation();

    /**
     * The implementation of the {@code <recipient-unavailable/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-recipient-unavailable">8.3.3.13.  recipient-unavailable</a></cite></p>
     * <p>The intended recipient is temporarily unavailable, undergoing maintenance, etc.; the associated error type SHOULD be "wait".</p>
     * </blockquote>
     */
    public static final Condition RECIPIENT_UNAVAILABLE = new RecipientUnavailable();

    /**
     * The implementation of the {@code <registration-required/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-registration-required">8.3.3.15.  registration-required</a></cite></p>
     * <p>The requesting entity is not authorized to access the requested service because prior registration is necessary (examples of prior registration include members-only rooms in XMPP multi-user chat [XEP-0045] and gateways to non-XMPP instant messaging services, which traditionally required registration in order to use the gateway [XEP-0100]); the associated error type SHOULD be "auth".</p>
     * </blockquote>
     */
    public static final Condition REGISTRATION_REQUIRED = new RegistrationRequired();

    /**
     * The implementation of the {@code <remote-server-not-found/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-remote-server-not-found">8.3.3.16.  remote-server-not-found</a></cite></p>
     * <p>A remote server or service specified as part or all of the JID of the intended recipient does not exist or cannot be resolved (e.g., there is no _xmpp-server._tcp DNS SRV record, the A or AAAA fallback resolution fails, or A/AAAA lookups succeed but there is no response on the IANA-registered port 5269); the associated error type SHOULD be "cancel".</p>
     * </blockquote>
     */
    public static final Condition REMOTE_SERVER_NOT_FOUND = new RemoteServerNotFound();

    /**
     * The implementation of the {@code <remote-server-timeout/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-remote-server-timeout">8.3.3.17.  remote-server-timeout</a></cite></p>
     * <p>A remote server or service specified as part or all of the JID of the intended recipient (or needed to fulfill a request) was resolved but communications could not be established within a reasonable amount of time (e.g., an XML stream cannot be established at the resolved IP address and port, or an XML stream can be established but stream negotiation fails because of problems with TLS, SASL, Server Dialback, etc.); the associated error type SHOULD be "wait" (unless the error is of a more permanent nature, e.g., the remote server is found but it cannot be authenticated or it violates security policies).</p>
     * </blockquote>
     */
    public static final Condition REMOTE_SERVER_TIMEOUT = new RemoteServerTimeout();

    /**
     * The implementation of the {@code <resource-constraint/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-resource-constraint">8.3.3.18.  resource-constraint</a></cite></p>
     * <p>The server or recipient is busy or lacks the system resources necessary to service the request; the associated error type SHOULD be "wait".</p>
     * </blockquote>
     */
    public static final Condition RESOURCE_CONSTRAINT = new ResourceConstraint();

    /**
     * The implementation of the {@code <service-unavailable/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-service-unavailable">8.3.3.19.  service-unavailable</a></cite></p>
     * <p>The server or recipient does not currently provide the requested service; the associated error type SHOULD be "cancel".</p>
     * </blockquote>
     */
    public static final Condition SERVICE_UNAVAILABLE = new ServiceUnavailable();

    /**
     * The implementation of the {@code <subscription-required/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-subscription-required">8.3.3.20.  subscription-required</a></cite></p>
     * <p>The requesting entity is not authorized to access the requested service because a prior subscription is necessary (examples of prior subscription include authorization to receive presence information as defined in [XMPP-IM] and opt-in data feeds for XMPP publish-subscribe as defined in [XEP-0060]); the associated error type SHOULD be "auth".</p>
     * </blockquote>
     */
    public static final Condition SUBSCRIPTION_REQUIRED = new SubscriptionRequired();

    /**
     * The implementation of the {@code <undefined-condition/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-undefined-condition">8.3.3.21.  undefined-condition</a></cite></p>
     * <p>The error condition is not one of those defined by the other conditions in this list; any error type can be associated with this condition, and it SHOULD NOT be used except in conjunction with an application-specific condition.</p>
     * </blockquote>
     */
    public static final Condition UNDEFINED_CONDITION = new UndefinedCondition();

    /**
     * The implementation of the {@code <unexpected-request/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-unexpected-request">8.3.3.22.  unexpected-request</a></cite></p>
     * <p>The recipient or server understood the request but was not expecting it at this time (e.g., the request was out of order); the associated error type SHOULD be "wait" or "modify".</p>
     * </blockquote>
     */
    public static final Condition UNEXPECTED_REQUEST = new UnexpectedRequest();

    private static final Map<Class<? extends Condition>, StanzaError.Type> ASSOCIATED_ERROR_TYPE = new HashMap<>();

    static {
        ASSOCIATED_ERROR_TYPE.put(BadRequest.class, StanzaError.Type.MODIFY);
        ASSOCIATED_ERROR_TYPE.put(Conflict.class, StanzaError.Type.CANCEL);
        ASSOCIATED_ERROR_TYPE.put(FeatureNotImplemented.class, StanzaError.Type.CANCEL);
        ASSOCIATED_ERROR_TYPE.put(Forbidden.class, StanzaError.Type.AUTH);
        ASSOCIATED_ERROR_TYPE.put(Gone.class, StanzaError.Type.CANCEL);
        ASSOCIATED_ERROR_TYPE.put(InternalServerError.class, StanzaError.Type.CANCEL);
        ASSOCIATED_ERROR_TYPE.put(ItemNotFound.class, StanzaError.Type.CANCEL);
        ASSOCIATED_ERROR_TYPE.put(JidMalformed.class, StanzaError.Type.MODIFY);
        ASSOCIATED_ERROR_TYPE.put(NotAcceptable.class, StanzaError.Type.MODIFY);
        ASSOCIATED_ERROR_TYPE.put(NotAllowed.class, StanzaError.Type.CANCEL);
        ASSOCIATED_ERROR_TYPE.put(NotAuthorized.class, StanzaError.Type.AUTH);
        ASSOCIATED_ERROR_TYPE.put(PolicyViolation.class, StanzaError.Type.MODIFY);
        ASSOCIATED_ERROR_TYPE.put(RecipientUnavailable.class, StanzaError.Type.WAIT);
        ASSOCIATED_ERROR_TYPE.put(Redirect.class, StanzaError.Type.MODIFY);
        ASSOCIATED_ERROR_TYPE.put(RegistrationRequired.class, StanzaError.Type.AUTH);
        ASSOCIATED_ERROR_TYPE.put(RemoteServerNotFound.class, StanzaError.Type.CANCEL);
        ASSOCIATED_ERROR_TYPE.put(RemoteServerTimeout.class, StanzaError.Type.WAIT);
        ASSOCIATED_ERROR_TYPE.put(ResourceConstraint.class, StanzaError.Type.WAIT);
        ASSOCIATED_ERROR_TYPE.put(ServiceUnavailable.class, StanzaError.Type.CANCEL);
        ASSOCIATED_ERROR_TYPE.put(SubscriptionRequired.class, StanzaError.Type.AUTH);
        ASSOCIATED_ERROR_TYPE.put(UndefinedCondition.class, StanzaError.Type.CANCEL);
        ASSOCIATED_ERROR_TYPE.put(UnexpectedRequest.class, StanzaError.Type.WAIT);
    }

    @XmlValue
    final String value;

    Condition() {
        this(null);
    }

    Condition(String value) {
        this.value = value;
    }

    /**
     * Gets the associated stanza error type for a condition.
     *
     * @param condition The condition.
     * @return The associated stanza error type.
     */
    public static StanzaError.Type getErrorTypeByCondition(Condition condition) {
        return ASSOCIATED_ERROR_TYPE.get(condition.getClass());
    }

    /**
     * Creates a {@code <redirect/>} stanza error with an alternate address.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-redirect">8.3.3.14.  redirect</a></cite></p>
     * <p>The recipient or server is redirecting requests for this information to another entity, typically in a temporary fashion (as opposed to the {@code <gone/>} error condition, which is used for permanent addressing failures); the associated error type SHOULD be "modify" and the error stanza SHOULD contain the alternate address in the XML character data of the {@code <redirect/>} element (which MUST be a URI or IRI with which the sender can communicate, typically an XMPP IRI as specified in [XMPP-URI]).</p>
     * </blockquote>
     *
     * @param alternateAddress The new address.
     * @return The error.
     */
    public static Redirect redirect(String alternateAddress) {
        return new Redirect(alternateAddress);
    }

    /**
     * Creates a {@code <gone/>} stanza error.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-gone">8.3.3.5.  gone</a></cite></p>
     * <p>The recipient or server can no longer be contacted at this address, typically on a permanent basis (as opposed to the {@code <redirect/>} error condition, which is used for temporary addressing failures); the associated error type SHOULD be "cancel" and the error stanza SHOULD include a new address (if available) as the XML character data of the {@code <gone/>} element (which MUST be a Uniform Resource Identifier [URI] or Internationalized Resource Identifier [IRI] at which the entity can be contacted, typically an XMPP IRI as specified in [XMPP-URI]).</p>
     * </blockquote>
     *
     * @return The error.
     */
    public static Gone gone() {
        return new Gone();
    }

    /**
     * Creates a {@code <gone/>} stanza error with a new address.
     *
     * @param newAddress The new address.
     * @return The error.
     */
    public static Gone gone(String newAddress) {
        return new Gone(newAddress);
    }

    @Override
    public final String toString() {
        return Strings.toDash(getClass().getSimpleName());
    }
}