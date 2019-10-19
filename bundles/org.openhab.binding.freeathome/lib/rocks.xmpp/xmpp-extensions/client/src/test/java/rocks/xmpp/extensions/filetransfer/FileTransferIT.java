/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Christian Schudt
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

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import rocks.xmpp.core.IntegrationTest;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.net.client.SocketConnectionConfiguration;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.session.XmppSessionConfiguration;
import rocks.xmpp.core.session.debug.ConsoleDebugger;
import rocks.xmpp.extensions.bytestreams.ibb.InBandByteStreamManager;
import rocks.xmpp.extensions.bytestreams.ibb.model.InBandByteStream;
import rocks.xmpp.extensions.bytestreams.s5b.model.Socks5ByteStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * @author Christian Schudt
 */
public class FileTransferIT extends IntegrationTest {

    private final XmppClient[] xmppSession = new XmppClient[2];

    private final FileTransferManager[] fileTransferManagers = new FileTransferManager[2];

    @BeforeClass
    public void before() throws XmppException {
        XmppSessionConfiguration configuration = XmppSessionConfiguration.builder()
                .debugger(ConsoleDebugger.class)
                .build();
        xmppSession[0] = XmppClient.create(DOMAIN, SocketConnectionConfiguration.getDefault());
        xmppSession[0].connect();
        xmppSession[0].login(USER_1, PASSWORD_1);

        xmppSession[1] = XmppClient.create(DOMAIN, configuration, SocketConnectionConfiguration.getDefault());
        xmppSession[1].connect();
        xmppSession[1].login(USER_2, PASSWORD_2);

        fileTransferManagers[0] = xmppSession[0].getManager(FileTransferManager.class);
        fileTransferManagers[1] = xmppSession[1].getManager(FileTransferManager.class);
    }

    @Test
    public void testInBandFileTransfer() throws InterruptedException, TimeoutException, ExecutionException {
        // Disable SOCKS 5 transfer method, so that IBB will be used.
        xmppSession[1].disableFeature(Socks5ByteStream.NAMESPACE);
        xmppSession[0].getManager(InBandByteStreamManager.class).setStanzaType(InBandByteStream.Open.StanzaType.IQ);
        testFileTransfer();
    }

    @Test
    public void testInBandFileTransferWithMessages() throws InterruptedException, TimeoutException, ExecutionException {
        // Disable SOCKS 5 transfer method, so that IBB will be used.
        xmppSession[1].disableFeature(Socks5ByteStream.NAMESPACE);
        xmppSession[0].getManager(InBandByteStreamManager.class).setStanzaType(InBandByteStream.Open.StanzaType.MESSAGE);
        testFileTransfer();
    }

    @Test
    public void testSocks5FileTransfer() throws InterruptedException, TimeoutException, ExecutionException {
        // Enable SOCKS 5 transfer method.
        xmppSession[1].enableFeature(Socks5ByteStream.NAMESPACE);
        testFileTransfer();
    }


    private void testFileTransfer() throws InterruptedException, TimeoutException, ExecutionException {
        // The data we want to send (representing a file).
        byte[] data = new byte[40960]; // 40 KB, should be splitted into 10 chunks.
        Random random = new Random();
        random.nextBytes(data);

        CompletableFuture<Void> transferCompleted = new CompletableFuture<>();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Consumer<FileTransferOfferEvent> listener = e -> {
            try {
                FileTransfer fileTransfer = e.accept(outputStream).get();
                Assert.assertEquals(e.getDescription(), "Description");
                Assert.assertEquals(e.getName(), "test.txt");
                Assert.assertEquals(e.getSize(), 40960);

                fileTransfer.addFileTransferStatusListener(ev -> {
                    System.out.println(ev.getStatus() + ", " + fileTransfer.isDone());
                    if (ev.getStatus() == FileTransfer.Status.COMPLETED) {
                        transferCompleted.complete(null);
                    }
                });
                fileTransfer.transfer();
            } catch (InterruptedException | ExecutionException e1) {
                Assert.fail(e1.getMessage(), e1);
            }
        };
        try {
            // Let the receiver listen for incoming file transfers.
            fileTransferManagers[1].addFileTransferOfferListener(listener);
            FileTransfer fileTransfer = fileTransferManagers[0].offerFile(new ByteArrayInputStream(data), "test.txt", data.length, Instant.now(), "Description", xmppSession[1].getConnectedResource(), Duration.ofSeconds(12)).get();
            fileTransfer.addFileTransferStatusListener(ev -> {
                //System.out.println(ev.getStatus() + ", " + fileTransfer.isDone());
            });
            fileTransfer.transfer();
            transferCompleted.get(5, TimeUnit.SECONDS);
            Assert.assertEquals(outputStream.toByteArray(), data);
        } finally {
            fileTransferManagers[1].removeFileTransferOfferListener(listener);
        }
    }
}
