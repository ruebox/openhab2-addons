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

package rocks.xmpp.extensions.vcard.temp.model;

import rocks.xmpp.addr.Jid;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The implementation of the {@code <vCard/>} element in the {@code vcard-temp} namespace.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0054.html">XEP-0054: vcard-temp</a>
 * @see <a href="https://xmpp.org/extensions/xep-0054.html#dtd">DTD</a>
 */
@XmlRootElement(name = "vCard")
public final class VCard {

    /**
     * vcard-temp
     */
    public static final String NAMESPACE = "vcard-temp";

    /**
     * To specify the version of the vCard specification used to format this vCard.
     * Some Jabber implementations add a 'version' attribute to the {@code <vCard/>} element, with the value set at "2.0" or "3.0". The DTD is incorrect, and the examples in draft-dawson-vcard-xml-dtd-01 clearly show that version information is to be included by means of a 'version' attribute, not the {@code <VERSION/>} element as defined in the DTD. However, to conform to draft-dawson-vcard-xml-dtd-01, the value should be "3.0", not "2.0".
     */
    @XmlAttribute
    private static final String VERSION = "3.0";

    /**
     * To specify the formatted text corresponding to the name of the object the vCard represents.
     */
    @XmlElement(name = "FN")
    private String formattedName;

    /**
     * To specify the components of the name of the object the vCard represents.
     */
    @XmlElement(name = "N")
    private Name name;

    /**
     * To specify the text corresponding to the nickname of the object the vCard represents.
     */
    @XmlElement(name = "NICKNAME")
    private String nickname;

    /**
     * To specify an image or photograph information that annotates some aspect of the object the vCard represents.
     */
    @XmlElement(name = "PHOTO")
    private Image photo;

    /**
     * To specify the birth date of the object the vCard represents.
     */
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    @XmlElement(name = "BDAY")
    private LocalDate birthday;

    /**
     * To specify a uniform resource locator associated with the object that the vCard refers to.
     */
    @XmlElement(name = "URL")
    private URL url;

    /**
     * To specify the organizational name and units associated with the vCard.
     */
    @XmlElement(name = "ORG")
    private Organization organization;

    /**
     * To specify the components of the delivery address for the vCard object.
     */
    @XmlElement(name = "ADR")
    private List<Address> addresses;

    /**
     * To specify the telephone number for telephony
     * communication with the object the vCard represents.
     */
    @XmlElement(name = "TEL")
    private List<TelephoneNumber> telephoneNumbers;

    /**
     * To specify the electronic mail address for
     * communication with the object the vCard represents.
     */
    @XmlElement(name = "EMAIL")
    private List<Email> emails;

    /**
     * To specify the formatted text corresponding to delivery address of the object the vCard represents.
     */
    @XmlElement(name = "LABEL")
    private List<AddressLabel> labels;

    @XmlElement(name = "JABBERID")
    private Jid jid;

    /**
     * To specify the type of electronic mail software that is used by the individual associated with the vCard.
     */
    @XmlElement(name = "MAILER")
    private String mailer;

    /**
     * To specify information related to the time zone of the object the vCard represents.
     */
    @XmlElement(name = "TZ")
    private String timezone;

    /**
     * To specify information related to the global positioning of the object the vCard represents.
     */
    @XmlElement(name = "GEO")
    private Geo geo;

    /**
     * To specify the job title, functional position or function of the object the vCard represents.
     */
    @XmlElement(name = "TITLE")
    private String title;

    /**
     * To specify information concerning the role, occupation, or business category of the object the vCard represents.
     */
    @XmlElement(name = "ROLE")
    private String role;

    /**
     * To specify information about another person who will act on behalf of the individual or resource associated with the vCard.
     */
    @XmlElement(name = "AGENT")
    private String agent;

    /**
     * To specify a graphic image of a logo associated with the object the vCard represents.
     */
    @XmlElement(name = "LOGO")
    private Image logo;

    /**
     * To specify application category information about the vCard.
     */
    @XmlElementWrapper(name = "CATEGORIES")
    @XmlElement(name = "KEYWORD")
    private List<String> categories;

    /**
     * To specify supplemental information or a comment that is associated with the vCard.
     */
    @XmlElement(name = "NOTE")
    private String note;

    /**
     * To specify the identifier for the product that created the vCard object.
     */
    @XmlElement(name = "PRODID")
    private String productId;

    /**
     * To specify revision information about the current vCard.
     */
    @XmlElement(name = "REV")
    private Instant revision;

    /**
     * To specify the family name or given name text to be used for national-language-specific sorting of the FN and N types.
     */
    @XmlElement(name = "SORT-STRING")
    private String sortString;

    /**
     * To specify a digital sound content information that annotates some aspect of the vCard. By default this type is used to
     * specify the proper pronunciation of the name type value of the vCard.
     */
    @XmlElement(name = "SOUND")
    private String sound;

    /**
     * To specify a value that represents a globally unique identifier corresponding to the individual or resource associated
     * with the vCard.
     */
    @XmlElement(name = "UID")
    private String uid;

    /**
     * Type purpose: To specify a public key or authentication certificate
     * associated with the object that the vCard represents.
     */
    @XmlElement(name = "KEY")
    private Key key;

