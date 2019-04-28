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

package rocks.xmpp.core.tls;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.tls.model.Failure;
import rocks.xmpp.core.tls.model.Proceed;
import rocks.xmpp.core.tls.model.StartTls;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class TlsTest extends XmlTest {

    protected TlsTest() throws JAXBException {
        super(StartTls.class, Proceed.class, Failure.class);
    }

    @Test
    public void unmarshalStartTls() throws XMLStreamException, JAXBException {
        String xml = "<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'/>";
        StartTls startTls = unmarshal(xml, StartTls.class);
        Assert.assertNotNull(startTls);
    }

    @Test
    public void unmarshalProceed() throws XMLStreamException, JAXBException {
        String xml = "<proceed xmlns='urn:ietf:params:xml:ns:xmpp-tls'/>";
        Proceed proceed = unmarshal(xml, Proceed.class);
        Proceed proceed1 = unmarshal(xml, Proceed.class);
        Assert.assertNotNull(proceed);
        Assert.assertTrue(proceed == proceed1);
    }

    @Test
    public void unmarshalFailure() throws XMLStreamException, JAXBException {
        String xml = "<failure xmlns='urn:ietf:params:xml:ns:xmpp-tls'/>";
        Failure failure = unmarshal(xml, Failure.class);
        Failure failure1 = unmarshal(xml, Failure.class);
        Assert.assertNotNull(failure);
        Assert.assertTrue(failure == failure1);
    }

    @Test
    public void marshalStartTls() throws JAXBException, XMLStreamException {
        String xml = marshal(new StartTls());
        Assert.assertEquals(xml, "<starttls xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\"></starttls>");
    }
}
