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

package rocks.xmpp.extensions.vcard.temp;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.vcard.temp.model.VCard;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.time.LocalDate;
import java.time.Month;

/**
 * @author Christian Schudt
 */
public class VCardTest extends XmlTest {
    protected VCardTest() throws JAXBException {
        super(ClientIQ.class, VCard.class);
    }

    @Test
    public void unmarshalVCardResponse() throws XMLStreamException, JAXBException {
        String xml = "<iq id='v1'\n" +
                "    to='stpeter@jabber.org/roundabout'\n" +
                "    type='result'>\n" +
                "  <vCard xmlns='vcard-temp'>\n" +
                "    <FN>Peter Saint-Andre</FN>\n" +
                "    <N>\n" +
                "      <FAMILY>Saint-Andre</FAMILY>\n" +
                "      <GIVEN>Peter</GIVEN>\n" +
                "      <MIDDLE/>\n" +
                "    </N>\n" +
                "    <NICKNAME>stpeter</NICKNAME>\n" +
                "    <URL>http://www.xmpp.org/xsf/people/stpeter.shtml</URL>\n" +
                "    <BDAY>1966-08-06</BDAY>\n" +
                "    <ORG>\n" +
                "      <ORGNAME>XMPP Standards Foundation</ORGNAME>\n" +
                "      <ORGUNIT/>\n" +
                "    </ORG>\n" +
                "    <TITLE>Executive Director</TITLE>\n" +
                "    <ROLE>Patron Saint</ROLE>\n" +
                "    <TEL><WORK/><VOICE/><NUMBER>303-308-3282</NUMBER></TEL>\n" +
                "    <TEL><WORK/><FAX/><NUMBER/></TEL>\n" +
                "    <TEL><WORK/><MSG/><NUMBER/></TEL>\n" +
                "    <ADR>\n" +
                "      <WORK/>\n" +
                "      <EXTADD>Suite 600</EXTADD>\n" +
                "      <STREET>1899 Wynkoop Street</STREET>\n" +
                "      <LOCALITY>Denver</LOCALITY>\n" +
                "      <REGION>CO</REGION>\n" +
                "      <PCODE>80202</PCODE>\n" +
                "      <CTRY>USA</CTRY>\n" +
                "    </ADR>\n" +
                "    <TEL><HOME/><VOICE/><NUMBER>303-555-1212</NUMBER></TEL>\n" +
                "    <TEL><HOME/><FAX/><NUMBER/></TEL>\n" +
                "    <TEL><HOME/><MSG/><NUMBER/></TEL>\n" +
                "    <ADR>\n" +
                "      <HOME/>\n" +
                "      <EXTADD/>\n" +
                "      <STREET/>\n" +
                "      <LOCALITY>Denver</LOCALITY>\n" +
                "      <REGION>CO</REGION>\n" +
                "      <PCODE>80209</PCODE>\n" +
                "      <CTRY>USA</CTRY>\n" +
                "    </ADR>\n" +
                "    <CATEGORIES><KEYWORD>test</KEYWORD></CATEGORIES>\n" +
                "    <EMAIL><INTERNET/><PREF/><USERID>stpeter@jabber.org</USERID></EMAIL>\n" +
                "    <JABBERID>stpeter@jabber.org</JABBERID>\n" +
                "    <DESC>\n" +
                "      More information about me is located on my \n" +
                "      personal website: http://www.saint-andre.com/\n" +
                "    </DESC>\n" +
                "    <CLASS><PUBLIC/></CLASS>\n" +
                "  </vCard>\n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        VCard vCard = iq.getExtension(VCard.class);
        Assert.assertNotNull(vCard);
        Assert.assertEquals(vCard.getFormattedName(), "Peter Saint-Andre");
        Assert.assertNotNull(vCard.getName());
        Assert.assertEquals(vCard.getName().getFamilyName(), "Saint-Andre");
        Assert.assertEquals(vCard.getName().getGivenName(), "Peter");
        Assert.assertEquals(vCard.getName().getMiddleName(), "");
        Assert.assertEquals(vCard.getNickname(), "stpeter");
        Assert.assertEquals(vCard.getUrl().toString(), "http://www.xmpp.org/xsf/people/stpeter.shtml");
        Assert.assertEquals(vCard.getBirthday().getYear(), 1966);
        Assert.assertEquals(vCard.getBirthday().getMonth(), Month.AUGUST);
        Assert.assertEquals(vCard.getBirthday().getDayOfMonth(), 6);
        Assert.assertNotNull(vCard.getOrganization());
        Assert.assertEquals(vCard.getOrganization().getName(), "XMPP Standards Foundation");
        Assert.assertEquals(vCard.getOrganization().getOrgUnits().size(), 1);
        Assert.assertEquals(vCard.getTitle(), "Executive Director");
        Assert.assertEquals(vCard.getRole(), "Patron Saint");

        Assert.assertEquals(vCard.getTelephoneNumbers().size(), 6);
        Assert.assertEquals(vCard.getTelephoneNumbers().get(0).getNumber(), "303-308-3282");
        Assert.assertTrue(vCard.getTelephoneNumbers().get(0).isVoice());
        Assert.assertTrue(vCard.getTelephoneNumbers().get(0).isWork());
        Assert.assertTrue(vCard.getTelephoneNumbers().get(1).isFax());
        Assert.assertTrue(vCard.getTelephoneNumbers().get(1).isWork());
        Assert.assertTrue(vCard.getTelephoneNumbers().get(2).isMsg());
        Assert.assertTrue(vCard.getTelephoneNumbers().get(2).isWork());
        Assert.assertEquals(vCard.getTelephoneNumbers().get(3).getNumber(), "303-555-1212");
        Assert.assertTrue(vCard.getTelephoneNumbers().get(3).isVoice());
        Assert.assertTrue(vCard.getTelephoneNumbers().get(3).isHome());
        Assert.assertTrue(vCard.getTelephoneNumbers().get(4).isFax());
        Assert.assertTrue(vCard.getTelephoneNumbers().get(4).isHome());
        Assert.assertTrue(vCard.getTelephoneNumbers().get(5).isMsg());
        Assert.assertTrue(vCard.getTelephoneNumbers().get(5).isHome());

        Assert.assertTrue(vCard.getEmails().get(0).isPreferred());
        Assert.assertTrue(vCard.getEmails().get(0).isInternet());
        Assert.assertEquals(vCard.getEmails().get(0).getEmail(), "stpeter@jabber.org");
        Assert.assertEquals(vCard.getJid(), Jid.of("stpeter@jabber.org"));
        Assert.assertEquals(vCard.getCategories().size(), 1);
        Assert.assertEquals(vCard.getCategories().get(0), "test");

        Assert.assertEquals(vCard.getDesc(), "\n" +
                "      More information about me is located on my \n" +
                "      personal website: http://www.saint-andre.com/\n" +
                "    ");

    }

    @Test
    public void marshalVCard() throws JAXBException, XMLStreamException {
        VCard vCard = new VCard();
        String xml = marshal(vCard);
        Assert.assertEquals("<vCard xmlns=\"vcard-temp\" version=\"3.0\"></vCard>", xml);
    }

    @Test
    public void marshalBirthDayVCard() throws JAXBException, XMLStreamException {
        VCard vCard = new VCard();
        LocalDate localDate = LocalDate.of(2004, Month.MARCH, 19);
        vCard.setBirthday(localDate);
        String xml = marshal(vCard);
        Assert.assertEquals("<vCard xmlns=\"vcard-temp\" version=\"3.0\"><BDAY>2004-03-19</BDAY></vCard>", xml);
    }
}
