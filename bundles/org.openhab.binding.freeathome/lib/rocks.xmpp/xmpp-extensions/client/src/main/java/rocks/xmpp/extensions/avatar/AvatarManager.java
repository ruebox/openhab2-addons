/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 Christian Schudt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package rocks.xmpp.extensions.avatar;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.MessageEvent;
import rocks.xmpp.core.stanza.PresenceEvent;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.extensions.address.model.Address;
import rocks.xmpp.extensions.address.model.Addresses;
import rocks.xmpp.extensions.avatar.model.data.AvatarData;
import rocks.xmpp.extensions.avatar.model.metadata.AvatarMetadata;
import rocks.xmpp.extensions.muc.model.user.MucUser;
import rocks.xmpp.extensions.pubsub.PubSubManager;
import rocks.xmpp.extensions.pubsub.PubSubService;
import rocks.xmpp.extensions.pubsub.model.Item;
import rocks.xmpp.extensions.pubsub.model.event.Event;
import rocks.xmpp.extensions.vcard.avatar.model.AvatarUpdate;
import rocks.xmpp.extensions.vcard.temp.VCardManager;
import rocks.xmpp.extensions.vcard.temp.model.VCard;
import rocks.xmpp.im.subscription.PresenceManager;
import rocks.xmpp.util.XmppUtils;
import rocks.xmpp.util.cache.DirectoryCache;
import rocks.xmpp.util.concurrent.AsyncResult;

/**
 * This class manages avatar updates as described in <a href="https://xmpp.org/extensions/xep-0153.html">XEP-0153: vCard-Based Avatars</a> and <a href="https://xmpp.org/extensions/xep-0084.html">XEP-0084: User Avatar</a>.
 * <p>
 * Whenever an avatar update is received by a contact, either via the presence based avatar extension (XEP-0153) or the PEP-based notification (XEP-0084), the registered listeners are triggered.
 * </p>
 * <p>
 * By default this manager is not enabled.
 * </p>
 * This class is thread-safe.
 *
 * @author Christian Schudt
 */
public final class AvatarManager extends Manager {

    private static final Logger logger = Logger.getLogger(VCardManager.class.getName());

    /**
     * Stores the current hash for a user. The Jid is a bare Jid.
     */
    private final Map<Jid, String> userHashes = new ConcurrentHashMap<>();

    private final Map<Jid, AsyncResult<byte[]>> avatarRequests = new ConcurrentHashMap<>();

    private final Set<Consumer<AvatarChangeEvent>> avatarChangeListeners = new CopyOnWriteArraySet<>();

    private final VCardManager vCardManager;

    private final Set<String> nonConformingResources = Collections.synchronizedSet(new HashSet<>());

    private final Map<String, byte[]> avatarCache;

    private final Map<String, byte[]> hashesLocalStore;

    private final Consumer<PresenceEvent> inboundPresenceListener;

    private final Consumer<PresenceEvent> outboundPresenceListener;

    private final Consumer<MessageEvent> inboundMessageListener;

