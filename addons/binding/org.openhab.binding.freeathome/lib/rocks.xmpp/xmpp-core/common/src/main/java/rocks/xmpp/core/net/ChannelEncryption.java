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

package rocks.xmpp.core.net;

/**
 * Represents different channel encryption modes.
 * <p>
 * Channel encryption uses the SSL/TLS protocol and can either be negotiated during stream negotiation with the STARTTLS command or used directly.
 *
 * @author Christian Schudt
 */
public enum ChannelEncryption {

    /**
     * Channel encryption via TLS is required. If TLS is not negotiated during stream negotiation, the connection fails.
     * This mode refers to the STARTTLS command used during stream negotiation.
     * <p>
     * Receiving entities (servers) advertise support for STARTTLS and mark it as mandatory-to-negotiate by including an empty {@code <required/>} element in the {@code <starttls/>} element.
     * Either entity closes the connection, if TLS has not been successfully negotiated.
     * <p>
     * Also known as explicit TLS mode.
     *
     * @see <a href="https://xmpp.org/rfcs/rfc6120.html#tls">STARTTLS Negotiation</a>
     */
    REQUIRED,
    /**
     * Channel encryption via TLS is optional.
     * <p>
     * Communication starts in plain text and is then optionally upgraded to a secured connection, via the STARTTLS command during stream negotiation.
     * Initiating entities (clients) try to negotiate TLS, if the receiving entity supports it, but do not enforce it.
     * Receiving entities (servers) advertise support for STARTTLS, but do not mark it as mandatory-to-negotiate.
     * <p>
     * This mode should be used with caution, since it's weak to a man-in-the-middle attack: an attacker could strip the STARTTLS command and the connection will be left unencrypted even if the server advertises support for TLS.
     *
     * @see <a href="https://xmpp.org/rfcs/rfc6120.html#tls">STARTTLS Negotiation</a>
     */
    OPTIONAL,
    /**
     * Channel encryption via TLS is disabled, i.e. the communication channel is unencrypted (using plain text).
     * <p>
     * Initiating entities (clients) neither negotiate TLS via the STARTTLS command nor connect via a non-XMPP secure transport layer.
     * Receiving entities (servers) do not advertise support for STARTTLS in their stream features.
     *
     * @see <a href="https://xmpp.org/rfcs/rfc6120.html#tls">STARTTLS Negotiation</a>
     */
    DISABLED,
    /**
     * TLS is attempted immediately on connect to a TCP socket, like how HTTPS works, not like how STARTTLS works with any protocol.
     * The channel is always encrypted, communication never takes place in plain text.
     * <p>
     * Also known as implicit TLS mode.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0368.html">XEP-0368: SRV records for XMPP over TLS</a>
     */
    DIRECT
}
