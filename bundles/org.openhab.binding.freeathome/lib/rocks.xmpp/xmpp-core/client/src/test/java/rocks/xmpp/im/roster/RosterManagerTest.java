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

package rocks.xmpp.im.roster;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.BaseTest;
import rocks.xmpp.core.session.TestXmppSession;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.im.roster.model.Contact;
import rocks.xmpp.im.roster.model.ContactGroup;
import rocks.xmpp.im.roster.model.Roster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Christian Schudt
 */
public class RosterManagerTest extends BaseTest {

    @Test
    public void testRosterListener() {
        final int[] rosterPushCount = new int[1];
        XmppSession xmppSession1 = new TestXmppSession();
        RosterManager rosterManager = xmppSession1.getManager(RosterManager.class);
        rosterManager.addRosterListener(e -> {
            switch (rosterPushCount[0]) {
                case 0:
                    Assert.assertEquals(e.getAddedContacts().size(), 3);
                    Assert.assertEquals(e.getUpdatedContacts().size(), 0);
                    Assert.assertEquals(e.getRemovedContacts().size(), 0);
                    break;
                case 1:
                    Assert.assertEquals(e.getAddedContacts().size(), 1);
                    Assert.assertEquals(e.getUpdatedContacts().size(), 0);
                    Assert.assertEquals(e.getRemovedContacts().size(), 0);
                    break;
                case 2:
                    Assert.assertEquals(e.getAddedContacts().size(), 0);
                    Assert.assertEquals(e.getUpdatedContacts().size(), 0);
                    Assert.assertEquals(e.getRemovedContacts().size(), 1);
                    Assert.assertEquals(e.getRemovedContacts().get(0).getJid(), Jid.of("contact2@domain"));
                    break;
                case 3:
                    Assert.assertEquals(e.getAddedContacts().size(), 0);
                    Assert.assertEquals(e.getUpdatedContacts().size(), 1);
                    Assert.assertEquals(e.getRemovedContacts().size(), 0);
                    Assert.assertEquals(e.getUpdatedContacts().get(0).getJid(), Jid.of("contact1@domain"));
                    Assert.assertEquals(e.getUpdatedContacts().get(0).getName(), "Name");
                    break;
            }
        });

        Roster roster1 = new Roster(new Contact(Jid.of("contact1@domain")),
                new Contact(Jid.of("contact2@domain")),
                new Contact(Jid.of("contact3@domain")));
        rosterManager.updateRoster(roster1, false);
        rosterPushCount[0]++;

        Roster roster2 = new Roster(new Contact(Jid.of("contact4@domain")));
        rosterManager.updateRoster(roster2, true);

        rosterPushCount[0]++;
        Roster roster3 = new Roster(new Contact(Jid.of("contact2@domain"), null, false, null, Contact.Subscription.REMOVE, Collections.emptyList()));
        rosterManager.updateRoster(roster3, true);

        rosterPushCount[0]++;
        Roster roster4 = new Roster(new Contact(Jid.of("contact1@domain"), "Name"));
        rosterManager.updateRoster(roster4, true);
    }

    @Test
    public void testRosterGroups() {
        XmppSession xmppSession1 = new TestXmppSession();
        RosterManager rosterManager = xmppSession1.getManager(RosterManager.class);

        Roster roster1 = new Roster(new Contact(Jid.of("contact1@domain"), "contact1", "Group1"),
                new Contact(Jid.of("contact2@domain"), "contact2", "Group2"),
                new Contact(Jid.of("contact4@domain"), "contact4", "Group3"),
                new Contact(Jid.of("contact3@domain"), "contact3", "Group3"),
                new Contact(Jid.of("contact5@domain"), "contact5", "Group3"));
        rosterManager.updateRoster(roster1, false);

        List<ContactGroup> list = new ArrayList<>(rosterManager.getContactGroups());
        Assert.assertEquals(list.size(), 3);
        Assert.assertEquals(list.get(0).getName(), "Group1");
        Assert.assertEquals(list.get(0).getContacts().size(), 1);
        Assert.assertEquals(list.get(0).getContacts().iterator().next().getJid(), Jid.of("contact1@domain"));
        Assert.assertEquals(list.get(1).getName(), "Group2");
        Assert.assertEquals(list.get(1).getContacts().iterator().next().getJid(), Jid.of("contact2@domain"));
        Assert.assertEquals(list.get(2).getName(), "Group3");
        Iterator<Contact> iterator = list.get(2).getContacts().iterator();
        Assert.assertEquals(iterator.next().getJid(), Jid.of("contact3@domain"));
        Assert.assertEquals(iterator.next().getJid(), Jid.of("contact4@domain"));
        Assert.assertEquals(iterator.next().getJid(), Jid.of("contact5@domain"));
    }

