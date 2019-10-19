/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 Christian Schudt
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

package rocks.xmpp.extensions.mood.model;

import rocks.xmpp.util.Strings;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Objects;

/**
 * The implementation of the {@code <mood/>} element in the {@code http://jabber.org/protocol/mood} namespace.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/extensions/xep-0107.html">XEP-0107: User Mood</a></cite></p>
 * <p>This specification defines a payload format for communicating information about user moods, such as whether a person is currently happy, sad, angy, or annoyed. The payload format is typically transported using the personal eventing protocol, a profile of XMPP publish-subscribe specified in XEP-0163.</p>
 * </blockquote>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0107.html">XEP-0107: User Mood</a>
 * @see <a href="https://xmpp.org/extensions/xep-0107.html#schema">XML Schema</a>
 */
@XmlRootElement
@XmlSeeAlso({
        Mood.Afraid.class,
        Mood.Amazed.class,
        Mood.Amorous.class,
        Mood.Angry.class,
        Mood.Annoyed.class,
        Mood.Anxious.class,
        Mood.Aroused.class,
        Mood.Ashamed.class,
        Mood.Bored.class,
        Mood.Brave.class,
        Mood.Calm.class,
        Mood.Cautious.class,
        Mood.Cold.class,
        Mood.Confident.class,
        Mood.Confused.class,
        Mood.Contemplative.class,
        Mood.Contented.class,
        Mood.Cranky.class,
        Mood.Crazy.class,
        Mood.Creative.class,
        Mood.Curious.class,
        Mood.Dejected.class,
        Mood.Depressed.class,
        Mood.Disappointed.class,
        Mood.Disgusted.class,
        Mood.Dismayed.class,
        Mood.Distracted.class,
        Mood.Embarrassed.class,
        Mood.Envious.class,
        Mood.Excited.class,
        Mood.Flirtatious.class,
        Mood.Frustrated.class,
        Mood.Grateful.class,
        Mood.Grieving.class,
        Mood.Grumpy.class,
        Mood.Guilty.class,
        Mood.Happy.class,
        Mood.Hopeful.class,
        Mood.Hot.class,
        Mood.Humbled.class,
        Mood.Humiliated.class,
        Mood.Hungry.class,
        Mood.Hurt.class,
        Mood.Impressed.class,
        Mood.InAwe.class,
        Mood.InLove.class,
        Mood.Indignant.class,
        Mood.Interested.class,
        Mood.Intoxicated.class,
        Mood.Invincible.class,
        Mood.Jealous.class,
        Mood.Lonely.class,
        Mood.Lost.class,
        Mood.Lucky.class,
        Mood.Mean.class,
        Mood.Moody.class,
        Mood.Nervous.class,
        Mood.Neutral.class,
        Mood.Offended.class,
        Mood.Outraged.class,
        Mood.Playful.class,
        Mood.Proud.class,
        Mood.Relaxed.class,
        Mood.Relieved.class,
        Mood.Remorseful.class,
        Mood.Restless.class,
        Mood.Sad.class,
        Mood.Sarcastic.class,
        Mood.Satisfied.class,
        Mood.Serious.class,
        Mood.Shocked.class,
        Mood.Shy.class,
        Mood.Sick.class,
        Mood.Sleepy.class,
        Mood.Spontaneous.class,
        Mood.Stressed.class,
        Mood.Strong.class,
        Mood.Surprised.class,
        Mood.Thankful.class,
        Mood.Thirsty.class,
        Mood.Tired.class,
        Mood.Undefined.class,
        Mood.Weak.class,
        Mood.Worried.class,
})
public final class Mood {

