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

package rocks.xmpp.extensions.sm;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.BaseTest;

/**
 * @author Christian Schudt
 */
public class StreamManagerTest extends BaseTest {

    @Test
    public void testIncrementation() {
        StreamManager streamManager = xmppSession.getManager(StreamManager.class);
        streamManager.inboundCount = 0;
        streamManager.incrementInboundStanzaCount();
        Assert.assertEquals(streamManager.inboundCount, 1);

        streamManager.inboundCount = 0xFFFFFFFFL; // 2^32-1
        streamManager.incrementInboundStanzaCount();
        Assert.assertEquals(streamManager.inboundCount, 0);
    }

    @Test
    public void testDiffAfter32BitLimit() {
        Assert.assertEquals(StreamManager.diff(123, 120), 3);
        Assert.assertEquals(StreamManager.diff(0xFFFFFFFFL, 0xFFFFFFFFL - 3), 3);
        Assert.assertEquals(StreamManager.diff(2, 0xFFFFFFFFL), 3);
    }
}
