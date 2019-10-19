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

import rocks.xmpp.extensions.muc.model.admin.MucAdmin;
import rocks.xmpp.extensions.muc.model.owner.MucOwner;
import rocks.xmpp.extensions.muc.model.user.MucUser;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * The implementation of the {@code <x/>} element in the {@code http://jabber.org/protocol/muc} namespace, which allows to enter a room.
 * <h3>Usage</h3>
 * ```java
 * // To create an empty element, e.g to indicate support in a presence
 * Muc muc = Muc.empty();
 *
 * // To create an element with a password.
 * Muc muc  = Muc.withPassword("secret");
 * ```
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0045.html">XEP-0045: Multi-User Chat</a>
 * @see <a href="https://xmpp.org/extensions/xep-0045.html#schemas-muc">XML Schema</a>
 */
@XmlRootElement(name = "x")
@XmlSeeAlso({MucUser.class, MucAdmin.class, MucOwner.class})
public final class Muc {

    /**
     * http://jabber.org/protocol/muc
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/muc";

    private final String password;

    private final DiscussionHistory history;

    /**
     * Creates an empty element.
     */
    private Muc() {
        this(null, null);
    }

    private Muc(String password, DiscussionHistory discussionHistory) {
        this.password = password;
        this.history = discussionHistory;
    }

    /**
     * Creates an empty element.
     *
     * @return The MUC element.
     */
    public static Muc empty() {
        return new Muc();
    }

    /**
     * Creates an element with a password for the room.
     *
     * @param password The password.
     * @return The MUC element.
     */
    public static Muc withPassword(String password) {
        return new Muc(password, null);
    }

    /**
     * Creates an element with a history element, indicating the user wishes to retrieve history.
     *
     * @param discussionHistory The history.
     * @return The MUC element.
     */
    public static Muc withHistory(DiscussionHistory discussionHistory) {
        return new Muc(null, discussionHistory);
    }

    /**
     * Creates an element with a history element and a password for the room.
     *
     * @param password          The password.
     * @param discussionHistory The history.
     * @return The MUC element.
     */
    public static Muc withPasswordAndHistory(String password, DiscussionHistory discussionHistory) {
        return new Muc(password, discussionHistory);
    }

    /**
     * Gets the password.
     *
     * @return The password.
     */
    public final String getPassword() {
        return password;
    }

    /**
     * Gets the history.
     *
     * @return The history.
     */
    public final DiscussionHistory getHistory() {
        return history;
    }

    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder("Multi-User Chat (entering a room).");
        if (history != null) {
            sb.append(' ').append(history);
        }
        return sb.toString();
    }
}
