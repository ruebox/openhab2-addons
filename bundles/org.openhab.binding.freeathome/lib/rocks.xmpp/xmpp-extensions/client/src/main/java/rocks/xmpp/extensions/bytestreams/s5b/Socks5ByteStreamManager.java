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

package rocks.xmpp.extensions.bytestreams.s5b;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.AbstractIQHandler;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.extensions.bytestreams.ByteStreamManager;
import rocks.xmpp.extensions.bytestreams.ByteStreamSession;
import rocks.xmpp.extensions.bytestreams.s5b.model.Socks5ByteStream;
import rocks.xmpp.extensions.bytestreams.s5b.model.StreamHost;
import rocks.xmpp.extensions.disco.ServiceDiscoveryManager;
import rocks.xmpp.extensions.disco.model.info.Identity;
import rocks.xmpp.util.XmppUtils;
import rocks.xmpp.util.concurrent.AsyncResult;
import rocks.xmpp.util.concurrent.CompletionStages;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * A manager for <a href="https://xmpp.org/extensions/xep-0065.html">XEP-0065: SOCKS5 Bytestreams</a>.
 * <p>
 * If {@linkplain #setLocalHostEnabled(boolean) enabled}, this class starts a local SOCKS5 server to support direct connections between two entities.
 * You can {@linkplain #setPort(int) set a port} of this local server, if you don't set a port, the default port 1080 is used.
 * <p>
 * It also allows you to {@linkplain #initiateSession(Jid, String) initiate a byte stream session} with another entity.
 * <p>
 * If you don't want to discover stream hosts, but want to configure your own, {@linkplain #setStreamHost(StreamHost) set a custom stream host}.
 *
 * @author Christian Schudt
 */
public final class Socks5ByteStreamManager extends ByteStreamManager {

    private final ServiceDiscoveryManager serviceDiscoveryManager;

    private final LocalSocks5Server localSocks5Server;

    /**
     * Guarded by "this".
     */
    private boolean localHostEnabled;

    /**
     * Guarded by "this".
     */
    private StreamHost streamHost;

    private Socks5ByteStreamManager(final XmppSession xmppSession) {
        super(xmppSession);
        this.serviceDiscoveryManager = xmppSession.getManager(ServiceDiscoveryManager.class);

        this.localSocks5Server = new LocalSocks5Server();
    }

    static S5bSession createS5bSession(Jid requester, Jid target, String sessionId, List<StreamHost> streamHosts, Duration timeout) throws IOException {
        Socket socketUsed = null;
        Jid streamHostUsed = null;
        IOException ioException = null;
        // If the Requester provides more than one StreamHost, the Target SHOULD try to connect to them in the order of the <streamhost/> children within the <query/> element.
        for (StreamHost streamHost : streamHosts) {
            try {
                Socket socket = new Socket();
                // Try to connect to each host for max "duration" / "streamhosts.size()", so that the total session creation takes no longer than "duration".
                socket.connect(new InetSocketAddress(streamHost.getHostname(), streamHost.getPort()), (int) (timeout.toMillis() / streamHosts.size()));
                // If the Target is able to open a TCP socket on a StreamHost/Requester, it MUST use the SOCKS5 protocol to establish a SOCKS5 connection.
                Socks5Protocol.establishClientConnection(socket, Socks5ByteStream.hash(sessionId, requester, target), 0);
                socketUsed = socket;
                streamHostUsed = streamHost.getJid();
                break;
            } catch (IOException e) {
                // ignore, try next.
                ioException = e;
            }
        }
        if (streamHostUsed == null) {
            throw new IOException("Unable to connect to any stream host.", ioException);
        }
        return new S5bSession(sessionId, socketUsed, streamHostUsed, timeout);
    }

    @Override
    protected void initialize() {
        super.initialize();
        xmppSession.addIQHandler(Socks5ByteStream.class, new AbstractIQHandler(IQ.Type.SET) {
            @Override
            protected IQ processRequest(IQ iq) {
                Socks5ByteStream socks5ByteStream = iq.getExtension(Socks5ByteStream.class);

                if (socks5ByteStream.getSessionId() == null) {
                    // If the request is malformed (e.g., the <query/> element does not include the 'sid' attribute), the Target MUST return an error of <bad-request/>.
                    return iq.createError(Condition.BAD_REQUEST);
                } else {
                    XmppUtils.notifyEventListeners(byteStreamListeners, new S5bEvent(Socks5ByteStreamManager.this, socks5ByteStream.getSessionId(), xmppSession, iq, new ArrayList<>(socks5ByteStream.getStreamHosts())));
                    return null;
                }
            }
        });
    }

    /**
     * Indicates whether the local host is enabled.
     *
     * @return If enabled.
     */
    public synchronized boolean isLocalHostEnabled() {
        return localHostEnabled;
    }

    /**
     * Enables or disables the use of a local SOCKS5 host.
     *
     * @param enabled If enabled.
     */
    public synchronized void setLocalHostEnabled(boolean enabled) {
        this.localHostEnabled = enabled;
        if (!enabled) {
            localSocks5Server.stop();
        }
    }

    /**
     * Gets the port of the local host.
     *
     * @return The port.
     */
    public int getPort() {
        return localSocks5Server.getPort();
    }

    /**
     * Sets the port of the local host.
     *
     * @param port The port.
     */
    public void setPort(int port) {
        this.localSocks5Server.setPort(port);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        if (isLocalHostEnabled()) {
            // Only stop the server here, if we disable support.
            // It will be enabled, when needed.
            localSocks5Server.start();
        }
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        if (!isLocalHostEnabled()) {
            // Only stop the server here, if we disable support.
            // It will be enabled, when needed.
            localSocks5Server.stop();
        }
    }

    /**
     * Discovers the SOCKS5 proxies.
     *
     * @return The async result with the proxies.
     * @see <a href="https://xmpp.org/extensions/xep-0065.html#disco">4. Discovering Proxies</a>
     */
    public AsyncResult<List<StreamHost>> discoverProxies() {
        // First discover the items which identify the proxy host.
        return serviceDiscoveryManager.discoverServices(Identity.proxyByteStreams()).thenCompose(items -> {

            // For each proxy service, send a disco request to it (to discover stream hosts)
            Collection<CompletionStage<List<StreamHost>>> stages = items.stream()
                    .map(service -> xmppSession.query(IQ.get(service.getJid(), new Socks5ByteStream())).thenApply(result -> {
                        Socks5ByteStream socks5ByteStream = result.getExtension(Socks5ByteStream.class);
                        if (socks5ByteStream != null) {
                            return socks5ByteStream.getStreamHosts();
                        }
                        return Collections.<StreamHost>emptyList();
                    }))
                    .collect(Collectors.toList());

            // Combine all discovery of all stream hosts into one future.
            return CompletionStages.allOf(stages);
        });
    }

    /**
     * Gets a list of available stream hosts, including the discovered proxies and the local host.
     *
     * @return The async result with the stream hosts.
     */
    public AsyncResult<List<StreamHost>> getAvailableStreamHosts() {
        final AsyncResult<List<StreamHost>> hosts;

        StreamHost host = getStreamHost();
        if (host != null) {
            // If a host has been configured use that one.
            hosts = new AsyncResult<>(CompletableFuture.completedFuture(Collections.singletonList(host)));
        } else {
            // Otherwise discover proxies via service discovery.
            hosts = discoverProxies();
        }
        return hosts.thenApply(streamHosts -> {
            try {
                List<StreamHost> result = new ArrayList<>();
                if (isLocalHostEnabled()) {
                    // First add the local SOCKS5 server to the list of stream hosts.
                    Jid requester = xmppSession.getConnectedResource();
                    result.add(new StreamHost(requester, localSocks5Server.getAddress(), localSocks5Server.getPort()));
                }

                IOException ioException = null;
                // Try, if we -as initiator- can connect to the offered stream proxies, before suggesting them to the receiver.
                for (StreamHost streamHost : streamHosts) {
                    try (Socket socket = new Socket()) {
                        socket.connect(new InetSocketAddress(streamHost.getHostname(), streamHost.getPort()), 5000);
                        // We can connect, let's add to the stream hosts we send to the receiver.
                        result.add(streamHost);
                    } catch (IOException e) {
                        ioException = e;
                    }
                }
                if (result.isEmpty()) {
                    XmppException xmppException = new XmppException("No stream hosts found.");
                    if (ioException != null) {
                        ioException.initCause(ioException);
                    }
                    throw xmppException;
                }
                return result;
            } catch (XmppException e) {
                throw new CompletionException(e);
            }
        });
    }

    /**
     * Configures a custom stream host. If a stream host is set this way, no stream hosts are discovered, but the configured one is used instead.
     *
     * @param streamHost The stream host.
     */
    public synchronized void setStreamHost(StreamHost streamHost) {
        this.streamHost = streamHost;
    }

    /**
     * Gets the configured stream host.
     *
     * @return The configured stream host or null if none was configured.
     */
    public synchronized StreamHost getStreamHost() {
        return streamHost;
    }

    /**
     * Initiates a SOCKS5 session with a target.
     *
     * @param target    The target.
     * @param sessionId The session id.
     * @return The async result with the SOCKS5 byte stream session.
     */
    public AsyncResult<ByteStreamSession> initiateSession(Jid target, String sessionId) {

        if (isLocalHostEnabled()) {
            localSocks5Server.start();
        }

        // Discover available stream hosts (which also adds ourselves to the list of available hosts)
        return getAvailableStreamHosts().thenCompose(streamHosts -> {
                    final Jid requester = xmppSession.getConnectedResource();

                    // Create the hash, which will identify the socket connection.
                    final String hash = Socks5ByteStream.hash(sessionId, requester, target);
                    localSocks5Server.allowedAddresses.add(hash);

                    // 5.3.1 Requester Initiates S5B Negotiation
                    // 6.3.1 Requester Initiates S5B Negotiation
                    // Then send the available stream hosts to the receiver, which will then try to connect to one of the hosts.
                    return xmppSession.query(IQ.set(target, new Socks5ByteStream(sessionId, streamHosts, hash))).whenComplete((a, e) ->
                                    // When the receiver responded (either with success or with error, we can remove the hash)
                                    localSocks5Server.allowedAddresses.remove(hash)
                    ).thenComposeAsync(result -> {

                        // Then complete the process by either connecting to a SOCKS5 proxy or use the socket which our receiver connected to.

                        // 5.3.3 Target Acknowledges Bytestream
                        // 6.3.3 Target Acknowledges Bytestream
                        Socks5ByteStream socks5ByteStream = result.getExtension(Socks5ByteStream.class);
                        StreamHost streamHostUsed = null;
                        for (StreamHost streamHost : streamHosts) {
                            if (socks5ByteStream.getStreamHostUsed() != null && socks5ByteStream.getStreamHostUsed().equals(streamHost.getJid())) {
                                streamHostUsed = streamHost;
                                break;
                            }
                        }
                        final StreamHost usedStreamHost = streamHostUsed;
                        if (usedStreamHost == null) {
                            throw new CompletionException(new XmppException("Target did not respond with a stream host."));
                        }

                        Socket socket;
                        if (!usedStreamHost.getJid().equals(requester)) {
                            socket = new Socket();

                            // 6.3.4 Requester Establishes SOCKS5 Connection with StreamHost
                            try {
                                socket.connect(new InetSocketAddress(usedStreamHost.getHostname(), usedStreamHost.getPort()));
                                Socks5Protocol.establishClientConnection(socket, hash, 0);
                            } catch (IOException e) {
                                throw new CompletionException(e);
                            }
                            // 6.3.5 Activation of Bytestream
                            return xmppSession.query(IQ.set(usedStreamHost.getJid(), Socks5ByteStream.activate(sessionId, target))).thenApply(aVoid -> {
                                try {
                                    return new S5bSession(sessionId, socket, usedStreamHost.getJid(), xmppSession.getConfiguration().getDefaultResponseTimeout());
                                } catch (SocketException e) {
                                    throw new CompletionException(e);
                                }
                            });
                        } else {
                            socket = localSocks5Server.getSocket(hash);
                            if (socket == null) {
                                throw new CompletionException(new IOException("Not connected to stream host"));
                            }
                            try {
                                return CompletableFuture.completedFuture(new S5bSession(sessionId, socket, usedStreamHost.getJid(), xmppSession.getConfiguration().getDefaultResponseTimeout()));
                            } catch (SocketException e) {
                                throw new CompletionException(e);
                            }
                        }
                    });
                }

        );
    }

    @Override
    protected void dispose() {
        super.dispose();
        localSocks5Server.stop();
    }
}
