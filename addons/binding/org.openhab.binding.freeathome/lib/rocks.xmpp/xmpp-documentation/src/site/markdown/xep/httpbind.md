# HTTP Binding / BOSH
---

[BOSH][BOSH] is a transport protocol which emulates the semantics of a long-lived, bidirectional TCP connection between two entities
by efficiently using multiple synchronous HTTP request/response pairs.

This is often useful, if port 5222 is blocked by a firewall or your XMPP server is otherwise only accessible via HTTP.

Configuration is straight forward as shown in the sample below.

## HTTP Pre-Binding

The following sample connects, logs in and finally closes ("detaches") the session (in your client only!). The server still holds session information.
This is called pre-binding and can be used, if you want to transport the session from Java to JavaScript for example.

```java
// Connects to http://domain:5280/http-bind/
BoshConnectionConfiguration boshConnectionConfiguration = BoshConnectionConfiguration.builder()
        .hostname("domain")
        .port(5280)
        .path("/http-bind/")
        .build();
        
        
try (XmppClient xmppClient = XmppClient.create("domain", boshConnectionConfiguration)) {
    // Connect
    xmppClient.connect();
    // Login
    xmppClient.login("username", "password", "resource");
    
    BoshConnection boshConnection = (BoshConnection) xmppClient.getActiveConnection();
    
    // Gets the session id (sid) of the BOSH connection.
    String sessionId = boshConnection.getSessionId();
    
    // Detaches the BOSH session, without terminating it.
    long rid = boshConnection.detach();
    
    System.out.println("JID: " + xmppClient.getConnectedResource());
    System.out.println("SID: " + sessionId);
    System.out.println("RID: " + rid);
} catch (XmppException e) {
    e.printStackTrace();
}
```

## Protecting Insecure Sessions

BOSH also specifies a technique to [protect insecure sessions][Protecting Insecure Sessions].
This can be easily enabled (if your server supports it as well) by a configration option:

```java
BoshConnectionConfiguration boshConnectionConfiguration = BoshConnectionConfiguration.builder()
    // ...
    .useKeySequence(true)
    .build();
```

[BOSH]: https://xmpp.org/extensions/xep-0124.html "XEP-0124: Bidirectional-streams Over Synchronous HTTP (BOSH)"
[Protecting Insecure Sessions]: https://xmpp.org/extensions/xep-0124.html#keys "XEP-0124: Bidirectional-streams Over Synchronous HTTP (BOSH)"