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

package rocks.xmpp.extensions.time;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.BaseTest;
import rocks.xmpp.core.MockServer;
import rocks.xmpp.core.session.TestXmppSession;
import rocks.xmpp.extensions.disco.ServiceDiscoveryManager;

import java.time.OffsetDateTime;
import java.util.concurrent.ExecutionException;

/**
 * @author Christian Schudt
 */
public class EntityTimeManagerTest extends BaseTest {

    @Test
    public void testEntityTimeManager() throws ExecutionException, InterruptedException {
        MockServer mockServer = new MockServer();
        TestXmppSession connection1 = new TestXmppSession(ROMEO, mockServer);
        new TestXmppSession(JULIET, mockServer);
        EntityTimeManager entityTimeManager = connection1.getManager(EntityTimeManager.class);
        OffsetDateTime entityTime = entityTimeManager.getEntityTime(JULIET).get();
        Assert.assertNotNull(entityTime);
    }

    @Test
    public void testEntityTimeIfDisabled() throws InterruptedException {
        MockServer mockServer = new MockServer();
        TestXmppSession connection1 = new TestXmppSession(ROMEO, mockServer);
        TestXmppSession connection2 = new TestXmppSession(JULIET, mockServer);
        connection2.getManager(EntityTimeManager.class).setEnabled(false);
        EntityTimeManager entityTimeManager = connection1.getManager(EntityTimeManager.class);
        try {
            entityTimeManager.getEntityTime(JULIET).get();
        } catch (ExecutionException e) {
            return;
        }
        Assert.fail();
    }

    @Test
    public void testServiceDiscoveryEntry() {
        TestXmppSession connection1 = new TestXmppSession();
        EntityTimeManager entityTimeManager = connection1.getManager(EntityTimeManager.class);
        // By default, the manager should be enabled.
        Assert.assertTrue(entityTimeManager.isEnabled());
        ServiceDiscoveryManager serviceDiscoveryManager = connection1.getManager(ServiceDiscoveryManager.class);
        String feature = "urn:xmpp:time";
        Assert.assertTrue(serviceDiscoveryManager.getFeatures().contains(feature));
        entityTimeManager.setEnabled(false);
        Assert.assertFalse(entityTimeManager.isEnabled());
        Assert.assertFalse(serviceDiscoveryManager.getFeatures().contains(feature));
    }
}
