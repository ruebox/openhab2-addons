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
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.AbstractIQHandler;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.StanzaError;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.extensions.jingle.apps.model.ApplicationFormat;
import rocks.xmpp.extensions.jingle.model.Jingle;
import rocks.xmpp.extensions.jingle.model.errors.UnknownSession;
import rocks.xmpp.util.XmppUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

/**
 * @author Christian Schudt
 */
public final class JingleManager extends Manager {

    private final Set<Consumer<JingleEvent>> jingleListeners = new CopyOnWriteArraySet<>();

    private final Map<String, JingleSession> jingleSessionMap = new ConcurrentHashMap<>();

    private final Map<Class<? extends ApplicationFormat>, Consumer<JingleSession>> registeredApplicationFormats = new ConcurrentHashMap<>();

    private JingleManager(final XmppSession xmppSession) {
        super(xmppSession, true);
    }

    @Override
    protected void initialize() {
        xmppSession.addIQHandler(Jingle.class, new AbstractIQHandler(IQ.Type.SET) {
            @Override
            public IQ processRequest(IQ iq) {
                final Jingle jingle = iq.getExtension(Jingle.class);
                final JingleSession jingleSession;

                // The value of the 'action' attribute MUST be one of the following.
                // If an entity receives a value not defined here, it MUST ignore the attribute and MUST return a <bad-request/> error to the sender.
                // There is no default value for the 'action' attribute.
                if (jingle.getAction() == null) {
                    return iq.createError(new StanzaError(Condition.BAD_REQUEST, "No valid action attribute set."));
                } else if (jingle.getSessionId() == null) {
                    return iq.createError(new StanzaError(Condition.BAD_REQUEST, "No session id set."));
                } else if (jingle.getAction() == Jingle.Action.SESSION_INITIATE) {


                    // Check if the Jingle request is not mal-formed, otherwise return a bad-request error.
                    // See 6.3.2 Errors
                    if (jingle.getContents().isEmpty()) {
                        return iq.createError(new StanzaError(Condition.BAD_REQUEST, "No contents found."));
                    } else {
                        boolean hasContentWithDispositionSession = false;
                        boolean hasSupportedApplications = false;
                        boolean hasSupportedTransports = false;
                        // Check if we support the application format and transport method and at least one content element has a disposition of "session".
                        for (Jingle.Content content : jingle.getContents()) {
                            // Check if the content disposition is "session" (default value is "session").
                            if (!hasContentWithDispositionSession && ("session".equals(content.getDisposition()) || content.getDisposition() == null)) {
                                hasContentWithDispositionSession = true;
                            }

                            if (!hasSupportedApplications && content.getApplicationFormat() != null) {
                                hasSupportedApplications = true;
                            }

                            if (!hasSupportedTransports && content.getTransportMethod() != null) {
                                hasSupportedTransports = true;
                            }
                        }

                        if (!hasContentWithDispositionSession) {
                            // When sending a session-initiate with one <content/> element,
                            // the value of the <content/> element's 'disposition' attribute MUST be "session"
                            // (if there are multiple <content/> elements then at least one MUST have a disposition of "session");
                            // if this rule is violated, the responder MUST return a <bad-request/> error to the initiator.
                            return iq.createError(new StanzaError(Condition.BAD_REQUEST, "No content with disposition 'session' found."));
                        } else {

                            // However, after acknowledging the session initiation request,
                            // the responder might subsequently determine that it cannot proceed with negotiation of the session
                            // (e.g., because it does not support any of the offered application formats or transport methods,
                            // because a human user is busy or unable to accept the session, because a human user wishes to formally decline
                            // the session, etc.). In these cases, the responder SHOULD immediately acknowledge the session initiation request
                            // but then terminate the session with an appropriate reason as described in the Termination section of this document.

                            if (!hasSupportedApplications) {
                                // Terminate the session with <unsupported-applications/>.
                                return IQ.set(iq.getFrom(), new Jingle(jingle.getSessionId(), Jingle.Action.SESSION_TERMINATE, new Jingle.Reason(new Jingle.Reason.UnsupportedApplications())));
                            } else if (!hasSupportedTransports) {
                                // Terminate the session with <unsupported-transports/>.
                                return IQ.set(iq.getFrom(), new Jingle(jingle.getSessionId(), Jingle.Action.SESSION_TERMINATE, new Jingle.Reason(new Jingle.Reason.UnsupportedTransports())));
                            } else {
                                // Everything is fine, create the session and notify the listeners.
                                jingleSession = new JingleSession(jingle.getSessionId(), iq.getFrom(), false, xmppSession, JingleManager.this, jingle.getContents());
                                jingleSessionMap.put(jingle.getSessionId(), jingleSession);
                                Consumer<JingleSession> consumer = registeredApplicationFormats.get(jingle.getContents().get(0).getApplicationFormat().getClass());
                                if (consumer != null) {
                                    consumer.accept(jingleSession);
                                }
                            }
                            // If the request was ok, immediately acknowledge the initiation request.
                            // See 6.3.1 Acknowledgement
                            //return iq.createResult();
                        }
                    }
                } else {
                    // Another action (!= session-initiate) has been sent.
                    // Check if we know the session.
                    jingleSession = jingleSessionMap.get(jingle.getSessionId());
                }

                if (jingleSession == null) {
                    // If we receive a non-session-initiate Jingle action with an unknown session id,
                    // return <item-not-found/> and <unknown-session/>
                    return iq.createError(new StanzaError(Condition.ITEM_NOT_FOUND, new UnknownSession()));
                } else {
                    ForkJoinPool.commonPool().execute(() -> XmppUtils.notifyEventListeners(jingleSession.jingleListeners, new JingleEvent(JingleManager.this, xmppSession, iq, jingle)));
                    // Immediately return a result, before sending a session-accept.
                    return iq.createResult();
                }
            }
        });
    }

    public JingleSession createSession(Jid responder, String sessionId, Jingle.Content... contents) {
        Objects.requireNonNull(responder, "responder must not be null.");
        if (contents.length == 0) {
            throw new IllegalArgumentException("no content provided.");
        }
        JingleSession jingleSession = new JingleSession(sessionId, responder, true, xmppSession, this, contents);

        // Register the session. It is now in pending state until it has been accepted or terminated.
        jingleSessionMap.put(sessionId, jingleSession);

        return jingleSession;
    }

    void removeSession(String sessionId) {
        jingleSessionMap.remove(sessionId);
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

    public final void registerApplicationFormat(Class<? extends ApplicationFormat> applicationFormat, Consumer<JingleSession> consumer) {
        registeredApplicationFormats.put(applicationFormat, consumer);
    }

    public final void unregisterApplicationFormat(Class<? extends ApplicationFormat> applicationFormat) {
        registeredApplicationFormats.remove(applicationFormat);
    }

    @Override
    protected void dispose() {
        jingleListeners.clear();
        jingleSessionMap.clear();
    }
}
