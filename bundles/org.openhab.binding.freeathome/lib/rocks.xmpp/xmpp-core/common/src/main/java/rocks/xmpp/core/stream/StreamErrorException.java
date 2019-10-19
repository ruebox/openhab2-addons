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

package rocks.xmpp.core.stream;

import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.stream.model.StreamError;
import rocks.xmpp.core.stream.model.errors.Condition;

/**
 * Represents a stream error.
 *
 * @author Christian Schudt
 * @see StreamError
 * @see <a href="https://xmpp.org/rfcs/rfc6120.html#streams-error">4.9.  Stream Errors</a>
 * @deprecated
 */
@Deprecated
public class StreamErrorException extends XmppException {

    private static final long serialVersionUID = -6169260329712442144L;

    private final StreamError streamError;

    /**
     * Constructs a stanza exception.
     *
     * @param streamError The underlying stream error.
     */
    public StreamErrorException(StreamError streamError) {
        this(streamError, null);
    }

    /**
     * Constructs a stream error exception with a cause.
     *
     * @param streamError The underlying stream error.
     * @param cause       The cause.
     */
    public StreamErrorException(StreamError streamError, Throwable cause) {
        super(streamError.toString(), cause);
        this.streamError = streamError;
    }

    /**
     * Gets the stream error.
     *
     * @return The stream error.
     */
    public final StreamError getError() {
        return streamError;
    }

    /**
     * Gets the defined error condition. If the condition is unknown, {@link Condition#UNDEFINED_CONDITION} is returned.
     * This is a shortcut for {@code getError().getCondition()}.
     *
     * @return The error condition.
     * @see <a href="https://xmpp.org/rfcs/rfc6120.html#streams-error-conditions">4.9.3.  Defined Stream Error Conditions</a>
     */
    public final Condition getCondition() {
        return streamError.getCondition();
    }
}