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

package rocks.xmpp.core;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.Stanza;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christian Schudt
 */
public class MockServer {

    private final Map<Jid, XmppSession> connections = new HashMap<>();

    public void registerConnection(XmppSession xmppSession) {
        connections.put(xmppSession.getConnectedResource(), xmppSession);
    }

    public void receive(Stanza stanza) {

        XmppSession toXmppSession = connections.get(stanza.getTo());
        if (toXmppSession != null) {
            try {
                toXmppSession.handleElement(stanza);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (stanza instanceof IQ) {
            connections.get(stanza.getFrom()).send(((IQ) stanza).createResult());
        }
    }
}
