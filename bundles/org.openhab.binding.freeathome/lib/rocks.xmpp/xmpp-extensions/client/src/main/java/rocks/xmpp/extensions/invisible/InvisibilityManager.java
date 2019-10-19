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

package rocks.xmpp.extensions.invisible;


import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.extensions.caps.EntityCapabilitiesManager;
import rocks.xmpp.extensions.invisible.model.InvisibleCommand;
import rocks.xmpp.util.concurrent.AsyncResult;

/**
 * @author Christian Schudt
 */
public final class InvisibilityManager extends Manager {

    private boolean invisible;

    private InvisibilityManager(XmppSession xmppSession) {
        super(xmppSession);
    }

    /**
     * Becomes invisible.
     *
     * @return The async result.
     */
    public AsyncResult<Void> becomeInvisible() {
        return xmppSession.query(IQ.set(InvisibleCommand.INVISIBLE)).thenRun(() -> {
            synchronized (InvisibilityManager.this) {
                invisible = true;
            }
        });
    }

    /**
     * Becomes visible.
     *
     * @return The async result.
     */
    public AsyncResult<Void> becomeVisible() {
        return xmppSession.query(IQ.set(InvisibleCommand.VISIBLE)).thenRun(() -> {
            synchronized (InvisibilityManager.this) {
                invisible = false;
            }
        });
    }

    /**
     * Indicates, whether the current session is invisible.
     *
     * @return True, of the current session is invisible.
     */
    public synchronized boolean isInvisible() {
        return invisible;
    }

    /**
     * Checks, whether invisibility is supported by the server.
     *
     * @return The async result with true, if invisibility is supported.
     */
    public AsyncResult<Boolean> isSupported() {
        EntityCapabilitiesManager entityCapabilitiesManager = xmppSession.getManager(EntityCapabilitiesManager.class);
        return entityCapabilitiesManager.isSupported(InvisibleCommand.NAMESPACE, xmppSession.getDomain());
    }
}
