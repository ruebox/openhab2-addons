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
import rocks.xmpp.core.stream.model.StreamHeader;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.websocket.model.Open;
import rocks.xmpp.core.stanza.model.Presence;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

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
 * 
 * Modified by @kjoglum to adapt to Busch-Jaeger Free@Home SysAp websocket protocol
 */
public final class XmppWebSocketDecoder implements Decoder.Text<StreamElement> {
    
    private static final Logger logger = Logger.getLogger(XmppWebSocketDecoder.class.getName());

    private Supplier<Unmarshaller> unmarshaller;

    private BiConsumer<String, StreamElement> onRead;
    
    @Override
    public final StreamElement decode(final String s) throws DecodeException {
        //Below code valid for 2.2.4 < SysAp version < 2.3.0 
        if (s.contains("<stream:stream")) {
            String ID = s.substring(s.indexOf("id")+4, s.indexOf("id")+40);
            String Domain = "busch-jaeger.de";
            Jid From = Jid.of(Domain);
            Open streamHead = new Open(null, From, ID, null, "1.0");
            //logger.warning("Converting wrong server websocket stream: " + streamHead.toString());
            return streamHead;
        }
        else if (s.contains("<presence") && !s.contains("xmlns=\"jabber:client\"")){
            String addString = "xmlns=\"jabber:client\" ";
            String newString = s.substring(0,10) + addString.substring(0, addString.length()) + s.substring(10, s.length());
            try (StringReader reader = new StringReader(newString)) {
                // logger.warning("Decoding presence server stream " + newString);
                StreamElement streamElement = (StreamElement) unmarshaller.get().unmarshal(reader);
                if (onRead != null) {
                    onRead.accept(newString, streamElement);
                }
            return streamElement;
        } catch (JAXBException e) {
            throw new DecodeException(newString, e.getMessage(), e);
        }            
        }
        else if (s.contains("<iq") && !s.contains("xmlns=\"jabber:client\"")){
            String addString = "xmlns=\"jabber:client\" ";
            String newString = s.substring(0,4) + addString.substring(0, addString.length()) + s.substring(4, s.length());
            try (StringReader reader = new StringReader(newString)) {
                // logger.warning("Decoding IQ server stream " + newString);
                StreamElement streamElement = (StreamElement) unmarshaller.get().unmarshal(reader);
                if (onRead != null) {
                    onRead.accept(newString, streamElement);
                }
            return streamElement;
        } catch (JAXBException e) {
            throw new DecodeException(newString, e.getMessage(), e);
        }            
        }
        else {
            try (StringReader reader = new StringReader(s)) {
                // logger.warning("Decoding regular server stream " + s);
                if (s.contains("presence") && s.contains("subscribed")) {
                    logger.warning("Successful Websocket Connection");
                }
                StreamElement streamElement = (StreamElement) unmarshaller.get().unmarshal(reader);
                if (onRead != null) {
                    onRead.accept(s, streamElement);
                }
            return streamElement;
        } catch (JAXBException e) {
            throw new DecodeException(s, e.getMessage(), e);
        }
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
