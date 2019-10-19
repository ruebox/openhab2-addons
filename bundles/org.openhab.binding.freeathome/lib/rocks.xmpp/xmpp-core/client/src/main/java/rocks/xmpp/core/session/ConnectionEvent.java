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

package rocks.xmpp.core.session;

import java.time.Duration;
import java.util.EventObject;
import java.util.function.Consumer;

/**
 * This event is fired for the following use cases:
 * <ul>
 * <li>When the XMPP session's underlying connection gets {@linkplain Type#DISCONNECTED disconnected}.</li>
 * <li>When the automatic reconnection has successfully {@linkplain Type#RECONNECTION_SUCCEEDED reconnected}, i.e. re-established the previous session status.</li>
 * <li>While the XMPP session is disconnected, a {@linkplain Type#RECONNECTION_PENDING pending} event is fired every second.</li>
 * <li>When the reconnection {@linkplain Type#RECONNECTION_FAILED failed}.</li>
 * </ul>
 *
 * @author Christian Schudt
 * @see XmppSession#addConnectionListener(Consumer)
 */
public final class ConnectionEvent extends EventObject {

    private final Duration nextReconnectionAttempt;

    private final Type type;

    private final Throwable cause;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    ConnectionEvent(XmppSession source, Type type, Throwable cause, Duration duration) {
        super(source);
        this.cause = cause;
        this.type = type;
        this.nextReconnectionAttempt = duration;
    }

    /**
     * Gets the cause of the disconnection or reconnection failure. Returns null for the {@link Type#RECONNECTION_SUCCEEDED} type.
     *
     * @return The cause of the disconnection or null.
     */
    public final Throwable getCause() {
        return cause;
    }

    /**
     * Gets the duration until the next reconnection is attempted. This method should only be used in conjunction with the type {@link Type#RECONNECTION_PENDING}.
     * Otherwise {@link Duration#ZERO} is returned.
     *
     * @return The duration until the next reconnection is attempted.
     */
    public final Duration getNextReconnectionAttempt() {
        return nextReconnectionAttempt;
    }

    /**
     * Gets the event type.
     *
     * @return The event type.
     */
    public final Type getType() {
        return type;
    }

    @Override
    public final String toString() {
        return getClass().getName() + "[type=" + type + ", nextReconnectionAttempt=" + nextReconnectionAttempt + ']';
    }

    /**
     * The connection event type.
     */
    public enum Type {
        /**
         * The connection is disconnected.
         */
        DISCONNECTED,
        /**
         * The reconnection has failed.
         */
        RECONNECTION_FAILED,
        /**
         * The reconnection has succeeded.
         */
        RECONNECTION_SUCCEEDED,
        /**
         * The reconnection is pending.
         */
        RECONNECTION_PENDING
    }
}
