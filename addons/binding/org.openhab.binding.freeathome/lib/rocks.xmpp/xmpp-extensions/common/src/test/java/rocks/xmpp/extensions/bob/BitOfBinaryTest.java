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

package rocks.xmpp.extensions.bob;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.bob.model.Data;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.nio.charset.StandardCharsets;

/**
 * @author Christian Schudt
 */
public class BitOfBinaryTest extends XmlTest {

    protected BitOfBinaryTest() throws JAXBException {
        super(ClientIQ.class, Data.class);
    }

    @Test
    public void unmarshalBitsOfBinaryResponse() throws JAXBException, XMLStreamException {
        String xml = "<iq from='ladymacbeth@shakespeare.lit/castle'\n" +
                "    id='get-data-1'\n" +
                "    to='doctor@shakespeare.lit/pda'\n" +
                "    type='result'>\n" +
                "  <data xmlns='urn:xmpp:bob' \n" +
                "        cid='sha1+8f35fef110ffc5df08d579a50083ff9308fb6242@bob.xmpp.org'\n" +
                "        max-age='86400'\n" +
                "        type='image/png'>\n" +
                "    iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAABGdBTUEAALGP\n" +
                "    C/xhBQAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9YGARc5KB0XV+IA\n" +
                "    AAAddEVYdENvbW1lbnQAQ3JlYXRlZCB3aXRoIFRoZSBHSU1Q72QlbgAAAF1J\n" +
                "    REFUGNO9zL0NglAAxPEfdLTs4BZM4DIO4C7OwQg2JoQ9LE1exdlYvBBeZ7jq\n" +
                "    ch9//q1uH4TLzw4d6+ErXMMcXuHWxId3KOETnnXXV6MJpcq2MLaI97CER3N0\n" +
                "    vr4MkhoXe0rZigAAAABJRU5ErkJggg==\n" +
                "  </data>\n" +
                "</iq>";
        IQ iq = unmarshal(xml, IQ.class);

        Data data = iq.getExtension(Data.class);
        Assert.assertNotNull(data);
        Assert.assertEquals(data.getContentId(), "sha1+8f35fef110ffc5df08d579a50083ff9308fb6242@bob.xmpp.org");
        Assert.assertEquals(data.getMaxAge(), (Integer) 86400);
        Assert.assertEquals(data.getType(), "image/png");
        Assert.assertNotNull(data.getBytes());
    }

    @Test
    public void testCreateContentId() {

        String base64 = "iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAABGdBTUEAALGP" +
                "C/xhBQAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9YGARc5KB0XV+IA" +
                "AAAddEVYdENvbW1lbnQAQ3JlYXRlZCB3aXRoIFRoZSBHSU1Q72QlbgAAAF1J" +
                "REFUGNO9zL0NglAAxPEfdLTs4BZM4DIO4C7OwQg2JoQ9LE1exdlYvBBeZ7jq" +
                "ch9//q1uH4TLzw4d6+ErXMMcXuHWxId3KOETnnXXV6MJpcq2MLaI97CER3N0" +
                "vr4MkhoXe0rZigAAAABJRU5ErkJggg==";
        // Here we use the bytes from the base64 encoded string, because it's also used in the examples of XEP-0231.
        // However, the example are wrong here. The sha1 string should actually be generated from the binary data.
        String cid = Data.createContendId(base64.getBytes(StandardCharsets.UTF_8));
        Assert.assertEquals(cid, "sha1+8f35fef110ffc5df08d579a50083ff9308fb6242@bob.xmpp.org");
    }
}
