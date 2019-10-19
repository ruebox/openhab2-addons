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

package rocks.xmpp.core.net.client;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.net.ChannelEncryption;
import rocks.xmpp.core.net.Connection;
import rocks.xmpp.core.net.TcpBinding;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.dns.DnsResolver;
import rocks.xmpp.dns.SrvRecord;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

/**
 * A configuration for a TCP connection.
 *
 * @param <T> The transport object, e.g. {@link java.net.Socket}.
 * @author Christian Schudt
 * @see rocks.xmpp.extensions.httpbind.BoshConnectionConfiguration
 * @see SocketConnection
 */
public abstract class TcpConnectionConfiguration<T> extends ClientConnectionConfiguration {

    private final int keepAliveInterval;

    protected TcpConnectionConfiguration(Builder<? extends Builder> builder) {
        super(builder);
        this.keepAliveInterval = builder.keepAliveInterval;
    }

    /**
     * Gets the whitespace keep-alive interval.
     *
     * @return The whitespace keep-alive interval.
     */
    public final int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    /**
     * Creates the connection
     *
     * @param xmppSession The XMPP session.
     * @param creator     The creation function.
     * @return The connection.
     * @throws Exception If any exception occurs when creating and connecting.
     */
    protected final Connection createConnection(XmppSession xmppSession, Function<T, TcpBinding> creator) throws Exception {
        T socket;
        if (getHostname() != null && !getHostname().isEmpty()) {
            socket = connect(getHostname(), getPort());
        } else if (xmppSession.getDomain() != null) {
            if ((socket = connectWithXmppServiceDomain(xmppSession.getDomain(), xmppSession.getConfiguration().getNameServer())) == null) {
                // 9. If the initiating entity does not receive a response to its SRV query, it SHOULD attempt the fallback process described in the next section.
                socket = connect(xmppSession.getDomain().toString(), getPort());
            }
        } else {
            throw new IllegalStateException("Neither 'xmppServiceDomain' nor 'host' is set.");
        }
        TcpBinding tcpBinding = creator.apply(socket);
        if (getChannelEncryption() == ChannelEncryption.DIRECT) {
            tcpBinding.secureConnection();
        }
        return tcpBinding;
    }

    /**
     * Connects to a host and port.
     *
     * @param hostname The host.
     * @param port     The port.
     * @return The connected transport.
     * @throws Exception If any exception occurred during connecting.
     */
    protected abstract T connect(String hostname, int port) throws Exception;

    /**
     * This is the preferred way to resolve the FQDN.
     *
     * @param xmppServiceDomain The fully qualified domain name.
     * @return If the connection could be established.
     * @see <a href="https://xmpp.org/rfcs/rfc6120.html#tcp-resolution-prefer">3.2.1.  Preferred Process: SRV Lookup</a>
     */
    private T connectWithXmppServiceDomain(final Jid xmppServiceDomain, final String nameServer) {

        // 1. The initiating entity constructs a DNS SRV query whose inputs are:
        //
        //   * Service of "xmpp-client" (for client-to-server connections) or "xmpp-server" (for server-to-server connections)
        try {
            final List<SrvRecord> srvRecords = DnsResolver.resolveSRV("xmpp-client", xmppServiceDomain, nameServer, getConnectTimeout());

            // 3. If a response is received, it will contain one or more combinations of a port and FDQN, each of which is weighted and prioritized as described in [DNS-SRV].
            // Sort the entries, so that the best one is tried first.
            srvRecords.sort(null);
            IOException ex = null;
            for (SrvRecord srvRecord : srvRecords) {
                if (srvRecord != null) {
                    // (However, if the result of the SRV lookup is a single resource record with a Target of ".", i.e., the root domain, then the initiating entity MUST abort SRV processing at this point because according to [DNS-SRV] such a Target "means that the service is decidedly not available at this domain".)
                    if (".".equals(srvRecord.getTarget())) {
                        return null;
                    }

                    try {
                        // 4. The initiating entity chooses at least one of the returned FQDNs to resolve (following the rules in [DNS-SRV]), which it does by performing DNS "A" or "AAAA" lookups on the FDQN; this will result in an IPv4 or IPv6 address.
                        // 5. The initiating entity uses the IP address(es) from the successfully resolved FDQN (with the corresponding port number returned by the SRV lookup) as the connection address for the receiving entity.
                        // 6. If the initiating entity fails to connect using that IP address but the "A" or "AAAA" lookups returned more than one IP address, then the initiating entity uses the next resolved IP address for that FDQN as the connection address.
                        return connect(srvRecord.getTarget(), srvRecord.getPort());
                    } catch (IOException e) {
                        // 7. If the initiating entity fails to connect using all resolved IP addresses for a given FDQN, then it repeats the process of resolution and connection for the next FQDN returned by the SRV lookup based on the priority and weight as defined in [DNS-SRV].
                        ex = e;
                    }
                }
            }

            // 8. If the initiating entity receives a response to its SRV query but it is not able to establish an XMPP connection using the data received in the response, it SHOULD NOT attempt the fallback process described in the next section (this helps to prevent a state mismatch between inbound and outbound connections).
            if (!srvRecords.isEmpty()) {
                throw new IOException("Could not connect to any host.", ex);
            }
        } catch (Exception e) {
            // Unable to resolve the domain, try fallback.
            return null;
        }
        return null;
    }

    /**
     * A builder to create a {@link TcpConnectionConfiguration} instance.
     */
    public static abstract class Builder<T extends Builder<T>> extends ClientConnectionConfiguration.Builder<T> {

        private int keepAliveInterval;

        protected Builder() {
            // default values.
            channelEncryption(ChannelEncryption.OPTIONAL);
            port(5222);
            keepAliveInterval(30);
        }

        /**
         * Sets the whitespace keep-alive interval in seconds. If the interval is negative, no whitespace will be sent at all.
         *
         * @param keepAliveInterval The whitespace keep-alive interval.
         * @return The builder.
         */
        public final Builder keepAliveInterval(int keepAliveInterval) {
            this.keepAliveInterval = keepAliveInterval;
            return this;
        }
    }
}
