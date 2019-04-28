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

package rocks.xmpp.extensions.disco;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.disco.model.info.Identity;
import rocks.xmpp.extensions.disco.model.info.InfoDiscovery;
import rocks.xmpp.extensions.disco.model.info.InfoNode;
import rocks.xmpp.extensions.disco.model.items.ItemDiscovery;
import rocks.xmpp.extensions.disco.model.items.ItemNode;
import rocks.xmpp.util.ComparableTestHelper;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

/**
 * @author Christian Schudt
 */
public class ServiceDiscoveryTest extends XmlTest {
    protected ServiceDiscoveryTest() throws JAXBException {
        super(ClientIQ.class, ItemDiscovery.class, InfoDiscovery.class);
    }

    @Test
    public void unmarshalServiceDiscoveryResponse() throws XMLStreamException, JAXBException {
        String xml = "<iq type='result'\n" +
                "    from='plays.shakespeare.lit'\n" +
                "    to='romeo@montague.net/orchard'\n" +
                "    id='info1'>\n" +
                "  <query xmlns='http://jabber.org/protocol/disco#info'>\n" +
                "    <identity\n" +
                "        category='conference'\n" +
                "        type='text'\n" +
                "        name='Play-Specific Chatrooms'/>\n" +
                "    <identity\n" +
                "        category='directory'\n" +
                "        type='chatroom'\n" +
                "        name='Play-Specific Chatrooms'/>\n" +
                "    <feature/>\n" +
                "    <feature xmlns=\"urn:xmpp:archive\"><optional xmlns=\"urn:xmpp:archive\"><default xmlns=\"urn:xmpp:archive\"/></optional></feature>\n" +
                "    <feature var='http://jabber.org/protocol/disco#info'/>\n" +
                "    <feature var='http://jabber.org/protocol/disco#items'/>\n" +
                "    <feature var='http://jabber.org/protocol/muc'/>\n" +
                "    <feature var='jabber:iq:register'/>\n" +
                "    <feature var='jabber:iq:search'/>\n" +
                "    <feature var='jabber:iq:time'/>\n" +
                "    <feature var='jabber:iq:version'/>\n" +
                "  </query>\n" +
                "</iq>";

        IQ iq = unmarshal(xml, IQ.class);
        InfoDiscovery serviceDiscovery = iq.getExtension(InfoDiscovery.class);
        Assert.assertNotNull(serviceDiscovery);
        Assert.assertEquals(serviceDiscovery.getIdentities().size(), 2);
        Assert.assertEquals(serviceDiscovery.getFeatures().size(), 7);

        Identity identity1 = Identity.conferenceText().withName("Play-Specific Chatrooms");
        Identity identity2 = Identity.directoryChatRoom().withName("Play-Specific Chatrooms");

        Assert.assertTrue(serviceDiscovery.getIdentities().contains(identity1));
        Assert.assertTrue(serviceDiscovery.getIdentities().contains(identity2));
        Assert.assertTrue(serviceDiscovery.getFeatures().contains("http://jabber.org/protocol/disco#info"));
        Assert.assertTrue(serviceDiscovery.getFeatures().contains("http://jabber.org/protocol/disco#items"));
        Assert.assertTrue(serviceDiscovery.getFeatures().contains("http://jabber.org/protocol/muc"));
        Assert.assertTrue(serviceDiscovery.getFeatures().contains("jabber:iq:register"));
        Assert.assertTrue(serviceDiscovery.getFeatures().contains("jabber:iq:search"));
        Assert.assertTrue(serviceDiscovery.getFeatures().contains("jabber:iq:time"));
        Assert.assertTrue(serviceDiscovery.getFeatures().contains("jabber:iq:version"));
    }

