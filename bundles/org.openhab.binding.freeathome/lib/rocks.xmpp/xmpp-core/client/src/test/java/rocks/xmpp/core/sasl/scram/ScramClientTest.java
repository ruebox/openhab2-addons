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

package rocks.xmpp.core.sasl.scram;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.session.XmppSessionConfiguration;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author Christian Schudt
 */
public class ScramClientTest {

    @Test
    public void testHiFunction() throws InvalidKeyException, NoSuchAlgorithmException, SaslException {
        ScramClient scramSaslClient = new ScramClient("SHA-1", "server", callbacks -> {
            for (Callback callback : callbacks) {
                if (callback instanceof NameCallback) {
                    ((NameCallback) callback).setName("Test");
                }
                if (callback instanceof PasswordCallback) {
                    ((PasswordCallback) callback).setPassword("pencil".toCharArray());
                }
            }
        });

        scramSaslClient.evaluateChallenge(new byte[0]);
        Assert.assertEquals(DatatypeConverter.printBase64Binary(scramSaslClient.hi("test".getBytes(StandardCharsets.UTF_8), "salt".getBytes(StandardCharsets.UTF_8), 4096)), "suIjHg0e14CDoom6wmHKz3naWOc=");
    }

    @Test
    public void testSasl() throws SaslException {
        List<String> saslMechs = XmppSessionConfiguration.getDefault().getAuthenticationMechanisms();
        String[] preferredMechanisms = saslMechs.toArray(new String[saslMechs.size()]);
        SaslClient sc = Sasl.createSaslClient(preferredMechanisms, "authorizationId", "xmpp", "localhost", null, callbacks -> {
            for (Callback callback : callbacks) {
                if (callback instanceof NameCallback) {
                    ((NameCallback) callback).setName("admin");
                }
                if (callback instanceof PasswordCallback) {
                    ((PasswordCallback) callback).setPassword("admin".toCharArray());
                }
                if (callback instanceof RealmCallback) {
                    ((RealmCallback) callback).setText("realm");
                }
            }
        });

        Assert.assertNotNull(sc);
    }

    @Test
    public void testServerResponse() throws SaslException {

        ScramClient scramSha1SaslClient = new ScramClient("SHA-1", null, callbacks -> {
            for (Callback callback : callbacks) {
                if (callback instanceof NameCallback) {
                    ((NameCallback) callback).setName("user");
                }
                if (callback instanceof PasswordCallback) {
                    ((PasswordCallback) callback).setPassword("pencil".toCharArray());
                }
            }
        });
        String serverResponse = "r=fyko+d2lbbFgONRv9qkxdawL3rfcNHYJY1ZVvWVs7j,s=QSXCR+Q6sek8bf92,i=4096";

        scramSha1SaslClient.evaluateChallenge(new byte[0]);
        byte[] result = scramSha1SaslClient.evaluateChallenge(serverResponse.getBytes(StandardCharsets.UTF_8));
        Assert.assertNotNull(result);
        Assert.assertTrue(new String(result, StandardCharsets.UTF_8).startsWith("c=biws,r=fyko+d2lbbFgONRv9qkxdawL3rfcNHYJY1ZVvWVs7j,p="));
    }

    @Test
    public void testReplacement() {
        String username = ",=,==";
        Assert.assertEquals("=2C=3D=2C=3D=3D", ScramClient.replaceUsername(username));
    }


    @Test
    public void testClientServer() throws SaslException {
        XmppClient.create("test");

        SaslClient saslClient = Sasl.createSaslClient(new String[]{"SCRAM-SHA-1"}, "authzid", "xmpp", "servername", null, callbacks -> {
            for (Callback callback : callbacks) {
                if (callback instanceof NameCallback) {
                    ((NameCallback) callback).setName("Test");
                }
                if (callback instanceof PasswordCallback) {
                    ((PasswordCallback) callback).setPassword("==".toCharArray());
                }
            }
        });

        byte[] initialResponse = saslClient.evaluateChallenge(new byte[0]);

        ScramServer scramServer = new ScramServer("SHA-1", callbacks -> {
            for (Callback callback : callbacks) {
                if (callback instanceof PasswordCallback) {
                    ((PasswordCallback) callback).setPassword("==".toCharArray());
                }
            }
        });

        byte[] challenge = scramServer.evaluateResponse(initialResponse);

        byte[] response = saslClient.evaluateChallenge(challenge);

        byte[] serverFinalMessage = scramServer.evaluateResponse(response);
        Assert.assertTrue(new String(serverFinalMessage, StandardCharsets.UTF_8).startsWith("v="));
        // Client should verify the server response successfully, otherwise exception would be thrown.
        saslClient.evaluateChallenge(serverFinalMessage);
    }
}
