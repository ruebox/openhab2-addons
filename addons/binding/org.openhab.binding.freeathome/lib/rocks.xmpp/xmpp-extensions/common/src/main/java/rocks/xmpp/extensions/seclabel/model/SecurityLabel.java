/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Christian Schudt
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

package rocks.xmpp.extensions.seclabel.model;

import rocks.xmpp.extensions.seclabel.model.catalog.Catalog;
import rocks.xmpp.extensions.seclabel.model.ess.EssSecurityLabel;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The implementation of the {@code <securitylabel/>} element in the {@code urn:xmpp:sec-label:0} namespace.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0258.html">XEP-0258: Security Labels in XMPP</a>
 * @see <a href="https://xmpp.org/extensions/xep-0258.html#schema-sl">XML Schema</a>
 */
@XmlSeeAlso({Catalog.class, EssSecurityLabel.class})
@XmlRootElement(name = "securitylabel")
public final class SecurityLabel {

    public static final String NAMESPACE = "urn:xmpp:sec-label:0";

    @XmlElement(name = "displaymarking")
    private final DisplayMarking displayMarking;

    @XmlElement(name = "label")
    private final Label label;

    @XmlElement(name = "equivalentlabel")
    private final List<Label> equivalentLabels = new ArrayList<>();

    public SecurityLabel() {
        this(null);
    }

    public SecurityLabel(Object primaryLabel) {
        this(primaryLabel, null);
    }

    public SecurityLabel(Object primaryLabel, DisplayMarking displayMarking) {
        this(primaryLabel, null, displayMarking);
    }

    public SecurityLabel(Object primaryLabel, Collection<Object> equivalentLabels, DisplayMarking displayMarking) {
        this.label = new Label(primaryLabel);
        this.displayMarking = displayMarking;
        if (equivalentLabels != null) {
            this.equivalentLabels.addAll(equivalentLabels.stream().map(Label::new).collect(Collectors.toList()));
        }
    }

    /**
     * Gets the display marking.
     *
     * @return The display marking.
     */
    public final DisplayMarking getDisplayMarking() {
        return displayMarking;
    }

    /**
     * Gets the primary security label.
     *
     * @return The primary security label or null to indicate the use of a default label.
     */
    public final Object getLabel() {
        return label != null ? label.securityLabel : null;
    }

    /**
     * Gets the equivalent security labels.
     * <p>
     * Each equivalent label represents an equivalent security label under other policies. This element might be used when a recipient is known to hold a clearance under a different policy than the sender.
     *
     * @return The equivalent security labels.
     */
    public final List<Object> getEquivalentLabels() {
        List<Object> labels = equivalentLabels.stream().map(label -> label.securityLabel).collect(Collectors.toList());
        return Collections.unmodifiableList(labels);
    }

    /**
     * The implementation of the {@code <displaymarking/>} element.
     * <blockquote>
     * <p>
     * The {@code <displaymarking/>} element contains a display string for use by implementations which are unable to utilize the applicable security policy to generate display markings.
     * The element may optionally contain two attributes, fgcolor= and bgcolor=, whose values are HTML color strings (e.g., 'red' or '#ff0000'), for use in colorizing the display marking. The fgcolor= default is black. The bgcolor= default is white.
     * </p>
     * </blockquote>
     */
    public static final class DisplayMarking {

        @XmlAttribute(name = "bgcolor")
        private final String bgcolor;

        @XmlAttribute(name = "fgcolor")
        private final String fgcolor;

        @XmlValue
        private final String value;

        private DisplayMarking() {
            this.value = null;
            this.fgcolor = null;
            this.bgcolor = null;
        }

        public DisplayMarking(String value) {
            this(value, null, null);
        }

        public DisplayMarking(String value, String foregroundColor, String backgroundColor) {
            this.value = Objects.requireNonNull(value);
            this.fgcolor = foregroundColor;
            this.bgcolor = backgroundColor;
        }

        /**
         * Gets the foreground color.
         *
         * @return The foreground color.
         */
        public final String getForegroundColor() {
            return fgcolor;
        }

        /**
         * Gets the background color.
         *
         * @return The background color.
         */
        public final String getBackgroundColor() {
            return bgcolor;
        }

        /**
         * Gets the display string.
         *
         * @return The display string.
         */
        public final String getValue() {
            return value;
        }
    }

    /**
     * The {@code <label/>} and {@code <equivalentlabel/>} element.
     */
    private static final class Label {

        @XmlAnyElement(lax = true)
        private final Object securityLabel;

        private Label() {
            this.securityLabel = null;
        }

        private Label(Object securityLabel) {
            this.securityLabel = securityLabel;
        }
    }
}
