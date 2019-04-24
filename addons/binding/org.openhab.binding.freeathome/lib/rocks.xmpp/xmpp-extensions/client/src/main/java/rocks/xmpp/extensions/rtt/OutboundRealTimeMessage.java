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

import rocks.xmpp.core.session.SendTask;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.extensions.rtt.model.RealTimeText;
import rocks.xmpp.im.chat.Chat;
import rocks.xmpp.util.XmppUtils;
import rocks.xmpp.util.concurrent.QueuedScheduledExecutorService;

import java.text.Normalizer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * An outbound real-time message.
 *
 * @author Christian Schudt
 */
public final class OutboundRealTimeMessage extends RealTimeMessage {

    private static final ExecutorService TRANSMISSION_EXECUTOR = Executors.newCachedThreadPool(XmppUtils.createNamedThreadFactory("Real-time Text Transmission Thread"));

    private final Collection<RealTimeText.Action> actions = new ArrayDeque<>();

    private final Chat chat;

    private final ScheduledExecutorService transmissionExecutor;

    private CharSequence text;

    private ScheduledFuture<?> nextRefresh;

    private ScheduledFuture<?> nextTransmission;

    private long lastTextChange;

    /**
     * The message refresh SHOULD be transmitted at intervals during active typing or composing. The RECOMMENDED interval is 10 seconds.
     */
    private final long refreshInterval;

    /**
     * For the best balance between interoperability and usability, the default transmission interval of {@code <rtt/>} elements for a continuously-changing message SHOULD be approximately 700 milliseconds.
     */
    private final long transmissionInterval;

    private boolean isNew = true;

