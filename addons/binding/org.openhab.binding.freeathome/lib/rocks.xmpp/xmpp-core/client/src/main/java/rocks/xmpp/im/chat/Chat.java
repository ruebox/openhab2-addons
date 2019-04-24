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

package rocks.xmpp.im.chat;

import rocks.xmpp.core.session.SendTask;
import rocks.xmpp.core.stanza.MessageEvent;
import rocks.xmpp.core.stanza.model.Message;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * An abstract chat class, which represents either a one-to-one chat session or a group chat session.
 *
 * @author Christian Schudt
 * @see ChatSession
 */
public abstract class Chat {

    protected final Set<Consumer<MessageEvent>> inboundMessageListeners = new CopyOnWriteArraySet<>();

    /**
     * Sends a message to the chat.
     *
     * @param message The message.
     * @return The message, which has been sent.
     */
    public abstract SendTask<Message> sendMessage(String message);

    /**
     * Sends a message to the chat.
     *
     * @param message The message.
     * @return The message, which has been sent.
     */
    public abstract SendTask<Message> sendMessage(Message message);

    /**
     * Adds a message listener, which allows to listen for inbound messages.
     *
     * @param messageListener The listener.
     * @see #removeInboundMessageListener(Consumer)
     */
    public final void addInboundMessageListener(Consumer<MessageEvent> messageListener) {
        inboundMessageListeners.add(messageListener);
    }

    /**
     * Removes a previously added message listener.
     *
     * @param messageListener The listener.
     * @see #addInboundMessageListener(Consumer)
     */
    public final void removeInboundMessageListener(Consumer<MessageEvent> messageListener) {
        inboundMessageListeners.remove(messageListener);
    }
}
