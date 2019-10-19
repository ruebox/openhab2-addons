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

package rocks.xmpp.core.stream.model;

import rocks.xmpp.core.Text;
import rocks.xmpp.core.stream.model.errors.Condition;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Locale;
import java.util.Objects;

/**
 * The implementation of the {@code <stream:error/>} element.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/rfcs/rfc6120.html#streams-error">4.9.  Stream Errors</a>
 */
@XmlRootElement(name = "error")
public final class StreamError implements StreamElement {

    @XmlElementRef
    private final Condition condition;

    @XmlElement(namespace = "urn:ietf:params:xml:ns:xmpp-streams")
    private final Text text;

    @XmlAnyElement(lax = true)
    private final Object extension;

    /**
     * Private default constructor for unmarshalling.
     */
    @SuppressWarnings("unused")
    private StreamError() {
        this.condition = null;
        this.text = null;
        this.extension = null;
    }

    /**
     * Creates a stream error with only a condition.
     *
     * @param condition The non-null condition.
     */
    public StreamError(Condition condition) {
        this(condition, null, null, null);
    }

    /**
     * Creates a stream error with a condition and a descriptive text.
     *
     * @param condition The non-null condition.
     * @param text      The text.
     * @param language  May be null, but it should be present.
     */
    public StreamError(Condition condition, String text, Locale language) {
        this(condition, text, language, null);
    }

    /**
     * Creates a stream error with a condition, optional text and an extension.
     *
     * @param condition The non-null condition.
     * @param extension The application-specific error extension.
     */
    public StreamError(Condition condition, Object extension) {
        this(condition, null, null, extension);
    }

    /**
     * Creates a stream error with a condition, optional text and an extension.
     *
     * @param condition The non-null condition.
     * @param text      The text.
     * @param language  May be null, but it should be present.
     * @param extension The application-specific error extension.
     */
    public StreamError(Condition condition, String text, Locale language, Object extension) {
        this.condition = Objects.requireNonNull(condition);
        this.text = text != null ? new Text(text, language) : null;
        this.extension = extension;
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
     * Gets the application specific condition, if any.
     *
     * @return The application specific condition.
     * @see <a href="https://xmpp.org/rfcs/rfc6120.html#streams-error-app">4.9.4.  Application-Specific Conditions</a>
     */
    public final Object getExtension() {
        return extension;
    }

    /**
     * Gets the defined stream error condition or {@link Condition#UNDEFINED_CONDITION} if the condition is unknown.
     *
     * @return The error condition.
     * @see <a href="https://xmpp.org/rfcs/rfc6120.html#streams-error-conditions">4.9.3.  Defined Stream Error Conditions</a>
     */
    public final Condition getCondition() {
        // The "defined-condition" MUST correspond to one of the stream error conditions defined under Section 4.9.3.
        // However, because additional error conditions might be defined in the future,
        // if an entity receives a stream error condition that it does not understand
        // then it MUST treat the unknown condition as equivalent to <undefined-condition/>.
        if (condition != null) {
            return condition;
        }
        return Condition.UNDEFINED_CONDITION;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(condition);

        if (text != null) {
            sb.append("\n        ").append(text);
        }

        if (extension != null) {
            sb.append("\n        ").append(extension);
        }
        return sb.toString();
    }
}