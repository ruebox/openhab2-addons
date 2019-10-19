# XEP-0080: User Location
---

[XEP-0080: User Location][XEP-0080: User Location] allows to communicate the current location of an entity.

How it usually works:
One of your contacts publishes its location to its [Personal Eventing Service][XEP-0163: Personal Eventing Protocol] and if you are interested in receiving geo location notifications the service notifies you about the update.

## Publishing your Location

Publishing your location to the [Personal Eventing Service][XEP-0163: Personal Eventing Protocol] is as simple as that:

```java
GeoLocationManager geoLocationManager = xmppClient.getManager(GeoLocationManager.class);
geoLocationManager.publish(GeoLocation.builder()
    .latitude(50.35)
    .longitude(7.59)
    .build());
```

## Listening for User Location Updates

Enabling the manager indicates, that you are interested in receiving geo location notifications. (This should probably happen implicitly when adding the listener in a future version).

Here's a sample, how to listen for location updates:

```
GeoLocationManager geoLocationManager = xmppClient.getManager(GeoLocationManager.class);
geoLocationManager.setEnabled(true);
geoLocationManager.addGeoLocationListener(e -> {
    System.out.println(e.getPublisher() + " updated his location: " + e.getGeoLocation());
});
```


[XEP-0080: User Location]: https://xmpp.org/extensions/xep-0080.html "XEP-0080: User Location"
[XEP-0163: Personal Eventing Protocol]: https://xmpp.org/extensions/xep-0163.html "XEP-0163: Personal Eventing Protocol"