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

package rocks.xmpp.websocket.net.client;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.client.SslEngineConfigurator;
import org.glassfish.tyrus.client.ThreadPoolConfig;
import org.glassfish.tyrus.container.jdk.client.JdkClientContainer;
import rocks.xmpp.core.net.ChannelEncryption;
import rocks.xmpp.core.net.Connection;
import rocks.xmpp.core.net.client.ClientConnectionConfiguration;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stream.model.StreamElement;
import rocks.xmpp.dns.DnsResolver;
import rocks.xmpp.dns.TxtRecord;
import rocks.xmpp.websocket.codec.XmppWebSocketDecoder;
import rocks.xmpp.websocket.codec.XmppWebSocketEncoder;
import rocks.xmpp.websocket.net.WebSocketConnection;

import javax.net.ssl.SSLContext;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.HandshakeResponse;
import javax.websocket.Session;
import javax.websocket.SessionException;
import javax.websocket.server.HandshakeRequest;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * A configuration for a WebSocket connection.
 * It allows you to configure basic connection settings like hostname and port, as well as the path in the WebSocket URI.
 * <h3>Usage</h3>
 * In order to create an instance of this class you have to use the builder pattern as shown below.
 * ```java
 * WebSocketConnectionConfiguration connectionConfiguration = WebSocketConnectionConfiguration.builder()
 * .hostname("localhost")
 * .port(7443)
 * .path("/ws/")
 * .sslContext(sslContext)
 * .channelEncryption(ChannelEncryption.DIRECT)
 * .build();
 * ```
 * The above sample configuration will connect to <code>wss://localhost:7443/ws/</code> using SSL with a custom {@link SSLContext}.
 * <p>
 * This class is immutable.
 *
 * @see WebSocketConnection
 * @see <a href="https://tools.ietf.org/html/rfc7395">XMPP Subprotocol for WebSocket</a>
 * @since 0.7.0
 */
public final class WebSocketConnectionConfiguration extends ClientConnectionConfiguration {

    private static volatile WebSocketConnectionConfiguration defaultConfiguration;

    private final String path;

    private final Duration pingInterval;

    private WebSocketConnectionConfiguration(Builder builder) {
        super(builder);
        this.path = builder.path;
        this.pingInterval = builder.pingInterval;
    }

    /**
     * Gets the default configuration.
     *
     * @return The default configuration.
     */
    public static WebSocketConnectionConfiguration getDefault() {
        // Use double-checked locking idiom
        if (defaultConfiguration == null) {
            synchronized (WebSocketConnectionConfiguration.class) {
                if (defaultConfiguration == null) {
                    defaultConfiguration = builder().build();
                }
            }
        }
        return defaultConfiguration;
    }

    /**
     * Sets the default configuration.
     *
     * @param configuration The default configuration.
     */
    public static void setDefault(WebSocketConnectionConfiguration configuration) {
        synchronized (WebSocketConnectionConfiguration.class) {
            defaultConfiguration = configuration;
        }
    }

    /**
     * Creates a new builder.
     *
     * @return The builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets the path on the host, e.g. "/ws/".
     *
     * @return The path on the host.
     */
    public final String getPath() {
        return path;
    }

    /**
     * Gets the ping interval.
     *
     * @return The ping interval.
     * @see Builder#pingInterval(Duration)
     */
    public final Duration getPingInterval() {
        return pingInterval;
    }

    @Override
    public final Connection createConnection(final XmppSession xmppSession) throws Exception {
        final CompletableFuture<Void> closeFuture = new CompletableFuture<>();
        return new WebSocketClientConnection(createWebSocketSession(xmppSession, closeFuture), closeFuture, xmppSession, this);
    }

