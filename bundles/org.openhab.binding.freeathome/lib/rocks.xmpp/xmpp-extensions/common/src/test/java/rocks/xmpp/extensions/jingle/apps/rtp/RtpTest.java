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

package rocks.xmpp.extensions.jingle.apps.rtp;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.jingle.apps.rtp.model.Rtp;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class RtpTest extends XmlTest {
    protected RtpTest() throws JAXBException {
        super(Rtp.class);
    }

    @Test
    public void unmarshalRtp() throws XMLStreamException, JAXBException {
        String xml = "<description xmlns='urn:xmpp:jingle:apps:rtp:1' media='audio'>\n" +
                "        <payload-type id='96' name='speex' clockrate='16000'/>\n" +
                "        <payload-type id='97' name='speex' clockrate='8000'/>\n" +
                "        <payload-type id='18' name='G729'/>\n" +
                "        <payload-type id='0' name='PCMU'/>\n" +
                "        <payload-type id='103' name='L16' clockrate='16000' channels='2'/>\n" +
                "        <payload-type id='98' name='x-ISAC' clockrate='8000'/>\n" +
                "        <payload-type id='96' name='speex' clockrate='16000' ptime='40'>\n" +
                "           <parameter name='vbr' value='on'/>\n" +
                "           <parameter name='cng' value='on'/>\n" +
                "        </payload-type>\n" +
                "      </description>\n";

        Rtp rtp = unmarshal(xml, Rtp.class);
        Assert.assertEquals(rtp.getMedia(), "audio");
        Assert.assertEquals(rtp.getPayloadTypes().size(), 7);
        Assert.assertEquals(rtp.getPayloadTypes().get(0).getName(), "speex");
        Assert.assertEquals(rtp.getPayloadTypes().get(0).getClockRate(), 16000);
        Assert.assertEquals(rtp.getPayloadTypes().get(4).getChannels(), 2);
        Assert.assertEquals(rtp.getPayloadTypes().get(6).getParameters().size(), 2);
        Assert.assertEquals(rtp.getPayloadTypes().get(6).getParameters().get(0).getName(), "vbr");
        Assert.assertEquals(rtp.getPayloadTypes().get(6).getParameters().get(0).getValue(), "on");
        Assert.assertEquals(rtp.getPayloadTypes().get(6).getMaxPacketTime(), 0);
    }

    @Test
    public void unmarshalRtpWithCrypto() throws XMLStreamException, JAXBException {
        String xml = "<description xmlns='urn:xmpp:jingle:apps:rtp:1' media='audio'>\n" +
                "        <encryption required='1'>\n" +
                "           <crypto \n" +
                "           crypto-suite='AES_CM_128_HMAC_SHA1_80' \n" +
                "           key-params='inline:WVNfX19zZW1jdGwgKCkgewkyMjA7fQp9CnVubGVz|2^20|1:32' \n" +
                "           session-params='KDR=1 UNENCRYPTED_SRTCP'\n" +
                "           tag='1'/>\n" +
                "       </encryption>\n" +
                "      </description>\n";

        Rtp rtp = unmarshal(xml, Rtp.class);
        Assert.assertNotNull(rtp.getEncryption());
        Assert.assertEquals(rtp.getEncryption().getCrypto().size(), 1);
        Assert.assertEquals(rtp.getEncryption().getCrypto().get(0).getCryptoSuite(), "AES_CM_128_HMAC_SHA1_80");
        Assert.assertEquals(rtp.getEncryption().getCrypto().get(0).getKeyParameters(), "inline:WVNfX19zZW1jdGwgKCkgewkyMjA7fQp9CnVubGVz|2^20|1:32");
        Assert.assertEquals(rtp.getEncryption().getCrypto().get(0).getSessionParameters(), "KDR=1 UNENCRYPTED_SRTCP");
        Assert.assertEquals(rtp.getEncryption().getCrypto().get(0).getTag(), "1");
    }
}
