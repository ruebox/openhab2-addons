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

package rocks.xmpp.im.roster.model;

import rocks.xmpp.addr.Jid;

import java.util.List;

/**
 * Represents a roster item. This interface is implemented already by {@link Contact} and could be implemented by
 * database entities as well to allow for easy conversion between database entities and XMPP elements.
 * <p>
 * Subscription state of an XMPP entity is always represented in a roster item, therefore this interface extends {@link SubscriptionState}.
 *
 * @author Christian Schudt
 */
public interface RosterItem extends SubscriptionState {

    /**
     * Gets the JID of the contact.
     *
     * @return The JID.
     */
    Jid getJid();

    /**
     * Gets the name of the contact.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6121.html#roster-syntax-items-name">2.1.2.4.  Name Attribute</a></cite></p>
     * <p>The 'name' attribute of the {@code <item/>} element specifies the "handle" to be associated with the JID, as determined by the user (not the contact). Although the value of the 'name' attribute MAY have meaning to a human user, it is opaque to the server. However, the 'name' attribute MAY be used by the server for matching purposes within the context of various XMPP extensions (one possible comparison method is that described for XMPP resourceparts in [XMPP-ADDR]).</p>
     * <p>It is OPTIONAL for a client to include the 'name' attribute when adding or updating a roster item.</p>
     * </blockquote>
     *
     * @return The name.
     */
    String getName();

    /**
     * Gets the subscription pre-approval status.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6121.html#roster-syntax-items-approved">2.1.2.1.  Approved Attribute</a></cite></p>
     * <p>The boolean 'approved' attribute with a value of "true" is used to signal subscription pre-approval as described under Section 3.4</p>
     * </blockquote>
     *
     * @return True, if the contact is pre approved.
     */
    boolean isApproved();

    /**
     * Gets the groups of the contact.
     *
     * @return The groups.
     */
    List<String> getGroups();
}
