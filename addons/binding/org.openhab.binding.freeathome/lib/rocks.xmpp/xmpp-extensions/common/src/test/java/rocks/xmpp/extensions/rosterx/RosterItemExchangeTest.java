/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Christian Schudt
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

package rocks.xmpp.extensions.rosterx;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.client.ClientMessage;
import rocks.xmpp.extensions.rosterx.model.ContactExchange;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class RosterItemExchangeTest extends XmlTest {
    protected RosterItemExchangeTest() throws JAXBException {
        super(ClientMessage.class, ContactExchange.class);
    }

    @Test
    public void unmarshalRosterItemExchange() throws XMLStreamException, JAXBException {
        String xml = "<message from='horatio@denmark.lit' to='hamlet@denmark.lit'>\n" +
                "  <body>Some visitors, m'lord!</body>\n" +
                "  <x xmlns='http://jabber.org/protocol/rosterx'> \n" +
                "    <item action='add'\n" +
                "          jid='rosencrantz@denmark.lit'\n" +
                "          name='Rosencrantz'>\n" +
                "      <group>Visitors</group>\n" +
                "    </item>\n" +
                "    <item action='delete'\n" +
                "          jid='guildenstern@denmark.lit'\n" +
                "          name='Guildenstern'>\n" +
                "      <group>Visitors</group>\n" +
                "    </item>\n" +
                "    <item action='modify'\n" +
                "          jid='rosencrantz@denmark.lit'\n" +
                "          name='Rosencrantz'>\n" +
                "      <group>Retinue</group>\n" +
                "    </item>\n" +
                "  </x>\n" +
                "</message>\n";

        Message message = unmarshal(xml, Message.class);
        ContactExchange contactExchange = message.getExtension(ContactExchange.class);
        Assert.assertNotNull(contactExchange);
        Assert.assertEquals(contactExchange.getItems().size(), 3);
        Assert.assertEquals(contactExchange.getItems().get(0).getJid(), Jid.of("rosencrantz@denmark.lit"));
        Assert.assertEquals(contactExchange.getItems().get(0).getAction(), ContactExchange.Item.Action.ADD);
        Assert.assertEquals(contactExchange.getItems().get(0).getName(), "Rosencrantz");
        Assert.assertEquals(contactExchange.getItems().get(0).getGroups().size(), 1);
        Assert.assertEquals(contactExchange.getItems().get(0).getGroups().get(0), "Visitors");
        Assert.assertEquals(contactExchange.getItems().get(1).getJid(), Jid.of("guildenstern@denmark.lit"));
        Assert.assertEquals(contactExchange.getItems().get(1).getAction(), ContactExchange.Item.Action.DELETE);
        Assert.assertEquals(contactExchange.getItems().get(1).getName(), "Guildenstern");
        Assert.assertEquals(contactExchange.getItems().get(1).getGroups().size(), 1);
        Assert.assertEquals(contactExchange.getItems().get(1).getGroups().get(0), "Visitors");
        Assert.assertEquals(contactExchange.getItems().get(2).getJid(), Jid.of("rosencrantz@denmark.lit"));
        Assert.assertEquals(contactExchange.getItems().get(2).getAction(), ContactExchange.Item.Action.MODIFY);
        Assert.assertEquals(contactExchange.getItems().get(2).getName(), "Rosencrantz");
        Assert.assertEquals(contactExchange.getItems().get(2).getGroups().size(), 1);
        Assert.assertEquals(contactExchange.getItems().get(2).getGroups().get(0), "Retinue");
    }
}
