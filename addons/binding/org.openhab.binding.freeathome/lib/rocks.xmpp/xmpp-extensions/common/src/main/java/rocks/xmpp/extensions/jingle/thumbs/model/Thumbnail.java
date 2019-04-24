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

package rocks.xmpp.extensions.jingle.thumbs.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Objects;

/**
 * The implementation of the {@code <thumbnail/>} element in the {@code urn:xmpp:thumbs:1"} namespace.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0264.html">XEP-0264: Jingle Content Thumbnails</a>
 */
@XmlRootElement
public final class Thumbnail {

    /**
     * urn:xmpp:thumbs:1
     */
    public static final String NAMESPACE = "urn:xmpp:thumbs:1";

    @XmlAttribute
    private final URI uri;

    @XmlAttribute(name = "media-type")
    private final String mediaType;

    @XmlAttribute
    private final Integer width;

    @XmlAttribute
    private final Integer height;

    private Thumbnail() {
        this.uri = null;
        this.mediaType = null;
        this.width = null;
        this.height = null;
    }

    /**
     * Creates a thumbnail.
     *
     * @param uri A URI where the thumbnail data can be accessed (typically by using a URI scheme of 'cid:', 'https:', or 'http:'). If the URI scheme is 'cid:' then the identifier MUST refer to a bit of binary data as described in <a href="https://xmpp.org/extensions/xep-0231.html">Bits of Binary (XEP-0231)</a>.
     */
    public Thumbnail(URI uri) {
        this(uri, null, null, null);
    }

    /**
     * Creates a thumbnail.
     *
     * @param uri       A URI where the thumbnail data can be accessed (typically by using a URI scheme of 'cid:', 'https:', or 'http:'). If the URI scheme is 'cid:' then the identifier MUST refer to a bit of binary data as described in <a href="https://xmpp.org/extensions/xep-0231.html">Bits of Binary (XEP-0231)</a>.
     * @param mediaType The value of the 'media-type' attribute MUST match the syntax specified in RFC 2045 [3]. That is, the value MUST include a top-level media type, the "/" character, and a subtype; in addition, it MAY include one or more optional parameters.
     * @param width     The intended display width of the thumbnail image. Used as a hint for the receiving client to prepare the appropriate UI, such as a dialog window.
     * @param height    The intended display height of the thumbnail image. Used as a hint for the receiving client to prepare the appropriate UI, such as a dialog window.
     */
    public Thumbnail(URI uri, String mediaType, Integer width, Integer height) {
        this.uri = Objects.requireNonNull(uri);
        this.mediaType = mediaType;
        this.width = width;
        this.height = height;
    }

    /**
     * A URI where the thumbnail data can be accessed (typically by using a URI scheme of 'cid:', 'https:', or 'http:'). If the URI scheme is 'cid:' then the identifier MUST refer to a bit of binary data as described in <a href="https://xmpp.org/extensions/xep-0231.html">Bits of Binary (XEP-0231)</a>.
     *
     * @return The URI.
     * @see <a href="https://xmpp.org/extensions/xep-0231.html">Bits of Binary (XEP-0231)</a>
     */
    public final URI getUri() {
        return uri;
    }

    /**
     * The value of the 'media-type' attribute MUST match the syntax specified in RFC 2045 [3]. That is, the value MUST include a top-level media type, the "/" character, and a subtype; in addition, it MAY include one or more optional parameters.
     *
     * @return The media type.
     */
    public final String getMediaType() {
        return mediaType;
    }

    /**
     * The intended display width of the thumbnail image. Used as a hint for the receiving client to prepare the appropriate UI, such as a dialog window.
     *
     * @return The width.
     */
    public final Integer getWidth() {
        return width;
    }

    /**
     * The intended display height of the thumbnail image. Used as a hint for the receiving client to prepare the appropriate UI, such as a dialog window.
     *
     * @return The height.
     */
    public final Integer getHeight() {
        return height;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder("Thumbnail: ");
        sb.append(uri);
        if (mediaType != null) {
            sb.append(" (").append(mediaType).append(')');
        }
        if (width != null) {
            sb.append(", width: ").append(width);
        }
        if (height != null) {
            sb.append(", height: ").append(height);
        }
        return sb.toString();
    }
}
