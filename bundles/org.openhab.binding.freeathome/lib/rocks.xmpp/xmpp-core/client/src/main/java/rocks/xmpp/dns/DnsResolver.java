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

package rocks.xmpp.dns;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A minimalistic DNS resolver, which can resolve SRV and TXT records in the context of XMPP.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/rfcs/rfc6120.html#tcp-resolution-prefer">RFC 6120 3.2.1.  Preferred Process: SRV Lookup</a>
 * @see <a href="https://xmpp.org/extensions/xep-0156.html">XEP-0156: Discovering Alternative XMPP Connection Methods</a>
 */
public final class DnsResolver {

    private DnsResolver() {
    }

    private static final Map<Question, Message> CACHE = new ConcurrentHashMap<>();

    /**
     * Resolves DNS SRV records for the given domain.
     * <p>
     * The service is "xmpp-client", the protocol is "tcp", resulting in a query of {@code _xmpp-client._tcp.domain}.
     *
     * @param service    The service, usually "xmpp-client" or "xmpps-client".
     * @param domain     The domain.
     * @param nameServer The name server.
     * @param timeout    The timeout.
     * @return The DNS SRV records.
     * @throws IOException If a timeout occurs or no connection to the DNS server can be established.
     * @see <a href="https://xmpp.org/rfcs/rfc6120.html#tcp-resolution-prefer">RFC 6120 3.2.1.  Preferred Process: SRV Lookup</a>
     */
    public static List<SrvRecord> resolveSRV(CharSequence service, CharSequence domain, String nameServer, long timeout) throws IOException {
        return resolve("_" + service + "._tcp.", domain, ResourceRecord.Type.SRV, nameServer, timeout, resourceRecord -> (SrvRecord) resourceRecord.data);
    }

    @Deprecated
    public static List<SrvRecord> resolveSRV(CharSequence service, CharSequence domain, long timeout) throws IOException {
        return resolveSRV(service, domain, null, timeout);
    }

    /**
     * Resolves DNS TXT records for the given domain.
     *
     * @param domain     The domain.
     * @param nameServer The name server.
     * @param timeout    The timeout.
     * @return The DNS SRV records.
     * @throws IOException If a timeout occurs or no connection to the DNS server can be established.
     * @see <a href="https://xmpp.org/extensions/xep-0156.html">XEP-0156: Discovering Alternative XMPP Connection Methods</a>
     */
    public static List<TxtRecord> resolveTXT(CharSequence domain, String nameServer, long timeout) throws IOException {
        return resolve("_xmppconnect.", domain, ResourceRecord.Type.TXT, nameServer, timeout, resourceRecord -> (TxtRecord) resourceRecord.data);
    }

    @Deprecated
    public static List<TxtRecord> resolveTXT(CharSequence domain, long timeout) throws IOException {
        return resolveTXT(domain, null, timeout);
    }

    private static <T> List<T> resolve(String prefix, CharSequence domain, ResourceRecord.Type type, String nameServer, long timeout, Function<ResourceRecord, T> mapper) throws IOException {
        try {
            // Ensure a timeout > 0 in order to not block infinitely.
            long t = timeout <= 0 ? 1000 : timeout;
            Question question = new Question(prefix + domain, type, ResourceRecord.Classification.IN);
            return CACHE.computeIfAbsent(question, key -> {
                final Message message = new Message(question);
                try (DatagramChannel channel = DatagramChannel.open();
                     Selector selector = Selector.open()) {
                    channel.configureBlocking(false);
                    // 8.8.8.8 = Google DNS service
                    channel.connect(new InetSocketAddress(nameServer == null ? "8.8.8.8" : nameServer, 53));
                    channel.register(selector, SelectionKey.OP_READ);
                    channel.write(ByteBuffer.wrap(message.toByteArray()));

                    if (selector.select(t) > 0) {
                        ByteBuffer response = ByteBuffer.allocate(512);
                        channel.receive(response);
                        response.flip();
                        final Message responseMessage = new Message(response);
                        if (message.id != responseMessage.id) {
                            throw new IOException("DNS message IDs did not match.");
                        }
                        return responseMessage;
                    } else {
                        throw new SocketTimeoutException();
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }).getAnswers().stream().map(mapper).collect(Collectors.toList());
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }
}
