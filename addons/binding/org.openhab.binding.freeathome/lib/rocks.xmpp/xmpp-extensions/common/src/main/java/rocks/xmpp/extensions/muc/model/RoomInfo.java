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

package rocks.xmpp.extensions.muc.model;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.extensions.data.model.DataForm;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Represents a standardized {@link rocks.xmpp.extensions.data.model.DataForm} with form type {@code http://jabber.org/protocol/muc#roominfo}, which can be used to retrieve MUC room info.
 * <h3>Usage</h3>
 * To wrap an existing {@link rocks.xmpp.extensions.data.model.DataForm} to retrieve standard data from it, use:
 * ```java
 * RoomInfo roomInfo = new RoomInfo(dataForm);
 * ```
 * To build a form:
 * ```java
 * RoomInfo roomInfo = RoomInfo.builder()
 *     .maxHistoryMessages(50)
 *     .contacts(Arrays.asList(Jid.of("contact1"), Jid.of("contact2")))
 *     .description("The place for all good witches!")
 *     .language("en")
 *     .ldapGroup("cn=witches,dc=shakespeare,dc=lit")
 *     .logs(new URL("http://www.shakespeare.lit/chatlogs/coven/"))
 *     .currentNumberOfOccupants(45)
 *     .subject("Spells")
 *     .changeSubjectAllowed(true)
 *     .build();
 * ```
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0045.html#disco-roominfo">6.4 Querying for Room Information</a>
 * @see <a href="https://xmpp.org/extensions/xep-0045.html#registrar-formtype-roominfo">15.5.4 muc#roominfo FORM_TYPE</a>
 */
public final class RoomInfo {

    public static final String FORM_TYPE = "http://jabber.org/protocol/muc#roominfo";

    /**
     * Maximum Number of History Messages Returned by Room
     */
    private static final String MAX_HISTORY_FETCH = "muc#maxhistoryfetch";

    /**
     * Contact Addresses (normally room owners)
     */
    private static final String CONTACT_JID = "muc#roominfo_contactjid";

    /**
     * Short Description of Room
     */
    private static final String DESCRIPTION = "muc#roominfo_description";

    /**
     * Natural Language for Room Discussions
     */
    private static final String LANGUAGE = "muc#roominfo_lang";

    /**
     * An associated LDAP group that defines
     * room membership; this should be an LDAP
     * Distinguished Name according to an
     * implementation-specific or
     * deployment-specific definition of a
     * group.
     */
    private static final String LDAP_GROUP = "muc#roominfo_ldapgroup";

    /**
     * URL for Archived Discussion Logs
     */
    private static final String LOGS = "muc#roominfo_logs";

    /**
     * Current Number of Occupants in Room
     */
    private static final String OCCUPANTS = "muc#roominfo_occupants";

    /**
     * Current Discussion Topic
     */
    private static final String SUBJECT = "muc#roominfo_subject";

    /**
     * The room subject can be modified by participants
     */
    private static final String SUBJECT_MOD = "muc#roominfo_subjectmod";

    private final DataForm dataForm;

    public RoomInfo(DataForm dataForm) {
        this.dataForm = dataForm;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets the maximum number of history messages returned by the room.
     *
     * @return The maximum number of history messages returned by the room.
     */
    public Integer getMaxHistoryMessages() {
        return dataForm.findValueAsInteger(MAX_HISTORY_FETCH);
    }

    /**
     * Gets the contact addresses (normally room owners).
     *
     * @return The contact addresses.
     */
    public List<Jid> getContacts() {
        return dataForm.findValuesAsJid(CONTACT_JID);
    }

    /**
     * Gets a short description.
     *
     * @return The description.
     */
    public String getDescription() {
        return dataForm.findValue(DESCRIPTION);
    }

    /**
     * Gets the natural language for room discussions.
     *
     * @return The language.
     */
    public Locale getLanguage() {
        String lang = dataForm.findValue(LANGUAGE);
        return lang != null ? Locale.forLanguageTag(lang) : null;
    }

    /**
     * Gets an associated LDAP group that defines
     * room membership; this should be an LDAP
     * Distinguished Name according to an
     * implementation-specific or
     * deployment-specific definition of a
     * group.
     *
     * @return The LDAP group.
     */
    public String getLdapGroup() {
        return dataForm.findValue(LDAP_GROUP);
    }

    /**
     * Gets an URL for archived discussion logs.
     *
     * @return The URL.
     */
    public URL getLogs() {
        String value = dataForm.findValue(LOGS);
        if (value != null) {
            try {
                return new URL(value);
            } catch (MalformedURLException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Gets the current number of occupants in the room.
     *
     * @return The number of occupants.
     */
    public Integer getCurrentNumberOfOccupants() {
        return dataForm.findValueAsInteger(OCCUPANTS);
    }

    /**
     * Gets the current discussion topic.
     *
     * @return The topic.
     */
    public String getSubject() {
        return dataForm.findValue(SUBJECT);
    }

    /**
     * Indicates, whether the room subject can be modified by participants.
     *
     * @return Whether the room subject can be modified by participants.
     */
    public boolean isChangeSubjectAllowed() {
        return dataForm.findValueAsBoolean(SUBJECT_MOD);
    }

    /**
     * Gets the underlying data form.
     *
     * @return The underlying data form.
     */
    public DataForm getDataForm() {
        return dataForm;
    }

    /**
     * A builder to build a room info form. The form is of type {@link rocks.xmpp.extensions.data.model.DataForm.Type#RESULT} by default.
     */
    public static final class Builder extends DataForm.Builder<Builder> {
        private Integer maxHistoryMessages;

        private Collection<Jid> contacts;

        private String description;

        private Locale language;

        private String ldapGroup;

        private URL logs;

        private Integer occupants;

        private String subject;

        private Boolean changeSubjectAllowed;

        private Builder() {
        }

        /**
         * Sets the maximum number of history messages returned by the room.
         *
         * @param maxHistoryMessages The maximum number of history messages returned by the room.
         * @return The builder.
         */
        public Builder maxHistoryMessages(int maxHistoryMessages) {
            this.maxHistoryMessages = maxHistoryMessages;
            return this;
        }

        /**
         * Gets the contact addresses (normally, room owner or owners).
         *
         * @param contacts The contact addresses.
         * @return The builder.
         */
        public Builder contacts(Collection<Jid> contacts) {
            this.contacts = contacts;
            return this;
        }

        /**
         * Sets a short description.
         *
         * @param description The description.
         * @return The builder.
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Sets the natural language for room discussions.
         *
         * @param language The language.
         * @return The builder.
         */
        public Builder language(Locale language) {
            this.language = language;
            return this;
        }

        /**
         * Sets an associated LDAP group that defines
         * room membership; this should be an LDAP
         * Distinguished Name according to an
         * implementation-specific or
         * deployment-specific definition of a
         * group.
         *
         * @param ldapGroup LDAP group.
         * @return The builder.
         */
        public Builder ldapGroup(String ldapGroup) {
            this.ldapGroup = ldapGroup;
            return this;
        }

        /**
         * Sets an URL for archived discussion logs.
         *
         * @param logs The URL.
         * @return The builder.
         */
        public Builder logs(URL logs) {
            this.logs = logs;
            return this;
        }

        /**
         * Sets the current number of occupants in the room.
         *
         * @param occupants The number of occupants.
         * @return The builder.
         */
        public Builder currentNumberOfOccupants(int occupants) {
            this.occupants = occupants;
            return this;
        }

        /**
         * Sets the current discussion topic.
         *
         * @param subject The topic.
         * @return The builder.
         */
        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        /**
         * Indicates, whether the room subject can be modified by participants.
         *
         * @param changeSubjectAllowed Whether the room subject can be modified by participants.
         * @return The builder.
         */
        public Builder changeSubjectAllowed(boolean changeSubjectAllowed) {
            this.changeSubjectAllowed = changeSubjectAllowed;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        /**
         * Builds the room info.
         *
         * @return The room info.
         */
        public RoomInfo build() {
            Collection<DataForm.Field> fields = new ArrayDeque<>();
            if (maxHistoryMessages != null) {
                fields.add(DataForm.Field.builder().var(MAX_HISTORY_FETCH).value(maxHistoryMessages).build());
            }
            if (contacts != null && !contacts.isEmpty()) {
                fields.add(DataForm.Field.builder().var(CONTACT_JID).valuesJid(contacts).build());
            }
            if (description != null) {
                fields.add(DataForm.Field.builder().var(DESCRIPTION).value(description).build());
            }
            if (language != null) {
                fields.add(DataForm.Field.builder().var(LANGUAGE).value(language.toLanguageTag()).build());
            }
            if (ldapGroup != null) {
                fields.add(DataForm.Field.builder().var(LDAP_GROUP).value(ldapGroup).build());
            }
            if (logs != null) {
                fields.add(DataForm.Field.builder().var(LOGS).value(logs.toString()).build());
            }
            if (occupants != null) {
                fields.add(DataForm.Field.builder().var(OCCUPANTS).value(occupants).build());
            }
            if (subject != null) {
                fields.add(DataForm.Field.builder().var(SUBJECT).value(subject).build());
            }
            if (changeSubjectAllowed != null) {
                fields.add(DataForm.Field.builder().var(SUBJECT_MOD).value(changeSubjectAllowed).build());
            }
            fields(fields).formType(FORM_TYPE).type(DataForm.Type.RESULT);
            return new RoomInfo(new DataForm(this));
        }
    }
}
