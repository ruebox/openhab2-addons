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

package rocks.xmpp.extensions.httpbind;


import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.core.stanza.model.client.ClientPresence;
import rocks.xmpp.extensions.httpbind.model.Body;
import rocks.xmpp.im.roster.model.Roster;
import rocks.xmpp.util.ComparableTestHelper;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Christian Schudt
 */
public class BoshTest extends XmlTest {

    protected BoshTest() throws JAXBException {
        super(ClientIQ.class, Roster.class, ClientPresence.class, Body.class);
    }

    @Test
    public void unmarshalHostGone() throws XMLStreamException, JAXBException {
        String xml = "<body type='terminate'\n" +
                "      condition='host-gone'\n" +
                "      xmlns='http://jabber.org/protocol/httpbind'/>";
        Body body = unmarshal(xml, Body.class);
        Assert.assertEquals(body.getType(), Body.Type.TERMINATE);
        Assert.assertEquals(body.getCondition(), Body.Condition.HOST_GONE);
    }

    @Test
    public void unmarshalHostUnknown() throws XMLStreamException, JAXBException {
        String xml = "<body type='terminate'\n" +
                "      condition='host-unknown'\n" +
                "      xmlns='http://jabber.org/protocol/httpbind'/>";
        Body body = unmarshal(xml, Body.class);
        Assert.assertEquals(body.getType(), Body.Type.TERMINATE);
        Assert.assertEquals(body.getCondition(), Body.Condition.HOST_UNKNOWN);
    }

    @Test
    public void unmarshalBadRequest() throws XMLStreamException, JAXBException {
        String xml = "<body type='terminate'\n" +
                "      condition='bad-request'\n" +
                "      xmlns='http://jabber.org/protocol/httpbind'/>";
        Body body = unmarshal(xml, Body.class);
        Assert.assertEquals(body.getType(), Body.Type.TERMINATE);
        Assert.assertEquals(body.getCondition(), Body.Condition.BAD_REQUEST);
    }

    @Test
    public void unmarshalImproperAddressing() throws XMLStreamException, JAXBException {
        String xml = "<body type='terminate'\n" +
                "      condition='improper-addressing'\n" +
                "      xmlns='http://jabber.org/protocol/httpbind'/>";
        Body body = unmarshal(xml, Body.class);
        Assert.assertEquals(body.getType(), Body.Type.TERMINATE);
        Assert.assertEquals(body.getCondition(), Body.Condition.IMPROPER_ADDRESSING);
    }

