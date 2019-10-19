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

package rocks.xmpp.extensions.pubsub.model;

/**
 * Defines who may subscribe to a node.
 *
 * @author Christian Schudt
 * @see NodeConfiguration#getAccessModel()
 * @see <a href="https://xmpp.org/extensions/xep-0060.html#accessmodels">4.5 Node Access Models</a>
 */
public enum AccessModel {

    /**
     * The node owner must approve all subscription requests, and only subscribers may retrieve items from the node.
     */
    AUTHORIZE,
    /**
     * Any entity may subscribe to the node (i.e., without the necessity for subscription approval) and any entity may retrieve items from the node (i.e., without being subscribed); this SHOULD be the default access model for generic pubsub services.
     */
    OPEN,
    /**
     * Any entity in the specified roster group(s) may subscribe to the node and retrieve items from the node; this access model applies mainly to instant messaging systems.
     */
    PRESENCE,
    /**
     * Any entity in the specified roster group(s) may subscribe to the node and retrieve items from the node; this access model applies mainly to instant messaging systems.
     */
    ROSTER,
    /**
     * An entity may subscribe or retrieve items only if on a whitelist managed by the node owner. The node owner MUST automatically be on the whitelist. In order to add entities to the whitelist, the node owner SHOULD use the protocol specified in the Manage Affiliated Entities section of this document, specifically by setting the affiliation to "member".
     */
    WHITELIST
}
