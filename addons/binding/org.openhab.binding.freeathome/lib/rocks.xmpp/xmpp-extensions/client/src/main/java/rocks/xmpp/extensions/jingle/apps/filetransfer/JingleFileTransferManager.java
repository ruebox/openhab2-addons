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

package rocks.xmpp.extensions.jingle.apps.filetransfer;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.NoResponseException;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.extensions.bytestreams.ibb.InBandByteStreamManager;
import rocks.xmpp.extensions.filetransfer.FileTransferRejectedException;
import rocks.xmpp.extensions.jingle.JingleManager;
import rocks.xmpp.extensions.jingle.JingleSession;
import rocks.xmpp.extensions.jingle.apps.filetransfer.model.JingleFileTransfer;
import rocks.xmpp.extensions.jingle.model.Jingle;
import rocks.xmpp.extensions.jingle.transports.ibb.model.InBandByteStreamsTransportMethod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Christian Schudt
 */
public final class JingleFileTransferManager extends Manager {

    private final JingleManager jingleManager;

    private JingleFileTransferManager(XmppSession xmppSession) {
        super(xmppSession);
        jingleManager = xmppSession.getManager(JingleManager.class);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        jingleManager.registerApplicationFormat(JingleFileTransfer.class, JingleFileTransferManager::onFileTransferRequest);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        jingleManager.unregisterApplicationFormat(JingleFileTransfer.class);
    }

    private static void onFileTransferRequest(JingleSession jingleSession) {
        jingleSession.accept(jingleSession.getContents().get(0));
    }

    public JingleFileTransferSession initiateFileTransferSession(final Jid responder, Path file, String description, long timeout) throws XmppException, IOException {

        // Create the jingle file transfer description element with a file element.
        JingleFileTransfer.File jingleFile = new JingleFileTransfer.File(file.getFileName().toString(), Files.size(file), Files.getLastModifiedTime(file).toInstant(), null, description);
        JingleFileTransfer jingleFileTransfer = new JingleFileTransfer(jingleFile);

        // Create an IBB transport.
        String sessionId = UUID.randomUUID().toString();
        InBandByteStreamsTransportMethod ibbTransportMethod = new InBandByteStreamsTransportMethod(4096, sessionId);

        // Create the content element with the application format and transport method.
        Jingle.Content content = new Jingle.Content("a-file-offer", Jingle.Content.Creator.INITIATOR, jingleFileTransfer, ibbTransportMethod, null, Jingle.Content.Senders.INITIATOR);
        final JingleSession jingleSession = jingleManager.createSession(responder, sessionId, content);

        CompletableFuture<Jingle> jingleResponseFuture = new CompletableFuture<>();
        jingleSession.addJingleListener(e -> {
            if (e.getJingle().getAction() == Jingle.Action.SESSION_ACCEPT || e.getJingle().getAction() == Jingle.Action.SESSION_TERMINATE) {
                jingleResponseFuture.complete(e.getJingle());
            }
        });
        jingleSession.initiate();

        try {
            // Wait until the session is either accepted or declined (terminated).
            Jingle jingle = jingleResponseFuture.get(timeout, TimeUnit.MILLISECONDS);

            if (jingle.getAction() == Jingle.Action.SESSION_TERMINATE) {
                throw new FileTransferRejectedException();
            }
            if (jingle.getAction() == Jingle.Action.SESSION_ACCEPT) {
                // TODO respect responders transport method.
                InBandByteStreamManager inBandByteStreamManager = xmppSession.getManager(InBandByteStreamManager.class);
                inBandByteStreamManager.initiateSession(responder, sessionId, 4096);
            }
            return new JingleFileTransferSession(jingleSession);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new XmppException(e);
        } catch (ExecutionException e) {
            throw new XmppException(e);
        } catch (TimeoutException e) {
            throw new NoResponseException("The receiver did not respond in time.");
        }
    }
}
