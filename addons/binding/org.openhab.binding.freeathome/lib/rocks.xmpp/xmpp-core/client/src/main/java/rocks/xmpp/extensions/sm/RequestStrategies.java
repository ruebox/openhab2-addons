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

package rocks.xmpp.extensions.sm;

import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.Stanza;

import java.util.function.Predicate;

/**
 * A set of request strategies which are used for requesting acknowledgements from the server.
 * <p>
 * Requests could be sent after every message or after X stanzas.
 *
 * @author Christian Schudt
 * @see StreamManager#setRequestStrategy(Predicate)
 */
public final class RequestStrategies {
    private static final Predicate<Stanza> FOR_EACH_MESSAGE = stanza -> stanza instanceof Message;

    private RequestStrategies() {
    }

    /**
     * Requests an ack for every X stanzas. In other words, if X stanzas have been sent and acknowledgement is requested for them.
     *
     * @param x The stanza count.
     * @return The request strategy.
     */
    public static Predicate<Stanza> forEveryXStanzas(final int x) {
        return new ForEveryXStanzas(x);
    }

    private static final class ForEveryXStanzas implements Predicate<Stanza> {

        private final int x;

        private int sentStanzas;

        private ForEveryXStanzas(final int x) {
            this.x = x;
        }

        @Override
        public final boolean test(final Stanza stanza) {
            if (++sentStanzas == x) {
                sentStanzas = 0;
                return true;
            }
            return false;
        }
    }

    /**
     * Requests an ack for each message.
     *
     * @return The request strategy.
     */
    public static Predicate<Stanza> forEachMessage() {
        return FOR_EACH_MESSAGE;
    }

    /**
     * Requests an acknowledgement for each message and at the latest after X non-message stanzas (presence or IQ).
     * E.g. if x = 5 and five presences are sent, a request is sent afterwards.
     * If a message is sent in between, no request is sent after the fifth presence, because the counter will be reset to 0 after each message.
     *
     * @param x The stanza count.
     * @return The request strategy.
     */
    public static Predicate<Stanza> forEachMessageOrEveryXStanzas(final int x) {
        return new Predicate<Stanza>() {

            private final ForEveryXStanzas forEveryStanza = new ForEveryXStanzas(x);

            @Override
            public final boolean test(final Stanza stanza) {
                if (FOR_EACH_MESSAGE.test(stanza)) {
                    forEveryStanza.sentStanzas = 0;
                    return true;
                }
                return forEveryStanza.test(stanza);
            }
        };
    }
}
