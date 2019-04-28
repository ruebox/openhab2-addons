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

package rocks.xmpp.extensions.privacy.model;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.im.roster.model.Contact;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnumValue;
import java.util.Objects;

/**
 * A privacy rule for privacy lists, which is applied by the server.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/extensions/xep-0016.html#protocol-rules">2.2 Business Rules</a></cite></p>
 * <ul>
 * <li>The order in which privacy list items are processed by the server is important. List items MUST be processed in ascending order determined by the integer values of the 'order' attribute for each {@code <item/>}.</li>
 * <li>As soon as a stanza is matched against a privacy list rule, the server MUST appropriately handle the stanza in accordance with the rule and cease processing.</li>
 * <li>If no fall-through item is provided in a list, the fall-through action is assumed to be "allow".</li>
 * </ul>
 * </blockquote>
 * <h3>Usage</h3>
 * In order to create a privacy rule, use one of the many static factory methods, e.g.:
 * ```java
 * // Blocks all messages from juliet@example.net
 * PrivacyRule rule1 = PrivacyRule.blockMessagesFrom(Jid.of("juliet@example.net"), 1);
 *
 * // Blocks outbound presence notifications to the roster group "Bad Friends".
 * PrivacyRule rule2 = PrivacyRule.blockPresenceToRosterGroup("Bad Friends", 2);
 * ```
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 */
public final class PrivacyRule implements Comparable<PrivacyRule> {

    @XmlAttribute
    private final Type type;

    @XmlAttribute
    private final String value;

    @XmlAttribute
    private final Action action;

    @XmlAttribute
    private final long order;

    private final String message;

    @XmlElement(name = "presence-in")
    private final String presenceIn;

    @XmlElement(name = "presence-out")
    private final String presenceOut;

    private final String iq;

    private PrivacyRule() {
        this.action = null;
        this.order = 0;
        this.type = null;
        this.value = null;
        this.message = null;
        this.presenceIn = null;
        this.presenceOut = null;
        this.iq = null;
    }

    /**
     * Creates a privacy list item.
     *
     * @param action The action to perform, i.e. either allow or deny.
     * @param order  The order in which the privacy item is processed by the server. A non-negative integer that is unique among all items in the list.
     * @param type   The type of the privacy item.
     * @param value  <ul>
     *               <li>If the type is {@link Type#JID}, then the value must be a valid JID.</li>
     *               <li>If the type is {@link Type#GROUP}, then the value should be the name of a group in the user's roster.</li>
     *               <li>If the type is {@link Type#SUBSCRIPTION}, then the value must be one of "both", "to", "from", or "none".</li>
     *               </ul>
     */
    private PrivacyRule(Action action, long order, Type type, String value, boolean filterMessage, boolean filterPresenceIn, boolean filterPresenceOut, boolean filterIQ) {
        if (order < 0) {
            throw new IllegalArgumentException("order must be greater 0.");
        }
        this.action = Objects.requireNonNull(action);
        this.order = order;
        this.type = type;
        this.value = value;
        this.message = filterMessage ? "" : null;
        this.presenceIn = filterPresenceIn ? "" : null;
        this.presenceOut = filterPresenceOut ? "" : null;
        this.iq = filterIQ ? "" : null;
    }

    /**
     * Creates a privacy list item, which allows or blocks everything.
     *
     * @param action The action to perform, i.e. either allow or deny.
     * @param order  The order in which the privacy item is processed by the server. A non-negative integer that is unique among all items in the list.
     * @return The privacy rule.
     */
    public static PrivacyRule of(Action action, long order) {
        return new PrivacyRule(action, order, null, null, false, false, false, false);
    }

    /**
     * Creates a privacy rule of type 'jid'.
     *
     * @param jid    The JID.
     * @param action The action to perform, i.e. either allow or deny.
     * @param order  The order in which the privacy item is processed by the server. A non-negative integer that is unique among all items in the list.
     * @return The privacy rule.
     */
    public static PrivacyRule of(Jid jid, Action action, long order) {
        return new PrivacyRule(action, order, Type.JID, jid.toEscapedString(), false, false, false, false);
    }

