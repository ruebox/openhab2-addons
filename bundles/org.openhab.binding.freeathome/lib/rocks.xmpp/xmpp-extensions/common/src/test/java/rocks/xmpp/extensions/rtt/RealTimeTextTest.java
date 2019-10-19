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

package rocks.xmpp.extensions.rtt;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.client.ClientMessage;
import rocks.xmpp.extensions.rtt.model.RealTimeText;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class RealTimeTextTest extends XmlTest {
    protected RealTimeTextTest() throws JAXBException {
        super(ClientMessage.class, RealTimeText.class);
    }

    @Test
    public void unmarshalRtt() throws XMLStreamException, JAXBException {
        String xml = "<message to='bob@example.com' from='alice@example.com/home' type='chat' id='a01'>\n" +
                "  <rtt xmlns='urn:xmpp:rtt:0' seq='123001' event='new'>\n" +
                "    <t>Helo</t>\n" +
                "    <e/>\n" +
                "    <t>lo...planet</t>\n" +
                "    <e n='6'/>\n" +
                "    <t> World</t>\n" +
                "    <e n='3' p='8'/>\n" +
                "    <t p='5'> there,</t>\n" +
                "  </rtt>\n" +
                "</message>";

        Message message = unmarshal(xml, Message.class);
        RealTimeText realTimeText = message.getExtension(RealTimeText.class);

        Assert.assertNotNull(realTimeText);
        Assert.assertEquals(realTimeText.getEvent(), RealTimeText.Event.NEW);
        Assert.assertEquals(realTimeText.getSequence(), (Integer) 123001);
        Assert.assertEquals(realTimeText.getActions().size(), 7);
        Assert.assertTrue(realTimeText.getActions().get(0) instanceof RealTimeText.InsertText);
        Assert.assertTrue(realTimeText.getActions().get(1) instanceof RealTimeText.EraseText);
        Assert.assertTrue(realTimeText.getActions().get(2) instanceof RealTimeText.InsertText);
        Assert.assertTrue(realTimeText.getActions().get(3) instanceof RealTimeText.EraseText);
        Assert.assertTrue(realTimeText.getActions().get(4) instanceof RealTimeText.InsertText);
        Assert.assertTrue(realTimeText.getActions().get(5) instanceof RealTimeText.EraseText);
        Assert.assertTrue(realTimeText.getActions().get(6) instanceof RealTimeText.InsertText);

        Assert.assertEquals(((RealTimeText.InsertText) realTimeText.getActions().get(0)).getText(), "Helo");
        Assert.assertEquals(((RealTimeText.InsertText) realTimeText.getActions().get(2)).getText(), "lo...planet");
        Assert.assertEquals(((RealTimeText.InsertText) realTimeText.getActions().get(4)).getText(), " World");
        Assert.assertEquals(((RealTimeText.InsertText) realTimeText.getActions().get(6)).getText(), " there,");
    }
}
