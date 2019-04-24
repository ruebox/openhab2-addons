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

package rocks.xmpp.extensions.jingle.transports.s5b;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.extensions.bytestreams.s5b.Socks5ByteStreamManager;
import rocks.xmpp.extensions.bytestreams.s5b.model.Socks5ByteStream;
import rocks.xmpp.extensions.bytestreams.s5b.model.StreamHost;
import rocks.xmpp.extensions.jingle.JingleSession;
import rocks.xmpp.extensions.jingle.transports.TransportNegotiator;
import rocks.xmpp.extensions.jingle.transports.s5b.model.S5bTransportMethod;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Christian Schudt
 */
public final class S5bTransportNegotiator extends TransportNegotiator<S5bTransportMethod> {

    private final Socks5ByteStreamManager socks5ByteStreamManager;

    private final XmppSession xmppSession;

    protected S5bTransportNegotiator(JingleSession jingleSession, XmppSession xmppSession) {
        super(jingleSession);
        this.xmppSession = xmppSession;
        this.socks5ByteStreamManager = xmppSession.getManager(Socks5ByteStreamManager.class);
    }


    private AsyncResult<S5bTransportMethod> createTransport(Jid requester, Jid target) {

        return socks5ByteStreamManager.getAvailableStreamHosts().thenApply(streamHosts -> {
            String sessionId = UUID.randomUUID().toString();
            // Create the hash, which will identify the socket connection.
            final String hash = Socks5ByteStream.hash(sessionId, requester, target);
            List<S5bTransportMethod.Candidate> candidateList = new ArrayList<>();

            int localPriority = 0;
            for (StreamHost streamHost : streamHosts) {
                S5bTransportMethod.Candidate.Type type = streamHost.getJid().equals(xmppSession.getConnectedResource()) ? S5bTransportMethod.Candidate.Type.DIRECT : S5bTransportMethod.Candidate.Type.PROXY;
                candidateList.add(new S5bTransportMethod.Candidate(UUID.randomUUID().toString(), streamHost.getHostname(), streamHost.getPort(), streamHost.getJid(), type, S5bTransportMethod.calculatePriority(type, localPriority++)));
            }
            return new S5bTransportMethod(sessionId, hash, Socks5ByteStream.Mode.TCP, candidateList);
        });
    }


}
