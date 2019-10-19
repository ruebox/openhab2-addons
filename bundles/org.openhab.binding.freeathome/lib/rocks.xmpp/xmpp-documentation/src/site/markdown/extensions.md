# Using Extensions
---

## Getting Extensions from Stanzas

Extensions in stanzas can simply be retrieved in the following way:

```java
DelayedDelivery delayedDelivery = message.getExtension(DelayedDelivery.class); // XEP-203
```
```java
Attention attention = message.getExtension(Attention.class); // XEP-224
```
```java
EntityCapabilities entityCapabilities = presence.getExtension(EntityCapabilities.class); // XEP-115
```

## Managing Extensions

Most extensions need some kind of logic or have to implement business rules defined in the respective XEP.

Therefore extensions are associated with a `Manager` class, which handles their business rules.

Nearly all extensions have one thing in common: They can be either enabled or disabled, e.g. you can either enable or disable
support for [XEP-0115: Entity Capabilities](https://xmpp.org/extensions/xep-0115.html) or [XEP-0184: Message Delivery Receipts](https://xmpp.org/extensions/xep-0184.html).

Extensions are also associated with an identifier, usually a namespace, e.g. `urn:xmpp:receipts`.

By enabling an extension, support for it will be automatically advertised by [XEP-0030: Service Discovery](https://xmpp.org/extensions/xep-0030.html).

To get a manager for a specific extension, you use the `getManager` method of the `XmppClient`.

This allows you to enable/disable an extension, but also to configure behavior of the extension.

### Examples

```
EntityTimeManager entityTimeManager = xmppClient.getManager(EntityTimeManager.class);
entityTimeManager.setEnabled(false);
```

This manager manages [XEP-0202: Entity Time](https://xmpp.org/extensions/xep-0202.html) and is enabled by default.
Inbound "time" requests are automatically replied to with the current time, while enabled.

---

```
SoftwareVersionManager softwareVersionManager = xmppClient.getManager(SoftwareVersionManager.class);
softwareVersionManager.setSoftwareVersion(new SoftwareVersion("Babbler", "1.0"));
```

If an entity requests your software version, this manager automatically replies with the specified version.

---

Managers usually also allow to interact with other entities, e.g. retrieving the time, software version, or last activity of another entity:

```
LastActivityManager lastActivityManager = xmppClient.getManager(LastActivityManager.class);
LastActivity lastActivity = lastActivityManager.getLastActivity(Jid.of("juliet@example.net")).getResult();
```

(This will get the last activity of the user, i.e. the last logout date.)

## Enabling or Disabling Extensions

In order to enable or disable an extension, you can either use the approach described above (using the `Manager` class) or use a more convenient method:

```
xmppClient.enableFeature(EntityTimeManager.class);
```

or by providing the Extension namespace, e.g. `urn:xmpp:time`:

```
xmppClient.enableFeature(EntityTime.NAMESPACE);
```

In both cases, the extension will either be included or excluded from service discovery and the business logic of the extension will either be executed or not.