    @Test
    public void unmarshalInternalServerError() throws XMLStreamException, JAXBException {
        String xml = "<body type='terminate'\n" +
                "      condition='internal-server-error'\n" +
                "      xmlns='http://jabber.org/protocol/httpbind'/>";
        Body body = unmarshal(xml, Body.class);
        Assert.assertEquals(body.getType(), Body.Type.TERMINATE);
        Assert.assertEquals(body.getCondition(), Body.Condition.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void unmarshalItemNotFound() throws XMLStreamException, JAXBException {
        String xml = "<body type='terminate'\n" +
                "      condition='item-not-found'\n" +
                "      xmlns='http://jabber.org/protocol/httpbind'/>";
        Body body = unmarshal(xml, Body.class);
        Assert.assertEquals(body.getType(), Body.Type.TERMINATE);
        Assert.assertEquals(body.getCondition(), Body.Condition.ITEM_NOT_FOUND);
    }

    @Test
    public void unmarshalOtherRequest() throws XMLStreamException, JAXBException {
        String xml = "<body type='terminate'\n" +
                "      condition='other-request'\n" +
                "      xmlns='http://jabber.org/protocol/httpbind'/>";
        Body body = unmarshal(xml, Body.class);
        Assert.assertEquals(body.getType(), Body.Type.TERMINATE);
        Assert.assertEquals(body.getCondition(), Body.Condition.OTHER_REQUEST);
    }

    @Test
    public void unmarshalPolicyViolation() throws XMLStreamException, JAXBException {
        String xml = "<body type='terminate'\n" +
                "      condition='policy-violation'\n" +
                "      xmlns='http://jabber.org/protocol/httpbind'/>";
        Body body = unmarshal(xml, Body.class);
        Assert.assertEquals(body.getType(), Body.Type.TERMINATE);
        Assert.assertEquals(body.getCondition(), Body.Condition.POLICY_VIOLATION);
    }

    @Test
    public void unmarshalRemoteConnectionFailed() throws XMLStreamException, JAXBException {
        String xml = "<body type='terminate'\n" +
                "      condition='remote-connection-failed'\n" +
                "      xmlns='http://jabber.org/protocol/httpbind'/>";
        Body body = unmarshal(xml, Body.class);
        Assert.assertEquals(body.getType(), Body.Type.TERMINATE);
        Assert.assertEquals(body.getCondition(), Body.Condition.REMOTE_CONNECTION_FAILED);
    }

    @Test
    public void unmarshalRemoteStreamError() throws XMLStreamException, JAXBException {
        String xml = "<body type='terminate'\n" +
                "      condition='remote-stream-error'\n" +
                "      xmlns='http://jabber.org/protocol/httpbind'/>";
        Body body = unmarshal(xml, Body.class);
        Assert.assertEquals(body.getType(), Body.Type.TERMINATE);
        Assert.assertEquals(body.getCondition(), Body.Condition.REMOTE_STREAM_ERROR);
    }

    @Test
    public void unmarshalSeeOtherUri() throws XMLStreamException, JAXBException {
        String xml = "<body condition='see-other-uri'\n" +
                "      type='terminate'\n" +
                "      xmlns='http://jabber.org/protocol/httpbind'>\n" +
                "  <uri>https://secure.jabber.org/xmppcm</uri>\n" +
                "</body>";
        Body body = unmarshal(xml, Body.class);
        Assert.assertEquals(body.getUri().toString(), "https://secure.jabber.org/xmppcm");
        Assert.assertEquals(body.getType(), Body.Type.TERMINATE);
        Assert.assertEquals(body.getCondition(), Body.Condition.SEE_OTHER_URI);
    }

    @Test
    public void unmarshalSystemShutdown() throws XMLStreamException, JAXBException {
        String xml = "<body type='terminate'\n" +
                "      condition='system-shutdown'\n" +
                "      xmlns='http://jabber.org/protocol/httpbind'/>";
        Body body = unmarshal(xml, Body.class);
        Assert.assertEquals(body.getType(), Body.Type.TERMINATE);
        Assert.assertEquals(body.getCondition(), Body.Condition.SYSTEM_SHUTDOWN);
    }

    @Test
    public void unmarshalUndefinedCondition() throws XMLStreamException, JAXBException {
        String xml = "<body charsets='ISO_8859-1 ISO-2022-JP'" +
                "      inactivity='86400'\n" +
                "      time='1000'\n" +
                "      type='terminate'\n" +
                "      condition='undefined-condition'\n" +
                "      xmlns='http://jabber.org/protocol/httpbind'/>";
        Body body = unmarshal(xml, Body.class);
        Assert.assertEquals(body.getType(), Body.Type.TERMINATE);
        Assert.assertEquals(body.getCondition(), Body.Condition.UNDEFINED_CONDITION);
        Assert.assertEquals(body.getCharsets().size(), 2);
        Assert.assertEquals(body.getInactivity(), Duration.ofDays(1));
        Assert.assertEquals(body.getTime(), Duration.ofSeconds(1));
    }

    @Test
    public void marshalBodyWithMultipleStanzas() throws XMLStreamException, JAXBException {
        IQ iq = ClientIQ.from(new IQ(IQ.Type.GET, new Roster(), "1"));
        Body body = Body.builder()
                .wrappedObjects(Arrays.asList(iq, ClientPresence.from(new Presence()))).build();

        Assert.assertEquals(marshal(body), "<body xmlns=\"http://jabber.org/protocol/httpbind\"><iq xmlns=\"jabber:client\" id=\"1\" type=\"get\"><query xmlns=\"jabber:iq:roster\"></query></iq><presence xmlns=\"jabber:client\"></presence></body>");
    }

    @Test
    public void marshalBody() throws XMLStreamException, JAXBException {
        Body body = Body.builder()
                .charsets(Charset.forName("ISO_8859-1"), Charset.forName("ISO-2022-JP"))
                .inactivity(Duration.ofDays(1))
                .maxPause(Duration.ofHours(1))
                .pause(Duration.ofSeconds(2))
                .polling(Duration.ofMinutes(1))
                .time(Duration.ofSeconds(1))
                .wait(Duration.ofMinutes(2))
                .build();

        Assert.assertEquals(marshal(body), "<body xmlns=\"http://jabber.org/protocol/httpbind\" charsets=\"ISO-8859-1 ISO-2022-JP\" inactivity=\"86400\" maxpause=\"3600\" pause=\"2\" polling=\"60\" time=\"1000\" wait=\"120\"></body>");
    }

    @Test
    public void testComparable() {
        Body bodyNoRid1 = Body.builder().build();
        Body bodyNoRid2 = Body.builder().build();
        Body body1 = Body.builder().requestId(1).build();
        Body body2 = Body.builder().requestId(2).build();

        List<Body> bodies = new ArrayList<>();
        bodies.add(bodyNoRid1);
        bodies.add(bodyNoRid2);
        bodies.add(body1);
        bodies.add(body2);
        Collections.shuffle(bodies);
        bodies.sort(null);

        Assert.assertSame(bodies.get(2), body1);
        Assert.assertSame(bodies.get(3), body2);
        ComparableTestHelper.checkCompareToContract(bodies);
    }
}
