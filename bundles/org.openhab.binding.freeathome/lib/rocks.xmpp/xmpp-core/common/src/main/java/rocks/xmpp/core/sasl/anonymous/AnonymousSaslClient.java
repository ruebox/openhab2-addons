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

package rocks.xmpp.core.sasl.anonymous;

import javax.security.sasl.SaslClient;

/**
 * The client implementation of the "ANONYMOUS" SASL mechanism.
 *
 * @author Christian Schudt
 * @see <a href="http://tools.ietf.org/html/rfc4505">Anonymous Simple Authentication and Security Layer (SASL) Mechanism</a>
 */
public final class AnonymousSaslClient implements SaslClient {
    @Override
    public final String getMechanismName() {
        return "ANONYMOUS";
    }

    @Override
    public final boolean hasInitialResponse() {
        return true;
    }

    @Override
    public final byte[] evaluateChallenge(byte[] challenge) {
        return new byte[0];
    }

    @Override
    public final boolean isComplete() {
        return true;
    }

    @Override
    public final byte[] unwrap(byte[] incoming, int offset, int len) {
        return new byte[0];
    }

    @Override
    public final byte[] wrap(byte[] outgoing, int offset, int len) {
        return new byte[0];
    }

    @Override
    public final Object getNegotiatedProperty(String propName) {
        return null;
    }

    @Override
    public final void dispose() {
    }
}
