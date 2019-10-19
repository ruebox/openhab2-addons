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

package rocks.xmpp.core.stream.model.errors;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

/**
 * The implementation of the {@code <see-other-host/>} stream error.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#streams-error-conditions-see-other-host">4.9.3.19.  see-other-host</a></cite></p>
 * <p>The server will not provide service to the initiating entity but is redirecting traffic to another host under the administrative control of the same service provider. The XML character data of the {@code <see-other-host/>} element returned by the server MUST specify the alternate FQDN or IP address at which to connect, which MUST be a valid domainpart or a domainpart plus port number (separated by the ':' character in the form "domainpart:port"). If the domainpart is the same as the source domain, derived domain, or resolved IPv4 or IPv6 address to which the initiating entity originally connected (differing only by the port number), then the initiating entity SHOULD simply attempt to reconnect at that address. (The format of an IPv6 address MUST follow [IPv6-ADDR], which includes the enclosing the IPv6 address in square brackets '[' and ']' as originally defined by [URI].) Otherwise, the initiating entity MUST resolve the FQDN specified in the {@code <see-other-host/>} element as described under Section 3.2.</p>
 * </blockquote>
 *
 * @see #seeOtherHost(String)
 */
@XmlRootElement(name = "see-other-host")
public final class SeeOtherHost extends Condition {

    private SeeOtherHost() {
    }

    SeeOtherHost(String otherHost) {
        super(Objects.requireNonNull(otherHost));
    }

    /**
     * Gets the other host.
     *
     * @return The other host.
     */
    public final String getOtherHost() {
        return value;
    }
}