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

package rocks.xmpp.core.session.context.extensions;

import rocks.xmpp.core.session.Extension;
import rocks.xmpp.core.session.Module;
import rocks.xmpp.extensions.activity.model.Activity;
import rocks.xmpp.extensions.address.model.Addresses;
import rocks.xmpp.extensions.attention.model.Attention;
import rocks.xmpp.extensions.avatar.AvatarManager;
import rocks.xmpp.extensions.avatar.model.data.AvatarData;
import rocks.xmpp.extensions.avatar.model.metadata.AvatarMetadata;
import rocks.xmpp.extensions.blocking.BlockingManager;
import rocks.xmpp.extensions.blocking.model.BlockList;
import rocks.xmpp.extensions.bob.model.Data;
import rocks.xmpp.extensions.bookmarks.model.BookmarkStorage;
import rocks.xmpp.extensions.bytestreams.ibb.InBandByteStreamManager;
import rocks.xmpp.extensions.bytestreams.ibb.model.InBandByteStream;
import rocks.xmpp.extensions.bytestreams.s5b.Socks5ByteStreamManager;
import rocks.xmpp.extensions.bytestreams.s5b.model.Socks5ByteStream;
import rocks.xmpp.extensions.carbons.MessageCarbonsManager;
import rocks.xmpp.extensions.carbons.model.MessageCarbons;
import rocks.xmpp.extensions.chatstates.ChatStateManager;
import rocks.xmpp.extensions.chatstates.model.ChatState;
import rocks.xmpp.extensions.commands.model.Command;
import rocks.xmpp.extensions.component.accept.model.ComponentIQ;
import rocks.xmpp.extensions.component.accept.model.ComponentMessage;
import rocks.xmpp.extensions.component.accept.model.ComponentPresence;
import rocks.xmpp.extensions.component.accept.model.Handshake;
import rocks.xmpp.extensions.csi.model.ClientState;
import rocks.xmpp.extensions.delay.model.DelayedDelivery;
import rocks.xmpp.extensions.featureneg.model.FeatureNegotiation;
import rocks.xmpp.extensions.filetransfer.FileTransferManager;
import rocks.xmpp.extensions.forward.StanzaForwardingManager;
import rocks.xmpp.extensions.forward.model.Forwarded;
import rocks.xmpp.extensions.geoloc.GeoLocationManager;
import rocks.xmpp.extensions.geoloc.model.GeoLocation;
import rocks.xmpp.extensions.hashes.model.Hash;
import rocks.xmpp.extensions.httpauth.HttpAuthenticationManager;
import rocks.xmpp.extensions.httpauth.model.ConfirmationRequest;
import rocks.xmpp.extensions.idle.model.Idle;
import rocks.xmpp.extensions.invisible.InvisibilityManager;
import rocks.xmpp.extensions.invisible.model.InvisibleCommand;
import rocks.xmpp.extensions.jingle.JingleManager;
import rocks.xmpp.extensions.jingle.apps.filetransfer.JingleFileTransferManager;
import rocks.xmpp.extensions.jingle.apps.filetransfer.model.JingleFileTransfer;
import rocks.xmpp.extensions.jingle.apps.rtp.model.Rtp;
import rocks.xmpp.extensions.jingle.model.Jingle;
import rocks.xmpp.extensions.jingle.thumbs.model.Thumbnail;
import rocks.xmpp.extensions.jingle.transports.ibb.model.InBandByteStreamsTransportMethod;
import rocks.xmpp.extensions.jingle.transports.iceudp.model.IceUdpTransportMethod;
import rocks.xmpp.extensions.jingle.transports.s5b.model.S5bTransportMethod;
import rocks.xmpp.extensions.json.model.Json;
import rocks.xmpp.extensions.langtrans.model.LanguageTranslation;
import rocks.xmpp.extensions.last.LastActivityManager;
import rocks.xmpp.extensions.last.model.LastActivity;
import rocks.xmpp.extensions.messagecorrect.model.Replace;
import rocks.xmpp.extensions.mood.MoodManager;
import rocks.xmpp.extensions.mood.model.Mood;
import rocks.xmpp.extensions.muc.MultiUserChatManager;
import rocks.xmpp.extensions.muc.conference.model.DirectInvitation;
import rocks.xmpp.extensions.muc.model.Muc;
import rocks.xmpp.extensions.nick.model.Nickname;
import rocks.xmpp.extensions.offline.OfflineMessageManager;
import rocks.xmpp.extensions.offline.model.OfflineMessage;
import rocks.xmpp.extensions.oob.OutOfBandFileTransferManager;
import rocks.xmpp.extensions.oob.model.iq.OobIQ;
import rocks.xmpp.extensions.oob.model.x.OobX;
import rocks.xmpp.extensions.ping.PingManager;
import rocks.xmpp.extensions.ping.model.Ping;
import rocks.xmpp.extensions.privacy.PrivacyListManager;
import rocks.xmpp.extensions.privacy.model.Privacy;
import rocks.xmpp.extensions.privatedata.rosternotes.model.Annotation;
import rocks.xmpp.extensions.pubsub.model.PubSub;
import rocks.xmpp.extensions.reach.ReachabilityManager;
import rocks.xmpp.extensions.reach.model.Reachability;
import rocks.xmpp.extensions.receipts.MessageDeliveryReceiptsManager;
import rocks.xmpp.extensions.receipts.model.MessageDeliveryReceipts;
import rocks.xmpp.extensions.register.RegistrationManager;
import rocks.xmpp.extensions.register.model.Registration;
import rocks.xmpp.extensions.register.model.feature.RegisterFeature;
import rocks.xmpp.extensions.rosterx.ContactExchangeManager;
import rocks.xmpp.extensions.rosterx.model.ContactExchange;
import rocks.xmpp.extensions.rpc.RpcManager;
import rocks.xmpp.extensions.rpc.model.Rpc;
import rocks.xmpp.extensions.rtt.RealTimeTextManager;
import rocks.xmpp.extensions.rtt.model.RealTimeText;
import rocks.xmpp.extensions.search.model.Search;
import rocks.xmpp.extensions.seclabel.model.SecurityLabel;
import rocks.xmpp.extensions.shim.HeaderManager;
import rocks.xmpp.extensions.shim.model.Headers;
import rocks.xmpp.extensions.si.StreamInitiationManager;
import rocks.xmpp.extensions.si.model.StreamInitiation;
import rocks.xmpp.extensions.si.profile.filetransfer.model.SIFileTransferOffer;
import rocks.xmpp.extensions.sm.StreamManager;
import rocks.xmpp.extensions.sm.model.StreamManagement;
import rocks.xmpp.extensions.time.EntityTimeManager;
import rocks.xmpp.extensions.time.model.EntityTime;
import rocks.xmpp.extensions.tune.model.Tune;
import rocks.xmpp.extensions.vcard.avatar.model.AvatarUpdate;
import rocks.xmpp.extensions.vcard.temp.VCardManager;
import rocks.xmpp.extensions.vcard.temp.model.VCard;
import rocks.xmpp.extensions.version.SoftwareVersionManager;
import rocks.xmpp.extensions.version.model.SoftwareVersion;
import rocks.xmpp.extensions.xhtmlim.model.Html;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Registers extensions and manager classes, (which should be initialized during the start of a session) to the {@link rocks.xmpp.core.session.XmppSession}.
 *
 * @author Christian Schudt
 */
