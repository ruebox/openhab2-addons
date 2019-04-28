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

package rocks.xmpp.extensions.privacy;

import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.AbstractIQHandler;
import rocks.xmpp.core.stanza.IQHandler;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.extensions.privacy.model.Privacy;
import rocks.xmpp.extensions.privacy.model.PrivacyList;
import rocks.xmpp.util.XmppUtils;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * This class manages privacy lists, which allow users to block communications from other users as described in <a href="https://xmpp.org/extensions/xep-0016.html">XEP-0016: Privacy Lists</a>.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/extensions/xep-0016.html#protocol">2. Protocol</a></cite></p>
 * <p>Server-side privacy lists enable successful completion of the following use cases:</p>
 * <ul>
 * <li>Retrieving one's privacy lists.</li>
 * <li>Adding, removing, and editing one's privacy lists.</li>
 * <li>Setting, changing, or declining active lists.</li>
 * <li>Setting, changing, or declining the default list (i.e., the list that is active by default).</li>
 * <li>Allowing or blocking messages based on JID, group, or subscription type (or globally).</li>
 * <li>Allowing or blocking inbound presence notifications based on JID, group, or subscription type (or globally).</li>
 * <li>Allowing or blocking outbound presence notifications based on JID, group, or subscription type (or globally).</li>
 * <li>Allowing or blocking IQ stanzas based on JID, group, or subscription type (or globally).</li>
 * <li>Allowing or blocking all communications based on JID, group, or subscription type (or globally).</li>
 * </ul>
 * </blockquote>
 *
 * @author Christian Schudt
 */
public final class PrivacyListManager extends Manager {
    private final Set<Consumer<PrivacyListEvent>> privacyListListeners = new CopyOnWriteArraySet<>();

    private final IQHandler iqHandler;

    private PrivacyListManager(final XmppSession xmppSession) {
        super(xmppSession, true);
        iqHandler = new AbstractIQHandler(IQ.Type.SET) {
            @Override
            protected IQ processRequest(IQ iq) {
                if (iq.getFrom() == null || iq.getFrom().equals(xmppSession.getConnectedResource().asBareJid())) {
                    Privacy privacy = iq.getExtension(Privacy.class);
                    if (privacy != null) {
                        List<PrivacyList> privacyLists = privacy.getPrivacyLists();
                        if (privacyLists.size() == 1) {
                            XmppUtils.notifyEventListeners(privacyListListeners, new PrivacyListEvent(PrivacyListManager.this, privacyLists.get(0).getName()));
                        }
                    }
                    // In accordance with the semantics of IQ stanzas defined in XMPP Core [7], each connected resource MUST return an IQ result to the server as well.
                    return iq.createResult();
                }
                return iq.createError(Condition.NOT_ACCEPTABLE);
            }
        };
    }


    @Override
    protected void onEnable() {
        super.onEnable();
        xmppSession.addIQHandler(Privacy.class, iqHandler, false);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        xmppSession.removeIQHandler(Privacy.class);
    }

    /**
     * Adds a privacy list listener.
     *
     * @param privacyListListener The listener.
     * @see #removePrivacyListListener(Consumer)
     */
    public void addPrivacyListListener(Consumer<PrivacyListEvent> privacyListListener) {
        privacyListListeners.add(privacyListListener);
    }

    /**
     * Removes a previously added privacy list listener.
     *
     * @param privacyListListener The listener.
     * @see #addPrivacyListListener(Consumer)
     */
    public void removePrivacyListListener(Consumer<PrivacyListEvent> privacyListListener) {
        privacyListListeners.remove(privacyListListener);
    }

    /**
     * Gets the privacy lists.
     *
     * @return The async result with the privacy lists.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-retrieve">2.3 Retrieving One's Privacy Lists</a>
     */
    public AsyncResult<Collection<PrivacyList>> getPrivacyLists() {
        return xmppSession.query(IQ.get(new Privacy())).thenApply(result -> {
            Privacy privacy = result.getExtension(Privacy.class);
            Collection<PrivacyList> privacyLists = new ArrayList<>();
            for (PrivacyList privacyList : privacy.getPrivacyLists()) {
                if (privacyList.getName() != null && privacyList.getName().equals(privacy.getDefaultName())) {
                    privacyLists.add(privacyList.asDefault());
                }
                if (privacyList.getName() != null && privacyList.getName().equals(privacy.getActiveName())) {
                    privacyLists.add(privacyList.asActive());
                }
            }
            return privacyLists;
        });
    }

    /**
     * Gets a privacy list.
     *
     * @param name The privacy list name.
     * @return The async result with the privacy list.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-retrieve">2.3 Retrieving One's Privacy Lists</a>
     */
    public AsyncResult<PrivacyList> getPrivacyList(String name) {
        return xmppSession.query(IQ.get(new Privacy(new PrivacyList(name)))).thenApply(result -> {
            Privacy privacy = result.getExtension(Privacy.class);
            if (privacy != null) {
                return privacy.getPrivacyLists().get(0);
            }
            return null;
        });
    }

    /**
     * Changes the active list currently being applied.
     *
     * @param name The active list name.
     * @return The async result with the server's IQ reply.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-active">2.4 Managing Active Lists</a>
     */
    public AsyncResult<IQ> setActiveList(String name) {
        return setPrivacy(Privacy.withActive(name));
    }

    /**
     * Declines the use of any active list.
     *
     * @return The async result with the server's IQ reply.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-active">2.4 Managing Active Lists</a>
     */
    public AsyncResult<IQ> declineActiveList() {
        return setActiveList("");
    }

    /**
     * Change the default list (which applies to the user as a whole, not only the sending resource).
     *
     * @param name The list name.
     * @return The async result with the server's IQ reply.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-default">2.5 Managing the Default List</a>
     */
    public AsyncResult<IQ> setDefaultList(String name) {
        return setPrivacy(Privacy.withDefault(name));
    }

    /**
     * Declines the use of any default list.
     *
     * @return The async result with the server's IQ reply.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-default">2.5 Managing the Default List</a>
     */
    public AsyncResult<IQ> declineDefaultList() {
        return setDefaultList("");
    }

    /**
     * Creates or edits a privacy list.
     *
     * @param privacyList The privacy list.
     * @return The async result with the server's IQ reply.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-edit">2.6 Editing a Privacy List</a>
     */
    public AsyncResult<IQ> createOrUpdateList(PrivacyList privacyList) {
        return setPrivacy(new Privacy(privacyList));
    }

    /**
     * Removes a privacy list.
     *
     * @param name The privacy list.
     * @return The async result with the server's IQ reply.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-remove">2.8 Removing a Privacy List</a>
     */
    public AsyncResult<IQ> removeList(String name) {
        return setPrivacy(new Privacy(new PrivacyList(name)));
    }

    private AsyncResult<IQ> setPrivacy(Privacy privacy) {
        return xmppSession.query(IQ.set(privacy));
    }

    @Override
    protected void dispose() {
        privacyListListeners.clear();
    }
}