    private AvatarManager(final XmppSession xmppSession) {
        super(xmppSession, true);

        vCardManager = xmppSession.getManager(VCardManager.class);
        avatarCache = xmppSession.getConfiguration().getCacheDirectory() != null ? new DirectoryCache(xmppSession.getConfiguration().getCacheDirectory().resolve("avatars")) : null;
        hashesLocalStore = xmppSession.getConfiguration().getCacheDirectory() != null ? new DirectoryCache(xmppSession.getConfiguration().getCacheDirectory().resolve("userhashes")) : null;

        restoreUserHashes();

        inboundPresenceListener = e -> {
            final Presence presence = e.getPresence();

            // If the presence has an avatar update information.
            final AvatarUpdate avatarUpdate = presence.getExtension(AvatarUpdate.class);

            // 4.3 Multiple Resources
            if (presence.getFrom().asBareJid().equals(xmppSession.getConnectedResource().asBareJid()) && presence.getFrom().getResource() != null && !presence.getFrom().getResource().equals(xmppSession.getConnectedResource().getResource())) {
                // We received a presence stanza from another resource of our own JID.

                if (avatarUpdate == null) {
                    // 1. If the presence stanza received from the other resource does not contain the update child element, then the other resource does not support vCard-based avatars.
                    // That resource could modify the contents of the vCard (including the photo element);
                    // because polling for vCard updates is not allowed, the client MUST stop advertising the avatar image hash.
                    if (presence.isAvailable()) {
                        nonConformingResources.add(presence.getFrom().getResource());
                    }
                    // However, the client MAY reset its hash if all instances of non-conforming resources have gone offline.
                    else if (presence.getType() == Presence.Type.UNAVAILABLE && nonConformingResources.remove(presence.getFrom().getResource()) && nonConformingResources.isEmpty()) {
                        resetHash();
                    }
                } else {
                    // If the presence stanza received from the other resource contains the update child element, then the other resource conforms to the protocol for vCard-based avatars. There are three possible scenarios.
                    // If the update child element contains a non-empty photo element, then the client MUST compare the image hashes.
                    if (avatarUpdate.getHash() != null && !avatarUpdate.getHash().equals(userHashes.get(xmppSession.getConnectedResource().asBareJid()))) {
                        // If the hashes are different, then the client MUST NOT attempt to resolve the conflict by uploading its avatar image again. Instead, it MUST defer to the content of the retrieved vCard by resetting its image hash
                        resetHash();
                    }
                }
            }

            if (avatarUpdate != null && avatarUpdate.getHash() != null) {
                final Jid contact;
                MucUser mucUser = presence.getExtension(MucUser.class);
                if (mucUser != null) {
                    if (mucUser.getItem() != null && mucUser.getItem().getJid() != null) {
                        contact = mucUser.getItem().getJid().asBareJid();
                    } else {
                        // Ignore presence received from anonymous MUC room.
                        return;
                    }
                } else {
                    contact = presence.getFrom().asBareJid();
                }
                // If the user sends the same hash as we already know, it's the same avatar. Therefore do nothing.
                if (!avatarUpdate.getHash().equals(userHashes.put(contact, avatarUpdate.getHash()))) {
                    // When the recipient's client receives the hash of the avatar image, it SHOULD check the hash to determine if it already has a cached copy of that avatar image.
                    byte[] imageData = loadFromCache(avatarUpdate.getHash());
                    byte[] avatar = null;
                    if (imageData != null) {
                        avatar = imageData;
                    }
                    if (avatar != null) {
                        notifyListeners(contact, avatar);
                    } else {
                        // If the avatar was either known before or could be successfully retrieved from the vCard.
                        getAvatarByVCard(contact).whenComplete((avatarResult, ex) -> {
                            if (ex == null) {
                                notifyListeners(contact, avatarResult);
                            } else {
                                logger.log(Level.WARNING, ex, () -> "Failed to retrieve vCard based avatar for user: " + contact);
                            }
                        });
                    }
                }
            }
        };

        this.outboundPresenceListener = e -> {

            final Presence presence = e.getPresence();
            if (presence.isAvailable() && nonConformingResources.isEmpty()) {
                // 1. If a client supports the protocol defined herein, it MUST include the update child element in every presence broadcast it sends and SHOULD also include the update child in directed presence stanzas.

                String myHash = userHashes.get(xmppSession.getConnectedResource().asBareJid());

                if (myHash == null) {
                    // 2. If a client is not yet ready to advertise an image, it MUST send an empty update child element:
                    presence.putExtension(new AvatarUpdate());

                    // Load my own avatar in order to advertise an image.
                    getAvatarByVCard(xmppSession.getConnectedResource().asBareJid()).whenComplete((avatarResult, ex) -> {
                        if (ex == null) {
                            notifyListeners(xmppSession.getConnectedResource().asBareJid(), avatarResult);

			    // If the client subsequently obtains an avatar image (e.g., by updating or retrieving the vCard), it SHOULD then publish a new <presence/> stanza with character data in the <photo/> element.
                            Presence lastPresence = xmppSession.getManager(PresenceManager.class).getLastSentPresence();
                            Presence presence1;
                            if (lastPresence != null) {
                                presence1 = new Presence(null, lastPresence.getType(), lastPresence.getShow(), lastPresence.getStatuses(), lastPresence.getPriority(), null, null, lastPresence.getLanguage(), null, null);
                            } else {
                                presence1 = new Presence();
                            }
                            // Send out a presence, which will be filled with the extension later, because we now know or own avatar and have the hash for it.
                            xmppSession.send(presence1);
                        } else {
                            logger.warning("Failed to retrieve own vCard based avatar.");
                        }
                    });
                } else if (!presence.hasExtension(AvatarUpdate.class)) {
                    presence.putExtension(new AvatarUpdate(myHash));
                }
            }
        };

        this.inboundMessageListener = e -> {
            final Message message = e.getMessage();
            Event event = message.getExtension(Event.class);
            if (event != null) {
                Addresses addresses = message.getExtension(Addresses.class);
                if (addresses != null) {
                    // See https://xmpp.org/extensions/xep-0163.html#notify-addressing
                    for (Address address : addresses.getAddresses()) {
                        if (address.getType() == Address.Type.REPLYTO && xmppSession.getConnectedResource().equals(address.getJid())) {
                            // Don't notify if the message came from our own connected resource.
                            return;
                        }
                    }
                }
                for (final Item item : event.getItems()) {
                    if (item.getPayload() instanceof AvatarMetadata) {
                        AvatarMetadata avatarMetadata = (AvatarMetadata) item.getPayload();

                        // Empty avatar
                        if (avatarMetadata.getInfoList().isEmpty()) {
                            notifyListeners(message.getFrom().asBareJid(), null);
                        } else {

                            // Check if we have a cached avatar.
                            byte[] cachedImage = loadFromCache(item.getId());
                            if (cachedImage != null) {
                                notifyListeners(message.getFrom().asBareJid(), cachedImage);
                            } else {
                                // We don't have a cached copy, let's retrieve it.

                                // Determine the best info
                                AvatarMetadata.Info chosenInfo = null;
                                // Check if there's an avatar, which is stored in PubSub node (and therefore must be in PNG format).
                                for (AvatarMetadata.Info info : avatarMetadata.getInfoList()) {
                                    if (info.getUrl() == null) {
                                        chosenInfo = info;
                                    }
                                }

                                // If only URLs are available, choose the first URL.
                                if (chosenInfo == null) {
                                    for (AvatarMetadata.Info info : avatarMetadata.getInfoList()) {
                                        if (info.getUrl() != null) {
                                            chosenInfo = info;
                                            break;
                                        }
                                    }
                                }

                                if (chosenInfo != null && chosenInfo.getUrl() != null) {
                                    try {
                                        URLConnection urlConnection = chosenInfo.getUrl().openConnection();
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        // Download the image file.
                                        try (InputStream in = urlConnection.getInputStream()) {
                                            byte data[] = new byte[4096];
                                            int n;
                                            while ((n = in.read(data, 0, 4096)) != -1) {
                                                baos.write(data, 0, n);
                                            }
                                        }
                                        byte[] data = baos.toByteArray();
                                        storeToCache(item.getId(), data);
                                        notifyListeners(message.getFrom().asBareJid(), data);
                                    } catch (IOException e1) {
                                        logger.log(Level.WARNING, "Failed to download avatar from advertised URL: {0}.", chosenInfo.getUrl());
                                    }
                                } else {
                                    PubSubService pubSubService = xmppSession.getManager(PubSubManager.class).createPubSubService(message.getFrom());
                                    pubSubService.node(AvatarData.NAMESPACE).getItems(item.getId()).whenComplete((items, ex) -> {
                                        if (ex != null) {
                                            logger.log(Level.WARNING, () -> String.format("Failed to retrieve avatar '%s' from PEP service for user '%s'", item.getId(), message.getFrom()));
                                        } else {
                                            if (!items.isEmpty()) {
                                                Item i = items.get(0);
                                                if (i.getPayload() instanceof AvatarData) {
                                                    AvatarData avatarData = (AvatarData) i.getPayload();
                                                    storeToCache(item.getId(), avatarData.getData());
                                                    notifyListeners(message.getFrom().asBareJid(), avatarData.getData());
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    @Override
    protected final void onEnable() {
        super.onEnable();
        xmppSession.addInboundPresenceListener(inboundPresenceListener);
        xmppSession.addOutboundPresenceListener(outboundPresenceListener);
        xmppSession.addInboundMessageListener(inboundMessageListener);
    }

    @Override
    protected final void onDisable() {
        super.onDisable();
        xmppSession.removeInboundPresenceListener(inboundPresenceListener);
        xmppSession.removeOutboundPresenceListener(outboundPresenceListener);
        xmppSession.removeInboundMessageListener(inboundMessageListener);
    }

    private void resetHash() {
        // Remove our own hash and send an empty presence.
        // The lack of our own hash, will download the vCard and either broadcasts the image hash or an empty hash.
        userHashes.remove(xmppSession.getConnectedResource().asBareJid());
        Presence presence = xmppSession.getManager(PresenceManager.class).getLastSentPresence();
        if (presence == null) {
            presence = new Presence();
        }
        presence.getExtensions().clear();
        xmppSession.send(presence);
    }

    private void notifyListeners(Jid contact, byte[] avatar) {
        XmppUtils.notifyEventListeners(avatarChangeListeners, new AvatarChangeEvent(AvatarManager.this, contact, avatar));
    }

    private AsyncResult<byte[]> getAvatarByVCard(Jid contact) {

        // Let's see, if there's a stored image already.
        String hash = userHashes.get(contact);
        // "" means, the user is known to have no avatar. Therefore don't try to load anything.
        if (!"".equals(hash)) {
            if (hash != null) {
                byte[] imageData = loadFromCache(hash);
                if (imageData != null) {
                    return new AsyncResult<>(CompletableFuture.completedFuture(imageData));
                }
            }

            return avatarRequests.computeIfAbsent(contact, key -> {

                VCardManager vCardManager = xmppSession.getManager(VCardManager.class);
                // Load the vCard for that user
                AsyncResult<VCard> vCard;

                if (contact.equals(xmppSession.getConnectedResource().asBareJid())) {
                    vCard = vCardManager.getVCard();
                } else {
                    vCard = vCardManager.getVCard(contact);
                }
                return vCard.handle((result, exc) -> {
                    // If there's no avatar for that user, create an empty avatar and load it.
                    byte[] avatar = new byte[0];
                    String hash1 = "";
                    // If the user has no vCard (e.g. server returned <item-not-found/> or <service-unavailable/>),
                    // the user also has no avatar obviously.
                    if (result != null) {
                        // And check if it has a photo.
                        VCard.Image image = result.getPhoto();
                        if (image != null && image.getValue() != null) {
                            hash1 = XmppUtils.hash(image.getValue());
                            avatar = image.getValue();
                        }
                    }
                    userHashes.put(contact, hash1);
                    if (!Arrays.equals(avatar, new byte[0])) {
                        storeToCache(hash1, avatar);
                    }
                    avatarRequests.remove(contact);
                    return avatar;
                });
            });
        }

        return new AsyncResult<>(CompletableFuture.completedFuture(new byte[0]));
    }

    private byte[] loadFromCache(String hash) {
        if (avatarCache != null) {
            try {
                return avatarCache.get(hash + ".avatar");
            } catch (Exception e) {
                logger.log(Level.WARNING, "Could not read avatar from cache.", e);
            }
        }
        return null;
    }

    private void storeToCache(String hash, byte[] image) {
        if (avatarCache != null) {
            try {
                avatarCache.put(hash + ".avatar", image);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Could not write avatar to cache.", e);
            }
        }
    }

    private final void backupUserHashes() {
        this.userHashes.forEach((jid, hash) -> this.hashesLocalStore.put(jid + ".userhash", hash.getBytes(StandardCharsets.UTF_16)));
    }

    private final void restoreUserHashes() {
        this.hashesLocalStore.forEach((jid, hash) -> this.userHashes.put(Jid.of(jid.replace(".userhash", "")), new String(hash, StandardCharsets.UTF_16)));
    }

    /**
     * Gets the user avatar from the user's vCard.
     *
     * @param contact The contact.
     * @return The async result with the contact's avatar or null, if it has no avatar.
     */
    public final AsyncResult<byte[]> getAvatar(Jid contact) {
        return getAvatarByVCard(contact.asBareJid());
    }

    /**
     * Gets the user avatar from the user's vCard.
     *
     * @param contact The contact. Must not be {@code null}.
     * @return The async result with the contact's avatar or null, if it has no avatar.
     */
    public final AsyncResult<BufferedImage> getAvatarImage(final Jid contact) {
        return this.getAvatar(requireNonNull(contact)).thenApply(bitmap -> {
            try {
                return bitmap == null ? null : asBufferedImage(bitmap);
            } catch (ConversionException e) {
                throw new CompletionException(e);
            }
        });
    }

    /**
     * Publishes an avatar to your VCard.
     *
     * @param imageData The avatar image data, which must be in PNG format. {@code null} resets the avatar.
     * @return The async result.
     * @see <a href="https://xmpp.org/extensions/xep-0153.html#publish">3.1 User Publishes Avatar</a>
     */
    public final AsyncResult<Void> publishAvatar(byte[] imageData) {

        String hash = imageData != null ? XmppUtils.hash(imageData) : null;
        AvatarMetadata.Info info = imageData != null ? new AvatarMetadata.Info(imageData.length, hash, hash) : null;

        // Try publishing to vCard first. If this fails, don't immediately throw an exception, but try PEP first.
        return publishToVCard(imageData, null, hash)
                .whenComplete((result, e) -> {
                    if (e != null) {
                        logger.warning("Failed to publish avatar to vCard.");
                    }
                })
                .thenCompose((aVoid) -> publishToPersonalEventingService(imageData, hash, info))
                .thenRun(() -> {
                });
    }

    /**
     * Publishes an avatar to your VCard.
     *
     * @param bufferedImage The avatar image, which must be in PNG format. {@code null}
     *                      resets the avatar.
     * @return The async result.
     * @throws XmppException If the image could not be converted to PNG.
     * @see <a href="https://xmpp.org/extensions/xep-0153.html#publish">3.1 User Publishes Avatar</a>
     */
    public final AsyncResult<Void> publishAvatarImage(final BufferedImage bufferedImage) throws XmppException {
        try {
            return this.publishAvatar(bufferedImage == null ? null : asPNG(bufferedImage));
        } catch (final ConversionException e) {
            throw new XmppException(e);
        }
    }

    /**
     * Publishes an avatar to the VCard and uses XEP-0153 to notify the contacts about the update.
     *
     * @param avatar The avatar or null, if the avatar is reset.
     * @param type   The image type.
     * @param hash   The hash.
     * @return The async result.
     */
    private AsyncResult<Void> publishToVCard(byte[] avatar, String type, String hash) {

        return vCardManager.getVCard().thenApply(result -> {
            // If there's no vCard yet (e.g. <item-not-found/>), create a new one.
            return result != null ? result : new VCard();
        }).thenAccept(vCard -> {
            if (avatar != null) {
                // Within a given session, a client MUST NOT attempt to upload a given avatar image more than once.
                // The client MAY upload the avatar image to the vCard on login and after that MUST NOT upload the vCard again
                // unless the user actively changes the avatar image.
                if (vCard.getPhoto() == null || !Arrays.equals(vCard.getPhoto().getValue(), avatar)) {
                    userHashes.put(xmppSession.getConnectedResource().asBareJid(), hash);
                    // If either there is avatar yet, or the old avatar is different from the new one: update
                    vCard.setPhoto(new VCard.Image(type, avatar));
                    vCardManager.setVCard(vCard);
                }
            } else {
                userHashes.put(xmppSession.getConnectedResource().asBareJid(), "");
                // If there's currently a photo, we want to reset it.
                if (vCard.getPhoto() != null && vCard.getPhoto().getValue() != null) {
                    vCard.setPhoto(null);
                    vCardManager.setVCard(vCard);
                }
            }
        });
    }

    /**
     * Publishes an avatar to the personal eventing service.
     *
     * @param avatar The avatar or null, if the avatar is reset.
     * @param itemId The item id.
     * @param info   The info element.
     * @return The async result.
     */
    private AsyncResult<String> publishToPersonalEventingService(byte[] avatar, String itemId, AvatarMetadata.Info info) {
        PubSubService personalEventingService = xmppSession.getManager(PubSubManager.class).createPersonalEventingService();
        if (avatar != null) {
            if (info.getUrl() == null) {
                // Publish image.
                return personalEventingService.node(AvatarData.NAMESPACE).publish(itemId, new AvatarData(avatar));
            }
            // Publish meta data.
            return personalEventingService.node(AvatarMetadata.NAMESPACE).publish(itemId, new AvatarMetadata(info));
        } else {
            return personalEventingService.node(AvatarMetadata.NAMESPACE).publish(itemId, new AvatarMetadata());
        }
    }

    /**
     * Adds an avatar listener, to listen for avatar updates.
     *
     * @param avatarChangeListener The avatar listener.
     */
    public final void addAvatarChangeListener(Consumer<AvatarChangeEvent> avatarChangeListener) {
        avatarChangeListeners.add(avatarChangeListener);
    }

    /**
     * Removes a previously added avatar listener.
     *
     * @param avatarChangeListener The avatar listener.
     */
    public final void removeAvatarChangeListener(Consumer<AvatarChangeEvent> avatarChangeListener) {
        avatarChangeListeners.remove(avatarChangeListener);
    }

    /**
     * Converts {@code bitmap} into {@link BufferedImage}.
     *
     * @param bitmap The bitmap to convert. Must not be {@code null}.
     * @return Instance of {@link BufferedImage} created from {@code bitmap}.
     * Never {@code null}.
     * @throws ConversionException if conversion failed
     */
    static final BufferedImage asBufferedImage(final byte[] bitmap) throws ConversionException {
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(requireNonNull(bitmap))) {
            return ofNullable(ImageIO.read(inputStream)).orElseThrow(ConversionException::new);
        } catch (final IOException e) {
            throw new ConversionException(e);
        }
    }

    /**
     * Converts {@code image} into {@code byte[]}.
     *
     * @param bufferedImage The image to convert. Must not be {@code null}.
     * @return PNG bitmap created from {@code image}. Never {@code null}.
     * @throws ConversionException if conversion failed
     */
    private static final byte[] asPNG(final BufferedImage bufferedImage) throws ConversionException {
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (!ImageIO.write(requireNonNull(bufferedImage), "png", outputStream))
                throw new ConversionException();
            return outputStream.toByteArray();
        } catch (final IOException e) {
            throw new ConversionException(e);
        }
    }

    /**
     * Indicates the inability to convert a value from one data type into
     * another.
     *
     * @author Markus KARG (markus@headcrashing.eu)
     */
    @SuppressWarnings("serial")
    static final class ConversionException extends Exception {

        public ConversionException() {
            // Intentionally left blank.
        }

        public ConversionException(final Throwable cause) {
            super(cause);
        }
    }

    @Override
    protected void dispose() {
        backupUserHashes();

        avatarChangeListeners.clear();
        avatarRequests.clear();
        nonConformingResources.clear();
        userHashes.clear();
    }
}
