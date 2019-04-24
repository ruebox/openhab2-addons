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

package rocks.xmpp.extensions.httpauth;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.Stanza;
import rocks.xmpp.core.stanza.model.StanzaError;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.extensions.httpauth.model.ConfirmationRequest;

import java.util.Collections;
import java.util.EventObject;

/**
 * This event notifies listeners, when a HTTP authentication confirmation request has been received.
 *
 * @author Christian Schudt
 */
public final class HttpAuthenticationEvent extends EventObject {

    private final transient ConfirmationRequest confirmationRequest;

    private final XmppSession xmppSession;

    private final Stanza stanza;

    /**
     * Constructs a HTTP auth event.
     *
     * @param source              The object on which the event initially occurred.
     * @param xmppSession         The XMPP session.
     * @param stanza              The stanza.
     * @param confirmationRequest The confirmation request.
     * @throws IllegalArgumentException if source is null.
     */
    HttpAuthenticationEvent(Object source, XmppSession xmppSession, Stanza stanza, ConfirmationRequest confirmationRequest) {
        super(source);
        this.confirmationRequest = confirmationRequest;
        this.xmppSession = xmppSession;
        this.stanza = stanza;
    }

    /**
     * Gets the requester.
     *
     * @return The requester.
     */
    public Jid getRequester() {
        return stanza.getFrom();
    }

    /**
     * Gets the confirmation request.
     *
     * @return The confirmation request.
     */
    public ConfirmationRequest getConfirmationRequest() {
        return confirmationRequest;
    }

    /**
     * Confirms the HTTP authentication request.
     */
    public void confirm() {
        if (stanza instanceof IQ) {
            // If the user wishes to confirm the request, the <iq/> response stanza MUST be of type "result"
            xmppSession.send(((IQ) stanza).createResult());
        } else if (stanza instanceof Message) {
            // If the user wishes to confirm the request, the <message/> response stanza SHOULD be of type "normal", MUST mirror the <thread/> ID (if provided by the XMPP Server), and MUST contain the original <confirm/> child element
            Message m = new Message(getRequester(), Message.Type.NORMAL, null, null, ((Message) stanza).getThread());
            m.putExtension(confirmationRequest);
            xmppSession.send(m);
        }
    }

    /**
     * Denies the HTTP authentication request.
     */
    public void deny() {
        if (stanza instanceof IQ) {
            // If the user wishes to deny the request, the <iq/> response stanza MUST be of type "error",
            // MAY contain the original <confirm/> child element (although this is not necessary since the XMPP 'id' attribute can be used for tracking purposes),
            // and MUST specify an error, which SHOULD be <not-authorized/>
            xmppSession.send(((IQ) stanza).createError(Condition.NOT_AUTHORIZED));
        } else if (stanza instanceof Message) {
            // If the user wishes to deny the request, the <message/> response stanza MUST be of type "error",
            // MUST mirror the <thread/> ID (if provided by the XMPP Server),
            // MUST contain the original <confirm/> child element,
            // and MUST specify an error, which SHOULD be <not-authorized/>
            xmppSession.send(new Message(getRequester(), Message.Type.ERROR, Collections.emptyList(), null, ((Message) stanza).getThread(), null, null, null, null, Collections.singleton(confirmationRequest), new StanzaError(Condition.NOT_AUTHORIZED)));
        }
    }
}
