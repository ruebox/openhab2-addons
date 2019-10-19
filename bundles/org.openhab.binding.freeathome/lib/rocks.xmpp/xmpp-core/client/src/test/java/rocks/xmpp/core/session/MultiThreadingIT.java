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

package rocks.xmpp.core.session;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.IntegrationTest;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.net.client.SocketConnectionConfiguration;
import rocks.xmpp.core.session.debug.ConsoleDebugger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Christian Schudt
 */
public class MultiThreadingIT extends IntegrationTest {

    private static final Logger logger = Logger.getLogger(MultiThreadingIT.class.getName());

    @Test
    public void shouldNotCloseMoreThanOnce() throws XmppException, InterruptedException {

        final XmppClient xmppSession = XmppClient.create(DOMAIN, SocketConnectionConfiguration.getDefault());

        final AtomicInteger closing = new AtomicInteger();
        final AtomicInteger closed = new AtomicInteger();
        xmppSession.addSessionStatusListener(e -> {
            if (e.getStatus() == XmppSession.Status.CLOSING) {
                closing.incrementAndGet();
            }
            if (e.getStatus() == XmppSession.Status.CLOSED) {
                closed.incrementAndGet();
            }
        });

        xmppSession.connect();

        Executor executor = Executors.newCachedThreadPool();
        final CountDownLatch countDownLatch = new CountDownLatch(100);
        final AtomicReference<Exception> exception = new AtomicReference<>();
        // Multiple threads try to close the session concurrently.
        for (int i = 0; i < 100; i++) {
            executor.execute(() -> {
                try {
                    xmppSession.close();
                } catch (XmppException e) {
                    exception.set(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        Assert.assertEquals(closing.get(), 1);
        Assert.assertEquals(closed.get(), 1);
        if (exception.get() != null) {
            Assert.fail(exception.get().getMessage(), exception.get());
        }
    }

    @Test
    public void shouldNotConnectMoreThanOnceWhileConnected() throws Exception {

        final XmppClient xmppSession = XmppClient.create(DOMAIN, SocketConnectionConfiguration.getDefault());

        final AtomicInteger connecting = new AtomicInteger();
        final AtomicInteger connected = new AtomicInteger();
        xmppSession.addSessionStatusListener(e -> {
            if (e.getStatus() == XmppSession.Status.CONNECTING) {
                connecting.incrementAndGet();
            }
            if (e.getStatus() == XmppSession.Status.CONNECTED) {
                connected.incrementAndGet();
            }
        });

        Executor executor = Executors.newCachedThreadPool();
        final CountDownLatch countDownLatch = new CountDownLatch(100);
        final AtomicReference<Exception> exception = new AtomicReference<>();
        // Multiple threads try to connect the session concurrently.
        for (int i = 0; i < 100; i++) {
            executor.execute(() -> {
                try {
                    xmppSession.connect();
                } catch (XmppException e) {
                    exception.set(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        Assert.assertEquals(connecting.get(), 1);
        Assert.assertEquals(connected.get(), 1);
        if (exception.get() != null) {
            throw exception.get();
        }
    }

    @Test
    public void shouldNotConnectWhileLoggedIn() throws Exception {

        final XmppClient xmppSession = XmppClient.create(DOMAIN, SocketConnectionConfiguration.getDefault());

        final AtomicInteger connecting = new AtomicInteger();
        final AtomicInteger connected = new AtomicInteger();
        xmppSession.addSessionStatusListener(e -> {
            if (e.getStatus() == XmppSession.Status.CONNECTING) {
                connecting.incrementAndGet();
            }
            if (e.getStatus() == XmppSession.Status.CONNECTED) {
                connected.incrementAndGet();
            }
        });

        xmppSession.connect();
        xmppSession.loginAnonymously();

        Executor executor = Executors.newCachedThreadPool();
        final CountDownLatch countDownLatch = new CountDownLatch(100);
        final AtomicReference<Exception> exception = new AtomicReference<>();
        // Multiple threads try to connect the session concurrently, although already connected.
        for (int i = 0; i < 100; i++) {
            executor.execute(() -> {
                try {
                    xmppSession.connect();
                } catch (XmppException e) {
                    exception.set(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        Assert.assertEquals(connecting.get(), 1);
        Assert.assertEquals(connected.get(), 1);
        if (exception.get() != null) {
            throw exception.get();
        }
    }

    @Test
    public void shouldOnlyLoginOnce() throws Exception {

        final XmppClient xmppSession = XmppClient.create(DOMAIN, SocketConnectionConfiguration.getDefault());

        final AtomicInteger authenticating = new AtomicInteger();
        final AtomicInteger authenticated = new AtomicInteger();
        final AtomicInteger exceptions = new AtomicInteger();
        xmppSession.addSessionStatusListener(e -> {
            if (e.getStatus() == XmppSession.Status.AUTHENTICATING) {
                authenticating.incrementAndGet();
            }
            if (e.getStatus() == XmppSession.Status.AUTHENTICATED) {
                authenticated.incrementAndGet();
            }
        });

        xmppSession.connect();

        Executor executor = Executors.newCachedThreadPool();
        final CountDownLatch countDownLatch = new CountDownLatch(100);
        // Multiple threads try to connect the session concurrently, although already connected.
        for (int i = 0; i < 100; i++) {
            executor.execute(() -> {
                try {
                    xmppSession.login(USER_1, PASSWORD_1);
                } catch (Exception e) {
                    exceptions.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        Assert.assertEquals(authenticating.get(), 1);
        Assert.assertEquals(authenticated.get(), 1);
        Assert.assertEquals(exceptions.get(), 0);
    }

    /**
     * Tests, that the behavior when calling connect() from multiple threads.
     * After connect(), it should always be in the correct status {@link rocks.xmpp.core.session.XmppSession.Status#CONNECTED}.
     * Listeners should only be notified once for status changes.
     *
     * @throws Exception
     */
    @Test
    public void testConnectedStatusAfterConnect() throws Exception {

        final XmppClient xmppSession = XmppClient.create(DOMAIN, XmppSessionConfiguration.builder().debugger(ConsoleDebugger.class).build(), SocketConnectionConfiguration.getDefault());
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
                    Assert.assertEquals(xmppSession.getStatus(), XmppSession.Status.CONNECTED);
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

    @Test
    public void testLoginStatusAfterConnect() throws Exception {

        final XmppClient xmppSession = XmppClient.create(DOMAIN, XmppSessionConfiguration.builder().debugger(ConsoleDebugger.class).build(), SocketConnectionConfiguration.getDefault());
        final AtomicInteger exceptions = new AtomicInteger();
        final AtomicInteger connecting = new AtomicInteger();
        final AtomicInteger connected = new AtomicInteger();
        final AtomicInteger authenticating = new AtomicInteger();
        final AtomicInteger authenticated = new AtomicInteger();

        xmppSession.addSessionStatusListener(e -> {
            System.out.println(e.getStatus());
            if (e.getStatus() == XmppSession.Status.CONNECTING) {
                connecting.incrementAndGet();
            }
            if (e.getStatus() == XmppSession.Status.CONNECTED) {
                connected.incrementAndGet();
            }
            if (e.getStatus() == XmppSession.Status.AUTHENTICATING) {
                authenticating.incrementAndGet();
            }
            if (e.getStatus() == XmppSession.Status.AUTHENTICATED) {
                authenticated.incrementAndGet();
            }
        });

        xmppSession.connect();

        Executor executor = Executors.newCachedThreadPool();
        int n = 100;
        final CountDownLatch countDownLatch = new CountDownLatch(n);
        // Multiple threads try to connect the session concurrently, although already connected.
        for (int i = 0; i < n; i++) {
            executor.execute(() -> {
                try {
                    xmppSession.login("admin", "admin");
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
        Assert.assertEquals(authenticating.get(), 1);
        Assert.assertEquals(authenticated.get(), 1);
    }
}
