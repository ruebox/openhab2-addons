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

package rocks.xmpp.extensions.last;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.last.model.LastActivity;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class LastActivityTest extends XmlTest {

    protected LastActivityTest() throws JAXBException {
        super(ClientIQ.class, LastActivity.class);
    }

    @Test
    public void unmarshalLastActivityResponse() throws XMLStreamException, JAXBException {
        String xml = "<iq from='juliet@capulet.com' \n" +
                "    id='last1'\n" +
                "    to='romeo@montague.net/orchard'\n" +
                "    type='result'>\n" +
                "  <query xmlns='jabber:iq:last' seconds='903'/>\n" +
                "</iq>";
        IQ iq = unmarshal(xml, IQ.class);
        LastActivity lastActivity = iq.getExtension(LastActivity.class);
        Assert.assertNotNull(lastActivity);
        Assert.assertEquals(lastActivity.getSeconds(), Long.valueOf(903));
    }
}
