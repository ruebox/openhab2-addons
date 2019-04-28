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

package rocks.xmpp.extensions.reach;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.extensions.reach.model.Address;

import java.util.EventObject;
import java.util.List;
import java.util.function.Consumer;

/**
 * This event notifies listeners, when a contact's reachability has changed.
 *
 * @author Christian Schudt
 * @see ReachabilityManager#addReachabilityListener(Consumer)
 */
public final class ReachabilityEvent extends EventObject {
    private final Jid contact;

    private final transient List<Address> reachabilityAddresses;

    /**
     * Constructs a reachability event.
     *
     * @param source                The object on which the event initially occurred.
     * @param contact               The contact.
     * @param reachabilityAddresses The reachability addresses.
     * @throws IllegalArgumentException if source is null.
     */
    ReachabilityEvent(Object source, Jid contact, List<Address> reachabilityAddresses) {
        super(source);
        this.contact = contact;
        this.reachabilityAddresses = reachabilityAddresses;
    }

    /**
     * Gets the contact, who changed reachability.
     *
     * @return The contact.
     */
    public Jid getContact() {
        return contact;
    }

    /**
     * Gets the reachability addresses.
     *
     * @return The reachability addresses.
     */
    public List<Address> getReachabilityAddresses() {
        return reachabilityAddresses;
    }
}