    @Test
    public void unmarshalServiceDiscoveryItemResponse() throws XMLStreamException, JAXBException {
        String xml = "<iq type='result'\n" +
                "    from='catalog.shakespeare.lit'\n" +
                "    to='romeo@montague.net/orchard'\n" +
                "    id='items2'>\n" +
                "  <query xmlns='http://jabber.org/protocol/disco#items' node='http://jabber.org/protocol/tune'>\n" +
                "    <item jid='catalog.shakespeare.lit'\n" +
                "          node='books'\n" +
                "          name='Books by and about Shakespeare'/>\n" +
                "    <item jid='catalog.shakespeare.lit'\n" +
                "          node='clothing'\n" +
                "          name='Wear your literary taste with pride'/>\n" +
                "    <item jid='catalog.shakespeare.lit'\n" +
                "          node='music'\n" +
                "          name='Music from the time of Shakespeare'/>\n" +
                "  </query>\n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        ItemNode itemNode = iq.getExtension(ItemDiscovery.class);
        Assert.assertNotNull(itemNode);
        Assert.assertEquals(itemNode.getItems().size(), 3);
        Assert.assertEquals(itemNode.getItems().get(0).getJid(), Jid.of("catalog.shakespeare.lit"));
        Assert.assertEquals(itemNode.getItems().get(0).getNode(), "books");
        Assert.assertEquals(itemNode.getItems().get(0).getName(), "Books by and about Shakespeare");
        Assert.assertEquals(itemNode.getItems().get(1).getJid(), Jid.of("catalog.shakespeare.lit"));
        Assert.assertEquals(itemNode.getItems().get(1).getNode(), "clothing");
        Assert.assertEquals(itemNode.getItems().get(1).getName(), "Wear your literary taste with pride");
        Assert.assertEquals(itemNode.getItems().get(2).getJid(), Jid.of("catalog.shakespeare.lit"));
        Assert.assertEquals(itemNode.getItems().get(2).getNode(), "music");
        Assert.assertEquals(itemNode.getItems().get(2).getName(), "Music from the time of Shakespeare");
    }

    @Test
    public void unmarshalServiceDiscoveryExtension() throws JAXBException, XMLStreamException {
        String xml = "<iq type='result'\n" +
                "    from='shakespeare.lit'\n" +
                "    to='capulet.com'\n" +
                "    id='disco1'>\n" +
                "  <query xmlns='http://jabber.org/protocol/disco#info'>\n" +
                "    <identity\n" +
                "        category='server'\n" +
                "        type='im'\n" +
                "        name='shakespeare.lit jabber server'/>\n" +
                "    <feature var='jabber:iq:register'/>\n" +
                "    <x xmlns='jabber:x:data' type='result'>\n" +
                "      <field var='FORM_TYPE' type='hidden'>\n" +
                "        <value>http://jabber.org/network/serverinfo</value>\n" +
                "      </field>\n" +
                "      <field var='c2s_port'>\n" +
                "        <value>5222</value>\n" +
                "      </field>\n" +
                "      <field var='c2s_port_ssl'>\n" +
                "        <value>5223</value>\n" +
                "      </field>\n" +
                "      <field var='http_access'>\n" +
                "        <value>http://shakespeare.lit/jabber</value>\n" +
                "      </field>\n" +
                "      <field var='ip_version'>\n" +
                "        <value>ipv4</value>\n" +
                "        <value>ipv6</value>\n" +
                "      </field>\n" +
                "      <field var='info_url'>\n" +
                "        <value>http://shakespeare.lit/support.php</value>\n" +
                "      </field>\n" +
                "    </x>\n" +
                "  </query>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        InfoNode infoDiscovery = iq.getExtension(InfoDiscovery.class);
        Assert.assertNotNull(infoDiscovery);
        Assert.assertEquals(infoDiscovery.getExtensions().size(), 1);
        Assert.assertEquals(infoDiscovery.getExtensions().get(0).getType(), DataForm.Type.RESULT);
    }

    @Test
    public void unmarshalServiceDiscoveryWithResultSet() throws JAXBException, XMLStreamException {
        String xml = "<iq type='set' from='stpeter@jabber.org/roundabout' to='conference.jabber.org' id='ex3'>\n" +
                "  <query xmlns='http://jabber.org/protocol/disco#items'>\n" +
                "    <set xmlns='http://jabber.org/protocol/rsm'>\n" +
                "      <max>20</max>\n" +
                "      <after>4da91d4b330112f683dddaebf93180b1bd25e95f</after>\n" +
                "    </set>\n" +
                "  </query>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        ItemDiscovery itemDiscovery = iq.getExtension(ItemDiscovery.class);
        Assert.assertNotNull(itemDiscovery);
        Assert.assertNotNull(itemDiscovery.getResultSetManagement());
        Assert.assertEquals(itemDiscovery.getResultSetManagement().getMaxSize(), Integer.valueOf(20));
    }

    @Test
    public void testIdentityCompareTo() {

        Collection<Identity> identities = Arrays.asList(Identity.accountAdmin(),
                Identity.ofCategoryAndType("category1", "type"),
                Identity.ofCategoryAndType("category1", "type1"),
                Identity.ofCategoryAndType("category", "type").withName("name", Locale.ENGLISH),
                Identity.ofCategoryAndType("category", "type").withName("name", Locale.GERMAN),
                Identity.ofCategoryAndType("category", "type").withName("name2", Locale.GERMAN),
                Identity.ofCategoryAndType("category", "type").withName("name2", Locale.GERMAN));

        ComparableTestHelper.checkCompareToContract(identities);
    }
}