    private Session createWebSocketSession(XmppSession xmppSession, CompletableFuture<Void> closeFuture) throws Exception {

        final URI path;
        synchronized (this) {

            URI uri;
            String protocol = getChannelEncryption() == ChannelEncryption.DIRECT ? "wss" : "ws";
            // If no port has been configured, use the default ports.
            int targetPort = getPort() > 0 ? getPort() : (getChannelEncryption() == ChannelEncryption.DIRECT ? 5281 : 5280);
            // If a hostname has been configured, use it to connect.
            if (getHostname() != null) {
                uri = new URI(protocol, null, getHostname(), targetPort, getPath(), null, null);
            } else if (xmppSession.getDomain() != null) {
                // If a URL has not been set, try to find the URL by the domain via a DNS-TXT lookup as described in XEP-0156.
                String resolvedUrl = findWebSocketEndpoint(xmppSession.getDomain().toString(), xmppSession.getConfiguration().getNameServer(), getConnectTimeout());
                if (resolvedUrl != null) {
                    uri = new URI(resolvedUrl);
                } else {
                    // Fallback mechanism:
                    // If the URL could not be resolved, use the domain name and port 5280 as default.
                    uri = new URI(protocol, null, xmppSession.getDomain().toString(), targetPort, getPath(), null, null);
                }
            } else {
                throw new IllegalStateException("Neither an URL nor a domain given for a WebSocket connection.");
            }

            path = uri;
        }
        final AtomicBoolean handshakeSucceeded = new AtomicBoolean();
        final ClientEndpointConfig clientEndpointConfig = ClientEndpointConfig.Builder.create()
                .encoders(Collections.singletonList(XmppWebSocketEncoder.class))
                .decoders(Collections.singletonList(XmppWebSocketDecoder.class))
                .preferredSubprotocols(Collections.singletonList("xmpp"))
                .configurator(new ClientEndpointConfig.Configurator() {
                    @Override
                    public void afterResponse(HandshakeResponse response) {
                        // If a client receives a handshake response that does not include
                        // 'xmpp' in the 'Sec-WebSocket-Protocol' header, then an XMPP
                        // subprotocol WebSocket connection was not established and the client
                        // MUST close the WebSocket connection.
                        List<String> responseHeader = response.getHeaders().get(HandshakeRequest.SEC_WEBSOCKET_PROTOCOL);
                        if (responseHeader != null && responseHeader.contains("xmpp")) {
                            handshakeSucceeded.set(true);
                        }
                    }
                }).build();
        clientEndpointConfig.getUserProperties().put(XmppWebSocketEncoder.UserProperties.MARSHALLER, (Supplier<Marshaller>) xmppSession::createMarshaller);
        clientEndpointConfig.getUserProperties().put(XmppWebSocketDecoder.UserProperties.UNMARSHALLER, (Supplier<Unmarshaller>) xmppSession::createUnmarshaller);
        if (xmppSession.getDebugger() != null) {
            clientEndpointConfig.getUserProperties().put(XmppWebSocketEncoder.UserProperties.ON_WRITE, (BiConsumer<String, StreamElement>) xmppSession.getDebugger()::writeStanza);
            clientEndpointConfig.getUserProperties().put(XmppWebSocketDecoder.UserProperties.ON_READ, (BiConsumer<String, StreamElement>) xmppSession.getDebugger()::readStanza);
        }
        clientEndpointConfig.getUserProperties().put(XmppWebSocketEncoder.UserProperties.XML_OUTPUT_FACTORY, xmppSession.getConfiguration().getXmlOutputFactory());

        final ClientManager client = ClientManager.createClient(JdkClientContainer.class.getName());
        if (getSSLContext() != null) {
            SslEngineConfigurator sslEngineConfigurator = new SslEngineConfigurator(getSSLContext());
            client.getProperties().put(ClientProperties.SSL_ENGINE_CONFIGURATOR, sslEngineConfigurator);
            sslEngineConfigurator.setHostnameVerifier(getHostnameVerifier());
        }

        final ThreadPoolConfig config = ThreadPoolConfig.defaultConfig();
        config.setThreadFactory(xmppSession.getConfiguration().getThreadFactory("WebSocket Client"));
        client.getProperties().put(ClientProperties.WORKER_THREAD_POOL_CONFIG, config);

        int connectTimeout = getConnectTimeout();
        if (connectTimeout > 0) {
            client.getProperties().put(ClientProperties.HANDSHAKE_TIMEOUT, connectTimeout);
        }
        final Proxy proxy = getProxy();
        if (proxy != null && proxy.type() == Proxy.Type.HTTP) {
            InetSocketAddress inetSocketAddress = ((InetSocketAddress) proxy.address());
            client.getProperties().put(ClientProperties.PROXY_URI, "http://" + inetSocketAddress.getHostName() + ':' + inetSocketAddress.getPort());
        }

        final Session session = client.connectToServer(new Endpoint() {
            @Override
            public void onOpen(Session session, EndpointConfig config) {
                if (!handshakeSucceeded.get()) {
                    try {
                        String msg = "Server response did not include 'Sec-WebSocket-Protocol' header with value 'xmpp'.";
                        session.close(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, msg));
                    } catch (IOException e) {
                        xmppSession.notifyException(e);
                    }
                }
            }

            @Override
            public void onError(Session session, Throwable t) {
                xmppSession.notifyException(t);
            }

            @Override
            public void onClose(Session session, CloseReason closeReason) {
                if (closeReason.getCloseCode() != CloseReason.CloseCodes.NORMAL_CLOSURE) {
                    closeFuture.completeExceptionally(new SessionException(closeReason.toString(), null, session));
                }
                closeFuture.complete(null);
            }
        }, clientEndpointConfig, path);