public final class ExtensionModule implements Module {

    private static final String[] REGISTERED_HASH_ALGORITHMS = new String[]{"md5", "sha-1", "sha-224", "sha-256", "sha-384", "sha-512"};

    private static final Set<String> HASH_FEATURES = new HashSet<>();

    static {
        for (String algorithm : REGISTERED_HASH_ALGORITHMS) {
            try {
                MessageDigest.getInstance(algorithm);
                HASH_FEATURES.add("urn:xmpp:hash-function-text-names:" + algorithm);
            } catch (NoSuchAlgorithmException e) {
                // ignore
            }
        }
    }

    @Override
    public final Collection<Extension> getExtensions() {
        return Arrays.asList(

                // XEP-0009: Jabber-RPC
                Extension.of(Rpc.NAMESPACE, RpcManager.class, false, Rpc.class),

                // XEP-0012: Last Activity
                Extension.of(LastActivity.NAMESPACE, LastActivityManager.class, true, LastActivity.class),

                // XEP-0013: Flexible Offline Message Retrieval
                Extension.of(OfflineMessage.NAMESPACE, OfflineMessageManager.class, false, OfflineMessage.class),

                // XEP-0016: Privacy Lists
                Extension.of(Privacy.NAMESPACE, PrivacyListManager.class, false, Privacy.class),

                // XEP-0020: Feature Negotiation
                Extension.of(FeatureNegotiation.NAMESPACE, true, FeatureNegotiation.class),

                // XEP-0033: Extended Stanza Addressing
                Extension.of(Addresses.class),

                // XEP-0045: Multi-User Chat
                Extension.of(Muc.NAMESPACE, MultiUserChatManager.class, true, Muc.class),

                // XEP-0047: In-Band Bytestreams
                Extension.of(InBandByteStream.NAMESPACE, InBandByteStreamManager.class, true, InBandByteStream.class),

                // XEP-0048: BookmarkStorage
                Extension.of(BookmarkStorage.class),

                // XEP-0050: Ad-Hoc Commands
                // TODO add manager class when implemented
                Extension.of(Command.NAMESPACE, false, Command.class),

                // XEP-0054: vcard-temp
                Extension.of(VCard.NAMESPACE, VCardManager.class, true, VCard.class),

                // XEP-0055: Jabber Search
                Extension.of(Search.class),

                // XEP-0060: Publish-Subscribe
                Extension.of(PubSub.class),

                // XEP-0065: SOCKS5 Bytestreams
                Extension.of(Socks5ByteStream.NAMESPACE, Socks5ByteStreamManager.class, true, Socks5ByteStream.class),

                // XEP-0066: Out of Band Data
                Extension.of(OobIQ.NAMESPACE, OutOfBandFileTransferManager.class, true, OobIQ.class),

                // XEP-0066: Out of Band Data
                Extension.of(OobX.NAMESPACE, false, OobX.class),

                // XEP-0070: Verifying HTTP Requests via XMPP
                Extension.of(ConfirmationRequest.NAMESPACE, HttpAuthenticationManager.class, false, ConfirmationRequest.class),

                // XEP-0071: XHTML-IM
                Extension.of(Html.NAMESPACE, false, Html.class),

                // XEP-0077: In-Band Registration
                Extension.of(Registration.NAMESPACE, RegistrationManager.class, false, RegisterFeature.class, Registration.class),

                // XEP-0080: User Location
                Extension.of(GeoLocation.NAMESPACE, GeoLocationManager.class, true, false, GeoLocation.class),

                // XEP-0084: User Avatar
                Extension.of(AvatarMetadata.NAMESPACE, AvatarManager.class, true, false, AvatarMetadata.class),
                Extension.of(AvatarData.NAMESPACE, AvatarManager.class, false, AvatarData.class),

                // XEP-0085: Chat State Notifications
                Extension.of(ChatState.NAMESPACE, ChatStateManager.class, false, ChatState.class),

                // XEP-0092: Software Version
                Extension.of(SoftwareVersion.NAMESPACE, SoftwareVersionManager.class, true, SoftwareVersion.class),

                // XEP-0095: Stream Initiation
                Extension.of(StreamInitiation.NAMESPACE, StreamInitiationManager.class, true, StreamInitiation.class),

                // XEP-0096: SI File Transfer
                Extension.of(SIFileTransferOffer.NAMESPACE, FileTransferManager.class, true, SIFileTransferOffer.class),

                // XEP-0107: User Mood
                Extension.of(Mood.NAMESPACE, MoodManager.class, true, false, Mood.class),

                // XEP-0108: User Activity
                Extension.of(Activity.NAMESPACE, null, true, false, Activity.class),

                // XEP-0114: Jabber Component Protocol
                Extension.of(Handshake.class, ComponentMessage.class, ComponentPresence.class, ComponentIQ.class),

                // XEP-0118: User Tune
                Extension.of(Tune.NAMESPACE, null, true, false, Tune.class),

                // XEP-0131: Stanza Headers and Internet Metadata
                Extension.of(Headers.NAMESPACE, HeaderManager.class, false, Headers.class),

                // XEP-0144: Roster Item Exchange
                Extension.of(ContactExchange.NAMESPACE, ContactExchangeManager.class, false, ContactExchange.class),

                // XEP-0145: Annotations
                Extension.of(Annotation.class),

                // XEP-0152: Reachability Addresses
                Extension.of(Reachability.NAMESPACE, ReachabilityManager.class, false, Reachability.class),

                // XEP-0153: vCard-Based Avatars
                Extension.of(AvatarUpdate.NAMESPACE, AvatarManager.class, false, AvatarUpdate.class),

                // XEP-0166: Jingle
                Extension.of(Jingle.NAMESPACE, JingleManager.class, false, Jingle.class),

                // XEP-0167: Jingle RTP Sessions
                Extension.of(Rtp.NAMESPACE, null, false, Rtp.class),

                // XEP-0171: Language Translation
                Extension.of(LanguageTranslation.class),

                // XEP-0172: User Nickname
                Extension.of(Nickname.class),

                // XEP-0176: Jingle ICE-UDP Transport Method
                Extension.of(IceUdpTransportMethod.class),

                // XEP-0184: Message Delivery Receipts
                Extension.of(MessageDeliveryReceipts.NAMESPACE, MessageDeliveryReceiptsManager.class, false, MessageDeliveryReceipts.class),

                // XEP-0186: Invisible Command
                Extension.of(InvisibleCommand.NAMESPACE, InvisibilityManager.class, false, InvisibleCommand.class),

                // XEP-0191: Blocking Command
                Extension.of(BlockList.NAMESPACE, BlockingManager.class, false, BlockList.class),

                // XEP-0198: Stream Management
                Extension.of(StreamManagement.NAMESPACE, StreamManager.class, false, StreamManagement.class),

                // XEP-0199: XMPP Ping
                Extension.of(Ping.NAMESPACE, PingManager.class, true, Ping.class),

                // XEP-0202: Entity Time
                Extension.of(EntityTime.NAMESPACE, EntityTimeManager.class, true, EntityTime.class),

                // XEP-0203: Delayed Delivery
                Extension.of(DelayedDelivery.class),

                // XEP-0224: Attention
                Extension.of(Attention.NAMESPACE, false, Attention.class),

                // XEP-0231: Bits of Binary
                Extension.of(Data.NAMESPACE, null, false, Data.class),

                // XEP-0234: Jingle File Transfer
                Extension.of(JingleFileTransfer.NAMESPACE, JingleFileTransferManager.class, false, JingleFileTransfer.class),

                // XEP-0249: Direct MUC Invitations
                Extension.of(DirectInvitation.NAMESPACE, MultiUserChatManager.class, true, DirectInvitation.class),

                // XEP-0258: Security Labels in XMPP
                Extension.of(SecurityLabel.NAMESPACE, false, SecurityLabel.class),

                // XEP-0260: Jingle SOCKS5 Bytestreams Transport Method
                Extension.of(S5bTransportMethod.NAMESPACE, null, false, S5bTransportMethod.class),

                // XEP-0261: Jingle In-Band Bytestreams Transport Method
                Extension.of(InBandByteStreamsTransportMethod.NAMESPACE, null, false, InBandByteStreamsTransportMethod.class),

                // XEP-0264: Jingle Content Thumbnails
                Extension.of(Thumbnail.class),

                // XEP-0280: Message Carbons
                Extension.of(MessageCarbons.NAMESPACE, MessageCarbonsManager.class, false, MessageCarbons.class),

                // XEP-0297: Stanza Forwarding
                Extension.of(Forwarded.NAMESPACE, StanzaForwardingManager.class, false, Forwarded.class),

                // XEP-0300: Use of Cryptographic Hash Functions in XMPP
                Extension.of(Hash.NAMESPACE, null, HASH_FEATURES, true, Hash.class),

                // XEP-0301: In-Band Real Time Text
                Extension.of(RealTimeText.NAMESPACE, RealTimeTextManager.class, false, RealTimeText.class),

                // XEP-0308: Last Message Correction
                Extension.of(Replace.NAMESPACE, false, Replace.class),

                // XEP-0319: Last User Interaction in Presence
                Extension.of(Idle.NAMESPACE, true, Idle.class),

                // XEP-0335: JSON Containers
                Extension.of(Json.class),

                // XEP-0352: Client State Indication
                Extension.of(ClientState.class)
        );
    }
}
