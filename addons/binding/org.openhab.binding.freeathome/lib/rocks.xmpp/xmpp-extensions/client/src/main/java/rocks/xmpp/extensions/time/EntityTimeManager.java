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

package rocks.xmpp.extensions.time;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.IQHandler;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.extensions.time.handler.EntityTimeHandler;
import rocks.xmpp.extensions.time.model.EntityTime;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.time.OffsetDateTime;

/**
 * This manager implements <a href="https://xmpp.org/extensions/xep-0202.html">XEP-0202: Entity Time</a>.
 * <p>
 * It automatically responds to entity time requests, with the system's current date and timezone information and allows to retrieve another entity's time.
 * </p>
 *
 * @author Christian Schudt
 */
public final class EntityTimeManager extends Manager {

    private final IQHandler iqHandler;

    private EntityTimeManager(final XmppSession xmppSession) {
        super(xmppSession);
        iqHandler = new EntityTimeHandler();
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        xmppSession.addIQHandler(EntityTime.class, iqHandler);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        xmppSession.removeIQHandler(EntityTime.class);
    }

    /**
     * Gets the time information (e.g. time zone) of another XMPP entity.
     *
     * @param jid The entity's JID.
     * @return The async result with the entity time or null if this protocol is not supported by the entity.
     */
    public AsyncResult<OffsetDateTime> getEntityTime(Jid jid) {
        AsyncResult<IQ> query = xmppSession.query(IQ.get(jid, new EntityTime()));
        return query.thenApply(result -> {
            EntityTime entityTime = result.getExtension(EntityTime.class);
            return entityTime != null ? entityTime.getDateTime() : null;
        });
    }
}
