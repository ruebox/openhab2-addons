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

package rocks.xmpp.extensions.idle;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.BaseTest;
import rocks.xmpp.core.MockServer;
import rocks.xmpp.core.session.TestXmppSession;
import rocks.xmpp.core.stanza.PresenceEvent;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.extensions.idle.model.Idle;

import java.util.function.Consumer;

/**
 * @author Christian Schudt
 */
public class IdleManagerTest extends BaseTest {

    @Test
    public void testIdleInAwayPresence() {
        final TestXmppSession xmppSession1 = new TestXmppSession(ROMEO, new MockServer());
        xmppSession1.addOutboundPresenceListener(new Consumer<PresenceEvent>() {
            @Override
            public void accept(PresenceEvent e) {
                xmppSession1.removeInboundPresenceListener(this);
                Assert.assertTrue(e.getPresence().hasExtension(Idle.class));
            }
        });
        xmppSession1.send(new Message(JULIET));
        xmppSession1.send(new Presence(Presence.Show.AWAY));
    }

    @Test
    public void testIdleInXAPresence() {
        TestXmppSession xmppSession1 = new TestXmppSession(ROMEO, new MockServer());
        xmppSession1.addOutboundPresenceListener(e -> Assert.assertTrue(e.getPresence().hasExtension(Idle.class)));
        xmppSession1.send(new Message(JULIET));
        xmppSession1.send(new Presence(Presence.Show.XA));
    }
}
