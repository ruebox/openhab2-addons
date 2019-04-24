This is a list of various issues which have occurred while reading through and implementing XEPs. Some of them were also mentioned in the XMPP standards mailing list.

## XEP-0045: Multi-User Chat

1. 7.2.18 Error Conditions does not list `<jid-malformed/>` (when no nickname provided)
3. Full JID in item element (e.g. Example 144, 147)
8. Room Subject can/should have delayed delivery?
9. Actor in #admin not needed?
10. 15.6.2 Initial Submission: Status code 174 is missing.

## XEP-0048: Bookmarks

1. *A bookmark set may contain any number of conference rooms.* contradicts with XML schema (?)

## XEP-0060: Publish-Subscribe

3. 8.2 Configure Node: `node` missing in XML Schema
4. 6.5 Retrieve Items from a Node vs 5.5 Discover Items for a Node (confusing)
5. `<subscription-required xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>` vs.
   `<presence-subscription-required xmlns='http://jabber.org/protocol/pubsub#errors'/>` ?

## XEP-0084: User Avatar

1. Should probably make use of persistent storage ([XEP-0222](http://www.xmpp.org/extensions/xep-0222.html))?
2. Example 4 is confusing because it uses only image/gif, although image/png is mandatory (?).

## XEP-0096

1. `<xs:attribute name='date' type='xs:string' use='optional'/>` should be `xsd:dateTime`

## XEP-0184: Message Delivery Receipts

1. Should clarify message type of receipts.

## XEP-0234: Jingle File Transfer

1. XML Schema is missing <received/> element.
2. "The <checksum/> element SHOULD contain 'creator'", but XML Schema defines it as required
3. Example 17 and 18 is missing </content> end tag (or empty closing tag)

