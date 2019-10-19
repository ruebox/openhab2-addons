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

package rocks.xmpp.extensions.address;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.extensions.address.model.Address;
import rocks.xmpp.extensions.address.model.Addresses;
import rocks.xmpp.im.roster.model.Roster;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Christian Schudt
 */
public class AddressTest extends XmlTest {

    protected AddressTest() throws JAXBException {
        super(Addresses.class, Roster.class);
    }

    @Test
    public void marshalAddresses() throws JAXBException, XMLStreamException {
        List<Address> addressList = new ArrayList<>();
        addressList.add(new Address(Address.Type.TO, Jid.of("hildjj@jabber.org/Work"), "description", "node"));
        addressList.add(new Address(Address.Type.CC, Jid.of("jer@jabber.org/Home"), new Roster()));
        Addresses addresses = new Addresses(addressList);

        Addresses addresses2 = new Addresses(new Address(Address.Type.TO, Jid.of("hildjj@jabber.org/Work"), "description", "node"),
                new Address(Address.Type.CC, Jid.of("jer@jabber.org/Home"), new Roster()));

        String xml = marshal(addresses);
        String xml2 = marshal(addresses2);
        Assert.assertEquals(xml, xml2);
        Assert.assertEquals(xml, "<addresses xmlns=\"http://jabber.org/protocol/address\"><address type=\"to\" jid=\"hildjj@jabber.org/Work\" node=\"node\" desc=\"description\"></address><address type=\"cc\" jid=\"jer@jabber.org/Home\"><query xmlns=\"jabber:iq:roster\"></query></address></addresses>");
        Assert.assertNotNull(addressList.get(1).getExtension(Roster.class));
    }

    @Test
    public void testWithoutBlindCarbonCopies() {
        List<Address> addressList = new ArrayList<>();
        addressList.add(new Address(Address.Type.BCC, Jid.of("jer@jabber.org/Home")));
        addressList.add(new Address(Address.Type.TO, Jid.of("hildjj@jabber.org/Work"), "description", "node"));
        addressList.add(new Address(Address.Type.BCC, Jid.of("jer@jabber.org/Home")));
        Addresses addresses = new Addresses(addressList);

        Addresses withoutBCC = addresses.deliveredAndWithoutBlindCarbonCopies();
        Assert.assertEquals(withoutBCC.getAddresses().size(), 1);
        Assert.assertEquals(withoutBCC.getAddresses().get(0).getType(), Address.Type.TO);
        Assert.assertTrue(withoutBCC.getAddresses().get(0).isDelivered());
    }

    @Test
    public void testDelivered() {
        Address address = new Address(Address.Type.CC, Jid.of("jer@jabber.org/Home"), new Roster(), new Roster());
        Address delivered = address.delivered();
        Assert.assertEquals(address.getType(), delivered.getType());
        Assert.assertEquals(address.getJid(), delivered.getJid());
        Assert.assertEquals(address.getDescription(), delivered.getDescription());
        Assert.assertEquals(address.getNode(), delivered.getNode());
        Assert.assertEquals(address.getExtensions(), delivered.getExtensions());
        Assert.assertTrue(delivered.isDelivered());
        Assert.assertFalse(address.isDelivered());
    }

    @Test
    public void testReplyHandling() {
        List<Address> addressList = new ArrayList<>();
        addressList.add(new Address(Address.Type.CC, Jid.of("jer@jabber.org/Home")));
        addressList.add(new Address(Address.Type.TO, Jid.of("hildjj@jabber.org/Work"), "description", "node"));
        addressList.add(new Address(Address.Type.BCC, Jid.of("jer@jabber.org/Home")));

        Addresses addresses = new Addresses(addressList);
        Message message = new Message();
        message.setTo(Jid.of("hildjj@jabber.org/Work"));
        message.setFrom(Jid.of("jer@jabber.org/Home"));
        message.addExtension(addresses);

        Message replyMessage = new Message();

        boolean reply = Addresses.createReply(message, replyMessage);
        Assert.assertTrue(reply);
        Assert.assertEquals(replyMessage.getExtension(Addresses.class).getAddresses().size(), 2);
    }
}
