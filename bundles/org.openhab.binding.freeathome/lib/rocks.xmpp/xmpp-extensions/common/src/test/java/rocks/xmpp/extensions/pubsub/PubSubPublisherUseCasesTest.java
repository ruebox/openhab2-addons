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

package rocks.xmpp.extensions.pubsub;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.pubsub.model.PubSub;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class PubSubPublisherUseCasesTest extends XmlTest {

    protected PubSubPublisherUseCasesTest() throws JAXBException {
        super(ClientIQ.class, PubSub.class);
    }

    @Test
    public void marshalPublish() throws JAXBException, XMLStreamException {
        PubSub pubSub = PubSub.withPublish("princely_musings", "bnd81g37d61f49fgn581", null, null);
        String xml = marshal(pubSub);
        Assert.assertEquals(xml, "<pubsub xmlns=\"http://jabber.org/protocol/pubsub\"><publish node=\"princely_musings\"><item id=\"bnd81g37d61f49fgn581\"></item></publish></pubsub>");
    }

    @Test
    public void marshalPublishWithOptions() throws JAXBException, XMLStreamException {
        PubSub pubSub = PubSub.withPublish("princely_musings", "bnd81g37d61f49fgn581", null, new DataForm(DataForm.Type.SUBMIT));
        String xml = marshal(pubSub);
        Assert.assertEquals(xml, "<pubsub xmlns=\"http://jabber.org/protocol/pubsub\"><publish node=\"princely_musings\"><item id=\"bnd81g37d61f49fgn581\"></item></publish><publish-options><x xmlns=\"jabber:x:data\" type=\"submit\"></x></publish-options></pubsub>");
    }

    @Test
    public void unmarshalPubSubPublishSuccess() throws XMLStreamException, JAXBException {
        String xml = "<iq type='result'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='hamlet@denmark.lit/blogbot'\n" +
                "    id='publish1'>\n" +
                "  <pubsub xmlns='http://jabber.org/protocol/pubsub'>\n" +
                "    <publish node='princely_musings'>\n" +
                "      <item id='ae890ac52d0df67ed7cfdf51b644e901'/>\n" +
                "    </publish>\n" +
                "  </pubsub>\n" +
                "</iq>";

        IQ iq = unmarshal(xml, IQ.class);
        PubSub pubSub = iq.getExtension(PubSub.class);
        Assert.assertNotNull(pubSub);
        Assert.assertNotNull(pubSub.getPublish());
        Assert.assertEquals(pubSub.getPublish().getNode(), "princely_musings");
        Assert.assertNotNull(pubSub.getPublish().getItem());
    }

    @Test
    public void marshalDeleteItem() throws JAXBException, XMLStreamException {
        PubSub pubSub = PubSub.withRetract("princely_musings", "ae890ac52d0df67ed7cfdf51b644e901", false);
        String xml = marshal(pubSub);
        Assert.assertEquals(xml, "<pubsub xmlns=\"http://jabber.org/protocol/pubsub\"><retract node=\"princely_musings\" notify=\"false\"><item id=\"ae890ac52d0df67ed7cfdf51b644e901\"></item></retract></pubsub>");
    }
}
