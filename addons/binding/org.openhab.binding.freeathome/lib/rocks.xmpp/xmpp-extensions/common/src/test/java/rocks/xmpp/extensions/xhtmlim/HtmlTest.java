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

package rocks.xmpp.extensions.xhtmlim;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.xhtmlim.model.Html;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class HtmlTest extends XmlTest {
    protected HtmlTest() throws JAXBException {
        super(ClientIQ.class, Html.class);
    }

    @Test
    public void unmarshalHtml() throws XMLStreamException, JAXBException {
        String xml = "<html xmlns='http://jabber.org/protocol/xhtml-im'>\n" +
                "    <body xmlns='http://www.w3.org/1999/xhtml'><p style='font-weight:bold'>hi!</p><p style='font-weight:bold'>hi!</p></body>\n" +
                "  </html>\n";
        Html html = unmarshal(xml, Html.class);
        Assert.assertNotNull(html.getBody());
        Assert.assertEquals(html.getBody().getChildNodes().getLength(), 2);
        Assert.assertEquals(html.getContent(), "<p style=\"font-weight:bold\">hi!</p><p style=\"font-weight:bold\">hi!</p>");
    }

    @Test
    public void marshalHtmlWithDocument() throws JAXBException, XMLStreamException, ParserConfigurationException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.newDocument();

        Html html = new Html(doc);
        Element body = html.getBody();
        Element p = doc.createElement("p");
        p.setAttribute("style", "font-weight:bold");
        p.setTextContent("Hi1");
        Element p2 = doc.createElement("p");
        p2.setAttribute("style", "font-weight:bold");
        p2.setTextContent("Hi2");
        body.appendChild(p);
        body.appendChild(p2);

        String xml = marshal(html);
        String htmlString = html.getContent();
        Assert.assertEquals(htmlString, "<p style=\"font-weight:bold\">Hi1</p><p style=\"font-weight:bold\">Hi2</p>");
        Assert.assertEquals(xml, "<html xmlns=\"http://jabber.org/protocol/xhtml-im\"><body xmlns=\"http://www.w3.org/1999/xhtml\"><p style=\"font-weight:bold\">Hi1</p><p style=\"font-weight:bold\">Hi2</p></body></html>");
    }

    @Test
    public void marshalHtmlWithPlainText() throws JAXBException, XMLStreamException, SAXException {
        Html html = new Html("<p>test</p><p>test2</p>");
        String xml = marshal(html);
        Assert.assertEquals(html.getContent(), "<p>test</p><p>test2</p>");
        Assert.assertEquals(xml, "<html xmlns=\"http://jabber.org/protocol/xhtml-im\"><body xmlns=\"http://www.w3.org/1999/xhtml\"><p>test</p><p>test2</p></body></html>");
    }
}
