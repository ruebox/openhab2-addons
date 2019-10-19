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

package rocks.xmpp.extensions.offline;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.offline.model.OfflineMessage;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class OfflineMessageTest extends XmlTest {
    protected OfflineMessageTest() throws JAXBException {
        super(ClientIQ.class, OfflineMessage.class);
    }

    @Test
    public void unmarshalOffline() throws XMLStreamException, JAXBException {
        String xml = "<iq type='get' id='view1'>\n" +
                "  <offline xmlns='http://jabber.org/protocol/offline'>\n" +
                "    <item action='view'\n" +
                "          node='2003-02-27T22:52:37.225Z'/>\n" +
                "  </offline>\n" +
                "</iq>";
        IQ iq = unmarshal(xml, IQ.class);
        OfflineMessage offlineMessage = iq.getExtension(OfflineMessage.class);
        Assert.assertNotNull(offlineMessage);
        Assert.assertEquals(offlineMessage.getItems().size(), 1);
    }

    @Test
    public void marshalOfflineMessagesFetch() throws JAXBException, XMLStreamException {
        String xml = marshal(new OfflineMessage(true, false));
        Assert.assertEquals("<offline xmlns=\"http://jabber.org/protocol/offline\"><fetch></fetch></offline>", xml);
    }

    @Test
    public void marshalOfflineMessagesPurge() throws JAXBException, XMLStreamException {
        String xml = marshal(new OfflineMessage(false, true));
        Assert.assertEquals("<offline xmlns=\"http://jabber.org/protocol/offline\"><purge></purge></offline>", xml);
    }

    @Test
    public void marshalOfflineMessagesRetrieval() throws JAXBException, XMLStreamException {
        String xml = marshal(new OfflineMessage(false, true));
        Assert.assertEquals("<offline xmlns=\"http://jabber.org/protocol/offline\"><purge></purge></offline>", xml);
    }
}
