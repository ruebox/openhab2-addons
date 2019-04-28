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

package rocks.xmpp.core.sasl.model;

import rocks.xmpp.core.Text;
import rocks.xmpp.core.stream.model.StreamElement;
import rocks.xmpp.util.Strings;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.Locale;
import java.util.Objects;

/**
 * The implementation of the {@code <failure/>} element, which indicates a SASL failure.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-process-neg-failure">6.4.5.  SASL Failure</a></cite></p>
 * <p>The receiving entity reports failure of the handshake for this authentication mechanism by sending a {@code <failure/>} element qualified by the 'urn:ietf:params:xml:ns:xmpp-sasl' namespace.</p>
 * </blockquote>
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 */
@XmlRootElement
public final class Failure implements StreamElement {

    @XmlElements({@XmlElement(name = "aborted", type = Aborted.class),
            @XmlElement(name = "account-disabled", type = AccountDisabled.class),
            @XmlElement(name = "credentials-expired", type = CredentialsExpired.class),
            @XmlElement(name = "encryption-required", type = EncryptionRequired.class),
            @XmlElement(name = "incorrect-encoding", type = IncorrectEncoding.class),
            @XmlElement(name = "invalid-authzid", type = InvalidAuthzid.class),
            @XmlElement(name = "invalid-mechanism", type = InvalidMechanism.class),
            @XmlElement(name = "malformed-request", type = MalformedRequest.class),
            @XmlElement(name = "mechanism-too-weak", type = MechanismTooWeak.class),
            @XmlElement(name = "not-authorized", type = NotAuthorized.class),
            @XmlElement(name = "temporary-auth-failure", type = TemporaryAuthFailure.class)
    })
    private final Condition condition;

    @XmlElement(namespace = "urn:ietf:params:xml:ns:xmpp-sasl")
    private final Text text;

    /**
     * Private default constructor, needed for unmarshalling.
     */
    private Failure() {
        this.condition = null;
        this.text = null;
    }

    public Failure(Condition condition) {
        this.condition = Objects.requireNonNull(condition);
        this.text = null;
    }

    public Failure(Condition condition, String text, Locale locale) {
        this.condition = Objects.requireNonNull(condition);
        if (text != null) {
            this.text = new Text(text, locale);
        } else {
            this.text = null;
        }
    }

    /**
     * Gets the defined error condition.
     *
     * @return The error condition.
     */
    public final Condition getCondition() {
        return condition;
    }

    /**
     * Gets the text of the failure.
     *
     * @return The text.
     */
    public final String getText() {
        if (text != null) {
            return text.getText();
        }
        return null;
    }

    /**
     * Gets the language of the text.
     *
     * @return The language.
     */
    public final Locale getLanguage() {
        if (text != null) {
            return text.getLanguage();
        }
        return null;
    }

    @Override
    public final String toString() {
        String text = getText();
        return "SASL failure:" + (condition != null ? " " + condition.toString() : "") + (text != null ? " (" + text + ')' : "");
    }

    /**
     * The implementation of the {@code <aborted/>} SASL failure.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-aborted">6.5.1.  aborted</a></cite></p>
     * <p>The receiving entity acknowledges that the authentication handshake has been aborted by the initiating entity; sent in reply to the {@code <abort/>} element.</p>
     * </blockquote>
     */
    @XmlType(factoryMethod = "create")
    static final class Aborted extends Condition {

        private Aborted() {
        }

        private static Aborted create() {
            return (Aborted) ABORTED;
        }
    }

    /**
     * The implementation of the {@code <account-disabled/>} SASL failure.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-account-disabled">6.5.2.  account-disabled</a></cite></p>
     * <p>The account of the initiating entity has been temporarily disabled; sent in reply to an {@code <auth/>} element (with or without initial response data) or a {@code <response/>} element.</p>
     * </blockquote>
     */
    @XmlType(factoryMethod = "create")
    static final class AccountDisabled extends Condition {

        private AccountDisabled() {
        }

