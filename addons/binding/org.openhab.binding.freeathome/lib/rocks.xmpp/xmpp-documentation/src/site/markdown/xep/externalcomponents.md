# External Components
---

[External Components][External Components] are like server plugins, which are not hosted within the server environment, but instead connect externally to the server via the "Jabber Component Protocol".
Usually, components "live" in their own subdomain, e.g. `mycomponent.xmpp.rocks`. Like internal plugins they allow you to enhance a server's functionality, e.g. provide additional services.
 
Such external components connect to the server and authenticate via a shared secret. Component and server then communicate with each other using XMPP.

Clients could send messages (or other stanzas) to the component (e.g. to `mycomponent.xmpp.rocks`) and the component could do whatever it wants, like doing statistics, sending or forwarding messages, returning IQ results, or providing translation services.

Here's a sample of a component:

```java
ExternalComponent myComponent = ExternalComponent.create("translation", "sharedSecret", "localhost", 5275);
```

It connects to a server hosted at `localhost` which allows external components to connect on port 5275.

The component name is `translation`, i.e. it will usually also be addressable via the correspondent subdomain, i.e. `translation.xmpp.rocks`. 

The shared secret (password), is configured in the server and is `sharedSecret` here.

## Give Your Component Some Logic

Before connecting you should setup any listeners or other configuration, e.g.:

```java
ServiceDiscoveryManager serviceDiscoveryManager = myComponent.getManager(ServiceDiscoveryManager.class);

// Add an identity for the component. This will be used by clients who want to discover the translation service.
serviceDiscoveryManager.addIdentity(Identity.automationTranslation().withName("Translation Provider Service"));

// Our component supports the XEP-0171 protocol, let's advertise it by including the protocol name in the feature list,
// so that clients can discover our component as language translation service and can send queries to it.
myComponent.enableFeature(LanguageTranslation.NAMESPACE);

// Don't advertise the MUC feature. We are no chat service.
myComponent.disableFeature(Muc.NAMESPACE);

// Don't advertise the SOCKS bytestreams feature. We are no stream proxy.
myComponent.disableFeature(Socks5ByteStream.NAMESPACE);

// Listen for language support queries.
myComponent.addIQHandler(LanguageSupport.class, new AbstractIQHandler(IQ.Type.GET) {
    @Override
    protected IQ processRequest(IQ iq) {
        // The client/requester likes to discover language support of our component.
        // Return a list of supported languages.
        return iq.createResult(new LanguageSupport(Collections.singleton(new LanguageSupport.Item("en", myComponent.getDomain(), "de", "testEngine", true, null))));
    }
});

// Listen for translation queries
myComponent.addIQHandler(LanguageTranslation.class, new AbstractIQHandler(IQ.Type.GET) {
    @Override
    protected IQ processRequest(IQ iq) {
        // The client/requester likes to translate something.
        Collection<LanguageTranslation.Translation> translations = new ArrayDeque<>();
        LanguageTranslation translation = iq.getExtension(LanguageTranslation.class);
        
        // Do a real translation here and return proper results in accordance with XEP-0171.
        translations.addAll(translation.getTranslations().stream()
                .map(t -> LanguageTranslation.Translation.forDestinationLanguage(t.getDestinationLanguage())
                        .withSourceLanguage(translation.getSourceLanguage())
                        .withTranslatedText("HALLO"))
                .collect(Collectors.toList()));
        LanguageTranslation languageTranslation = new LanguageTranslation(translations);
        return iq.createResult(languageTranslation);
    }
});
```

## Connecting to the Server

```java
myComponent.connect();
```

Now, when a client discovers services of your server, the server will advertise your component as "Translation Provider Service" and clients could ask your component to translate a text.

`ExternalComponent` is in many things similar to `XmppClient` because they derive from the same base class `XmppSession`.

That also means, that there's a lot of shared functionality and `ExternalComponent` supports many protocols out of the box, like Service Discovery, Message Delivery Receipts, Entity Time, ...

[External Components]: https://xmpp.org/extensions/xep-0114.html "XEP-0114: Jabber Component Protocol"
