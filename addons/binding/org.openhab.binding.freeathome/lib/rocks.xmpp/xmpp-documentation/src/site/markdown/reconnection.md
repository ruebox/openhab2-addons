# Dis- and Reconnection
---

It may happen that the connection drops, e.g. because the server has been shutdown or the client's connection is dead
(e.g. because the laptop is in sleep mode).

Babbler tries to reconnect automatically by default after a random number of seconds. It basically follows the recommendation described in
[RFC 6120 § 3.3 Reconnection](https://xmpp.org/rfcs/rfc6120.html#tcp-reconnect), but makes smart differentiations between system shutdown and client disconnects by default
(on system shutdown, the reconnection window is a bit larger).

## Custom Reconnection Strategies

Just implement your own `ReconnectionStrategy` or use one of the predefined, e.g. one which always tries to reconnect after a fix amount of time, e.g. after 10 seconds:

```java
XmppSessionConfiguration configuration = XmppSessionConfiguration.builder()
    .reconnectionStrategy(ReconnectionStrategy.alwaysAfter(Duration.ofSeconds(10)))
    .build();
```

There are a couple of other strategies, like:

```java
// Always reconnects after a random time between 10 and 20 seconds
ReconnectionStrategy.alwaysRandomlyAfter(Duration.ofSeconds(10), Duration.ofSeconds(20))

// Starts with a low reconnection time (< 60 seconds) and backs off on each successive attempt.
ReconnectionStrategy.truncatedBinaryExponentialBackoffStrategy(60, 4)

// Disables automatic reconnection:
ReconnectionStrategy.none();
```

## Listening to Dis- and Reconnection Events

Add the following listener to listen for dis- and reconnection events:

```java
xmppClient.addConnectionListener(e -> {
    switch (e.getType()) {
       case DISCONNECTED:
            // disconnected due to e.getCause()
            break;
       case RECONNECTION_SUCCEEDED:
            // successfully reconnected
            break;
       case RECONNECTION_FAILED:
            // reconnection failed due to e.getCause()
            break;
       case RECONNECTION_PENDING:
            // reconnection pending, next reconnection attempt in e.getNextReconnectionAttempt()
            // emitted every second.
            break;
    }
});
```

## Manual Reconnection

If you don't want to wait for the automatic reconnection, you can manually reconnect by calling:

```java
xmppClient.connect();
```