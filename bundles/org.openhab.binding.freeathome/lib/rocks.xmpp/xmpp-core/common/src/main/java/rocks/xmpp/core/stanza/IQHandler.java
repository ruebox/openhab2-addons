/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Christian Schudt
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

import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.IQ.Type;

/**
 * Handles an inbound IQ request, (IQ stanzas of type {@link Type#GET} or {@link Type#SET}) by processing the request and returning an IQ response of type {@link Type#RESULT result} or {@link Type#ERROR error}.
 * <p>
 * <blockquote>
 * <cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-semantics-iq">8.2.3.  IQ Semantics</a></cite>
 * <p>An entity that receives an IQ request of type "get" or "set" MUST reply with an IQ response of type "result" or "error". The response MUST preserve the 'id' attribute of the request (or be empty if the generated stanza did not include an 'id' attribute).</p>
 * </blockquote>
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-semantics-iq">8.2.3.  IQ Semantics</a>
 */
@FunctionalInterface
public interface IQHandler {

    /**
     * Handles an inbound IQ stanza of type {@link Type#GET get} or {@link Type#SET set}.
     * <p>
     * The returned IQ must be of type {@link Type#RESULT result} or {@link Type#ERROR error}.
     * If <code>null</code> is returned, no response is returned to the requester and you must take responsibility of sending a response manually. However, this approach is not recommended.
     * <p>
     * Use {@link IQ#createResult()} or {@link IQ#createError(rocks.xmpp.core.stanza.model.StanzaError)} to generate the response IQ (i.e. an IQ with the same id).
     *
     * @param iq The inbound IQ stanza.
     * @return The result or error IQ, which is the response to sending entity.
     */
    IQ handleRequest(IQ iq);
}
