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
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.core.stanza.model.StanzaError;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;
import java.util.Locale;

/**
 * The implementation of the {@code <presence/>} element in the {@code jabber:client} namespace.
 *
 * @author Christian Schudt
 */
@XmlRootElement(name = "presence")
@XmlType(propOrder = {"from", "id", "to", "type", "lang", "show", "status", "priority", "extensions", "error"})
public final class ClientPresence extends Presence {

    @SuppressWarnings("unused")
    private ClientPresence() {
    }

    /**
     * Constructs a presence with all possible values.
     *
     * @param to         The recipient.
     * @param type       The type.
     * @param show       The 'show' value.
     * @param status     The status.
     * @param priority   The priority.
     * @param id         The id.
     * @param from       The 'from' attribute.
     * @param language   The language.
     * @param extensions The extensions.
     * @param error      The stanza error.
     */
    private ClientPresence(Jid to, Type type, Show show, Collection<Text> status, Byte priority, String id, Jid from, Locale language, Collection<?> extensions, StanzaError error) {
        super(to, type, show, status, priority, id, from, language, extensions, error);
    }

    /**
     * Creates a presence for the {@code jabber:client} namespace from a generic presence.
     *
     * @param p The presence.
     * @return The client presence.
     */
    public static ClientPresence from(Presence p) {
        if (p instanceof ClientPresence) {
            return (ClientPresence) p;
        }
        return new ClientPresence(p.getTo(), p.getType(), p.getShow(), p.getStatuses(), p.getPriority(), p.getId(), p.getFrom(), p.getLanguage(), p.getExtensions(), p.getError());
    }
}
