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
import rocks.xmpp.extensions.pubsub.model.PubSub;
import rocks.xmpp.extensions.pubsub.model.PubSubFeature;
import rocks.xmpp.extensions.pubsub.model.errors.ClosedNode;
import rocks.xmpp.extensions.pubsub.model.errors.ConfigurationRequired;
import rocks.xmpp.extensions.pubsub.model.errors.InvalidJid;
import rocks.xmpp.extensions.pubsub.model.errors.InvalidOptions;
import rocks.xmpp.extensions.pubsub.model.errors.InvalidPayload;
import rocks.xmpp.extensions.pubsub.model.errors.InvalidSubId;
import rocks.xmpp.extensions.pubsub.model.errors.ItemForbidden;
import rocks.xmpp.extensions.pubsub.model.errors.ItemRequired;
import rocks.xmpp.extensions.pubsub.model.errors.JidRequired;
import rocks.xmpp.extensions.pubsub.model.errors.MaxItemsExceeded;
import rocks.xmpp.extensions.pubsub.model.errors.NodeIdRequired;
import rocks.xmpp.extensions.pubsub.model.errors.NotInRosterGroup;
import rocks.xmpp.extensions.pubsub.model.errors.NotSubscribed;
import rocks.xmpp.extensions.pubsub.model.errors.PayloadRequired;
import rocks.xmpp.extensions.pubsub.model.errors.PayloadTooBig;
import rocks.xmpp.extensions.pubsub.model.errors.PendingSubscription;
import rocks.xmpp.extensions.pubsub.model.errors.PresenceSubscriptionRequired;
import rocks.xmpp.extensions.pubsub.model.errors.PubSubError;
import rocks.xmpp.extensions.pubsub.model.errors.SubIdRequired;
import rocks.xmpp.extensions.pubsub.model.errors.TooManySubscriptions;
import rocks.xmpp.extensions.pubsub.model.errors.Unsupported;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class PubSubErrorTest extends XmlTest {

    protected PubSubErrorTest() throws JAXBException {
        super(ClientIQ.class, PubSub.class);
    }

    @Test
    public void testUnsupportedErrorRetrieveSubscriptions() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='subscriptions1'>\n" +
                "  <error type='cancel'>\n" +
                "    <feature-not-implemented xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <unsupported xmlns='http://jabber.org/protocol/pubsub#errors'\n" +
                "                 feature='retrieve-subscriptions'/>\n" +
                "  </error>\n" +
                "</iq>";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof Unsupported);
        Assert.assertEquals(((Unsupported) iq.getError().getExtension()).getFeature(), PubSubFeature.RETRIEVE_SUBSCRIPTIONS);
    }

    @Test
    public void testUnsupportedErrorRetrieveAffiliations() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='affil1'>\n" +
                "  <error type='cancel'>\n" +
                "    <feature-not-implemented xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <unsupported xmlns='http://jabber.org/protocol/pubsub#errors'\n" +
                "                 feature='retrieve-affiliations'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof Unsupported);
        Assert.assertEquals(((Unsupported) iq.getError().getExtension()).getFeature(), PubSubFeature.RETRIEVE_AFFILIATIONS);
    }

    @Test
    public void testInvalidJidError() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='sub1'>\n" +
                "  <error type='modify'>\n" +
                "    <bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <invalid-jid xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof InvalidJid);
        Assert.assertTrue(iq.getError().getExtension() == PubSubError.INVALID_JID);
    }

    @Test
    public void testPresenceSubscriptionRequiredError() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='sub1'>\n" +
                "  <error type='auth'>\n" +
                "    <not-authorized xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <presence-subscription-required xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof PresenceSubscriptionRequired);
        Assert.assertTrue(iq.getError().getExtension() == PubSubError.PRESENCE_SUBSCRIPTION_REQUIRED);
    }

    @Test
    public void testNotInRosterGroupError() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='sub1'>\n" +
                "  <error type='auth'>\n" +
                "    <not-authorized xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <not-in-roster-group xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof NotInRosterGroup);
        Assert.assertTrue(iq.getError().getExtension() == PubSubError.NOT_IN_ROSTER_GROUP);
    }

    @Test
    public void testClosedNodeError() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='sub1'>\n" +
                "  <error type='cancel'>\n" +
                "    <not-allowed xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <closed-node xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof ClosedNode);
        Assert.assertTrue(iq.getError().getExtension() == PubSubError.CLOSED_NODE);
    }

    @Test
    public void testPendingSubscriptionError() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='sub1'>\n" +
                "  <error type='auth'>\n" +
                "    <not-authorized xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <pending-subscription xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof PendingSubscription);
        Assert.assertTrue(iq.getError().getExtension() == PubSubError.PENDING_SUBSCRIPTION);
    }

    @Test
    public void testTooManySubscriptionsError() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='sub1'>\n" +
                "  <error type='wait'>\n" +
                "    <policy-violation xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <too-many-subscriptions xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof TooManySubscriptions);
        Assert.assertTrue(iq.getError().getExtension() == PubSubError.TOO_MANY_SUBSCRIPTIONS);
    }

    @Test
    public void testUnsupportedErrorSubscribe() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='sub1'>\n" +
                "  <error type='cancel'>\n" +
                "    <feature-not-implemented xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <unsupported xmlns='http://jabber.org/protocol/pubsub#errors'\n" +
                "                 feature='subscribe'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof Unsupported);
        Assert.assertEquals(((Unsupported) iq.getError().getExtension()).getFeature(), PubSubFeature.SUBSCRIBE);
    }

    @Test
    public void testConfigurationRequiredError() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='sub1'>\n" +
                "  <pubsub xmlns='http://jabber.org/protocol/pubsub'>\n" +
                "    <subscribe\n" +
                "        node='princely_musings'\n" +
                "        jid='francisco@denmark.lit'/>\n" +
                "    <options node='princely_musings' jid='francisco@denmark.lit'>\n" +
                "      <x xmlns='jabber:x:data' type='submit'>\n" +
                "        <field var='FORM_TYPE' type='hidden'>\n" +
                "          <value>http://jabber.org/protocol/pubsub#subscribe_options</value>\n" +
                "        </field>\n" +
                "        <field var='pubsub#deliver'><value>1</value></field>\n" +
                "        <field var='pubsub#digest'><value>0</value></field>\n" +
                "        <field var='pubsub#include_body'><value>false</value></field>\n" +
                "        <field var='pubsub#show-values'>\n" +
                "          <value>chat</value>\n" +
                "          <value>online</value>\n" +
                "          <value>away</value>\n" +
                "        </field>\n" +
                "      </x>\n" +
                "    </options>\n" +
                "  </pubsub>\n" +
                "  <error type='modify'>\n" +
                "    <not-acceptable xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <configuration-required xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "  </error>\n" +
                "</iq>";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof ConfigurationRequired);
        Assert.assertTrue(iq.getError().getExtension() == PubSubError.CONFIGURATION_REQUIRED);
    }

    @Test
    public void testSubIdRequiredError() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='unsub1'>\n" +
                "  <error type='modify'>\n" +
                "    <bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <subid-required xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof SubIdRequired);
        Assert.assertTrue(iq.getError().getExtension() == PubSubError.SUB_ID_REQUIRED);
    }

    @Test
    public void testNotSubscribedError() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='unsub1'>\n" +
                "  <error type='cancel'>\n" +
                "    <unexpected-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <not-subscribed xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "  </error>\n" +
                "</iq>";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof NotSubscribed);
        Assert.assertTrue(iq.getError().getExtension() == PubSubError.NOT_SUBSCRIBED);
    }

    @Test
    public void testInvalidSubIdError() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='unsub1'>\n" +
                "  <error type='modify'>\n" +
                "    <not-acceptable xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <invalid-subid xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof InvalidSubId);
        Assert.assertTrue(iq.getError().getExtension() == PubSubError.INVALID_SUB_ID);
    }

    @Test
    public void testJidRequiredError() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='options1'>\n" +
                "  <error type='modify'>\n" +
                "    <bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <jid-required xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof JidRequired);
        Assert.assertTrue(iq.getError().getExtension() == PubSubError.JID_REQUIRED);
    }

    @Test
    public void testUnsupportedErrorSubscriptionOptions() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='options1'>\n" +
                "  <error type='cancel'>\n" +
                "    <feature-not-implemented xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <unsupported xmlns='http://jabber.org/protocol/pubsub#errors'\n" +
                "                 feature='subscription-options'/>\n" +
                "  </error>\n" +
                "</iq>";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof Unsupported);
        Assert.assertEquals(((Unsupported) iq.getError().getExtension()).getFeature(), PubSubFeature.SUBSCRIPTION_OPTIONS);
    }

    @Test
    public void testInvalidOptionsError() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='options2'>\n" +
                "  <error type='modify'>\n" +
                "    <bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <invalid-options xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "  </error>\n" +
                "</iq>";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof InvalidOptions);
        Assert.assertTrue(iq.getError().getExtension() == PubSubError.INVALID_OPTIONS);
    }

    @Test
    public void testUnsupportedErrorRetrieveDefaultSub() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='francisco@denmark.lit/barracks'\n" +
                "    id='def1'>\n" +
                "  <error type='cancel'>\n" +
                "    <feature-not-implemented xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <unsupported xmlns='http://jabber.org/protocol/pubsub#errors'\n" +
                "                 feature='retrieve-default-sub'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof Unsupported);
        Assert.assertEquals(((Unsupported) iq.getError().getExtension()).getFeature(), PubSubFeature.RETRIEVE_DEFAULT_SUB);
    }

    @Test
    public void testUnsupportedErrorPublish() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='hamlet@denmark.lit/elsinore'\n" +
                "    id='publish1'>\n" +
                "  <error type='cancel'>\n" +
                "    <feature-not-implemented xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <unsupported xmlns='http://jabber.org/protocol/pubsub#errors'\n" +
                "                 feature='publish'/>\n" +
                "  </error>\n" +
                "</iq>";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof Unsupported);
        Assert.assertEquals(((Unsupported) iq.getError().getExtension()).getFeature(), PubSubFeature.PUBLISH);
    }

    @Test
    public void testPayloadTooBigError() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='hamlet@denmark.lit/elsinore'\n" +
                "    id='publish1'>\n" +
                "  <error type='modify'>\n" +
                "    <not-acceptable xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <payload-too-big xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof PayloadTooBig);
        Assert.assertTrue(iq.getError().getExtension() == PubSubError.PAYLOAD_TOO_BIG);
    }

    @Test
    public void testInvalidPayloadError() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='hamlet@denmark.lit/elsinore'\n" +
                "    id='publish1'>\n" +
                "  <error type='modify'>\n" +
                "    <bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <invalid-payload xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof InvalidPayload);
        Assert.assertTrue(iq.getError().getExtension() == PubSubError.INVALID_PAYLOAD);
    }

    @Test
    public void testItemRequiredError() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='hamlet@denmark.lit/elsinore'\n" +
                "    id='publish1'>\n" +
                "  <error type='modify'>\n" +
                "    <bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <item-required xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof ItemRequired);
        Assert.assertTrue(iq.getError().getExtension() == PubSubError.ITEM_REQUIRED);
    }

    @Test
    public void testPayloadRequiredError() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='hamlet@denmark.lit/elsinore'\n" +
                "    id='publish1'>\n" +
                "  <error type='modify'>\n" +
                "    <bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <payload-required xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof PayloadRequired);
        Assert.assertTrue(iq.getError().getExtension() == PubSubError.PAYLOAD_REQUIRED);
    }

    @Test
    public void testItemForbiddenError() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='hamlet@denmark.lit/elsinore'\n" +
                "    id='publish1'>\n" +
                "  <error type='modify'>\n" +
                "    <bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <item-forbidden xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof ItemForbidden);
        Assert.assertTrue(iq.getError().getExtension() == PubSubError.ITEM_FORBIDDEN);
    }

    @Test
    public void testNodeIdRequiredError() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='hamlet@denmark.lit/elsinore'\n" +
                "    id='retract1'>\n" +
                "  <error type='modify'>\n" +
                "    <bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <nodeid-required xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof NodeIdRequired);
        Assert.assertTrue(iq.getError().getExtension() == PubSubError.NODE_ID_REQUIRED);
    }

    @Test
    public void testUnsupportedErrorDeleteItems() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='hamlet@denmark.lit/elsinore'\n" +
                "    id='retract1'>\n" +
                "  <error type='cancel'>\n" +
                "    <feature-not-implemented xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <unsupported xmlns='http://jabber.org/protocol/pubsub#errors'\n" +
                "                 feature='delete-items'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof Unsupported);
        Assert.assertEquals(((Unsupported) iq.getError().getExtension()).getFeature(), PubSubFeature.DELETE_ITEMS);
    }

    @Test
    public void testUnsupportedErrorCreateNodes() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='hamlet@denmark.lit/elsinore'\n" +
                "    id='create1'>\n" +
                "  <error type='cancel'>\n" +
                "    <feature-not-implemented xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <unsupported xmlns='http://jabber.org/protocol/pubsub#errors'\n" +
                "                 feature='create-nodes'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof Unsupported);
        Assert.assertEquals(((Unsupported) iq.getError().getExtension()).getFeature(), PubSubFeature.CREATE_NODES);
    }

    @Test
    public void testUnsupportedErrorConfigNode() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='hamlet@denmark.lit/elsinore'\n" +
                "    to='pubsub.shakespeare.lit'\n" +
                "    id='config1'>\n" +
                "  <error type='cancel'>\n" +
                "    <feature-not-implemented xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <unsupported xmlns='http://jabber.org/protocol/pubsub#errors'\n" +
                "                 feature='config-node'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof Unsupported);
        Assert.assertEquals(((Unsupported) iq.getError().getExtension()).getFeature(), PubSubFeature.CONFIG_NODE);
    }

    @Test
    public void testUnsupportedErrorRetrieveDefault() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='hamlet@denmark.lit/elsinore'\n" +
                "    id='def1'>\n" +
                "  <error type='cancel'>\n" +
                "    <feature-not-implemented xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <unsupported xmlns='http://jabber.org/protocol/pubsub#errors'\n" +
                "                 feature='retrieve-default'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof Unsupported);
        Assert.assertEquals(((Unsupported) iq.getError().getExtension()).getFeature(), PubSubFeature.RETRIEVE_DEFAULT);
    }

    @Test
    public void testUnsupportedErrorPurgeNodes() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='hamlet@denmark.lit/elsinore'\n" +
                "    id='purge1'>\n" +
                "  <error type='cancel'>\n" +
                "    <feature-not-implemented xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <unsupported xmlns='http://jabber.org/protocol/pubsub#errors'\n" +
                "                 feature='purge-nodes'/>\n" +
                "  </error>\n" +
                "</iq>";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof Unsupported);
        Assert.assertEquals(((Unsupported) iq.getError().getExtension()).getFeature(), PubSubFeature.PURGE_NODES);
    }

    @Test
    public void testUnsupportedErrorGetPending() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='hamlet@denmark.lit/elsinore'\n" +
                "    id='pending1'>\n" +
                "  <error type='cancel'>\n" +
                "    <feature-not-implemented xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <unsupported xmlns='http://jabber.org/protocol/pubsub#errors'\n" +
                "                 feature='get-pending'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof Unsupported);
        Assert.assertEquals(((Unsupported) iq.getError().getExtension()).getFeature(), PubSubFeature.GET_PENDING);
    }

    @Test
    public void testUnsupportedErrorManageSubscriptions() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    id='purge1'>\n" +
                "  <error type='cancel'>\n" +
                "    <feature-not-implemented xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <unsupported xmlns='http://jabber.org/protocol/pubsub#errors'\n" +
                "                 feature='manage-subscriptions'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof Unsupported);
        Assert.assertEquals(((Unsupported) iq.getError().getExtension()).getFeature(), PubSubFeature.MANAGE_SUBSCRIPTIONS);
    }

    @Test
    public void testUnsupportedErrorModifyAffiliations() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    id='ent1'>\n" +
                "  <error type='cancel'>\n" +
                "    <feature-not-implemented xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <unsupported xmlns='http://jabber.org/protocol/pubsub#errors'\n" +
                "                 feature='modify-affiliations'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof Unsupported);
        Assert.assertEquals(((Unsupported) iq.getError().getExtension()).getFeature(), PubSubFeature.MODIFY_AFFILIATIONS);
    }

    @Test
    public void testUnsupportedErrorMemberAffiliations() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    id='ent1'>\n" +
                "  <error type='cancel'>\n" +
                "    <feature-not-implemented xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <unsupported xmlns='http://jabber.org/protocol/pubsub#errors'\n" +
                "                 feature='member-affiliation'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof Unsupported);
        Assert.assertEquals(((Unsupported) iq.getError().getExtension()).getFeature(), PubSubFeature.MEMBER_AFFILIATION);
    }

    @Test
    public void testMaxItemsExceededError() throws JAXBException, XMLStreamException {
        String xml = "<iq type='error'\n" +
                "    from='pubsub.shakespeare.lit'\n" +
                "    to='hamlet@denmark.lit/elsinore'\n" +
                "    id='publish1'>\n" +
                "  <error type='modify'>\n" +
                "    <not-allowed xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <max-items-exceeded xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "  </error>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertTrue(iq.getError().getExtension() instanceof MaxItemsExceeded);
        Assert.assertTrue(iq.getError().getExtension() == PubSubError.MAX_ITEMS_EXCEEDED);
    }
}
