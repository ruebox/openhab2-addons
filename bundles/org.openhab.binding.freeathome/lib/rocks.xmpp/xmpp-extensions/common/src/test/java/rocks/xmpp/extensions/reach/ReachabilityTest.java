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

package rocks.xmpp.extensions.reach;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.reach.model.Address;
import rocks.xmpp.extensions.reach.model.Reachability;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.net.URI;
import java.util.Collections;
import java.util.Locale;

/**
 * @author Christian Schudt
 */
public class ReachabilityTest extends XmlTest {

    protected ReachabilityTest() throws JAXBException {
        super(Reachability.class);
    }

    @Test
    public void unmarshalReachability() throws XMLStreamException, JAXBException {
        String xml = "<reach xmlns='urn:xmpp:reach:0'>\n" +
                "          <addr uri='tel:+1-303-555-1212'>\n" +
                "            <desc xml:lang='en'>Conference room phone</desc>\n" +
                "          </addr>\n" +
                "          <addr uri='sip:room123@example.com'>\n" +
                "            <desc xml:lang='en'>In-room video system</desc>\n" +
                "          </addr>\n" +
                "        </reach>\n";
        Reachability reachability = unmarshal(xml, Reachability.class);
        Assert.assertNotNull(reachability);
        Assert.assertEquals(reachability.getAddresses().size(), 2);
        Assert.assertEquals(reachability.getAddresses().get(0).getUri(), URI.create("tel:+1-303-555-1212"));
        Assert.assertEquals(reachability.getAddresses().get(0).getDescriptions().size(), 1);
        Assert.assertEquals(reachability.getAddresses().get(0).getDescriptions().get(0).getValue(), "Conference room phone");
        Assert.assertEquals(reachability.getAddresses().get(0).getDescriptions().get(0).getLanguage(), Locale.ENGLISH);
        Assert.assertEquals(reachability.getAddresses().get(1).getUri(), URI.create("sip:room123@example.com"));
        Assert.assertEquals(reachability.getAddresses().get(1).getDescriptions().get(0).getValue(), "In-room video system");
        Assert.assertEquals(reachability.getAddresses().get(1).getDescriptions().get(0).getLanguage(), Locale.ENGLISH);
    }

    @Test
    public void testReachabilityEquality() {

        Reachability reachability1 = new Reachability(Collections.singleton(new Address(URI.create("sip:room123@example.com"), new Address.Description("In-room video system", Locale.ENGLISH))));
        Reachability reachability2 = new Reachability(Collections.singleton(new Address(URI.create("sip:room123@example.com"), new Address.Description("In-room video system", Locale.ENGLISH))));

        Assert.assertEquals(reachability1, reachability2);
    }
}
