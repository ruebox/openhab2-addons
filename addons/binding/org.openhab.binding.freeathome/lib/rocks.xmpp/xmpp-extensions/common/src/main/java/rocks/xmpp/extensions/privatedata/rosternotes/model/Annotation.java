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

package rocks.xmpp.extensions.privatedata.rosternotes.model;

import rocks.xmpp.addr.Jid;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The implementation of the {@code <storage/>} element in the {@code storage:rosternotes} namespace.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/extensions/xep-0145.html">XEP-0145: Annotations</a></cite></p>
 * <p>Many modern IM clients offer functionality that enables users to make notes about items in their roster. This comes in handy if users don't have meaningful information in their vCard or if you need to remember additional things related to a roster item.</p>
 * <p>This specification defines a protocol for storing annotations about a given set of entities. Its primary goal is to enable users to store some personal piece of information with their roster items.</p>
 * <p>Annotations are stored using server-side private XML storage (the 'jabber:iq:private' namespace).</p>
 * </blockquote>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0145.html">XEP-0145: Annotations</a>
 * @see <a href="https://xmpp.org/extensions/xep-0145.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "storage")
public final class Annotation {

    /**
     * storage:rosternotes
     */
    public static final String NAMESPACE = "storage:rosternotes";

    private final List<Note> note = new ArrayList<>();

    private Annotation() {
    }

    public Annotation(Collection<Note> notes) {
        if (notes != null) {
            this.note.addAll(notes);
        }
    }

    /**
     * Gets the roster notes.
     *
     * @return The notes.
     */
    public final List<Note> getNotes() {
        return Collections.unmodifiableList(note);
    }

    /**
     * Represents a note for a contact in the roster (roster item).
     */
    public static final class Note {

        @XmlAttribute
        private final Jid jid;

        @XmlAttribute
        private final OffsetDateTime cdate;

        @XmlAttribute
        private final OffsetDateTime mdate;

        @XmlValue
        private final String value;

        private Note() {
            this.value = null;
            this.jid = null;
            this.cdate = null;
            this.mdate = null;
        }

        /**
         * Creates a roster note.
         *
         * @param note The note.
         * @param jid  The 'jid' attribute of the {@code <note/>} element SHOULD be used without a resource.
         */
        public Note(String note, Jid jid) {
            this(note, jid, null, null);
        }

        /**
         * Creates a roster note.
         *
         * @param note             The note.
         * @param jid              The JID of the contact.
         * @param creationDate     The creation date (optional).
         * @param modificationDate The modification date (optional).
         */
        public Note(String note, Jid jid, OffsetDateTime creationDate, OffsetDateTime modificationDate) {
            this.value = note;
            this.jid = Objects.requireNonNull(jid, "jid must not be null.").asBareJid();
            this.cdate = creationDate;
            this.mdate = modificationDate;
        }

        /**
         * Gets the value of the note.
         *
         * @return The value.
         */
        public final String getValue() {
            return value;
        }

        /**
         * Gets the modification date.
         *
         * @return The modification date.
         */
        public final OffsetDateTime getModificationDate() {
            return mdate;
        }

        /**
         * Gets the creation date.
         *
         * @return The creation date.
         */
        public final OffsetDateTime getCreationDate() {
            return cdate;
        }

        /**
         * Gets the JID of the contact (roster item).
         *
         * @return The JID.
         */
        public final Jid getJid() {
            return jid;
        }

        @Override
        public final String toString() {
            return "Annotation:" + value;
        }
    }
}
