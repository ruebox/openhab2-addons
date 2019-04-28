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

package rocks.xmpp.extensions.soap;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.core.stanza.model.client.ClientMessage;

import javax.xml.bind.JAXBException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMSource;

/**
 * @author Christian Schudt
 */
public class SoapTest extends XmlTest {

    protected SoapTest() throws JAXBException {
        super(ClientMessage.class, ClientIQ.class);
    }

    @Test
    public void unmarshalSoap() throws JAXBException, XMLStreamException, SOAPException {
        String xml = "<iq from='requester@example.com/soap-client'\n" +
                "    id='soap1'\n" +
                "    to='responder@example.com/soap-server' \n" +
                "    type='set'> \n" +
                "  <env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"> \n" +
                "    <env:Header>\n" +
                "      <m:reservation \n" +
                "         xmlns:m=\"http://travelcompany.example.org/reservation\" \n" +
                "         env:role=\"http://www.w3.org/2003/05/soap-envelope/role/next\"\n" +
                "         env:mustUnderstand=\"true\">\n" +
                "        <m:reference>uuid:093a2da1-q345-739r-ba5d-pqff98fe8j7d</m:reference>\n" +
                "        <m:dateAndTime>2001-11-29T13:20:00.000-05:00</m:dateAndTime>\n" +
                "      </m:reservation>\n" +
                "      <n:passenger \n" +
                "         xmlns:n=\"http://mycompany.example.com/employees\"\n" +
                "         env:role=\"http://www.w3.org/2003/05/soap-envelope/role/next\"\n" +
                "         env:mustUnderstand=\"true\">\n" +
                "        <n:name>Ake Jogvan Ovind</n:name>\n" +
                "      </n:passenger>\n" +
                "    </env:Header>\n" +
                "    <env:Body>\n" +
                "      <p:itinerary xmlns:p=\"http://travelcompany.example.org/reservation/travel\">\n" +
                "        <p:departure>\n" +
                "          <p:departing>New York</p:departing>\n" +
                "          <p:arriving>Los Angeles</p:arriving>\n" +
                "          <p:departureDate>2001-12-14</p:departureDate>\n" +
                "          <p:departureTime>late afternoon</p:departureTime>\n" +
                "          <p:seatPreference>aisle</p:seatPreference>\n" +
                "        </p:departure>\n" +
                "        <p:return>\n" +
                "          <p:departing>Los Angeles</p:departing>\n" +
                "          <p:arriving>New York</p:arriving>\n" +
                "          <p:departureDate>2001-12-20</p:departureDate>\n" +
                "          <p:departureTime>mid-morning</p:departureTime>\n" +
                "          <p:seatPreference/>\n" +
                "        </p:return>\n" +
                "      </p:itinerary>\n" +
                "      <q:lodging xmlns:q=\"http://travelcompany.example.org/reservation/hotels\">\n" +
                "        <q:preference>none</q:preference>\n" +
                "      </q:lodging>\n" +
                "    </env:Body>\n" +
                "  </env:Envelope>  \n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        Element element = iq.getExtension(Element.class);
        Assert.assertNotNull(element);

        MessageFactory messageFactory = null;

        if (SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE.equals(element.getNamespaceURI())) {
            messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        } else if (SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE.equals(element.getNamespaceURI())) {
            messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        }
        if (messageFactory != null) {
            SOAPMessage soapMessage = messageFactory.createMessage();
            soapMessage.getSOAPPart().setContent(new DOMSource(element));
            Assert.assertNotNull(soapMessage.getSOAPPart().getEnvelope());
            Assert.assertEquals(element.getNamespaceURI(), "http://www.w3.org/2003/05/soap-envelope");
        } else {
            Assert.fail();
        }
    }

    @Test
    public void marshalSoap() throws SOAPException, JAXBException, XMLStreamException {
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("p", "http://travelcompany.example.org/reservation/travel");

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem1 = soapBody.addChildElement("departing", "p");
        soapBodyElem1.addTextNode("New York");

        SOAPElement soapBodyElem2 = soapBody.addChildElement("arriving", "p");
        soapBodyElem2.addTextNode("Los Angeles");

        Message message = new Message(Jid.of("juliet@example.net"));
        message.addExtension(envelope);
        String xml = marshal(message);

        Assert.assertEquals(xml, "<message to=\"juliet@example.net\"><env:Envelope xmlns:p=\"http://travelcompany.example.org/reservation/travel\" xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"><env:Header></env:Header><env:Body><p:departing>New York</p:departing><p:arriving>Los Angeles</p:arriving></env:Body></env:Envelope></message>");
    }
}
