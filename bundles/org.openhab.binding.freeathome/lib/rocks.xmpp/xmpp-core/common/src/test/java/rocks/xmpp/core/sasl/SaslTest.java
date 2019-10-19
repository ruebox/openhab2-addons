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

package rocks.xmpp.core.sasl;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.sasl.model.Abort;
import rocks.xmpp.core.sasl.model.Auth;
import rocks.xmpp.core.sasl.model.Challenge;
import rocks.xmpp.core.sasl.model.Failure;
import rocks.xmpp.core.sasl.model.Response;
import rocks.xmpp.core.sasl.model.Success;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.util.Locale;

/**
 * @author Christian Schudt
 */
public class SaslTest extends XmlTest {

    protected SaslTest() throws JAXBException {
        super(Auth.class, Challenge.class, Response.class, Success.class, Abort.class, Failure.class);
    }

    @Test
    public void unmarshalAuth() throws XMLStreamException, JAXBException {
        String xml = "<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl'/>";
        Auth auth = unmarshal(xml, Auth.class);
        Assert.assertNotNull(auth);
    }

    @Test
    public void unmarshalChallenge() throws XMLStreamException, JAXBException {
        String xml = "<challenge xmlns='urn:ietf:params:xml:ns:xmpp-sasl'/>";
        Challenge challenge = unmarshal(xml, Challenge.class);
        Assert.assertNotNull(challenge);
    }

    @Test
    public void unmarshalResponse() throws XMLStreamException, JAXBException {
        String xml = "<response xmlns='urn:ietf:params:xml:ns:xmpp-sasl'/>";
        Response response = unmarshal(xml, Response.class);
        Assert.assertNotNull(response);
    }

    @Test
    public void unmarshalAbort() throws XMLStreamException, JAXBException {
        String xml = "<abort xmlns='urn:ietf:params:xml:ns:xmpp-sasl'/>";
        Abort abort = unmarshal(xml, Abort.class);
        Assert.assertTrue(Abort.INSTANCE == abort);
    }

    @Test
    public void unmarshalSuccess() throws XMLStreamException, JAXBException {
        String xml = "<success xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>base64</success>";
        Success success = unmarshal(xml, Success.class);
        Assert.assertNotNull(success);
        Assert.assertNotNull(success.getAdditionalData());
    }

    @Test
    public void unmarshalAccountDisabled() throws XMLStreamException, JAXBException {
        String xml = "<failure xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>\n" +
                "     <account-disabled/>\n" +
                "     <text xml:lang='en'>Call 212-555-1212 for assistance.</text>\n" +
                "   </failure>";
        Failure failure = unmarshal(xml, Failure.class);
        Assert.assertNotNull(failure);
        Assert.assertTrue(failure.getCondition() == Failure.Condition.ACCOUNT_DISABLED);
        Assert.assertEquals(failure.getLanguage(), Locale.ENGLISH);
        Assert.assertEquals(failure.getText(), "Call 212-555-1212 for assistance.");
    }

    @Test
    public void unmarshalCredentialsExpired() throws XMLStreamException, JAXBException {
        String xml = "<failure xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>\n" +
                "     <credentials-expired/>\n" +
                "   </failure>";
        Failure failure = unmarshal(xml, Failure.class);
        Assert.assertNotNull(failure);
        Assert.assertTrue(failure.getCondition() == Failure.Condition.CREDENTIALS_EXPIRED);
    }

    @Test
    public void unmarshalEncryptionRequired() throws XMLStreamException, JAXBException {
        String xml = "<failure xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>\n" +
                "     <encryption-required/>\n" +
                "   </failure>";
        Failure failure = unmarshal(xml, Failure.class);
        Assert.assertNotNull(failure);
        Assert.assertTrue(failure.getCondition() == Failure.Condition.ENCRYPTION_REQUIRED);
    }

    @Test
    public void unmarshalIncorrectEncoding() throws XMLStreamException, JAXBException {
        String xml = "<failure xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>\n" +
                "     <incorrect-encoding/>\n" +
                "   </failure>";
        Failure failure = unmarshal(xml, Failure.class);
        Assert.assertNotNull(failure);
        Assert.assertTrue(failure.getCondition() == Failure.Condition.INCORRECT_ENCODING);
    }

    @Test
    public void unmarshalInvalidAuthzid() throws XMLStreamException, JAXBException {
        String xml = "<failure xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>\n" +
                "     <invalid-authzid/>\n" +
                "   </failure>";
        Failure failure = unmarshal(xml, Failure.class);
        Assert.assertNotNull(failure);
        Assert.assertTrue(failure.getCondition() == Failure.Condition.INVALID_AUTHZID);
    }

    @Test
    public void unmarshalMalformedRequest() throws XMLStreamException, JAXBException {
        String xml = "<failure xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>\n" +
                "     <malformed-request/>\n" +
                "   </failure>";
        Failure failure = unmarshal(xml, Failure.class);
        Assert.assertNotNull(failure);
        Assert.assertTrue(failure.getCondition() == Failure.Condition.MALFORMED_REQUEST);
    }

    @Test
    public void unmarshalMechanismTooWeak() throws XMLStreamException, JAXBException {
        String xml = "<failure xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>\n" +
                "     <mechanism-too-weak/>\n" +
                "   </failure>";
        Failure failure = unmarshal(xml, Failure.class);
        Assert.assertNotNull(failure);
        Assert.assertTrue(failure.getCondition() == Failure.Condition.MECHANISM_TOO_WEAK);
    }

    @Test
    public void unmarshalInvalidMechanism() throws XMLStreamException, JAXBException {
        String xml = "<failure xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>\n" +
                "     <invalid-mechanism/>\n" +
                "   </failure>";
        Failure failure = unmarshal(xml, Failure.class);
        Assert.assertNotNull(failure);
        Assert.assertTrue(failure.getCondition() == Failure.Condition.INVALID_MECHANISM);
    }

    @Test
    public void unmarshalNotAuthorized() throws XMLStreamException, JAXBException {
        String xml = "<failure xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>\n" +
                "     <not-authorized/>\n" +
                "   </failure>";
        Failure failure = unmarshal(xml, Failure.class);
        Assert.assertNotNull(failure);
        Assert.assertTrue(failure.getCondition() == Failure.Condition.NOT_AUTHORIZED);
    }

    @Test
    public void unmarshalTemporaryAuthFailure() throws XMLStreamException, JAXBException {
        String xml = "<failure xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>\n" +
                "     <temporary-auth-failure/>\n" +
                "   </failure>";
        Failure failure = unmarshal(xml, Failure.class);
        Assert.assertNotNull(failure);
        Assert.assertTrue(failure.getCondition() == Failure.Condition.TEMPORARY_AUTH_FAILURE);
    }

    @Test
    public void marshalAuth() throws XMLStreamException, JAXBException {
        String xml = marshal(new Auth("PLAIN", null));
        Assert.assertEquals(xml, "<auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"PLAIN\"></auth>");
        String xml2 = marshal(new Auth("PLAIN", new byte[0]));
        Assert.assertEquals(xml2, "<auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"PLAIN\">=</auth>");
    }

    @Test
    public void unmarshalEmptyAuth() throws XMLStreamException, JAXBException {
        Auth auth = unmarshal("<auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"PLAIN\"></auth>", Auth.class);
        Auth authEmpty = unmarshal("<auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"PLAIN\">=</auth>", Auth.class);
        Assert.assertEquals(auth.getInitialResponse(), null);
        Assert.assertEquals(authEmpty.getInitialResponse(), new byte[0]);
    }
}
