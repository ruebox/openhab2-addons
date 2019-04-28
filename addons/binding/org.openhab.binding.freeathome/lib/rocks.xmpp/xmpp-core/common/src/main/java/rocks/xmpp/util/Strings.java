package rocks.xmpp.util;

import java.nio.charset.Charset;
import java.util.regex.Pattern;

/**
 * Utility class for Strings.
 *
 * @author Christian Schudt
 */
public final class Strings {

    private static final Pattern CAMEL_CASE = Pattern.compile("(\\p{Ll})(\\p{Lu}+)");

    private Strings() {
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Converts a string from camel case to a string with dashes.
     * <p>
     * Each upper case character (except the first one) followed by a lower case character is converted to a lower case character preceded by a dash.
     * <p>
     * E.g. "CamelCase" is converted to "camel-case".
     *
     * @param str The string.
     * @return The string with dashes.
     */
    public static String toDash(final String str) {
        if (str == null) {
            return null;
        }
        return CAMEL_CASE.matcher(str).replaceAll("$1-$2").toLowerCase();
    }

    /**
     * Converts a string from camel case to a string with underscores.
     * <p>
     * Each upper case character (except the first one) followed by a lower case character is converted to a lower case character preceded by an underscore.
     * <p>
     * E.g. "CamelCase" is converted to "camel_case".
     *
     * @param str The string.
     * @return The string with dashes.
     */
    public static String toUnderscore(final String str) {
        if (str == null) {
            return null;
        }
        return CAMEL_CASE.matcher(str).replaceAll("$1_$2").toLowerCase();
    }

    /**
     * Compares two string by comparing their byte arrays.
     * The "i;octet" collation is described in <a href="https://tools.ietf.org/html/rfc4790#section-9.3.1">9.3.1.  Octet Collation Description</a>.
     *
     * @param s1      The first byte array.
     * @param s2      The second byte array.
     * @param charset The charset.
     * @return The comparison result.
     */
    public static int compareUnsignedBytes(final String s1, final String s2, final Charset charset) {
        final byte[] o1 = s1.getBytes(charset), o2 = s2.getBytes(charset);
        final int length = Math.min(o1.length, o2.length);
        for (int i = 0; i < length; i++) {
            int a = o1[i] & 0xff;
            int b = o2[i] & 0xff;
            if (a != b) {
                return a - b;
            }
        }
        return o1.length - o2.length;
    }
}
