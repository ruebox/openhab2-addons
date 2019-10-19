# Custom Authentication
---

XMPP uses SASL (Simple Authentication and Security Layer) for authentication and this library -obviously- uses the [Java SASL API](https://docs.oracle.com/javase/8/docs/technotes/guides/security/sasl/sasl-refguide.html) for authentication.
Although there are already some default authentication mechanisms, which every server should support, like SCRAM-SHA-1, it can happen that you want to support your own.

Here's a brief how-to.

## Implement Your Own `SaslClient`

First you should implement your own `javax.security.sasl.SaslClient`. Most logic will go into the method `evaluateChallenge(byte[] challenge)`, however the exact implementation depends on the mechanism. Maybe you also need custom `CallbackHandler` implementations.

```
import javax.security.sasl.SaslClient;

public final class CustomSaslClient implements SaslClient {

    public static final String NAME = "X-CUSTOM-AUTH";

    @Override
    public final String getMechanismName() {
        return NAME;
    }

    // ...

}
```

## Implement Your Own `SaslClientFactory`

Next you need to create a `javax.security.sasl.SaslClientFactory`, which creates your `CustomSaslClient`. This factory will be registered with a security provider in the next step.

```
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import javax.security.sasl.SaslException;
import java.util.Map;

public final class CustomSaslClientFactory implements SaslClientFactory {
    @Override
    public SaslClient createSaslClient(String[] mechanisms, String authorizationId, String protocol, String serverName, Map<String, ?> props, CallbackHandler cbh) throws SaslException {
        for (String mechanism : mechanisms) {
            if (CustomSaslClient.NAME.equals(mechanism)) {
                return new CustomSaslClient(cbh);
            }
        }
        return null;
    }

    @Override
    public String[] getMechanismNames(Map<String, ?> props) {
        return new String[]{CustomSaslClient.NAME};
    }
}
```

## Register a Security Provider

You need to add a security provider for your factory. This step is necessary, so that `javax.security.sasl.Sasl.createSaslClient()` (used by the login method) finds your `SaslClient`.

Since you only need to register it once, you could do this in a static constructor of any class which gets loaded. The following sample uses an anonymous `Provider` implementation. Of course you could also extend you own class.

```
import java.security.Provider;
import java.security.Security;

static {
    Security.addProvider(new Provider("Custom Sasl Provider", 1.0, "Provides custom SASL mechanisms.") {
        {
                put("SaslClientFactory." + CustomSaslClient.NAME, CustomSaslClientFactory.class.getName());
        }
    });
}
```

## Register Your SASL Mechanism

In order for the XMPP client to use your custom authentication mechanism during login, you need to register its name in the configuration.

Note that, the following configuration disables all other default mechanism, like PLAIN or SCRAM-SHA-1. If you want to support them as well, you need to specify them, too.

```java
XmppSessionConfiguration configuration = XmppSessionConfiguration.builder()
    .authenticationMechanisms(CustomSaslClient.NAME)
    .build();
```

## Login

Login is as usual, but depending on your `SaslClient` implementation, you may want to call a different login method, e.g. with an authzid and a `CallbackHandler`, which retrieves information used by the `SaslClient`.

The server will advertise its supported authentication mechanism and your client will choose one of the above configuration.

```
xmppClient.login("authzid", callbackHandler, resource);
```
