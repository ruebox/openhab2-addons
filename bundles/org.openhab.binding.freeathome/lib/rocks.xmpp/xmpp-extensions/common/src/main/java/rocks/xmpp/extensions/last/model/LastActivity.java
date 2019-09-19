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

package rocks.xmpp.extensions.last.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * The implementation of the {@code <query/>} element in the {@code jabber:iq:last} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0012.html">XEP-0012: Last Activity</a>
 * @see <a href="https://xmpp.org/extensions/xep-0256.html">XEP-0256: Last Activity in Presence</a>
 * @see <a href="https://xmpp.org/extensions/xep-0012.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "query")
public final class LastActivity {

    /**
     * jabber:iq:last
     */
    public static final String NAMESPACE = "jabber:iq:last";

    @XmlAttribute
    private final Long seconds;

    @XmlValue
    private final String status;

    /**
     * Creates an empty last activity instance, used for querying another entity.
     */
    public LastActivity() {
        this.seconds = null;
        this.status = null;
    }

    /**
     * Creates a last activity instance with a number of seconds, used as response.
     *
     * @param seconds The number of seconds since the last activity.
     * @param status  The status text.
     */
    public LastActivity(long seconds, String status) {
        this.seconds = seconds;
        this.status = status;
    }

    /**
     * Gets the number of seconds since the last activity.
     *
     * @return The number of seconds since the last activity.
     */
    public final Long getSeconds() {
        return seconds;
    }

    /**
     * Gets the status message of the last unavailable presence received from the user, if the last activity request was a <a href="https://xmpp.org/extensions/xep-0012.html#offline">Offline User Query</a>.
     *
     * @return The status message of the last unavailable presence received from the user or null.
     */
    public final String getStatus() {
        return status;
    }

    @Override
    public final String toString() {
        return "Last activity: " + String.valueOf(seconds) + " seconds ago";
    }
}
