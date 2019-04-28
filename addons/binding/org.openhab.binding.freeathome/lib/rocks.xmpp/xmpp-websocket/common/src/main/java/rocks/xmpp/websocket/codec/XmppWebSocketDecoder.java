/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 Christian Schudt
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

package rocks.xmpp.websocket.codec;

import rocks.xmpp.core.stream.model.StreamElement;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Decodes WebSocket text messages to XMPP {@link StreamElement}s.
 * <p>
 * The required {@link Unmarshaller} for decoding must be supplied via {@link EndpointConfig#getUserProperties()}, see {@link UserProperties#UNMARSHALLER}.
 * <p>
 * Optionally you can also provide a callback, which is called after unmarshalling with the XML string (the text message) and the unmarshalled element.
 * This is useful for debugging purposes, see {@link UserProperties#ON_READ}.
 *
 * @author Christian Schudt
 * @see XmppWebSocketEncoder
 * @see UserProperties
 */
public final class XmppWebSocketDecoder implements Decoder.Text<StreamElement> {

    private Supplier<Unmarshaller> unmarshaller;

    private BiConsumer<String, StreamElement> onRead;

    @Override
    public final StreamElement decode(final String s) throws DecodeException {
        try (StringReader reader = new StringReader(s)) {
            StreamElement streamElement = (StreamElement) unmarshaller.get().unmarshal(reader);
            if (onRead != null) {
                onRead.accept(s, streamElement);
            }
            return streamElement;
        } catch (JAXBException e) {
            throw new DecodeException(s, e.getMessage(), e);
        }
    }

    @Override
    public final boolean willDecode(final String s) {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void init(final EndpointConfig config) {
        this.unmarshaller = (Supplier<Unmarshaller>) config.getUserProperties().get(UserProperties.UNMARSHALLER);
        this.onRead = (BiConsumer<String, StreamElement>) config.getUserProperties().get(UserProperties.ON_READ);
    }

    @Override
    public final void destroy() {
        this.unmarshaller = null;
        this.onRead = null;
    }

    /**
     * User properties for usage in {@link EndpointConfig#getUserProperties()}.
     */
    public static final class UserProperties {
        /**
         * The property key to set the unmarshaller. The value must be a {@code java.util.function.Supplier<Unmarshaller>}.
         */
        public static final String UNMARSHALLER = "unmarshaller";

        /**
         * The property to set the read callback. The value must be a {@code java.util.function.BiConsumer<String, StreamElement>}.
         */
        public static final String ON_READ = "onRead";

        private UserProperties() {
        }
    }
}
