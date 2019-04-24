# XEP-0202: Entity Time
---

[XEP-0202: Entity Time][Entity Time] allows you to request another entity's time and timezone.

Here's a simple example:

```java
EntityTimeManager entityTimeManager = xmppClient.getManager(EntityTimeManager.class);
OffsetDateTime dateTime = entityTimeManager.getEntityTime(Jid.of("juliet@example.net/balcony")).getResult();
```


[Entity Time]: https://xmpp.org/extensions/xep-0202.html "XEP-0202: Entity Time"
