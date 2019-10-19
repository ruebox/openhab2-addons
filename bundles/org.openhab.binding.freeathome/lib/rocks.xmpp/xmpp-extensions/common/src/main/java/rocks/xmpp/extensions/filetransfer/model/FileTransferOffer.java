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

package rocks.xmpp.extensions.filetransfer.model;

import rocks.xmpp.extensions.hashes.model.Hash;

import java.time.Instant;
import java.util.List;

/**
 * An interface for file transfer requests, which covers XEP-0066, XEP-0096 and XEP-0234.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0066.html">XEP-0066: Out of Band Data</a>
 * @see <a href="https://xmpp.org/extensions/xep-0096.html">XEP-0096: SI File Transfer</a>
 * @see <a href="https://xmpp.org/extensions/xep-0234.html">XEP-0234: Jingle File Transfer</a>
 */
public interface FileTransferOffer {

    /**
     * Gets the size, in bytes, of the data to be sent.
     *
     * @return The size.
     */
    long getSize();

    /**
     * Gets the name of the file that the Sender wishes to send.
     *
     * @return The file name.
     */
    String getName();

    /**
     * Gets the last modification time of the file.
     *
     * @return The date.
     */
    Instant getDate();

    /**
     * Gets the hashes of the file contents.
     *
     * @return The hashes.
     */
    List<Hash> getHashes();

    /**
     * Gets a sender-generated description of the file.
     *
     * @return The description.
     */
    String getDescription();

    /**
     * Gets the range.
     *
     * @return The range.
     */
    Range getRange();
}
