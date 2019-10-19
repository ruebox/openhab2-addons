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

package rocks.xmpp.extensions.muc;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import rocks.xmpp.core.IntegrationTest;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.net.client.SocketConnectionConfiguration;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.session.XmppSessionConfiguration;
import rocks.xmpp.core.stanza.MessageEvent;
import rocks.xmpp.extensions.muc.model.RoomConfiguration;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Christian Schudt
 */
public class MultiUserChatIT extends IntegrationTest {

    private final ChatRoom[] chatRoom = new ChatRoom[2];

    private final XmppClient[] xmppSession = new XmppClient[2];

    private final MultiUserChatManager[] multiUserChatManager = new MultiUserChatManager[2];

    @BeforeClass
    public void before() throws XmppException, ExecutionException, InterruptedException {
        XmppSessionConfiguration configuration = XmppSessionConfiguration.builder()
                //.debugger(ConsoleDebugger.class)
                .build();
        xmppSession[0] = XmppClient.create(DOMAIN, configuration, SocketConnectionConfiguration.getDefault());
        xmppSession[0].connect();
        xmppSession[0].login(USER_1, PASSWORD_1);

        xmppSession[1] = XmppClient.create(DOMAIN, SocketConnectionConfiguration.getDefault());
        xmppSession[1].connect();
        xmppSession[1].login(USER_2, PASSWORD_2);

        multiUserChatManager[0] = xmppSession[0].getManager(MultiUserChatManager.class);
        multiUserChatManager[1] = xmppSession[1].getManager(MultiUserChatManager.class);
        Collection<ChatService> chatServices0 = multiUserChatManager[0].discoverChatServices().get();
        Collection<ChatService> chatServices1 = multiUserChatManager[1].discoverChatServices().get();
        Assert.assertFalse(chatServices0.isEmpty());

        ChatService chatService0 = chatServices0.iterator().next();
        ChatService chatService1 = chatServices1.iterator().next();

        chatRoom[0] = chatService0.createRoom("test");
        chatRoom[1] = chatService1.createRoom("test");
        Assert.assertEquals(chatRoom[0].getAddress(), chatService0.getAddress().withLocal("test"));
    }

    @Test
    public void testMessageListener() throws InterruptedException, ExecutionException {

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Consumer<MessageEvent> messageListener = e -> {
            if ("Hello".equals(e.getMessage().getBody())) {
                countDownLatch.countDown();
            }
        };
        chatRoom[0].addInboundMessageListener(messageListener);

        try {
            chatRoom[0].enter("test").get();
            chatRoom[0].sendMessage("Hello");
            if (!countDownLatch.await(3, TimeUnit.SECONDS)) {
                Assert.fail("Timeout reached while waiting on message.");
            }
        } finally {
            chatRoom[0].removeInboundMessageListener(messageListener);
            chatRoom[0].exit().get();
        }
    }

    @Test(dependsOnMethods = "testMessageListener")
    public void testInvitation() throws InterruptedException, ExecutionException {

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        multiUserChatManager[1].addInvitationListener(e -> {
            System.out.println(e);
            if (e.getReason().equals("join!")) {
                countDownLatch.countDown();
                //ChatRoom chatRoom = multiUserChatManager[0].createChatService(Jid.of(e.getRoomAddress().getDomain())).createRoom(e.getRoomAddress().getLocal());
            }
        });
        try {
            chatRoom[0].enter("test").get();
            chatRoom[0].invite(xmppSession[1].getConnectedResource().asBareJid(), "join!");
            if (!countDownLatch.await(3, TimeUnit.SECONDS)) {
                Assert.fail("Timeout reached while waiting on invitation.");
            }
        } finally {
            chatRoom[0].exit().get();
        }
    }

    @Test
    public void testUserEnters() throws InterruptedException, ExecutionException {

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        chatRoom[0].addOccupantListener(e -> {
            if (e.getType() == OccupantEvent.Type.ENTERED) {
                countDownLatch.countDown();
            }
        });
        try {
            chatRoom[0].enter("test").get();
            chatRoom[0].configure(RoomConfiguration.builder().build()).get();
            chatRoom[1].enter("nick").get();
            if (!countDownLatch.await(6, TimeUnit.SECONDS)) {
                Assert.fail("Timeout reached while waiting on user entering.");
            }
        } finally {
            chatRoom[0].exit().get();
            chatRoom[1].exit().get();
        }
    }
}
