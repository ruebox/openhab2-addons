/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Christian Schudt
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

package rocks.xmpp.core;

import org.testng.annotations.BeforeClass;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.TestXmppSession;
import rocks.xmpp.core.session.XmppSession;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * @author Christian Schudt
 */
public class BaseTest {

    protected static final Jid JULIET = Jid.of("juliet@example.com/resource");

    protected static final Jid ROMEO = Jid.of("romeo@example.com/resource");

    protected final XmppSession xmppSession;

    protected Marshaller marshaller;

    protected Unmarshaller unmarshaller;

    public BaseTest() {
        xmppSession = new TestXmppSession();
    }

    @BeforeClass
    public void setupMarshaller() {
        marshaller = xmppSession.createMarshaller();
        unmarshaller = xmppSession.createUnmarshaller();
    }
}
