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

package rocks.xmpp.extensions.bytestreams.ibb;

import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.StanzaError;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.extensions.bytestreams.ByteStreamEvent;
import rocks.xmpp.extensions.bytestreams.ByteStreamSession;
import rocks.xmpp.extensions.bytestreams.ibb.model.InBandByteStream;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.util.concurrent.CompletableFuture;

/**
 * @author Christian Schudt
 */
final class IbbEvent extends ByteStreamEvent {

    private final transient XmppSession xmppSession;

    private final transient IQ iq;

    private final int blockSize;

    private final InBandByteStream.Open.StanzaType stanzaType;

    IbbEvent(Object source, String sessionId, XmppSession xmppSession, IQ iq, int blockSize, InBandByteStream.Open.StanzaType stanzaType) {
        super(source, sessionId);
        this.xmppSession = xmppSession;
        this.iq = iq;
        this.blockSize = blockSize;
        this.stanzaType = stanzaType;
    }

    @Override
    public final AsyncResult<ByteStreamSession> accept() {
        xmppSession.send(iq.createResult());
        return new AsyncResult<>(CompletableFuture.completedFuture(xmppSession.getManager(InBandByteStreamManager.class).createSession(iq.getFrom(), getSessionId(), blockSize, stanzaType)));
    }

    @Override
    public final void reject() {
        // If the responder supports IBB but does not wish to proceed with the session, it returns a <not-acceptable/> error.
        xmppSession.send(iq.createError(new StanzaError(StanzaError.Type.CANCEL, Condition.NOT_ACCEPTABLE)));
    }
}
