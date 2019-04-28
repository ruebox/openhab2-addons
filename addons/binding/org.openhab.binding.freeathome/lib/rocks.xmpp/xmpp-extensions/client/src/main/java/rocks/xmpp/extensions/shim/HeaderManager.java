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

package rocks.xmpp.extensions.shim;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.disco.ServiceDiscoveryManager;
import rocks.xmpp.extensions.disco.model.info.Identity;
import rocks.xmpp.extensions.disco.model.info.InfoNode;
import rocks.xmpp.extensions.shim.model.Headers;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * Manages support for <a href="https://xmpp.org/extensions/xep-0131.html">XEP-0131: Stanza Headers and Internet Metadata</a>.
 * <p>
 * By default support is disabled, so that service discovery won't reveal the 'http://jabber.org/protocol/shim' feature.
 * </p>
 *
 * @author Christian Schudt
 */
public final class HeaderManager extends Manager implements InfoNode {

    private final Set<String> supportedHeaders;

    private final ServiceDiscoveryManager serviceDiscoveryManager;

    private HeaderManager(XmppSession xmppSession) {
        super(xmppSession);
        this.supportedHeaders = new CopyOnWriteArraySet<>();
        serviceDiscoveryManager = xmppSession.getManager(ServiceDiscoveryManager.class);
    }

    /**
     * Gets the supported headers.
     * <p>
     * If you want to advertise support for a specific header, add it to this set.
     * Service discovery requests to the 'header' node will then reveal supported headers.
     * </p>
     *
     * @return The supported headers.
     */
    public Set<String> getSupportedHeaders() {
        return supportedHeaders;
    }

    @Override
    public String getNode() {
        return Headers.NAMESPACE;
    }

    /**
     * Discovers the supported headers of another entity.
     *
     * @param jid The JID.
     * @return The async result with the list of supported headers.
     */
    public AsyncResult<List<String>> discoverSupportedHeaders(Jid jid) {
        return serviceDiscoveryManager.discoverInformation(jid, Headers.NAMESPACE).thenApply(infoNode ->
                infoNode.getFeatures()
                        .stream()
                        .map(feature -> feature.substring(feature.indexOf('#') + 1))
                        .collect(Collectors.toList()));
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        serviceDiscoveryManager.addInfoNode(this);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        serviceDiscoveryManager.removeInfoNode(getNode());
    }

    @Override
    public Set<String> getFeatures() {
        // https://xmpp.org/extensions/xep-0131.html#disco-header
        return supportedHeaders.stream().map(supportedHeader -> Headers.NAMESPACE + '#' + supportedHeader).collect(Collectors.toSet());
    }

    @Override
    public Set<Identity> getIdentities() {
        return null;
    }

    @Override
    public List<DataForm> getExtensions() {
        return null;
    }
}
