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

import java.net.URI;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a standardized {@link rocks.xmpp.extensions.data.model.DataForm} with form type {@code http://jabber.org/protocol/muc#roomconfig}, which can be used to configure a MUC room.
 * <h3>Usage</h3>
 * To wrap an existing {@link rocks.xmpp.extensions.data.model.DataForm} to retrieve standard data from it, use:
 * ```java
 * RoomConfiguration roomConfiguration = new RoomConfiguration(dataForm);
 * ```
 * To build a form:
 * ```java
 * RoomConfiguration roomConfiguration = RoomConfiguration.builder()
 *     .maxHistoryMessages(4)
 *     .rolesThatMaySendPrivateMessages(Arrays.asList(Role.MODERATOR, Role.PARTICIPANT))
 *     .invitesAllowed(true)
 *     .changeSubjectAllowed(true)
 *     .loggingEnabled(true)
 *     .rolesThatMayRetrieveMemberList(Collections.singleton(Role.PARTICIPANT))
 *     .language("en")
 *     .pubSubNode(URI.create("xmpp:pubsub.shakespeare.lit?;node=princely_musings"))
 *     .maxUsers(30)
 *     .membersOnly(true)
 *     .moderated(true)
 *     .passwordProtected(true)
 *     .persistent(true)
 *     .rolesForWhichPresenceIsBroadcast(Arrays.asList(Role.MODERATOR, Role.PARTICIPANT))
 *     .publicRoom(true)
 *     .administrators(Arrays.asList(Jid.of("admin1"), Jid.of("admin2")))
 *     .description("description")
 *     .name("name")
 *     .owners(Arrays.asList(Jid.of("owner1"), Jid.of("owner2")))
 *     .password("pass")
 *     .rolesThatMayDiscoverRealJids(EnumSet.of(Role.MODERATOR))
 *     .build();
 * ```
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0045.html#createroom-reserved">10.1.3 Creating a Reserved Room</a>
 * @see <a href="https://xmpp.org/extensions/xep-0045.html#registrar-formtype-owner">15.5.3 muc#roomconfig FORM_TYPE</a>
 */
public final class RoomConfiguration {

    private static final String FORM_TYPE = "http://jabber.org/protocol/muc#roomconfig";

    private static final String MAX_HISTORY_FETCH = "muc#maxhistoryfetch";

    private static final String ALLOW_PM = "muc#roomconfig_allowpm";

    private static final String ALLOW_INVITES = "muc#roomconfig_allowinvites";

    private static final String CHANGE_SUBJECT = "muc#roomconfig_changesubject";

    private static final String ENABLE_LOGGING = "muc#roomconfig_enablelogging";

    private static final String GET_MEMBER_LIST = "muc#roomconfig_getmemberlist";

    private static final String LANGUAGE = "muc#roomconfig_lang";

    private static final String PUBSUB = "muc#roomconfig_pubsub";

    private static final String MAX_USERS = "muc#roomconfig_maxusers";

    private static final String MEMBERS_ONLY = "muc#roomconfig_membersonly";

    private static final String MODERATED_ROOM = "muc#roomconfig_moderatedroom";

    private static final String PASSWORD_PROTECTED = "muc#roomconfig_passwordprotectedroom";

    private static final String PERSISTENT_ROOM = "muc#roomconfig_persistentroom";

    private static final String PRESENCE_BROADCAST = "muc#roomconfig_presencebroadcast";

    private static final String PUBLIC_ROOM = "muc#roomconfig_publicroom";

    private static final String ROOM_ADMINS = "muc#roomconfig_roomadmins";

    private static final String ROOM_DESC = "muc#roomconfig_roomdesc";

    private static final String ROOM_NAME = "muc#roomconfig_roomname";

    private static final String ROOM_OWNERS = "muc#roomconfig_roomowners";

    private static final String ROOM_SECRET = "muc#roomconfig_roomsecret";

    private static final String WHOIS = "muc#roomconfig_whois";

    private final DataForm dataForm;

    public RoomConfiguration(DataForm dataForm) {
        this.dataForm = dataForm;
    }

    public static Builder builder() {
        return new Builder();
    }

    private static String rolesToValue(Collection<Role> roles, boolean isWhois) {
        if (roles.isEmpty() || roles.contains(Role.NONE)) {
            return "none";
        } else if (roles.contains(Role.MODERATOR) && roles.contains(Role.PARTICIPANT) && roles.contains(Role.VISITOR)) {
            return "anyone";
        } else if (roles.contains(Role.PARTICIPANT) && !isWhois) {
            // The _whois configuration option specifies whether the room is
            // non-anonymous (a value of "anyone"),
            // semi-anonymous (a value of "moderators"),
            // or fully anonmyous (a value of "none", not shown here).
            // => it lacks "participant" value, therefore don't use it.
            return "participants";
        } else {
            return "moderators";
        }
    }

    private static Set<Role> valueToRoles(String value) {
        Set<Role> roles = new LinkedHashSet<>();
        if (value != null) {
            switch (value) {
                case "anyone":
                    roles.addAll(EnumSet.of(Role.MODERATOR, Role.PARTICIPANT, Role.VISITOR));
                    break;
                case "participants":
                    // Anyone with Voice
                    roles.addAll(EnumSet.of(Role.MODERATOR, Role.PARTICIPANT));
                    break;
                case "moderators":
                    // Moderators Only
                    roles.add(Role.MODERATOR);
                    break;
                default:
                    roles.add(Role.NONE);
                    break;
            }
        }
        return Collections.unmodifiableSet(roles);
    }

    private static Set<Role> valuesToRoles(Collection<String> values) {
        Set<Role> roles = values.stream().map(value -> Role.valueOf(value.toUpperCase())).collect(Collectors.toCollection(LinkedHashSet::new));
        return Collections.unmodifiableSet(roles);
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
     * Gets the roles that may send private messages.
     *
     * @return The roles.
     */
    public Collection<Role> getRolesThatMaySendPrivateMessages() {
        return valueToRoles(dataForm.findValue(ALLOW_PM));
    }

    /**
     * Indicates, whether to allow occupants to invite others.
     *
     * @return True, if occupants are allowed to invite others.
     */
    public boolean isInvitesAllowed() {
        return dataForm.findValueAsBoolean(ALLOW_INVITES);
    }

    /**
     * Indicates, whether to allow occupants to change subject.
     *
     * @return True, if occupants are allowed to change subject.
     */
    public boolean isChangeSubjectAllowed() {
        return dataForm.findValueAsBoolean(CHANGE_SUBJECT);
    }

    /**
     * Indicates, whether to enable public logging of room conversations.
     *
     * @return True, if public logging of room conversations is enabled.
     */
    public boolean isLoggingEnabled() {
        return dataForm.findValueAsBoolean(ENABLE_LOGGING);
    }

    /**
     * Gets the roles which may retrieve member list.
     *
     * @return The roles which may retrieve member list.
     */
    public Collection<Role> getRolesThatMayRetrieveMemberList() {
        return valuesToRoles(dataForm.findValues(GET_MEMBER_LIST));
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
     * Gets the XMPP URI of associated publish-subscribe node.
     *
     * @return The URI.
     */
    public URI getPubSubNode() {
        String value = dataForm.findValue(PUBSUB);
        if (value != null) {
            try {
                return URI.create(value);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Gets the maximum number of room occupants.
     *
     * @return The maximum number of room occupants.
     */
    public Integer getMaxUsers() {
        return dataForm.findValueAsInteger(MAX_USERS);
    }

    /**
     * Indicates, whether the room is members-only.
     *
     * @return True, if the room is members-only.
     */
    public boolean isMembersOnly() {
        return dataForm.findValueAsBoolean(MEMBERS_ONLY);
    }

    /**
     * Indicates, whether the room is moderated.
     *
     * @return True, if the room is moderated.
     */
    public boolean isModerated() {
        return dataForm.findValueAsBoolean(MODERATED_ROOM);
    }

    /**
     * Indicates, whether the room is password protected.
     *
     * @return True, if the room is password protected.
     */
    public boolean isPasswordProtected() {
        return dataForm.findValueAsBoolean(PASSWORD_PROTECTED);
    }

    /**
     * Indicates, whether the room is persistent.
     *
     * @return True, if the room is persistent.
     */
    public boolean isPersistent() {
        return dataForm.findValueAsBoolean(PERSISTENT_ROOM);
    }

    /**
     * Gets the roles for which presence is broadcast.
     *
     * @return The roles for which presence is broadcast.
     */
    public Collection<Role> getRolesForWhichPresenceIsBroadcast() {
        return valuesToRoles(dataForm.findValues(PRESENCE_BROADCAST));
    }

    /**
     * Indicates, whether the room is public.
     *
     * @return True, if the room is public.
     */
    public boolean isPublicRoom() {
        return dataForm.findValueAsBoolean(PUBLIC_ROOM);
    }

    /**
     * Gets the administrators.
     *
     * @return The administrators.
     */
    public Collection<Jid> getAdministrators() {
        return dataForm.findValuesAsJid(ROOM_ADMINS);
    }

    /**
     * Gets a short description.
     *
     * @return The description.
     */
    public String getDescription() {
        return dataForm.findValue(ROOM_DESC);
    }

    /**
     * Gets the natural-language room name.
     *
     * @return The room name.
     */
    public String getName() {
        return dataForm.findValue(ROOM_NAME);
    }

    /**
     * Gets the owners.
     *
     * @return The owners.
     */
    public Collection<Jid> getOwners() {
        return dataForm.findValuesAsJid(ROOM_OWNERS);
    }

    /**
     * Gets the room password.
     *
     * @return The password.
     */
    public String getPassword() {
        return dataForm.findValue(ROOM_SECRET);
    }

    /**
     * Get the roles, which may discover real JIDs.
     *
     * @return The roles, which may discover real JIDs.
     */
    public Collection<Role> getRolesThatMayDiscoverRealJids() {
        return valueToRoles(dataForm.findValue(WHOIS));
    }

    /**
     * Gets the underlying form.
     *
     * @return The underlying data form.
     */
    public DataForm getDataForm() {
        return dataForm;
    }

    /**
     * A builder to build a room configuration. The form is of type {@link rocks.xmpp.extensions.data.model.DataForm.Type#SUBMIT} by default.
     */
    public static final class Builder extends DataForm.Builder<Builder> {
        private Integer maxHistoryFetch;

        private Collection<Role> rolesThatMaySendPrivateMessages;

        private Boolean invitesAllowed;

        private Boolean changeSubjectAllowed;

        private Boolean loggingEnabled;

        private Collection<Role> rolesThatMayRetrieveMemberList;

        private Locale language;

        private URI pubsubNode;

        private Integer maxUsers;

        private Boolean membersOnly;

        private Boolean moderated;

        private Boolean passwordProtected;

        private Boolean persistent;

        private Collection<Role> presenceBroadcast;

        private Boolean publicRoom;

        private Collection<Jid> admins;

        private String description;

        private String name;

        private Collection<Jid> owners;

        private String password;

        private Collection<Role> whois;

        private Builder() {
        }

        /**
         * Sets the maximum number of history messages returned by the room.
         *
         * @param maxHistoryMessages The maximum number of history messages returned by the room.
         * @return The builder.
         */
        public Builder maxHistoryMessages(int maxHistoryMessages) {
            this.maxHistoryFetch = maxHistoryMessages;
            return this;
        }

        /**
         * Roles that may send private messages.
         *
         * @param rolesThatMaySendPrivateMessages The roles.
         * @return The builder.
         */
        public Builder rolesThatMaySendPrivateMessages(Collection<Role> rolesThatMaySendPrivateMessages) {
            this.rolesThatMaySendPrivateMessages = rolesThatMaySendPrivateMessages;
            return this;
        }

        /**
         * Whether to allow occupants to invite others
         *
         * @param invitesAllowed Whether to allow occupants to invite others
         * @return The builder.
         */
        public Builder invitesAllowed(boolean invitesAllowed) {
            this.invitesAllowed = invitesAllowed;
            return this;
        }

        /**
         * Whether to allow occupants to change subject.
         *
         * @param changeSubjectAllowed Whether to allow occupants to change subject.
         * @return The builder.
         */
        public Builder changeSubjectAllowed(boolean changeSubjectAllowed) {
            this.changeSubjectAllowed = changeSubjectAllowed;
            return this;
        }

        /**
         * Whether to enable public logging of room conversations.
         *
         * @param loggingEnabled Whether to enable public logging of room conversations.
         * @return The builder.
         */
        public Builder loggingEnabled(boolean loggingEnabled) {
            this.loggingEnabled = loggingEnabled;
            return this;
        }

        /**
         * Roles that may retrieve member list.
         *
         * @param rolesThatMayRetrieveMemberList Roles that may retrieve member list.
         * @return The builder.
         */
        public Builder rolesThatMayRetrieveMemberList(Collection<Role> rolesThatMayRetrieveMemberList) {
            this.rolesThatMayRetrieveMemberList = rolesThatMayRetrieveMemberList;
            return this;
        }

        /**
         * Natural language for room discussions.
         *
         * @param language The language.
         * @return The builder.
         */
        public Builder language(Locale language) {
            this.language = language;
            return this;
        }

        /**
         * XMPP URI of associated publish-subcribe node.
         *
         * @param pubsubNode The URI.
         * @return The builder.
         */
        public Builder pubSubNode(URI pubsubNode) {
            this.pubsubNode = pubsubNode;
            return this;
        }

        /**
         * Maximum number of room occupants.
         *
         * @param maxUsers The max users.
         * @return The builder.
         */
        public Builder maxUsers(int maxUsers) {
            this.maxUsers = maxUsers;
            return this;
        }

        /**
         * Whether to make room members-only.
         *
         * @param membersOnly Whether to make room members-only.
         * @return The builder.
         */
        public Builder membersOnly(boolean membersOnly) {
            this.membersOnly = membersOnly;
            return this;
        }

        /**
         * Whether to make room moderated.
         *
         * @param moderated Whether to make room moderated.
         * @return The builder.
         */
        public Builder moderated(boolean moderated) {
            this.moderated = moderated;
            return this;
        }

        /**
         * Whether a password is required to enter.
         *
         * @param passwordProtected Whether a password is required to enter.
         * @return The builder.
         */
        public Builder passwordProtected(boolean passwordProtected) {
            this.passwordProtected = passwordProtected;
            return this;
        }

        /**
         * Whether to make room persistent.
         *
         * @param persistent Whether to maker room persistent.
         * @return The builder.
         */
        public Builder persistent(boolean persistent) {
            this.persistent = persistent;
            return this;
        }

        /**
         * Roles for which presence is broadcast.
         *
         * @param roles Roles for which presence is broadcast.
         * @return The builder.
         */
        public Builder rolesForWhichPresenceIsBroadcast(Collection<Role> roles) {
            this.presenceBroadcast = roles;
            return this;
        }

        /**
         * Whether to allow public searching for room.
         *
         * @param publicRoom Whether to allow public searching for room.
         * @return The builder.
         */
        public Builder publicRoom(boolean publicRoom) {
            this.publicRoom = publicRoom;
            return this;
        }

        /**
         * Full list of room admins.
         *
         * @param admins The admins.
         * @return The builder.
         */
        public Builder administrators(Collection<Jid> admins) {
            this.admins = admins;
            return this;
        }

        /**
         * Short description of room.
         *
         * @param description The description.
         * @return The builder.
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Natural-language room name.
         *
         * @param name The name.
         * @return The builder.
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Full list of room owners.
         *
         * @param owners Full list of room owners.
         * @return The builder.
         */
        public Builder owners(Collection<Jid> owners) {
            this.owners = owners;
            return this;
        }

        /**
         * The room password.
         *
         * @param password The password.
         * @return The builder.
         */
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        /**
         * Roles that may discover real JIDs of occupants.
         *
         * @param roles Roles that may discover real JIDs of occupants.
         * @return The builder.
         */
        public Builder rolesThatMayDiscoverRealJids(Collection<Role> roles) {
            this.whois = roles;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        /**
         * Builds the room configuration.
         *
         * @return The room configuration.
         */
        public RoomConfiguration build() {
            Collection<DataForm.Field> fields = new ArrayDeque<>();
            if (maxHistoryFetch != null) {
                fields.add(DataForm.Field.builder().var(MAX_HISTORY_FETCH).value(maxHistoryFetch).build());
            }
            if (rolesThatMaySendPrivateMessages != null && !rolesThatMaySendPrivateMessages.isEmpty()) {
                fields.add(DataForm.Field.builder().var(ALLOW_PM).value(rolesToValue(rolesThatMaySendPrivateMessages, false)).type(DataForm.Field.Type.LIST_SINGLE).build());
            }
            if (invitesAllowed != null) {
                fields.add(DataForm.Field.builder().var(ALLOW_INVITES).value(invitesAllowed).build());
            }
            if (changeSubjectAllowed != null) {
                fields.add(DataForm.Field.builder().var(CHANGE_SUBJECT).value(changeSubjectAllowed).build());
            }
            if (loggingEnabled != null) {
                fields.add(DataForm.Field.builder().var(ENABLE_LOGGING).value(loggingEnabled).build());
            }
            if (rolesThatMayRetrieveMemberList != null && !rolesThatMayRetrieveMemberList.isEmpty()) {
                fields.add(DataForm.Field.builder().var(GET_MEMBER_LIST).valuesEnum(rolesThatMayRetrieveMemberList).type(DataForm.Field.Type.LIST_MULTI).build());
            }
            if (language != null) {
                fields.add(DataForm.Field.builder().var(LANGUAGE).value(language.toLanguageTag()).build());
            }
            if (pubsubNode != null) {
                fields.add(DataForm.Field.builder().var(PUBSUB).value(pubsubNode.toString()).build());
            }
            if (maxUsers != null) {
                fields.add(DataForm.Field.builder().var(MAX_USERS).value(maxUsers).type(DataForm.Field.Type.LIST_SINGLE).build());
            }
            if (membersOnly != null) {
                fields.add(DataForm.Field.builder().var(MEMBERS_ONLY).value(membersOnly).build());
            }
            if (moderated != null) {
                fields.add(DataForm.Field.builder().var(MODERATED_ROOM).value(moderated).build());
            }
            if (passwordProtected != null) {
                fields.add(DataForm.Field.builder().var(PASSWORD_PROTECTED).value(passwordProtected).build());
            }
            if (persistent != null) {
                fields.add(DataForm.Field.builder().var(PERSISTENT_ROOM).value(persistent).build());
            }
            if (presenceBroadcast != null) {
                fields.add(DataForm.Field.builder().var(PRESENCE_BROADCAST).valuesEnum(presenceBroadcast).type(DataForm.Field.Type.LIST_MULTI).build());
            }
            if (publicRoom != null) {
                fields.add(DataForm.Field.builder().var(PUBLIC_ROOM).value(publicRoom).build());
            }
            if (admins != null && !admins.isEmpty()) {
                fields.add(DataForm.Field.builder().var(ROOM_ADMINS).valuesJid(admins).build());
            }
            if (description != null) {
                fields.add(DataForm.Field.builder().var(ROOM_DESC).value(description).build());
            }
            if (name != null) {
                fields.add(DataForm.Field.builder().var(ROOM_NAME).value(name).build());
            }
            if (owners != null && !owners.isEmpty()) {
                fields.add(DataForm.Field.builder().var(ROOM_OWNERS).valuesJid(owners).build());
            }
            if (password != null) {
                fields.add(DataForm.Field.builder().var(ROOM_SECRET).value(password).build());
            }
            if (whois != null && !whois.isEmpty()) {
                fields.add(DataForm.Field.builder().var(WHOIS).value(rolesToValue(whois, true)).type(DataForm.Field.Type.LIST_SINGLE).build());
            }
            fields(fields).formType(FORM_TYPE).type(DataForm.Type.SUBMIT);
            return new RoomConfiguration(new DataForm(this));
        }
    }
}

