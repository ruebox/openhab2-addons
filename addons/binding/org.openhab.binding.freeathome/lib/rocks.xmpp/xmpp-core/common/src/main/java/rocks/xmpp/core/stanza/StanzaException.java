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

package rocks.xmpp.core.stanza;

import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.stanza.model.Stanza;
import rocks.xmpp.core.stanza.model.StanzaError;
import rocks.xmpp.core.stanza.model.errors.Condition;

/**
 * A stanza exception represents a {@linkplain StanzaError stanza error}.
 * It should be thrown, if a request (e.g. an IQ stanza) returned a stanza error.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error">8.3.  Stanza Errors</a>
 * @deprecated Use {@link rocks.xmpp.core.stanza.model.StanzaErrorException}
 */
@Deprecated
public class StanzaException extends XmppException {

    private final Stanza stanza;

    /**
     * Constructs a stanza exception.
     *
     * @param stanza The underlying stanza.
     */
    public StanzaException(Stanza stanza) {
        super(stanza.getError().toString());
        this.stanza = stanza;
    }

    /**
     * Gets the stanza, which includes the error.
     *
     * @return The stanza.
     */
    public final Stanza getStanza() {
        return stanza;
    }

    /**
     * Gets the stanza error, i.e. the {@code <error/>} element.
     *
     * @return The stanza error.
     * @see <a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error">8.3.  Stanza Errors</a>
     */
    public final StanzaError getError() {
        return stanza.getError();
    }

    /**
     * Gets the defined error condition. If the condition is unknown, {@link Condition#UNDEFINED_CONDITION} is returned.
     * This is a shortcut for {@code getError().getCondition()}.
     *
     * @return The error condition.
     * @see <a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error-conditions">8.3.3.  Defined Conditions</a>
     */
    public final Condition getCondition() {
        return stanza.getError().getCondition();
    }
}