    /**
     * Creates a privacy rule of type 'group'.
     *
     * @param group  The roster group.
     * @param action The action to perform, i.e. either allow or deny.
     * @param order  The order in which the privacy item is processed by the server. A non-negative integer that is unique among all items in the list.
     * @return The privacy rule.
     */
    public static PrivacyRule of(String group, Action action, long order) {
        return new PrivacyRule(action, order, Type.GROUP, group, false, false, false, false);
    }

    /**
     * Creates a privacy rule of type 'subscription'.
     *
     * @param subscription The subscription type.
     * @param action       The action to perform, i.e. either allow or deny.
     * @param order        The order in which the privacy item is processed by the server. A non-negative integer that is unique among all items in the list.
     * @return The privacy rule.
     */
    public static PrivacyRule of(Contact.Subscription subscription, Action action, long order) {
        if (Contact.Subscription.REMOVE.equals(subscription)) {
            throw new IllegalArgumentException("subscription must not be 'remove'");
        }
        return new PrivacyRule(action, order, Type.SUBSCRIPTION, subscription.name().toLowerCase(), false, false, false, false);
    }

    /**
     * Creates a rule, which blocks all inbound messages from another entity.
     * <p>
     * The user will not receive messages from the entity with the specified JID.
     *
     * @param entity The entity.
     * @param order  The order, this rule will be applied within the privacy list.
     * @return The privacy rule.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-message">2.9 Blocking Messages</a>
     */
    public static PrivacyRule blockMessagesFrom(Jid entity, long order) {
        return new PrivacyRule(Action.DENY, order, Type.JID, entity.toEscapedString(), true, false, false, false);
    }

    /**
     * Creates a rule, which blocks all inbound messages from contacts, which are in the specified roster group.
     * <p>
     * The user will not receive messages from any entities in the specified roster group.
     *
     * @param rosterGroup The roster group.
     * @param order       The order, this rule will be applied within the privacy list.
     * @return The privacy rule.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-message">2.9 Blocking Messages</a>
     */
    public static PrivacyRule blockMessagesFromRosterGroup(String rosterGroup, long order) {
        return new PrivacyRule(Action.DENY, order, Type.GROUP, Objects.requireNonNull(rosterGroup), true, false, false, false);
    }

    /**
     * Creates a rule, which blocks all inbound messages from entities with the given subscription type.
     * <p>
     * The user will not receive messages from any entities with the specified subscription type.
     *
     * @param subscription The subscription type.
     * @param order        The order, this rule will be applied within the privacy list.
     * @return The privacy rule.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-message">2.9 Blocking Messages</a>
     */
    public static PrivacyRule blockMessagesFromEntitiesWithSubscription(Contact.Subscription subscription, long order) {
        return new PrivacyRule(Action.DENY, order, Type.SUBSCRIPTION, checkSubscriptionType(subscription), true, false, false, false);
    }

    /**
     * Creates a rule, which blocks all inbound presence notifications from another entity.
     * <p>
     * The user will not receive presence notifications from the entity with the specified JID.
     *
     * @param entity The entity.
     * @param order  The order, this rule will be applied within the privacy list.
     * @return The privacy rule.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-presencein">2.10 Blocking Inbound Presence Notifications</a>
     */
    public static PrivacyRule blockPresenceFrom(Jid entity, long order) {
        return new PrivacyRule(Action.DENY, order, Type.JID, entity.toEscapedString(), false, true, false, false);
    }

    /**
     * Creates a rule, which blocks all inbound presence notifications from contacts, which are in the specified roster group.
     * <p>
     * The user will not receive presence notifications from any entities in the specified roster group.
     *
     * @param rosterGroup The roster group.
     * @param order       The order, this rule will be applied within the privacy list.
     * @return The privacy rule.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-presencein">2.10 Blocking Inbound Presence Notifications</a>
     */
    public static PrivacyRule blockPresenceFromRosterGroup(String rosterGroup, long order) {
        return new PrivacyRule(Action.DENY, order, Type.GROUP, Objects.requireNonNull(rosterGroup), false, true, false, false);
    }

