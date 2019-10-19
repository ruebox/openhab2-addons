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

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Represents the affiliations which allow to manage permissions.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0060.html#affiliations">4.1 Affiliations</a>
 */
public enum AffiliationState {
    /**
     * The manager of a node, of which there may be more than one; often but not necessarily the node creator.
     */
    @XmlEnumValue("owner")
    OWNER,
    /**
     * An entity that is allowed to publish items to a node and that is automatically subscribed to the node.
     */
    @XmlEnumValue("publisher")
    PUBLISHER,
    /**
     * An entity that is allowed to publish items to a node but that is not allowed to receive notifications. (This affiliation is useful in the context of nodes that do not have an open access model when automated entities need to generate notifications on behalf of the owner.)
     */
    @XmlEnumValue("publish-only")
    PUBLISH_ONLY,
    /**
     * A member, which is allowed to subscribe to and retrieve items from a node.
     */
    @XmlEnumValue("member")
    MEMBER,
    /**
     * No affiliation.
     */
    @XmlEnumValue("none")
    NONE,
    /**
     * An entity that is disallowed from subscribing or publishing to a node.
     */
    @XmlEnumValue("outcast")
    OUTCAST

}