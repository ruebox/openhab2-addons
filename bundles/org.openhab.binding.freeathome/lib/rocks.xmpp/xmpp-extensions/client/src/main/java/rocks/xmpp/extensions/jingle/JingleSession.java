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

package rocks.xmpp.extensions.jingle;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.extensions.jingle.model.Jingle;
import rocks.xmpp.extensions.jingle.transports.model.TransportMethod;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * @author Christian Schudt
 */
public final class JingleSession {

    final Set<Consumer<JingleEvent>> jingleListeners = new CopyOnWriteArraySet<>();

    private final String sessionId;

    private final XmppSession xmppSession;

    private final Jid peer;

    private final JingleManager jingleManager;

    private final boolean createdLocally;

    // See 7.2.2 content-add:
    // Therefore it is the responsibility of the recipient to maintain a local copy of the current content definition(s).
    private final List<Jingle.Content> contents;

    private State state = State.PENDING;

    JingleSession(String sessionId, Jid peer, boolean createdLocally, XmppSession xmppSession, JingleManager jingleManager, Jingle.Content... contents) {
        this(sessionId, peer, createdLocally, xmppSession, jingleManager, Arrays.asList(contents));
    }

    JingleSession(String sessionId, Jid peer, boolean createdLocally, XmppSession xmppSession, JingleManager jingleManager, List<Jingle.Content> contents) {
        this.sessionId = sessionId;
        this.xmppSession = xmppSession;
        this.peer = peer;
        this.jingleManager = jingleManager;
        this.createdLocally = createdLocally;
        this.contents = new ArrayList<>(contents);
    }

    /**
     * Gets the session id.
     *
     * @return The session id.
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Initiates the session.
     *
     * @return The async result.
     * @see <a href="https://xmpp.org/extensions/xep-0166.html#protocol-initiate">6.2 Initiation</a>
     * @see <a href="https://xmpp.org/extensions/xep-0166.html#def-action-session-initiate">7.2.10 session-initiate</a>
     */
    public AsyncResult<Void> initiate() {
        if (!createdLocally) {
            throw new UnsupportedOperationException("You are not the initiator.");
        }
        return xmppSession.query(IQ.set(peer, Jingle.initiator(xmppSession.getConnectedResource(), sessionId, Jingle.Action.SESSION_INITIATE, contents))).thenAccept(result ->
                state = State.PENDING);
    }

    /**
     * Accepts the session. You must at least provide one content element.
     *
     * @param contents The contents.
     * @return The async result.
     * @see <a href="https://xmpp.org/extensions/xep-0166.html#session-acceptance">6.5 Acceptance</a>
     * @see <a href="https://xmpp.org/extensions/xep-0166.html#def-action-session-accept">7.2.8 session-accept</a>
     */
    public AsyncResult<Void> accept(Jingle.Content... contents) {
        if (state != State.PENDING) {
            throw new IllegalStateException("The session is not in pending state.");
        }
        if (createdLocally) {
            throw new UnsupportedOperationException("You are the initiator and cannot accept the session.");
        }
        // In the session-accept stanza, the <jingle/> element MUST contain one or more <content/> elements, each of which MUST contain one <description/> element and one <transport/> element.
        if (contents.length == 0) {
            throw new IllegalArgumentException("No content element provided.");
        }
        for (Jingle.Content content : contents) {
            if (content.getApplicationFormat() == null) {
                throw new IllegalArgumentException("No application format provided in content.");
            }
            if (content.getTransportMethod() == null) {
                throw new IllegalArgumentException("No transport method provided in content.");
            }
        }

        return xmppSession.query(IQ.set(peer, Jingle.responder(xmppSession.getConnectedResource(), sessionId, Jingle.Action.SESSION_ACCEPT, Arrays.asList(contents)))).thenAccept(result -> {
            // The session is now in the ACTIVE state.
            state = State.ACTIVE;
        });
    }

    /**
     * Terminates the Jingle session.
     *
     * @param reason The reason for termination.
     * @return The async result.
     * @see <a href="https://xmpp.org/extensions/xep-0166.html#session-terminate">6.7 Termination</a>
     */
    public AsyncResult<IQ> terminate(Jingle.Reason reason) {
        // As soon as an entity sends a session-terminate action, it MUST consider the session to be in the ENDED state
        // (even before receiving acknowledgement from the other party).
        state = State.ENDED;
        try {
            return xmppSession.query(IQ.set(peer, new Jingle(sessionId, Jingle.Action.SESSION_TERMINATE, reason)));
        } finally {
            jingleManager.removeSession(sessionId);
        }
    }

    /**
     * @param contentName     The content name.
     * @param transportMethod The replaced transport method.
     * @return The async result.
     * @see <a href="https://xmpp.org/extensions/xep-0166.html#def-action-transport-replace">7.2.15 transport-replace</a>
     */
    public AsyncResult<IQ> replaceTransport(String contentName, TransportMethod transportMethod) {
        Jingle.Content content = new Jingle.Content(contentName, Jingle.Content.Creator.INITIATOR, null, transportMethod);
        return xmppSession.query(IQ.set(peer, Jingle.initiator(xmppSession.getConnectedResource(), sessionId, Jingle.Action.TRANSPORT_REPLACE, Collections.singletonList(content))));
    }

    public AsyncResult<IQ> acceptTransport(String contentName, TransportMethod transportMethod) {
        Jingle.Content content = new Jingle.Content(contentName, Jingle.Content.Creator.INITIATOR, null, transportMethod);
        return xmppSession.query(IQ.set(peer, Jingle.initiator(xmppSession.getConnectedResource(), sessionId, Jingle.Action.TRANSPORT_ACCEPT, Collections.singletonList(content))));
    }

    public AsyncResult<IQ> rejectTransport(String contentName, TransportMethod transportMethod) {
        Jingle.Content content = new Jingle.Content(contentName, Jingle.Content.Creator.INITIATOR, null, transportMethod);
        return xmppSession.query(IQ.set(peer, Jingle.initiator(xmppSession.getConnectedResource(), sessionId, Jingle.Action.TRANSPORT_REJECT, Collections.singletonList(content))));
    }

    public List<Jingle.Content> getContents() {
        return Collections.unmodifiableList(contents);
    }

    /**
     * Sends a session info.
     *
     * @param object The session info payload.
     * @return The async result.
     * @see <a href="https://xmpp.org/extensions/xep-0166.html#def-action-session-info">7.2.9 session-info</a>
     */
    public AsyncResult<IQ> sendSessionInfo(Object object) {
        return xmppSession.query(IQ.set(peer, new Jingle(sessionId, Jingle.Action.SESSION_INFO, object)));
    }

    /**
     * Adds a Jingle listener, which allows to listen for Jingle events.
     *
     * @param jingleListener The listener.
     * @see #removeJingleListener(Consumer)
     */
    public final void addJingleListener(Consumer<JingleEvent> jingleListener) {
        jingleListeners.add(jingleListener);
    }

    /**
     * Removes a previously added Jingle listener.
     *
     * @param jingleListener The listener.
     * @see #addJingleListener(Consumer)
     */
    public final void removeJingleListener(Consumer<JingleEvent> jingleListener) {
        jingleListeners.remove(jingleListener);
    }

    /**
     * Represents the state of a Jingle session.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0166.html#concepts-session">5.1 Overall Session Management</a>
     */
    public enum State {
        /**
         * The session has been initiated, but not yet accepted.
         */
        PENDING,
        /**
         * The session has been accepted.
         */
        ACTIVE,
        /**
         * The session has been ended.
         */
        ENDED
    }
}
