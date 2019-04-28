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

package rocks.xmpp.core.sasl;

import rocks.xmpp.core.sasl.model.Failure;
import rocks.xmpp.core.stream.StreamNegotiationException;

/**
 * An exception thrown during the login process, if SASL negotiation with the XMPP server fails.
 * <p>
 * Typical error cases are invalid username and/or password in which case the {@linkplain #getCondition() failure condition} is {@link rocks.xmpp.core.sasl.model.Failure.Condition#NOT_AUTHORIZED}.
 * <p>
 * If you want to know the exact failure case, ask for the condition as shown below:
 * ```java
 * catch (AuthenticationException e) {
 *     if (e.getCondition() == Failure.Condition.NOT_AUTHORIZED) {
 *         //...
 *     }
 * }
 * ```
 *
 * @author Christian Schudt
 */
public final class AuthenticationException extends StreamNegotiationException {

    private final Failure.Condition condition;

    public AuthenticationException(String message) {
        this(message, null);
    }

    public AuthenticationException(String message, Failure failure) {
        super(message);
        this.condition = failure != null ? failure.getCondition() : null;
    }

    /**
     * The specific SASL error condition.
     *
     * @return The error condition.
     * @see <a href="https://xmpp.org/rfcs/rfc6120.html#sasl-errors">6.5.  SASL Errors</a>
     */
    public Failure.Condition getCondition() {
        return condition;
    }
}
