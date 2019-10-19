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

package rocks.xmpp.extensions.bytestreams.ibb;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.bytestreams.ibb.model.InBandByteStream;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class IbbTest extends XmlTest {
    protected IbbTest() throws JAXBException {
        super(ClientIQ.class, InBandByteStream.class);
    }

    @Test
    public void unmarshalData() throws XMLStreamException, JAXBException {
        String xml = "<iq from='romeo@montague.net/orchard' \n" +
                "    id='kr91n475'\n" +
                "    to='juliet@capulet.com/balcony'\n" +
                "    type='set'>\n" +
                "  <data xmlns='http://jabber.org/protocol/ibb' seq='1' sid='i781hf64'>\n" +
                "    qANQR1DBwU4DX7jmYZnncmUQB/9KuKBddzQH+tZ1ZywKK0yHKnq57kWq+RFtQdCJ\n" +
                "    WpdWpR0uQsuJe7+vh3NWn59/gTc5MDlX8dS9p0ovStmNcyLhxVgmqS8ZKhsblVeu\n" +
                "    IpQ0JgavABqibJolc3BKrVtVV1igKiX/N7Pi8RtY1K18toaMDhdEfhBRzO/XB0+P\n" +
                "    AQhYlRjNacGcslkhXqNjK5Va4tuOAPy2n1Q8UUrHbUd0g+xJ9Bm0G0LZXyvCWyKH\n" +
                "    kuNEHFQiLuCY6Iv0myq6iX6tjuHehZlFSh80b5BVV9tNLwNR5Eqz1klxMhoghJOA\n" +
                "  </data>\n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        InBandByteStream.Data data = iq.getExtension(InBandByteStream.Data.class);
        Assert.assertNotNull(data);
        Assert.assertEquals(data.getSequence(), 1);
        Assert.assertEquals(data.getSessionId(), "i781hf64");
        Assert.assertNotNull(data.getBytes());
    }

    @Test
    public void unmarshalOpen() throws XMLStreamException, JAXBException {
        String xml = "<iq from='romeo@montague.net/orchard'\n" +
                "    id='jn3h8g65'\n" +
                "    to='juliet@capulet.com/balcony'\n" +
                "    type='set'>\n" +
                "  <open xmlns='http://jabber.org/protocol/ibb'\n" +
                "        block-size='4096'\n" +
                "        sid='i781hf64'\n" +
                "        stanza='iq'/>\n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        InBandByteStream.Open open = iq.getExtension(InBandByteStream.Open.class);
        Assert.assertNotNull(open);
        Assert.assertEquals(open.getBlockSize(), 4096);
        Assert.assertEquals(open.getSessionId(), "i781hf64");
        Assert.assertEquals(open.getStanzaType(), InBandByteStream.Open.StanzaType.IQ);
    }

    @Test
    public void unmarshalClose() throws XMLStreamException, JAXBException {
        String xml = "<iq from='romeo@montague.net/orchard'\n" +
                "    id='us71g45j'\n" +
                "    to='juliet@capulet.com/balcony'\n" +
                "    type='set'>\n" +
                "  <close xmlns='http://jabber.org/protocol/ibb' sid='i781hf64'/>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        InBandByteStream.Close close = iq.getExtension(InBandByteStream.Close.class);
        Assert.assertNotNull(close);
        Assert.assertEquals(close.getSessionId(), "i781hf64");
    }
}
