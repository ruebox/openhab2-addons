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

package rocks.xmpp.extensions.data.mediaelement.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The implementation of the {@code <media/>} element in the {@code urn:xmpp:media-element} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0221.html">XEP-0221: Data Forms Media Element</a>
 * @see <a href="https://xmpp.org/extensions/xep-0221.html#schema">XML Schema</a>
 * @see rocks.xmpp.extensions.data.model.DataForm.Field#getMedia()
 */
@XmlRootElement
public final class Media {

    /**
     * urn:xmpp:media-element
     */
    public static final String NAMESPACE = "urn:xmpp:media-element";

    private final List<Location> uri = new ArrayList<>();

    @XmlAttribute
    private final int height;

    @XmlAttribute
    private final int width;

    private Media() {
        this.height = 0;
        this.width = 0;
    }

    /**
     * Creates a media object with a location.
     *
     * @param locations The location.
     */
    public Media(Location... locations) {
        this.uri.addAll(Arrays.asList(locations));
        this.height = 0;
        this.width = 0;
    }

    /**
     * Creates a media object with a location.
     *
     * @param width     The width.
     * @param height    The height.
     * @param locations The location.
     */
    public Media(int width, int height, Location... locations) {
        this.width = width;
        this.height = height;
        this.uri.addAll(Arrays.asList(locations));
    }

    /**
     * Gets the locations to the media.
     *
     * @return The locations.
     */
    public final List<Location> getLocations() {
        return Collections.unmodifiableList(uri);
    }

    /**
     * If the media is an image or video, gets the recommended display height of the image.
     *
     * @return The height.
     */
    public final int getHeight() {
        return height;
    }

    /**
     * If the media is an image or video, gets the recommended display width of the image.
     *
     * @return The width.
     */
    public final int getWidth() {
        return width;
    }

    /**
     * Specifies the out-of-band location of the media data.
     * <p>
     * This class is immutable.
     */
    public static final class Location {

        @XmlValue
        private final URI uri;

        @XmlAttribute
        private final String type;

        /**
         * Creates a media location.
         *
         * @param type The MIME type, see also {@link #getType()}
         * @param uri  The location of the media.
         */
        public Location(String type, URI uri) {
            this.type = Objects.requireNonNull(type);
            this.uri = Objects.requireNonNull(uri);
        }

        private Location() {
            this.type = null;
            this.uri = null;
        }

        /**
         * Gets the URI.
         *
         * @return The URI.
         */
        public final URI getUri() {
            return uri;
        }

        /**
         * Gets the MIME type of the media.
         * <blockquote>
         * <p><cite><a href="https://xmpp.org/extensions/xep-0221.html#media">2. Media Element</a></cite></p>
         * <p>The value of the 'type' attribute MUST match the syntax specified in RFC 2045 [5]. That is, the value MUST include a top-level media type, the "/" character, and a subtype; in addition, it MAY include one or more optional parameters (e.g., the "audio/ogg" MIME type in the example shown below includes a "codecs" parameter as specified in RFC 4281 [6]). The "type/subtype" string SHOULD be registered in the IANA MIME Media Types Registry [7], but MAY be an unregistered or yet-to-be-registered value.</p>
         * </blockquote>
         *
         * @return The type.
         */
        public final String getType() {
            return type;
        }
    }
}
