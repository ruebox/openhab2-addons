# XEP-0030: Service Discovery
---

[XEP-0030: Service Discovery][Service Discovery] allows you to discover features and items of another XMPP entity.

By enabling an extension (XEP), you automatically support it and it will be listed in the feature list of your service discovery response.

E.g., if you have enabled support for [XEP-0092: Software Version][Software Version] your client automatically includes this XEP in its feature list, when it responds to a query.

Generally speaking all enabled extensions will be added to the feature list of service discovery. If you disable a certain extension, it will be removed.


## Discovering Information

You discover information about another XMPP entity by sending a request to it:

```java
ServiceDiscoveryManager serviceDiscoveryManager = xmppClient.getManager(ServiceDiscoveryManager.class);
InfoNode infoNode = serviceDiscoveryManager.discoverInformation(Jid.of("example.net")).getResult();
```

`infoNode` will contain information about the entity, i.e. its identity and features it supports.

### Determining Feature Support

For most use cases you are probably only interested if another entity supports a specific feature.

Because that use case is specified by nearly every XEP ("determining support"), there's a convenience method for it directly on `XmppClient`, which internally also uses [XEP-0115: Entity Capabilities][Entity Capabilities] (for optimization and caching):

```
boolean supportsChatStates = xmppClient.isSupported(ChatState.NAMESPACE, Jid.of("romeo@example.net/park")).getResult();
```

Note that the passed JID should be a full JID in most cases (if you want to check client capabilities).

This method will first check the cache for entity capabilities. Only if no cache entry is found it will ask the entity via Service Discovery.

## Discovering Items

You can also discover items associated with an XMPP entity.

```
ItemNode node = serviceDiscoveryManager.discoverItems(Jid.of("example.net")).getResult();
List<Item> items = node.getItems();
```

This will discover items at the "root" node, but you can also discover items at another node:

```
ItemNode node = serviceDiscoveryManager.discoverItems(Jid.of("example.net"), "music").getResult();
List<Item> items = node.getItems();
```

### Limited Result Sets

You can request a limited result set like this, which will request the first 20 items:

```
ItemNode itemNode = serviceDiscoveryManager.discoverItems(Jid.of("example.net"), ResultSetManagement.forLimit(20)).getResult();
List<Item> items = itemNode.getItems();
```

For more limited result set options refer to `ResultSetManagement`.

## Discovering Services

The following would discover Multi-User Chat services at your server:

```
serviceDiscoveryManager.discoverServices(Muc.NAMESPACE).getResult();
```

## Providing Items

If you want to publish items yourself (which then can be discovered by other entities), you should set an item provider like this:

```
Collection<Item> myItems = ...;
serviceDiscoveryManager.setItemProvider(new DefaultItemProvider(myItems));
```

The `DefaultItemProvider` keeps the items in memory. If you want to set another source as items (e.g. from a database), create your own implementation of 
`ResultSetProvider<Item>` instead.

### Result Set Management

`ResultSetProvider<T>` provides methods which are necessary for enabling support for [XEP-0059: Result Set Management][Result Set Management]:

```
public interface ResultSetProvider<T extends ResultSetItem> {
    List<T> getItems();
    int getItemCount();
    List<T> getItems(int index, int maxSize);
    List<T> getItemsAfter(String itemId, int maxSize);
    List<T> getItemsBefore(String itemId, int maxSize);
    int indexOf(String itemId);
}
```

E.g., speaking in XMPP terms, when your client receives a `disco#items` request with a RSM extension `<set xmlns='http://jabber.org/protocol/rsm'><max>20</max></set>`,
the `getItems(0, 20)` will be called, which should return the first 20 items.


## Identities

An entity in XMPP can have one or more identities, which consists of a category (server, client, gateway, directory, etc.) and a type within that category (IM server, phone vs. handheld client, MSN gateway vs. AIM gateway, user directory vs. chatroom directory, etc.).

This information helps requesting entities to determine the group or "bucket" of services into which the entity is most appropriately placed (e.g., perhaps the entity is shown in a GUI with an appropriate icon).

You can add such identities with:

```
// Adds an identity of category "client" and type "pc":
serviceDiscoveryManager.addIdentity(Identity.clientPc());
```

*client/pc* is also the default identity, if none was added (every entity requires at least one identity), so in most cases you don't need to add one.


[Service Discovery]: https://xmpp.org/extensions/xep-0030.html "XEP-0030: Service Discovery"
[Entity Capabilities]: https://xmpp.org/extensions/xep-0115.html "XEP-0115: Entity Capabilities"
[Software Version]: https://xmpp.org/extensions/xep-0092.html "XEP-0092: Software Version"
[Result Set Management]: https://xmpp.org/extensions/xep-0059.html "XEP-0059: Result Set Management"