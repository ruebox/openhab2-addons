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

package rocks.xmpp.extensions.bytestreams.s5b;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.extensions.bytestreams.ByteStreamSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.time.Duration;

/**
 * @author Christian Schudt
 */
final class S5bSession extends ByteStreamSession {

    private final Jid streamHost;

    private final Socket socket;

    S5bSession(String sessionId, Socket socket, Jid streamHost, Duration readTimeout) throws SocketException {
        super(sessionId);
        this.socket = socket;
        this.socket.setSoTimeout((int) readTimeout.toMillis());
        this.streamHost = streamHost;
    }

    @Override
    public final OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    @Override
    public final InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    @Override
    public final void close() throws Exception {
        socket.close();
    }

    /**
     * Gets the used stream host for this session.
     *
     * @return The session.
     */
    public final Jid getStreamHost() {
        return streamHost;
    }

    @Override
    public final String toString() {
        return "SOCKS5 Bytestream Session: " + getSessionId();
    }
}
