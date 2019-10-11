/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.freeathome.internal.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openhab.binding.freeathome.internal.FreeAtHomeUpdateHandler;
import org.openhab.binding.freeathome.internal.config.FreeAtHomeBridgeConfig;
import org.openhab.binding.freeathome.internal.xmpp.rocks.extension.abb.com.protocol.update.Update;
import org.openhab.binding.freeathome.internal.xmpp.rocks.extension.abb.com.protocol.update.UpdateEvent;
import org.openhab.binding.freeathome.internal.xmpp.rocks.extension.abb.com.protocol.update.UpdateManager;
import org.openhab.binding.freeathome.internal.xmpp.rocks.extensions.abb.com.protocol.data.Channel;
import org.openhab.binding.freeathome.internal.xmpp.rocks.extensions.abb.com.protocol.data.DataPoint;
import org.openhab.binding.freeathome.internal.xmpp.rocks.extensions.abb.com.protocol.data.Device;
import org.openhab.binding.freeathome.internal.xmpp.rocks.extensions.abb.com.protocol.data.Project;
import org.slf4j.LoggerFactory;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.net.ChannelEncryption;
import rocks.xmpp.core.session.Extension;
import rocks.xmpp.core.session.SessionStatusEvent;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.session.XmppSessionConfiguration;
import rocks.xmpp.core.session.debug.ConsoleDebugger;
import rocks.xmpp.core.stanza.IQEvent;
import rocks.xmpp.core.stanza.MessageEvent;
import rocks.xmpp.core.stanza.PresenceEvent;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.extensions.commands.model.Command;
import rocks.xmpp.extensions.pubsub.PubSubManager;
import rocks.xmpp.extensions.pubsub.model.Item;
import rocks.xmpp.extensions.pubsub.model.event.Event;
import rocks.xmpp.extensions.rpc.RpcManager;
import rocks.xmpp.extensions.rpc.model.Value;
import rocks.xmpp.im.subscription.PresenceManager;
import rocks.xmpp.websocket.net.client.WebSocketConnectionConfiguration;

/**
 * Handler that connects to FreeAtHome gateway.
 *
 * @author kjoglum - Initial contribution
 *
 */
public class FreeAtHomeBridgeHandler extends BaseBridgeHandler {

    public static FreeAtHomeBridgeHandler g_freeAtHomeBridgeHandler = null;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(FreeAtHomeBridgeHandler.class);

    private WebSocketConnectionConfiguration m_WebSocketConfiguration;
    private XmppSessionConfiguration m_XmppConfiguration;
    private XmppClient m_XmppClient = null;
    private RpcManager m_RpcManager;
    private int counter = 0;

    /*
     * Store bridge configuration
     */
    protected FreeAtHomeBridgeConfig m_Configuration;

    public FreeAtHomeUpdateHandler m_UpdateHandler;

