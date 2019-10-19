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

package rocks.xmpp.extensions.version;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.BaseTest;
import rocks.xmpp.core.MockServer;
import rocks.xmpp.core.session.TestXmppSession;
import rocks.xmpp.extensions.disco.ServiceDiscoveryManager;
import rocks.xmpp.extensions.version.model.SoftwareVersion;

import java.util.concurrent.ExecutionException;

/**
 * @author Christian Schudt
 */
public class SoftwareVersionManagerTest extends BaseTest {

    @Test
    public void testSoftwareVersionManager() throws ExecutionException, InterruptedException {
        MockServer mockServer = new MockServer();
        TestXmppSession connection1 = new TestXmppSession(ROMEO, mockServer);
        new TestXmppSession(JULIET, mockServer);
        TestXmppSession connection2 = new TestXmppSession(JULIET, mockServer);
        connection2.getManager(SoftwareVersionManager.class).setSoftwareVersion(new SoftwareVersion("Name", "Version"));
        SoftwareVersionManager softwareVersionManager = connection1.getManager(SoftwareVersionManager.class);
        SoftwareVersion softwareVersion = softwareVersionManager.getSoftwareVersion(JULIET).get();
        Assert.assertNotNull(softwareVersion);
        Assert.assertEquals(softwareVersion.getName(), "Name");
        Assert.assertEquals(softwareVersion.getVersion(), "Version");
    }

    @Test
    public void testSoftwareVersionManagerIfDisabled() {
        MockServer mockServer = new MockServer();
        TestXmppSession connection1 = new TestXmppSession(ROMEO, mockServer);
        TestXmppSession connection2 = new TestXmppSession(JULIET, mockServer);
        connection2.getManager(SoftwareVersionManager.class).setEnabled(false);
        SoftwareVersionManager softwareVersionManager = connection1.getManager(SoftwareVersionManager.class);
        try {
            softwareVersionManager.getSoftwareVersion(JULIET).get();
        } catch (InterruptedException | ExecutionException e) {
            return;
        }
        Assert.fail();
    }

    @Test
    public void testServiceDiscoveryEntry() {
        TestXmppSession connection1 = new TestXmppSession();
        SoftwareVersionManager softwareVersionManager = connection1.getManager(SoftwareVersionManager.class);
        // By default, the manager should be enabled.
        Assert.assertTrue(softwareVersionManager.isEnabled());
        ServiceDiscoveryManager serviceDiscoveryManager = connection1.getManager(ServiceDiscoveryManager.class);
        String feature = "jabber:iq:version";
        Assert.assertTrue(serviceDiscoveryManager.getFeatures().contains(feature));
        softwareVersionManager.setEnabled(false);
        Assert.assertFalse(softwareVersionManager.isEnabled());
        Assert.assertFalse(serviceDiscoveryManager.getFeatures().contains(feature));
    }

}
