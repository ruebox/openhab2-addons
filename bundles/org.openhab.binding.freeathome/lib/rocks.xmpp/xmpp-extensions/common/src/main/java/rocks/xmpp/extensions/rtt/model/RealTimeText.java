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

package rocks.xmpp.extensions.rtt.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The implementation of the {@code <rtt/>} element in the {@code urn:xmpp:rtt:0} namespace.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0301.html">XEP-0301: In-Band Real Time Text</a>
 * @see <a href="https://xmpp.org/extensions/xep-0301.html#xml_schema">XML Schema</a>
 */
@XmlRootElement(name = "rtt")
public final class RealTimeText {

    /**
     * urn:xmpp:rtt:0
     */
    public static final String NAMESPACE = "urn:xmpp:rtt:0";

    @XmlElements({
            @XmlElement(name = "t", type = InsertText.class),
            @XmlElement(name = "e", type = EraseText.class),
            @XmlElement(name = "w", type = WaitInterval.class),
    })
    private final List<Action> actions = new ArrayList<>();

    @XmlAttribute(name = "seq")
    private final Integer sequence;

    @XmlAttribute(name = "event")
    private final Event event;

    @XmlAttribute(name = "id")
    private final String id;

    private RealTimeText() {
        this.event = null;
        this.sequence = null;
        this.id = null;
    }

    /**
     * Creates a {@code <rtt/>} element.
     *
     * @param event    The event.
     * @param actions  The actions.
     * @param sequence The sequence number.
     * @param id       The id (optional).
     */
    public RealTimeText(Event event, Collection<Action> actions, int sequence, String id) {
        this.event = event;
        this.actions.addAll(actions);
        this.sequence = sequence;
        this.id = id;
    }

    /**
     * Gets the event.
     * <blockquote>
     * <p>This attribute signals events for real-time text. If the 'event' attribute is omitted, event="edit" is assumed as the default.</p>
     * </blockquote>
     *
     * @return The event.
     * @see <a href="https://xmpp.org/extensions/xep-0301.html#event">4.2.2 event</a>
     */
    public final Event getEvent() {
        return event;
    }

    /**
     * Gets the sequence.
     *
     * @return The sequence.
     * @see <a href="https://xmpp.org/extensions/xep-0301.html#seq">4.2.1 seq</a>
     */
    public final Integer getSequence() {
        return sequence;
    }

    /**
     * Gets the actions.
     *
     * @return The actions.
     * @see <a href="https://xmpp.org/extensions/xep-0301.html#realtime_text_actions">4.6 Real-Time Text Actions</a>
     */
    public final List<Action> getActions() {
        return Collections.unmodifiableList(actions);
    }

    /**
     * Gets the id.
     *
     * @return The id.
     * @see <a href="https://xmpp.org/extensions/xep-0301.html#id">4.2.3 id</a>
     */
    public final String getId() {
        return id;
    }

    /**
     * The real-time text event.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0301.html#event">4.2.2 event</a>
     */
    public enum Event {
        /**
         * Begin a new real-time message.
         */
        @XmlEnumValue(value = "new")
        NEW,
        /**
         * Re-initialize the real-time message.
         */
        @XmlEnumValue(value = "reset")
        RESET,
        /**
         * Modify existing real-time message.
         */
        @XmlEnumValue(value = "edit")
        EDIT,
        /**
         * Signals activation of real-time text.
         */
        @XmlEnumValue(value = "init")
        INIT,
        /**
         * Signals deactivation of real-time text.
         */
        @XmlEnumValue(value = "cancel")
        CANCEL
    }


    /**
     * An abstract base class for all three RTT actions.
     */
    @XmlTransient
    public abstract static class Action {
    }

    /**
     * The implementation of the {@code <t/>} element.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/extensions/xep-0301.html#element_t_insert_text">4.6.3.1 Element {@code <t/>} - Insert Text</a></cite></p>
     * <p>Supports the transmission of text, including key presses, and text block inserts.</p>
     * </blockquote>
     */
    public static final class InsertText extends Action {

