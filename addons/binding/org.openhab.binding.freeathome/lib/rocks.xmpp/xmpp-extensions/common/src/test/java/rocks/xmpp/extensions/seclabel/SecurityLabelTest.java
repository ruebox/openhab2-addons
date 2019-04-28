/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Christian Schudt
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

package rocks.xmpp.extensions.seclabel;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.core.stanza.model.client.ClientMessage;
import rocks.xmpp.extensions.seclabel.model.SecurityLabel;
import rocks.xmpp.extensions.seclabel.model.catalog.Catalog;
import rocks.xmpp.extensions.seclabel.model.ess.EssSecurityLabel;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class SecurityLabelTest extends XmlTest {

    protected SecurityLabelTest() throws JAXBException {
        super(ClientIQ.class, ClientMessage.class, SecurityLabel.class);
    }

    @Test
    public void unmarshalSecurityLabel() throws XMLStreamException, JAXBException {
        String xml = "<message to='romeo@example.net' from='juliet@example.com/balcony'>\n" +
                "    <body>This content is classified.</body>\n" +
                "    <securitylabel xmlns='urn:xmpp:sec-label:0'>\n" +
                "        <displaymarking fgcolor='black' bgcolor='red'>SECRET</displaymarking>\n" +
                "        <label>\n" +
                "            <esssecuritylabel xmlns='urn:xmpp:sec-label:ess:0'\n" +
                "                >MQYCAQIGASk=</esssecuritylabel>\n" +
                "        </label>\n" +
                "        <equivalentlabel>\n" +
                "            <esssecuritylabel xmlns='urn:xmpp:sec-label:ess:0'\n" +
                "                >MRUCAgD9DA9BcXVhIChvYnNvbGV0ZSk=</esssecuritylabel>\n" +
                "        </equivalentlabel>\n" +
                "        <equivalentlabel>" +
                "            <esssecuritylabel xmlns='urn:xmpp:sec-label:ess:0'\n" +
                "                >MRUCAgD9DA9BcXVhIChvYnNvbGV0ZSk=</esssecuritylabel>\n" +
                "        </equivalentlabel>\n" +
                "    </securitylabel>\n" +
                "</message>\n";
        Message message = unmarshal(xml, Message.class);
        SecurityLabel securityLabel = message.getExtension(SecurityLabel.class);
        Assert.assertNotNull(securityLabel);
        Assert.assertNotNull(securityLabel.getDisplayMarking());
        Assert.assertEquals(securityLabel.getDisplayMarking().getForegroundColor(), "black");
        Assert.assertEquals(securityLabel.getDisplayMarking().getBackgroundColor(), "red");
        Assert.assertEquals(securityLabel.getDisplayMarking().getValue(), "SECRET");
        Assert.assertTrue(securityLabel.getLabel() instanceof EssSecurityLabel);
        Assert.assertEquals(securityLabel.getEquivalentLabels().size(), 2);
        Assert.assertTrue(securityLabel.getEquivalentLabels().get(0) instanceof EssSecurityLabel);
        Assert.assertTrue(securityLabel.getEquivalentLabels().get(1) instanceof EssSecurityLabel);
        Assert.assertNotNull(((EssSecurityLabel) securityLabel.getLabel()).getValue());
    }

    @Test
    public void unmarshalDefaultSecurityLabel() throws XMLStreamException, JAXBException {
        String xml = "<message to='romeo@example.net' from='juliet@example.com/balcony'>\n" +
                "    <body>This content is classified.</body>\n" +
                "    <securitylabel xmlns='urn:xmpp:sec-label:0'>\n" +
                "        <label>\n" +
                "        </label>\n" +
                "    </securitylabel>\n" +
                "</message>\n";
        Message message = unmarshal(xml, Message.class);
        SecurityLabel securityLabel = message.getExtension(SecurityLabel.class);
        Assert.assertNotNull(securityLabel);
        Assert.assertNull(securityLabel.getLabel());
    }

    @Test
    public void marshalDefaultSecurityLabel() throws XMLStreamException, JAXBException {
        SecurityLabel securityLabel = new SecurityLabel();
        String xml2 = marshal(securityLabel);
        Assert.assertEquals(xml2, "<securitylabel xmlns=\"urn:xmpp:sec-label:0\"><label></label></securitylabel>");
    }

    @Test
    public void marshalCatalogRequest() throws JAXBException, XMLStreamException {
        IQ iq = new IQ(IQ.Type.GET, new Catalog(), "id");
        String xml = marshal(iq);
        Assert.assertEquals(xml, "<iq id=\"id\" type=\"get\"><catalog xmlns=\"urn:xmpp:sec-label:catalog:2\"></catalog></iq>");
    }

    @Test
    public void unmarshalCatalogRequestResponse() throws JAXBException, XMLStreamException {
        String xml = "<iq type='result' to='user@example.com/Work' id='cat1'>\n" +
                "  <catalog xmlns='urn:xmpp:sec-label:catalog:2'\n" +
                "      to='example.com' name='Default'\n" +
                "      desc='an example set of labels'\n" +
                "          restrict='false'>\n" +
                "        <item selector=\"Classified|SECRET\">\n" +
                "            <securitylabel xmlns='urn:xmpp:sec-label:0'>\n" +
                "                <displaymarking fgcolor='black' bgcolor='red'>SECRET</displaymarking>\n" +
                "                <label>\n" +
                "                    <esssecuritylabel xmlns='urn:xmpp:sec-label:ess:0'\n" +
                "                        >MQYCAQQGASk=</esssecuritylabel>\n" +
                "                </label>\n" +
                "            </securitylabel>\n" +
                "        </item>\n" +
                "        <item selector=\"Classified|CONFIDENTIAL\">\n" +
                "            <securitylabel xmlns='urn:xmpp:sec-label:0'>\n" +
                "                <displaymarking fgcolor='black' bgcolor='navy'>CONFIDENTIAL</displaymarking>\n" +
                "                <label>\n" +
                "                    <esssecuritylabel xmlns='urn:xmpp:sec-label:ess:0'\n" +
                "                        >MQYCAQMGASk</esssecuritylabel>\n" +
                "                </label>\n" +
                "            </securitylabel>\n" +
                "        </item>\n" +
                "        <item selector=\"Classified|RESTRICTED\">\n" +
                "            <securitylabel xmlns='urn:xmpp:sec-label:0'>\n" +
                "                <displaymarking fgcolor='black' bgcolor='aqua'>RESTRICTED</displaymarking>\n" +
                "                <label>\n" +
                "                    <esssecuritylabel xmlns='urn:xmpp:sec-label:ess:0'\n" +
                "                        >MQYCAQIGASk=</esssecuritylabel>\n" +
                "                </label>\n" +
                "            </securitylabel>\n" +
                "        </item>\n" +
                "        <item selector=\"UNCLASSIFIED\" default=\"true\"/>\n" +
                "  </catalog>\n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        Catalog catalog = iq.getExtension(Catalog.class);
        Assert.assertNotNull(catalog);
        Assert.assertEquals(catalog.getTo(), Jid.of("example.com"));
        Assert.assertEquals(catalog.getName(), "Default");
        Assert.assertEquals(catalog.getDescription(), "an example set of labels");
        Assert.assertFalse(catalog.isRestrictive());
        Assert.assertEquals(catalog.getItems().size(), 4);
        Assert.assertNotNull(catalog.getItems().get(0).getSecurityLabel());
        Assert.assertEquals(catalog.getItems().get(0).getSelector(), "Classified|SECRET");
        Assert.assertEquals(catalog.getItems().get(0).getSecurityLabel().getDisplayMarking().getValue(), "SECRET");
    }
}
