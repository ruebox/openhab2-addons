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

package rocks.xmpp.extensions.jingle.transports.s5b;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.bytestreams.s5b.model.Socks5ByteStream;
import rocks.xmpp.extensions.jingle.apps.rtp.model.Rtp;
import rocks.xmpp.extensions.jingle.model.Jingle;
import rocks.xmpp.extensions.jingle.transports.s5b.model.S5bTransportMethod;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Christian Schudt
 */
public class S5bTransportMethodTest extends XmlTest {
    protected S5bTransportMethodTest() throws JAXBException {
        super(Jingle.class, S5bTransportMethod.class, Rtp.class);
    }

    @Test
    public void unmarshalJingleWithTransport() throws XMLStreamException, JAXBException {
        String xml = "<jingle xmlns='urn:xmpp:jingle:1'\n" +
                "          action='session-accept'\n" +
                "          responder='juliet@capulet.lit/balcony'\n" +
                "          sid='a73sjjvkla37jfea'>\n" +
                "    <content creator='initiator' name='ex'>\n" +
                "      <description xmlns='urn:xmpp:example'/>\n" +
                "      <transport xmlns='urn:xmpp:jingle:transports:s5b:1'\n" +
                "                 dstaddr='1a12fb7bc625e55f3ed5b29a53dbe0e4aa7d80ba'\n" +
                "                 mode='tcp'\n" +
                "                 sid='vj3hs98y'>\n" +
                "        <candidate cid='ht567dq'\n" +
                "                   host='192.169.1.10'\n" +
                "                   jid='juliet@capulet.lit/balcony'\n" +
                "                   port='6539'\n" +
                "                   priority='8257636'\n" +
                "                   type='direct'/>\n" +
                "        <candidate cid='grt654q2'\n" +
                "                   host='2001:638:708:30c9:219:d1ff:fea4:a17d'\n" +
                "                   jid='juliet@capulet.lit/balcony'\n" +
                "                   port='6539'\n" +
                "                   priority='8257606'\n" +
                "                   type='tunnel'/>\n" +
                "        <candidate cid='hr65dqyd'\n" +
                "                   host='134.102.201.180'\n" +
                "                   jid='juliet@capulet.lit/balcony'\n" +
                "                   port='16453'\n" +
                "                   priority='7929856'\n" +
                "                   type='assisted'/>\n" +
                "        <candidate cid='pzv14s74'\n" +
                "                   host='234.567.8.9'\n" +
                "                   jid='proxy.marlowe.lit'\n" +
                "                   port='7676'\n" +
                "                   priority='7788877'\n" +
                "                   type='proxy'/>\n" +
                "      </transport>\n" +
                "    </content>\n" +
                "  </jingle>\n";

        Jingle jingle = unmarshal(xml, Jingle.class);
        Assert.assertNotNull(jingle);
        Assert.assertTrue(jingle.getContents().get(0).getTransportMethod() instanceof S5bTransportMethod);
        S5bTransportMethod s5bTransportMethod = (S5bTransportMethod) jingle.getContents().get(0).getTransportMethod();
        Assert.assertNotNull(s5bTransportMethod);
        Assert.assertNull(jingle.getContents().get(0).getApplicationFormat());
        Assert.assertEquals(s5bTransportMethod.getDstAddr(), "1a12fb7bc625e55f3ed5b29a53dbe0e4aa7d80ba");
        Assert.assertEquals(s5bTransportMethod.getSessionId(), "vj3hs98y");
        Assert.assertEquals(s5bTransportMethod.getMode(), Socks5ByteStream.Mode.TCP);
        Assert.assertEquals(s5bTransportMethod.getCandidates().size(), 4);
        Assert.assertEquals(s5bTransportMethod.getCandidates().get(0).getCid(), "ht567dq");
        Assert.assertEquals(s5bTransportMethod.getCandidates().get(0).getHostname(), "192.169.1.10");
        Assert.assertEquals(s5bTransportMethod.getCandidates().get(0).getJid(), Jid.of("juliet@capulet.lit/balcony"));
        Assert.assertEquals(s5bTransportMethod.getCandidates().get(0).getPort(), 6539);
        Assert.assertEquals(s5bTransportMethod.getCandidates().get(0).getPriority(), 8257636);
        Assert.assertEquals(s5bTransportMethod.getCandidates().get(0).getType(), S5bTransportMethod.Candidate.Type.DIRECT);

        Assert.assertEquals(s5bTransportMethod.getCandidates().get(1).getType(), S5bTransportMethod.Candidate.Type.TUNNEL);

        Assert.assertEquals(s5bTransportMethod.getCandidates().get(2).getType(), S5bTransportMethod.Candidate.Type.ASSISTED);

        Assert.assertEquals(s5bTransportMethod.getCandidates().get(3).getJid(), Jid.of("proxy.marlowe.lit"));
        Assert.assertEquals(s5bTransportMethod.getCandidates().get(3).getType(), S5bTransportMethod.Candidate.Type.PROXY);
    }

    @Test
    public void testUnmarshalCandidateUsed() throws JAXBException, XMLStreamException {
        String xml = "<transport xmlns='urn:xmpp:jingle:transports:s5b:1'\n" +
                "                 sid='vj3hs98y'>\n" +
                "        <candidate-used cid='hr65dqyd'/>\n" +
                "      </transport>\n";

        S5bTransportMethod transportMethod = unmarshal(xml, S5bTransportMethod.class);
        Assert.assertEquals(transportMethod.getCandidateUsed(), "hr65dqyd");
    }

