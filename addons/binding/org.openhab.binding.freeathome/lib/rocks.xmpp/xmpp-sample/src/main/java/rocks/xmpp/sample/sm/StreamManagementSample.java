/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 Christian Schudt
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

package rocks.xmpp.sample.sm;

import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.net.ChannelEncryption;
import rocks.xmpp.core.session.SendTask;
import rocks.xmpp.core.net.client.SocketConnectionConfiguration;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.session.XmppSessionConfiguration;
import rocks.xmpp.core.session.debug.ConsoleDebugger;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.extensions.httpbind.BoshConnectionConfiguration;
import rocks.xmpp.extensions.sm.model.StreamManagement;
import rocks.xmpp.websocket.net.client.WebSocketConnectionConfiguration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StreamManagementSample {

    public static void main(String[] args) {

        Executors.newFixedThreadPool(1).execute(() -> {
            try {

                SocketConnectionConfiguration socketConnectionConfiguration = SocketConnectionConfiguration.builder()
                        .hostname("localhost")
                        .port(5222)
                        .channelEncryption(ChannelEncryption.DISABLED) // Disable TLS only for simpler example here.
                        .build();

                BoshConnectionConfiguration boshConfiguration = BoshConnectionConfiguration.builder()
                        .hostname("localhost")
                        .port(7070)
                                //.sslContext(getTrustAllSslContext())
                        .channelEncryption(ChannelEncryption.DISABLED)
                        .build();

                WebSocketConnectionConfiguration webSocketConnectionConfiguration = WebSocketConnectionConfiguration.builder()
                        .hostname("localhost")
                        .port(80)
                        .channelEncryption(ChannelEncryption.DISABLED) // Disable TLS only for simpler example here.
                        .build();

                XmppSessionConfiguration configuration = XmppSessionConfiguration.builder()
                        .debugger(ConsoleDebugger.class)
                        .build();

                XmppClient xmppSession = XmppClient.create("localhost", configuration, socketConnectionConfiguration);
                xmppSession.enableFeature(StreamManagement.NAMESPACE);
                // Connect
                xmppSession.connect();

                // Login
                xmppSession.login("admin", "admin", "sm");
                xmppSession.addMessageAcknowledgedListener(messageEvent -> System.out.println("Received by server!!!"));
                xmppSession.addSendSucceededListener(messageEvent -> System.out.println("Sent to server!!!" + messageEvent));
                xmppSession.addSendFailedListener((messageEvent, e) -> {
                    System.out.println("FAILED!!!" + messageEvent);
                    throw new RuntimeException(e);
                });
                // Send a message to myself, which is caught by the listener above.
                SendTask<Message> trackableMessage = xmppSession.sendMessage(new Message(xmppSession.getConnectedResource(), Message.Type.CHAT, "Hello World! Echo!"));
                trackableMessage.onAcknowledge(message -> System.out.println("Received by server: " + message));

                SendTask<Presence> trackablePresence = xmppSession.sendPresence(new Presence(Presence.Show.AWAY));
                trackablePresence.onAcknowledge(presence -> System.out.println("Received by server: " + presence));

                ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                scheduledExecutorService.scheduleAtFixedRate(() -> xmppSession.sendPresence(new Presence(Presence.Show.AWAY)), 0, 10, TimeUnit.SECONDS);

            } catch (XmppException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
