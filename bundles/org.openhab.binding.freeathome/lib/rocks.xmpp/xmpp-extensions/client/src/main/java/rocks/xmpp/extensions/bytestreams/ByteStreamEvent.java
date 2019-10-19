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

package rocks.xmpp.extensions.bytestreams;

import rocks.xmpp.util.concurrent.AsyncResult;

import java.util.EventObject;
import java.util.function.Consumer;

/**
 * A byte stream event, which notifies a listener about inbound byte stream requests.
 * <p>
 * In order to accept an inbound byte stream, use {@link #accept()}, which will return a {@link rocks.xmpp.extensions.bytestreams.ByteStreamSession}.
 * To reject the byte stream, use {@link #reject()}, which will return a {@code <not-acceptable/>} error to the sender.
 *
 * @author Christian Schudt
 * @see ByteStreamManager#addByteStreamListener(Consumer)
 */
public abstract class ByteStreamEvent extends EventObject {

    private final String sessionId;

    /**
     * Constructs a byte stream event.
     *
     * @param source    The object on which the Event initially occurred.
     * @param sessionId The session id.
     * @throws IllegalArgumentException if source is null.
     */
    protected ByteStreamEvent(Object source, String sessionId) {
        super(source);
        this.sessionId = sessionId;
    }

    /**
     * Gets the session id.
     *
     * @return The session id.
     */
    public final String getSessionId() {
        return sessionId;
    }

    /**
     * Accepts the session.
     *
     * @return The byte stream session.
     */
    public abstract AsyncResult<ByteStreamSession> accept();

    /**
     * Rejects the session.
     */
    public abstract void reject();
}
