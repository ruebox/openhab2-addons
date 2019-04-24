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
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.extensions.muc.model.user.MucUser;

import java.util.EventObject;
import java.util.function.Consumer;

/**
 * The multi-user chat invitation event, which is triggered upon receiving an invitation to a multi-user chat.
 *
 * @author Christian Schudt
 * @see MultiUserChatManager#addInvitationListener(Consumer)
 */
public final class InvitationEvent extends EventObject {

    private final Jid inviter;

    private final boolean aContinue;

    private final Jid room;

    private final String password;

    private final String reason;

    private final String thread;

    private final boolean mediated;

    private final XmppSession xmppSession;

    InvitationEvent(Object source, XmppSession xmppSession, Jid inviter, Jid room, String reason, String password, boolean aContinue, String thread, boolean mediated) {
        super(source);
        this.inviter = inviter;
        this.room = room;
        this.reason = reason;
        this.password = password;
        this.aContinue = aContinue;
        this.thread = thread;
        this.mediated = mediated;
        this.xmppSession = xmppSession;
    }

    /**
     * Declines the invitation.
     *
     * @param reason The reason.
     */
    public void decline(String reason) {
        // For direct invitations:
        // If the contact declines the invitation, it shall silently discard the invitation.

        // Therefore only decline mediated invitations.
        if (mediated) {
            Message message = new Message(room);
            message.addExtension(MucUser.withDecline(inviter, reason));
            xmppSession.send(message);
        }
    }

    /**
     * Gets the inviter. If the invitation was mediated by the chat room, the inviter can either be a bare or full JID or the in-room JID of an occupant.
     *
     * @return The inviter.
     */
    public Jid getInviter() {
        return inviter;
    }

    /**
     * Indicates, whether a one-to-one chat session is continued in the chat room.
     *
     * @return If a one-to-one chat session is continued in the chat room.
     * @see #getThread()
     */
    public boolean isContinue() {
        return aContinue;
    }

    /**
     * Gets the room address.
     *
     * @return The room address.
     */
    public Jid getRoomAddress() {
        return room;
    }

    /**
     * Gets the password to the room.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the reason for the invitation.
     *
     * @return The reason.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Gets the thread of the continued one-to-one chat session (if any).
     *
     * @return The thread.
     * @see #isContinue()
     */
    public String getThread() {
        return thread;
    }

    /**
     * Indicates, whether the invitation is a mediated or direct invitation.
     *
     * @return True, if the invitation was mediated by the room; false, if it is a direct invitation.
     * @see <a href="https://xmpp.org/extensions/xep-0045.html#invite-direct">7.8.1 Direct Invitation</a>
     * @see <a href="https://xmpp.org/extensions/xep-0045.html#invite-mediated">7.8.2 Mediated Invitation</a>
     */
    public boolean isMediated() {
        return mediated;
    }
}