    @Test
    public void testNestedRosterGroups() {
        XmppSession xmppSession1 = new TestXmppSession();
        RosterManager rosterManager = xmppSession1.getManager(RosterManager.class);
        rosterManager.setGroupDelimiter("::");
        Roster roster1 = new Roster(new Contact(Jid.of("contact3@domain"), "contact3", "Group3::SubGroup"),
                new Contact(Jid.of("contact4@domain"), "contact4", "Group3::SubGroup::3rdLevel"),
                new Contact(Jid.of("contact5@domain"), "contact5", "Group3"));
        rosterManager.updateRoster(roster1, false);

        List<ContactGroup> list = new ArrayList<>(rosterManager.getContactGroups());
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(list.get(0).getContacts().iterator().next().getJid(), Jid.of("contact5@domain"));
        Assert.assertEquals(list.get(0).getName(), "Group3");
        Assert.assertEquals(list.get(0).getContacts().size(), 1);
        Assert.assertEquals(list.get(0).getGroups().size(), 1);

        ContactGroup contactGroup = list.get(0).getGroups().iterator().next();
        Assert.assertEquals(contactGroup.getName(), "SubGroup");
        Assert.assertEquals(contactGroup.getGroups().size(), 1);
        Assert.assertEquals(contactGroup.getContacts().size(), 1);
        Assert.assertEquals(contactGroup.getContacts().iterator().next().getJid(), Jid.of("contact3@domain"));
        ContactGroup nestedGroup = contactGroup.getGroups().iterator().next();
        Assert.assertEquals(nestedGroup.getName(), "3rdLevel");
        Assert.assertEquals(nestedGroup.getContacts().size(), 1);
        Assert.assertEquals(nestedGroup.getContacts().iterator().next().getJid(), Jid.of("contact4@domain"));
    }

    @Test
    public void testRosterIntegrity() {
        XmppSession xmppSession1 = new TestXmppSession();
        RosterManager rosterManager = xmppSession1.getManager(RosterManager.class);

        // Initial roster
        Roster roster1 = new Roster(new Contact(Jid.of("contact1@domain"), "contact1", "group1"),
                new Contact(Jid.of("contact2@domain"), "contact2", "group2"),
                new Contact(Jid.of("contact3@domain"), "contact3", true, null, Contact.Subscription.FROM, Collections.emptyList()),
                new Contact(Jid.of("contact4@domain"), "contact4", true, null, Contact.Subscription.FROM, Collections.singleton("group2")));
        rosterManager.updateRoster(roster1, false);

        Assert.assertEquals(rosterManager.getUnaffiliatedContacts().size(), 1);
        Assert.assertEquals(rosterManager.getUnaffiliatedContacts().iterator().next().getSubscription(), Contact.Subscription.FROM);
        List<ContactGroup> groups = new ArrayList<>(rosterManager.getContactGroups());
        Assert.assertEquals(groups.get(0).getContacts().size(), 1);
        Assert.assertEquals(groups.get(1).getContacts().size(), 2);

        Roster roster2 = new Roster(new Contact(Jid.of("contact3@domain"), "contact3", true, null, Contact.Subscription.BOTH, Collections.emptyList()));
        rosterManager.updateRoster(roster2, true);

        Assert.assertEquals(rosterManager.getUnaffiliatedContacts().size(), 1);
        Assert.assertEquals(rosterManager.getUnaffiliatedContacts().iterator().next().getSubscription(), Contact.Subscription.BOTH);

        Assert.assertEquals(rosterManager.getContactGroups().size(), 2);

        Roster roster3 = new Roster(new Contact(Jid.of("contact2@domain"), "contact2", true, null, Contact.Subscription.TO, Collections.singleton("group1")));
        rosterManager.updateRoster(roster3, true);

        groups = new ArrayList<>(rosterManager.getContactGroups());
        List<Contact> contacts = new ArrayList<>(groups.get(0).getContacts());
        Assert.assertEquals(groups.get(0).getContacts().size(), 2);
        Assert.assertEquals(contacts.get(1).getSubscription(), Contact.Subscription.TO);
        Assert.assertTrue(contacts.get(1).isPendingOut());
        Assert.assertEquals(groups.get(1).getContacts().size(), 1);

        Roster roster4 = new Roster(new Contact(Jid.of("contact3@domain"), "", false, null, Contact.Subscription.REMOVE, Collections.emptyList()));
        rosterManager.updateRoster(roster4, true);
        Assert.assertTrue(rosterManager.getUnaffiliatedContacts().isEmpty());
    }
}
