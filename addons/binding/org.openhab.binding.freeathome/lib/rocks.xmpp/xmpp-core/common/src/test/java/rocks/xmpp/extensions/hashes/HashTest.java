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

package rocks.xmpp.extensions.hashes;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.hashes.model.Hash;
import rocks.xmpp.extensions.hashes.model.HashUsed;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Christian Schudt
 */
public class HashTest extends XmlTest {

    protected HashTest() throws JAXBException {
        super(Hash.class);
    }

    @Test
    public void unmarshalHash() throws JAXBException, XMLStreamException {
        String xml = "<hash xmlns='urn:xmpp:hashes:2' algo='sha-1'>2AfMGH8O7UNPTvUVAM9aK13mpCY=</hash>\n";
        Hash hash = unmarshal(xml, Hash.class);
        Assert.assertNotNull(hash);
        Assert.assertEquals(hash.getHashAlgorithm(), "sha-1");
        Assert.assertEquals(DatatypeConverter.printBase64Binary(hash.getHashValue()), "2AfMGH8O7UNPTvUVAM9aK13mpCY=");
    }

    @Test
    public void unmarshalHashUsed() throws JAXBException, XMLStreamException {
        String xml = "<hash-used xmlns='urn:xmpp:hashes:2' algo='sha-1'/>";
        HashUsed hashUsed = unmarshal(xml, HashUsed.class);
        Assert.assertNotNull(hashUsed);
        Assert.assertEquals(hashUsed.getHashAlgorithm(), "sha-1");
    }

    @Test
    public void testEquals() throws JAXBException, XMLStreamException {
        String xml = "<hash xmlns='urn:xmpp:hashes:2' algo='sha-1'>2AfMGH8O7UNPTvUVAM9aK13mpCY=</hash>\n";
        Hash hash1 = unmarshal(xml, Hash.class);
        Hash hash2 = unmarshal(xml, Hash.class);

        Set<Hash> hashes = new HashSet<>();
        Assert.assertTrue(hashes.add(hash1));
        Assert.assertFalse(hashes.add(hash2));
    }
}
