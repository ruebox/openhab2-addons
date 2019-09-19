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
 * Provides XML schema implementations of <a href="https://xmpp.org/extensions/xep-0059.html">XEP-0059: Result Set Management</a>.
 * <p>
 * It defines an XMPP protocol extension that enables an entity to page through and otherwise manage the receipt of large result sets. The protocol can be used in the context of any XMPP protocol that might send large result sets (such as service discovery, multi-user chat, and publish-subscribe). While the requesting entity in such an interaction can explicitly request the use of result set management, an indication that result set management is in use can also be proactively included by the responding entity when returning a limited result set in response to a query.
 * </p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSchema(namespace = ResultSetManagement.NAMESPACE, elementFormDefault = XmlNsForm.QUALIFIED) package rocks.xmpp.extensions.rsm.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;