/**
 * Copyright (c) 2014-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.freeathome.handler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.freeathome.config.FreeAtHomeBridgeConfig;
import org.openhab.binding.freeathome.xmpp.rocks.extension.abb.com.protocol.update.Update;
import org.openhab.binding.freeathome.xmpp.rocks.extension.abb.com.protocol.update.UpdateEvent;
import org.openhab.binding.freeathome.xmpp.rocks.extension.abb.com.protocol.update.UpdateManager;
import org.openhab.binding.freeathome.xmpp.rocks.extensions.abb.com.protocol.data.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.session.Extension;
import rocks.xmpp.core.session.SessionStatusEvent;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.session.XmppSessionConfiguration;
import rocks.xmpp.core.session.debug.ConsoleDebugger;
import rocks.xmpp.core.stanza.IQEvent;
import rocks.xmpp.core.stanza.MessageEvent;
import rocks.xmpp.core.stanza.PresenceEvent;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.extensions.httpbind.BoshConnectionConfiguration;
import rocks.xmpp.extensions.pubsub.PubSubManager;
import rocks.xmpp.extensions.pubsub.model.Item;
import rocks.xmpp.extensions.pubsub.model.event.Event;
import rocks.xmpp.extensions.rpc.RpcException;
import rocks.xmpp.extensions.rpc.RpcManager;

//import rocks.xmpp.core.session.SessionStatusEvent;
//import rocks.xmpp.core.session.XmppSession;

//import rocks.xmpp.addr.Jid;
//import rocks.xmpp.core.XmppException;
//import rocks.xmpp.core.session.SessionStatusEvent;
//import rocks.xmpp.core.session.XmppClient;
//import rocks.xmpp.core.session.XmppSession;
//import rocks.xmpp.core.session.XmppSessionConfiguration;
//import rocks.xmpp.extensions.httpbind.BoshConnectionConfiguration;
//import rocks.xmpp.extensions.rpc.RpcException;
//import rocks.xmpp.extensions.rpc.RpcManager;
import rocks.xmpp.extensions.rpc.model.Value;
import rocks.xmpp.im.subscription.PresenceManager;

/**
 * Handler that connects to FreeAtHome gateway.
 *
 * @author ruebox
 *
 */
public class FreeAtHomeBridgeHandler extends BaseBridgeHandler {

    private Logger logger = LoggerFactory.getLogger(FreeAtHomeBridgeHandler.class);

    private BoshConnectionConfiguration m_BoshConfiguration;
    private XmppClient m_XmppClient = null;
    private XmppSessionConfiguration m_XmppConfiguration;
    private RpcManager m_RpcManager;
    private int counter = 0;

    /*
     * Store bridge configuration
     */
    protected FreeAtHomeBridgeConfig m_Configuration;

    public FreeAtHomeBridgeHandler(Bridge bridge) {
        super(bridge);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void initialize() {
        logger.debug("Initializing FreeAtHome bridge handler.");

        FreeAtHomeBridgeConfig configuration = getConfigAs(FreeAtHomeBridgeConfig.class);

        m_Configuration = configuration;

        logger.debug("Gateway IP      {}.", m_Configuration.ipAddress);
        logger.debug("Port            {}.", m_Configuration.port);
        logger.debug("Login           {}.", m_Configuration.login);
        logger.debug("Password        {}.", m_Configuration.password);

        connectGateway();
    }

    @Override
    public void dispose() {

        try {
            m_XmppClient.close();
        } catch (XmppException e) {
            logger.error("Can not close XMPP Client");
            logger.error(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("Can not close XMPP Client");
            logger.error(e.getMessage());
        }

    };

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            logger.debug("Refresh command received.");
            // refreshData();
        } else {
            logger.warn("No bridge commands defined. Cannot process '{}'.", command.toString());
        }
    }

