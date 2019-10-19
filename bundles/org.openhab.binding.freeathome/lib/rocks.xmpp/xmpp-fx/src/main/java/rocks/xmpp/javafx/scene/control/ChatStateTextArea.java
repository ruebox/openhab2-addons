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

package rocks.xmpp.javafx.scene.control;

import javafx.animation.PauseTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TextArea;
import javafx.util.Duration;
import rocks.xmpp.extensions.chatstates.model.ChatState;

/**
 * A text area which is automatically sets a chat state and therefore is useful for a chat applications.
 * <h3>How it works</h3>
 * Initially the text area is in {@linkplain ChatState#INACTIVE inactive} state. When receiving focus, the chat state transitions to {@linkplain ChatState#ACTIVE active}.
 * As soon as you start typing, it transitions to {@linkplain ChatState#COMPOSING composing}
 * and will automatically transition to {@linkplain ChatState#PAUSED paused} after a specified {@linkplain #delayProperty() delay} (which is 3 seconds by default), if you pause typing.
 * <p>
 * If you clear the text area, the state becomes {@linkplain ChatState#ACTIVE active} again, after the specified delay.
 * <p>
 * When losing focus, the state will become {@linkplain ChatState#INACTIVE inactive} after the delay (which means, if you refocus before the delay time is up, it will stay active).
 * <p>
 * Clearing the text area immediately sets the state to {@linkplain ChatState#INACTIVE active} (if focused) or {@linkplain ChatState#INACTIVE inactive} (if not focused).
 *
 * @author Christian Schudt
 * @see ChatState
 * @see <a href="https://xmpp.org/extensions/xep-0085.html">XEP-0085: Chat State Notifications</a>
 */
public class ChatStateTextArea extends TextArea {

    private final PauseTransition pauseTransition = new PauseTransition(Duration.seconds(3));

    private final ReadOnlyObjectWrapper<ChatState> chatState = new ReadOnlyObjectWrapper<>();

    public ChatStateTextArea() {
        // This is the initial state.
        chatState.set(ChatState.INACTIVE);

        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (getText().isEmpty()) {
                    // If we have received focus in an empty text field, immediately transition to "active".
                    chatState.set(ChatState.ACTIVE);
                    pauseTransition.stop();
                } else {
                    // If we have received focus in an non-empty text field, transition to "paused".
                    chatState.set(ChatState.PAUSED);
                    // Start the timer, which will automatically transition to the next state.
                    pauseTransition.playFromStart();
                }
            } else {
                pauseTransition.playFromStart();
            }
        });

        textProperty().addListener((observable, oldValue, newValue) -> {
            if (isFocused()) {
                // We are in "composing" state.
                chatState.set(ChatState.COMPOSING);
            }
            // Restart the timer.
            pauseTransition.playFromStart();
        });

        pauseTransition.setOnFinished(e -> {
            // When the time is up, switch to "paused", if there's any text, otherwise to active.
            if (isFocused()) {
                if (getText() != null && !getText().isEmpty()) {
                    chatState.set(ChatState.PAUSED);
                } else {
                    chatState.set(ChatState.ACTIVE);
                }
            } else {
                chatState.set(ChatState.INACTIVE);
            }
        });
    }

    @Override
    public void clear() {
        super.clear();
        chatState.set(isFocused() ? ChatState.ACTIVE : ChatState.INACTIVE);
    }

    /**
     * Gets the chat state property.
     *
     * @return The chat state property.
     */
    public final ReadOnlyObjectProperty<ChatState> chatStateProperty() {
        return chatState.getReadOnlyProperty();
    }

    /**
     * Gets the chat state.
     *
     * @return The chat state.
     */
    public final ChatState getChatState() {
        return chatState.get();
    }

    /**
     * Gets the delay property. It specifies the delay for transitioning from {@linkplain ChatState#COMPOSING composing} to {@linkplain ChatState#PAUSED paused} or {@linkplain ChatState#ACTIVE active} after having stopped composing.
     *
     * @return The delay property.
     */
    public final ObjectProperty<Duration> delayProperty() {
        return pauseTransition.durationProperty();
    }

    /**
     * Gets the delay for transitioning from {@linkplain ChatState#COMPOSING composing} to {@linkplain ChatState#PAUSED paused} or {@linkplain ChatState#ACTIVE active} after having stopped composing.
     *
     * @return The delay.
     */
    public final Duration getDelay() {
        return pauseTransition.getDuration();
    }

    /**
     * Sets the delay for transitioning from {@link ChatState#COMPOSING} to {@link ChatState#PAUSED} or {@link ChatState#ACTIVE} after having stopped composing.
     *
     * @param duration The pause duration.
     * @see #delayProperty()
     */
    public final void setDelay(Duration duration) {
        pauseTransition.setDuration(duration);
    }
}
