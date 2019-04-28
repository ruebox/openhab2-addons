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

package rocks.xmpp.sample.register;

import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.net.ChannelEncryption;
import rocks.xmpp.core.net.client.SocketConnectionConfiguration;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.extensions.register.RegistrationManager;
import rocks.xmpp.extensions.register.model.Registration;

import java.util.concurrent.Executors;

public class RegistrationSample {

    public static void main(String[] args) {

        Executors.newFixedThreadPool(1).execute(() -> {
            try {

                SocketConnectionConfiguration socketConnectionConfiguration = SocketConnectionConfiguration.builder()
                        .hostname("localhost")
                        .port(5222)
                        .channelEncryption(ChannelEncryption.DISABLED) // Disable TLS only for simpler example here.
                        .build();

                XmppClient xmppSession = XmppClient.create("localhost", socketConnectionConfiguration);

                // Connect
                xmppSession.connect();

                RegistrationManager registrationManager = xmppSession.getManager(RegistrationManager.class);
                registrationManager.getRegistration().thenAccept(registration -> {
                    if (!registration.isRegistered()) {
                        // Usually you would probably show a visual registration form here.
                        // Then submit the registration as follows:
                        Registration registration1 = Registration.builder()
                                .username("user")
                                .password("pass")
                                .familyName("Family Name")
                                .givenName("Given Name")
                                .nickname("Nick Name")
                                .email("E-Mail")
                                .build();
                        registrationManager.register(registration1);
                    }
                    // Login
                    try {
                        xmppSession.login("user", "pass", "register");
                    } catch (XmppException e) {
                        throw new RuntimeException(e);
                    }
                });



            } catch (XmppException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
