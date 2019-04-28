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

package rocks.xmpp.extensions.compress.model;

import rocks.xmpp.core.stream.model.StreamElement;
import rocks.xmpp.extensions.compress.model.feature.CompressionFeature;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * An abstract base class for all stream compression classes in the {@code http://jabber.org/protocol/compress} namespace.
 *
 * @author Christian Schudt
 */
@XmlTransient
@XmlSeeAlso({CompressionFeature.class, StreamCompression.Compress.class, StreamCompression.Compressed.class, StreamCompression.Failure.class})
public abstract class StreamCompression implements StreamElement {

    /**
     * http://jabber.org/protocol/compress
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/compress";

    /**
     * The implementation of the {@code <compressed/>} element.
     */
    public static final StreamCompression COMPRESSED = new Compressed();

    private StreamCompression() {
    }

    /**
     * The implementation of the {@code <failure/>} element in the {@code http://jabber.org/protocol/compress} namespace, which indicates failure during compression negotiation.
     * <p>
     * This class is immutable.
     *
     * @author Christian Schudt
     * @see <a href="https://xmpp.org/extensions/xep-0138.html">XEP-0138: Stream Compression</a>
     * @see <a href="https://xmpp.org/extensions/xep-0138.html#schemas-protocol">XML Schema</a>
     */
    @XmlRootElement
    public static final class Failure extends StreamCompression {

        @XmlElements({@XmlElement(name = "setup-failed", type = SetupFailed.class),
                @XmlElement(name = "processing-failed", type = ProcessingFailed.class),
                @XmlElement(name = "unsupported-method", type = UnsupportedMethod.class)})
        private final Condition condition;

        private Failure() {
            this.condition = null;
        }

        public Failure(Condition condition) {
            this.condition = Objects.requireNonNull(condition);
        }

        /**
         * Gets the failure condition.
         *
         * @return The condition.
         * @see rocks.xmpp.extensions.compress.model.StreamCompression.Failure.UnsupportedMethod
         * @see rocks.xmpp.extensions.compress.model.StreamCompression.Failure.SetupFailed
         * @see rocks.xmpp.extensions.compress.model.StreamCompression.Failure.ProcessingFailed
         */
        public final Condition getCondition() {
            return condition;
        }

        /**
         * An abstract base class for a compression failure condition.
         */
        @XmlTransient
        public abstract static class Condition {
            /**
             * If the receiving entity finds the requested method unacceptable or unworkable for any other reason.
             */
            public static final Condition SETUP_FAILED = new SetupFailed();

            /**
             * If compression processing fails after the new (compressed) stream has been established.
             */
            public static final Condition PROCESSING_FAILED = new ProcessingFailed();

            /**
             * If the initiating entity requests a stream compression method that is not supported by the receiving entity.
             */
            public static final Condition UNSUPPORTED_METHOD = new UnsupportedMethod();

            private Condition() {
            }

            @Override
            public final String toString() {
                return getClass().getName();
            }
        }

        /**
         * If the receiving entity finds the requested method unacceptable or unworkable for any other reason.
         */
        @XmlType(factoryMethod = "create")
        private static final class SetupFailed extends Condition {
            private SetupFailed() {
            }

            private static SetupFailed create() {
                return (SetupFailed) SETUP_FAILED;
            }
        }

        /**
         * If compression processing fails after the new (compressed) stream has been established.
         */
        @XmlType(factoryMethod = "create")
        private static final class ProcessingFailed extends Condition {
            private ProcessingFailed() {
            }

            private static ProcessingFailed create() {
                return (ProcessingFailed) PROCESSING_FAILED;
            }
        }

        /**
         * If the initiating entity requests a stream compression method that is not supported by the receiving entity.
         */
        @XmlType(factoryMethod = "create")
        private static final class UnsupportedMethod extends Condition {
            private UnsupportedMethod() {
            }

            private static UnsupportedMethod create() {
                return (UnsupportedMethod) UNSUPPORTED_METHOD;
            }
        }
    }

    /**
     * The implementation of the {@code <compress/>} element in the {@code http://jabber.org/protocol/compress} namespace.
     * <p>
     * This class is immutable.
     *
     * @author Christian Schudt
     * @see <a href="https://xmpp.org/extensions/xep-0138.html">XEP-0138: Stream Compression</a>
     * @see <a href="https://xmpp.org/extensions/xep-0138.html#schemas-protocol">XML Schema</a>
     */
    @XmlRootElement
    public static final class Compress implements StreamElement {

        private final String method;

        private Compress() {
            this.method = null;
        }

        public Compress(String method) {
            this.method = Objects.requireNonNull(method);
        }

        /**
         * Gets the compression method.
         *
         * @return The compression method.
         */
        public final String getMethod() {
            return method;
        }
    }

    /**
     * The implementation of the {@code <compressed/>} element in the {@code http://jabber.org/protocol/compress} namespace, which indicates that the stream is now compressed.
     *
     * @author Christian Schudt
     * @see <a href="https://xmpp.org/extensions/xep-0138.html">XEP-0138: Stream Compression</a>
     * @see <a href="https://xmpp.org/extensions/xep-0138.html#schemas-protocol">XML Schema</a>
     */
    @XmlRootElement
    @XmlType(factoryMethod = "create")
    static final class Compressed extends StreamCompression {
        private Compressed() {
        }

        private static Compressed create() {
            return (Compressed) COMPRESSED;
        }
    }
}