    @XmlJavaTypeAdapter(ClassificationAdapter.class)
    @XmlElement(name = "CLASS")
    private Classification classification;

    @XmlElement(name = "DESC")
    private String desc;

    /**
     * Gets the formatted text corresponding to the name.
     *
     * @return The formatted name.
     * @see #setFormattedName(String)
     */
    public String getFormattedName() {
        return formattedName;
    }

    /**
     * Sets the formatted name.
     *
     * @param formattedName The formatted name.
     * @see #getFormattedName()
     */
    public void setFormattedName(String formattedName) {
        this.formattedName = formattedName;
    }

    /**
     * Gets the name.
     *
     * @return The name.
     * @see #setName(VCard.Name)
     */
    public Name getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name The name.
     * @see #getName()
     */
    public void setName(Name name) {
        this.name = name;
    }

    /**
     * Gets the nickname.
     *
     * @return The nickname.
     * @see #setNickname(String)
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets the nickname.
     *
     * @param nickname The nickname.
     * @see #getNickname()
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Gets the photo.
     *
     * @return Either a URL to a photo or a base64 encoded photo.
     * @see #setPhoto(VCard.Image)
     */
    public Image getPhoto() {
        return photo;
    }

    /**
     * Sets the photo.
     *
     * @param photo The photo.
     * @see #getPhoto()
     */
    public void setPhoto(Image photo) {
        this.photo = photo;
    }

    /**
     * Gets the birthday.
     *
     * @return The birth day.
     * @see #setBirthday(LocalDate)
     */
    public LocalDate getBirthday() {
        return birthday;
    }

    /**
     * Sets the birthday.
     *
     * @param birthday The birthday.
     * @see #getBirthday()
     */
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    /**
     * Gets an URL associated with the object that the vCard refers to.
     *
     * @return The URL.
     * @see #setUrl(java.net.URL)
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Sets an URL.
     *
     * @param url The URL.
     * @see #getUrl()
     */
    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * Gets the organization.
     *
     * @return The organization.
     * @see #setOrganization(VCard.Organization)
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets the organization.
     *
     * @param organization The organization.
     * @see #getOrganization()
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * Gets the addresses.
     *
     * @return The addresses.
     */
    public List<Address> getAddresses() {
        if (addresses == null) {
            addresses = new ArrayList<>();
        }
        return addresses;
    }

    /**
     * Gets the telephone numbers.
     *
     * @return The telephone numbers.
     */
    public List<TelephoneNumber> getTelephoneNumbers() {
        if (telephoneNumbers == null) {
            telephoneNumbers = new ArrayList<>();
        }
        return telephoneNumbers;
    }

    /**
     * Gets the email addresses.
     *
     * @return The email addresses.
     */
    public List<Email> getEmails() {
        if (emails == null) {
            emails = new ArrayList<>();
        }
        return emails;
    }

    /**
     * Gets the labels.
     *
     * @return The labels.
     */
    public List<AddressLabel> getLabels() {
        if (labels == null) {
            labels = new ArrayList<>();
        }
        return labels;
    }

    /**
     * Gets the JID.
     *
     * @return The JID.
     * @see #setJid(Jid)
     */
    public Jid getJid() {
        return jid;
    }

    /**
     * Sets the JID.
     *
     * @param jid The JID.
     * @see #getJid()
     */
    public void setJid(Jid jid) {
        this.jid = jid;
    }

    /**
     * Gets the type of electronic mail software that is used by the individual associated with the vCard.
     *
     * @return The mailer.
     * @see #setMailer(String)
     */
    public String getMailer() {
        return mailer;
    }

    /**
     * Sets the mailer.
     *
     * @param mailer The mailer.
     * @see #getMailer()
     */
    public void setMailer(String mailer) {
        this.mailer = mailer;
    }

    /**
     * Gets the time zone.
     *
     * @return The time zone.
     * @see #setTimeZone(String)
     */
    public String getTimeZone() {
        return timezone;
    }

    /**
     * Sets the time zone.
     *
     * @param timezone The time zone.
     * @see #getTimeZone()
     */
    public void setTimeZone(String timezone) {
        this.timezone = timezone;
    }

    /**
     * Gets information related to the global positioning of the object the vCard represents.
     *
     * @return The geo location.
     * @see #setGeo(VCard.Geo)
     */
    public Geo getGeo() {
        return geo;
    }

    /**
     * Sets the geo location.
     *
     * @param geo The geo location.
     * @see #getGeo()
     */
    public void setGeo(Geo geo) {
        this.geo = geo;
    }

    /**
     * Gets the job title, functional position or function of the object the vCard represents.
     *
     * @return The title.
     * @see #setTitle(String)
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title The title.
     * @see #getTitle()
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets information concerning the role, occupation, or business category of the object the vCard represents.
     *
     * @return The role.
     * @see #setRole(String)
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role.
     *
     * @param role The role.
     * @see #getRole()
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Gets information about another person who will act on behalf of the individual or resource associated with the vCard.
     *
     * @return The agent.
     * @see #setAgent(String)
     */
    public String getAgent() {
        return agent;
    }

