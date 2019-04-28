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

package rocks.xmpp.core.net.client;

import rocks.xmpp.core.net.ChannelEncryption;
import rocks.xmpp.core.net.Connection;
import rocks.xmpp.core.net.ConnectionConfiguration;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.extensions.compress.CompressionMethod;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.net.IDN;
import java.net.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A base class for connection configurations.
 * <p>
 * All connection methods have a few properties in common, which are abstracted in this class.
 * Among these common properties are hostname, port, proxy, security settings and a timeout.
 *
 * @author Christian Schudt
 */
public abstract class ClientConnectionConfiguration implements ConnectionConfiguration {

    private final String hostname;

    private final int port;

    private final Proxy proxy;

    private final ChannelEncryption channelEncryption;

    private final SSLContext sslContext;

    private final HostnameVerifier hostnameVerifier;

    private final int connectTimeout;

    private final List<CompressionMethod> compressionMethods;

    protected ClientConnectionConfiguration(Builder<? extends Builder> builder) {
        this.hostname = builder.hostname;
        this.port = builder.port;
        this.proxy = builder.proxy;
        this.channelEncryption = builder.channelEncryption;
        this.sslContext = builder.sslContext;
        this.hostnameVerifier = builder.hostnameVerifier;
        this.connectTimeout = builder.connectTimeout;
        this.compressionMethods = builder.compressionMethods;
    }

    /**
     * A factory method to create the connection.
     *
     * @param xmppSession The XMPP session, which is associated with the connection.
     * @return The connection.
     * @throws Exception Any exception which may occur during connection establishment.
     */
    public abstract Connection createConnection(XmppSession xmppSession) throws Exception;

    /**
     * Gets the hostname.
     *
     * @return The hostname.
     */
    public final String getHostname() {
        return hostname;
    }

    /**
     * Gets the port.
     *
     * @return The port.
     */
    public final int getPort() {
        return port;
    }

    /**
     * Gets the proxy.
     *
     * @return The proxy.
     */
    public final Proxy getProxy() {
        return proxy;
    }

    @Override
    public final ChannelEncryption getChannelEncryption() {
        return channelEncryption;
    }

    /**
     * Gets the SSL context.
     *
     * @return The SSL context.
     */
    @Override
    public final SSLContext getSSLContext() {
        return sslContext;
    }

    /**
     * Gets the hostname verifier.
     *
     * @return The hostname verifier.
     */
    public final HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    /**
     * Gets the timeout for connection establishment.
     *
     * @return The timeout.
     */
    public final int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Gets the compression methods.
     *
     * @return The compression methods.
     */
    public final List<CompressionMethod> getCompressionMethods() {
        return compressionMethods;
    }

    @Override
    public String toString() {
        return hostname + ':' + port;
    }

    /**
     * An abstract builder class for building immutable configuration objects.
     *
     * @param <T> The concrete builder class.
     */
    public abstract static class Builder<T extends Builder<T>> {

        protected String hostname;

        protected int port;

        protected Proxy proxy;

        protected ChannelEncryption channelEncryption;

        protected SSLContext sslContext;

        protected HostnameVerifier hostnameVerifier;

        protected int connectTimeout;

        protected List<CompressionMethod> compressionMethods = Collections.emptyList();

        protected Builder() {
        }

        /**
         * Returns an instance of the concrete builder.
         *
         * @return The concrete builder.
         */
        protected abstract T self();

        /**
         * Sets the hostname.
         *
         * @param hostname The hostname.
         * @return The builder.
         */
        public final T hostname(String hostname) {
            this.hostname = IDN.toASCII(hostname, IDN.USE_STD3_ASCII_RULES);
            return self();
        }

        /**
         * Sets the port.
         *
         * @param port The port.
         * @return The builder.
         */
        public final T port(int port) {
            this.port = port;
            return self();
        }

        /**
         * Sets the proxy, e.g. if you are behind a HTTP proxy and use a BOSH connection.
         *
         * @param proxy The proxy.
         * @return The builder.
         */
        public final T proxy(Proxy proxy) {
            this.proxy = proxy;
            return self();
        }

        /**
         * Sets whether the connection is secured via SSL.
         * <p>
         * A standard TCP connection starts with a plain socket and negotiates a secure SSL connection during stream negotiation via 'StartTLS'.
         * Hence, setting {@code secure(true)} means, you start with a plain socket and upgrade it to a secure socket during XMPP negotiation.
         * <p>
         * Setting {@code secure(false)} means, you start with plain socket and won't upgrade to a secure socket.
         * However, some servers require that the client secures the connection, in which case an exception is thrown during connecting.
         * <p>
         * If your server expects the connection to be secured immediately (often on port 5223), you should {@linkplain SocketConnectionConfiguration.Builder#socketFactory(SocketFactory) set a custom SSL socket factory}.
         * <p>
         * See <a href="https://xmpp.org/rfcs/rfc6120.html#tls">RFC 6120 § 5.  STARTTLS Negotiation</a> for further information.
         * <p>
         * HTTP (BOSH) and WebSocket connections provide TLS outside of the XMPP layer, i.e. it's not negotiated in XMPP.
         * Setting {@code secure(true)} for these connection methods means the connection connects via {@code https} or {@code wss} respectively.
         * <p>
         * If you set this to {@code true}, you should also {@linkplain #sslContext(SSLContext) set} an {@link SSLContext}. Otherwise {@code SSLContext.getDefault()} is used.
         *
         * @param secure If the connection is secured via SSL.
         * @return The builder.
         * @see #sslContext(SSLContext)
         * @deprecated Use {@link #channelEncryption}.
         */
        @Deprecated
        public final T secure(boolean secure) {
            channelEncryption(secure ? ChannelEncryption.REQUIRED : ChannelEncryption.DISABLED);
            return self();
        }

