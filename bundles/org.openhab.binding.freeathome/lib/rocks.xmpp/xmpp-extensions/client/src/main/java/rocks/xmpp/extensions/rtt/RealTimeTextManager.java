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
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.MessageEvent;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.extensions.messagecorrect.model.Replace;
import rocks.xmpp.extensions.rtt.model.RealTimeText;
import rocks.xmpp.im.chat.Chat;
import rocks.xmpp.util.XmppUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * Manages In-Band Real Time Text.
 *
 * @author Christian Schudt
 * @see <a href="http://www.xmpp.org/extensions/xep-0301.html">XEP-0301: In-Band Real Time Text</a>
 */
public final class RealTimeTextManager extends Manager {

    private final Map<TrackingKey, InboundRealTimeMessage> realTimeMessageMap = new ConcurrentHashMap<>();

    private final Set<Consumer<RealTimeTextActivationEvent>> realTimeTextActivationListeners = new CopyOnWriteArraySet<>();

    private final Set<Consumer<RealTimeMessageEvent>> inboundRealTimeMessageListeners = new CopyOnWriteArraySet<>();

    private final Consumer<MessageEvent> messageListener;

    private RealTimeTextManager(final XmppSession xmppSession) {
        super(xmppSession);

        messageListener = e -> {
            Message message = e.getMessage();
            if (message.getType() == Message.Type.CHAT || message.getType() == Message.Type.GROUPCHAT) {
                // Recipient clients MUST keep track of separate real-time messages on a per-contact basis
                // Participants that enable real-time text during group chat need to keep track of multiple concurrent real-time messages on a per-participant basis.
                Jid sender = message.getFrom();
                Jid trackingJid = message.getType() == Message.Type.CHAT ? sender.asBareJid() : sender;

                RealTimeText rtt = message.getExtension(RealTimeText.class);
                if (rtt != null && rtt.getSequence() != null) {
                    TrackingKey trackingKey = new TrackingKey(trackingJid, rtt.getId());
                    if (rtt.getEvent() == null || rtt.getEvent() == RealTimeText.Event.EDIT) {
                        // If the 'event' attribute is omitted, event="edit" is assumed as the default.
                        InboundRealTimeMessage realTimeMessage = realTimeMessageMap.get(trackingKey);
                        // Recipient clients must verify that the 'seq' attribute increments by 1 in consecutively received <rtt/> elements from the same sender.
                        if (realTimeMessage != null && realTimeMessage.getSequence() + 1 == rtt.getSequence()) {
                            // If 'seq' increments as expected, the Action Elements (e.g., text insertions and deletions) included with this element MUST be processed to modify the existing real-time message.
                            realTimeMessage.processActions(rtt.getActions(), true);
                        }
                    } else if (rtt.getEvent() == RealTimeText.Event.RESET) {
                        InboundRealTimeMessage realTimeMessage = realTimeMessageMap.get(trackingKey);
                        if (realTimeMessage != null) {
                            realTimeMessage.reset(rtt.getSequence(), rtt.getId());
                        } else {
                            realTimeMessage = new InboundRealTimeMessage(xmppSession, message.getFrom(), rtt.getSequence(), rtt.getId());
                            realTimeMessageMap.put(trackingKey, realTimeMessage);
                            XmppUtils.notifyEventListeners(inboundRealTimeMessageListeners, new RealTimeMessageEvent(RealTimeTextManager.this, realTimeMessage));
                        }
                        realTimeMessage.processActions(rtt.getActions(), false);
                    } else if (rtt.getEvent() == RealTimeText.Event.NEW) {
                        InboundRealTimeMessage realTimeMessage = new InboundRealTimeMessage(xmppSession, message.getFrom(), rtt.getSequence(), rtt.getId());
                        InboundRealTimeMessage oldRealTimeMessage = realTimeMessageMap.put(trackingKey, realTimeMessage);
                        if (oldRealTimeMessage != null) {
                            oldRealTimeMessage.complete();
                        }
                        XmppUtils.notifyEventListeners(inboundRealTimeMessageListeners, new RealTimeMessageEvent(RealTimeTextManager.this, realTimeMessage));
                        realTimeMessage.processActions(rtt.getActions(), false);
                    } else if (rtt.getEvent() == RealTimeText.Event.CANCEL) {
                        XmppUtils.notifyEventListeners(realTimeTextActivationListeners, new RealTimeTextActivationEvent(RealTimeTextManager.this, sender, false));
                    } else if (rtt.getEvent() == RealTimeText.Event.INIT) {
                        XmppUtils.notifyEventListeners(realTimeTextActivationListeners, new RealTimeTextActivationEvent(RealTimeTextManager.this, sender, true));
                    }
                }
                if (message.getBody() != null) {
                    Replace replace = message.getExtension(Replace.class);
                    String id = replace != null ? replace.getId() : null;
                    TrackingKey trackingKey = new TrackingKey(trackingJid, id);
                    InboundRealTimeMessage realTimeMessage = realTimeMessageMap.remove(trackingKey);
                    if (realTimeMessage != null) {
                        realTimeMessage.complete();
                    }
                }
            }
        };
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        xmppSession.addInboundMessageListener(messageListener);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        xmppSession.removeInboundMessageListener(messageListener);
    }

