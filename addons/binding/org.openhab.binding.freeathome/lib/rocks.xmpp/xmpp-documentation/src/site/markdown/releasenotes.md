# Release Notes

## 0.8.0

### New Project Structure and Modularization

The following artifacts have been renamed:

* `xmpp-core` => `xmpp-core-common`
* `xmpp-extensions` => `xmpp-extensions-common`
* `xmpp-websocket` => `xmpp-websocket-common` + `xmpp-websocket-client`

Experimental work on a server implementation has revealed, that it's useful to have a "common" module of -let's say- the WebSocket implementation,
which now includes codecs, which can be used likewise by a client and server implementation.
The "common" artifacts therefore contain code, which could be shared by clients and servers.


### `Jid` is now an interface

Motivation:

1. The old `Jid` class always constructed a new instance with every call of `asBareJid()`.
The interface now allows to reference the enclosing class' fields and returning only a "bare" view of the enclosing `Jid` instead of creating new instances.
This is a performance improvement to avoid creating too many objects and decreases GC pressure.

2. Jid being an interface allows for more flexibility. E.g. there's now a package-private `MalformedJid` implementation,
which is internally used during parsing and allows to send a `malformed-jid` stanza error.

### Update PRECIS to RFC 8264

The XMPP Address Format uses PRECIS for internationalizing the local part.
The previous `Jid` implementation used [RFC 7564](https://tools.ietf.org/html/rfc7564), which case folded the Jid's local part.
The updated PRECIS specification [RFC 8264](https://tools.ietf.org/html/rfc8264) only lower cases it.
This has the following impact:

**Old** `Jid` behavior:

```java
Jid jid = Jid.of('fußball@domain');
// jid.toString() == "fussball@domain"
```

**New** `Jid` behavior:

```java
Jid jid = Jid.of('fußball@domain');
// jid.toString() == "fußball@domain"
```

### Java NIO Support

There's a new artifact `xmpp-nio-netty-client`, which enables you to use Java NIO for TCP connections using the Netty framework.

Background: The current (and default) implementation (see `SocketConnectionConfiguration`) uses two threads per connection, one for reading, one for writing.
This is required, because it uses `java.net.Socket` internally, which uses blocking IO.
For most client applications this is not an issue and totally acceptable, because clients rarely open multiple connections.

However, the advantage of NIO is that it doesn't use blocking IO, so now you can use only one (!) thread for a large number of connections:

```java
NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);

NettyTcpConnectionConfiguration tcpConnectionConfiguration = NettyTcpConnectionConfiguration.builder()
        .hostname("localhost")
        .port(5222)
        .eventLoopGroup(eventLoopGroup)
        .build();

for (int i = 0; i < 100; i++) {
    XmppClient xmppClient = XmppClient.create("localhost", configuration, tcpConnectionConfiguration);
    xmppClient.connect();
}
```

As you can see, you can reuse the same `NioEventLoopGroup` for multiple connection configurations.


### XEP-0392: Consistent Color Generation

Sample usage:

```java
ConsistentColor color = ConsistentColor.generate(input);
float red = color.getRed();
float green = color.getGreen();
float blue = color.getBlue();
```

(`float` is used for easier integration with `java.awt.Color`.)

### Custom ThreadFactory

For all threads being started you can now specify a custom thread factory. This might be useful when using this library in Java EE with a `ManagedThreadFactory`:

```java
@Resource
private ManagedThreadFactory mtf;
```
```java
XmppSessionConfiguration configuration = XmppSessionConfiguration.builder()
    .threadFactory(mtf)
    .build();
```

### Major API changes

* new `rocks.xmpp.core.Text` class to represent texts in XMPP (Message, Subjects, Errors). The affected classes use the new class now.
* class `rocks.xmpp.core.session.Connection` became an interface `rocks.xmpp.core.net.Connection`
* `rocks.xmpp.core.session.ConnectionConfiguration` became `rocks.xmpp.core.net.client.ClientConnectionConfiguration`
* new enum `ChannelEncryption` to set the behavior, if, how and when a channel should be encrypted (e.g. direct or via StartTLS)
* `rocks.xmpp.websocket.WebSocketConnection` became `rocks.xmpp.websocket.net.client.WebSocketClientConnection`
* `rocks.xmpp.websocket.WebSocketConnectionConfiguration` became `rocks.xmpp.websocket.net.client.WebSocketConnectionConfiguration`
