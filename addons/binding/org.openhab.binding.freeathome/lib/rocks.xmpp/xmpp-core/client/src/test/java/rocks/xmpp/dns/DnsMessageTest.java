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

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Schudt
 */
public class DnsMessageTest {

    public static void main(String[] args) throws IOException {
        List<?> answers = DnsResolver.resolveTXT("jabber.ru", "8.8.8.8", 1122);
        List<?> answers2 = DnsResolver.resolveSRV("xmpp-client", "jabber.ru", "8.8.8.8", 1122);
        System.out.println(answers);
        System.out.println(answers2);
    }

    @Test
    public void testQuestion() {
        Question question = new Question("test.domain.de", ResourceRecord.Type.SRV, ResourceRecord.Classification.IN);
        byte[] byteBuffer = question.toByteArray();
        Assert.assertEquals(byteBuffer, new byte[]{4, 't', 'e', 's', 't', 6, 'd', 'o', 'm', 'a', 'i', 'n', 2, 'd', 'e', 0, 0, 33, 0, 1});

        Question question2 = new Question("öäü.com", ResourceRecord.Type.SRV, ResourceRecord.Classification.IN);
        byte[] byteBuffer2 = question2.toByteArray();
        Assert.assertEquals(byteBuffer2, new byte[]{10, 'x', 'n', '-', '-', '4', 'c', 'a', '9', 'a', 't', 3, 'c', 'o', 'm', 0, 0, 33, 0, 1});
    }

    @Test
    public void testName() {
        byte[] test = new byte[]{7, 'e', 'x', 'a', 'm', 'p', 'l', 'e', 0};
        Assert.assertEquals("example", ResourceRecord.parse(ByteBuffer.wrap(test)));
        test = new byte[]{7, 'e', 'x', 'a', 'm', 'p', 'l', 'e', 3, 'c', 'o', 'm', 0};
        Assert.assertEquals("example.com", ResourceRecord.parse(ByteBuffer.wrap(test)));
    }

    @Test
    public void parseCompressedDomainName() {
        byte pointer = 0;
        pointer |= (byte) (pointer | 1 << 7);
        pointer |= (byte) (pointer | 1 << 6);

        byte[] test = new byte[]{1, 'F', 3, 'I', 'S', 'I', 4, 'A', 'R', 'P', 'A', 0,
                3, 'F', 'O', 'O', pointer, 0, pointer, 16, 0
        };
        ByteBuffer buffer = ByteBuffer.wrap(test);
        for (int i = 0; i < 12; i++) {
            buffer.get();
        }
        Assert.assertEquals("FOO.F.ISI.ARPA", ResourceRecord.parse(buffer));
        Assert.assertEquals("F.ISI.ARPA", ResourceRecord.parse(buffer));
    }

    @Test
    public void testTXTRecord() {
        byte[] test = new byte[]{7, 'e', 'x', 'a', 'm', 'p', 'l', 'e', 3, 'c', 'o', 'm'};
        Assert.assertEquals(new TxtRecord(ByteBuffer.wrap(test), test.length).getText(), "examplecom");

        // Test the limit of 255 per character string.
        test = new byte[256];
        StringBuilder sb = new StringBuilder();
        test[0] = (byte) 255;
        for (int i = 1; i < 256; i++) {
            test[i] = 'a';
            sb.append('a');
        }

        Assert.assertEquals(new TxtRecord(ByteBuffer.wrap(test), test.length).getText(), sb.toString());
    }

    @Test
    public void testTXTAttributes() {
        byte[] test = new byte[]{9, 'a', 't', 't', 'r', '=', 't', 'e', 's', 't'};
        TxtRecord txtRecord = new TxtRecord(ByteBuffer.wrap(test), test.length);
        Map<String, String> map = txtRecord.asAttributes();
        Assert.assertEquals(map.get("attr"), "test");

        test = new byte[]{11, 'a', 't', '`', '=', 't', 'r', '=', 't', 'e', 's', 't'};
        txtRecord = new TxtRecord(ByteBuffer.wrap(test), test.length);
        map = txtRecord.asAttributes();
        Assert.assertEquals(map.get("at`=tr"), "test");

        test = new byte[]{2, 'a', 't'};
        txtRecord = new TxtRecord(ByteBuffer.wrap(test), test.length);
        map = txtRecord.asAttributes();
        Assert.assertEquals(map.size(), 0);

        test = new byte[]{10, 'a', 't', 't', 'r', '=', 't', 'e', '=', 's', 't'};
        txtRecord = new TxtRecord(ByteBuffer.wrap(test), test.length);
        map = txtRecord.asAttributes();
        Assert.assertEquals(map.get("attr"), "te=st");
    }
}
