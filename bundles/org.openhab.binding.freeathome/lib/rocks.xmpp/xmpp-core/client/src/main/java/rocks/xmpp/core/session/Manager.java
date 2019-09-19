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

package rocks.xmpp.core.session;

/**
 * A generic manager class, which allows to enable or disable certain functionality.
 *
 * @author Christian Schudt
 */
public abstract class Manager {
    protected final XmppSession xmppSession;

    protected Manager(XmppSession xmppSession) {
        this(xmppSession, false);
    }

    protected Manager(XmppSession xmppSession, boolean disposable) {
        this.xmppSession = xmppSession;
        if (disposable) {
            xmppSession.addSessionStatusListener(e -> {
                if (e.getStatus() == XmppSession.Status.CLOSING) {
                    dispose();
                }
            });
        }
    }

    private volatile boolean enabled;

    /**
     * Indicates, whether this manager is enabled or not.
     *
     * @return True, if this manager is enabled.
     * @see #setEnabled(boolean)
     */
    public final boolean isEnabled() {
        return enabled;
    }

    /**
     * Enables or disables this manager.
     *
     * @param enabled If the manager gets enabled.
     * @see #isEnabled()
     */
    public final void setEnabled(boolean enabled) {
        boolean wasEnabled = this.enabled;
        this.enabled = enabled;
        if (enabled && !wasEnabled) {
            onEnable();
        } else if (!enabled && wasEnabled) {
            onDisable();
        }
    }

    /**
     * Called when the manager is enabled.
     */
    protected void onEnable() {
        xmppSession.enableFeature(getClass());
    }

    /**
     * Called when the manager is disabled.
     */
    protected void onDisable() {
        xmppSession.disableFeature(getClass());
    }

    /**
     * Initializes the manager. Logic which shouldn't be in the constructor can go here.
     * This allows thread-safe construction of objects, e.g. when you need to publish the "this" reference.
     *
     * @see <a href="http://www.ibm.com/developerworks/library/j-jtp0618/">Java theory and practice: Safe construction techniques</a>
     */
    protected void initialize() {
    }

    /**
     * Called when the session gets closed.
     */
    protected void dispose() {
    }
}
