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

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.bind.model.Bind;
import rocks.xmpp.core.issue137.ChildType;
import rocks.xmpp.core.issue137.ObjectFactory;
import rocks.xmpp.core.issue137.ParentType;
import rocks.xmpp.core.sasl.model.Auth;
import rocks.xmpp.core.sasl.model.Response;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.core.stanza.model.client.ClientMessage;
import rocks.xmpp.core.stanza.model.server.ServerMessage;
import rocks.xmpp.core.stream.model.StreamError;
import rocks.xmpp.core.stream.model.StreamFeatures;
import rocks.xmpp.core.stream.model.StreamHeader;
import rocks.xmpp.core.stream.model.errors.Condition;
import rocks.xmpp.extensions.httpbind.model.Body;
import rocks.xmpp.im.roster.model.Contact;
import rocks.xmpp.im.roster.model.Roster;
import rocks.xmpp.util.XmppUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Christian Schudt
 */
public class PrefixFreeCanonicalizationWriterTest {

    @Test
    public void testSimpleStanza() throws XMLStreamException, JAXBException {

        Writer writer = new StringWriter();

        XMLStreamWriter xmlStreamWriter = XmlTest.OUTPUT_FACTORY.createXMLStreamWriter(writer);

        XMLStreamWriter prefixFreeWriter = XmppUtils.createXmppStreamWriter(xmlStreamWriter);
        StreamHeader streamHeader = StreamHeader.create(null, null, null, "1.0", null, "jabber:client");
        streamHeader.writeTo(xmlStreamWriter);
        JAXBContext jaxbContext = JAXBContext.newInstance(ClientIQ.class, Roster.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);


        Collection<Contact> contacts = new ArrayDeque<>();
        contacts.add(new Contact(Jid.ofDomain("domain")));
        Roster roster = new Roster(contacts);
        IQ iq = ClientIQ.from(new IQ(IQ.Type.GET, roster, "1"));

        marshaller.marshal(iq, prefixFreeWriter);
        prefixFreeWriter.flush();
        Assert.assertEquals(writer.toString(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?><stream:stream xmlns=\"jabber:client\" xmlns:stream=\"http://etherx.jabber.org/streams\" version=\"1.0\"><iq id=\"1\" type=\"get\"><query xmlns=\"jabber:iq:roster\"><item jid=\"domain\"></item></query></iq>");
    }

    @Test
    public void testElementWithPrefixedAttribute() throws XMLStreamException, JAXBException {

        Writer writer = new StringWriter();

        XMLStreamWriter xmlStreamWriter = XmlTest.OUTPUT_FACTORY.createXMLStreamWriter(writer);
        XMLStreamWriter xmppStreamWriter = XmppUtils.createXmppStreamWriter(xmlStreamWriter);

        JAXBContext jaxbContext = JAXBContext.newInstance(Body.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        Body body = Body.builder().xmppVersion("1.0").restart(true).requestId(1L).build();

        marshaller.marshal(body, xmppStreamWriter);
        xmppStreamWriter.flush();
        Assert.assertEquals(writer.toString(), "<body xmlns=\"http://jabber.org/protocol/httpbind\" rid=\"1\" xmlns:xmpp=\"urn:xmpp:xbosh\" xmpp:version=\"1.0\" xmpp:restart=\"true\"></body>");
    }

    @Test
    public void testTwoElementsWithSameNamespace() throws XMLStreamException, JAXBException {

        Writer writer = new StringWriter();

        XMLStreamWriter xmlStreamWriter = XmlTest.OUTPUT_FACTORY.createXMLStreamWriter(writer);
        XMLStreamWriter xmppStreamWriter = XmppUtils.createXmppStreamWriter(xmlStreamWriter);
        xmppStreamWriter.writeStartElement("stream");
        JAXBContext jaxbContext = JAXBContext.newInstance(ClientMessage.class, Auth.class, Response.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

        Auth auth = new Auth("PLAIN", null);
        marshaller.marshal(auth, xmppStreamWriter);

        Response response = new Response(null);
        marshaller.marshal(response, xmppStreamWriter);
        xmppStreamWriter.flush();
        Assert.assertEquals(writer.toString(), "<stream><auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"PLAIN\"></auth><response xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\"></response>");
    }

    @Test
    public void testStreamFeatures() throws XMLStreamException, JAXBException {

        Writer writer = new StringWriter();

        XMLStreamWriter xmlStreamWriter = XmlTest.OUTPUT_FACTORY.createXMLStreamWriter(writer);
        XMLStreamWriter xmppStreamWriter = XmppUtils.createXmppStreamWriter(xmlStreamWriter);
        xmppStreamWriter.setDefaultNamespace("jabber:client");
        JAXBContext jaxbContext = JAXBContext.newInstance(Body.class, StreamFeatures.class, Bind.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

        StreamHeader streamHeader = StreamHeader.create(null, null, null, "1.0", null, "jabber:client");
        streamHeader.writeTo(xmlStreamWriter);
        StreamFeatures streamFeatures = new StreamFeatures(Collections.singleton(new Bind("res")));

        marshaller.marshal(streamFeatures, xmppStreamWriter);
        xmppStreamWriter.flush();
        Assert.assertEquals(writer.toString(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?><stream:stream xmlns=\"jabber:client\" xmlns:stream=\"http://etherx.jabber.org/streams\" version=\"1.0\"><stream:features><bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\"><resource>res</resource></bind></stream:features>");
    }

    @Test
    public void testStreamFeaturesInBody() throws XMLStreamException, JAXBException {

        Writer writer = new StringWriter();

        XMLStreamWriter xmlStreamWriter = XmlTest.OUTPUT_FACTORY.createXMLStreamWriter(writer);
        XMLStreamWriter xmppStreamWriter = XmppUtils.createXmppStreamWriter(xmlStreamWriter, true);

        JAXBContext jaxbContext = JAXBContext.newInstance(Body.class, StreamFeatures.class, Bind.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        StreamFeatures streamFeatures = new StreamFeatures(Collections.singleton(new Bind("res")));

        marshaller.marshal(Body.builder().xmppVersion("1.0").wrappedObjects(Collections.singleton(streamFeatures)).build(), xmppStreamWriter);
        xmppStreamWriter.flush();
        Assert.assertEquals(writer.toString(), "<body xmlns=\"http://jabber.org/protocol/httpbind\" xmlns:stream=\"http://etherx.jabber.org/streams\" xmlns:xmpp=\"urn:xmpp:xbosh\" xmpp:version=\"1.0\"><stream:features><bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\"><resource>res</resource></bind></stream:features></body>");
    }

    @Test
    public void testStreamFeaturesNotInBody() throws XMLStreamException, JAXBException {

        Writer writer = new StringWriter();

        XMLStreamWriter xmlStreamWriter = XmlTest.OUTPUT_FACTORY.createXMLStreamWriter(writer);
        XMLStreamWriter xmppStreamWriter = XmppUtils.createXmppStreamWriter(xmlStreamWriter, false);

        JAXBContext jaxbContext = JAXBContext.newInstance(Body.class, StreamFeatures.class, Auth.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        Auth auth = new Auth("PLAIN", null);

        marshaller.marshal(Body.builder().xmppVersion("1.0").wrappedObjects(Collections.singleton(auth)).build(), xmppStreamWriter);
        xmppStreamWriter.flush();
        Assert.assertEquals(writer.toString(), "<body xmlns=\"http://jabber.org/protocol/httpbind\" xmlns:xmpp=\"urn:xmpp:xbosh\" xmpp:version=\"1.0\"><auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"PLAIN\"></auth></body>");
    }

    @Test
    public void testStanzaInBody() throws XMLStreamException, JAXBException {

        Writer writer = new StringWriter();

        XMLStreamWriter xmlStreamWriter = XmlTest.OUTPUT_FACTORY.createXMLStreamWriter(writer);
        XMLStreamWriter xmppStreamWriter = XmppUtils.createXmppStreamWriter(xmlStreamWriter, true);

        JAXBContext jaxbContext = JAXBContext.newInstance(Body.class, StreamFeatures.class, Bind.class, ClientMessage.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        Message message = ClientMessage.from(new Message());

        marshaller.marshal(Body.builder().xmppVersion("1.0").wrappedObjects(Collections.singleton(message)).build(), xmppStreamWriter);
        xmppStreamWriter.flush();
        Assert.assertEquals(writer.toString(), "<body xmlns=\"http://jabber.org/protocol/httpbind\" xmlns:stream=\"http://etherx.jabber.org/streams\" xmlns:xmpp=\"urn:xmpp:xbosh\" xmpp:version=\"1.0\"><message xmlns=\"jabber:client\"></message></body>");
    }

    @Test
    public void testStandaloneClientStanza() throws XMLStreamException, JAXBException {

        Writer writer = new StringWriter();

        XMLStreamWriter xmlStreamWriter = XmlTest.OUTPUT_FACTORY.createXMLStreamWriter(writer);
        XMLStreamWriter xmppStreamWriter = XmppUtils.createXmppStreamWriter(xmlStreamWriter, true);

        JAXBContext jaxbContext = JAXBContext.newInstance(ClientMessage.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        Message message = ClientMessage.from(new Message());

        marshaller.marshal(message, xmppStreamWriter);
        xmppStreamWriter.flush();
        Assert.assertEquals(writer.toString(), "<message xmlns=\"jabber:client\"></message>");
    }

    @Test
    public void testStandaloneStreamFeatures() throws XMLStreamException, JAXBException {

        Writer writer = new StringWriter();

        XMLStreamWriter xmlStreamWriter = XmlTest.OUTPUT_FACTORY.createXMLStreamWriter(writer);
        XMLStreamWriter xmppStreamWriter = XmppUtils.createXmppStreamWriter(xmlStreamWriter, true);

        JAXBContext jaxbContext = JAXBContext.newInstance(StreamFeatures.class, ClientMessage.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        StreamFeatures streamFeatures = new StreamFeatures(Collections.emptyList());

        marshaller.marshal(streamFeatures, xmppStreamWriter);
        xmppStreamWriter.flush();
        Assert.assertEquals(writer.toString(), "<stream:features xmlns:stream=\"http://etherx.jabber.org/streams\"></stream:features>");
    }

    @Test
    public void testStandaloneServerStanza() throws XMLStreamException, JAXBException {

        Writer writer = new StringWriter();

        XMLStreamWriter xmlStreamWriter = XmlTest.OUTPUT_FACTORY.createXMLStreamWriter(writer);
        XMLStreamWriter xmppStreamWriter = XmppUtils.createXmppStreamWriter(xmlStreamWriter, false);
        xmppStreamWriter.setDefaultNamespace("jabber:server");
        JAXBContext jaxbContext = JAXBContext.newInstance(ClientMessage.class, ServerMessage.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        Message message = ServerMessage.from(new Message());
        Message message1 = ClientMessage.from(new Message());
        Message message2 = ServerMessage.from(new Message());

        marshaller.marshal(message, xmppStreamWriter);
        marshaller.marshal(message1, xmppStreamWriter);
        marshaller.marshal(message2, xmppStreamWriter);

        xmppStreamWriter.flush();
        Assert.assertEquals(writer.toString(), "<message></message><message xmlns=\"jabber:client\"></message><message></message>");
    }

    @Test
    public void testStandaloneStreamError() throws XMLStreamException, JAXBException {

        Writer writer = new StringWriter();

        XMLStreamWriter xmlStreamWriter = XmlTest.OUTPUT_FACTORY.createXMLStreamWriter(writer);
        XMLStreamWriter xmppStreamWriter = XmppUtils.createXmppStreamWriter(xmlStreamWriter, true);

        JAXBContext jaxbContext = JAXBContext.newInstance(StreamError.class, ClientMessage.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

        marshaller.marshal(new StreamError(Condition.INTERNAL_SERVER_ERROR), xmppStreamWriter);
        xmppStreamWriter.flush();
        Assert.assertEquals(writer.toString(), "<stream:error xmlns:stream=\"http://etherx.jabber.org/streams\"><internal-server-error xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"></internal-server-error></stream:error>");
    }

    @Test
    public void testClientMessageInContext() throws XMLStreamException, JAXBException {

        Writer writer = new StringWriter();

        XMLStreamWriter xmlStreamWriter = XmlTest.OUTPUT_FACTORY.createXMLStreamWriter(writer);
        XMLStreamWriter xmppStreamWriter = XmppUtils.createXmppStreamWriter(xmlStreamWriter, false);
        xmppStreamWriter.setDefaultNamespace("jabber:client");

        JAXBContext jaxbContext = JAXBContext.newInstance(ServerMessage.class, ClientMessage.class, Auth.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        Message message = ClientMessage.from(new Message());
        marshaller.marshal(message, xmppStreamWriter);
        xmppStreamWriter.flush();
        Assert.assertEquals(writer.toString(), "<message></message>");
    }

    @Test
    public void testStreamHeader() throws XMLStreamException, JAXBException {

        Writer writer = new StringWriter();

        XMLStreamWriter xmlStreamWriter = XmlTest.OUTPUT_FACTORY.createXMLStreamWriter(writer);
        XMLStreamWriter xmppStreamWriter = XmppUtils.createXmppStreamWriter(xmlStreamWriter, false);

        StreamHeader streamHeader = StreamHeader.create(null, null, null, "1.0", null, "jabber:client");
        streamHeader.writeTo(xmppStreamWriter);
        xmppStreamWriter.flush();
        Assert.assertEquals(writer.toString(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?><stream:stream version=\"1.0\">");
    }

    @Test
    public void testIssue137() throws JAXBException, XMLStreamException {

        Writer writer = new StringWriter();
        XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(writer);
        XMLStreamWriter xmppStreamWriter = XmppUtils.createXmppStreamWriter(
                xmlStreamWriter, true);

        JAXBContext jaxbContext = JAXBContext.newInstance(ClientIQ.class, ObjectFactory.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

        ObjectFactory factory = new ObjectFactory();
        JAXBElement<ParentType> childElem = factory.createMyElement(new ChildType());

        IQ iq = new IQ(Jid.of("romeo@example.net"), IQ.Type.SET, childElem, "1");
        marshaller.marshal(ClientIQ.from(iq), xmppStreamWriter);
        xmppStreamWriter.flush();
        Assert.assertEquals(writer.toString(), "<iq xmlns=\"jabber:client\" id=\"1\" to=\"romeo@example.net\" type=\"set\"><myElement xmlns=\"http://testnamespace.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"childType\"></myElement></iq>");
    }
}
