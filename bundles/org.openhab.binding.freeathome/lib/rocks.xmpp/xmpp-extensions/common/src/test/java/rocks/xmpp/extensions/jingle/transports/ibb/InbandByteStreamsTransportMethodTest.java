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

package rocks.xmpp.extensions.jingle.transports.ibb;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.bytestreams.ibb.model.InBandByteStream;
import rocks.xmpp.extensions.jingle.apps.rtp.model.Rtp;
import rocks.xmpp.extensions.jingle.model.Jingle;
import rocks.xmpp.extensions.jingle.transports.ibb.model.InBandByteStreamsTransportMethod;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class InbandByteStreamsTransportMethodTest extends XmlTest {
    protected InbandByteStreamsTransportMethodTest() throws JAXBException {
        super(Jingle.class, InBandByteStreamsTransportMethod.class, Rtp.class);
    }

    @Test
    public void unmarshalJingleWithTransport() throws XMLStreamException, JAXBException {
        String xml = "<jingle xmlns='urn:xmpp:jingle:1'\n" +
                "          action='session-accept'\n" +
                "          responder='juliet@capulet.lit/balcony'\n" +
                "          sid='a73sjjvkla37jfea'>\n" +
                "    <content creator='initiator' name='ex'>\n" +
                "      <description xmlns='urn:xmpp:example'/>\n" +
                "      <transport xmlns='urn:xmpp:jingle:transports:ibb:1'\n" +
                "                 block-size='2048'\n" +
                "                 sid='ch3d9s71'" +
                "                 stanza='iq' />\n" +
                "    </content>\n" +
                "  </jingle>\n";

        Jingle jingle = unmarshal(xml, Jingle.class);
        Assert.assertNotNull(jingle);
        Assert.assertTrue(jingle.getContents().get(0).getTransportMethod() instanceof InBandByteStreamsTransportMethod);
        InBandByteStreamsTransportMethod inBandByteStreamsTransportMethod = (InBandByteStreamsTransportMethod) jingle.getContents().get(0).getTransportMethod();
        Assert.assertNotNull(inBandByteStreamsTransportMethod);
        Assert.assertNull(jingle.getContents().get(0).getApplicationFormat());
        Assert.assertEquals(inBandByteStreamsTransportMethod.getSessionId(), "ch3d9s71");
        Assert.assertEquals(inBandByteStreamsTransportMethod.getBlockSize(), 2048);
        Assert.assertEquals(inBandByteStreamsTransportMethod.getStanzaType(), InBandByteStream.Open.StanzaType.IQ);
    }
}
