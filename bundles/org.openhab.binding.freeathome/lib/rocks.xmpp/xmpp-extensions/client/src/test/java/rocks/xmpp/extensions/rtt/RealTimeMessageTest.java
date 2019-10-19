/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Christian Schudt
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

package rocks.xmpp.extensions.rtt;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.extensions.rtt.model.RealTimeText;

import java.util.Arrays;
import java.util.List;

/**
 * @author Christian Schudt
 */
public class RealTimeMessageTest {

    private static void applyActionElements(InboundRealTimeMessage realTimeMessage, List<? extends RealTimeText.Action> actions) {
        actions.forEach(realTimeMessage::applyActionElement);
    }

    @Test
    public void shouldComputeEmptyActionElements() {
        Assert.assertTrue(OutboundRealTimeMessage.computeActionElements(null, null).isEmpty());
        Assert.assertTrue(OutboundRealTimeMessage.computeActionElements("", "").isEmpty());
        Assert.assertTrue(OutboundRealTimeMessage.computeActionElements("", null).isEmpty());
        Assert.assertTrue(OutboundRealTimeMessage.computeActionElements(null, "").isEmpty());
        Assert.assertTrue(OutboundRealTimeMessage.computeActionElements("aaa", "aaa").isEmpty());
    }

    @Test
    public void shouldComputeInsertionElements() {
        List<RealTimeText.Action> actionList1 = OutboundRealTimeMessage.computeActionElements(null, "aa");
        Assert.assertEquals(actionList1.size(), 1);
        Assert.assertTrue(actionList1.get(0) instanceof RealTimeText.InsertText);
        Assert.assertEquals(((RealTimeText.InsertText) actionList1.get(0)).getText(), "aa");

        List<RealTimeText.Action> actionList2 = OutboundRealTimeMessage.computeActionElements("abcd11", "abde11");
        Assert.assertEquals(actionList2.size(), 2);
        Assert.assertTrue(actionList2.get(0) instanceof RealTimeText.EraseText);
        Assert.assertEquals((long) ((RealTimeText.EraseText) actionList2.get(0)).getPosition(), 4);
        Assert.assertEquals((long) ((RealTimeText.EraseText) actionList2.get(0)).getNumberOfCharacters(), 2);
        Assert.assertTrue(actionList2.get(1) instanceof RealTimeText.InsertText);
        Assert.assertEquals((long) ((RealTimeText.InsertText) actionList2.get(1)).getPosition(), 2);
        Assert.assertEquals(((RealTimeText.InsertText) actionList2.get(1)).getText(), "de");

        InboundRealTimeMessage realTimeMessage = new InboundRealTimeMessage(null, null, 0, null);
        realTimeMessage.applyActionElement(new RealTimeText.InsertText("abcd11"));
        applyActionElements(realTimeMessage, actionList2);
        Assert.assertEquals(realTimeMessage.getText(), "abde11");
    }

    @Test
    public void shouldComputeInsertionElements2() {
        List<RealTimeText.Action> actionList2 = OutboundRealTimeMessage.computeActionElements("Hello Bob, this is Alice!", "Hello, this is Alice!");
        Assert.assertEquals(actionList2.size(), 1);
        Assert.assertTrue(actionList2.get(0) instanceof RealTimeText.EraseText);
        Assert.assertEquals((int) ((RealTimeText.EraseText) actionList2.get(0)).getPosition(), 9);
        Assert.assertEquals((int) ((RealTimeText.EraseText) actionList2.get(0)).getNumberOfCharacters(), 4);

        InboundRealTimeMessage realTimeMessage = new InboundRealTimeMessage(null, null, 0, null);
        realTimeMessage.applyActionElement(new RealTimeText.InsertText("Hello Bob, this is Alice!"));
        applyActionElements(realTimeMessage, actionList2);
        Assert.assertEquals(realTimeMessage.getText(), "Hello, this is Alice!");
    }

    @Test
    public void shouldComputeInsertionElements3() {
        List<RealTimeText.Action> actionList2 = OutboundRealTimeMessage.computeActionElements("a", "aaa");
        Assert.assertEquals(actionList2.size(), 1);
        Assert.assertTrue(actionList2.get(0) instanceof RealTimeText.InsertText);
        Assert.assertEquals(((RealTimeText.InsertText) actionList2.get(0)).getPosition(), null);
        Assert.assertEquals(((RealTimeText.InsertText) actionList2.get(0)).getText(), "aa");

        InboundRealTimeMessage realTimeMessage = new InboundRealTimeMessage(null, null, 0, null);
        realTimeMessage.applyActionElement(new RealTimeText.InsertText("a"));
        applyActionElements(realTimeMessage, actionList2);
        Assert.assertEquals(realTimeMessage.getText(), "aaa");
    }

