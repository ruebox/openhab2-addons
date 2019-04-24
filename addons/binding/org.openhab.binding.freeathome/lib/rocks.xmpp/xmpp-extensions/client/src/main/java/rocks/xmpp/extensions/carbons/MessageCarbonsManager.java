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

package rocks.xmpp.extensions.carbons;

import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.extensions.carbons.model.MessageCarbons;
import rocks.xmpp.util.concurrent.AsyncResult;

/**
 * Manages message carbons. It allows you to {@linkplain #enableCarbons()} ()} enable} or {@linkplain #disableCarbons()} disable} message carbons on the server.
 * <p>
 * If successfully enabled, you should have the following behavior:
 * </p>
 * <ul>
 * <li>Chat messages sent to your bare jid, will be "forked" to your carbons-enabled session.</li>
 * <li>Chat messages sent to another of your available resources (i.e. to another full JID), will be copied to you with a {@link rocks.xmpp.extensions.carbons.model.MessageCarbons.Received} extension.</li>
 * <li>Chat messages you sent from another resource, will be copied to you with a {@link rocks.xmpp.extensions.carbons.model.MessageCarbons.Sent} extension.</li>
 * </ul>
 * This class is thread-safe.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0280.html">XEP-0280: Message Carbons</a>
 */
public final class MessageCarbonsManager extends Manager {

    private MessageCarbonsManager(XmppSession xmppSession) {
        super(xmppSession);
    }

    /**
     * Enables message carbons on the server for this session.
     *
     * @return The async result.
     */
    public final AsyncResult<IQ> enableCarbons() {
        return xmppSession.query(IQ.set(MessageCarbons.ENABLE));
    }

    /**
     * Disables message carbons on the server for this session.
     *
     * @return The async result.
     */
    public final AsyncResult<IQ> disableCarbons() {
        return xmppSession.query(IQ.set(MessageCarbons.DISABLE));
    }
}
