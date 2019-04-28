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

package rocks.xmpp.extensions.oob;

import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.AbstractIQHandler;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.extensions.filetransfer.FileTransfer;
import rocks.xmpp.extensions.filetransfer.FileTransferManager;
import rocks.xmpp.extensions.filetransfer.FileTransferNegotiator;
import rocks.xmpp.extensions.filetransfer.FileTransferStatusEvent;
import rocks.xmpp.extensions.filetransfer.model.FileTransferOffer;
import rocks.xmpp.extensions.filetransfer.model.Range;
import rocks.xmpp.extensions.hashes.model.Hash;
import rocks.xmpp.extensions.oob.model.iq.OobIQ;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

/**
 * @author Christian Schudt
 */
public final class OutOfBandFileTransferManager extends Manager implements FileTransferNegotiator {
    private final FileTransferManager fileTransferManager;

    private OutOfBandFileTransferManager(final XmppSession xmppSession) {
        super(xmppSession);
        fileTransferManager = xmppSession.getManager(FileTransferManager.class);
    }

    @Override
    protected void initialize() {
        xmppSession.addIQHandler(OobIQ.class, new AbstractIQHandler(IQ.Type.SET) {
            @Override
            protected IQ processRequest(IQ iq) {
                OobIQ oobIQ = iq.getExtension(OobIQ.class);
                final URI uri = oobIQ.getUri();
                final String description = oobIQ.getDescription();
                try {
                    HttpURLConnection connection = null;
                    try {
                        URLConnection urlConnection = uri.toURL().openConnection();

                        // Get the header information like file length and content type.
                        if (urlConnection instanceof HttpURLConnection) {
                            String mimeType;
                            final long length;
                            long lastModified;

                            connection = (HttpURLConnection) urlConnection;
                            connection.setRequestMethod("HEAD");
                            connection.connect();

                            mimeType = connection.getContentType();
                            length = connection.getContentLength();
                            lastModified = connection.getLastModified();

                            final Instant date = lastModified > 0 ? Instant.ofEpochMilli(lastModified) : null;
                            final String name = uri.toString();
                            fileTransferManager.fileTransferOffered(iq, null, mimeType, null, new FileTransferOffer() {
                                @Override
                                public long getSize() {
                                    return length;
                                }

                                @Override
                                public String getName() {
                                    return name;
                                }

                                @Override
                                public Instant getDate() {
                                    return date;
                                }

                                @Override
                                public List<Hash> getHashes() {
                                    return Collections.emptyList();
                                }

                                @Override
                                public String getDescription() {
                                    return description;
                                }

                                @Override
                                public Range getRange() {
                                    return null;
                                }
                            }, OutOfBandFileTransferManager.this);
                            return null;
                        } else {
                            // If the URL is no HTTP URL, return a stanza error.
                            return iq.createError(Condition.NOT_ACCEPTABLE);
                        }
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                } catch (IOException e1) {
                    // If the recipient attempts to retrieve the file but is unable to do so, the receiving application MUST return an <iq/> of type 'error' to the sender specifying a Not Found condition:
                    return iq.createError(Condition.ITEM_NOT_FOUND);
                }
            }
        });
    }

    @Override
    public AsyncResult<FileTransfer> accept(final IQ iq, String sessionId, FileTransferOffer fileTransferOffer, Object protocol, OutputStream outputStream) {
        return new AsyncResult<>(CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(fileTransferOffer.getName());
                URLConnection urlConnection = url.openConnection();
                final FileTransfer fileTransfer = new FileTransfer(xmppSession, iq.getId(), urlConnection.getInputStream(), outputStream, fileTransferOffer.getSize());
                fileTransfer.addFileTransferStatusListener(new Consumer<FileTransferStatusEvent>() {
                    @Override
                    public void accept(FileTransferStatusEvent e) {
                        if (e.getStatus() == FileTransfer.Status.COMPLETED) {
                            xmppSession.send(iq.createResult());
                            fileTransfer.removeFileTransferStatusListener(this);
                        } else if (e.getStatus() == FileTransfer.Status.CANCELED ||
                                e.getStatus() == FileTransfer.Status.FAILED) {
                            xmppSession.send(iq.createError(Condition.ITEM_NOT_FOUND));
                            fileTransfer.removeFileTransferStatusListener(this);
                        }
                    }
                });
                return fileTransfer;
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }));
    }

    @Override
    public void reject(IQ iq) {
        // If the recipient rejects the request outright, the receiving application MUST return an <iq/> of type 'error' to the sender specifying a Not Acceptable condition:
        xmppSession.send(iq.createError(Condition.NOT_ACCEPTABLE));
    }
}
