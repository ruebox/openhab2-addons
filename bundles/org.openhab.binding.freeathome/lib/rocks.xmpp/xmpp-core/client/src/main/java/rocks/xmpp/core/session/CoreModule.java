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

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.bind.model.Bind;
import rocks.xmpp.core.sasl.model.Mechanisms;
import rocks.xmpp.core.session.model.Session;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.core.stanza.model.client.ClientMessage;
import rocks.xmpp.core.stanza.model.client.ClientPresence;
import rocks.xmpp.core.stream.model.StreamError;
import rocks.xmpp.core.stream.model.StreamFeatures;
import rocks.xmpp.core.tls.model.StartTls;
import rocks.xmpp.extensions.caps.EntityCapabilitiesManager;
import rocks.xmpp.extensions.caps.model.EntityCapabilities1;
import rocks.xmpp.extensions.caps2.model.EntityCapabilities2;
import rocks.xmpp.extensions.compress.model.StreamCompression;
import rocks.xmpp.extensions.data.layout.model.Page;
import rocks.xmpp.extensions.data.mediaelement.model.Media;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.data.validate.model.Validation;
import rocks.xmpp.extensions.disco.ServiceDiscoveryManager;
import rocks.xmpp.extensions.disco.model.info.InfoDiscovery;
import rocks.xmpp.extensions.disco.model.items.ItemDiscovery;
import rocks.xmpp.extensions.errors.model.ResourceLimitExceeded;
import rocks.xmpp.extensions.errors.model.StanzaTooBig;
import rocks.xmpp.extensions.errors.model.TooManyStanzas;
import rocks.xmpp.extensions.httpbind.model.Body;
import rocks.xmpp.extensions.privatedata.model.PrivateData;
import rocks.xmpp.extensions.privatedata.rosterdelimiter.model.RosterDelimiter;
import rocks.xmpp.extensions.rsm.model.ResultSetManagement;
import rocks.xmpp.im.roster.RosterManager;
import rocks.xmpp.im.roster.model.Roster;
import rocks.xmpp.im.roster.versioning.model.RosterVersioning;
import rocks.xmpp.im.subscription.PresenceManager;
import rocks.xmpp.im.subscription.preapproval.model.SubscriptionPreApproval;

import java.util.Arrays;
import java.util.Collection;

/**
 * The core module provides XMPP classes as well as manager classes which are associated with an XMPP session.
 * These are combined in an {@link Extension}. Although core classes are not extensions, they use the same concept as real extensions.
 *
 * @author Christian Schudt
 */
public final class CoreModule implements Module {

    @Override
    public final Collection<Extension> getExtensions() {
        return Arrays.asList(

                // Core
                Extension.of(StreamFeatures.class, StreamError.class, ClientMessage.class, ClientPresence.class, ClientIQ.class, Session.class, Bind.class, Mechanisms.class, StartTls.class, SubscriptionPreApproval.class, RosterVersioning.class),

                // XEP-0004: Data Forms
                Extension.of(DataForm.class),

                // XEP-0030: Service Discovery
                Extension.of(InfoDiscovery.NAMESPACE, ServiceDiscoveryManager.class, true, InfoDiscovery.class),
                Extension.of(ItemDiscovery.NAMESPACE, ServiceDiscoveryManager.class, true, ItemDiscovery.class),

                // XEP-0059: Result Set Management
                Extension.of(ResultSetManagement.NAMESPACE, true, ResultSetManagement.class),

                // XEP-0049: Private XML Storage
                Extension.of(PrivateData.class),

                // XEP-0083: Nested Roster Groups
                Extension.of(RosterDelimiter.class),

                // XEP-0106: JID Escaping
                Extension.of(Jid.ESCAPING_FEATURE, true),

                // XEP-0115: Entity Capabilities
                Extension.of(EntityCapabilities1.NAMESPACE, EntityCapabilitiesManager.class, true, EntityCapabilities1.class),

                // XEP-0122: Data Forms Validation
                Extension.of(Validation.NAMESPACE, false, Validation.class),

                // XEP-0124: Bidirectional-streams Over Synchronous HTTP (BOSH)
                Extension.of(Body.class),

                // XEP-0138: Stream Compression
                Extension.of(StreamCompression.class),

                // XEP-0141: Data Forms Layout
                Extension.of(Page.NAMESPACE, false, Page.class),

                // XEP-0205: Best Practices to Discourage Denial of Service Attacks
                Extension.of(ResourceLimitExceeded.class, StanzaTooBig.class, TooManyStanzas.class),

                // XEP-0221: Data Forms Media Element
                Extension.of(Media.class),

                // XEP-0390 Entity Capabilities 2.0
                Extension.of(EntityCapabilities2.NAMESPACE, EntityCapabilitiesManager.class, true, EntityCapabilities2.class),

                Extension.of(PresenceManager.class, true),

                Extension.of(ReconnectionManager.class, true),

                Extension.of(Roster.NAMESPACE, RosterManager.class, true, Roster.class)
        );
    }
}
