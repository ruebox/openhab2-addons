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

package rocks.xmpp.extensions.vcard.temp;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.extensions.avatar.AvatarManager;
import rocks.xmpp.extensions.vcard.temp.model.VCard;
import rocks.xmpp.im.subscription.PresenceManager;
import rocks.xmpp.util.concurrent.AsyncResult;

/**
 * This manager allows to retrieve or save one owns vCard or retrieve another user's vCard.
 * <p>
 * The use cases are also described in <a href="https://xmpp.org/extensions/xep-0054.html">XEP-0054: vcard-temp</a> in more detail.
 * </p>
 *
 * @author Christian Schudt
 */
public final class VCardManager extends Manager {

    private VCardManager(final XmppSession xmppSession) {
        super(xmppSession);
    }

    /**
     * Gets the vCard of the current user.
     *
     * @return The async result with the vCard.
     */
    public AsyncResult<VCard> getVCard() {
        return xmppSession.query(IQ.get(new VCard()), VCard.class);
    }

    /**
     * Gets the vCard of another user.
     *
     * @param jid The user's JID.
     * @return The async result of the vCard which may be null, if it does not exist.
     */
    public AsyncResult<VCard> getVCard(Jid jid) {
        return xmppSession.query(IQ.get(jid.asBareJid(), new VCard()), VCard.class);
    }

    /**
     * Saves or updates a vCard.
     *
     * @param vCard The vCard.
     * @return The async result.
     */
    public AsyncResult<Void> setVCard(VCard vCard) {
        // Update the vCard
        AsyncResult<IQ> query = xmppSession.query(IQ.set(vCard));
        return query.thenRun(() -> {
            // Then inform about the update by sending a presence. The avatar manager will add the update extension.
            AvatarManager avatarManager = xmppSession.getManager(AvatarManager.class);
            if (isEnabled() && avatarManager.isEnabled()) {
                Presence presence = xmppSession.getManager(PresenceManager.class).getLastSentPresence();
                if (presence == null) {
                    presence = new Presence();
                }
                presence.getExtensions().clear();
                xmppSession.send(presence);
            }
        });
    }
}
