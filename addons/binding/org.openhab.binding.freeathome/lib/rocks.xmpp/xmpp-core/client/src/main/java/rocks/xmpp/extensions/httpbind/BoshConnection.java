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

import rocks.xmpp.core.net.AbstractConnection;
import rocks.xmpp.core.net.ChannelEncryption;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.session.debug.XmppDebugger;
import rocks.xmpp.core.session.model.SessionOpen;
import rocks.xmpp.core.stanza.model.Stanza;
import rocks.xmpp.core.stream.model.StreamElement;
import rocks.xmpp.extensions.compress.CompressionMethod;
import rocks.xmpp.extensions.httpbind.model.Body;
import rocks.xmpp.util.XmppUtils;
import rocks.xmpp.util.concurrent.CompletionStages;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The implementation of <a href="https://xmpp.org/extensions/xep-0124.html">XEP-0124: Bidirectional-streams Over Synchronous HTTP (BOSH)</a> and <a href="https://xmpp.org/extensions/xep-0206.html">XEP-0206: XMPP Over BOSH</a>.
 *
 * @author Christian Schudt
 */
public final class BoshConnection extends AbstractConnection {

    /**
     * The executor, which will execute HTTP requests.
     */
    private static ExecutorService HTTP_BIND_EXECUTOR = Executors.newCachedThreadPool(XmppUtils.createNamedThreadFactory("XMPP BOSH Request Thread"));

    private static final Logger logger = Logger.getLogger(BoshConnection.class.getName());

    /**
     * Use ConcurrentSkipListMap to maintain insertion order.
     */
    final Map<Long, Body.Builder> unacknowledgedRequests = new ConcurrentSkipListMap<>();

    /**
     * The request id. A large number which will get incremented with every request.
     */
    private final AtomicLong rid = new AtomicLong();

    private final BoshConnectionConfiguration boshConnectionConfiguration;

    private final XmppDebugger debugger;

    private final XmppSession xmppSession;

    private final Deque<String> keySequence = new ArrayDeque<>();

    /**
     * The current request count, i.e. the current number of simultaneous requests.
     */
    private final AtomicInteger requestCount = new AtomicInteger();

    /**
     * Our supported compression methods.
     */
    private final Map<String, CompressionMethod> compressionMethods;

    /**
     * Maps the stream element which is sent to a future associated with it. The future is done, when the element has been sent.
     */
    private final Map<StreamElement, CompletableFuture<Void>> sendFutures = new ConcurrentHashMap<>();

    /**
     * When sending, elements are put ("collected") into this collection first. Later, when the HTTP request is sent, they are all put to the request.
     * This allows to send multiple elements with one request.
     */
    private final Collection<Object> elementsToSend = new ArrayDeque<>();

    /**
     * The encoding we can support. This is added as "Accept-Encoding" header in a request.
     */
    private final String clientAcceptEncoding;

    /**
     * Guarded by "this".
     */
    private final URL url;

    private final CompletableFuture<Void> closeFuture = new CompletableFuture<>();

    private final AtomicBoolean shutdown = new AtomicBoolean(true);

    /**
     * The compression method which is used to compress requests.
     * Guarded by "this".
     */
    private CompressionMethod requestCompressionMethod;

    /**
     * Guarded by "elementsToSend".
     */
    private long highestReceivedRid;

    /**
     * The SID MUST be unique within the context of the connection manager application.
     * Guarded by "this".
     */
    private String sessionId;

    /**
     * True, if the connection manager sends acknowledgments.
     * Guarded by "this".
     */
    private boolean usingAcknowledgments;

    private SessionOpen sessionOpen;

    BoshConnection(final URL url, final XmppSession xmppSession, final BoshConnectionConfiguration configuration) {
        super(configuration);
        this.url = url;
        this.xmppSession = xmppSession;
        this.boshConnectionConfiguration = configuration;
        this.debugger = xmppSession.getDebugger();

        compressionMethods = new LinkedHashMap<>();
        for (CompressionMethod compressionMethod : boshConnectionConfiguration.getCompressionMethods()) {
            compressionMethods.put(compressionMethod.getName(), compressionMethod);
        }
        if (!compressionMethods.isEmpty()) {
            clientAcceptEncoding = String.join(",", compressionMethods.keySet());
        } else {
            clientAcceptEncoding = null;
        }
    }

