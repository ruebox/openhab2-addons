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

package rocks.xmpp.im.roster;

import rocks.xmpp.im.roster.model.Contact;

import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.function.Consumer;

/**
 * A roster event which holds information about added, updated and removed contacts of the roster.
 * <p>
 * This event is dispatched by the {@link RosterManager}, whenever a roster push or result is received.
 * </p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see RosterManager#addRosterListener(Consumer)
 */
public final class RosterEvent extends EventObject {

    private final List<Contact> addedContacts;

    private final List<Contact> updatedContacts;

    private final List<Contact> removedContacts;

    /**
     * Constructs a prototypical Event.
     *
     * @param addedContacts   The added contacts.
     * @param updatedContacts The updated contacts.
     * @param deletedContacts The deleted contacts.
     * @param source          The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    RosterEvent(Object source, List<Contact> addedContacts, List<Contact> updatedContacts, List<Contact> deletedContacts) {
        super(source);
        this.addedContacts = Collections.unmodifiableList(addedContacts);
        this.updatedContacts = Collections.unmodifiableList(updatedContacts);
        this.removedContacts = Collections.unmodifiableList(deletedContacts);
    }

    /**
     * Gets the added contacts, which have been added since the last roster push or which have been initially set by the first roster result.
     *
     * @return The added contacts.
     */
    public final List<Contact> getAddedContacts() {
        return addedContacts;
    }

    /**
     * Gets the updated contacts, i.e. if an existing contact has changed its name, groups or subscription state.
     *
     * @return The updated contacts.
     */
    public final List<Contact> getUpdatedContacts() {
        return updatedContacts;
    }

    /**
     * Gets the removed contacts, i.e. contacts which were on the roster, but now are no longer on the roster.
     *
     * @return The removed contacts.
     */
    public final List<Contact> getRemovedContacts() {
        return removedContacts;
    }
}
