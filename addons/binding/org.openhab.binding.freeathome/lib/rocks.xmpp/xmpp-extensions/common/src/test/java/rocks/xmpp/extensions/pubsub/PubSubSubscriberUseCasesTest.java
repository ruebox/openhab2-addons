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
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.avatar.model.data.AvatarData;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.pubsub.model.PubSub;
import rocks.xmpp.extensions.pubsub.model.SubscriptionState;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class PubSubSubscriberUseCasesTest extends XmlTest {

    protected PubSubSubscriberUseCasesTest() throws JAXBException {
        super(ClientIQ.class, PubSub.class, AvatarData.class);
    }

    @Test
    public void marshalSubscriptions() throws JAXBException, XMLStreamException {
        PubSub pubSub = PubSub.withSubscribe("princely_musings", Jid.of("francisco@denmark.lit"));
        String xml = marshal(pubSub);
        Assert.assertEquals(xml, "<pubsub xmlns=\"http://jabber.org/protocol/pubsub\"><subscribe node=\"princely_musings\" jid=\"francisco@denmark.lit\"></subscribe></pubsub>");
    }

    @Test
    public void unmarshalPubSubSubscriptionSuccess() throws XMLStreamException, JAXBException {
        String xml = "<iq type='result'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='sub1'>\n" +
                "  <pubsub xmlns='http://jabber.org/protocol/pubsub'>\n" +
                "    <subscription\n" +
                "        node='princely_musings'\n" +
                "        jid='francisco@denmark.lit'\n" +
                "        subid='ba49252aaa4f5d320c24d3766f0bdcade78c78d3'\n" +
                "        subscription='subscribed'/>\n" +
                "  </pubsub>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        PubSub pubSub = iq.getExtension(PubSub.class);
        Assert.assertNotNull(pubSub);
        Assert.assertNotNull(pubSub.getSubscription());
        Assert.assertEquals(pubSub.getSubscription().getNode(), "princely_musings");
        Assert.assertEquals(pubSub.getSubscription().getJid(), Jid.of("francisco@denmark.lit"));
        Assert.assertEquals(pubSub.getSubscription().getSubId(), "ba49252aaa4f5d320c24d3766f0bdcade78c78d3");
        Assert.assertEquals(pubSub.getSubscription().getSubscriptionState(), SubscriptionState.SUBSCRIBED);
    }

    @Test
    public void unmarshalPubSubPending() throws XMLStreamException, JAXBException {
        String xml = "<iq type='result'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='sub1'>\n" +
                "  <pubsub xmlns='http://jabber.org/protocol/pubsub'>\n" +
                "    <subscription\n" +
                "        node='princely_musings'\n" +
                "        jid='francisco@denmark.lit'\n" +
                "        subscription='pending'/>\n" +
                "  </pubsub>\n" +
                "</iq>";
        IQ iq = unmarshal(xml, IQ.class);
        PubSub pubSub = iq.getExtension(PubSub.class);
        Assert.assertNotNull(pubSub);
        Assert.assertNotNull(pubSub.getSubscription());
        Assert.assertEquals(pubSub.getSubscription().getSubscriptionState(), SubscriptionState.PENDING);
    }

    @Test
    public void unmarshalPubSubConfigurationRequired() throws XMLStreamException, JAXBException {
        String xml = "<iq type='result'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='sub1'>\n" +
                "  <pubsub xmlns='http://jabber.org/protocol/pubsub'>\n" +
                "    <subscription\n" +
                "        node='princely_musings'\n" +
                "        jid='francisco@denmark.lit'\n" +
                "        subscription='unconfigured'>\n" +
                "      <subscribe-options>\n" +
                "        <required/>\n" +
                "      </subscribe-options>\n" +
                "    </subscription>\n" +
                "  </pubsub>\n" +
                "</iq>";
        IQ iq = unmarshal(xml, IQ.class);
        PubSub pubSub = iq.getExtension(PubSub.class);
        Assert.assertNotNull(pubSub);
        Assert.assertNotNull(pubSub.getSubscription());
        Assert.assertTrue(pubSub.getSubscription().isConfigurationRequired());
    }

    @Test
    public void unmarshalPubSubConfigurationSupported() throws XMLStreamException, JAXBException {
        String xml = "<iq type='result'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='sub1'>\n" +
                "  <pubsub xmlns='http://jabber.org/protocol/pubsub'>\n" +
                "    <subscription\n" +
                "        node='princely_musings'\n" +
                "        jid='francisco@denmark.lit'\n" +
                "        subscription='unconfigured'>\n" +
                "      <subscribe-options/>\n" +
                "    </subscription>\n" +
                "  </pubsub>\n" +
                "</iq>";
        IQ iq = unmarshal(xml, IQ.class);
        PubSub pubSub = iq.getExtension(PubSub.class);
        Assert.assertNotNull(pubSub);
        Assert.assertNotNull(pubSub.getSubscription());
        Assert.assertFalse(pubSub.getSubscription().isConfigurationRequired());
    }

    // TODO Example 48. Service returns error specifying that subscription configuration is required

    @Test
    public void marshalUnsubscribe() throws JAXBException, XMLStreamException {
        PubSub pubSub = PubSub.withUnsubscribe("node6", Jid.of("francisco@denmark.lit"), null);
        String xml = marshal(pubSub);
        Assert.assertEquals(xml, "<pubsub xmlns=\"http://jabber.org/protocol/pubsub\"><unsubscribe node=\"node6\" jid=\"francisco@denmark.lit\"></unsubscribe></pubsub>");
    }

    @Test
    public void marshalRequestOptions() throws JAXBException, XMLStreamException {
        PubSub pubSub = PubSub.withOptions("node6", Jid.of("francisco@denmark.lit"), null, null);
        String xml = marshal(pubSub);
        Assert.assertEquals(xml, "<pubsub xmlns=\"http://jabber.org/protocol/pubsub\"><options node=\"node6\" jid=\"francisco@denmark.lit\"></options></pubsub>");
    }

    @Test
    public void marshalSubmitOptions() throws JAXBException, XMLStreamException {
        PubSub pubSub = PubSub.withOptions("node6", Jid.of("francisco@denmark.lit"), null, new DataForm(DataForm.Type.SUBMIT));
        String xml = marshal(pubSub);
        Assert.assertEquals(xml, "<pubsub xmlns=\"http://jabber.org/protocol/pubsub\"><options node=\"node6\" jid=\"francisco@denmark.lit\"><x xmlns=\"jabber:x:data\" type=\"submit\"></x></options></pubsub>");
    }

    @Test
    public void unmarshalPubSubOptions() throws XMLStreamException, JAXBException {
        String xml = "<iq type='result'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='options1'>\n" +
                "  <pubsub xmlns='http://jabber.org/protocol/pubsub'>\n" +
                "    <options node='princely_musings' jid='francisco@denmark.lit'>\n" +
                "      <x xmlns='jabber:x:data' type='form'>\n" +
                "        <field var='FORM_TYPE' type='hidden'>\n" +
                "          <value>http://jabber.org/protocol/pubsub#subscribe_options</value>\n" +
                "        </field>\n" +
                "        <field var='pubsub#deliver' type='boolean'\n" +
                "               label='Enable delivery?'>\n" +
                "          <value>1</value>\n" +
                "        </field>\n" +
                "        <field var='pubsub#digest' type='boolean'\n" +
                "               label='Receive digest notifications (approx. one per day)?'>\n" +
                "          <value>0</value>\n" +
                "        </field>\n" +
                "        <field var='pubsub#include_body' type='boolean'\n" +
                "               label='Receive message body in addition to payload?'>\n" +
                "          <value>false</value>\n" +
                "        </field>\n" +
                "        <field\n" +
                "            var='pubsub#show-values'\n" +
                "            type='list-multi'\n" +
                "            label='Select the presence types which are\n" +
                "                   allowed to receive event notifications'>\n" +
                "          <option label='Want to Chat'><value>chat</value></option>\n" +
                "          <option label='Available'><value>online</value></option>\n" +
                "          <option label='Away'><value>away</value></option>\n" +
                "          <option label='Extended Away'><value>xa</value></option>\n" +
                "          <option label='Do Not Disturb'><value>dnd</value></option>\n" +
                "          <value>chat</value>\n" +
                "          <value>online</value>\n" +
                "        </field>\n" +
                "      </x>\n" +
                "    </options>\n" +
                "  </pubsub>\n" +
                "</iq>";
        IQ iq = unmarshal(xml, IQ.class);
        PubSub pubSub = iq.getExtension(PubSub.class);
        Assert.assertNotNull(pubSub);
        Assert.assertNotNull(pubSub.getOptions());
        Assert.assertNotNull(pubSub.getOptions().getDataForm());
    }

    @Test
    public void marshalDefault() throws JAXBException, XMLStreamException {
        PubSub pubSub = PubSub.withDefault("node6");
        String xml = marshal(pubSub);
        Assert.assertEquals(xml, "<pubsub xmlns=\"http://jabber.org/protocol/pubsub\"><default node=\"node6\"></default></pubsub>");
    }

    @Test
    public void marshalDefaultAllNodes() throws JAXBException, XMLStreamException {
        PubSub pubSub = PubSub.withDefault();
        String xml = marshal(pubSub);
        Assert.assertEquals(xml, "<pubsub xmlns=\"http://jabber.org/protocol/pubsub\"><default></default></pubsub>");
    }

    @Test
    public void marshalRequestAllItems() throws JAXBException, XMLStreamException {
        PubSub pubSub = PubSub.withItems("princely_musings");
        String xml = marshal(pubSub);
        Assert.assertEquals(xml, "<pubsub xmlns=\"http://jabber.org/protocol/pubsub\"><items node=\"princely_musings\"></items></pubsub>");
    }

    @Test
    public void unmarshalPubSubItems() throws XMLStreamException, JAXBException {
        String xml = "<iq type='result' \n" +
                "    from='juliet@capulet.lit' \n" +
                "    to='romeo@montague.lit/home' \n" +
                "    id='retrieve1'>\n" +
                "  <pubsub xmlns='http://jabber.org/protocol/pubsub'>\n" +
                "    <items node='urn:xmpp:avatar:data'>\n" +
                "      <item id='111f4b3c50d7b0df729d299bc6f8e9ef9066971f'>\n" +
                "        <data xmlns='urn:xmpp:avatar:data'>\n" +
                "          qANQR1DBwU4DX7jmYZnncm...\n" +
                "        </data>\n" +
                "      </item>\n" +
                "    </items>\n" +
                "  </pubsub>\n" +
                "</iq>";
        IQ iq = unmarshal(xml, IQ.class);
        PubSub pubSub = iq.getExtension(PubSub.class);
        Assert.assertNotNull(pubSub);
        Assert.assertNotNull(pubSub.getItems());
        Assert.assertEquals(pubSub.getItems().size(), 1);
        Assert.assertEquals(pubSub.getItems().get(0).getId(), "111f4b3c50d7b0df729d299bc6f8e9ef9066971f");
        Assert.assertTrue(pubSub.getItems().get(0).getPayload() instanceof AvatarData);
    }

    @Test
    public void marshalRequestMostRecentItems() throws JAXBException, XMLStreamException {
        PubSub pubSub = PubSub.withItems("princely_musings", 2);
        String xml = marshal(pubSub);
        Assert.assertEquals(xml, "<pubsub xmlns=\"http://jabber.org/protocol/pubsub\"><items node=\"princely_musings\" max_items=\"2\"></items></pubsub>");
    }

    @Test
    public void marshalRequestParticularItem() throws JAXBException, XMLStreamException {
        PubSub pubSub = PubSub.withItems("princely_musings", "ae890ac52d0df67ed7cfdf51b644e901");
        String xml = marshal(pubSub);
        Assert.assertEquals(xml, "<pubsub xmlns=\"http://jabber.org/protocol/pubsub\"><items node=\"princely_musings\"><item id=\"ae890ac52d0df67ed7cfdf51b644e901\"></item></items></pubsub>");
    }
}
