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

import java.net.IDN;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static rocks.xmpp.dns.Message.concatArrays;

/**
 * A DNS question.
 *
 * @author Christian Schudt
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-4.1.2">RFC 1035 4.1.2. Question section format</a>
 */
final class Question {

    private final String domain;

    private final ResourceRecord.Type type;

    private final ResourceRecord.Classification clazz;

    Question(String domain, ResourceRecord.Type type, ResourceRecord.Classification clazz) {
        this.domain = domain;
        this.type = type;
        this.clazz = clazz;
    }

    Question(ByteBuffer data) {
        this.domain = ResourceRecord.parse(data);
        this.type = ResourceRecord.Type.valueOf(data.getShort()& 0xFFFF);
        this.clazz = ResourceRecord.Classification.valueOf(data.getShort() & 0xFFFF);
    }

    final byte[] toByteArray() {

        // Whenever a domain name is put into an IDN-unaware domain name slot
        // (see section 2), it MUST contain only ASCII characters.
        final String asciiDomain = IDN.toASCII(domain);

        // https://tools.ietf.org/html/rfc3490#section-3.1
        final String[] labels = asciiDomain.split("[\u002E\u3002\uFF0E\uFF61]", 127);

        // a domain name represented as a sequence of labels, where
        // each label consists of a length octet followed by that
        // number of octets.  The domain name terminates with the
        // zero length octet for the null label of the root.  Note
        // that this field may be an odd number of octets; no
        // padding is used.

        byte[] bytes = new byte[0];
        byte[] tmpBuffer;
        for (final String label : labels) {
            byte[] lbl = label.getBytes(StandardCharsets.US_ASCII);
            tmpBuffer = bytes;
            bytes = concatArrays(tmpBuffer, concatArrays(new byte[]{(byte) lbl.length}, lbl));
        }

        final byte[] value = new byte[5];
        value[0] = (byte) 0;
        value[1] = (byte) (type.value >> 8);
        value[2] = (byte) type.value;
        value[3] = (byte) (clazz.value >> 8);
        value[4] = (byte) (clazz.value);

        return concatArrays(bytes, value);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(toByteArray());
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Question)) {
            return false;
        }
        Question other = (Question) o;

        return Arrays.equals(toByteArray(), other.toByteArray());
    }

    @Override
    public final String toString() {
        return domain + ' ' + type + ' ' + clazz;
    }
}
