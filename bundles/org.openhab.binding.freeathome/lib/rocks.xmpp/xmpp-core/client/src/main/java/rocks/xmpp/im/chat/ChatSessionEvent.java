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

import java.util.EventObject;

/**
 * A chat session event is fired, whenever a new chat session is created.
 *
 * @author Christian Schudt
 */
public final class ChatSessionEvent extends EventObject {

    private final ChatSession chatSession;

    private final boolean inbound;

    /**
     * Constructs a chat session event.
     *
     * @param source      The chat manager on which the event initially occurred.
     * @param chatSession The chat session.
     * @param inbound     True, if the chat session has been created by an inbound message.
     */
    ChatSessionEvent(ChatManager source, ChatSession chatSession, boolean inbound) {
        super(source);
        this.chatSession = chatSession;
        this.inbound = inbound;
    }

    /**
     * Gets the chat session.
     *
     * @return The chat session.
     */
    public final ChatSession getChatSession() {
        return chatSession;
    }

    /**
     * Indicates, whether the chat session has been created by an inbound message or programmatically.
     *
     * @return True, if the chat session has been created by an inbound message; false if it was created programmatically.
     */
    public final boolean isInbound() {
        return inbound;
    }
}
