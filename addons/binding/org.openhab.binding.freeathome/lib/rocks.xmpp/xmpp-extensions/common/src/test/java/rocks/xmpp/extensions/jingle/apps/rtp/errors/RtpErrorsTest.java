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

package rocks.xmpp.extensions.jingle.apps.rtp.errors;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.jingle.apps.rtp.model.Rtp;
import rocks.xmpp.extensions.jingle.apps.rtp.model.errors.CryptoRequired;
import rocks.xmpp.extensions.jingle.apps.rtp.model.errors.InvalidCrypto;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class RtpErrorsTest extends XmlTest {
    protected RtpErrorsTest() throws JAXBException {
        super(Rtp.class);
    }

    @Test
    public void unmarshalInvalidCrypto() throws XMLStreamException, JAXBException {
        String xml = "<invalid-crypto xmlns='urn:xmpp:jingle:apps:rtp:errors:1'/>\n";

        InvalidCrypto invalidCrypto = unmarshal(xml, InvalidCrypto.class);
        Assert.assertNotNull(invalidCrypto);
    }

    @Test
    public void unmarshalRtp() throws XMLStreamException, JAXBException {
        String xml = "<crypto-required xmlns='urn:xmpp:jingle:apps:rtp:errors:1'/>\n";

        CryptoRequired cryptoRequired = unmarshal(xml, CryptoRequired.class);
        Assert.assertNotNull(cryptoRequired);
    }
}
