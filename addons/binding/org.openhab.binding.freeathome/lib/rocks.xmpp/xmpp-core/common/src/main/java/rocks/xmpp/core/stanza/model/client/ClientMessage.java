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

package rocks.xmpp.core.stanza.model.client;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.Text;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.StanzaError;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;
import java.util.Locale;

/**
 * The implementation of the {@code <message/>} element in the {@code jabber:client} namespace.
 *
 * @author Christian Schudt
 */
@XmlRootElement(name = "message")
@XmlType(propOrder = {"from", "id", "to", "type", "lang", "subject", "body", "thread", "extensions", "error"})
public final class ClientMessage extends Message {

    @SuppressWarnings("unused")
    private ClientMessage() {
    }

    /**
     * Constructs a message with body and type.
     *
     * @param to           The recipient.
     * @param bodies       The message bodies.
     * @param type         The message type.
     * @param subjects     The subjects.
     * @param thread       The thread.
     * @param parentThread The parent thread.
     * @param from         The sender.
     * @param id           The id.
     * @param language     The language.
     * @param extensions   The extensions.
     * @param error        The error.
     */
    private ClientMessage(Jid to, Type type, Collection<Text> bodies, Collection<Text> subjects, String thread, String parentThread, String id, Jid from, Locale language, Collection<?> extensions, StanzaError error) {
        super(to, type, bodies, subjects, thread, parentThread, id, from, language, extensions, error);
    }

    /**
     * Creates a message for the {@code jabber:client} namespace from a generic message.
     *
     * @param m The message.
     * @return The client message.
     */
    public static ClientMessage from(Message m) {
        if (m instanceof ClientMessage) {
            return (ClientMessage) m;
        }
        return new ClientMessage(m.getTo(), m.getType(), m.getBodies(), m.getSubjects(), m.getThread(), m.getParentThread(), m.getId(), m.getFrom(), m.getLanguage(), m.getExtensions(), m.getError());
    }
}
