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

package rocks.xmpp.extensions.geoloc;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.geoloc.model.GeoLocation;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.net.URI;
import java.time.ZoneOffset;
import java.util.Locale;

/**
 * @author Christian Schudt
 */
public class GeoLocationTest extends XmlTest {

    protected GeoLocationTest() throws JAXBException {
        super(GeoLocation.class);
    }

    @Test
    public void unmarshalGeoLocation() throws JAXBException, XMLStreamException {
        String xml = "<geoloc xmlns='http://jabber.org/protocol/geoloc' xml:lang='en'>\n" +
                "          <accuracy>20</accuracy>\n" +
                "          <country>Italy</country>\n" +
                "          <lat>45.44</lat>\n" +
                "          <locality>Venice</locality>\n" +
                "          <lon>12.33</lon>\n" +
                "          <tzo>-08:00</tzo>\n" +
                "        </geoloc>\n";
        GeoLocation geoLocation = unmarshal(xml, GeoLocation.class);
        Assert.assertNotNull(geoLocation);
        Assert.assertEquals(geoLocation.getLanguage(), Locale.ENGLISH);
        Assert.assertEquals(geoLocation.getAccuracy(), 20.0);
        Assert.assertEquals(geoLocation.getCountry(), "Italy");
        Assert.assertEquals(geoLocation.getLatitude(), 45.44);
        Assert.assertEquals(geoLocation.getLocality(), "Venice");
        Assert.assertEquals(geoLocation.getLongitude(), 12.33);
        Assert.assertEquals(geoLocation.getTimeZoneOffset(), ZoneOffset.of("-08:00"));
    }

    @Test
    public void marshalGeoLocation() throws JAXBException, XMLStreamException {
        GeoLocation geoLocation = GeoLocation.builder()
                .accuracy(1.0)
                .altitude(2.0)
                .altitudeAccuracy(10.3)
                .area("area")
                .bearing(3.0)
                .building("building")
                .country("country")
                .countryCode("countryCode")
                .datum("datum")
                .description("description")
                .floor("floor")
                .language(Locale.FRENCH)
                .latitude(4.0)
                .locality("locality")
                .longitude(5.0)
                .postalCode("postalCode")
                .region("region")
                .room("room")
                .speed(6.0)
                .street("street")
                .text("text")
                .timeZoneOffset(ZoneOffset.of("+01:00"))
                .uri(URI.create("xmpp.org"))
                .build();

        String xml = marshal(geoLocation);
        Assert.assertEquals(xml, "<geoloc xmlns=\"http://jabber.org/protocol/geoloc\" xml:lang=\"fr\">" +
                "<accuracy>1.0</accuracy>" +
                "<alt>2.0</alt>" +
                "<altaccuracy>10.3</altaccuracy>" +
                "<area>area</area>" +
                "<bearing>3.0</bearing>" +
                "<building>building</building>" +
                "<country>country</country>" +
                "<countrycode>countryCode</countrycode>" +
                "<datum>datum</datum>" +
                "<description>description</description>" +
                "<floor>floor</floor>" +
                "<lat>4.0</lat>" +
                "<locality>locality</locality>" +
                "<lon>5.0</lon>" +
                "<postalcode>postalCode</postalcode>" +
                "<region>region</region>" +
                "<room>room</room>" +
                "<speed>6.0</speed>" +
                "<street>street</street>" +
                "<text>text</text>" +
                "<tzo>+01:00</tzo>" +
                "<uri>xmpp.org</uri>" +
                "</geoloc>");
    }
}
