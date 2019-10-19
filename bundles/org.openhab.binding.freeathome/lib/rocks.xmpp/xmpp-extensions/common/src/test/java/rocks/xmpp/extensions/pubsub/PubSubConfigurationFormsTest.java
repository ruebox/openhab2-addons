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
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.pubsub.model.AccessModel;
import rocks.xmpp.extensions.pubsub.model.ChildrenAssociationPolicy;
import rocks.xmpp.extensions.pubsub.model.ItemReply;
import rocks.xmpp.extensions.pubsub.model.NodeConfiguration;
import rocks.xmpp.extensions.pubsub.model.NodeMetaData;
import rocks.xmpp.extensions.pubsub.model.NodeType;
import rocks.xmpp.extensions.pubsub.model.PublishOptions;
import rocks.xmpp.extensions.pubsub.model.PublisherModel;
import rocks.xmpp.extensions.pubsub.model.SendLastPublishedItem;
import rocks.xmpp.extensions.pubsub.model.SubscribeOptions;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

/**
 * @author Christian Schudt
 */
public class PubSubConfigurationFormsTest extends XmlTest {

    protected PubSubConfigurationFormsTest() throws JAXBException {
        super(DataForm.class);
    }

    @Test
    public void testMetaData() throws JAXBException, XMLStreamException {
        Instant now = Instant.now();
        NodeMetaData pubSubMetaDataForm = NodeMetaData.builder()
                .contacts(Collections.singleton(Jid.of("contact")))
                .creationDate(now)
                .creator(Jid.of("creator"))
                .description("desc")
                .language(Locale.GERMAN)
                .numberOfSubscribers(2)
                .owners(Collections.singleton(Jid.of("owner")))
                .publishers(Collections.singleton(Jid.of("publisher")))
                .nodeTitle("title")
                .payloadType("namespace")
                .build();

        String xml = marshal(pubSubMetaDataForm.getDataForm());
        Assert.assertEquals(xml, "<x xmlns=\"jabber:x:data\" type=\"result\">" +
                "<field type=\"hidden\" var=\"FORM_TYPE\"><value>http://jabber.org/protocol/pubsub#meta-data</value></field>" +
                "<field type=\"jid-multi\" var=\"pubsub#contact\"><value>contact</value></field>" +
                "<field type=\"text-single\" var=\"pubsub#creation_date\"><value>" + now + "</value></field>" +
                "<field type=\"jid-single\" var=\"pubsub#creator\"><value>creator</value></field>" +
                "<field type=\"text-single\" var=\"pubsub#description\"><value>desc</value></field>" +
                "<field type=\"list-single\" var=\"pubsub#language\"><value>de</value></field>" +
                "<field type=\"text-single\" var=\"pubsub#num_subscribers\"><value>2</value></field>" +
                "<field type=\"jid-multi\" var=\"pubsub#owner\"><value>owner</value></field>" +
                "<field type=\"jid-multi\" var=\"pubsub#publisher\"><value>publisher</value></field>" +
                "<field type=\"text-single\" var=\"pubsub#title\"><value>title</value></field>" +
                "<field type=\"text-single\" var=\"pubsub#type\"><value>namespace</value></field>" +
                "</x>");
        DataForm dataForm = unmarshal(xml, DataForm.class);
        NodeMetaData pubSubMetaDataForm1 = new NodeMetaData(dataForm);
        Assert.assertEquals(pubSubMetaDataForm1.getCreationDate(), now);
        Assert.assertEquals(pubSubMetaDataForm1.getCreator(), Jid.of("creator"));
        Assert.assertEquals(pubSubMetaDataForm1.getDescription(), "desc");
        Assert.assertEquals(pubSubMetaDataForm1.getLanguage(), Locale.GERMAN);
        Assert.assertEquals(pubSubMetaDataForm1.getNumberOfSubscribers(), Integer.valueOf(2));
        Assert.assertEquals(pubSubMetaDataForm1.getOwners(), Collections.singleton(Jid.of("owner")));
        Assert.assertEquals(pubSubMetaDataForm1.getPublishers(), Collections.singleton(Jid.of("publisher")));
        Assert.assertEquals(pubSubMetaDataForm1.getNodeTitle(), "title");
        Assert.assertEquals(pubSubMetaDataForm1.getPayloadType(), "namespace");
    }