        private static AccountDisabled create() {
            return (AccountDisabled) ACCOUNT_DISABLED;
        }
    }

    /**
     * The implementation of the {@code <credentials-expired/>} SASL failure.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-credentials-expired">6.5.3.  credentials-expired</a></cite></p>
     * <p>The authentication failed because the initiating entity provided credentials that have expired; sent in reply to a {@code <response/>} element or an {@code <auth/>} element with initial response data.</p>
     * </blockquote>
     */
    @XmlType(factoryMethod = "create")
    static final class CredentialsExpired extends Condition {

        private CredentialsExpired() {
        }

        private static CredentialsExpired create() {
            return (CredentialsExpired) CREDENTIALS_EXPIRED;
        }
    }

    /**
     * The implementation of the {@code <encryption-required/>} SASL failure.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-encryption-required">6.5.4.  encryption-required</a></cite></p>
     * <p>The mechanism requested by the initiating entity cannot be used unless the confidentiality and integrity of the underlying stream are protected (typically via TLS); sent in reply to an {@code <auth/>} element (with or without initial response data).</p>
     * </blockquote>
     */
    @XmlType(factoryMethod = "create")
    static final class EncryptionRequired extends Condition {

        private EncryptionRequired() {
        }

        private static EncryptionRequired create() {
            return (EncryptionRequired) ENCRYPTION_REQUIRED;
        }
    }

    /**
     * The implementation of the {@code <incorrect-encoding/>} SASL failure.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-incorrect-encoding">6.5.5.  incorrect-encoding</a></cite></p>
     * <p>The data provided by the initiating entity could not be processed because the base 64 encoding is incorrect (e.g., because the encoding does not adhere to the definition in Section 4 of [BASE64]); sent in reply to a {@code <response/>} element or an {@code <auth/>} element with initial response data.</p>
     * </blockquote>
     */
    @XmlType(factoryMethod = "create")
    static final class IncorrectEncoding extends Condition {

        private IncorrectEncoding() {
        }

        private static IncorrectEncoding create() {
            return (IncorrectEncoding) INCORRECT_ENCODING;
        }
    }

    /**
     * The implementation of the {@code <invalid-authzid/>} SASL failure.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-invalid-authzid">6.5.6.  invalid-authzid</a></cite></p>
     * <p>The authzid provided by the initiating entity is invalid, either because it is incorrectly formatted or because the initiating entity does not have permissions to authorize that ID; sent in reply to a {@code <response/>} element or an {@code <auth/>} element with initial response data.</p>
     * </blockquote>
     */
    @XmlType(factoryMethod = "create")
    static final class InvalidAuthzid extends Condition {

        private InvalidAuthzid() {
        }

        private static InvalidAuthzid create() {
            return (InvalidAuthzid) INVALID_AUTHZID;
        }
    }

    /**
     * The implementation of the {@code <invalid-mechanism/>} SASL failure.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-invalid-mechanism">6.5.7.  invalid-mechanism</a></cite></p>
     * <p>The initiating entity did not specify a mechanism, or requested a mechanism that is not supported by the receiving entity; sent in reply to an {@code <auth/>} element.</p>
     * </blockquote>
     */
    @XmlType(factoryMethod = "create")
    static final class InvalidMechanism extends Condition {

        private InvalidMechanism() {
        }

        private static InvalidMechanism create() {
            return (InvalidMechanism) INVALID_MECHANISM;
        }
    }

    /**
     * The implementation of the {@code <malformed-request/>} SASL failure.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-malformed-request">6.5.8.  malformed-request</a></cite></p>
     * <p>The request is malformed (e.g., the {@code <auth/>} element includes initial response data but the mechanism does not allow that, or the data sent violates the syntax for the specified SASL mechanism); sent in reply to an {@code <abort/>}, {@code <auth/>}, {@code <challenge/>}, or {@code <response/>} element.</p>
     * </blockquote>
     */
    @XmlType(factoryMethod = "create")
    static class MalformedRequest extends Condition {

        private MalformedRequest() {
        }

