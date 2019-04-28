/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 Christian Schudt
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

package rocks.xmpp.extensions.colors;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * A consistent color which is generated from an input string such as a nickname or a bare JID.
 * Consistent means, the resulting color is always the same for same input strings.
 * <h2>Generating a Color</h2>
 * To generate a color pass either a nickname or the bare JID of the conversation as <code>input</code> to the {@link #generate(CharSequence)} method.
 * Generally, nick names should be preferred over bare JIDs.
 * ```java
 * ConsistentColor color = ConsistentColor.generate(input);
 * float red = color.getRed();
 * float green = color.getGreen();
 * float blue = color.getBlue();
 * ```
 * <h2>Corrections for Color Vision Deficiencies</h2>
 * In order to ensure accessibility to users with color vision deficiencies, you can generate corrected colors.
 * ```java
 * ConsistentColor color = ConsistentColor.generate(input, ConsistentColor.ColorVisionDeficiency.RED_GREEN_BLINDNESS);}
 * ```
 * <h2>Adapting the Color for specific Background Colors</h2>
 * To increase readability on colored background, you may want to adapt the color for background colors.
 * ```java
 * ConsistentColor color = ConsistentColor.generate(input);
 * // Adaption for black background
 * ConsistentColor adapted = color.adaptForBackground(0, 0, 0);
 * ```
 * <h2>Integration with JavaFX and AWT</h2>
 * ```java
 * ConsistentColor c = ConsistentColor.generate(input);
 * javafx.scene.paint.Color color = javafx.scene.paint.Color.color(c.getRed(), c.getGreen(), c.getBlue());
 * java.awt.Color color = new java.awt.Color(c.getRed(), c.getGreen(), c.getBlue());
 * ```
 * The default Y value in the YCbCr color space is 0.732.
 * <p>
 * Note that this class overrides {@link #equals(Object)} and {@link #hashCode()}:
 * Two instances of this classes are equal, if their RGB values are equal.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0392.html">XEP-0392: Consistent Color Generation</a>
 */
public final class ConsistentColor {

    private static final float KR = 0.299f;

    private static final float KG = 0.587f;

    private static final float KB = 0.114f;

    private final float red, green, blue;

    private ConsistentColor(final float red, final float green, final float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * Generates a color for an input string.
     *
     * @param input The input string.
     * @return The color.
     * @see #generate(CharSequence, ColorVisionDeficiency)
     */
    public static ConsistentColor generate(final CharSequence input) {
        return generate(input, ColorVisionDeficiency.NONE);
    }

    /**
     * Generates a color for an input string and for a color vision deficiency.
     *
     * @param input                 The input string.
     * @param colorVisionDeficiency The color vision deficiency.
     * @return The color.
     * @see #generate(CharSequence, ColorVisionDeficiency, float)
     */
    public static ConsistentColor generate(final CharSequence input, final ColorVisionDeficiency colorVisionDeficiency) {
        return generate(input, colorVisionDeficiency, 0.732f);
    }

    /**
     * Generates a color for an input string and for a color vision deficiency.
     * This method also allow to pass other values than 0.732 for the Y value in the YCbCr color space.
     *
     * @param input                 The input string.
     * @param colorVisionDeficiency The color vision deficiency.
     * @param y                     The Y value in the YCbCr color space.
     * @return The color.
     */
    public static ConsistentColor generate(final CharSequence input, final ColorVisionDeficiency colorVisionDeficiency, final float y) {
        final double angle = generateAngle(input, colorVisionDeficiency);

        double cr = Math.sin(angle);
        double cb = Math.cos(angle);
        final double factor;
        final double crAbs = Math.abs(cr);
        final double cbAbs = Math.abs(cb);
        if (crAbs > cbAbs) {
            factor = 0.5 / crAbs;
        } else {
            factor = 0.5 / cbAbs;
        }
        cb = cb * factor;
        cr = cr * factor;

        float r = (float) (2 * (1 - KR) * cr + y);
        float b = (float) (2 * (1 - KB) * cb + y);
        float g = (y - KR * r - KB * b) / KG;

        r = Math.max(Math.min(r, 1), 0);
        g = Math.max(Math.min(g, 1), 0);
        b = Math.max(Math.min(b, 1), 0);
        return new ConsistentColor(r, g, b);
    }

    static double generateAngle(final CharSequence input, final ColorVisionDeficiency colorVisionDeficiency) {
        try {
            // 1. Run the input through SHA-1
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            final byte[] hash = messageDigest.digest(input.toString().getBytes(StandardCharsets.UTF_8));

            // 2. Extract the first 16 bits (little-endian)
            final double first16Bits = ((hash[1] & 0xff) << 8) | (hash[0] & 0xff);

            // 3. Divide the value by 65535 (use float division) and multiply it by 2π (two Pi).
            final double angle = first16Bits / 65535 * 2 * Math.PI;

            switch (colorVisionDeficiency) {
                case RED_GREEN_BLINDNESS:
                    return angle / 2;
                case BLUE_BLINDNESS:
                    return angle / 2 + Math.PI / 2;
                default:
                    return angle;
            }
        } catch (NoSuchAlgorithmException e) {
            // Every implementation of the Java platform is required to support SHA-1
            throw new AssertionError(e);
        }
    }

    /**
     * Adapts the color for a background color.
     *
     * @param red   The red component of the background color in the range {@code 0.0-1.0}.
     * @param green The green component of the background color in the range {@code 0.0-1.0}.
     * @param blue  The blue component of the background color in the range {@code 0.0-1.0}.
     * @return The adapted color.
     * @throws IllegalArgumentException If the parameters are out of range.
     */
    public final ConsistentColor adaptForBackground(final float red, final float green, final float blue) {
        if (red > 1 || red < 0) {
            throw new IllegalArgumentException("red value out of range 0.0 - 1.0");
        }
        if (green > 1 || green < 0) {
            throw new IllegalArgumentException("green value out of range 0.0 - 1.0");
        }
        if (blue > 1 || blue < 0) {
            throw new IllegalArgumentException("blue value out of range 0.0 - 1.0");
        }

        final float ri = getRed();
        final float gi = getGreen();
        final float bi = getBlue();

        final float rb_inv = 1 - red;
        final float gb_inv = 1 - green;
        final float bb_inv = 1 - blue;

        final float rc = (float) (0.2 * rb_inv + 0.8 * ri);
        final float gc = (float) (0.2 * gb_inv + 0.8 * gi);
        final float bc = (float) (0.2 * bb_inv + 0.8 * bi);
        return new ConsistentColor(rc, gc, bc);
    }

    /**
     * Gets the red component of the color, in the range {@code 0.0-1.0}.
     *
     * @return The red component.
     */
    public final float getRed() {
        return red;
    }

    /**
     * Gets the green component of the color, in the range {@code 0.0-1.0}.
     *
     * @return The green component.
     */
    public final float getGreen() {
        return green;
    }

    /**
     * Gets the blue component of the color, in the range {@code 0.0-1.0}.
     *
     * @return The blue component.
     */
    public final float getBlue() {
        return blue;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConsistentColor)) {
            return false;
        }
        ConsistentColor other = (ConsistentColor) o;

        return red == other.red
                && green == other.green
                && blue == other.blue;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(red, green, blue);
    }

    @Override
    public final String toString() {
        final int r = (int) Math.round(red * 255.0);
        final int g = (int) Math.round(green * 255.0);
        final int b = (int) Math.round(blue * 255.0);
        return String.format("#%02x%02x%02x", r, g, b);
    }

    /**
     * Represents a color vision deficiency, used to re-map the angle to map it away from ranges
     * which can not be distinguished by people with the respective color vision deficiencies.
     *
     * @see #generate(CharSequence, ColorVisionDeficiency)
     */
    public enum ColorVisionDeficiency {
        /**
         * No color vision deficiency. This is the default.
         */
        NONE,
        /**
         * Red-green blindness.
         */
        RED_GREEN_BLINDNESS,
        /**
         * Blue blindness.
         */
        BLUE_BLINDNESS
    }
}
