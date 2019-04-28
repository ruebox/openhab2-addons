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

package rocks.xmpp.extensions.blocking;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.blocking.model.Block;
import rocks.xmpp.extensions.blocking.model.BlockList;
import rocks.xmpp.extensions.blocking.model.Unblock;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.util.ArrayDeque;
import java.util.Collection;

/**
 * @author Christian Schudt
 */
public class BlockingTest extends XmlTest {
    protected BlockingTest() throws JAXBException {
        super(ClientIQ.class, BlockList.class);
    }

    @Test
    public void unmarshalBlockList() throws XMLStreamException, JAXBException {
        String xml = "<iq type='result' id='blocklist1'>\n" +
                "  <blocklist xmlns='urn:xmpp:blocking'>\n" +
                "    <item jid='romeo@montague.net'/>\n" +
                "    <item jid='iago@shakespeare.lit'/>\n" +
                "  </blocklist>\n" +
                "</iq>";
        IQ iq = unmarshal(xml, IQ.class);
        BlockList blockList = iq.getExtension(BlockList.class);
        Assert.assertEquals(blockList.getItems().size(), 2);
        Assert.assertEquals(blockList.getItems().get(0), Jid.of("romeo@montague.net"));
        Assert.assertEquals(blockList.getItems().get(1), Jid.of("iago@shakespeare.lit"));
        Assert.assertNotNull(blockList);
    }

    @Test
    public void marshalBlock() throws JAXBException, XMLStreamException {
        Collection<Jid> items = new ArrayDeque<>();
        items.add(Jid.of("romeo@montague.net"));
        IQ iq = new IQ(IQ.Type.SET, new Block(items), "1");
        String xml = marshal(iq);
        Assert.assertEquals(xml, "<iq id=\"1\" type=\"set\"><block xmlns=\"urn:xmpp:blocking\"><item jid=\"romeo@montague.net\"></item></block></iq>");
    }

    @Test
    public void marshalUnblock() throws JAXBException, XMLStreamException {
        Collection<Jid> items = new ArrayDeque<>();
        items.add(Jid.of("romeo@montague.net"));
        IQ iq = new IQ(IQ.Type.SET, new Unblock(items), "1");
        String xml = marshal(iq);
        Assert.assertEquals(xml, "<iq id=\"1\" type=\"set\"><unblock xmlns=\"urn:xmpp:blocking\"><item jid=\"romeo@montague.net\"></item></unblock></iq>");
    }
}
