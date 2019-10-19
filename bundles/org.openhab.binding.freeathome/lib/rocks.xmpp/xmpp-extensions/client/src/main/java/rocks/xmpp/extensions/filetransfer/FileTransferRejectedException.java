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

package rocks.xmpp.extensions.filetransfer;

import rocks.xmpp.core.XmppException;

/**
 * This is generic exception when a file transfer request has been rejected.
 * The reason for it is, that different file transfer protocols use different ways to indicate file transfer rejection:
 * <ul>
 * <li><a href="https://xmpp.org/extensions/xep-0066.html">XEP-0066: Out of Band Data</a> uses the {@code <not-acceptable/>} stanza error</li>
 * <li><a href="https://xmpp.org/extensions/xep-0095.html">XEP-0095: Stream Initiation</a> / <a href="https://xmpp.org/extensions/xep-0096.html">XEP-0096: SI File Transfer</a> uses the {@code <forbidden/>} stanza error</li>
 * <li><a href="https://xmpp.org/extensions/xep-0166.html">XEP-0166: Jingle</a> / <a href="https://xmpp.org/extensions/xep-0234.html">XEP-0234: Jingle File Transfer</a> uses {@code action='session-terminate'} and  {@code <decline/>} to reject an offer.</li>
 * </ul>
 *
 * @author Christian Schudt
 */
public final class FileTransferRejectedException extends XmppException {
}
