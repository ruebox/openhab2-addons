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

package rocks.xmpp.extensions.reach;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.BaseTest;
import rocks.xmpp.core.session.TestXmppSession;
import rocks.xmpp.extensions.disco.ServiceDiscoveryManager;

/**
 * @author Christian Schudt
 */
public class ReachabilityManagerTest extends BaseTest {

    @Test
    public void testServiceDiscoveryEntry() {

        TestXmppSession connection1 = new TestXmppSession();
        ReachabilityManager reachabilityManager = connection1.getManager(ReachabilityManager.class);
        Assert.assertFalse(reachabilityManager.isEnabled());
        ServiceDiscoveryManager serviceDiscoveryManager = connection1.getManager(ServiceDiscoveryManager.class);
        String feature = "urn:xmpp:reach:0";
        Assert.assertFalse(serviceDiscoveryManager.getFeatures().contains(feature));
        reachabilityManager.setEnabled(true);
        Assert.assertTrue(reachabilityManager.isEnabled());
        Assert.assertTrue(serviceDiscoveryManager.getFeatures().contains(feature));
    }
}