    /*
     * Call set data point via XMPP service
     */
    public void setDataPoint(String adress, String value) {
        try {
            // ArrayList parameter = new ArrayList();
            // parameter.add("ABB260851E51/ch0003/idp0000");
            // parameter.add(1);
            // Elevate EG Raffstore
            // <body rid='531107' sid='2c1433b0-af08-4624-b043-1ff8dca805c6'
            // xmlns='http://jabber.org/protocol/httpbind' key='03bee55104d73ca72418239c85b2178703a39249'><iq
            // xmlns="jabber:client" to="mrha@busch-jaeger.de/rpc" type="set" id="1454004737242"><query
            //
            // xmlns="jabber:iq:rpc"><methodCall><methodName>RemoteInterface.setDatapoint</methodName><params><param><value><string>FFFF00000000/ch0000/odp0003</string></value></param><param><value><string>0</string></value></param></params></methodCall></query></iq></body>
            // Value para1 = Value.of("ABB260851E51/ch0003/idp0000");

            Value para0 = Value.of(adress);
            Value para1 = Value.of(value);

            Value response = m_RpcManager.call(Jid.of("mrha@busch-jaeger.de/rpc"), "RemoteInterface.setDatapoint",
                    para0, para1);
            logger.debug("Response from RPC RemoteInterface.setDatapoint: " + response.getAsString());
            boolean resp = response.getAsBoolean();

        } catch (XmppException e) {
            logger.error("XMPP Exception: " + e.getMessage());
            e.printStackTrace();
            // E.g. a StanzaException, if the responder does not support the protocol or an
            // // internal-server-error has occurred.
        } catch (RpcException e) {
            logger.error("RPC Exception: " + e.getMessage());
            e.printStackTrace();
            // If the responder responded with an application level XML-RPC fault.
        }
    }

    private void connectGateway() {

        m_BoshConfiguration = BoshConnectionConfiguration.builder().hostname(m_Configuration.ipAddress)
                .port(m_Configuration.port).file("/http-bind/")
                // .sslContext(getTrustAllSslContext())
                .secure(false).build();

        m_XmppConfiguration = XmppSessionConfiguration.builder().debugger(ConsoleDebugger.class)
                .extensions(Extension.of("http://abb.com/protocol/update", null, true, true, Update.class),
                        Extension.of("http://abb.com/protocol/update", null, true, Update.class))
                .build();

        m_XmppClient = new XmppClient("busch-jaeger.de", m_XmppConfiguration, m_BoshConfiguration);

        // Listen for inbound messages.
        m_XmppClient.addInboundMessageListener(e -> logger.info("Received: " + e.getMessage()));
        m_XmppClient.addInboundMessageListener(e -> onMessageEvent(e));
        m_XmppClient.addOutboundMessageListener(e -> onMessageEvent(e));
        m_XmppClient.addInboundIQListener(e -> logger.info("Received IQ: " + e.toString()));
        m_XmppClient.addInboundIQListener(e -> onIQEvent(e));

        // Listen for inbound presence.
        m_XmppClient.addInboundPresenceListener(e -> logger.info("Received: " + e.getPresence()));
        m_XmppClient.addInboundPresenceListener(e -> onPresenceEvent(e));

        m_XmppClient.addSessionStatusListener(e -> onUpdateXMPPStatus(e));

        PubSubManager pubSubManager = m_XmppClient.getManager(PubSubManager.class);
        pubSubManager.setEnabled(true);

        UpdateManager updateManager = m_XmppClient.getManager(UpdateManager.class);
        updateManager.setEnabled(true);

        updateManager.addUpdateListener(e -> onUpdateEvent(e));

        // Connect
        try {
            m_XmppClient.connect();
        } catch (XmppException e1) {
            onConnectionLost(ThingStatusDetail.COMMUNICATION_ERROR);
            logger.error("Can not connect to IP gateway");
            logger.error(e1.getMessage());
            e1.printStackTrace();
            return;
        }

        // Login
        try {
            m_XmppClient.login(m_Configuration.login, m_Configuration.password);
        } catch (XmppException e1) {
            onConnectionLost(ThingStatusDetail.CONFIGURATION_ERROR);
            logger.error("Can not login with: " + m_Configuration.login);
            logger.error(e1.getMessage());
            e1.printStackTrace();
            return;
        }

        // updateManager.publish(Update.builder().data("Test").build());

        // Send a message to myself, which is caught by the listener above.
        // xmppClient
        // .send(new Message(xmppClient.getConnectedResource(), Message.Type.CHAT, "Hello World! Echo!"));

        // Jid j = new Jid("mrha", "busch-jaeger.de", "rpc");

        // PubSubService personalEventingService = pubSubManager.createPersonalEventingService();

        // PubSubNode pubSubNode = personalEventingService.node(Update.NAMESPACE);
        // try {
        // pubSubNode.publish(Update.builder().data("Test").build());
        // } catch (XmppException e2) {
        // // TODO Auto-generated catch block
        // e2.printStackTrace();
        // }

        m_RpcManager = m_XmppClient.getManager(RpcManager.class);

        // Write to System.out for debugging
        // m.marshal(emp, System.out);

        // Write to File

        // m_XmppClient.getManager(PresenceManager.class).requestSubscription(j, "");
        // m_XmppClient.getManager(PresenceManager.class).approveSubscription(j);

        // Presence presence = new Presence(j);

        // EntityCapabilities c = new EntityCapabilities("http://gonicus.de/caps", "", "1.0");
        // presence.addExtension(c);

        // logger.debug(c.getVerificationString());

        // m_XmppClient.send(presence);

        // try {
        // InfoNode result = m_XmppClient.getManager(EntityCapabilitiesManager.class).discoverCapabilities(j);
        // } catch (XmppException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }

        // m_XmppClient.getActiveConnection().getXmppSession().getManager(PresenceManager.class)
        // .requestSubscription(Jid.of("mrha@busch-jaeger.de/rpc"), "");

        // Notify on connection established

        onConnectionEstablished();

    }

