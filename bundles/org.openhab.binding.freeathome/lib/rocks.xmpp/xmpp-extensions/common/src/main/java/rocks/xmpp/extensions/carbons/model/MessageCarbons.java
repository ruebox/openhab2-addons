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

package rocks.xmpp.extensions.carbons.model;

import rocks.xmpp.extensions.forward.model.Forwarded;
import rocks.xmpp.extensions.receipts.model.MessageDeliveryReceipts;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * A non-instantiable container class for holding the five different Message Carbons elements.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0280.html">XEP-0280: Message Carbons</a>
 * @see <a href="https://xmpp.org/extensions/xep-0280.html#schema">XML Schema</a>
 */
@XmlTransient
@XmlSeeAlso({MessageCarbons.Enable.class, MessageCarbons.Disable.class, MessageCarbons.Private.class, MessageDeliveryReceipts.Received.class, MessageCarbons.Sent.class})
public final class MessageCarbons {
    /**
     * urn:xmpp:carbons:2
     */
    public static final String NAMESPACE = "urn:xmpp:carbons:2";

    /**
     * The implementation of the {@code <disable/>} element in the {@code urn:xmpp:carbons:2} namespace.
     */
    public static final Disable DISABLE = new Disable();

    /**
     * The implementation of the {@code <enable/>} element in the {@code urn:xmpp:carbons:2} namespace.
     */
    public static final Enable ENABLE = new Enable();

    /**
     * The implementation of the {@code <private/>} element in the {@code urn:xmpp:carbons:2} namespace.
     */
    public static final Private PRIVATE = new Private();

    private MessageCarbons() {
    }

    /**
     * The implementation of the {@code <disable/>} element in the {@code urn:xmpp:carbons:2} namespace, used to disable message carbons.
     * <p>
     * This class is a singleton.
     *
     * @see #DISABLE
     */
    @XmlRootElement
    @XmlType(factoryMethod = "create")
    public static final class Disable {
        private Disable() {
        }

        private static Disable create() {
            return DISABLE;
        }
    }

    /**
     * The implementation of the {@code <enable/>} element in the {@code urn:xmpp:carbons:2} namespace, used to enable message carbons.
     * <p>
     * This class is a singleton.
     *
     * @see #ENABLE
     */
    @XmlRootElement
    @XmlType(factoryMethod = "create")
    public static final class Enable {
        private Enable() {
        }

        private static Enable create() {
            return ENABLE;
        }
    }

    /**
     * The implementation of the {@code <private/>} element in the {@code urn:xmpp:carbons:2} namespace, used to send private messages, which are not copied.
     * <p>
     * This class is a singleton.
     *
     * @see #PRIVATE
     */
    @XmlRootElement
    @XmlType(factoryMethod = "create")
    public static final class Private {
        private Private() {
        }

        private static Private create() {
            return PRIVATE;
        }
    }

    /**
     * The implementation of the {@code <received/>} element in the {@code urn:xmpp:carbons:2} namespace, used to mark a carbon copied message as received.
     * <p>
     * This class is immutable.
     */
    @XmlRootElement
    public static final class Received {

        @XmlElementRef
        private final Forwarded forwarded;

        private Received() {
            forwarded = null;
        }

        /**
         * @param forwardedMessage The forwarded message.
         */
        public Received(Forwarded forwardedMessage) {
            this.forwarded = Objects.requireNonNull(forwardedMessage, "forwardedMessage must not be null.");
        }

        /**
         * Gets the forwarded message.
         *
         * @return The forwarded message.
         */
        public final Forwarded getForwardedMessage() {
            return forwarded;
        }
    }

    /**
     * The implementation of the {@code <sent/>} element in the {@code urn:xmpp:carbons:2} namespace, used to mark a carbon copied message as sent.
     * <p>
     * This class is immutable.
     */
    @XmlRootElement
    public static final class Sent {

        @XmlElementRef
        private final Forwarded forwarded;

        private Sent() {
            this.forwarded = null;
        }

        /**
         * @param forwardedMessage The forwarded message.
         */
        public Sent(Forwarded forwardedMessage) {
            this.forwarded = Objects.requireNonNull(forwardedMessage, "forwardedMessage must not be null.");
        }

        /**
         * Gets the forwarded message.
         *
         * @return The forwarded message.
         */
        public final Forwarded getForwardedMessage() {
            return forwarded;
        }
    }
}
