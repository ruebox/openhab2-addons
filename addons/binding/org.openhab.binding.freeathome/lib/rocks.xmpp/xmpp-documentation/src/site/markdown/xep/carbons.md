# XEP-0280: Message Carbons
---

[XEP-0280: Message Carbons][Message Carbons] allows you to tell the server to fork a message to all connected resources of a user.

This if useful if you want all your connected resources to stay in sync with each other.

## Enabling Message Carbons for Your Session

If you want to make use of Message Carbons, you must first tell it the server:

```java
MessageCarbonsManager messageCarbonsManager = xmppClient.getManager(MessageCarbonsManager.class);
messageCarbonsManager.enableCarbons();
```

Your server now copies all inbound and outbound chat messages to all connected resources.

## Get Outbound Copies of a Message

Let's say, you enabled Message Carbons for your resource `A` and your other resource `B` sends a message to one of your contacts, resource `A` should then receive a copy of `B`'s message.

You can get the original sent message by checking for the `Sent` extension:

```java
MessageCarbons.Sent sent = message.getExtension(MessageCarbons.Sent.class);
if (sent != null) {
    Forwarded forwardedMessage = sent.getForwardedMessage();
    Stanza stanza = forwardedMessage.getStanza();
    if (stanza instanceof Message) {
        Message originalSentMessage = (Message) stanza;
        // This is the original message, which has been sent by another resource.
    }
}
```

## Get Inbound Copies of a Message

Similarly, if your resource `B` receives a message (to its full JID), resource `A` will receive a carbon copy of this message.

You can get the original message (sent to `B`) by checking for the `Received` extension:

```java
MessageCarbons.Received received = message.getExtension(MessageCarbons.Received.class);
if (received != null) {
    Forwarded forwardedMessage = received.getForwardedMessage();
    Stanza stanza = forwardedMessage.getStanza();
    if (stanza instanceof Message) {
        Message originalReceivedMessage = (Message) stanza;
        // This is the original message, which has been received by another resource.
    }
}
```

[Message Carbons]: https://xmpp.org/extensions/xep-0280.html "XEP-0280: Message Carbons"
