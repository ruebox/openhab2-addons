/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Christian Schudt
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

package rocks.xmpp.extensions.seclabel.model.ess;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.util.Objects;

/**
 * The implementation of the {@code <esssecuritylabel/>} element in the {@code urn:xmpp:sec-label:ess:0} namespace.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0258.html">XEP-0258: Security Labels in XMPP</a>
 * @see <a href="https://xmpp.org/extensions/xep-0258.html#schema-ess">XML Schema</a>
 * @see <a href="http://tools.ietf.org/html/rfc2634">RFC 2634</a>
 */
@XmlRootElement(name = "esssecuritylabel")
public final class EssSecurityLabel {

    @XmlValue
    private final byte[] value;

    private EssSecurityLabel() {
        this.value = null;
    }

    public EssSecurityLabel(byte[] value) {
        this.value = Objects.requireNonNull(value).clone();
    }

    /**
     * The BER/DER encoding of an ASN.1 ESSSecurityLabel type as defined in RFC 2634.
     *
     * @return The BER/DER encoding of an ASN.1 ESSSecurityLabel type as defined in RFC 2634.
     */
    public final byte[] getValue() {
        return value.clone();
    }
}
