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

package rocks.xmpp.extensions.geoloc.model;

import rocks.xmpp.util.adapters.ZoneOffsetAdapter;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Locale;

/**
 * The implementation of the {@code <geoloc/>} element in the {@code http://jabber.org/protocol/geoloc} namespace.
 * <p>
 * This class represents the geological location of a user.
 * </p>
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/extensions/xep-0080.html#transport">4. Recommended Transport</a></cite></p>
 * <p>Location information about human users SHOULD be communicated and transported by means of Publish-Subscribe (XEP-0060) [5] or the subset thereof specified in Personal Eventing Protocol (XEP-0163) [6].</p>
 * <p>Although the XMPP publish-subscribe extension is the preferred means for transporting location information about human users, applications that do not involve human users (e.g., device tracking) MAY use other transport methods; however, because location information is not pure presence information and can change independently of network availability, it SHOULD NOT be provided as an extension to {@code <presence/>}.</p>
 * </blockquote>
 * <h3>Usage</h3>
 * This class is immutable, you have to use a builder to create a geo location instance. Here's an example:
 * ```java
 * GeoLocation geoLocation = GeoLocation.builder()
 *     .countryCode("de")
 *     .latitude(50.2)
 *     .longitude(7.5)
 *     .timeZoneOffset(ZoneOffset.of("+01:00"))
 *     .build();
 * ```
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0080.html">XEP-0080: User Location</a>
 * @see <a href="https://xmpp.org/extensions/xep-0080.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "geoloc")
public final class GeoLocation {

    /**
     * http://jabber.org/protocol/geoloc
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/geoloc";

    @XmlAttribute(namespace = XMLConstants.XML_NS_URI)
    private final Locale lang;

    private final Double accuracy;

    private final Double alt;

    private final Double altaccuracy;

    private final String area;

    private final Double bearing;

    private final String building;

    private final String country;

    @XmlElement(name = "countrycode")
    private final String countryCode;

    private final String datum;

    private final String description;

    private final String floor;

    private final Double lat;

    private final String locality;

    private final Double lon;

    @XmlElement(name = "postalcode")
    private final String postalCode;

    private final String region;

    private final String room;

    private final Double speed;

    private final String street;

    private final String text;

    private final Instant timestamp;

    @XmlJavaTypeAdapter(ZoneOffsetAdapter.class)
    private final ZoneOffset tzo;

    private final URI uri;

    /**
     * Creates an empty geolocation element.
     */
    private GeoLocation() {
        this.accuracy = null;
        this.alt = null;
        this.altaccuracy = null;
        this.area = null;
        this.bearing = null;
        this.building = null;
        this.country = null;
        this.countryCode = null;
        this.datum = null;
        this.description = null;
        this.floor = null;
        this.lang = null;
        this.lat = null;
        this.locality = null;
        this.lon = null;
        this.postalCode = null;
        this.region = null;
        this.room = null;
        this.speed = null;
        this.street = null;
        this.text = null;
        this.timestamp = null;
        this.tzo = null;
        this.uri = null;
    }

    private GeoLocation(Builder builder) {
        this.accuracy = builder.accuracy;
        this.alt = builder.alt;
        this.altaccuracy = builder.altaccuracy;
        this.area = builder.area;
        this.bearing = builder.bearing;
        this.building = builder.building;
        this.country = builder.country;
        this.countryCode = builder.countryCode;
        this.datum = builder.datum;
        this.description = builder.description;
        this.floor = builder.floor;
        this.lang = builder.language;
        this.lat = builder.latitude;
        this.locality = builder.locality;
        this.lon = builder.longitude;
        this.postalCode = builder.postalCode;
        this.region = builder.region;
        this.room = builder.room;
        this.speed = builder.speed;
        this.street = builder.street;
        this.text = builder.text;
        this.timestamp = builder.timestamp;
        this.tzo = builder.zoneOffset;
        this.uri = builder.uri;
    }

    /**
     * Creates the builder to build a geo location.
     *
     * @return The builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets the horizontal GPS error in meters.
     *
     * @return The accuracy.
     */
    public final Double getAccuracy() {
        return accuracy;
    }

    /**
     * Gets the altitude in meters above or below sea level.
     *
     * @return The altitude.
     */
    public final Double getAltitude() {
        return alt;
    }

    /**
     * Gets the vertical GPS error in meters.
     *
     * @return The altitude accuracy.
     */
    public final Double getAltitudeAccuracy() {
        return altaccuracy;
    }

    /**
     * Gets a named area such as a campus or neighborhood.
     *
     * @return The area.
     */
    public final String getArea() {
        return area;
    }

    /**
     * Gets the GPS bearing (direction in which the entity is heading to reach its next waypoint), measured in decimal degrees relative to true north.
     *
     * @return The bearing.
     */
    public final Double getBearing() {
        return bearing;
    }

    /**
     * Gets a specific building on a street or in an area.
     *
     * @return The building.
     */
    public final String getBuilding() {
        return building;
    }

    /**
     * Gets the nation where the user is located.
     *
     * @return The country.
     */
    public final String getCountry() {
        return country;
    }

    /**
     * Gets the ISO 3166 two-letter country code.
     *
     * @return The country code.
     */
    public final String getCountryCode() {
        return countryCode;
    }

    /**
     * Gets the GPS datum.
     *
     * @return The GPS datum.
     */
    public final String getDatum() {
        return datum;
    }

    /**
     * Gets a natural-language name for or description of the location.
     *
     * @return The description.
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Gets a particular floor in a building.
     *
     * @return The floor.
     */
    public final String getFloor() {
        return floor;
    }

    /**
     * Gets the latitude in decimal degrees North.
     *
     * @return The latitude.
     */
    public final Double getLatitude() {
        return lat;
    }

    /**
     * Gets a locality within the administrative region, such as a town or city.
     *
     * @return The locality.
     */
    public final String getLocality() {
        return locality;
    }

    /**
     * Gets the longitude in decimal degrees East.
     *
     * @return The longitude.
     */
    public final Double getLongitude() {
        return lon;
    }

    /**
     * Gets a code used for postal delivery.
     *
     * @return The postal code.
     */
    public final String getPostalCode() {
        return postalCode;
    }

    /**
     * Gets an administrative region of the nation, such as a state or province.
     *
     * @return The region.
     */
    public final String getRegion() {
        return region;
    }

    /**
     * Gets a particular room in a building.
     *
     * @return The room.
     */
    public final String getRoom() {
        return room;
    }

    /**
     * Gets the speed at which the entity is moving, in meters per second.
     *
     * @return The speed.
     */
    public final Double getSpeed() {
        return speed;
    }

    /**
     * Gets a thoroughfare within the locality, or a crossing of two thoroughfares.
     *
     * @return The street.
     */
    public final String getStreet() {
        return street;
    }

    /**
     * Gets a catch-all element that captures any other information about the location.
     *
     * @return The text.
     */
    public final String getText() {
        return text;
    }

    /**
     * Gets the UTC timestamp specifying the moment when the reading was taken.
     *
     * @return The timestamp.
     */
    public final Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Gets a URI or URL pointing to information about the location.
     *
     * @return The URI.
     */
    public final URI getUri() {
        return uri;
    }

    /**
     * Gets the the natural language of location data.
     *
     * @return The language.
     */
    public final Locale getLanguage() {
        return lang;
    }

    /**
     * Gets the time zone offset from UTC for the current location.
     *
     * @return The time zone offset.
     */
    public final ZoneOffset getTimeZoneOffset() {
        return tzo;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder("Geolocation: ");
        if (accuracy != null) {
            sb.append("Accuracy: ").append(accuracy).append("m; ");
        }
        if (alt != null) {
            sb.append("Altitude: ").append(alt).append("m; ");
        }
        if (altaccuracy != null) {
            sb.append("Altitude Accuracy: ").append(altaccuracy).append("m; ");
        }
        if (area != null) {
            sb.append("Area: ").append(area).append("; ");
        }
        if (bearing != null) {
            sb.append("Bearing: ").append(bearing).append("°; ");
        }
        if (building != null) {
            sb.append("Building: ").append(building).append("; ");
        }
        if (country != null) {
            sb.append("Country: ").append(country).append("; ");
        }
        if (countryCode != null) {
            sb.append("Country Code: ").append(countryCode).append("; ");
        }
        if (datum != null) {
            sb.append("Datum: ").append(datum).append("; ");
        }
        if (description != null) {
            sb.append("Description: ").append(description).append("; ");
        }
        if (floor != null) {
            sb.append("Floor: ").append(floor).append("; ");
        }
        if (lat != null) {
            sb.append("Latitude: ").append(lat).append("°; ");
        }
        if (locality != null) {
            sb.append("Locality: ").append(locality).append("; ");
        }
        if (lon != null) {
            sb.append("Longitude: ").append(lon).append("°; ");
        }
        if (postalCode != null) {
            sb.append("Postal Code: ").append(postalCode).append("; ");
        }
        if (region != null) {
            sb.append("Region: ").append(region).append("; ");
        }
        if (room != null) {
            sb.append("Room: ").append(room).append("; ");
        }
        if (speed != null) {
            sb.append("Speed: ").append(speed).append("m/s; ");
        }
        if (street != null) {
            sb.append("Street: ").append(street).append("; ");
        }
        if (text != null) {
            sb.append("Text: ").append(text).append("; ");
        }
        if (timestamp != null) {
            sb.append("Timestamp: ").append(timestamp).append("; ");
        }
        if (tzo != null) {
            sb.append("Time Zone: ").append(tzo).append("; ");
        }
        if (uri != null) {
            sb.append("URI: ").append(uri).append("; ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    /**
     * A builder class to which builds geo location objects.
     */
    public static final class Builder {

        private Locale language;

        private Double accuracy;

        private Double alt;

        private Double altaccuracy;

        private String area;

        private Double bearing;

        private String building;

        private String country;

        private String countryCode;

        private String datum;

        private String description;

        private String floor;

        private Double latitude;

        private String locality;

        private Double longitude;

        private String postalCode;

        private String region;

        private String room;

        private Double speed;

        private String street;

        private String text;

        private Instant timestamp;

        private ZoneOffset zoneOffset;

        private URI uri;

        private Builder() {
        }

        /**
         * Sets the natural language of location data.
         *
         * @param language The language.
         * @return The builder.
         */
        public Builder language(Locale language) {
            this.language = language;
            return this;
        }

        /**
         * Sets the horizontal GPS error in meters.
         *
         * @param accuracy The accuracy.
         * @return The builder.
         */
        public Builder accuracy(Double accuracy) {
            this.accuracy = accuracy;
            return this;
        }

        /**
         * Sets the altitude in meters above or below sea level.
         *
         * @param altitude The altitude.
         * @return The builder.
         */
        public Builder altitude(Double altitude) {
            this.alt = altitude;
            return this;
        }

        /**
         * Sets the vertical GPS error in meters.
         *
         * @param altitudeAccuracy The vertical GPS error in meters.
         * @return The builder.
         */
        public Builder altitudeAccuracy(Double altitudeAccuracy) {
            this.altaccuracy = altitudeAccuracy;
            return this;
        }

        /**
         * Sets a named area such as a campus or neighborhood.
         *
         * @param area The area.
         * @return The builder.
         */
        public Builder area(String area) {
            this.area = area;
            return this;
        }

        /**
         * Sets the GPS bearing (direction in which the entity is heading to reach its next waypoint), measured in decimal degrees relative to true north.
         *
         * @param bearing The bearing.
         * @return The builder.
         */
        public Builder bearing(Double bearing) {
            this.bearing = bearing;
            return this;
        }

        /**
         * Sets a specific building on a street or in an area.
         *
         * @param building The building.
         * @return The builder.
         */
        public Builder building(String building) {
            this.building = building;
            return this;
        }

        /**
         * Sets the nation where the user is located.
         *
         * @param country The country.
         * @return The builder.
         */
        public Builder country(String country) {
            this.country = country;
            return this;
        }

        /**
         * Sets the ISO 3166 two-letter country code.
         *
         * @param countryCode The country code.
         * @return The builder.
         */
        public Builder countryCode(String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        /**
         * Sets the GPS datum.
         *
         * @param datum The GPS datum.
         * @return The builder.
         */
        public Builder datum(String datum) {
            this.datum = datum;
            return this;
        }

        /**
         * Sets a natural-language name for or description of the location.
         *
         * @param description The description.
         * @return The builder.
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Sets a particular floor in a building.
         *
         * @param floor The floor.
         * @return The builder.
         */
        public Builder floor(String floor) {
            this.floor = floor;
            return this;
        }

        /**
         * Sets the latitude in decimal degrees North.
         *
         * @param latitude The latitude.
         * @return The builder.
         */
        public Builder latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        /**
         * Sets a locality within the administrative region, such as a town or city.
         *
         * @param locality The locality.
         * @return The builder.
         */
        public Builder locality(String locality) {
            this.locality = locality;
            return this;
        }

        /**
         * Sets the longitude in decimal degrees East.
         *
         * @param longitude The longitude.
         * @return The builder.
         */
        public Builder longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        /**
         * Sets a code used for postal delivery.
         *
         * @param postalCode The postal code.
         * @return The builder.
         */
        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        /**
         * Sets an administrative region of the nation, such as a state or province.
         *
         * @param region The region.
         * @return The builder.
         */
        public Builder region(String region) {
            this.region = region;
            return this;
        }

        /**
         * Sets a particular room in a building.
         *
         * @param room The room.
         * @return The builder.
         */
        public Builder room(String room) {
            this.room = room;
            return this;
        }

        /**
         * Sets the speed at which the entity is moving, in meters per second.
         *
         * @param speed The speed.
         * @return The builder.
         */
        public Builder speed(Double speed) {
            this.speed = speed;
            return this;
        }

        /**
         * Sets a thoroughfare within the locality, or a crossing of two thoroughfares.
         *
         * @param street The street.
         * @return The builder.
         */
        public Builder street(String street) {
            this.street = street;
            return this;
        }

        /**
         * Sets a catch-all element that captures any other information about the location.
         *
         * @param text The text.
         * @return The builder.
         */
        public Builder text(String text) {
            this.text = text;
            return this;
        }

        /**
         * Sets the UTC timestamp specifying the moment when the reading was taken.
         *
         * @param timestamp The timestamp.
         * @return The builder.
         */
        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        /**
         * Sets the time zone offset from UTC for the current location.
         *
         * @param zoneOffset The time zone offset.
         * @return The builder.
         */
        public Builder timeZoneOffset(ZoneOffset zoneOffset) {
            this.zoneOffset = zoneOffset;
            return this;
        }

        /**
         * Sets a URI or URL pointing to information about the location.
         *
         * @param uri The URI.
         * @return The builder.
         */
        public Builder uri(URI uri) {
            this.uri = uri;
            return this;
        }

        /**
         * Builds the geo location.
         *
         * @return The geo location.
         */
        public GeoLocation build() {
            return new GeoLocation(this);
        }
    }
}