        if (!session.isOpen()) {
            throw new IOException("Session could not be opened.");
        }
        return session;
    }

    private static String findWebSocketEndpoint(String xmppServiceDomain, String nameServer, long timeout) {

        try {
            List<TxtRecord> txtRecords = DnsResolver.resolveTXT(xmppServiceDomain, nameServer, timeout);
            for (TxtRecord txtRecord : txtRecords) {
                Map<String, String> attributes = txtRecord.asAttributes();
                String url = attributes.get("_xmpp-client-websocket");
                if (url != null) {
                    return url;
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    @Override
    public final String toString() {
        return "WebSocket connection configuration: " + (getChannelEncryption() == ChannelEncryption.DIRECT ? "wss" : "ws") + "://" + super.toString() + path;
    }

    /**
     * A builder to create a {@link WebSocketConnectionConfiguration} instance.
     */
    public static final class Builder extends ClientConnectionConfiguration.Builder<Builder> {

        private String path;

        private Duration pingInterval;

        private Builder() {
            // default values
            path("/ws/");
        }

        /**
         * Sets the path on the host, e.g. "/ws/"
         *
         * @param path The path on the host.
         * @return The builder.
         */
        public final Builder path(String path) {
            this.path = path;
            return this;
        }

        /**
         * Sets the ping interval. If not null and non-negative, a WebSocket ping is sent periodically to the server.
         * If no pong is received within the configured response timeout ({@link rocks.xmpp.core.session.XmppSessionConfiguration.Builder#defaultResponseTimeout(Duration)}) the XMPP session is closed with an exception.
         *
         * @param pingInterval The ping interval.
         * @return The builder.
         * @see <a href="https://tools.ietf.org/html/rfc6455#section-5.5.2">5.5.2.  Ping</a>
         */
        public final Builder pingInterval(Duration pingInterval) {
            this.pingInterval = pingInterval;
            return this;
        }

        @Override
        protected final Builder self() {
            return this;
        }

        @Override
        public final WebSocketConnectionConfiguration build() {
            if (proxy != null && proxy.type() != Proxy.Type.HTTP && proxy.type() != Proxy.Type.DIRECT) {
                throw new UnsupportedOperationException("Non-HTTP proxies are not supported by WebSockets.");
            }
            if (channelEncryption != null && channelEncryption != ChannelEncryption.DISABLED && channelEncryption != ChannelEncryption.DIRECT) {
                throw new IllegalArgumentException("WebSocket connections only support ChannelEncryption.DIRECT (wss) or ChannelEncryption.DISABLED (ws).");
            }
            return new WebSocketConnectionConfiguration(this);
        }
    }
}
