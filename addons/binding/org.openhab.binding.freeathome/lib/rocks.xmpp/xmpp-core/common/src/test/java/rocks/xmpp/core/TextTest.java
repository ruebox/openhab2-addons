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

package rocks.xmpp.core;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.util.ComparableTestHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Christian Schudt
 */
public class TextTest {

    @Test
    public void testTextComparable() {
        Text text1 = new Text("A", Locale.GERMAN);
        Text text2 = new Text("B", Locale.GERMAN);
        Text text3 = new Text("C", Locale.GERMAN);
        Text text4 = new Text("c", Locale.GERMAN);
        Text text5 = new Text("Ä", Locale.GERMAN);
        Text text6 = new Text("A", Locale.ENGLISH);
        Text text7 = new Text("D");
        Text text8 = new Text("D", Locale.FRENCH);

        List<Text> textList = new ArrayList<>();
        textList.add(text1);
        textList.add(text2);
        textList.add(text3);
        textList.add(text4);
        textList.add(text5);
        textList.add(text6);
        textList.add(text7);
        textList.add(text8);

        Collections.shuffle(textList);
        Collections.sort(textList);

        ComparableTestHelper.checkCompareToContract(textList);
        Assert.assertTrue(ComparableTestHelper.isConsistentWithEquals(textList));

        Assert.assertSame(textList.get(0), text1);
        Assert.assertSame(textList.get(1), text6);
        Assert.assertSame(textList.get(2), text5);
        Assert.assertSame(textList.get(3), text2);
        Assert.assertSame(textList.get(4), text4);
        Assert.assertSame(textList.get(5), text3);
        Assert.assertSame(textList.get(6), text7);
        Assert.assertSame(textList.get(7), text8);
    }
}
