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

package rocks.xmpp.core.stream;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stream.model.StreamError;
import rocks.xmpp.core.stream.model.errors.Condition;
import rocks.xmpp.core.stream.model.errors.SeeOtherHost;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.util.Locale;

/**
 * @author Christian Schudt
 */
public class StreamErrorTest extends XmlTest {

    protected StreamErrorTest() throws JAXBException {
        super(StreamError.class);
    }

    @Test
    public void unmarshalBadFormat() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <bad-format\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.BAD_FORMAT);
    }

    @Test
    public void unmarshalBadNamespacePrefix() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <bad-namespace-prefix\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.BAD_NAMESPACE_PREFIX);
    }

    @Test
    public void unmarshalConflict() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <conflict\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.CONFLICT);
    }

    @Test
    public void unmarshalConnectionTimeout() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <connection-timeout\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.CONNECTION_TIMEOUT);
    }

    @Test
    public void unmarshalHostGone() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <host-gone\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.HOST_GONE);
    }

    @Test
    public void unmarshalHostUnknown() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <host-unknown\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.HOST_UNKNOWN);
    }

    @Test
    public void unmarshalImproperAddressing() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <improper-addressing\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.IMPROPER_ADDRESSING);
    }

    @Test
    public void unmarshalInternalServerError() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <internal-server-error\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void unmarshalInvalidFrom() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <invalid-from\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.INVALID_FROM);
    }

    @Test
    public void unmarshalInvalidNamespace() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <invalid-namespace\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.INVALID_NAMESPACE);
    }

    @Test
    public void unmarshalInvalidXml() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <invalid-xml\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.INVALID_XML);
    }

    @Test
    public void unmarshalNotAuthorized() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <not-authorized\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.NOT_AUTHORIZED);
    }

    @Test
    public void unmarshalNotWellFormed() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <not-well-formed\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.NOT_WELL_FORMED);
    }

    @Test
    public void unmarshalPolicyViolation() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <policy-violation\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.POLICY_VIOLATION);
    }

    @Test
    public void unmarshalRemoteConnectionFailed() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <remote-connection-failed\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.REMOTE_CONNECTION_FAILED);
    }

    @Test
    public void unmarshalReset() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <reset\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.RESET);
    }

    @Test
    public void unmarshalResourceConstraint() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <resource-constraint\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.RESOURCE_CONSTRAINT);
    }

    @Test
    public void unmarshalRestrictedXml() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <restricted-xml\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.RESTRICTED_XML);
    }

    @Test
    public void unmarshalSeeOtherHost() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <see-other-host\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'>[2001:41D0:1:A49b::1]:9222</see-other-host>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() instanceof SeeOtherHost);
        Assert.assertEquals(((SeeOtherHost) streamError.getCondition()).getOtherHost(), "[2001:41D0:1:A49b::1]:9222");
    }

    @Test
    public void unmarshalSystemShutdown() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <system-shutdown\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.SYSTEM_SHUTDOWN);
    }

    @Test
    public void unmarshalUndefinedCondition() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <undefined-condition\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.UNDEFINED_CONDITION);
    }

    @Test
    public void unmarshalUnsupportedEncoding() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <unsupported-encoding\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.UNSUPPORTED_ENCODING);
    }

    @Test
    public void unmarshalUnsupportedFeature() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <unsupported-feature\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.UNSUPPORTED_FEATURE);
    }

    @Test
    public void unmarshalUnsupportedStanzaType() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <unsupported-stanza-type\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.UNSUPPORTED_STANZA_TYPE);
    }

    @Test
    public void shouldUnmarshalUnsupportedVersion() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <unsupported-version\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertTrue(streamError.getCondition() == Condition.UNSUPPORTED_VERSION);
    }

    @Test
    public void shouldUnmarshalErrorWithText() throws JAXBException, XMLStreamException {
        String xml = "<stream:error>\n" +
                "     <host-unknown\n" +
                "         xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>\n" +
                "     <text xmlns='urn:ietf:params:xml:ns:xmpp-streams'\n" +
                "           xml:lang='en'>OPTIONAL descriptive text</text>" +
                "   </stream:error>";
        StreamError streamError = unmarshal(xml, StreamError.class);
        Assert.assertEquals(streamError.getText(), "OPTIONAL descriptive text");
        Assert.assertEquals(streamError.getLanguage(), Locale.ENGLISH);
        Assert.assertTrue(streamError.getCondition() == Condition.HOST_UNKNOWN);
    }
}
