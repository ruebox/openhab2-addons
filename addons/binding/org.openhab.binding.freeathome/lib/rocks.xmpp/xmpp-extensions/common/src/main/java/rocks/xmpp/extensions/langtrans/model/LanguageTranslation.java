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

package rocks.xmpp.extensions.langtrans.model;

import rocks.xmpp.extensions.langtrans.model.items.LanguageSupport;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * The implementation of the the {@code <x/>} element in the {@code urn:xmpp:langtrans} namespace.
 *
 * @author Christian Schudt
 */
@XmlRootElement(name = "x")
@XmlSeeAlso(LanguageSupport.class)
public final class LanguageTranslation {

    /**
     * urn:xmpp:langtrans
     */
    public static final String NAMESPACE = "urn:xmpp:langtrans";

    @XmlElement(name = "translation")
    private final List<Translation> translations = new ArrayList<>();

    @XmlElement
    private final Source source;

    private LanguageTranslation() {
        this.source = null;
    }

    public LanguageTranslation(Translation... translations) {
        this(Arrays.asList(translations));
    }

    public LanguageTranslation(Collection<Translation> translations) {
        this.source = null;
        this.translations.addAll(translations);
    }

    public LanguageTranslation(String sourceText, Locale sourceLanguage, Collection<Translation> translations) {
        this.source = new Source(sourceText, sourceLanguage);
        this.translations.addAll(translations);
    }

    /**
     * Gets the translations.
     *
     * @return The translations.
     */
    public final List<Translation> getTranslations() {
        return Collections.unmodifiableList(translations);
    }

    /**
     * Gets the source text.
     *
     * @return The source text.
     */
    public final String getSourceText() {
        return source != null ? source.getText() : null;
    }

    /**
     * Gets the source language.
     *
     * @return The source language.
     */
    public final Locale getSourceLanguage() {
        return source != null ? source.getLanguage() : null;
    }

    /**
     * The source element, which represents a source text and source language.
     */
    private static final class Source {

        @XmlAttribute(name = "lang", namespace = XMLConstants.XML_NS_URI)
        private final Locale language;

        @XmlValue
        private final String text;

        private Source() {
            this.text = null;
            this.language = null;
        }

        private Source(String text, Locale language) {
            this.text = Objects.requireNonNull(text);
            this.language = Objects.requireNonNull(language);
        }

        /**
         * Gets the source language.
         *
         * @return The source language.
         */
        private Locale getLanguage() {
            return language;
        }

        /**
         * Gets the source text.
         *
         * @return The source text.
         */
        private String getText() {
            return text;
        }
    }

    /**
     * The translation element.
     */
    public static final class Translation {

        @XmlValue
        private final String text;

        @XmlAttribute(name = "destination_lang")
        private final Locale destinationLanguage;

        @XmlAttribute(name = "source_lang")
        private final Locale sourceLanguage;

        @XmlAttribute(name = "engine")
        private final String engine;

        @XmlAttribute(name = "dictionary")
        private final String dictionary;

        @XmlAttribute(name = "reviewed")
        private final Boolean reviewed;

        private Translation() {
            this.destinationLanguage = null;
            this.sourceLanguage = null;
            this.engine = null;
            this.dictionary = null;
            this.reviewed = null;
            this.text = null;
        }

        private Translation(Locale destinationLanguage, Locale sourceLanguage, String engine, String dictionary, Boolean reviewed, String text) {
            this.destinationLanguage = Objects.requireNonNull(destinationLanguage);
            this.sourceLanguage = sourceLanguage;
            this.engine = engine;
            this.dictionary = dictionary;
            this.reviewed = reviewed;
            this.text = text;
        }

        /**
         * Creates a translation element for a destination language.
         *
         * @param destinationLanguage The destination language.
         * @return The translation element.
         */
        public static Translation forDestinationLanguage(Locale destinationLanguage) {
            return new Translation(destinationLanguage, null, null, null, null, null);
        }

        /**
         * Sets the source language.
         *
         * @param sourceLanguage The source language.
         * @return A new translation.
         */
        public Translation withSourceLanguage(Locale sourceLanguage) {
            return new Translation(destinationLanguage, sourceLanguage, engine, dictionary, reviewed, text);
        }

        /**
         * Sets the engine.
         *
         * @param engine The engine.
         * @return A new translation.
         */
        public Translation withEngine(String engine) {
            return new Translation(destinationLanguage, sourceLanguage, engine, dictionary, reviewed, text);
        }

        /**
         * Sets the dictionary.
         *
         * @param dictionary The dictionary.
         * @return A new translation.
         */
        public Translation withDictionary(String dictionary) {
            return new Translation(destinationLanguage, sourceLanguage, engine, dictionary, reviewed, text);
        }

        /**
         * Indicates that the translation has been reviewed by a human.
         *
         * @param reviewed If the translation has been reviewed.
         * @return A new translation.
         */
        public Translation withReviewed(boolean reviewed) {
            return new Translation(destinationLanguage, sourceLanguage, engine, dictionary, reviewed, text);
        }

        /**
         * Sets the translated text.
         *
         * @param text The text.
         * @return A new translation.
         */
        public Translation withTranslatedText(String text) {
            return new Translation(destinationLanguage, sourceLanguage, engine, dictionary, reviewed, text);
        }

        /**
         * Gets the destination language.
         *
         * @return The destination language.
         */
        public final Locale getDestinationLanguage() {
            return destinationLanguage;
        }

        /**
         * Gets the source language.
         *
         * @return The source language.
         */
        public final Locale getSourceLanguage() {
            return sourceLanguage;
        }

        /**
         * Gets the engine.
         *
         * @return The engine.
         */
        public final String getEngine() {
            return engine;
        }

        /**
         * Gets the translated text.
         *
         * @return The translated text.
         */
        public final String getTranslatedText() {
            return text;
        }

        /**
         * Gets the dictionary.
         *
         * @return The dictionary.
         */
        public final String getDictionary() {
            return dictionary;
        }

        /**
         * Indicates whether the translation has been reviewed.
         *
         * @return If it has been reviewed.
         */
        public final boolean isReviewed() {
            return reviewed != null && reviewed;
        }
    }
}
