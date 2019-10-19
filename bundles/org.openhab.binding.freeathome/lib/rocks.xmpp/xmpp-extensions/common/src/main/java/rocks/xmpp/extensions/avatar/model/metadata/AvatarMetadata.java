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

package rocks.xmpp.extensions.avatar.model.metadata;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The implementation of the {@code <metadata/>} element in the {@code urn:xmpp:avatar:metadata} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0084.html">XEP-0084: User Avatar</a>
 * @see <a href="https://xmpp.org/extensions/xep-0084.html#schema-metadata">XML Schema</a>
 */
@XmlRootElement(name = "metadata")
public final class AvatarMetadata {

    /**
     * urn:xmpp:avatar:metadata
     */
    public static final String NAMESPACE = "urn:xmpp:avatar:metadata";

    private final List<Info> info = new ArrayList<>();

    @XmlAnyElement(lax = true)
    private final Object pointer;

    private AvatarMetadata() {
        this.pointer = null;
    }

    public AvatarMetadata(Info... info) {
        this.info.addAll(Arrays.asList(info));
        this.pointer = null;
    }

    /**
     * Gets the info list.
     *
     * @return The info list.
     */
    public final List<Info> getInfoList() {
        return Collections.unmodifiableList(info);
    }

    /**
     * The implementation of the {@code <info/>} element in the {@code urn:xmpp:avatar:metadata} namespace.
     * <p>
     * This class is immutable.
     */
    public static final class Info {

        @XmlAttribute
        private final Integer bytes;

        @XmlAttribute
        private final String id;

        @XmlAttribute
        private final String type;

        @XmlAttribute
        private final URL url;

        @XmlAttribute
        private final Integer width;

        @XmlAttribute
        private final Integer height;

        private Info() {
            this.bytes = null;
            this.id = null;
            this.type = null;
            this.width = null;
            this.height = null;
            this.url = null;
        }

        public Info(Integer bytes, String id, String type) {
            this(bytes, id, type, null, null, null);
        }

        public Info(Integer bytes, String id, String type, Integer width, Integer height, URL url) {
            this.bytes = Objects.requireNonNull(bytes);
            this.id = Objects.requireNonNull(id);
            this.type = Objects.requireNonNull(type);
            this.width = width;
            this.height = height;
            this.url = url;
        }

        /**
         * Gets the size of the image data in bytes.
         *
         * @return The bytes.
         */
        public final Integer getBytes() {
            return bytes;
        }

        /**
         * Gets a hash of the image data for the specified content-type, where the hash is produced in accordance with the SHA-1 algorithm as specified in RFC 3174 [11] (with binary output).
         *
         * @return The id.
         */
        public final String getId() {
            return id;
        }

        /**
         * Gets the IANA-registered content type of the image data.
         *
         * @return The type.
         */
        public final String getType() {
            return type;
        }

        /**
         * Gets the http: or https: URL at which the image data file is hosted; this attribute MUST NOT be included unless the image data file can be retrieved via HTTP.
         *
         * @return The URL.
         */
        public final URL getUrl() {
            return url;
        }

        /**
         * Gets the width of the image in pixels.
         *
         * @return The width.
         */
        public final Integer getWidth() {
            return width;
        }

        /**
         * Gets the height of the image in pixels.
         *
         * @return The height.
         */
        public final Integer getHeight() {
            return height;
        }
    }
}