        private static MalformedRequest create() {
            return (MalformedRequest) MALFORMED_REQUEST;
        }
    }

    /**
     * The implementation of the {@code <mechanism-too-weak/>} SASL failure.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-mechanism-too-weak">6.5.9.  mechanism-too-weak</a></cite></p>
     * <p>The mechanism requested by the initiating entity is weaker than server policy permits for that initiating entity; sent in reply to an {@code <auth/>} element (with or without initial response data).</p>
     * </blockquote>
     */
    @XmlType(factoryMethod = "create")
    static final class MechanismTooWeak extends Condition {

        private MechanismTooWeak() {
        }

        private static MechanismTooWeak create() {
            return (MechanismTooWeak) MECHANISM_TOO_WEAK;
        }
    }

    /**
     * The implementation of the {@code <not-authorized/>} SASL failure.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-not-authorized">6.5.10.  not-authorized</a></cite></p>
     * <p>The authentication failed because the initiating entity did not provide proper credentials, or because some generic authentication failure has occurred but the receiving entity does not wish to disclose specific information about the cause of the failure; sent in reply to a {@code <response/>} element or an {@code <auth/>} element with initial response data.</p>
     * </blockquote>
     */
    @XmlType(factoryMethod = "create")
    static final class NotAuthorized extends Condition {

        private NotAuthorized() {
        }

        private static NotAuthorized create() {
            return (NotAuthorized) NOT_AUTHORIZED;
        }
    }

    /**
     * The implementation of the {@code <temporary-auth-failure/>} SASL failure.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-temporary-auth-failure">6.5.11.  temporary-auth-failure</a></cite></p>
     * <p>The authentication failed because of a temporary error condition within the receiving entity, and it is advisable for the initiating entity to try again later; sent in reply to an {@code <auth/>} element or a {@code <response/>} element.</p>
     * </blockquote>
     */
    @XmlType(factoryMethod = "create")
    static final class TemporaryAuthFailure extends Condition {

        private TemporaryAuthFailure() {
        }

        private static TemporaryAuthFailure create() {
            return (TemporaryAuthFailure) TEMPORARY_AUTH_FAILURE;
        }
    }

    /**
     * A general class for a SASL failure condition.
     */
    @XmlTransient
    public abstract static class Condition {

        /**
         * The implementation of the {@code <aborted/>} SASL failure.
         * <blockquote>
         * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-aborted">6.5.1.  aborted</a></cite></p>
         * <p>The receiving entity acknowledges that the authentication handshake has been aborted by the initiating entity; sent in reply to the {@code <abort/>} element.</p>
         * </blockquote>
         */
        public static final Condition ABORTED = new Aborted();

        /**
         * The implementation of the {@code <account-disabled/>} SASL failure.
         * <blockquote>
         * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-account-disabled">6.5.2.  account-disabled</a></cite></p>
         * <p>The account of the initiating entity has been temporarily disabled; sent in reply to an {@code <auth/>} element (with or without initial response data) or a {@code <response/>} element.</p>
         * </blockquote>
         */
        public static final Condition ACCOUNT_DISABLED = new AccountDisabled();

        /**
         * The implementation of the {@code <credentials-expired/>} SASL failure.
         * <blockquote>
         * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-credentials-expired">6.5.3.  credentials-expired</a></cite></p>
         * <p>The authentication failed because the initiating entity provided credentials that have expired; sent in reply to a {@code <response/>} element or an {@code <auth/>} element with initial response data.</p>
         * </blockquote>
         */
        public static final Condition CREDENTIALS_EXPIRED = new CredentialsExpired();

        /**
         * The implementation of the {@code <encryption-required/>} SASL failure.
         * <blockquote>
         * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-encryption-required">6.5.4.  encryption-required</a></cite></p>
         * <p>The mechanism requested by the initiating entity cannot be used unless the confidentiality and integrity of the underlying stream are protected (typically via TLS); sent in reply to an {@code <auth/>} element (with or without initial response data).</p>
         * </blockquote>
         */
        public static final Condition ENCRYPTION_REQUIRED = new EncryptionRequired();

