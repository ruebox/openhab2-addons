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

package rocks.xmpp.extensions.jingle;

import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.extensions.jingle.model.Jingle;

import java.util.EventObject;

/**
 * This event notifies listeners, when a Jingle session is initiated.
 *
 * @author Christian Schudt
 */
public final class JingleEvent extends EventObject {

    private final String sessionId;

    private final transient IQ iq;

    private final transient XmppSession xmppSession;

    private final transient Jingle jingle;

    /**
     * Constructs a Jingle event.
     *
     * @param source The object on which the event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    JingleEvent(Object source, XmppSession xmppSession, IQ iq, Jingle jingle) {
        super(source);
        this.xmppSession = xmppSession;
        this.iq = iq;
        this.sessionId = jingle.getSessionId();
        this.jingle = jingle;
    }

    public JingleSession accept() {
        xmppSession.send(iq.createResult());
        return null;
    }

    /**
     * Rejects the Jingle session.
     */
    public void reject() {
        // Another reason for terminating the session is that the terminating party wishes to formally decline the session; in this case, the recommended condition is <decline/>.
        xmppSession.send(IQ.set(iq.getFrom(), new Jingle(sessionId, Jingle.Action.SESSION_TERMINATE, new Jingle.Reason(new Jingle.Reason.Decline()))));
    }

    public IQ getIQ() {
    	return iq;
    }

    public Jingle getJingle() {
        return jingle;
    }

    public String getSessionId() {
        return sessionId;
    }
}
