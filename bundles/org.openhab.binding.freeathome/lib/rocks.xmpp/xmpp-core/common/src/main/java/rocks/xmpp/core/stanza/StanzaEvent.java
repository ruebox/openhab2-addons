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

package rocks.xmpp.core.stanza;

import rocks.xmpp.core.stanza.model.Stanza;

import java.util.EventObject;

/**
 * An event which is triggered for inbound and outbound stanzas.
 *
 * @author Christian Schudt
 */
abstract class StanzaEvent<S extends Stanza> extends EventObject {

    final S stanza;

    private final boolean inbound;

    private boolean consumed;

    StanzaEvent(Object source, S stanza, boolean inbound) {
        super(source);
        this.stanza = stanza;
        this.inbound = inbound;
    }

    /**
     * Indicates, whether the stanza has been received (inbound) or is about to being sent (outbound).
     *
     * @return True, if the stanza is inbound; false if it is outbound.
     */
    public final boolean isInbound() {
        return inbound;
    }

    /**
     * Marks this event as consumed.
     * For outbound stanzas, consuming this event cancels sending the stanza.
     *
     * @see #isConsumed()
     */
    public final void consume() {
        this.consumed = true;
    }

    /**
     * Indicates, whether this event has been consumed.
     *
     * @return True, if this event has been consumed.
     * @see #consume()
     */
    public final boolean isConsumed() {
        return consumed;
    }

    @Override
    public final String toString() {
        return (inbound ? "Inbound " : "Outbound ") + stanza;
    }
}
