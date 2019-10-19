# XEP-0072: SOAP Over XMPP
---

There's some very basic support for [XEP-0072: SOAP Over XMPP][SOAP Over XMPP]. For more details you should read the specification. Here are some examples.

## Discovering Support

```java
boolean isSupported = xmppClient.isSupported("http://jabber.org/protocol/soap", Jid.of("responder@example.com/soap-server"));
```

## Sending a SOAP Message Over XMPP

The following code creates a SOAP envelope and adds it as extension to a message (IQ-set could be used as well).

```java
MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
SOAPMessage soapMessage = messageFactory.createMessage();
SOAPPart soapPart = soapMessage.getSOAPPart();

// SOAP Envelope
SOAPEnvelope envelope = soapPart.getEnvelope();
envelope.addNamespaceDeclaration("p", "http://travelcompany.example.org/reservation/travel");

// SOAP Body
SOAPBody soapBody = envelope.getBody();
SOAPElement soapBodyElem1 = soapBody.addChildElement("departing", "p");
soapBodyElem1.addTextNode("New York");

SOAPElement soapBodyElem2 = soapBody.addChildElement("arriving", "p");
soapBodyElem2.addTextNode("Los Angeles");

Message message = new Message(Jid.of("juliet@example.net"));
message.addExtension(envelope);
```

This will generate the following XML on the XMPP stream.

```xml
<message to="juliet@example.net">
    <env:Envelope xmlns:p="http://travelcompany.example.org/reservation/travel" xmlns:env="http://www.w3.org/2003/05/soap-envelope">
        <env:Header></env:Header>
        <env:Body>
            <p:departing>New York</p:departing>
            <p:arriving>Los Angeles</p:arriving>
        </env:Body>
    </env:Envelope>
</message>
```

## Receiving a SOAP Message

Since `SOAPEnvelope` is an interface and not known to the JAXB Context, it will be converted to `org.w3c.Element`. You should ask the extension for `Element` and check its namespace. If it matches a SOAP namespace, you can convert it to `SOAPEnvelope`.

```java
Element element = message.getExtension(Element.class);

MessageFactory messageFactory = null;

if (SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE.equals(element.getNamespaceURI())) {
        messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
} else if (SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE.equals(element.getNamespaceURI())) {
        messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
}
if (messageFactory != null) {
        SOAPMessage soapMessage = messageFactory.createMessage();
        soapMessage.getSOAPPart().setContent(new DOMSource(element));
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
        // ...
}
```

[SOAP Over XMPP]: https://xmpp.org/extensions/xep-0072.html "XEP-0072: SOAP Over XMPP"
