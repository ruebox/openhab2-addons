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

package rocks.xmpp.extensions.data.layout;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.data.layout.model.Page;
import rocks.xmpp.extensions.data.model.DataForm;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class DataFormsLayoutTest extends XmlTest {
    protected DataFormsLayoutTest() throws JAXBException {
        super(DataForm.class, Page.class);
    }

    @Test
    public void unmarshalDataFormWithPages() throws XMLStreamException, JAXBException {
        String xml = "<x xmlns='jabber:x:data' type='form'>\n" +
                "  <title>XSF Application</title>\n" +
                "  <instructions>Please fill out this form</instructions>\n" +
                "  <page xmlns='http://jabber.org/protocol/xdata-layout' label='Personal Information'>\n" +
                "    <text>This is page one of three.</text>\n" +
                "    <text>\n" +
                "      Note: In accordance with the XSF privacy policy, your personal information will \n" +
                "      never be shared outside the organization in any way for any purpose; however, \n" +
                "      your name and JID may be published in the XSF membership directory.\n" +
                "    </text>\n" +
                "    <fieldref var='name.first'/>\n" +
                "    <fieldref var='name.last'/>\n" +
                "    <fieldref var='email'/>\n" +
                "    <fieldref var='jid'/>\n" +
                "    <fieldref var='background'/>\n" +
                "  </page>\n" +
                "  <page xmlns='http://jabber.org/protocol/xdata-layout' label='Community Activity'>\n" +
                "    <text>This is page two of three.</text>\n" +
                "    <text>\n" +
                "      We use this page to gather information about any XEPs you&apos;ve worked on, \n" +
                "      as well as your mailing list activity.\n" +
                "    </text>\n" +
                "    <text>You do post to the mailing lists, don't you?</text>\n" +
                "    <fieldref var='activity.mailing-lists'/>\n" +
                "    <fieldref var='activity.xeps'/>\n" +
                "  </page>\n" +
                "  <page xmlns='http://jabber.org/protocol/xdata-layout' label='Plans and Reasonings'>\n" +
                "    <text>This is page three of three.</text>\n" +
                "    <text>You're almost done!</text>\n" +
                "    <text>\n" +
                "      This is where you describe your future plans and why you think you \n" +
                "      deserve to be a member of the XMPP Standards Foundation.\n" +
                "    </text>\n" +
                "    <fieldref var='future'/>\n" +
                "    <fieldref var='reasoning'/>\n" +
                "  </page>\n" +
                "  <field var='name.first' type='text-single' label='First Name'>\n" +
                "    <required/>\n" +
                "  </field>\n" +
                "  <field var='name.last' type='text-single' label='Last Name'>\n" +
                "    <required/>\n" +
                "  </field>\n" +
                "  <field var='email' type='text-single' label='E-mail Address'>\n" +
                "    <required/>\n" +
                "  </field>\n" +
                "  <field var='jid' type='jid-single' label='Jabber JID'>\n" +
                "    <required/>\n" +
                "  </field>\n" +
                "  <field var='background' type='text-multi' label='Background Information'>\n" +
                "  </field>\n" +
                "  <field var='future' type='text-multi' label='Jabber Plans for the Next Six Months'>\n" +
                "  </field>\n" +
                "  <field var='reasoning' type='text-multi' label='Reasons for Joining'>\n" +
                "  </field>\n" +
                "  <field var='activity.mailing-lists' type='text-multi' label='Recent Mailing List Activity'>\n" +
                "  </field>\n" +
                "  <field var='activity.xeps' type='text-multi' label='XEPs Authored or Co-Authored'>\n" +
                "  </field>\n" +
                "</x>\n";

        DataForm dataForm = unmarshal(xml, DataForm.class);

        Assert.assertNotNull(dataForm);
        Assert.assertEquals(dataForm.getPages().size(), 3);
        Assert.assertEquals(dataForm.getPages().get(0).getText().size(), 2);
        Assert.assertEquals(dataForm.getPages().get(0).getText().get(0), "This is page one of three.");
        Assert.assertEquals(dataForm.getPages().get(0).getFieldReferences().size(), 5);
        Assert.assertEquals(dataForm.getPages().get(0).getFieldReferences().get(0).getVar(), "name.first");
        Assert.assertEquals(dataForm.getPages().get(0).getFieldReferences().get(1).getVar(), "name.last");
        Assert.assertEquals(dataForm.getPages().get(0).getFieldReferences().get(2).getVar(), "email");
        Assert.assertEquals(dataForm.getPages().get(0).getFieldReferences().get(3).getVar(), "jid");
        Assert.assertEquals(dataForm.getPages().get(0).getFieldReferences().get(4).getVar(), "background");
    }

    @Test
    public void unmarshalDataFormWithSections() throws XMLStreamException, JAXBException {
        String xml = "<x xmlns='jabber:x:data' type='form'>\n" +
                "  <title>XSF Application</title>\n" +
                "  <instructions>Please fill out this form</instructions>\n" +
                "  <page xmlns='http://jabber.org/protocol/xdata-layout'>\n" +
                "    <section label='Personal Information'>\n" +
                "      <text>\n" +
                "        Note: In accordance with the XSF privacy policy, your personal information will \n" +
                "        never be shared outside the organization in any way for any purpose; however, \n" +
                "        your name and JID may be published in the XSF membership directory.\n" +
                "      </text>\n" +
                "      <fieldref var='name.first'/>\n" +
                "      <fieldref var='name.last'/>\n" +
                "      <fieldref var='email'/>\n" +
                "      <fieldref var='jid'/>\n" +
                "      <fieldref var='background'/>\n" +
                "    </section>\n" +
                "    <section label='Community Activity'>\n" +
                "      <text>\n" +
                "        We use this page to gather information about any XEPs you&apos;ve worked on, \n" +
                "        as well as your mailing list activity.\n" +
                "      </text>\n" +
                "      <text>You do post to the mailing lists, don't you?</text>\n" +
                "      <fieldref var='activity.mailing-lists'/>\n" +
                "      <fieldref var='activity.xeps'/>\n" +
                "    </section>\n" +
                "    <section label='Plans and Reasoning'>\n" +
                "      <text>You're almost done!</text>\n" +
                "      <text>\n" +
                "        This is where you describe your future plans and why you think you \n" +
                "        deserve to be a member of the XMPP Standards Foundation.\n" +
                "      </text>\n" +
                "      <fieldref var='future'/>\n" +
                "      <fieldref var='reasoning'/>\n" +
                "    </section>\n" +
                "  </page>\n" +
                "  <field var='name.first' type='text-single' label='First Name'>\n" +
                "    <required/>\n" +
                "  </field>\n" +
                "  <field var='name.last' type='text-single' label='Last Name'>\n" +
                "    <required/>\n" +
                "  </field>\n" +
                "  <field var='email' type='text-single' label='E-mail Address'>\n" +
                "    <required/>\n" +
                "  </field>\n" +
                "  <field var='jid' type='jid-single' label='Jabber JID'>\n" +
                "    <required/>\n" +
                "  </field>\n" +
                "  <field var='background' type='text-multi' label='Background Information'>\n" +
                "  </field>\n" +
                "  <field var='future' type='text-multi' label='Jabber Plans for the Next Six Months'>\n" +
                "  </field>\n" +
                "  <field var='reasoning' type='text-multi' label='Reasons for Joining'>\n" +
                "  </field>\n" +
                "  <field var='activity.mailing-lists' type='text-multi' label='Recent Mailing List Activity'>\n" +
                "  </field>\n" +
                "  <field var='activity.xeps' type='text-multi' label='XEPs Authored or Co-Authored'>\n" +
                "  </field>\n" +
                "</x>\n";

        DataForm dataForm = unmarshal(xml, DataForm.class);

        Assert.assertNotNull(dataForm);
        Assert.assertEquals(dataForm.getPages().size(), 1);
        Assert.assertEquals(dataForm.getPages().get(0).getSections().size(), 3);
        Assert.assertEquals(dataForm.getPages().get(0).getSections().get(0).getLabel(), "Personal Information");
        Assert.assertEquals(dataForm.getPages().get(0).getSections().get(0).getText().size(), 1);
        Assert.assertEquals(dataForm.getPages().get(0).getSections().get(0).getFieldReferences().get(0).getVar(), "name.first");
        Assert.assertEquals(dataForm.getPages().get(0).getSections().get(0).getFieldReferences().get(1).getVar(), "name.last");
        Assert.assertEquals(dataForm.getPages().get(0).getSections().get(0).getFieldReferences().get(2).getVar(), "email");
        Assert.assertEquals(dataForm.getPages().get(0).getSections().get(0).getFieldReferences().get(3).getVar(), "jid");
        Assert.assertEquals(dataForm.getPages().get(0).getSections().get(0).getFieldReferences().get(4).getVar(), "background");
    }

    @Test
    public void unmarshalDataFormWithNestedSections() throws XMLStreamException, JAXBException {
        String xml = "<x xmlns='jabber:x:data' type='form'>\n" +
                "  <page xmlns='http://jabber.org/protocol/xdata-layout'>\n" +
                "    <section label='Personal Information'>\n" +
                "      <text>\n" +
                "        Note: In accordance with the XSF privacy policy, your personal information will \n" +
                "        never be shared outside the organization in any way for any purpose; however, \n" +
                "        your name and JID may be published in the XSF membership directory.\n" +
                "      </text>\n" +
                "      <section label='Name'>\n" +
                "        <text>Who are you?</text>\n" +
                "        <fieldref var='name.first'/>\n" +
                "        <fieldref var='name.last'/>\n" +
                "      </section>\n" +
                "      <section label='Contact Information'>\n" +
                "        <text>How can we contact you?</text>\n" +
                "        <fieldref var='email'/>\n" +
                "        <fieldref var='jid'/>\n" +
                "      </section>\n" +
                "      <fieldref var='background'/>\n" +
                "    </section>\n" +
                "    <section label='Community Activity'>\n" +
                "      <text>\n" +
                "        We use this page to gather information about any XEPs you&apos;ve worked on, \n" +
                "        as well as your mailing list activity.\n" +
                "      </text>\n" +
                "      <text>You do post to the mailing lists, don't you?</text>\n" +
                "      <fieldref var='activity.mailing-lists'/>\n" +
                "      <fieldref var='activity.xeps'/>\n" +
                "    </section>\n" +
                "    <section label='Plans and Reasoning'>\n" +
                "      <text>\n" +
                "        This is where you describe your future plans and why you think you \n" +
                "        deserve to be a member of the XMPP Standards Foundation.\n" +
                "      </text>\n" +
                "      <fieldref var='future'/>\n" +
                "      <fieldref var='reasoning'/>\n" +
                "    </section>\n" +
                "  </page>\n" +
                "</x>\n";

        DataForm dataForm = unmarshal(xml, DataForm.class);

        Assert.assertNotNull(dataForm);
        Assert.assertEquals(dataForm.getPages().size(), 1);
        Assert.assertEquals(dataForm.getPages().get(0).getSections().size(), 3);
        Assert.assertEquals(dataForm.getPages().get(0).getSections().get(0).getLabel(), "Personal Information");
        Assert.assertEquals(dataForm.getPages().get(0).getSections().get(0).getSections().size(), 2);
        Assert.assertEquals(dataForm.getPages().get(0).getSections().get(0).getSections().get(0).getFieldReferences().get(0).getVar(), "name.first");
        Assert.assertEquals(dataForm.getPages().get(0).getSections().get(0).getSections().get(0).getFieldReferences().get(1).getVar(), "name.last");
        Assert.assertEquals(dataForm.getPages().get(0).getSections().get(0).getSections().get(1).getFieldReferences().get(0).getVar(), "email");
        Assert.assertEquals(dataForm.getPages().get(0).getSections().get(0).getSections().get(1).getFieldReferences().get(1).getVar(), "jid");
        Assert.assertEquals(dataForm.getPages().get(0).getSections().get(0).getFieldReferences().get(0).getVar(), "background");
    }

    @Test
    public void unmarshalDataFormWithReportedFields() throws XMLStreamException, JAXBException {
        String xml = "<x xmlns='jabber:x:data' type='form'>\n" +
                "  <page xmlns='http://jabber.org/protocol/xdata-layout'>\n" +
                "    <section label='Personal Information'>\n" +
                "      <text>\n" +
                "        Note: In accordance with the XSF privacy policy, your personal information will \n" +
                "        never be shared outside the organization in any way for any purpose; however, \n" +
                "        your name and JID may be published in the XSF membership directory.\n" +
                "      </text>\n" +
                "      <text>Who are you?</text>\n" +
                "      <reportedref var='name.first'/>\n" +
                "    </section>\n" +
                "    <reportedref var='background'/>\n" +
                "  </page>\n" +
                "</x>\n";

        DataForm dataForm = unmarshal(xml, DataForm.class);

        Assert.assertNotNull(dataForm);
        Assert.assertEquals(dataForm.getPages().size(), 1);
        Assert.assertEquals(dataForm.getPages().get(0).getSections().size(), 1);
        Assert.assertEquals(dataForm.getPages().get(0).getReportedReference().getVar(), "background");
        Assert.assertEquals(dataForm.getPages().get(0).getSections().get(0).getReportedReference().getVar(), "name.first");
    }
}
