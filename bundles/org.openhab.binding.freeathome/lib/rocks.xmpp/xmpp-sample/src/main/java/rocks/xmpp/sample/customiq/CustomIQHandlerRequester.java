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

package rocks.xmpp.sample.customiq;

import rocks.xmpp.core.net.ChannelEncryption;
import rocks.xmpp.core.session.Extension;
import rocks.xmpp.core.net.client.SocketConnectionConfiguration;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.session.XmppSessionConfiguration;
import rocks.xmpp.core.session.debug.ConsoleDebugger;
import rocks.xmpp.core.stanza.model.IQ;

import java.util.concurrent.Executors;

/**
 * @author Christian Schudt
 */
public class CustomIQHandlerRequester {

    public static void main(String[] args) {

        Executors.newFixedThreadPool(1).execute(() -> {
            try {

                SocketConnectionConfiguration tcpConfiguration = SocketConnectionConfiguration.builder()
                        .hostname("localhost")
                        .port(5222)
                        .channelEncryption(ChannelEncryption.DISABLED)
                        .build();

                XmppSessionConfiguration configuration = XmppSessionConfiguration.builder()
                        .debugger(ConsoleDebugger.class)
                                // This registers the custom IQ payload to the JAXB context.
                        .extensions(Extension.of(Addition.class))
                        .build();

                XmppClient xmppSession = XmppClient.create("localhost", configuration, tcpConfiguration);

                // Connect
                xmppSession.connect();
                // Login
                xmppSession.login("222", "222", "iq");

                Addition addition = new Addition(52, 22);
                System.out.println("Requesting: " + addition);
                // Request the sum of two values (52 + 22). 111 will calculate it for you and return a result.
                IQ resultIQ = xmppSession.query(IQ.get(xmppSession.getDomain().withLocal("111").withResource("iq"), addition)).get();

                // Print the result.
                System.out.println(resultIQ.getExtension(Addition.class));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
