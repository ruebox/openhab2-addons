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

package rocks.xmpp.extensions.xhtmlim.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Objects;

/**
 * The implementation of the {@code <html/>} element in the {@code http://jabber.org/protocol/xhtml-im} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0071.html">XEP-0071: XHTML-IM</a>
 */
@XmlRootElement
public final class Html {

    /**
     * http://jabber.org/protocol/xhtml-im
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/xhtml-im";

    @XmlElement(name = "body", namespace = "http://www.w3.org/1999/xhtml")
    private final Object body;

    private Html() {
        this.body = null;
    }

    /**
     * Creates an empty HTML document with an empty body.
     * Use this constructor if you want to create your body element manually with DOM operations.
     *
     * @param document The document.
     * @see #getBody()
     */
    public Html(Document document) {
        this.body = document.createElement("body");
    }

    /**
     * Creates an HTML document from a string source (body).
     *
     * @param xhtmlContent The body element as string, e.g. {@code <p>Hi</p>}.
     * @throws SAXException If the input string could not be parsed.
     * @see #getContent()
     */
    public Html(String xhtmlContent) throws SAXException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader("<body>" + Objects.requireNonNull(xhtmlContent) + "</body>")));
            this.body = document.getDocumentElement();
        } catch (ParserConfigurationException | IOException e) {
            // Should never occur with this setup, so don't let the API user deal with it.
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the body of this XHTML document. Use this to append child nodes.
     *
     * @return The body.
     */
    public final Element getBody() {
        return (Element) body;
    }

    /**
     * Gets XHTML content of the body, i.e. the content between the {@code <body></body>} element.
     *
     * @return The XHTML content as string.
     */
    public final String getContent() {
        if (body != null) {
            try {
                TransformerFactory transFactory = TransformerFactory.newInstance();
                Transformer transformer = transFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

                StringWriter writer = new StringWriter();
                transformer.transform(new DOMSource(((Element) body)), new StreamResult(writer));
                String body = writer.toString();
                return body.substring(body.indexOf('>') + 1, body.lastIndexOf('<'));
            } catch (TransformerException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public final String toString() {
        return getContent();
    }
}
