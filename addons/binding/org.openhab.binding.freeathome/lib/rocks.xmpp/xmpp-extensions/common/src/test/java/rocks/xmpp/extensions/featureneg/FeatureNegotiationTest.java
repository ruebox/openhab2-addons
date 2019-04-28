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

package rocks.xmpp.extensions.featureneg;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.featureneg.model.FeatureNegotiation;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class FeatureNegotiationTest extends XmlTest {

    protected FeatureNegotiationTest() throws JAXBException {
        super(ClientIQ.class, FeatureNegotiation.class);
    }

    @Test
    public void unmarshalFeatureNegotiation() throws JAXBException, XMLStreamException {
        String xml = "<iq type='set'\n" +
                "    from='romeo@montague.net/orchard'\n" +
                "    to='juliet@capulet.com/balcony'\n" +
                "    id='neg1'>\n" +
                "  <feature xmlns='http://jabber.org/protocol/feature-neg'>\n" +
                "    <x xmlns='jabber:x:data' type='form'>\n" +
                "      <field var='FORM_TYPE' type='hidden'>\n" +
                "        <value>romantic_meetings</value>\n" +
                "      </field>\n" +
                "      <field type='list-single' var='places-to-meet'>\n" +
                "         <option><value>Secret Grotto</value></option>\n" +
                "         <option><value>Verona Park</value></option>\n" +
                "      </field>\n" +
                "      <field type='list-single' var='times-to-meet'>\n" +
                "         <option><value>22:00</value></option>\n" +
                "         <option><value>22:30</value></option>\n" +
                "         <option><value>23:00</value></option>\n" +
                "      </field>\n" +
                "    </x>\n" +
                "  </feature>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        FeatureNegotiation featureNegotiation = iq.getExtension(FeatureNegotiation.class);
        Assert.assertNotNull(featureNegotiation);
        Assert.assertNotNull(featureNegotiation.getDataForm());
    }
}