    /**
     * Sets the agent.
     *
     * @param agent The agent.
     * @see #getAgent()
     */
    public void setAgent(String agent) {
        this.agent = agent;
    }

    /**
     * Gets the logo.
     *
     * @return Either an URL to an image or a base64 encoded image.
     * @see #setLogo(VCard.Image)
     */
    public Image getLogo() {
        return logo;
    }

    /**
     * Sets the logo.
     *
     * @param logo The logo.
     * @see #getLogo()
     */
    public void setLogo(Image logo) {
        this.logo = logo;
    }

    /**
     * Gets application category information about the vCard.
     *
     * @return The categories.
     */
    public List<String> getCategories() {
        if (categories == null) {
            categories = new ArrayList<>();
        }
        return categories;
    }

    /**
     * Gets supplemental information or a comment that is associated with the vCard.
     *
     * @return The note.
     * @see #setNote(String)
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets a note.
     *
     * @param note The note.
     * @see #getNote()
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Gets the identifier for the product that created the vCard object.
     *
     * @return The product id.
     * @see #setProductId(String)
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Sets the product id.
     *
     * @param productId The product id.
     * @see #getProductId()
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * Gets revision information about the current vCard.
     *
     * @return The revision.
     * @see #setRevision(Instant)
     */
    public Instant getRevision() {
        return revision;
    }

    /**
     * Sets revision information.
     *
     * @param revision The revision information.
     * @see #getRevision()
     */
    public void setRevision(Instant revision) {
        this.revision = revision;
    }

    /**
     * Gets the sort string, specifying the family name or given name text to be used for national-language-specific sorting of the {@linkplain #getFormattedName() FN} and {@linkplain Name N} types.
     *
     * @return The sort string.
     * @see #setSortString(String)
     */
    public String getSortString() {
        return sortString;
    }

    /**
     * Sets the sort string.
     *
     * @param sortString The sort string.
     * @see #getSortString()
     */
    public void setSortString(String sortString) {
        this.sortString = sortString;
    }

    /**
     * Gets the sound, zo specify a digital sound content information that annotates some aspect of the vCard. By default this type is used to
     * specify the proper pronunciation of the name type value of the vCard.
     *
     * @return The sound, either as pronunciation string, as URL or as base64 encoded binary.
     * @see #setSound(String)
     */
    public String getSound() {
        return sound;
    }

    /**
     * Sets the sound.
     *
     * @param sound The sound.
     * @see #getSound()
     */
    public void setSound(String sound) {
        this.sound = sound;
    }

    /**
     * Gets the UID, to specify a value that represents a globally unique identifier corresponding to the individual or resource associated
     * with the vCard.
     *
     * @return The UID.
     * @see #setUid(String)
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets the UID.
     *
     * @param uid The UID.
     * @see #getUid()
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Gets the authentication credential or encryption key.
     *
     * @return The key.
     * @see #setKey(VCard.Key)
     */
    public Key getKey() {
        return key;
    }

    /**
     * Sets the authentication credential or encryption key.
     *
     * @param key The key.
     * @see #getKey()
     */
    public void setKey(Key key) {
        this.key = key;
    }

    /**
     * Gets free-form descriptive text.
     *
     * @return The descriptive text.
     * @see #setDesc(String)
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets free-form descriptive text.
     *
     * @param desc The descriptive text.
     * @see #getDesc()
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public enum Classification {
        PUBLIC,
        PRIVATE,
        CONFIDENTIAL
    }

    /**
     * Stores information related to the global positioning.
     */
    public static final class Geo {

        @XmlElement(name = "LAT")
        private Double latitude;

        @XmlElement(name = "LON")
        private Double longitude;

        private Geo() {
        }

        /**
         * @param latitude  The latitude.
         * @param longitude The longitude.
         */
        public Geo(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        /**
         * Gets the latitude (represents the location north and south of the equator).
         *
         * @return The latitude.
         * @see #setLatitude(double)
         */
        public double getLatitude() {
            return latitude != null ? latitude : 0;
        }

        /**
         * Sets the latitude.
         *
         * @param latitude The latitude.
         * @see #getLatitude()
         */
        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        /**
         * Gets the longitude (represents the location east and west of the prime meridian).
         *
         * @return The longitude.
         * @see #setLongitude(double)
         */
        public double getLongitude() {
            return longitude != null ? longitude : 0;
        }

        /**
         * Sets the longitude.
         *
         * @param longitude The longitude.
         * @see #getLongitude()
         */
        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }

    /**
     * Represents structured address data.
     */
    public static final class Address extends AbstractAddress {
        // The default is "TYPE=intl,postal,parcel,work".

        @XmlElement(name = "POBOX")
        private String postOfficeBox;

        @XmlElement(name = "EXTADD")
        private String extendedAddress;

        @XmlElement(name = "STREET")
        private String street;

        @XmlElement(name = "LOCALITY")
        private String city;

        @XmlElement(name = "REGION")
        private String region;

        @XmlElement(name = "PCODE")
        private String postalCode = "";

        @XmlElement(name = "CTRY")
        private String country;

        public Address() {
            super(true, false, true, true, true, true);
        }

