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

package rocks.xmpp.core.session;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.stanza.model.IQ;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A predicate which checks for valid IQ responses to a IQ request.
 * <p>
 * An IQ response matches to an IQ request if:
 * <ol>
 * <li>the response IQ is of type 'result' or 'error'</li>
 * <li>both stanza IDs of request and response are equal</li>
 * <li>the request's 'to' attribute exactly matches the response's 'from' attribute, unless</li>
 * <ul>
 * <li>the response's 'from' is not set and the request's 'to' was the user's bare JID</li>
 * <li>or the response is from the user's account (bare JID) or from the server itself.</li>
 * </ul>
 * </ol>
 * Checking the 'from' attribute in addition to the to stanza id is recommended, because an attacker could spoof an IQ response when guessing the correct stanza id.
 *
 * @author Christian Schudt
 */
final class IQResponsePredicate implements Predicate<IQ> {

    private final Jid to;

    private final String id;

    private final Jid connectedResource;

    /**
     * @param iq                The IQ request
     * @param connectedResource The connected resource.
     */
    IQResponsePredicate(final IQ iq, final Jid connectedResource) {
        if (!iq.isRequest()) {
            throw new IllegalArgumentException("IQ must be of type 'get' or 'set'");
        }
        this.to = iq.getTo();
        this.id = iq.getId();
        this.connectedResource = connectedResource;
    }

    @Override
    public final boolean test(final IQ responseIQ) {
        // Must be of type 'result' or 'error'
        return responseIQ.isResponse()
                // and 'id' must be set and be equal the original id
                && responseIQ.getId() != null && responseIQ.getId().equals(id)
                // and 'to' and 'from' must match exactly
                && (
                Objects.equals(to, responseIQ.getFrom())
                        // unless the 'from' is null and the 'to' was the user's bare JID
                        || (responseIQ.getFrom() == null && (connectedResource == null || to.equals(connectedResource.asBareJid())))
                        // or the 'to' is null and the response is from the user's account or from the server
                        || (to == null && (connectedResource == null || responseIQ.getFrom().asBareJid().equals(connectedResource.asBareJid()) || responseIQ.getFrom().asBareJid().getDomain().equals(connectedResource.getDomain())))
        );
    }
}