    public FreeAtHomeBridgeHandler(Bridge bridge) {
        super(bridge);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void initialize() {

        FreeAtHomeBridgeConfig configuration = getConfigAs(FreeAtHomeBridgeConfig.class);

        m_Configuration = configuration;

        logger.debug("Gateway IP            {}.", m_Configuration.host);
        logger.debug("Port                  {}.", m_Configuration.port);
        logger.debug("Login                 {}.", m_Configuration.login);
        logger.debug("Password              {}.", m_Configuration.password);
        logger.debug("log_dir               {}.", m_Configuration.log_dir);
        logger.debug("dummy_things_enabled  {}.", m_Configuration.dummy_things_enabled);

        m_UpdateHandler = new FreeAtHomeUpdateHandler();

        connectGateway();

        if (m_Configuration.log_enabled) {
            try {
                String xml = this.getAll();
                this.writeStringToFile(xml, m_Configuration.log_dir + "/getAll.xml");
            } catch (Exception e) {
                logger.warn(
                        "Could not successfully get the getAll.xml from sysAP. Can happen if connecting to server takes too long.");
            }
        }

        g_freeAtHomeBridgeHandler = this;
        // TODO iterate over things and register update

    }

    public boolean dummyThingsEnabled() {
        return m_Configuration.dummy_things_enabled;
    }

    @Override
    public void dispose() {

        onConnectionLost(ThingStatusDetail.CONFIGURATION_ERROR, "Bridge removed");

        m_UpdateHandler = null;

        try {
            m_XmppClient.close();
        } catch (XmppException e) {
            logger.error("Can not close XMPP Client");
            logger.error("Message {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Can not close XMPP Client");
            logger.error("Message {}", e.getMessage());
        }

    };

    // @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        try {
            handleCommand(channelUID, command);
        } catch (Exception e) {
            logger.warn("No bridge commands defined. Cannot process '{}'.", command.toString());
        }
    }

    /*
     * Call set data point via XMPP service
     */
    public void setDataPoint(String adress, String value) {

        try {

            Value response = m_RpcManager.call(Jid.of("mrha@busch-jaeger.de/rpc"), "RemoteInterface.setDatapoint",
                    Value.of(adress), Value.of(value)).getResult();
            logger.debug("Datapoint Result {}", response.getAsString());
            response = null;

        } catch (XmppException e) {
            logger.debug("XMPP Exception{}", e.getMessage());
            // E.g. a StanzaException, if the responder does not support the protocol or an
            // // internal-server-error has occurred.
        }
    }

    public String getAll() {
        try {

            Value response = m_RpcManager.call(Jid.of("mrha@busch-jaeger.de/rpc"), "RemoteInterface.getAll",
                    Value.of("de"), Value.of(4), Value.of(0), Value.of(0)).getResult();
            logger.debug("Message {} ", response.getAsString());
            String resp = response.getAsString();

            return resp;

        } catch (XmppException e) {
            logger.debug("XMPP Exception{} ", e.getMessage());
            // E.g. a StanzaException, if the responder does not support the protocol or an
            // internal-server-error has occurred.
        }
        return null;
    }

    private void connectGateway() {

        // If old session is still connected -> close xmpp session
        if (m_XmppClient != null) {
            try {
                m_XmppClient.close();
            } catch (XmppException e1) {
                logger.error("Error {}", e1.getMessage());
            }
        }

        m_WebSocketConfiguration = WebSocketConnectionConfiguration.builder().hostname(m_Configuration.host)
                .port(m_Configuration.port).path("/xmpp-websocket/").channelEncryption(ChannelEncryption.DISABLED)
                .build();

        m_XmppConfiguration = XmppSessionConfiguration.builder().debugger(ConsoleDebugger.class)
                .extensions(Extension.of("http://abb.com/protocol/update", null, true, true, Update.class),
                        Extension.of("http://abb.com/protocol/update", null, true, Update.class))
                .build();

        m_XmppClient = XmppClient.create("busch-jaeger.de", m_XmppConfiguration, m_WebSocketConfiguration);

        // Listen for inbound messages.
        m_XmppClient.addInboundMessageListener(e -> logger.debug("Received Message {} ", e.getMessage()));
        m_XmppClient.addInboundMessageListener(e -> onMessageEvent(e));
        m_XmppClient.addOutboundMessageListener(e -> onMessageEvent(e));
        m_XmppClient.addInboundIQListener(e -> logger.debug("Received IQ {} ", e.toString()));
        m_XmppClient.addInboundIQListener(e -> onIQEvent(e));

        // Listen for inbound presence.
        m_XmppClient.addInboundPresenceListener(e -> logger.debug("Received Presence {} ", e.getPresence()));
        m_XmppClient.addInboundPresenceListener(e -> onPresenceEvent(e));

        m_XmppClient.addSessionStatusListener(e -> onUpdateXMPPStatus(e));

        PubSubManager pubSubManager = m_XmppClient.getManager(PubSubManager.class);
        pubSubManager.setEnabled(true);

        UpdateManager updateManager = m_XmppClient.getManager(UpdateManager.class);
        updateManager.setEnabled(true);

        updateManager.addUpdateListener(e -> onUpdateEvent(e));
        updateManager.addUpdateListener(e -> logger.debug("Received UpdateEvent "));

        // Connect XMPP client over websocket layer
        try {
            Jid From = Jid.of(getJid(m_Configuration.login));
            // Jid To = Jid.of("busch-jaeger.de");

            try {
                m_XmppClient.connect(From);
            } catch (XmppException e1) {
                onConnectionLost(ThingStatusDetail.COMMUNICATION_ERROR,
                        "Can not connect to SysAP with address: " + m_Configuration.host);
                return;
            }
        } catch (Exception e3) {
            logger.debug("Warning {}", e3.toString());
        }

        // Login
        try {
            // Extract jID
            String jid = this.getJid(m_Configuration.login);
            String id = jid.split("@")[0];
            m_XmppClient.login(id, m_Configuration.password);

            Presence presence = new Presence(Jid.of("mrha@busch-jaeger.de/rpc"), Presence.Type.SUBSCRIBE, null, null,
                    null, null, Jid.of(jid), null, null, null);
            logger.debug("Presence update {}", presence.toString());
            m_XmppClient.send(presence);

        } catch (XmppException e1) {
            onConnectionLost(ThingStatusDetail.CONFIGURATION_ERROR,
                    "Login on SysAP with login name: " + m_Configuration.login);
            logger.error("Can not login with {}", m_Configuration.login);
            try {
                m_XmppClient.close();
            } catch (XmppException e2) {
                // TODO Auto-generated catch block
                logger.error("Ops!", e2);
            }
            return;
        } catch (Exception e) {
            onConnectionLost(ThingStatusDetail.CONFIGURATION_ERROR,
                    "Login on SysAP with login name: " + m_Configuration.login + " (Login name could not be resolved)");
            try {
                m_XmppClient.close();
            } catch (XmppException e1) {
                // TODO Auto-generated catch block
                logger.error("Ops!", e1);
            }
        }

        m_RpcManager = m_XmppClient.getManager(RpcManager.class);

        Presence presence = new Presence();
        m_XmppClient.send(presence);

        onConnectionEstablished();

    }

    private String getJid(String userName) throws Exception {

        /*
         * Read settings.json from SysAP
         */
        String url = "http://" + m_Configuration.host + "/settings.json"; // settings stores mapping to jid

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");
        String USER_AGENT = "Mozilla/5.0";
        // add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("Sending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        if (m_Configuration.log_enabled) {
            this.writeStringToFile(response.toString(), m_Configuration.log_dir + "/settings.json");
        }

        /*
         * Parse json to find mapping to jid
         */
        Object o = new JSONParser().parse(response.toString());

        JSONObject jo = (JSONObject) o;

        JSONArray ja = (JSONArray) jo.get("users");
        Iterator itr2 = ja.iterator();

        String foundJid = "";

        while (itr2.hasNext()) {
            JSONObject currentUser = ((JSONObject) itr2.next());
            String login = (String) currentUser.get("name");
            String jid = (String) currentUser.get("jid");
            String loginName = login + " with the current JID: " + jid;
            if (login.equals(userName)) {
                foundJid = jid;
            }
        }
        String loginMatch = "Matching JID for login: " + userName + " & " + foundJid;
        logger.info("Login Match {}", loginMatch);

        return foundJid;

    }

    private void onConnectionEstablished() {
        logger.warn("Bridge connected. Updating thing status to ONLINE.");
        updateStatus(ThingStatus.ONLINE);
    }

    private void onConnectionLost(ThingStatusDetail detail, String msg) {
        logger.warn("Bridge connection lost. Updating thing status to OFFLINE.");
        updateStatus(ThingStatus.OFFLINE, detail, msg);
    }

    private void onUpdateXMPPStatus(SessionStatusEvent e) {
        if (e.getStatus() == XmppClient.Status.DISCONNECTED) {
            onConnectionLost(ThingStatusDetail.BRIDGE_OFFLINE, "XMPP connection lost");
            logger.warn("Connection lost {}", e.toString());
        }

        if (e.getStatus() == XmppClient.Status.AUTHENTICATED) {
            onConnectionEstablished();
            logger.warn("Connection authenticated");
        }
    }

    /*
     * XMPP Event handlers
     */
    private void onPresenceEvent(PresenceEvent e) {
        Presence presence = e.getPresence();
        // EntityCapabilities c = presence.getExtension(EntityCapabilities.class);
        if (presence.getType() == Presence.Type.SUBSCRIBE) {
            logger.debug("Presence {}", presence.toString());
        }
        m_XmppClient.getManager(PresenceManager.class).approveSubscription(presence.getFrom());
        logger.debug("Presence {}", presence.toString());

    }

    /**
     * When an XMPP message is received from the bridge. It will be parsed to an XML string.
     *
     * TODO can be used to react on pressed switches and status update of raffstores.
     *
     * @param e
     */
    private void onMessageEvent(MessageEvent e) {
        logger.debug("MessageEvent Handler called");
        Message message = e.getMessage();
        Event event = message.getExtension(Event.class);

        // Open update log file
        BufferedWriter bw = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

        if (this.m_Configuration.log_enabled) {
            try {
                bw = new BufferedWriter(new FileWriter(this.m_Configuration.log_dir + "/update.csv", true));
            } catch (IOException e3) {
                // TODO Auto-generated catch block
                logger.error("Ops!", e3);
                logger.error("Error {}", e3.getMessage());
            } catch (Exception ex) {
                logger.error("Ops!", ex);
                logger.error("Error {}", ex.getMessage());
            }
        }

        logger.debug("Namespace {}", event.getNode());
        if (Update.NAMESPACE.equals(event.getNode())) {
            for (Item item : event.getItems()) {

                logger.debug("Payload of Item {}", item.getPayload().toString());

                if (item.getPayload() instanceof org.openhab.binding.freeathome.internal.xmpp.rocks.extension.abb.com.protocol.update.Update) {
                    org.openhab.binding.freeathome.internal.xmpp.rocks.extension.abb.com.protocol.update.Update updateData = (org.openhab.binding.freeathome.internal.xmpp.rocks.extension.abb.com.protocol.update.Update) item
                            .getPayload();
                    String data = updateData.getData().replace("&amp;", "&").replace("&apos;", "'").replace("&lt;", "<")
                            .replace("&gt;", ">").replace("&quot;", "\"");
                    logger.debug("UpdateEvent {}", data);

                    try {
                        Project p = org.openhab.binding.freeathome.internal.xmpp.rocks.extensions.abb.com.protocol.data.Project
                                .builder().build(data);

                        // create JAXB context and instantiate marshaller
                        JAXBContext context = JAXBContext.newInstance(
                                org.openhab.binding.freeathome.internal.xmpp.rocks.extensions.abb.com.protocol.data.Project.class);
                        Marshaller m = context.createMarshaller();
                        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                        // Write to File
                        if (this.m_Configuration.log_enabled) {
                            m.marshal(p, new File(this.m_Configuration.log_dir + "/update_" + counter + ".xml"));
                            counter++;
                        }

                        try {
                            List<Device> devices = p.getDevices();
                            for (int i = 0; i < devices.size(); i++) {
                                Device currentDevice = devices.get(i);

                                logger.debug("Update From {}", currentDevice.getSerialNumber());
                                List<Channel> channels = currentDevice.getChannels();

                                for (int j = 0; j < channels.size(); j++) {
                                    Channel channel = channels.get(j);

                                    /*
                                     * Outputs
                                     */
                                    List<DataPoint> dataPointsOut = channel.getOutputs();
                                    for (int d = 0; d < dataPointsOut.size(); d++) {
                                        DataPoint datapoint = dataPointsOut.get(d);
                                        String dataPoint = "Serial: " + currentDevice.getSerialNumber() + " Channel: "
                                                + channel.getI() + " DataPoint: " + datapoint.getI() + " Value: "
                                                + datapoint.getValue();
                                        logger.debug("Datapoint {}", dataPoint);

                                        if (bw != null && this.m_Configuration.log_enabled) {
                                            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                            bw.write(sdf.format(timestamp) + " ; " + currentDevice.getSerialNumber()
                                                    + " ; " + channel.getI() + " ; " + datapoint.getI() + " ; "
                                                    + datapoint.getValue() + " ; ");
                                            bw.newLine();
                                            bw.flush();
                                        }

                                        m_UpdateHandler.NotifyThing(currentDevice.getSerialNumber(), channel.getI(),
                                                datapoint.getI(), datapoint.getValue());
                                    }
                                }
                            }

                        } catch (Exception e2) {
                            logger.error("Ops!", e2);
                            logger.error("Exception {}", e2.getMessage());
                        }

                    } catch (JAXBException e1) {
                        // TODO Auto-generated catch block
                        logger.error("Ops!", e1);
                        logger.error("JaxbException {}", e1.getMessage());
                    } catch (Exception ex) {
                        logger.error("General Exception {}", ex.getMessage());
                    }

                    // ...
                } else {
                    logger.debug("Payload is not instance of extension.abb.com.protocol.update.Update");
                }
            }
        } else {
            logger.debug("Message does not have namespace" + Update.NAMESPACE);
        }

        if (bw != null) {
            try {
                bw.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                logger.error("Ops!", e1);
            }
        }

    }

    private void onIQEvent(IQEvent e) {
        logger.debug("IQEvent Handler called");
    }

    private void onUpdateEvent(UpdateEvent e) {
        logger.debug("UpdateEvent {}", e.toString());
    }

    private void writeStringToFile(String content, String file) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file));
            out.write(content); // Replace with the string
                                // you are trying to write
        } catch (IOException e) {
            System.out.println("Exception ");

        } finally {
            try {
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                logger.error("Ops!", e);
            }
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, org.eclipse.smarthome.core.types.Command command) {
        // TODO Auto-generated method stub

    }

}
