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

package rocks.xmpp.extensions.component;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.IntegrationTest;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.session.XmppSessionConfiguration;
import rocks.xmpp.core.session.debug.ConsoleDebugger;
import rocks.xmpp.extensions.component.accept.ExternalComponent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Christian Schudt
 */
public class ExternalComponentIT extends IntegrationTest {

    private static final Logger logger = Logger.getLogger(ExternalComponentIT.class.getName());


    @Test
    public void testStatusAfterConnect() throws Exception {

        final XmppSession xmppSession = ExternalComponent.create("test."+DOMAIN, "test", XmppSessionConfiguration.builder().debugger(ConsoleDebugger.class).build(), "localhost", 5275);
        final AtomicInteger exceptions = new AtomicInteger();
        final AtomicInteger connecting = new AtomicInteger();
        final AtomicInteger connected = new AtomicInteger();
        xmppSession.addSessionStatusListener(e -> {
            System.out.println(e.getStatus());
            if (e.getStatus() == XmppSession.Status.CONNECTING) {
                connecting.incrementAndGet();
            }
            if (e.getStatus() == XmppSession.Status.CONNECTED) {
                connected.incrementAndGet();
            }
        });


        Executor executor = Executors.newCachedThreadPool();
        int n = 100;
        final CountDownLatch countDownLatch = new CountDownLatch(n);
        // Multiple threads try to connect the session concurrently, although already connected.
        for (int i = 0; i < n; i++) {
            executor.execute(() -> {
                try {
                    xmppSession.connect();
                    Assert.assertEquals(xmppSession.getStatus(), XmppSession.Status.AUTHENTICATED);
                } catch (Throwable e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                    exceptions.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        Assert.assertEquals(exceptions.get(), 0);
        // Should only connect once
        Assert.assertEquals(connecting.get(), 1);
        Assert.assertEquals(connected.get(), 1);
    }

}
