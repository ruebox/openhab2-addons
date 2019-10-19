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

package rocks.xmpp.extensions.blocking;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.client.ClientMessage;
import rocks.xmpp.extensions.blocking.model.errors.Blocked;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class BlockingErrorsTest extends XmlTest {

    protected BlockingErrorsTest() throws JAXBException {
        super(ClientMessage.class, Blocked.class);
    }


    @Test
    public void unmarshalBlockedError() throws XMLStreamException, JAXBException {
        String xml = "<message type='error' from='romeo@montague.net' to='juliet@capulet.com'>\n" +
                "  <body>Can you hear me now?</body>\n" +
                "  <error type='cancel'>\n" +
                "    <not-acceptable xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <blocked xmlns='urn:xmpp:blocking:errors'/>\n" +
                "  </error>\n" +
                "</message>\n";
        Message message = unmarshal(xml, Message.class);
        Assert.assertTrue(message.getError().getExtension() == Blocked.INSTANCE);
    }
}
