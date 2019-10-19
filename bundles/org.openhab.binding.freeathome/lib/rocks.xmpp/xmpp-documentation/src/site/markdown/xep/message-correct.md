# XEP-0308: Last Message Correction
---

[XEP-0308: Last Message Correction][Last Message Correction] allows you to correct the last sent message, e.g. if there was a typo in your message.

## Advertising Support

If your client supports this feature it should advertise support for it:

```java
xmppClient.enableFeature(Replace.NAMESPACE);
```

## Correcting a Message

Let's say you send a message with a typo in it:

```java
Message message = new Message(Jid.of("juliet@example.net/balcony"), Message.Type.CHAT, "Hello, my frind");
message.setId("123");
xmppClient.send(message);
```

You then recognize the typo in it and want to correct it. You would send a replacement message, replacing the old message:

```java
Message correctedMessage = new Message(jid, Message.Type.CHAT, "Hello, my friend");
correctedMessage.addExtension(new Replace("123"));
xmppClient.send(correctedMessage);
```


## Listening for Message Corrections

If a message should be replaced by another message, you should check inbound messages for the `Replace` extension and then replace the old message:

```java
Replace replace = message.getExtension(Replace.class);
if (replace != null) {
    String oldMessageId = replace.getId();
    // Replace old message with message...
}
```

Keeping track of the message ids is of course the developer's responsibility.

[Last Message Correction]: https://xmpp.org/extensions/xep-0308.html "XEP-0308: Last Message Correction"
