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

package rocks.xmpp.extensions.tune;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.tune.model.Tune;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.net.URI;

/**
 * @author Christian Schudt
 */
public class TuneTest extends XmlTest {

    protected TuneTest() throws JAXBException {
        super(Tune.class);
    }

    @Test
    public void unmarshalTune() throws XMLStreamException, JAXBException {
        String xml = "<tune xmlns='http://jabber.org/protocol/tune'>\n" +
                "          <artist>Yes</artist>\n" +
                "          <length>686</length>\n" +
                "          <rating>8</rating>\n" +
                "          <source>Yessongs</source>\n" +
                "          <title>Heart of the Sunrise</title>\n" +
                "          <track>3</track>\n" +
                "          <uri>http://www.yesworld.com/lyrics/Fragile.html#9</uri>\n" +
                "        </tune>\n";
        Tune tune = unmarshal(xml, Tune.class);
        Assert.assertNotNull(tune);
        Assert.assertEquals(tune.getArtist(), "Yes");
        Assert.assertEquals(tune.getLength(), Integer.valueOf(686));
        Assert.assertEquals(tune.getRating(), Integer.valueOf(8));
        Assert.assertEquals(tune.getSource(), "Yessongs");
        Assert.assertEquals(tune.getTitle(), "Heart of the Sunrise");
        Assert.assertEquals(tune.getTrack(), "3");
        Assert.assertEquals(tune.getUri(), URI.create("http://www.yesworld.com/lyrics/Fragile.html#9"));
    }
}
