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

package rocks.xmpp.core.sasl.scram;

import java.text.Bidi;
import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Utility class, which prepares a string to be used for username and password comparison according to RFC 4013.
 *
 * @author Christian Schudt
 * @see <a href="http://tools.ietf.org/html/rfc4013">SASLprep: Stringprep Profile for User Names and Passwords</a>
 */
final class SaslPrep {

    /**
     * C.1.2 Non-ASCII space characters
     */
    private static final Pattern MAP_TO_SPACE = Pattern.compile("([\u00A0\u1680\u2000-\u200B\u202F\u205F\u3000])");

    /**
     * B.1 Commonly mapped to nothing
     */
    private static final Pattern MAP_TO_NOTHING = Pattern.compile("([\u00AD\u034F\u1806\u180B-\u180D\u200B-\u200D\u2060\uFE00-\uFE0F\uFEFF])");

    /**
     * Every character, which is not a letter, number, punctuation, symbol character, marker character or space is prohibited.
     */
    private static final Pattern PROHIBITED_CHARACTERS = Pattern.compile("[^\\p{L}\\p{N}\\p{P}\\p{S}\\p{M}\\s]");

    private SaslPrep() {
    }

    /**
     * Prepares a string for username and passwords.
     *
     * @return The mapped string.
     */
    static String prepare(String input) {

        // 2.1. Mapping
        // This profile specifies:
        // -  non-ASCII space characters [StringPrep, C.1.2] that can be
        //    mapped to SPACE (U+0020), and
        // -  the "commonly mapped to nothing" characters [StringPrep, B.1]
        //    that can be mapped to nothing.
        String prepared = MAP_TO_NOTHING.matcher(MAP_TO_SPACE.matcher(input).replaceAll(" ")).replaceAll("");

        // 2.2. Normalization
        // This profile specifies using Unicode normalization form KC
        prepared = Normalizer.normalize(prepared, Normalizer.Form.NFKC);

        // 2.3. Prohibited Output
        if (PROHIBITED_CHARACTERS.matcher(prepared).find()) {
            throw new IllegalArgumentException("Input string contains prohibited characters");
        }

        // 2.4. Bidirectional Characters
        // This profile specifies checking bidirectional strings
        if (Bidi.requiresBidi(prepared.toCharArray(), 0, prepared.length())) {
            Bidi bidi = new Bidi(input, Bidi.DIRECTION_LEFT_TO_RIGHT);

            //  2) If a string contains any RandALCat character, the string MUST NOT
            //     contain any LCat character.
            if (bidi.isMixed()) {
                // except...
                // 3) If a string contains any RandALCat character, a RandALCat
                //    character MUST be the first character of the string, and a
                //    RandALCat character MUST be the last character of the string.
                if (!(bidi.getLevelAt(0) == Bidi.DIRECTION_RIGHT_TO_LEFT && bidi.getLevelAt(0) == bidi.getLevelAt(input.length() - 1))) {
                    throw new IllegalArgumentException("String contains mixed bidirectional characters.");
                }
            }
        }

        return prepared;
    }
}
