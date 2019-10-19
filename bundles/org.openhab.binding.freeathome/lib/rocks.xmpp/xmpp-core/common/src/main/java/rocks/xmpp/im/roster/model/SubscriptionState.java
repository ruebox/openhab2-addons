/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Christian Schudt
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

package rocks.xmpp.im.roster.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import java.util.Objects;

/**
 * Represents a presence subscription state.
 * <p>
 * There are four primary subscription states (these states are described from the perspective of the user, not the contact):
 * <ul>
 * <li>None:
 * The user does not have a subscription to the contact's presence, and the contact does not have a subscription to the user's presence.
 * </li>
 * <li>To:
 * The user has a subscription to the contact's presence, but the contact does not have a subscription to the user's presence.
 * </li>
 * <li>From:
 * The contact has a subscription to the user's presence, but the user does not have a subscription to the contact's presence.
 * </li>
 * <li>Both:
 * Both the user and the contact have subscriptions to each other's presence (i.e., the union of 'from' and 'to').
 * </li>
 * </ul>
 * <p>
 * The foregoing states are supplemented by various sub-states related to pending inbound and outbound subscriptions.
 *
 * @author Christian Schudt
 * @see DefinedState
 * @see <a href="https://xmpp.org/rfcs/rfc6121.html#substates">RFC 6121, Appendix A.  Subscription States</a>
 */
public interface SubscriptionState {

    /**
     * Compares two subscription states.
     *
     * @param state1 The first state.
     * @param state2 The second state.
     * @return True, if both subscription states are equal.
     */
    static boolean equals(SubscriptionState state1, SubscriptionState state2) {
        return Objects.equals(state1.getSubscription(), state2.getSubscription())
                && state1.isPendingIn() == state2.isPendingIn()
                && state1.isPendingOut() == state2.isPendingOut();
    }

    /**
     * Gets the primary subscription state.
     * It may be one of {@link Subscription#NONE}, {@link Subscription#TO}, {@link Subscription#FROM} or {@link Subscription#BOTH}.
     *
     * @return The primary subscription state.
     */
    Subscription getSubscription();

    /**
     * If the presence subscription is pending out from a user's perspective.
     * The user has sent contact a subscription request but contact has not replied yet.
     *
     * @return If the state is pending out.
     */
    boolean isPendingOut();

    /**
     * If the presence subscription is pending in from a user's perspective.
     * The contact has sent user a subscription request but user has not replied yet.
     *
     * @return If the state is pending in.
     */
    boolean isPendingIn();

    @XmlEnum
    enum Subscription {
        /**
         * The user and the contact have subscriptions to each other's presence (also called a "mutual subscription").
         */
        @XmlEnumValue("both")
        BOTH,
        /**
         * The contact has a subscription to the user's presence, but the user does not have a subscription to the contact's presence.
         */
        @XmlEnumValue("from")
        FROM,
        /**
         * The user has a subscription to the contact's presence, but the contact does not have a subscription to the user's presence.
         */
        @XmlEnumValue("to")
        TO,
        /**
         * The user does not have a subscription to the contact's presence, and the contact does not have a subscription to the user's presence; this is the default value, so if the subscription attribute is not included then the state is to be understood as "none".
         */
        @XmlEnumValue("none")
        NONE,
        /**
         * At any time, a client can delete an item from his or her roster by sending a roster set and specifying a value of "remove" for the 'subscription' attribute.
         */
        @XmlEnumValue("remove")
        REMOVE
    }
}
