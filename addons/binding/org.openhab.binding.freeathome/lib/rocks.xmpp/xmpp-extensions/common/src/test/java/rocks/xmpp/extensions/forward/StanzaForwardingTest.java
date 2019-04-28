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

package rocks.xmpp.extensions.forward;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.client.ClientMessage;
import rocks.xmpp.extensions.forward.model.Forwarded;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class StanzaForwardingTest extends XmlTest {
    protected StanzaForwardingTest() throws JAXBException {
        super(ClientMessage.class, Forwarded.class);
    }

    @Test
    public void unmarshalForwarded() throws XMLStreamException, JAXBException {
        String xml = "<message to='mercutio@verona.lit' from='romeo@montague.lit/orchard' type='chat' id='28gs'>\n" +
                "        <body>A most courteous exposition!</body>\n" +
                "        <forwarded xmlns='urn:xmpp:forward:0'>\n" +
                "          <delay xmlns='urn:xmpp:delay' stamp='2010-07-10T23:08:25Z'/>\n" +
                "          <message from='juliet@capulet.lit/orchard'\n" +
                "                   id='0202197'\n" +
                "                   to='romeo@montague.lit'\n" +
                "                   type='chat'\n" +
                "                   xmlns='jabber:client'>\n" +
                "             <body>Yet I should kill thee with much cherishing.</body>\n" +
                "              <mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "                  <amorous/>\n" +
                "              </mood>\n" +
                "          </message>\n" +
                "        </forwarded>\n" +
                "      </message>";

        Message message = unmarshal(xml, Message.class);
        String xml2 = marshal(message);
        Assert.assertEquals(xml2, "<message from=\"romeo@montague.lit/orchard\" id=\"28gs\" to=\"mercutio@verona.lit\" type=\"chat\"><body>A most courteous exposition!</body><forwarded xmlns=\"urn:xmpp:forward:0\"><delay xmlns=\"urn:xmpp:delay\" stamp=\"2010-07-10T23:08:25Z\"></delay><message xmlns=\"jabber:client\" from=\"juliet@capulet.lit/orchard\" id=\"0202197\" to=\"romeo@montague.lit\" type=\"chat\"><body>Yet I should kill thee with much cherishing.</body><mood xmlns=\"http://jabber.org/protocol/mood\">\n" +
                "                  <amorous></amorous>\n" +
                "              </mood></message></forwarded></message>");
        Forwarded forwarded = message.getExtension(Forwarded.class);
        Assert.assertNotNull(forwarded);
        Assert.assertTrue(forwarded.getStanza() instanceof Message);
    }
}