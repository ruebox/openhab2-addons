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

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.core.stanza.model.client.ClientMessage;
import rocks.xmpp.core.stanza.model.client.ClientPresence;
import rocks.xmpp.core.stream.model.StreamHeader;
import rocks.xmpp.util.XmppUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;

/**
 * @author Christian Schudt
 */
public abstract class XmlTest {

    private static final Jid FROM = Jid.ofDomain("localhost");

    public static final XMLInputFactory INPUT_FACTORY = XMLInputFactory.newFactory();

    public static final XMLOutputFactory OUTPUT_FACTORY = XMLOutputFactory.newFactory();

    private final Unmarshaller unmarshaller;

    private final Marshaller marshaller;

    private final String namespace;

    protected XmlTest(Class<?>... context) throws JAXBException {
        this("jabber:client", context);
    }

    protected XmlTest(String namespace, Class<?>... context) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(context);
        unmarshaller = jaxbContext.createUnmarshaller();
        marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        this.namespace = namespace;
    }

    private static XMLEventReader getStream(String stanza) throws XMLStreamException {
        String stream = StreamHeader.responseClientToServer(FROM, null, "1", Locale.ENGLISH) + stanza + StreamHeader.CLOSING_STREAM_TAG;
        Reader reader = new StringReader(stream);
        XMLEventReader xmlEventReader = INPUT_FACTORY.createXMLEventReader(reader);
        xmlEventReader.nextEvent();
        xmlEventReader.nextEvent();
        return xmlEventReader;
    }

    @SuppressWarnings("unchecked")
    protected <T> T unmarshal(String xml, Class<T> type) throws XMLStreamException, JAXBException {
        Class<?> clazz = type;
        if (type == Message.class) {
            clazz = ClientMessage.class;
        }
        if (type == Presence.class) {
            clazz = ClientPresence.class;
        }
        if (type == IQ.class) {
            clazz = ClientIQ.class;
        }
        XMLEventReader xmlEventReader = getStream(xml);
        return (T) unmarshaller.unmarshal(xmlEventReader, clazz).getValue();
    }

    protected Object unmarshal(String xml) throws XMLStreamException, JAXBException {
        XMLEventReader xmlEventReader = getStream(xml);
        return unmarshaller.unmarshal(xmlEventReader);
    }

    protected String marshal(Object object) throws XMLStreamException, JAXBException {
        if (object instanceof Message) {
            object = ClientMessage.from((Message) object);
        }
        if (object instanceof Presence) {
            object = ClientPresence.from((Presence) object);
        }
        if (object instanceof IQ) {
            object = ClientIQ.from((IQ) object);
        }
        Writer writer = new StringWriter();

        XMLStreamWriter xmlStreamWriter = OUTPUT_FACTORY.createXMLStreamWriter(writer);

        XMLStreamWriter prefixFreeWriter = XmppUtils.createXmppStreamWriter(xmlStreamWriter, false);
        prefixFreeWriter.setDefaultNamespace(namespace);
        marshaller.marshal(object, prefixFreeWriter);
        prefixFreeWriter.flush();
        return writer.toString();
    }
}
