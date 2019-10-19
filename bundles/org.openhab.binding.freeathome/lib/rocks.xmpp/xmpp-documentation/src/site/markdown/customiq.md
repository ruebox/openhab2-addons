# Custom IQs
---

The IQ semantic is a simple request-response mechanism.
One entity requests another entity (with a specific payload) and the responder returns a result.
Writing custom IQs (and the handling of those) is easy.

Here's a simple example, which allows one entity to request the sum of two integers. The responding entity calculates the result and returns it to the requester.

## Writing the Custom Payload Class

First write your payload class and annotate it with JAXB annotations. Make sure to mark it with `@XmlRootElement`:

```java
@XmlRootElement(name = "addition")
public final class Addition {

    @XmlElement(name = "summand1")
    private Integer summand1;

    @XmlElement(name = "summand2")
    private Integer summand2;

    @XmlElement(name = "sum")
    private Integer sum;

    /**
     * No-arg default constructor needed for JAXB.
     */
    private Addition() {
    }

    public Addition(Integer summand1, Integer summand2) {
        this.summand1 = Objects.requireNonNull(summand1);
        this.summand2 = Objects.requireNonNull(summand2);
    }

    public Addition(Integer sum) {
        this.sum = Objects.requireNonNull(sum);
    }

    public Integer getSummand1() {
        return summand1;
    }

    public Integer getSummand2() {
        return summand2;
    }

    @Override
    public String toString() {
        if (summand1 != null && summand2 != null) {
            return summand1 + " + " + summand2 + " = ???";
        }
        return "Sum: " + sum;
    }
}
```

Then create a _package-info.java_ in the same package as your JAXB annotated class:

```
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSchema(namespace = "http://xmpp.rocks", elementFormDefault = XmlNsForm.QUALIFIED)
package rocks.xmpp.sample.customiq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
```

This is important, because you need to assigns your own XML namespace for your class and all its elements.

## Registering Your Class

Then create a configuration for the session, which registers your class with the `JAXBContext`:

**This needs to be done on the requester as well as on the responder side!**

```java
XmppSessionConfiguration configuration = XmppSessionConfiguration.builder()
    .extensions(Extension.of(Addition.class))
    .build();
```

Then create the session with that configuration:

```java
XmppClient xmppClient = XmppClient.create("domain", configuration);
```


## The Requester Side

After having established an XMPP session the requester can then query the responder like that:

```java
Addition addition = new Addition(52, 22);

// Request the sum of two values (52 + 22). The requester will calculate it for you and return a result.
IQ resultIQ = xmppClient.query(IQ.get(Jid.of("responder@domain/resource"), addition)).getResult();

System.out.println(resultIQ.getExtension(Addition.class));
```

## The Responder Side

In addition to registering your custom class to the session (see above), the responder needs to register an `IQHandler` implementation,
which will handle inbound requests (of type `Addition`) and return either an error or the result:

```java
// Reqister an IQ Handler, which will return the sum of two values.
xmppClient.addIQHandler(Addition.class, new IQHandler() {
    @Override
    public IQ handleRequest(IQ iq) {
        Addition addition = iq.getExtension(Addition.class);
        if (addition.getSummand1() == null) {
            // This is how you would return an error.
            return iq.createError(new StanzaError(Condition.BAD_REQUEST, "No summand provided."));
        }
        return iq.createResult(new Addition(addition.getSummand1() + addition.getSummand2()));
    }
});
```

Preferably this should be done before connecting.

Each `IQHandler` is invoked in its own thread, so the result calculation could take some time without blocking any other communication.

The result IQ will look like this on the XMPP stream and will eventually arrive at the requester:

```xml
<iq from="responder@domain/resource" id="123" to="requester@domain/resource" type="result">
    <addition xmlns="http://xmpp.rocks">
        <sum>74</sum>
    </addition>
</iq>
```
