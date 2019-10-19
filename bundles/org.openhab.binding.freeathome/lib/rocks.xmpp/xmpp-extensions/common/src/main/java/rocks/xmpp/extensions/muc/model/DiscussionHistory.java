/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Christian Schudt
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

package rocks.xmpp.extensions.muc.model;

import rocks.xmpp.util.adapters.InstantAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.Instant;

/**
 * The discussion history for a multi-user chat room, which can be requested when entering a room.
 * <h3>Usage</h3>
 * ```java
 * // To request discussion history for max. 65000 characters
 * DiscussionHistory history = DiscussionHistory.forMaxChars(65000);
 *
 * // To request discussion history for max. the last 25 messages
 * DiscussionHistory history = DiscussionHistory.forMaxMessages(25);
 *
 * // To request discussion history for the last 180 seconds
 * DiscussionHistory history = DiscussionHistory.forSeconds(180);
 *
 * // To request no discussion history at all
 * DiscussionHistory history = DiscussionHistory.none();
 * ```
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0045.html#enter-history">7.2.14 Discussion History</a>
 * @see <a href="https://xmpp.org/extensions/xep-0045.html#enter-managehistory">7.2.15 Managing Discussion History</a>
 */
public final class DiscussionHistory {

    @XmlAttribute(name = "maxchars")
    private final Integer maxChars;

    @XmlAttribute
    private final Integer maxstanzas;

    @XmlAttribute
    private final Integer seconds;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantAdapter.class)
    private final Instant since;

    private DiscussionHistory() {
        this(null, null, null, null);
    }

    private DiscussionHistory(Integer maxChars, Integer maxstanzas, Integer seconds, Instant since) {
        this.maxChars = maxChars;
        this.maxstanzas = maxstanzas;
        this.seconds = seconds;
        this.since = since;
    }

    /**
     * Limit the total number of characters in the history to "X" (where the character count is the characters of the complete XML stanzas, not only their XML character data).
     *
     * @param maxChars The maximal character count.
     * @return The history.
     */
    public static DiscussionHistory forMaxChars(int maxChars) {
        return new DiscussionHistory(maxChars, null, null, null);
    }

    /**
     * Limit the total number of messages in the history to "X".
     *
     * @param maxMessages The maximal number of messages.
     * @return The history.
     */
    public static DiscussionHistory forMaxMessages(int maxMessages) {
        return new DiscussionHistory(null, maxMessages, null, null);
    }

    /**
     * Send only the messages received in the last "X" seconds.
     *
     * @param seconds The seconds.
     * @return The history.
     */
    public static DiscussionHistory forSeconds(int seconds) {
        return new DiscussionHistory(null, null, seconds, null);
    }

    /**
     * Send only the messages received since the date.
     *
     * @param date The date.
     * @return The history.
     */
    public static DiscussionHistory since(Instant date) {
        return new DiscussionHistory(null, null, null, date);
    }

    /**
     * Send no history.
     *
     * @return The history.
     */
    public static DiscussionHistory none() {
        return new DiscussionHistory(0, null, null, null);
    }

    @Override
    public final String toString() {
        if (maxChars != null && maxChars == 0) {
            return "No discussion history";
        }
        final StringBuilder sb = new StringBuilder("Discussion history ");
        boolean appended = false;
        if (maxChars != null) {
            sb.append("limited to ").append(maxChars).append(" characters");
            appended = true;
        }
        if (maxstanzas != null) {
            if (appended) {
                sb.append(", ");
            }
            sb.append("limited to ").append(maxstanzas).append(" messages");
            appended = true;
        }
        if (seconds != null) {
            if (appended) {
                sb.append(", ");
            }
            sb.append("for the last ").append(seconds).append(" seconds");
            appended = true;
        }
        if (since != null) {
            if (appended) {
                sb.append(", ");
            }
            sb.append("since ").append(since);
        }
        return sb.toString();
    }
}
