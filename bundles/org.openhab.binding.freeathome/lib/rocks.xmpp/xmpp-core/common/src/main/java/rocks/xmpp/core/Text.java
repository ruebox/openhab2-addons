/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 Christian Schudt
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

package rocks.xmpp.core;

import rocks.xmpp.core.sasl.model.Failure;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.core.stanza.model.StanzaError;
import rocks.xmpp.core.stream.model.StreamError;
import rocks.xmpp.util.adapters.LocaleAdapter;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.text.Collator;
import java.util.Locale;
import java.util.Objects;

/**
 * A common text element which is used by multiple elements and namespaces in XMPP. This list of elements it is used by includes:
 * <ul>
 * <li>A message's body or subject</li>
 * <li>A presence's status</li>
 * <li>A stanza error's text</li>
 * <li>A stream error's text</li>
 * <li>A SASL failure's text</li>
 * </ul>
 * This class is immutable and has a natural ordering which orders its text using a {@link Collator} instance of the locale (if any) and is consistent with {@code equals}.
 *
 * @author Christian Schudt
 * @see Message#getBodies()
 * @see Message#getSubjects()
 * @see Presence#getStatuses()
 * @see Failure
 * @see StreamError
 * @see StanzaError
 */
public final class Text implements CharSequence, Comparable<Text> {

    @XmlJavaTypeAdapter(LocaleAdapter.class)
    @XmlAttribute(namespace = XMLConstants.XML_NS_URI)
    private final Locale lang;

    @XmlValue
    private final String text;

    /**
     * Private default constructor, needed for unmarshalling.
     */
    @SuppressWarnings("unused")
    private Text() {
        this.text = null;
        this.lang = null;
    }

    /**
     * Constructs a default status.
     *
     * @param text The text.
     */
    public Text(String text) {
        this(text, null);
    }

    /**
     * Constructs a status with a language attribute.
     *
     * @param text     The text
     * @param language The language.
     */
    public Text(String text, Locale language) {
        this.text = Objects.requireNonNull(text);
        this.lang = language;
    }

    /**
     * Gets the language.
     *
     * @return The language.
     */
    public final Locale getLanguage() {
        return lang;
    }

    /**
     * Gets the text.
     *
     * @return The text.
     */
    public final String getText() {
        return text;
    }

    @Override
    public final int length() {
        return toString().length();
    }

    @Override
    public final char charAt(int index) {
        return toString().charAt(index);
    }

    @Override
    public final CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    @Override
    public final int compareTo(Text o) {
        if (o == null) {
            return -1;
        }
        final Collator collator;
        if (lang != null) {
            collator = Collator.getInstance(lang);
        } else {
            collator = Collator.getInstance();
        }
        int result = collator.compare(text, o.text);
        if (result == 0) {
            if (lang != null) {
                if (o.lang != null) {
                    return lang.toLanguageTag().compareTo(o.lang.toLanguageTag());
                } else {
                    return 1;
                }
            } else {
                if (o.lang == null) {
                    return 0;
                } else {
                    return -1;
                }
            }
        }
        return result;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Text)) {
            return false;
        }
        Text other = (Text) o;

        return Objects.equals(text, other.getText())
                && Objects.equals(lang, other.getLanguage());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(text, lang);
    }

    @Override
    public final String toString() {
        return text;
    }
}