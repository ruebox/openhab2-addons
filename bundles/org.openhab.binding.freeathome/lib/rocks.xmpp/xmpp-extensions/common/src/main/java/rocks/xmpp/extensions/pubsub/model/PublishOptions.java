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

import rocks.xmpp.extensions.data.model.DataForm;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;

/**
 * Represents a standardized {@link rocks.xmpp.extensions.data.model.DataForm} with form type {@code http://jabber.org/protocol/pubsub#publish-options}, which can be used to retrieve node meta data.
 * <h3>Usage</h3>
 * To wrap an existing {@link rocks.xmpp.extensions.data.model.DataForm} to retrieve standard data from it, use:
 * ```java
 * PublishOptions publishOptions = new PublishOptions(dataForm);
 * ```
 * To build a form:
 * ```java
 * PublishOptions publishOptions = PublishOptions.builder()
 *     .accessModel(AccessModel.AUTHORIZE)
 *     .persistItems(true)
 *     .rosterGroupsAllowed(Collections.singleton("Friends"))
 *     .sendLastPublishedItem(SendLastPublishedItem.ON_SUB)
 *     .build();
 * ```
 * <h4>Persistent Storage of Public Data via PubSub</h4>
 * ```java
 * PublishOptions publishOptions = PublishOptions.forStorageOfPublicData();
 * ```
 * <h4>Persistent Storage of Public Private via PubSub</h4>
 * ```java
 * PublishOptions publishOptions = PublishOptions.forStorageOfPrivateData();
 * ```
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0060.html#registrar-formtypes-publish">16.4.5 pubsub#publish-options FORM_TYPE</a>
 * @see <a href="https://xmpp.org/extensions/xep-0060.html#publisher-publish-options">7.1.5 Publishing Options</a>
 * @see <a href="https://xmpp.org/extensions/xep-0222.html">XEP-0222: Persistent Storage of Public Data via PubSub</a>
 * @see <a href="https://xmpp.org/extensions/xep-0223.html">XEP-0223: Persistent Storage of Private Data via PubSub</a>
 */
public final class PublishOptions {

    private static final String FORM_TYPE = "http://jabber.org/protocol/pubsub#publish-options";

    private static final String ACCESS_MODEL = "pubsub#access_model";

    private static final String PERSIST_ITEMS = "pubsub#persist_items";

    private static final String SEND_LAST_PUBLISHED_ITEM = "pubsub#send_last_published_item";

    private static final String ROSTER_GROUPS_ALLOWED = "pubsub#roster_groups_allowed";

    private final DataForm dataForm;

    public PublishOptions(DataForm dataForm) {
        this.dataForm = dataForm;
    }

    /**
     * Creates the builder to build a meta data form.
     *
     * @return The builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates publish options for use of persistent storage of public data via pubsub.
     *
     * @param accessModel The access model.
     * @return The publish options.
     * @see <a href="https://xmpp.org/extensions/xep-0222.html">XEP-0222: Persistent Storage of Public Data via PubSub</a>
     */
    public static PublishOptions forStorageOfPublicData(AccessModel accessModel) {
        return builder().persistItems(true).sendLastPublishedItem(SendLastPublishedItem.NEVER).accessModel(accessModel).build();
    }

    /**
     * Creates publish options for use of persistent storage of private data via pubsub.
     *
     * @return The publish options.
     * @see <a href="https://xmpp.org/extensions/xep-0223.html">XEP-0223: Persistent Storage of Private Data via PubSub</a>
     */
    public static PublishOptions forStorageOfPrivateData() {
        return builder().persistItems(true).accessModel(AccessModel.WHITELIST).build();
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
     * Gets the access model.
     *
     * @return The access model.
     */
    public final AccessModel getAccessModel() {
        String value = dataForm.findValue(ACCESS_MODEL);
        if (value != null) {
            return AccessModel.valueOf(value.toUpperCase());
        }
        return null;
    }

    /**
     * Whether items are persisted.
     *
     * @return Whether items are persisted.
     */
    public final Boolean isPersistItems() {
        return dataForm.findValueAsBoolean(PERSIST_ITEMS);
    }

    /**
     * Gets the value which specifies when to send the last published item.
     *
     * @return When to send the last published item.
     */
    public final SendLastPublishedItem getSendLastPublishedItem() {
        String value = dataForm.findValue(SEND_LAST_PUBLISHED_ITEM);
        if (value != null) {
            return SendLastPublishedItem.valueOf(value.toUpperCase());
        }
        return null;
    }

    /**
     * Gets the allowed roster groups.
     *
     * @return The allowed roster groups.
     */
    public final List<String> getRosterGroupsAllowed() {
        return dataForm.findValues(ROSTER_GROUPS_ALLOWED);
    }

    /**
     * A builder class to build the publish options form. If not provided the default data form type is {@link rocks.xmpp.extensions.data.model.DataForm.Type#SUBMIT}.
     */
    public static final class Builder extends DataForm.Builder<Builder> {

        private AccessModel accessModel;

        private Boolean persistItems;

        private SendLastPublishedItem sendLastPublishedItem;

        private Collection<String> rosterGroupsAllowed;

        public final Builder accessModel(AccessModel accessModel) {
            this.accessModel = accessModel;
            return this;
        }

        public final Builder persistItems(boolean persistItems) {
            this.persistItems = persistItems;
            return this;
        }

        public final Builder sendLastPublishedItem(SendLastPublishedItem sendLastPublishedItem) {
            this.sendLastPublishedItem = sendLastPublishedItem;
            return this;
        }

        public final Builder rosterGroupsAllowed(Collection<String> rosterGroupsAllowed) {
            this.rosterGroupsAllowed = rosterGroupsAllowed;
            return this;
        }

        @Override
        protected final Builder self() {
            return this;
        }

        /**
         * Builds the publish options.
         *
         * @return The publish options.
         */
        public PublishOptions build() {
            Collection<DataForm.Field> fields = new ArrayDeque<>();

            if (accessModel != null) {
                fields.add(DataForm.Field.builder().var(ACCESS_MODEL).value(accessModel.name().toLowerCase()).type(DataForm.Field.Type.LIST_SINGLE).build());
            }
            if (persistItems != null) {
                fields.add(DataForm.Field.builder().var(PERSIST_ITEMS).value(persistItems).build());
            }
            if (sendLastPublishedItem != null) {
                fields.add(DataForm.Field.builder().var(SEND_LAST_PUBLISHED_ITEM).value(sendLastPublishedItem.name().toLowerCase()).type(DataForm.Field.Type.LIST_SINGLE).build());
            }
            if (rosterGroupsAllowed != null && !rosterGroupsAllowed.isEmpty()) {
                fields.add(DataForm.Field.builder().var(ROSTER_GROUPS_ALLOWED).values(rosterGroupsAllowed).type(DataForm.Field.Type.LIST_MULTI).build());
            }

            fields(fields).formType(FORM_TYPE).type(DataForm.Type.SUBMIT);
            return new PublishOptions(new DataForm(this));
        }
    }
}
