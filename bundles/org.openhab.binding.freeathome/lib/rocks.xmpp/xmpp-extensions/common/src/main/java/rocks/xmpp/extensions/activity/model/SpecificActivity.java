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

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * An abstract class for specific activities.
 *
 * @author Christian Schudt
 */
@XmlSeeAlso({
        SpecificActivity.AtTheSpa.class,
        SpecificActivity.BrushingTeeth.class,
        SpecificActivity.BuyingGroceries.class,
        SpecificActivity.Cleaning.class,
        SpecificActivity.Coding.class,
        SpecificActivity.Commuting.class,
        SpecificActivity.Cooking.class,
        SpecificActivity.Cycling.class,
        SpecificActivity.Dancing.class,
        SpecificActivity.DayOff.class,
        SpecificActivity.DoingMaintenance.class,
        SpecificActivity.DoingTheDishes.class,
        SpecificActivity.DoingTheLaundry.class,
        SpecificActivity.Driving.class,
        SpecificActivity.Fishing.class,
        SpecificActivity.Gaming.class,
        SpecificActivity.Gardening.class,
        SpecificActivity.GettingAHaircut.class,
        SpecificActivity.GoingOut.class,
        SpecificActivity.HangingOut.class,
        SpecificActivity.HavingABeer.class,
        SpecificActivity.HavingASnack.class,
        SpecificActivity.HavingBreakfast.class,
        SpecificActivity.HavingCoffee.class,
        SpecificActivity.HavingDinner.class,
        SpecificActivity.HavingLunch.class,
        SpecificActivity.HavingTea.class,
        SpecificActivity.Hiding.class,
        SpecificActivity.Hiking.class,
        SpecificActivity.InACar.class,
        SpecificActivity.InAMeeting.class,
        SpecificActivity.InRealLife.class,
        SpecificActivity.Jogging.class,
        SpecificActivity.OnABus.class,
        SpecificActivity.OnAPlane.class,
        SpecificActivity.OnATrain.class,
        SpecificActivity.OnATrip.class,
        SpecificActivity.OnThePhone.class,
        SpecificActivity.OnVacation.class,
        SpecificActivity.OnVideoPhone.class,
        SpecificActivity.Other.class,
        SpecificActivity.Partying.class,
        SpecificActivity.PlayingSports.class,
        SpecificActivity.Praying.class,
        SpecificActivity.Reading.class,
        SpecificActivity.Rehearsing.class,
        SpecificActivity.Running.class,
        SpecificActivity.RunningAnErrand.class,
        SpecificActivity.ScheduledHoliday.class,
        SpecificActivity.Shaving.class,
        SpecificActivity.Shopping.class,
        SpecificActivity.Skiing.class,
        SpecificActivity.Sleeping.class,
        SpecificActivity.Smoking.class,
        SpecificActivity.Socializing.class,
        SpecificActivity.Studying.class,
        SpecificActivity.Sunbathing.class,
        SpecificActivity.Swimming.class,
        SpecificActivity.TakingABath.class,
        SpecificActivity.TakingAShower.class,
        SpecificActivity.Thinking.class,
        SpecificActivity.Walking.class,
        SpecificActivity.WalkingTheDog.class,
        SpecificActivity.WatchingAMovie.class,
        SpecificActivity.WatchingTv.class,
        SpecificActivity.WorkingOut.class,
        SpecificActivity.Writing.class})
public abstract class SpecificActivity {
    @XmlElementRef
    private final SpecificActivity specificActivity;

    public SpecificActivity() {
        this.specificActivity = null;
    }

    public SpecificActivity(SpecificActivity specificActivity) {
        this.specificActivity = specificActivity;
    }

    public SpecificActivity getSpecificActivity() {
        return specificActivity;
    }

    @Override
    public final String toString() {
        return Strings.toUnderscore(getClass().getSimpleName());
    }

    /**
     * The specific {@code <at_the_spa/>} activity.
     */
    @XmlRootElement(name = "at_the_spa")
    public static final class AtTheSpa extends SpecificActivity {
        public AtTheSpa() {
        }

