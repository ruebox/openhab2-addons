# About
---

**Babbler** is a young [XMPP][XMPP] client library for Java 8 SE built on top of [JAXB][JAXB], which allows you to write **XMPP clients** as well as **external server components** using the [XEP-0114: Jabber Component Protocol](https://xmpp.org/extensions/xep-0114.html).

[JAXB][JAXB] as underlying technology makes it robust and allows you to write extensions in a simple and clean manner.

It aims to provide good (JavaDoc) documentation, clean code, an easy to use API and a high level of software quality (which is currently ensured by more than 800 unit tests).

**Note:** Since this project is quite young, it is still a work in progress and the API might change. Comments on the API are highly appreciated (write a message on my [BitBucket account](https://bitbucket.org/sco0ter) or comment on my [Blog](http://babbler-xmpp.blogspot.de/)).

On this site you learn about some general design considerations, a brief user guide and API documentation.

## Features

* **XMPP core specifications** ([RFC 6120][RFC 6120], [RFC 6121][RFC 6121], [RFC 7622][RFC 7622])
* Various authentication methods: SCRAM-SHA-1, DIGEST-MD5, CRAM-MD5, PLAIN, ANONYMOUS
* Security: TLS with Hostname Verification
* **Modern API**, leveraging many of new Java 8 features like:
    * [new Date Time API](http://www.oracle.com/technetwork/articles/java/jf14-date-time-2125367.html)
    * Lambda Expressions
    * Functional Interfaces
* 800+ unit tests
* Support for **70+** [extensions](https://xmpp.org/xmpp-protocols/xmpp-extensions/), including important ones like:
    * Multi-User Chat
    * File Transfer
    * PubSub / PEP
    * Entity Capabilities
    * BOSH
    * Jabber Component Protocol
* **Compact library**, no dependencies, one core package: `rocks.xmpp`
* [JAXB][JAXB] (XML binding) in the background, in order to make it **robust**, **easy to use** and easy to extend (e.g. writing custom protocol extensions)
* Good documentation
* Very liberal **MIT license**
* Works as [JPMS](jpms.md) module on **Java 9** *and* as conventional library JAR on Java 8


[JAXB]: http://en.wikipedia.org/wiki/Java_Architecture_for_XML_Binding "Java Architecture for XML Binding"
[XMPP]: https://xmpp.org "eXtensible Messaging and Presence Protocol"
[RFC 6120]: https://xmpp.org/rfcs/rfc6120.html "Extensible Messaging and Presence Protocol (XMPP): Core"
[RFC 6121]: https://xmpp.org/rfcs/rfc6121.html "Extensible Messaging and Presence Protocol (XMPP): Instant Messaging and Presence"
[RFC 7622]: https://tools.ietf.org/html/rfc7622 "Extensible Messaging and Presence Protocol (XMPP): Address Format"