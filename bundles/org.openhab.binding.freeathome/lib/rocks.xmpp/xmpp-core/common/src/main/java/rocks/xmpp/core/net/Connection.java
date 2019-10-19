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

package rocks.xmpp.core.net;

import rocks.xmpp.core.session.model.SessionOpen;
import rocks.xmpp.core.stream.model.StreamElement;
import rocks.xmpp.core.stream.model.StreamError;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletionStage;

/**
 * Represents a bi-directional XMPP connection.
 *
 * @author Christian Schudt
 */
public interface Connection extends AutoCloseable {

    /**
     * Gets the remote address of this connection.
     *
     * @return The remote address.
     */
    InetSocketAddress getRemoteAddress();

    /**
     * Gets the configuration used to create this connection.
     *
     * @return The
     */
    ConnectionConfiguration getConfiguration();

    /**
     * Indicates whether this connection uses acknowledgements, e.g. by means of stream management or BOSH acks.
     *
     * @return If this connection uses acknowledgements.
     */
    boolean isUsingAcknowledgements();

    /**
     * Opens the XML stream to the peer entity.
     *
     * @param sessionOpen The session open information.
     */
    CompletionStage<Void> open(SessionOpen sessionOpen);

    /**
     * Sends an element to the peer entity. This is basically a short cut for {@linkplain #write(StreamElement) write} + {@linkplain #flush() flush}.
     *
     * @param streamElement The element.
     * @return The future representing the send process and which allows to cancel it.
     */
    CompletionStage<Void> send(StreamElement streamElement);

    /**
     * Writes the element to the stream without really sending it. It must be {@linkplain #flush() flushed}.
     *
     * @param streamElement The element.
     * @return The send future.
     */
    CompletionStage<Void> write(StreamElement streamElement);

    /**
     * Flushes the connection. Any buffered elements written via {@link #write(StreamElement)} are sent.
     */
    void flush();

    /**
     * Indicates whether this connection is secured by TLS/SSL.
     *
     * @return True, if this connection is secured.
     */
    boolean isSecure();

    /**
     * Gets the stream id of this connection.
     *
     * @return The stream id.
     */
    String getStreamId();

    /**
     * Asynchronously closes the connection.
     * <p>
     * Closing usually involves a round-trip with the peer on the XMPP layer first by sending a closing stream element,
     * then waiting on the response and then closing the underlying transport layer.
     * <p>
     * Implementations wait a maximum of 500ms for the XMPP level close.
     *
     * @return The future, which is complete, when the connection is closed.
     * @see #close()
     */
    CompletionStage<Void> closeAsync();

    /**
     * Asynchronously closes the connection with a stream error.
     *
     * @return The completion stage, which is complete, when the connection is closed.
     * @see #closeAsync()
     */
    CompletionStage<Void> closeAsync(StreamError streamError);

    /**
     * Returns a future which is complete, when the connection is closed.
     *
     * @return The close future.
     */
    CompletionStage<Void> closeFuture();

    @Override
    void close() throws Exception;
}
