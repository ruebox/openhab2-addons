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

package rocks.xmpp.extensions.rpc;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.BaseTest;
import rocks.xmpp.core.MockServer;
import rocks.xmpp.core.session.TestXmppSession;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.extensions.disco.ServiceDiscoveryManager;
import rocks.xmpp.extensions.rpc.model.Value;

import java.util.concurrent.ExecutionException;

/**
 * @author Christian Schudt
 */
public class RpcManagerTest extends BaseTest {

    @Test
    public void testServiceDiscoveryEntry() {
        TestXmppSession connection1 = new TestXmppSession();
        RpcManager rpcManager = connection1.getManager(RpcManager.class);
        Assert.assertFalse(rpcManager.isEnabled());
        ServiceDiscoveryManager serviceDiscoveryManager = connection1.getManager(ServiceDiscoveryManager.class);
        String feature = "jabber:iq:rpc";
        Assert.assertFalse(serviceDiscoveryManager.getFeatures().contains(feature));
        rpcManager.setEnabled(true);
        Assert.assertTrue(rpcManager.isEnabled());
        Assert.assertTrue(serviceDiscoveryManager.getFeatures().contains(feature));
    }

    @Test
    public void testCall() throws ExecutionException, InterruptedException {
        MockServer mockServer = new MockServer();

        XmppSession xmppSession1 = new TestXmppSession(ROMEO, mockServer);
        XmppSession xmppSession2 = new TestXmppSession(JULIET, mockServer);

        RpcManager rpcManager = xmppSession1.getManager(RpcManager.class);
        //rpcManager.executorService = new SameThreadExecutorService();
        rpcManager.setEnabled(true);
        rpcManager.setRpcHandler((requester, methodName, parameters) -> {
            if (methodName.equals("square")) {
                return Value.of(parameters.get(0).getAsInteger() * parameters.get(0).getAsInteger());
            }
            return null;
        });

        Value result = xmppSession2.getManager(RpcManager.class).call(ROMEO, "square", Value.of(2)).get();
        Assert.assertEquals(result.getAsInteger().intValue(), 4);
    }

    @Test
    public void testRpcException() {
        MockServer mockServer = new MockServer();

        XmppSession xmppSession1 = new TestXmppSession(ROMEO, mockServer);
        XmppSession xmppSession2 = new TestXmppSession(JULIET, mockServer);

        RpcManager rpcManager = xmppSession1.getManager(RpcManager.class);
        rpcManager.setEnabled(true);

        //rpcManager.executorService = new SameThreadExecutorService();

        rpcManager.setRpcHandler((requester, methodName, parameters) -> {
            if (methodName.equals("fault")) {
                throw new RpcException(2, "faulty");
            }
            return null;
        });

        try {
            xmppSession2.getManager(RpcManager.class).call(ROMEO, "fault", Value.of(2)).get();
        } catch (Exception e) {
            Assert.assertEquals(((RpcException) e.getCause()).getFaultCode(), 2);
            Assert.assertEquals(((RpcException) e.getCause()).getFaultString(), "faulty");
            return;
        }
        Assert.fail("RpcException expected.");
    }

//    @Test
//    public void testStanzaException() throws XmppException, RpcException {
//        MockServer mockServer = new MockServer();
//
//        XmppSession xmppSession1 = new TestXmppSession(ROMEO, mockServer);
//        XmppSession xmppSession2 = new TestXmppSession(JULIET, mockServer);
//
//        RpcManager rpcManager = xmppSession1.getManager(RpcManager.class);
//        rpcManager.setEnabled(true);
//        rpcManager.setRpcHandler(new RpcHandler() {
//            @Override
//            public Value process(Jid requester, String methodName, List<Value> parameters) throws RpcException {
//                if (methodName.equals("fault")) {
//                    IQ iq = new IQ(IQ.Type.ERROR);
//                    iq.setError(new StanzaError(new Forbidden()));
//                    throw new StanzaException(iq);
//                }
//                return null;
//            }
//        });
//
//        try {
//            xmppSession2.getManager(RpcManager.class).call(ROMEO, "fault", new Value(2));
//        } catch (StanzaException e) {
//            Assert.assertTrue(e.getStanza().getError().getCondition() instanceof Forbidden);
//            return;
//        }
//        Assert.fail("StanzaException expected.");
//    }
}
