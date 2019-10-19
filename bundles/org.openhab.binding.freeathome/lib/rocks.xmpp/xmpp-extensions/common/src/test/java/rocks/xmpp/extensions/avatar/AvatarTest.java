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

package rocks.xmpp.extensions.avatar;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.avatar.model.data.AvatarData;
import rocks.xmpp.extensions.avatar.model.metadata.AvatarMetadata;
import rocks.xmpp.extensions.pubsub.model.Item;
import rocks.xmpp.extensions.pubsub.model.PubSub;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class AvatarTest extends XmlTest {
    protected AvatarTest() throws JAXBException {
        super(ClientIQ.class, PubSub.class, AvatarData.class, AvatarMetadata.class);
    }

    @Test
    public void unmarshalAvatar() throws XMLStreamException, JAXBException {
        String xml = "<iq type='set' from='juliet@capulet.lit/chamber' id='publish3'>\n" +
                "  <pubsub xmlns='http://jabber.org/protocol/pubsub'>\n" +
                "    <publish node='urn:xmpp:avatar:metadata'>\n" +
                "      <item id='111f4b3c50d7b0df729d299bc6f8e9ef9066971f'>\n" +
                "        <metadata xmlns='urn:xmpp:avatar:metadata'>\n" +
                "          <info bytes='12345'\n" +
                "                height='64'\n" +
                "                id='111f4b3c50d7b0df729d299bc6f8e9ef9066971f'\n" +
                "                type='image/png'\n" +
                "                width='64'/>\n" +
                "          <info bytes='12345'\n" +
                "                height='64'\n" +
                "                id='e279f80c38f99c1e7e53e262b440993b2f7eea57'\n" +
                "                type='image/png'\n" +
                "                url='http://avatars.example.org/happy.png'\n" +
                "                width='64'/>\n" +
                "          <info bytes='23456'\n" +
                "                height='64'\n" +
                "                id='357a8123a30844a3aa99861b6349264ba67a5694'\n" +
                "                type='image/gif'\n" +
                "                url='http://avatars.example.org/happy.gif'\n" +
                "                width='64'/>\n" +
                "          <info bytes='78912'\n" +
                "                height='64'\n" +
                "                id='03a179fe37bd5d6bf9c2e1e592a14ae7814e31da'\n" +
                "                type='image/mng'\n" +
                "                url='http://avatars.example.org/happy.mng'\n" +
                "                width='64'/>\n" +
                "        </metadata>\n" +
                "      </item>\n" +
                "    </publish>\n" +
                "  </pubsub>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        PubSub pubSub = iq.getExtension(PubSub.class);
        Assert.assertNotNull(pubSub);
        Item item = pubSub.getPublish().getItem();
        Assert.assertTrue(item.getPayload() instanceof AvatarMetadata);
        AvatarMetadata avatarMetadata = (AvatarMetadata) item.getPayload();
        Assert.assertEquals(avatarMetadata.getInfoList().size(), 4);
        Assert.assertEquals(avatarMetadata.getInfoList().get(1).getBytes(), Integer.valueOf(12345));
        Assert.assertEquals(avatarMetadata.getInfoList().get(1).getHeight(), Integer.valueOf(64));
        Assert.assertEquals(avatarMetadata.getInfoList().get(1).getWidth(), Integer.valueOf(64));
        Assert.assertEquals(avatarMetadata.getInfoList().get(1).getType(), "image/png");
        Assert.assertEquals(avatarMetadata.getInfoList().get(1).getUrl().toString(), "http://avatars.example.org/happy.png");
    }

    @Test
    public void marshalBlock() throws JAXBException, XMLStreamException {
        String xml = "<iq type='set' from='juliet@capulet.lit/chamber' id='publish1'>\n" +
                "  <pubsub xmlns='http://jabber.org/protocol/pubsub'>\n" +
                "    <publish node='urn:xmpp:avatar:data'>\n" +
                "      <item id='111f4b3c50d7b0df729d299bc6f8e9ef9066971f'>\n" +
                "        <data xmlns='urn:xmpp:avatar:data'>\n" +
                "          qANQR1DBwU4DX7jmYZnncm...\n" +
                "        </data>\n" +
                "      </item>\n" +
                "    </publish>\n" +
                "  </pubsub>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        PubSub pubSub = iq.getExtension(PubSub.class);
        Assert.assertNotNull(pubSub);
        Item item = pubSub.getPublish().getItem();
        Assert.assertTrue(item.getPayload() instanceof AvatarData);
        AvatarData avatarData = (AvatarData) item.getPayload();
        Assert.assertTrue(avatarData.getData().length > 0);
    }

    @Test
    public void marshalMetaData() throws JAXBException, XMLStreamException {
        String xml = marshal(new AvatarMetadata());
        Assert.assertEquals(xml, "<metadata xmlns=\"urn:xmpp:avatar:metadata\"></metadata>");
    }
}
