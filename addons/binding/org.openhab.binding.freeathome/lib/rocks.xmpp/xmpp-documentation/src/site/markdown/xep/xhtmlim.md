# XEP-0071: XHTML-IM
---

## Building the XHTML Extension

You can either build the XHTML extension with the `org.w3c.dom.Element` or pass XHTML directly as String.

First create a `Document`:

```java
DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
DocumentBuilder builder = dbf.newDocumentBuilder();
Document doc = builder.newDocument();
```

Then create the HTML extension with that document, get the body element and assemble your HTML with DOM operations.
Make sure to use valid HTML tags, especially those defined in [XEP-0071: XHTML-IM][XHTML-IM]!

```java
Html html = new Html(doc);
Element body = html.getBody();
Element p = doc.createElement("p");
p.setAttribute("style", "font-weight:bold");
p.setTextContent("Hi!");
body.appendChild(p);
```

Then add the extension:

```java
Message message = new Message(Jid.of("romeo@example.im", "Hi!"));
message.addExtension(html);
```

Alternatively you can pass valid XHTML (which will be appended to the `<body>` element), directly as String to the constructor:

```java
Html html = new Html("<p style=\"font-weight:bold\">Hi!</p>");
```

Both will result in the following message:

```java
<message to="romeo@example.im">
  <body>Hi!</body>
  <html xmlns="http://jabber.org/protocol/xhtml-im">
    <body xmlns="http://www.w3.org/1999/xhtml">
      <p style="font-weight:bold">Hi!</p>
    </body>
  </html>
</message>
```

## Getting the HTML Extension

```java
Html html = message.getExtension(Html.class);
if (html != null) {
    // html.getBody()
    // html.getContent()
}
```

Use either `getBody()` which gets the body element as DOM Element or `getContent()`, which gets the XHTML content (between the `<body>` element) as String.


[XHTML-IM]: https://xmpp.org/extensions/xep-0071.html "XEP-0071: XHTML-IM"
