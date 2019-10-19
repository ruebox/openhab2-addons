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

package rocks.xmpp.extensions.tune.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;

/**
 * The implementation of the {@code <tune/>} element in the {@code http://jabber.org/protocol/tune} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0118.html">XEP-0118: User Tune</a>
 * @see <a href="https://xmpp.org/extensions/xep-0118.html#schema">XML Schema</a>
 */
@XmlRootElement
public final class Tune {

    /**
     * http://jabber.org/protocol/tune
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/tune";

    private final String artist;

    private final Integer length;

    private final Integer rating;

    private final String source;

    private final String title;

    private final String track;

    private final URI uri;

    private Tune() {
        this(null, null);
    }

    public Tune(String artist, String title) {
        this(artist, title, null, null, null, null, null);
    }

    public Tune(String artist, String title, Integer length, Integer rating, String source, String track, URI uri) {
        this.artist = artist;
        this.title = title;
        this.length = length;
        if (rating != null && (rating < 0 || rating > 10)) {
            throw new IllegalArgumentException("rating must not be greater than 10.");
        }
        this.rating = rating;
        this.source = source;
        this.track = track;
        this.uri = uri;
    }

    /**
     * Gets the artist or performer of the song or piece.
     *
     * @return The artist.
     */
    public final String getArtist() {
        return artist;
    }

    /**
     * Gets the duration of the song or piece in seconds.
     *
     * @return The duration.
     */
    public final Integer getLength() {
        return length;
    }

    /**
     * Gets the user's rating of the song or piece, from 1 (lowest) to 10 (highest).
     *
     * @return The rating.
     */
    public final Integer getRating() {
        return rating;
    }

    /**
     * Gets the collection (e.g., album) or other source (e.g., a band website that hosts streams or audio files).
     *
     * @return The source.
     */
    public final String getSource() {
        return source;
    }

    /**
     * Gets the title of the song or piece.
     *
     * @return The title.
     */
    public final String getTitle() {
        return title;
    }

    /**
     * Gets a unique identifier for the tune; e.g., the track number within a collection or the specific URI for the object (e.g., a stream or audio file).
     *
     * @return The track.
     */
    public final String getTrack() {
        return track;
    }

    /**
     * Gets a URI or URL pointing to information about the song, collection, or artist.
     *
     * @return The URI.
     */
    public final URI getUri() {
        return uri;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        if (artist != null) {
            sb.append("Artist: ").append(artist).append("; ");
        }
        if (title != null) {
            sb.append("Title: ").append(title).append("; ");
        }
        if (length != null) {
            sb.append("Length: ").append(length).append("; ");
        }
        if (rating != null) {
            sb.append("Rating: ").append(rating).append("; ");
        }
        if (source != null) {
            sb.append("Source: ").append(source).append("; ");
        }
        if (track != null) {
            sb.append("Track: ").append(track).append("; ");
        }
        if (uri != null) {
            sb.append("URI: ").append(uri);
        }
        return sb.toString();
    }
}
