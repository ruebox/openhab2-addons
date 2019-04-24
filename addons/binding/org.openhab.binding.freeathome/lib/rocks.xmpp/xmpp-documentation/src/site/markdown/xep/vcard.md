# XEP-0054: vcard-temp
---

[XEP-0054: vcard-temp][vcard-temp] allows you to retrieve another user's profile and store your own.

Here are a few examples how to do it.

## Retrieving Another User's Profile

```java
VCardManager vCardManager = xmppClient.getManager(VCardManager.class);
VCard vCard = vCardManager.getVCard(Jid.of("juliet@example.net")).getResult();
if (vCard != null) {
    if (vCard.getName() != null) {
        String familyName = vCard.getName().getFamilyName();
        String givenName = vCard.getName().getGivenName();
        // ...
    }
    if (vCard.getPhoto() != null) {
        byte[] imageData = vCard.getPhoto().getValue();
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));
        // ...
    }
    for (VCard.Address address : vCard.getAddresses()) {
        String street = address.getStreet();
        String city = address.getCity();
        // etc...
    }
}
```

## Storing Your Own Profile

```java
VCardManager vCardManager = xmppClient.getManager(VCardManager.class);
VCard vCard = new VCard();
vCard.setName(new VCard.Name("Family Name", "Given Name", "Middle Name"));
vCard.setNickname("Nickname");
vCardManager.setVCard(vCard);
```

[vcard-temp]: https://xmpp.org/extensions/xep-0054.html "XEP-0054: vcard-temp"
