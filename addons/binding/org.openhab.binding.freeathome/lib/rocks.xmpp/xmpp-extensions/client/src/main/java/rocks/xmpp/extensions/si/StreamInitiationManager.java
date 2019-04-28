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

package rocks.xmpp.extensions.si;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.AbstractIQHandler;
import rocks.xmpp.core.stanza.IQHandler;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.StanzaError;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.extensions.bytestreams.ByteStreamEvent;
import rocks.xmpp.extensions.bytestreams.ByteStreamSession;
import rocks.xmpp.extensions.bytestreams.ibb.InBandByteStreamManager;
import rocks.xmpp.extensions.bytestreams.ibb.model.InBandByteStream;
import rocks.xmpp.extensions.bytestreams.s5b.Socks5ByteStreamManager;
import rocks.xmpp.extensions.bytestreams.s5b.model.Socks5ByteStream;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.featureneg.model.FeatureNegotiation;
import rocks.xmpp.extensions.filetransfer.FileTransfer;
import rocks.xmpp.extensions.filetransfer.FileTransferManager;
import rocks.xmpp.extensions.filetransfer.FileTransferNegotiator;
import rocks.xmpp.extensions.filetransfer.model.FileTransferOffer;
import rocks.xmpp.extensions.si.model.StreamInitiation;
import rocks.xmpp.extensions.si.profile.filetransfer.model.SIFileTransferOffer;
import rocks.xmpp.util.concurrent.AsyncResult;
import rocks.xmpp.util.concurrent.CompletionStages;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Christian Schudt
 */
public final class StreamInitiationManager extends Manager implements FileTransferNegotiator {

    private static final Logger logger = Logger.getLogger(StreamInitiationManager.class.getName());

    private static final String STREAM_METHOD = "stream-method";

    private final Map<String, ProfileManager> profileManagers = new ConcurrentHashMap<>();

    private final InBandByteStreamManager inBandByteStreamManager;

    private final Socks5ByteStreamManager socks5ByteStreamManager;

    private final IQHandler iqHandler;

    private StreamInitiationManager(final XmppSession xmppSession) {
        super(xmppSession);

        inBandByteStreamManager = xmppSession.getManager(InBandByteStreamManager.class);
        socks5ByteStreamManager = xmppSession.getManager(Socks5ByteStreamManager.class);

        // Currently, there's only one profile in XMPP, namely XEP-0096 SI File Transfer.
        profileManagers.put(SIFileTransferOffer.NAMESPACE, (iq, streamInitiation) -> {
            FileTransferManager fileTransferManager = xmppSession.getManager(FileTransferManager.class);
            fileTransferManager.fileTransferOffered(iq, streamInitiation.getId(), streamInitiation.getMimeType(), (FileTransferOffer) streamInitiation.getProfileElement(), streamInitiation, StreamInitiationManager.this);
        });

        iqHandler = new AbstractIQHandler(IQ.Type.SET) {
            @Override
            protected IQ processRequest(IQ iq) {
                StreamInitiation streamInitiation = iq.getExtension(StreamInitiation.class);

                FeatureNegotiation featureNegotiation = streamInitiation.getFeatureNegotiation();
                // Assume no valid streams by default, unless valid streams are found.
                boolean noValidStreams = true;
                if (featureNegotiation != null) {
                    DataForm dataForm = featureNegotiation.getDataForm();
                    if (dataForm != null) {
                        DataForm.Field field = dataForm.findField(STREAM_METHOD);
                        if (field != null) {
                            List<String> streamMethods = field.getOptions().stream().map(DataForm.Option::getValue).collect(Collectors.toList());
                            if (!Collections.disjoint(streamMethods, getSupportedStreamMethods())) {
                                // Request contains valid streams
                                noValidStreams = false;
                            }
                        }
                    }
                }
                if (noValidStreams) {
                    return iq.createError(new StanzaError(rocks.xmpp.core.stanza.model.errors.Condition.BAD_REQUEST, StreamInitiation.NO_VALID_STREAMS));
                } else {
                    ProfileManager profileManager = profileManagers.get(streamInitiation.getProfile());

                    if (profileManager == null) {
                        return iq.createError(new StanzaError(rocks.xmpp.core.stanza.model.errors.Condition.BAD_REQUEST, StreamInitiation.BAD_PROFILE));
                    } else {
                        profileManager.handle(iq, streamInitiation);
                        return null;
                    }
                }
            }
        };
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        xmppSession.addIQHandler(StreamInitiation.class, iqHandler);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        xmppSession.removeIQHandler(StreamInitiation.class);
    }

