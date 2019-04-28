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

/**
 * A resource record.
 *
 * @author Christian Schudt
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-4.1.3">RFC 1035 4.1.3. Resource record format</a>
 */
final class ResourceRecord {

    /**
     * a domain name to which this resource record pertains.
     */
    private final String name;

    /**
     * two octets which specify the class of the data in the
     * RDATA field.
     */
    private final Classification clazz;

    /**
     * a 32 bit unsigned integer that specifies the time
     * interval (in seconds) that the resource record may be
     * cached before it should be discarded.  Zero values are
     * interpreted to mean that the RR can only be used for the
     * transaction in progress, and should not be cached.
     */
    private final long ttl;

    final Object data;

    ResourceRecord(final ByteBuffer data) {
        this.name = parse(data);
        /*
        two octets containing one of the RR type codes.  This
        field specifies the meaning of the data in the RDATA field.
        */
        Type type = Type.valueOf(data.getShort() & 0xFFFF);
        this.clazz = Classification.valueOf(data.getShort() & 0xFFFF);
        this.ttl = data.getInt() & 0xFFFFFFFFL;
        int resourceDataLength = data.getShort() & 0xFFFF;
        if (type != null) {
            switch (type) {
                case SRV:
                    this.data = new SrvRecord(data);
                    break;
                case TXT:
                    this.data = new TxtRecord(data, resourceDataLength);
                    break;
                default:
                    this.data = null;
            }
        } else {
            this.data = null;
        }
    }

    /**
     * Parses a domain name.
     *
     * @param data The raw data (for cross references).
     * @return The domain name string.
     * @see <a href="https://tools.ietf.org/html/rfc1035#section-4.1.4">4.1.4. Message compression</a>
     */
    public static String parse(final ByteBuffer data) {
        int count = data.get() & 0xFF;
        int offset = data.position();
        final StringBuilder sb = new StringBuilder();

        while (count > 0) {

            // Check if the byte is a pointer.

            // The pointer takes the form of a two octet sequence:
            // +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
            // | 1  1|                OFFSET                   |
            // +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+

            // 0xC0 == 11000000
            if ((count & 0xC0) == 0xC0) {
                // We are pointing to a previous domain name.
                // Get the rest of the "pointer" byte:
                // 0x3F == 00111111
                int off = (count & 0x3F) << 8;
                // and add the second byte to get the complete offset.
                if (offset == data.position()) {
                    // Read from the current position
                    off += data.get() & 0xFF;
                } else {
                    // Read from a previous offset
                    off += data.get(offset + 1) & 0xFF;
                }
                offset = off;
                count = data.get(offset) & 0xFF;
            } else {
                if (offset == data.position()) {
                    byte b[] = new byte[count];
                    data.get(b);
                    sb.append(new String(b, StandardCharsets.US_ASCII));
                    offset += count + 1;
                    count = data.get() & 0xFF;
                } else {
                    if (data.hasArray()) {
                        sb.append(new String(data.array(), data.arrayOffset() + offset + 1, count, StandardCharsets.US_ASCII));
                    }
                    offset += count + 1;
                    count = data.get(offset) & 0xFF;
                }
                if (count > 0) {
                    sb.append('.');
                }
            }
        }
        return sb.toString();
    }

    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(name).append(' ').append(ttl).append(' ').append(clazz);
        if (data != null) {
            sb.append(' ').append(data);
        }
        return sb.toString();
    }

    enum Classification {
        /**
         * the Internet
         */
        IN(1),
        /**
         * the CSNET class
         */
        CS(2),
        /**
         * the CHAOS class
         */
        CH(3),
        /**
         * Hesiod [Dyer 87]
         */
        HS(4),
        /**
         * any class
         */
        ANY(255);

        final int value;

        Classification(int value) {
            this.value = value;
        }

        static Classification valueOf(int value) {
            for (Classification v : values()) {
                if (v.value == value) {
                    return v;
                }
            }
            return null;
        }
    }

    /**
     * @see <a href="http://www.iana.org/assignments/dns-parameters/dns-parameters.xhtml#dns-parameters-4">Resource Record (RR) TYPEs</a>
     */
    enum Type {

        /**
         * Text strings.
         */
        TXT(16),
        /**
         * Server Selection.
         */
        SRV(33);

        final int value;

        Type(int value) {
            this.value = value;
        }

        static Type valueOf(int value) {
            for (Type v : values()) {
                if (v.value == value) {
                    return v;
                }
            }
            return null;
        }
    }
}
