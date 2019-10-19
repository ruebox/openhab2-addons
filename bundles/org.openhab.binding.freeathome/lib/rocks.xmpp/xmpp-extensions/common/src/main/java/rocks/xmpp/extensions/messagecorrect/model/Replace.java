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

package rocks.xmpp.extensions.messagecorrect.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

/**
 * The implementation of the {@code <replace/>} element in the {@code urn:xmpp:message-correct:0} namespace.
 * <h3>Advertising Support for Message Correction</h3>
 * ```java
 * xmppClient.enableFeature(Replace.NAMESPACE);
 * ```
 * <h3>Checking if a Message Should be Corrected</h3>
 * ```
 * Replace replace = message.getExtension(Replace.class);
 * if (replace != null) {
 *     String oldMessageId = replace.getId();
 *     // Replace old message with message...
 * }
 * ```
 * <h3>Correcting a Message</h3>
 * ```
 * Message correctedMessage = new Message(jid, Message.Type.CHAT, "This is the corrected text");
 * correctedMessage.addExtension(new Replace(id));
 * xmppClient.send(correctedMessage);
 * ```
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0308.html">XEP-0308: Last Message Correction</a>
 * @see <a href="https://xmpp.org/extensions/xep-0308.html#schema">XML Schema</a>
 */
@XmlRootElement
public final class Replace {

    /**
     * urn:xmpp:message-correct:0
     */
    public static final String NAMESPACE = "urn:xmpp:message-correct:0";

    @XmlAttribute
    private final String id;

    private Replace() {
        this.id = null;
    }

    /**
     * @param id The id of the message which shall be corrected.
     */
    public Replace(String id) {
        this.id = Objects.requireNonNull(id);
    }

    /**
     * Gets the id of the message which shall be corrected.
     *
     * @return The message id.
     */
    public final String getId() {
        return id;
    }

    @Override
    public final String toString() {
        return "Replacement for message with id: " + id;
    }
}