        /**
         * Creates a address with all possible values.
         *
         * @param preferred       True, if this is the preferred address.
         * @param home            If this is a home address.
         * @param work            If it is a work address.
         * @param postal          If it is a postal address.
         * @param parcel          If it is a parcel address.
         * @param international   If it is a international address.
         * @param postOfficeBox   The post office box.
         * @param extendedAddress The extended address.
         * @param street          The street.
         * @param city            The city.
         * @param region          The region.
         * @param postalCode      The postal code.
         * @param country         The country.
         */
        public Address(boolean preferred, boolean home, boolean work, boolean postal, boolean parcel, boolean international, String postOfficeBox, String extendedAddress, String street, String city, String region, String postalCode, String country) {
            super(preferred, home, work, postal, parcel, international);
            this.postOfficeBox = postOfficeBox;
            this.extendedAddress = extendedAddress;
            this.street = street;
            this.city = city;
            this.region = region;
            this.postalCode = postalCode;
            this.country = country;
        }

        /**
         * Creates a postal home address (marked as preferred).
         *
         * @param street     The street.
         * @param city       The city.
         * @param postalCode The postal code.
         * @param country    The country.
         */
        public Address(String street, String city, String postalCode, String country) {
            super(true, true, false, true, true, true);
            this.street = street;
            this.city = city;
            this.postalCode = postalCode;
            this.country = country;
        }

        /**
         * Gets the post office box.
         *
         * @return The post office box.
         * @see #setPostOfficeBox(String)
         */
        public String getPostOfficeBox() {
            return postOfficeBox;
        }

        /**
         * Sets the post office box.
         *
         * @param postOfficeBox The post office box.
         * @see #getPostOfficeBox()
         */
        public void setPostOfficeBox(String postOfficeBox) {
            this.postOfficeBox = postOfficeBox;
        }

        /**
         * Gets the extended address.
         *
         * @return The extended address.
         * @see #setExtendedAddress(String)
         */
        public String getExtendedAddress() {
            return extendedAddress;
        }

        /**
         * Sets the extended address.
         *
         * @param extendedAddress The extended address.
         * @see #getExtendedAddress()
         */
        public void setExtendedAddress(String extendedAddress) {
            this.extendedAddress = extendedAddress;
        }

        /**
         * Gets the street.
         *
         * @return The street.
         * @see #setStreet(String)
         */
        public String getStreet() {
            return street;
        }

        /**
         * Sets the street.
         *
         * @param street The street.
         * @see #getStreet()
         */
        public void setStreet(String street) {
            this.street = street;
        }

        /**
         * Gets the city.
         *
         * @return The city.
         * @see #setCity(String)
         */
        public String getCity() {
            return city;
        }

        /**
         * Sets the city.
         *
         * @param city The city.
         * @see #getCity()
         */
        public void setCity(String city) {
            this.city = city;
        }

        /**
         * Gets the region.
         *
         * @return The region.
         * @see #setRegion(String)
         */
        public String getRegion() {
            return region;
        }

        /**
         * Sets the region.
         *
         * @param region The region.
         * @see #getRegion()
         */
        public void setRegion(String region) {
            this.region = region;
        }

        /**
         * Gets the postal code.
         *
         * @return The postal code.
         * @see #setPostalCode(String)
         */
        public String getPostalCode() {
            return postalCode;
        }

        /**
         * Sets the postal code.
         *
         * @param postalCode The postal code.
         * @see #getPostalCode()
         */
        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        /**
         * Gets the country.
         *
         * @return The country.
         * @see #setCountry(String)
         */
        public String getCountry() {
            return country;
        }

        /**
         * Sets the country.
         *
         * @param country The country.
         * @see #getCountry()
         */
        public void setCountry(String country) {
            this.country = country;
        }
    }

    /**
     * Represents the formatted text corresponding to a delivery address.
     */
    public static class AddressLabel extends AbstractAddress {

        @XmlElement(name = "LINE")
        private List<String> line;

        private AddressLabel() {
            this(false, false, true, true, true, true);
        }

        /**
         * Creates a default address label for an international, postal, parcel and work address.
         *
         * @param addressLine The address label lines.
         */
        public AddressLabel(String... addressLine) {
            // The default is "TYPE=intl,postal,parcel,work"
            this(false, false, true, true, true, true, addressLine);
        }

        /**
         * Creates an address label.
         *
         * @param preferred     If it is a preferred address.
         * @param home          If it is a home address.
         * @param work          If it is a work address.
         * @param postal        If it is a postal address.
         * @param parcel        If it is a parcel address.
         * @param international If it is a international address.
         * @param addressLine   The address label lines.
         */
        public AddressLabel(boolean preferred, boolean home, boolean work, boolean postal, boolean parcel, boolean international, String... addressLine) {
            super(preferred, home, work, postal, parcel, international);
            // The DTD requires at least one LINE element.
            if (addressLine.length == 0 || addressLine[0] == null) {
                throw new IllegalArgumentException("At least one address line must be set.");
            }
            line = Arrays.asList(addressLine);
        }
    }

    /**
     * The abstract base class for {@link Address} and {@link AddressLabel}.
     */
    @XmlTransient
    private abstract static class AbstractAddress extends ContactData {

