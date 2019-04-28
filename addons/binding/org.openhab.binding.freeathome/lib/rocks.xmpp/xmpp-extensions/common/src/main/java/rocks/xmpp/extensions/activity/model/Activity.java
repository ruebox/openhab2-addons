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

package rocks.xmpp.extensions.activity.model;

import rocks.xmpp.util.Strings;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The implementation of the {@code <activity/>} element in the {@code http://jabber.org/protocol/activity} namespace.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/extensions/xep-0108.html#activities">3. Activity Values</a></cite></p>
 * <p>Each activity has a REQUIRED general category and an OPTIONAL specific instance. One can understand each specifier as '[user] is [activity]' (e.g., 'Juliet is partying'), where the relevant value is the most specific activity provided (e.g., specifically "partying" rather than generally "relaxing").</p>
 * </blockquote>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0108.html">XEP-0108: User Activity</a>
 * @see <a href="https://xmpp.org/extensions/xep-0108.html#schema">XML Schema</a>
 */
@XmlRootElement
public final class Activity {

    /**
     * http://jabber.org/protocol/activity
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/activity";

    @XmlElements({
            @XmlElement(name = "doing_chores", type = AbstractCategory.DoingChores.class),
            @XmlElement(name = "drinking", type = AbstractCategory.Drinking.class),
            @XmlElement(name = "eating", type = AbstractCategory.Eating.class),
            @XmlElement(name = "exercising", type = AbstractCategory.Exercising.class),
            @XmlElement(name = "grooming", type = AbstractCategory.Grooming.class),
            @XmlElement(name = "having_appointment", type = AbstractCategory.HavingAppointment.class),
            @XmlElement(name = "inactive", type = AbstractCategory.Inactive.class),
            @XmlElement(name = "relaxing", type = AbstractCategory.Relaxing.class),
            @XmlElement(name = "talking", type = AbstractCategory.Talking.class),
            @XmlElement(name = "traveling", type = AbstractCategory.Traveling.class),
            @XmlElement(name = "undefined", type = AbstractCategory.Undefined.class),
            @XmlElement(name = "working", type = AbstractCategory.Working.class)
    })
    private final AbstractCategory category;

    private final String text;

    /**
     * Creates an empty activity which is used to disable publishing an activity.
     */
    public Activity() {
        this.category = null;
        this.text = null;
    }

    /**
     * Creates an activity with a category.
     *
     * @param category The category.
     */
    public Activity(Category category) {
        this(category, null, null);
    }

    /**
     * Creates an activity with a category and text.
     *
     * @param category The category.
     * @param text     The text.
     */
    public Activity(Category category, String text) {
        this(category, null, text);
    }

    /**
     * Creates an activity with a category and a specific activity.
     *
     * @param category         The category.
     * @param specificActivity The specific activity.
     */
    public Activity(Category category, SpecificActivity specificActivity) {
        this(category, specificActivity, null);
    }

    /**
     * Creates an activity with a category, a specific activity and a text.
     *
     * @param category         The category.
     * @param specificActivity The specific activity.
     * @param text             The text.
     */
    public Activity(Category category, SpecificActivity specificActivity, String text) {
        try {
            this.category = category.categoryClass.getDeclaredConstructor(SpecificActivity.class).newInstance(specificActivity);
            this.text = text;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the text of the activity.
     *
     * @return The text.
     */
    public final String getText() {
        return text;
    }

    /**
     * Gets the category.
     *
     * @return The category.
     */
    public final Category getCategory() {
        return category != null ? category.category : null;
    }

    /**
     * Gets the specific activity.
     *
     * @return The activity.
     */
    public final SpecificActivity getSpecificActivity() {
        return category != null ? category.specificActivity : null;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        if (category != null) {
            sb.append(category);
            if (category.specificActivity != null) {
                sb.append(" / ").append(category.specificActivity);
            }
            if (text != null) {
                sb.append(" (").append(text).append(')');
            }
            return sb.toString();
        }
        return super.toString();
    }

    /**
     * An abstract class for general activities.
     */
    @XmlTransient
    abstract static class AbstractCategory {

        @XmlTransient
        private final Category category;

        @XmlElementRef
        private final SpecificActivity specificActivity;

        private AbstractCategory() {
            this(null, null);
        }

        private AbstractCategory(Category category) {
            this(category, null);
        }

        private AbstractCategory(Category category, SpecificActivity specificActivity) {
            this.category = category;
            this.specificActivity = specificActivity;
        }

        @Override
        public final String toString() {
            return Strings.toUnderscore(getClass().getSimpleName());
        }

        /**
         * The "doing_chores" activity.
         */
        static final class DoingChores extends AbstractCategory {
            DoingChores() {
                super(Category.DOING_CHORES);
            }

            DoingChores(SpecificActivity specificActivity) {
                super(Category.DOING_CHORES, specificActivity);
            }
        }

        /**
         * The "drinking" activity.
         */
        static final class Drinking extends AbstractCategory {
            Drinking() {
                super(Category.DRINKING);
            }

            Drinking(SpecificActivity specificActivity) {
                super(Category.DRINKING, specificActivity);
            }
        }

        /**
         * The "eating" activity.
         */
        static final class Eating extends AbstractCategory {
            Eating() {
                super(Category.EATING);
            }

            Eating(SpecificActivity specificActivity) {
                super(Category.EATING, specificActivity);
            }
        }

        /**
         * The "exercising" activity.
         */
        static final class Exercising extends AbstractCategory {
            Exercising() {
                super(Category.EXERCISING);
            }

            Exercising(SpecificActivity specificActivity) {
                super(Category.EXERCISING, specificActivity);
            }
        }

        /**
         * The "grooming" activity.
         */
        static final class Grooming extends AbstractCategory {
            Grooming() {
                super(Category.GROOMING);
            }

            Grooming(SpecificActivity specificActivity) {
                super(Category.GROOMING, specificActivity);
            }
        }

        /**
         * The "having_appointment" activity.
         */
        static final class HavingAppointment extends AbstractCategory {
            HavingAppointment() {
                super(Category.HAVING_APPOINTMENT);
            }

            HavingAppointment(SpecificActivity specificActivity) {
                super(Category.HAVING_APPOINTMENT, specificActivity);
            }
        }

        /**
         * The "inactive" activity.
         */
        static final class Inactive extends AbstractCategory {
            Inactive() {
                super(Category.INACTIVE);
            }

            Inactive(SpecificActivity specificActivity) {
                super(Category.INACTIVE, specificActivity);
            }
        }

        /**
         * The "relaxing" activity.
         */
        static final class Relaxing extends AbstractCategory {
            Relaxing() {
                super(Category.RELAXING);
            }

            Relaxing(SpecificActivity specificActivity) {
                super(Category.RELAXING, specificActivity);
            }
        }

        /**
         * The "talking" activity.
         */
        static final class Talking extends AbstractCategory {
            Talking() {
                super(Category.TALKING);
            }

            Talking(SpecificActivity specificActivity) {
                super(Category.TALKING, specificActivity);
            }
        }

        /**
         * The "traveling" activity.
         */
        static final class Traveling extends AbstractCategory {
            Traveling() {
                super(Category.TRAVELING);
            }

            Traveling(SpecificActivity specificActivity) {
                super(Category.TRAVELING, specificActivity);
            }
        }

        /**
         * The "doing_chores" activity.
         */
        static final class Undefined extends AbstractCategory {
            Undefined() {
                super(Category.UNDEFINED);
            }

            Undefined(SpecificActivity specificActivity) {
                super(Category.UNDEFINED, specificActivity);
            }
        }

        /**
         * The "working" activity.
         */
        static final class Working extends AbstractCategory {
            Working() {
                super(Category.WORKING);
            }

            Working(SpecificActivity specificActivity) {
                super(Category.WORKING, specificActivity);
            }
        }
    }
}
