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

package rocks.xmpp.sample.httpprebind;

import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.extensions.httpbind.BoshConnection;
import rocks.xmpp.extensions.httpbind.BoshConnectionConfiguration;

import java.util.concurrent.Executors;

/**
 * @author Christian Schudt
 */
public class HttpPreBindSample {

    public static void main(String[] args) {

        Executors.newFixedThreadPool(1).execute(() -> {
            BoshConnectionConfiguration boshConnectionConfiguration = BoshConnectionConfiguration.builder()
                    .hostname("localhost")
                    .port(7070)
                    .path("/http-bind/")
                    .build();

            try (XmppClient xmppClient = XmppClient.create("localhost", boshConnectionConfiguration)) {
                // Connect
                xmppClient.connect();
                // Login
                xmppClient.login("admin", "admin", "xmpp");

                BoshConnection boshConnection = (BoshConnection) xmppClient.getActiveConnection();

                // Gets the session id (sid) of the BOSH connection.
                String sessionId = boshConnection.getSessionId();

                // Detaches the BOSH session, without terminating it.
                long rid = boshConnection.detach();
                System.out.println("JID: " + xmppClient.getConnectedResource());
                System.out.println("SID: " + sessionId);
                System.out.println("RID: " + rid);
            } catch (XmppException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