    /**
     * Initiates a stream with another entity.
     *
     * @param receiver The receiver, i.e. the XMPP entity you want to negotiate a stream.
     * @param profile  The profile. Currently there's only the {@link rocks.xmpp.extensions.si.profile.filetransfer.model.SIFileTransferOffer} profile.
     * @param mimeType The mime type of the stream.
     * @param timeout  The timeout, which wait until the stream has been negotiated.
     * @return The async result with the output stream which has been negotiated.
     */
    public AsyncResult<ByteStreamSession> initiateStream(Jid receiver, SIFileTransferOffer profile, String mimeType, Duration timeout) {
        return this.initiateStream(receiver, profile, mimeType, timeout, UUID.randomUUID().toString());
    }

    /**
     * Initiates a stream with another entity.
     *
     * @param receiver  The receiver, i.e. the XMPP entity you want to negotiate a stream.
     * @param profile   The profile. Currently there's only the {@link rocks.xmpp.extensions.si.profile.filetransfer.model.SIFileTransferOffer} profile.
     * @param mimeType  The mime type of the stream.
     * @param timeout   The timeout, which wait until the stream has been negotiated.
     * @param sessionId The session id.
     * @return The async result with the output stream which has been negotiated.
     */
    public AsyncResult<ByteStreamSession> initiateStream(Jid receiver, SIFileTransferOffer profile, String mimeType, Duration timeout, String sessionId) {

        // Offer stream methods.
        List<DataForm.Option> options = getSupportedStreamMethods().stream().map(DataForm.Option::new).collect(Collectors.toList());
        DataForm.Field field = DataForm.Field.builder().var(STREAM_METHOD).type(DataForm.Field.Type.LIST_SINGLE).options(options).build();
        DataForm dataForm = new DataForm(DataForm.Type.FORM, Collections.singleton(field));
        // Offer the file to the recipient and wait until it's accepted.
        return xmppSession.query(IQ.set(receiver, new StreamInitiation(Objects.requireNonNull(sessionId), SIFileTransferOffer.NAMESPACE, mimeType, profile, new FeatureNegotiation(dataForm))), timeout).thenCompose(result -> {

            // The recipient must response with a stream initiation.
            StreamInitiation streamInitiation = result.getExtension(StreamInitiation.class);

            FeatureNegotiation featureNegotiation = streamInitiation.getFeatureNegotiation();
            // Get the stream method which has been chosen by the recipient.
            String streamMethod = featureNegotiation.getDataForm().findField(STREAM_METHOD).getValues().get(0);

            CompletionStage<ByteStreamSession> byteStreamSessionStage;
            // Choose the stream method to be used based on the recipient's choice.
            switch (streamMethod) {
                case Socks5ByteStream.NAMESPACE:
                    byteStreamSessionStage = CompletionStages.withFallback(socks5ByteStreamManager.initiateSession(receiver, sessionId), (future, throwable) -> {
                                // As fallback, if SOCKS5 negotiation failed, try IBB.
                                logger.log(Level.FINE, "SOCKS5 file transfer failed, falling back to IBB", throwable);
                                return inBandByteStreamManager.initiateSession(receiver, sessionId, 4096);
                            }
                    );
                    break;
                case InBandByteStream.NAMESPACE:
                    byteStreamSessionStage = inBandByteStreamManager.initiateSession(receiver, sessionId, 4096);
                    break;
                default:
                    throw new CompletionException(new IOException("Receiver returned unsupported stream method."));
            }
            return byteStreamSessionStage;
        });
    }

