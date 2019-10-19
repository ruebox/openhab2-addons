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

package rocks.xmpp.extensions.receipts;

import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.MessageEvent;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.extensions.delay.model.DelayedDelivery;
import rocks.xmpp.extensions.receipts.model.MessageDeliveryReceipts;
import rocks.xmpp.util.XmppUtils;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The implementation of <a href="https://xmpp.org/extensions/xep-0184.html">XEP-0184: Message Delivery Receipts</a>.
 * <p>
 * This manager automatically adds message delivery requests to outbound messages, if enabled.
 * If a message has been received by the recipient, registered listeners will be notified about the receipt.
 * </p>
 * <p>
 * If an inbound message contains a delivery receipt request, a receipt is automatically sent back to the requesting entity.
 * </p>
 * <p>
 * Note that messages must contain an id, in order to track receipts. If a message does not contain an id, requests won't be added.
 * </p>
 * <h3>Code sample</h3>
 * ```java
 * MessageDeliveryReceiptsManager messageDeliveryReceiptsManager = xmppSession.getManager(MessageDeliveryReceiptsManager.class);
 * messageDeliveryReceiptsManager.addMessageDeliveredListener(e -> System.out.println("Message delivered: " + e.getMessageId()));
 * ```
 */
public final class MessageDeliveryReceiptsManager extends Manager {

    /**
     * A default filter for automatic receipt request on outbound messages.
     */
    private static final Predicate<Message> DEFAULT_FILTER = message ->
            // A sender could request receipts on any non-error content message (chat, groupchat, headline, or normal) no matter if the recipient's address is a bare JID <localpart@domain.tld> or a full JID <localpart@domain.tld/resource>.
            message.getType() != Message.Type.ERROR
                    // To prevent looping, an entity MUST NOT include a receipt request (i.e., the <request/> element) in an ack message (i.e., a message stanza that includes the <received/> element).
                    && !message.hasExtension(MessageDeliveryReceipts.Received.class)
                    // A sender MUST include an 'id' attribute on every content message that requests a receipt, so that the sender can properly track ack messages.
                    && message.getId() != null
                    // The message must not have a request extension already.
                    && !message.hasExtension(MessageDeliveryReceipts.Request.class);

    final Set<Consumer<MessageDeliveredEvent>> messageDeliveredListeners = new CopyOnWriteArraySet<>();

    private final Consumer<MessageEvent> inboundMessageListener;

    private final Consumer<MessageEvent> outboundMessageListener;

    private Predicate<Message> messageFilter;

    /**
     * Creates the manager.
     *
     * @param xmppSession The underlying connection.
     */
    private MessageDeliveryReceiptsManager(final XmppSession xmppSession) {
        super(xmppSession, true);

        this.inboundMessageListener = e -> {
            Message message = e.getMessage();
            // If a client requests a receipt, send an ack message.
            if (message.hasExtension(MessageDeliveryReceipts.Request.class) && message.getId() != null) {
                Message receiptMessage = new Message(message.getFrom(), message.getType());
                receiptMessage.setFrom(message.getTo());
                receiptMessage.addExtension(new MessageDeliveryReceipts.Received(message.getId()));
                xmppSession.send(receiptMessage);
            }
            // If the message is a receipt.
            MessageDeliveryReceipts.Received received = message.getExtension(MessageDeliveryReceipts.Received.class);
            if (received != null) {
                // Notify the listeners about the reception.
                XmppUtils.notifyEventListeners(messageDeliveredListeners, new MessageDeliveredEvent(MessageDeliveryReceiptsManager.this, received.getId(), DelayedDelivery.sendDate(message), message.getFrom()));
            }
        };

        this.outboundMessageListener = e -> {
            Message message = e.getMessage();
            // If we are sending a message, append a receipt request, if it passes all filters.
            Predicate<Message> predicate;
            synchronized (this) {
                if (messageFilter != null) {
                    predicate = DEFAULT_FILTER.and(messageFilter);
                } else {
                    predicate = DEFAULT_FILTER;
                }
            }
            if (!predicate.test(message)) {
                return;
            }
            message.putExtension(MessageDeliveryReceipts.REQUEST);
        };
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        xmppSession.addInboundMessageListener(inboundMessageListener);
        xmppSession.addOutboundMessageListener(outboundMessageListener);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        xmppSession.removeInboundMessageListener(inboundMessageListener);
        xmppSession.removeOutboundMessageListener(outboundMessageListener);
    }

    /**
     * Adds a message delivered listener, which allows to listen for delivered messages.
     *
     * @param messageDeliveredListener The listener.
     * @see #removeMessageDeliveredListener(Consumer)
     */
    public void addMessageDeliveredListener(Consumer<MessageDeliveredEvent> messageDeliveredListener) {
        messageDeliveredListeners.add(messageDeliveredListener);
    }

    /**
     * Removes a previously added message delivered listener.
     *
     * @param messageDeliveredListener The listener.
     * @see #addMessageDeliveredListener(Consumer)
     */
    public void removeMessageDeliveredListener(Consumer<MessageDeliveredEvent> messageDeliveredListener) {
        messageDeliveredListeners.remove(messageDeliveredListener);
    }

    /**
     * Outbound messages, which pass the filter automatically request a receipt, i.e. a {@code <request/>} extension.
     *
     * @param messageFilter The message filter.
     */
    public synchronized void setMessageFilter(Predicate<Message> messageFilter) {
        this.messageFilter = messageFilter;
    }

    @Override
    protected void dispose() {
        messageDeliveredListeners.clear();
    }
}
