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

package rocks.xmpp.websocket;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.websocket.model.Close;
import rocks.xmpp.websocket.model.Open;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.net.URI;
import java.util.Locale;

/**
 * @author Christian Schudt
 */
public class WebSocketTest extends XmlTest {

    protected WebSocketTest() throws JAXBException {
        super(Open.class, Close.class);
    }

    @Test
    public void unmarshalOpen() throws XMLStreamException, JAXBException {
        Open open = unmarshal("<open xmlns=\"urn:ietf:params:xml:ns:xmpp-framing\" version=\"1.0\" to=\"test\" xml:lang=\"en\"></open>", Open.class);
        Assert.assertEquals(open.getLanguage(), Locale.ENGLISH);
        Assert.assertEquals(open.getTo(), Jid.ofDomain("test"));
    }

    @Test
    public void unmarshalClose() throws XMLStreamException, JAXBException {
        Close close = unmarshal("<close xmlns=\"urn:ietf:params:xml:ns:xmpp-framing\" see-other-uri=\"wss://otherendpoint.example/xmpp-bind\"></close>", Close.class);
        Assert.assertEquals(close.getUri(), URI.create("wss://otherendpoint.example/xmpp-bind"));
    }

    @Test
    public void marshalOpen() throws XMLStreamException, JAXBException {
        Open open = new Open(Jid.of("test"), Locale.ENGLISH);
        Assert.assertEquals(marshal(open), "<open xmlns=\"urn:ietf:params:xml:ns:xmpp-framing\" to=\"test\" xml:lang=\"en\" version=\"1.0\"></open>");
    }

    @Test
    public void marshalClose() throws XMLStreamException, JAXBException {
        Close close = new Close(URI.create("wss://otherendpoint.example/xmpp-bind"));
        Assert.assertEquals(marshal(close), "<close xmlns=\"urn:ietf:params:xml:ns:xmpp-framing\" see-other-uri=\"wss://otherendpoint.example/xmpp-bind\"></close>");
    }
}
