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

package rocks.xmpp.extensions.httpauth;

import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.AbstractIQHandler;
import rocks.xmpp.core.stanza.IQHandler;
import rocks.xmpp.core.stanza.MessageEvent;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.extensions.httpauth.model.ConfirmationRequest;
import rocks.xmpp.util.XmppUtils;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * This manager allows to listen for inbound requests (by an XMPP server) to confirm that the current XMPP user made an HTTP request, i.e. to verify that the HTTP request was in fact made by the XMPP user.
 * <p>
 * If you want to confirm or deny HTTP requests, {@linkplain #addHttpAuthenticationListener(Consumer) add a listener} and call {@link HttpAuthenticationEvent#confirm()} or {@link HttpAuthenticationEvent#deny()} on the event object.
 * </p>
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0070.html">XEP-0070: Verifying HTTP Requests via XMPP</a>
 */
public final class HttpAuthenticationManager extends Manager {

    private final Set<Consumer<HttpAuthenticationEvent>> httpAuthenticationListeners = new CopyOnWriteArraySet<>();

    private final IQHandler iqHandler;

    private final Consumer<MessageEvent> inboundMessageListener;

    private HttpAuthenticationManager(XmppSession xmppSession) {
        // TODO: Include namespace here for Service Discovery? (no mentioning in XEP-0070)
        super(xmppSession, true);

        iqHandler = new AbstractIQHandler(IQ.Type.GET) {
            @Override
            protected IQ processRequest(IQ iq) {
                ConfirmationRequest confirmationRequest = iq.getExtension(ConfirmationRequest.class);
                XmppUtils.notifyEventListeners(httpAuthenticationListeners, new HttpAuthenticationEvent(HttpAuthenticationManager.this, xmppSession, iq, confirmationRequest));
                return httpAuthenticationListeners.isEmpty() ? iq.createError(Condition.SERVICE_UNAVAILABLE) : null;
            }
        };

        inboundMessageListener = e -> {
            Message message = e.getMessage();
            if (message.getType() == null || message.getType() == Message.Type.NORMAL) {
                ConfirmationRequest confirmationRequest = message.getExtension(ConfirmationRequest.class);
                if (confirmationRequest != null) {
                    XmppUtils.notifyEventListeners(httpAuthenticationListeners, new HttpAuthenticationEvent(HttpAuthenticationManager.this, xmppSession, message, confirmationRequest));
                }
            }
        };
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        xmppSession.addIQHandler(ConfirmationRequest.class, iqHandler);
        xmppSession.addInboundMessageListener(inboundMessageListener);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        xmppSession.removeIQHandler(ConfirmationRequest.class);
        xmppSession.removeInboundMessageListener(inboundMessageListener);
    }

    /**
     * Adds a HTTP authentication listener, which allows to listen for HTTP authentication confirmation requests.
     *
     * @param httpAuthenticationListener The listener.
     * @see #removeHttpAuthenticationListener(Consumer)
     */
    public void addHttpAuthenticationListener(Consumer<HttpAuthenticationEvent> httpAuthenticationListener) {
        httpAuthenticationListeners.add(httpAuthenticationListener);
    }

    /**
     * Removes a previously added HTTP authentication listener.
     *
     * @param httpAuthenticationListener The listener.
     * @see #addHttpAuthenticationListener(Consumer)
     */
    public void removeHttpAuthenticationListener(Consumer<HttpAuthenticationEvent> httpAuthenticationListener) {
        httpAuthenticationListeners.remove(httpAuthenticationListener);
    }

    @Override
    protected void dispose() {
        httpAuthenticationListeners.clear();
    }
}
