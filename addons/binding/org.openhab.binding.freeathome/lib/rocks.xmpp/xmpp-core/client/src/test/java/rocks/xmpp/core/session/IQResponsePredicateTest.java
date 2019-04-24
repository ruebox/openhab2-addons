/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Christian Schudt
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
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.stanza.model.IQ;

import java.util.function.Predicate;

/**
 * @author Christian Schudt
 */
public class IQResponsePredicateTest {

    private static final Jid CONNECTED_RESOURCE = Jid.of("connected@test/res");

    private static final Jid CONNECTED_DOMAIN = Jid.of("test");

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTypeResult() {
        IQ originalIQRequest = new IQ(IQ.Type.RESULT, "");
        new IQResponsePredicate(originalIQRequest, CONNECTED_RESOURCE);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTypeError() {
        IQ originalIQRequest = new IQ(IQ.Type.ERROR, "");
        new IQResponsePredicate(originalIQRequest, CONNECTED_RESOURCE);
    }

    @Test
    public void testNoToInReply() {
        IQ originalIQRequest = new IQ(IQ.Type.GET, "");
        Predicate<IQ> predicate = new IQResponsePredicate(originalIQRequest, CONNECTED_RESOURCE);

        IQ iqResponse = new IQ(IQ.Type.RESULT, "", originalIQRequest.getId());

        Assert.assertTrue(predicate.test(iqResponse));
    }

    @Test
    public void testToIsBareJid() {

        IQ originalIQRequest = new IQ(CONNECTED_RESOURCE.asBareJid(), IQ.Type.GET, "");
        Predicate<IQ> predicate = new IQResponsePredicate(originalIQRequest, CONNECTED_RESOURCE);

        // No from attribute (valid)
        IQ iqResponse = new IQ(IQ.Type.RESULT, "", originalIQRequest.getId());
        Assert.assertTrue(predicate.test(iqResponse));

        // From attribute = bare JID of connected resource (valid)
        IQ iqResponse2 = new IQ(IQ.Type.RESULT, "", originalIQRequest.getId());
        iqResponse2.setFrom(CONNECTED_RESOURCE.asBareJid());
        Assert.assertTrue(predicate.test(iqResponse2));

        // From attribute = Full Jid of connected resource (valid)
        IQ iqResponse3 = new IQ(IQ.Type.RESULT, "", originalIQRequest.getId());
        iqResponse3.setFrom(CONNECTED_RESOURCE);
        Assert.assertFalse(predicate.test(iqResponse3));
    }

    @Test
    public void testToIsEmpty() {

        IQ originalIQRequest = new IQ(IQ.Type.GET, "");
        Predicate<IQ> predicate = new IQResponsePredicate(originalIQRequest, CONNECTED_RESOURCE);

        // No from attribute (valid)
        IQ iqResponse = new IQ(IQ.Type.RESULT, "", originalIQRequest.getId());
        Assert.assertTrue(predicate.test(iqResponse));

        // From attribute = bare JID of connected resource (valid)
        IQ iqResponse2 = new IQ(IQ.Type.RESULT, "", originalIQRequest.getId());
        iqResponse2.setFrom(CONNECTED_RESOURCE.asBareJid());
        Assert.assertTrue(predicate.test(iqResponse2));

        // From attribute = Full Jid of connected resource (valid!?)
        IQ iqResponse3 = new IQ(IQ.Type.RESULT, "", originalIQRequest.getId());
        iqResponse3.setFrom(CONNECTED_RESOURCE);
        Assert.assertTrue(predicate.test(iqResponse3));

        // From attribute = Domain of server (valid)
        IQ iqResponse4 = new IQ(IQ.Type.RESULT, "", originalIQRequest.getId());
        iqResponse4.setFrom(Jid.ofDomain(CONNECTED_RESOURCE.getDomain()));
        Assert.assertTrue(predicate.test(iqResponse4));
    }

    @Test
    public void testToIsFullJid() {
        Jid jid = Jid.of("test@test/full");
        IQ originalIQRequest = new IQ(jid, IQ.Type.GET, "");
        Predicate<IQ> predicate = new IQResponsePredicate(originalIQRequest, CONNECTED_RESOURCE);

        IQ iqResponse = new IQ(IQ.Type.RESULT, "", originalIQRequest.getId());
        iqResponse.setFrom(jid);
        Assert.assertTrue(predicate.test(iqResponse));

        IQ iqResponse2 = new IQ(IQ.Type.RESULT, "", originalIQRequest.getId());
        Assert.assertFalse(predicate.test(iqResponse2));
    }

    @Test
    public void testNullConnectedResource() {
        IQ originalIQRequest = new IQ(IQ.Type.SET, "");
        Predicate<IQ> predicate = new IQResponsePredicate(originalIQRequest, null);

        IQ iqResponse = new IQ(IQ.Type.RESULT, "", originalIQRequest.getId());
        iqResponse.setFrom(CONNECTED_DOMAIN);
        Assert.assertTrue(predicate.test(iqResponse));
    }

    @Test
    public void testUnavailableResource() {
        IQ originalIQRequest = new IQ(Jid.of("test@bla/test"), IQ.Type.SET, "");
        Predicate<IQ> predicate = new IQResponsePredicate(originalIQRequest, CONNECTED_RESOURCE);

        IQ iqResponse = new IQ(IQ.Type.RESULT, "", originalIQRequest.getId());
        iqResponse.setFrom(Jid.of("test"));
        Assert.assertFalse(predicate.test(iqResponse));
    }

    @Test
    public void testSpoofed() {
        IQ originalIQRequest = new IQ(Jid.of("connected@test/res2"), IQ.Type.SET, "");
        Predicate<IQ> predicate = new IQResponsePredicate(originalIQRequest, CONNECTED_RESOURCE);

        IQ iqResponse = new IQ(IQ.Type.RESULT, "", originalIQRequest.getId());
        iqResponse.setFrom(Jid.of("test"));
        Assert.assertFalse(predicate.test(iqResponse));

        IQ iqResponse2 = new IQ(IQ.Type.RESULT, "", originalIQRequest.getId());
        Assert.assertFalse(predicate.test(iqResponse2));
    }
}