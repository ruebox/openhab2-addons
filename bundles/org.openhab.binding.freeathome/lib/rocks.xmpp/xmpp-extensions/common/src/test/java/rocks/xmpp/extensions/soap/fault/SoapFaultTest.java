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

package rocks.xmpp.extensions.soap.fault;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.soap.model.fault.DataEncodingUnknown;
import rocks.xmpp.extensions.soap.model.fault.MustUnderstand;
import rocks.xmpp.extensions.soap.model.fault.Receiver;
import rocks.xmpp.extensions.soap.model.fault.Sender;
import rocks.xmpp.extensions.soap.model.fault.VersionMismatch;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class SoapFaultTest extends XmlTest {

    protected SoapFaultTest() throws JAXBException {
        super(DataEncodingUnknown.class, MustUnderstand.class, Receiver.class, Sender.class, VersionMismatch.class);
    }

    @Test
    public void testDataEncodingUnknown() throws JAXBException, XMLStreamException {
        String xml = "<DataEncodingUnknown xmlns='http://jabber.org/protocol/soap#fault'/>";
        Receiver sender = unmarshal(xml, Receiver.class);
        Assert.assertNotNull(sender);
    }

    @Test
    public void testMustUnderstand() throws JAXBException, XMLStreamException {
        String xml = "<MustUnderstand xmlns='http://jabber.org/protocol/soap#fault'/>";
        MustUnderstand sender = unmarshal(xml, MustUnderstand.class);
        Assert.assertNotNull(sender);
    }

    @Test
    public void testReceiver() throws JAXBException, XMLStreamException {
        String xml = "<Receiver xmlns='http://jabber.org/protocol/soap#fault'/>";
        Receiver sender = unmarshal(xml, Receiver.class);
        Assert.assertNotNull(sender);
    }

    @Test
    public void testSender() throws JAXBException, XMLStreamException {
        String xml = "<Sender xmlns='http://jabber.org/protocol/soap#fault'/>";
        Sender sender = unmarshal(xml, Sender.class);
        Assert.assertNotNull(sender);
    }

    @Test
    public void testVersionMismatch() throws JAXBException, XMLStreamException {
        String xml = "<VersionMismatch xmlns='http://jabber.org/protocol/soap#fault'/>";
        VersionMismatch sender = unmarshal(xml, VersionMismatch.class);
        Assert.assertNotNull(sender);
    }
}
