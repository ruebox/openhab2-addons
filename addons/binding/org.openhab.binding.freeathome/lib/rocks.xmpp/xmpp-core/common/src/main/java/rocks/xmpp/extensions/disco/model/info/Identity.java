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

package rocks.xmpp.extensions.disco.model.info;

import rocks.xmpp.util.Strings;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlAttribute;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents an identity of an XMPP entity.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/extensions/xep-0030.html#info">3. Discovering Information About a Jabber Entity</a></cite></p>
 * <p>In disco, an entity's identity is broken down into its category (server, client, gateway, directory, etc.) and its particular type within that category (IM server, phone vs. handheld client, MSN gateway vs. AIM gateway, user directory vs. chatroom directory, etc.). This information helps requesting entities to determine the group or "bucket" of services into which the entity is most appropriately placed (e.g., perhaps the entity is shown in a GUI with an appropriate icon). An entity MAY have multiple identities. When multiple identity elements are provided, the name attributes for each identity element SHOULD have the same value.</p>
 * </blockquote>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/registrar/disco-categories.html">Service Discovery Identities</a>
 */
public final class Identity implements Comparable<Identity> {

    private static final String ACCOUNT = "account";

    private static final String AUTH = "auth";

    private static final String AUTOMATION = "automation";

    private static final String CLIENT = "client";

    private static final String COLLABORATION = "collaboration";

    private static final String COMPONENT = "component";

    private static final String CONFERENCE = "conference";

    private static final String DIRECTORY = "directory";

    private static final String GATEWAY = "gateway";

    private static final String HEADLINE = "headline";

    private static final String HIERARCHY = "hierarchy";

    private static final String PROXY = "proxy";

    private static final String PUBSUB = "pubsub";

    private static final String SERVER = "server";

    private static final String STORE = "store";

    @XmlAttribute
    private final String category;

    @XmlAttribute
    private final String type;

    @XmlAttribute
    private final String name;

    @XmlAttribute(namespace = XMLConstants.XML_NS_URI)
    private final Locale lang;

    /**
     * Private default constructor for unmarshalling.
     */
    private Identity() {
        this.category = null;
        this.type = null;
        this.name = null;
        this.lang = null;
    }

    /**
     * Creates an identity with a category and type.
     *
     * @param category The category.
     * @param type     The type.
     */
    private Identity(String category, String type) {
        this(category, type, null, null);
    }

    /**
     * Creates an identity with a category, type, name and language.
     *
     * @param category The category.
     * @param type     The type.
     * @param name     The name.
     * @param language The language.
     */
    private Identity(String category, String type, String name, Locale language) {
        this.category = Objects.requireNonNull(category);
        this.type = Objects.requireNonNull(type);
        this.name = name;
        this.lang = language;
    }

    /**
     * Creates an identity with a category and type.
     * <p>
     * Only use this method in exceptional cases, in most case you should use one of the many static factory methods, which creates a registered identity, e.g. {@link #clientBot()}
     *
     * @param category The category.
     * @param type     The type.
     * @return The identity.
     */
    public static Identity ofCategoryAndType(String category, String type) {
        return new Identity(Objects.requireNonNull(category), Objects.requireNonNull(type));
    }

    /**
     * The user@host is an administrative account.
     *
     * @return The identity.
     */
    public static Identity accountAdmin() {
        return new Identity(ACCOUNT, "admin");
    }

    /**
     * The user@host is a "guest" account that allows anonymous login by any user.
     *
     * @return The identity.
     */
    public static Identity accountAnonymous() {
        return new Identity(ACCOUNT, "anonymous");
    }

    /**
     * The user@host is a registered or provisioned account associated with a particular non-administrative user.
     *
     * @return The identity.
     */
    public static Identity accountRegistered() {
        return new Identity(ACCOUNT, "registered");
    }

    /**
     * A server component that authenticates based on external certificates.
     *
     * @return The identity.
     */
    public static Identity authCert() {
        return new Identity(AUTH, "cert");
    }

    /**
     * A server authentication component other than one of the registered types.
     *
     * @return The identity.
     */
    public static Identity authGeneric() {
        return new Identity(AUTH, "generic");
    }

