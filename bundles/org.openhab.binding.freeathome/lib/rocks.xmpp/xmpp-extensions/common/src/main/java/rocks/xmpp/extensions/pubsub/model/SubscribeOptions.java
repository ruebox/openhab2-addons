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

package rocks.xmpp.extensions.pubsub.model;

import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.extensions.data.model.DataForm;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a standardized {@link rocks.xmpp.extensions.data.model.DataForm} with form type {@code http://jabber.org/protocol/pubsub#subscribe_options}, which can be used to configure a node subscription.
 * <h3>Usage</h3>
 * To wrap an existing {@link rocks.xmpp.extensions.data.model.DataForm} to retrieve standard data from it, use:
 * ```java
 * SubscribeOptions subscribeOptions = new SubscribeOptions(dataForm);
 * ```
 * To build a form:
 * ```java
 * SubscribeOptions subscribeOptions = SubscribeOptions.builder()
 *     .deliver(true)
 *     .digest(true)
 *     .digestFrequency(3)
 *     .includeBody(true)
 *     .temporary(true)
 *     .showValues(Arrays.asList(AbstractPresence.Show.AWAY, AbstractPresence.Show.CHAT, null))
 *     .subscriptionType(SubscribeOptions.SubscriptionType.NODES)
 *     .subscriptionDepth(-1)
 *     .build();
 * ```
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0060.html#registrar-formtypes-subscribe">16.4.2 pubsub#subscribe_options FORM_TYPE</a>
 */
public final class SubscribeOptions {

    private static final String FORM_TYPE = "http://jabber.org/protocol/pubsub#subscribe_options";

    /**
     * Whether an entity wants to receive
     * or disable notifications
     */
    private static final String DELIVER = "pubsub#deliver";

    /**
     * Whether an entity wants to receive digests
     * (aggregations) of notifications or all
     * notifications individually
     */
    private static final String DIGEST = "pubsub#digest";

    /**
     * The minimum number of milliseconds between
     * sending any two notification digests
     */
    private static final String DIGEST_FREQUENCY = "pubsub#digest_frequency";

    /**
     * The DateTime at which a leased subscription
     * will end or has ended
     */
    private static final String EXPIRE = "pubsub#expire";

    /**
     * Whether an entity wants to receive an XMPP
     * message body in addition to the payload
     * format
     */
    private static final String INCLUDE_BODY = "pubsub#include_body";

    /**
     * The presence states for which an entity
     * wants to receive notifications
     */
    private static final String SHOW_VALUES = "pubsub#show-values";

    private static final String SUBSCRIPTION_TYPE = "pubsub#subscription_type";

    private static final String SUBSCRIPTION_DEPTH = "pubsub#subscription_depth";

    private final DataForm dataForm;

    public SubscribeOptions(DataForm dataForm) {
        this.dataForm = dataForm;
    }

    /**
     * Creates a builder to build subscribe options.
     *
     * @return The builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets the underlying data form.
     *
     * @return The underlying data form.
     */
    public final DataForm getDataForm() {
        return dataForm;
    }

    /**
     * Whether an entity wants to receive or disable notifications.
     *
     * @return True, if notifications are delivered.
     */
    public final Boolean isDeliver() {
        return dataForm.findValueAsBoolean(DELIVER);
    }

    /**
     * Whether an entity wants to receive digests
     * (aggregations) of notifications or all
     * notifications individually.
     *
     * @return True, if digests are sent.
     */
    public Boolean isDigest() {
        return dataForm.findValueAsBoolean(DIGEST);
    }

    /**
     * The minimum number of milliseconds between
     * sending any two notification digests
     *
     * @return The digest frequency.
     */
    public final Integer getDigestFrequency() {
        return dataForm.findValueAsInteger(DIGEST_FREQUENCY);
    }

    /**
     * The DateTime at which a leased subscription
     * will end or has ended.
     *
     * @return The expiration date.
     */
    public final Instant getExpire() {
        DataForm.Field field = dataForm.findField(EXPIRE);
        if (!field.getValues().isEmpty() && field.getValues().get(0) != null && !field.getValues().get(0).equals("presence")) {
            return field.getValueAsInstant();
        }
        return null;
    }

    /**
     * Whether an entity wants to receive an XMPP
     * message body in addition to the payload
     * format.
     *
     * @return True, if the body is included.
     */
    public final Boolean isIncludeBody() {
        return dataForm.findValueAsBoolean(INCLUDE_BODY);
    }

    /**
     * Gets the show values.
     *
     * @return The show values.
     */
    public final List<Presence.Show> getShowValues() {
        List<String> values = dataForm.findValues(SHOW_VALUES);
        List<Presence.Show> list = new ArrayList<>();
        for (String value : values) {
            if ("online".equals(value)) {
                list.add(null);
            } else {
                list.add(Presence.Show.valueOf(value.toUpperCase()));
            }
        }
        return list;
    }

    /**
     * Gets the subscription type.
     *
     * @return The subscription type.
     */
    public final SubscriptionType getSubscriptionType() {
        String value = dataForm.findValue(SUBSCRIPTION_TYPE);
        if (value != null) {
            return SubscriptionType.valueOf(value.toUpperCase());
        }
        return null;
    }

    /**
     * Gets the subscription depth. A negative value of -1 represents "all".
     *
     * @return The subscription depth.
     */
    public final Integer getSubscriptionDepth() {
        String value = dataForm.findValue(SUBSCRIPTION_DEPTH);
        if ("all".equals(value)) {
            return -1;
        } else {
            return Integer.valueOf(value);
        }
    }

    /**
     * Whether the subscription is temporary, i.e. presence-based.
     *
     * @return True, if the subscription is temporary.
     */
    public final boolean isTemporary() {
        DataForm.Field field = dataForm.findField(EXPIRE);
        return !field.getValues().isEmpty() && field.getValues().get(0) != null && field.getValues().get(0).equals("presence");
    }

    /**
     * The subscription type.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0060.html#auto-subscribe">9.1 Auto-Subscribe</a>
     */
    public enum SubscriptionType {
        /**
         * Receive notification of new items only
         */
        ITEMS,
        /**
         * Receive notification of new nodes only
         */
        NODES
    }

    /**
     * A builder for the subscribe options.
     *
     * @see SubscribeOptions
     */
    public static final class Builder extends DataForm.Builder<SubscribeOptions.Builder> {

        private Boolean deliver;

        private Boolean digest;

        private Integer digestFrequency;

        private Boolean includeBody;

        private Collection<Presence.Show> showValues;

        private Instant expireAt;

        private Boolean temporary;

        private SubscriptionType subscriptionType;

        private Integer subscriptionDepth;

        private Builder() {
        }

        /**
         * Sets whether an entity wants to receive or disable notifications.
         *
         * @param deliver Whether an entity wants to receive or disable notifications.
         * @return The builder.
         */
        public final Builder deliver(boolean deliver) {
            this.deliver = deliver;
            return this;
        }

        /**
         * Sets whether you want to receive digests (aggregations) of notifications or all notifications individually.
         *
         * @param digest Whether you want to receive digests (aggregations) of notifications or all notifications individually.
         * @return The builder.
         */
        public final Builder digest(boolean digest) {
            this.digest = digest;
            return this;
        }

        /**
         * Sets the minimum number of milliseconds between sending any two notification digests
         *
         * @param digestFrequency The minimum number of milliseconds between sending any two notification digests.
         * @return The builder.
         */
        public final Builder digestFrequency(int digestFrequency) {
            this.digestFrequency = digestFrequency;
            return this;
        }

        /**
         * Sets whether you want to receive an XMPP message body in addition to the payload.
         *
         * @param includeBody Whether you want to receive an XMPP message body in addition to the payload.
         * @return The builder.
         * @see <a href="https://xmpp.org/extensions/xep-0060.html#impl-body">12.7 Including a Message Body</a>
         */
        public final Builder includeBody(boolean includeBody) {
            this.includeBody = includeBody;
            return this;
        }

        /**
         * Sets the presence states for which an entity wants to receive notifications. A null value corresponds to "available" presence.
         *
         * @param showValues The presence states for which an entity wants to receive notifications.
         * @return The builder.
         */
        public final Builder showValues(Collection<Presence.Show> showValues) {
            this.showValues = showValues;
            return this;
        }

        /**
         * Sets the expiration date.
         *
         * @param expireAt The expiration date.
         * @return The builder.
         * @see <a href="https://xmpp.org/extensions/xep-0060.html#impl-leases">12.18 Time-Based Subscriptions (Leases)</a>
         */
        public final Builder expireAt(Instant expireAt) {
            this.expireAt = expireAt;
            return this;
        }

        /**
         * If the subscription is temporary, i.e. only as long as you are online.
         *
         * @param temporary If the subscription is temporary, i.e. only as long as you are online.
         * @return The builder.
         * @see <a href="https://xmpp.org/extensions/xep-0060.html#impl-tempsub">12.4 Temporary Subscriptions</a>
         */
        public final Builder temporary(boolean temporary) {
            this.temporary = temporary;
            return this;
        }

        /**
         * Sets the subscription type.
         *
         * @param subscriptionType The subscription type.
         * @return The builder.
         * @see <a href="https://xmpp.org/extensions/xep-0060.html#auto-subscribe">9.1 Auto-Subscribe</a>
         */
        public final Builder subscriptionType(SubscriptionType subscriptionType) {
            this.subscriptionType = subscriptionType;
            return this;
        }

        /**
         * Sets the subscription depth. If the depth is negative, the depth is interpreted as "all".
         *
         * @param subscriptionDepth The subscription depth.
         * @return The builder.
         * @see <a href="https://xmpp.org/extensions/xep-0060.html#auto-subscribe">9.1 Auto-Subscribe</a>
         */
        public final Builder subscriptionDepth(int subscriptionDepth) {
            this.subscriptionDepth = subscriptionDepth;
            return this;
        }

        @Override
        protected final SubscribeOptions.Builder self() {
            return this;
        }

        /**
         * Builds the subscribe options.
         *
         * @return The subscribe options.
         */
        public final SubscribeOptions build() {
            Collection<DataForm.Field> fields = new ArrayDeque<>();
            if (deliver != null) {
                fields.add(DataForm.Field.builder().var(DELIVER).value(deliver).build());
            }
            if (digest != null) {
                fields.add(DataForm.Field.builder().var(DIGEST).value(digest).build());
            }
            if (digestFrequency != null) {
                fields.add(DataForm.Field.builder().var(DIGEST_FREQUENCY).value(digestFrequency).build());
            }
            if (temporary != null && temporary) {
                // To subscribe temporarily, the subscriber MUST set the "pubsub#expire" subscription configuration option to a literal value of "presence".
                fields.add(DataForm.Field.builder().var(EXPIRE).value("presence").build());
            } else if (expireAt != null) {
                fields.add(DataForm.Field.builder().var(EXPIRE).value(expireAt).build());
            }
            if (includeBody != null) {
                fields.add(DataForm.Field.builder().var(INCLUDE_BODY).value(includeBody).build());
            }
            if (showValues != null && !showValues.isEmpty()) {
                DataForm.Field.Builder fieldBuilder = DataForm.Field.builder().var(SHOW_VALUES);
                Collection<String> values = new ArrayDeque<>();
                for (Presence.Show show : showValues) {
                    if (show != null) {
                        values.add(show.name().toLowerCase());
                    } else {
                        values.add("online");
                    }
                }
                fieldBuilder.values(values).type(DataForm.Field.Type.LIST_MULTI);
                fields.add(fieldBuilder.build());
            }
            if (subscriptionType != null) {
                fields.add(DataForm.Field.builder().var(SUBSCRIPTION_TYPE).value(subscriptionType.name().toLowerCase()).type(DataForm.Field.Type.LIST_SINGLE).build());
            }
            if (subscriptionDepth != null) {
                DataForm.Field.Builder fieldBuilder = DataForm.Field.builder().var(SUBSCRIPTION_DEPTH);
                if (subscriptionDepth < 1) {
                    fieldBuilder.value("all");
                } else {
                    fieldBuilder.value(subscriptionDepth.toString());
                }
                fieldBuilder.type(DataForm.Field.Type.LIST_SINGLE);
                fields.add(fieldBuilder.build());
            }

            fields(fields).formType(FORM_TYPE).type(DataForm.Type.SUBMIT);
            return new SubscribeOptions(new DataForm(this));
        }
    }
}
