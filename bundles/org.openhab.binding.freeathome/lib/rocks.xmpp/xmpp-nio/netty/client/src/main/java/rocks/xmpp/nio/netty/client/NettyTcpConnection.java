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

package rocks.xmpp.nio.netty.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.JdkSslContext;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stream.client.StreamFeaturesManager;
import rocks.xmpp.core.stream.model.StreamError;
import rocks.xmpp.core.stream.model.StreamErrorException;
import rocks.xmpp.core.stream.model.errors.Condition;
import rocks.xmpp.core.tls.client.StartTlsManager;
import rocks.xmpp.extensions.compress.CompressionManager;
import rocks.xmpp.extensions.sm.StreamManager;
import rocks.xmpp.nio.netty.net.NettyChannelConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Locale;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Christian Schudt
 */
public final class NettyTcpConnection extends NettyChannelConnection {

    private static final Logger logger = Logger.getLogger(NettyTcpConnection.class.getName());

    private final StreamFeaturesManager streamFeaturesManager;

    private final StreamManager streamManager;

    private final StartTlsManager startTlsManager;

    private final CompressionManager compressionManager;

    private final NettyTcpConnectionConfiguration connectionConfiguration;

    private final XmppSession xmppSession;

    /**
     * Creates a connection to the specified host and port through a proxy.
     *
     * @param xmppSession             The XMPP session.
     * @param connectionConfiguration The connection configuration.
     */
    NettyTcpConnection(final Channel channel, final XmppSession xmppSession, final NettyTcpConnectionConfiguration connectionConfiguration) {
        super(channel, xmppSession, xmppSession.getDebugger() != null ? xmppSession.getDebugger()::readStanza : null, xmppSession::createUnmarshaller,
                xmppSession.getDebugger() != null ? xmppSession.getDebugger()::writeStanza : null,
                xmppSession::createMarshaller,
                xmppSession::notifyException,
                connectionConfiguration);
        this.xmppSession = xmppSession;
        this.connectionConfiguration = connectionConfiguration;

        int keepAliveInterval = connectionConfiguration.getKeepAliveInterval();

        channel.pipeline().addLast("idleStateHandler", new IdleStateHandler(0, keepAliveInterval, 0));
        channel.pipeline().addLast("idleStateEventHandler", new ChannelDuplexHandler() {

            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                if (evt instanceof IdleStateEvent) {
                    ctx.writeAndFlush(' ');
                }
            }

        });

        closeFuture().whenComplete((aVoid, throwable) -> {
            if (throwable != null) {
                xmppSession.notifyException(throwable);
            } else if (!isClosed()) {
                // If the server closed the connection, initiate a reconnection.
                xmppSession.notifyException(new StreamErrorException(new StreamError(Condition.UNDEFINED_CONDITION, "Stream closed by server", Locale.ENGLISH, null)));
            }
        });

        this.streamManager = xmppSession.getManager(StreamManager.class);
        this.streamManager.reset();

        this.startTlsManager = new StartTlsManager(xmppSession, this, connectionConfiguration.getChannelEncryption());

        this.compressionManager = new CompressionManager(xmppSession, this);
        this.compressionManager.getConfiguredCompressionMethods().clear();
        this.compressionManager.getConfiguredCompressionMethods().addAll(connectionConfiguration.getCompressionMethods());

        this.streamFeaturesManager = xmppSession.getManager(StreamFeaturesManager.class);
        this.streamFeaturesManager.addFeatureNegotiator(streamManager);
        this.streamFeaturesManager.addFeatureNegotiator(startTlsManager);
        this.streamFeaturesManager.addFeatureNegotiator(compressionManager);
    }

    @Override
    protected final void restartStream() {
        super.restartStream();
        open(sessionOpen);
    }

    @Override
    public final void secureConnection() throws NoSuchAlgorithmException {
        final SSLContext sslContext = getConfiguration().getSSLContext() != null ? getConfiguration().getSSLContext() : SSLContext.getDefault();
        final SslContext sslCtx = new JdkSslContext(sslContext, true, ClientAuth.OPTIONAL);
        final SslHandler handler = sslCtx.newHandler(channel.alloc(), String.valueOf(xmppSession.getDomain()), connectionConfiguration.getPort());
        final HostnameVerifier verifier = connectionConfiguration.getHostnameVerifier();
        final SSLEngine sslEngine = handler.engine();

        if (verifier == null) {
            final SSLParameters sslParameters = sslEngine.getSSLParameters();
            sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
            sslEngine.setSSLParameters(sslParameters);
        }

        final Future<Channel> handshakeFuture = handler.handshakeFuture();
        handshakeFuture.addListener(future -> {
            if (future.isSuccess()) {
                if (verifier != null && !verifier.verify(xmppSession.getDomain().toString(), sslEngine.getSession())) {
                    xmppSession.notifyException(new CertificateException("Server failed to authenticate as " + xmppSession.getDomain()));
                } else {
                    logger.log(Level.FINE, "Connection has been secured via TLS.");
                }
            } else {
                xmppSession.notifyException(future.cause());
            }
        });
        channel.pipeline().addFirst("SSL", handler);
    }

    @Override
    public final boolean isUsingAcknowledgements() {
        return streamManager.isActive();
    }

    @Override
    protected final CompletionStage<Void> closeConnection() {
        return super.closeConnection().thenRun(() -> {
            this.streamFeaturesManager.removeFeatureNegotiator(streamManager);
            this.streamFeaturesManager.removeFeatureNegotiator(startTlsManager);
            this.streamFeaturesManager.removeFeatureNegotiator(compressionManager);
        });
    }
}
