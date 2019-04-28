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

package rocks.xmpp.extensions.pubsub.model.errors;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The implementation of the {@code <nodeid-required/>} pubsub error.
 * This class is a singleton.
 *
 * @author Christian Schudt
 * @see #NODE_ID_REQUIRED
 * @see <a href="https://xmpp.org/extensions/xep-0060.html#publisher-delete-error-nodeid">7.2.3.3 NodeID Required</a>
 * @see <a href="https://xmpp.org/extensions/xep-0060.html#owner-configure-error-nodeid">8.2.3.3 NodeID Required</a>
 */
@XmlRootElement(name = "nodeid-required")
@XmlType(factoryMethod = "create")
public final class NodeIdRequired extends PubSubError {
    NodeIdRequired() {
    }

    private static NodeIdRequired create() {
        return NODE_ID_REQUIRED;
    }
}