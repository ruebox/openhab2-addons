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

package rocks.xmpp.core;

import rocks.xmpp.core.net.ChannelEncryption;
import rocks.xmpp.core.net.client.SocketConnectionConfiguration;
import rocks.xmpp.extensions.httpbind.BoshConnectionConfiguration;

/**
 * @author Christian Schudt
 */
public abstract class IntegrationTest {
    public static final String DOMAIN = "localhost";

    public static final String HOSTNAME = "localhost";

    public static final String USER_1 = "111";

    public static final String PASSWORD_1 = "111";

    public static final String USER_2 = "222";

    public static final String PASSWORD_2 = "222";

    static {
        BoshConnectionConfiguration boshConnectionConfiguration = BoshConnectionConfiguration.builder()
                .hostname(HOSTNAME)
                .port(7070)
                .build();
        BoshConnectionConfiguration.setDefault(boshConnectionConfiguration);

        SocketConnectionConfiguration socketConnectionConfiguration = SocketConnectionConfiguration.builder()
                .hostname(HOSTNAME)
                .port(5222)
                .channelEncryption(ChannelEncryption.DISABLED)
                .build();
    }


}
