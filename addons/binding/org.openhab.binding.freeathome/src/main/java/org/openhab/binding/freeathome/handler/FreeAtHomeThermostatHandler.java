/**
 * Copyright (c) 2014-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.freeathome.handler;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.freeathome.FreeAtHomeBindingConstants;
import org.openhab.binding.freeathome.config.FreeAtHomeThermostatConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FreeAtHomeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author ruebox - Initial contribution
 */
public class FreeAtHomeThermostatHandler extends BaseThingHandler {

    public FreeAtHomeThermostatHandler(Thing thing) {
        super(thing);

    }

    private Logger logger = LoggerFactory.getLogger(FreeAtHomeThermostatHandler.class);

    private FreeAtHomeThermostatConfig m_Configuration;

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        FreeAtHomeBridgeHandler bridge = getFreeAtHomeBridge();

        if (bridge == null) {
            logger.error("No bridge connected");
            updateStatus(ThingStatus.OFFLINE);
            return;
        }

        if (command instanceof RefreshType) {
            if (bridge != null) {
                updateStatus(ThingStatus.ONLINE);
            }
            return;
        }

        // Values
        if (command instanceof DecimalType) {

            // Switch on/off
            if (channelUID.getId().equals(FreeAtHomeBindingConstants.THERMOSTAT_TARGET_TEMP_THING_CHANNEL)) {
                DecimalType udCommand = (DecimalType) command;
                String channel = m_Configuration.deviceId + "/" + m_Configuration.channelId + "/"
                        + m_Configuration.dataPointIdTarget;

                logger.debug("Set target temperature: " + channel + "   value(" + udCommand.toString() + ")");
                bridge.setDataPoint(channel, udCommand.toString());
            }
        }
        /*
         * OnOff
         */
        if (command instanceof OnOffType) {

            // Switch on/off
            if (channelUID.getId().equals(FreeAtHomeBindingConstants.THERMOSTAT_SWITCH_THING_CHANNEL)) {
                OnOffType udCommand = (OnOffType) command;
                String channel = m_Configuration.deviceId + "/" + m_Configuration.channelId + "/"
                        + m_Configuration.dataPointIdSwitch;

                if (udCommand.equals(OnOffType.ON)) {

                    logger.debug("Thermostat: Switch on " + channel);
                    bridge.setDataPoint(channel, "1");

                }
                if (udCommand.equals(OnOffType.OFF)) {

                    logger.debug("Thermostat: Switch off " + channel);
                    bridge.setDataPoint(channel, "0");

                }
            }
            // Switch eco on/off
            if (channelUID.getId().equals(FreeAtHomeBindingConstants.THERMOSTAT_ECO_THING_CHANNEL)) {
                OnOffType udCommand = (OnOffType) command;
                String channel = m_Configuration.deviceId + "/" + m_Configuration.channelId + "/"
                        + m_Configuration.dataPointIdEco;

                if (udCommand.equals(OnOffType.ON)) {

                    logger.debug("Thermostat: Eco switch on " + channel);
                    bridge.setDataPoint(channel, "1");

                }
                if (udCommand.equals(OnOffType.OFF)) {

                    logger.debug("Thermostat: Eco switch off " + channel);
                    bridge.setDataPoint(channel, "0");

                }
            }
        }
    }

    @Override
    public void initialize() {
        m_Configuration = getConfigAs(FreeAtHomeThermostatConfig.class);

        if (getFreeAtHomeBridge() == null) {
            updateStatus(ThingStatus.OFFLINE);
            return;
        }

        updateStatus(ThingStatus.ONLINE);
    }

    private FreeAtHomeBridgeHandler getFreeAtHomeBridge() {
        Bridge bridge = getBridge();
        if (bridge == null) {
            return null;
        }

        ThingHandler handler = bridge.getHandler();
        if (handler instanceof FreeAtHomeBridgeHandler) {
            return (FreeAtHomeBridgeHandler) handler;
        }
        return null;
    }

}