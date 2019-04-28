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

package rocks.xmpp.extensions.data.validate;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.data.validate.model.Validation;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class DataFormsValidateTest extends XmlTest {
    protected DataFormsValidateTest() throws JAXBException {
        super(DataForm.class, Validation.class);
    }

    @Test
    public void unmarshalDataFormWithValidateBasic() throws XMLStreamException, JAXBException {
        String xml = "<x xmlns='jabber:x:data' type='form'>" +
                "<field var='evt.date' type='text-single' label='Event Date/Time'>\n" +
                "  <validate xmlns='http://jabber.org/protocol/xdata-validate'\n" +
                "            datatype='xs:dateTime'>\n" +
                "    <basic/>\n" +
                "  </validate>\n" +
                "  <value>2003-10-06T11:22:00-07:00</value>\n" +
                "</field>" +
                "</x>";

        DataForm dataForm = unmarshal(xml, DataForm.class);

        Assert.assertNotNull(dataForm);
        Validation validation = dataForm.getFields().get(0).getValidation();
        Assert.assertNotNull(validation);
        Assert.assertEquals(validation.getDataType(), "xs:dateTime");
        Assert.assertTrue(validation.getValidationMethod() instanceof Validation.ValidationMethod.Basic);
    }

    @Test
    public void unmarshalDataFormWithValidateOpen() throws XMLStreamException, JAXBException {
        String xml = "<x xmlns='jabber:x:data' type='form'>" +
                "<field var='evt.category' \n" +
                "       type='list-single' \n" +
                "       label='Event Category'>\n" +
                "  <validate xmlns='http://jabber.org/protocol/xdata-validate'\n" +
                "            datatype='xs:string'>\n" +
                "    <open/>\n" +
                "  </validate>\n" +
                "  <option><value>holiday</value></option>\n" +
                "  <option><value>reminder</value></option>\n" +
                "  <option><value>appointment</value></option>\n" +
                "</field>" +
                "</x>";

        DataForm dataForm = unmarshal(xml, DataForm.class);

        Assert.assertNotNull(dataForm);
        Validation validation = dataForm.getFields().get(0).getValidation();
        Assert.assertNotNull(validation);
        Assert.assertEquals(validation.getDataType(), "xs:string");
        Assert.assertTrue(validation.getValidationMethod() instanceof Validation.ValidationMethod.Open);
    }

    @Test
    public void unmarshalDataFormWithValidateRange() throws XMLStreamException, JAXBException {
        String xml = "<x xmlns='jabber:x:data' type='form'>" +
                "<field var='evt.date' type='text-single' label='Event Date/Time'>\n" +
                "  <validate xmlns='http://jabber.org/protocol/xdata-validate'\n" +
                "            datatype='xs:dateTime'>\n" +
                "    <range min='2003-10-05T00:00:00-07:00' \n" +
                "           max='2003-10-24T23:59:59-07:00'/>\n" +
                "  </validate>\n" +
                "  <value>2003-10-06T11:22:00-07:00</value>\n" +
                "</field>" +
                "</x>";

        DataForm dataForm = unmarshal(xml, DataForm.class);

        Assert.assertNotNull(dataForm);
        Validation validation = dataForm.getFields().get(0).getValidation();
        Assert.assertNotNull(validation);
        Assert.assertEquals(validation.getDataType(), "xs:dateTime");
        Assert.assertTrue(validation.getValidationMethod() instanceof Validation.ValidationMethod.Range);
        Assert.assertEquals(((Validation.ValidationMethod.Range) validation.getValidationMethod()).getMin(), "2003-10-05T00:00:00-07:00");
        Assert.assertEquals(((Validation.ValidationMethod.Range) validation.getValidationMethod()).getMax(), "2003-10-24T23:59:59-07:00");
    }

    @Test
    public void unmarshalDataFormWithValidateRegex() throws XMLStreamException, JAXBException {
        String xml = "<x xmlns='jabber:x:data' type='form'>" +
                "<field var='ssn' type='text-single' label='Social Security Number'>\n" +
                "  <desc>This field should be your SSN, including '-' (e.g. 123-12-1234)</desc>\n" +
                "  <validate xmlns='http://jabber.org/protocol/xdata-validate'\n" +
                "            datatype='xs:string'>\n" +
                "    <regex>([0-9]{3})-([0-9]{2})-([0-9]{4})</regex>\n" +
                "  </validate>\n" +
                "</field>\n" +
                "</x>";

        DataForm dataForm = unmarshal(xml, DataForm.class);

        Assert.assertNotNull(dataForm);
        Validation validation = dataForm.getFields().get(0).getValidation();
        Assert.assertNotNull(validation);
        Assert.assertEquals(validation.getDataType(), "xs:string");
        Assert.assertTrue(validation.getValidationMethod() instanceof Validation.ValidationMethod.Regex);
        Assert.assertEquals(((Validation.ValidationMethod.Regex) validation.getValidationMethod()).getRegex(), "([0-9]{3})-([0-9]{2})-([0-9]{4})");
    }

    @Test
    public void unmarshalDataFormWithValidateListRange() throws XMLStreamException, JAXBException {
        String xml = "<x xmlns='jabber:x:data' type='form'>" +
                "<field var='evt.notify-methods' \n" +
                "       type='list-multi' \n" +
                "       label='Notify me by'>\n" +
                "  <validate xmlns='http://jabber.org/protocol/xdata-validate'\n" +
                "            datatype='xs:string'>\n" +
                "    <basic/>\n" +
                "    <list-range min='1' max='3'/>\n" +
                "  </validate>\n" +
                "  <option><value>e-mail</value></option>\n" +
                "  <option><value>jabber/xmpp</value></option>\n" +
                "  <option><value>work phone</value></option>\n" +
                "  <option><value>home phone</value></option>\n" +
                "  <option><value>cell phone</value></option>\n" +
                "</field>\n" +
                "</x>";

        DataForm dataForm = unmarshal(xml, DataForm.class);

        Assert.assertNotNull(dataForm);
        Validation validation = dataForm.getFields().get(0).getValidation();

        Assert.assertNotNull(validation);
        Assert.assertEquals(validation.getDataType(), "xs:string");
        Assert.assertTrue(validation.getValidationMethod() instanceof Validation.ValidationMethod.Basic);
        Assert.assertNotNull(validation.getListRange());
        Assert.assertEquals(validation.getListRange().getMin(), (Integer) 1);
        Assert.assertEquals(validation.getListRange().getMax(), (Integer) 3);
    }
}
