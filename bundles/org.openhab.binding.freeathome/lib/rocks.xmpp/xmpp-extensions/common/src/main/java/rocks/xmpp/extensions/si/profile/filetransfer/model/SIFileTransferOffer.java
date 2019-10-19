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

package rocks.xmpp.extensions.si.profile.filetransfer.model;

import rocks.xmpp.extensions.filetransfer.model.FileTransferOffer;
import rocks.xmpp.extensions.filetransfer.model.Range;
import rocks.xmpp.extensions.hashes.model.Hash;
import rocks.xmpp.util.adapters.InstantAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * The implementation of the file transfer profile, i.e. the {@code <file/>} element.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0096.html">XEP-0096: SI File Transfer</a>
 * @see <a href="https://xmpp.org/extensions/xep-0096.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "file")
public final class SIFileTransferOffer implements FileTransferOffer {

    /**
     * http://jabber.org/protocol/si/profile/file-transfer
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/si/profile/file-transfer";

    @XmlAttribute
    private final Long size;

    @XmlAttribute
    private final String name;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantAdapter.class)
    private final Instant date;

    @XmlAttribute
    private final String hash;

    private final String desc;

    private final SIRange range;

    private SIFileTransferOffer() {
        this(null, 0);
    }

    public SIFileTransferOffer(String name, long size) {
        this(name, size, null, null, null, null);
    }

    public SIFileTransferOffer(String name, long size, Instant lastModified, String hash, String description, SIRange range) {
        this.name = name;
        this.size = size;
        this.date = lastModified;
        this.hash = hash;
        this.desc = description;
        this.range = range;
    }

    /**
     * Gets the size, in bytes, of the data to be sent.
     *
     * @return The size.
     */
    @Override
    public final long getSize() {
        return size != null ? size : 0;
    }

    /**
     * Gets the name of the file that the Sender wishes to send.
     *
     * @return The file name.
     */
    @Override
    public final String getName() {
        return name;
    }

    /**
     * Gets the last modification time of the file.
     *
     * @return The date.
     */
    @Override
    public final Instant getDate() {
        return date;
    }

    /**
     * Gets the MD5 sum of the file contents.
     *
     * @return The MD5 sum.
     */
    @Override
    public final List<Hash> getHashes() {
        if (hash != null) {
            // XEP-0096 seem to be hex encoded, while XEP-300 are base64 encoded. Convert from hex to byte array.
            return Collections.unmodifiableList(Collections.singletonList(new Hash(new BigInteger(hash, 16).toByteArray(), "md5")));
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Gets a sender-generated description of the file.
     *
     * @return The description.
     */
    @Override
    public final String getDescription() {
        return desc;
    }

    /**
     * Gets the range.
     *
     * @return The range.
     */
    @Override
    public final Range getRange() {
        return range;
    }

    /**
     * Allows to do ranged transfers.
     */
    public static final class SIRange implements Range {
        @XmlAttribute
        private final long offset;

        @XmlAttribute
        private final long length;

        private SIRange() {
            this.offset = this.length = 0;
        }

        /**
         * @param offset Specifies the position, in bytes, to start transferring the file data from. This defaults to zero (0) if not specified.
         * @param length Specifies the number of bytes to retrieve starting at offset. This defaults to the length of the file from offset to the end.
         */
        public SIRange(long offset, long length) {
            this.offset = offset;
            this.length = length;
        }

        /**
         * Gets the position, in bytes, to start transferring the file data from. This defaults to zero (0) if not specified.
         *
         * @return The offset.
         */
        @Override
        public final long getOffset() {
            return offset;
        }

        /**
         * Gets the number of bytes to retrieve starting at offset. This defaults to the length of the file from offset to the end.
         *
         * @return The length.
         */
        @Override
        public final long getLength() {
            return length;
        }
    }
}
