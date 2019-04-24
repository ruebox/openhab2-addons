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

package rocks.xmpp.extensions.muc;

import java.time.Instant;
import java.util.EventObject;
import java.util.function.Consumer;

/**
 * This event is fired, when the subject in a chat room has changed.
 *
 * @author Christian Schudt
 * @see ChatRoom#addSubjectChangeListener(Consumer)
 */
public final class SubjectChangeEvent extends EventObject {
    private final String subject;

    private final Instant date;

    private final boolean isDelayed;

    private final String nickname;

    /**
     * Constructs a prototypical Event.
     *
     * @param source  The object on which the Event initially occurred.
     * @param subject The subject.
     * @throws IllegalArgumentException if source is null.
     */
    SubjectChangeEvent(Object source, String subject, String nick, boolean isDelayed, Instant date) {
        super(source);
        this.subject = subject;
        this.isDelayed = isDelayed;
        this.date = date;
        this.nickname = nick;
    }

    /**
     * Gets the new subject.
     *
     * @return The subject.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Gets the date, when the message was sent.
     *
     * @return The send date.
     */
    public Instant getDate() {
        return date;
    }

    /**
     * Gets the nickname who changed the subject.
     *
     * @return The nickname.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Indicates, if the subject change is delayed.
     *
     * @return True, if the subject change is delayed.
     */
    public boolean isDelayed() {
        return isDelayed;
    }
}
