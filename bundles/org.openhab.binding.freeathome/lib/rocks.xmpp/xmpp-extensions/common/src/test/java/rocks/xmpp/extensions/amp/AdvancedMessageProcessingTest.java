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

package rocks.xmpp.extensions.amp;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.client.ClientMessage;
import rocks.xmpp.extensions.amp.model.AdvancedMessageProcessing;
import rocks.xmpp.extensions.amp.model.InvalidRules;
import rocks.xmpp.extensions.amp.model.Rule;
import rocks.xmpp.extensions.amp.model.UnsupportedActions;
import rocks.xmpp.extensions.amp.model.UnsupportedConditions;
import rocks.xmpp.extensions.amp.model.errors.FailedRules;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.time.Instant;
import java.util.Collections;

/**
 * @author Christian Schudt
 */
public class AdvancedMessageProcessingTest extends XmlTest {

    protected AdvancedMessageProcessingTest() throws JAXBException {
        super(ClientMessage.class, AdvancedMessageProcessing.class);
    }

    @Test
    public void unmarshalAmp() throws XMLStreamException, JAXBException {
        String xml = "<message\n" +
                "    from='northumberland@shakespeare.lit'\n" +
                "    id='richard2-4.1.247'\n" +
                "    to='kingrichard@royalty.england.lit'>\n" +
                "  <body>My lord, dispatch; read o'er these articles.</body>\n" +
                "  <amp xmlns='http://jabber.org/protocol/amp'\n" +
                "         per-hop='true'" +
                "         status='alert'\n" +
                "         from='bernardo@hamlet.lit/elsinore'\n" +
                "         to='francisco@hamlet.lit'>\n" +
                "    <rule condition='expire-at'\n" +
                "          action='drop'\n" +
                "          value='2004-01-01T00:00:00Z'/>\n" +
                "  </amp>\n" +
                "</message>\n";
        Message message = unmarshal(xml, Message.class);
        AdvancedMessageProcessing amp = message.getExtension(AdvancedMessageProcessing.class);
        Assert.assertNotNull(amp);
        Assert.assertTrue(amp.isPerHop());
        Assert.assertEquals(amp.getStatus(), Rule.Action.ALERT);
        Assert.assertEquals(amp.getFrom(), Jid.of("bernardo@hamlet.lit/elsinore"));
        Assert.assertEquals(amp.getRules().size(), 1);
        Assert.assertEquals(amp.getRules().get(0).getCondition(), Rule.Condition.EXPIRE_AT);
        Assert.assertEquals(amp.getRules().get(0).getAction(), Rule.Action.DROP);
        Assert.assertEquals(amp.getRules().get(0).getValue(), "2004-01-01T00:00:00Z");
    }

    @Test
    public void unmarshalAmpFailedRules() throws XMLStreamException, JAXBException {
        String xml = "<message from='hamlet.lit'\n" +
                "           to='bernardo@hamlet.lit/elsinore'\n" +
                "           type='error'\n" +
                "           id='chatty2'>\n" +
                "    <amp xmlns='http://jabber.org/protocol/amp'\n" +
                "         status='error'\n" +
                "         to='bernardo@hamlet.lit/elsinore'\n" +
                "         from='francisco@hamlet.lit'>\n" +
                "      <rule action='error' condition='deliver' value='stored'/>\n" +
                "    </amp>\n" +
                "    <error type='modify' code='500'>\n" +
                "      <undefined-condition xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "      <failed-rules xmlns='http://jabber.org/protocol/amp#errors'>\n" +
                "        <rule action='error' condition='deliver' value='stored'/>\n" +
                "      </failed-rules>\n" +
                "    </error>\n" +
                "  </message>\n";
        Message message = unmarshal(xml, Message.class);
        AdvancedMessageProcessing amp = message.getExtension(AdvancedMessageProcessing.class);
        Assert.assertNotNull(amp);
        Assert.assertEquals(amp.getStatus(), Rule.Action.ERROR);
        Assert.assertEquals(amp.getTo(), Jid.of("bernardo@hamlet.lit/elsinore"));
        Assert.assertEquals(amp.getRules().size(), 1);
        Assert.assertTrue(message.getError().getExtension() instanceof FailedRules);
        FailedRules failedRules = (FailedRules) (message.getError().getExtension());
        Assert.assertEquals(failedRules.getRules().size(), 1);
        Assert.assertEquals(failedRules.getRules().get(0).getAction(), Rule.Action.ERROR);
        Assert.assertEquals(failedRules.getRules().get(0).getCondition(), Rule.Condition.DELIVER);
        Assert.assertEquals(failedRules.getRules().get(0).getValue(), "stored");
    }

