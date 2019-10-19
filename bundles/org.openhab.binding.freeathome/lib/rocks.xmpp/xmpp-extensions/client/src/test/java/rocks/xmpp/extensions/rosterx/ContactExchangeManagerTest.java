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

package rocks.xmpp.extensions.rosterx;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.BaseTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.extensions.rosterx.model.ContactExchange;
import rocks.xmpp.im.roster.model.Contact;
import rocks.xmpp.im.roster.model.Roster;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Christian Schudt
 */
public class ContactExchangeManagerTest extends BaseTest {

    @BeforeClass
    public void prepareRoster() throws Exception {
        Roster roster = new Roster(new Contact(Jid.of("juliet@example.net"), "juliet", "friends", "friends2"),
                new Contact(Jid.of("romeo@example.net"), "romeo", "friends"));
        // Simulate a roster push in order to fill the roster.
        xmppSession.handleElement(IQ.set(roster));
    }

    @Test
    public void testRosterItemAdditionWhenContactDoesNotExist() {
        ContactExchangeManager contactExchangeManager = xmppSession.getManager(ContactExchangeManager.class);

        Collection<ContactExchange.Item> suggestedContacts = new ArrayDeque<>();
        suggestedContacts.add(new ContactExchange.Item(Jid.of("contact@example.net"), "juliet", Collections.singleton("friends"), ContactExchange.Item.Action.ADD));

        List<ContactExchange.Item> items = contactExchangeManager.getItemsToProcess(suggestedContacts);

        Assert.assertEquals(items.size(), 1);
        ContactExchange.Item item = items.get(0);
        Assert.assertEquals(item.getAction(), ContactExchange.Item.Action.ADD);
        Assert.assertEquals(item.getGroups().size(), 1);
        Assert.assertEquals(item.getGroups().get(0), "friends");
    }

    @Test
    public void testRosterItemAdditionWhenContactAlreadyExists() {

        ContactExchangeManager contactExchangeManager = xmppSession.getManager(ContactExchangeManager.class);

        Collection<ContactExchange.Item> suggestedContacts = new ArrayDeque<>();
        suggestedContacts.add(new ContactExchange.Item(Jid.of("juliet@example.net"), "juliet", Collections.singleton("friends"), ContactExchange.Item.Action.ADD));

        Collection<ContactExchange.Item> items = contactExchangeManager.getItemsToProcess(suggestedContacts);

        // Contact already exists in roster (and in same group), therefore do nothing.
        Assert.assertEquals(items.size(), 0);

        // Contact already exists, but not in the suggested group
        Collection<ContactExchange.Item> suggestedContacts2 = new ArrayDeque<>();
        suggestedContacts2.add(new ContactExchange.Item(Jid.of("juliet@example.net"), "juliet", Collections.singleton("newGroup"), ContactExchange.Item.Action.ADD));

        List<ContactExchange.Item> items2 = contactExchangeManager.getItemsToProcess(suggestedContacts2);
        Assert.assertEquals(items2.size(), 1);
        ContactExchange.Item item = items2.get(0);
        Assert.assertEquals(item.getAction(), ContactExchange.Item.Action.ADD);
        Assert.assertEquals(item.getGroups().size(), 1);
        Assert.assertEquals(item.getGroups().get(0), "newGroup");
    }

    @Test
    public void testRosterItemDeletionWhenContactDoesNotExist() {
        ContactExchangeManager contactExchangeManager = xmppSession.getManager(ContactExchangeManager.class);

        Collection<ContactExchange.Item> suggestedContacts = new ArrayDeque<>();
        suggestedContacts.add(new ContactExchange.Item(Jid.of("contact@example.net"), "juliet", Collections.singleton("friends"), ContactExchange.Item.Action.DELETE));

        List<ContactExchange.Item> items = contactExchangeManager.getItemsToProcess(suggestedContacts);

        // Contact does not exist in roster, therefore do nothing.
        Assert.assertEquals(items.size(), 0);
    }

    @Test
    public void testRosterItemDeletionWhenContactDoesExistButNotInSpecifiedGroup() {
        ContactExchangeManager contactExchangeManager = xmppSession.getManager(ContactExchangeManager.class);

        Collection<ContactExchange.Item> suggestedContacts = new ArrayDeque<>();
        suggestedContacts.add(new ContactExchange.Item(Jid.of("juliet@example.net"), "juliet", Collections.singleton("otherGroup"), ContactExchange.Item.Action.DELETE));

        List<ContactExchange.Item> items = contactExchangeManager.getItemsToProcess(suggestedContacts);

        // Contact does exist in roster, but not in the specified group, therefore do nothing.
        Assert.assertEquals(items.size(), 0);
    }

    @Test
    public void testRosterItemDeletionWhenContactDoesExistInSpecifiedGroup() {

        ContactExchangeManager contactExchangeManager = xmppSession.getManager(ContactExchangeManager.class);

        Collection<ContactExchange.Item> suggestedContacts = new ArrayDeque<>();
        suggestedContacts.add(new ContactExchange.Item(Jid.of("juliet@example.net"), "juliet", Arrays.asList("friends", "unknownGroup"), ContactExchange.Item.Action.DELETE));

        List<ContactExchange.Item> items = contactExchangeManager.getItemsToProcess(suggestedContacts);

        // Contact does exist in roster, but not in the specified group, therefore do nothing.
        Assert.assertEquals(items.size(), 1);
        ContactExchange.Item item = items.get(0);
        Assert.assertEquals(item.getGroups().size(), 1);
        Assert.assertEquals(item.getGroups().get(0), "friends");
    }