    /**
     * Creates a new real-time message for sending real-time text.
     * This method is intended for creating a new message, when editing an existing message, when used in concert with <a href="http://www.xmpp.org/extensions/xep-0308.html">XEP-0308: Last Message Correction</a>.
     *
     * @param chat The chat to send real-time text with.
     * @param id   The id of the message, which is edited with a real-time message.
     * @return The real-time message.
     * @see rocks.xmpp.extensions.messagecorrect.model.Replace
     */
    public final OutboundRealTimeMessage createRealTimeMessage(Chat chat, String id) {
        return new OutboundRealTimeMessage(chat, id, 700, 10000);
    }

    /**
     * Creates a new real-time message for sending real-time text.
     *
     * @param chat The chat to send real-time text with.
     * @return The real-time message.
     */
    public final OutboundRealTimeMessage createRealTimeMessage(Chat chat) {
        return createRealTimeMessage(chat, null);
    }

    /**
     * Adds a real-time message listener, which allows to listen for new inbound real-time messages.
     *
     * @param realTimeMessageListener The listener.
     */
    public final void addRealTimeMessageListener(Consumer<RealTimeMessageEvent> realTimeMessageListener) {
        inboundRealTimeMessageListeners.add(realTimeMessageListener);
    }

    /**
     * Adds a real-time text listener, which allows to listen for real-time text.
     *
     * @param realTimeTextListener The listener.
     * @see #removeRealTimeTextActivationListener(Consumer)
     */
    public final void addRealTimeTextActivationListener(Consumer<RealTimeTextActivationEvent> realTimeTextListener) {
        realTimeTextActivationListeners.add(realTimeTextListener);
    }

    /**
     * Removes a previously added real-time text listener.
     *
     * @param realTimeTextListener The listener.
     * @see #addRealTimeTextActivationListener(Consumer)
     */
    public final void removeRealTimeTextActivationListener(Consumer<RealTimeTextActivationEvent> realTimeTextListener) {
        realTimeTextActivationListeners.remove(realTimeTextListener);
    }

    /**
     * Activates real-time text for a chat session.
     *
     * @param chat The chat.
     * @see <a href="https://xmpp.org/extensions/xep-0301.html#activating_realtime_text">6.1 Activating Real-Time Text</a>
     */
    public final void activate(Chat chat) {
        Message message = new Message();
        RealTimeText realTimeText = new RealTimeText(RealTimeText.Event.INIT, Collections.emptyList(), 0, null);
        message.addExtension(realTimeText);
        chat.sendMessage(message);
    }

    /**
     * Deactivates real-time text for a chat session.
     *
     * @param chat The chat.
     * @see <a href="https://xmpp.org/extensions/xep-0301.html#deactivating_realtime_text">6.2 Deactivating Real-Time Text</a>
     */
    public final void deactivate(Chat chat) {
        Message message = new Message();
        RealTimeText realTimeText = new RealTimeText(RealTimeText.Event.CANCEL, Collections.emptyList(), 0, null);
        message.addExtension(realTimeText);
        chat.sendMessage(message);
    }

    private static final class TrackingKey {

        private final Jid sender;

        private final String id;

        private TrackingKey(Jid sender, String id) {
            this.sender = sender;
            this.id = id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(sender, id);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof TrackingKey)) {
                return false;
            }
            TrackingKey other = (TrackingKey) o;

            return Objects.equals(sender, other.sender)
                    && Objects.equals(id, other.id);
        }
    }
}