    @Test
    public void testPublishOptions() throws JAXBException, XMLStreamException {
        PublishOptions publishOptions = PublishOptions.builder()
                .accessModel(AccessModel.AUTHORIZE)
                .persistItems(true)
                .rosterGroupsAllowed(Collections.singleton("Friends"))
                .sendLastPublishedItem(SendLastPublishedItem.ON_SUB)
                .build();

        String xml = marshal(publishOptions.getDataForm());
        Assert.assertEquals(xml, "<x xmlns=\"jabber:x:data\" type=\"submit\">" +
                "<field type=\"hidden\" var=\"FORM_TYPE\"><value>http://jabber.org/protocol/pubsub#publish-options</value></field>" +
                "<field type=\"list-single\" var=\"pubsub#access_model\"><value>authorize</value></field>" +
                "<field type=\"boolean\" var=\"pubsub#persist_items\"><value>1</value></field>" +
                "<field type=\"list-single\" var=\"pubsub#send_last_published_item\"><value>on_sub</value></field>" +
                "<field type=\"list-multi\" var=\"pubsub#roster_groups_allowed\"><value>Friends</value></field>" +
                "</x>");
        DataForm dataForm = unmarshal(xml, DataForm.class);
        PublishOptions publishOptionsForm = new PublishOptions(dataForm);
        Assert.assertEquals(publishOptionsForm.getAccessModel(), AccessModel.AUTHORIZE);
        Assert.assertTrue(publishOptionsForm.isPersistItems());
        Assert.assertEquals(publishOptionsForm.getSendLastPublishedItem(), SendLastPublishedItem.ON_SUB);
        Assert.assertEquals(publishOptionsForm.getRosterGroupsAllowed(), Collections.singleton("Friends"));
    }

