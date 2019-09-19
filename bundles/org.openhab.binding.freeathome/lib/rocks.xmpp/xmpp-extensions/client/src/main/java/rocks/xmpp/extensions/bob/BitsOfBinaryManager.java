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

package rocks.xmpp.extensions.bob;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.AbstractIQHandler;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.extensions.bob.model.Data;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Christian Schudt
 */
class BitsOfBinaryManager extends Manager {

    private final Map<String, Data> dataCache = new ConcurrentHashMap<>();

    private BitsOfBinaryManager(final XmppSession xmppSession) {
        super(xmppSession, true);
    }

    @Override
    protected void initialize() {
        xmppSession.addIQHandler(Data.class, new AbstractIQHandler(IQ.Type.GET) {
            @Override
            protected IQ processRequest(IQ iq) {
                Data data = iq.getExtension(Data.class);
                // The recipient then would either return an error (e.g., <item-not-found/> if it does not have data matching the Content-ID) or return the data.
                Data cachedData = dataCache.get(data.getContentId());
                if (cachedData != null) {
                    return iq.createResult(cachedData);
                } else {
                    return iq.createError(Condition.ITEM_NOT_FOUND);
                }
            }
        });
    }

    /**
     * Retrieves data from another entity.
     *
     * @param contentId Gets the data from
     * @param to        The recipient. This should be a full JID.
     * @return The async result with the data.
     */

    public AsyncResult<Data> getData(String contentId, Jid to) {
        return xmppSession.query(IQ.get(to, new Data(contentId))).thenApply(result -> {
            Data data = result.getExtension(Data.class);
            // Only cache the data, if the max-age attribute absent or not zero.
//        if (data != null && (data.getMaxAge() != null && data.getMaxAge() != 0 || data.getMaxAge() == null)) {
//            dataCache.put(contentId, data);
//        }
            return data;
        });
    }

    /**
     * Puts data to the data cache, so that requests to this data can be returned.
     * Whenever you use {@link Data} (i.e. its content id) anywhere, you should make it known to this manager, so that it can return the data to a requesting entity.
     *
     * @param data The data.
     */

    public void put(Data data) {
        dataCache.put(data.getContentId(), data);
    }

    @Override
    protected void dispose() {
        dataCache.clear();
    }
}
