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
public class MoodMarshalTest extends XmlTest {
    protected MoodMarshalTest() throws JAXBException {
        super(Mood.class);
    }

    @Test
    public void marshalHappy() throws JAXBException, XMLStreamException {
        String xml = marshal(new Mood(new Mood.Happy()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><happy></happy></mood>");
    }

    @Test
    public void marshalAfraid() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Afraid()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><afraid></afraid></mood>");
    }

    @Test
    public void marshalAmazed() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Happy()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><happy></happy></mood>");
    }

    @Test
    public void marshalAngry() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Angry()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><angry></angry></mood>");
    }

    @Test
    public void marshalAmorous() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Amorous()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><amorous></amorous></mood>");
    }

    @Test
    public void marshalAnnoyed() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Annoyed()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><annoyed></annoyed></mood>");
    }

    @Test
    public void marshalAnxious() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Anxious()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><anxious></anxious></mood>");
    }

    @Test
    public void marshalAroused() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Aroused()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><aroused></aroused></mood>");
    }

    @Test
    public void marshalAshamed() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Happy()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><happy></happy></mood>");
    }

    @Test
    public void marshalBored() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Bored()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><bored></bored></mood>");
    }

    @Test
    public void marshalBrave() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Brave()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><brave></brave></mood>");
    }

    @Test
    public void marshalCalm() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Calm()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><calm></calm></mood>");
    }

    @Test
    public void marshalCautious() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Cautious()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><cautious></cautious></mood>");
    }

    @Test
    public void marshalCold() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Cold()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><cold></cold></mood>");
    }

    @Test
    public void marshalConfident() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Confident()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><confident></confident></mood>");
    }

    @Test
    public void marshalConfused() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Confused()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><confused></confused></mood>");
    }

    @Test
    public void marshalContemplative() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Contemplative()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><contemplative></contemplative></mood>");
    }

    @Test
    public void marshalContented() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Contented()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><contented></contented></mood>");
    }

    @Test
    public void marshalCranky() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Cranky()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><cranky></cranky></mood>");
    }

    @Test
    public void marshalCrazy() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Crazy()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><crazy></crazy></mood>");
    }

    @Test
    public void marshalCreative() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Creative()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><creative></creative></mood>");
    }

    @Test
    public void marshalCurious() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Curious()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><curious></curious></mood>");
    }

    @Test
    public void marshalDejected() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Dejected()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><dejected></dejected></mood>");
    }

    @Test
    public void marshalDepressed() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Happy()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><happy></happy></mood>");
    }

    @Test
    public void marshalDisappointed() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Disappointed()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><disappointed></disappointed></mood>");
    }

    @Test
    public void marshalDisgusted() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Disgusted()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><disgusted></disgusted></mood>");
    }

    @Test
    public void marshalDismayed() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Dismayed()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><dismayed></dismayed></mood>");
    }

    @Test
    public void marshalDistracted() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Distracted()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><distracted></distracted></mood>");
    }

    @Test
    public void marshalEmbarrassed() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Embarrassed()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><embarrassed></embarrassed></mood>");
    }

    @Test
    public void marshalEnvious() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Envious()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><envious></envious></mood>");
    }

    @Test
    public void marshalExcited() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Excited()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><excited></excited></mood>");
    }

    @Test
    public void marshalFlirtatious() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Flirtatious()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><flirtatious></flirtatious></mood>");
    }

    @Test
    public void marshalFrustrated() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Frustrated()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><frustrated></frustrated></mood>");
    }

    @Test
    public void marshalGrumpy() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Grumpy()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><grumpy></grumpy></mood>");
    }

    @Test
    public void marshalGuilty() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Guilty()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><guilty></guilty></mood>");
    }

    @Test
    public void marshalHopeful() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Hopeful()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><hopeful></hopeful></mood>");
    }

    @Test
    public void marshalHot() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Hot()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><hot></hot></mood>");
    }

    @Test
    public void marshalHumbled() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Humbled()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><humbled></humbled></mood>");
    }

    @Test
    public void marshalHumiliated() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Humiliated()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><humiliated></humiliated></mood>");
    }

    @Test
    public void marshalHungry() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Hungry()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><hungry></hungry></mood>");
    }

    @Test
    public void marshalHurt() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Hurt()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><hurt></hurt></mood>");
    }

    @Test
    public void marshalImpressed() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Impressed()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><impressed></impressed></mood>");
    }

    @Test
    public void marshalInAwe() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.InAwe()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><in_awe></in_awe></mood>");
    }

    @Test
    public void marshalInLove() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.InLove()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><in_love></in_love></mood>");
    }

    @Test
    public void marshalIndignant() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Indignant()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><indignant></indignant></mood>");
    }

    @Test
    public void marshalInterested() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Interested()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><interested></interested></mood>");
    }

    @Test
    public void marshalIntoxicated() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Intoxicated()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><intoxicated></intoxicated></mood>");
    }

    @Test
    public void marshalInvincible() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Invincible()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><invincible></invincible></mood>");
    }

    @Test
    public void marshalJealous() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Jealous()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><jealous></jealous></mood>");
    }

    @Test
    public void marshalonely() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Lonely()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><lonely></lonely></mood>");
    }

    @Test
    public void marshalucky() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Lucky()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><lucky></lucky></mood>");
    }

    @Test
    public void marshalMean() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Mean()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><mean></mean></mood>");
    }

    @Test
    public void marshalMoody() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Moody()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><moody></moody></mood>");
    }

    @Test
    public void marshalNervous() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Nervous()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><nervous></nervous></mood>");
    }

    @Test
    public void marshalNeutral() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Happy()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><happy></happy></mood>");
    }

    @Test
    public void marshalOffended() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Offended()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><offended></offended></mood>");
    }

    @Test
    public void marshalPlayful() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Playful()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><playful></playful></mood>");
    }

    @Test
    public void marshalProud() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Proud()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><proud></proud></mood>");
    }

    @Test
    public void marshalRelaxed() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Relaxed()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><relaxed></relaxed></mood>");
    }

    @Test
    public void marshalRelieved() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Relieved()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><relieved></relieved></mood>");
    }

    @Test
    public void marshalRemorseful() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Remorseful()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><remorseful></remorseful></mood>");
    }

    @Test
    public void marshalRestless() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Restless()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><restless></restless></mood>");
    }

    @Test
    public void marshalSad() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Sad()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><sad></sad></mood>");
    }

    @Test
    public void marshalSarcastic() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Sarcastic()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><sarcastic></sarcastic></mood>");
    }

    @Test
    public void marshalSerious() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Serious()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><serious></serious></mood>");
    }

    @Test
    public void marshalShocked() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Shocked()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><shocked></shocked></mood>");
    }

    @Test
    public void marshalShy() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Shy()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><shy></shy></mood>");
    }

    @Test
    public void marshalSick() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Sick()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><sick></sick></mood>");
    }

    @Test
    public void marshalSleepy() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Sleepy()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><sleepy></sleepy></mood>");
    }

    @Test
    public void marshalSpontaneous() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Spontaneous()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><spontaneous></spontaneous></mood>");
    }

    @Test
    public void marshalStressed() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Stressed()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><stressed></stressed></mood>");
    }

    @Test
    public void marshalStrong() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Strong()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><strong></strong></mood>");
    }

    @Test
    public void marshalSurprised() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Surprised()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><surprised></surprised></mood>");
    }

    @Test
    public void marshalThankful() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Thankful()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><thankful></thankful></mood>");
    }

    @Test
    public void marshalThirsty() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Thirsty()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><thirsty></thirsty></mood>");
    }

    @Test
    public void marshalTired() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Tired()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><tired></tired></mood>");
    }

    @Test
    public void marshalUndefined() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Undefined()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><undefined></undefined></mood>");
    }

    @Test
    public void marshalWeak() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Weak()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><weak></weak></mood>");
    }

    @Test
    public void marshalWorried() throws XMLStreamException, JAXBException {
        String xml = marshal(new Mood(new Mood.Worried()));
        Assert.assertEquals(xml, "<mood xmlns=\"http://jabber.org/protocol/mood\"><worried></worried></mood>");
    }
}
