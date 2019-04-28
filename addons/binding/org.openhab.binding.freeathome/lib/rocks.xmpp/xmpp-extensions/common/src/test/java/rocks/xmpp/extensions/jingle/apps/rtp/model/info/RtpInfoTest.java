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

package rocks.xmpp.extensions.jingle.apps.rtp.model.info;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.jingle.apps.rtp.model.Rtp;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class RtpInfoTest extends XmlTest {
    protected RtpInfoTest() throws JAXBException {
        super(Rtp.class);
    }

    @Test
    public void unmarshalActive() throws XMLStreamException, JAXBException {
        String xml = "<active xmlns='urn:xmpp:jingle:apps:rtp:info:1'/>\n";

        Active active = unmarshal(xml, Active.class);
        Assert.assertNotNull(active);
    }

    @Test
    public void unmarshalHold() throws XMLStreamException, JAXBException {
        String xml = "<hold xmlns='urn:xmpp:jingle:apps:rtp:info:1'/>\n";

        Hold hold = unmarshal(xml, Hold.class);
        Assert.assertNotNull(hold);
    }

    @Test
    public void unmarshalUnHold() throws XMLStreamException, JAXBException {
        String xml = "<unhold xmlns='urn:xmpp:jingle:apps:rtp:info:1'/>\n";

        Unhold unhold = unmarshal(xml, Unhold.class);
        Assert.assertNotNull(unhold);
    }

    @Test
    public void unmarshalMute() throws XMLStreamException, JAXBException {
        String xml = "<mute xmlns='urn:xmpp:jingle:apps:rtp:info:1'\n" +
                "          creator='initiator'\n" +
                "          name='voice'/>\n";

        Mute mute = unmarshal(xml, Mute.class);
        Assert.assertNotNull(mute);
        Assert.assertEquals(mute.getCreator(), MutingInfo.Creator.INITIATOR);
        Assert.assertEquals(mute.getName(), "voice");
    }

    @Test
    public void unmarshalUnmute() throws XMLStreamException, JAXBException {
        String xml = "<unmute xmlns='urn:xmpp:jingle:apps:rtp:info:1'\n" +
                "            creator='responder'\n" +
                "            name='voice'/>\n";

        Unmute unmute = unmarshal(xml, Unmute.class);
        Assert.assertNotNull(unmute);
        Assert.assertEquals(unmute.getCreator(), MutingInfo.Creator.RESPONDER);
        Assert.assertEquals(unmute.getName(), "voice");
    }

    @Test
    public void unmarshalRinging() throws XMLStreamException, JAXBException {
        String xml = "<ringing xmlns='urn:xmpp:jingle:apps:rtp:info:1'/>\n";

        Ringing ringing = unmarshal(xml, Ringing.class);
        Assert.assertNotNull(ringing);
    }
}
