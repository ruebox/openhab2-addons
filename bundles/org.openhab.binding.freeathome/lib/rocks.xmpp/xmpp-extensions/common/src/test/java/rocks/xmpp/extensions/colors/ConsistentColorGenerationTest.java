/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Christian Schudt
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


import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;

import java.math.BigDecimal;

/**
 * @author Christian Schudt
 */
public class ConsistentColorGenerationTest {

    @Test
    public void testNoColorVisionDeficiencyCorrection() {

        double angle1 = ConsistentColor.generateAngle("Romeo", ConsistentColor.ColorVisionDeficiency.NONE);
        assertEqualsRounded(angle1, 5.711769, 6);

        double angle2 = ConsistentColor.generateAngle(Jid.of("juliet@capulet.lit"), ConsistentColor.ColorVisionDeficiency.NONE);
        assertEqualsRounded(angle2, 3.654957, 6);

        double angle3 = ConsistentColor.generateAngle("\uD83D\uDE3A", ConsistentColor.ColorVisionDeficiency.NONE);
        assertEqualsRounded(angle3, 5.780607, 6);

        ConsistentColor color1 = ConsistentColor.generate("Romeo");
        assertEqualsRounded(color1.getRed(), 0.281, 3);
        assertEqualsRounded(color1.getGreen(), 0.790, 3);
        assertEqualsRounded(color1.getBlue(), 1.0, 3);

        ConsistentColor color2 = ConsistentColor.generate(Jid.of("juliet@capulet.lit"));
        assertEqualsRounded(color2.getRed(), 0.337, 3);
        assertEqualsRounded(color2.getGreen(), 1.0, 3);
        assertEqualsRounded(color2.getBlue(), 0.0, 3);

        ConsistentColor color3 = ConsistentColor.generate("\uD83D\uDE3A");
        assertEqualsRounded(color3.getRed(), 0.347, 3);
        assertEqualsRounded(color3.getGreen(), 0.756, 3);
        assertEqualsRounded(color3.getBlue(), 1.0, 3);
    }

    @Test
    public void testRedGreenBlindnessCorrection() {

        double angle1 = ConsistentColor.generateAngle("Romeo", ConsistentColor.ColorVisionDeficiency.RED_GREEN_BLINDNESS);
        assertEqualsRounded(angle1, 2.855884, 6);

        double angle2 = ConsistentColor.generateAngle(Jid.of("juliet@capulet.lit"), ConsistentColor.ColorVisionDeficiency.RED_GREEN_BLINDNESS);
        assertEqualsRounded(angle2, 1.827478, 6);

        double angle3 = ConsistentColor.generateAngle("\uD83D\uDE3A", ConsistentColor.ColorVisionDeficiency.RED_GREEN_BLINDNESS);
        assertEqualsRounded(angle3, 2.890304, 6);

        ConsistentColor color1 = ConsistentColor.generate("Romeo", ConsistentColor.ColorVisionDeficiency.RED_GREEN_BLINDNESS);
        assertEqualsRounded(color1.getRed(), 0.938, 3);
        assertEqualsRounded(color1.getGreen(), 0.799, 3);
        assertEqualsRounded(color1.getBlue(), 0.0, 3);

        ConsistentColor color2 = ConsistentColor.generate(Jid.of("juliet@capulet.lit"), ConsistentColor.ColorVisionDeficiency.RED_GREEN_BLINDNESS);
        assertEqualsRounded(color2.getRed(), 1.0, 3);
        assertEqualsRounded(color2.getGreen(), 0.420, 3);
        assertEqualsRounded(color2.getBlue(), 0.499, 3);

        ConsistentColor color3 = ConsistentColor.generate("\uD83D\uDE3A", ConsistentColor.ColorVisionDeficiency.RED_GREEN_BLINDNESS);
        assertEqualsRounded(color3.getRed(), 0.912, 3);
        assertEqualsRounded(color3.getGreen(), 0.812, 3);
        assertEqualsRounded(color3.getBlue(), 0.0, 3);
    }

    @Test
    public void testBlueBlindnessCorrection() {

        double angle1 = ConsistentColor.generateAngle("Romeo", ConsistentColor.ColorVisionDeficiency.BLUE_BLINDNESS);
        assertEqualsRounded(angle1, 4.426681, 6);

        double angle2 = ConsistentColor.generateAngle(Jid.of("juliet@capulet.lit"), ConsistentColor.ColorVisionDeficiency.BLUE_BLINDNESS);
        assertEqualsRounded(angle2, 3.398275, 6);

        double angle3 = ConsistentColor.generateAngle("\uD83D\uDE3A", ConsistentColor.ColorVisionDeficiency.BLUE_BLINDNESS);
        assertEqualsRounded(angle3, 4.461100, 6);

        ConsistentColor color1 = ConsistentColor.generate("Romeo", ConsistentColor.ColorVisionDeficiency.BLUE_BLINDNESS);
        assertEqualsRounded(color1.getRed(), 0.031, 3);
        assertEqualsRounded(color1.getGreen(), 1.0, 3);
        assertEqualsRounded(color1.getBlue(), 0.472, 3);

        ConsistentColor color2 = ConsistentColor.generate(Jid.of("juliet@capulet.lit"), ConsistentColor.ColorVisionDeficiency.BLUE_BLINDNESS);
        assertEqualsRounded(color2.getRed(), 0.548, 3);
        assertEqualsRounded(color2.getGreen(), 0.998, 3);
        assertEqualsRounded(color2.getBlue(), 0.0, 3);

        ConsistentColor color3 = ConsistentColor.generate("\uD83D\uDE3A", ConsistentColor.ColorVisionDeficiency.BLUE_BLINDNESS);
        assertEqualsRounded(color3.getRed(), 0.031, 3);
        assertEqualsRounded(color3.getGreen(), 1.0, 3);
        assertEqualsRounded(color3.getBlue(), 0.505, 3);
    }

    private static void assertEqualsRounded(double actual, double expected, int scale) {
        Assert.assertEquals(BigDecimal.valueOf(actual).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue(), expected);
    }
}
