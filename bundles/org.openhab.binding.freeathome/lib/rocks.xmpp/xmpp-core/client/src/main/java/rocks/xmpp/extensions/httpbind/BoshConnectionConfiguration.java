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

package rocks.xmpp.extensions.httpbind;

import rocks.xmpp.core.net.ChannelEncryption;
import rocks.xmpp.core.net.Connection;
import rocks.xmpp.core.net.client.ClientConnectionConfiguration;
import rocks.xmpp.core.net.client.SocketConnectionConfiguration;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.dns.DnsResolver;
import rocks.xmpp.dns.TxtRecord;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * A configuration for a BOSH connection.
 * It allows you to configure basic connection settings like hostname and port, but also BOSH specific settings like the wait interval, a route or the use of a key sequencing mechanism.
 * <h3>Usage</h3>
 * In order to create an instance of this class you have to use the builder pattern as shown below.
 * ```java
 * BoshConnectionConfiguration boshConnectionConfiguration = BoshConnectionConfiguration.builder()
 *     .hostname("localhost")
 *     .port(5280)
 *     .path("/http-bind/")
 *     .build();
 * ```
 * The above sample configuration will connect to <code>http://localhost:5280/http-bind/</code>.
 * <p>
 * This class is immutable.
 *
 * @see SocketConnectionConfiguration
 * @see BoshConnection
 */
public final class BoshConnectionConfiguration extends ClientConnectionConfiguration {

    private static volatile BoshConnectionConfiguration defaultConfiguration;

    private final Duration wait;

    private final String path;

    private final String route;

    private final boolean useKeySequence;

    private BoshConnectionConfiguration(Builder builder) {
        super(builder);
        this.wait = builder.wait;
        this.path = builder.path;
        this.route = builder.route;
        this.useKeySequence = builder.useKeySequence;
    }

