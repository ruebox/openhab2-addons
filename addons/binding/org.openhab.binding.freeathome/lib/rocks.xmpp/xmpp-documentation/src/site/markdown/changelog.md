# Changelog
---

### Version 0.8.1 (2019-03-06)

* Limit the thread usage when multiple clients are running in the same JVM
* Make the JVM shutdown hook optionally ([Issue #150](https://bitbucket.org/sco0ter/babbler/issues/150))
* Fix NPE in InfoDiscovery#getFeatures() ([Issue #146](https://bitbucket.org/sco0ter/babbler/issues/146))
* Remove errant &lt;p&gt; element in javadoc ([Issue #144](https://bitbucket.org/sco0ter/babbler/issues/144))
* Convert hostname to ASCII before applying it ([Issue #143](https://bitbucket.org/sco0ter/babbler/issues/143))
* Don't demand a full JID for SI file transfer ([Issue #142](https://bitbucket.org/sco0ter/babbler/issues/142))
* Make debugger optional in WebSocketConnection and NettyTcpConnection ([Issue #141](https://bitbucket.org/sco0ter/babbler/issues/141))
* MessageDeliveryReceiptsManager: correctly set from attribute ([Issue #139](https://bitbucket.org/sco0ter/babbler/issues/139))
* Make use of `TcpConnectionConfiguration#getKeepAliveInterval` in `NettyTcpConnection`


## Version 0.8.0 (2018-07-17)

* Works as [JPMS modules](jpms.md) on Java 9 *and* as conventional JAR library on Java 8
* Make `Jid` class an interface. Full JIDs and bare JIDs now share the same instance. No new instances need to be created when calling `asBareJid()`, the interface just returns a different view on the full JID.
Reduces GC pressure and increase performance.
* Allow Unicode (non-ASCII) domain names in JIDs ([Issue #128](https://bitbucket.org/sco0ter/babbler/issues/128))
* Update `Jid` implementation to use new PRECIS specification [RFC 8264](https://tools.ietf.org/html/rfc8264)
* Add a `MalformedJid` implementation to allow to use malformed-jid stanza error.
* Add Java NIO support
* Every connection now has `closeAsync()` method
* Allow to set a custom thread factory for all threads being started
* Add support for [XEP-0390: Entity Capabilities 2.0](http://www.xmpp.org/extensions/xep-0390.html)
* Add support for [XEP-0392: Consistent Color Generation](http://www.xmpp.org/extensions/xep-0392.html)
* Add low-level support for [XEP-0258: Security Labels in XMPP](http://www.xmpp.org/extensions/xep-0258.html)
* Add low-level support for [XEP-0264: Jingle Content Thumbnails](http://www.xmpp.org/extensions/xep-0264.html)
* Add low-level support for [XEP-0352: Client State Indication](http://www.xmpp.org/extensions/xep-0352.html)
* Add a class `StreamHeader` which represents the stream header and checks for the rules in RFC 6120.
* Refactor Text classes from the `urn:ietf:params:xml:ns:xmpp-stanzas`, `urn:ietf:params:xml:ns:xmpp-streams` and `urn:ietf:params:xml:ns:xmpp-sasl` namespaces into one common `rocks.xmpp.core.Text` class (API change).
* Add `putExtension()` and `addExtensions()` methods to Stanza.
* Move `rocks.xmpp.core.stream.StreamErrorException` to `rocks.xmpp.core.stream.model.StreamErrorException`
* XEP-0033: Extended Stanza Addressing: Implement reply handling, add `delivered` attribute, add varargs constructor.
* XEP-0045: Revoking admin status should result in mere membership, rather than no affiliation at all.
* XEP-0059: Add `nextPage()` and `previousPage()` method to result set management. Also refine the naming of the RSM methods.
* XEP-0085: Add `isSupported` method for Chat State Notifications (either discover implicit support or else use service discovery)
* XEP-0096: Only return a single value during SI FileTransfer negotiation.
* XEP-0115: Sort by octets, not by characters.
* XEP-0300: Update to urn:xmpp:hashes:2
* XEP-0184: Receipt messages should have the same type as the request message instead of normal type.
* Fix Message#createError() ([Issue #127](https://bitbucket.org/sco0ter/babbler/issues/127))
* Check the 'from' attribute of IQ responses to prevent spoofing
* Ensure that xsi:type values have a known namespace prefix ([Issue #137](https://bitbucket.org/sco0ter/babbler/issues/137))
* EntitityCapabilities payload is not added to outgoing presence when presence have 'to' address ([Issue #132](https://bitbucket.org/sco0ter/babbler/issues/132))
* sendAndAwaitMessage not handling concurrent requests/responses correctly ([Issue #136](https://bitbucket.org/sco0ter/babbler/issues/136))


### Version 0.7.5 (2018-01-27)

* Add stream ID to ConsoleDebugger output ([Issue #105](https://bitbucket.org/sco0ter/babbler/issues/105)).
* Improve CustomIQ example and documentation ([Issue #112](https://bitbucket.org/sco0ter/babbler/issues/112)).
* PingManager: Make exception for feature-not-implemented ([Issue #113](https://bitbucket.org/sco0ter/babbler/issues/113)).
* Allow configuration of custom name server for DNS SRV resolution.
* MUC service discovery should handle items with non-room JIDs ([Issue #106](https://bitbucket.org/sco0ter/babbler/issues/106)).
* Connecting to stream hosts should not exceed configured response timeout ([Issue #111](https://bitbucket.org/sco0ter/babbler/issues/111)).
* Fixed DataForm.getReportedFields() to work when null.
* Fix NullPointerException in RPC Value class ([Issue #117](https://bitbucket.org/sco0ter/babbler/issues/117)).
* Fix memory leak on WebSocket connection failure ([Issue #122](https://bitbucket.org/sco0ter/babbler/issues/122))
* Ensure WebSocket session is closed, if server does not respond with `<close/>` element.
* Store avatar image using correct hash code.

### Version 0.7.4 (2017-03-14)

* Resolve rare deadlock when using Stream Management
* Rework how WebSocket connections are closed
* Don't let a stream error close the stream immediately, but instead wait for the closing stream element and then close the connection.
* Increase performance of IBB stream
* Prevent rare, but possible `NullPointerException` after sending stanzas.
* Fix error when using pages (XEP-0141) within data forms (XEP-0004)
* Reset nick to `null`, if entering a chat room fails

### Version 0.7.3 (2017-02-09)

* Use single equals sign ("=") for zero-length data in SASL, as per RFC 6120 § 6.4.2
* Allow configuring a custom stream host and skip proxy discovery then for SI file transfer.
* Implement WebSocket pings/pongs.
* Fix WebSocket's proxy URI construction.
* Use connect timeout for WebSocket connections.
* XEP-0198: Send an ack right before gracefully closing the stream (i.e. update to version 1.5.2).
* MUC Room "enter" events should fire for oneself entering the room as well.
* Use `java.text.Collator` for String-based default comparison.
* XEP-0066: Use URI instead of URL.
* Fix XMPP Ping in External Components, which broke the connection.
* `Jid.asBareJid` returns `this` if it is already bare, reducing GC pressure.
* `connect()` method should not throw `CancellationException`
* Check if the connection has been secured (if configured) before starting to authenticate.

### Version 0.7.2 (2016-09-08)

* Fix reconnection issue, when using multiple connection methods per session.
* Improve and fix stanza acknowledging and Stream Management
    * Add Delayed Delivery (XEP-0203) extension to stanzas, which are resent automatically later (when reconnected again)
    * Always resent all unacknowledged stanzas after login, not only after stream resumption.
    * Highlight StreamManagement's request / answer pairs in VisualDebugger.
    * Update XEP-0198 Stream Management to version 1.5 (respect the 'h' attribute in the `failed` element)
* Wait for the roster response before sending initial presence during login, to prevent receiving presence information from yet unknown contacts.
* Make sure asynchronous method calls do not block (affected only few methods for avatars and entity capabilities)
* Use the hostname instead of the domain for SASL clients (i.e. use the `Sasl.createSaslClient` API correctly as per the documentation, may affect DIGEST-MD5 authentication).
* Call `SaslClient.dispose()` when SASL authentication has completed.
* Include the requesting IQ in `NoResponseException`, when doing IQ queries.
* XEP-0184: Add the sender of a receipt to the MessageDeliveryEvent.
* Allow event consumption for outbound stanzas, which prevents the stanza to be sent.
* Make stream feature negotiation more stable.
* Minor graphical fixes in VisualDebugger.
* Add API to include the hash and mime type in File Transfer offers.
* Add API to create a chat session with a thread id.
* Immediately complete (IQ-)queries if sending failed and don't wait on the timeout.

### Version 0.7.1 (2016-08-09)

* Discovering services should not fail immediately if one sub-query fails.
* Make sure abnormal WebSocket disconnections trigger the reconnection.
* Make sure RECONNECTION_SUCCEEDED event is triggered for external components.
* Add listeners to listen for successful or failed send operations.
* Add public constructor for the SASL challenge class.
* Add public constructors to SASL Failure class.
* Make sure to not write `XMLConstants.XML_NS_URI` to XML elements (FasterXML Aalto's XMLStreamWriter implementation writes it)
* Add `DataForm.Field#getValue()` and implement `toString()` method.
* Add convenient API to compare two MUC affiliations and roles (i.e. `Affiliation.OWNER.isHigherThan(Affiliation.ADMIN)`)
* Compare presences of MUC occupants in the Occupant's `Comparable` implementation.
* Minor performance improvement by using a `ListIterator` in collection based result sets.
* Discover PubSub services by identity, not by feature name (it's more reliable)
* Add `nextPage()` and `previousPage()` method and refine the naming of other methods in result set management (e.g. having `forCount()` and `forItemCount()` was confusing)

## Version 0.7.0 (2016-06-05)

* Add support for [XEP-0198: Stream Management](http://www.xmpp.org/extensions/xep-0198.html)
* Add support for WebSocket connection method ([RFC 7395](https://tools.ietf.org/html/rfc7395)).
* Update `Jid` class to the new XMPP Address Format ([RFC 7622](https://tools.ietf.org/html/rfc7622))
* IQ queries can now be executed asynchronously (non-blocking) using Java 8's `java.util.concurrent.CompletableFuture` API.
* Represent `xml:lang` attributes as `java.util.Locale`, not as `String`.
* Represent timeouts as `java.time.Duration` instead of `int`/`long` for better clearness.
* Add a very minimalistic DNS resolver for resolving SRV and TXT records in order to remove the dependency to `com.sun.*` classes.
* Add more `ReconnectionStrategy` implementations.
* Check connected state of socket before connecting (to prevent `SocketException` when a `SocketFactory` provides a connected socket)
* Add `XmppSession#isAuthenticated()` method.
* Add static `XmppSession#addCreationListener()` method to allow to listen for newly created sessions.
* Update XEP-0080 to version 1.9 (add `altaccuracy` element).
* Add API to destroy a MUC room without a reason.
* More documentation, e.g. clarify the use of `ConnectionConfiguration#secure()`
* Don't include an empty body in Message Delivery Receipts.
* Add correct XML names to component namespace stanzas.
* Eagerly release unused port to prevent ports-leaks due to delayed GC
* Improve thread-safety during `connect()` and `login()`
* Fix encoding issues, caused by missing UTF-8 encoding, mainly in the debugger.
* XEP-0033: Address should have extensions.
* Add workaround for a [JDK bug](https://bugs.openjdk.java.net/browse/JDK-8054446) causing memory issues and high CPU.
* Add documentation for custom SASL authentication.

### Version 0.6.2 (2015-12-08)

* Fix bug when closing a BOSH connection and when using key sequences.
* Prevent long DNS resolution of `InetAddress.getHostname()` when using an IP address in TCP connection as hostname.
* Make the acceptance of file transfer requests more stable.
* Real-time Text (XEP-0301) should count Unicode code points instead of characters (e.g. a surrogate pair counts as 1, not as 2)
* Prevent high CPU usage when receiving and processing real-time text.
* Include session id in `<streamhost-used/>` SOCKS5 query.
* Prevent `UnsupportedOperationException` when suggesting a contact addition (XEP-0144 Roster Item Exchange).
* Prefer `CharSequence` over `String` in some APIs.

### Version 0.6.1 (2015-09-13)

* Verify SCRAM-SHA-1 server response during login.
* Add `Jid.atSubdomain()` method and use `CharSequence` in static `Jid` factory methods.
* Fix bug with [FasterXML/Aalto's](http://wiki.fasterxml.com/AaltoHome) `XMLStreamWriter` implementation.
* Add shortcuts to retrieve the error condition from `StanzaException` and `StreamErrorException`. 
* Validate MUC room JID, when creating a chat room.
* Add API to retrieve owners, admins and outcasts in a chat room.
* Fix bug when declining a MUC invitation.
* Minor bug fixes in VisualDebugger.
* Add factory method to create XEP-131 headers from a `Map`.
* Improve BoshConnection class by queuing up stanzas and send multiple stanzas in the same request.
* Add API to better listen for disconnects and reconnects.
* Process inbound IQs on another thread as messages and presences. This allows to query IQs from within a Message listener without using extra threads/executors, which seems to be a common use case.

## Version 0.6.0 (2015-08-12)

* Add support for [XEP-0114: Jabber Component Protocol](http://www.xmpp.org/extensions/xep-0114.html)
* Add support for [XEP-0171: Language Translation](https://xmpp.org/extensions/xep-0171.html)
* Add support for [XEP-0205: Best Practices to Discourage Denial of Service Attacks](http://www.xmpp.org/extensions/xep-0205.html) (error conditions)
* Add support for [XEP-0301: In-Band Real Time Text](http://www.xmpp.org/extensions/xep-0301.html)
* Add support for [XEP-0319: Last User Interaction in Presence](https://xmpp.org/extensions/xep-0319.html)
* Send initial presence automatically during login (no need to do it manually anymore).
* Disabled extensions no longer process stanzas (for increased performance).
* `XmppSession` is now `XmppClient` and derives the now abstract `XmppSession`. This is due to the added support for XEP-0114.
* `XmppSession` has a new convenient method for determining support of a feature (since it's used by many XEPs).
* `login` method now returns "additional data with success", i.e. the contents of the `<success/>` element.
* Add a new `ReconnectionStrategy` implementation, which always tries to reconnect after fix time.
* There's a new "xmpp-addr" project for [RFC 6122](https://xmpp.org/rfcs/rfc6122.html) which contains the `Jid` class, which has therefore also moved to a new package (`rocks.xmpp.addr`)
* Likewise the Roster and Chat classes have been moved from the 'core' to 'im' package to better resemble the separation of RFC 6120 and 6121.
* Use [Java 8's Functional Interfaces](https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html):
    * All event listeners have been replaced by `java.util.function.Consumer<T extends EventObject>` (if you used lambda expressions, nothing has changed for you)
* Use [Java 8's Date-Time API](https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html):
    * `java.util.TimeZone` has been replaced with `java.time.ZoneOffset`
    * `java.util.Date` has been replaced with `java.time.OffsetDateTime` or `java.time.Instant`
* Reduce logging overhead by deferred string building.
* XEP-0092 Software Version now responds with Babbler's version automatically.
* Stanza classes refactoring:
    * Stanzas moved from `rocks.xmpp.core.stanza.model.client` to `rocks.xmpp.core.stanza.model`. This was a necessary change for XEP-0114.
    * `Message.Body`, `Message.Subject`, `Presence.Status` classes have been replaced by a common `Text` class, since they are all the same.
    * There are new `addExtension()` / `removeExtension()` / `hasExtension()` methods.
* `XmppSession.getDomain()` returns a `Jid` instead of a `String`.    

### Version 0.5.1 (2015-06-18)

* Fix cross-compilation issue.
* Fix IllegalStateException caused from AvatarManager.
* Add support for [Woodstox JAXB implementation](http://woodstox.codehaus.org/).

## Version 0.5.0 (2015-03-21)

* Add support for [XEP-0059: Result Set Management](https://xmpp.org/extensions/xep-0059.html)
    * Specifically your hosted Service Discovery items (XEP-0030) can now return [limited result sets](https://xmpp.org/extensions/xep-0059.html#examples).
* Add support for [XEP-0222: Persistent Storage of Public Data via PubSub](https://xmpp.org/extensions/xep-0222.html)
* Add support for [XEP-0223: Persistent Storage of Private Data via PubSub](https://xmpp.org/extensions/xep-0223.html)
* Add support for [Roster Versioning](https://xmpp.org/rfcs/rfc6121.html#roster-versioning)
* Rework the way inbound/outbound stanzas are handled: Instead of `add[Message|Presence|IQ]Listener` you now have to use `addInbound[Message|Presence|IQ]Listener`. (API change!)
* Add `IQHandler` interface which allows to easily respond to IQ request.
* PubSub: Add support for [Publish Options](https://xmpp.org/extensions/xep-0060.html#publisher-publish-options)
* PubSub: Add API to retrieve subscription options for a specific subscription id.
* Harmonize Exception design: Most methods now only throw `XmppException` (or a subclass thereof)
* Add helper classes to work with [standardized MUC](https://xmpp.org/extensions/xep-0045.html#registrar-formtype) and [PubSub data forms](https://xmpp.org/extensions/xep-0060.html#registrar-formtypes), e.g. to configure a node.
* Rename some methods to better resemble the terminology of the specifications (e.g. `discover*` instead of `get*`).
* Refactor Chat State Notifications, Message Delivery Receipts, PingManager
* Refactor [XEP-0107](https://xmpp.org/extensions/xep-0107.html): Mood values can now take specific (custom) moods.
* Most extension classes are now immutable.
* Couple RosterManager more tightly with [XEP-0083: Nested Roster Groups](https://xmpp.org/extensions/xep-0083.html).
* Add new `connect(Jid from)` method to set the 'from' attribute in the stream header.
* Add new `login()` method, which allows to pass an authorization id and a `CallbackHandler`.
* Implement `Comparable` for ChatRoom and ChatService classes.
* MUC: Add API to discover [allowable traffic](https://xmpp.org/extensions/xep-0045.html#impl-service-traffic) in a chat room.
* MUC: Add support for [`http://jabber.org/protocol/muc#rooms`](https://xmpp.org/extensions/xep-0045.html#disco-client)
* Add support for [optional session establishment](http://tools.ietf.org/html/draft-cridland-xmpp-session-01).
* Add persistent (directory-based) cache support for [Entity Capabilities](https://xmpp.org/extensions/xep-0115.html) and [Avatars](https://xmpp.org/extensions/xep-0084.html).
* Use Singleton pattern for XML elements where appropriate (e.g. most stanza errors).
* Add support for BOSH compression (gzip, deflate)
* `XmppSession.getExtensionManager()` has been replaced with a more general `getManager()`, which also covers core managers.
* Various minor tweaks and bug fixes (e.g. thread-safety, `Objects.requireNonNull()`)


## Version 0.4.0 (2014-11-01)

* Add support for [XEP-0070: Verifying HTTP Requests via XMPP](https://xmpp.org/extensions/xep-0070.html)
* Add support for [XEP-0084: User Avatar](https://xmpp.org/extensions/xep-0084.html)
* Add visual debugger.
* First version available on Maven Central.
* Add ability for modularization.
* Improve package structure and rename base package to `rocks.xmpp` (due to new domain and Maven group id).
* More options to configure a connection (e.g. to set a `SocketFactory`, a keep-alive ping interval, etc...)
* Improve reconnection logic.
* Improvements for BOSH connection:
    * Allow secure HTTPS connection
    * Provide ability to use a [key sequencing mechanism](https://xmpp.org/extensions/xep-0124.html#keys).
    * minor improvements and tests with ejabberd server.
* Periodically ping the server (XEP-0199) to allow for a more stable connection.
* Add `isRequest()` and `isResponse()` method to IQ.
* Bug fix in authentication logic, which prevented successful authentication on ejabberd server.
* Various minor bug fixes (e.g. stuff like `NullPointerException`).
* Add more documentation.


## Version 0.3.0 (2014-08-02)

* Add support for [XEP-0047: In-Band Bytestreams](https://xmpp.org/extensions/xep-0047.html)
* Add support for [XEP-0065: SOCKS5 Bytestreams](https://xmpp.org/extensions/xep-0065.html)
* Add support for [XEP-0066: Out of Band Data](https://xmpp.org/extensions/xep-0066.html)
* Add support for [XEP-0071: XHTML-IM](https://xmpp.org/extensions/xep-0071.html)
* Add support for [XEP-0072: SOAP Over XMPP](https://xmpp.org/extensions/xep-0072.html)
* Add support for [XEP-0095: Stream Initiation](https://xmpp.org/extensions/xep-0095.html)
* Add support for [XEP-0096: SI File Transfer](https://xmpp.org/extensions/xep-0096.html)
* Add support for [XEP-0186: Invisible Command](https://xmpp.org/extensions/xep-0186.html)
* Add support for [XEP-0300: Use of Cryptographic Hash Functions in XMPP](https://xmpp.org/extensions/xep-0300.html)
* Fix Man-in-the-Middle vulnerability / Added Hostname Verification. [Read more about it](http://tersesystems.com/2014/03/23/fixing-hostname-verification/).
* Add `BookmarkManager` for more convenient management of bookmarks.
* Implement `Comparable` interface for bookmarks.
* Improve BOSH logic with regard to overactivity. Also updated BOSH version to 1.11.
* Stream errors now have their own `StreamError` class and no longer derive from `Exception` due to some JAXB problems.
* Enhance `RosterManager` API to easily rename or remove (nested) roster groups.
* Add helper methods to facilitate use of [XEP-0149: Time Periods](https://xmpp.org/extensions/xep-0149.html)
* Set encoding for the XMPP stream to UTF-8.
* Make connection establishment a little bit more robust.
* Increase performance by using buffered streams.
* Improve logic for [vCard based avatars](https://xmpp.org/extensions/xep-0153.html).
* Implement `toString()` method on many objects.
* Add more documentation.


## Version 0.2.0 (2014-07-06)

* Add support for [XEP-0045: Multi-User Chat](https://xmpp.org/extensions/xep-0045.html)
* Add support for [XEP-0079: Advanced Message Processing](https://xmpp.org/extensions/xep-0079.html)
* Add support for [XEP-0122: Data Forms Validation](https://xmpp.org/extensions/xep-0122.html)
* Add support for [XEP-0141: Data Forms Layout](https://xmpp.org/extensions/xep-0141.html)
* Add support for [XEP-0144: Roster Item Exchange](https://xmpp.org/extensions/xep-0144.html)
* Add support for [XEP-0280: Message Carbons](https://xmpp.org/extensions/xep-0280.html)
* Add support for [XEP-0335: JSON Containers](https://xmpp.org/extensions/xep-0335.html)
* RosterManager now supports (nested) contact groups
* Improve `Jid` class (nodeprep, resourceprep, better escaping logic, caching for better performance, `Comparable` interface implemented)
* Add JID Escaping feature (`jid\\escaping`) to feature list for Service Discovery.
* Restructure the project: It's now separated into modules:
    * xmpp-core, which contains XML schema implementations and core classes. It could theoretically be useful for a server implementation, too.
    * xmpp-client, which contains business logic, used by XMPP clients (e.g. connection logic, roster management, ...)
* The base `Connection` class is now called `XmppSession`, which can have multiple connection methods. Each connection method is tried while connecting. That way a XMPP session can have a normal `TcpConnection` and an alternative `BoshConnection`, which is tried as fallback.
* Move Message, Presence and IQ classes from `org.xmpp.stanza` to `org.xmpp.stanza.client package (API change).
* The 'from' attribute of roster/privacy lists/blocking command pushes are now checked to prevent IQ spoofing.
* Update [XEP-0080: User Location](https://xmpp.org/extensions/xep-0080.html) implementation from version 1.7 to 1.8.
* Provide convenience methods for creating [XEP-0126: Invisibility](https://xmpp.org/extensions/xep-0126.html) privacy lists.
* Implement `Comparable` interface for `PrivacyList` and `PrivacyRule`.
* Various minor refactoring and improvements.


## Version 0.1.0 (2014-03-22)

* Initial version