    /**
     * Creates a rule, which blocks all inbound presence notifications from entities with the given subscription type.
     * <p>
     * The user will not receive presence notifications from any entities with the specified subscription type.
     *
     * @param subscription The subscription type.
     * @param order        The order, this rule will be applied within the privacy list.
     * @return The privacy rule.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-presencein">2.10 Blocking Inbound Presence Notifications</a>
     */
    public static PrivacyRule blockPresenceFromEntitiesWithSubscription(Contact.Subscription subscription, long order) {
        return new PrivacyRule(Action.DENY, order, Type.SUBSCRIPTION, checkSubscriptionType(subscription), false, true, false, false);
    }

    /**
     * Creates a rule, which blocks all inbound presence notifications.
     * <p>
     * The user will not receive presence notifications from any other users.
     *
     * @param order The order, this rule will be applied within the privacy list.
     * @return The privacy rule.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-presenceout">2.11 Blocking Outbound Presence Notifications</a>
     */
    public static PrivacyRule blockInboundPresence(long order) {
        return new PrivacyRule(Action.DENY, order, null, null, false, true, false, false);
    }

    /**
     * Creates a rule, which blocks all outbound presence notifications to another entity.
     * <p>
     * The user will not send presence notifications to the entity with the specified JID.
     *
     * @param entity The entity.
     * @param order  The order, this rule will be applied within the privacy list.
     * @return The privacy rule.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-presenceout">2.11 Blocking Outbound Presence Notifications</a>
     */
    public static PrivacyRule blockPresenceTo(Jid entity, long order) {
        return new PrivacyRule(Action.DENY, order, Type.JID, entity.toEscapedString(), false, false, true, false);
    }

    /**
     * Creates a rule, which blocks all outbound presence notifications to contacts, which are in the specified roster group.
     * <p>
     * The user will not send presence notifications to any entities in the specified roster group.
     *
     * @param rosterGroup The roster group.
     * @param order       The order, this rule will be applied within the privacy list.
     * @return The privacy rule.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-presenceout">2.11 Blocking Outbound Presence Notifications</a>
     */
    public static PrivacyRule blockPresenceToRosterGroup(String rosterGroup, long order) {
        return new PrivacyRule(Action.DENY, order, Type.GROUP, Objects.requireNonNull(rosterGroup), false, false, true, false);
    }

    /**
     * Creates a rule, which blocks all outbound presence notifications to entities with the given subscription type.
     * <p>
     * The user will not send presence notifications to any entities with the specified subscription type.
     *
     * @param subscription The subscription type.
     * @param order        The order, this rule will be applied within the privacy list.
     * @return The privacy rule.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-presenceout">2.11 Blocking Outbound Presence Notifications</a>
     */
    public static PrivacyRule blockPresenceToEntitiesWithSubscription(Contact.Subscription subscription, long order) {
        return new PrivacyRule(Action.DENY, order, Type.SUBSCRIPTION, checkSubscriptionType(subscription), false, false, true, false);
    }

    /**
     * Creates a rule, which blocks all outbound presence notifications.
     * <p>
     * The user will not send presence notifications to any other users.
     *
     * @param order The order, this rule will be applied within the privacy list.
     * @return The privacy rule.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-presenceout">2.11 Blocking Outbound Presence Notifications</a>
     */
    public static PrivacyRule blockOutboundPresence(long order) {
        return new PrivacyRule(Action.DENY, order, null, null, false, false, true, false);
    }

    /**
     * Creates a rule, which blocks all inbound IQ stanzas from another entity.
     * <p>
     * The user will not receive IQ stanzas from the entity with the specified JID.
     *
     * @param entity The entity.
     * @param order  The order, this rule will be applied within the privacy list.
     * @return The privacy rule.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-iq">2.12 Blocking IQ Stanzas</a>
     */
    public static PrivacyRule blockIQFrom(Jid entity, long order) {
        return new PrivacyRule(Action.DENY, order, Type.JID, entity.toEscapedString(), false, false, false, true);
    }