    /**
     * A server component that authenticates against an LDAP database.
     *
     * @return The identity.
     */
    public static Identity authLdap() {
        return new Identity(AUTH, "ldap");
    }

    /**
     * A server component that authenticates against an NT domain.
     *
     * @return The identity.
     */
    public static Identity authNtlm() {
        return new Identity(AUTH, "ntlm");
    }

    /**
     * A server component that authenticates against a PAM system.
     *
     * @return The identity.
     */
    public static Identity authPam() {
        return new Identity(AUTH, "pam");
    }

    /**
     * The node for a list of commands; valid only for the node "http://jabber.org/protocol/commands".
     *
     * @return The identity.
     */
    public static Identity automationCommandList() {
        return new Identity(AUTOMATION, "command-list");
    }

    /**
     * A node for a specific command; the "node" attribute uniquely identifies the command.
     *
     * @return The identity.
     */
    public static Identity automationCommandNode() {
        return new Identity(AUTOMATION, "command-node");
    }

    /**
     * An entity that supports Jabber-RPC.
     *
     * @return The identity.
     */
    public static Identity automationRpc() {
        return new Identity(AUTOMATION, "rpc");
    }

    /**
     * An entity that supports the SOAP XMPP Binding.
     *
     * @return The identity.
     */
    public static Identity automationSoap() {
        return new Identity(AUTOMATION, "soap");
    }

    /**
     * An entity that provides automated translation services.
     *
     * @return The identity.
     */
    public static Identity automationTranslation() {
        return new Identity(AUTOMATION, "translation");
    }

    /**
     * An automated client that is not controlled by a human user.
     *
     * @return The identity.
     */
    public static Identity clientBot() {
        return new Identity(CLIENT, "bot");
    }

    /**
     * Minimal non-GUI client used on dumb terminals or text-only screens.
     *
     * @return The identity.
     */
    public static Identity clientConsole() {
        return new Identity(CLIENT, "console");
    }

    /**
     * A client running on a gaming console.
     *
     * @return The identity.
     */
    public static Identity clientGame() {
        return new Identity(CLIENT, "game");
    }

    /**
     * A client running on a PDA, RIM device, or other handheld.
     *
     * @return The identity.
     */
    public static Identity clientHandheld() {
        return new Identity(CLIENT, "handheld");
    }

    /**
     * Standard full-GUI client used on desktops and laptops.
     *
     * @return The identity.
     */
    public static Identity clientPc() {
        return new Identity(CLIENT, "pc");
    }

    /**
     * A client running on a mobile phone or other telephony device.
     *
     * @return The identity.
     */
    public static Identity clientPhone() {
        return new Identity(CLIENT, "phone");
    }

    /**
     * A client that is not actually using an instant messaging client; however, messages sent to this contact will be delivered as Short Message Service (SMS) messages.
     *
     * @return The identity.
     */
    public static Identity clientSms() {
        return new Identity(CLIENT, "sms");
    }

    /**
     * A client operated from within a web browser.
     *
     * @return The identity.
     */
    public static Identity clientWeb() {
        return new Identity(CLIENT, "web");
    }

    /**
     * Multi-user whiteboarding service.
     *
     * @return The identity.
     */
    public static Identity collaborationWhiteboard() {
        return new Identity(COLLABORATION, "whiteboard");
    }

    /**
     * A server component that archives traffic.
     *
     * @return The identity.
     */
    public static Identity componentArchive() {
        return new Identity(COMPONENT, "archive");
    }

    /**
     * A server component that handles client connections.
     *
     * @return The identity.
     */
    public static Identity componentClientToServer() {
        return new Identity(COMPONENT, "c2s");
    }

    /**
     * A server component other than one of the registered types.
     *
     * @return The identity.
     */
    public static Identity componentGeneric() {
        return new Identity(COMPONENT, "generic");
    }

    /**
     * A server component that handles load balancing.
     *
     * @return The identity.
     */
    public static Identity componentLoadBalancing() {
        return new Identity(COMPONENT, "load");
    }

    /**
     * A server component that logs server information.
     *
     * @return The identity.
     */
    public static Identity componentLog() {
        return new Identity(COMPONENT, "log");
    }

