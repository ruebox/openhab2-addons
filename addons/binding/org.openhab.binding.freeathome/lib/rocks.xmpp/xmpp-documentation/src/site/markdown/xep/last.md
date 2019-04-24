# Last Activity and Idle Time
---

[XEP-0012: Last Activity][Last Activity] allows you to determine the last activity of an XMPP entity.

It's also tightly coupled with [XEP-0256: Last Activity in Presence][Last Activity in Presence] and [XEP-0319: Last User Interaction in Presence][Last User Interaction in Presence]. 

## Getting the Last Logout Date

If you want to find out how long ago a user logged out, you query for the user's bare JID. The server should then answer on behalf of the user with the last logout date.

```java
LastActivityManager lastActivityManager = xmppClient.getManager(LastActivityManager.class);
LastActivity lastActivity = lastActivityManager.getLastActivity(Jid.of("juliet@im.example.com")).getResult();
// user logged out lastActivity.getSeconds() ago
```

## Getting the Idle Time of Available Entities

In order to query a connected user for its idle time you must query for its full JID, so that the XMPP server routes the query directly to the resource.

```java
LastActivityManager lastActivityManager = xmppClient.getManager(LastActivityManager.class);
LastActivity lastActivity = lastActivityManager.getLastActivity(Jid.of("juliet@im.example.com/resource"));
Instant idleTime = Instant.now().minusSeconds(lastActivity.getSeconds());
```

If supported by the client, it will respond with its idle time.

Alternatively, if a contact/client supports [XEP-0256: Last Activity in Presence][Last Activity in Presence] or [XEP-0319: Last User Interaction in Presence][Last User Interaction in Presence], it will include idle time in its presence.

Just listen for presence and extract the idle time like shown below, which will try to get the idle time by first trying XEP-0319, and then XEP-0256 (also respecting delayed delivery).

```java
xmppClient.addInboundPresenceListener(e -> {
    Instant idleSince = Idle.timeFromPresence(e.getPresence());
});
```
                
## Setting the Idle Time of Your Client

The idle time can be set via a strategy pattern, which let's you define different strategies for determining idle time. E.g. you could track the last mouse movement or keyboard input. By default, the last sent message is used to determine your idle time.

```java
lastActivityManager.setIdleStrategy(() -> {
    // Return whatever your idle time is.
    return Instant.now();
});
```

This time will be returned if another entity asks you for your idle time and will also be included in outbound 'away' and 'extended away' presences,
in order to inform your contacts automatically, so that they don't need to ask you.
            
If you don't want to reveal your idle time, you have to disable the manager, otherwise keep it enabled.

```java
lastActivityManager.setEnabled(false);
```

## Getting the Server Uptime

When querying a server or component the result is the uptime of it.

```java
LastActivityManager lastActivityManager = xmppClient.getManager(LastActivityManager.class);
LastActivity lastActivity = lastActivityManager.getLastActivity(Jid.of("im.example.com")).getResult();
Instant uptime = Instant.now().minusSeconds(lastActivity.getSeconds());
```


                
                
[Last Activity]: https://xmpp.org/extensions/xep-0012.html "XEP-0012: Last Activity"
[Last Activity in Presence]: https://xmpp.org/extensions/xep-0256.html "XEP-0256: Last Activity in Presence"
[Last User Interaction in Presence]: https://xmpp.org/extensions/xep-0319.html "XEP-0319: Last User Interaction in Presence"