    @Test
    public void unmarshalAmpUnsupportedActions() throws XMLStreamException, JAXBException {
        String xml = "<message\n" +
                "    from='shakespeare.lit'\n" +
                "    id='richard2-4.1.247'\n" +
                "    to='northumberland@shakespeare.lit'\n" +
                "    type='error'>\n" +
                "  <amp xmlns='http://jabber.org/protocol/amp'>\n" +
                "    <rule action='drop' condition='expire-at' value='2004-01-01T00:00:00Z'/>\n" +
                "  </amp>\n" +
                "  <error type='modify' code='400'>\n" +
                "    <bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <unsupported-actions xmlns='http://jabber.org/protocol/amp'>\n" +
                "      <rule condition='expire-at'\n" +
                "            action='drop'\n" +
                "            value='2004-01-01T00:00:00Z'/>\n" +
                "    </unsupported-actions>\n" +
                "  </error>\n" +
                "</message>\n";
        Message message = unmarshal(xml, Message.class);
        AdvancedMessageProcessing amp = message.getExtension(AdvancedMessageProcessing.class);
        Assert.assertNotNull(amp);
        Assert.assertTrue(message.getError().getExtension() instanceof UnsupportedActions);
        UnsupportedActions ampError = (UnsupportedActions) message.getError().getExtension();
        Assert.assertEquals(ampError.getRules().size(), 1);
        Assert.assertEquals(ampError.getRules().get(0).getAction(), Rule.Action.DROP);
        Assert.assertEquals(ampError.getRules().get(0).getCondition(), Rule.Condition.EXPIRE_AT);
        Assert.assertEquals(ampError.getRules().get(0).getValue(), "2004-01-01T00:00:00Z");
    }

    @Test
    public void unmarshalAmpUnsupportedConditions() throws XMLStreamException, JAXBException {
        String xml = "<message\n" +
                "    from='shakespeare.lit'\n" +
                "    id='richard2-4.1.247'\n" +
                "    to='northumberland@shakespeare.lit'\n" +
                "    type='error'>\n" +
                "  <amp xmlns='http://jabber.org/protocol/amp'>\n" +
                "    <rule action='drop' condition='expire-at' value='2004-01-01T00:00:00Z'/>\n" +
                "  </amp>\n" +
                "  <error type='modify' code='400'>\n" +
                "    <bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <unsupported-conditions xmlns='http://jabber.org/protocol/amp'>\n" +
                "      <rule action='drop' condition='expire-at' value='2004-01-01T00:00:00Z'/>\n" +
                "    </unsupported-conditions>\n" +
                "  </error>\n" +
                "</message>\n";
        Message message = unmarshal(xml, Message.class);
        AdvancedMessageProcessing amp = message.getExtension(AdvancedMessageProcessing.class);
        Assert.assertNotNull(amp);
        Assert.assertTrue(message.getError().getExtension() instanceof UnsupportedConditions);
        UnsupportedConditions ampError = (UnsupportedConditions) message.getError().getExtension();
        Assert.assertEquals(ampError.getRules().size(), 1);
        Assert.assertEquals(ampError.getRules().get(0).getAction(), Rule.Action.DROP);
        Assert.assertEquals(ampError.getRules().get(0).getCondition(), Rule.Condition.EXPIRE_AT);
        Assert.assertEquals(ampError.getRules().get(0).getValue(), "2004-01-01T00:00:00Z");
    }

