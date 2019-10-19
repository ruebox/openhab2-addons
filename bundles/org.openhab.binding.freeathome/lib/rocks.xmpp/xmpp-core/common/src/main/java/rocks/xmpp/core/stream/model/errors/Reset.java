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

package rocks.xmpp.core.stream.model.errors;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The implementation of the {@code <reset/>} stream error.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#streams-error-conditions-reset">4.9.3.16.  reset</a></cite></p>
 * <p>The server is closing the stream because it has new (typically security-critical) features to offer, because the keys or certificates used to establish a secure context for the stream have expired or have been revoked during the life of the stream (Section 13.7.2.3), because the TLS sequence number has wrapped (Section 5.3.5), etc. The reset applies to the stream and to any security context established for that stream (e.g., via TLS and SASL), which means that encryption and authentication need to be negotiated again for the new stream (e.g., TLS session resumption cannot be used).</p>
 * </blockquote>
 * This class is a singleton.
 *
 * @see #RESET
 */
@XmlRootElement
@XmlType(factoryMethod = "create")
final class Reset extends Condition {

    Reset() {
    }

    private static Reset create() {
        return (Reset) RESET;
    }
}
