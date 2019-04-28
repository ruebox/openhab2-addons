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

package rocks.xmpp.extensions.shim;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.BaseTest;
import rocks.xmpp.core.MockServer;
import rocks.xmpp.core.session.TestXmppSession;
import rocks.xmpp.extensions.disco.ServiceDiscoveryManager;
import rocks.xmpp.extensions.disco.model.info.InfoNode;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Christian Schudt
 */
public class HeadersManagerTest extends BaseTest {

    @Test
    public void testServiceDiscoveryIfHeadersAreDisabled() throws InterruptedException {
        MockServer mockServer = new MockServer();
        TestXmppSession connection1 = new TestXmppSession(JULIET, mockServer);
        TestXmppSession connection2 = new TestXmppSession(ROMEO, mockServer);

        // JULIET supports the following headers:
        HeaderManager headerManager = connection1.getManager(HeaderManager.class);
        headerManager.getSupportedHeaders().add("In-Reply-To");
        headerManager.getSupportedHeaders().add("Keywords");

        ServiceDiscoveryManager serviceDiscoveryManager = connection2.getManager(ServiceDiscoveryManager.class);
        InfoNode infoNode = null;
        try {
            infoNode = serviceDiscoveryManager.discoverInformation(JULIET).get();
        } catch (ExecutionException e) {
            Assert.fail();
        }
        // By default headers are not support, unless they are enabled.
        Assert.assertFalse(infoNode.getFeatures().contains("http://jabber.org/protocol/shim"));
    }

    @Test
    public void testServiceDiscoveryIfHeadersAreEnabled() throws InterruptedException {
        MockServer mockServer = new MockServer();
        TestXmppSession connection1 = new TestXmppSession(JULIET, mockServer);
        TestXmppSession connection2 = new TestXmppSession(ROMEO, mockServer);

        // JULIET supports the following headers:
        HeaderManager headerManager = connection1.getManager(HeaderManager.class);
        headerManager.getSupportedHeaders().add("In-Reply-To");
        headerManager.getSupportedHeaders().add("Keywords");
        headerManager.setEnabled(true);

        ServiceDiscoveryManager serviceDiscoveryManager = connection2.getManager(ServiceDiscoveryManager.class);
        InfoNode infoNode = null;
        try {
            infoNode = serviceDiscoveryManager.discoverInformation(JULIET).get();
        } catch (ExecutionException e) {
            Assert.fail();
        }
        Assert.assertTrue(infoNode.getFeatures().contains("http://jabber.org/protocol/shim"));

        try {
            InfoNode infoNode1 = serviceDiscoveryManager.discoverInformation(JULIET, "http://jabber.org/protocol/shim").get();
            Assert.assertTrue(infoNode1.getFeatures().contains("http://jabber.org/protocol/shim#In-Reply-To"));
            Assert.assertTrue(infoNode1.getFeatures().contains("http://jabber.org/protocol/shim#Keywords"));
        } catch (ExecutionException e) {
            Assert.fail();
        }
    }

    @Test
    public void testDiscoverSupportedHeaders() throws ExecutionException, InterruptedException {
        MockServer mockServer = new MockServer();
        TestXmppSession connection1 = new TestXmppSession(JULIET, mockServer);
        TestXmppSession connection2 = new TestXmppSession(ROMEO, mockServer);

        // JULIET supports the following headers:
        HeaderManager headerManager = connection1.getManager(HeaderManager.class);
        headerManager.getSupportedHeaders().add("In-Reply-To");
        headerManager.getSupportedHeaders().add("Keywords");
        headerManager.setEnabled(true);

        HeaderManager headerManager2 = connection2.getManager(HeaderManager.class);
        List<String> headers = headerManager2.discoverSupportedHeaders(JULIET).get();

        Assert.assertEquals(headers.size(), 2);
        Assert.assertTrue(headers.contains("In-Reply-To"));
        Assert.assertTrue(headers.contains("Keywords"));
    }
}
