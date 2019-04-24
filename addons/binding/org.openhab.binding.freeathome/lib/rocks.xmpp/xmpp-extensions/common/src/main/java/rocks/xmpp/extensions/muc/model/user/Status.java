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

package rocks.xmpp.extensions.muc.model.user;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.Objects;

/**
 * The implementation of the {@code <status/>} element.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0045.html#statuscodes">11. Status Codes</a>
 */
public final class Status {

    /**
     * Inform user that any occupant is allowed to see the user's full JID (100).
     */
    public static final Status ROOM_IS_NON_ANONYMOUS = new Status(100);

    /**
     * Inform user that his or her affiliation changed while not in the room (101).
     */
    public static final Status AFFILIATION_CHANGED = new Status(101);

    /**
     * Inform occupants that room now shows unavailable members (102).
     */
    public static final Status ROOM_SHOWS_UNAVAILABLE_MEMBERS = new Status(102);

    /**
     * Inform occupants that room now does not show unavailable members (103).
     */
    public static final Status ROOM_DOES_NOT_SHOW_UNAVAILABLE_MEMBERS = new Status(103);

    /**
     * Inform occupants that a non-privacy-related room configuration change has occurred (104).
     */
    public static final Status ROOM_CONFIGURATION_CHANGED = new Status(104);

    /**
     * Inform user that presence refers to itself (110).
     */
    public static final Status SELF_PRESENCE = new Status(110);

    /**
     * Inform occupants that room logging is now enabled (170).
     */
    public static final Status ROOM_LOGGING_ENABLED = new Status(170);

    /**
     * Inform occupants that room logging is now disabled (171).
     */
    public static final Status ROOM_LOGGING_DISABLED = new Status(171);

    /**
     * Inform occupants that the room is now non-anonymous (172).
     */
    public static final Status ROOM_NON_ANONYMOUS = new Status(172);

    /**
     * Inform occupants that the room is now semi-anonymous (173).
     */
    public static final Status ROOM_SEMI_ANONYMOUS = new Status(173);

    /**
     * Inform occupants that the room is now fully-anonymous (174).
     */
    public static final Status ROOM_FULLY_ANONYMOUS = new Status(174);

    /**
     * Inform user that a new room has been created (201).
     */
    public static final Status NEW_ROOM_CREATED = new Status(201);

    /**
     * Inform user that service has assigned or modified occupant's roomnick (210).
     */
    public static final Status SERVICE_HAS_ASSIGNED_OR_MODIFIED_NICK = new Status(210);

    /**
     * Inform user that he or she has been banned from the room (301).
     */
    public static final Status BANNED = new Status(301);

    /**
     * Inform all occupants of new room nickname (303).
     */
    public static final Status NICK_CHANGED = new Status(303);

    /**
     * Inform user that he or she has been kicked from the room (307).
     */
    public static final Status KICKED = new Status(307);

    /**
     * Inform user that he or she is being removed from the room
     * because of an affiliation change (321).
     */
    public static final Status MEMBERSHIP_REVOKED = new Status(321);

    /**
     * Inform user that he or she is being removed from the room
     * because the room has been changed to members-only and the
     * user is not a member (322).
     */
    public static final Status ROOM_CHANGED_TO_MEMBERS_ONLY = new Status(322);

    /**
     * Inform user that he or she is being removed from the room
     * because the MUC service is being shut down (332).
     */
    public static final Status SERVICE_SHUT_DOWN = new Status(332);

    /**
     * Inform users that a user was removed because of an error reply (for example
     * when an s2s link fails between the MUC and the removed users server).
     */
    public static final Status REMOVED_DUE_TO_ERROR = new Status(333);

    @XmlAttribute
    private final Integer code;

    private Status() {
        this.code = null;
    }

    /**
     * Creates a 'status' element with a code.
     *
     * @param code The code.
     */
    private Status(int code) {
        this.code = code;
    }

    /**
     * Gets the status code.
     *
     * @return The status code.
     */
    public final int getCode() {
        return code != null ? code : 0;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Status)) {
            return false;
        }
        Status other = (Status) o;

        return Objects.equals(code, other.code);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(code);
    }
}
