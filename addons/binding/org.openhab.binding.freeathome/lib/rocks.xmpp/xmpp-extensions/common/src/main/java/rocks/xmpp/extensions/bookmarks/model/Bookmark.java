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
import javax.xml.bind.annotation.XmlTransient;
import java.text.Collator;

/**
 * An abstract base class for bookmarks.
 *
 * @author Christian Schudt
 * @see ChatRoomBookmark
 * @see WebPageBookmark
 */
@XmlTransient
public abstract class Bookmark implements Comparable<Bookmark> {
    @XmlAttribute
    private final String name;

    protected Bookmark(String name) {
        this.name = name;
    }

    /**
     * Gets a friendly name for the bookmark.
     *
     * @return The name.
     */
    public final String getName() {
        return name;
    }

    /**
     * Compares this bookmark by its name.
     *
     * @param o The other bookmark.
     * @return The comparison result.
     */
    @Override
    public final int compareTo(Bookmark o) {
        if (this == o) {
            return 0;
        }
        if (o != null) {
            if (name != null) {
                if (o.name != null) {
                    return Collator.getInstance().compare(name, o.name);
                } else {
                    return -1;
                }
            } else {
                if (o.name != null) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } else {
            return -1;
        }
    }
}