    @Test
    public void shouldComputeInsertionElements4() {
        List<RealTimeText.Action> actionList2 = OutboundRealTimeMessage.computeActionElements("fefefefe", "fefefe");
        Assert.assertEquals(actionList2.size(), 1);
        Assert.assertTrue(actionList2.get(0) instanceof RealTimeText.EraseText);
        Assert.assertEquals(((RealTimeText.EraseText) actionList2.get(0)).getPosition(), null);
        Assert.assertEquals((int) ((RealTimeText.EraseText) actionList2.get(0)).getNumberOfCharacters(), 2);

        InboundRealTimeMessage realTimeMessage = new InboundRealTimeMessage(null, null, 0, null);
        realTimeMessage.applyActionElement(new RealTimeText.InsertText("fefefefe"));
        applyActionElements(realTimeMessage, actionList2);
        Assert.assertEquals(realTimeMessage.getText(), "fefefe");
    }

    @Test
    public void shouldComputeInsertionElements5() {
        List<RealTimeText.Action> actionList2 = OutboundRealTimeMessage.computeActionElements("Hello, this is Alice!", "Hello Bob, this is Alice!");
        Assert.assertEquals(actionList2.size(), 1);
        Assert.assertTrue(actionList2.get(0) instanceof RealTimeText.InsertText);
        Assert.assertEquals((int) ((RealTimeText.InsertText) actionList2.get(0)).getPosition(), 5);
        Assert.assertEquals(((RealTimeText.InsertText) actionList2.get(0)).getText(), " Bob");

        InboundRealTimeMessage realTimeMessage = new InboundRealTimeMessage(null, null, 0, null);
        realTimeMessage.applyActionElement(new RealTimeText.InsertText("Hello, this is Alice!"));
        applyActionElements(realTimeMessage, actionList2);
        Assert.assertEquals(realTimeMessage.getText(), "Hello Bob, this is Alice!");
    }

    @Test
    public void shouldComputeInsertionElements6() {
        List<RealTimeText.Action> actionList2 = OutboundRealTimeMessage.computeActionElements("abbbbbbbaaaaaaaaaaaa", "abbbbbbaaaaaaaaaaaa");
        Assert.assertEquals(actionList2.size(), 1);
        Assert.assertTrue(actionList2.get(0) instanceof RealTimeText.EraseText);
        Assert.assertEquals((int) ((RealTimeText.EraseText) actionList2.get(0)).getPosition(), 8);
        Assert.assertNull(((RealTimeText.EraseText) actionList2.get(0)).getNumberOfCharacters());

        InboundRealTimeMessage realTimeMessage = new InboundRealTimeMessage(null, null, 0, null);
        realTimeMessage.applyActionElement(new RealTimeText.InsertText("abbbbbbbaaaaaaaaaaaa"));
        applyActionElements(realTimeMessage, actionList2);
        Assert.assertEquals(realTimeMessage.getText(), "abbbbbbaaaaaaaaaaaa");
    }

    @Test
    public void shouldComputeInsertionHighSurrogate() {
        List<RealTimeText.Action> actionList = OutboundRealTimeMessage.computeActionElements("1\uDBFF\uDFFC23\uDBFF\uDFFCa", "1\uDBFF\uDFFC23\uDBFF\uDFFCba");
        Assert.assertEquals(actionList.size(), 1);
        Assert.assertTrue(actionList.get(0) instanceof RealTimeText.InsertText);
        Assert.assertEquals((int) ((RealTimeText.InsertText) actionList.get(0)).getPosition(), 5);
        Assert.assertEquals(((RealTimeText.InsertText) actionList.get(0)).getText(), "b");

        InboundRealTimeMessage realTimeMessage = new InboundRealTimeMessage(null, null, 0, null);
        realTimeMessage.applyActionElement(new RealTimeText.InsertText("1\uDBFF\uDFFC23\uDBFF\uDFFCa"));
        applyActionElements(realTimeMessage, actionList);
        Assert.assertEquals(realTimeMessage.getText(), "1\uDBFF\uDFFC23\uDBFF\uDFFCba");
    }

    @Test
    public void shouldComputeErasureHighSurrogate() {
        List<RealTimeText.Action> actionList = OutboundRealTimeMessage.computeActionElements("a\uDBFF\uDFFCb\uDBFF\uDFFCc\uDBFF\uDFFCd", "ad");
        Assert.assertEquals(actionList.size(), 1);
        Assert.assertTrue(actionList.get(0) instanceof RealTimeText.EraseText);
        Assert.assertEquals((int) ((RealTimeText.EraseText) actionList.get(0)).getPosition(), 6);
        Assert.assertEquals(((RealTimeText.EraseText) actionList.get(0)).getNumberOfCharacters(), Integer.valueOf(5));

        InboundRealTimeMessage realTimeMessage = new InboundRealTimeMessage(null, null, 0, null);
        realTimeMessage.applyActionElement(new RealTimeText.InsertText("a\uDBFF\uDFFCb\uDBFF\uDFFCc\uDBFF\uDFFCd"));
        applyActionElements(realTimeMessage, actionList);
        Assert.assertEquals(realTimeMessage.getText(), "ad");
    }


