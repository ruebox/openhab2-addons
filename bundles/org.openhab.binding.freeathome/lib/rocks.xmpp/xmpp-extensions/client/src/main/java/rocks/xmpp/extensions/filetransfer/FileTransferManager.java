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

package rocks.xmpp.extensions.filetransfer;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.StanzaErrorException;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.extensions.caps.EntityCapabilitiesManager;
import rocks.xmpp.extensions.filetransfer.model.FileTransferOffer;
import rocks.xmpp.extensions.oob.model.iq.OobIQ;
import rocks.xmpp.extensions.si.StreamInitiationManager;
import rocks.xmpp.extensions.si.model.StreamInitiation;
import rocks.xmpp.extensions.si.profile.filetransfer.model.SIFileTransferOffer;
import rocks.xmpp.util.XmppUtils;
import rocks.xmpp.util.concurrent.AsyncResult;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;


/**
 * @author Christian Schudt
 */
public final class FileTransferManager extends Manager {

    private static final ExecutorService FILE_TRANSFER_OFFER_EXECUTOR = Executors.newCachedThreadPool(XmppUtils.createNamedThreadFactory("File Transfer Offer Thread"));

    private final StreamInitiationManager streamInitiationManager;

    private final EntityCapabilitiesManager entityCapabilitiesManager;

    private final Set<Consumer<FileTransferOfferEvent>> fileTransferOfferListeners = new CopyOnWriteArraySet<>();

    private FileTransferManager(final XmppSession xmppSession) {
        super(xmppSession, true);
        this.streamInitiationManager = xmppSession.getManager(StreamInitiationManager.class);
        this.entityCapabilitiesManager = xmppSession.getManager(EntityCapabilitiesManager.class);
    }

    /**
     * Calculates the MD5 hash for a file. The returned string is hex encoded.
     *
     * @param source The source path.
     * @return The hex encoded MD5 hash.
     * @throws IOException If the file could not be read.
     */
    public static String md5Hash(Path source) throws IOException {
        final MessageDigest md;
        try (InputStream is = Files.newInputStream(source)) {
            md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int n;
            while ((n = is.read(buffer)) > -1) {
                md.update(buffer, 0, n);
            }
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            // Every implementation of the Java platform is required to support MD5
            throw new AssertionError(e);
        }
    }

    /**
     * Offers a file to another user in form of an URL. The file can be downloaded by the recipient via an HTTP GET request.
     * If this method returns without exception you can assume, that the file has been successfully downloaded by the recipient.
     *
     * @param url         The URL of the file.
     * @param description The description of the file.
     * @param recipient   The recipient's JID (must be a full JID).
     * @param timeout     The timeout (indicates how long to wait until the file offer has either been accepted or rejected).
     * @return The async result.
     * @see <a href="https://xmpp.org/extensions/xep-0066.html">XEP-0066: Out of Band Data</a>
     * @deprecated Use {@link #offerFile(URI, String, Jid, Duration)}
     */
    @Deprecated
    public final AsyncResult<IQ> offerFile(URL url, String description, Jid recipient, Duration timeout) {
        return offerFile(URI.create(url.toString()), description, recipient, timeout);
    }

    /**
     * Offers a file to another user in form of an URL. The file can be downloaded by the recipient via an HTTP GET request.
     * If this method returns without exception you can assume, that the file has been successfully downloaded by the recipient.
     *
     * @param uri         The URI of the file.
     * @param description The description of the file.
     * @param recipient   The recipient's JID (must be a full JID).
     * @param timeout     The timeout (indicates how long to wait until the file offer has either been accepted or rejected).
     * @return The async result.
     * @see <a href="https://xmpp.org/extensions/xep-0066.html">XEP-0066: Out of Band Data</a>
     */
    public final AsyncResult<IQ> offerFile(URI uri, String description, Jid recipient, Duration timeout) {
        return xmppSession.query(IQ.set(recipient, new OobIQ(uri, description)), timeout).handle((iq, e) -> {
            if (e != null) {
                if (e instanceof CompletionException) {
                    if (e.getCause() instanceof StanzaErrorException && ((StanzaErrorException) e.getCause()).getCondition() == Condition.NOT_ACCEPTABLE) {
                        throw new CompletionException(new FileTransferRejectedException());
                    } else {
                        throw (CompletionException) e;
                    }
                } else {
                    throw new CompletionException(e);
                }
            }
            return null;
        });
    }

