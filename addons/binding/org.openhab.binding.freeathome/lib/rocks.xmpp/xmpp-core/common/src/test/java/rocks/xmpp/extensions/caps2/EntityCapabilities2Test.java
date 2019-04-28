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

package rocks.xmpp.extensions.caps2;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.caps.model.EntityCapabilities;
import rocks.xmpp.extensions.caps2.model.EntityCapabilities2;
import rocks.xmpp.extensions.disco.model.info.InfoDiscovery;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author Christian Schudt
 */
public class EntityCapabilities2Test extends XmlTest {
    protected EntityCapabilities2Test() throws JAXBException {
        super(InfoDiscovery.class, EntityCapabilities2.class);
    }

    @Test
    public void unmarshal() throws JAXBException, XMLStreamException {
        String xml = "<c xmlns=\"urn:xmpp:caps\">\n" +
                "    <hash xmlns=\"urn:xmpp:hashes:2\" algo=\"sha-256\">K1Njy3HZBThlo4moOD5gBGhn0U0oK7/CbfLlIUDi6o4=</hash>\n" +
                "    <hash xmlns=\"urn:xmpp:hashes:2\" algo=\"sha3-256\">+sDTQqBmX6iG/X3zjt06fjZMBBqL/723knFIyRf0sg8=</hash>\n" +
                "  </c>";
        EntityCapabilities entityCapabilities = unmarshal(xml, EntityCapabilities2.class);

        Assert.assertNotNull(entityCapabilities);
        Assert.assertEquals(entityCapabilities.getCapabilityHashSet().size(), 2);
    }

    @Test
    public void generateSimpleVerificationString() throws JAXBException, XMLStreamException, NoSuchAlgorithmException {
        String xml = "<query xmlns=\"http://jabber.org/protocol/disco#info\">\n" +
                "  <identity category=\"client\" name=\"BombusMod\" type=\"mobile\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/si\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/bytestreams\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/chatstates\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/disco#info\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/disco#items\"/>\n" +
                "  <feature var=\"urn:xmpp:ping\"/>\n" +
                "  <feature var=\"jabber:iq:time\"/>\n" +
                "  <feature var=\"jabber:iq:privacy\"/>\n" +
                "  <feature var=\"jabber:iq:version\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/rosterx\"/>\n" +
                "  <feature var=\"urn:xmpp:time\"/>\n" +
                "  <feature var=\"jabber:x:oob\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/ibb\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/si/profile/file-transfer\"/>\n" +
                "  <feature var=\"urn:xmpp:receipts\"/>\n" +
                "  <feature var=\"jabber:iq:roster\"/>\n" +
                "  <feature var=\"jabber:iq:last\"/>\n" +
                "</query>";
        InfoDiscovery infoDiscovery = unmarshal(xml, InfoDiscovery.class);
        EntityCapabilities caps = new EntityCapabilities2(infoDiscovery, MessageDigest.getInstance("SHA-256"));
        Assert.assertEquals(caps.getCapabilityHashSet().size(), 1);
        Assert.assertEquals(Base64.getEncoder().encodeToString(caps.getCapabilityHashSet().iterator().next().getHashValue()), "kzBZbkqJ3ADrj7v08reD1qcWUwNGHaidNUgD7nHpiw8=");
    }

    @Test
    public void generateComplexVerificationString() throws JAXBException, XMLStreamException, NoSuchAlgorithmException {
        String xml = "<query xmlns=\"http://jabber.org/protocol/disco#info\">\n" +
                "  <identity category=\"client\" name=\"Tkabber\" type=\"pc\" xml:lang=\"en\"/>\n" +
                "  <identity category=\"client\" name=\"Ткаббер\" type=\"pc\" xml:lang=\"ru\"/>\n" +
                "  <feature var=\"games:board\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/activity\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/activity+notify\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/bytestreams\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/chatstates\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/commands\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/disco#info\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/disco#items\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/evil\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/feature-neg\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/geoloc\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/geoloc+notify\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/ibb\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/iqibb\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/mood\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/mood+notify\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/rosterx\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/si\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/si/profile/file-transfer\"/>\n" +
                "  <feature var=\"http://jabber.org/protocol/tune\"/>\n" +
                "  <feature var=\"http://www.facebook.com/xmpp/messages\"/>\n" +
                "  <feature var=\"http://www.xmpp.org/extensions/xep-0084.html#ns-metadata+notify\"/>\n" +
                "  <feature var=\"jabber:iq:avatar\"/>\n" +
                "  <feature var=\"jabber:iq:browse\"/>\n" +
                "  <feature var=\"jabber:iq:dtcp\"/>\n" +
                "  <feature var=\"jabber:iq:filexfer\"/>\n" +
                "  <feature var=\"jabber:iq:ibb\"/>\n" +
                "  <feature var=\"jabber:iq:inband\"/>\n" +
                "  <feature var=\"jabber:iq:jidlink\"/>\n" +
                "  <feature var=\"jabber:iq:last\"/>\n" +
                "  <feature var=\"jabber:iq:oob\"/>\n" +
                "  <feature var=\"jabber:iq:privacy\"/>\n" +
                "  <feature var=\"jabber:iq:roster\"/>\n" +
                "  <feature var=\"jabber:iq:time\"/>\n" +
                "  <feature var=\"jabber:iq:version\"/>\n" +
                "  <feature var=\"jabber:x:data\"/>\n" +
                "  <feature var=\"jabber:x:event\"/>\n" +
                "  <feature var=\"jabber:x:oob\"/>\n" +
                "  <feature var=\"urn:xmpp:avatar:metadata+notify\"/>\n" +
                "  <feature var=\"urn:xmpp:ping\"/>\n" +
                "  <feature var=\"urn:xmpp:receipts\"/>\n" +
                "  <feature var=\"urn:xmpp:time\"/>\n" +
                "  <x xmlns=\"jabber:x:data\" type=\"result\">\n" +
                "    <field type=\"hidden\" var=\"FORM_TYPE\">\n" +
                "      <value>urn:xmpp:dataforms:softwareinfo</value>\n" +
                "    </field>\n" +
                "    <field var=\"software\">\n" +
                "      <value>Tkabber</value>\n" +
                "    </field>\n" +
                "    <field var=\"software_version\">\n" +
                "      <value>0.11.1-svn-20111216-mod (Tcl/Tk 8.6b2)</value>\n" +
                "    </field>\n" +
                "    <field var=\"os\">\n" +
                "      <value>Windows</value>\n" +
                "    </field>\n" +
                "    <field var=\"os_version\">\n" +
                "      <value>XP</value>\n" +
                "    </field>\n" +
                "  </x>\n" +
                "</query>";
        InfoDiscovery infoDiscovery = unmarshal(xml, InfoDiscovery.class);
        EntityCapabilities caps = new EntityCapabilities2(infoDiscovery, MessageDigest.getInstance("SHA-256"));
        Assert.assertEquals(caps.getCapabilityHashSet().size(), 1);
        Assert.assertEquals(Base64.getEncoder().encodeToString(caps.getCapabilityHashSet().iterator().next().getHashValue()), "u79ZroNJbdSWhdSp311mddz44oHHPsEBntQ5b1jqBSY=");
    }
}
