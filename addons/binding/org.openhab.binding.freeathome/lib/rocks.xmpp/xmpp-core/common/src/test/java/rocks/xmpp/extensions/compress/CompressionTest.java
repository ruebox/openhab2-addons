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

package rocks.xmpp.extensions.compress;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.compress.model.StreamCompression;
import rocks.xmpp.extensions.compress.model.feature.CompressionFeature;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class CompressionTest extends XmlTest {
    protected CompressionTest() throws JAXBException {
        super(StreamCompression.class);
    }

    @Test
    public void unmarshalCompression() throws XMLStreamException, JAXBException {
        String xml = "<compression xmlns='http://jabber.org/features/compress'>\n" +
                "    <method>zlib</method>\n" +
                "    <method>lzw</method>\n" +
                "  </compression>\n";
        CompressionFeature compressionFeature = unmarshal(xml, CompressionFeature.class);

        Assert.assertNotNull(compressionFeature);
        Assert.assertEquals(compressionFeature.getMethods().size(), 2);
        Assert.assertEquals(compressionFeature.getMethods().get(0), "zlib");
        Assert.assertEquals(compressionFeature.getMethods().get(1), "lzw");
    }

    @Test
    public void unmarshalCompressed() throws XMLStreamException, JAXBException {
        String xml = "<compressed xmlns='http://jabber.org/protocol/compress'/>\n";
        Object compressed = unmarshal(xml);

        Assert.assertTrue(compressed == StreamCompression.COMPRESSED);
    }

    @Test
    public void marshalCompress() throws JAXBException, XMLStreamException {
        StreamCompression.Compress compress = new StreamCompression.Compress("zlib");
        String xml = marshal(compress);
        Assert.assertEquals(xml, "<compress xmlns=\"http://jabber.org/protocol/compress\"><method>zlib</method></compress>");
    }

    @Test
    public void unmarshalFailureUnsupportedMethod() throws XMLStreamException, JAXBException {
        String xml = "<failure xmlns='http://jabber.org/protocol/compress'>\n" +
                "  <unsupported-method/>\n" +
                "</failure>\n";
        StreamCompression.Failure failure = unmarshal(xml, StreamCompression.Failure.class);

        Assert.assertNotNull(failure);
        Assert.assertTrue(failure.getCondition() == StreamCompression.Failure.Condition.UNSUPPORTED_METHOD);
    }

    @Test
    public void unmarshalFailureSetupFailed() throws XMLStreamException, JAXBException {
        String xml = "<failure xmlns='http://jabber.org/protocol/compress'>\n" +
                "  <setup-failed/>\n" +
                "</failure>\n";
        StreamCompression.Failure failure = unmarshal(xml, StreamCompression.Failure.class);
        Assert.assertNotNull(failure);
        Assert.assertTrue(failure.getCondition() == StreamCompression.Failure.Condition.SETUP_FAILED);
    }

    @Test
    public void unmarshalFailureProcessingFailed() throws XMLStreamException, JAXBException {
        String xml = "<failure xmlns='http://jabber.org/protocol/compress'>\n" +
                "  <processing-failed/>\n" +
                "</failure>\n";
        StreamCompression.Failure failure = unmarshal(xml, StreamCompression.Failure.class);

        Assert.assertNotNull(failure);
        Assert.assertTrue(failure.getCondition() == StreamCompression.Failure.Condition.PROCESSING_FAILED);
    }

    @Test
    public void marshalFailureProcessingFailed() throws XMLStreamException, JAXBException {
        String xml = marshal(new StreamCompression.Failure(StreamCompression.Failure.Condition.PROCESSING_FAILED));
        Assert.assertEquals(xml, "<failure xmlns=\"http://jabber.org/protocol/compress\"><processing-failed></processing-failed></failure>");
    }
    @Test
    public void marshalFailureSetupFailed() throws XMLStreamException, JAXBException {
        String xml = marshal(new StreamCompression.Failure(StreamCompression.Failure.Condition.UNSUPPORTED_METHOD));
        Assert.assertEquals(xml, "<failure xmlns=\"http://jabber.org/protocol/compress\"><unsupported-method></unsupported-method></failure>");
    }
    @Test
    public void marshalFailureUnsupportedMethod() throws XMLStreamException, JAXBException {
        String xml = marshal(new StreamCompression.Failure(StreamCompression.Failure.Condition.SETUP_FAILED));
        Assert.assertEquals(xml, "<failure xmlns=\"http://jabber.org/protocol/compress\"><setup-failed></setup-failed></failure>");
    }
}
