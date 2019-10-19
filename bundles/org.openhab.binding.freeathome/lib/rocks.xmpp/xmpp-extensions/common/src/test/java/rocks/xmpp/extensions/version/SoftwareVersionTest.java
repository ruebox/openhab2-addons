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

package rocks.xmpp.extensions.version;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.version.model.SoftwareVersion;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class SoftwareVersionTest extends XmlTest {
    protected SoftwareVersionTest() throws JAXBException {
        super(ClientIQ.class, SoftwareVersion.class);
    }

    @Test
    public void unmarshalSoftwareVersionRequest() throws XMLStreamException, JAXBException {
        String xml = "<iq\n" +
                "    type='get'\n" +
                "    from='romeo@montague.net/orchard'\n" +
                "    to='juliet@capulet.com/balcony'\n" +
                "    id='version_1'>\n" +
                "  <query xmlns='jabber:iq:version'/>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        SoftwareVersion softwareVersion = iq.getExtension(SoftwareVersion.class);
        Assert.assertNotNull(softwareVersion);
    }

    @Test
    public void unmarshalSearchResponse() throws XMLStreamException, JAXBException {
        String xml = "<iq\n" +
                "    type='result'\n" +
                "    to='romeo@montague.net/orchard'\n" +
                "    from='juliet@capulet.com/balcony'\n" +
                "    id='version_1'>\n" +
                "  <query xmlns='jabber:iq:version'>\n" +
                "    <name>Exodus</name>\n" +
                "    <version>0.7.0.4</version>\n" +
                "    <os>Windows-XP 5.01.2600</os>\n" +
                "  </query>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        SoftwareVersion softwareVersion = iq.getExtension(SoftwareVersion.class);
        Assert.assertNotNull(softwareVersion);
        Assert.assertEquals(softwareVersion.getName(), "Exodus");
        Assert.assertEquals(softwareVersion.getVersion(), "0.7.0.4");
        Assert.assertEquals(softwareVersion.getOs(), "Windows-XP 5.01.2600");
    }

    @Test
    public void marshalSoftwareVersion() throws JAXBException, XMLStreamException {
        SoftwareVersion softwareVersion = new SoftwareVersion("Babbler", "1.0");
        String xml = marshal(softwareVersion);
        Assert.assertEquals("<query xmlns=\"jabber:iq:version\"><name>Babbler</name><version>1.0</version><os>" + System.getProperty("os.name") + "</os></query>", xml);
    }
}
