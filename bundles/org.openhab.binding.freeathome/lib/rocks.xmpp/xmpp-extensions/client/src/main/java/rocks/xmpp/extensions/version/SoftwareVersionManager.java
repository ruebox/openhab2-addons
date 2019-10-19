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

package rocks.xmpp.extensions.version;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.AbstractIQHandler;
import rocks.xmpp.core.stanza.IQHandler;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.extensions.version.model.SoftwareVersion;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.io.InputStream;
import java.util.Properties;

/**
 * This manager implements <a href="https://xmpp.org/extensions/xep-0092.html">XEP-0092: Software Version</a>.
 * <p>
 * If enabled and a software version has been set, it automatically responds to inbound queries for the software version.
 * </p>
 * It also allows to query for the software version of another entity.
 *
 * @author Christian Schudt
 */
public final class SoftwareVersionManager extends Manager {

    private static final SoftwareVersion DEFAULT_VERSION;

    static {
        Properties properties = new Properties();
        SoftwareVersion version;
        try (InputStream inputStream = SoftwareVersionManager.class.getResourceAsStream("/META-INF/maven/rocks.xmpp/xmpp-extensions-client/pom.properties")) {
            properties.load(inputStream);
            version = new SoftwareVersion("Babbler", properties.getProperty("version"));
        } catch (Exception e) {
            version = null;
        }
        DEFAULT_VERSION = version;
    }

    private final IQHandler iqHandler;

    private SoftwareVersion softwareVersion;

    private SoftwareVersionManager(final XmppSession xmppSession) {
        super(xmppSession);
        iqHandler = new AbstractIQHandler(IQ.Type.GET) {
            @Override
            protected IQ processRequest(IQ iq) {
                synchronized (SoftwareVersionManager.this) {
                    if (softwareVersion != null) {
                        return iq.createResult(softwareVersion);
                    }
                }
                if (DEFAULT_VERSION != null) {
                    return iq.createResult(DEFAULT_VERSION);
                }
                return iq.createError(Condition.SERVICE_UNAVAILABLE);
            }
        };
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        xmppSession.addIQHandler(SoftwareVersion.class, iqHandler);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        xmppSession.removeIQHandler(SoftwareVersion.class);
    }

    /**
     * Gets the software version of another entity.
     *
     * @param jid The JID of the entity you want get the software version from. You can also pass null, if you want to get the server's software version.
     * @return The async result with the software version or null, if this protocol is not supported.
     */
    public AsyncResult<SoftwareVersion> getSoftwareVersion(Jid jid) {
        return xmppSession.query(IQ.get(jid, new SoftwareVersion()), SoftwareVersion.class);
    }

    /**
     * Gets my own software version, which should be set first.
     *
     * @return My software version.
     * @see #setSoftwareVersion(SoftwareVersion)
     */
    public synchronized SoftwareVersion getSoftwareVersion() {
        return softwareVersion;
    }

    /**
     * Sets my own software version.
     *
     * @param softwareVersion My software version.
     * @see #getSoftwareVersion()
     */
    public synchronized void setSoftwareVersion(SoftwareVersion softwareVersion) {
        this.softwareVersion = softwareVersion;
    }
}
