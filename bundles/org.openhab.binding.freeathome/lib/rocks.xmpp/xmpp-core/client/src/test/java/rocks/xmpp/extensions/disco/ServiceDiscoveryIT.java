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

package rocks.xmpp.extensions.disco;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import rocks.xmpp.core.IntegrationTest;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.net.client.SocketConnectionConfiguration;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.session.XmppSessionConfiguration;
import rocks.xmpp.extensions.disco.model.items.Item;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

/**
 * @author Christian Schudt
 */
public class ServiceDiscoveryIT extends IntegrationTest {

    private XmppClient xmppSession;

    @BeforeClass
    public void before() throws XmppException {
        XmppSessionConfiguration configuration = XmppSessionConfiguration.builder()
                //.debugger(ConsoleDebugger.class)
                .build();
        xmppSession = XmppClient.create(DOMAIN, configuration, SocketConnectionConfiguration.getDefault());
        xmppSession.connect();
        xmppSession.login(USER_1, PASSWORD_1);
    }

    @Test
    public void testDiscoverService() throws ExecutionException, InterruptedException {
        ServiceDiscoveryManager serviceDiscoveryManager = xmppSession.getManager(ServiceDiscoveryManager.class);
        Collection<Item> items = serviceDiscoveryManager.discoverServices("http://jabber.org/protocol/pubsub").get();
        Assert.assertEquals(items.size(), 1);
    }
}
