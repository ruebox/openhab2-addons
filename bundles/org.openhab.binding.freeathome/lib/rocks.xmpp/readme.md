**Babbler** is a young [XMPP](https://xmpp.org) client library for Java SE 8 based on [JAXB](http://docs.oracle.com/javase/tutorial/jaxb/intro/index.html) as XML processing technology.

It aims to provide good JavaDoc documentation, clean code, an easy to use API and a high level of software quality (which is currently ensured by 700+ unit tests).

It supports the core specifications [RFC 6120](https://xmpp.org/rfcs/rfc6120.html), [RFC 6121](https://xmpp.org/rfcs/rfc6121.html), [RFC 7622](https://tools.ietf.org/html/rfc7622), as well as many [extensions](https://xmpp.org/xmpp-protocols/xmpp-extensions/).

Since this project is quite young, the API might change. Comments on the API are appreciated.

# Project Links
|
|---------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Blog                            | [blog.xmpp.rocks](http://blog.xmpp.rocks)                                                                                                                                           |
| Documentation                   | [docs.xmpp.rocks](http://docs.xmpp.rocks)                                                                                                                                           |
| Mailing List                    | [groups.xmpp.rocks](http://groups.xmpp.rocks)                                                                                                                                       |
| Latest Version in Maven Central | [![Maven Central](http://img.shields.io/maven-central/v/rocks.xmpp/xmpp-core-client.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/rocks.xmpp/xmpp-core-client)  |
| License                         | [![License](http://img.shields.io/badge/license-MIT-red.svg?style=flat)](https://bitbucket.org/sco0ter/babbler/src/master/LICENSE.txt)                                              |


# Maven Dependency

```xml
<dependency>
    <groupId>rocks.xmpp</groupId>
    <artifactId>xmpp-core-client</artifactId>
    <version>0.7.5</version>
</dependency>
<dependency>
    <groupId>rocks.xmpp</groupId>
    <artifactId>xmpp-extensions-client</artifactId>
    <version>0.7.5</version>
</dependency>
```

## Snapshots

Development snapshots are available on OSS Sonatype nexus:

```xml
<repositories>
    <repository>
        <id>sonatype-nexus-snapshots</id>
        <name>Sonatype Nexus Snapshots</name>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>rocks.xmpp</groupId>
    <artifactId>xmpp-core-client</artifactId>
    <version>0.8.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>rocks.xmpp</groupId>
    <artifactId>xmpp-extensions-client</artifactId>
    <version>0.8.0-SNAPSHOT</version>
</dependency>
```

# Supported Extensions
*(Only listing historical and standard tracks extensions with status 'Draft' or 'Final' that are applicable to XMPP clients)*

* ![supported][supported]           [XEP-0004: Data Forms](https://xmpp.org/extensions/xep-0004.html)
* ![supported][supported]           [XEP-0009: Jabber-RPC](https://xmpp.org/extensions/xep-0009.html)
* ![supported][supported]           [XEP-0012: Last Activity](https://xmpp.org/extensions/xep-0012.html)
* ![supported][supported]           [XEP-0013: Flexible Offline Message Retrieval](https://xmpp.org/extensions/xep-0013.html)
* ![supported][supported]           [XEP-0016: Privacy Lists](https://xmpp.org/extensions/xep-0016.html)
* ![supported][supported]           [XEP-0020: Feature Negotiation](https://xmpp.org/extensions/xep-0020.html)
* ![supported][supported]           [XEP-0030: Service Discovery](https://xmpp.org/extensions/xep-0030.html)
* ![supported][supported]           [XEP-0033: Extended Stanza Addressing](https://xmpp.org/extensions/xep-0033.html)
* ![supported][supported]           [XEP-0045: Multi-User Chat](https://xmpp.org/extensions/xep-0045.html)
* ![supported][supported]           [XEP-0047: In-Band Bytestreams](https://xmpp.org/extensions/xep-0047.html)
* ![supported][supported]           [XEP-0048: Bookmarks](https://xmpp.org/extensions/xep-0048.html)
* ![supported][supported]           [XEP-0049: Private XML Storage](https://xmpp.org/extensions/xep-0049.html)
* ![not supported][not supported]   [XEP-0050: Ad-Hoc Commands](https://xmpp.org/extensions/xep-0050.html)
* ![supported][supported]           [XEP-0054: vcard-temp](https://xmpp.org/extensions/xep-0054.html)
* ![supported][supported]           [XEP-0055: Jabber Search](https://xmpp.org/extensions/xep-0055.html)
* ![supported][supported]           [XEP-0059: Result Set Management](https://xmpp.org/extensions/xep-0059.html)
* ![supported][supported]           [XEP-0060: Publish-Subscribe](https://xmpp.org/extensions/xep-0060.html)
* ![supported][supported]           [XEP-0065: SOCKS5 Bytestreams](https://xmpp.org/extensions/xep-0065.html)
* ![supported][supported]           [XEP-0066: Out of Band Data](https://xmpp.org/extensions/xep-0066.html)
* ![supported][supported]           [XEP-0070: Verifying HTTP Requests via XMPP](https://xmpp.org/extensions/xep-0070.html)
* ![supported][supported]           [XEP-0071: XHTML-IM](https://xmpp.org/extensions/xep-0071.html)
* ![supported][supported]           [XEP-0072: SOAP Over XMPP](https://xmpp.org/extensions/xep-0072.html)
* ![supported][supported]           [XEP-0077: In-Band Registration](https://xmpp.org/extensions/xep-0077.html)
* ![supported][supported]           [XEP-0079: Advanced Message Processing](https://xmpp.org/extensions/xep-0079.html)
* ![supported][supported]           [XEP-0080: User Location](https://xmpp.org/extensions/xep-0080.html)
* ![supported][supported]           [XEP-0084: User Avatar](https://xmpp.org/extensions/xep-0084.html)
* ![supported][supported]           [XEP-0085: Chat State Notifications](https://xmpp.org/extensions/xep-0085.html)
* ![supported][supported]           [XEP-0092: Software Version](https://xmpp.org/extensions/xep-0092.html)
* ![supported][supported]           [XEP-0095: Stream Initiation](https://xmpp.org/extensions/xep-0095.html)
* ![supported][supported]           [XEP-0096: SI File Transfer](https://xmpp.org/extensions/xep-0096.html)
* ![supported][supported]           [XEP-0106: JID Escaping](https://xmpp.org/extensions/xep-0106.html)
* ![supported][supported]           [XEP-0107: User Mood](https://xmpp.org/extensions/xep-0107.html)
* ![supported][supported]           [XEP-0108: User Activity](https://xmpp.org/extensions/xep-0108.html)
* ![supported][supported]           [XEP-0114: Jabber Component Protocol](https://xmpp.org/extensions/xep-0114.html)
* ![supported][supported]           [XEP-0115: Entity Capabilities](https://xmpp.org/extensions/xep-0115.html)
* ![supported][supported]           [XEP-0118: User Tune](https://xmpp.org/extensions/xep-0118.html)
* ![supported][supported]           [XEP-0122: Data Forms Validation](https://xmpp.org/extensions/xep-0122.html)
* ![supported][supported]           [XEP-0124: Bidirectional-streams Over Synchronous HTTP (BOSH)](https://xmpp.org/extensions/xep-0124.html)
* ![supported][supported]           [XEP-0131: Stanza Headers and Internet Metadata](https://xmpp.org/extensions/xep-0131.html)
* ![not supported][not supported]   [XEP-0136: Message Archiving](https://xmpp.org/extensions/xep-0136.html)
* ![not supported][not supported]   [XEP-0137: Publishing Stream Initiation Requests](https://xmpp.org/extensions/xep-0137.html)
* ![supported][supported]           [XEP-0138: Stream Compression](https://xmpp.org/extensions/xep-0138.html)
* ![supported][supported]           [XEP-0141: Data Forms Layout](https://xmpp.org/extensions/xep-0141.html)
* ![supported][supported]           [XEP-0144: Roster Item Exchange](https://xmpp.org/extensions/xep-0144.html)
* ![supported][supported]           [XEP-0145: Annotations](https://xmpp.org/extensions/xep-0145.html)
* ![supported][supported]           [XEP-0152: Reachability Addresses](https://xmpp.org/extensions/xep-0152.html)
* ![supported][supported]           [XEP-0153: vCard-Based Avatars](https://xmpp.org/extensions/xep-0153.html)
* ![not supported][not supported]   [XEP-0155: Stanza Session Negotiation](https://xmpp.org/extensions/xep-0155.html)
* ![supported][supported]           [XEP-0156: Discovering Alternative XMPP Connection Methods](https://xmpp.org/extensions/xep-0156.html)
* ![not supported][not supported]   [XEP-0158: CAPTCHA Forms](https://xmpp.org/extensions/xep-0158.html)
* ![supported][supported]           [XEP-0163: Personal Eventing Protocol](https://xmpp.org/extensions/xep-0163.html)
* ![in development][in development] [XEP-0166: Jingle](https://xmpp.org/extensions/xep-0166.html)
* ![not supported][not supported]   [XEP-0167: Jingle RTP Sessions](https://xmpp.org/extensions/xep-0167.html)
* ![supported][supported]           [XEP-0171: Language Translation](https://xmpp.org/extensions/xep-0171.html)
* ![supported][supported]           [XEP-0172: User Nickname](https://xmpp.org/extensions/xep-0172.html)
* ![not supported][not supported]   [XEP-0174: Serverless Messaging](https://xmpp.org/extensions/xep-0174.html)
* ![not supported][not supported]   [XEP-0176: Jingle ICE-UDP Transport Method](https://xmpp.org/extensions/xep-0176.html)
* ![not supported][not supported]   [XEP-0177: Jingle Raw UDP Transport Method](https://xmpp.org/extensions/xep-0177.html)
* ![supported][supported]           [XEP-0184: Message Delivery Receipts](https://xmpp.org/extensions/xep-0184.html)
* ![supported][supported]           [XEP-0191: Blocking Command](https://xmpp.org/extensions/xep-0191.html)
* ![supported][supported]           [XEP-0198: Stream Management](https://xmpp.org/extensions/xep-0198.html)
* ![supported][supported]           [XEP-0199: XMPP Ping](https://xmpp.org/extensions/xep-0199.html)
* ![supported][supported]           [XEP-0202: Entity Time](https://xmpp.org/extensions/xep-0202.html)
* ![supported][supported]           [XEP-0203: Delayed Delivery](https://xmpp.org/extensions/xep-0203.html)
* ![supported][supported]           [XEP-0206: XMPP Over BOSH](https://xmpp.org/extensions/xep-0206.html)
* ![supported][supported]           [XEP-0221: Data Forms Media Element](https://xmpp.org/extensions/xep-0221.html)
* ![supported][supported]           [XEP-0224: Attention](https://xmpp.org/extensions/xep-0224.html)
* ![not supported][not supported]   [XEP-0229: Stream Compression with LZW](https://xmpp.org/extensions/xep-0229.html)
* ![not supported][not supported]   [XEP-0231: Bits of Binary](https://xmpp.org/extensions/xep-0231.html)
* ![supported][supported]           [XEP-0249: Direct MUC Invitations](https://xmpp.org/extensions/xep-0249.html)
* ![supported][supported]           [XEP-0256: Last Activity in Presence](https://xmpp.org/extensions/xep-0256.html)
* ![not supported][not supported]   [XEP-0258: Security Labels in XMPP](https://xmpp.org/extensions/xep-0258.html)
* ![in development][in development] [XEP-0260: Jingle SOCKS5 Bytestreams Transport Method](https://xmpp.org/extensions/xep-0260.html)
* ![in development][in development] [XEP-0261: Jingle In-Band Bytestreams Transport Method](https://xmpp.org/extensions/xep-0261.html)
* ![not supported][not supported]   [XEP-0262: Use of ZRTP in Jingle RTP Sessions](https://xmpp.org/extensions/xep-0262.html)
* ![not supported][not supported]   [XEP-0266: Codecs for Jingle Audio](https://xmpp.org/extensions/xep-0266.html)
* ![supported][supported]           [XEP-0297: Stanza Forwarding](https://xmpp.org/extensions/xep-0297.html)
* ![supported][supported]           [XEP-0301: In-Band Real Time Text](https://xmpp.org/extensions/xep-0301.html)
* ![supported][supported]           [XEP-0308: Last Message Correction](https://xmpp.org/extensions/xep-0308.html)
* ![supported][supported]           [XEP-0319: Last User Interaction in Presence](https://xmpp.org/extensions/xep-0319.html)
* ![not supported][not supported]   [XEP-0368: SRV records for XMPP over TLS](https://xmpp.org/extensions/xep-0368.html)


Supported experimental XEPs:

* ![supported][supported]           [XEP-0186: Invisible Command](https://xmpp.org/extensions/xep-0186.html)
* ![supported][supported]           [XEP-0280: Message Carbons](https://xmpp.org/extensions/xep-0280.html)
* ![supported][supported]           [XEP-0300: Use of Cryptographic Hash Functions in XMPP](https://xmpp.org/extensions/xep-0300.html)
* ![supported][supported]           [XEP-0335: JSON Containers](https://xmpp.org/extensions/xep-0335.html)


Additionally following informational XEP documents are respected:

* ![supported][supported]           [XEP-0068: Field Standardization for Data Forms](https://xmpp.org/extensions/xep-0068.html)
* ![supported][supported]           [XEP-0082: XMPP Date and Time Profiles](https://xmpp.org/extensions/xep-0082.html)
* ![supported][supported]           [XEP-0083: Nested Roster Groups](https://xmpp.org/extensions/xep-0083.html)
* ![supported][supported]           [XEP-0126: Invisibility](https://xmpp.org/extensions/xep-0126.html)
* ![supported][supported]           [XEP-0128: Service Discovery Extensions](https://xmpp.org/extensions/xep-0128.html)
* ![supported][supported]           [XEP-0149: Time Periods](https://xmpp.org/extensions/xep-0149.html)
* ![supported][supported]           [XEP-0170: Recommended Order of Stream Feature Negotiation](https://xmpp.org/extensions/xep-0170.html)
* ![supported][supported]           [XEP-0175: Best Practices for Use of SASL ANONYMOUS](https://xmpp.org/extensions/xep-0175.html)
* ![supported][supported]           [XEP-0201: Best Practices for Message Threads](https://xmpp.org/extensions/xep-0201.html)
* ![supported][supported]           [XEP-0205: Best Practices to Discourage Denial of Service Attacks](https://xmpp.org/extensions/xep-0205.html)
* ![supported][supported]           [XEP-0222: Persistent Storage of Public Data via PubSub](https://xmpp.org/extensions/xep-0222.html)
* ![supported][supported]           [XEP-0223: Persistent Storage of Private Data via PubSub](https://xmpp.org/extensions/xep-0223.html)

# License

This project is licensed under [MIT License](http://opensource.org/licenses/MIT).

# Getting Started
---

## Establishing an XMPP Session

The first thing you want to do in order to connect to an XMPP server is creating a `XmppClient` object:

```java
XmppClient xmppClient = XmppClient.create("domain");
```

The `XmppClient` instance is the central object. Every other action you will do revolves around this instance (e.g. sending and receiving messages).

A session to an XMPP server can be established in three ways (connection methods):

1. By a [normal TCP socket connection](https://xmpp.org/rfcs/rfc6120.html#tcp)
2. By a [BOSH connection (XEP-0124)](https://xmpp.org/extensions/xep-0124.html)
3. By a [WebSocket connection (RFC 7395)](https://tools.ietf.org/html/rfc7395)

By default, the `XmppClient` will try to establish a connection via TCP first during the connection process.
If the connection fails, it will try to discover alternative connection methods and try to connect with one of them (usually BOSH).
The hostname and port is determined by doing a DNS lookup.

### Configuring the Connections

You can also configure different connection methods manually (e.g. if you want to use another port or want to use a proxy).

In order to create immutable and reusable configuration objects (which could be reused by multiple sessions) and to avoid huge constructors, the Builder Pattern is used to create custom configurations:

```java
SocketConnectionConfiguration tcpConfiguration = SocketConnectionConfiguration.builder()
    .hostname("localhost")
    .port(5222)
    .build();
```
Here's another example how to configure a BOSH connection (which would connect to the URL `http://domain:5280/http-bind/` over a HTTP proxy server):

```java
BoshConnectionConfiguration boshConfiguration = BoshConnectionConfiguration.builder()
    .hostname("domain")
    .port(5280)
    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("hostname", 3128)))
    .path("/http-bind/")
    .build();
```

And this is how you would configure a WebSocket connection to `wss://host:7443/ws` (requires `xmpp-websocket` dependency):

```java
WebSocketConnectionConfiguration webSocketConfiguration = WebSocketConnectionConfiguration.builder()
    .hostname("host")
    .port(7443)
    .path("/ws/")
    .sslContext(sslContext)
    .channelEncryption(ChannelEncryption.DIRECT)
    .build();
```

Now let's pass them to the session to tell it that it should use them:

```java
XmppClient xmppClient = XmppClient.create("domain", tcpConfiguration, boshConfiguration);
```

During connecting, the session will try all configured connections in order, until a connection is established.


#### Securing the Connection

You can set a custom `SSLContext` by configuring it like this:

```java
SocketConnectionConfiguration tcpConfiguration = SocketConnectionConfiguration.builder()
    .channelEncryption(ChannelEncryption.OPTIONAL)
    .sslContext(sslContext)
    .hostnameVerifier(hostnameVerifier)
    .build();
```

Note that the use of a custom `HostnameVerifier` is possible but not recommended in most cases, since the built-in logic to verify the host name does a good job.

## Preparing the Session

Before connecting to a server, you should configure your XMPP session.

You might want to do one of the following:

* Adding event listeners in order to listen for inbound messages, roster and presence changes or to modify outbound messages.
* Setting up a custom SSL context
* Configuring extensions, e.g.
    * Enable or disable certain extensions
    * Setting an identity for the connection (Service Discovery)
    * etc.


Here are some examples:

```java
// Listen for presence changes
xmppClient.addInboundPresenceListener(e -> {
    // Handle inbound presence.
});
// Listen for messages
xmppClient.addInboundMessageListener(e -> {
    // Handle inbound message
});
// Listen for roster pushes
xmppClient.getManager(RosterManager.class).addRosterListener(e -> {

});
```

## Connecting to a Server

If you have prepared your session, you are now ready to connect to the server:

```java
try {
   xmppClient.connect();
} catch (XmppException e) {
   // ...
}
```

The session will try to connect to the XMPP server by using the configured connections in order.

Connecting involves opening the initial XMPP stream header and negotiate any features offered by the server (most likely only TLS).


## Authenticating and Binding a Resource

After connecting, you have to authenticate and bind a resource, in order to become a \"connected resource\". Both steps are understood as \"login\":

```java
try {
   xmppClient.login("username", "password", "resource");
} catch (AuthenticationException e) {
   // Login failed
}
```

Initial presence is sent automatically, so that you are now an \"available resource\" (you will appear online to your contacts) and can now start sending messages.

## Sending a Message

Sending a simple chat message works like this:

```java
xmppClient.send(new Message(Jid.of("juliet@example.net"), Message.Type.CHAT));
```

## Changing Availability

If you want to change your presence availability, just send a new presence with a \"show\" value.

```java
xmppClient.send(new Presence(Presence.Show.AWAY));
```

## Closing the Session

Closing a session is simply done with:

```java
xmppClient.close();
```

Note, that `XmppClient` implements `java.lang.AutoCloseable`, which means you can also use the try-with-resources statement, which automatically closes the session:

```java
try (XmppClient xmppClient = XmppClient.create("domain")) {
    xmppClient.connect();
} catch (XmppException e) {
    // handle exception
}
```

[supported]: https://bitbucket.org/sco0ter/babbler/raw/master/xmpp-documentation/src/site/resources/supported.png "Is supported"
[not supported]: https://bitbucket.org/sco0ter/babbler/raw/master/xmpp-documentation/src/site/resources/notsupported.png "Is not supported"
[in development]: https://bitbucket.org/sco0ter/babbler/raw/master/xmpp-documentation/src/site/resources/development.png "Is in development or planned"