    /**
     * Creates a rule, which blocks all inbound IQ stanzas from contacts, which are in the specified roster group.
     * <p>
     * The user will not receive IQ stanzas from any entities in the specified roster group.
     *
     * @param rosterGroup The roster group.
     * @param order       The order, this rule will be applied within the privacy list.
     * @return The privacy rule.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-iq">2.12 Blocking IQ Stanzas</a>
     */
    public static PrivacyRule blockIQFromRosterGroup(String rosterGroup, long order) {
        return new PrivacyRule(Action.DENY, order, Type.GROUP, Objects.requireNonNull(rosterGroup), false, false, false, true);
    }

    /**
     * Creates a rule, which blocks all inbound IQ stanzas from entities with the given subscription type.
     * <p>
     * The user will not receive IQ stanzas from any entities with the specified subscription type.
     *
     * @param subscription The subscription type.
     * @param order        The order, this rule will be applied within the privacy list.
     * @return The privacy rule.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-iq">2.12 Blocking IQ Stanzas</a>
     */
    public static PrivacyRule blockIQFromEntitiesWithSubscription(Contact.Subscription subscription, long order) {
        return new PrivacyRule(Action.DENY, order, Type.SUBSCRIPTION, checkSubscriptionType(subscription), false, false, false, true);
    }

    /**
     * Creates a rule, which blocks all communication from and to any entities in the specified roster group.
     * <p>
     * The user will not receive any communications from, nor send any stanzas to, any entities in the specified roster group.
     *
     * @param rosterGroup The roster group.
     * @param order       The order, this rule will be applied within the privacy list.
     * @return The privacy rule.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-all">2.13 Blocking All Communication</a>
     */
    public static PrivacyRule blockAllCommunicationWithRosterGroup(String rosterGroup, long order) {
        return new PrivacyRule(Action.DENY, order, Type.GROUP, Objects.requireNonNull(rosterGroup), false, false, false, false);
    }

    /**
     * Creates a rule, which blocks all communication from and to any entities in the specified roster group.
     * <p>
     * The user will not receive any communications from, nor send any stanzas to, any entities in the specified roster group.
     *
     * @param subscription The subscription type.
     * @param order        The order, this rule will be applied within the privacy list.
     * @return The privacy rule.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-all">2.13 Blocking All Communication</a>
     */
    public static PrivacyRule blockAllCommunicationWithEntitiesWithSubscription(Contact.Subscription subscription, long order) {
        return new PrivacyRule(Action.DENY, order, Type.SUBSCRIPTION, checkSubscriptionType(subscription), false, false, false, false);
    }

    /**
     * Creates a rule, which blocks all communication from and to another entity.
     * <p>
     * The user will not receive any communications from, nor send any stanzas to, the entity with the specified JID.
     *
     * @param entity The entity.
     * @param order  The order, this rule will be applied within the privacy list.
     * @return The privacy rule.
     * @see <a href="https://xmpp.org/extensions/xep-0016.html#protocol-all">2.13 Blocking All Communication</a>
     */
    public static PrivacyRule blockAllCommunicationWith(Jid entity, long order) {
        return new PrivacyRule(Action.DENY, order, Type.JID, entity.toEscapedString(), false, false, false, false);
    }

    private static String checkSubscriptionType(Contact.Subscription subscription) {
        if (Contact.Subscription.REMOVE.equals(subscription)) {
            throw new IllegalArgumentException("subscription must not be 'remove'");
        }
        return subscription.name().toLowerCase();
    }

    /**
     * Gets the type.
     *
     * @return The type.
     */
    public final Type getType() {
        return type;
    }

    /**
     * Sets the action to perform (either allow or deny).
     *
     * @return The action.
     */
    public final Action getAction() {
        return action;
    }

    /**
     * Gets the value.
     *
     * @return The value.
     */
    public final String getValue() {
        return value;
    }

