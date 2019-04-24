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
import rocks.xmpp.core.stanza.model.Message;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A predicate which checks for valid message responses.
 * <p>
 * An message response must meet the following criteria:
 * <ol>
 * <li>both stanza IDs of request and response are equal</li>
 * <li>the request's 'to' bare JID matches the response's 'from' bare JID, unless</li>
 * <ul>
 * <li>the response's 'from' is not set and the request's 'to' was the user's bare JID</li>
 * <li>or the response is from the user's account (bare JID), if there was not 'to' attribute.</li>
 * </ul>
 * </ol>
 *
 * @author Christian Schudt
 */
final class MessageResponsePredicate implements Predicate<Message> {

    private final Jid to;

    private final String id;

    private final Jid connectedResource;

    /**
     * @param message The message
     */
    MessageResponsePredicate(final Message message, final Jid connectedResource) {
        this.to = message.getTo();
        this.id = message.getId();
        this.connectedResource = connectedResource;
    }

    @Override
    public final boolean test(final Message responseMessage) {
        return (id == null || (Objects.equals(id, responseMessage.getId())))
                && (
                (to != null
                        && ((responseMessage.getFrom() != null && Objects.equals(to.asBareJid(), responseMessage.getFrom().asBareJid()))
                        || (responseMessage.getFrom() == null && connectedResource != null && to.equals(connectedResource.asBareJid()))
                )
                ) ||
                        (to == null && (responseMessage.getFrom() == null || (connectedResource != null && responseMessage.getFrom().asBareJid().equals(connectedResource.asBareJid()))))
        );
    }
}
