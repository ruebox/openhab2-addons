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

package rocks.xmpp.extensions.mood;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.mood.model.Mood;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class MoodTest extends XmlTest {

    protected MoodTest() throws JAXBException {
        super(Mood.class);
    }

    @Test
    public void unmarshalHappy() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <happy>\n" +
                "    <ecstatic xmlns='http://ik.nu/ralphm'/>\n" +
                "  </happy>\n" +
                "  <text>Yay, the mood spec has been approved!</text>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertEquals(mood.getText(), "Yay, the mood spec has been approved!");
        Assert.assertTrue(mood.getValue() instanceof Mood.Happy);
        Assert.assertTrue(mood.getValue().getSpecificMood() != null);
        Assert.assertEquals(mood.toString(), "happy (Yay, the mood spec has been approved!)");
    }

    @Test
    public void unmarshalAfraid() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <afraid/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Afraid);
    }

    @Test
    public void unmarshalAmazed() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <amazed/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Amazed);
    }

    @Test
    public void unmarshalAngry() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <angry/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Angry);
    }

    @Test
    public void unmarshalAmorous() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <amorous/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Amorous);
    }

    @Test
    public void unmarshalAnnoyed() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <annoyed/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Annoyed);
    }

    @Test
    public void unmarshalAnxious() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <anxious/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Anxious);
    }

    @Test
    public void unmarshalAroused() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <aroused/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Aroused);
    }

    @Test
    public void unmarshalAshamed() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <ashamed/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Ashamed);
    }

    @Test
    public void unmarshalBored() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <bored/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Bored);
    }

    @Test
    public void unmarshalBrave() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <brave/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Brave);
    }

    @Test
    public void unmarshalCalm() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <calm/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Calm);
    }

    @Test
    public void unmarshalCautious() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <cautious/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Cautious);
    }

    @Test
    public void unmarshalCold() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <cold/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Cold);
    }

    @Test
    public void unmarshalConfident() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <confident/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Confident);
    }

    @Test
    public void unmarshalConfused() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <confused/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Confused);
    }

    @Test
    public void unmarshalContemplative() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <contemplative/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Contemplative);
    }

    @Test
    public void unmarshalContented() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <contented/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Contented);
    }

    @Test
    public void unmarshalCranky() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <cranky/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Cranky);
    }

    @Test
    public void unmarshalCrazy() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <crazy/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Crazy);
    }

    @Test
    public void unmarshalCreative() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <creative/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Creative);
    }

    @Test
    public void unmarshalCurious() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <curious/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Curious);
    }

    @Test
    public void unmarshalDejected() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <dejected/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Dejected);
    }

    @Test
    public void unmarshalDepressed() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <depressed/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Depressed);
    }

    @Test
    public void unmarshalDisappointed() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <disappointed/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Disappointed);
    }

    @Test
    public void unmarshalDisgusted() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <disgusted/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Disgusted);
    }

    @Test
    public void unmarshalDismayed() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <dismayed/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Dismayed);
    }

    @Test
    public void unmarshalDistracted() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <distracted/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Distracted);
    }

    @Test
    public void unmarshalEmbarrassed() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <embarrassed/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Embarrassed);
    }

    @Test
    public void unmarshalEnvious() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <envious/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Envious);
    }

    @Test
    public void unmarshalExcited() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <excited/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Excited);
    }

    @Test
    public void unmarshalFrustrated() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <frustrated/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Frustrated);
    }

    @Test
    public void unmarshalGrumpy() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <grumpy/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Grumpy);
    }

    @Test
    public void unmarshalGuilty() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <guilty/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Guilty);
    }

    @Test
    public void unmarshalFlirtatious() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <flirtatious/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Flirtatious);
    }

    @Test
    public void unmarshalHopeful() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <hopeful/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Hopeful);
    }

    @Test
    public void unmarshalHot() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <hot/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Hot);
    }

    @Test
    public void unmarshalHumbled() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <humbled/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Humbled);
    }

    @Test
    public void unmarshalHumiliated() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <humiliated/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Humiliated);
    }

    @Test
    public void unmarshalHungry() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <hungry/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Hungry);
    }

    @Test
    public void unmarshalHurt() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <hurt/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Hurt);
    }

    @Test
    public void unmarshalImpressed() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <impressed/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Impressed);
        Assert.assertEquals(mood.getValue().toString(), "impressed");
    }

    @Test
    public void unmarshalInAwe() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <in_awe/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.InAwe);
        Assert.assertEquals(mood.getValue().toString(), "in_awe");
    }

    @Test
    public void unmarshalInLove() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <in_love/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.InLove);
    }

    @Test
    public void unmarshalIndignant() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <indignant/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Indignant);
    }

    @Test
    public void unmarshalInterested() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <interested/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Interested);
    }

    @Test
    public void unmarshalIntoxicated() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <intoxicated/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Intoxicated);
    }

    @Test
    public void unmarshalInvincible() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <invincible/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Invincible);
    }

    @Test
    public void unmarshalJealous() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <jealous/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Jealous);
    }

    @Test
    public void unmarshalLonely() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <lonely/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Lonely);
    }

    @Test
    public void unmarshalLucky() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <lucky/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Lucky);
    }

    @Test
    public void unmarshalMean() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <mean/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Mean);
    }

    @Test
    public void unmarshalMoody() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <moody/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Moody);
    }

    @Test
    public void unmarshalNervous() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <nervous/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Nervous);
    }

    @Test
    public void unmarshalNeutral() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <neutral/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Neutral);
    }

    @Test
    public void unmarshalOffended() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <offended/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Offended);
    }

    @Test
    public void unmarshalPlayful() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <playful/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Playful);
    }

    @Test
    public void unmarshalProud() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <proud/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Proud);
    }

    @Test
    public void unmarshalRelaxed() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <relaxed/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Relaxed);
    }

    @Test
    public void unmarshalRelieved() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <relieved/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Relieved);
    }

    @Test
    public void unmarshalRemorseful() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <remorseful/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Remorseful);
    }

    @Test
    public void unmarshalRestless() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <restless/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Restless);
    }

    @Test
    public void unmarshalSad() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <sad/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Sad);
    }

    @Test
    public void unmarshalSarcastic() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <sarcastic/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Sarcastic);
    }

    @Test
    public void unmarshalSerious() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <serious/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Serious);
    }

    @Test
    public void unmarshalShocked() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <shocked/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Shocked);
    }

    @Test
    public void unmarshalShy() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <shy/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Shy);
    }

    @Test
    public void unmarshalSick() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <sick/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Sick);
    }

    @Test
    public void unmarshalSleepy() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <sleepy/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Sleepy);
    }

    @Test
    public void unmarshalSpontaneous() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <spontaneous/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Spontaneous);
    }

    @Test
    public void unmarshalStressed() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <stressed/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Stressed);
    }

    @Test
    public void unmarshalStrong() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <strong/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Strong);
    }

    @Test
    public void unmarshalSurprised() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <surprised/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Surprised);
    }

    @Test
    public void unmarshalThankful() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <thankful/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Thankful);
    }

    @Test
    public void unmarshalThirsty() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <thirsty/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Thirsty);
    }

    @Test
    public void unmarshalTired() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <tired/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Tired);
    }

    @Test
    public void unmarshalUndefined() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <undefined/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Undefined);
    }

    @Test
    public void unmarshalWeak() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <weak/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Weak);
    }

    @Test
    public void unmarshalWorried() throws XMLStreamException, JAXBException {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>\n" +
                "  <worried/>\n" +
                "</mood>\n";
        Mood mood = unmarshal(xml, Mood.class);
        Assert.assertNotNull(mood);
        Assert.assertTrue(mood.getValue() instanceof Mood.Worried);
    }
}
