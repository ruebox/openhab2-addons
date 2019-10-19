# Contacts, Presence and Messaging
---

## Adding Contacts to Your Roster

```java
xmppClient.getManager(RosterManager.class).addContact(new Contact(Jid.of("juliet@example.net"), "Juliet"), true, "Hi Juliet, please add me.");
```

This will create a contact on your roster and subsequently sends a presence subscription request to the user.

The roster manager also provides other methods, e.g. for deleting and updating a contact.

## Roster Pushes

You can listen for roster pushes like this:

```java
RosterManager rosterManager = xmppClient.getManager(RosterManager.class);
rosterManager.addRosterListener(e -> {
    // The roster event contains information about added, updated or deleted contacts.
    // TODO: Update your roster!
    Collection<Contact> contacts = rosterManager.getContacts();
    for (Contact contact : contacts) {
        System.out.println(contact.getName());
    }
});
```

## Dealing with Presence Updates and Subscription Requests

Whenever one of your contacts updates his presence (e.g. comes online, goes away, goes offline, ...), you can react to it with:

```java
xmppClient.addInboundPresenceListener(e -> {
    Presence presence = e.getPresence();
    Contact contact = xmppClient.getManager(RosterManager.class).getContact(presence.getFrom());
    if (contact != null) {
        // ... contact's presence has updated.
    }
});
```

Presence is also used for requesting subscription to your presence status.

```java
xmppClient.addInboundPresenceListener(e -> {
    Presence presence = e.getPresence();
    if (presence.getType() == Presence.Type.SUBSCRIBE) {
        // presence.getFrom() wants to subscribe to your presence.
    }
});
```

You can then either approve or deny the subscription request:

```java
xmppClient.getManager(PresenceManager.class).approveSubscription(presence.getFrom());
```

```java
xmppClient.getManager(PresenceManager.class).denySubscription(presence.getFrom());
```

## Listening for Inbound Messages

Listening for messages is done by adding a message listener.

```java
xmppClient.addInboundMessageListener(e -> {
    Message message = e.getMessage();
    // Handle message.
});
```

*Note:* All listeners should be added *before* you login to the session. Otherwise you might miss messages and presences sent directly after login.

## Intercepting Outbound Messages (or Stanzas in General)

The same approach as for inbound messages is also used for outbound messages. The only difference is the 'inbound’ property of the event.

You can use this, if you want to add extensions to a stanza or otherwise modify the stanza.

```java
xmppClient.addOutboundMessageListener(e -> {
    Message message = e.getMessage();
    // you could add an extension to the message here.
});
```
