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

package rocks.xmpp.extensions.si.profile.filetransfer;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.si.profile.filetransfer.model.SIFileTransferOffer;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.math.BigInteger;

/**
 * @author Christian Schudt
 */
public class FileTransferTest extends XmlTest {
    protected FileTransferTest() throws JAXBException {
        super(SIFileTransferOffer.class);
    }

    @Test
    public void unmarshalFileTransfer() throws XMLStreamException, JAXBException {
        String xml = "<file xmlns='http://jabber.org/protocol/si/profile/file-transfer'\n" +
                "          name='test.txt'\n" +
                "          size='1022'\n" +
                "          hash='552da749930852c69ae5d2141d3766b1'\n" +
                "          date='1969-07-21T02:56:15Z'>\n" +
                "      <desc>This is a test. If this were a real file...</desc>\n" +
                "    </file>";

        SIFileTransferOffer fileTransfer = unmarshal(xml, SIFileTransferOffer.class);
        Assert.assertNotNull(fileTransfer);
        Assert.assertEquals(fileTransfer.getName(), "test.txt");
        Assert.assertEquals(fileTransfer.getSize(), 1022);
        Assert.assertEquals(fileTransfer.getHashes().size(), 1);
        Assert.assertEquals(fileTransfer.getHashes().get(0).getHashAlgorithm(), "md5");
        Assert.assertEquals(new BigInteger(1, fileTransfer.getHashes().get(0).getHashValue()).toString(16), "552da749930852c69ae5d2141d3766b1");
        Assert.assertNotNull(fileTransfer.getDate());
        Assert.assertEquals(fileTransfer.getDescription(), "This is a test. If this were a real file...");
    }
}