        @XmlElement(name = "POSTAL")
        private Boolean postal;

        @XmlElement(name = "PARCEL")
        private Boolean parcel;

        @XmlElement(name = "INTL")
        private Boolean international;

        @XmlElement(name = "DOM")
        private Boolean domestic;

        /**
         * Creates an abstract address.
         *
         * @param preferred     If it is a preferred address.
         * @param home          If it is a home address.
         * @param work          If it is a work address.
         * @param postal        If it is a postal address.
         * @param parcel        If it is a parcel address.
         * @param international If it is a international address.
         */
        public AbstractAddress(boolean preferred, boolean home, boolean work, boolean postal, boolean parcel, boolean international) {
            super(preferred, home, work);
            this.postal = postal;
            this.parcel = parcel;
            setInternational(international);
        }

        /**
         * Indicates whether this address is domestic or international.
         *
         * @return True if the address is international; false if it is domestic.
         */
        public boolean isInternational() {
            return international != null && international && domestic != null && !domestic;
        }

        /**
         * Sets, whether the address is international.
         *
         * @param international True, if is is international; false if it is domestic.
         */
        public void setInternational(boolean international) {
            this.international = international;
            this.domestic = !international;
        }

        /**
         * Indicates a postal delivery address.
         *
         * @return If it is a postal address.
         */
        public boolean isPostal() {
            return postal != null && postal;
        }

        /**
         * Sets, whether this is a postal delivery address.
         *
         * @param postal If it is a postal address.
         */
        public void setPostal(boolean postal) {
            this.postal = postal;
        }

        /**
         * Indicates a parcel delivery address.
         *
         * @return If it is a parcel address.
         */
        public boolean isParcel() {
            return parcel != null && parcel;
        }

        /**
         * Sets, whether this is a parcel delivery address.
         *
         * @param parcel If it is a parcel address.
         */
        public void setParcel(boolean parcel) {
            this.parcel = parcel;
        }
    }

    /**
     * The abstract base class for {@link Address} and {@link TelephoneNumber}.
     */
    @XmlTransient
    private abstract static class ContactData extends Preferable {
        @XmlElement(name = "HOME")
        private Boolean home;

        @XmlElement(name = "WORK")
        private Boolean work;

        /**
         * @param preferred If this is a preferred contact data.
         * @param home      If this is a home contact data.
         * @param work      If this is a work contact data.
         */
        protected ContactData(boolean preferred, boolean home, boolean work) {
            super(preferred);
            this.home = home;
            this.work = work;
        }

        /**
         * Indicates a delivery address or telephone number for a residence.
         *
         * @return True, if this is a home address.
         * @see #setHome(boolean)
         */
        public boolean isHome() {
            return home != null && home;
        }

        /**
         * Sets, whether this a delivery address or telephone number for a residence.
         *
         * @param home If this is a home address.
         * @see #isHome()
         */
        public void setHome(boolean home) {
            this.home = home;
        }

        /**
         * Indicate delivery address or telephone number for a place of work.
         *
         * @return True, if this is a work address.
         */
        public boolean isWork() {
            return work != null && work;
        }

        /**
         * Sets, whether this a delivery address or telephone number for a place of work.
         *
         * @param work If this is a work address.
         * @see #isWork() ()
         */
        public void setWork(boolean work) {
            this.work = work;
        }
    }

    /**
     * An abstract base class for address, email and telephone data.
     */
    @XmlTransient
    private abstract static class Preferable {
        @XmlElement(name = "PREF")
        private Boolean pref;

        /**
         * @param preferred If this is a preferred contact data.
         */
        protected Preferable(boolean preferred) {
            this.pref = preferred;
        }

        /**
         * Indicates the preferred delivery address, email or telephone when more than one is specified.
         *
         * @return True, if this is the preferred address, email or telephone.
         * @see #setPreferred(boolean)
         */
        public boolean isPreferred() {
            return pref != null && pref;
        }

        /**
         * Sets, whether this is the preferred address, email or telephone.
         *
         * @param preferred True, if this is the preferred address, email or telephone.
         * @see #isPreferred()
         */
        public void setPreferred(boolean preferred) {
            this.pref = preferred;
        }
    }

    /**
     * Represents a telephone number.
     */
    public static class TelephoneNumber extends ContactData {

        /**
         * to indicate the telephone number has voice messaging support
         */
        @XmlElement(name = "MSG")
        private Boolean msg;

        /**
         * to indicate a voice telephone number
         */
        @XmlElement(name = "VOICE")
        private Boolean voice;

        /**
         * to indicate a facsimile telephone number
         */
        @XmlElement(name = "FAX")
        private Boolean fax;

        /**
         * to indicate a cellular telephone number
         */
        @XmlElement(name = "CELL")
        private Boolean cell;

        /**
         * to indicate a video conferencing telephone number
         */
        @XmlElement(name = "VIDEO")
        private Boolean video;

        /**
         * to indicate a paging device telephone number
         */
        @XmlElement(name = "PAGER")
        private Boolean pager;

        /**
         * to indicate a bulletin board system telephone number
         */
        @XmlElement(name = "BBS")
        private Boolean bbs;

        /**
         * to indicate a MODEM connected telephone number
         */
        @XmlElement(name = "MODEM")
        private Boolean modem;

