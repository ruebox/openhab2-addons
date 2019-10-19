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

package rocks.xmpp.extensions.idle;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.core.stanza.model.client.ClientPresence;
import rocks.xmpp.extensions.delay.model.DelayedDelivery;
import rocks.xmpp.extensions.idle.model.Idle;
import rocks.xmpp.extensions.last.model.LastActivity;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.time.Instant;
import java.time.OffsetDateTime;

/**
 * @author Christian Schudt
 */
public class IdleTest extends XmlTest {

    protected IdleTest() throws JAXBException {
        super(ClientPresence.class, Idle.class, LastActivity.class, DelayedDelivery.class);
    }

    @Test
    public void unmarshalIdle() throws JAXBException, XMLStreamException {
        String xml = "<presence from='juliet@capulet.com/balcony'>\n" +
                "  <show>away</show>\n" +
                "  <idle xmlns='urn:xmpp:idle:1' since='1969-07-21T02:56:15Z'/>\n" +
                "</presence>\n";
        Presence presence = unmarshal(xml, Presence.class);
        Idle idle = presence.getExtension(Idle.class);
        Assert.assertNotNull(idle);
        Assert.assertEquals(idle.getSince(), OffsetDateTime.parse("1969-07-21T02:56:15Z"));
    }

    @Test
    public void marshalIdle() throws JAXBException, XMLStreamException {
        OffsetDateTime now = OffsetDateTime.now();
        Idle idle = Idle.since(now);
        String xml = marshal(idle);
        Assert.assertNotNull(idle);
        Assert.assertEquals(xml, "<idle xmlns=\"urn:xmpp:idle:1\" since=\"" + now.toString() + "\"></idle>");
    }

    @Test
    public void testIdleSinceXEP0256() throws JAXBException, XMLStreamException {
        String xml = "<presence from='juliet@capulet.com/balcony' to='romeo@montague.net'>\n" +
                "  <show>away</show>\n" +
                "  <query xmlns='jabber:iq:last' seconds='600'/>\n" +
                "  <delay xmlns='urn:xmpp:delay'\n" +
                "     from='capulet.com'\n" +
                "     stamp='2002-09-10T23:41:07Z'/>\n" +
                "</presence>\n";
        Presence presence = unmarshal(xml, Presence.class);
        Instant instant = Idle.timeFromPresence(presence);
        Assert.assertEquals(instant, Instant.parse("2002-09-10T23:31:07Z"));
    }

    @Test
    public void testIdleSinceXEP0319() throws JAXBException, XMLStreamException {
        String xml = "<presence from='juliet@capulet.com/balcony'>\n" +
                "  <show>away</show>\n" +
                "  <idle xmlns='urn:xmpp:idle:1' since='1969-07-21T02:56:15Z'/>\n" +
                "</presence>\n";
        Presence presence = unmarshal(xml, Presence.class);
        Instant instant = Idle.timeFromPresence(presence);
        Assert.assertEquals(instant, Instant.parse("1969-07-21T02:56:15Z"));
    }
}