    @Test
    public void testNodeConfiguration() throws JAXBException, XMLStreamException, MalformedURLException {
        NodeConfiguration nodeConfiguration = NodeConfiguration.builder()
                .accessModel(AccessModel.AUTHORIZE)
                .bodyXslt(new URL("https://xmpp.org"))
                .childrenAssociationPolicy(ChildrenAssociationPolicy.OWNERS)
                .childrenAssociationWhitelist(Collections.singleton(Jid.of("domain")))
                .children(Collections.singleton("collection1"))
                .childrenMax(23)
                .collection(Collections.singleton("collections"))
                .contacts(Collections.singleton(Jid.of("contact")))
                .dataformXslt(new URL("http://www.xmpp.org"))
                .deliverNotifications(true)
                .deliverPayloads(false)
                .description("description")
                .itemExpire(2)
                .itemReply(ItemReply.OWNER)
                .language(Locale.GERMAN)
                .maxItems(4)
                .maxPayloadSize(54)
                .nodeType(NodeType.LEAF)
                .notificationType(Message.Type.NORMAL)
                .notifyConfig(true)
                .notifyDelete(true)
                .notifyRetract(true)
                .notifySub(true)
                .persistItems(true)
                .presenceBasedDelivery(true)
                .publisherModel(PublisherModel.OPEN)
                .purgeOffline(false)
                .rosterGroupsAllowed(Arrays.asList("group1", "group2"))
                .sendLastPublishedItem(SendLastPublishedItem.ON_SUB_AND_PRESENCE)
                .temporarySubscriptions(true)
                .allowSubscriptions(true)
                .nodeTitle("Title")
                .type("Type")
                .build();

        String xml = marshal(nodeConfiguration.getDataForm());
        Assert.assertEquals(xml, "<x xmlns=\"jabber:x:data\" type=\"submit\">" +
                "<field type=\"hidden\" var=\"FORM_TYPE\"><value>http://jabber.org/protocol/pubsub#node_config</value></field>" +
                "<field type=\"list-single\" var=\"pubsub#access_model\"><value>authorize</value></field>" +
                "<field type=\"text-single\" var=\"pubsub#body_xslt\"><value>https://xmpp.org</value></field>" +
                "<field type=\"list-single\" var=\"pubsub#children_association_policy\"><value>owners</value></field>" +
                "<field type=\"jid-multi\" var=\"pubsub#children_association_whitelist\"><value>domain</value></field>" +
                "<field type=\"text-multi\" var=\"pubsub#children\"><value>collection1</value></field>" +
                "<field type=\"text-single\" var=\"pubsub#children_max\"><value>23</value></field>" +
                "<field type=\"text-multi\" var=\"pubsub#collection\"><value>collections</value></field>" +
                "<field type=\"jid-multi\" var=\"pubsub#contact\"><value>contact</value></field>" +
                "<field type=\"text-single\" var=\"pubsub#dataform_xslt\"><value>http://www.xmpp.org</value></field>" +
                "<field type=\"boolean\" var=\"pubsub#deliver_notifications\"><value>1</value></field>" +
                "<field type=\"boolean\" var=\"pubsub#deliver_payloads\"><value>0</value></field>" +
                "<field type=\"text-single\" var=\"pubsub#description\"><value>description</value></field>" +
                "<field type=\"text-single\" var=\"pubsub#item_expire\"><value>2</value></field>" +
                "<field type=\"list-single\" var=\"pubsub#itemreply\"><value>owner</value></field>" +
                "<field type=\"list-single\" var=\"pubsub#language\"><value>de</value></field>" +
                "<field type=\"text-single\" var=\"pubsub#max_items\"><value>4</value></field>" +
                "<field type=\"text-single\" var=\"pubsub#max_payload_size\"><value>54</value></field>" +
                "<field type=\"list-single\" var=\"pubsub#node_type\"><value>leaf</value></field>" +
                "<field type=\"list-single\" var=\"pubsub#notification_type\"><value>normal</value></field>" +
                "<field type=\"boolean\" var=\"pubsub#notify_config\"><value>1</value></field>" +
                "<field type=\"boolean\" var=\"pubsub#notify_delete\"><value>1</value></field>" +
                "<field type=\"boolean\" var=\"pubsub#notify_retract\"><value>1</value></field>" +
                "<field type=\"boolean\" var=\"pubsub#notify_sub\"><value>1</value></field>" +
                "<field type=\"boolean\" var=\"pubsub#persist_items\"><value>1</value></field>" +
                "<field type=\"boolean\" var=\"pubsub#presence_based_delivery\"><value>1</value></field>" +
                "<field type=\"list-single\" var=\"pubsub#publish_model\"><value>open</value></field>" +
                "<field type=\"boolean\" var=\"pubsub#purge_offline\"><value>0</value></field>" +
                "<field type=\"list-multi\" var=\"pubsub#roster_groups_allowed\"><value>group1</value><value>group2</value></field>" +
                "<field type=\"list-single\" var=\"pubsub#send_last_published_item\"><value>on_sub_and_presence</value></field>" +
                "<field type=\"boolean\" var=\"pubsub#tempsub\"><value>1</value></field>" +
                "<field type=\"boolean\" var=\"pubsub#subscribe\"><value>1</value></field>" +
                "<field type=\"text-single\" var=\"pubsub#title\"><value>Title</value></field>" +
                "<field type=\"text-single\" var=\"pubsub#type\"><value>Type</value></field>" +
                "</x>");

        DataForm dataForm = unmarshal(xml, DataForm.class);
        NodeConfiguration nodeConfiguration1 = new NodeConfiguration(dataForm);
        Assert.assertEquals(nodeConfiguration1.getAccessModel(), AccessModel.AUTHORIZE);
        Assert.assertNotNull(nodeConfiguration1.getBodyXslt());
        Assert.assertEquals(nodeConfiguration1.getBodyXslt().toString(), "https://xmpp.org");
        Assert.assertEquals(nodeConfiguration1.getChildrenAssociationPolicy(), ChildrenAssociationPolicy.OWNERS);
        Assert.assertEquals(nodeConfiguration1.getChildrenAssociationWhitelist(), Collections.singleton(Jid.of("domain")));
        Assert.assertEquals(nodeConfiguration1.getChildren(), Collections.singleton("collection1"));
        Assert.assertEquals(nodeConfiguration1.getChildrenMax(), Integer.valueOf(23));
        Assert.assertEquals(nodeConfiguration1.getContacts(), Collections.singleton(Jid.of("contact")));
        Assert.assertNotNull(nodeConfiguration1.getDataformXslt());
        Assert.assertEquals(nodeConfiguration1.getDataformXslt().toString(), "http://www.xmpp.org");
        Assert.assertTrue(nodeConfiguration1.isDeliverNotifications());
        Assert.assertFalse(nodeConfiguration1.isDeliverPayloads());
        Assert.assertEquals(nodeConfiguration1.getDescription(), "description");
        Assert.assertEquals(nodeConfiguration1.getItemExpire(), Integer.valueOf(2));
        Assert.assertEquals(nodeConfiguration1.getItemReply(), ItemReply.OWNER);
        Assert.assertEquals(nodeConfiguration1.getLanguage(), "de");
        Assert.assertEquals(nodeConfiguration1.getMaxItems(), Integer.valueOf(4));
        Assert.assertEquals(nodeConfiguration1.getMaxPayloadSize(), Integer.valueOf(54));
        Assert.assertEquals(nodeConfiguration1.getNodeType(), NodeType.LEAF);
        Assert.assertEquals(nodeConfiguration1.getNotificationType(), Message.Type.NORMAL);
        Assert.assertTrue(nodeConfiguration1.isNotifyConfig());
        Assert.assertTrue(nodeConfiguration1.isNotifyDelete());
        Assert.assertTrue(nodeConfiguration1.isNotifyRetract());
        Assert.assertTrue(nodeConfiguration1.isNotifySub());
        Assert.assertTrue(nodeConfiguration1.isPersistItems());
        Assert.assertTrue(nodeConfiguration1.isPresenceBasedDelivery());
        Assert.assertEquals(nodeConfiguration1.getPublisherModel(), PublisherModel.OPEN);
        Assert.assertFalse(nodeConfiguration1.isPurgeOffline());
        Assert.assertEquals(nodeConfiguration1.getRosterGroupsAllowed(), Arrays.asList("group1", "group2"));
        Assert.assertEquals(nodeConfiguration1.getSendLastPublishedItem(), SendLastPublishedItem.ON_SUB_AND_PRESENCE);
        Assert.assertTrue(nodeConfiguration1.isTemporarySubscriptions());
        Assert.assertTrue(nodeConfiguration1.isAllowSubscriptions());
        Assert.assertEquals(nodeConfiguration1.getNodeTitle(), "Title");
        Assert.assertEquals(nodeConfiguration1.getPayloadType(), "Type");

    }

