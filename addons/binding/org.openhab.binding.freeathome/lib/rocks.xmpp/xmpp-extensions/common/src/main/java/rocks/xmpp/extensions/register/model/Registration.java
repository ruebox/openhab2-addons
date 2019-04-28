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

package rocks.xmpp.extensions.register.model;

import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.oob.model.x.OobX;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.net.URL;

/**
 * The implementation of the {@code <query/>} element in the {@code jabber:iq:register} namespace.
 * <h3>Usage</h3>
 * To request the registration form:
 * ```java
 * Registration registration = Registration.empty();
 * ```
 * To create a registration for submission:
 * ```java
 * Registration registration = Registration.builder()
 *     .username("user")
 *     .password("pass")
 *     .familyName("Family Name")
 *     .givenName("Given Name")
 *     .nickname("Nick Name")
 *     .email("E-Mail")
 *     .build();
 * ```
 * To create a registration for removing the current account:
 * ```java
 * Registration registration = Registration.remove();
 * ```
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0077.html">XEP-0077: In-Band Registration</a>
 * @see <a href="https://xmpp.org/extensions/xep-0077.html#schemas-register">XML Schema</a>
 */
@XmlRootElement(name = "query")
public final class Registration {

    /**
     * jabber:iq:register
     */
    public static final String NAMESPACE = "jabber:iq:register";

    private final String registered;

    private final String instructions;

    private final String username;

    private final String nick;

    private final String password;

    private final String name;

    @XmlElement(name = "first")
    private final String givenName;

    @XmlElement(name = "last")
    private final String familyName;

    private final String email;

    @XmlElement(name = "address")
    private final String street;

    private final String city;

    private final String region;

    @XmlElement(name = "zip")
    private final String postalCode;

    @XmlElement(name = "phone")
    private final String telephone;

    private final URL url;

    private final String date;

    private final String remove;

    @XmlElementRef
    private final DataForm dataForm;

    @XmlElementRef
    private final OobX oobX;

    private Registration() {
        this(false);
    }

    private Registration(Builder builder) {
        this.registered = builder.registered ? "" : null;
        this.instructions = builder.instructions;
        this.username = builder.username;
        this.nick = builder.nickname;
        this.password = builder.password;
        this.name = builder.name;
        this.givenName = builder.givenName;
        this.familyName = builder.familyName;
        this.email = builder.email;
        this.street = builder.street;
        this.city = builder.city;
        this.region = builder.region;
        this.postalCode = builder.postalCode;
        this.telephone = builder.phone;
        this.url = builder.url;
        this.date = builder.date;
        this.dataForm = builder.dataForm;
        this.oobX = builder.oobX;
        this.remove = null;
    }

    /**
     * Creates an empty registration element to request the registration.
     */
    private Registration(boolean remove) {
        this.registered = null;
        this.instructions = null;
        this.username = null;
        this.nick = null;
        this.password = null;
        this.name = null;
        this.givenName = null;
        this.familyName = null;
        this.email = null;
        this.street = null;
        this.city = null;
        this.region = null;
        this.postalCode = null;
        this.telephone = null;
        this.url = null;
        this.date = null;
        this.dataForm = null;
        this.oobX = null;
        this.remove = remove ? "" : null;
    }

    /**
     * Creates a registration element with an {@code <remove/>} element used to indicate account removal.
     *
     * @return The registration.
     */
    public static Registration remove() {
        return new Registration(true);
    }

    /**
     * Creates an empty registration element for requesting the registration form.
     *
     * @return The registration.
     */
    public static Registration empty() {
        return new Registration(false);
    }

    /**
     * Creates the builder for an registration.
     *
     * @return The registration.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Indicates whether the account is already registered.
     *
     * @return True, if the account is already registered.
     */
    public boolean isRegistered() {
        return registered != null;
    }

    /**
     * Gets the registration instructions.
     *
     * @return The instructions.
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * Gets the username.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the nickname.
     *
     * @return The nickname.
     */
    public String getNickname() {
        return nick;
    }

    /**
     * Gets the password.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the full name.
     *
     * @return The full name.
     */
    public String getFullName() {
        return name;
    }

    /**
     * Gets the given name.
     *
     * @return The given name.
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * Gets the familyName name.
     *
     * @return The familyName name.
     */
    public String getLastName() {
        return familyName;
    }

    /**
     * Gets the email street.
     *
     * @return The email street.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the street.
     *
     * @return The street.
     */
    public String getStreet() {
        return street;
    }

    /**
     * Gets the city.
     *
     * @return The city.
     */
    public String getCity() {
        return city;
    }

    /**
     * Gets the region.
     *
     * @return The region.
     */
    public String getRegion() {
        return region;
    }