        public AtTheSpa(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <brushing_teeth/>} activity.
     */
    @XmlRootElement(name = "brushing_teeth")
    public static final class BrushingTeeth extends SpecificActivity {
        public BrushingTeeth() {
        }

        public BrushingTeeth(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <buying_groceries/>} activity.
     */
    @XmlRootElement(name = "buying_groceries")
    public static final class BuyingGroceries extends SpecificActivity {
        public BuyingGroceries() {
        }

        public BuyingGroceries(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <cleaning/>} activity.
     */
    @XmlRootElement
    public static final class Cleaning extends SpecificActivity {
        public Cleaning() {
        }

        public Cleaning(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <coding/>} activity.
     */
    @XmlRootElement
    public static final class Coding extends SpecificActivity {
        public Coding() {
        }

        public Coding(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <commuting/>} activity.
     */
    @XmlRootElement
    public static final class Commuting extends SpecificActivity {
        public Commuting() {
        }

        public Commuting(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <cooking/>} activity.
     */
    @XmlRootElement
    public static final class Cooking extends SpecificActivity {
        public Cooking() {
        }

        public Cooking(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <cycling/>} activity.
     */
    @XmlRootElement
    public static final class Cycling extends SpecificActivity {
        public Cycling() {
        }

        public Cycling(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <dancing/>} activity.
     */
    @XmlRootElement
    public static final class Dancing extends SpecificActivity {
        public Dancing() {
        }

        public Dancing(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <day_off/>} activity.
     */
    @XmlRootElement(name = "day_off")
    public static final class DayOff extends SpecificActivity {
        public DayOff() {
        }

        public DayOff(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <doing_maintenance/>} activity.
     */
    @XmlRootElement(name = "doing_maintenance")
    public static final class DoingMaintenance extends SpecificActivity {
        public DoingMaintenance() {
        }

        public DoingMaintenance(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <doing_the_dishes/>} activity.
     */
    @XmlRootElement(name = "doing_the_dishes")
    public static final class DoingTheDishes extends SpecificActivity {
        public DoingTheDishes() {
        }

        public DoingTheDishes(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <doing_the_laundry/>} activity.
     */
    @XmlRootElement(name = "doing_the_laundry")
    public static final class DoingTheLaundry extends SpecificActivity {
        public DoingTheLaundry() {
        }

        public DoingTheLaundry(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <driving/>} activity.
     */
    @XmlRootElement
    public static final class Driving extends SpecificActivity {
        public Driving() {
        }

        public Driving(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <fishing/>} activity.
     */
    @XmlRootElement
    public static final class Fishing extends SpecificActivity {
        public Fishing() {
        }

        public Fishing(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <gaming/>} activity.
     */
    @XmlRootElement
    public static final class Gaming extends SpecificActivity {
        public Gaming() {
        }

        public Gaming(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <gardening/>} activity.
     */
    @XmlRootElement
    public static final class Gardening extends SpecificActivity {
        public Gardening() {
        }

        public Gardening(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <getting_a_haircut/>} activity.
     */
    @XmlRootElement(name = "getting_a_haircut")
    public static final class GettingAHaircut extends SpecificActivity {
        public GettingAHaircut() {
        }

        public GettingAHaircut(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <going_out/>} activity.
     */
    @XmlRootElement(name = "going_out")
    public static final class GoingOut extends SpecificActivity {
        public GoingOut() {
        }

        public GoingOut(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <hanging_out/>} activity.
     */
    @XmlRootElement(name = "hanging_out")
    public static final class HangingOut extends SpecificActivity {
        public HangingOut() {
        }

        public HangingOut(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <having_a_beer/>} activity.
     */

    @XmlRootElement(name = "having_a_beer")
    public static final class HavingABeer extends SpecificActivity {
        public HavingABeer() {
        }

        public HavingABeer(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <having_a_snack/>} activity.
     */
    @XmlRootElement(name = "having_a_snack")
    public static final class HavingASnack extends SpecificActivity {
        public HavingASnack() {
        }

        public HavingASnack(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <having_breakfast/>} activity.
     */
    @XmlRootElement(name = "having_breakfast")
    public static final class HavingBreakfast extends SpecificActivity {
        public HavingBreakfast() {
        }