    @Test
    public void testSubscribeOptions() throws JAXBException, XMLStreamException {
        SubscribeOptions subscribeOptions1 = SubscribeOptions.builder()
                .deliver(true)
                .digest(true)
                .digestFrequency(3)
                .includeBody(true)
                .temporary(true)
                .showValues(Arrays.asList(Presence.Show.AWAY, Presence.Show.CHAT, null))
                .subscriptionType(SubscribeOptions.SubscriptionType.NODES)
                .subscriptionDepth(-1)
                .build();

        String xml = marshal(subscribeOptions1.getDataForm());
        Assert.assertEquals(xml, "<x xmlns=\"jabber:x:data\" type=\"submit\">" +
                "<field type=\"hidden\" var=\"FORM_TYPE\"><value>http://jabber.org/protocol/pubsub#subscribe_options</value></field>" +
                "<field type=\"boolean\" var=\"pubsub#deliver\"><value>1</value></field>" +
                "<field type=\"boolean\" var=\"pubsub#digest\"><value>1</value></field>" +
                "<field type=\"text-single\" var=\"pubsub#digest_frequency\"><value>3</value></field>" +
                "<field type=\"text-single\" var=\"pubsub#expire\"><value>presence</value></field>" +
                "<field type=\"boolean\" var=\"pubsub#include_body\"><value>1</value></field>" +
                "<field type=\"list-multi\" var=\"pubsub#show-values\"><value>away</value><value>chat</value><value>online</value></field>" +
                "<field type=\"list-single\" var=\"pubsub#subscription_type\"><value>nodes</value></field>" +
                "<field type=\"list-single\" var=\"pubsub#subscription_depth\"><value>all</value></field>" +
                "</x>");

        DataForm dataForm = unmarshal(xml, DataForm.class);
        SubscribeOptions subscribeOptions = new SubscribeOptions(dataForm);
        Assert.assertTrue(subscribeOptions.isDeliver());
        Assert.assertTrue(subscribeOptions.isDigest());
        Assert.assertEquals(subscribeOptions.getDigestFrequency(), Integer.valueOf(3));
        Assert.assertNull(subscribeOptions.getExpire());
        Assert.assertTrue(subscribeOptions.isTemporary());
        Assert.assertTrue(subscribeOptions.isIncludeBody());
        Assert.assertEquals(subscribeOptions.getShowValues(), Arrays.asList(Presence.Show.AWAY, Presence.Show.CHAT, null));
        Assert.assertEquals(subscribeOptions.getSubscriptionType(), SubscribeOptions.SubscriptionType.NODES);
        Assert.assertEquals(subscribeOptions.getSubscriptionDepth(), Integer.valueOf(-1));

    }
}
