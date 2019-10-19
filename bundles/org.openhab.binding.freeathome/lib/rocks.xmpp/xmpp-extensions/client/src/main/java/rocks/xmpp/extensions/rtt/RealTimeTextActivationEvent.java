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

package rocks.xmpp.extensions.rtt;

import rocks.xmpp.addr.Jid;

import java.util.EventObject;

/**
 * This event notifies listeners, when a real-time message has been created.
 *
 * @author Christian Schudt
 * @see RealTimeTextActivationEvent
 */
public final class RealTimeTextActivationEvent extends EventObject {

    private final boolean active;

    private final Jid sender;

    /**
     * Constructs a real-time text event.
     *
     * @param source The object on which the event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    RealTimeTextActivationEvent(Object source, Jid sender, boolean active) {
        super(source);
        this.active = active;
        this.sender = sender;
    }

    /**
     * Whether the sender has activated real-time text.
     *
     * @return True, if the sender has activated real-time text.
     */
    public final boolean isActivated() {
        return active;
    }

    /**
     * Gets the sender of the real-time text.
     *
     * @return The sender.
     */
    public final Jid getSender() {
        return sender;
    }

}