    @Test
    public void testMarshalCandidateUsed() throws JAXBException, XMLStreamException {
        String xml = "<transport xmlns=\"urn:xmpp:jingle:transports:s5b:1\" sid=\"1234\"><candidate-used cid=\"789\"></candidate-used></transport>";
        S5bTransportMethod transportMethod = S5bTransportMethod.candidateUsed("1234", "789");
        Assert.assertEquals(marshal(transportMethod), xml);
    }

    @Test
    public void testUnmarshalCandidateError() throws JAXBException, XMLStreamException {
        String xml = "<transport xmlns='urn:xmpp:jingle:transports:s5b:1'\n" +
                "                 sid='vj3hs98y'>\n" +
                "        <candidate-error/>\n" +
                "      </transport>\n";

        S5bTransportMethod transportMethod = unmarshal(xml, S5bTransportMethod.class);
        Assert.assertEquals(transportMethod.getSessionId(), "vj3hs98y");
        Assert.assertTrue(transportMethod.isCandidateError());
    }

    @Test
    public void testMarshalCandidateError() throws JAXBException, XMLStreamException {
        String xml = "<transport xmlns=\"urn:xmpp:jingle:transports:s5b:1\" sid=\"1234\"><candidate-error></candidate-error></transport>";
        S5bTransportMethod transportMethod = S5bTransportMethod.candidateError("1234");
        Assert.assertEquals(marshal(transportMethod), xml);
    }

    @Test
    public void testUnmarshalProxyError() throws JAXBException, XMLStreamException {
        String xml = "<transport xmlns='urn:xmpp:jingle:transports:s5b:1'\n" +
                "                 sid='vj3hs98y'>\n" +
                "        <proxy-error/>\n" +
                "      </transport>\n";

        S5bTransportMethod transportMethod = unmarshal(xml, S5bTransportMethod.class);
        Assert.assertEquals(transportMethod.getSessionId(), "vj3hs98y");
        Assert.assertTrue(transportMethod.isProxyError());
    }

    @Test
    public void testMarshalProxyError() throws JAXBException, XMLStreamException {
        String xml = "<transport xmlns=\"urn:xmpp:jingle:transports:s5b:1\" sid=\"1234\"><proxy-error></proxy-error></transport>";
        S5bTransportMethod transportMethod = S5bTransportMethod.proxyError("1234");
        Assert.assertEquals(marshal(transportMethod), xml);
    }


    @Test
    public void testUnmarshalCandidateActivated() throws JAXBException, XMLStreamException {
        String xml = "<transport xmlns='urn:xmpp:jingle:transports:s5b:1'\n" +
                "                 sid='vj3hs98y'>\n" +
                "        <activated cid='xmdh4b7i'/>\n" +
                "      </transport>\n";

        S5bTransportMethod transportMethod = unmarshal(xml, S5bTransportMethod.class);
        Assert.assertEquals(transportMethod.getSessionId(), "vj3hs98y");
        Assert.assertEquals(transportMethod.getActivated(), "xmdh4b7i");
    }

    @Test
    public void testMarshalCandidateActivated() throws JAXBException, XMLStreamException {
        String xml = "<transport xmlns=\"urn:xmpp:jingle:transports:s5b:1\" sid=\"1234\"><activated cid=\"555\"></activated></transport>";
        S5bTransportMethod transportMethod = S5bTransportMethod.activated("1234", "555");
        Assert.assertEquals(marshal(transportMethod), xml);
    }

    @Test
    public void testCandidatePriority() {
        List<S5bTransportMethod.Candidate> candidateList = new ArrayList<>();
        S5bTransportMethod.Candidate candidate1 = new S5bTransportMethod.Candidate("123", "host1", 0, Jid.of("test"), S5bTransportMethod.Candidate.Type.DIRECT, S5bTransportMethod.calculatePriority(S5bTransportMethod.Candidate.Type.DIRECT, 1));
        S5bTransportMethod.Candidate candidate2 = new S5bTransportMethod.Candidate("123", "host1", 0, Jid.of("test"), S5bTransportMethod.Candidate.Type.ASSISTED, S5bTransportMethod.calculatePriority(S5bTransportMethod.Candidate.Type.ASSISTED, 65538));
        S5bTransportMethod.Candidate candidate3 = new S5bTransportMethod.Candidate("123", "host1", 0, Jid.of("test"), S5bTransportMethod.Candidate.Type.TUNNEL, S5bTransportMethod.calculatePriority(S5bTransportMethod.Candidate.Type.TUNNEL, 3));
        S5bTransportMethod.Candidate candidate4 = new S5bTransportMethod.Candidate("123", "host1", 0, Jid.of("test"), S5bTransportMethod.Candidate.Type.PROXY, S5bTransportMethod.calculatePriority(S5bTransportMethod.Candidate.Type.PROXY, 65536));
        candidateList.add(candidate1);
        candidateList.add(candidate2);
        candidateList.add(candidate3);
        candidateList.add(candidate4);
        Collections.shuffle(candidateList);

        candidateList.sort(null);

        Assert.assertEquals(candidateList.get(0), candidate1);
        Assert.assertEquals(candidateList.get(1), candidate2);
        Assert.assertEquals(candidateList.get(2), candidate3);
        Assert.assertEquals(candidateList.get(3), candidate4);
    }

    @Test
    public void testPriority() {
        Assert.assertEquals(S5bTransportMethod.calculatePriority(S5bTransportMethod.Candidate.Type.DIRECT, 226), 8257636);
        Assert.assertEquals(S5bTransportMethod.calculatePriority(S5bTransportMethod.Candidate.Type.DIRECT, 196), 8257606);
        Assert.assertEquals(S5bTransportMethod.calculatePriority(S5bTransportMethod.Candidate.Type.ASSISTED, 65656), 7929856);
        Assert.assertEquals(S5bTransportMethod.calculatePriority(S5bTransportMethod.Candidate.Type.PROXY, 7133527), 7788877);
    }
}