        /**
         * to indicate a personal communication services telephone number
         */
        @XmlElement(name = "PCS")
        private Boolean pcs;

        /**
         * to indicate an ISDN service telephone number
         */
        @XmlElement(name = "ISDN")
        private Boolean isdn;

        /**
         *
         */
        @XmlElement(name = "NUMBER")
        private String number;

        private TelephoneNumber() {
            super(false, false, true);
        }

        public TelephoneNumber(String number, boolean preferred) {
            this(number, preferred, false, true);
        }

        public TelephoneNumber(String number, boolean preferred, boolean home, boolean work) {
            this(number, preferred, home, work, false, true, false, false, false, false, false, false, false, false);
        }

        /**
         * Creates a full telephone number object.
         *
         * @param number    The telephone number.
         * @param preferred Indicates a preferred number.
         * @param home      Indicates a home number.
         * @param work      Indicates a work number.
         * @param message   Indicates the telephone number has voice messaging support.
         * @param voice     Indicates a voice telephone number.
         * @param fax       Indicates a facsimile telephone number.
         * @param cell      Indicates a cellular telephone number.
         * @param video     Indicates a video conferencing telephone number.
         * @param pager     Indicates a paging device telephone number.
         * @param bbs       Indicates a bulletin board system telephone number.
         * @param modem     Indicates a MODEM connected telephone number.
         * @param pcs       Indicates a personal communication services telephone number.
         * @param isdn      Indicates an ISDN service telephone number.
         */
        public TelephoneNumber(String number, boolean preferred, boolean home, boolean work, boolean message, boolean voice, boolean fax, boolean cell, boolean video, boolean pager, boolean bbs, boolean modem, boolean pcs, boolean isdn) {
            super(preferred, home, work);
            // If no telephone number is included in a <TEL/> element, an empty <NUMBER/> child MUST be included.
            this.number = number == null ? "" : number;
            this.voice = voice;
            this.msg = message;
            this.fax = fax;
            this.cell = cell;
            this.video = video;
            this.pager = pager;
            this.bbs = bbs;
            this.modem = modem;
            this.pcs = pcs;
            this.isdn = isdn;
        }

        /**
         * Indicates a voice telephone number.
         *
         * @return The value.
         * @see #setVoice(boolean)
         */
        public boolean isVoice() {
            return voice != null && voice;
        }

        /**
         * Indicates a voice telephone number.
         *
         * @param voice The value.
         * @see #isVoice()
         */
        public void setVoice(boolean voice) {
            this.voice = voice;
        }

        /**
         * Indicates a facsimile telephone telephone number.
         *
         * @return The value.
         * @see #setFax(boolean)
         */
        public boolean isFax() {
            return fax != null && fax;
        }

        /**
         * Indicates a facsimile telephone telephone number.
         *
         * @param fax The value.
         * @see #isFax()
         */
        public void setFax(boolean fax) {
            this.fax = fax;
        }

        /**
         * Indicates a cellular telephone number.
         *
         * @return The value.
         * @see #setCell(boolean)
         */
        public boolean isCell() {
            return cell != null && cell;
        }

        /**
         * Indicates a cellular telephone number.
         *
         * @param cell The value.
         * @see #isCell()
         */
        public void setCell(boolean cell) {
            this.cell = cell;
        }

        /**
         * Indicates a video conferencing telephone number.
         *
         * @return The value.
         * @see #setVideo(boolean)
         */
        public boolean isVideo() {
            return video != null && video;
        }

        /**
         * Indicates a video conferencing telephone number.
         *
         * @param video The value.
         * @see #isVideo()
         */
        public void setVideo(boolean video) {
            this.video = video;
        }

        /**
         * Indicates a paging device telephone number.
         *
         * @return The value.
         * @see #setPager(boolean)
         */
        public boolean isPager() {
            return pager != null && pager;
        }

        /**
         * Indicates a paging device telephone number.
         *
         * @param pager The value.
         * @see #isPager() ()
         */
        public void setPager(boolean pager) {
            this.pager = pager;
        }

        /**
         * Indicates a bulletin board system telephone number.
         *
         * @return The value.
         * @see #setBbs(boolean)
         */
        public boolean isBbs() {
            return bbs != null && bbs;
        }

        /**
         * Indicates a bulletin board system telephone number.
         *
         * @param bbs The value.
         * @see #isBbs()
         */
        public void setBbs(boolean bbs) {
            this.bbs = bbs;
        }

        /**
         * Indicates a MODEM connected telephone number.
         *
         * @return The value.
         * @see #setModem(boolean)
         */
        public boolean isModem() {
            return modem != null && modem;
        }

        /**
         * Indicates a MODEM connected telephone number.
         *
         * @param modem The value.
         * @see #isModem()
         */
        public void setModem(boolean modem) {
            this.modem = modem;
        }

        /**
         * Indicates a personal communication services telephone number.
         *
         * @return The value.
         * @see #setPcs(boolean)
         */
        public boolean isPcs() {
            return pcs != null && pcs;
        }

        /**
         * Indicates a personal communication services telephone number.
         *
         * @param pcs The value.
         * @see #isPcs()
         */
        public void setPcs(boolean pcs) {
            this.pcs = pcs;
        }

