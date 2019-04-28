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

package rocks.xmpp.extensions.bytestreams;

import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * An abstract class to manage both <a href="https://xmpp.org/extensions/xep-0047.html">XEP-0047: In-Band Bytestreams</a> or <a href="https://xmpp.org/extensions/xep-0065.html">XEP-0065: SOCKS5 Bytestreams</a>.
 * <p>
 * This class is thread-safe.
 *
 * @author Christian Schudt
 */
public abstract class ByteStreamManager extends Manager {

    protected final Set<Consumer<ByteStreamEvent>> byteStreamListeners = new CopyOnWriteArraySet<>();

    protected ByteStreamManager(XmppSession xmppSession) {
        super(xmppSession, true);
    }

    /**
     * Adds a byte stream listener, which allows to listen for inbound byte stream requests.
     *
     * @param byteStreamListener The listener.
     * @see #removeByteStreamListener(Consumer)
     */
    public final void addByteStreamListener(Consumer<ByteStreamEvent> byteStreamListener) {
        byteStreamListeners.add(byteStreamListener);
    }

    /**
     * Removes a previously added byte stream listener.
     *
     * @param byteStreamListener The listener.
     * @see #addByteStreamListener(Consumer)
     */
    public final void removeByteStreamListener(Consumer<ByteStreamEvent> byteStreamListener) {
        byteStreamListeners.remove(byteStreamListener);
    }

    @Override
    protected void dispose() {
        byteStreamListeners.clear();
    }
}
