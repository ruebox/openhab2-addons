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

package rocks.xmpp.extensions.data.mediaelement;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.data.mediaelement.model.Media;
import rocks.xmpp.extensions.data.model.DataForm;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.net.URI;

/**
 * @author Christian Schudt
 */
public class DataFormsMediaTest extends XmlTest {
    protected DataFormsMediaTest() throws JAXBException {
        super(DataForm.class, Media.class);
    }

    @Test
    public void unmarshalDataFormWithMedia1() throws XMLStreamException, JAXBException {
        String xml = "<x xmlns='jabber:x:data' type='form'>\n" +
                "  <field var='ocr'>\n" +
                "    <media xmlns='urn:xmpp:media-element'>\n" +
                "  <uri type='audio/x-wav'>\n" +
                "    http://victim.example.com/challenges/speech.wav?F3A6292C\n" +
                "  </uri>\n" +
                "  <uri type='audio/ogg; codecs=speex'>\n" +
                "    cid:sha1+a15a505e360702b79c75a5f67773072ed392f52a@bob.xmpp.org\n" +
                "  </uri>\n" +
                "  <uri type='audio/mpeg'>\n" +
                "    http://victim.example.com/challenges/speech.mp3?F3A6292C\n" +
                "  </uri>\n" +
                "   </media>" +
                "  </field>\n" +
                "</x>\n";

        DataForm dataForm = unmarshal(xml, DataForm.class);

        Assert.assertNotNull(dataForm);
        Assert.assertEquals(dataForm.getType(), DataForm.Type.FORM);
        Media media = dataForm.getFields().get(0).getMedia();
        Assert.assertNotNull(media);
        Assert.assertEquals(media.getHeight(), 0);
        Assert.assertEquals(media.getWidth(), 0);
    }

    @Test
    public void unmarshalDataFormWithMedia2() throws XMLStreamException, JAXBException {
        String xml = "<x xmlns='jabber:x:data' type='form'>\n" +
                "  <field var='ocr'>\n" +
                "    <media xmlns='urn:xmpp:media-element'\n" +
                "           height='80'\n" +
                "           width='290'>\n" +
                "      <uri type='image/jpeg'>http://www.victim.com/challenges/ocr.jpeg?F3A6292C</uri>\n" +
                "      <uri type='image/jpeg'>cid:sha1+f24030b8d91d233bac14777be5ab531ca3b9f102@bob.xmpp.org</uri>\n" +
                "    </media>\n" +
                "  </field>\n" +
                "</x>\n";

        DataForm dataForm = unmarshal(xml, DataForm.class);

        Assert.assertNotNull(dataForm);
        Assert.assertEquals(dataForm.getType(), DataForm.Type.FORM);
        Media media = dataForm.getFields().get(0).getMedia();
        Assert.assertNotNull(media);
        Assert.assertEquals(media.getHeight(), 80);
        Assert.assertEquals(media.getWidth(), 290);
        Assert.assertEquals(media.getLocations().size(), 2);
        Assert.assertEquals(media.getLocations().get(0).getType(), "image/jpeg");
        Assert.assertEquals(media.getLocations().get(0).getUri(), URI.create("http://www.victim.com/challenges/ocr.jpeg?F3A6292C"));
        Assert.assertEquals(media.getLocations().get(1).getType(), "image/jpeg");
        Assert.assertEquals(media.getLocations().get(1).getUri(), URI.create("cid:sha1+f24030b8d91d233bac14777be5ab531ca3b9f102@bob.xmpp.org"));
    }
}
