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

package rocks.xmpp.extensions.bookmarks.model;


import javax.xml.bind.annotation.XmlAttribute;
import java.net.URL;
import java.util.Objects;

/**
 * A web page bookmark.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 */
public final class WebPageBookmark extends Bookmark {
    @XmlAttribute
    private final URL url;

    private WebPageBookmark() {
        super(null);
        this.url = null;
    }

    /**
     * Creates a web page bookmark.
     *
     * @param name The bookmark name.
     * @param url  The URL of the web page.
     */
    public WebPageBookmark(String name, URL url) {
        super(name);
        this.url = Objects.requireNonNull(url);
    }

    /**
     * Gets the URL of the web page.
     *
     * @return The URL.
     */
    public final URL getUrl() {
        return url;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WebPageBookmark)) {
            return false;
        }
        WebPageBookmark other = (WebPageBookmark) o;

        return Objects.equals(url, other.url);

    }

    @Override
    public final int hashCode() {
        return Objects.hash(url);
    }

    @Override
    public final String toString() {
        return getName() + ": " + (url != null ? url.toString() : "");
    }
}