    /**
     * A server component that provides presence information.
     *
     * @return The identity.
     */
    public static Identity componentPresence() {
        return new Identity(COMPONENT, "presence");
    }

    /**
     * A server component that handles core routing logic.
     *
     * @return The identity.
     */
    public static Identity componentRouter() {
        return new Identity(COMPONENT, "router");
    }

    /**
     * A server component that handles server connections	.
     *
     * @return The identity.
     */
    public static Identity componentServerToServer() {
        return new Identity(COMPONENT, "s2s");
    }

    /**
     * A server component that manages user sessions.
     *
     * @return The identity.
     */
    public static Identity componentSessionManagement() {
        return new Identity(COMPONENT, "sm");
    }

    /**
     * A server component that provides server statistics.
     *
     * @return The identity.
     */
    public static Identity componentStatistics() {
        return new Identity(COMPONENT, "stats");
    }

    /**
     * Internet Relay Chat service.
     *
     * @return The identity.
     */
    public static Identity conferenceIrc() {
        return new Identity(CONFERENCE, "irc");
    }

    /**
     * Text conferencing service.
     *
     * @return The identity.
     */
    public static Identity conferenceText() {
        return new Identity(CONFERENCE, "text");
    }

    /**
     * A directory of chatrooms.
     *
     * @return The identity.
     */
    public static Identity directoryChatRoom() {
        return new Identity(DIRECTORY, "chatroom");
    }

    /**
     * A directory that provides shared roster groups.
     *
     * @return The identity.
     */
    public static Identity directoryGroup() {
        return new Identity(DIRECTORY, "group");
    }

    /**
     * A directory of end users (e.g., JUD).
     *
     * @return The identity.
     */
    public static Identity directoryUser() {
        return new Identity(DIRECTORY, "user");
    }

    /**
     * A directory of waiting list entries.
     *
     * @return The identity.
     */
    public static Identity directoryWaitingList() {
        return new Identity(DIRECTORY, "waitinglist");
    }

    /**
     * Gateway to AOL Instant Messenger.
     *
     * @return The identity.
     */
    public static Identity gatewayAIM() {
        return new Identity(GATEWAY, "aim");
    }

    /**
     * Gateway to the Facebook IM service.
     *
     * @return The identity.
     */
    public static Identity gatewayFacebook() {
        return new Identity(GATEWAY, "facebook");
    }

    /**
     * Gateway to the Gadu-Gadu IM service.
     *
     * @return The identity.
     */
    public static Identity gatewayGaduGadu() {
        return new Identity(GATEWAY, "gadu-gadu");
    }

    /**
     * Gateway that provides HTTP Web Services access.
     *
     * @return The identity.
     */
    public static Identity gatewayHttpWs() {
        return new Identity(GATEWAY, "http-ws");
    }

    /**
     * Gateway to ICQ.
     *
     * @return The identity.
     */
    public static Identity gatewayICQ() {
        return new Identity(GATEWAY, "icq");
    }

    /**
     * Gateway to IRC.
     *
     * @return The identity.
     */
    public static Identity gatewayIRC() {
        return new Identity(GATEWAY, "irc");
    }

    /**
     * Gateway to Microsoft Live Communications Server.
     *
     * @return The identity.
     */
    public static Identity gatewayLCS() {
        return new Identity(GATEWAY, "lcs");
    }

    /**
     * Gateway to MSN Messenger.
     *
     * @return The identity.
     */
    public static Identity gatewayMSN() {
        return new Identity(GATEWAY, "msn");
    }

    /**
     * Gateway to the MySpace IM service.
     *
     * @return The identity.
     */
    public static Identity gatewayMySpaceIM() {
        return new Identity(GATEWAY, "myspaceim");
    }

    /**
     * Gateway to Microsoft Office Communications Server.
     *
     * @return The identity.
     */
    public static Identity gatewayOCS() {
        return new Identity(GATEWAY, "ocs");
    }

    /**
     * Gateway to the QQ IM service.
     *
     * @return The identity.
     */
    public static Identity gatewayQQ() {
        return new Identity(GATEWAY, "qq");
    }

    /**
     * Gateway to IBM Lotus Sametime.
     *
     * @return The identity.
     */
    public static Identity gatewaySametime() {
        return new Identity(GATEWAY, "sametime");
    }

