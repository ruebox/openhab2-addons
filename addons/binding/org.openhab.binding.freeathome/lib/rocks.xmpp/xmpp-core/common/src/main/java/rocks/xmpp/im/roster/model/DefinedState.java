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

import rocks.xmpp.core.stanza.model.Presence;

/**
 * Represents the nine possible defined subscription states.
 * <p>
 * Each state can transition to a new state depending on an in- or outbound presence subscription change.
 *
 * @author Christian Schudt
 * @see rocks.xmpp.core.stanza.model.Presence.Type
 * @see <a href="https://xmpp.org/rfcs/rfc6121.html#substates-defined">RFC 6121, A.1.  Defined States</a>
 */
public enum DefinedState implements SubscriptionState {
    /**
     * Contact and user are not subscribed to each other, and neither has requested a subscription from the other;
     * this is reflected in the user's roster by subscription='none'.
     */
    NONE(Subscription.NONE, false, false),
    /**
     * Contact and user are not subscribed to each other, and user has sent contact a subscription request but contact has not replied yet;
     * this is reflected in the user's roster by subscription='none' and ask='subscribe'.
     */
    NONE_PENDING_OUT(Subscription.NONE, true, false),
    /**
     * Contact and user are not subscribed to each other, and contact has sent user a subscription request but user has not replied yet.
     * This state might or might not be reflected in the user's roster, as follows:
     * if the user has created a roster item for the contact then the server MUST maintain that roster item and also note the existence of the inbound presence subscription request,
     * whereas if the user has not created a roster item for the contact then the user's server MUST note the existence of the inbound presence subscription request but MUST NOT create a roster item for the contact
     * (instead, the server MUST wait until the user has approved the subscription request before adding the contact to the user's roster).
     */
    NONE_PENDING_IN(Subscription.NONE, false, true),
    /**
     * Contact and user are not subscribed to each other, contact has sent user a subscription request but user has not replied yet,
     * and user has sent contact a subscription request but contact has not replied yet;
     * this is reflected in the user's roster by subscription='none' and ask='subscribe'.
     */
    NONE_PENDING_OUT_IN(Subscription.NONE, true, true),
    /**
     * User is subscribed to contact (one-way);
     * this is reflected in the user's roster by subscription='to'.
     */
    TO(Subscription.TO, false, false),
    /**
     * User is subscribed to contact, and contact has sent user a subscription request but user has not replied yet;
     * this is reflected in the user's roster by subscription='to'.
     */
    TO_PENDING_IN(Subscription.TO, false, true),
    /**
     * Contact is subscribed to user (one-way);
     * this is reflected in the user's roster by subscription='from'.
     */
    FROM(Subscription.FROM, false, false),
    /**
     * Contact is subscribed to user, and user has sent contact a subscription request but contact has not replied yet;
     * this is reflected in the user's roster by subscription='from' and ask='subscribe'.
     */
    FROM_PENDING_OUT(Subscription.FROM, true, false),
    /**
     * User and contact are subscribed to each other (two-way);
     * this is reflected in the user's roster by subscription='both'.
     */
    BOTH(Subscription.BOTH, false, false);

    private final Subscription subscription;

    private final boolean pendingOut;

    private final boolean pendingIn;

    DefinedState(Subscription subscription, boolean pendingOut, boolean pendingIn) {
        this.subscription = subscription;
        this.pendingOut = pendingOut;
        this.pendingIn = pendingIn;
    }

    /**
     * Returns a defined subscription state from an existing subscription state, e.g. a {@link RosterItem}.
     *
     * @param subscriptionState The existing subscription state.
     * @return The defined subscription state. Never <code>null</code>.
     */
    public static DefinedState valueOf(final SubscriptionState subscriptionState) {
        if (subscriptionState == null || subscriptionState.getSubscription() == null) {
            // a subscription state of "None" includes the case of the contact not being in the user's roster at all,
            // i.e., an unknown entity from the perspective of the user's roster
            return NONE;
        }
        switch (subscriptionState.getSubscription()) {
            case NONE:
                if (subscriptionState.isPendingOut()) {
                    if (subscriptionState.isPendingIn()) {
                        return NONE_PENDING_OUT_IN;
                    }
                    return NONE_PENDING_OUT;
                } else if (subscriptionState.isPendingIn()) {
                    return NONE_PENDING_IN;
                }
                return NONE;
            case TO:
                if (subscriptionState.isPendingIn()) {
                    return TO_PENDING_IN;
                }
                return TO;
            case FROM:
                if (subscriptionState.isPendingOut()) {
                    return FROM_PENDING_OUT;
                }
                return FROM;
            case BOTH:
                return BOTH;
            default:
                return NONE;
        }
    }

