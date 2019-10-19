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

package rocks.xmpp.extensions.muc.model.owner;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.muc.model.Destroy;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The implementation of the {@code <query/>} element in the {@code http://jabber.org/protocol/muc#owner} namespace.
 * <h3>Usage</h3>
 * ```java
 * // To create an empty element (to query the configuration form).
 * MucOwner mucOwner = MucOwner.empty();
 * ```
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0045.html">XEP-0045: Multi-User Chat</a>
 * @see <a href="https://xmpp.org/extensions/xep-0045.html#schemas-owner">XML Schema</a>
 */
@XmlRootElement(name = "query")
public final class MucOwner {

    @XmlElementRef
    private final DataForm dataForm;

    private final MucOwnerDestroy destroy;

    /**
     * Creates an empty query element.
     */
    private MucOwner() {
        this(null, null);
    }

    /**
     * Creates a query element with a {@code <destroy/>} element.
     *
     * @param dataForm The data form element.
     * @param destroy  The destroy element.
     */
    private MucOwner(DataForm dataForm, MucOwnerDestroy destroy) {
        this.destroy = destroy;
        this.dataForm = dataForm;
    }

    /**
     * Creates an empty query element.
     *
     * @return The {@link MucOwner} instance.
     */
    public static MucOwner empty() {
        return new MucOwner();
    }

    /**
     * Creates a {@code <query/>} element with a {@code <destroy/>} child element.
     * <p><b>Sample:</b></p>
     * ```xml
     * <query xmlns='http://jabber.org/protocol/muc#owner'>
     *     <destroy jid='coven@chat.shakespeare.lit'>
     *         <reason>Macbeth doth come.</reason>
     *     </destroy>
     * </query>
     * ```
     *
     * @param jid    The JID.
     * @param reason The reason.
     * @return The {@link MucOwner} instance.
     */
    public static MucOwner withDestroy(Jid jid, String reason) {
        return new MucOwner(null, new MucOwnerDestroy(jid, reason));
    }

    /**
     * Creates a {@code <query/>} element with a {@code <x/>} (data form) child element.
     *
     * @param dataForm The configuration form.
     * @return The {@link MucOwner} instance.
     */
    public static MucOwner withConfiguration(DataForm dataForm) {
        return new MucOwner(dataForm, null);
    }

    /**
     * Gets the configuration form.
     *
     * @return The configuration form.
     */
    public DataForm getConfigurationForm() {
        return dataForm;
    }

    /**
     * Gets the destroy element.
     *
     * @return The destroy element.
     */
    public Destroy getDestroy() {
        return destroy;
    }

    private static final class MucOwnerDestroy implements Destroy {

        private final String reason;

        @XmlAttribute
        private final Jid jid;

        private MucOwnerDestroy() {
            this(null, null);
        }

        private MucOwnerDestroy(Jid jid, String reason) {
            this.jid = jid;
            this.reason = reason;
        }

        @Override
        public Jid getJid() {
            return jid;
        }

        @Override
        public String getReason() {
            return reason;
        }
    }
}
