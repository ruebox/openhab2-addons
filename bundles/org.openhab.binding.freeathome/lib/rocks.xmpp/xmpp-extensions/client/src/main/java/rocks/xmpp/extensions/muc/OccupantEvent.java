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
import rocks.xmpp.extensions.muc.model.Actor;

import java.util.EventObject;
import java.util.function.Consumer;

/**
 * An event which is triggered by an occupant in a chat room.
 * Typical events are "joins" and "leaves", as well as "bans" or "kicks".
 *
 * @author Christian Schudt
 * @see ChatRoom#addOccupantListener(Consumer)
 */
public final class OccupantEvent extends EventObject {

    private final transient Occupant occupant;

    private final Type type;

    private final transient Actor actor;

    private final String reason;

    private final Jid alternativeRoom;

    /**
     * Constructs a prototypical Event.
     *
     * @param source         The object on which the Event initially occurred.
     * @param occupant       The occupant.
     * @param type           The exit reason.
     * @param actor          The actor in case the occupant was kicked or banned.
     * @param reason         The reason for kicking or banning an occupant or for destroying the room.
     * @param alternativeJid The alternative room JID in case the room has been destroyed.
     * @throws IllegalArgumentException if source is null.
     */
    OccupantEvent(Object source, Occupant occupant, Type type, Actor actor, String reason, Jid alternativeJid) {
        super(source);
        this.occupant = occupant;
        this.type = type;
        this.reason = reason;
        this.actor = actor;
        this.alternativeRoom = alternativeJid;
    }

    /**
     * Gets the occupant.
     *
     * @return The occupant.
     */
    public Occupant getOccupant() {
        return occupant;
    }

    /**
     * Gets the type of this event.
     *
     * @return The type.
     */
    public Type getType() {
        return type;
    }

    /**
     * Gets the actor in case the occupant was kicked or banned from the room.
     *
     * @return The actor or null.
     */
    public Actor getActor() {
        return actor;
    }

    /**
     * Gets the reason for kicking or banning an occupant or for destroying the room.
     *
     * @return The reason for kicking or banning an occupant or for destroying the room.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Gets the alternative room address in case the old room has been destroyed.
     *
     * @return The alternative room address.
     * @see Type#ROOM_DESTROYED
     */
    public Jid getAlternativeRoom() {
        return alternativeRoom;
    }

    /**
     * The type of the occupant event.
     */
    public enum Type {
        /**
         * The occupant has entered the room.
         */
        ENTERED,
        /**
         * The occupant has exited the room in a normal way.
         */
        EXITED,
        /**
         * The occupants has changed his or her presence status.
         */
        STATUS_CHANGED,
        /**
         * The occupant got kicked out of the room by a moderator.
         */
        KICKED,
        /**
         * The occupant got banned by an admin.
         */
        BANNED,
        /**
         * The occupant has exited the room, because he's no longer a member of the room.
         */
        MEMBERSHIP_REVOKED,
        /**
         * The occupant has exited the room, because the room became member's only.
         */
        ROOM_BECAME_MEMBERS_ONLY,
        /**
         * The occupant has exited the room, because he changed his nickname.
         */
        NICKNAME_CHANGED,
        /**
         * The occupant has exited the room, because the system was shutdown.
         */
        SYSTEM_SHUTDOWN,
        /**
         * The occupant has exited because the room got destroyed by the owner.
         */
        ROOM_DESTROYED
    }
}
