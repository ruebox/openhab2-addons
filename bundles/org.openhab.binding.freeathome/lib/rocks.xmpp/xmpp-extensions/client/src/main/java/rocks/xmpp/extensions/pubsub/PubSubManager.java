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

package rocks.xmpp.extensions.pubsub;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.extensions.disco.ServiceDiscoveryManager;
import rocks.xmpp.extensions.disco.model.info.Identity;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is the entry point to work with pubsub.
 * <p>
 * You should first {@linkplain #createPubSubService(Jid) create a pubsub service}, which allows you to work with that service.
 * If you don't know the service address, you can {@linkplain #discoverPubSubServices()} discover} the pubsub services hosted at your server.
 * <p>
 * It also allows you to {@linkplain #createPersonalEventingService() create a Personal Eventing Service}, which is a virtual pubsub service, bound to your account.
 *
 * @author Christian Schudt
 * @see rocks.xmpp.extensions.pubsub.PubSubService
 * @see <a href="https://xmpp.org/extensions/xep-0060.html">XEP-0060: Publish-Subscribe</a>
 * @see <a href="https://xmpp.org/extensions/xep-0163.html">XEP-0163: Personal Eventing Protocol</a>
 */
public final class PubSubManager extends Manager {

    private final ServiceDiscoveryManager serviceDiscoveryManager;

    private PubSubManager(XmppSession xmppSession) {
        super(xmppSession);
        serviceDiscoveryManager = xmppSession.getManager(ServiceDiscoveryManager.class);
    }

    /**
     * Discovers the publish-subscribe services for the current connection.
     *
     * @return The async result with the list of publish-subscribe services.
     */
    public AsyncResult<List<PubSubService>> discoverPubSubServices() {
        return serviceDiscoveryManager.discoverServices(Identity.pubsubService())
                .thenApply(services -> services.stream()
                        .map(service -> new PubSubService(service.getJid(), service.getName(), xmppSession, serviceDiscoveryManager))
                        .collect(Collectors.toList()));
    }

    /**
     * Creates a pubsub service.
     *
     * @param service The pubsub service address, e.g. {@code Jid.of("pubsub.mydomain")}
     * @return The pubsub service.
     */
    public PubSubService createPubSubService(Jid service) {
        return new PubSubService(service, null, xmppSession, serviceDiscoveryManager);
    }

    /**
     * Creates a personal eventing service.
     *
     * @return The personal eventing service.
     * @see <a href="https://xmpp.org/extensions/xep-0163.html">XEP-0163: Personal Eventing Protocol</a>
     */
    public PubSubService createPersonalEventingService() {
        return new PubSubService(xmppSession.getConnectedResource().asBareJid(), "Personal Eventing Service", xmppSession, serviceDiscoveryManager);
    }
}