        /**
         * Sets how the connection is secured via SSL.
         * <p>
         * A standard TCP connection starts with a plain socket and negotiates a secure SSL connection during stream negotiation via 'StartTLS'.
         * Hence, setting {@link ChannelEncryption#OPTIONAL} means, you start with a plain socket and upgrade it to a secure socket during XMPP negotiation, if possible.
         * <p>
         * Setting {@link ChannelEncryption#DISABLED} means, you start with plain socket and won't upgrade to a secure socket.
         * However, some servers require that the client secures the connection, in which case an exception is thrown during connecting.
         * <p>
         * If your server expects the connection to be secured immediately (often on port 5223), you should use {@link ChannelEncryption#DIRECT}.
         * <p>
         * See <a href="https://xmpp.org/rfcs/rfc6120.html#tls">RFC 6120 § 5.  STARTTLS Negotiation</a> for further information.
         * <p>
         * HTTP (BOSH) and WebSocket connections provide TLS outside of the XMPP layer, i.e. it's not negotiated in XMPP.
         * Setting {@link ChannelEncryption#DIRECT} for these connection methods means the connection connects via {@code https} or {@code wss} respectively.
         * {@link ChannelEncryption#OPTIONAL} and {@link ChannelEncryption#REQUIRED} are not applicable for these connecion methods.
         * <p>
         * If you set this to {@code true}, you should also {@linkplain #sslContext(SSLContext) set} an {@link SSLContext}. Otherwise {@code SSLContext.getDefault()} is used.
         *
         * @param channelEncryption The channel encryption mode.
         * @return The builder.
         * @see #sslContext(SSLContext)
         */
        public final T channelEncryption(ChannelEncryption channelEncryption) {
            this.channelEncryption = channelEncryption;
            return self();
        }

        /**
         * Sets a custom SSL context, used to secure the connection.
         * This SSL context only takes effect, when setting {@code secure(true)}.
         *
         * @param sslContext The SSL context.
         * @return The builder.
         * @see #secure(boolean)
         */
        public final T sslContext(SSLContext sslContext) {
            this.sslContext = sslContext;
            return self();
        }

        /**
         * Sets an optional hostname verifier, used to verify the hostname in the certificate presented by the server.
         * If no verifier is set, the hostname is verified nonetheless using the default.
         *
         * @param hostnameVerifier The hostname verifier.
         * @return The builder.
         */
        public final T hostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return self();
        }

        /**
         * Sets a timeout for the connection establishment.
         * <p>
         * Connecting to a XMPP server involves multiple steps:
         * <ul>
         * <li>DNS lookup</li>
         * <li>Connection establishment of the underlying transport (e.g. TCP or HTTP)</li>
         * <li>XMPP stream negotiation</li>
         * </ul>
         * This timeout is only used for DNS lookup (which is not used in all cases) and for connection establishment of the underlying transport (e.g. for a socket connection), but not for stream negotiation.
         * Therefore it does not reflect how long the whole connection process may take, but should be understood as hint for establishing the underlying XMPP transport.
         * <p>
         * XMPP stream negotiation is configured via {@link rocks.xmpp.core.session.XmppSessionConfiguration.Builder#defaultResponseTimeout(java.time.Duration)}
         *
         * @param connectTimeout The timeout in milliseconds.
         * @return The builder.
         * @throws IllegalArgumentException If the timeout is negative.
         * @see XmppSession#connect()
         */
        public final T connectTimeout(int connectTimeout) {
            if (connectTimeout < 0) {
                throw new IllegalArgumentException("connectionTimeout cannot be negative.");
            }
            this.connectTimeout = connectTimeout;
            return self();
        }

        /**
         * Sets the compression method.
         *
         * @param compressionMethods The compression methods.
         * @return The builder.
         * @see CompressionMethod#ZLIB
         * @see CompressionMethod#GZIP
         * @see CompressionMethod#DEFLATE
         */
        public final T compressionMethods(CompressionMethod... compressionMethods) {
            this.compressionMethods = Arrays.asList(compressionMethods);
            return self();
        }

        /**
         * Builds the connection configuration.
         *
         * @return The concrete connection configuration.
         */
        public abstract ClientConnectionConfiguration build();
    }
}