    /**
     * http://jabber.org/protocol/mood
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/mood";

    private final String text;

    @XmlElements({@XmlElement(name = "afraid", type = Afraid.class),
            @XmlElement(name = "amazed", type = Amazed.class),
            @XmlElement(name = "angry", type = Angry.class),
            @XmlElement(name = "amorous", type = Amorous.class),
            @XmlElement(name = "annoyed", type = Annoyed.class),
            @XmlElement(name = "anxious", type = Anxious.class),
            @XmlElement(name = "aroused", type = Aroused.class),
            @XmlElement(name = "ashamed", type = Ashamed.class),
            @XmlElement(name = "bored", type = Bored.class),
            @XmlElement(name = "brave", type = Brave.class),
            @XmlElement(name = "calm", type = Calm.class),
            @XmlElement(name = "cautious", type = Cautious.class),
            @XmlElement(name = "cold", type = Cold.class),
            @XmlElement(name = "confident", type = Confident.class),
            @XmlElement(name = "confused", type = Confused.class),
            @XmlElement(name = "contemplative", type = Contemplative.class),
            @XmlElement(name = "contented", type = Contented.class),
            @XmlElement(name = "cranky", type = Cranky.class),
            @XmlElement(name = "crazy", type = Crazy.class),
            @XmlElement(name = "creative", type = Creative.class),
            @XmlElement(name = "curious", type = Curious.class),
            @XmlElement(name = "dejected", type = Dejected.class),
            @XmlElement(name = "depressed", type = Depressed.class),
            @XmlElement(name = "disappointed", type = Disappointed.class),
            @XmlElement(name = "disgusted", type = Disgusted.class),
            @XmlElement(name = "dismayed", type = Dismayed.class),
            @XmlElement(name = "distracted", type = Distracted.class),
            @XmlElement(name = "embarrassed", type = Embarrassed.class),
            @XmlElement(name = "envious", type = Envious.class),
            @XmlElement(name = "excited", type = Excited.class),
            @XmlElement(name = "flirtatious", type = Flirtatious.class),
            @XmlElement(name = "frustrated", type = Frustrated.class),
            @XmlElement(name = "grumpy", type = Grumpy.class),
            @XmlElement(name = "guilty", type = Guilty.class),
            @XmlElement(name = "happy", type = Happy.class),
            @XmlElement(name = "hopeful", type = Hopeful.class),
            @XmlElement(name = "hot", type = Hot.class),
            @XmlElement(name = "humbled", type = Humbled.class),
            @XmlElement(name = "humiliated", type = Humiliated.class),
            @XmlElement(name = "hungry", type = Hungry.class),
            @XmlElement(name = "hurt", type = Hurt.class),
            @XmlElement(name = "impressed", type = Impressed.class),
            @XmlElement(name = "in_awe", type = InAwe.class),
            @XmlElement(name = "in_love", type = InLove.class),
            @XmlElement(name = "indignant", type = Indignant.class),
            @XmlElement(name = "interested", type = Interested.class),
            @XmlElement(name = "intoxicated", type = Intoxicated.class),
            @XmlElement(name = "invincible", type = Invincible.class),
            @XmlElement(name = "jealous", type = Jealous.class),
            @XmlElement(name = "lonely", type = Lonely.class),
            @XmlElement(name = "lucky", type = Lucky.class),
            @XmlElement(name = "mean", type = Mean.class),
            @XmlElement(name = "moody", type = Moody.class),
            @XmlElement(name = "nervous", type = Nervous.class),
            @XmlElement(name = "neutral", type = Neutral.class),
            @XmlElement(name = "offended", type = Offended.class),
            @XmlElement(name = "outraged", type = Outraged.class),
            @XmlElement(name = "playful", type = Playful.class),
            @XmlElement(name = "proud", type = Proud.class),
            @XmlElement(name = "relaxed", type = Relaxed.class),
            @XmlElement(name = "relieved", type = Relieved.class),
            @XmlElement(name = "remorseful", type = Remorseful.class),
            @XmlElement(name = "restless", type = Restless.class),
            @XmlElement(name = "sad", type = Sad.class),
            @XmlElement(name = "sarcastic", type = Sarcastic.class),
            @XmlElement(name = "serious", type = Serious.class),
            @XmlElement(name = "shocked", type = Shocked.class),
            @XmlElement(name = "shy", type = Shy.class),
            @XmlElement(name = "sick", type = Sick.class),
            @XmlElement(name = "sleepy", type = Sleepy.class),
            @XmlElement(name = "spontaneous", type = Spontaneous.class),
            @XmlElement(name = "stressed", type = Stressed.class),
            @XmlElement(name = "strong", type = Strong.class),
            @XmlElement(name = "surprised", type = Surprised.class),
            @XmlElement(name = "thankful", type = Thankful.class),
            @XmlElement(name = "thirsty", type = Thirsty.class),
            @XmlElement(name = "tired", type = Tired.class),
            @XmlElement(name = "undefined", type = Undefined.class),
            @XmlElement(name = "weak", type = Weak.class),
            @XmlElement(name = "worried", type = Worried.class)})
    private final Value value;

    /**
     * Creates an empty mood, which indicates that no mood is used.
     */
    public Mood() {
        this.value = null;
        this.text = null;
    }

    /**
     * Creates a mood with a specific value.
     *
     * @param value The mood value.
     */
    public Mood(Value value) {
        this(value, null);
    }

    /**
     * Creates a mood with a specific value and a text.
     *
     * @param value The mood value.
     * @param text  A natural-language description of, or reason for, the mood.
     */
    public Mood(Value value, String text) {
        this.value = Objects.requireNonNull(value);
        this.text = text;
    }

