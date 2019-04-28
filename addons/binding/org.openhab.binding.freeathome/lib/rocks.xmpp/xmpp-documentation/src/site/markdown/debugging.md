# Debugging XMPP Traffic
---

## Default Debugger

There's a built-in default debugger, which prints XMPP communication to the console. You can enable it by configuring the session accordingly:

```java
XmppSessionConfiguration configuration = XmppSessionConfiguration.builder()
    .debugger(ConsoleDebugger.class)
    .build();
XmppClient xmppClient = XmppClient.create("domain", configuration);
```

## Visual Debugger

There's also an advanced visual debugger implementation in the `xmpp-debug` project:

![Visual Debugger](VisualDebugger.png)

If you have the corresponding jar file on your classpath, you can use it like that:

```java
XmppSessionConfiguration configuration = XmppSessionConfiguration.builder()
    .debugger(VisualDebugger.class)
    .build();
XmppClient xmppClient = XmppClient.create("domain", configuration);
```