    /**
     * @param chat                 The chat.
     * @param id                   The message id.
     * @param transmissionInterval The message refresh SHOULD be transmitted at intervals during active typing or composing. The RECOMMENDED interval is 10 seconds.
     * @param refreshInterval      For the best balance between interoperability and usability, the default transmission interval of {@code <rtt/>} elements for a continuously-changing message SHOULD be approximately 700 milliseconds.
     */
    OutboundRealTimeMessage(Chat chat, String id, long transmissionInterval, long refreshInterval) {
        this.chat = chat;
        this.id = id;
        this.transmissionInterval = transmissionInterval;
        this.refreshInterval = refreshInterval;

        // Set up two executors, which periodically send RTT messages and "refresh messages".
        transmissionExecutor = new QueuedScheduledExecutorService(TRANSMISSION_EXECUTOR);

        // This executor periodically sends RTT messages in the preferred transmission interval.
        nextTransmission = transmissionExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                synchronized (OutboundRealTimeMessage.this) {
                    if (!actions.isEmpty()) {
                        // If these are the first actions being sent, schedule a refresh message.
                        if (isNew) {
                            OutboundRealTimeMessage.this.sequence.set(generateSequenceNumber());

                            // This executor periodically sends "message refreshes" (4.7.3 Message Refresh)
                            nextRefresh = transmissionExecutor.schedule(new Runnable() {
                                @Override
                                public void run() {
                                    synchronized (OutboundRealTimeMessage.this) {
                                        // To save bandwidth, message refreshes SHOULD NOT occur continuously while the sender is idle.
                                        if (System.currentTimeMillis() - lastTextChange < refreshInterval) {
                                            reset();
                                        }
                                        // Reschedule
                                        nextRefresh = transmissionExecutor.schedule(this, refreshInterval, TimeUnit.MILLISECONDS);
                                    }
                                }
                            }, refreshInterval, TimeUnit.MILLISECONDS);
                        }
                        // 2. During every Transmission Interval, all buffered action elements are transmitted in <rtt/> element in a <message/> stanza. This is equivalent to transmitting a small sequence of typing at a time.
                        sendRttMessage(isNew ? RealTimeText.Event.NEW : RealTimeText.Event.EDIT);
                        isNew = false;
                    }
                    nextTransmission = transmissionExecutor.schedule(this, transmissionInterval, TimeUnit.MILLISECONDS);
                    // 3. If there are no message changes occurring, no unnecessary transmission takes place.
                }
            }
        }, transmissionInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * Generates the starting value for the sequence number.
     * <blockquote>
     * <p>Sender clients MAY use any new starting value for 'seq' when initializing a real-time message using event="new" or event="reset". Recipient clients receiving such elements MUST use this 'seq' value as the new starting value. A random starting value is RECOMMENDED to improve reliability of Keeping Real-Time Text Synchronized during Usage with Multi-User Chat and Simultaneous Logins.</p>
     * </blockquote>
     *
     * @return The sequence number.
     */
    static int generateSequenceNumber() {
        return ThreadLocalRandom.current().nextInt(100000);
    }

    /**
     * Computes the action elements by comparing the old and the new text.
     *
     * @param oldText The old text.
     * @param newText The new text.
     * @return The actions.
     */
    static List<RealTimeText.Action> computeActionElements(CharSequence oldText, CharSequence newText) {
        if ((oldText == null && newText == null) || (oldText != null && oldText.equals(newText)) || (oldText == null && newText.length() == 0) || (newText == null && oldText.length() == 0)) {
            return Collections.emptyList();
        }
        List<RealTimeText.Action> actions = new ArrayList<>();
        if (oldText == null) {
            actions.add(new RealTimeText.InsertText(newText));
        } else if (newText == null) {
            actions.add(new RealTimeText.EraseText(oldText.length(), oldText.length()));
        } else {

            // In order to calculate what text changes took place, the first changed character and the last changed character are determined.
            int[] bounds = determineBounds(oldText, newText);
            int firstChangedCharacter = bounds[0];
            int lastChangedCharacter = bounds[1];
            int n = Character.codePointCount(oldText, firstChangedCharacter, lastChangedCharacter);
            if (n > 0) {
                actions.add(new RealTimeText.EraseText(n == 1 ? null : n, lastChangedCharacter == oldText.length() ? null : Character.codePointCount(oldText, 0, lastChangedCharacter)));
            }
            int endIndex = newText.length() - oldText.length() + lastChangedCharacter;
            if (endIndex > firstChangedCharacter) {
                actions.add(new RealTimeText.InsertText(newText.subSequence(firstChangedCharacter, endIndex), firstChangedCharacter == oldText.length() ? null : Character.codePointCount(oldText, 0, firstChangedCharacter)));
            }
        }
        return actions;
    }

    /**
     * Determines the first and last changed character of a string by comparing it to another string.
     *
     * @param oldText The old text.
     * @param newText The new text.
     * @return An array with two values containing the first and last changed character.
     */
    static int[] determineBounds(CharSequence oldText, CharSequence newText) {
        // In order to calculate what text changes took place, the first changed character and the last changed character are determined.
        int firstChangedCharacter = 0;
        while (firstChangedCharacter < oldText.length() && firstChangedCharacter < newText.length() && oldText.charAt(firstChangedCharacter) == newText.charAt(firstChangedCharacter)) {
            firstChangedCharacter++;
        }

        int lastChangedCharacter = 0;
        while (lastChangedCharacter < oldText.length()
                && lastChangedCharacter < newText.length()
                && firstChangedCharacter < newText.length()
                && firstChangedCharacter < oldText.length() - lastChangedCharacter
                && firstChangedCharacter < newText.length() - lastChangedCharacter
                && oldText.charAt(oldText.length() - 1 - lastChangedCharacter) == newText.charAt(newText.length() - 1 - lastChangedCharacter)) {
            lastChangedCharacter++;
        }
        return new int[]{firstChangedCharacter, oldText.length() - lastChangedCharacter};
    }

    /**
     * Updates the text. The passed text is the complete text of the text field / text area.
     * Action elements are computed automatically and are sent to the recipient.
     *
     * @param text The text.
     */
    public final synchronized void update(CharSequence text) {
        if (complete) {
            throw new IllegalStateException("Real-time message is already completed.");
        }
        // 1. Monitor for text changes in the sender’s message. Whenever a text change event occurs, compute action element(s) and append these action element(s) to a buffer.
        long now = System.currentTimeMillis();
        if (!actions.isEmpty() && now != lastTextChange) {
            actions.add(new RealTimeText.WaitInterval(now - lastTextChange));
        }
        this.lastTextChange = now;
        // Pre-processing before generating real-time text includes Unicode normalization,
        // conversion of emoticons graphics to text, removal of illegal characters, line-break conversion,
        // and any other necessary text modifications. For Unicode normalization,
        // sender clients SHOULD ensure the message is in Unicode Normalization Form C [14] ("NFC")
        // For the purpose of calculating Attribute Values, any line breaks MUST be treated as a single character.
        text = Normalizer.normalize(text, Normalizer.Form.NFC).replace("\r\n", "\n");
        actions.addAll(computeActionElements(this.text, text));
        this.text = text;
    }

    /**
     * Sends a message refresh. A new sequence id is generated and the current text is sent.
     * This method is usually called automatically in during the refresh interval.
     *
     * @see <a href="http://www.xmpp.org/extensions/xep-0301.html#message_refresh">4.7.3 Message Refresh</a>
     */
    public final synchronized void reset() {
        reset(null, text);
    }

    /**
     * Sends a message refresh, if you want to switch the message, which is being edited.
     * Use this method, if you are composing a new message and want to switch to another (previous) message.
     *
     * @param id   The message id for the message which is edited.
     * @param text The text to reset this message to.
     * @see <a href="http://www.xmpp.org/extensions/xep-0301.html#usage_with_last_message_correction">7.5.3 Usage with Last Message Correction</a>
     */
    public final synchronized void reset(String id, CharSequence text) {
        // Senders clients need to transmit a Message Refresh when transmitting <rtt/> for a different message than the previously transmitted <rtt/> (i.e., the value of the 'id' attribute changes, 'id' becomes included, or 'id' becomes not included). This keeps real-time text synchronized when beginning to edit a previously delivered message versus continuing to compose a new message.
        this.id = id;
        this.text = text;
        // Generate a new sequence number for every message refresh.
        this.sequence.set(generateSequenceNumber());
        // Drop every outgoing actions, which are scheduled for the next transmission interval, because we reset the whole text.
        actions.clear();
        actions.add(new RealTimeText.InsertText(text));
        sendRttMessage(RealTimeText.Event.RESET);
    }

    @Override
    public final synchronized String getText() {
        return text != null ? text.toString() : "";
    }

    /**
     * Commits the real-time message.
     *
     * @return The final message.
     */
    public final SendTask<Message> commit() {
        if (complete) {
            throw new IllegalStateException("Already committed.");
        }
        SendTask<Message> message = chat.sendMessage(getText());
        complete = true;
        synchronized (this) {
            if (nextRefresh != null) {
                nextRefresh.cancel(false);
            }
            if (nextTransmission != null) {
                nextTransmission.cancel(false);
            }
        }
        transmissionExecutor.shutdown();
        return message;
    }

    /**
     * Sends the RTT message.
     *
     * @param event The event type.
     */
    private void sendRttMessage(RealTimeText.Event event) {
        Message message = new Message();
        RealTimeText realTimeText = new RealTimeText(event, actions, this.sequence.getAndIncrement(), id);
        message.addExtension(realTimeText);
        chat.sendMessage(message);
        actions.clear();
    }

    /**
     * Gets the refresh interval, after which a refresh message is sent to ensure real-time text is kept in sync. The default is 10 seconds.
     *
     * @return The refresh interval.
     * @see <a href="https://xmpp.org/extensions/xep-0301.html#message_refresh">4.7.3 Message Refresh</a>
     */
    public final long getRefreshInterval() {
        return refreshInterval;
    }

    /**
     * Gets the transmission interval of real-time text. The default is 700 milliseconds.
     *
     * @return The refresh interval.
     * @see <a href="https://xmpp.org/extensions/xep-0301.html#transmission_interval">4.5 Transmission Interval</a>
     */
    public final long getTransmissionInterval() {
        return transmissionInterval;
    }
}