    /**
     * Gets a natural-language description of, or reason for, the mood.
     *
     * @return The description.
     */
    public final String getText() {
        return text;
    }

    /**
     * Gets the mood value.
     *
     * @return The mood value.
     */
    public final Value getValue() {
        return value;
    }

    /**
     * Gets the mood value.
     *
     * @return The mood value.
     */
    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        if (value != null) {
            sb.append(value);
        }
        if (text != null) {
            sb.append(" (").append(text).append(')');
        }
        return sb.toString();
    }

    /**
     * An abstract base class for all possible mood values.
     */
    @XmlTransient
    public abstract static class Value {

        @XmlAnyElement(lax = true)
        private Object specificMood;

        private Value() {
        }

        private Value(Object specificMood) {
            this.specificMood = specificMood;
        }

        /**
         * Gets the specific mood value.
         *
         * @return The specific mood.
         */
        public final Object getSpecificMood() {
            return specificMood;
        }

        @Override
        public final String toString() {
            return Strings.toUnderscore(getClass().getSimpleName());
        }
    }

    /**
     * Impressed with fear or apprehension; in fear; apprehensive.
     */
    public static final class Afraid extends Value {
        public Afraid() {
        }

        public Afraid(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Astonished; confounded with fear, surprise or wonder.
     */
    public static final class Amazed extends Value {
        public Amazed() {
        }

        public Amazed(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Inclined to love; having a propensity to love, or to sexual enjoyment; loving, fond, affectionate, passionate, lustful, sexual, etc.
     */
    public static final class Amorous extends Value {
        public Amorous() {
        }

        public Amorous(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Displaying or feeling anger, i.e., a strong feeling of displeasure, hostility or antagonism towards someone or something, usually combined with an urge to harm.
     */
    public static final class Angry extends Value {
        public Angry() {
        }

        public Angry(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * To be disturbed or irritated, especially by continued or repeated acts.
     */
    public static final class Annoyed extends Value {
        public Annoyed() {
        }

        public Annoyed(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Full of anxiety or disquietude; greatly concerned or solicitous, esp. respecting something future or unknown; being in painful suspense.
     */
    public static final class Anxious extends Value {
        public Anxious() {
        }

        public Anxious(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * To be stimulated in one's feelings, especially to be sexually stimulated.
     */
    public static final class Aroused extends Value {
        public Aroused() {
        }

        public Aroused(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * To be stimulated in one's feelings, especially to be sexually stimulated.
     */
    public static final class Ashamed extends Value {
        public Ashamed() {
        }

        public Ashamed(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Suffering from boredom; uninterested, without attention.
     */
    public static final class Bored extends Value {
        public Bored() {
        }

        public Bored(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Strong in the face of fear; courageous.
     */
    public static final class Brave extends Value {
        public Brave() {
        }

        public Brave(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Peaceful, quiet.
     */
    public static final class Calm extends Value {
        public Calm() {
        }

        public Calm(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Taking care or caution; tentative.
     */
    public static final class Cautious extends Value {
        public Cautious() {
        }

        public Cautious(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling the sensation of coldness, especially to the point of discomfort.
     */
    public static final class Cold extends Value {
        public Cold() {
        }

        public Cold(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling very sure of or positive about something, especially about one's own capabilities.
     */
    public static final class Confident extends Value {
        public Confident() {
        }

        public Confident(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Chaotic, jumbled or muddled.
     */
    public static final class Confused extends Value {
        public Confused() {
        }

        public Confused(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling introspective or thoughtful.
     */
    public static final class Contemplative extends Value {
        public Contemplative() {
        }

        public Contemplative(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Pleased at the satisfaction of a want or desire; satisfied.
     */
    public static final class Contented extends Value {
        public Contented() {
        }

        public Contented(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Grouchy, irritable; easily upset.
     */
    public static final class Cranky extends Value {
        public Cranky() {
        }

        public Cranky(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling out of control; feeling overly excited or enthusiastic.
     */
    public static final class Crazy extends Value {
        public Crazy() {
        }

        public Crazy(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling original, expressive, or imaginative.
     */
    public static final class Creative extends Value {
        public Creative() {
        }

        public Creative(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Inquisitive; tending to ask questions, investigate, or explore.
     */
    public static final class Curious extends Value {
        public Curious() {
        }

        public Curious(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling sad and dispirited.
     */
    public static final class Dejected extends Value {
        public Dejected() {
        }

        public Dejected(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Severely despondent and unhappy.
     */
    public static final class Depressed extends Value {
        public Depressed() {
        }

        public Depressed(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Defeated of expectation or hope; let down.
     */
    public static final class Disappointed extends Value {
        public Disappointed() {
        }

        public Disappointed(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Filled with disgust; irritated and out of patience.
     */
    public static final class Disgusted extends Value {
        public Disgusted() {
        }

        public Disgusted(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling a sudden or complete loss of courage in the face of trouble or danger.
     */
    public static final class Dismayed extends Value {
        public Dismayed() {
        }

        public Dismayed(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Having one's attention diverted; preoccupied.
     */
    public static final class Distracted extends Value {
        public Distracted() {
        }

        public Distracted(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Having a feeling of shameful discomfort.
     */
    public static final class Embarrassed extends Value {
        public Embarrassed() {
        }

        public Embarrassed(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling pain by the excellence or good fortune of another.
     */
    public static final class Envious extends Value {
        public Envious() {
        }

        public Envious(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Having great enthusiasm.
     */
    public static final class Excited extends Value {
        public Excited() {
        }

        public Excited(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * In the mood for flirting.
     */
    public static final class Flirtatious extends Value {
        public Flirtatious() {
        }

        public Flirtatious(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Suffering from frustration; dissatisfied, agitated, or discontented because one is unable to perform an action or fulfill a desire.
     */
    public static final class Frustrated extends Value {
        public Frustrated() {
        }

        public Frustrated(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling appreciation or thanks.
     */
    public static final class Grateful extends Value {
        public Grateful() {
        }

        public Grateful(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling very sad about something, especially something lost; mournful; sorrowful.
     */
    public static final class Grieving extends Value {
        public Grieving() {
        }

        public Grieving(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Unhappy and irritable.
     */
    public static final class Grumpy extends Value {
        public Grumpy() {
        }

        public Grumpy(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling responsible for wrongdoing; feeling blameworthy.
     */
    public static final class Guilty extends Value {
        public Guilty() {
        }

        public Guilty(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Experiencing the effect of favourable fortune; having the feeling arising from the consciousness of well-being or of enjoyment; enjoying good of any kind, as peace, tranquillity, comfort; contented; joyous.
     */
    public static final class Happy extends Value {
        public Happy() {
        }

        public Happy(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Having a positive feeling, belief, or expectation that something wished for can or will happen.
     */
    public static final class Hopeful extends Value {
        public Hopeful() {
        }

        public Hopeful(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling the sensation of heat, especially to the point of discomfort.
     */
    public static final class Hot extends Value {
        public Hot() {
        }

        public Hot(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Having or showing a modest or low estimate of one's own importance; feeling lowered in dignity or importance.
     */
    public static final class Humbled extends Value {
        public Humbled() {
        }

        public Humbled(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling deprived of dignity or self-respect.
     */
    public static final class Humiliated extends Value {
        public Humiliated() {
        }

        public Humiliated(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Having a physical need for food.
     */
    public static final class Hungry extends Value {
        public Hungry() {
        }

        public Hungry(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Wounded, injured, or pained, whether physically or emotionally.
     */
    public static final class Hurt extends Value {
        public Hurt() {
        }

        public Hurt(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Favourably affected by something or someone.
     */
    public static final class Impressed extends Value {
        public Impressed() {
        }

        public Impressed(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling amazement at something or someone; or feeling a combination of fear and reverence.
     */
    public static final class InAwe extends Value {
        public InAwe() {
        }

        public InAwe(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling strong affection, care, liking, or attraction..
     */
    public static final class InLove extends Value {
        public InLove() {
        }

        public InLove(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Showing anger or indignation, especially at something unjust or wrong.
     */
    public static final class Indignant extends Value {
        public Indignant() {
        }

        public Indignant(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Showing great attention to something or someone; having or showing interest.
     */
    public static final class Interested extends Value {
        public Interested() {
        }

        public Interested(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Under the influence of alcohol; drunk.
     */
    public static final class Intoxicated extends Value {
        public Intoxicated() {
        }

        public Intoxicated(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling as if one cannot be defeated, overcome or denied.
     */
    public static final class Invincible extends Value {
        public Invincible() {
        }

        public Invincible(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Fearful of being replaced in position or affection.
     */
    public static final class Jealous extends Value {
        public Jealous() {
        }

        public Jealous(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling isolated, empty, or abandoned.
     */
    public static final class Lonely extends Value {
        public Lonely() {
        }

        public Lonely(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Unable to find one's way, either physically or emotionally.
     */
    public static final class Lost extends Value {
        public Lost() {
        }

        public Lost(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling as if one will be favored by luck.
     */
    public static final class Lucky extends Value {
        public Lucky() {
        }

        public Lucky(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Causing or intending to cause intentional harm; bearing ill will towards another; cruel; malicious.
     */
    public static final class Mean extends Value {
        public Mean() {
        }

        public Mean(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Given to sudden or frequent changes of mind or feeling; temperamental.
     */
    public static final class Moody extends Value {
        public Moody() {
        }

        public Moody(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Easily agitated or alarmed; apprehensive or anxious.
     */
    public static final class Nervous extends Value {
        public Nervous() {
        }

        public Nervous(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Not having a strong mood or emotional state.
     */
    public static final class Neutral extends Value {
        public Neutral() {
        }

        public Neutral(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling emotionally hurt, displeased, or insulted.
     */
    public static final class Offended extends Value {
        public Offended() {
        }

        public Offended(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling resentful anger caused by an extremely violent or vicious attack, or by an offensive, immoral, or indecent act.
     */
    public static final class Outraged extends Value {
        public Outraged() {
        }

        public Outraged(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Interested in play; fun, recreational, unserious, lighthearted; joking, silly.
     */
    public static final class Playful extends Value {
        public Playful() {
        }

        public Playful(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling a sense of one's own worth or accomplishment.
     */
    public static final class Proud extends Value {
        public Proud() {
        }

        public Proud(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Having an easy-going mood; not stressed; calm.
     */
    public static final class Relaxed extends Value {
        public Relaxed() {
        }

        public Relaxed(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling uplifted because of the removal of stress or discomfort.
     */
    public static final class Relieved extends Value {
        public Relieved() {
        }

        public Relieved(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling regret or sadness for doing something wrong.
     */
    public static final class Remorseful extends Value {
        public Remorseful() {
        }

        public Remorseful(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Without rest; unable to be still or quiet; uneasy; continually moving.
     */
    public static final class Restless extends Value {
        public Restless() {
        }

        public Restless(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling sorrow; sorrowful, mournful.
     */
    public static final class Sad extends Value {
        public Sad() {
        }

        public Sad(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Mocking and ironical.
     */
    public static final class Sarcastic extends Value {
        public Sarcastic() {
        }

        public Sarcastic(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Pleased at the fulfillment of a need or desire.
     */
    public static final class Satisfied extends Value {
        public Satisfied() {
        }

        public Satisfied(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Without humor or expression of happiness; grave in manner or disposition; earnest; thoughtful; solemn.
     */
    public static final class Serious extends Value {
        public Serious() {
        }

        public Serious(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Surprised, startled, confused, or taken aback.
     */
    public static final class Shocked extends Value {
        public Shocked() {
        }

        public Shocked(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling easily frightened or scared; timid; reserved or coy.
     */
    public static final class Shy extends Value {
        public Shy() {
        }

        public Shy(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling in poor health; ill.
     */
    public static final class Sick extends Value {
        public Sick() {
        }

        public Sick(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling the need for sleep.
     */
    public static final class Sleepy extends Value {
        public Sleepy() {
        }

        public Sleepy(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Acting without planning; natural; impulsive.
     */
    public static final class Spontaneous extends Value {
        public Spontaneous() {
        }

        public Spontaneous(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Suffering emotional pressure.
     */
    public static final class Stressed extends Value {
        public Stressed() {
        }

        public Stressed(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Capable of producing great physical force; or, emotionally forceful, able, determined, unyielding.
     */
    public static final class Strong extends Value {
        public Strong() {
        }

        public Strong(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Experiencing a feeling caused by something unexpected.
     */
    public static final class Surprised extends Value {
        public Surprised() {
        }

        public Surprised(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Showing appreciation or gratitude.
     */
    public static final class Thankful extends Value {
        public Thankful() {
        }

        public Thankful(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling the need to drink.
     */
    public static final class Thirsty extends Value {
        public Thirsty() {
        }

        public Thirsty(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * In need of rest or sleep.
     */
    public static final class Tired extends Value {
        public Tired() {
        }

        public Tired(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Feeling any emotion.
     */
    public static final class Undefined extends Value {
        public Undefined() {
        }

        public Undefined(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Lacking in force or ability, either physical or emotional.
     */
    public static final class Weak extends Value {
        public Weak() {
        }

        public Weak(Object specificMood) {
            super(specificMood);
        }
    }

    /**
     * Thinking about unpleasant things that have happened or that might happen; feeling afraid and unhappy.
     */
    public static final class Worried extends Value {
        public Worried() {
        }

        public Worried(Object specificMood) {
            super(specificMood);
        }
    }
}