    /**
     * Gets the default configuration.
     *
     * @return The default configuration.
     */
    public static BoshConnectionConfiguration getDefault() {
        // Use double-checked locking idiom
        if (defaultConfiguration == null) {
            synchronized (BoshConnectionConfiguration.class) {
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
    public static void setDefault(BoshConnectionConfiguration configuration) {
        synchronized (BoshConnectionConfiguration.class) {
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

    @Override
    public Connection createConnection(XmppSession xmppSession) throws Exception {

        URL url;
        String protocol = getChannelEncryption() == ChannelEncryption.DIRECT ? "https" : "http";
        // If no port has been configured, use the default ports.
        int targetPort = getPort() > 0 ? getPort() : (getChannelEncryption() == ChannelEncryption.DIRECT ? 5281 : 5280);
        // If a hostname has been configured, use it to connect.
        if (getHostname() != null) {
            url = new URL(protocol, getHostname(), targetPort, getPath());
        } else if (xmppSession.getDomain() != null) {
            // If a URL has not been set, try to find the URL by the domain via a DNS-TXT lookup as described in XEP-0156.
            String resolvedUrl = findBoshUrl(xmppSession.getDomain().toString(), xmppSession.getConfiguration().getNameServer(), getConnectTimeout());
            if (resolvedUrl != null) {
                url = new URL(resolvedUrl);
            } else {
                // Fallback mechanism:
                // If the URL could not be resolved, use the domain name and port 5280 as default.
                url = new URL(protocol, xmppSession.getDomain().toString(), targetPort, getPath());
            }
        } else {
            throw new IllegalStateException("Neither an URL nor a domain given for a BOSH connection.");
        }

        BoshConnection boshConnection = new BoshConnection(url, xmppSession, this);
        boshConnection.connect();
        return boshConnection;
    }

    /**
     * Tries to find the BOSH URL by a DNS TXT lookup as described in <a href="https://xmpp.org/extensions/xep-0156.html">XEP-0156</a>.
     *
     * @param xmppServiceDomain The fully qualified domain name.
     * @param nameServer        The name server.
     * @param timeout           The lookup timeout.
     * @return The BOSH URL, if it could be found or null.
     */
    private static String findBoshUrl(String xmppServiceDomain, String nameServer, long timeout) {

        try {
            List<TxtRecord> txtRecords = DnsResolver.resolveTXT(xmppServiceDomain, nameServer, timeout);
            for (TxtRecord txtRecord : txtRecords) {
                Map<String, String> attributes = txtRecord.asAttributes();
                String url = attributes.get("_xmpp-client-xbosh");
                if (url != null) {
                    return url;
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    /**
     * Gets the longest time (in seconds) that the connection manager is allowed to wait before responding to any request during the session.
     *
     * @return The wait time.
     */
    public final Duration getWait() {
        return wait;
    }

    /**
     * Gets the path on the host, e.g. "/http-bind/".
     *
     * @return The path on the host.
     */
    public final String getPath() {
        return path;
    }

    /**
     * Gets the route.
     *
     * @return The route.
     */
    public final String getRoute() {
        return route;
    }

    /**
     * If the connection is secured via a key sequence mechanism.
     *
     * @return If the connection is secured via a key sequence mechanism.
     */
    public final boolean isUseKeySequence() {
        return useKeySequence;
    }

    @Override
    public final String toString() {
        return "BOSH connection configuration: " + (getChannelEncryption() == ChannelEncryption.DIRECT ? "https" : "http") + "://" + super.toString() + path;
    }

    /**
     * A builder to create a {@link rocks.xmpp.extensions.httpbind.BoshConnectionConfiguration} instance.
     */
    public static final class Builder extends ClientConnectionConfiguration.Builder<Builder> {

        private Duration wait;

        private String path;

        private String route;

        private boolean useKeySequence;

        private Builder() {
            // default values
            channelEncryption(ChannelEncryption.DISABLED);
            wait(Duration.ofMinutes(1));
            path("/http-bind/");
        }

        /**
         * Sets the path on the host, e.g. "/http-bind/"
         *
         * @param path The path on the host.
         * @return The builder.
         */
        public Builder path(String path) {
            this.path = path;
            return this;
        }

        /**
         * Sets the longest time that the connection manager is allowed to wait before responding to any request during the session.
         *
         * @param wait The time in seconds.
         * @return The builder.
         * @see <a href="https://xmpp.org/extensions/xep-0124.html#session-request">7.1 Session Creation Request</a>
         */
        public Builder wait(Duration wait) {
            this.wait = wait;
            return this;
        }

        /**
         * Sets the longest time (in seconds) that the connection manager is allowed to wait before responding to any request during the session.
         *
         * @param wait The time in seconds.
         * @return The builder.
         * @see <a href="https://xmpp.org/extensions/xep-0124.html#session-request">7.1 Session Creation Request</a>
         * @deprecated Use {@link #wait(Duration)}
         */
        @Deprecated
        public Builder wait(int wait) {
            this.wait = Duration.ofSeconds(wait);
            return this;
        }

        /**
         * Sets the route, formatted as "protocol:host:port" (e.g., "xmpp:example.com:9999").
         *
         * @param route The route.
         * @return The builder.
         * @see <a href="https://xmpp.org/extensions/xep-0124.html#session-request">7.1 Session Creation Request</a>
         */
        public Builder route(String route) {
            this.route = route;
            return this;
        }

        /**
         * Indicates whether a key sequencing mechanism is used to secure a connection.
         * Note that if the connection is already secured via HTTPS, it is not necessary to use this mechanism.
         *
         * @param useKeySequence If a key sequence should be used.
         * @return The builder.
         * @see <a href="https://xmpp.org/extensions/xep-0124.html#keys">15. Protecting Insecure Sessions</a>
         */
        public Builder useKeySequence(boolean useKeySequence) {
            this.useKeySequence = useKeySequence;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public BoshConnectionConfiguration build() {
            if (proxy != null && proxy.type() != Proxy.Type.HTTP && proxy.type() != Proxy.Type.DIRECT) {
                throw new UnsupportedOperationException("Non-HTTP proxies are not supported by BOSH connections.");
            }
            if (channelEncryption != ChannelEncryption.DISABLED && channelEncryption != ChannelEncryption.DIRECT) {
                throw new IllegalArgumentException("BOSH connections only support ChannelEncryption.DIRECT (https) or ChannelEncryption.DISABLED (http).");
            }
            return new BoshConnectionConfiguration(this);
        }
    }
}