    @Test
    public void testRosterItemDeletionWhenNoGroupSpecified() {

        ContactExchangeManager contactExchangeManager = xmppSession.getManager(ContactExchangeManager.class);

        Collection<ContactExchange.Item> suggestedContacts = new ArrayDeque<>();
        suggestedContacts.add(new ContactExchange.Item(Jid.of("juliet@example.net"), "juliet", Collections.emptyList(), ContactExchange.Item.Action.DELETE));

        List<ContactExchange.Item> items = contactExchangeManager.getItemsToProcess(suggestedContacts);

        // Contact does exist in roster, but not in the specified group, therefore do nothing.
        Assert.assertEquals(items.size(), 0);
    }

    @Test
    public void testRosterItemModificationIfContactDoesExist() {

        ContactExchangeManager contactExchangeManager = xmppSession.getManager(ContactExchangeManager.class);

        Collection<ContactExchange.Item> suggestedContacts = new ArrayDeque<>();
        suggestedContacts.add(new ContactExchange.Item(Jid.of("juliet@example.net"), "Juliet", Collections.singleton("newGroup1"), ContactExchange.Item.Action.MODIFY));

        List<ContactExchange.Item> items = contactExchangeManager.getItemsToProcess(suggestedContacts);

        // Only one contact in roster.
        Assert.assertEquals(items.size(), 1);
        ContactExchange.Item item = items.get(0);
        Assert.assertEquals(item.getGroups().size(), 1);
        Assert.assertEquals(item.getGroups().get(0), "newGroup1");
        Assert.assertEquals(item.getName(), "Juliet");
    }

    @Test
    public void testRosterItemModification() {

        ContactExchangeManager contactExchangeManager = xmppSession.getManager(ContactExchangeManager.class);

        Collection<ContactExchange.Item> suggestedContacts = new ArrayDeque<>();
        suggestedContacts.add(new ContactExchange.Item(Jid.of("contact@example.net"), "contact", Collections.emptyList(), ContactExchange.Item.Action.MODIFY));
        suggestedContacts.add(new ContactExchange.Item(Jid.of("romeo@example.net"), "romeo", Collections.emptyList(), ContactExchange.Item.Action.MODIFY));

        List<ContactExchange.Item> items = contactExchangeManager.getItemsToProcess(suggestedContacts);

        // Only one contact in roster.
        Assert.assertEquals(items.size(), 1);
    }

    @Test
    public void testRosterItemApproval() {

        ContactExchangeManager contactExchangeManager = xmppSession.getManager(ContactExchangeManager.class);

        // Does already exist, therefore return null.
        Assert.assertNull(contactExchangeManager.approve(new ContactExchange.Item(Jid.of("juliet@example.net"), "juliet", Collections.singleton("friends"), ContactExchange.Item.Action.ADD)));

        // Does already exist, but not in all groups, therefore return modify.
        Assert.assertEquals(contactExchangeManager.approve(new ContactExchange.Item(Jid.of("juliet@example.net"), "juliet", Arrays.asList("friends", "newGroup"), ContactExchange.Item.Action.ADD)), ContactExchange.Item.Action.MODIFY);

        // Does not yet exist
        Assert.assertEquals(contactExchangeManager.approve(new ContactExchange.Item(Jid.of("contact1@example.net"), "contact1", Arrays.asList("friends", "newGroup"), ContactExchange.Item.Action.ADD)), ContactExchange.Item.Action.ADD);

        // Does not exist, therefore return null.
        Assert.assertNull(contactExchangeManager.approve(new ContactExchange.Item(Jid.of("contact2@example.net"), "contact2", Collections.emptyList(), ContactExchange.Item.Action.DELETE)));

        // Is only removed from group "friends", but still in group "friends2".
        Assert.assertEquals(contactExchangeManager.approve(new ContactExchange.Item(Jid.of("juliet@example.net"), "juliet", Collections.singleton("friends"), ContactExchange.Item.Action.DELETE)), ContactExchange.Item.Action.MODIFY);

        // Is removed completely.
        Assert.assertEquals(contactExchangeManager.approve(new ContactExchange.Item(Jid.of("juliet@example.net"), "juliet", Arrays.asList("friends", "friends2"), ContactExchange.Item.Action.DELETE)), ContactExchange.Item.Action.DELETE);

        // Does not exist, therefore return null.
        Assert.assertNull(contactExchangeManager.approve(new ContactExchange.Item(Jid.of("contact3@example.net"), "contact3", Collections.emptyList(), ContactExchange.Item.Action.MODIFY)));

        // Does not exist, therefore return null.
        Assert.assertEquals(contactExchangeManager.approve(new ContactExchange.Item(Jid.of("romeo@example.net"), "Romeo", Collections.emptyList(), ContactExchange.Item.Action.MODIFY)), ContactExchange.Item.Action.MODIFY);
    }
}
