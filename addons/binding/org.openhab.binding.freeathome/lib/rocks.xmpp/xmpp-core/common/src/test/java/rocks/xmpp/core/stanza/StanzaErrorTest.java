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

package rocks.xmpp.core.stanza;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.core.stanza.model.StanzaError;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.core.stanza.model.client.ClientMessage;
import rocks.xmpp.core.stanza.model.client.ClientPresence;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.core.stanza.model.errors.Gone;
import rocks.xmpp.core.stanza.model.errors.Redirect;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.util.Locale;

/**
 * @author Christian Schudt
 */
public class StanzaErrorTest extends XmlTest {

    public StanzaErrorTest() throws JAXBException {
        super(ClientIQ.class, ClientMessage.class, ClientPresence.class, StanzaError.class);
    }

    @Test
    public void unmarshalBadRequest() throws JAXBException, XMLStreamException {
        String xml = "<iq from='im.example.com'\n" +
                "       id='zj3v142b'\n" +
                "       to='juliet@im.example.com/balcony'\n" +
                "       type='error'>\n" +
                "     <error type='modify'>\n" +
                "       <bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "     </error>\n" +
                "   </iq>";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertEquals(iq.getType(), IQ.Type.ERROR);
        Assert.assertNotNull(iq.getError());
        Assert.assertEquals(iq.getError().getType(), StanzaError.Type.MODIFY);
        Assert.assertTrue(iq.getError().getCondition() == Condition.BAD_REQUEST);
    }

    @Test
    public void unmarshalConflict() throws JAXBException, XMLStreamException {
        String xml = "<iq id='wy2xa82b4' type='error'>\n" +
                "     <error type='cancel'>\n" +
                "       <conflict xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "     </error>\n" +
                "   </iq>";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertEquals(iq.getType(), IQ.Type.ERROR);
        Assert.assertNotNull(iq.getError());
        Assert.assertEquals(iq.getError().getType(), StanzaError.Type.CANCEL);
        Assert.assertTrue(iq.getError().getCondition() == Condition.CONFLICT);
    }

    @Test
    public void unmarshalFeatureNotImplemented() throws JAXBException, XMLStreamException {
        String xml = "<iq from='pubsub.example.com'\n" +
                "       id='9u2bax16'\n" +
                "       to='juliet@im.example.com/balcony'\n" +
                "       type='error'>\n" +
                "     <error type='cancel'>\n" +
                "       <feature-not-implemented\n" +
                "           xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "       <unsupported\n" +
                "           xmlns='http://jabber.org/protocol/pubsub#errors'\n" +
                "           feature='retrieve-subscriptions'/>\n" +
                "     </error>\n" +
                "   </iq>";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertEquals(iq.getType(), IQ.Type.ERROR);
        Assert.assertNotNull(iq.getError());
        Assert.assertEquals(iq.getError().getType(), StanzaError.Type.CANCEL);
        Assert.assertTrue(iq.getError().getCondition() == Condition.FEATURE_NOT_IMPLEMENTED);
    }

    @Test
    public void unmarshalForbidden() throws JAXBException, XMLStreamException {
        String xml = "<presence\n" +
                "       from='characters@muc.example.com/JulieC'\n" +
                "       id='y2bs71v4'\n" +
                "       to='juliet@im.example.com/balcony'\n" +
                "       type='error'>\n" +
                "     <error type='auth'>\n" +
                "       <forbidden xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "     </error>\n" +
                "   </presence>";
        Presence presence = unmarshal(xml, Presence.class);
        Assert.assertEquals(presence.getType(), Presence.Type.ERROR);
        Assert.assertNotNull(presence.getError());
        Assert.assertEquals(presence.getError().getType(), StanzaError.Type.AUTH);
        Assert.assertTrue(presence.getError().getCondition() == Condition.FORBIDDEN);
    }

