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

package rocks.xmpp.extensions.muc.model;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * A long-lived association or connection with a room; the possible affiliations are "owner", "admin", "member", and "outcast" (naturally it is also possible to have no affiliation); affiliation is distinct from role. An affiliation lasts across a user's visits to a room.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0045.html#affil">5.2 Affiliations</a>
 */
public enum Affiliation {
    /**
     * The owner affiliation.
     */
    @XmlEnumValue("owner")
    OWNER,

    /**
     * The admin affiliation.
     */
    @XmlEnumValue("admin")
    ADMIN,

    /**
     * The member affiliation.
     */
    @XmlEnumValue("member")
    MEMBER,

    /**
     * No affiliation.
     */
    @XmlEnumValue("none")
    NONE,

    /**
     * The outcast affiliation.
     */
    @XmlEnumValue("outcast")
    OUTCAST;

    /**
     * Compares two affiliations and returns true, if this affiliation is higher than the other
     * with regards to their privileges in a multi-user chat.
     * <p>
     * The highest affiliation is owner, followed by admin, followed by member, followed by none and then outcast.
     *
     * @param affiliation The other affiliation.
     * @return True, if this affiliation is higher than the other.
     */
    public final boolean isHigherThan(final Affiliation affiliation) {
        return compareTo(affiliation) < 0;
    }
}
