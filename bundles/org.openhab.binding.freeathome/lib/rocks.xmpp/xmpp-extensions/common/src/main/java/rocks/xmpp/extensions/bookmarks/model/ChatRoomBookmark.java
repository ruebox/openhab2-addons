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

package rocks.xmpp.extensions.bookmarks.model;

import rocks.xmpp.addr.Jid;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.Objects;

/**
 * A conference bookmark to bookmark multi-user chat rooms.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 */
public final class ChatRoomBookmark extends Bookmark {

    private final String nick;

    private final String password;

    @XmlAttribute
    private final Boolean autojoin;

    @XmlAttribute
    private final Jid jid;

    private ChatRoomBookmark() {
        super(null);
        this.jid = null;
        this.nick = null;
        this.password = null;
        this.autojoin = null;
    }

    /**
     * Creates a conference bookmark.
     *
     * @param name The bookmark name.
     * @param room The JID of the chat room.
     */
    public ChatRoomBookmark(String name, Jid room) {
        super(name);
        this.jid = Objects.requireNonNull(room);
        this.nick = null;
        this.password = null;
        this.autojoin = null;
    }

    /**
     * Creates a conference bookmark.
     *
     * @param name     The bookmark name.
     * @param room     The JID of the chat room.
     * @param nick     The user's preferred roomnick for the chatroom.
     * @param password The unencrypted string for the password needed to enter a password-protected room. See also {@link #getPassword()}.
     * @param autojoin Whether the client should automatically join the conference room on login.
     */
    public ChatRoomBookmark(String name, Jid room, String nick, String password, boolean autojoin) {
        super(name);
        this.jid = room;
        this.nick = nick;
        this.password = password;
        this.autojoin = autojoin;
    }

    /**
     * Gets the user's preferred roomnick for the chatroom.
     *
     * @return The nick.
     */
    public final String getNick() {
        return nick;
    }

    /**
     * Gets the unencrypted string for the password needed to enter a password-protected room. For security reasons, use of this element is NOT RECOMMENDED.
     *
     * @return The password.
     */
    public final String getPassword() {
        return password;
    }

    /**
     * Gets whether the client should automatically join the conference room on login.
     *
     * @return True, if the client should automatically join the conference room on login.
     */
    public final boolean isAutojoin() {
        return autojoin != null && autojoin;
    }

    /**
     * Gets the JID of the chat room.
     *
     * @return The room.
     */
    public final Jid getRoom() {
        return jid;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ChatRoomBookmark)) {
            return false;
        }
        ChatRoomBookmark other = (ChatRoomBookmark) o;

        return Objects.equals(jid, other.jid);

    }

    @Override
    public final int hashCode() {
        return Objects.hash(jid);
    }

    @Override
    public final String toString() {
        return getName() + ": " + (jid != null ? jid.toString() : "");
    }
}