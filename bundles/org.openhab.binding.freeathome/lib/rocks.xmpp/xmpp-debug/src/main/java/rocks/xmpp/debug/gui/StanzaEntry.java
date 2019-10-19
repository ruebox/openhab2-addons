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

package rocks.xmpp.debug.gui;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.core.stanza.model.Stanza;
import rocks.xmpp.core.stream.model.StreamError;

import java.time.LocalDateTime;

/**
 * @author Christian Schudt
 */
final class StanzaEntry {
    private final boolean inbound;

    private final String xml;

    private final Object stanza;

    private final LocalDateTime date;

    public StanzaEntry(boolean inbound, String xml, Object stanza) {
        this.inbound = inbound;
        this.xml = xml;
        this.stanza = stanza;
        this.date = LocalDateTime.now();
    }

    public String getXml() {
        return xml;
    }

    public Object getStanza() {
        return stanza;
    }

    public boolean isInbound() {
        return inbound;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Jid getFrom() {
        return stanza instanceof Stanza ? ((Stanza) stanza).getFrom() : null;
    }

    public Jid getTo() {
        return stanza instanceof Stanza ? ((Stanza) stanza).getTo() : null;
    }

    public boolean isError() {
        return (stanza instanceof IQ && ((IQ) stanza).getType() == IQ.Type.ERROR)
                || (stanza instanceof Message && ((Message) stanza).getType() == Message.Type.ERROR)
                || (stanza instanceof Presence && ((Presence) stanza).getType() == Presence.Type.ERROR)
                || stanza instanceof StreamError;
    }

    @Override
    public String toString() {
        return (inbound ? "IN : " : "OUT: ") + xml;
    }
}
