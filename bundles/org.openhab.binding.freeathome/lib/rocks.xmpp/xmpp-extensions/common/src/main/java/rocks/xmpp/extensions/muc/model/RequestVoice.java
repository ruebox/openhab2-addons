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

import java.util.ArrayDeque;
import java.util.Collection;

/**
 * Represents a standardized {@link rocks.xmpp.extensions.data.model.DataForm} with form type {@code http://jabber.org/protocol/muc#request}, which can be used to request voice in a MUC room.
 * <h3>Usage</h3>
 * To wrap an existing {@link rocks.xmpp.extensions.data.model.DataForm} to retrieve standard data from it, use:
 * ```java
 * RequestVoice requestVoice = new RequestVoice(dataForm);
 * ```
 * To build a form:
 * ```java
 * RequestVoice requestVoice = RequestVoice.builder()
 *     .jid(Jid.of("hag66@shakespeare.lit/pda"))
 *     .role(Role.PARTICIPANT)
 *     .roomNick("thirdwitch")
 *     .allowRequest(true)
 *     .build();
 * ```
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0045.html#requestvoice">7.13 Requesting Voice</a>
 * @see <a href="https://xmpp.org/extensions/xep-0045.html#registrar-formtype-request">15.5.2 muc#request FORM_TYPE</a>
 */
public final class RequestVoice {

    private static final String FORM_TYPE = "http://jabber.org/protocol/muc#request";

    /**
     * Requested role
     */
    private static final String ROLE = "muc#role";

    /**
     * User ID
     */
    private static final String JID = "muc#jid";

    /**
     * Room Nickname
     */
    private static final String ROOM_NICK = "muc#roomnick";

    /**
     * Whether to grant voice
     */
    private static final String REQUEST_ALLOW = "muc#request_allow";

    private final DataForm dataForm;

    /**
     * Creates the request voice form.
     *
     * @param dataForm The underlying data form.
     */
    public RequestVoice(DataForm dataForm) {
        this.dataForm = dataForm;
    }

    /**
     * Creates the builder to build a form.
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
    public DataForm getDataForm() {
        return dataForm;
    }

    /**
     * Gets the JID.
     *
     * @return The JID.
     */
    public Jid getJid() {
        return dataForm.findValueAsJid(JID);
    }

    /**
     * Gets the room nick.
     *
     * @return The room nick.
     */
    public String getRoomNick() {
        return dataForm.findValue(ROOM_NICK);
    }

    /**
     * Gets the requested role.
     *
     * @return The requested role.
     */
    public Role getRole() {
        String value = dataForm.findValue(ROLE);
        if (value != null) {
            return Role.valueOf(value.toUpperCase());
        }
        return null;
    }

    /**
     * Indicates, whether the request is approved.
     *
     * @return True, if the request is approved.
     */
    public boolean isRequestAllowed() {
        return dataForm.findValueAsBoolean(REQUEST_ALLOW);
    }

    /**
     * A builder to build a request voice form. The form is of type {@link rocks.xmpp.extensions.data.model.DataForm.Type#SUBMIT} by default.
     */
    public static final class Builder extends DataForm.Builder<Builder> {

        private Role role;

        private Jid jid;

        private String roomNick;

        private Boolean allowRequest;

        private Builder() {
        }

        /**
         * Sets the requested role.
         *
         * @param role The role.
         * @return The builder.
         */
        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        /**
         * Sets the JID.
         *
         * @param jid The JID.
         * @return The builder.
         */
        public Builder jid(Jid jid) {
            this.jid = jid;
            return this;
        }

        /**
         * Sets the room nickname.
         *
         * @param roomNick The room nickname.
         * @return The builder.
         */
        public Builder roomNick(String roomNick) {
            this.roomNick = roomNick;
            return this;
        }

        /**
         * Whether to grant voice.
         *
         * @param allowRequest Whether to grant voice.
         * @return The builder.
         */
        public Builder allowRequest(boolean allowRequest) {
            this.allowRequest = allowRequest;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        /**
         * Builds the request voice form.
         *
         * @return The request voice form.
         */
        public RequestVoice build() {
            Collection<DataForm.Field> fields = new ArrayDeque<>();
            if (role != null) {
                fields.add(DataForm.Field.builder().var(ROLE).value(role.name().toLowerCase()).build());
            }
            if (jid != null) {
                fields.add(DataForm.Field.builder().var(JID).value(jid).build());
            }
            if (roomNick != null) {
                fields.add(DataForm.Field.builder().var(ROOM_NICK).value(roomNick).build());
            }
            if (allowRequest != null) {
                fields.add(DataForm.Field.builder().var(REQUEST_ALLOW).value(allowRequest).build());
            }
            fields(fields).formType(FORM_TYPE).type(DataForm.Type.SUBMIT);
            return new RequestVoice(new DataForm(this));
        }
    }
}
