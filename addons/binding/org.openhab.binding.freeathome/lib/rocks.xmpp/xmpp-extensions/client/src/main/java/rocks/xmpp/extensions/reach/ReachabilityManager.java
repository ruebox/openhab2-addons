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

package rocks.xmpp.extensions.reach;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.AbstractIQHandler;
import rocks.xmpp.core.stanza.IQHandler;
import rocks.xmpp.core.stanza.MessageEvent;
import rocks.xmpp.core.stanza.PresenceEvent;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.core.stanza.model.Stanza;
import rocks.xmpp.extensions.reach.model.Address;
import rocks.xmpp.extensions.reach.model.Reachability;
import rocks.xmpp.util.XmppUtils;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * Allows to query for reachability addresses of another contact, automatically responds to reachability queries and notifies {@linkplain Consumer}s,
 * when the reachability of a contact has changed either via presence or PEP.
 * <p>
 * By default this manager is not enabled. If you support reachability addresses you have to {@linkplain #setEnabled(boolean) enable} it.
 * </p>
 *
 * @author Christian Schudt
 */
public final class ReachabilityManager extends Manager {

    private final Set<Consumer<ReachabilityEvent>> reachabilityListeners = new CopyOnWriteArraySet<>();

    private final Map<Jid, List<Address>> reachabilities = new ConcurrentHashMap<>();

    private final List<Address> addresses = new CopyOnWriteArrayList<>();

    private final Consumer<PresenceEvent> inboundPresenceListener;

    private final Consumer<PresenceEvent> outboundPresenceListener;

    private final Consumer<MessageEvent> inboundMessageEvent;

    private final IQHandler iqHandler;

    private ReachabilityManager(final XmppSession xmppSession) {
        super(xmppSession, true);

        this.inboundPresenceListener = e -> {
            Presence presence = e.getPresence();
            boolean hasReachability = checkStanzaForReachabilityAndNotify(presence);
            Jid contact = presence.getFrom().asBareJid();
            if (!hasReachability && reachabilities.remove(contact) != null) {
                // If no reachability was found in presence, check, if the contact has previously sent any reachability via presence.
                XmppUtils.notifyEventListeners(reachabilityListeners, new ReachabilityEvent(ReachabilityManager.this, contact, Collections.emptyList()));
            }
        };

        this.outboundPresenceListener = e -> {
            Presence presence = e.getPresence();
            if (presence.isAvailable() && presence.getTo() == null) {
                synchronized (addresses) {
                    if (!addresses.isEmpty()) {
                        presence.putExtension(new Reachability(new ArrayDeque<>(addresses)));
                    }
                }
            }
        };

        this.inboundMessageEvent = e -> checkStanzaForReachabilityAndNotify(e.getMessage());

        this.iqHandler = new AbstractIQHandler(IQ.Type.GET) {
            @Override
            protected IQ processRequest(IQ iq) {
                // In addition, a contact MAY request a user's reachability addresses in an XMPP <iq/> stanza of type "get"
                return iq.createResult(new Reachability(addresses));
            }
        };
    }


    @Override
    protected void onEnable() {
        super.onEnable();
        xmppSession.addInboundPresenceListener(inboundPresenceListener);
        xmppSession.addOutboundPresenceListener(outboundPresenceListener);

        // A user MAY send reachability addresses in an XMPP <message/> stanza.
        xmppSession.addInboundMessageListener(inboundMessageEvent);

        // In addition, a contact MAY request a user's reachability addresses in an XMPP <iq/> stanza of type "get"
        xmppSession.addIQHandler(Reachability.class, iqHandler);

        // TODO: implement similar logic for PEP
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        xmppSession.removeInboundPresenceListener(inboundPresenceListener);
        xmppSession.removeOutboundPresenceListener(outboundPresenceListener);
        xmppSession.removeInboundMessageListener(inboundMessageEvent);
        xmppSession.removeIQHandler(Reachability.class);
    }

    public List<Address> getReachabilityAddresses() {
        return addresses;
    }

    private boolean checkStanzaForReachabilityAndNotify(Stanza stanza) {
        Reachability reachability = stanza.getExtension(Reachability.class);
        if (stanza.getFrom() != null) {
            Jid contact = stanza.getFrom().asBareJid();
            if (reachability != null) {
                synchronized (reachabilities) {
                    List<Address> oldReachabilityAddresses = reachabilities.get(contact);
                    if (oldReachabilityAddresses == null || !oldReachabilityAddresses.equals(reachability.getAddresses())) {
                        reachabilities.put(contact, reachability.getAddresses());
                        XmppUtils.notifyEventListeners(reachabilityListeners, new ReachabilityEvent(this, contact, reachability.getAddresses()));
                    }
                }
            }
        }
        return reachability != null;
    }

    /**
     * Adds a reachability listener, which allows to listen for reachability updates.
     *
     * @param reachabilityListener The listener.
     * @see #removeReachabilityListener(Consumer)
     */
    public void addReachabilityListener(Consumer<ReachabilityEvent> reachabilityListener) {
        reachabilityListeners.add(reachabilityListener);
    }

    /**
     * Removes a previously added reachability listener.
     *
     * @param reachabilityListener The listener.
     * @see #addReachabilityListener(Consumer)
     */
    public void removeReachabilityListener(Consumer<ReachabilityEvent> reachabilityListener) {
        reachabilityListeners.remove(reachabilityListener);
    }

    /**
     * Requests the reachability addresses of a contact.
     *
     * @param contact The contact.
     * @return The async result with the reachability addresses.
     */
    public AsyncResult<List<Address>> requestReachabilityAddresses(Jid contact) {
        // In addition, a contact MAY request a user's reachability addresses in an XMPP <iq/> stanza of type "get".
        return xmppSession.query(IQ.get(contact, new Reachability())).thenApply(result -> {
            Reachability reachability = result.getExtension(Reachability.class);
            if (reachability != null) {
                return reachability.getAddresses();
            }
            return null;
        });
    }

    @Override
    protected void dispose() {
        reachabilityListeners.clear();
        reachabilities.clear();
        addresses.clear();
    }
}
