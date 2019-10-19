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

package rocks.xmpp.extensions.csi;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stream.model.StreamFeature;
import rocks.xmpp.extensions.csi.model.ClientState;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class ClientStateTest extends XmlTest {

    protected ClientStateTest() throws JAXBException {
        super(ClientState.class);
    }

    @Test
    public void unmarshalActiveState() throws XMLStreamException, JAXBException {
        String xml = "<active xmlns='urn:xmpp:csi:0'/>\n";
        ClientState clientState = (ClientState) unmarshal(xml);
        Assert.assertEquals(clientState, ClientState.ACTIVE);
    }

    @Test
    public void unmarshalInactiveState() throws XMLStreamException, JAXBException {
        String xml = "<inactive xmlns='urn:xmpp:csi:0'/>\n";
        ClientState clientState = (ClientState) unmarshal(xml);
        Assert.assertEquals(clientState, ClientState.INACTIVE);
    }

    @Test
    public void unmarshalStreamFeature() throws XMLStreamException, JAXBException {
        String xml = "<csi xmlns='urn:xmpp:csi:0'/>\n";
        StreamFeature feature = (StreamFeature) unmarshal(xml);
        Assert.assertEquals(feature, ClientState.FEATURE);
    }

    @Test
    public void marshalActiveState() throws XMLStreamException, JAXBException {
        String xml = "<active xmlns=\"urn:xmpp:csi:0\"></active>";
        Assert.assertEquals(marshal(ClientState.ACTIVE), xml);
    }

    @Test
    public void marshalInactiveState() throws XMLStreamException, JAXBException {
        String xml = "<inactive xmlns=\"urn:xmpp:csi:0\"></inactive>";
        Assert.assertEquals(marshal(ClientState.INACTIVE), xml);
    }

    @Test
    public void marshalStreamFeature() throws XMLStreamException, JAXBException {
        String xml = "<csi xmlns=\"urn:xmpp:csi:0\"></csi>";
        Assert.assertEquals(marshal(ClientState.FEATURE), xml);
    }
}
