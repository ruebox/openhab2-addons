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

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.extensions.bytestreams.ByteStreamSession;
import rocks.xmpp.extensions.bytestreams.ibb.model.InBandByteStream;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.concurrent.Future;

/**
 * @author Christian Schudt
 */
final class IbbSession extends ByteStreamSession {

    private final IbbOutputStream outputStream;

    private final IbbInputStream inputStream;

    private final Jid jid;

    private final int blockSize;

    private final XmppSession xmppSession;

    private final InBandByteStreamManager inBandByteStreamManager;

    private final InBandByteStream.Open.StanzaType stanzaType;

    /**
     * Guarded by "this"
     */
    private int inboundSequence = 0;

    /**
     * Guarded by "outputStream"
     */
    private int outboundSequence = 0;

    /**
     * Guarded by "this"
     */
    private boolean closed;

    IbbSession(String sessionId, XmppSession xmppSession, Jid jid, int blockSize, Duration readTimeout, InBandByteStreamManager manager, InBandByteStream.Open.StanzaType stanzaType) {
        super(sessionId);
        this.outputStream = new IbbOutputStream(this, blockSize);
        this.inputStream = new IbbInputStream(this, readTimeout.toMillis());
        this.jid = jid;
        this.xmppSession = xmppSession;
        this.blockSize = blockSize;
        this.inBandByteStreamManager = manager;
        this.stanzaType = stanzaType;
    }

    final synchronized boolean dataReceived(InBandByteStream.Data data) {
        return inboundSequence++ == data.getSequence() && inputStream.queue.offer(data);
    }

    final AsyncResult<IQ> open() {
        return xmppSession.query(IQ.set(jid, new InBandByteStream.Open(blockSize, getSessionId(), stanzaType)));
    }

    @Override
    public final synchronized OutputStream getOutputStream() throws IOException {
        if (closed) {
            throw new IOException("IBB session is closed.");
        }
        return outputStream;
    }

    @Override
    public final synchronized InputStream getInputStream() throws IOException {
        if (closed) {
            throw new IOException("IBB session is closed.");
        }
        return inputStream;
    }

    final Future<?> send(byte[] bytes) {

        final Future<?> result;
        final InBandByteStream.Data data = new InBandByteStream.Data(bytes, getSessionId(), outboundSequence);
        if (stanzaType == InBandByteStream.Open.StanzaType.MESSAGE) {
            Message message = new Message(jid);
            message.addExtension(data);
            result = xmppSession.sendMessage(message);
        } else {
            result = xmppSession.query(IQ.set(jid, data));
        }

        // The 'seq' value starts at 0 (zero) for each sender and MUST be incremented for each packet sent by that entity. Thus, the second chunk sent has a 'seq' value of 1, the third chunk has a 'seq' value of 2, and so on. The counter loops at maximum, so that after value 65535 (215 - 1) the 'seq' MUST start again at 0.
        if (++outboundSequence > 65535) {
            outboundSequence = 0;
        }
        return result;
    }

    @Override
    public final void close() throws IOException {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        try {
            inputStream.close();
            outputStream.close();
            xmppSession.send(IQ.set(jid, new InBandByteStream.Close(getSessionId())));
        } finally {
            // the party that sent the original <close/> element SHOULD wait to receive the IQ response from the receiving party before considering the bytestream to be closed.
            // Remove this session from the map.
            inBandByteStreamManager.ibbSessionMap.remove(getSessionId());
        }
    }

    final void closedByPeer() throws IOException {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        inputStream.close();
        outputStream.close();
    }

    @Override
    public final String toString() {
        return "In-Band Bytestream Session: " + getSessionId();
    }
}
