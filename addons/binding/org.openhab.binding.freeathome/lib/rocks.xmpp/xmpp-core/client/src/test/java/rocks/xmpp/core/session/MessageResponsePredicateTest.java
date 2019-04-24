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
import rocks.xmpp.core.stanza.model.Message;

import java.util.function.Predicate;

/**
 * @author Christian Schudt
 */
public class MessageResponsePredicateTest {

    private static final Jid CONNECTED_RESOURCE = Jid.of("connected@test/res");

    private static final Jid CONNECTED_DOMAIN = Jid.of("test");

    @Test
    public void testWrongIdInResponse() {
        Message originalMessage = new Message();
        originalMessage.setTo(Jid.of("test@test"));
        originalMessage.setId("1");
        Predicate<Message> predicate = new MessageResponsePredicate(originalMessage, CONNECTED_RESOURCE);

        Message response = new Message();
        response.setFrom(Jid.of("test@test"));
        response.setId("2");

        Assert.assertFalse(predicate.test(response));
    }

    @Test
    public void testNoToInMessageAndResponseNotFromUser() {
        Message originalMessage = new Message();
        originalMessage.setId("1");
        Predicate<Message> predicate = new MessageResponsePredicate(originalMessage, CONNECTED_RESOURCE);

        Message response = new Message();
        response.setFrom(Jid.of("otherjid@test"));
        response.setId(originalMessage.getId());

        Assert.assertFalse(predicate.test(response));
    }

    @Test
    public void testNoFromInResponse() {
        Message originalMessage = new Message();
        originalMessage.setTo(CONNECTED_RESOURCE.asBareJid());
        Predicate<Message> predicate = new MessageResponsePredicate(originalMessage, CONNECTED_RESOURCE);

        Message response = new Message();
        response.setId(originalMessage.getId());

        Assert.assertTrue(predicate.test(response));
    }

    @Test
    public void testNoToInMessage() {
        Message originalMessage = new Message();
        Predicate<Message> predicate = new MessageResponsePredicate(originalMessage, CONNECTED_RESOURCE);

        Message response = new Message();
        response.setId(originalMessage.getId());

        Assert.assertTrue(predicate.test(response));

        Message response2 = new Message();
        response2.setId(originalMessage.getId());
        response2.setFrom(CONNECTED_RESOURCE.asBareJid());

        Assert.assertTrue(predicate.test(response2));

        Message response3 = new Message();
        response3.setId(originalMessage.getId());
        response3.setFrom(CONNECTED_RESOURCE);

        Assert.assertTrue(predicate.test(response3));
    }

    @Test
    public void testNoConnectedResource() {
        Message originalMessage = new Message();
        originalMessage.setTo(Jid.of("test@test"));
        Predicate<Message> predicate = new MessageResponsePredicate(originalMessage, null);

        Message response = new Message();
        response.setId(originalMessage.getId());

        Assert.assertFalse(predicate.test(response));

        Message response2 = new Message();
        response2.setId(originalMessage.getId());
        response2.setFrom(originalMessage.getTo());

        Assert.assertTrue(predicate.test(response2));

        Message response3 = new Message();
        response3.setId(originalMessage.getId());
        response3.setFrom(Jid.of("otherjid@test"));

        Assert.assertFalse(predicate.test(response3));
    }

    @Test
    public void testChangeSubject() {
        Message originalMessage = new Message();
        originalMessage.setId("lh2bs617");
        originalMessage.setTo(Jid.of("coven@chat.shakespeare.lit"));
        Predicate<Message> predicate = new MessageResponsePredicate(originalMessage, null);

        Message response = new Message();
        response.setId(originalMessage.getId());
        response.setFrom(Jid.of("coven@chat.shakespeare.lit/thirdwitch"));
        Assert.assertTrue(predicate.test(response));
    }
}