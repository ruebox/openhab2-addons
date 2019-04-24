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

package rocks.xmpp.nio.netty.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import rocks.xmpp.core.stream.model.StreamElement;
import rocks.xmpp.nio.codec.XmppStreamDecoder;

import javax.xml.bind.Unmarshaller;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Decodes byte buffers to stream elements.
 * <p>
 * This class should be added to Netty's channel pipeline.
 * The output of this decoder are implementations of {@link StreamElement}.
 *
 * @author Christian Schudt
 * @see NettyXmppEncoder
 */
final class NettyXmppDecoder extends ByteToMessageDecoder {

    private final BiConsumer<String, StreamElement> onRead;

    private final XmppStreamDecoder xmppStreamDecoder;

    private final Consumer<Throwable> onFailure;

    /**
     * Creates the decoder.
     *
     * @param onRead               The first parameter of this callback is the decoded XML element, the second one is the unmarshalled element.
     * @param unmarshallerSupplier Supplies the unmarshaller, e.g. via a {@code ThreadLocal<Unmarshaller>}
     * @param onFailure            Called when an exception in the pipeline has occurred. If null, the exception is propagated to next handler. If non-null this callback is called instead.
     */
    NettyXmppDecoder(final BiConsumer<String, StreamElement> onRead, final Supplier<Unmarshaller> unmarshallerSupplier, final Consumer<Throwable> onFailure) {
        this.onRead = onRead;
        this.xmppStreamDecoder = new XmppStreamDecoder(unmarshallerSupplier);
        this.onFailure = onFailure;
    }

    @Override
    protected final void decode(final ChannelHandlerContext ctx, final ByteBuf byteBuf, final List<Object> list) throws Exception {
        final ByteBuffer byteBuffer = byteBuf.nioBuffer();
        this.xmppStreamDecoder.decode(byteBuffer, (s, streamElement) -> {
            list.add(streamElement);
            if (onRead != null) {
                onRead.accept(s, streamElement);
            }
        });
        byteBuf.readerIndex(byteBuffer.position());
    }

    @Override
    public final void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        if (onFailure != null) {
            onFailure.accept(cause);
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }

    /**
     * Restarts the stream.
     */
    final void restart() {
        this.xmppStreamDecoder.restart();
    }
}
