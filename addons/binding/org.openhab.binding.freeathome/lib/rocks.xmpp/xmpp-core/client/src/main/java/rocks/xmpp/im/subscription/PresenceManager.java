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

package rocks.xmpp.im.subscription;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.Presence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages subscription requests and presences.
 * <p>
 * This class allows to request, approve, deny and unsubscribe subscriptions.
 * </p>
 * This class is unconditionally thread-safe.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/rfcs/rfc6121.html#sub">3.  Managing Presence Subscriptions</a>
 */
public final class PresenceManager extends Manager {

    // TODO auto deny or auto approve some or all requests.

    private final Map<Jid, Map<String, Presence>> presenceMap = new ConcurrentHashMap<>();

    private final Map<String, Presence> lastSentPresences = new ConcurrentHashMap<>();

    private PresenceManager(final XmppSession xmppSession) {
        super(xmppSession, false);
    }

    @Override
    protected final void initialize() {
        xmppSession.addInboundPresenceListener(e -> {
            Presence presence = e.getPresence();
            if (presence.getFrom() != null) {
                // Store the user (bare JID) in the map, associated with different resources.
                Map<String, Presence> presencesPerResource = presenceMap.computeIfAbsent(presence.getFrom().asBareJid(), key -> new ConcurrentHashMap<>());
                // Update the contact's resource with the presence.
                presencesPerResource.put(presence.getFrom().getResource() != null ? presence.getFrom().getResource() : "", presence);
            }
        });
        xmppSession.addOutboundPresenceListener(e -> {
            Presence presence = e.getPresence();
            // Store the last sent presences, in order to automatically resend them, after a disconnect.
            if (presence.isAvailable()) {
                if (presence.getTo() == null) {
                    lastSentPresences.put("", presence);
                } else {
                    lastSentPresences.put(presence.getTo().toString(), presence);
                }
            } else if (presence.getType() == Presence.Type.UNAVAILABLE) {
                if (presence.getTo() == null) {
                    lastSentPresences.remove("");
                } else {
                    lastSentPresences.remove(presence.getTo().toString());
                }
            }
        });
    }

    /**
     * Gets the presence for a given contact.
     * <p>
     * If the given JID is a bare JID, and the contact has sent multiple presences with different resources,
     * the "best" presence is returned, i.e. the presence with the highest priority or the presence with the "most available" {@code <show/>} element.
     * </p>
     * <p>
     * If the JID is a full JID, the exact presence of that JID is returned.
     * </p>
     * <p>
     * If no presence could be found an unavailable presence is returned.
     * </p>
     *
     * @param jid The JID.
     * @return The presence.
     */
    public final Presence getPresence(Jid jid) {

        if (Objects.requireNonNull(jid, "jid must not be null.").isBareJid()) {
            Map<String, Presence> presencesPerResource = presenceMap.get(jid);
            if (presencesPerResource != null) {
                List<Presence> presences = new ArrayList<>(presencesPerResource.values());
                if (!presences.isEmpty()) {
                    presences.sort(null);
                    return presences.get(0);
                }
            }
        } else {
            Map<String, Presence> presencesPerResource = presenceMap.get(jid.asBareJid());
            if (presencesPerResource != null) {
                Presence presence = presencesPerResource.get(jid.getResource());
                if (presence != null) {
                    return presence;
                }
            }
        }
        Presence presence = new Presence(Presence.Type.UNAVAILABLE);
        presence.setFrom(jid);
        return presence;
    }

    /**
     * Sends a subscription request to a potential contact.
     *
     * @param jid    The contact's JID.
     * @param status The status, which is used for additional information during the subscription request.
     * @return The id, which is used for the request.
     * @see <a href="https://xmpp.org/rfcs/rfc6121.html#sub-request-gen">3.1.1.  Client Generation of Outbound Subscription Request</a>
     */
    public final String requestSubscription(Jid jid, String status) {
        // the value of the 'to' attribute MUST be a bare JID
        Presence presence = new Presence(jid.asBareJid(), Presence.Type.SUBSCRIBE, status, UUID.randomUUID().toString());
        xmppSession.send(presence);
        return presence.getId();
    }

    /**
     * Approves a subscription request.
     *
     * @param jid The contact's JID, who has previously requested a subscription.
     * @return The id, which is used for the approval.
     * @see <a href="https://xmpp.org/rfcs/rfc6121.html#sub-request-handle">3.1.4.  Client Processing of Inbound Subscription Request</a>
     */
    public final String approveSubscription(Jid jid) {
        // For tracking purposes, a client SHOULD include an 'id' attribute in a subscription approval or subscription denial; this 'id' attribute MUST NOT mirror the 'id' attribute of the subscription request.
        Presence presence = new Presence(jid, Presence.Type.SUBSCRIBED, null, UUID.randomUUID().toString());
        xmppSession.send(presence);
        return presence.getId();
    }

    /**
     * Cancels a previously granted subscription or denies a subscription request.
     * <p>
     * This basically means that the contact won't receive presence information from you.
     * </p>
     *
     * @param jid The contact's JID, whose subscription is denied or canceled.
     * @return The id, which is used for the subscription denial.
     * @see <a href="https://xmpp.org/rfcs/rfc6121.html#sub-cancel-gen">3.2.1.  Client Generation of Subscription Cancellation</a>
     */
    public final String denySubscription(Jid jid) {
        // For tracking purposes, a client SHOULD include an 'id' attribute in a subscription approval or subscription denial; this 'id' attribute MUST NOT mirror the 'id' attribute of the subscription request.
        Presence presence = new Presence(jid, Presence.Type.UNSUBSCRIBED, null, UUID.randomUUID().toString());
        xmppSession.send(presence);
        return presence.getId();
    }

    /**
     * Unsubscribes from a contact's presence.
     * <p>
     * That basically means, that you will not receive any presence information from the contact anymore.
     * </p>
     *
     * @param jid The contact's JID.
     * @return The id, which is used for the unsubscription.
     * @see <a href="https://xmpp.org/rfcs/rfc6121.html#sub-unsub-gen">3.3.1.  Client Generation of Unsubscribe</a>
     */
    public final String unsubscribe(Jid jid) {
        // For tracking purposes, a client SHOULD include an 'id' attribute in a subscription approval or subscription denial; this 'id' attribute MUST NOT mirror the 'id' attribute of the subscription request.
        Presence presence = new Presence(jid, Presence.Type.UNSUBSCRIBE, null, UUID.randomUUID().toString());
        xmppSession.send(presence);
        return presence.getId();
    }

    /**
     * Gets the last sent (non-directed) presence, that has been broadcast by the server.
     *
     * @return The presence.
     */
    public final Presence getLastSentPresence() {
        return lastSentPresences.get("");
    }

    /**
     * Gets the last sent presences, that have been sent, including directed presences.
     *
     * @return The presence.
     */
    public final Collection<Presence> getLastSentPresences() {
        return lastSentPresences.values();
    }
}
