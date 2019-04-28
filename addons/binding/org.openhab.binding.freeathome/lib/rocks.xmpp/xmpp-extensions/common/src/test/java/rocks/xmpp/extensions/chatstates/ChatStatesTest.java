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

package rocks.xmpp.extensions.chatstates;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.client.ClientMessage;
import rocks.xmpp.extensions.chatstates.model.ChatState;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class ChatStatesTest extends XmlTest {

    protected ChatStatesTest() throws JAXBException {
        super(ClientMessage.class, ChatState.class);
    }

    @Test
    public void unmarshalActiveState() throws XMLStreamException, JAXBException {
        String xml = "<message \n" +
                "    from='bernardo@shakespeare.lit/pda'\n" +
                "    to='francisco@shakespeare.lit'\n" +
                "    type='chat'>\n" +
                "  <body>Who's there?</body>\n" +
                "  <active xmlns='http://jabber.org/protocol/chatstates'/>\n" +
                "</message>";
        Message message = unmarshal(xml, Message.class);
        Assert.assertEquals(message.getExtensions().size(), 1);
        Assert.assertTrue(message.getExtension(ChatState.class) == ChatState.ACTIVE);
    }

    @Test
    public void unmarshalComposingState() throws XMLStreamException, JAXBException {
        String xml = "<message \n" +
                "    from='bernardo@shakespeare.lit/pda'\n" +
                "    to='francisco@shakespeare.lit/elsinore'\n" +
                "    type='chat'>\n" +
                "  <composing xmlns='http://jabber.org/protocol/chatstates'/>\n" +
                "</message>\n";
        Message message = unmarshal(xml, Message.class);
        Assert.assertEquals(message.getExtensions().size(), 1);
        Assert.assertTrue(message.getExtension(ChatState.class) == ChatState.COMPOSING);
    }

    @Test
    public void unmarshalPausedState() throws XMLStreamException, JAXBException {
        String xml = "<message \n" +
                "    from='romeo@montague.net/orchard' \n" +
                "    to='juliet@capulet.com/balcony'\n" +
                "    type='chat'>\n" +
                "  <thread>act2scene2chat1</thread>\n" +
                "  <paused xmlns='http://jabber.org/protocol/chatstates'/>\n" +
                "</message>";
        Message message = unmarshal(xml, Message.class);
        Assert.assertEquals(message.getExtensions().size(), 1);
        Assert.assertTrue(message.getExtension(ChatState.class) == ChatState.PAUSED);
    }

    @Test
    public void unmarshalInactiveState() throws XMLStreamException, JAXBException {
        String xml = "<message \n" +
                "    from='juliet@capulet.com/balcony'\n" +
                "    to='romeo@shakespeare.lit/orchard'\n" +
                "    type='chat'>\n" +
                "  <thread>act2scene2chat1</thread>\n" +
                "  <inactive xmlns='http://jabber.org/protocol/chatstates'/>\n" +
                "</message>";
        Message message = unmarshal(xml, Message.class);
        Assert.assertEquals(message.getExtensions().size(), 1);
        Assert.assertTrue(message.getExtension(ChatState.class) == ChatState.INACTIVE);
    }

    @Test
    public void unmarshalGoneState() throws XMLStreamException, JAXBException {
        String xml = "<message \n" +
                "    from='juliet@capulet.com/balcony'\n" +
                "    to='romeo@shakespeare.lit/orchard'\n" +
                "    type='chat'>\n" +
                "  <thread>act2scene2chat1</thread>\n" +
                "  <gone xmlns='http://jabber.org/protocol/chatstates'/>\n" +
                "</message>";
        Message message = unmarshal(xml, Message.class);
        Assert.assertEquals(message.getExtensions().size(), 1);
        Assert.assertTrue(message.getExtension(ChatState.class) == ChatState.GONE);
    }
}
