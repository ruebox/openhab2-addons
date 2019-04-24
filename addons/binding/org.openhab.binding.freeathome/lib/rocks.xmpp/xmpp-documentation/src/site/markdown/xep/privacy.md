# XEP-0016: Privacy Lists
---

[XEP-0016: Privacy Lists][Privacy Lists] allow you to block communication with other entities, either based on their JID, subscription state or roster group.

## Managing Privacy Lists

### Getting Privacy Lists

You can get all existing privacy lists from the server like this:

```java
PrivacyListManager privacyListManager = xmppClient.getManager(PrivacyListManager.class);
Collection<PrivacyList> privacyLists = privacyListManager.getPrivacyLists().getResult();
```

### Creating or Updating a Privacy List

Creating or updating a privacy list use the same protocol, therefore there's only one method for both use cases.

```java
Collection<PrivacyRule> rules = Collections.singleton(PrivacyRule.blockMessagesFrom(Jid.of("juliet@example.com"), 1));
PrivacyList privacyList = new PrivacyList("listName", rules);
privacyListManager.createOrUpdateList(privacyList);
```

### Removing a Privacy List

```java
privacyListManager.removeList("listName");
```

### Setting a Default or Active List

Default lists apply to the user as a whole, and are processed if there is no active list set for the target session.

```java
privacyListManager.setDefaultList("listName");
```

Active lists only affect the current session for which they are activated and only for the duration of the session.

```java
privacyListManager.setActiveList("listName");
```

### Declining the Use of a Privacy List

The following declines the use of the active list.

```java
privacyListManager.declineActiveList();
```

And this declines the use of the default list:

```java
privacyListManager.declineDefaultList();
```

## Listening for Privacy List Changes

Whenever a privacy list is updated, the server will notify all connected resources about the updated list. You can listen for these "pushes" in the following way:

```java
privacyListManager.addPrivacyListListener(e -> {
    // e.getListName() has been updated.
});
```

## Blocking Communications With Particular Users

A common use case for Privacy Lists is to block communication with another user.

For this use case you should add a privacy rule for the user, which denies all communication.

```java
new PrivacyList("name", Collections.singleton(PrivacyRule.blockAllCommunicationWith(Jid.of("tybalt@example.com"), 1)));
```

Privacy rules can also be defined more granular. E.g. if you only want to block IQ stanzas, but allow presences and messages, you would define the rule like:

```java
PrivacyRule privacyRule = PrivacyRule.blockIQFrom(Jid.of("tybalt@example.com"), 1);
```


## XEP-0126: Invisibility

[XEP-0126: Invisibility][Invisibility] defines a recommendation for using Privacy Lists for invisibility.

You can create an "invisibility" list with a handful of static factory methods, e.g.:

```java
PrivacyList invisibilityList = PrivacyList.createInvisibilityList();
```

[Privacy Lists]: https://xmpp.org/extensions/xep-0016.html "XEP-0016: Privacy Lists"
[Invisibility]: https://xmpp.org/extensions/xep-0126.html "XEP-0126: Invisibility"
