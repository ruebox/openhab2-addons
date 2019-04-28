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

import java.util.Map;

/**
 * @author Christian Schudt
 */
public class ScramBaseTest {

    @Test
    public void testGetAttributes1() {
        String attributes = "n,,n==2C=3D=2C=3D=3D,r=fyko+d2lbbFgONRv9qkxdawL";
        Map<Character, String> map = ScramBase.getAttributes(attributes);
        Assert.assertEquals(map.size(), 2);
        Assert.assertEquals(map.get('n'), "=2C=3D=2C=3D=3D");
        Assert.assertEquals(map.get('r'), "fyko+d2lbbFgONRv9qkxdawL");
    }

    @Test
    public void testGetAttributes2() {
        String attributes = "r=fyko+d2lbbFgONRv9qkxdawL3rfcNHYJY1ZVvWVs7j,s=QSXCR+Q6sek8bf92,i=4096";
        Map<Character, String> map = ScramBase.getAttributes(attributes);
        Assert.assertEquals(map.size(), 3);
        Assert.assertEquals(map.get('r'), "fyko+d2lbbFgONRv9qkxdawL3rfcNHYJY1ZVvWVs7j");
        Assert.assertEquals(map.get('s'), "QSXCR+Q6sek8bf92");
        Assert.assertEquals(map.get('i'), "4096");
    }

    @Test
    public void testGetAttributes3() {
        String attributes = "c=biws,r=fyko+d2lbbFgONRv9qkxdawL3rfcNHYJY1ZVvWVs7j,p=v0X8v3Bz2T0CJGbJQyF0X+HI4Ts=";
        Map<Character, String> map = ScramBase.getAttributes(attributes);
        Assert.assertEquals(map.size(), 3);
        Assert.assertEquals(map.get('c'), "biws");
        Assert.assertEquals(map.get('r'), "fyko+d2lbbFgONRv9qkxdawL3rfcNHYJY1ZVvWVs7j");
        Assert.assertEquals(map.get('p'), "v0X8v3Bz2T0CJGbJQyF0X+HI4Ts=");
    }

    @Test
    public void testGetAttributes4() {
        String attributes = "v=rmF9pqV8S7suAoZWja4dJRkFsKQ=";
        Map<Character, String> map = ScramBase.getAttributes(attributes);
        Assert.assertEquals(map.size(), 1);
        Assert.assertEquals(map.get('v'), "rmF9pqV8S7suAoZWja4dJRkFsKQ=");
    }

}