    @Override
    public AsyncResult<FileTransfer> accept(IQ iq, final String sessionId, FileTransferOffer fileTransferOffer, Object
            protocol, OutputStream outputStream) {
        StreamInitiation streamInitiation = (StreamInitiation) protocol;
        DataForm.Field field = streamInitiation.getFeatureNegotiation().getDataForm().findField(STREAM_METHOD);
        // These are the offered stream methods by the initiator of the file transfer.
        final List<String> offeredStreamMethods = field.getOptions().stream().map(DataForm.Option::getValue).collect(Collectors.toList());
        // In the SI response, only include stream methods, which we actually support.
        offeredStreamMethods.retainAll(getSupportedStreamMethods());

        DataForm.Field fieldReply = DataForm.Field.builder().var(STREAM_METHOD).value(offeredStreamMethods.get(0)).type(DataForm.Field.Type.LIST_SINGLE).build();
        DataForm dataForm = new DataForm(DataForm.Type.SUBMIT, Collections.singleton(fieldReply));
        StreamInitiation siResponse = new StreamInitiation(new FeatureNegotiation(dataForm));

        CompletableFuture<ByteStreamSession> completableFutureS5b = new CompletableFuture<>();
        CompletableFuture<ByteStreamSession> completableFutureIbb = new CompletableFuture<>();

        // Before we reply with the chosen stream method, we
        // register a byte stream listener, because we expect the initiator to open a byte stream with us.
        Consumer<ByteStreamEvent> byteStreamListenerS5b = createSessionListener(sessionId, completableFutureS5b);
        Consumer<ByteStreamEvent> byteStreamListenerIbb = createSessionListener(sessionId, completableFutureIbb);

        socks5ByteStreamManager.addByteStreamListener(byteStreamListenerS5b);
        inBandByteStreamManager.addByteStreamListener(byteStreamListenerIbb);

        // Send the stream initiation result.
        xmppSession.send(iq.createResult(siResponse));

        // Create a stage, which completes when either S5B or IBB negotiation finished, whichever comes first.
        CompletionStage<ByteStreamSession> eitherS5bOrIbb = completableFutureS5b.applyToEither(completableFutureIbb, Function.identity());
        // If any of the previous negotiation failed, always try IBB as fallback.
        CompletionStage<ByteStreamSession> withFallbackStage = CompletionStages.withFallback(eitherS5bOrIbb, (f, t) -> completableFutureIbb);

        // And then wait until the peer opens the stream.
        return new AsyncResult<>(withFallbackStage.applyToEither(CompletionStages.timeoutAfter(xmppSession.getConfiguration().getDefaultResponseTimeout().toMillis() * 5, TimeUnit.MILLISECONDS), byteStreamSession -> {
                    try {
                        return new FileTransfer(xmppSession, byteStreamSession.getSessionId(), byteStreamSession.getInputStream(), outputStream, fileTransferOffer.getSize());
                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }
                }
        )).whenComplete((byteStreamSession, throwable) -> {
            // Remove the listeners when everything completed.
            socks5ByteStreamManager.removeByteStreamListener(byteStreamListenerS5b);
            inBandByteStreamManager.removeByteStreamListener(byteStreamListenerIbb);
        });
    }

    private static Consumer<ByteStreamEvent> createSessionListener(final String sessionId, final CompletableFuture<ByteStreamSession> completableFuture) {
        return e -> {
            if (sessionId.equals(e.getSessionId())) {
                // Auto-accept the inbound stream
                e.accept().whenComplete((byteStreamSession, throwable) -> {
                    if (throwable != null) {
                        completableFuture.completeExceptionally(throwable);
                    } else {
                        completableFuture.complete(byteStreamSession);
                    }
                });
            }
        };
    }

    @Override
    public void reject(IQ iq) {
        xmppSession.send(iq.createError(Condition.FORBIDDEN));
    }

    Collection<String> getSupportedStreamMethods() {
        Collection<String> allStreamMethods = new ArrayDeque<>(Arrays.asList(Socks5ByteStream.NAMESPACE, InBandByteStream.NAMESPACE));
        allStreamMethods.retainAll(xmppSession.getEnabledFeatures());
        return allStreamMethods;
    }

    private interface ProfileManager {
        void handle(IQ iq, StreamInitiation streamInitiation);
    }
}
