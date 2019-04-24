# XEP-0163: Personal Eventing Protocol
---

[XEP-0163: Personal Eventing Protocol][PEP] (PEP) defines semantics for using the XMPP publish-subscribe protocol to broadcast state change events associated with an instant messaging and presence account.

For every instant messaging account a PubSub service is automatically created, which is then referred to as Personal Eventing Service. By default your contacts are automatically added as subscribers to every node you publish.

Therefore it can be used, if you want to publish your [location][GeoLocation], [tune][Tune], [mood][Mood], [activity][Activity] or your [avatar][Avatar], so that your contacts will receive notifications for these kind of information, e.g. you publish your geo location and your contacts receive it.

## Creating the Personal Eventing Service

First you have to create the personal eventing service. Since it's just a subset of PubSub, you have to use the `PubSubManager` and the personal eventing service is just a `PubSubService`.

```java
PubSubManager pubSubManager = xmppClient.getManager(PubSubManager.class);
PubSubService personalEventingService = pubSubManager.createPersonalEventingService();
```

## Publishing User Data

If you want to publish data to a node in your personal service, you create a local `PubSubNode` instance so that you can work with it, then publish data to that node.

Here's an example to publish your geo location. As per [XEP-0080: User Location][GeoLocation] you want to publish it to the node "`http://jabber.org/protocol/geoloc`" which is `GeoLocation.NAMESPACE`.

```java
PubSubNode pubSubNode = personalEventingService.node(GeoLocation.NAMESPACE);
pubSubNode.publish(GeoLocation.builder()
    .latitude(45.44)
    .longitude(12.33)
    .build());
```

By default (i.e. if not otherwise configured) all your contacts now receive an event notification about your new geo location.

## Listening for PEP Events

Now that you have published your geo location all your contacts will receive notifications about it. This is just a message with a "PubSub event" extension.

```java
xmppClient.addInboundMessageListener(e -> {
    Message message = e.getMessage();
    Event event = message.getExtension(Event.class);
    if (event != null) {
        if (GeoLocation.NAMESPACE.equals(event.getNode())) {
            for (Item item : event.getItems()) {
                if (item.getPayload() instanceof GeoLocation) {
                    GeoLocation geoLocation = (GeoLocation) item.getPayload();
                    Double latitude = geoLocation.getLatitude();   // 45.44
                    Double longitude = geoLocation.getLongitude(); // 12.33
                    // ...
                }
            }
        }
    }
});
```

[GeoLocation]: https://xmpp.org/extensions/xep-0080.html "XEP-0080: User Location"
[Mood]: https://xmpp.org/extensions/xep-0107.html "XEP-0107: User Mood"
[Activity]: https://xmpp.org/extensions/xep-0108.html "XEP-0108: User Activity"
[Avatar]: https://xmpp.org/extensions/xep-0084.html "XEP-0084: User Avatar"
[Tune]: https://xmpp.org/extensions/xep-0118.html "XEP-0118: User Tune"
[PubSub]: https://xmpp.org/extensions/xep-0060.html "XEP-0060: Publish-Subscribe"
[PEP]: https://xmpp.org/extensions/xep-0163.html "XEP-0163: Personal Eventing Protocol"