        public HavingBreakfast(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <having_coffee/>} activity.
     */
    @XmlRootElement(name = "having_coffee")
    public static final class HavingCoffee extends SpecificActivity {
        public HavingCoffee() {
        }

        public HavingCoffee(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <having_dinner/>} activity.
     */
    @XmlRootElement(name = "having_dinner")
    public static final class HavingDinner extends SpecificActivity {
        public HavingDinner() {
        }

        public HavingDinner(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <having_lunch/>} activity.
     */

    @XmlRootElement(name = "having_lunch")
    public static final class HavingLunch extends SpecificActivity {
        public HavingLunch() {
        }

        public HavingLunch(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <having_tea/>} activity.
     */
    @XmlRootElement(name = "having_tea")
    public static final class HavingTea extends SpecificActivity {
        public HavingTea() {
        }

        public HavingTea(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <hiding/>} activity.
     */
    @XmlRootElement
    public static final class Hiding extends SpecificActivity {
        public Hiding() {
        }

        public Hiding(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <hiking/>} activity.
     */
    @XmlRootElement
    public static final class Hiking extends SpecificActivity {
        public Hiking() {
        }

        public Hiking(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <in_a_car/>} activity.
     */
    @XmlRootElement(name = "in_a_car")
    public static final class InACar extends SpecificActivity {
        public InACar() {
        }

        public InACar(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <in_a_meeting/>} activity.
     */
    @XmlRootElement(name = "in_a_meeting")
    public static final class InAMeeting extends SpecificActivity {
        public InAMeeting() {
        }

        public InAMeeting(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <in_real_life/>} activity.
     */
    @XmlRootElement(name = "in_real_life")
    public static final class InRealLife extends SpecificActivity {
        public InRealLife() {
        }

        public InRealLife(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <jogging/>} activity.
     */
    @XmlRootElement
    public static final class Jogging extends SpecificActivity {
        public Jogging() {
        }

        public Jogging(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <on_a_bus/>} activity.
     */
    @XmlRootElement(name = "on_a_bus")
    public static final class OnABus extends SpecificActivity {
        public OnABus() {
        }

        public OnABus(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <on_a_plane/>} activity.
     */
    @XmlRootElement(name = "on_a_plane")
    public static final class OnAPlane extends SpecificActivity {
        public OnAPlane() {
        }

        public OnAPlane(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <on_a_train/>} activity.
     */
    @XmlRootElement(name = "on_a_train")
    public static final class OnATrain extends SpecificActivity {
        public OnATrain() {
        }

        public OnATrain(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <on_a_trip/>} activity.
     */
    @XmlRootElement(name = "on_a_trip")
    public static final class OnATrip extends SpecificActivity {
        public OnATrip() {
        }

        public OnATrip(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <on_the_phone/>} activity.
     */
    @XmlRootElement(name = "on_the_phone")
    public static final class OnThePhone extends SpecificActivity {
        public OnThePhone() {
        }

        public OnThePhone(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <on_vacation/>} activity.
     */

    @XmlRootElement(name = "on_vacation")
    public static final class OnVacation extends SpecificActivity {
        public OnVacation() {
        }

        public OnVacation(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <on_video_phone/>} activity.
     */

    @XmlRootElement(name = "on_video_phone")
    public static final class OnVideoPhone extends SpecificActivity {
        public OnVideoPhone() {
        }

        public OnVideoPhone(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <other/>} activity.
     */
    @XmlRootElement(name = "other")
    public static final class Other extends SpecificActivity {
        public Other() {
        }

        public Other(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <partying/>} activity.
     */
    @XmlRootElement
    public static final class Partying extends SpecificActivity {
        public Partying() {
        }

        public Partying(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <playing_sports/>} activity.
     */
    @XmlRootElement(name = "playing_sports")
    public static final class PlayingSports extends SpecificActivity {
        public PlayingSports() {
        }

        public PlayingSports(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <praying/>} activity.
     */
    @XmlRootElement
    public static final class Praying extends SpecificActivity {
        public Praying() {
        }

        public Praying(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <reading/>} activity.
     */
    @XmlRootElement
    public static final class Reading extends SpecificActivity {
        public Reading() {
        }

