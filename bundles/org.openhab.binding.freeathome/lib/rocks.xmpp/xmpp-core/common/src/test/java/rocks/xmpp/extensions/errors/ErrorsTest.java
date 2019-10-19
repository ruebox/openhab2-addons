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

package rocks.xmpp.extensions.errors;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.StanzaError;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.core.stanza.model.client.ClientMessage;
import rocks.xmpp.extensions.errors.model.ResourceLimitExceeded;
import rocks.xmpp.extensions.errors.model.StanzaTooBig;
import rocks.xmpp.extensions.errors.model.TooManyStanzas;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class ErrorsTest extends XmlTest {
    protected ErrorsTest() throws JAXBException {
        super(ClientIQ.class, ClientMessage.class, StanzaTooBig.class, TooManyStanzas.class, ResourceLimitExceeded.class);
    }

    @Test
    public void unmarshalStanzaTooBig() throws JAXBException, XMLStreamException {

        String xml = "<message from='shakespeare.lit' to='iago@shakespare.lit/evilos'>\n" +
                "  <error type='modify'>\n" +
                "    <not-allowed xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <stanza-too-big xmlns='urn:xmpp:errors'/>\n" +
                "  </error>\n" +
                "</message>";
        Message message = unmarshal(xml, Message.class);
        StanzaError stanzaError = message.getError();
        Assert.assertTrue(stanzaError.getExtension() == StanzaTooBig.INSTANCE);
    }

    @Test
    public void unmarshalTooManyStanzas() throws JAXBException, XMLStreamException {

        String xml = "<message from='iago@shakespeare.lit/evilos' to='juliet@capulet.lit'>\n" +
                "  <error type='wait'>\n" +
                "    <unexpected-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <too-many-stanzas xmlns='urn:xmpp:errors'/>\n" +
                "  </error>\n" +
                "</message>";
        Message message = unmarshal(xml, Message.class);
        StanzaError stanzaError = message.getError();
        Assert.assertTrue(stanzaError.getExtension() == TooManyStanzas.INSTANCE);
    }

    @Test
    public void unmarshalResourceLimitExceeded() throws JAXBException, XMLStreamException {

        String xml = "<iq type='error' id='bind_2'>\n" +
                "  <bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'>\n" +
                "    <resource>someresource</resource>\n" +
                "  </bind>\n" +
                "  <error type='cancel'>\n" +
                "    <resource-constraint xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <resource-limit-exceeded xmlns='urn:xmpp:errors'/>\n" +
                "  </error>\n" +
                "</iq>";
        IQ iq = unmarshal(xml, IQ.class);
        StanzaError stanzaError = iq.getError();
        Assert.assertTrue(stanzaError.getExtension() == ResourceLimitExceeded.INSTANCE);
    }
}
