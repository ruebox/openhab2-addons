# XEP-0009: Jabber-RPC
---

[XEP-0009: Jabber-RPC][Jabber-RPC] allows you to transport XML-RPC encoded requests and responses between two XMPP entities.

## Responding to XML-RPC Requests

If you want to respond to requests, you have to set an `RpcHandler`, which handles inbound requests. Here's an example:

```java
RpcManager rpcManager = xmppClient.getManager(RpcManager.class);
rpcManager.setRpcHandler((requester, methodName, parameters) -> {
    if (methodName.equals("examples.getStateName")) {
        if (!parameters.isEmpty()) {
            if (parameters.get(0).getAsInteger() == 6) {
                return Value.of("Colorado");
            }
        }
    }
    throw new RpcException(123, "Invalid method name or parameter.");
});
```

Basically you examine the method name and the parameters and return an appropriate result.

Note that setting a handler like this automatically enables support for the `jabber:iq:rpc` protocol for service discovery.

Also note, that each method call is processed in a separate thread, so that you can process multiple requests simultaneously.

### Error Handling

If you throw an `RpcException` like in the example above, it means you want to return an application-level XML-RPC fault, e.g.:

```
<methodResponse>
  <fault>
    <value>
      <struct>
        <member>
          <name>faultCode</name>
          <value><int>123</int></value>
        </member>
        <member>
          <name>faultString</name>
          <value><string>Invalid method name or parameter.</string></value>
        </member>
      </struct>
    </value>
  </fault>
</methodResponse>
```

If any other exception occurs during processing, an XMPP `<internal-server-error/>` is returned to the requester.

## Calling a Remote Procedure

We now consider the requester's side, i.e. if you want to call a remote procedure.

Assume you want to call the above remote procedure (`examples.getStateName`), you can do it like this:

```java
RpcManager rpcManager = xmppClient.getManager(RpcManager.class);
try {
    Value response = rpcManager.call(Jid.of("responder@company-a.com/jrpc-server"), "examples.getStateName", Value.of(6)).getResult();
    System.out.println(response.getAsString()); // Colorado
} catch (XmppException e) {
    e.printStackTrace();
    // E.g. a StanzaException, if the responder does not support the protocol or an internal-server-error has occurred.
} catch (RpcException e) {
    e.printStackTrace();
    // If the responder responded with an application level XML-RPC fault.
}
```

XML-RPC fault errors are translated into `RpcException`.

[Jabber-RPC]: https://xmpp.org/extensions/xep-0079.html "XEP-0009: Jabber-RPC"