    /**
     * Gateway to SIP for Instant Messaging and Presence Leveraging Extensions (SIMPLE).
     *
     * @return The identity.
     */
    public static Identity gatewaySimple() {
        return new Identity(GATEWAY, "simple");
    }

    /**
     * Gateway to the Skype service.
     *
     * @return The identity.
     */
    public static Identity gatewaySkype() {
        return new Identity(GATEWAY, "skype");
    }

    /**
     * Gateway to Short Message Service.
     *
     * @return The identity.
     */
    public static Identity gatewaySMS() {
        return new Identity(GATEWAY, "sms");
    }

    /**
     * Gateway to the SMTP (email) network.
     *
     * @return The identity.
     */
    public static Identity gatewaySMTP() {
        return new Identity(GATEWAY, "smtp");
    }

    /**
     * Gateway to the Tlen IM service.
     *
     * @return The identity.
     */
    public static Identity gatewayTlen() {
        return new Identity(GATEWAY, "tlen");
    }

    /**
     * Gateway to the Xfire gaming and IM service.
     *
     * @return The identity.
     */
    public static Identity gatewayXfire() {
        return new Identity(GATEWAY, "xfire");
    }

    /**
     * Gateway to another XMPP service (NOT via native server-to-server communication).
     *
     * @return The identity.
     */
    public static Identity gatewayXMPP() {
        return new Identity(GATEWAY, "xmpp");
    }

    /**
     * Gateway to Yahoo! Instant Messenger.
     *
     * @return The identity.
     */
    public static Identity gatewayYahoo() {
        return new Identity(GATEWAY, "yahoo");
    }

    /**
     * Service that notifies a user of new email messages.
     *
     * @return The identity.
     */
    public static Identity headlineNewMail() {
        return new Identity(HEADLINE, "newmail");
    }

    /**
     * RSS notification service.
     *
     * @return The identity.
     */
    public static Identity headlineRss() {
        return new Identity(HEADLINE, "rss");
    }

    /**
     * Service that provides weather alerts..
     *
     * @return The identity.
     */
    public static Identity headlineWeather() {
        return new Identity(HEADLINE, "weather");
    }

    /**
     * A service discovery node that contains further nodes in the hierarchy.
     *
     * @return The identity.
     */
    public static Identity hierarchyBranch() {
        return new Identity(HIERARCHY, "branch");
    }

    /**
     * A service discovery node that does not contain further nodes in the hierarchy.
     *
     * @return The identity.
     */
    public static Identity hierarchyLeaf() {
        return new Identity(HIERARCHY, "leaf");
    }

    /**
     * SOCKS5 bytestreams proxy service.
     *
     * @return The identity.
     */
    public static Identity proxyByteStreams() {
        return new Identity(PROXY, "bytestreams");
    }

    /**
     * A pubsub node of the "collection" type.
     *
     * @return The identity.
     */
    public static Identity pubsubCollection() {
        return new Identity(PUBSUB, "collection");
    }

    /**
     * A pubsub node of the "leaf" type.
     *
     * @return The identity.
     */
    public static Identity pubsubLeaf() {
        return new Identity(PUBSUB, "leaf");
    }

    /**
     * A personal eventing service that supports the publish-subscribe subset defined in XEP-0163..
     *
     * @return The identity.
     */
    public static Identity pubsubPersonalEventingService() {
        return new Identity(PUBSUB, "pep");
    }

    /**
     * A pubsub service that supports the functionality defined in XEP-0060.
     *
     * @return The identity.
     */
    public static Identity pubsubService() {
        return new Identity(PUBSUB, "service");
    }

    /**
     * Standard Jabber/XMPP server used for instant messaging and presence.
     *
     * @return The identity.
     */
    public static Identity serverInstantMessaging() {
        return new Identity(SERVER, "im");
    }

    /**
     * A server component that stores data in a Berkeley database.
     *
     * @return The identity.
     */
    public static Identity storeBerkeley() {
        return new Identity(STORE, "berkeley");
    }

    /**
     * A server component that stores data on the file system.
     *
     * @return The identity.
     */
    public static Identity storeFile() {
        return new Identity(STORE, "file");
    }

