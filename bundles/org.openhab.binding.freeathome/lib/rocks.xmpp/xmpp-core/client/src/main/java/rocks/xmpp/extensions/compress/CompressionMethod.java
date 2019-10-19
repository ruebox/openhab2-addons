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

package rocks.xmpp.extensions.compress;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * An interface to describe a compression method.
 *
 * @author Christian Schudt
 */
public interface CompressionMethod {
    /**
     * The "zlib" compression method.
     */
    CompressionMethod ZLIB = new CompressionMethod() {
        @Override
        public String getName() {
            return "zlib";
        }

        @Override
        public InputStream decompress(InputStream inputStream) {
            return new InflaterInputStream(inputStream);
        }

        @Override
        public OutputStream compress(OutputStream outputStream) {
            return new DeflaterOutputStream(outputStream, true);
        }
    };

    /**
     * The "gzip" compression method.
     */
    CompressionMethod GZIP = new CompressionMethod() {
        @Override
        public String getName() {
            return "gzip";
        }

        @Override
        public InputStream decompress(InputStream inputStream) throws IOException {
            return new GZIPInputStream(inputStream, 65536);
        }

        @Override
        public OutputStream compress(OutputStream outputStream) throws IOException {
            return new GZIPOutputStream(outputStream, true);
        }
    };

    /**
     * The "deflate" compression method.
     */
    CompressionMethod DEFLATE = new CompressionMethod() {
        @Override
        public String getName() {
            return "deflate";
        }

        @Override
        public InputStream decompress(InputStream inputStream) {
            // See http://stackoverflow.com/a/3932260
            // Seems like most (web)server implement deflate in a wrong way.
            return new InflaterInputStream(inputStream, new Inflater(true));
        }

        @Override
        public OutputStream compress(OutputStream outputStream) {
            return new DeflaterOutputStream(outputStream, new Deflater(Deflater.DEFAULT_COMPRESSION, true), true);
        }
    };

    /**
     * Gets the compression method name, e.g. "zlib", "lzw", "exi", ...
     *
     * @return The compression method name.
     */
    String getName();

    /**
     * Decompresses an input stream.
     *
     * @param inputStream The compressed input stream.
     * @return The decompressed input stream.
     * @throws IOException If the decompression failed.
     */
    InputStream decompress(InputStream inputStream) throws IOException;

    /**
     * Gets the compressed output stream.
     *
     * @param outputStream The uncompressed output stream.
     * @return The compressed output stream.
     * @throws IOException If the compression failed.
     */
    OutputStream compress(OutputStream outputStream) throws IOException;
}
