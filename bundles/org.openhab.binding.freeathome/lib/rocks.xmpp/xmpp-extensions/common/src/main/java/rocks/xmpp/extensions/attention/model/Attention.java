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

package rocks.xmpp.extensions.attention.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The implementation of the {@code <attention/>} element in the {@code urn:xmpp:attention:0} namespace.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/extensions/xep-0224.html">XEP-0224: Attention</a></cite></p>
 * <p>This feature is known as 'nudge' or 'buzz' in some non-XMPP IM protocols.</p>
 * </blockquote>
 * <h3>Listening for Attention Requests</h3>
 * <p>If you want to listen for inbound attention requests, listen for inbound messages and check if they have the {@link rocks.xmpp.extensions.attention.model.Attention} extension.
 * </p>
 * ```java
 * xmppClient.addInboundMessageListener(e -> {
 *     if (e.getMessage().hasExtension(Attention.class)) {
 *         // Handle attention request.
 *     }
 * });
 * ```java
 * <p>You should also enable this feature (preferably before login), in order to register this extension in service discovery:</p>
 * ```java
 * xmppClient.enableFeature(Attention.NAMESPACE);
 * ```
 * <h3>Requesting Attention</h3>
 * Attention requests are sent via a 'headline' message:
 * ```java
 * Message message = new Message(jid, Message.Type.HEADLINE);
 * message.addExtension(Attention.INSTANCE);
 * xmppClient.send(message);
 * ```
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0224.html">XEP-0224: Attention</a>
 * @see <a href="https://xmpp.org/extensions/xep-0224.html#schema">XML Schema</a>
 */
@XmlRootElement
@XmlType(factoryMethod = "create")
public final class Attention {

    /**
     * The {@code <attention/>} element.
     */
    public static final Attention INSTANCE = new Attention();

    /**
     * urn:xmpp:attention:0
     */
    public static final String NAMESPACE = "urn:xmpp:attention:0";

    private Attention() {
    }

    private static Attention create() {
        return INSTANCE;
    }
}