    private void onConnectionEstablished() {
        logger.debug("Bridge connected. Updating thing status to ONLINE.");
        updateStatus(ThingStatus.ONLINE);
    }

    private void onConnectionLost(ThingStatusDetail detail) {
        logger.debug("Bridge connection lost. Updating thing status to OFFLINE.");
        updateStatus(ThingStatus.OFFLINE, detail);
    }

    private void onUpdateXMPPStatus(SessionStatusEvent e) {
        logger.debug(e.toString());
        if (e != null && e.getStatus() == XmppSession.Status.DISCONNECTED) {
            onConnectionLost(ThingStatusDetail.BRIDGE_OFFLINE);
        }
    }

    private void onPresenceEvent(PresenceEvent e) {
        Presence presence = e.getPresence();
        // EntityCapabilities c = presence.getExtension(EntityCapabilities.class);
        if (presence.getType() == Presence.Type.SUBSCRIBE) {
            logger.debug(presence.toString());
        }
        m_XmppClient.getManager(PresenceManager.class).approveSubscription(presence.getFrom());
        logger.debug(presence.toString());

    }

    /**
     * When an XMPP message is received from the bridge. It will be parsed to an XML string.
     *
     * TODO can be used to react on pressed switches and status update of raffstores.
     *
     * @param e
     */
    private void onMessageEvent(MessageEvent e) {
        Message message = e.getMessage();
        logger.debug(message.getBody());
        Event event = message.getExtension(Event.class);
        logger.debug(event.toString());
        if (event != null) {
            if (Update.NAMESPACE.equals(event.getNode())) {
                for (Item item : event.getItems()) {

                    logger.debug(item.getPayload().toString());

                    if (item.getPayload() instanceof org.openhab.binding.freeathome.xmpp.rocks.extension.abb.com.protocol.update.Update) {
                        org.openhab.binding.freeathome.xmpp.rocks.extension.abb.com.protocol.update.Update updateData = (org.openhab.binding.freeathome.xmpp.rocks.extension.abb.com.protocol.update.Update) item
                                .getPayload();
                        String data = updateData.getData().replace("&amp;", "&").replace("&apos;", "'")
                                .replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"");
                        logger.debug("UpdateEvent" + data);

                        try {
                            Project p = org.openhab.binding.freeathome.xmpp.rocks.extensions.abb.com.protocol.data.Project
                                    .builder().build(data);

                            // create JAXB context and instantiate marshaller
                            JAXBContext context = JAXBContext.newInstance(
                                    org.openhab.binding.freeathome.xmpp.rocks.extensions.abb.com.protocol.data.Project.class);
                            Marshaller m = context.createMarshaller();
                            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                            // Write to File
                            // m.marshal(p, new File("/tmp/update_" + counter + ".xml"));
                            counter++;

                        } catch (JAXBException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                        // ...
                    }
                }
            }
        }
    }

    private void onIQEvent(IQEvent e) {
        logger.debug(e.toString());
    }

    private void onUpdateEvent(UpdateEvent e) {
        logger.debug("UpdateEvent:" + e.toString());
    }

}
