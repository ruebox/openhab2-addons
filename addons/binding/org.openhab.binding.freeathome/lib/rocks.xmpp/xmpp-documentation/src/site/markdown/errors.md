# Exceptions and XMPP Errors
---

There is one abstract `XmppException` for most kind of exceptions, which are related to XMPP.

Mainly, there are three subclasses of it:

* `StreamErrorException`  for [Stream Errors][Stream Errors].
* `StanzaException` for [Stanza Errors][Stanza Errors].
* `NoResponseException` for IQ queries, which didn't receive a response.


## Dealing with Stream Errors

XMPP Stream Errors are unrecoverable, which means the session will get disconnected after they have occurred.

Here's an example how to react to stream errors.

```java
xmppClient.addSessionStatusListener(e -> {
    if (e.getThrowable() instanceof StreamErrorException) {
        StreamErrorException streamException = (StreamErrorException) e.getThrowable();
        if (streamException.getCondition() == Condition.SYSTEM_SHUTDOWN) {
            // Server was shut down.
        }
    }
});
```

## Dealing with Stanza Errors and Lack of Responses

Most stanza errors are returned in response to an IQ-get or IQ-set query.

For querying another XMPP entity, there's a `query` method on the `XmppClient` class, which queries another entity for information and which is used by most methods.

Now two things can happen:

* The entity does not respond at all.
* The entity returned a stanza error.

In the first case a `NoResponseException` is thrown, in the latter case a `StanzaException` is thrown.

If you want to determine, which kind of exception it is, simply ask for their type.

Here's an example:

```java
try {
    EntityTime entityTime = entityTimeManager.getEntityTime(Jid.of("juliet@example.net/balcony")).getResult();
} catch (NoResponseException e) {
    // The entity did not respond
} catch (StanzaException e) {
    if (e.getCondition() == Condition.SERVICE_UNAVAILABLE) {
        // The entity returned a <service-unavailable/> stanza error.
    }
} catch (XmppException e) {
    // everything else
}
```

As you can see asking for the type and then for the specific condition gets you where you want.

Btw: If you wonder, why the error conditions aren't represented as enum, it is because some error conditions like `<gone/>` have additional data.


[Stream Errors]: https://xmpp.org/rfcs/rfc6120.html#streams-error "Stream Errors"
[Stanza Errors]: https://xmpp.org/rfcs/rfc6120.html#stanzas-error "Stanza Errors"
