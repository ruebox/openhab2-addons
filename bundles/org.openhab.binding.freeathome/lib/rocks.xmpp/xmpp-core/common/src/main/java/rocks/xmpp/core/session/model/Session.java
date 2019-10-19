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

package rocks.xmpp.core.session.model;

import rocks.xmpp.core.stream.model.StreamFeature;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The implementation of the {@code <session/>} element to establish a session.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/rfcs/rfc3921.html#session">3.  Session Establishment</a></cite></p>
 * <p>If a server supports sessions, it MUST include a {@code <session/>} element qualified by the 'urn:ietf:params:xml:ns:xmpp-session' namespace in the stream features it advertises to a client after the completion of stream authentication as defined in [XMPP-CORE].</p>
 * <p>Upon being so informed that session establishment is required (and after completing resource binding), the client MUST establish a session if it desires to engage in instant messaging and presence functionality; it completes this step by sending to the server an IQ stanza of type "set" containing an empty {@code <session/>} child element qualified by the 'urn:ietf:params:xml:ns:xmpp-session' namespace.</p>
 * </blockquote>
 * <p>
 * <b>Note:</b> <i>Session establishment has been removed from the <a href="https://xmpp.org/rfcs/rfc6120.html#diffs">updated specification</a>.</i>
 * </p>
 * This class is immutable.
 *
 * @author Christian Schudt
 */
@XmlRootElement
public final class Session extends StreamFeature {

    private final String optional;

    public Session() {
        this.optional = null;
    }

    public Session(Boolean optional) {
        this.optional = optional != null && optional ? "" : null;
    }

    @Override
    public final boolean isMandatory() {
        return optional == null;
    }

    @Override
    public final int getPriority() {
        return 4;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder("Session establishment");
        if (optional != null) {
            sb.append(" (optional)");
        }
        return sb.toString();
    }
}
