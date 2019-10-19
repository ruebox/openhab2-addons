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

package rocks.xmpp.extensions.address.model;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.stanza.model.Message;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The implementation of the {@code <addresses/>} element in the {@code http://jabber.org/protocol/address} namespace..
 * <p>
 * Use this class to add extended address information to a stanza.
 * </p>
 * <h3>Usage</h3>
 * ```java
 * Address address = new Address(Address.Type.CC, Jid.of("juliet@example.net"));
 * Addresses addresses = new Addresses(Arrays.asList(address));
 * Message message = new Message(Jid.of("romeo@example.net"));
 * message.addExtension(addresses);
 * ```
 * <h3>Creating a Reply for a Message</h3>
 * To create a reply use {@link #createReply(Message, Message)}:
 * ```java
 * Message replyMessage = new Message();
 * boolean replyGenerated = Addresses.createReply(originalMessage, replyMessage);
 * ```
 * This will append an address extension to the reply message according to the business rules.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0033.html">XEP-0033: Extended Stanza Addressing</a>
 * @see <a href="https://xmpp.org/extensions/xep-0033.html#schema">XML Schema</a>
 * @see Address
 */
@XmlRootElement
public final class Addresses {

    private final List<Address> address = new ArrayList<>();

    @SuppressWarnings("unused")
    private Addresses() {
    }

    /**
     * Creates an address extension.
     *
     * @param addresses The addresses.
     */
    public Addresses(Collection<Address> addresses) {
        this.address.addAll(addresses);
    }

    /**
     * Creates an address extension.
     *
     * @param address The addresses.
     */
    public Addresses(Address... address) {
        this.address.addAll(Arrays.asList(address));
    }

    /**
     * Gets the addresses.
     *
     * @return The addresses.
     */
    public final List<Address> getAddresses() {
        return Collections.unmodifiableList(address);
    }

    /**
     * Creates a copy of this addresses extension, but without any BCC addresses.
     * This is useful for server processing (multicast usage).
     *
     * @return A new addresses extension.
     * @see <a href="https://xmpp.org/extensions/xep-0033.html#multicast">6. Multicast Usage</a>
     */
    public final Addresses deliveredAndWithoutBlindCarbonCopies() {
        return new Addresses(this.address.stream().map(Address::delivered).filter(address -> address.getType() != Address.Type.BCC).collect(Collectors.toList()));
    }

    /**
     * If a noreply address is specified, a reply SHOULD NOT be generated.
     *
     * @return True, if a reply should not be generated.
     * @see <a href="https://xmpp.org/extensions/xep-0033.html#replies">8. Reply Handling</a>
     */
    public final boolean shouldNotReply() {
        // If a noreply address is specified, a reply SHOULD NOT be generated.
        return address.stream().anyMatch(a -> a.getType() == Address.Type.NOREPLY);
    }

    /**
     * Creates a reply for a message.
     * If the original message contains address information, a new address extension is created based on the original one and added to the reply.
     * <p>
     * This method return false, if either no address information is found on the original message or it contains either {@link Address.Type#NOREPLY} or {@link Address.Type#REPLYROOM} addresses.
     *
     * @param original The original message.
     * @param reply    The reply message.
     * @return True, if a reply has been generated; false, if a reply should not be generated.
     * @see <a href="https://xmpp.org/extensions/xep-0033.html#replies">8. Reply Handling</a>
     */
    public static boolean createReply(Message original, Message reply) {
        Addresses addresses = original.getExtension(Addresses.class);
        if (addresses == null) {
            return false;
        }

        if (addresses.shouldNotReply()) {
            return false;
        }
        // an extended-address aware client MUST copy the address header from the original message into the reply, removing any delivered attributes
        List<Address> addressList = new ArrayList<>();
        Jid sender = original.getFrom() != null ? original.getFrom().asBareJid() : null;
        Jid receiver = original.getTo() != null ? original.getTo().asBareJid() : null;
        boolean containsOriginalSender = false;
        for (Address addr : addresses.address) {
            if (addr.getJid() != null) {
                // If a replyto address is specified, the reply SHOULD go to the specified address.
                // No further extended address processing is required.
                // Any <thread/> element from the initial message MUST be copied into the reply.
                if (addr.getType() == Address.Type.REPLYTO) {
                    reply.setTo(addr.getJid());
                    if (original.getThread() != null) {
                        reply.setThread(original.getThread());
                    }
                    return true;
                }
                if (addr.getType() == Address.Type.REPLYROOM) {
                    return false;
                }
                // The recipient's address SHOULD be removed from the list.
                Jid bareJid = addr.getJid().asBareJid();
                if (!bareJid.equals(receiver)) {
                    addressList.add(addr.undelivered());
                }
                if (bareJid.equals(sender)) {
                    containsOriginalSender = true;
                }
            }
        }
        //  If the original sender is not in the copied list, the original sender MUST be added as a 'to' address.
        if (!containsOriginalSender) {
            addressList.add(0, new Address(Address.Type.TO, sender));
        }
        reply.putExtension(new Addresses(addressList));
        return true;
    }
}
