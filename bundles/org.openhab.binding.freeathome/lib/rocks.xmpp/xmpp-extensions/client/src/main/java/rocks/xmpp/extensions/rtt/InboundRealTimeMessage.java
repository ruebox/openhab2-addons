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
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.extensions.rtt.model.RealTimeText;
import rocks.xmpp.util.XmppUtils;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * An inbound real-time message.
 *
 * @author Christian Schudt
 */
public final class InboundRealTimeMessage extends RealTimeMessage {

    private static final ExecutorService PROCESS_ACTIONS_EXECUTOR = Executors.newCachedThreadPool(XmppUtils.createNamedThreadFactory("Real-time Text Processing Thread"));

    private final StringBuilder sb;

    private final BlockingQueue<RealTimeText.Action> actions = new LinkedBlockingQueue<>();

    private final Jid from;

    private final Set<Consumer<RealTimeTextChangeEvent>> textChangeListeners = new CopyOnWriteArraySet<>();

    InboundRealTimeMessage(XmppSession xmppSession, Jid contact, int sequence, String id) {
        this.from = contact;
        this.sequence.set(sequence);
        this.sb = new StringBuilder();
        this.id = id;

        if (xmppSession != null) {
            PROCESS_ACTIONS_EXECUTOR.execute(() -> {
                        try {
                            RealTimeText.Action action;
                            // Periodically poll for new action elements until the message is complete.
                            while ((action = actions.poll(700, TimeUnit.MILLISECONDS)) != null || !complete) {
                                if (action != null) {
                                    if (action instanceof RealTimeText.WaitInterval) {
                                        Long ms = ((RealTimeText.WaitInterval) action).getMilliSeconds();
                                        if (ms != null) {
                                            if (ms == Integer.MIN_VALUE) {
                                                // "Poison" element to break the blocking queue immediately.
                                                break;
                                            }
                                            // Wait the amount of ms, until it's waken up by new incoming RTT actions.
                                            // See 7.4 Receiving Real-Time Text
                                            synchronized (actions) {
                                                actions.wait(ms);
                                            }
                                        }
                                    } else {
                                        applyActionElement(action);
                                    }
                                }
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
            );
        }
    }

    /**
     * Processes a list of incoming action elements by adding them to a queue. Later, they will be taken from the queue and applied to the current text.
     *
     * @param actions           The actions.
     * @param incrementSequence Whether to increment the current sequence.
     */

    void processActions(Collection<RealTimeText.Action> actions, boolean incrementSequence) {
        if (isComplete()) {
            throw new IllegalStateException("Real-time message is already completed.");
        }
        if (incrementSequence) {
            sequence.getAndIncrement();
        }
        this.actions.addAll(actions);
        synchronized (this.actions) {
            // Wake up the waiting thread, which processes the queue.
            // This is to ensure that new actions are processed immediately, in case if there's still a wait from a previous waiting element.
            this.actions.notifyAll();
        }
    }

    /**
     * Applies an action to the current text.
     *
     * @param action The action.
     */
    void applyActionElement(RealTimeText.Action action) {
        String s;
        synchronized (this) {
            if (action instanceof RealTimeText.InsertText) {
                RealTimeText.InsertText insertText = (RealTimeText.InsertText) action;
                if (insertText.getText() != null) {
                    int i = 0, charCount = 0;
                    int pos = normalizePosition(insertText.getPosition());
                    // Iterate over the code points and sum up the char count, which is used by StringBuilder.insert()
                    // RTT counts with code points, while Java APIs counts with character indexes.
                    while (i < sb.codePointCount(0, sb.length()) && i < pos) {
                        charCount += Character.charCount(sb.codePointAt(charCount));
                        i++;
                    }
                    sb.insert(charCount, insertText.getText());
                }
            } else if (action instanceof RealTimeText.EraseText) {
                RealTimeText.EraseText eraseText = (RealTimeText.EraseText) action;
                Integer n = eraseText.getNumberOfCharacters();
                // If 'n' is omitted, the default value of 'n' MUST be “1”.
                if (n == null) {
                    n = 1;
                }
                if (n < 0) {
                    n = 0;
                }
                int i = 0, endIndex = 0, startIndex = 0;
                int pos = normalizePosition(eraseText.getPosition());
                // Iterate over the code points and sum up the char count, which is used by StringBuilder.delete()
                // RTT counts with code points, while Java APIs counts with character indexes.
                while (i < sb.codePointCount(0, sb.length()) && i < pos) {
                    if (i++ < pos - n) {
                        startIndex += Character.charCount(sb.codePointAt(startIndex));
                    }
                    endIndex += Character.charCount(sb.codePointAt(endIndex));
                }
                // Protect against faulty <e/> elements.
                if (startIndex <= endIndex && startIndex <= sb.length()) {
                    sb.delete(startIndex, endIndex);
                }
            }
            s = sb.toString();
        }
        XmppUtils.notifyEventListeners(textChangeListeners, new RealTimeTextChangeEvent(this, s));
    }

    private int normalizePosition(Integer pos) {
        // If 'p' is omitted, the default value of 'p' MUST point to the end of the message (i.e., 'p' is set to the current length of the real-time message).
        if (pos == null) {
            pos = sb.length();
        }
        // However, recipients receiving such values MUST clip negative values to “0”, and clip excessively high 'p' values to the current length of the real-time message.
        if (pos < 0) {
            pos = 0;
        }
        if (pos > sb.length()) {
            pos = sb.length();
        }
        return pos;
    }

    /**
     * Resets the message and sets the new sequence.
     *
     * @param sequence The sequence.
     */
    synchronized void reset(int sequence, String id) {
        this.sequence.set(sequence);
        this.sb.setLength(0);
        this.id = id;
        this.actions.clear();
    }

    /**
     * Completes the message.
     */
    synchronized void complete() {
        if (!complete) {
            complete = true;
            // Add a "poison" element to break the blocking queue immediately.
            actions.offer(new RealTimeText.WaitInterval(Integer.MIN_VALUE));
            textChangeListeners.clear();
        }
    }

    @Override
    public final synchronized String getText() {
        return sb.toString();
    }

    /**
     * Gets the sender of the real-time message.
     *
     * @return The sender.
     */
    public final Jid getFrom() {
        return from;
    }

    /**
     * Adds a text change listener, which allows to listen for text changes.
     *
     * @param realTimeTextListener The listener.
     */
    public final void addRealTimeTextChangeListener(Consumer<RealTimeTextChangeEvent> realTimeTextListener) {
        textChangeListeners.add(realTimeTextListener);
    }

    /**
     * Removes a previously added text change listener.
     *
     * @param realTimeTextListener The listener.
     */
    public final void removeRealTimeTextChangeListener(Consumer<RealTimeTextChangeEvent> realTimeTextListener) {
        textChangeListeners.remove(realTimeTextListener);
    }
}