        /**
         * The implementation of the {@code <incorrect-encoding/>} SASL failure.
         * <blockquote>
         * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-incorrect-encoding">6.5.5.  incorrect-encoding</a></cite></p>
         * <p>The data provided by the initiating entity could not be processed because the base 64 encoding is incorrect (e.g., because the encoding does not adhere to the definition in Section 4 of [BASE64]); sent in reply to a {@code <response/>} element or an {@code <auth/>} element with initial response data.</p>
         * </blockquote>
         */
        public static final Condition INCORRECT_ENCODING = new IncorrectEncoding();

        /**
         * The implementation of the {@code <invalid-authzid/>} SASL failure.
         * <blockquote>
         * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-invalid-authzid">6.5.6.  invalid-authzid</a></cite></p>
         * <p>The authzid provided by the initiating entity is invalid, either because it is incorrectly formatted or because the initiating entity does not have permissions to authorize that ID; sent in reply to a {@code <response/>} element or an {@code <auth/>} element with initial response data.</p>
         * </blockquote>
         */
        public static final Condition INVALID_AUTHZID = new InvalidAuthzid();

        /**
         * The implementation of the {@code <invalid-mechanism/>} SASL failure.
         * <blockquote>
         * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-invalid-mechanism">6.5.7.  invalid-mechanism</a></cite></p>
         * <p>The initiating entity did not specify a mechanism, or requested a mechanism that is not supported by the receiving entity; sent in reply to an {@code <auth/>} element.</p>
         * </blockquote>
         */
        public static final Condition INVALID_MECHANISM = new InvalidMechanism();

        /**
         * The implementation of the {@code <malformed-request/>} SASL failure.
         * <blockquote>
         * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-malformed-request">6.5.8.  malformed-request</a></cite></p>
         * <p>The request is malformed (e.g., the {@code <auth/>} element includes initial response data but the mechanism does not allow that, or the data sent violates the syntax for the specified SASL mechanism); sent in reply to an {@code <abort/>}, {@code <auth/>}, {@code <challenge/>}, or {@code <response/>} element.</p>
         * </blockquote>
         */
        public static final Condition MALFORMED_REQUEST = new MalformedRequest();

        /**
         * The implementation of the {@code <mechanism-too-weak/>} SASL failure.
         * <blockquote>
         * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-mechanism-too-weak">6.5.9.  mechanism-too-weak</a></cite></p>
         * <p>The mechanism requested by the initiating entity is weaker than server policy permits for that initiating entity; sent in reply to an {@code <auth/>} element (with or without initial response data).</p>
         * </blockquote>
         */
        public static final Condition MECHANISM_TOO_WEAK = new MechanismTooWeak();

        /**
         * The implementation of the {@code <not-authorized/>} SASL failure.
         * <blockquote>
         * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-not-authorized">6.5.10.  not-authorized</a></cite></p>
         * <p>The authentication failed because the initiating entity did not provide proper credentials, or because some generic authentication failure has occurred but the receiving entity does not wish to disclose specific information about the cause of the failure; sent in reply to a {@code <response/>} element or an {@code <auth/>} element with initial response data.</p>
         * </blockquote>
         */
        public static final Condition NOT_AUTHORIZED = new NotAuthorized();

        /**
         * The implementation of the {@code <temporary-auth-failure/>} SASL failure.
         * <blockquote>
         * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors-temporary-auth-failure">6.5.11.  temporary-auth-failure</a></cite></p>
         * <p>The authentication failed because of a temporary error condition within the receiving entity, and it is advisable for the initiating entity to try again later; sent in reply to an {@code <auth/>} element or a {@code <response/>} element.</p>
         * </blockquote>
         */
        public static final Condition TEMPORARY_AUTH_FAILURE = new TemporaryAuthFailure();

        private Condition() {
        }

        @Override
        public final String toString() {
            return Strings.toDash(getClass().getSimpleName());
        }
    }
}
