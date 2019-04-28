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

package rocks.xmpp.extensions.attention;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.client.ClientMessage;
import rocks.xmpp.extensions.attention.model.Attention;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class AttentionTest extends XmlTest {
    protected AttentionTest() throws JAXBException {
        super(ClientMessage.class, Attention.class);
    }

    @Test
    public void unmarshalAttentionInMessage() throws XMLStreamException, JAXBException {
        String xml = "<message from='calvin@usrobots.lit/lab'\n" +
                "         to='herbie@usrobots.lit/home'\n" +
                "         type='headline'>\n" +
                "  <attention xmlns='urn:xmpp:attention:0'/>\n" +
                "  <body>Why don't you answer, Herbie?</body>\n" +
                "</message>";
        Message message = unmarshal(xml, Message.class);
        Message message2 = unmarshal(xml, Message.class);
        Attention attention = message.getExtension(Attention.class);
        Attention attention2 = message2.getExtension(Attention.class);
        Assert.assertNotNull(attention);
        Assert.assertTrue(attention == attention2);
    }
}
