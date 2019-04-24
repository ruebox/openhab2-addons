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

/**
 * Provides XML schema implementations of <a href="https://xmpp.org/extensions/xep-0124.html">XEP-0124: Bidirectional-streams Over Synchronous HTTP (BOSH)</a> and <a href="https://xmpp.org/extensions/xep-0206.html">XEP-0206: XMPP Over BOSH</a>.
 * <p>
 * XEP-0124 defines a transport protocol that emulates the semantics of a long-lived, bidirectional TCP connection between two entities (such as a client and a server) by efficiently using multiple synchronous HTTP request/response pairs without requiring the use of frequent polling or chunked responses.
 * </p>
 * <p>
 * XEP-0206 defines how the Bidirectional-streams Over Synchronous HTTP (BOSH) technology can be used to transport XMPP stanzas. The result is an HTTP binding for XMPP communications that is useful in situations where a device or client is unable to maintain a long-lived TCP connection to an XMPP server.
 * </p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
// Some server BOSH implementations require the prefix to be "xmpp" for namespace "urn:xmpp:xbosh"
@XmlSchema(namespace = Body.NAMESPACE, elementFormDefault = XmlNsForm.QUALIFIED, xmlns = {@XmlNs(prefix = "xmpp", namespaceURI = Body.XBOSH_NAMESPACE)})
@XmlJavaTypeAdapter(value = LocaleAdapter.class, type = Locale.class)
package rocks.xmpp.extensions.httpbind.model;

import rocks.xmpp.util.adapters.LocaleAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Locale;