    /**
     * Gets the order in which privacy items are processed by the server.
     *
     * @return The order.
     */
    public final long getOrder() {
        return order;
    }

    /**
     * Indicates, whether this rule is applied to inbound messages.
     *
     * @return True, if this rule is applied to inbound messages.
     */
    public final boolean isAppliedToMessages() {
        return message != null || isAppliedToEverything();
    }

    /**
     * Creates a new privacy rule, which is applied to inbound messages.
     *
     * @return The privacy rule.
     * @see #isAppliedToMessages()
     */
    public final PrivacyRule appliedToMessages() {
        return new PrivacyRule(action, order, type, value, true, presenceIn != null, presenceOut != null, iq != null);
    }

    /**
     * Indicates, whether this rule is applied to inbound IQ stanzas.
     *
     * @return True, if this rule is applied to inbound IQ stanzas.
     */
    public final boolean isAppliedToIQs() {
        return iq != null || isAppliedToEverything();
    }

    /**
     * Creates a new privacy rule, which is applied to IQ stanzas.
     *
     * @return The privacy rule.
     * @see #isAppliedToIQs()
     */
    public final PrivacyRule appliedToIQs() {
        return new PrivacyRule(action, order, type, value, message != null, presenceIn != null, presenceOut != null, true);
    }

    /**
     * Indicates, whether this rule is applied to inbound presence notifications.
     *
     * @return True, if this rule is applied to inbound presence notifications.
     */
    public final boolean isAppliedToInboundPresence() {
        return presenceIn != null || isAppliedToEverything();
    }

    /**
     * Creates a new privacy rule, which is applied to inbound presence notifications.
     *
     * @return The privacy rule.
     * @see #isAppliedToInboundPresence()
     */
    public final PrivacyRule appliedToInboundPresence() {
        return new PrivacyRule(action, order, type, value, message != null, true, presenceOut != null, iq != null);
    }

    /**
     * Indicates, whether this rule is applied to outbound presence notifications.
     *
     * @return True, if this rule is applied to outbound presence notifications.
     */
    public final boolean isAppliedToOutboundPresence() {
        return presenceOut != null || isAppliedToEverything();
    }

    /**
     * Creates a new privacy rule, which is applied to outbound presence notifications.
     *
     * @return The privacy rule.
     * @see #isAppliedToOutboundPresence()
     */
    public final PrivacyRule appliedToOutboundPresence() {
        return new PrivacyRule(action, order, type, value, message != null, presenceIn != null, true, iq != null);
    }

    private boolean isAppliedToEverything() {
        return presenceIn == null && presenceOut == null && message == null && iq == null;
    }

    /**
     * Compares this rule with another rule by comparing their order.
     *
     * @param o The other rule.
     * @return The comparison result.
     */
    @Override
    public final int compareTo(PrivacyRule o) {
        if (this == o) {
            return 0;
        }
        return o != null ? Long.compare(order, o.order) : -1;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(action.name().toLowerCase()).append(", ").append(order);

        if (type != null) {
            sb.append(", ").append(type.name().toLowerCase());
        }
        if (value != null) {
            sb.append(", ").append(value);
        }

        return sb.toString();
    }

    /**
     * Defines the type of communication which should be allowed of denied.
     */
    public enum Type {
        /**
         * Allows or blocks communication based on a user's roster group name.
         */
        @XmlEnumValue("group")
        GROUP,
        /**
         * Allows or blocks communication based on a JID.
         */
        @XmlEnumValue("jid")
        JID,
        /**
         * Allows or blocks communication based on subscription.
         */
        @XmlEnumValue("subscription")
        SUBSCRIPTION
    }

    /**
     * Defines the action to perform with the privacy item: either allow or deny communication.
     */
    public enum Action {
        /**
         * Allows communication.
         */
        @XmlEnumValue("allow")
        ALLOW,
        /**
         * Denies (blocks) communication.
         */
        @XmlEnumValue("deny")
        DENY
    }
}
