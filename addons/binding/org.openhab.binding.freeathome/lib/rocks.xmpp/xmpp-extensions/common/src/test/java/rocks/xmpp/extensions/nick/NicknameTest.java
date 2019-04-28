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

package rocks.xmpp.extensions.nick;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.core.stanza.model.client.ClientPresence;
import rocks.xmpp.extensions.nick.model.Nickname;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class NicknameTest extends XmlTest {
    protected NicknameTest() throws JAXBException {
        super(ClientPresence.class, Nickname.class);
    }

    @Test
    public void unmarshalNickname() throws XMLStreamException, JAXBException {
        String xml = "<presence from='narrator@moby-dick.lit' to='starbuck@moby-dick.lit' type='subscribe'>\n" +
                "  <nick xmlns='http://jabber.org/protocol/nick'>Ishmael</nick>\n" +
                "</presence>\n";
        Presence presence = unmarshal(xml, Presence.class);
        Nickname nickname = presence.getExtension(Nickname.class);
        Assert.assertNotNull(nickname);
        Assert.assertEquals(nickname.getValue(), "Ishmael");
    }

    @Test
    public void marshalNickname() throws JAXBException, XMLStreamException {
        String xml = marshal(new Nickname("Ishmael"));
        Assert.assertEquals("<nick xmlns=\"http://jabber.org/protocol/nick\">Ishmael</nick>", xml);
    }

}