    @Override
    public final Subscription getSubscription() {
        return subscription;
    }

    @Override
    public final boolean isPendingOut() {
        return pendingOut;
    }

    @Override
    public final boolean isPendingIn() {
        return pendingIn;
    }

    /**
     * Transforms an existing subscription state into a new state, depending on an outbound presence subscription change.
     *
     * @param type The presence subscription type.
     * @return The new subscription state.
     * @see <a href="https://xmpp.org/rfcs/rfc6121.html#substates-out">A.2.  Server Processing of Outbound Presence Subscription Stanzas</a>
     */
    public final DefinedState onOutboundSubscriptionChange(final Presence.Type type) {
        switch (type) {
            case SUBSCRIBE:
                switch (this) {
                    case NONE:
                        return NONE_PENDING_OUT;
                    case NONE_PENDING_IN:
                        return NONE_PENDING_OUT_IN;
                    case FROM:
                        return FROM_PENDING_OUT;
                    default:
                        // no state change
                        return this;
                }
            case UNSUBSCRIBE:
                switch (this) {
                    case NONE_PENDING_OUT:
                    case TO:
                        return NONE;
                    case NONE_PENDING_OUT_IN:
                    case TO_PENDING_IN:
                        return NONE_PENDING_IN;
                    case FROM_PENDING_OUT:
                    case BOTH:
                        return FROM;
                    default:
                        // no state change
                        return this;
                }
            case SUBSCRIBED:
                switch (this) {
                    case NONE_PENDING_IN:
                        return FROM;
                    case NONE_PENDING_OUT_IN:
                        return FROM_PENDING_OUT;
                    case TO_PENDING_IN:
                        return BOTH;
                    default:
                        // no state change
                        return this;
                }
            case UNSUBSCRIBED:
                switch (this) {
                    case NONE_PENDING_IN:
                    case FROM:
                        return NONE;
                    case NONE_PENDING_OUT_IN:
                    case FROM_PENDING_OUT:
                        return NONE_PENDING_OUT;
                    case TO_PENDING_IN:
                    case BOTH:
                        return TO;
                    default:
                        // no state change
                        return this;
                }
            default:
                throw new IllegalArgumentException("Only subscription related presence types allowed.");
        }
    }

    /**
     * Transforms an existing subscription state into a new state, depending on an inbound presence subscription change.
     *
     * @param type The presence subscription type.
     * @return The new subscription state.
     * @see <a href="https://xmpp.org/rfcs/rfc6121.html#substates-in">A.3.  Server Processing of Inbound Presence Subscription Stanzas</a>
     */
    public final DefinedState onInboundSubscriptionChange(final Presence.Type type) {
        switch (type) {
            case SUBSCRIBE:
                switch (this) {
                    case NONE:
                        return NONE_PENDING_IN;
                    case NONE_PENDING_OUT:
                        return NONE_PENDING_OUT_IN;
                    case TO:
                        return TO_PENDING_IN;
                    default:
                        // no state change
                        return this;
                }
            case UNSUBSCRIBE:
                switch (this) {
                    case NONE_PENDING_IN:
                    case FROM:
                        return NONE;
                    case NONE_PENDING_OUT_IN:
                    case FROM_PENDING_OUT:
                        return NONE_PENDING_OUT;
                    case TO_PENDING_IN:
                    case BOTH:
                        return TO;
                    default:
                        // no state change
                        return this;
                }
            case SUBSCRIBED:
                switch (this) {
                    case NONE_PENDING_OUT:
                        return TO;
                    case NONE_PENDING_OUT_IN:
                        return TO_PENDING_IN;
                    case FROM_PENDING_OUT:
                        return BOTH;
                    default:
                        // no state change
                        return this;
                }
            case UNSUBSCRIBED:
                switch (this) {
                    case NONE_PENDING_OUT:
                    case TO:
                        return NONE;
                    case NONE_PENDING_OUT_IN:
                    case TO_PENDING_IN:
                        return NONE_PENDING_IN;
                    case FROM_PENDING_OUT:
                    case BOTH:
                        return FROM;
                    default:
                        // no state change
                        return this;
                }
            default:
                throw new IllegalArgumentException("Only subscription related presence types allowed.");
        }
    }
}
