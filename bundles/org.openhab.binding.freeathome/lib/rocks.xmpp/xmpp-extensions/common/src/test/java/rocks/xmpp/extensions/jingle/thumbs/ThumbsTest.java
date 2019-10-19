/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 Christian Schudt
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

package rocks.xmpp.extensions.jingle.thumbs;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.jingle.thumbs.model.Thumbnail;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.net.URI;

/**
 * @author Christian Schudt
 */
public class ThumbsTest extends XmlTest {
    protected ThumbsTest() throws JAXBException {
        super(ClientIQ.class, Thumbnail.class);
    }

    @Test
    public void unmarshalThumbs() throws XMLStreamException, JAXBException {
        String xml = "<thumbnail xmlns='urn:xmpp:thumbs:1'\n" +
                "                     uri='cid:sha1+ffd7c8d28e9c5e82afea41f97108c6b4@bob.xmpp.org'\n" +
                "                     media-type='image/png'\n" +
                "                     width='128'\n" +
                "                     height='96'/>";

        Thumbnail thumbnail = unmarshal(xml, Thumbnail.class);
        Assert.assertNotNull(thumbnail);
        Assert.assertEquals(thumbnail.getUri(), URI.create("cid:sha1+ffd7c8d28e9c5e82afea41f97108c6b4@bob.xmpp.org"));
        Assert.assertEquals(thumbnail.getWidth(), Integer.valueOf(128));
        Assert.assertEquals(thumbnail.getHeight(), Integer.valueOf(96));
    }

    @Test
    public void marshalThumbs() throws XMLStreamException, JAXBException {
        Thumbnail thumbnail = new Thumbnail(URI.create("cid:sha1+test"), "image/png", 12, 23);
        String xml = marshal(thumbnail);
        Assert.assertEquals(xml, "<thumbnail xmlns=\"urn:xmpp:thumbs:1\" uri=\"cid:sha1+test\" media-type=\"image/png\" width=\"12\" height=\"23\"></thumbnail>");
    }
}