        /**
         * Indicates an ISDN service telephone number.
         *
         * @return The value.
         * @see #setIsdn(boolean)
         */
        public boolean isIsdn() {
            return isdn != null && isdn;
        }

        /**
         * Indicates an ISDN service telephone number.
         *
         * @param isdn The value.
         * @see #isIsdn()
         */
        public void setIsdn(boolean isdn) {
            this.isdn = isdn;
        }

        /**
         * Indicates the telephone number has voice messaging support.
         *
         * @return The value.
         * @see #setMsg(boolean)
         */
        public boolean isMsg() {
            return msg != null && msg;
        }

        /**
         * Sets support for voice messaging.
         *
         * @param msg The value.
         * @see #isMsg()
         */
        public void setMsg(boolean msg) {
            this.msg = msg;
        }

        /**
         * Gets the actual telephone number.
         *
         * @return The telephone number.
         * @see #setNumber(String)
         */
        public String getNumber() {
            return number;
        }

        /**
         * Sets the actual telephone number.
         *
         * @param number The telephone number.
         * @see #getNumber()
         */
        public void setNumber(String number) {
            this.number = number;
        }
    }

    /**
     * Represents an email address.
     */
    public static final class Email extends Preferable {

        /**
         * to indicate an Internet addressing type
         */
        @XmlElement(name = "INTERNET")
        private Boolean internet;

        /**
         * to indicate a X.400 addressing type
         */
        @XmlElement(name = "X400")
        private Boolean x400;

        @XmlElement(name = "USERID")
        private String userId;

        private Email() {
            super(false);
        }

        /**
         * Creates an email address.
         *
         * @param email     The email address.
         * @param preferred If this is the preferred email address.
         */
        public Email(String email, boolean preferred) {
            this(email, preferred, true, false);
        }

        /**
         * Creates an email address.
         *
         * @param email     The email address.
         * @param preferred If this is the preferred email address.
         * @param internet  Indicate an internet addressing type.
         * @param x400      Indicate a X.400 addressing type.
         */
        public Email(String email, boolean preferred, boolean internet, boolean x400) {
            super(preferred);
            this.userId = email;
            this.internet = internet;
            this.x400 = x400;
        }

        /**
         * Gets, whether this is an internet email address.
         *
         * @return True, if this is an internet email address.
         * @see #setInternet(boolean)
         */
        public boolean isInternet() {
            return internet != null && internet;
        }

        /**
         * Sets, whether this is an internet email address.
         *
         * @param internet True, if this is an internet email address.
         * @see #isInternet()
         */
        public void setInternet(boolean internet) {
            this.internet = internet;
        }

        /**
         * Gets, whether this is an X.400 email address.
         *
         * @return True, if this is an X.400 email address.
         * @see #setX400(boolean)
         */
        public boolean isX400() {
            return x400 != null && x400;
        }

        /**
         * Sets, whether this is an X.400 email address.
         *
         * @param x400 True, if this is an X.400 email address.
         * @see #isX400()
         */
        public void setX400(boolean x400) {
            this.x400 = x400;
        }

        /**
         * Gets the actual email address.
         *
         * @return The email address.
         * @see #setEmail(String)
         */
        public String getEmail() {
            return userId;
        }

        /**
         * Sets the actual email address.
         *
         * @param email The email address.
         * @see #getEmail() ()
         */
        public void setEmail(String email) {
            this.userId = email;
        }
    }

    /**
     * Represents an organization.
     */
    public static final class Organization {

        @XmlElement(name = "ORGNAME")
        private final String name;

        @XmlElement(name = "ORGUNIT")
        private final List<String> orgUnits = new ArrayList<>();

        private Organization() {
            this(null);
        }

        private Organization(String name) {
            this.name = name;
        }

        /**
         * Creates an organization.
         *
         * @param name     The organization name.
         * @param orgUnits The organization units.
         */
        public Organization(String name, String... orgUnits) {
            this.name = name;
            this.orgUnits.addAll(Arrays.asList(orgUnits));
        }

        /**
         * Gets the organization name.
         *
         * @return The organization name.
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the organization units.
         *
         * @return The units.
         */
        public List<String> getOrgUnits() {
            return Collections.unmodifiableList(orgUnits);
        }
    }

    /**
     * Represents a name.
     */
    public static final class Name {
        @XmlElement(name = "FAMILY")
        private final String familyName;

        @XmlElement(name = "GIVEN")
        private final String givenName;

        @XmlElement(name = "MIDDLE")
        private final String middleName;

        @XmlElement(name = "PREFIX")
        private final String prefix;

        @XmlElement(name = "SUFFIX")
        private final String suffix;

        private Name() {
            this(null, null, null);
        }

        /**
         * Creates name.
         *
         * @param familyName The family name.
         * @param givenName  The given name.
         * @param middleName The middle name.
         */
        public Name(String familyName, String givenName, String middleName) {
            this(familyName, givenName, middleName, null, null);
        }

