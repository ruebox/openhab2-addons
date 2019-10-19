# XEP-0203: Delayed Delivery
---

[XEP-0203: Delayed Delivery][Delayed Delivery] is usually attached to messages, when they are sent from the server's offline storage.

Checking if a message has delayed delivery info attached is pretty simple:

```java
DelayedDelivery delayedDelivery = message.getExtension(DelayedDelivery.class);
if (delayedDelivery != null) {
    // This message was sent from offline storage
}
```

There's also a convenient method to retrieve the send date of a stanza, which is either "now" (real-time) or some time ago in case the server stored the stanza for delayed delivery, e.g. in an offline storage:

```java
Instant sendDate = DelayedDelivery.sendDate(message);
```

[Delayed Delivery]: https://xmpp.org/extensions/xep-0203.html "XEP-0203: Delayed Delivery"
