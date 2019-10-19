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

package rocks.xmpp.extensions.blocking;

import rocks.xmpp.addr.Jid;

import java.util.EventObject;
import java.util.List;
import java.util.function.Consumer;

/**
 * This event notifies listeners, when a block or unblock "push" occurs.
 *
 * @author Christian Schudt
 * @see BlockingManager#addBlockingListener(Consumer)
 */
public final class BlockingEvent extends EventObject {
    private final List<Jid> blockedContacts;

    private final List<Jid> unblockedContacts;

    /**
     * Constructs a blocking event.
     *
     * @param source            The object on which the event initially occurred.
     * @param blockedContacts   The blocked contacts.
     * @param unblockedContacts The unblocked contacts.
     * @throws IllegalArgumentException if source is null.
     */
    BlockingEvent(Object source, List<Jid> blockedContacts, List<Jid> unblockedContacts) {
        super(source);
        this.blockedContacts = blockedContacts;
        this.unblockedContacts = unblockedContacts;
    }

    /**
     * Gets the (newly) blocked contacts, which were pushed by the server.
     *
     * @return The blocked contacts.
     */
    public List<Jid> getBlockedContacts() {
        return blockedContacts;
    }

    /**
     * Gets the (newly) unblocked contacts, which were pushed by the server.
     *
     * @return The unblocked contacts.
     */
    public List<Jid> getUnblockedContacts() {
        return unblockedContacts;
    }
}
