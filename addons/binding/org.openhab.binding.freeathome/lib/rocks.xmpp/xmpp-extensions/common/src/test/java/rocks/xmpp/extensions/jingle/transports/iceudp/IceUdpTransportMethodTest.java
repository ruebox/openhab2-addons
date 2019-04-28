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

package rocks.xmpp.extensions.jingle.transports.iceudp;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.jingle.apps.rtp.model.Rtp;
import rocks.xmpp.extensions.jingle.model.Jingle;
import rocks.xmpp.extensions.jingle.transports.iceudp.model.Candidate;
import rocks.xmpp.extensions.jingle.transports.iceudp.model.IceUdpTransportMethod;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class IceUdpTransportMethodTest extends XmlTest {
    protected IceUdpTransportMethodTest() throws JAXBException {
        super(ClientIQ.class, Jingle.class, Rtp.class, IceUdpTransportMethod.class);
    }

    @Test
    public void unmarshalJingleWithTransport() throws XMLStreamException, JAXBException {
        String xml = "<iq from='romeo@montague.lit/orchard'\n" +
                "    id='ixt174g9'\n" +
                "    to='juliet@capulet.lit/balcony'\n" +
                "    type='set'>\n" +
                "  <jingle xmlns='urn:xmpp:jingle:1'\n" +
                "          action='session-initiate'\n" +
                "          initiator='romeo@montague.lit/orchard'\n" +
                "          sid='a73sjjvkla37jfea'>\n" +
                "    <content creator='initiator' name='this-is-the-audio-content'>\n" +
                "      <description xmlns='urn:xmpp:jingle:apps:rtp:1' media='audio'>\n" +
                "        <payload-type id='96' name='speex' clockrate='16000'/>\n" +
                "        <payload-type id='97' name='speex' clockrate='8000'/>\n" +
                "        <payload-type id='18' name='G729'/>\n" +
                "        <payload-type id='0' name='PCMU' />\n" +
                "        <payload-type id='103' name='L16' clockrate='16000' channels='2'/>\n" +
                "        <payload-type id='98' name='x-ISAC' clockrate='8000'/>\n" +
                "      </description>\n" +
                "      <transport xmlns='urn:xmpp:jingle:transports:ice-udp:1'\n" +
                "                 pwd='asd88fgpdd777uzjYhagZg'\n" +
                "                 ufrag='8hhy'>\n" +
                "        <candidate component='1'\n" +
                "                   foundation='1'\n" +
                "                   generation='0'\n" +
                "                   id='el0747fg11'\n" +
                "                   ip='10.0.1.1'\n" +
                "                   network='1'\n" +
                "                   port='8998'\n" +
                "                   priority='2130706431'\n" +
                "                   protocol='udp'\n" +
                "                   type='host'/>\n" +
                "        <candidate component='1'\n" +
                "                   foundation='2'\n" +
                "                   generation='0'\n" +
                "                   id='y3s2b30v3r'\n" +
                "                   ip='192.0.2.3'\n" +
                "                   network='1'\n" +
                "                   port='45664'\n" +
                "                   priority='1694498815'\n" +
                "                   protocol='udp'\n" +
                "                   rel-addr='10.0.1.1'\n" +
                "                   rel-port='8998'\n" +
                "                   type='srflx'/>\n" +
                "      </transport>\n" +
                "    </content>\n" +
                "  </jingle>\n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        Jingle jingle = iq.getExtension(Jingle.class);
        Assert.assertNotNull(jingle);
        Assert.assertTrue(jingle.getContents().get(0).getTransportMethod() instanceof IceUdpTransportMethod);
        IceUdpTransportMethod iceUdpTransport = (IceUdpTransportMethod) jingle.getContents().get(0).getTransportMethod();
        Assert.assertNotNull(iceUdpTransport);
        Assert.assertEquals(iceUdpTransport.getPassword(), "asd88fgpdd777uzjYhagZg");
        Assert.assertEquals(iceUdpTransport.getUserFragment(), "8hhy");
        Assert.assertEquals(iceUdpTransport.getCandidates().size(), 2);
        Assert.assertEquals(iceUdpTransport.getCandidates().get(0).getComponent(), 1);
        Assert.assertEquals(iceUdpTransport.getCandidates().get(0).getFoundation(), Short.valueOf((short) 1));
        Assert.assertEquals(iceUdpTransport.getCandidates().get(0).getGeneration(), Short.valueOf((short) 0));
        Assert.assertEquals(iceUdpTransport.getCandidates().get(0).getId(), "el0747fg11");
        Assert.assertEquals(iceUdpTransport.getCandidates().get(0).getIpAddress(), "10.0.1.1");
        Assert.assertEquals(iceUdpTransport.getCandidates().get(0).getNetwork(), Short.valueOf((short) 1));
        Assert.assertEquals(iceUdpTransport.getCandidates().get(0).getPort(), (Integer) 8998);
        Assert.assertEquals(iceUdpTransport.getCandidates().get(0).getPriority(), (Integer) 2130706431);
        Assert.assertEquals(iceUdpTransport.getCandidates().get(0).getProtocol(), "udp");
        Assert.assertEquals(iceUdpTransport.getCandidates().get(0).getType(), Candidate.Type.HOST);
    }
}