        /**
         * Creates a full name.
         *
         * @param familyName The family name.
         * @param givenName  The given name.
         * @param middleName The middle name.
         * @param prefix     The prefix.
         * @param suffix     The suffix.
         */
        public Name(String familyName, String givenName, String middleName, String prefix, String suffix) {
            this.familyName = familyName;
            this.givenName = givenName;
            this.middleName = middleName;
            this.prefix = prefix;
            this.suffix = suffix;
        }

        /**
         * Gets the family name.
         *
         * @return The family name.
         */
        public String getFamilyName() {
            return familyName;
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
         * Gets the middle name.
         *
         * @return The middle name.
         */
        public String getMiddleName() {
            return middleName;
        }

        /**
         * Gets the prefix.
         *
         * @return The prefix.
         */
        public String getPrefix() {
            return prefix;
        }

        /**
         * Gets the suffix.
         *
         * @return The suffix.
         */
        public String getSuffix() {
            return suffix;
        }
    }

    /**
     * Represents an image. The image is either defined by a type/binary value pair or it is referenced by an URI.
     */
    public static final class Image {

        @XmlElement(name = "TYPE")
        private final String type;

        @XmlElement(name = "BINVAL")
        private final byte[] value;

        @XmlElement(name = "EXTVAL")
        private final URI uri;

        private Image() {
            this.type = null;
            this.value = null;
            this.uri = null;
        }

        /**
         * Creates an image with a type and binary value.
         *
         * @param type  The type, e.g. "image/png"
         * @param value The binary value.
         */
        public Image(String type, byte[] value) {
            this.type = type;
            this.value = Objects.requireNonNull(value).clone();
            this.uri = null;
        }

        /**
         * Creates an image with an URI.
         *
         * @param uri The URI.
         */
        public Image(URI uri) {
            this.uri = uri;
            this.type = null;
            this.value = null;
        }

        /**
         * Gets the mime type of the photo, e.g. image/png.
         *
         * @return The mime type.
         */
        public String getType() {
            return type;
        }

        /**
         * Gets the photo as byte array.
         *
         * @return The photo.
         */
        public byte[] getValue() {
            return value.clone();
        }

        /**
         * Gets the URI to an external photo.
         *
         * @return The URI.
         */
        public URI getUri() {
            return uri;
        }
    }

    /**
     * Represents an image. The image is either defined by a type/binary value pair or it is referenced by an URI.
     */
    public static final class Sound {

        @XmlElement(name = "BINVAL")
        private final byte[] value;

        @XmlElement(name = "EXTVAL")
        private final URI uri;

        @XmlElement(name = "PHONETIC")
        private final String phonetic;

        private Sound() {
            this(null, null, null);
        }

        public Sound(byte[] value, URI uri, String phonetic) {
            this.value = Objects.requireNonNull(value).clone();
            this.uri = uri;
            this.phonetic = phonetic;
        }

        /**
         * Gets the binary digital audio pronunciation.
         *
         * @return The photo.
         */
        public byte[] getValue() {
            return value.clone();
        }

        /**
         * Gets the URI to an external binary digital audio pronunciation.
         *
         * @return The URI.
         */
        public URI getUri() {
            return uri;
        }

        /**
         * Gets the textual phonetic pronunciation.
         *
         * @return The textual phonetic pronunciation
         */
        public String getPhonetic() {
            return phonetic;
        }
    }

    /**
     * Represents an authentication credential or encryption key.
     */
    public static final class Key {

        @XmlElement(name = "TYPE")
        private final String type;

        @XmlElement(name = "CRED")
        private final String credential;

        private Key() {
            this(null, null);
        }

        public Key(String type, String credential) {
            this.type = type;
            this.credential = credential;
        }

        /**
         * Gets the credential.
         *
         * @return The credential.
         */
        public String getCredential() {
            return credential;
        }

        /**
         * Gets the type.
         *
         * @return The type.
         */
        public String getType() {
            return type;
        }
    }

    private static final class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

        @Override
        public LocalDate unmarshal(final String v) {
            if (v != null) {
                return LocalDate.parse(v);
            }
            return null;
        }

        @Override
        public String marshal(final LocalDate v) {
            if (v != null) {
                return v.toString();
            }
            return null;
        }
    }

    private static final class ClassificationAdapter extends XmlAdapter<ClassElement, Classification> {

        @Override
        public Classification unmarshal(ClassElement v) {
            if (v != null) {
                if (v._public != null) {
                    return Classification.PUBLIC;
                } else if (v._private != null) {
                    return Classification.PRIVATE;
                } else if (v.confidential != null) {
                    return Classification.CONFIDENTIAL;
                }
            }
            return null;
        }

        @Override
        public ClassElement marshal(Classification v) {
            if (v != null) {
                ClassElement cl = new ClassElement();
                switch (v) {
                    case PUBLIC:
                        cl._public = "";
                        break;
                    case PRIVATE:
                        cl._private = "";
                        break;
                    case CONFIDENTIAL:
                        cl.confidential = "";
                        break;
                }
                return cl;
            }
            return null;
        }
    }

    private static final class ClassElement {
        @XmlElement(name = "PUBLIC")
        private String _public;

        @XmlElement(name = "PRIVATE")
        private String _private;

        @XmlElement(name = "CONFIDENTIAL")
        private String confidential;
    }
}
