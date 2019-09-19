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

package rocks.xmpp.extensions.jingle.transports.ibb;

import rocks.xmpp.extensions.jingle.JingleSession;
import rocks.xmpp.extensions.jingle.transports.TransportNegotiator;
import rocks.xmpp.extensions.jingle.transports.ibb.model.InBandByteStreamsTransportMethod;
import rocks.xmpp.extensions.jingle.transports.model.TransportMethod;

/**
 * @author Christian Schudt
 */
public class IbbTransportNegotiator extends TransportNegotiator<InBandByteStreamsTransportMethod> {

    private TransportMethod currentTransport = null;

    IbbTransportNegotiator(JingleSession jingleSession) {
        super(jingleSession);
    }

    public boolean replaceTransport(InBandByteStreamsTransportMethod transport) {
        if (currentTransport == null) {
            throw new IllegalStateException("No transport has been set yet, therefore no transport can be replaced.");
        }

        if (transport.getBlockSize() <= 65535) {
            currentTransport = transport;
            return true;
        } else {
            return false;
        }
    }
}
