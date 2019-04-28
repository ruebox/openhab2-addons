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

package rocks.xmpp.extensions.bytestreams.ibb;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.BaseTest;
import rocks.xmpp.core.MockServer;
import rocks.xmpp.core.session.TestXmppSession;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.StanzaErrorException;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.extensions.bytestreams.ByteStreamEvent;
import rocks.xmpp.extensions.bytestreams.ibb.model.InBandByteStream;
import rocks.xmpp.extensions.disco.ServiceDiscoveryManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * @author Christian Schudt
 */
public class IbbTest extends BaseTest {

    @Test
    public void testServiceDiscoveryEntry() {
        TestXmppSession connection1 = new TestXmppSession();
        InBandByteStreamManager inBandBytestreamManager = connection1.getManager(InBandByteStreamManager.class);
        // By default, the manager should be enabled.
        Assert.assertTrue(inBandBytestreamManager.isEnabled());
        ServiceDiscoveryManager serviceDiscoveryManager = connection1.getManager(ServiceDiscoveryManager.class);
        String feature = "http://jabber.org/protocol/ibb";
        Assert.assertTrue(serviceDiscoveryManager.getFeatures().contains(feature));
        inBandBytestreamManager.setEnabled(false);
        Assert.assertFalse(inBandBytestreamManager.isEnabled());
        Assert.assertFalse(serviceDiscoveryManager.getFeatures().contains(feature));
    }

    @Test
    public void testIbbSessionRejection() throws InterruptedException {
        MockServer mockServer = new MockServer();
        final XmppSession xmppSession1 = new TestXmppSession(ROMEO, mockServer);
        final XmppSession xmppSession2 = new TestXmppSession(JULIET, mockServer);
        InBandByteStreamManager inBandBytestreamManager2 = xmppSession2.getManager(InBandByteStreamManager.class);
        inBandBytestreamManager2.addByteStreamListener(ByteStreamEvent::reject);

        InBandByteStreamManager inBandBytestreamManager1 = xmppSession1.getManager(InBandByteStreamManager.class);
        boolean rejected = false;
        try {
            inBandBytestreamManager1.initiateSession(JULIET, UUID.randomUUID().toString(), 4096).get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof StanzaErrorException) {
                if (((StanzaErrorException) e.getCause()).getCondition() == Condition.NOT_ACCEPTABLE) {
                    rejected = true;
                }
            }
        }

        if (!rejected) {
            Assert.fail("Should have been rejected");
        }
    }

    @Test
    public void testIbbInputStream() throws IOException {
        MockServer mockServer = new MockServer();
        final XmppSession xmppSession1 = new TestXmppSession(ROMEO, mockServer);
        IbbSession ibbSession = new IbbSession("1", xmppSession1, ROMEO, 4096, Duration.ofSeconds(5), xmppSession1.getManager(InBandByteStreamManager.class), InBandByteStream.Open.StanzaType.IQ);
        IbbInputStream ibbInputStream = (IbbInputStream) ibbSession.getInputStream();

        IbbSession ibbSession2 = new IbbSession("1", xmppSession1, ROMEO, 4096, Duration.ofSeconds(5), xmppSession1.getManager(InBandByteStreamManager.class), InBandByteStream.Open.StanzaType.IQ);
        IbbInputStream ibbInputStream2 = (IbbInputStream) ibbSession2.getInputStream();


        Random random = new Random();

        for (int i = 0; i < 1000; i++) {
            byte[] b = new byte[4096];
            random.nextBytes(b);
            InBandByteStream.Data data = new InBandByteStream.Data(b, "1", i);
            ibbInputStream.queue.offer(data);
            ibbInputStream2.queue.offer(data);
        }

        int len;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        while ((len = ibbInputStream.read()) > -1) {
            outputStream.write(len);
            if (ibbInputStream.queue.isEmpty()) {
                ibbInputStream.close();
            }
        }


        byte[] buffer = new byte[8048];
        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        while ((len = ibbInputStream2.read(buffer)) > -1) {
            outputStream2.write(buffer, 0, len);
            if (ibbInputStream2.queue.isEmpty()) {
                ibbInputStream2.close();
            }
        }

        byte[] array1 = outputStream.toByteArray();
        Assert.assertEquals(array1.length, 4096000);
        Assert.assertEquals(array1, outputStream2.toByteArray());
    }
}