    @Test
    public void unmarshalGone() throws JAXBException, XMLStreamException {
        String xml = "<message\n" +
                "       from='romeo@example.net'\n" +
                "       id='sj2b371v'\n" +
                "       to='juliet@im.example.com/churchyard'\n" +
                "       type='error'>\n" +
                "     <error by='example.net'\n" +
                "            type='cancel'>\n" +
                "       <gone xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'>xmpp:romeo@afterlife.example.net</gone>\n" +
                "     </error>\n" +
                "   </message>";
        Message message = unmarshal(xml, Message.class);
        Assert.assertEquals(message.getType(), Message.Type.ERROR);
        Assert.assertNotNull(message.getError());
        Assert.assertEquals(message.getError().getType(), StanzaError.Type.CANCEL);
        Assert.assertTrue(message.getError().getCondition() instanceof Gone);
        Assert.assertEquals(((Gone) message.getError().getCondition()).getNewAddress(), "xmpp:romeo@afterlife.example.net");
    }

    @Test
    public void unmarshalInternalServerError() throws JAXBException, XMLStreamException {
        String xml = "<presence\n" +
                "       from='characters@muc.example.com/JulieC'\n" +
                "       id='y2bs71v4'\n" +
                "       to='juliet@im.example.com/balcony'\n" +
                "       type='error'>\n" +
                "     <error type='cancel'>\n" +
                "       <internal-server-error\n" +
                "           xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "     </error>\n" +
                "   </presence>";
        Presence presence = unmarshal(xml, Presence.class);
        Assert.assertEquals(presence.getType(), Presence.Type.ERROR);
        Assert.assertNotNull(presence.getError());
        Assert.assertEquals(presence.getError().getType(), StanzaError.Type.CANCEL);
        Assert.assertTrue(presence.getError().getCondition() == Condition.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void unmarshalItemNotFound() throws JAXBException, XMLStreamException {
        String xml = "<presence from='nosuchroom@conference.example.org/foo'\n" +
                "             id='pwb2n78i'\n" +
                "             to='userfoo@example.com/bar'\n" +
                "             type='error'>\n" +
                "     <error type='cancel'>\n" +
                "       <item-not-found xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "     </error>\n" +
                "   </presence>";
        Presence presence = unmarshal(xml, Presence.class);
        Assert.assertEquals(presence.getType(), Presence.Type.ERROR);
        Assert.assertNotNull(presence.getError());
        Assert.assertEquals(presence.getError().getType(), StanzaError.Type.CANCEL);
        Assert.assertTrue(presence.getError().getCondition() == Condition.ITEM_NOT_FOUND);
    }

    @Test
    public void unmarshalJidMalformed() throws JAXBException, XMLStreamException {
        String xml = "<presence\n" +
                "       from='ch@r@cters@muc.example.com/JulieC'\n" +
                "       id='y2bs71v4'\n" +
                "       to='juliet@im.example.com/balcony'\n" +
                "       type='error'>\n" +
                "     <error by='muc.example.com'\n" +
                "            type='modify'>\n" +
                "       <jid-malformed\n" +
                "           xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "     </error>\n" +
                "   </presence>";
        Presence presence = unmarshal(xml, Presence.class);
        Assert.assertEquals(presence.getType(), Presence.Type.ERROR);
        Assert.assertNotNull(presence.getError());
        Assert.assertEquals(presence.getError().getType(), StanzaError.Type.MODIFY);
        Assert.assertTrue(presence.getError().getCondition() == Condition.JID_MALFORMED);
    }

    @Test
    public void unmarshalNotAcceptable() throws JAXBException, XMLStreamException {
        String xml = "<message from='juliet@im.example.com' id='yt2vs71m'>\n" +
                "     <error type='modify'>\n" +
                "       <not-acceptable\n" +
                "           xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "     </error>\n" +
                "   </message>";
        Message message = unmarshal(xml, Message.class);
        Assert.assertNotNull(message.getError());
        Assert.assertEquals(message.getError().getType(), StanzaError.Type.MODIFY);
        Assert.assertTrue(message.getError().getCondition() == Condition.NOT_ACCEPTABLE);
    }

    @Test
    public void unmarshalNotAllowed() throws JAXBException, XMLStreamException {
        String xml = "<presence\n" +
                "       from='characters@muc.example.com/JulieC'\n" +
                "       id='y2bs71v4'\n" +
                "       to='juliet@im.example.com/balcony'\n" +
                "       type='error'>\n" +
                "     <error type='cancel'>\n" +
                "       <not-allowed xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "     </error>\n" +
                "   </presence>";
        Presence presence = unmarshal(xml, Presence.class);
        Assert.assertEquals(presence.getType(), Presence.Type.ERROR);
        Assert.assertNotNull(presence.getError());
        Assert.assertEquals(presence.getError().getType(), StanzaError.Type.CANCEL);
        Assert.assertTrue(presence.getError().getCondition() == Condition.NOT_ALLOWED);
    }

    @Test
    public void unmarshalNotAuthorized() throws JAXBException, XMLStreamException {
        String xml = "<presence\n" +
                "       from='characters@muc.example.com/JulieC'\n" +
                "       id='y2bs71v4'\n" +
                "       to='juliet@im.example.com/balcony'>\n" +
                "     <error type='auth'>\n" +
                "       <not-authorized xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "     </error>\n" +
                "   </presence>";
        Presence presence = unmarshal(xml, Presence.class);
        Assert.assertNotNull(presence.getError());
        Assert.assertEquals(presence.getError().getType(), StanzaError.Type.AUTH);
        Assert.assertTrue(presence.getError().getCondition() == Condition.NOT_AUTHORIZED);
    }

    @Test
    public void unmarshalPolicyViolation() throws JAXBException, XMLStreamException {
        String xml = "<message from='bill@im.example.com'\n" +
                "            id='vq71f4nb'\n" +
                "            to='romeo@example.net/foo'>\n" +
                "     <error by='example.net' type='modify'>\n" +
                "       <policy-violation\n" +
                "           xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "     </error>\n" +
                "   </message>";
        Message message = unmarshal(xml, Message.class);
        Assert.assertNotNull(message.getError());
        Assert.assertEquals(message.getError().getType(), StanzaError.Type.MODIFY);
        Assert.assertTrue(message.getError().getCondition() == Condition.POLICY_VIOLATION);
    }

    @Test
    public void unmarshalRecipientUnavailable() throws JAXBException, XMLStreamException {
        String xml = "<presence\n" +
                "       from='characters@muc.example.com/JulieC'\n" +
                "       id='y2bs71v4'\n" +
                "       to='juliet@im.example.com/balcony'>\n" +
                "     <error type='wait'>\n" +
                "       <recipient-unavailable\n" +
                "           xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "     </error>\n" +
                "   </presence>";
        Presence presence = unmarshal(xml, Presence.class);
        Assert.assertNotNull(presence.getError());
        Assert.assertEquals(presence.getError().getType(), StanzaError.Type.WAIT);
        Assert.assertTrue(presence.getError().getCondition() == Condition.RECIPIENT_UNAVAILABLE);
    }

    @Test
    public void unmarshalRedirect() throws JAXBException, XMLStreamException {
        String xml = "<presence\n" +
                "       from='characters@muc.example.com/JulieC'\n" +
                "       id='y2bs71v4'\n" +
                "       to='juliet@im.example.com/balcony'\n" +
                "       type='error'>\n" +
                "     <error type='modify'>\n" +
                "       <redirect xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'>xmpp:characters@conference.example.org</redirect>\n" +
                "     </error>\n" +
                "   </presence>";
        Presence presence = unmarshal(xml, Presence.class);
        Assert.assertNotNull(presence.getError());
        Assert.assertEquals(presence.getType(), Presence.Type.ERROR);
        Assert.assertEquals(presence.getError().getType(), StanzaError.Type.MODIFY);
        Assert.assertTrue(presence.getError().getCondition() instanceof Redirect);
        Assert.assertEquals(((Redirect) presence.getError().getCondition()).getAlternateAddress(), "xmpp:characters@conference.example.org");
    }

    @Test
    public void unmarshalRegistrationRequired() throws JAXBException, XMLStreamException {
        String xml = "<presence\n" +
                "       from='characters@muc.example.com/JulieC'\n" +
                "       id='y2bs71v4'\n" +
                "       to='juliet@im.example.com/balcony'>\n" +
                "     <error type='auth'>\n" +
                "       <registration-required\n" +
                "           xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "     </error>\n" +
                "   </presence>";
        Presence presence = unmarshal(xml, Presence.class);
        Assert.assertNotNull(presence.getError());
        Assert.assertEquals(presence.getError().getType(), StanzaError.Type.AUTH);
        Assert.assertTrue(presence.getError().getCondition() == Condition.REGISTRATION_REQUIRED);
    }

    @Test
    public void unmarshalRemoteServerNotFound() throws JAXBException, XMLStreamException {
        String xml = "<message\n" +
                "       from='bar@example.org'\n" +
                "       id='ud7n1f4h'\n" +
                "       to='romeo@example.net/home'\n" +
                "       type='error'>\n" +
                "     <error type='cancel'>\n" +
                "       <remote-server-not-found\n" +
                "           xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "     </error>\n" +
                "   </message>";
        Message message = unmarshal(xml, Message.class);
        Assert.assertNotNull(message.getError());
        Assert.assertEquals(message.getError().getType(), StanzaError.Type.CANCEL);
        Assert.assertTrue(message.getError().getCondition() == Condition.REMOTE_SERVER_NOT_FOUND);
    }

    @Test
    public void unmarshalRemoteServerTimeout() throws JAXBException, XMLStreamException {
        String xml = "<message\n" +
                "       from='bar@example.org'\n" +
                "       id='ud7n1f4h'\n" +
                "       to='romeo@example.net/home'\n" +
                "       type='error'>\n" +
                "     <error type='wait'>\n" +
                "       <remote-server-timeout\n" +
                "           xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "     </error>\n" +
                "   </message>";
        Message message = unmarshal(xml, Message.class);
        Assert.assertNotNull(message.getError());
        Assert.assertEquals(message.getError().getType(), StanzaError.Type.WAIT);
        Assert.assertTrue(message.getError().getCondition() == Condition.REMOTE_SERVER_TIMEOUT);
    }

    @Test
    public void unmarshalResourceConstraint() throws JAXBException, XMLStreamException {
        String xml = "<iq from='pubsub.example.com'\n" +
                "       id='kj4vz31m'\n" +
                "       to='romeo@example.net/foo'\n" +
                "       type='error'>\n" +
                "     <error type='wait'>\n" +
                "       <resource-constraint\n" +
                "           xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "     </error>\n" +
                "   </iq>";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertNotNull(iq.getError());
        Assert.assertEquals(iq.getType(), IQ.Type.ERROR);
        Assert.assertEquals(iq.getError().getType(), StanzaError.Type.WAIT);
        Assert.assertTrue(iq.getError().getCondition() == Condition.RESOURCE_CONSTRAINT);
    }

    @Test
    public void unmarshalRemoteServiceUnavailable() throws JAXBException, XMLStreamException {
        String xml = "<message from='juliet@im.example.com/foo'\n" +
                "            to='romeo@example.net'>\n" +
                "     <error type='cancel'>\n" +
                "       <service-unavailable\n" +
                "           xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "     </error>\n" +
                "   </message>";
        Message message = unmarshal(xml, Message.class);
        Assert.assertNotNull(message.getError());
        Assert.assertEquals(message.getError().getType(), StanzaError.Type.CANCEL);
        Assert.assertTrue(message.getError().getCondition() == Condition.SERVICE_UNAVAILABLE);
    }

    @Test
    public void unmarshalRemoteSubscriptionRequired() throws JAXBException, XMLStreamException {
        String xml = "<message\n" +
                "       from='playwright@shakespeare.example.com'\n" +
                "       id='pa73b4n7'\n" +
                "       to='romeo@example.net/orchard'\n" +
                "       type='error'>\n" +
                "     <error type='auth'>\n" +
                "       <subscription-required\n" +
                "           xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "     </error>\n" +
                "   </message>";
        Message message = unmarshal(xml, Message.class);
        Assert.assertNotNull(message.getError());
        Assert.assertEquals(message.getType(), Message.Type.ERROR);
        Assert.assertEquals(message.getError().getType(), StanzaError.Type.AUTH);
        Assert.assertTrue(message.getError().getCondition() == Condition.SUBSCRIPTION_REQUIRED);
    }

    @Test
    public void unmarshalRemoteUndefinedCondition() throws JAXBException, XMLStreamException {
        String xml = "<message from='example.org'\n" +
                "            id='amp1'\n" +
                "            to='northumberland@example.net/field'\n" +
                "            type='error'>\n" +
                "     <amp xmlns='http://jabber.org/protocol/amp'\n" +
                "          from='kingrichard@example.org'\n" +
                "          status='error'\n" +
                "          to='northumberland@example.net/field'>\n" +
                "       <rule action='error'\n" +
                "             condition='deliver'\n" +
                "             value='stored'/>\n" +
                "     </amp>\n" +
                "     <error type='modify'>\n" +
                "       <undefined-condition\n" +
                "           xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "       <failed-rules xmlns='http://jabber.org/protocol/amp#errors'>\n" +
                "         <rule action='error'\n" +
                "               condition='deliver'\n" +
                "               value='stored'/>\n" +
                "       </failed-rules>\n" +
                "     </error>\n" +
                "   </message>";
        Message message = unmarshal(xml, Message.class);
        Assert.assertNotNull(message.getError());
        Assert.assertEquals(message.getType(), Message.Type.ERROR);
        Assert.assertEquals(message.getError().getType(), StanzaError.Type.MODIFY);
        Assert.assertTrue(message.getError().getCondition() == Condition.UNDEFINED_CONDITION);
    }

    @Test
    public void unmarshalUnexpectedRequest() throws JAXBException, XMLStreamException {
        String xml = "<iq from='pubsub.example.com'\n" +
                "       id='o6hsv25z'\n" +
                "       to='romeo@example.net/foo'\n" +
                "       type='error'>\n" +
                "     <error type='modify'>\n" +
                "       <unexpected-request\n" +
                "           xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "       <not-subscribed\n" +
                "           xmlns='http://jabber.org/protocol/pubsub#errors'/>\n" +
                "     </error>\n" +
                "   </iq>";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertNotNull(iq.getError());
        Assert.assertEquals(iq.getType(), IQ.Type.ERROR);
        Assert.assertEquals(iq.getError().getType(), StanzaError.Type.MODIFY);
        Assert.assertTrue(iq.getError().getCondition() == Condition.UNEXPECTED_REQUEST);
    }

    @Test
    public void unmarshalApplicationSpecificCondition() throws JAXBException, XMLStreamException {
        String xml = "<message type='error' id='7h3baci9'>\n" +
                "  <error type='modify' by='romeo@example.net/foo'>\n" +
                "    <undefined-condition\n" +
                "          xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <text xml:lang='en'\n" +
                "          xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'>[ ... application-specific information ... ]</text>\n" +
                "    <too-many-parameters xmlns='http://example.org/ns'/>\n" +
                "  </error>\n" +
                "</message>";
        Message message = unmarshal(xml, Message.class);
        Assert.assertNotNull(message.getError());
        Assert.assertEquals(message.getError().getType(), StanzaError.Type.MODIFY);
        Assert.assertEquals(message.getError().getText(), "[ ... application-specific information ... ]");
        Assert.assertEquals(message.getError().getLanguage(), Locale.ENGLISH);
        Assert.assertEquals(message.getError().getBy().toString(), "romeo@example.net/foo");
        Assert.assertTrue(message.getError().getExtension() instanceof Element);
        Assert.assertEquals(((Element) message.getError().getExtension()).getTagName(), "too-many-parameters");
    }

    @Test
    public void unmarshalUnknownFutureCondition() throws JAXBException, XMLStreamException {
        String xml = "<iq from='im.example.com'\n" +
                "       id='zj3v142b'\n" +
                "       to='juliet@im.example.com/balcony'\n" +
                "       type='error'>\n" +
                "     <error type='continue'>\n" +
                "       <unknown xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "     </error>\n" +
                "   </iq>";
        IQ iq = unmarshal(xml, IQ.class);
        Assert.assertEquals(iq.getType(), IQ.Type.ERROR);
        Assert.assertNotNull(iq.getError());
        Assert.assertEquals(iq.getError().getType(), StanzaError.Type.CONTINUE);
        Assert.assertTrue(iq.getError().getCondition() == Condition.UNDEFINED_CONDITION);
    }

    @Test
    public void marshalCondition() throws JAXBException, XMLStreamException {
        String xml = "<iq id=\"1\" type=\"error\"><error type=\"wait\"><unexpected-request xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"></unexpected-request></error></iq>";
        StanzaError error = new StanzaError(Condition.UNEXPECTED_REQUEST);
        IQ iq = new IQ(null, IQ.Type.ERROR, null, "1", null, null, error);
        Assert.assertEquals(marshal(iq), xml);
    }
}
