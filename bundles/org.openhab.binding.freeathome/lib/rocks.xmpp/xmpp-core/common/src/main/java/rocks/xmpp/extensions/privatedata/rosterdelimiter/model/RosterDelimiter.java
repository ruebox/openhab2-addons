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

package rocks.xmpp.extensions.privatedata.rosterdelimiter.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * The implementation of the {@code <roster/>} element in the {@code roster:delimiter} namespace.
 * <p>
 * It allows to store a roster group delimiter in the private storage, in order to display nested roster groups.
 * </p>
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/extensions/xep-0083.html#namespace">2. roster:delimiter Namespace</a></cite></p>
 * <p>Therefore, the delimiter SHOULD contain multiple characters in order to avoid inconveniencing the user, but single-character delimiters MUST be honored by the client. The exception is if the delimiter is a single alphanumeric character (a-z, A-Z, 0-9); in this case compliant clients MUST treat the situation as if nesting were disabled, to avoid malicious use of this element by setting 'e' or 'm' or some other common single character as a delimiter.</p>
 * <p>A compliant client SHOULD ask for the nested delimiter before requesting the user's roster, in order to know whether or not to parse the roster 'group' fields accordingly. If there is no delimiter stored, a client MAY set a delimiter but MUST either prompt the user for a delimiter, or use a user-configurable default.</p>
 * </blockquote>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0083.html">XEP-0083: Nested Roster Groups</a>
 * @see <a href="https://xmpp.org/extensions/xep-0083.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "roster")
public final class RosterDelimiter {
    /**
     * roster:delimiter
     */
    public static final String NAMESPACE = "roster:delimiter";

    @XmlValue
    private final String rosterDelimiter;

    private RosterDelimiter() {
        this(null);
    }

    private RosterDelimiter(String rosterDelimiter) {
        this.rosterDelimiter = rosterDelimiter;
    }

    public static RosterDelimiter of(String rosterDelimiter) {
        return new RosterDelimiter(rosterDelimiter);
    }

    public static RosterDelimiter empty() {
        return new RosterDelimiter();
    }

    /**
     * Gets the roster delimiter.
     *
     * @return The roster delimiter.
     */
    public final String getRosterDelimiter() {
        return rosterDelimiter;
    }

    @Override
    public final String toString() {
        return rosterDelimiter;
    }
}
