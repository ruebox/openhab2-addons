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

package rocks.xmpp.extensions.receipts;

import rocks.xmpp.addr.Jid;

import java.time.Instant;
import java.util.EventObject;

/**
 * This event notifies listeners, when a message has been successfully delivered.
 *
 * @author Christian Schudt
 */
public final class MessageDeliveredEvent extends EventObject {

    private final String messageId;

    private final Instant deliveryDate;

    private final Jid from;

    /**
     * Constructs a message delivered event.
     *
     * @param source       The object on which the event initially occurred.
     * @param messageId    The message id of the delivered message.
     * @param deliveryDate The date of the delivery.
     * @throws IllegalArgumentException if source is null.
     */
    MessageDeliveredEvent(Object source, String messageId, Instant deliveryDate, Jid from) {
        super(source);
        this.messageId = messageId;
        this.deliveryDate = deliveryDate;
        this.from = from;
    }

    /**
     * Gets the message id of the successfully delivered message.
     *
     * @return The message id.
     */
    public final String getMessageId() {
        return messageId;
    }

    /**
     * Gets the date, when the original message has been delivered, i.e. when the delivery receipt has been sent.
     *
     * @return The delivery date.
     */
    public final Instant getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * Gets the sender of the receipt. Usually this is the receiver of the original message, which requested the receipt
     * and which can be referenced via the id.
     * But in the context of Multi-User Chat (XEP-0045) or Multi Cast (XEP-0033) this is not the case.s
     *
     * @return The sender.
     */
    public final Jid getFrom() {
        return from;
    }
}
