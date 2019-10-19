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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The implementation of the {@code <storage/>} element in the {@code storage:bookmarks} namespace.
 * <p>Note:</p>
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/extensions/xep-0048.html#storage">3. Storage</a></cite></p>
 * <p>It is RECOMMENDED to use Publish-Subscribe (XEP-0060) [4] for data storage, specifically through the use of personal data nodes hosted at the user's virtual publish-subscribe service as described in Best Practices for Persistent Storage of Private Data via Publish-Subscribe (XEP-0223) [5] and illustrated in the following sections.</p>
 * <p>Note: In the past, Private XML Storage (XEP-0049) [6] was the recommended method. In addition, other methods could be used, such as HTTP or WebDAV.</p>
 * </blockquote>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0048.html">XEP-0048: Bookmarks</a>
 * @see <a href="https://xmpp.org/extensions/xep-0048.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "storage")
public final class BookmarkStorage {

    /**
     * storage:bookmarks
     */
    public static final String NAMESPACE = "storage:bookmarks";

    @XmlElements({
            @XmlElement(name = "conference", type = ChatRoomBookmark.class),
            @XmlElement(name = "url", type = WebPageBookmark.class)})
    private final List<Bookmark> bookmarks = new ArrayList<>();

    public BookmarkStorage() {
    }

    public BookmarkStorage(Collection<Bookmark> bookmarks) {
        this.bookmarks.addAll(bookmarks);
    }

    /**
     * Gets the bookmarks.
     *
     * @return The bookmarks.
     * @see ChatRoomBookmark
     * @see WebPageBookmark
     */
    public final List<Bookmark> getBookmarks() {
        return Collections.unmodifiableList(bookmarks);
    }
}