        public Reading(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <rehearsing/>} activity.
     */
    @XmlRootElement
    public static final class Rehearsing extends SpecificActivity {
        public Rehearsing() {
        }

        public Rehearsing(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <running/>} activity.
     */
    @XmlRootElement
    public static final class Running extends SpecificActivity {
        public Running() {
        }

        public Running(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <running_an_errand/>} activity.
     */
    @XmlRootElement(name = "running_an_errand")
    public static final class RunningAnErrand extends SpecificActivity {
        public RunningAnErrand() {
        }

        public RunningAnErrand(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <scheduled_holiday/>} activity.
     */
    @XmlRootElement(name = "scheduled_holiday")
    public static final class ScheduledHoliday extends SpecificActivity {
        public ScheduledHoliday() {
        }

        public ScheduledHoliday(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <shaving/>} activity.
     */
    @XmlRootElement
    public static final class Shaving extends SpecificActivity {
        public Shaving() {
        }

        public Shaving(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <shopping/>} activity.
     */
    @XmlRootElement
    public static final class Shopping extends SpecificActivity {
        public Shopping() {
        }

        public Shopping(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <skiing/>} activity.
     */
    @XmlRootElement
    public static final class Skiing extends SpecificActivity {
        public Skiing() {
        }

        public Skiing(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <sleeping/>} activity.
     */
    @XmlRootElement(name = "sleeping")
    public static final class Sleeping extends SpecificActivity {
        public Sleeping() {
        }

        public Sleeping(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <smoking/>} activity.
     */
    @XmlRootElement
    public static final class Smoking extends SpecificActivity {
        public Smoking() {
        }

        public Smoking(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <socializing/>} activity.
     */
    @XmlRootElement
    public static final class Socializing extends SpecificActivity {
        public Socializing() {
        }

        public Socializing(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <studying/>} activity.
     */
    @XmlRootElement
    public static final class Studying extends SpecificActivity {
        public Studying() {
        }

        public Studying(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <sunbathing/>} activity.
     */
    @XmlRootElement
    public static final class Sunbathing extends SpecificActivity {
        public Sunbathing() {
        }

        public Sunbathing(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <swimming/>} activity.
     */
    @XmlRootElement
    public static final class Swimming extends SpecificActivity {
        public Swimming() {
        }

        public Swimming(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <taking_a_bath/>} activity.
     */
    @XmlRootElement(name = "taking_a_bath")
    public static final class TakingABath extends SpecificActivity {
        public TakingABath() {
        }

        public TakingABath(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <taking_a_shower/>} activity.
     */
    @XmlRootElement(name = "taking_a_shower")
    public static final class TakingAShower extends SpecificActivity {
        public TakingAShower() {
        }

        public TakingAShower(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <thinking/>} activity.
     */
    @XmlRootElement
    public static final class Thinking extends SpecificActivity {
        public Thinking() {
        }

        public Thinking(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <walking/>} activity.
     */
    @XmlRootElement
    public static final class Walking extends SpecificActivity {
        public Walking() {
        }

        public Walking(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <walking_the_dog/>} activity.
     */
    @XmlRootElement(name = "walking_the_dog")
    public static final class WalkingTheDog extends SpecificActivity {
        public WalkingTheDog() {
        }

        public WalkingTheDog(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <watching_a_movie/>} activity.
     */
    @XmlRootElement(name = "watching_a_movie")
    public static final class WatchingAMovie extends SpecificActivity {
        public WatchingAMovie() {
        }

        public WatchingAMovie(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <watching_tv/>} activity.
     */
    @XmlRootElement(name = "watching_tv")
    public static final class WatchingTv extends SpecificActivity {
        public WatchingTv() {
        }

        public WatchingTv(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <working_out/>} activity.
     */
    @XmlRootElement(name = "working_out")
    public static final class WorkingOut extends SpecificActivity {
        public WorkingOut() {
        }

        public WorkingOut(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }

    /**
     * The specific {@code <writing/>} activity.
     */
    @XmlRootElement
    public static final class Writing extends SpecificActivity {
        public Writing() {
        }

        public Writing(SpecificActivity specificActivity) {
            super(specificActivity);
        }
    }
}