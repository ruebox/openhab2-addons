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

package rocks.xmpp.extensions.si;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.StanzaError;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.extensions.si.model.StreamInitiation;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class StreamInitiationTest extends XmlTest {
    protected StreamInitiationTest() throws JAXBException {
        super(ClientIQ.class, StreamInitiation.class);
    }

    @Test
    public void unmarshalStreamInitiation() throws XMLStreamException, JAXBException {
        String xml = "<iq type='result' to='sender@jabber.org/resource' id='offer1'>\n" +
                "  <si xmlns='http://jabber.org/protocol/si'>\n" +
                "    <feature xmlns='http://jabber.org/protocol/feature-neg'>\n" +
                "      <x xmlns='jabber:x:data' type='submit'>\n" +
                "        <field var='stream-method'>\n" +
                "          <value>http://jabber.org/protocol/bytestreams</value>\n" +
                "        </field>\n" +
                "      </x>\n" +
                "    </feature>\n" +
                "  </si>\n" +
                "</iq>";

        IQ iq = unmarshal(xml, IQ.class);
        StreamInitiation streamInitiation = iq.getExtension(StreamInitiation.class);
        Assert.assertNotNull(streamInitiation);
        Assert.assertNotNull(streamInitiation.getFeatureNegotiation());
    }

    @Test
    public void marshalBadProfile() throws JAXBException, XMLStreamException {
        IQ result = new IQ(null, IQ.Type.ERROR, null, "1", null, null, new StanzaError(Condition.BAD_REQUEST, StreamInitiation.BAD_PROFILE));
        String xml = marshal(result);
        Assert.assertEquals(xml, "<iq id=\"1\" type=\"error\"><error type=\"modify\"><bad-request xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"></bad-request><bad-profile xmlns=\"http://jabber.org/protocol/si\"></bad-profile></error></iq>");

    }

    @Test
    public void marshalNoValidStreams() throws JAXBException, XMLStreamException {
        IQ result = new IQ(null, IQ.Type.ERROR, null, "1", null, null, new StanzaError(Condition.BAD_REQUEST, StreamInitiation.NO_VALID_STREAMS));
        String xml = marshal(result);
        Assert.assertEquals(xml, "<iq id=\"1\" type=\"error\"><error type=\"modify\"><bad-request xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"></bad-request><no-valid-streams xmlns=\"http://jabber.org/protocol/si\"></no-valid-streams></error></iq>");
    }
}
