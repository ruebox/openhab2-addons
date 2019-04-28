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

package rocks.xmpp.extensions.muc.conference.model;

import rocks.xmpp.addr.Jid;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The implementation of the {@code <x/>} element in the {@code jabber:x:conference} namespace, which represents a direct multi-user chat invitation.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 */
@XmlRootElement(name = "x")
public final class DirectInvitation {

    /**
     * jabber:x:conference
     */
    public static final String NAMESPACE = "jabber:x:conference";

    @XmlAttribute(name = "continue")
    private Boolean aContinue;

    @XmlAttribute
    private Jid jid;

    @XmlAttribute
    private String password;

    @XmlAttribute
    private String reason;

    @XmlAttribute
    private String thread;

    private DirectInvitation() {
    }

    public DirectInvitation(Jid jid) {
        this.jid = jid;
    }

    public DirectInvitation(Jid jid, String password, String reason) {
        this.jid = jid;
        this.password = password;
        this.reason = reason;
    }

    public DirectInvitation(Jid jid, String password, String reason, boolean isContinue, String thread) {
        this.jid = jid;
        this.password = password;
        this.reason = reason;
        this.aContinue = isContinue;
        this.thread = thread;
    }

    /**
     * Indicates, if the groupchat room continues a one-to-one chat.
     *
     * @return True, if the groupchat room continues a one-to-one chat.
     */
    public boolean isContinue() {
        return aContinue != null && aContinue;
    }

    /**
     * Gets the thread of the one-to-one chat, which is continued (optional).
     *
     * @return The thread or null.
     */
    public String getThread() {
        return thread;
    }

    /**
     * Gets a human-readable purpose for the invitation (optional).
     *
     * @return The reason or null.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Gets a password needed for entry into a password-protected room (optional).
     *
     * @return The password or null.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the address of the groupchat room to be joined.
     *
     * @return The room address.
     */
    public Jid getRoomAddress() {
        return jid;
    }
}
