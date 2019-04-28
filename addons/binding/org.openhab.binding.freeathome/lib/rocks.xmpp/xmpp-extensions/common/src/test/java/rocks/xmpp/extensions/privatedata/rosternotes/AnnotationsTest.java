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

package rocks.xmpp.extensions.privatedata.rosternotes;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.privatedata.model.PrivateData;
import rocks.xmpp.extensions.privatedata.rosternotes.model.Annotation;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class AnnotationsTest extends XmlTest {
    protected AnnotationsTest() throws JAXBException {
        super(ClientIQ.class, PrivateData.class, Annotation.class);
    }

    @Test
    public void unmarshalAnnotations() throws XMLStreamException, JAXBException {
        String xml = "<iq type='set' id='a1'>\n" +
                "  <query xmlns='jabber:iq:private'>\n" +
                "    <storage xmlns='storage:rosternotes'>\n" +
                "      <note jid='hamlet@shakespeare.lit'\n" +
                "            cdate='2004-09-24T15:23:21Z'\n" +
                "            mdate='2004-09-24T15:23:21Z'>Seems to be a good writer</note>\n" +
                "      <note jid='juliet@capulet.com'\n" +
                "            cdate='2004-09-27T17:23:14Z'\n" +
                "            mdate='2004-09-28T12:43:12Z'>Oh my sweetest love ...</note>\n" +
                "    </storage>\n" +
                "  </query>\n" +
                "</iq>";
        IQ iq = unmarshal(xml, IQ.class);
        PrivateData privateData = iq.getExtension(PrivateData.class);
        Assert.assertNotNull(privateData);
        Assert.assertTrue(privateData.getData() instanceof Annotation);
        Annotation annotations = (Annotation) privateData.getData();
        Assert.assertEquals(annotations.getNotes().size(), 2);
        Assert.assertEquals(annotations.getNotes().get(0).getJid(), Jid.of("hamlet@shakespeare.lit"));
        Assert.assertEquals(annotations.getNotes().get(0).getValue(), "Seems to be a good writer");
        Assert.assertNotNull(annotations.getNotes().get(0).getCreationDate());
        Assert.assertNotNull(annotations.getNotes().get(0).getModificationDate());
        Assert.assertEquals(annotations.getNotes().get(1).getJid(), Jid.of("juliet@capulet.com"));
        Assert.assertEquals(annotations.getNotes().get(1).getValue(), "Oh my sweetest love ...");
        Assert.assertNotNull(annotations.getNotes().get(1).getCreationDate());
        Assert.assertNotNull(annotations.getNotes().get(1).getModificationDate());
    }
}
