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

package rocks.xmpp.extensions.rsm;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.rsm.model.ResultSetManagement;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class ResultSetTest extends XmlTest {
    protected ResultSetTest() throws JAXBException {
        super(ResultSetManagement.class);
    }

    @Test
    public void marshalItemCount() throws JAXBException, XMLStreamException {
        ResultSetManagement resultSetManagement = ResultSetManagement.forItemCount();
        Assert.assertTrue(resultSetManagement.isRequestingCount());
        String xml = marshal(ResultSetManagement.forItemCount());
        Assert.assertEquals(xml, "<set xmlns=\"http://jabber.org/protocol/rsm\"><max>0</max></set>");
    }

    @Test
    public void marshalLimit() throws JAXBException, XMLStreamException {
        ResultSetManagement resultSetManagement = ResultSetManagement.forFirstPage(10);
        Assert.assertFalse(resultSetManagement.isRequestingCount());
        String xml = marshal(resultSetManagement);
        Assert.assertEquals(xml, "<set xmlns=\"http://jabber.org/protocol/rsm\"><max>10</max></set>");
    }

    @Test
    public void marshalForNextPage() throws JAXBException, XMLStreamException {
        String xml = marshal(ResultSetManagement.forNextPage(10, "next"));
        Assert.assertEquals(xml, "<set xmlns=\"http://jabber.org/protocol/rsm\"><max>10</max><after>next</after></set>");
    }

    @Test
    public void marshalForLastPage() throws JAXBException, XMLStreamException {
        String xml = marshal(ResultSetManagement.forLastPage(10));
        Assert.assertEquals(xml, "<set xmlns=\"http://jabber.org/protocol/rsm\"><max>10</max><before></before></set>");
    }

    @Test
    public void marshalForPreviousPage() throws JAXBException, XMLStreamException {
        String xml = marshal(ResultSetManagement.forPreviousPage(10, "previous"));
        Assert.assertEquals(xml, "<set xmlns=\"http://jabber.org/protocol/rsm\"><max>10</max><before>previous</before></set>");
    }

    @Test
    public void marshalForIndex() throws JAXBException, XMLStreamException {
        String xml = marshal(ResultSetManagement.forLimit(10, 371));
        Assert.assertEquals(xml, "<set xmlns=\"http://jabber.org/protocol/rsm\"><max>10</max><index>371</index></set>");
    }

    @Test
    public void marshalForResponse() throws JAXBException, XMLStreamException {
        String xml = marshal(ResultSetManagement.forCountResponse(10));
        Assert.assertEquals(xml, "<set xmlns=\"http://jabber.org/protocol/rsm\"><count>10</count></set>");

        String xml2 = marshal(ResultSetManagement.forResponse(10, 1, "first", "last"));
        Assert.assertEquals(xml2, "<set xmlns=\"http://jabber.org/protocol/rsm\"><count>10</count><first index=\"1\">first</first><last>last</last></set>");
    }

    @Test
    public void testPaging() throws JAXBException, XMLStreamException {
        ResultSetManagement rsm = ResultSetManagement.forResponse(10, 1, "first", "last");
        ResultSetManagement next = rsm.nextPage(5);
        Assert.assertEquals(marshal(next), "<set xmlns=\"http://jabber.org/protocol/rsm\"><max>5</max><after>last</after></set>");
        ResultSetManagement prev = rsm.previousPage(5);
        Assert.assertEquals(marshal(prev), "<set xmlns=\"http://jabber.org/protocol/rsm\"><max>5</max><before>first</before></set>");
    }

    @Test
    public void unmarshalResultSet() throws XMLStreamException, JAXBException {
        String xml = "<set xmlns='http://jabber.org/protocol/rsm'>\n" +
                "      <first index='0'>stpeter@jabber.org</first>\n" +
                "      <last>peterpan@neverland.lit</last>\n" +
                "      <count>800</count>\n" +
                "    </set>";
        ResultSetManagement set = unmarshal(xml, ResultSetManagement.class);
        Assert.assertNotNull(set);
        Assert.assertEquals(set.getFirstItem(), "stpeter@jabber.org");
        Assert.assertEquals(set.getFirstItemIndex(), Integer.valueOf(0));
        Assert.assertEquals(set.getLastItem(), "peterpan@neverland.lit");
        Assert.assertEquals(set.getItemCount(), Integer.valueOf(800));
    }
}
