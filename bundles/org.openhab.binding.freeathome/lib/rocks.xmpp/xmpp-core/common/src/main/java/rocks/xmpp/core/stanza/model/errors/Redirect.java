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
import java.util.Objects;

/**
 * The implementation of the {@code <redirect/>} stanza error.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions-redirect">8.3.3.14.  redirect</a></cite></p>
 * <p>The recipient or server is redirecting requests for this information to another entity, typically in a temporary fashion (as opposed to the {@code <gone/>} error condition, which is used for permanent addressing failures); the associated error type SHOULD be "modify" and the error stanza SHOULD contain the alternate address in the XML character data of the {@code <redirect/>} element (which MUST be a URI or IRI with which the sender can communicate, typically an XMPP IRI as specified in [XMPP-URI]).</p>
 * </blockquote>
 *
 * @see #redirect(String)
 */
@XmlRootElement
public final class Redirect extends Condition {

    Redirect() {
    }

    Redirect(String alternateAddress) {
        super(Objects.requireNonNull(alternateAddress));
    }

    /**
     * Gets the alternate address.
     *
     * @return The alternate address.
     */
    public final String getAlternateAddress() {
        return value;
    }
}
