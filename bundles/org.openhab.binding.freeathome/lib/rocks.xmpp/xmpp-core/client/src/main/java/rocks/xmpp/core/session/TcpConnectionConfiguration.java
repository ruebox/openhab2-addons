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

package rocks.xmpp.core.session;

import rocks.xmpp.core.net.Connection;
import rocks.xmpp.core.net.client.SocketConnection;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * A configuration for a TCP connection.
 * It allows you to configure various connection settings for a TCP socket connection, most importantly the host address and port,
 * but also a whitespace keep-alive interval, a custom socket factory, a custom SSL context and compression methods.
 * <h3>Usage</h3>
 * In order to create an instance of this class you have to use the builder pattern as shown below.
 * ```java
 * TcpConnectionConfiguration tcpConfiguration = TcpConnectionConfiguration.builder()
 * .hostname("localhost")
 * .port(5222)
 * .sslContext(sslContext)
 * .channelEncryption(ChannelEncryption.DISABLED)
 * .build();
 * ```
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see rocks.xmpp.extensions.httpbind.BoshConnectionConfiguration
 * @see SocketConnection
 * @deprecated Use {@link rocks.xmpp.core.net.client.SocketConnectionConfiguration}
 */
@Deprecated
public class TcpConnectionConfiguration extends rocks.xmpp.core.net.client.TcpConnectionConfiguration<Socket> {

    private static volatile TcpConnectionConfiguration defaultConfiguration;

    private final SocketFactory socketFactory;

    protected TcpConnectionConfiguration(Builder builder) {
        super(builder);
        this.socketFactory = builder.socketFactory;
    }

    /**
     * Creates a new builder for this class.
     *
     * @return The builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets the default configuration.
     *
     * @return The default configuration.
     */
    public static TcpConnectionConfiguration getDefault() {
        // Use double-checked locking idiom
        if (defaultConfiguration == null) {
            synchronized (TcpConnectionConfiguration.class) {
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
    public static void setDefault(TcpConnectionConfiguration configuration) {
        synchronized (TcpConnectionConfiguration.class) {
            defaultConfiguration = configuration;
        }
    }

    @Override
    public final Connection createConnection(XmppSession xmppSession) throws Exception {
        return createConnection(xmppSession, socket -> new SocketConnection(socket, xmppSession, this));
    }

    @Override
    protected final Socket connect(final String hostname, final int port) throws IOException {
        final Socket socket;
        if (getSocketFactory() == null) {
            if (getProxy() != null) {
                socket = new Socket(getProxy());
            } else {
                socket = new Socket();
            }
        } else {
            socket = getSocketFactory().createSocket();
        }
        // SocketFactory may return an already connected socket, so check the connected state to prevent SocketException.
        if (!socket.isConnected()) {
            socket.connect(new InetSocketAddress(hostname, port), getConnectTimeout());
        }

        return socket;
    }

    /**
     * Gets the socket factory.
     *
     * @return The socket factory.
     */
    public final SocketFactory getSocketFactory() {
        return socketFactory;
    }

    @Override
    public final String toString() {
        return "TCP connection configuration: " + super.toString();
    }

    /**
     * A builder to create a {@link TcpConnectionConfiguration} instance.
     */
    public static final class Builder extends rocks.xmpp.core.net.client.TcpConnectionConfiguration.Builder<Builder> {

        private SocketFactory socketFactory;

        /**
         * Sets a socket factory which creates the socket.
         * This can be useful if you want connect to the legacy SSL port (usually 5223) and the connection is encrypted right from the beginning.
         * <p>
         * However, usually, there's no need to set a custom socket factory.
         *
         * @param socketFactory The socket factory.
         * @return The builder.
         * @see #secure(boolean)
         */
        public Builder socketFactory(SocketFactory socketFactory) {
            this.socketFactory = socketFactory;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public TcpConnectionConfiguration build() {
            return new TcpConnectionConfiguration(this);
        }
    }
}