    /**
     * Gets the postal code.
     *
     * @return The postal code.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Gets the telephone number.
     *
     * @return The telephone number.
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Gets the URL to a web page.
     *
     * @return The URL.
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Gets some date (e.g. birth date, hire date, sign-up date).
     *
     * @return The date.
     */
    public String getDate() {
        return date;
    }

    /**
     * Gets the extended registration form.
     *
     * @return The registration form.
     */
    public DataForm getRegistrationForm() {
        return dataForm;
    }

    /**
     * Gets the web registration URL, if any.
     *
     * @return The web registration URL or null.
     * @deprecated Use {@link #getWebRegistrationUri()}.
     */
    @Deprecated
    public URL getWebRegistrationUrl() {
        return oobX != null ? oobX.getUrl() : null;
    }

    /**
     * Gets the web registration URI, if any.
     *
     * @return The web registration URI or null.
     */
    public URI getWebRegistrationUri() {
        return oobX != null ? oobX.getUri() : null;
    }

    /**
     * Indicates whether the account is to be removed.
     *
     * @return True, if the account is to be removed.
     */
    public boolean isRemove() {
        return remove != null;
    }

    /**
     * A builder to build a registration.
     */
    public static final class Builder {

        private String instructions;

        private String username;

        private String password;

        private String nickname;

        private String name;

        private String givenName;

        private String familyName;

        private String email;

        private String street;

        private String city;

        private String region;

        private String postalCode;

        private String phone;

        private URL url;

        private String date;

        private DataForm dataForm;

        private OobX oobX;

        private boolean registered;

        private Builder() {
        }

        /**
         * Sets the instructions.
         *
         * @param instructions The instructions.
         * @return The builder.
         */
        public Builder instructions(String instructions) {
            this.instructions = instructions;
            return this;
        }

        /**
         * Sets the username.
         *
         * @param username The username.
         * @return The builder.
         */
        public Builder username(String username) {
            this.username = username;
            return this;
        }

        /**
         * Sets the password.
         *
         * @param password The password.
         * @return The builder.
         */
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        /**
         * Sets the nickname.
         *
         * @param nickname The nickname.
         * @return The builder.
         */
        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        /**
         * Sets the name.
         *
         * @param name The name.
         * @return The builder.
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the given name.
         *
         * @param givenName The given name.
         * @return The builder.
         */
        public Builder givenName(String givenName) {
            this.givenName = givenName;
            return this;
        }

        /**
         * Sets the family name.
         *
         * @param familyName The family name.
         * @return The builder.
         */
        public Builder familyName(String familyName) {
            this.familyName = familyName;
            return this;
        }

        /**
         * Sets the email.
         *
         * @param email The email address.
         * @return The builder.
         */
        public Builder email(String email) {
            this.email = email;
            return this;
        }

        /**
         * The street.
         *
         * @param street The street.
         * @return The builder.
         */
        public Builder street(String street) {
            this.street = street;
            return this;
        }

        /**
         * Sets the city.
         *
         * @param city The city.
         * @return The builder.
         */
        public Builder city(String city) {
            this.city = city;
            return this;
        }

        /**
         * Sets the region, e.g. state.
         *
         * @param region The region.
         * @return The builder.
         */
        public Builder region(String region) {
            this.region = region;
            return this;
        }

        /**
         * Sets the postal code.
         *
         * @param postalCode The postal code.
         * @return The builder.
         */
        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        /**
         * Sets the phone.
         *
         * @param phone The phone.
         * @return The builder.
         */
        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        /**
         * Sets the URL.
         *
         * @param url The URL.
         * @return The builder.
         */
        public Builder url(URL url) {
            this.url = url;
            return this;
        }

        /**
         * Sets the date, e.g. birth date.
         *
         * @param date The date.
         * @return The builder.
         */
        public Builder date(String date) {
            this.date = date;
            return this;
        }

        /**
         * Sets the extended registration form.
         *
         * @param dataForm The data form.
         * @return The builder.
         */
        public Builder registrationForm(DataForm dataForm) {
            this.dataForm = dataForm;
            return this;
        }

        /**
         * Sets the web registration URI.
         *
         * @param uri The uri.
         * @return The builder.
         */
        public Builder webRegistration(URI uri) {
            this.oobX = new OobX(uri);
            return this;
        }

        /**
         * Marks the registration as registered.
         *
         * @param registered True, if registered.
         * @return The builder.
         */
        public Builder registered(boolean registered) {
            this.registered = registered;
            return this;
        }

        /**
         * Builds the registration.
         *
         * @return The builder.
         */
        public Registration build() {
            return new Registration(this);
        }
    }
}
