# XEP-0092: Software Version
---

[XEP-0092: Software Version][Software Version] allows you to discover the software version and operating system associated with another XMPP entity.


## Querying Another Entity for Its Software Version

```java
SoftwareVersionManager softwareVersionManager = xmppClient.getManager(SoftwareVersionManager.class);
SoftwareVersion softwareVersion = softwareVersionManager.getSoftwareVersion(Jid.of("example.net")).getResult();
```

If you query a server, you will obviously get the server software.
If you query a full JID, you will get the client software associated with the connected resource.

## Setting Your Own Software Version

You can set your own version, if you want other entities to discover your version:

```java
softwareVersionManager.setSoftwareVersion(new SoftwareVersion("MyClient", "0.9"));
```

[Software Version]: https://xmpp.org/extensions/xep-0092.html "XEP-0092: Software Version"
