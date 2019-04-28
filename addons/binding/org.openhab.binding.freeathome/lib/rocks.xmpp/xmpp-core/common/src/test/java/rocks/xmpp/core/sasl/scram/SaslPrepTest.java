/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Christian Schudt
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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Christian Schudt
 */
public class SaslPrepTest {

    @Test
    public void shouldMapToSpace() {
        // http://tools.ietf.org/html/rfc3454#appendix-B.1
        String s = "\u00A0\u1680\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u200B\u202F\u205F\u3000";
        Assert.assertEquals(SaslPrep.prepare(s), "                 ");
    }

    @Test
    public void shouldMapToNothing() {
        String s = "\u034F\u1806\u180B\u180C\u180D\u200C\u200D\u2060\uFE00\uFE01\uFE02\uFE03\uFE04\uFE05\uFE06\uFE07\uFE08\uFE09\uFE0A\uFE0B\uFE0C\uFE0D\uFE0E\uFE0F\uFEFF";
        Assert.assertEquals(SaslPrep.prepare(s), "");
    }

    @Test
    public void testAsciiControlCharacters() {
        String[] chars = new String[]{"\u0000", "\u001F", "\u007F", "\uD83F\uDFFE"};
        int failed = 0;
        for (String aChar : chars) {
            try {
                SaslPrep.prepare(aChar);
            } catch (IllegalArgumentException e) {
                failed++;
            }
        }
        Assert.assertEquals(failed, chars.length);
    }

    @Test
    public void testSurrogateCodes() {
        String[] chars = new String[]{"\uD800", "\uDFFF"};
        int failed = 0;
        for (String aChar : chars) {
            try {
                SaslPrep.prepare(aChar);
            } catch (IllegalArgumentException e) {
                failed++;
            }
        }
        Assert.assertEquals(failed, chars.length);
    }

    @Test
    public void testNonAsciiControlCharacters() {
        String[] chars = new String[]{"\u0080", "\u06DD", "\u070F", "\u180E", "\u2028", "\u2029", "\u2061", "\u2062", "\u2063", "\u206A", "\u206B", "\u206C", "\u206D", "\u206E", "\u206F", "\uFFF9", "\uFFFA", "\uFFFB", "\uD834\uDD73", "\uD834\uDD7A"};
        int failed = 0;
        for (String aChar : chars) {

            try {
                SaslPrep.prepare(aChar);
            } catch (IllegalArgumentException e) {
                failed++;
            }
        }
        Assert.assertEquals(failed, chars.length);
    }

    @Test
    public void testPrivateUseCharacters() {
        String[] chars = new String[]{"\uE000", "\uF8FF", "\uDB80\uDC00", "\uDBC0\uDC00"};
        int failed = 0;
        for (String aChar : chars) {
            try {
                SaslPrep.prepare(aChar);
            } catch (IllegalArgumentException e) {
                failed++;
            }
        }
        Assert.assertEquals(failed, chars.length);
    }

    @Test
    public void testNonCharacterCodePoints() {
        String[] chars = new String[]{"\uFDD0", "\uFDEF"};
        int failed = 0;
        for (String aChar : chars) {
            try {
                SaslPrep.prepare(aChar);
            } catch (IllegalArgumentException e) {
                failed++;
            }
        }
        Assert.assertEquals(failed, chars.length);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidBidiString() {
        String s = "\u0627\u0031";
        SaslPrep.prepare(s);
    }

    @Test
    public void testValidBidiString() {
        String s = "\u0627\u0031\u0628";
        Assert.assertEquals(SaslPrep.prepare(s), "\u0627\u0031\u0628");
    }

    /**
     * C.8 Change display properties or are deprecated
     */
    @Test
    public void testProhibitedChars() {
        String[] str = new String[]{"\u200E", "\u200F", "\u202A", "\u202B", "\u202C", "\u202D", "\u202E", "\u206A", "\u206B", "\u206C", "\u206D", "\u206E", "\u206F"};

        int fails = 0;
        for (String s : str) {

            try {
                SaslPrep.prepare(s);
            } catch (Exception e) {
                fails++;
            }
        }

        Assert.assertEquals(fails, 13);
    }

    @Test
    public void testExample1() {
        String input = "I\u00ADX";
        String output = SaslPrep.prepare(input);
        Assert.assertEquals(output, "IX");
    }

    @Test
    public void testExample2() {
        String input = "user";
        String output = SaslPrep.prepare(input);
        Assert.assertEquals(output, "user");
    }

    @Test
    public void testExample3() {
        String input = "USER";
        String output = SaslPrep.prepare(input);
        Assert.assertEquals(output, "USER");
    }

    @Test
    public void testExample4() {
        String input = "\u00AA";
        String output = SaslPrep.prepare(input);
        Assert.assertEquals(output, "a");
    }

    @Test
    public void testExample5() {
        String input = "\u2168";
        String output = SaslPrep.prepare(input);
        Assert.assertEquals(output, "IX");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExample6() {
        String input = "\u0007";
        SaslPrep.prepare(input);
        Assert.fail("Should have thrown exception");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExample7() {
        String input = "\u0627\u0031";
        SaslPrep.prepare(input);
        Assert.fail("Should have thrown exception");
    }
}
