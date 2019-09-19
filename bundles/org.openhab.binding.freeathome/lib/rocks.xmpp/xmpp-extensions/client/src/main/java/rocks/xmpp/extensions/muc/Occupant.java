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

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.extensions.muc.model.Affiliation;
import rocks.xmpp.extensions.muc.model.Role;
import rocks.xmpp.extensions.muc.model.user.MucUser;

import java.text.Collator;

/**
 * The main actor in a multi-user chat environment is the occupant, who can be said to be located "in" a multi-user chat room and to participate in the discussions held in that room.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0045.html#user">7. Occupant Use Cases</a>
 */
public final class Occupant implements Comparable<Occupant> {

    private final Affiliation affiliation;

    private final Role role;

    private final Jid jid;

    private final String nick;

    private final boolean isSelf;

    private final Presence presence;

    Occupant(Presence presence, boolean isSelf) {
        this.presence = presence;
        this.nick = presence.getFrom().getResource();
        MucUser mucUser = presence.getExtension(MucUser.class);
        if (mucUser != null && mucUser.getItem() != null) {
            this.affiliation = mucUser.getItem().getAffiliation();
            this.role = mucUser.getItem().getRole();
            this.jid = mucUser.getItem().getJid();
            this.isSelf = isSelf;
        } else {
            this.affiliation = null;
            this.role = null;
            this.jid = null;
            this.isSelf = isSelf;
        }
    }

    /**
     * Gets the affiliation of the occupant.
     *
     * @return The affiliation.
     */
    public Affiliation getAffiliation() {
        return affiliation;
    }

    /**
     * Gets the role of the occupant.
     *
     * @return The role.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Gets the JID of the occupant. Note that it can be null, if the room is (semi-)anonymous.
     *
     * @return The JID or null for (semi-)anonymous rooms.
     */
    public Jid getJid() {
        return jid;
    }

    /**
     * Gets the nickname.
     *
     * @return The nickname.
     */
    public String getNick() {
        return nick;
    }

    /**
     * Gets the current presence of this occupant.
     *
     * @return The presence.
     */
    public Presence getPresence() {
        return presence;
    }

    /**
     * If the occupant is yourself.
     *
     * @return True, if this occupant is you.
     */
    public boolean isSelf() {
        return isSelf;
    }

    /**
     * Compares this occupant with another occupant.
     * Occupants are compared first by their affiliation, then by their role, then by their nickname.
     * <p>
     * Affiliations and roles are ranked by their privileges, so that occupants with the most privileges are ranked higher.
     * </p>
     * <p>
     * That means, in a sorted list of occupants, the owners are listed first, followed by the admins, followed by the mere members.
     * Within each affiliation group, the moderators are listed first, followed by the participants and visitors. Each group is then sorted by its occupants' nicknames.
     * </p>
     *
     * @param o The other occupant.
     * @return The comparison result.
     */
    @Override
    public int compareTo(Occupant o) {
        if (this == o) {
            return 0;
        }

        if (o != null) {
            int result;
            // First compare affiliations.
            if (affiliation != null) {
                result = o.affiliation != null ? affiliation.compareTo(o.affiliation) : -1;
            } else {
                result = o.affiliation != null ? 1 : 0;
            }
            // If the affiliations are equal, compare roles.
            if (result == 0) {
                if (role != null) {
                    // If this role is not null, but the other is null, move this up (-1).
                    result = o.role != null ? role.compareTo(o.role) : -1;
                } else {
                    // If this role is null, but the other is not, move this down (1).
                    result = o.role != null ? 1 : 0;
                }
            }
            // If the roles are equal, compare presences.
            if (result == 0) {
                result = presence.compareTo(o.presence);
            }
            // If the presences are equal, compare nick names.
            if (result == 0) {
                if (nick != null) {
                    // If this nick is not null, but the other is null, move this up (-1).
                    return o.nick != null ? Collator.getInstance().compare(nick, o.nick) : -1;
                } else {
                    // If this nick is null, but the other is not, move this down (1).
                    return o.nick != null ? 1 : 0;
                }
            }
            return result;
        }
        return -1;
    }

    @Override
    public String toString() {
        return nick;
    }
}
