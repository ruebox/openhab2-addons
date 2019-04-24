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
 * A debugger interface, which allows to implement custom debugger implementation to log XMPP traffic.
 *
 * @author Christian Schudt
 */
public interface XmppDebugger {

    /**
     * This method is called when a new XMPP session is initialized.
     *
     * @param xmppSession The XMPP session.
     */
    void initialize(XmppSession xmppSession);

    /**
     * This method is called, whenever a stream element is written.
     *
     * @param xml           The xml representation of the stream element.
     * @param streamElement The stream element. Maybe null, if no stream element, but an opening or closing stream element is written.
     */
    void writeStanza(String xml, Object streamElement);

    /**
     * This method is called, whenever a stream element is read.
     *
     * @param xml           The xml representation of the stream element.
     * @param streamElement The stream element. Maybe null, if no stream element, but an opening or closing stream element is read.
     */
    void readStanza(String xml, Object streamElement);

    /**
     * Creates a new output stream from the actual output stream. This is useful is you want to log the actually written bytes.
     * In this case you could fork the output stream and return the new forked stream.
     *
     * @param outputStream The actual output stream.
     * @return The (forked) output stream.
     */
    OutputStream createOutputStream(OutputStream outputStream);

    /**
     * Creates a new input stream from the actual input stream. This is useful is you want to log the actually read bytes.
     * In this case you could fork the input stream and return the new forked stream.
     *
     * @param inputStream The actual input stream.
     * @return The (forked) input stream.
     */
    InputStream createInputStream(InputStream inputStream);
}
