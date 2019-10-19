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

package rocks.xmpp.extensions.activity;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.activity.model.Activity;
import rocks.xmpp.extensions.activity.model.Category;
import rocks.xmpp.extensions.activity.model.SpecificActivity;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class ActivityTest extends XmlTest {

    protected ActivityTest() throws JAXBException {
        super(Activity.class);
    }

    @Test
    public void unmarshalActivityWithText() throws XMLStreamException, JAXBException {
        String xml = "<activity xmlns='http://jabber.org/protocol/activity'>\n" +
                "  <relaxing>\n" +
                "    <partying/>\n" +
                "  </relaxing>\n" +
                "  <text xml:lang='en'>My nurse&apos;s birthday!</text>\n" +
                "</activity>";

        Activity activity = unmarshal(xml, Activity.class);
        Assert.assertNotNull(activity);
        Assert.assertEquals(activity.getText(), "My nurse's birthday!");
        Assert.assertEquals(activity.getCategory(), Category.RELAXING);
        Assert.assertTrue(activity.getSpecificActivity() instanceof SpecificActivity.Partying);
        Assert.assertEquals(activity.toString(), "relaxing / partying (My nurse's birthday!)");
    }

    @Test
    public void unmarshalSpecificSpecificActivity() throws XMLStreamException, JAXBException {
        String xml = "<activity xmlns='http://jabber.org/protocol/activity'>\n" +
                "  <inactive>\n" +
                "    <sleeping>\n" +
                "      <hibernating xmlns='http://www.ursus.info/states'/>\n" +
                "    </sleeping>\n" +
                "  </inactive>\n" +
                "</activity>";
        Activity activity = unmarshal(xml, Activity.class);
        Assert.assertNotNull(activity);
        Assert.assertEquals(activity.getCategory(), Category.INACTIVE);
        Assert.assertTrue(activity.getSpecificActivity() instanceof SpecificActivity.Sleeping);
        Assert.assertNull(activity.getSpecificActivity().getSpecificActivity());
    }


    @Test
    public void marshalDoingChores() throws JAXBException, XMLStreamException {
        String xml = marshal(new Activity(Category.DOING_CHORES));
        Assert.assertEquals(xml, "<activity xmlns=\"http://jabber.org/protocol/activity\"><doing_chores></doing_chores></activity>");
    }

    @Test
    public void marshalActivityWithText() throws JAXBException, XMLStreamException {
        String xml = marshal(new Activity(Category.DOING_CHORES, "test"));
        Assert.assertEquals(xml, "<activity xmlns=\"http://jabber.org/protocol/activity\"><doing_chores></doing_chores><text>test</text></activity>");
    }

    @Test
    public void unmarshalDrinking() throws XMLStreamException, JAXBException {
        String xml = "<activity xmlns='http://jabber.org/protocol/activity'>\n" +
                "  <drinking>\n" +
                "    <partying/>\n" +
                "  </drinking>\n" +
                "</activity>";
        Activity activity = unmarshal(xml, Activity.class);
        Assert.assertNotNull(activity);
        Assert.assertEquals(activity.getCategory(), Category.DRINKING);
    }

    @Test
    public void unmarshalEating() throws XMLStreamException, JAXBException {
        String xml = "<activity xmlns='http://jabber.org/protocol/activity'>\n" +
                "  <eating>\n" +
                "    <partying/>\n" +
                "  </eating>\n" +
                "</activity>";
        Activity activity = unmarshal(xml, Activity.class);
        Assert.assertNotNull(activity);
        Assert.assertEquals(activity.getCategory(), Category.EATING);
    }

    @Test
    public void unmarshalGrooming() throws XMLStreamException, JAXBException {
        String xml = "<activity xmlns='http://jabber.org/protocol/activity'>\n" +
                "  <grooming>\n" +
                "    <partying/>\n" +
                "  </grooming>\n" +
                "</activity>";
        Activity activity = unmarshal(xml, Activity.class);
        Assert.assertNotNull(activity);
        Assert.assertEquals(activity.getCategory(), Category.GROOMING);
    }

    @Test
    public void unmarshalHavingAppointment() throws XMLStreamException, JAXBException {
        String xml = "<activity xmlns='http://jabber.org/protocol/activity'>\n" +
                "  <having_appointment>\n" +
                "    <partying/>\n" +
                "  </having_appointment>\n" +
                "</activity>";
        Activity activity = unmarshal(xml, Activity.class);
        Assert.assertNotNull(activity);
        Assert.assertEquals(activity.getCategory(), Category.HAVING_APPOINTMENT);
    }

    @Test
    public void unmarshalInactive() throws XMLStreamException, JAXBException {
        String xml = "<activity xmlns='http://jabber.org/protocol/activity'>\n" +
                "  <inactive>\n" +
                "    <partying/>\n" +
                "  </inactive>\n" +
                "</activity>";
        Activity activity = unmarshal(xml, Activity.class);
        Assert.assertNotNull(activity);
        Assert.assertEquals(activity.getCategory(), Category.INACTIVE);
    }

    @Test
    public void unmarshalRelaxing() throws XMLStreamException, JAXBException {
        String xml = "<activity xmlns='http://jabber.org/protocol/activity'>\n" +
                "  <relaxing>\n" +
                "    <partying/>\n" +
                "  </relaxing>\n" +
                "</activity>";
        Activity activity = unmarshal(xml, Activity.class);
        Assert.assertNotNull(activity);
        Assert.assertEquals(activity.getCategory(), Category.RELAXING);
    }

    @Test
    public void unmarshalTalking() throws XMLStreamException, JAXBException {
        String xml = "<activity xmlns='http://jabber.org/protocol/activity'>\n" +
                "  <talking>\n" +
                "    <partying/>\n" +
                "  </talking>\n" +
                "</activity>";
        Activity activity = unmarshal(xml, Activity.class);
        Assert.assertNotNull(activity);
        Assert.assertEquals(activity.getCategory(), Category.TALKING);
    }

    @Test
    public void unmarshalTraveling() throws XMLStreamException, JAXBException {
        String xml = "<activity xmlns='http://jabber.org/protocol/activity'>\n" +
                "  <traveling>\n" +
                "    <partying/>\n" +
                "  </traveling>\n" +
                "</activity>";
        Activity activity = unmarshal(xml, Activity.class);
        Assert.assertNotNull(activity);
        Assert.assertEquals(activity.getCategory(), Category.TRAVELING);
    }

    @Test
    public void unmarshalUndefined() throws XMLStreamException, JAXBException {
        String xml = "<activity xmlns='http://jabber.org/protocol/activity'>\n" +
                "  <undefined>\n" +
                "    <partying/>\n" +
                "  </undefined>\n" +
                "</activity>";
        Activity activity = unmarshal(xml, Activity.class);
        Assert.assertNotNull(activity);
        Assert.assertEquals(activity.getCategory(), Category.UNDEFINED);
    }

    @Test
    public void unmarshalWorking() throws XMLStreamException, JAXBException {
        String xml = "<activity xmlns='http://jabber.org/protocol/activity'>\n" +
                "  <working>\n" +
                "    <partying/>\n" +
                "  </working>\n" +
                "</activity>";
        Activity activity = unmarshal(xml, Activity.class);
        Assert.assertNotNull(activity);
        Assert.assertEquals(activity.getCategory(), Category.WORKING);
    }
}
