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

package rocks.xmpp.core.sasl;

import rocks.xmpp.core.sasl.anonymous.AnonymousSaslClient;
import rocks.xmpp.core.sasl.scram.ScramClient;

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import java.util.Map;

/**
 * A factory which creates {@linkplain SaslClient}s, which are used in XMPP context and are not natively provided by the default security provider.
 * <p>
 * For example, the <a href="http://tools.ietf.org/html/rfc2245">ANONYMOUS</a> and <a href="http://tools.ietf.org/html/rfc5802">SCRAM-SHA-1</a> mechanisms are not provided by default.
 * </p>
 *
 * @author Christian Schudt
 */
public final class XmppSaslClientFactory implements SaslClientFactory {
    @Override
    public final SaslClient createSaslClient(String[] mechanisms, String authorizationId, String protocol, String serverName, Map<String, ?> props, CallbackHandler cbh) {

        for (String mechanism : mechanisms) {
            if ("ANONYMOUS".equals(mechanism)) {
                return new AnonymousSaslClient();
            }
            if ("SCRAM-SHA-1".equals(mechanism)) {
                return new ScramClient("SHA-1", authorizationId, cbh);
            }
        }
        return null;
    }

    @Override
    public final String[] getMechanismNames(Map<String, ?> props) {
        return new String[]{"ANONYMOUS", "SCRAM-SHA-1"};
    }
}
