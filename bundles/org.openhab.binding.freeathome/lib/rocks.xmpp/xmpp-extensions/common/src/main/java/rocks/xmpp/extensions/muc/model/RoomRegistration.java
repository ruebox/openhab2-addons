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

import rocks.xmpp.extensions.data.model.DataForm;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Collection;

/**
 * Represents a standardized {@link rocks.xmpp.extensions.data.model.DataForm} with form type {@code http://jabber.org/protocol/muc#register}, which can be used to register with a MUC room.
 * <h3>Usage</h3>
 * To wrap an existing {@link rocks.xmpp.extensions.data.model.DataForm} to retrieve standard data from it, use:
 * ```java
 * RoomRegistration roomRegistration = new RoomRegistration(dataForm);
 * ```
 * To build a form:
 * ```java
 * RoomRegistration roomRegistration = RoomRegistration.builder()
 *     .allowRegister(true)
 *     .email("hag66@witchesonline")
 *     .familyName("Entwhistle-Throckmorton")
 *     .givenName("Brunhilde")
 *     .faqEntry("Just another witch.")
 *     .nickname("thirdwitch")
 *     .webPage(new URL("http://witchesonline/~hag66/"))
 *     .build();
 * ```
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0045.html#register">7.10 Registering with a Room</a>
 * @see <a href="https://xmpp.org/extensions/xep-0045.html#registrar-formtype-register">15.5.1 muc#register FORM_TYPE</a>
 */
public final class RoomRegistration {

    private static final String FORM_TYPE = "http://jabber.org/protocol/muc#register";

    /**
     * Allow this person to register with the room?
     */
    private static final String REGISTER_ALLOW = "muc#register_allow";

    /**
     * Email Address
     */
    private static final String EMAIL = "muc#register_email";

    /**
     * FAQ Entry
     */
    private static final String FAQ_ENTRY = "muc#register_faqentry";

    /**
     * Given Name
     */
    private static final String GIVEN_NAME = "muc#register_first";

    /**
     * Family Name
     */
    private static final String FAMILY_NAME = "muc#register_last";

    /**
     * Desired Nickname
     */
    private static final String ROOM_NICK = "muc#register_roomnick";

    /**
     * A Web Page
     */
    private static final String URL = "muc#register_url";

    private final DataForm dataForm;

    public RoomRegistration(DataForm dataForm) {
        this.dataForm = dataForm;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets the e-mail address.
     *
     * @return The e-mail address.
     */
    public String getEmail() {
        return dataForm.findValue(EMAIL);
    }

    /**
     * Gets the family name.
     *
     * @return The family name.
     */
    public String getFamilyName() {
        return dataForm.findValue(FAMILY_NAME);
    }

    /**
     * Gets the given name.
     *
     * @return The given name.
     */
    public String getGivenName() {
        return dataForm.findValue(GIVEN_NAME);
    }

    /**
     * Gets the desired room nick.
     *
     * @return The room nick.
     */
    public String getRoomNick() {
        return dataForm.findValue(ROOM_NICK);
    }

    /**
     * Gets an URL to a web page.
     *
     * @return The URL.
     */
    public URL getWebPage() {
        String value = dataForm.findValue(URL);
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
     * Gets the FAQ entry.
     *
     * @return The FAQ entry.
     */
    public String getFaqEntry() {
        return dataForm.findValue(FAQ_ENTRY);
    }

    /**
     * Indicates, whether the registration request is approved.
     *
     * @return True, if the registration request is approved.
     */
    public boolean isRegisterAllowed() {
        return dataForm.findValueAsBoolean(REGISTER_ALLOW);
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
     * A builder to build MUC registration forms. The form is of type {@link rocks.xmpp.extensions.data.model.DataForm.Type#SUBMIT} by default.
     */
    public static final class Builder extends DataForm.Builder<Builder> {
        private Boolean allowRegister;

        private String email;

        private String faqEntry;

        private String givenName;

        private String familyName;

        private String nickname;

        private URL webPage;

        private Builder() {
        }

        /**
         * Whether to allow registration with the room.
         *
         * @param allowRegister Whether to allow registration with the room.
         * @return THe builder.
         */
        public Builder allowRegister(boolean allowRegister) {
            this.allowRegister = allowRegister;
            return this;
        }

        /**
         * The email address.
         *
         * @param email The email address.
         * @return The builder.
         */
        public Builder email(String email) {
            this.email = email;
            return this;
        }

        /**
         * The FAQ entry.
         *
         * @param faqEntry The FAQ entry.
         * @return The builder.
         */
        public Builder faqEntry(String faqEntry) {
            this.faqEntry = faqEntry;
            return this;
        }

        /**
         * The given name.
         *
         * @param givenName The given name.
         * @return The builder.
         */
        public Builder givenName(String givenName) {
            this.givenName = givenName;
            return this;
        }

        /**
         * The family name.
         *
         * @param familyName The family name.
         * @return The builder.
         */
        public Builder familyName(String familyName) {
            this.familyName = familyName;
            return this;
        }

        /**
         * The desired nickname.
         *
         * @param nickname The nickname.
         * @return The builder.
         */
        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        /**
         * The web page.
         *
         * @param webPage The web page.
         * @return The builder.
         */
        public Builder webPage(URL webPage) {
            this.webPage = webPage;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        /**
         * Builds the registration form.
         *
         * @return The registration form.
         */
        public RoomRegistration build() {
            Collection<DataForm.Field> fields = new ArrayDeque<>();
            if (allowRegister != null) {
                fields.add(DataForm.Field.builder().var(REGISTER_ALLOW).value(allowRegister).build());
            }
            if (email != null) {
                fields.add(DataForm.Field.builder().var(EMAIL).value(email).build());
            }
            if (faqEntry != null) {
                fields.add(DataForm.Field.builder().var(FAQ_ENTRY).value(faqEntry).type(DataForm.Field.Type.TEXT_MULTI).build());
            }
            if (givenName != null) {
                fields.add(DataForm.Field.builder().var(GIVEN_NAME).value(givenName).build());
            }
            if (familyName != null) {
                fields.add(DataForm.Field.builder().var(FAMILY_NAME).value(familyName).build());
            }
            if (nickname != null) {
                fields.add(DataForm.Field.builder().var(ROOM_NICK).value(nickname).build());
            }
            if (webPage != null) {
                fields.add(DataForm.Field.builder().var(URL).value(webPage.toString()).build());
            }
            fields(fields).formType(FORM_TYPE).type(DataForm.Type.SUBMIT);
            return new RoomRegistration(new DataForm(this));
        }
    }
}
