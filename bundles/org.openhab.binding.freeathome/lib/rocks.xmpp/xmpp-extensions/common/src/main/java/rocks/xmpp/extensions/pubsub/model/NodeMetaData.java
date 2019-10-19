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

package rocks.xmpp.extensions.pubsub.model;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.extensions.data.model.DataForm;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Represents a standardized {@link rocks.xmpp.extensions.data.model.DataForm} with form type {@code http://jabber.org/protocol/pubsub#meta-data}, which can be used to retrieve node meta data.
 * <h3>Usage</h3>
 * To wrap an existing {@link rocks.xmpp.extensions.data.model.DataForm} to retrieve standard data from it, use:
 * ```java
 * NodeMetaData nodeMetaData = new NodeMetaData(dataForm);
 * ```
 * To build a form:
 * ```java
 * NodeMetaData nodeMetaData = NodeMetaData.builder()
 *     .contacts(Collections.singleton(Jid.of("contact")))
 *     .creationDate(date)
 *     .creator(Jid.of("creator"))
 *     .description("desc")
 *     .language("de")
 *     .numberOfSubscribers(2)
 *     .owners(Collections.singleton(Jid.of("owner")))
 *     .publishers(Collections.singleton(Jid.of("publisher")))
 *     .title("title")
 *     .payloadType("namespace")
 *     .build();
 * ```
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0060.html#entity-metadata">5.4 Discover Node Metadata</a>
 * @see <a href="https://xmpp.org/extensions/xep-0060.html#registrar-formtypes-metadata">16.4.3 pubsub#meta-data FORM_TYPE</a>
 */
public final class NodeMetaData {

    public static final String FORM_TYPE = "http://jabber.org/protocol/pubsub#meta-data";

    /**
     * The JIDs of those to contact with questions
     */
    private static final String CONTACT = "pubsub#contact";

    /**
     * The datetime when the node was created
     */
    private static final String CREATION_DATE = "pubsub#creation_date";

    /**
     * The JID of the node creator
     */
    private static final String CREATOR = "pubsub#creator";

    /**
     * A description of the node
     */
    private static final String DESCRIPTION = "pubsub#description";

    /**
     * The default language of the node
     */
    private static final String LANGUAGE = "pubsub#language";

    /**
     * The number of subscribers to the node
     */
    private static final String NUM_SUBSCRIBERS = "pubsub#num_subscribers";

    /**
     * The JIDs of those with an affiliation of owner
     */
    private static final String OWNER = "pubsub#owner";

    /**
     * The JIDs of those with an affiliation of publisher
     */
    private static final String PUBLISHER = "pubsub#publisher";

    /**
     * The name of the node
     */
    private static final String TITLE = "pubsub#title";

    /**
     * Payload type
     */
    private static final String TYPE = "pubsub#type";

    private final DataForm dataForm;

    /**
     * Creates a node meta data form.
     *
     * @param dataForm The underlying data form.
     */
    public NodeMetaData(DataForm dataForm) {
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
     * Gets the underlying data form.
     *
     * @return The underlying data form.
     */
    public final DataForm getDataForm() {
        return dataForm;
    }

    /**
     * Gets the contacts.
     *
     * @return The contacts.
     */
    public final List<Jid> getContacts() {
        return dataForm.findValuesAsJid(CONTACT);
    }

    /**
     * Gets the creation date.
     *
     * @return The creation date.
     */
    public final Instant getCreationDate() {
        return dataForm.findValueAsInstant(CREATION_DATE);
    }

    /**
     * Gets the creator.
     *
     * @return The creator.
     */
    public final Jid getCreator() {
        return dataForm.findValueAsJid(CREATOR);
    }

    /**
     * Gets the description.
     *
     * @return The description.
     */
    public final String getDescription() {
        return dataForm.findValue(DESCRIPTION);
    }

    /**
     * Gets the language.
     *
     * @return The language.
     */
    public final Locale getLanguage() {
        String lang = dataForm.findValue(LANGUAGE);
        return lang != null ? Locale.forLanguageTag(lang) : null;
    }

    /**
     * Gets the number of subscribers.
     *
     * @return The subscribers.
     */
    public final Integer getNumberOfSubscribers() {
        return dataForm.findValueAsInteger(NUM_SUBSCRIBERS);
    }

    /**
     * Gets the owners.
     *
     * @return The owners.
     */
    public final List<Jid> getOwners() {
        return dataForm.findValuesAsJid(OWNER);
    }

    /**
     * Gets the publishers.
     *
     * @return The publishers.
     */
    public final List<Jid> getPublishers() {
        return dataForm.findValuesAsJid(PUBLISHER);
    }

    /**
     * Gets the title.
     *
     * @return The title.
     */
    public final String getNodeTitle() {
        return dataForm.findValue(TITLE);
    }

    /**
     * Gets the payload type of the node.
     *
     * @return The payload type.
     */
    public final String getPayloadType() {
        return dataForm.findValue(TYPE);
    }

    /**
     * A builder class to build the meta data form. If not provided the default data form type is {@link rocks.xmpp.extensions.data.model.DataForm.Type#RESULT}.
     */
    public static final class Builder extends DataForm.Builder<Builder> {
        private Collection<Jid> contacts;

        private Instant creationDate;

        private Jid creator;

        private String description;

        private Locale language;

        private Integer numberOfSubscribers;

        private Collection<Jid> owners;

        private Collection<Jid> publishers;

        private String nodeTitle;

        private String payloadType;

        private Builder() {
        }

        /**
         * The JIDs of those to contact with questions.
         *
         * @param contacts The contacts.
         * @return The builder.
         */
        public final Builder contacts(Collection<Jid> contacts) {
            this.contacts = contacts;
            return this;
        }

        /**
         * The datetime when the node was created.
         *
         * @param creationDate The creation date.
         * @return The builder.
         */
        public final Builder creationDate(Instant creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        /**
         * The JID of the node creator.
         *
         * @param creator The creator.
         * @return The builder.
         */
        public final Builder creator(Jid creator) {
            this.creator = creator;
            return this;
        }

        /**
         * A description of the node.
         *
         * @param description The description.
         * @return The builder.
         */
        public final Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * The default language of the node.
         *
         * @param language The language.
         * @return The builder.
         */
        public final Builder language(Locale language) {
            this.language = language;
            return this;
        }

        /**
         * The number of subscribers to the node.
         *
         * @param numberOfSubscribers The number of subscribers to the node.
         * @return The builder.
         */
        public final Builder numberOfSubscribers(int numberOfSubscribers) {
            this.numberOfSubscribers = numberOfSubscribers;
            return this;
        }

        /**
         * The JIDs of those with an affiliation of owner.
         *
         * @param owners The owners.
         * @return The builder.
         */
        public final Builder owners(Collection<Jid> owners) {
            this.owners = owners;
            return this;
        }

        /**
         * The JIDs of those with an affiliation of publisher.
         *
         * @param publishers The publishers.
         * @return The builder.
         */
        public final Builder publishers(Collection<Jid> publishers) {
            this.publishers = publishers;
            return this;
        }

        /**
         * The name of the node.
         *
         * @param title The title.
         * @return The builder.
         */
        public final Builder nodeTitle(String title) {
            this.nodeTitle = title;
            return this;
        }

        /**
         * The payload type.
         *
         * @param payloadType The payload type.
         * @return The builder.
         */
        public final Builder payloadType(String payloadType) {
            this.payloadType = payloadType;
            return this;
        }

        @Override
        protected final Builder self() {
            return this;
        }

        /**
         * Builds the meta data form.
         *
         * @return The meta data form.
         */
        public final NodeMetaData build() {
            Collection<DataForm.Field> fields = new ArrayDeque<>();

            if (contacts != null && !contacts.isEmpty()) {
                fields.add(DataForm.Field.builder().var(CONTACT).valuesJid(contacts).build());
            }
            if (creationDate != null) {
                fields.add(DataForm.Field.builder().var(CREATION_DATE).value(creationDate).build());
            }
            if (creator != null) {
                fields.add(DataForm.Field.builder().var(CREATOR).value(creator).build());
            }
            if (description != null) {
                fields.add(DataForm.Field.builder().var(DESCRIPTION).value(description).build());
            }
            if (language != null) {
                fields.add(DataForm.Field.builder().var(LANGUAGE).value(language.toLanguageTag()).type(DataForm.Field.Type.LIST_SINGLE).build());
            }
            if (numberOfSubscribers != null) {
                fields.add(DataForm.Field.builder().var(NUM_SUBSCRIBERS).value(numberOfSubscribers).build());
            }
            if (owners != null && !owners.isEmpty()) {
                fields.add(DataForm.Field.builder().var(OWNER).valuesJid(owners).build());
            }
            if (publishers != null && !publishers.isEmpty()) {
                fields.add(DataForm.Field.builder().var(PUBLISHER).valuesJid(publishers).build());
            }
            if (nodeTitle != null) {
                fields.add(DataForm.Field.builder().var(TITLE).value(nodeTitle).build());
            }
            if (payloadType != null) {
                fields.add(DataForm.Field.builder().var(TYPE).value(payloadType).build());
            }
            fields(fields).formType(FORM_TYPE).type(DataForm.Type.RESULT);
            return new NodeMetaData(new DataForm(this));
        }
    }
}
