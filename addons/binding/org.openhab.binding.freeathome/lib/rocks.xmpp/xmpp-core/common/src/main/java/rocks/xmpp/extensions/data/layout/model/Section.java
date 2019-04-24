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

package rocks.xmpp.extensions.data.layout.model;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The implementation of the {@code <section/>} element in the {@code http://jabber.org/protocol/xdata-layout} namespace.
 * <p>
 * A section is used to partition a page.
 * </p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0141.html">XEP-0141: Data Forms Layout</a>
 * @see <a href="https://xmpp.org/extensions/xep-0141.html#sectioning">3.2 Sectioning Fields</a>
 * @see Page#getSections()
 */
public final class Section {

    private final List<Section> section = new ArrayList<>();

    private final List<String> text = new ArrayList<>();

    private final List<FieldReference> fieldref = new ArrayList<>();

    @XmlAttribute
    private final String label;

    private final FieldReference reportedref;

    private Section() {
        this.label = null;
        this.reportedref = null;
    }

    /**
     * Creates a section.
     *
     * @param label           The label.
     * @param fieldReferences The field references.
     */
    public Section(String label, Collection<FieldReference> fieldReferences) {
        this(label, fieldReferences, null);
    }

    /**
     * Creates a section.
     *
     * @param label           The label.
     * @param fieldReferences The field references.
     * @param text            The text.
     */
    public Section(String label, Collection<FieldReference> fieldReferences, Collection<String> text) {
        this(label, fieldReferences, text, null);
    }

    /**
     * Creates a section.
     *
     * @param label             The label.
     * @param fieldReferences   The field references.
     * @param text              The text.
     * @param reportedReference The reference to a reported field.
     */
    public Section(String label, Collection<FieldReference> fieldReferences, Collection<String> text, FieldReference reportedReference) {
        this.label = label;
        if (text != null) {
            this.text.addAll(text);
        }
        if (fieldReferences != null) {
            this.fieldref.addAll(fieldReferences);
        }
        this.reportedref = reportedReference;
    }

    /**
     * Gets the label of the section.
     *
     * @return The label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets additional information for the section.
     *
     * @return Additional information.
     */
    public List<String> getText() {
        return Collections.unmodifiableList(text);
    }

    /**
     * Gets the field references. These are the fields, which appear in this section.
     *
     * @return The field references.
     */
    public List<FieldReference> getFieldReferences() {
        return Collections.unmodifiableList(fieldref);
    }

    /**
     * Gets nested sub-sections.
     *
     * @return The nested sub-sections.
     */
    public List<Section> getSections() {
        return Collections.unmodifiableList(section);
    }

    /**
     * Gets the reported field reference.
     *
     * @return The reported field reference.
     */
    public FieldReference getReportedReference() {
        return reportedref;
    }
}
