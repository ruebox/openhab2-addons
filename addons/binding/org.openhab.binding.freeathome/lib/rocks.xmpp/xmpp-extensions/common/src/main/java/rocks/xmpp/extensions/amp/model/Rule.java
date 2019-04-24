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

package rocks.xmpp.extensions.amp.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnumValue;
import java.time.Instant;
import java.util.Objects;

/**
 * The implementation of the {@code <rule/>} element, used both in the {@code http://jabber.org/protocol/amp} namespace as well as in the {@code http://jabber.org/protocol/amp#errors} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0079.html">XEP-0079: Advanced Message Processing</a>
 * @see <a href="https://xmpp.org/extensions/xep-0079.html#schemas-amp">XML Schema</a>
 */
public final class Rule {
    @XmlAttribute
    private final Action action;

    @XmlAttribute
    private final Condition condition;

    @XmlAttribute
    private final String value;

    private Rule() {
        this.action = null;
        this.condition = null;
        this.value = null;
    }

    /**
     * Creates a rule.
     *
     * @param action    The action.
     * @param condition The condition.
     * @param value     The value. This depends on the condition.
     */
    private Rule(Action action, Condition condition, String value) {
        this.action = Objects.requireNonNull(action);
        this.condition = Objects.requireNonNull(condition);
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Creates a rule.
     *
     * @param action    The action.
     * @param condition The condition.
     * @param value     The value. This depends on the condition.
     * @return The rule.
     */
    public static Rule of(Action action, Condition condition, String value) {
        return new Rule(action, condition, value);
    }

    /**
     * Creates the defined "expire-at" rule.
     *
     * @param action  The action.
     * @param instant The expiration date.
     * @return The rule.
     */
    public static Rule expireAt(Action action, Instant instant) {
        return new Rule(action, Condition.EXPIRE_AT, instant.toString());
    }

    /**
     * Creates the defined "deliver" rule.
     *
     * @param action The action.
     * @param value  The value.
     * @return The rule.
     */
    public static Rule deliver(Action action, DeliveryMode value) {
        return new Rule(action, Condition.DELIVER, value.name().toLowerCase());
    }

    /**
     * Creates the defined "match-resource" rule.
     *
     * @param action The action.
     * @param value  The value.
     * @return The rule.
     */
    public static Rule matchResource(Action action, MatchResource value) {
        return new Rule(action, Condition.MATCH_RESOURCE, value.name().toLowerCase());
    }

    /**
     * The 'action' attribute defines the result for this rule.
     *
     * @return The action.
     */
    public final Action getAction() {
        return action;
    }

    /**
     * The 'condition' attribute defines the overall condition this rule applies to.
     *
     * @return The condition.
     */
    public final Condition getCondition() {
        return condition;
    }

    /**
     * The 'value' attribute defines how the condition is matched.
     *
     * @return The value.
     */
    public final String getValue() {
        return value;
    }

    /**
     * The action defines what occurs when a particular rule is triggered. The value of the action attribute determines the behavior if the rule's condition is met.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0079.html#actions-def">3.4 Defined Actions</a>
     */
    public enum Action {
        /**
         * The "alert" action triggers a reply {@code <message/>} stanza to the sending entity.
         *
         * @see <a href="https://xmpp.org/extensions/xep-0079.html#actions-def-alert">3.4.1 alert</a>
         */
        @XmlEnumValue("alert")
        ALERT,
        /**
         * The "drop" action silently discards the message from any further delivery attempts and ensures that it is not placed into offline storage.
         *
         * @see <a href="https://xmpp.org/extensions/xep-0079.html#actions-def-drop">3.4.2 drop</a>
         */
        @XmlEnumValue("drop")
        DROP,
        /**
         * The "error" action triggers a reply {@code <message/>} stanza of type "error" to the sending entity.
         *
         * @see <a href="https://xmpp.org/extensions/xep-0079.html#actions-def-error">3.4.3 error</a>
         */
        @XmlEnumValue("error")
        ERROR,
        /**
         * The "notify" action triggers a reply {@code <message/>} stanza to the sending entity.
         *
         * @see <a href="https://xmpp.org/extensions/xep-0079.html#actions-def-notify">3.4.4 notify</a>
         */
        @XmlEnumValue("notify")
        NOTIFY
    }

    /**
     * The condition defines how or when a particular rule is triggered. The value of the condition attribute determines what the contents of the {@code <rule/>} mean.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0079.html#conditions-def">3.3 Defined Conditions</a>
     */
    public enum Condition {
        /**
         * The "deliver" condition is used to ensure delivery (or non-delivery) in one of five ways.
         *
         * @see <a href="https://xmpp.org/extensions/xep-0079.html#conditions-def-deliver">3.3.1 deliver</a>
         */
        @XmlEnumValue("deliver")
        DELIVER,
        /**
         * The "expire-at" condition is used to ensure delivery before an absolute point in time.
         *
         * @see <a href="https://xmpp.org/extensions/xep-0079.html#conditions-def-expireat">3.3.2 expire-at</a>
         */
        @XmlEnumValue("expire-at")
        EXPIRE_AT,
        /**
         * The "match-resource" condition is used to restrict delivery based on the resource identifier of the recipient JID.
         *
         * @see <a href="https://xmpp.org/extensions/xep-0079.html#conditions-def-match">3.3.3 match-resource</a>
         */
        @XmlEnumValue("match-resource")
        MATCH_RESOURCE,
    }

    /**
     * The possible values for the {@link Rule.Condition#DELIVER} condition.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0079.html#conditions-def-deliver">3.3.1 deliver</a>
     */
    public enum DeliveryMode {
        /**
         * The message would be immediately delivered to the intended recipient or routed to the next hop.
         */
        DIRECT,
        /**
         * The message would be forwarded to another XMPP address or account.
         */
        FORWARD,
        /**
         * The message would be sent through a gateway to an address or account on a non-XMPP system.
         */
        GATEWAY,
        /**
         * The message would not be delivered at all (e.g., because the intended recipient is offline and message storage is not enabled).
         */
        NONE,
        /**
         * The message would be stored offline for later delivery to the intended recipient.
         */
        STORED
    }

    /**
     * The possible values for the {@link Rule.Condition#MATCH_RESOURCE} condition.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0079.html#conditions-def-match">3.3.3 match-resource</a>
     */
    public enum MatchResource {
        /**
         * Destination resource matches any value, effectively ignoring the intended resource.
         */
        ANY,
        /**
         * Destination resource exactly matches the intended resource.
         */
        EXACT,
        /**
         * Destination resource matches any value except for the intended resource.
         */
        OTHER
    }
}
