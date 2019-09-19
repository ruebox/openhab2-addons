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

package rocks.xmpp.extensions.offline;


import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.extensions.caps.EntityCapabilitiesManager;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.disco.ServiceDiscoveryManager;
import rocks.xmpp.extensions.offline.model.OfflineMessage;
import rocks.xmpp.extensions.offline.model.OfflineMessageHeader;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This manager covers the use cases of <a href="https://xmpp.org/extensions/xep-0013.html">XEP-0013: Flexible Offline Message Retrieval</a>.
 * <p>
 * Offline Message Retrieval must be used before sending initial presence, in order to tell the server, that it must not flood the client with offline messages later.
 * </p>
 * Enabling or disabling this manager has no effect.
 *
 * @author Christian Schudt
 */
public final class OfflineMessageManager extends Manager {


    private OfflineMessageManager(XmppSession xmppSession) {
        super(xmppSession);
    }

    /**
     * Discovers support for flexible offline message retrieval.
     *
     * @return The async result with true, if the server supports flexible offline message retrieval; otherwise false.
     */
    public AsyncResult<Boolean> isSupported() {
        EntityCapabilitiesManager entityCapabilitiesManager = xmppSession.getManager(EntityCapabilitiesManager.class);
        return entityCapabilitiesManager.discoverCapabilities(xmppSession.getDomain())
                .thenApply(infoNode -> infoNode.getFeatures().contains(OfflineMessage.NAMESPACE));
    }

    /**
     * Gets the number of offline messages.
     *
     * @return The async result with the number of offline messages.
     * @see <a href="https://xmpp.org/extensions/xep-0013.html#request-number">2.2 Requesting Number of Messages</a>
     */
    public AsyncResult<Integer> requestNumberOfMessages() {
        ServiceDiscoveryManager serviceDiscoveryManager = xmppSession.getManager(ServiceDiscoveryManager.class);
        return serviceDiscoveryManager.discoverInformation(null, OfflineMessage.NAMESPACE).thenApply(infoDiscovery -> {
            if (!infoDiscovery.getExtensions().isEmpty()) {
                DataForm dataForm = infoDiscovery.getExtensions().get(0);
                if (dataForm != null) {
                    for (DataForm.Field field : dataForm.getFields()) {
                        if ("number_of_messages".equals(field.getVar())) {
                            String numberOfMessages = field.getValues().get(0);
                            return Integer.valueOf(numberOfMessages);
                        }
                    }
                }
            }
            return 0;
        });
    }

    /**
     * Gets the offline message headers.
     *
     * @return The async result with the list of message headers.
     * @see <a href="https://xmpp.org/extensions/xep-0013.html#request-headers">2.3 Requesting Message Headers</a>
     */
    public AsyncResult<List<OfflineMessageHeader>> requestMessageHeaders() {
        ServiceDiscoveryManager serviceDiscoveryManager = xmppSession.getManager(ServiceDiscoveryManager.class);
        return serviceDiscoveryManager.discoverItems(null, OfflineMessage.NAMESPACE).thenApply(itemNode ->
                itemNode.getItems().stream()
                        .map(item -> new OfflineMessageHeader(Jid.of(item.getName()), item.getNode()))
                        .collect(Collectors.toList())
        );
    }

    /**
     * Requests a specific offline message. The message will be sent in a normal way and contains the {@link OfflineMessage} extension, which can be used to match the id {@link OfflineMessage#getId()}.
     *
     * @param id The offline message id, which corresponds to {@link OfflineMessageHeader#getId()}
     * @return The async result with the server's IQ reply.
     * @see <a href="https://xmpp.org/extensions/xep-0013.html#retrieve-specific">2.4 Retrieving Specific Messages</a>
     */
    public AsyncResult<IQ> requestMessage(String id) {
        return xmppSession.query(IQ.get(new OfflineMessage(new OfflineMessage.Item(id, OfflineMessage.Item.Action.VIEW))));
    }

    /**
     * Removes specific offline messages.
     *
     * @param ids The offline message ids, which correspond to {@link OfflineMessageHeader#getId()}
     * @return The async result with the server's IQ reply.
     * @see <a href="https://xmpp.org/extensions/xep-0013.html#remove-specific">2.5 Removing Specific Messages</a>
     */
    public AsyncResult<IQ> removeMessages(String... ids) {
        List<OfflineMessage.Item> items = new ArrayList<>();
        for (String id : ids) {
            items.add(new OfflineMessage.Item(id, OfflineMessage.Item.Action.REMOVE));
        }
        return xmppSession.query(IQ.set(new OfflineMessage(items)));
    }

    /**
     * Requests all offline messages.
     *
     * @return The async result with the server's IQ reply.
     * @see <a href="https://xmpp.org/extensions/xep-0013.html#retrieve-all">2.6 Retrieving All Messages</a>
     */
    public AsyncResult<IQ> requestAllMessages() {
        return xmppSession.query(IQ.get(new OfflineMessage(true, false)));
    }

    /**
     * Removes all offline messages.
     *
     * @return The async result with the server's IQ reply.
     * @see <a href="https://xmpp.org/extensions/xep-0013.html#remove-all">2.7 Removing All Messages</a>
     */
    public AsyncResult<IQ> removeAllMessages() {
        return xmppSession.query(IQ.set(new OfflineMessage(false, true)));
    }
}
