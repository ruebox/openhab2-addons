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

package rocks.xmpp.extensions.muc.model;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * A temporary position or privilege level within a room, distinct from a user's long-lived affiliation with the room; the possible roles are "moderator", "participant", and "visitor" (it is also possible to have no defined role). A role lasts only for the duration of an occupant's visit to a room.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0045.html#roles">5.1 Roles</a>
 */
public enum Role {
    /**
     * The moderator role.
     */
    @XmlEnumValue("moderator")
    MODERATOR,

    /**
     * The participant role.
     */
    @XmlEnumValue("participant")
    PARTICIPANT,

    /**
     * The visitor role.
     */
    @XmlEnumValue("visitor")
    VISITOR,

    /**
     * No role.
     */
    @XmlEnumValue("none")
    NONE;

    /**
     * Compares two roles and returns true, if this role is higher than the other
     * with regards to their privileges in a multi-user chat.
     * <p>
     * The highest role is moderator, followed by participant, followed by visitor, followed by none.
     *
     * @param role The other role.
     * @return True, if this role is higher than the other.
     */
    public final boolean isHigherThan(final Role role) {
        return compareTo(role) < 0;
    }
}