    /**
     * <blockquote>
     * <p>All HTTP codes except 200 have been superseded by Terminal Binding Conditions to allow clients to determine whether the source of errors is the connection manager application or an HTTP intermediary.</p>
     * <p>A legacy client (or connection manager) is a client (or connection manager) that did not include a 'ver' attribute in its session creation request (or response). A legacy client (or connection manager) will interpret (or respond with) HTTP error codes according to the table below.</p>
     * </blockquote>
     *
     * @param httpCode The HTTP response code.
     * @throws BoshException If the HTTP code was not 200.
     */
    private static void handleCode(int httpCode) throws BoshException {
        if (httpCode != HttpURLConnection.HTTP_OK) {
            switch (httpCode) {
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    // Superseded by bad-request
                    throw new BoshException(Body.Condition.BAD_REQUEST, httpCode);
                case HttpURLConnection.HTTP_FORBIDDEN:
                    // Superseded by policy-violation
                    throw new BoshException(Body.Condition.POLICY_VIOLATION, httpCode);
                case HttpURLConnection.HTTP_NOT_FOUND:
                    // Superseded by item-not-found
                    throw new BoshException(Body.Condition.ITEM_NOT_FOUND, httpCode);
                default:
                    throw new BoshException(Body.Condition.UNDEFINED_CONDITION, httpCode);
            }
        }
    }