    @Test
    public void testBoundaries() {
        int[] boundaries1 = OutboundRealTimeMessage.determineBounds("fefefe", "fefe");
        Assert.assertEquals(boundaries1[0], 4);
        Assert.assertEquals(boundaries1[1], 6);

        int[] boundaries2 = OutboundRealTimeMessage.determineBounds("aabbbcc", "aaddcc");
        Assert.assertEquals(boundaries2[0], 2);
        Assert.assertEquals(boundaries2[1], 5);

        int[] boundaries3 = OutboundRealTimeMessage.determineBounds("aabbcc", "aadddcc");
        Assert.assertEquals(boundaries3[0], 2);
        Assert.assertEquals(boundaries3[1], 4);

        int[] boundaries4 = OutboundRealTimeMessage.determineBounds("abc", "12345");
        Assert.assertEquals(boundaries4[0], 0);
        Assert.assertEquals(boundaries4[1], 3);

        int[] boundaries5 = OutboundRealTimeMessage.determineBounds("aaa", "aaaaa");
        Assert.assertEquals(boundaries5[0], 3);
        Assert.assertEquals(boundaries5[1], 3);

        int[] boundaries6 = OutboundRealTimeMessage.determineBounds("aaa", "aaa11");
        Assert.assertEquals(boundaries6[0], 3);
        Assert.assertEquals(boundaries6[1], 3);

        int[] boundaries7 = OutboundRealTimeMessage.determineBounds("Hello, this is Alice!", "Hello Bob, this is Alice!");
        Assert.assertEquals(boundaries7[0], 5);
        Assert.assertEquals(boundaries7[1], 5);

        int[] boundaries8 = OutboundRealTimeMessage.determineBounds("afcd", "abcd");
        Assert.assertEquals(boundaries8[0], 1);
        Assert.assertEquals(boundaries8[1], 2);

        int[] boundaries9 = OutboundRealTimeMessage.determineBounds("acd", "abcd");
        Assert.assertEquals(boundaries9[0], 1);
        Assert.assertEquals(boundaries9[1], 1);

        int[] boundaries10 = OutboundRealTimeMessage.determineBounds("abbbbbbbaaaaaaaaaaaa", "abbbbbbaaaaaaaaaaaa");
        Assert.assertEquals(boundaries10[0], 7);
        Assert.assertEquals(boundaries10[1], 8);
    }

    @Test
    public void testApplyActions1() {
        RealTimeText.InsertText insertText = new RealTimeText.InsertText("Hello Bob, this is Alice!");
        RealTimeText.EraseText eraseText = new RealTimeText.EraseText(4, 9);
        InboundRealTimeMessage realTimeMessage = new InboundRealTimeMessage(null, null, 0, null);
        applyActionElements(realTimeMessage, Arrays.asList(insertText, eraseText));
        Assert.assertEquals(realTimeMessage.getText(), "Hello, this is Alice!");
    }

    @Test
    public void testApplyActions2() {
        RealTimeText.InsertText insertText1 = new RealTimeText.InsertText("Hello, this is Alice!");
        RealTimeText.InsertText insertText2 = new RealTimeText.InsertText(" Bob", 5);
        InboundRealTimeMessage realTimeMessage = new InboundRealTimeMessage(null, null, 0, null);
        applyActionElements(realTimeMessage, Arrays.asList(insertText1, insertText2));
        Assert.assertEquals(realTimeMessage.getText(), "Hello Bob, this is Alice!");
    }

    @Test
    public void testApplyActions3() {
        RealTimeText.InsertText insertText1 = new RealTimeText.InsertText("Hello Bob, tihsd is Alice!");
        RealTimeText.EraseText eraseText = new RealTimeText.EraseText(5, 16);
        RealTimeText.InsertText insertText2 = new RealTimeText.InsertText("this", 11);
        InboundRealTimeMessage realTimeMessage = new InboundRealTimeMessage(null, null, 0, null);
        applyActionElements(realTimeMessage, Arrays.asList(insertText1, eraseText, insertText2));
        Assert.assertEquals(realTimeMessage.getText(), "Hello Bob, this is Alice!");
    }

    @Test
    public void testApplyActions4() {
        RealTimeText.InsertText insertText1 = new RealTimeText.InsertText("Helo");
        RealTimeText.EraseText eraseText1 = new RealTimeText.EraseText();
        RealTimeText.InsertText insertText2 = new RealTimeText.InsertText("lo...planet");
        RealTimeText.EraseText eraseText2 = new RealTimeText.EraseText(6);
        RealTimeText.InsertText insertText3 = new RealTimeText.InsertText(" World");
        RealTimeText.EraseText eraseText3 = new RealTimeText.EraseText(3, 8);
        RealTimeText.InsertText insertText4 = new RealTimeText.InsertText(" there,", 5);
        InboundRealTimeMessage realTimeMessage = new InboundRealTimeMessage(null, null, 0, null);
        applyActionElements(realTimeMessage, Arrays.asList(insertText1, eraseText1, insertText2, eraseText2, insertText3, eraseText3, insertText4));
        Assert.assertEquals(realTimeMessage.getText(), "Hello there, World");
    }
}
