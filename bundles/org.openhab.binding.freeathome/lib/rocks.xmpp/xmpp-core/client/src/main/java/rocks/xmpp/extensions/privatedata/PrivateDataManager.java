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

package rocks.xmpp.extensions.privatedata;

import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.extensions.privatedata.model.PrivateData;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * This class implements <a href="https://xmpp.org/extensions/xep-0049.html">XEP-0049: Private XML Storage</a>.
 * <p>
 * It allows to retrieve and store private data in the server's private XML storage.
 * </p>
 * This class is thread-safe.
 *
 * @author Christian Schudt
 */
public final class PrivateDataManager extends Manager {

    private PrivateDataManager(XmppSession xmppSession) {
        super(xmppSession);
    }

    /**
     * Gets private data, which is stored on the server.
     *
     * @param type The class of the private data. Note that this class needs a no-arg default constructor.
     * @param <T>  The type of private data.
     * @return The async result with the list of stored items of the given type.
     */
    @SuppressWarnings("unchecked")
    public final <T> AsyncResult<T> getData(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            T instance = constructor.newInstance();
            AsyncResult<IQ> query = xmppSession.query(IQ.get(new PrivateData(instance)));
            return query.thenApply(result -> {
                PrivateData privateData = result.getExtension(PrivateData.class);
                return (T) privateData.getData();
            });
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Cannot instantiate class.", e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Could not find no-args default constructor.", e);
        }
    }

    /**
     * Stores private data.
     *
     * @param privateData The private data. The class of this object must be annotated with JAXB annotations and must known to the XMPP context in order to marshal und unmarshal it.
     * @return The async result.
     */
    public final AsyncResult<Void> storeData(Object privateData) {
        return xmppSession.query(IQ.set(new PrivateData(privateData)), Void.class);
    }
}
