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

package rocks.xmpp.core.stanza.model.errors;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The implementation of the {@code <gone/>} stanza error.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-gone">8.3.3.5.  gone</a></cite></p>
 * <p>The recipient or server can no longer be contacted at this address, typically on a permanent basis (as opposed to the {@code <redirect/>} error condition, which is used for temporary addressing failures); the associated error type SHOULD be "cancel" and the error stanza SHOULD include a new address (if available) as the XML character data of the {@code <gone/>} element (which MUST be a Uniform Resource Identifier [URI] or Internationalized Resource Identifier [IRI] at which the entity can be contacted, typically an XMPP IRI as specified in [XMPP-URI]).</p>
 * </blockquote>
 *
 * @see #gone(String)
 */
@XmlRootElement
public final class Gone extends Condition {

    Gone() {
    }

    Gone(String newAddress) {
        super(newAddress);
    }

    /**
     * Gets the new address.
     *
     * @return The new address.
     */
    public final String getNewAddress() {
        return value;
    }
}
