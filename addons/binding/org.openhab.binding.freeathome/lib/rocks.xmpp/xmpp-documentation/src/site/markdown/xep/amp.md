# XEP-0079: Advanced Message Processing
---

[XEP-0079: Advanced Message Processing][Advanced Message Processing] allows to request advanced processing of XMPP message stanzas, including reliable data transport, time-sensitive delivery, and expiration of transient messages.

## How to Add an AMP Extension

Here are some simple examples:

```java
AdvancedMessageProcessing amp = new AdvancedMessageProcessing(Rule.matchResource(Rule.Action.ALERT, Rule.MatchResource.EXACT));
message.addExtension(amp);
```

```java
AdvancedMessageProcessing amp = new AdvancedMessageProcessing(Rule.expireAt(Rule.Action.DROP, Instant.now()));
message.addExtension(amp);
```

```java
AdvancedMessageProcessing amp = new AdvancedMessageProcessing(Rule.deliver(Rule.Action.ALERT, Rule.DeliveryMode.STORED));
message.addExtension(amp);
```


[Advanced Message Processing]: https://xmpp.org/extensions/xep-0079.html "XEP-0079: Advanced Message Processing"
