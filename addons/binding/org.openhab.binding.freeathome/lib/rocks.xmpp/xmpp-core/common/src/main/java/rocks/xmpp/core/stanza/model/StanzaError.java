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

package rocks.xmpp.core.stanza.model;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.Text;
import rocks.xmpp.core.stanza.model.errors.Condition;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import java.util.Locale;
import java.util.Objects;

/**
 * The implementation of a stanza's {@code <error/>} element.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error">8.3.  Stanza Errors</a>
 */
public final class StanzaError {

    @XmlAttribute
    private final Jid by;

    @XmlAttribute
    private final Type type;

    @XmlElementRef
    private final Condition condition;

    @XmlElement(namespace = "urn:ietf:params:xml:ns:xmpp-stanzas")
    private final Text text;

    @XmlAnyElement(lax = true)
    private final Object extension;

    /**
     * Private default constructor for unmarshalling.
     * <p/>
     * By default set an undefined condition.
     * <blockquote><p>The "defined-condition" MUST correspond to one of the stanza error conditions defined under Section 8.3.3. However, because additional error conditions might be defined in the future, if an entity receives a stanza error condition that it does not understand then it MUST treat the unknown condition as equivalent to {@code <undefined-condition/>}</p></blockquote>
     */
    @SuppressWarnings("unused")
    private StanzaError() {
        this(null, Condition.UNDEFINED_CONDITION, null, null, null, null);
    }

    /**
     * Creates an error with a given error type and a condition.
     * <blockquote><p>The {@code <error/>} element MUST contain a defined condition element.</p></blockquote>
     *
     * @param type      The error type.
     * @param condition The condition.
     */
    public StanzaError(Type type, Condition condition) {
        this(type, condition, null);
    }

    /**
     * Creates an error with a given error type and a condition.
     * <blockquote><p>The {@code <error/>} element MUST contain a defined condition element.</p></blockquote>
     *
     * @param type      The error type.
     * @param condition The condition.
     * @param text      The text.
     */
    public StanzaError(Type type, Condition condition, String text) {
        this(type, condition, text, null, null, null);
    }

    /**
     * Creates an error with a given condition.
     * <p>
     * The error type is set by the condition's associated error type.
     * </p>
     *
     * @param condition The condition.
     */
    public StanzaError(Condition condition) {
        this(condition, null);
    }

    /**
     * Creates an error with a given condition and text.
     * <p>
     * The error type is set by the condition's associated error type.
     * </p>
     *
     * @param condition The condition.
     * @param text      The text.
     */
    public StanzaError(Condition condition, String text) {
        this(null, condition, text, null, null, null);
    }

    /**
     * Creates an error with a given condition and extension.
     * <p>
     * The error type is set by the condition's associated error type.
     * </p>
     *
     * @param condition The condition.
     * @param extension The extension.
     */
    public StanzaError(Condition condition, Object extension) {
        this(null, condition, null, null, extension, null);
    }

    /**
     * Creates a stanza error with all possible values.
     *
     * @param type      The error type.
     * @param condition The condition.
     * @param text      The text.
     * @param language  The language.
     * @param extension The application specific condition.
     * @param by        The entity which returns the error.
     */
    public StanzaError(Type type, Condition condition, String text, Locale language, Object extension, Jid by) {
        this.condition = Objects.requireNonNull(condition);
        if (type == null) {
            this.type = Condition.getErrorTypeByCondition(condition);
        } else {
            this.type = type;
        }
        this.text = text != null ? new Text(text, language) : null;
        this.extension = extension;
        this.by = by;
    }

    /**
     * Gets the 'by' attribute.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-rules">8.3.1.  Rules</a></cite></p>
     * The entity that returns an error stanza MAY pass along its JID to the sender of the generated stanza (e.g., for diagnostic or tracking purposes) through the addition of a 'by' attribute to the {@code <error/>} child element.
     * </blockquote>
     *
     * @return The JID.
     */
    public final Jid getBy() {
        return by;
    }

    /**
     * Gets the error type, either 'auth', 'cancel', 'continue', 'modify' or 'wait'.
     *
     * @return The type.
     */
    public final Type getType() {
        return type;
    }

    /**
     * Gets the optional error text.
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
     * Gets the language of the error text.
     *
     * @return The language.
     */
    public final Locale getLanguage() {
        if (text != null) {
            return text.getLanguage();
        }
        return null;
    }

    /**
     * Gets the application specific condition, if any.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-app">8.3.4.  Application-Specific Conditions</a></cite></p>
     * <p>As noted, an application MAY provide application-specific stanza error information by including a properly namespaced child within the error element. Typically, the application-specific element supplements or further qualifies a defined element. Thus, the {@code <error/>} element will contain two or three child elements.</p>
     * </blockquote>
     *
     * @return The application specific condition.
     */
    public final Object getExtension() {
        return extension;
    }

    /**
     * Gets the defined error condition or {@link Condition#UNDEFINED_CONDITION} if the condition is unknown.
     *
     * @return The error condition.
     * @see <a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions">8.3.3.  Defined Conditions</a>
     */
    public final Condition getCondition() {
        if (condition != null) {
            return condition;
        }
        // The "defined-condition" MUST correspond to one of the stanza error conditions defined under Section 8.3.3.
        // However, because additional error conditions might be defined in the future,
        // if an entity receives a stanza error condition that it does not understand
        // then it MUST treat the unknown condition as equivalent to <undefined-condition/>
        return Condition.UNDEFINED_CONDITION;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(condition).append("  -  (").append(type).append(')');
        if (text != null) {
            sb.append("\n        ").append(text);
        }

        if (extension != null) {
            sb.append("\n        ").append(extension);
        }
        return sb.toString();
    }

    /**
     * Represents a {@code <error/>} 'type' attribute.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-syntax">8.3.2.  Syntax</a></cite></p>
     * <div>
     * The "error-type" MUST be one of the following:
     * <ul>
     * <li>auth -- retry after providing credentials</li>
     * <li>cancel -- do not retry (the error cannot be remedied)</li>
     * <li>continue -- proceed (the condition was only a warning)</li>
     * <li>modify -- retry after changing the data sent</li>
     * <li>wait -- retry after waiting (the error is temporary)</li>
     * </ul>
     * </div>
     * </blockquote>
     */
    @XmlEnum
    public enum Type {
        /**
         * Retry after providing credentials.
         */
        @XmlEnumValue(value = "auth")
        AUTH("retry after providing credentials"),
        /**
         * Do not retry (the error cannot be remedied).
         */
        @XmlEnumValue(value = "cancel")
        CANCEL("do not retry (the error cannot be remedied)"),
        /**
         * Proceed (the condition was only a warning).
         */
        @XmlEnumValue(value = "continue")
        CONTINUE("proceed (the condition was only a warning)"),
        /**
         * Retry after changing the data sent.
         */
        @XmlEnumValue(value = "modify")
        MODIFY("retry after changing the data sent"),
        /**
         * Retry after waiting (the error is temporary).
         */
        @XmlEnumValue(value = "wait")
        WAIT("retry after waiting (the error is temporary)");

        private final String errorText;

        Type(String errorText) {
            this.errorText = errorText;
        }

        @Override
        public final String toString() {
            return "type '" + name().toLowerCase() + "': " + errorText;
        }
    }
}