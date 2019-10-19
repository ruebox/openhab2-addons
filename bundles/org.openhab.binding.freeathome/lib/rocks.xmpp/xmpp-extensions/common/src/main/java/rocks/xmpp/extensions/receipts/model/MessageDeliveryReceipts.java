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

package rocks.xmpp.extensions.receipts.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * A container class for Message Delivery Receipts.
 * <h3>Requesting a Message Delivery Receipt</h3>
 * ```java
 * message.addExtension(MessageDeliveryReceipts.REQUEST);
 * ```
 * <h3>Sending an Receipt to a Message Delivery Request</h3>
 * ```java
 * message.addExtension(new MessageDeliveryReceipts.Received(id));
 * ```
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0184.html">XEP-0184: Message Delivery Receipts</a>
 * @see <a href="https://xmpp.org/extensions/xep-0184.html#schema">XML Schema</a>
 */
@XmlTransient
@XmlSeeAlso({MessageDeliveryReceipts.Request.class, MessageDeliveryReceipts.Received.class})
public abstract class MessageDeliveryReceipts {

    /**
     * The implementation of the {@code <request/>} element in the {@code urn:xmpp:receipts} namespace.
     */
    public static final Request REQUEST = new Request();

    /**
     * urn:xmpp:receipts
     */
    public static final String NAMESPACE = "urn:xmpp:receipts";

    private MessageDeliveryReceipts() {
    }

    /**
     * The implementation of the {@code <received/>} element in the {@code urn:xmpp:receipts} namespace.
     * <p>
     * This class is immutable.
     */
    @XmlRootElement
    public static final class Received extends MessageDeliveryReceipts {

        @XmlAttribute
        public final String id;

        private Received() {
            this.id = null;
        }

        /**
         * Create the {@code <received/>} element.
         *
         * @param id The message id to confirm.
         */
        public Received(String id) {
            this.id = Objects.requireNonNull(id, "id must not be null");
        }

        /**
         * Gets the message id of the received message.
         *
         * @return The message id.
         */
        public final String getId() {
            return id;
        }

        @Override
        public final String toString() {
            return "Message Delivery Receipt for: " + id;
        }
    }

    /**
     * The implementation of the {@code <request/>} element in the {@code urn:xmpp:receipts} namespace.
     * This class is a singleton.
     *
     * @see #REQUEST
     */
    @XmlRootElement
    @XmlType(factoryMethod = "create")
    public static final class Request extends MessageDeliveryReceipts {
        private Request() {
        }

        private static Request create() {
            return MessageDeliveryReceipts.REQUEST;
        }

        @Override
        public final String toString() {
            return "Message Delivery Receipt Request";
        }
    }
}
