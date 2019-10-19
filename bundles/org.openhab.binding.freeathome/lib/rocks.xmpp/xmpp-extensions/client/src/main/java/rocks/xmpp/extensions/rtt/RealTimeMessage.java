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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The base class for real-time messages. Derived classes are {@link InboundRealTimeMessage} and {@link OutboundRealTimeMessage}.
 *
 * @author Christian Schudt
 */
abstract class RealTimeMessage {

    volatile boolean complete;

    final AtomicInteger sequence = new AtomicInteger();

    protected String id;

    /**
     * Indicates whether this real-time message is complete.
     *
     * @return True, if the message is complete.
     */
    public final boolean isComplete() {
        return complete;
    }

    /**
     * Gets the current text.
     *
     * @return The text.
     */
    public abstract String getText();

    /**
     * Gets the sequence number of this real-time message.
     *
     * @return The sequence number.
     */
    public final int getSequence() {
        return sequence.get();
    }

    public final synchronized String getId() {
        return id;
    }

    @Override
    public final String toString() {
        return getText();
    }
}
