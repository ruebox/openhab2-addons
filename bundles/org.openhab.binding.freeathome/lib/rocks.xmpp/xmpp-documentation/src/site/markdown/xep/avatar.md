# Avatars
---

User Avatars are specified in both the historical [XEP-0153: vCard-Based Avatars][vCard-Based Avatars] and the newer [XEP-0084: User Avatar][User Avatar],
which makes life more complicated, if you just want to set your avatar or listen for avatar updates of your contacts.

The `AvatarManager` encapsulates the logic of both specifications and shields you from the complexity of dealing with two extensions.

By default avatars are disabled, which means you have to enable it, if you want to use them, i.e. receive notifications about updates and also send updates yourself.

## Publishing your Avatar

Here's an example, which utilizes the JavaFX file chooser to choose an image, then resize it, and then publish it.

Publishing means, it is published to your vCard (XEP-0153) as well as to the Personal Eventing Service (XEP-0084).

```java
try {
    // Get the avatar manager
    AvatarManager avatarManager = xmppClient.getManager(AvatarManager.class);
    avatarManager.setEnabled(true);

    // Choose a file with JavaFX file dialog.
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", ".png", ".jpg", ".gif"));
    File file = fileChooser.showOpenDialog(null);

    // If the user has chosen a file
    if (file != null) {
        // Read the file as image.
        BufferedImage bufferedImage = ImageIO.read(file);
        Image thumbnail = bufferedImage.getScaledInstance(64, -1, Image.SCALE_SMOOTH);
        BufferedImage bufferedThumbnail = new BufferedImage(thumbnail.getWidth(null),
            thumbnail.getHeight(null),
            BufferedImage.TYPE_INT_RGB);
        bufferedThumbnail.getGraphics().drawImage(thumbnail, 0, 0, null);
        // Publish the image as your avatar.
        avatarManager.publishAvatarImage(bufferedThumbnail);
    }
} catch (IOException | XmppException e) {
    // Deal with it. Chosen file was probably no image file.
}
```

## Listening for Avatar Updates

You can listen for your contacts' avatar updates by adding a listener to the manager:

```java
AvatarManager avatarManager = xmppClient.getManager(AvatarManager.class);
avatarManager.setEnabled(true);
avatarManager.addAvatarChangeListener(e -> {
    BufferedImage bufferedImage = e.getAvatarImage();
    // ...
});
```

## Avatar Cache

There's also a file based cache for downloaded avatars, so that they don't need to be downloaded again.
Avatars are cached in the folder "avatars" in your cache directory (configurable in `XmppSessionConfiguration`)

[User Avatar]: https://xmpp.org/extensions/xep-0084.html "XEP-0084: User Avatar"
[vCard-Based Avatars]: https://xmpp.org/extensions/xep-0153.html "XEP-0153: vCard-Based Avatars"
