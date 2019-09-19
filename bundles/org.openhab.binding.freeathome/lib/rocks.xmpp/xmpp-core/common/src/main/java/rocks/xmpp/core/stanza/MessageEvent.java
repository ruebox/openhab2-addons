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

package rocks.xmpp.core.stanza;

import rocks.xmpp.core.stanza.model.Message;

/**
 * A message event is fired whenever a message is received or sent.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 */
public final class MessageEvent extends StanzaEvent<Message> {
    /**
     * Constructs a message event.
     *
     * @param source  The object on which the event initially occurred.
     * @param message The message stanza.
     * @param inbound True, if the stanza is inbound.
     * @throws IllegalArgumentException if source is null.
     */
    public MessageEvent(Object source, Message message, boolean inbound) {
        super(source, message, inbound);
    }

    /**
     * Gets the message.
     *
     * @return The message.
     */
    public final Message getMessage() {
        return stanza;
    }
}
