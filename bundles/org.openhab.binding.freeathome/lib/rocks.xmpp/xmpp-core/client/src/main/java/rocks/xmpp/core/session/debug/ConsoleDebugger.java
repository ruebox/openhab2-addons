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

package rocks.xmpp.core.session.debug;

import rocks.xmpp.core.session.XmppSession;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A simple debugger implementation, which uses {@code System.out} to print XMPP traffic.
 *
 * @author Christian Schudt
 * @see rocks.xmpp.core.session.XmppSessionConfiguration.Builder#debugger(Class)
 */
public final class ConsoleDebugger implements XmppDebugger {

    private XmppSession xmppSession;

    @Override
    public void initialize(XmppSession xmppSession) {
        this.xmppSession = xmppSession;
    }

    @Override
    public void writeStanza(String xml, Object stanza) {
        System.out.println("OUT (" + (xmppSession.getActiveConnection() != null ? xmppSession.getActiveConnection().getStreamId() : "") + "): " + xml);
    }

    @Override
    public void readStanza(String xml, Object stanza) {
        System.out.println("IN  (" + (xmppSession.getActiveConnection() != null ? xmppSession.getActiveConnection().getStreamId() : "") + "): " + xml);
    }

    @Override
    public OutputStream createOutputStream(OutputStream outputStream) {
        return outputStream;
    }

    @Override
    public InputStream createInputStream(InputStream inputStream) {
        return inputStream;
    }
}
