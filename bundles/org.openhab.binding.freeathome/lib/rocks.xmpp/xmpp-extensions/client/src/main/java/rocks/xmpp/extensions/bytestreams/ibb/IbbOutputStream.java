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

package rocks.xmpp.extensions.bytestreams.ibb;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An output stream implementation for in-band bytestreams.
 * <p>
 * This class is thread-safe.
 *
 * @author Christian Schudt
 */
final class IbbOutputStream extends OutputStream {

    private final IbbSession ibbSession;

    /**
     * Guarded by "this"
     */
    private final byte[] buffer;

    /**
     * Guarded by "this"
     */
    private int n;

    /**
     * Guarded by "this"
     */
    private boolean closed;

    public IbbOutputStream(IbbSession ibbSession, int blockSize) {
        this.ibbSession = ibbSession;
        this.buffer = new byte[blockSize];
    }

    @Override
    public final synchronized void write(int b) throws IOException {
        buffer[n++] = (byte) b;
        if (n == buffer.length) {
            flush();
        }
    }

    @Override
    public final synchronized void flush() throws IOException {
        super.flush();
        if (closed) {
            throw new IOException("Stream is closed.");
        }
        // If the buffer is empty, there's nothing to do.
        if (n == 0) {
            return;
        }
        try {
            ibbSession.send(Arrays.copyOf(buffer, n)).get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException();
        } catch (ExecutionException e) {
            throw new IOException(e.getCause());
        } catch (TimeoutException e) {
            throw new IOException(e);
        } finally {
            n = 0;
        }
    }

    @Override
    public final void close() throws IOException {
        synchronized (this) {
            if (closed) {
                return;
            }
            super.close();
            flush();
            closed = true;
        }
        ibbSession.close();
    }
}
