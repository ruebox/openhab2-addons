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

package rocks.xmpp.extensions.httpbind;

import rocks.xmpp.core.XmppException;
import rocks.xmpp.extensions.httpbind.model.Body;

import java.net.URI;

/**
 * A BOSH exception is thrown when the BOSH connection manager returned an error condition or the HTTP request responded with an HTTP error code.
 *
 * @author Christian Schudt
 * @see rocks.xmpp.extensions.httpbind.model.Body.Condition
 */
public final class BoshException extends XmppException {
    private final Body.Condition condition;

    private URI uri;

    private int httpErrorCode;

    BoshException(Body.Condition condition) {
        this(condition, null);
    }

    BoshException(Body.Condition condition, int httpErrorCode) {
        super("The connection was terminated due to HTTP error " + httpErrorCode);
        this.condition = condition;
        this.httpErrorCode = httpErrorCode;
    }

    BoshException(Body.Condition condition, URI uri) {
        super("The connection was terminated with condition: " + (condition != null ? condition.toString() : Body.Condition.UNDEFINED_CONDITION.toString()));
        this.uri = uri;
        this.condition = condition;
    }

    /**
     * Gets the BOSH error condition.
     *
     * @return The BOSH error condition.
     */
    public final Body.Condition getCondition() {
        return condition;
    }

    /**
     * Gets the URI in case of a "see-other-uri" error.
     *
     * @return The URI.
     */
    public final URI getUri() {
        return uri;
    }

    /**
     * Gets the HTTP error code.
     *
     * @return The HTTP error code.
     */
    public final int getHttpErrorCode() {
        return httpErrorCode;
    }
}
