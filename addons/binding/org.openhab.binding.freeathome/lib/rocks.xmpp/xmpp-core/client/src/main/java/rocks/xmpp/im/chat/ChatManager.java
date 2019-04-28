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

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.MessageEvent;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.util.XmppUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * This class manages one-to-one chat sessions, which are described in <a href="https://xmpp.org/rfcs/rfc6121.html#message-chat">5.1.  One-to-One Chat Sessions</a> and <a href="https://xmpp.org/extensions/xep-0201.html">XEP-0201: Best Practices for Message Threads</a>.
 * <h3>Creating a new chat session</h3>
 * ```java
 * ChatSession chatSession = xmppSession.getManager(ChatManager.class).createChatSession(chatPartner);
 * ```
 * <h3>Listen for new chat sessions</h3>
 * <p>When a contact initiates a new chat session with you, you can listen for it with {@link #addChatSessionListener(Consumer)}.
 * The listener will be called either if you created the session programmatically as shown above, or if it is created by a contact, i.e. because he or she sent you a chat message.
 * </p>
 * <p>You should add a {@link ChatSession#addInboundMessageListener(Consumer)} to the chat session in order to listen for messages.</p>
 * ```java
 * xmppSession.getManager(ChatManager.class).addChatSessionListener(chatSessionEvent -> {
 *     ChatSession chatSession = chatSessionEvent.getChatSession();
 *     chatSession.addInboundMessageListener(e -> {
 *         Message message = e.getMessage();
 *     });
 * });
 * ```
 */
public final class ChatManager extends Manager {

    /**
     * <blockquote>
     * <p>For {@code <message/>} stanzas of type "chat" exchanged between two entities, the value of the {@code <thread/>} element shall be considered equivalent to a unique identifier for the chat session or conversation thread.</p>
     * </blockquote>
     */
    private final Map<Jid, Map<String, ChatSession>> chatSessions = new ConcurrentHashMap<>();

    private final Set<Consumer<ChatSessionEvent>> chatSessionListeners = new CopyOnWriteArraySet<>();

    /**
     * Creates the chat manager.
     *
     * @param xmppSession The connection.
     */
    private ChatManager(final XmppSession xmppSession) {
        super(xmppSession, true);
    }

    @Override
    protected final void initialize() {
        xmppSession.addInboundMessageListener(e -> {
            Message message = e.getMessage();
            if (message.getType() == Message.Type.CHAT) {
                Jid chatPartner = message.getFrom();
                // If an entity receives such a message with a new or unknown ThreadID, it SHOULD treat the message as part of a new chat session.
                // If an entity receives a message of type "chat" without a thread ID, then it SHOULD create a new session with a new thread ID (and include that thread ID in all the messages it sends within the new session).
                String threadId = message.getThread() != null ? message.getThread() : UUID.randomUUID().toString();
                if (chatPartner != null) {
                    ChatSession chatSession = buildChatSession(chatPartner, threadId, xmppSession, true);
                    // Until and unless the user's client receives a reply from the contact, it SHOULD send any further messages to the contact's bare JID. The contact's client SHOULD address its replies to the user's full JID <user@domainpart/resourcepart> as provided in the 'from' address of the initial message.
                    chatSession.setChatPartner(message.getFrom());
                    XmppUtils.notifyEventListeners(chatSession.inboundMessageListeners, new MessageEvent(chatSession, message, true));
                }
            }
        });

        xmppSession.addInboundPresenceListener(e -> {
            // A client SHOULD "unlock" after having received a <message/> or <presence/> stanza from any other resource controlled by the peer (or a presence stanza from the locked resource); as a result, it SHOULD address its next message(s) in the chat session to the bare JID of the peer (thus "unlocking" the previous "lock") until it receives a message from one of the peer's full JIDs.
            Presence presence = e.getPresence();
            Jid contact = presence.getFrom().asBareJid();
            Map<String, ChatSession> chatSessionMap;
            if ((chatSessionMap = chatSessions.get(contact)) != null) {
                for (ChatSession chatSession : chatSessionMap.values()) {
                    chatSession.setChatPartner(contact);
                }
            }
        });
    }

    /**
     * Adds a chat session listener.
     *
     * @param chatSessionListener The listener.
     * @see #removeChatSessionListener(Consumer)
     */
    public void addChatSessionListener(Consumer<ChatSessionEvent> chatSessionListener) {
        chatSessionListeners.add(chatSessionListener);
    }

    /**
     * Removes a previously added chat session listener.
     *
     * @param chatSessionListener The listener.
     * @see #addChatSessionListener(Consumer)
     */
    public void removeChatSessionListener(Consumer<ChatSessionEvent> chatSessionListener) {
        chatSessionListeners.remove(chatSessionListener);
    }

    /**
     * Creates a new chat session or returns an existing one for the given chat partner and notifies any {@linkplain Consumer chat session listeners} about it.
     *
     * @param chatPartner The chat partner.
     * @return The chat session.
     */
    public final ChatSession createChatSession(final Jid chatPartner) {
        return createChatSession(chatPartner, UUID.randomUUID().toString());
    }

    /**
     * Creates a new chat session with a given thread id or returns an existing one for the given chat partner and notifies any {@linkplain Consumer chat session listeners} about it.
     *
     * @param chatPartner The chat partner.
     * @param threadId    The thread id to use for this chat session.
     * @return The chat session.
     */
    public final ChatSession createChatSession(final Jid chatPartner, final String threadId) {
        return buildChatSession(Objects.requireNonNull(chatPartner, "chatPartner must not be null."), threadId, xmppSession, false);
    }

    private ChatSession buildChatSession(final Jid chatPartner, final String threadId, final XmppSession xmppSession, final boolean inbound) {
        Jid contact = chatPartner.asBareJid();
        // If there are no chat sessions with that contact yet, put the contact into the map.
        Map<String, ChatSession> chatSessionMap = chatSessions.computeIfAbsent(contact, k -> new ConcurrentHashMap<>());
        return chatSessionMap.computeIfAbsent(threadId, k -> {
            ChatSession chatSession = new ChatSession(chatPartner, threadId, xmppSession, this);
            XmppUtils.notifyEventListeners(chatSessionListeners, new ChatSessionEvent(this, chatSession, inbound));
            return chatSession;
        });
    }

    /**
     * Destroys the chat session.
     *
     * @param chatSession The chat session.
     */
    void destroyChatSession(ChatSession chatSession) {
        Jid user = Objects.requireNonNull(chatSession, "chatSession must not be null.").getChatPartner().asBareJid();
        Map<String, ChatSession> chatSessionMap = chatSessions.get(user);
        if (chatSessionMap != null) {
            chatSessionMap.remove(chatSession.getThread());
            if (chatSessionMap.isEmpty()) {
                chatSessions.remove(user);
            }
        }
    }

    @Override
    protected void dispose() {
        chatSessionListeners.clear();
        chatSessions.clear();
    }
}
