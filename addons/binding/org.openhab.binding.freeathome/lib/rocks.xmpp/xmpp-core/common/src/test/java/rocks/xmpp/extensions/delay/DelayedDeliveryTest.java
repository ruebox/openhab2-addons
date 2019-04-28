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

package rocks.xmpp.extensions.delay;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.client.ClientMessage;
import rocks.xmpp.extensions.delay.model.DelayedDelivery;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.time.Instant;

/**
 * @author Christian Schudt
 */
public class DelayedDeliveryTest extends XmlTest {
    protected DelayedDeliveryTest() throws JAXBException {
        super(ClientMessage.class, DelayedDelivery.class);
    }

    @Test
    public void unmarshalMessageWithDelayedDelivery() throws XMLStreamException, JAXBException {
        String xml = "<message\n" +
                "    from='romeo@montague.net/orchard'\n" +
                "    to='juliet@capulet.com'\n" +
                "    type='chat'>\n" +
                "  <body>\n" +
                "    O blessed, blessed night! I am afeard.\n" +
                "    Being in night, all this is but a dream,\n" +
                "    Too flattering-sweet to be substantial.\n" +
                "  </body>\n" +
                "  <delay xmlns='urn:xmpp:delay'\n" +
                "     from='capulet.com'\n" +
                "     stamp='2002-09-10T23:08:25Z'>Offline Storage</delay>\n" +
                "</message>\n";

        Message message = unmarshal(xml, Message.class);
        Assert.assertEquals(message.getExtensions().size(), 1);
        DelayedDelivery delay = message.getExtension(DelayedDelivery.class);
        Assert.assertNotNull(delay);
        Assert.assertEquals(delay.getFrom(), Jid.ofDomain("capulet.com"));
        Assert.assertEquals(delay.getTimeStamp(), Instant.parse("2002-09-10T23:08:25Z"));
        Assert.assertEquals(delay.getReason(), "Offline Storage");
    }
}
