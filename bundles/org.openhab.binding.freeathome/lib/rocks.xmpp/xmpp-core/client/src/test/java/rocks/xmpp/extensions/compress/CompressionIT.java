/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Christian Schudt
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

package rocks.xmpp.extensions.compress;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.IntegrationTest;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.net.ChannelEncryption;
import rocks.xmpp.core.net.client.SocketConnectionConfiguration;
import rocks.xmpp.core.session.XmppClient;

/**
 * @author Christian Schudt
 */
public class CompressionIT extends IntegrationTest {

    @Test
    public void testConnectingWithCompression() throws XmppException {
        SocketConnectionConfiguration tcpConfiguration = SocketConnectionConfiguration.builder()
                .hostname(HOSTNAME)
                .compressionMethods(CompressionMethod.ZLIB)
                .channelEncryption(ChannelEncryption.DISABLED)
                .build();

        try (XmppClient xmppSession = XmppClient.create(DOMAIN, tcpConfiguration)) {
            xmppSession.connect();
            xmppSession.loginAnonymously();
            CompressionManager compressionManager = xmppSession.getManager(CompressionManager.class);
            Assert.assertEquals(compressionManager.getNegotiatedCompressionMethod(), CompressionMethod.ZLIB);
        }
    }
}