    /**
     * Offers a file to another user. If this method returns successfully, the recipient has accepted the offer, the stream method has been negotiated and the file is ready to be transferred.
     * Call {@link FileTransfer#transfer()} to start the file transfer.
     *
     * @param file        The file.
     * @param description The description of the file.
     * @param recipient   The recipient's JID (must be a <em>full</em> JID, i. e. including {@code resource}).
     * @param timeout     The timeout (indicates how long to wait until the file offer has either been accepted or rejected).
     * @return The async result with the file transfer object.
     * @throws IOException If the file can't be read.
     */
    public final AsyncResult<FileTransfer> offerFile(final File file, final String description, final Jid recipient, final Duration timeout) throws IOException {
        return offerFile(requireNonNull(file, "file must not be null.").toPath(), description, recipient, timeout);
    }

    /**
     * Offers a file to another user. If this method returns successfully, the recipient has accepted the offer, the stream method has been negotiated and the file is ready to be transferred.
     * Call {@link FileTransfer#transfer()} to start the file transfer.
     *
     * @param source      The file.
     * @param description The description of the file.
     * @param recipient   The recipient's JID (must be a <em>full</em> JID, i. e. including {@code resource}).
     * @param timeout     The timeout (indicates how long to wait until the file offer has either been accepted or rejected).
     * @return The async result with the file transfer object.
     * @throws IOException If the file can't be read.
     */
    public final AsyncResult<FileTransfer> offerFile(final Path source, final String description, final Jid recipient, final Duration timeout) throws IOException {
        return offerFile(source, description, recipient, timeout, null, false);
    }

    /**
     * Offers a file to another user. If this method returns successfully, the recipient has accepted the offer, the stream method has been negotiated and the file is ready to be transferred.
     * Call {@link FileTransfer#transfer()} to start the file transfer.
     *
     * @param source        The file.
     * @param description   The description of the file.
     * @param recipient     The recipient's JID (must be a <em>full</em> JID, i. e. including {@code resource}).
     * @param timeout       The timeout (indicates how long to wait until the file offer has either been accepted or rejected).
     * @param mimeType      The mime type. If null, the mime type is guessed.
     * @param calculateHash If true, the MD5 hash for the file is calculated and included in the offer.
     * @return The async result with the file transfer object.
     * @throws IOException If the file can't be read.
     */
    public final AsyncResult<FileTransfer> offerFile(final Path source, final String description, final Jid recipient, final Duration timeout, final String mimeType, final boolean calculateHash) throws IOException {
        if (Files.notExists(requireNonNull(source, "source must not be null."))) {
            throw new NoSuchFileException(source.getFileName().toString());
        }
        String hash = null;
        if (calculateHash) {
            hash = md5Hash(source);
        }
        return offerFile(Files.newInputStream(source), source.getFileName().toString(), Files.size(source), Files.getLastModifiedTime(source).toInstant(), description, recipient, timeout, UUID.randomUUID().toString(), mimeType, hash);
    }

    /**
     * Offers a stream to another user. If this method returns successfully, the recipient has accepted the offer, the stream method has been negotiated and the file is ready to be transferred.
     * Call {@link FileTransfer#transfer()} to start the file transfer.
     *
     * @param source       The stream.
     * @param fileName     The file name.
     * @param fileSize     The file size.
     * @param lastModified The last modified date.
     * @param description  The description of the file.
     * @param recipient    The recipient's JID (must be a <em>full</em> JID, i. e. including {@code resource}).
     * @param timeout      The timeout (indicates how long to wait until the file offer has either been accepted or rejected).
     * @return The async result with the file transfer object.
     */
    public final AsyncResult<FileTransfer> offerFile(final InputStream source, final String fileName, final long fileSize, final Instant lastModified, final String description, final Jid recipient, final Duration timeout) {
        return this.offerFile(source, fileName, fileSize, lastModified, description, recipient, timeout, UUID.randomUUID().toString());
    }

    /**
     * Offers a stream to another user. If this method returns successfully, the recipient has accepted the offer, the stream method has been negotiated and the file is ready to be transferred.
     * Call {@link FileTransfer#transfer()} to start the file transfer.
     *
     * @param source       The stream.
     * @param fileName     The file name.
     * @param fileSize     The file size.
     * @param lastModified The last modified date.
     * @param description  The description of the file.
     * @param recipient    The recipient's JID (must be a <em>full</em> JID, i. e. including {@code resource}).
     * @param timeout      The timeout (indicates how long to wait until the file offer has either been accepted or rejected).
     * @param sessionId    The session id.
     * @return The async result with the file transfer object.
     */
    public final AsyncResult<FileTransfer> offerFile(final InputStream source, final String fileName, final long fileSize, final Instant lastModified, final String description, final Jid recipient, final Duration timeout, final String sessionId) {
        return this.offerFile(source, fileName, fileSize, lastModified, description, recipient, timeout, sessionId, null, null);
    }

