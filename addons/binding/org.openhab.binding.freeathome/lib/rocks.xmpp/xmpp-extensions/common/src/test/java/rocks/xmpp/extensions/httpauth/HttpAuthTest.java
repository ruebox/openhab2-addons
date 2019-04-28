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

package rocks.xmpp.extensions.httpauth;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.httpauth.model.ConfirmationRequest;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class HttpAuthTest extends XmlTest {

    protected HttpAuthTest() throws JAXBException {
        super(ClientIQ.class, ConfirmationRequest.class);
    }

    @Test
    public void unmarshalConfirm() throws JAXBException, XMLStreamException {
        String xml = "<iq type='get' \n" +
                "    from='files.shakespeare.lit' \n" +
                "    to='juliet@capulet.com/balcony' \n" +
                "    id='ha000'>\n" +
                "  <confirm xmlns='http://jabber.org/protocol/http-auth'\n" +
                "           id='a7374jnjlalasdf82'\n" +
                "           method='GET'\n" +
                "           url='https://files.shakespeare.lit:9345/missive.html'/>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        ConfirmationRequest confirmationRequest = iq.getExtension(ConfirmationRequest.class);
        Assert.assertNotNull(confirmationRequest);
        Assert.assertEquals(confirmationRequest.getId(), "a7374jnjlalasdf82");
        Assert.assertEquals(confirmationRequest.getMethod(), "GET");
        Assert.assertEquals(confirmationRequest.getUrl().toString(), "https://files.shakespeare.lit:9345/missive.html");
    }
}