        @XmlValue
        private final String text;

        @XmlAttribute(name = "p")
        private final Integer position;

        private InsertText() {
            this.text = null;
            this.position = null;
        }

        /**
         * @param text The text.
         */
        public InsertText(CharSequence text) {
            this.text = text.toString();
            this.position = null;
        }

        /**
         * @param text     The text.
         * @param position The position, where the text is inserted.
         */
        public InsertText(CharSequence text, Integer position) {
            if (position != null && position < 0) {
                throw new IllegalArgumentException("position must not be < 0");
            }
            this.text = text.toString();
            this.position = position;
        }

        /**
         * Gets the text.
         *
         * @return The text.
         */
        public final String getText() {
            return text;
        }

        /**
         * Gets the character position.
         *
         * @return The position.
         */
        public final Integer getPosition() {
            return position;
        }

        @Override
        public final String toString() {
            return "Insert '" + (text != null ? text : "") + "'" + (position != null ? " at position " + position : "");
        }
    }

    /**
     * The implementation of the {@code <e/>} element.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/extensions/xep-0301.html#element_e_erase_text">4.6.3.2 Element {@code <e/>} - Erase Text</a></cite></p>
     * <p>Supports the behavior of backspace key presses. Text is removed towards beginning of the message. This element is also used for all delete operations, including the backspace key, the delete key, and text block deletes.</p>
     * </blockquote>
     */
    public static final class EraseText extends Action {
        @XmlAttribute(name = "p")
        private final Integer position;

        @XmlAttribute(name = "n")
        private final Integer numberOfCharacters;

        /**
         * Erases one character from the end of the text.
         */
        public EraseText() {
            this.position = null;
            this.numberOfCharacters = null;
        }

        /**
         * @param n The number of characters, which are erased from the end of the message.
         */
        public EraseText(Integer n) {
            if (n != null && n < 0) {
                throw new IllegalArgumentException("n must not be < 0");
            }
            this.position = null;
            this.numberOfCharacters = n;
        }

        /**
         * @param n The number of characters, which are erased before the position.
         * @param p The character position.
         */
        public EraseText(Integer n, Integer p) {
            if (n != null && n < 0) {
                throw new IllegalArgumentException("n must not be < 0");
            }
            if (p != null && p < 0) {
                throw new IllegalArgumentException("p must not be < 0");
            }
            this.position = p;
            this.numberOfCharacters = n;
        }

        /**
         * Gets the character position.
         *
         * @return The position.
         */
        public final Integer getPosition() {
            return position;
        }

        /**
         * Indicates, how many characters are removed before the position.
         *
         * @return The number of characters to be removed.
         * @see #getPosition()
         */
        public final Integer getNumberOfCharacters() {
            return numberOfCharacters;
        }

        @Override
        public final String toString() {
            return "Erase " + (numberOfCharacters != null ? numberOfCharacters : 1) + " character(s) at " + (position != null ? "position " + position : "last position");
        }
    }

    /**
     * The implementation of the {@code <w/>} element.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/extensions/xep-0301.html#element_w_wait_interval">4.6.3.3 Element {@code <w/>} - Wait Interval</a></cite></p>
     * <p>Allow for the transmission of intervals, between real-time text actions, to recreate the pauses between key presses.</p>
     * </blockquote>
     */
    public static final class WaitInterval extends Action {
        @XmlAttribute(name = "n")
        private final Long milliSeconds;

        private WaitInterval() {
            this.milliSeconds = null;
        }

        public WaitInterval(long milliSeconds) {
            this.milliSeconds = milliSeconds;
        }

        /**
         * Gets the milliseconds to wait between other actions.
         *
         * @return The milliseconds.
         */
        public final Long getMilliSeconds() {
            return milliSeconds;
        }

        @Override
        public final String toString() {
            return "Wait " + milliSeconds + " ms";
        }
    }
}