    @Test
    public void unmarshalAmpInvalidRules() throws XMLStreamException, JAXBException {
        String xml = "<message\n" +
                "    from='shakespeare.lit'\n" +
                "    id='richard2-4.1.247'\n" +
                "    to='northumberland@shakespeare.lit'\n" +
                "    type='error'>\n" +
                "  <amp xmlns='http://jabber.org/protocol/amp'>\n" +
                "    <rule action='drop' condition='expire-at' value='2004-01-01T00:00:00Z'/>\n" +
                "  </amp>\n" +
                "  <error type='modify' code='405'>\n" +
                "    <not-acceptable xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <invalid-rules xmlns='http://jabber.org/protocol/amp'>\n" +
                "      <rule action='drop' condition='expire-at' value='2004-01-01T00:00:00Z'/>\n" +
                "    </invalid-rules>\n" +
                "  </error>\n" +
                "</message>\n";
        Message message = unmarshal(xml, Message.class);
        AdvancedMessageProcessing amp = message.getExtension(AdvancedMessageProcessing.class);
        Assert.assertNotNull(amp);
        Assert.assertTrue(message.getError().getExtension() instanceof InvalidRules);
        InvalidRules ampError = (InvalidRules) message.getError().getExtension();
        Assert.assertEquals(ampError.getRules().size(), 1);
        Assert.assertEquals(ampError.getRules().get(0).getAction(), Rule.Action.DROP);
        Assert.assertEquals(ampError.getRules().get(0).getCondition(), Rule.Condition.EXPIRE_AT);
        Assert.assertEquals(ampError.getRules().get(0).getValue(), "2004-01-01T00:00:00Z");
    }

    @Test
    public void unmarshalAmpStreamFeature() throws XMLStreamException, JAXBException {
        String xml = "<amp xmlns='http://jabber.org/features/amp'/>\n";
        AdvancedMessageProcessing feature = unmarshal(xml, AdvancedMessageProcessing.class);
        Assert.assertNotNull(feature);
    }

    @Test
    public void marshalAmp() throws XMLStreamException, JAXBException {
        AdvancedMessageProcessing amp = new AdvancedMessageProcessing(Collections.emptyList());
        String xml = marshal(amp);
        Assert.assertEquals(xml, "<amp xmlns=\"http://jabber.org/protocol/amp\"></amp>");
    }

    @Test
    public void marshalDeliver() throws XMLStreamException, JAXBException {
        AdvancedMessageProcessing amp = new AdvancedMessageProcessing(Rule.deliver(Rule.Action.ALERT, Rule.DeliveryMode.FORWARD));
        String xml = marshal(amp);
        Assert.assertEquals(xml, "<amp xmlns=\"http://jabber.org/protocol/amp\"><rule action=\"alert\" condition=\"deliver\" value=\"forward\"></rule></amp>");
    }

    @Test
    public void marshalMatchResource() throws XMLStreamException, JAXBException {
        AdvancedMessageProcessing amp = new AdvancedMessageProcessing(Rule.matchResource(Rule.Action.ALERT, Rule.MatchResource.EXACT));
        String xml = marshal(amp);
        Assert.assertEquals(xml, "<amp xmlns=\"http://jabber.org/protocol/amp\"><rule action=\"alert\" condition=\"match-resource\" value=\"exact\"></rule></amp>");
    }

    @Test
    public void marshalExpireAt() throws XMLStreamException, JAXBException {
        Instant now = Instant.now();
        AdvancedMessageProcessing amp = new AdvancedMessageProcessing(Rule.expireAt(Rule.Action.ALERT, now));
        String xml = marshal(amp);
        Assert.assertEquals(xml, "<amp xmlns=\"http://jabber.org/protocol/amp\"><rule action=\"alert\" condition=\"expire-at\" value=\"" + now.toString() + "\"></rule></amp>");
    }
}
