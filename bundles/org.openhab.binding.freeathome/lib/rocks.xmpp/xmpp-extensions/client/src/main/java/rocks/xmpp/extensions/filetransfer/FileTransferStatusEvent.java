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

import java.util.EventObject;

/**
 * @author Christian Schudt
 */
@SuppressWarnings("serial")
public final class FileTransferStatusEvent extends EventObject {

    private final FileTransfer.Status status;

    /**
     * Constructs a prototypical Event.
     *
     * @param source           The object on which the Event initially occurred.
     * @param bytesTransferred The number of bytes transferred so far.
     * @throws IllegalArgumentException if source is null.
     */
    FileTransferStatusEvent(Object source, FileTransfer.Status status, final long bytesTransferred) {
        super(source);
        this.status = status;
        this.bytesTransferred = bytesTransferred;
    }

    /**
     * Gets the file transfer status.
     *
     * @return The status.
     */
    public FileTransfer.Status getStatus() {
        return status;
    }

    private final long bytesTransferred;

    /**
     * Gets the number of bytes transferred so far.
     *
     * @return The number of bytes transferred so far.
     */
    public final long getBytesTransferred() {
        return bytesTransferred;
    }

    /**
     * Returns a {@code String} representation of this {@code FileTransferStatusEvent} object.
     *
     * @since 0.5.0
     */
    @Override
    public final String toString() {
        return FileTransferStatusEvent.class.getName() + "[source=" + source + ", status=" + status + ", bytesTransferred=" + bytesTransferred + "]";
    }
}
