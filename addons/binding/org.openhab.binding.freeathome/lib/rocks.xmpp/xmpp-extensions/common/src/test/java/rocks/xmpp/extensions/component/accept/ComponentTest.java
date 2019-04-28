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

package rocks.xmpp.extensions.component.accept;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.component.accept.model.ComponentMessage;
import rocks.xmpp.extensions.component.accept.model.Handshake;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class ComponentTest extends XmlTest {
    protected ComponentTest() throws JAXBException {
        super("jabber:component:accept", ComponentMessage.class, Handshake.class);
    }

    @Test
    public void unmarshalMessage() throws XMLStreamException, JAXBException {
        String xml = "<message\n" +
                "    from='romeo@montague.net/orchard'\n" +
                "    to='juliet@capulet.com'\n" +
                "    type='chat'>\n" +
                "  <body>\n" +
                "    O blessed, blessed night! I am afeard.\n" +
                "    Being in night, all this is but a dream,\n" +
                "    Too flattering-sweet to be substantial.\n" +
                "  </body>\n" +
                "</message>\n";

        ComponentMessage message = unmarshal(xml, ComponentMessage.class);
        Assert.assertNotNull(message);
    }

    @Test
    public void unmarshalHandshake() throws XMLStreamException, JAXBException {
        String xml = "<handshake>aaee83c26aeeafcbabeabfcbcd50df997e0a2a1e</handshake>";

        Handshake handshake = unmarshal(xml, Handshake.class);
        Assert.assertNotNull(handshake);
        Assert.assertEquals(handshake.getValue(), "aaee83c26aeeafcbabeabfcbcd50df997e0a2a1e");
    }

    @Test
    public void marshalHandshake() throws XMLStreamException, JAXBException {
        Handshake handshake = Handshake.create("3BF96D32", "test");
        String xml = marshal(handshake);
        // Same as in XEP-0114 Example 3. ;-)
        Assert.assertEquals(xml, "<handshake>aaee83c26aeeafcbabeabfcbcd50df997e0a2a1e</handshake>");
    }
}
