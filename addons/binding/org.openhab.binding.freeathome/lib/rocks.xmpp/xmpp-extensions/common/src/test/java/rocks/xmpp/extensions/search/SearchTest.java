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

package rocks.xmpp.extensions.search;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.search.model.Search;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class SearchTest extends XmlTest {

    protected SearchTest() throws JAXBException {
        super(ClientIQ.class, Search.class);
    }

    @Test
    public void unmarshalSearchRequest() throws XMLStreamException, JAXBException {
        String xml = "<iq type='get'\n" +
                "    from='romeo@montague.net/home'\n" +
                "    to='characters.shakespeare.lit'\n" +
                "    id='search1'\n" +
                "    xml:lang='en'>\n" +
                "  <query xmlns='jabber:iq:search'/>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Search search = iq.getExtension(Search.class);
        Assert.assertNotNull(search);
    }

    @Test
    public void unmarshalSearchResponse() throws XMLStreamException, JAXBException {
        String xml = "<iq type='result'\n" +
                "    from='characters.shakespeare.lit'\n" +
                "    to='romeo@montague.net/home'\n" +
                "    id='search1'\n" +
                "    xml:lang='en'>\n" +
                "  <query xmlns='jabber:iq:search'>\n" +
                "    <instructions>\n" +
                "      Fill in one or more fields to search\n" +
                "      for any matching Jabber users.\n" +
                "    </instructions>\n" +
                "    <first/>\n" +
                "    <last/>\n" +
                "    <nick/>\n" +
                "    <email/>\n" +
                "  </query>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Search search = iq.getExtension(Search.class);
        Assert.assertEquals("\n" +
                "      Fill in one or more fields to search\n" +
                "      for any matching Jabber users.\n" +
                "    ", search.getInstructions());

    }

    @Test
    public void marshalSearchRequest() throws JAXBException, XMLStreamException {
        Search search = new Search(null, "Capulet", null, null);
        String xml = marshal(search);
        Assert.assertEquals("<query xmlns=\"jabber:iq:search\"><last>Capulet</last></query>", xml);
    }

    @Test
    public void unmarshalSearchResult() throws JAXBException, XMLStreamException {
        String xml = "<iq type='result'\n" +
                "    from='characters.shakespeare.lit'\n" +
                "    to='romeo@montague.net/home'\n" +
                "    id='search2'\n" +
                "    xml:lang='en'>\n" +
                "  <query xmlns='jabber:iq:search'>\n" +
                "    <item jid='juliet@capulet.com'>\n" +
                "      <first>Juliet</first>\n" +
                "      <last>Capulet</last>\n" +
                "      <nick>JuliC</nick>\n" +
                "      <email>juliet@shakespeare.lit</email>\n" +
                "    </item>\n" +
                "    <item jid='tybalt@shakespeare.lit'>\n" +
                "      <first>Tybalt</first>\n" +
                "      <last>Capulet</last>\n" +
                "      <nick>ty</nick>\n" +
                "      <email>tybalt@shakespeare.lit</email>\n" +
                "    </item>\n" +
                "  </query>\n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        Search search = iq.getExtension(Search.class);

        Assert.assertNotNull(search);
        Assert.assertEquals(search.getItems().size(), 2);
        Assert.assertEquals(search.getItems().get(0).getJid(), Jid.of("juliet@capulet.com"));
        Assert.assertEquals(search.getItems().get(0).getFirst(), "Juliet");
        Assert.assertEquals(search.getItems().get(0).getLast(), "Capulet");
        Assert.assertEquals(search.getItems().get(0).getNick(), "JuliC");
        Assert.assertEquals(search.getItems().get(0).getEmail(), "juliet@shakespeare.lit");

        Assert.assertEquals(search.getItems().get(1).getJid(), Jid.of("tybalt@shakespeare.lit"));
        Assert.assertEquals(search.getItems().get(1).getFirst(), "Tybalt");
        Assert.assertEquals(search.getItems().get(1).getLast(), "Capulet");
        Assert.assertEquals(search.getItems().get(1).getNick(), "ty");
        Assert.assertEquals(search.getItems().get(1).getEmail(), "tybalt@shakespeare.lit");
    }

    @Test
    public void unmarshalDataForm() throws JAXBException, XMLStreamException {
        String xml = "<iq type='result'\n" +
                "    from='characters.shakespeare.lit'\n" +
                "    to='juliet@capulet.com/balcony'\n" +
                "    id='search3'\n" +
                "    xml:lang='en'>\n" +
                "  <query xmlns='jabber:iq:search'>\n" +
                "    <instructions>\n" +
                "      Use the enclosed form to search. If your Jabber client does not\n" +
                "      support Data Forms, visit http://shakespeare.lit/\n" +
                "    </instructions>\n" +
                "    <x xmlns='jabber:x:data' type='form'>\n" +
                "      <title>User Directory Search</title>\n" +
                "      <instructions>\n" +
                "        Please provide the following information\n" +
                "        to search for Shakespearean characters.\n" +
                "      </instructions>\n" +
                "      <field type='hidden'\n" +
                "             var='FORM_TYPE'>\n" +
                "        <value>jabber:iq:search</value>\n" +
                "      </field>\n" +
                "      <field type='text-single'\n" +
                "             label='Given Name'\n" +
                "             var='first'/>\n" +
                "      <field type='text-single'\n" +
                "             label='Family Name'\n" +
                "             var='last'/>\n" +
                "      <field type='list-single'\n" +
                "             label='Gender'\n" +
                "             var='x-gender'>\n" +
                "        <option label='Male'><value>male</value></option>\n" +
                "        <option label='Female'><value>female</value></option>\n" +
                "      </field>\n" +
                "    </x>\n" +
                "  </query>\n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        Search search = iq.getExtension(Search.class);

        Assert.assertNotNull(search);
        Assert.assertNotNull(search.getAdditionalInformation());
        Assert.assertEquals("User Directory Search", search.getAdditionalInformation().getTitle());
        // The rest should be covered by data form test.
    }

    @Test
    public void unmarshalExtendedSearchResult() throws JAXBException, XMLStreamException {
        String xml = "<iq type='result'\n" +
                "    from='characters.shakespeare.lit'\n" +
                "    to='juliet@capulet.com/balcony'\n" +
                "    id='search4'\n" +
                "    xml:lang='en'>\n" +
                "  <query xmlns='jabber:iq:search'>\n" +
                "    <x xmlns='jabber:x:data' type='result'>\n" +
                "      <field type='hidden' var='FORM_TYPE'>\n" +
                "        <value>jabber:iq:search</value>\n" +
                "      </field>\n" +
                "      <reported>\n" +
                "        <field var='first' label='Given Name' type='text-single'/>\n" +
                "        <field var='last' label='Family Name' type='text-single'/>\n" +
                "        <field var='jid' label='Jabber ID' type='jid-single'/>\n" +
                "        <field var='x-gender' label='Gender' type='list-single'/>\n" +
                "      </reported>\n" +
                "      <item>\n" +
                "        <field var='first'><value>Benvolio</value></field>\n" +
                "        <field var='last'><value>Montague</value></field>\n" +
                "        <field var='jid'><value>benvolio@montague.net</value></field>\n" +
                "        <field var='x-gender'><value>male</value></field>\n" +
                "      </item>\n" +
                "      <item>\n" +
                "        <field var='first'><value>Romeo</value></field>\n" +
                "        <field var='last'><value>Montague</value></field>\n" +
                "        <field var='jid'><value>romeo@montague.net</value></field>\n" +
                "        <field var='x-gender'><value>male</value></field>\n" +
                "      </item>\n" +
                "    </x>\n" +
                "  </query>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        Search search = iq.getExtension(Search.class);

        Assert.assertNotNull(search);

        DataForm dataForm = search.getAdditionalInformation();
        Assert.assertNotNull(dataForm);
        Assert.assertNotNull(dataForm.getReportedFields());
        Assert.assertEquals(dataForm.getReportedFields().size(), 4);
        Assert.assertEquals(dataForm.getReportedFields().get(0).getVar(), "first");
        Assert.assertEquals(dataForm.getReportedFields().get(0).getLabel(), "Given Name");
        Assert.assertEquals(dataForm.getReportedFields().get(0).getType(), DataForm.Field.Type.TEXT_SINGLE);

        Assert.assertEquals(dataForm.getItems().size(), 2);
        Assert.assertEquals(dataForm.getItems().get(0).getFields().size(), 4);
        Assert.assertEquals(dataForm.getItems().get(0).getFields().get(0).getValues().get(0), "Benvolio");
        Assert.assertEquals(dataForm.getItems().get(0).getFields().get(0).getVar(), "first");
        Assert.assertEquals(dataForm.getItems().get(0).getFields().get(1).getValues().get(0), "Montague");
        Assert.assertEquals(dataForm.getItems().get(0).getFields().get(1).getVar(), "last");
        Assert.assertEquals(dataForm.getItems().get(0).getFields().get(2).getValues().get(0), "benvolio@montague.net");
        Assert.assertEquals(dataForm.getItems().get(0).getFields().get(2).getVar(), "jid");
        Assert.assertEquals(dataForm.getItems().get(0).getFields().get(3).getValues().get(0), "male");
        Assert.assertEquals(dataForm.getItems().get(0).getFields().get(3).getVar(), "x-gender");

        Assert.assertEquals(dataForm.getItems().get(1).getFields().size(), 4);
        Assert.assertEquals(dataForm.getItems().get(1).getFields().get(0).getValues().get(0), "Romeo");
        Assert.assertEquals(dataForm.getItems().get(1).getFields().get(0).getVar(), "first");
        Assert.assertEquals(dataForm.getItems().get(1).getFields().get(1).getValues().get(0), "Montague");
        Assert.assertEquals(dataForm.getItems().get(1).getFields().get(1).getVar(), "last");
        Assert.assertEquals(dataForm.getItems().get(1).getFields().get(2).getValues().get(0), "romeo@montague.net");
        Assert.assertEquals(dataForm.getItems().get(1).getFields().get(2).getVar(), "jid");
        Assert.assertEquals(dataForm.getItems().get(1).getFields().get(3).getValues().get(0), "male");
        Assert.assertEquals(dataForm.getItems().get(1).getFields().get(3).getVar(), "x-gender");
    }
}
