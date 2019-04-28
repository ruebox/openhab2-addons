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
import rocks.xmpp.extensions.muc.model.Affiliation;
import rocks.xmpp.extensions.muc.model.Role;
import rocks.xmpp.extensions.muc.model.admin.MucAdmin;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.util.Arrays;

/**
 * @author Christian Schudt
 */
public class MultiUserChatAdminTest extends XmlTest {

    protected MultiUserChatAdminTest() throws JAXBException {
        super(ClientIQ.class, MucAdmin.class);
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
    public void marshalBanUser() throws JAXBException, XMLStreamException {
        MucAdmin mucAdmin = MucAdmin.withItem(Affiliation.OUTCAST, Jid.of("earlofcambridge@shakespeare.lit"), null);
        String xml = marshal(mucAdmin);
        Assert.assertEquals(xml, "<query xmlns=\"http://jabber.org/protocol/muc#admin\"><item affiliation=\"outcast\" jid=\"earlofcambridge@shakespeare.lit\"></item></query>");
    }

    @Test
    public void marshalRequestBanlist() throws JAXBException, XMLStreamException {
        MucAdmin mucAdmin = MucAdmin.withItem(Affiliation.OUTCAST);
        String xml = marshal(mucAdmin);
        Assert.assertEquals(xml, "<query xmlns=\"http://jabber.org/protocol/muc#admin\"><item affiliation=\"outcast\"></item></query>");
    }

    @Test
    public void marshalModifiedBanlist() throws JAXBException, XMLStreamException {
        MucAdmin mucAdmin = MucAdmin.withItems(MucAdmin.createItem(Affiliation.OUTCAST, Jid.of("lordscroop@shakespeare.lit"), "Treason"), MucAdmin.createItem(Affiliation.OUTCAST, Jid.of("sirthomasgrey@shakespeare.lit"), "Treason"));
        String xml = marshal(mucAdmin);
        Assert.assertEquals(xml, "<query xmlns=\"http://jabber.org/protocol/muc#admin\"><item affiliation=\"outcast\" jid=\"lordscroop@shakespeare.lit\"><reason>Treason</reason></item><item affiliation=\"outcast\" jid=\"sirthomasgrey@shakespeare.lit\"><reason>Treason</reason></item></query>");
    }

    @Test
    public void marshalGrantMembership() throws JAXBException, XMLStreamException {
        MucAdmin mucAdmin = MucAdmin.withItem(Affiliation.MEMBER, Jid.of("hag66@shakespeare.lit"), "A worthy witch indeed!");
        String xml = marshal(mucAdmin);
        Assert.assertEquals(xml, "<query xmlns=\"http://jabber.org/protocol/muc#admin\"><item affiliation=\"member\" jid=\"hag66@shakespeare.lit\"><reason>A worthy witch indeed!</reason></item></query>");
    }

    @Test
    public void marshalGrantModerator() throws JAXBException, XMLStreamException {
        MucAdmin mucAdmin = MucAdmin.withItem(Role.MODERATOR, "thirdwitch", "A worthy witch indeed!");
        String xml = marshal(mucAdmin);
        Assert.assertEquals(xml, "<query xmlns=\"http://jabber.org/protocol/muc#admin\"><item nick=\"thirdwitch\" role=\"moderator\"><reason>A worthy witch indeed!</reason></item></query>");
    }

    @Test
    public void marshalRevokeModerator() throws JAXBException, XMLStreamException {
        MucAdmin mucAdmin = MucAdmin.withItem(Role.PARTICIPANT, "thirdwitch", "Not so worthy after all!");
        String xml = marshal(mucAdmin);
        Assert.assertEquals(xml, "<query xmlns=\"http://jabber.org/protocol/muc#admin\"><item nick=\"thirdwitch\" role=\"participant\"><reason>Not so worthy after all!</reason></item></query>");
    }

    @Test
    public void marshalModifyModeratorList() throws JAXBException, XMLStreamException {
        MucAdmin mucAdmin = MucAdmin.withItems(MucAdmin.createItem(Role.PARTICIPANT, "thirdwitch"), MucAdmin.createItem(Role.MODERATOR, "Hecate"));
        String xml = marshal(mucAdmin);
        Assert.assertEquals(xml, "<query xmlns=\"http://jabber.org/protocol/muc#admin\"><item nick=\"thirdwitch\" role=\"participant\"></item><item nick=\"Hecate\" role=\"moderator\"></item></query>");
    }

    @Test
    public void marshalModifyModeratorList2() throws JAXBException, XMLStreamException {
        MucAdmin mucAdmin = MucAdmin.withItems(Arrays.asList(MucAdmin.createItem(Role.PARTICIPANT, "thirdwitch"), MucAdmin.createItem(Role.MODERATOR, "Hecate")));
        String xml = marshal(mucAdmin);
        Assert.assertEquals(xml, "<query xmlns=\"http://jabber.org/protocol/muc#admin\"><item nick=\"thirdwitch\" role=\"participant\"></item><item nick=\"Hecate\" role=\"moderator\"></item></query>");
    }
}
