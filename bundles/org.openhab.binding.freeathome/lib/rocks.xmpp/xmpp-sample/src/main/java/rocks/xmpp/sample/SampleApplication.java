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

package rocks.xmpp.sample;

import io.netty.channel.nio.NioEventLoopGroup;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.net.client.SocketConnectionConfiguration;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.session.XmppSessionConfiguration;
import rocks.xmpp.core.session.debug.ConsoleDebugger;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.net.ChannelEncryption;
import rocks.xmpp.extensions.httpbind.BoshConnectionConfiguration;
import rocks.xmpp.extensions.sm.model.StreamManagement;
import rocks.xmpp.nio.netty.client.NettyTcpConnectionConfiguration;
import rocks.xmpp.websocket.net.client.WebSocketConnectionConfiguration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * A simple example for connecting and sending a message.
 */
public class SampleApplication {

    private static final Logger logger = Logger.getLogger(SampleApplication.class.getName());

    public static void main(String[] args) {

        configureLogging();

        // Create a "main application" thread, which keeps the JVM running.
        Executors.newFixedThreadPool(1).execute(() -> {
            try {
                NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);
                XmppSessionConfiguration configuration = XmppSessionConfiguration.builder()
                        .debugger(ConsoleDebugger.class)
                        .authenticationMechanisms("PLAIN")
                        .build();
                long now = System.currentTimeMillis();
                for (int i = 0; i < 1; i++) {

                    SocketConnectionConfiguration socketConnectionConfiguration = SocketConnectionConfiguration.builder()
                            .hostname("localhost") // The hostname.
                            .port(5222) // The XMPP default port.
                            .sslContext(getTrustAllSslContext()) // Use an SSL context, which trusts every server. Only use it for testing!
                                    //.channelEncryption(ChannelEncryption.DIRECT) // We want to negotiate a TLS connection.
                            .hostnameVerifier((s, sslSession) -> true)
                            .build();

                    BoshConnectionConfiguration boshConfiguration = BoshConnectionConfiguration.builder()
                            .hostname("localhost")
                            .port(7443)
                            .sslContext(getTrustAllSslContext())
                            .channelEncryption(ChannelEncryption.DIRECT)
                            .hostnameVerifier((s, sslSession) -> true)
                            .build();
                    WebSocketConnectionConfiguration webSocketConfiguration = WebSocketConnectionConfiguration.builder()
                            .hostname("localhost")
                            .port(7070)
                            .sslContext(getTrustAllSslContext())
                            .channelEncryption(ChannelEncryption.DIRECT)
                            .hostnameVerifier((s, sslSession) -> true)
                            .build();

                    NettyTcpConnectionConfiguration nettyTcpConnectionConfiguration = NettyTcpConnectionConfiguration.builder()
                            //.hostname("localhost")
                            .port(5222)
                            .sslContext(getTrustAllSslContext())
                            //.channelEncryption(ChannelEncryption.DIRECT)
                            .hostnameVerifier((s, sslSession) -> true)
                            .eventLoopGroup(eventLoopGroup)
                            .build();


                    XmppClient xmppClient = XmppClient.create("localhost", configuration, socketConnectionConfiguration);

                    // Listen for inbound messages.
                    xmppClient.addInboundMessageListener(e -> logger.info("Received: " + e.getMessage()));

                    // Listen for inbound presence.
                    xmppClient.addInboundPresenceListener(e -> logger.info("Received: " + e.getPresence()));
                    xmppClient.enableFeature(StreamManagement.NAMESPACE);
                    // Connect
                    xmppClient.connect();
                    // Login
                    xmppClient.login("admin", "admin");

                    // Send a message to myself, which is caught by the listener above.
                    xmppClient.send(new Message(xmppClient.getConnectedResource(), Message.Type.CHAT, "Hello World! öäü!"));
                    xmppClient.send(new Message(xmppClient.getConnectedResource(), Message.Type.CHAT, "Hello World! Echo!"));
                    xmppClient.send(new Message(xmppClient.getConnectedResource(), Message.Type.CHAT, "Hello World! Echo!"));

                    now = System.currentTimeMillis();
                    xmppClient.close();
                    System.out.println(System.currentTimeMillis() - now);
                    //logger.info(xmppClient.getActiveConnection().toString());
                }
                long time = System.currentTimeMillis() - now;
                int i = 0;
            } catch (XmppException | GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected static void configureLogging() {

        LogManager.getLogManager().reset();

        // Log everything from the rocks.xmpp package with level FINE or above to the console.

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINE);
        consoleHandler.setFormatter(new LogFormatter());

        Logger logger = Logger.getLogger("rocks.xmpp");
        logger.setLevel(Level.FINE);
        logger.addHandler(consoleHandler);
    }

    protected static SSLContext getTrustAllSslContext() throws GeneralSecurityException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        }, new SecureRandom());
        return sslContext;
    }
}