    /**
     * Offers a stream to another user. If this method returns successfully, the recipient has accepted the offer, the stream method has been negotiated and the file is ready to be transferred.
     * Call {@link FileTransfer#transfer()} to start the file transfer.
     *
     * @param source       The stream.
     * @param fileName     The file name.
     * @param fileSize     The file size.
     * @param lastModified The last modified date.
     * @param description  The description of the file.
     * @param recipient    The recipient's JID (must be a <em>full</em> JID for client-to-client file transfers, i. e. including {@code resource}).
     * @param timeout      The timeout (indicates how long to wait until the file offer has either been accepted or rejected).
     * @param sessionId    The session id.
     * @param mimeType     The mime type. If null, the mime type is guessed.
     * @param hash         The hash (maybe null). As per the specification this should be a hex-encoded MD5 hash.
     * @return The async result with the file transfer object.
     * @see #md5Hash(Path)
     */
    public final AsyncResult<FileTransfer> offerFile(final InputStream source, final String fileName, final long fileSize, final Instant lastModified, final String description, final Jid recipient, final Duration timeout, final String sessionId, final String mimeType, final String hash) {

        // Before a Stream Initiation is attempted the Sender should be sure that the Receiver supports both Stream Initiation and the specific profile that they wish to use.
        return this.entityCapabilitiesManager.isSupported(StreamInitiation.NAMESPACE, requireNonNull(recipient, "jid must not be null.")).thenCompose(isSISupported -> {
            if (!isSISupported) {
                throw new CompletionException(new XmppException("Feature not supported"));
            }
            return this.entityCapabilitiesManager.isSupported(SIFileTransferOffer.NAMESPACE, recipient).thenCompose(isSIFileTransferSupported -> {
                if (!isSIFileTransferSupported) {
                    throw new CompletionException(new XmppException("Feature not supported"));
                }

                final SIFileTransferOffer fileTransfer = new SIFileTransferOffer(fileName, fileSize, lastModified, hash, description, null);
                String mType = mimeType;
                if (mType == null) {
                    try {
                        mType = URLConnection.guessContentTypeFromStream(source);
                    } catch (IOException e) {
                        mType = null;
                    }
                }

                return this.streamInitiationManager.initiateStream(recipient, fileTransfer, mType, timeout, sessionId).handle((byteStreamSession, e) -> {
                    if (e != null) {
                        if (e instanceof CompletionException) {
                            if (e.getCause() instanceof StanzaErrorException && ((StanzaErrorException) e.getCause()).getCondition() == Condition.FORBIDDEN) {
                                throw new CompletionException(new FileTransferRejectedException());
                            } else {
                                throw (CompletionException) e;
                            }
                        } else {
                            throw new CompletionException(e);
                        }
                    }
                    OutputStream outputStream;
                    try {
                        outputStream = byteStreamSession.getOutputStream();
                    } catch (IOException e1) {
                        throw new CompletionException(e1);
                    }
                    return new FileTransfer(xmppSession, byteStreamSession.getSessionId(), source, outputStream, fileSize);
                });
            });
        });
    }

    public void fileTransferOffered(final IQ iq, final String sessionId, final String mimeType, final FileTransferOffer fileTransferOffer, final Object protocol, final FileTransferNegotiator fileTransferNegotiator) {
        FILE_TRANSFER_OFFER_EXECUTOR.execute(() -> XmppUtils.notifyEventListeners(fileTransferOfferListeners, new FileTransferOfferEvent(this, iq, sessionId, mimeType, fileTransferOffer, protocol, fileTransferNegotiator)));
    }

    /**
     * Adds a file transfer listener, which allows to listen for inbound file transfer requests.
     *
     * @param fileTransferOfferListener The listener.
     * @see #removeFileTransferOfferListener(Consumer)
     */
    public void addFileTransferOfferListener(Consumer<FileTransferOfferEvent> fileTransferOfferListener) {
        fileTransferOfferListeners.add(fileTransferOfferListener);
    }

    /**
     * Removes a previously added file transfer listener.
     *
     * @param fileTransferOfferListener The listener.
     * @see #addFileTransferOfferListener(Consumer)
     */
    public void removeFileTransferOfferListener(Consumer<FileTransferOfferEvent> fileTransferOfferListener) {
        fileTransferOfferListeners.remove(fileTransferOfferListener);
    }

    @Override
    protected void dispose() {
        fileTransferOfferListeners.clear();
    }
}
