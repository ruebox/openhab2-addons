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

package rocks.xmpp.extensions.oob;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.core.stanza.model.client.ClientMessage;
import rocks.xmpp.extensions.oob.model.iq.OobIQ;
import rocks.xmpp.extensions.oob.model.x.OobX;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class OutOfBandTest extends XmlTest {
    protected OutOfBandTest() throws JAXBException {
        super(ClientIQ.class, ClientMessage.class, OobIQ.class, OobX.class);
    }

    @Test
    public void unmarshalOobIQ() throws XMLStreamException, JAXBException {
        String xml = "<iq type='set'\n" +
                "    from='stpeter@jabber.org/work'\n" +
                "    to='MaineBoy@jabber.org/home'\n" +
                "    id='oob1'>\n" +
                "  <query xmlns='jabber:iq:oob' sid='a0'>\n" +
                "    <url>http://www.jabber.org/images/psa-license.jpg</url>\n" +
                "    <desc>A license to Jabber!</desc>\n" +
                "  </query>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        OobIQ oobIQ = iq.getExtension(OobIQ.class);
        Assert.assertNotNull(oobIQ);
        Assert.assertEquals(oobIQ.getUri().toString(), "http://www.jabber.org/images/psa-license.jpg");
        Assert.assertEquals(oobIQ.getDescription(), "A license to Jabber!");
        Assert.assertEquals(oobIQ.getSessionId(), "a0");
    }

    @Test
    public void unmarshalOobX() throws XMLStreamException, JAXBException {
        String xml = "<message from='stpeter@jabber.org/work'\n" +
                "         to='MaineBoy@jabber.org/home'>\n" +
                "  <body>Yeah, but do you have a license to Jabber?</body>\n" +
                "  <x xmlns='jabber:x:oob'>\n" +
                "    <url>http://www.jabber.org/images/psa-license.jpg</url>\n" +
                "    <desc>A license to Jabber!</desc>\n" +
                "  </x>\n" +
                "</message>\n";
        Message message = unmarshal(xml, Message.class);
        OobX oobX = message.getExtension(OobX.class);
        Assert.assertNotNull(oobX);
        Assert.assertEquals(oobX.getUri().toString(), "http://www.jabber.org/images/psa-license.jpg");
        Assert.assertEquals(oobX.getDescription(), "A license to Jabber!");
    }
}
