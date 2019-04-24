# XEP-0301: In-Band Real Time Text
---

[In-Band Real Time Text][In-Band Real Time Text]

## Initiating Real-Time Text

Real Time Text is transmitted in the context of a chat session, which is usually a one-to-one chat session, but can also be a multi-user chat.

So first you need to retrieve a `Chat` instance, e.g. as follows:

```
Chat chat = xmppClient.getManager(ChatManager.class).createChatSession(Jid.of("juliet@example.net"));
```

Next, create an `OutboundRealTimeMessage` object. It takes care of various aspects of real-time text, like:

* calculating the diffs between text changes
* the wait intervals for smoother user experience
* submitting real-time text every 700ms (the recommended default)
* as well as a 'reset' message every 10s.

```
RealTimeTextManager realTimeTextManager = xmppClient.getManager(RealTimeTextManager.class);
OutboundRealTimeMessage realTimeMessage = realTimeTextManager.createRealTimeMessage(chat);
```

Then whenever text changes, update the message. Here's an example with a JavaFX TextArea:

```
textArea.textProperty().addListener((observable, oldValue, newValue) -> {
    realTimeMessage.update(newValue);
});
```

When done, it is important to commit the message (i.e. sending a backwards-compatible message with a `<body/>` element), which also shuts down executors, which were responsible for the transmission:

```
realTimeMessage.commit();
```

Most likely you now want to start a new real time message and clear the TextArea:

```
realTimeMessage = realTimeTextManager.createRealTimeMessage(chat);
textArea.clear();
```        
  
## Receiving Real-Time Messages

```
realTimeTextManager.addRealTimeMessageListener(e -> {
    InboundRealTimeMessage inboundRealTimeMessage = e.getRealTimeMessage();
    // Here route the message correctly to the sender's UI interface, using inboundRealTimeMessage.getFrom()
    inboundRealTimeMessage.addRealTimeTextChangeListener(t ->
        System.out.println(t.getText()));
});
```

[In-Band Real Time Text]: http://www.xmpp.org/extensions/xep-0301.html "XEP-0301: In-Band Real Time Text"
