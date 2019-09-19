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
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.extensions.filetransfer.model.FileTransferOffer;
import rocks.xmpp.extensions.filetransfer.model.Range;
import rocks.xmpp.extensions.hashes.model.Hash;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.EventObject;
import java.util.List;

/**
 * @author Christian Schudt
 */
public final class FileTransferOfferEvent extends EventObject implements FileTransferOffer {

    private final transient FileTransferOffer fileTransferOffer;

    private final transient IQ iq;

    private final String mimeType;

    private final String sessionId;

    private final transient FileTransferNegotiator fileTransferNegotiator;

    private final transient Object protocol;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    FileTransferOfferEvent(Object source, IQ iq, String sessionId, String mimeType, FileTransferOffer fileTransferOffer, Object protocol, FileTransferNegotiator fileTransferNegotiator) {
        super(source);
        this.sessionId = sessionId;
        this.iq = iq;
        this.fileTransferOffer = fileTransferOffer;
        this.mimeType = mimeType;
        this.fileTransferNegotiator = fileTransferNegotiator;
        this.protocol = protocol;
    }

    /**
     * Gets the mime type of the file.
     *
     * @return The mime type.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Gets the initiator.
     *
     * @return The initiator.
     */
    public Jid getInitiator() {
        return iq.getFrom();
    }

    /**
     * Accepts the inbound file transfer request.
     * After accepting the file transfer you should call {@link FileTransfer#transfer()} in order to start the transfer.
     *
     * @param outputStream The output stream, to which the file will be written.
     * @return The file transfer object.
     */
    public AsyncResult<FileTransfer> accept(OutputStream outputStream) {
        return fileTransferNegotiator.accept(iq, sessionId, fileTransferOffer, protocol, outputStream);
    }

    /**
     * Accepts the inbound file transfer request.
     * After accepting the file transfer you should call {@link FileTransfer#transfer()} in order to start the transfer.
     *
     * @param destination The path of the file to be written.
     * @return The file transfer object.
     * @throws java.io.IOException If the byte stream session could not be established.
     */
    public final AsyncResult<FileTransfer> accept(final Path destination) throws IOException {
        return accept(Files.newOutputStream(destination));
    }

    /**
     * Accepts the inbound file transfer request.
     * After accepting the file transfer you should call {@link FileTransfer#transfer()} in order to start the transfer.
     *
     * @param target The file to be written.
     * @return The file transfer object.
     * @throws java.io.IOException If the byte stream session could not be established.
     */
    public final AsyncResult<FileTransfer> accept(final File target) throws IOException {
        return accept(target.toPath());
    }

    /**
     * Rejects the inbound file transfer request.
     */
    public void reject() {
        fileTransferNegotiator.reject(iq);
    }

    @Override
    public long getSize() {
        return fileTransferOffer.getSize();
    }

    @Override
    public String getName() {
        return fileTransferOffer.getName();
    }

    @Override
    public Instant getDate() {
        return fileTransferOffer.getDate();
    }

    @Override
    public List<Hash> getHashes() {
        return fileTransferOffer.getHashes();
    }

    @Override
    public String getDescription() {
        return fileTransferOffer.getDescription();
    }

    @Override
    public Range getRange() {
        return fileTransferOffer.getRange();
    }

    public String getSessionId() {
        return sessionId;
    }
}