    /**
     * A server data storage component other than one of the registered types.
     *
     * @return The identity.
     */
    public static Identity storeGeneric() {
        return new Identity(STORE, "generic");
    }

    /**
     * A server component that stores data in an LDAP database.
     *
     * @return The identity.
     */
    public static Identity storeLdap() {
        return new Identity(STORE, "ldap");
    }

    /**
     * A server component that stores data in a MySQL database.
     *
     * @return The identity.
     */
    public static Identity storeMySQL() {
        return new Identity(STORE, "mysql");
    }

    /**
     * A server component that stores data in an Oracle database.
     *
     * @return The identity.
     */
    public static Identity storeOracle() {
        return new Identity(STORE, "oracle");
    }

    /**
     * A server component that stores data in a PostgreSQL database.
     *
     * @return The identity.
     */
    public static Identity storePostgreSQL() {
        return new Identity(STORE, "postgres");
    }

    /**
     * Gets the category, e.g. server, client, gateway, directory, etc.
     *
     * @return The category.
     */
    public final String getCategory() {
        return category;
    }

    /**
     * Gets the type within the {@linkplain #getCategory() category}, e.g. IM server, phone vs. handheld client, MSN gateway vs. AIM gateway, user directory vs. chatroom directory, etc.
     *
     * @return The type.
     */
    public final String getType() {
        return type;
    }

    /**
     * Gets the identity's name.
     *
     * @return The name
     */
    public final String getName() {
        return name;
    }

    /**
     * The optional language to localize the {@linkplain #getName() name}.
     *
     * @return The language.
     */
    public final Locale getLanguage() {
        return lang;
    }

    /**
     * An identity is considered equal, if category, type and language are equal, because there cannot be two identities with the same category, type and language, but with different names.
     *
     * @param o The other object.
     * @return True, if category, type and language are equal.
     */
    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Identity)) {
            return false;
        }
        Identity other = (Identity) o;

        return Objects.equals(category, other.category)
                && Objects.equals(type, other.type)
                && Objects.equals(lang, other.lang);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(category, type, lang);
    }

    /**
     * Implements a natural ordering of an identity, as suggested and required by <a href="https://xmpp.org/extensions/xep-0115.html">XEP-0115: Entity Capabilities</a>.
     *
     * @param o The other identity.
     * @return The result of the comparison.
     */
    @Override
    public final int compareTo(Identity o) {
        int result;
        if (o == null) {
            result = 1;
        } else {

            if (category == null && o.category == null) {
                result = 0;
            } else if (category == null) {
                result = -1;
            } else if (o.category == null) {
                result = 1;
            } else {
                result = Strings.compareUnsignedBytes(category, o.category, StandardCharsets.UTF_8);
            }

            if (result == 0) {
                if (type == null && o.type == null) {
                    result = 0;
                } else if (type == null) {
                    result = -1;
                } else if (o.type == null) {
                    result = 1;
                } else {
                    result = Strings.compareUnsignedBytes(type, o.type, StandardCharsets.UTF_8);
                }
            }

            if (result == 0) {
                if (lang == null && o.lang == null) {
                    result = 0;
                } else if (lang == null) {
                    result = -1;
                } else if (o.lang == null) {
                    result = 1;
                } else {
                    result = Strings.compareUnsignedBytes(lang.toLanguageTag(), o.lang.toLanguageTag(), StandardCharsets.UTF_8);
                }
            }

            if (result == 0) {
                if (name == null && o.name == null) {
                    return 0;
                } else if (name == null) {
                    return -1;
                } else if (o.name == null) {
                    return 1;
                } else {
                    return Strings.compareUnsignedBytes(name, o.name, StandardCharsets.UTF_8);
                }
            }
        }
        return result;
    }

    @Override
    public final String toString() {
        return "Category: " + category + " / Type: " + type + " / Name: " + name;
    }

    /**
     * Creates a new identity with a name.
     *
     * @param name The name.
     * @return The identity.
     */
    public Identity withName(String name) {
        return new Identity(category, type, name, lang);
    }

    /**
     * Creates a new identity with a name and a language.
     *
     * @param name     The name.
     * @param language The language.
     * @return The identity.
     */
    public Identity withName(String name, Locale language) {
        return new Identity(category, type, name, language);
    }
}

