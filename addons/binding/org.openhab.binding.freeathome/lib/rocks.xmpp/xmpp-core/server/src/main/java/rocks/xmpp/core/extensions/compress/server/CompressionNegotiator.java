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

package rocks.xmpp.core.extensions.compress.server;

import rocks.xmpp.core.net.TcpBinding;
import rocks.xmpp.core.stream.StreamNegotiationResult;
import rocks.xmpp.core.stream.model.StreamError;
import rocks.xmpp.core.stream.model.errors.Condition;
import rocks.xmpp.core.stream.server.ServerStreamFeatureNegotiator;
import rocks.xmpp.extensions.compress.model.StreamCompression;
import rocks.xmpp.extensions.compress.model.feature.CompressionFeature;

import java.util.Arrays;

/**
 * Negotiates stream compression by advertising support for it and later compressing the stream.
 *
 * @author Christian Schudt
 */
public final class CompressionNegotiator extends ServerStreamFeatureNegotiator<CompressionFeature> {

    private final CompressionFeature feature;

    private final TcpBinding connection;

    /**
     * Constructs the compression negotiator.
     *
     * @param connection The connection.
     * @param method     The method.
     */
    public CompressionNegotiator(final TcpBinding connection, final String... method) {
        super(CompressionFeature.class);
        this.connection = connection;
        this.feature = new CompressionFeature(Arrays.asList(method));
    }

    @Override
    public final CompressionFeature createStreamFeature() {
        return feature;
    }

    @Override
    public final StreamNegotiationResult processNegotiation(final Object element) {
        if (element instanceof StreamCompression.Compress) {
            final StreamCompression.Compress compress = (StreamCompression.Compress) element;
            try {
                final Runnable onSuccess = () -> connection.send(StreamCompression.COMPRESSED);
                connection.compressConnection(compress.getMethod(), onSuccess);
                return StreamNegotiationResult.RESTART;
            } catch (Exception e) {
                connection.closeAsync(new StreamError(Condition.UNDEFINED_CONDITION, new StreamCompression.Failure(StreamCompression.Failure.Condition.UNSUPPORTED_METHOD)));
            }
        }
        return StreamNegotiationResult.IGNORE;
    }

    @Override
    public final boolean canProcess(final Object element) {
        return element instanceof StreamCompression.Compress;
    }
}

