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

package rocks.xmpp.dns;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A DNS TXT resource record.
 *
 * @author Christian Schudt
 */
public final class TxtRecord implements Comparable<TxtRecord> {

    private final String txt;

    TxtRecord(final ByteBuffer data, final int length) {
        final StringBuilder sb = new StringBuilder();
        short i = 0;
        // http://tools.ietf.org/html/rfc7208#section-3.3
        // If a published record contains multiple strings, then the
        // record MUST be treated as if those strings are concatenated together
        // without adding spaces.
        while (i < length) {
            // <character-string> is a single
            // length octet followed by that number of characters.
            final int l = data.get() & 0xFF;
            final byte[] characterString = new byte[l];
            data.get(characterString);
            sb.append(new String(characterString, StandardCharsets.UTF_8));
            i = (short) (i + (l + 1));
        }
        this.txt = sb.toString();
    }

    /**
     * Gets the text.
     *
     * @return The text.
     */
    public final String getText() {
        return txt;
    }

    /**
     * Gets the attributes of this record.
     *
     * @return The attributes or an empty map if there are no attributes.
     * @see <a href="https://tools.ietf.org/html/rfc1464">RFC 1464</a>
     */
    public final Map<String, String> asAttributes() {

        // In other words, the
        // first unquoted equals sign in the TXT record is the name/value
        // delimiter.  All subsequent characters are part of the value.
        final Matcher matcher = Pattern.compile("(?<!`)=").matcher(txt);
        if (matcher.find()) {
            return Collections.singletonMap(txt.substring(0, matcher.start()), txt.substring(matcher.start() + 1));
        }
        return Collections.emptyMap();
    }

    @Override
    public final int compareTo(TxtRecord o) {
        if (o == null) {
            return -1;
        }
        return txt.compareTo(o.txt);
    }

    @Override
    public final String toString() {
        return "TXT \"" + txt + '"';
    }
}
