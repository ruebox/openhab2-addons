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

package rocks.xmpp.extensions.jingle;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.jingle.apps.rtp.model.Rtp;
import rocks.xmpp.extensions.jingle.apps.rtp.model.info.Ringing;
import rocks.xmpp.extensions.jingle.model.Jingle;
import rocks.xmpp.extensions.jingle.transports.iceudp.model.IceUdpTransportMethod;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class JingleTest extends XmlTest {
    protected JingleTest() throws JAXBException {
        super(ClientIQ.class, Jingle.class, IceUdpTransportMethod.class, Rtp.class, Ringing.class);
    }

    @Test
    public void unmarshalJingleSessionInitiate() throws XMLStreamException, JAXBException {
        String xml = "<iq from='romeo@montague.lit/orchard'\n" +
                "    id='zid615d9'\n" +
                "    to='juliet@capulet.lit/balcony'\n" +
                "    type='set'>\n" +
                "  <jingle xmlns='urn:xmpp:jingle:1'\n" +
                "          action='session-initiate'\n" +
                "          initiator='romeo@montague.lit/orchard'\n" +
                "          sid='a73sjjvkla37jfea'>\n" +
                "    <content creator='initiator' name='this-is-a-stub'>\n" +
                "      <description xmlns='urn:xmpp:jingle:apps:stub:0'/>\n" +
                "      <transport xmlns='urn:xmpp:jingle:transports:stub:0'/>\n" +
                "    </content>\n" +
                "  </jingle>\n" +
                "</iq>";

        IQ iq = unmarshal(xml, IQ.class);
        Jingle jingle = iq.getExtension(Jingle.class);
        Assert.assertNotNull(jingle);
        Assert.assertEquals(jingle.getAction(), Jingle.Action.SESSION_INITIATE);
        Assert.assertEquals(jingle.getInitiator(), Jid.of("romeo@montague.lit/orchard"));
        Assert.assertEquals(jingle.getSessionId(), "a73sjjvkla37jfea");
    }

    @Test
    public void unmarshalJingleSessionAccept() throws XMLStreamException, JAXBException {
        String xml = "<iq from='juliet@capulet.lit/balcony'\n" +
                "    id='rc61n59s'\n" +
                "    to='romeo@montague.lit/orchard'\n" +
                "    type='set'>\n" +
                "  <jingle xmlns='urn:xmpp:jingle:1'\n" +
                "          action='session-accept'\n" +
                "          responder='juliet@capulet.lit/balcony'\n" +
                "          sid='a73sjjvkla37jfea'>\n" +
                "    <content creator='initiator' name='this-is-a-stub'>\n" +
                "      <description xmlns='urn:xmpp:jingle:apps:stub:0'/>\n" +
                "      <transport xmlns='urn:xmpp:jingle:transports:stub:0'/>\n" +
                "    </content>\n" +
                "  </jingle>\n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        Jingle jingle = iq.getExtension(Jingle.class);
        Assert.assertNotNull(jingle);
        Assert.assertEquals(jingle.getAction(), Jingle.Action.SESSION_ACCEPT);
        Assert.assertEquals(jingle.getResponder(), Jid.of("juliet@capulet.lit/balcony"));
        Assert.assertEquals(jingle.getSessionId(), "a73sjjvkla37jfea");
    }

    @Test
    public void unmarshalJingleTerminate() throws XMLStreamException, JAXBException {
        String xml = "<iq from='juliet@capulet.lit/balcony'\n" +
                "    id='vua614d9'\n" +
                "    to='romeo@montague.lit/orchard'\n" +
                "    type='set'>\n" +
                "  <jingle xmlns='urn:xmpp:jingle:1'\n" +
                "          action='session-terminate'\n" +
                "          sid='a73sjjvkla37jfea'>\n" +
                "    <reason>\n" +
                "      <success/>\n" +
                "      <text>Sorry, gotta go!</text>\n" +
                "    </reason>\n" +
                "  </jingle>\n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        Jingle jingle = iq.getExtension(Jingle.class);
        Assert.assertNotNull(jingle);
        Assert.assertEquals(jingle.getAction(), Jingle.Action.SESSION_TERMINATE);
        Assert.assertNotNull(jingle.getReason());
        Assert.assertTrue(jingle.getReason().getType() instanceof Jingle.Reason.Success);
        Assert.assertEquals(jingle.getReason().getText(), "Sorry, gotta go!");
        Assert.assertEquals(jingle.getSessionId(), "a73sjjvkla37jfea");
    }

    @Test
    public void unmarshalJingleWithTransport() throws JAXBException, XMLStreamException {
        String xml = "<iq from='romeo@montague.lit/orchard'\n" +
                "    id='ph37a419'\n" +
                "    to='juliet@capulet.lit/balcony'\n" +
                "    type='set'>\n" +
                "  <jingle xmlns='urn:xmpp:jingle:1'\n" +
                "          action='session-initiate'\n" +
                "          initiator='romeo@montague.lit/orchard'\n" +
                "          sid='a73sjjvkla37jfea'>\n" +
                "    <content creator='initiator' name='voice'>\n" +
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
        Assert.assertEquals(jingle.getAction(), Jingle.Action.SESSION_INITIATE);
        Assert.assertEquals(jingle.getSessionId(), "a73sjjvkla37jfea");
        Assert.assertEquals(jingle.getContents().size(), 1);
        Assert.assertEquals(jingle.getContents().get(0).getCreator(), Jingle.Content.Creator.INITIATOR);
        Assert.assertEquals(jingle.getContents().get(0).getName(), "voice");
        Assert.assertTrue(jingle.getContents().get(0).getApplicationFormat() instanceof Rtp);
        Assert.assertTrue(jingle.getContents().get(0).getTransportMethod() instanceof IceUdpTransportMethod);
    }

    @Test
    public void marshalJingleSessionTerminate() throws XMLStreamException, JAXBException {
        Jingle jingle = new Jingle("a73sjjvkla37jfea", Jingle.Action.SESSION_TERMINATE, new Jingle.Reason(new Jingle.Reason.Decline()));
        String xml = marshal(jingle);
        Assert.assertEquals(xml, "<jingle xmlns=\"urn:xmpp:jingle:1\" action=\"session-terminate\" sid=\"a73sjjvkla37jfea\"><reason><decline></decline></reason></jingle>");
    }

    @Test
    public void unmarshalJingleSessionInfo() throws JAXBException, XMLStreamException {
        String xml = "<iq from='juliet@capulet.lit/balcony'\n" +
                "    id='hq7rg186'\n" +
                "    to='romeo@montague.lit/orchard'\n" +
                "    type='set'>\n" +
                "  <jingle xmlns='urn:xmpp:jingle:1'\n" +
                "          action='session-info'\n" +
                "          sid='a73sjjvkla37jfea'>\n" +
                "    <ringing xmlns='urn:xmpp:jingle:apps:rtp:info:1'/>\n" +
                "  </jingle>\n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        Jingle jingle = iq.getExtension(Jingle.class);
        Assert.assertNotNull(jingle);
        Assert.assertNotNull(jingle.getPayload());
        Assert.assertTrue(jingle.getPayload() instanceof Ringing);
    }
}
