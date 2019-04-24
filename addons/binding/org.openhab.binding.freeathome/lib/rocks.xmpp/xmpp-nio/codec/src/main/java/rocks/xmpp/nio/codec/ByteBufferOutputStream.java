/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 Christian Schudt
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

package rocks.xmpp.nio.codec;

import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * An output stream which writes to a byte buffer, similar as {@link java.io.ByteArrayOutputStream}.
 *
 * @author Christian Schudt
 */
final class ByteBufferOutputStream extends OutputStream {

    private ByteBuffer buffer;

    ByteBufferOutputStream(final int size, final boolean direct) {
        if (direct) {
            buffer = ByteBuffer.allocateDirect(size);
        } else {
            buffer = ByteBuffer.allocate(size);
        }
    }

    @Override
    public final void write(final int b) {
        if (!buffer.hasRemaining()) {
            ensureCapacity(buffer.limit() + 1);
        }
        buffer.put((byte) b);
    }

    @Override
    public final void write(final byte[] b, final int off, final int len) {
        if (off < 0 || off > b.length || len < 0 || off + len - b.length > 0) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(buffer.position() + len);
        buffer.put(b, 0, len);
    }

    private void ensureCapacity(final int minCapacity) {
        if (minCapacity - buffer.limit() > 0) {
            // Double the capacity of the buffer.
            int newCapacity = buffer.limit() << 1;
            // If it's not enough, set the new capacity to the min capacity.
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            final ByteBuffer newBuffer;
            if (buffer.isDirect()) {
                newBuffer = ByteBuffer.allocateDirect(newCapacity);
            } else {
                newBuffer = ByteBuffer.allocate(newCapacity);
            }
            buffer.flip();
            newBuffer.put(buffer);
            buffer = newBuffer;
        }
    }

    /**
     * Gets the underlying {@link ByteBuffer}.
     *
     * @return The byte buffer.
     */
    final ByteBuffer getBuffer() {
        return buffer;
    }

    @Override
    public final void close() {
        buffer = null;
    }
}