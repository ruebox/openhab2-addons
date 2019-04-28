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

package rocks.xmpp.extensions.muc;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.muc.model.Affiliation;
import rocks.xmpp.extensions.muc.model.Role;
import rocks.xmpp.extensions.muc.model.admin.MucAdmin;
import rocks.xmpp.extensions.muc.model.owner.MucOwner;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class MultiUserChatOwnerTest extends XmlTest {
    protected MultiUserChatOwnerTest() throws JAXBException {
        super(ClientIQ.class, MucOwner.class, MucAdmin.class);
    }

    @Test
    public void testKickOccupant() throws JAXBException, XMLStreamException {
        String xml = "<iq from='fluellen@shakespeare.lit/pda'\n" +
                "    id='kick1'\n" +
                "    to='harfleur@chat.shakespeare.lit'\n" +
                "    type='set'>\n" +
                "  <query xmlns='http://jabber.org/protocol/muc#admin'>\n" +
                "    <item nick='pistol' role='none'>\n" +
                "      <reason>Avaunt, you cullion!</reason>\n" +
                "    </item>\n" +
                "  </query>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        MucAdmin mucAdmin = iq.getExtension(MucAdmin.class);
        Assert.assertNotNull(mucAdmin);
        Assert.assertNotNull(mucAdmin.getItems());
        Assert.assertEquals(mucAdmin.getItems().get(0).getNick(), "pistol");
        Assert.assertEquals(mucAdmin.getItems().get(0).getRole(), Role.NONE);
    }

    @Test
    public void testGrantVoice() throws JAXBException, XMLStreamException {
        String xml = "<iq from='crone1@shakespeare.lit/desktop'\n" +
                "    id='voice1'\n" +
                "    to='coven@chat.shakespeare.lit'\n" +
                "    type='set'>\n" +
                "  <query xmlns='http://jabber.org/protocol/muc#admin'>\n" +
                "    <item nick='thirdwitch'\n" +
                "          role='participant'/>\n" +
                "  </query>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        MucAdmin mucAdmin = iq.getExtension(MucAdmin.class);
        Assert.assertNotNull(mucAdmin);
        Assert.assertNotNull(mucAdmin.getItems());
        Assert.assertEquals(mucAdmin.getItems().get(0).getNick(), "thirdwitch");
        Assert.assertEquals(mucAdmin.getItems().get(0).getRole(), Role.PARTICIPANT);
    }

    @Test
    public void testRevokeVoice() throws JAXBException, XMLStreamException {
        String xml = "<iq from='crone1@shakespeare.lit/desktop'\n" +
                "    id='voice2'\n" +
                "    to='coven@chat.shakespeare.lit'\n" +
                "    type='set'>\n" +
                "  <query xmlns='http://jabber.org/protocol/muc#admin'>\n" +
                "    <item nick='thirdwitch'\n" +
                "          role='visitor'>\n" +
                "      <reason>Not so worthy after all!</reason>\n" +
                "    </item>\n" +
                "  </query>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        MucAdmin mucAdmin = iq.getExtension(MucAdmin.class);
        Assert.assertNotNull(mucAdmin);
        Assert.assertNotNull(mucAdmin.getItems());
        Assert.assertEquals(mucAdmin.getItems().get(0).getNick(), "thirdwitch");
        Assert.assertEquals(mucAdmin.getItems().get(0).getRole(), Role.VISITOR);
        Assert.assertEquals(mucAdmin.getItems().get(0).getReason(), "Not so worthy after all!");
    }

    @Test
    public void testBanUser() throws JAXBException, XMLStreamException {
        String xml = "<iq from='kinghenryv@shakespeare.lit/throne'\n" +
                "    id='ban1'\n" +
                "    to='southampton@chat.shakespeare.lit'\n" +
                "    type='set'>\n" +
                "  <query xmlns='http://jabber.org/protocol/muc#admin'>\n" +
                "    <item affiliation='outcast'\n" +
                "          jid='earlofcambridge@shakespeare.lit'>\n" +
                "      <reason>Treason</reason>\n" +
                "    </item>\n" +
                "  </query>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        MucAdmin mucAdmin = iq.getExtension(MucAdmin.class);
        Assert.assertNotNull(mucAdmin);
        Assert.assertNotNull(mucAdmin.getItems());
        Assert.assertEquals(mucAdmin.getItems().get(0).getJid(), Jid.of("earlofcambridge@shakespeare.lit"));
        Assert.assertEquals(mucAdmin.getItems().get(0).getAffiliation(), Affiliation.OUTCAST);
        Assert.assertEquals(mucAdmin.getItems().get(0).getReason(), "Treason");
    }

    @Test
    public void marshalCreateInstantRoom() throws JAXBException, XMLStreamException {
        MucOwner mucOwner = MucOwner.withConfiguration(new DataForm(DataForm.Type.SUBMIT));
        String xml = marshal(mucOwner);
        Assert.assertEquals(xml, "<query xmlns=\"http://jabber.org/protocol/muc#owner\"><x xmlns=\"jabber:x:data\" type=\"submit\"></x></query>");
    }

    @Test
    public void marshalRequestConfigurationForm() throws JAXBException, XMLStreamException {
        MucOwner mucOwner = MucOwner.empty();
        String xml = marshal(mucOwner);
        Assert.assertEquals(xml, "<query xmlns=\"http://jabber.org/protocol/muc#owner\"></query>");
    }

    @Test
    public void unmarshalConfigurationForm() throws JAXBException, XMLStreamException {
        String xml = "<iq from='coven@chat.shakespeare.lit'\n" +
                "    id='config1'\n" +
                "    to='crone1@shakespeare.lit/desktop'\n" +
                "    type='result'>\n" +
                "  <query xmlns='http://jabber.org/protocol/muc#owner'>\n" +
                "    <x xmlns='jabber:x:data' type='form'>\n" +
                "      <title>Configuration for \"coven\" Room</title>\n" +
                "    </x>\n" +
                "  </query>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        MucOwner mucOwner = iq.getExtension(MucOwner.class);
        Assert.assertNotNull(mucOwner);
        Assert.assertNotNull(mucOwner.getConfigurationForm());
    }

    @Test
    public void marshalDestroyRoom() throws JAXBException, XMLStreamException {
        MucOwner mucOwner = MucOwner.withDestroy(Jid.of("coven@chat.shakespeare.lit"), "Macbeth doth come.");
        String xml = marshal(mucOwner);
        Assert.assertEquals(xml, "<query xmlns=\"http://jabber.org/protocol/muc#owner\"><destroy jid=\"coven@chat.shakespeare.lit\"><reason>Macbeth doth come.</reason></destroy></query>");
    }
}