    /**
     * Generates a key sequence.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0124.html#keys-generate">15.3 Generating the Key Sequence</a>
     */
    private void generateKeySequence() {
        keySequence.clear();
        try {
            // K(1) = hex(SHA-1(seed))
            // K(2) = hex(SHA-1(K(1)))
            // ...
            // K(n) = hex(SHA-1(K(n-1)))

            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            Random random = new SecureRandom();
            // Generate a high random value "n"
            int n = 256 + random.nextInt(32768 - 256);
            // Generate a random seed value.
            byte[] seed = new byte[1024];
            random.nextBytes(seed);
            String kn = DatatypeConverter.printHexBinary(seed).toLowerCase();
            for (int i = 0; i < n; i++) {
                kn = DatatypeConverter.printHexBinary(digest.digest(kn.getBytes(StandardCharsets.UTF_8))).toLowerCase();
                keySequence.add(kn);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Connects to the BOSH server.
     *
     * @throws IOException If a connection could not be established.
     */
    final synchronized void connect() throws IOException {

        if (sessionId != null) {
            // Already connected.
            return;
        }

        this.sessionId = null;
        this.usingAcknowledgments = false;
        this.requestCompressionMethod = null;
        this.requestCount.set(0);

        // Set the initial request id with a large random number.
        // The largest possible number for a RID is (2^53)-1
        // So initialize it with a random number with max value of 2^52.
        // This will still allow for at least 4503599627370495 requests (2^53-1-2^52), which should be sufficient.
        rid.set(new BigInteger(52, new Random()).longValue());


        // Try if we can connect in order to fail fast if we can't.
        HttpURLConnection connection = null;
        try {
            connection = getConnection();
            connection.setConnectTimeout(boshConnectionConfiguration.getConnectTimeout());
            connection.setReadTimeout(boshConnectionConfiguration.getConnectTimeout());
            connection.connect();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        shutdown.set(false);
    }

    @Override
    public final CompletionStage<Void> open(final SessionOpen sessionOpen) {
        synchronized (this) {
            this.sessionOpen = sessionOpen;
        }
        // Create initial request.
        Body.Builder body = Body.builder()
                .language(xmppSession.getConfiguration().getLanguage())
                .version("1.11")
                .wait(boshConnectionConfiguration.getWait())
                .hold((byte) 1)
                .route(boshConnectionConfiguration.getRoute())
                .ack(1L)
                .from(sessionOpen.getFrom())
                .xmppVersion("1.0");

        if (xmppSession.getDomain() != null) {
            body.to(xmppSession.getDomain());
        }
        // Send the initial request.
        return sendNewRequest(body, false);
    }

    @Override
    public final boolean isSecure() {
        return boshConnectionConfiguration.getChannelEncryption() == ChannelEncryption.DIRECT;
    }

    /**
     * Gets the body element from the response and unpacks its content.
     * <p>
     * If it is the session creation response it contains additional attributes like the session id. These attributes are set to this connection.
     * </p>
     * The contents are delegated to the {@link rocks.xmpp.core.session.XmppSession#handleElement(Object)} method, where they are treated as normal XMPP elements, i.e. the same way as in a normal TCP connection.
     *
     * @param responseBody The body.
     * @throws Exception If any exception occurred during handling the inner XMPP elements.
     */
    private void unpackBody(Body responseBody) throws Exception {
        // It's the session creation response.
        if (responseBody.getSid() != null) {
            synchronized (this) {
                openedByPeer(responseBody);
                sessionId = responseBody.getSid();
                if (responseBody.getAck() != null) {
                    usingAcknowledgments = true;
                }
                // The connection manager MAY include an 'accept' attribute in the session creation response element, to specify a comma-separated list of the content encodings it can decompress.
                if (responseBody.getAccept() != null) {
                    // After receiving a session creation response with an 'accept' attribute,
                    // clients MAY include an HTTP Content-Encoding header in subsequent requests (indicating one of the encodings specified in the 'accept' attribute) and compress the bodies of the requests accordingly.
                    String[] serverAcceptedEncodings = responseBody.getAccept().split(",", 16);
                    // Let's see if we can compress the contents for the server by choosing a known compression method.
                    for (String serverAcceptedEncoding : serverAcceptedEncodings) {
                        requestCompressionMethod = compressionMethods.get(serverAcceptedEncoding.trim().toLowerCase());
                        if (requestCompressionMethod != null) {
                            break;
                        }
                    }
                }
                xmppSession.handleElement(responseBody);
            }
        }

        if (responseBody.getAck() != null) {
            // The response has acknowledged another request.
            ackReceived(responseBody.getAck());
        }

        // If the body contains an error condition, which is not a stream error, terminate the connection by throwing an exception.
        if (responseBody.getType() == Body.Type.TERMINATE && responseBody.getCondition() != null && responseBody.getCondition() != Body.Condition.REMOTE_STREAM_ERROR) {
            // Shutdown the connection, we don't want to send further requests from now on.
            shutdown();
            closeFuture.completeExceptionally(new BoshException(responseBody.getCondition(), responseBody.getUri()));
            throw new BoshException(responseBody.getCondition(), responseBody.getUri());
        } else if (responseBody.getType() == Body.Type.ERROR) {
            // In any response it sends to the client, the connection manager MAY return a recoverable error by setting a 'type' attribute of the <body/> element to "error". These errors do not imply that the HTTP session is terminated.
            // If it decides to recover from the error, then the client MUST repeat the HTTP request that resulted in the error, as well as all the preceding HTTP requests that have not received responses. The content of these requests MUST be identical to the <body/> elements of the original requests. This enables the connection manager to recover a session after the previous request was lost due to a communication failure.
            unacknowledgedRequests.forEach((key, value) -> sendNewRequest(value, true));
        }

        for (Object wrappedObject : responseBody.getWrappedObjects()) {
            if (xmppSession.handleElement(wrappedObject)) {
                restartStream();
            }
        }
    }

    /**
     * Restarts the stream.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/extensions/xep-0206.html#preconditions-sasl">Authentication and Resource Binding</a></cite></p>
     * <p>Upon receiving the {@code <success/>} element, the client MUST then ask the connection manager to restart the stream by sending a "restart request" that is structured as follows</p>
     * <ul>
     * <li>The BOSH {@code <body/>} element MUST include a boolean 'restart' attribute (qualified by the 'urn:xmpp:xbosh' namespace) whose value is set to "true".</li>
     * <li>The BOSH {@code <body/>} element SHOULD include the 'to' attribute.</li>
     * <li>The BOSH {@code <body/>} element SHOULD include the 'xml:lang' attribute.</li>
     * <li>The BOSH {@code <body/>} element SHOULD be empty (i.e., not contain an XML stanza). However, if the client includes an XML stanza in the body, the connection manager SHOULD ignore it.</li>
     * </ul>
     * </blockquote>
     */
    @Override
    protected final void restartStream() {
        Body.Builder bodyBuilder;
        synchronized (this) {
            bodyBuilder = Body.builder()
                    .sessionId(sessionId)
                    .restart(true)
                    .to(xmppSession.getDomain())
                    .language(xmppSession.getConfiguration().getLanguage())
                    .from(sessionOpen.getFrom());
        }
        sendNewRequest(bodyBuilder, false);
    }

    @Override
    protected CompletionStage<Void> closeStream() {
        final CompletableFuture<Void> future;
        if (!shutdown.get()) {
            final String sid = getSessionId();
            if (sid != null) {
                // Terminate the BOSH session.
                Body.Builder bodyBuilder = Body.builder()
                        .sessionId(sid)
                        .type(Body.Type.TERMINATE);

                future = sendNewRequest(bodyBuilder, false)
                        .applyToEither(CompletionStages.timeoutAfter(500, TimeUnit.MILLISECONDS), Function.identity())
                        .exceptionally(exc -> null);
            } else {
                future = CompletableFuture.completedFuture(null);
            }
            shutdown();
        } else {
            future = CompletableFuture.completedFuture(null);
        }
        return future;
    }

    @Override
    protected CompletionStage<Void> closeConnection() {
        return CompletableFuture.runAsync(() -> {
            try {
                synchronized (this) {
                    sessionId = null;
                    requestCompressionMethod = null;
                    keySequence.clear();
                }
            } finally {
                closeFuture.complete(null);
            }
        });
    }

    @Override
    public final CompletionStage<Void> closeFuture() {
        return closeFuture;
    }

    private void shutdown() {
        shutdown.set(true);
    }

    /**
     * Detaches this BOSH session without closing (aka terminating) it. This way the BOSH session is still alive on the server and can be ported over to a web page, but new BOSH requests are no longer sent by this connection.
     *
     * @return The current request ID (RID) which was used for the last BOSH request.
     * @see <a href="https://conversejs.org/docs/html/#prebinding-and-single-session-support">https://conversejs.org/docs/html/#prebinding-and-single-session-support</a>
     */
    public final long detach() {
        shutdown();
        // Return the latest and greatest rid.
        return rid.get();
    }

    @Override
    public final CompletableFuture<Void> send(StreamElement element) {
        CompletableFuture<Void> future = write(element);
        flush();
        return future;
    }

    @Override
    public final CompletableFuture<Void> write(StreamElement streamElement) {
        synchronized (elementsToSend) {
            elementsToSend.add(streamElement);
        }
        CompletableFuture<Void> sendFuture = new CompletableFuture<>();
        sendFutures.put(streamElement, sendFuture);
        return sendFuture;
    }

    @Override
    public final void flush() {
        sendNewRequest(Body.builder().sessionId(getSessionId()), false);
    }

    /**
     * Appends a key attribute to the body and generates a new key sequence if the old one is empty.
     *
     * @param bodyBuilder The builder.
     * @see <a href="https://xmpp.org/extensions/xep-0124.html#keys-use">15.4 Use of Keys</a>
     * @see <a href="https://xmpp.org/extensions/xep-0124.html#keys-switch">15.5 Switching to Another Key Sequence</a>
     */
    private void appendKey(Body.Builder bodyBuilder) {
        if (boshConnectionConfiguration.isUseKeySequence()) {
            synchronized (keySequence) {
                // For the initial request generate the sequence and set the new key.
                if (keySequence.isEmpty()) {
                    generateKeySequence();
                    bodyBuilder.newKey(keySequence.removeLast());
                } else {
                    // For every other request, set the key
                    bodyBuilder.key(keySequence.removeLast());
                    // and switch to a new sequence, if the sequence is empty.
                    if (keySequence.isEmpty()) {
                        generateKeySequence();
                        bodyBuilder.newKey(keySequence.removeLast());
                    }
                }
            }
        }
    }

    /**
     * Gets the session id of this BOSH connection.
     *
     * @return The session id.
     */
    public final synchronized String getSessionId() {
        return sessionId;
    }

//    @Override
//    public final synchronized String getStreamId() {
//        // The same procedure applies to the obsolete XMPP-specific 'authid' attribute of the BOSH <body/> element, which contains the value of the XMPP stream ID generated by the XMPP server.
//        return authId;
//    }

    @Override
    public final InetSocketAddress getRemoteAddress() {
        return InetSocketAddress.createUnresolved(url.getHost(), url.getPort());
    }

    @Override
    public final synchronized boolean isUsingAcknowledgements() {
        return usingAcknowledgments;
    }

    /**
     * Sends all elements waiting in the queue to the server.
     * <p>
     * If there are currently more requests than allowed by the server, the waiting elements will be send as soon one of the requests return.
     * </p>
     *
     * @param bodyBuilder      The body builder.
     * @param resendAfterError If the body is resent after an error has occurred. In this case the RID is not incremented.
     */
    private CompletableFuture<Void> sendNewRequest(final Body.Builder bodyBuilder, boolean resendAfterError) {

        // Make sure, no two threads access this block, in order to ensure that requestCount and shutdown.get() don't return inconsistent values.
        synchronized (this) {
            if (!shutdown.get()) {
                return CompletableFuture.runAsync(() -> {

                    // Open a HTTP connection.
                    HttpURLConnection httpConnection = null;
                    boolean responseReceived = false;
                    Body body = null;
                    try {

                        if (!resendAfterError) {
                            synchronized (elementsToSend) {
                                appendKey(bodyBuilder);

                                // Acknowledge the highest received rid.
                                // The only exception is that, after its session creation request, the client SHOULD NOT include an 'ack' attribute in any request if it has received responses to all its previous requests.
                                if (!unacknowledgedRequests.isEmpty()) {
                                    bodyBuilder.ack(highestReceivedRid);
                                }
                                bodyBuilder.wrappedObjects(elementsToSend);
                                // Prevent that the session is terminated with policy-violation due to this:
                                //
                                // If during any period the client sends a sequence of new requests equal in length to the number specified by the 'requests' attribute,
                                // and if the connection manager has not yet responded to any of the requests,
                                // and if the last request was empty
                                // and did not include either a 'pause' attribute or a 'type' attribute set to "terminate",
                                // and if the last two requests arrived within a period shorter than the number of seconds specified by the 'polling' attribute in the session creation response,
                                // then the connection manager SHOULD consider that the client is making requests more frequently than it was permitted
                                // and terminate the HTTP session and return a 'policy-violation' terminal binding error to the client.
                                //
                                // In short: If we would send a second empty request, don't do that!
                                // Also don't send a new request, if the connection is shutdown.
                                body = bodyBuilder.build();
                                if (body.getType() != Body.Type.TERMINATE &&
                                        (shutdown.get()
                                                || (requestCount.get() > 0
                                                && body.getPause() == null
                                                && !body.isRestart() && getSessionId() != null && elementsToSend.isEmpty()))) {
                                    return;
                                }
                                // Clear everything after the elements have been sent.
                                elementsToSend.clear();
                            }
                        }

                        httpConnection = getConnection();
                        httpConnection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");

                        // We can decompress server responses, so tell the server about it.
                        if (clientAcceptEncoding != null) {
                            httpConnection.setRequestProperty("Accept-Encoding", clientAcceptEncoding);
                        }

                        final CompressionMethod compressionMethod;
                        synchronized (this) {
                            compressionMethod = requestCompressionMethod;
                        }
                        // If we can compress, tell the server about it.
                        if (compressionMethod != null) {
                            httpConnection.setRequestProperty("Content-Encoding", compressionMethod.getName());
                        }

                        httpConnection.setDoOutput(true);
                        httpConnection.setRequestMethod("POST");
                        // If the connection manager does not respond in time, throw a SocketTimeoutException, which terminates the connection.
                        httpConnection.setReadTimeout(((int) boshConnectionConfiguration.getWait().getSeconds() + 5) * 1000);

                        requestCount.getAndIncrement();
                        try {
                            try (OutputStream requestStream = compressionMethod != null ? compressionMethod.compress(httpConnection.getOutputStream()) : httpConnection.getOutputStream()) {
                                ByteArrayOutputStream byteArrayOutputStreamRequest = null;
                                XMLStreamWriter xmlStreamWriter = null;
                                OutputStream xmppOutputStream = null;
                                try {
                                    if (debugger != null) {
                                        // This is for logging only.
                                        byteArrayOutputStreamRequest = new ByteArrayOutputStream();

                                        // Branch the stream, so that its output can also be logged.
                                        xmppOutputStream = XmppUtils.createBranchedOutputStream(requestStream, byteArrayOutputStreamRequest);
                                        OutputStream debuggerOutputStream = debugger.createOutputStream(xmppOutputStream);
                                        if (debuggerOutputStream != null) {
                                            xmppOutputStream = debuggerOutputStream;
                                        }
                                    }
                                    if (xmppOutputStream == null) {
                                        xmppOutputStream = requestStream;
                                    }

                                    // Create the writer for this connection.
                                    xmlStreamWriter = XmppUtils.createXmppStreamWriter(xmppSession.getConfiguration().getXmlOutputFactory().createXMLStreamWriter(xmppOutputStream, StandardCharsets.UTF_8.name()));
                                    body = bodyBuilder.requestId(rid.getAndIncrement()).build();
                                    // Then write the XML to the output stream by marshalling the object to the writer.
                                    // Marshaller needs to be recreated here, because it's not thread-safe.
                                    xmppSession.createMarshaller().marshal(body, xmlStreamWriter);
                                    xmlStreamWriter.flush();

                                    if (debugger != null) {
                                        debugger.writeStanza(new String(byteArrayOutputStreamRequest.toByteArray(), StandardCharsets.UTF_8), body);
                                    }

                                    body.getWrappedObjects().stream().filter(wrappedObject -> wrappedObject instanceof StreamElement).forEach(wrappedObject -> {
                                        StreamElement streamElement = (StreamElement) wrappedObject;
                                        CompletableFuture<Void> future = sendFutures.remove(streamElement);
                                        if (future != null) {
                                            future.complete(null);
                                        }
                                    });

                                } catch (Exception e) {
                                    rid.getAndDecrement();
                                    throw e;
                                } finally {
                                    if (xmlStreamWriter != null) {
                                        xmlStreamWriter.close();
                                    }
                                    if (xmppOutputStream != null) {
                                        xmppOutputStream.close();
                                    }
                                }
                            }

                            if (isUsingAcknowledgements()) {
                                unacknowledgedRequests.put(body.getRid(), bodyBuilder);
                            }
                            // Wait for the response
                            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                                responseReceived = true;

                                // The response itself acknowledges the request, so we can remove the request.
                                ackReceived(body.getRid());

                                // We received a response for the request. Store the RID, so that we can inform the connection manager with our next request, that we received a response.
                                synchronized (elementsToSend) {
                                    highestReceivedRid = body.getRid() != null ? body.getRid() : 0;
                                }

                                String contentEncoding = httpConnection.getHeaderField("Content-Encoding");
                                try (InputStream responseStream = contentEncoding != null ? compressionMethods.get(contentEncoding).decompress(httpConnection.getInputStream()) : httpConnection.getInputStream()) {
                                    InputStream xmppInputStream = null;
                                    ByteArrayOutputStream byteArrayOutputStream = null;
                                    XMLEventReader xmlEventReader = null;
                                    try {
                                        if (debugger != null) {
                                            // This is for logging only.
                                            byteArrayOutputStream = new ByteArrayOutputStream();
                                            // Branch the stream so that its input can be logged.
                                            xmppInputStream = XmppUtils.createBranchedInputStream(responseStream, byteArrayOutputStream);
                                            InputStream debuggerInputStream = debugger.createInputStream(xmppInputStream);
                                            if (debuggerInputStream != null) {
                                                xmppInputStream = debuggerInputStream;
                                            }
                                        }

                                        if (xmppInputStream == null) {
                                            xmppInputStream = responseStream;
                                        }

                                        // Read the response.
                                        xmlEventReader = xmppSession.getConfiguration().getXmlInputFactory().createXMLEventReader(xmppInputStream, "UTF-8");
                                        while (xmlEventReader.hasNext()) {
                                            XMLEvent xmlEvent = xmlEventReader.peek();

                                            // Parse the <body/> element.
                                            if (xmlEvent.isStartElement()) {
                                                JAXBElement<Body> element = xmppSession.createUnmarshaller().unmarshal(xmlEventReader, Body.class);

                                                if (debugger != null) {
                                                    debugger.readStanza(new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8), element.getValue());
                                                }
                                                unpackBody(element.getValue());
                                            } else {
                                                xmlEventReader.next();
                                            }
                                        }
                                    } catch (JAXBException e) {
                                        logger.log(Level.WARNING, "Server responded with malformed XML.", e);
                                    } finally {
                                        if (xmlEventReader != null) {
                                            xmlEventReader.close();
                                        }
                                        if (xmppInputStream != null) {
                                            xmppInputStream.close();
                                        }
                                    }
                                }
                                // Wait shortly before sending the long polling request.
                                // This allows the send method to chime in and send a <body/> with actual payload instead of an empty body just to "hold the line".
                                Thread.sleep(50);
                            } else {
                                // Shutdown the connection, we don't want to send further requests from now on.
                                shutdown();
                                handleCode(httpConnection.getResponseCode());
                                try (InputStream errorStream = httpConnection.getErrorStream()) {
                                    while (errorStream.read() > -1) {
                                        // Just read the error stream, so that the connection can be reused.
                                        // http://docs.oracle.com/javase/8/docs/technotes/guides/net/http-keepalive.html
                                    }
                                }
                            }
                        } finally {
                            // As soon as the client receives a response from the connection manager it sends another request, thereby ensuring that the connection manager is (almost) always holding a request that it can use to "push" data to the client.
                            if (requestCount.decrementAndGet() == 0 && responseReceived) {
                                synchronized (this) {
                                    if (!shutdown.get()) {
                                        sendNewRequest(Body.builder().sessionId(sessionId), false);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        xmppSession.notifyException(e);
                        if (body != null) {
                            body.getWrappedObjects().stream().filter(wrappedObject -> wrappedObject instanceof StreamElement).forEach(wrappedObject -> {
                                StreamElement streamElement = (StreamElement) wrappedObject;
                                CompletableFuture<Void> future = sendFutures.remove(streamElement);
                                if (future != null) {
                                    future.completeExceptionally(e);
                                }
                            });
                        }
                        throw new CompletionException(e);
                    } finally {
                        if (httpConnection != null) {
                            httpConnection.disconnect();
                        }
                    }
                }, HTTP_BIND_EXECUTOR);
            } else {
                throw new IllegalStateException("Connection already shutdown via close() or detach()");
            }
        }
    }

    private void ackReceived(Long rid) {
        if (rid != null) {
            Body.Builder body = unacknowledgedRequests.remove(rid);
            if (body != null) {
                body.build().getWrappedObjects().stream().filter(object -> object instanceof Stanza).forEach(object -> {
                    Stanza stanza = (Stanza) object;
                    xmppSession.markAcknowledged(stanza);
                });
            }
        }
    }

    private HttpURLConnection getConnection() throws IOException {
        Proxy proxy = boshConnectionConfiguration.getProxy();
        HttpURLConnection httpURLConnection;
        if (proxy != null) {
            httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
        } else {
            httpURLConnection = (HttpURLConnection) url.openConnection();
        }
        if (httpURLConnection instanceof HttpsURLConnection) {
            if (boshConnectionConfiguration.getSSLContext() != null) {
                ((HttpsURLConnection) httpURLConnection).setSSLSocketFactory(boshConnectionConfiguration.getSSLContext().getSocketFactory());
            }
            if (boshConnectionConfiguration.getHostnameVerifier() != null) {
                ((HttpsURLConnection) httpURLConnection).setHostnameVerifier(boshConnectionConfiguration.getHostnameVerifier());
            }
        }
        return httpURLConnection;
    }

    /**
     * Gets the route.
     * <blockquote>
     * <p>A connection manager MAY be configured to enable sessions with more than one server in different domains. When requesting a session with such a "proxy" connection manager, a client SHOULD include a 'route' attribute that specifies the protocol, hostname, and port of the server with which it wants to communicate, formatted as "proto:host:port" (e.g., "xmpp:example.com:9999").</p>
     * </blockquote>
     *
     * @return The route.
     */
    public final String getRoute() {
        return boshConnectionConfiguration.getRoute();
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder("BOSH connection at ").append(url);
        String streamId = getStreamId();
        if (streamId != null) {
            sb.append(" (").append(streamId).append(')');
        }
        return sb.toString();
    }
}
