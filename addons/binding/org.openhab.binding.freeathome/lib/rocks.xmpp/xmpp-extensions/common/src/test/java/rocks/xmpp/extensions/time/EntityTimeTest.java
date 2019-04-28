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

package rocks.xmpp.extensions.time;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.time.model.EntityTime;
import rocks.xmpp.util.adapters.ZoneOffsetAdapter;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * @author Christian Schudt
 */
public class EntityTimeTest extends XmlTest {

    protected EntityTimeTest() throws JAXBException {
        super(ClientIQ.class, EntityTime.class);
    }


    @Test
    public void marshalEntityTimeRequest() throws XMLStreamException, JAXBException {
        IQ iq = new IQ(IQ.Type.GET, new EntityTime(), "time_1");
        String xml = marshal(iq);
        Assert.assertEquals(xml, "<iq id=\"time_1\" type=\"get\"><time xmlns=\"urn:xmpp:time\"></time></iq>");
    }

    @Test
    public void unmarshalEntityTimeResponse() throws XMLStreamException, JAXBException {
        String xml = "<iq type='result'\n" +
                "    from='juliet@capulet.com/balcony'\n" +
                "    to='romeo@montague.net/orchard'\n" +
                "    id='time_1'>\n" +
                "  <time xmlns='urn:xmpp:time'>\n" +
                "    <tzo>-06:00</tzo>\n" +
                "    <utc>2006-12-19T17:58:35Z</utc>\n" +
                "  </time>\n" +
                "</iq>";

        IQ iq = unmarshal(xml, IQ.class);
        EntityTime entityTime = iq.getExtension(EntityTime.class);
        Assert.assertNotNull(entityTime);
        Assert.assertEquals(entityTime.getDateTime().getHour(), 11);
    }

    @Test
    public void testZoneOffsetAdapter() {
        ZoneOffsetAdapter adapter = new ZoneOffsetAdapter();
        ZoneOffset zoneOffset = ZoneOffset.of("-08:00");
        String str = adapter.marshal(zoneOffset);
        Assert.assertEquals(str, "-08:00");

        ZoneId timeZoneUtc = adapter.unmarshal("Z");
        Assert.assertEquals(timeZoneUtc, ZoneOffset.UTC);
    }

    @Test
    public void marshalEntityTimeResponse() throws Exception {
        OffsetDateTime offsetDateTime = OffsetDateTime.of(LocalDateTime.of(2015, 3, 22, 20, 48, 11), ZoneOffset.ofHours(-2));
        String xml = marshal(new EntityTime(offsetDateTime));
        Assert.assertEquals(xml, "<time xmlns=\"urn:xmpp:time\"><tzo>-02:00</tzo><utc>2015-03-22T22:48:11Z</utc></time>");
    }
}
