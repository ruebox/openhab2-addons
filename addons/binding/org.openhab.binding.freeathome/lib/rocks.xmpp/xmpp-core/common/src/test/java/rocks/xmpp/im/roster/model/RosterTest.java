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

package rocks.xmpp.im.roster.model;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.util.ComparableTestHelper;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Christian Schudt
 */
public class RosterTest extends XmlTest {

    protected RosterTest() throws JAXBException {
        super(ClientIQ.class, Roster.class);
    }

    @Test
    public void testPlainRoster() throws XMLStreamException, JAXBException {
        String xml = "<iq from='juliet@example.com/balcony'\n" +
                "       id='hu2bac18'\n" +
                "       type='get'>\n" +
                "     <query xmlns='jabber:iq:roster'/>\n" +
                "   </iq>";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertNotNull(iq);
    }

    @Test
    public void testUnmarshalRoster() throws XMLStreamException, JAXBException {
        String xml = "<iq id='hu2bac18'\n" +
                "       to='juliet@example.com/balcony'\n" +
                "       type='result'>\n" +
                "     <query xmlns='jabber:iq:roster' ver='ver11'>\n" +
                "       <item jid='romeo@example.net'\n" +
                "             name='Romeo'\n" +
                "             subscription='both'>\n" +
                "         <group>Friends</group>\n" +
                "       </item>\n" +
                "       <item jid='mercutio@example.com'\n" +
                "             name='Mercutio'\n" +
                "             subscription='from'/>\n" +
                "       <item jid='benvolio@example.net'\n" +
                "             name='Benvolio'\n" +
                "             subscription='both'/>\n" +
                "       <item jid='none@example.net'\n" +
                "             name='None'\n" +
                "             subscription='none'/>\n" +
                "       <item jid='To@example.net'\n" +
                "             name='To'\n" +
                "             subscription='to'/>\n" +
                "     </query>\n" +
                "   </iq>";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertEquals(iq.getType(), IQ.Type.RESULT);
        Assert.assertNotNull(iq);
        Roster roster = iq.getExtension(Roster.class);
        Assert.assertNotNull(roster);
        Assert.assertEquals(roster.getContacts().size(), 5);

        Assert.assertEquals(roster.getContacts().get(0).getJid().toString(), "romeo@example.net");
        Assert.assertEquals(roster.getContacts().get(0).getGroups().size(), 1);
        Assert.assertEquals(roster.getContacts().get(0).getGroups().get(0), "Friends");
        Assert.assertEquals(roster.getContacts().get(1).getName(), "Mercutio");
        Assert.assertEquals(roster.getContacts().get(2).getSubscription(), Contact.Subscription.BOTH);
        Assert.assertEquals(roster.getContacts().get(3).getSubscription(), Contact.Subscription.NONE);
        Assert.assertEquals(roster.getContacts().get(4).getSubscription(), Contact.Subscription.TO);
        Assert.assertEquals(roster.getVersion(), "ver11");
    }

    @Test
    public void testUnmarshalRemoveRosterItem() throws XMLStreamException, JAXBException {
        String xml = "<iq from='juliet@example.com/balcony'\n" +
                "       id='hm4hs97y'\n" +
                "       type='set'>\n" +
                "     <query xmlns='jabber:iq:roster'>\n" +
                "       <item jid='nurse@example.com'\n" +
                "             subscription='remove'/>\n" +
                "     </query>\n" +
                "   </iq>";
        IQ iq = unmarshal(xml, IQ.class);

        Assert.assertNotNull(iq);
        Roster roster = iq.getExtension(Roster.class);
        Assert.assertNotNull(roster);
        Assert.assertEquals(roster.getContacts().get(0).getSubscription(), Contact.Subscription.REMOVE);
        Assert.assertFalse(roster.getContacts().get(0).isPendingOut());
        Assert.assertFalse(roster.getContacts().get(0).isApproved());
    }

    @Test
    public void testUnmarshalPendingItem() throws XMLStreamException, JAXBException {
        String xml = "<iq id='b89c5r7ib575'\n" +
                "        to='romeo@example.net/bar'\n" +
                "        type='set'>\n" +
                "      <query xmlns='jabber:iq:roster'>\n" +
                "        <item ask='subscribe'\n" +
                "              jid='juliet@example.com'\n" +
                "              subscription='none'/>\n" +
                "      </query>\n" +
                "    </iq>";
        IQ iq = unmarshal(xml, IQ.class);

        Assert.assertNotNull(iq);
        Roster roster = iq.getExtension(Roster.class);
        Assert.assertNotNull(roster);
        Assert.assertTrue(roster.getContacts().get(0).isPendingOut());

    }

    @Test
    public void testUnmarshalApprovedRostertem() throws XMLStreamException, JAXBException {
        String xml = "<iq id='b89c5r7ib575'\n" +
                "        to='romeo@example.net/bar'\n" +
                "        type='set'>\n" +
                "      <query xmlns='jabber:iq:roster'>\n" +
                "        <item approved='true'\n" +
                "              jid='juliet@example.com'\n" +
                "              subscription='none'/>\n" +
                "      </query>\n" +
                "    </iq>";
        IQ iq = unmarshal(xml, IQ.class);

        Assert.assertNotNull(iq);
        Roster roster = iq.getExtension(Roster.class);
        Assert.assertNotNull(roster);
        Assert.assertTrue(roster.getContacts().get(0).isApproved());
    }

    @Test
    public void testMarshalRoster() throws XMLStreamException, JAXBException {
        String xml = "<query xmlns=\"jabber:iq:roster\"><item jid=\"node1@domain\"></item><item jid=\"node2@domain\" name=\"Name\"><group>Group1</group><group>Group2</group></item></query>";

        Roster roster = new Roster(Arrays.asList(new Contact(Jid.of("node1@domain")), new Contact(Jid.of("node2@domain"), "Name", false, null, null, Arrays.asList("Group1", "Group2"))));
        String rosterXml = marshal(roster);
        Assert.assertEquals(rosterXml, xml);
    }

    @Test
    public void testContactEquality() {

        Contact contact1 = new Contact(Jid.of("node1@domain"), "name", false, null, Contact.Subscription.FROM, Arrays.asList("group2", "group1"));
        Contact contact2 = new Contact(Jid.of("node1@domain"), "name", false, null, Contact.Subscription.FROM, Arrays.asList("group1", "group2"));

        Assert.assertEquals(contact1, contact2);
    }

    @Test
    public void testContactCompareTo() {

        List<Contact> contacts = Arrays.asList(
                new Contact(Jid.of("node1@domain"), "name", false, null, Contact.Subscription.FROM, Arrays.asList("group2", "group1")),
                new Contact(Jid.of("node1@domain"), "name2", true, null, Contact.Subscription.FROM, Arrays.asList("group1", "group2")),
                new Contact(Jid.of("node1@domain"), "name3", true, true, null, Collections.emptyList()),
                new Contact(Jid.of("node2@domain"), "name4", true, true, null, Collections.emptyList())
        );
        Collections.shuffle(contacts);
        ComparableTestHelper.checkCompareToContract(contacts);

        ContactGroup group1 = new ContactGroup("name", "fullname", null);

        List<ContactGroup> contactGroups = Arrays.asList(
                group1,
                new ContactGroup("name", "fullname", group1),
                new ContactGroup("name2", null, null),
                new ContactGroup("name2", "fullname2", null),
                new ContactGroup(null, null, null)
        );
        Collections.shuffle(contactGroups);
        ComparableTestHelper.checkCompareToContract(contactGroups);
    }
}
