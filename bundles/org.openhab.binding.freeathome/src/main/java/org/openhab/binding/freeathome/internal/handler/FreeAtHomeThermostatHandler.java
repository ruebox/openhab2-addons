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

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.freeathome.internal.FreeAtHomeBindingConstants;
import org.openhab.binding.freeathome.internal.FreeAtHomeUpdateChannel;
import org.openhab.binding.freeathome.internal.config.FreeAtHomeThermostatConfig;
import org.openhab.binding.freeathome.internal.internal.stateconvert.DefaultDecimalTypeConverter;
import org.openhab.binding.freeathome.internal.internal.stateconvert.DefaultOnOffTypeConverter;
import org.openhab.binding.freeathome.internal.internal.stateconvert.StateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FreeAtHomeThermostatHandler} represents thermostat
 *
 * @author kjoglum - Initial contribution
 */
public class FreeAtHomeThermostatHandler extends FreeAtHomeBaseHandler {

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
            return;
        }

        // Values
        if (command instanceof DecimalType) {

            // Switch on/off
            if (channelUID.getId().equals(FreeAtHomeBindingConstants.THERMOSTAT_TARGET_TEMP_THING_CHANNEL)) {
                DecimalType udCommand = (DecimalType) command;
                String channel = m_Configuration.deviceId + "/" + m_Configuration.channelId + "/"
                        + m_Configuration.dataPointIdTarget;
                String targetTemperature = "Set target temperature: " + channel + " value(" + udCommand.toString()
                        + ")";
                logger.debug("Set target temperature {}", targetTemperature);
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

                    logger.debug("Thermostat Switch ON {}", channel);
                    bridge.setDataPoint(channel, "1");

                }
                if (udCommand.equals(OnOffType.OFF)) {

                    logger.debug("Thermostat Switch OFF {}", channel);
                    bridge.setDataPoint(channel, "0");

                }
            }
            // Switch eco on/off
            if (channelUID.getId().equals(FreeAtHomeBindingConstants.THERMOSTAT_ECO_THING_CHANNEL)) {
                OnOffType udCommand = (OnOffType) command;
                String channel = m_Configuration.deviceId + "/" + m_Configuration.channelId + "/"
                        + m_Configuration.dataPointIdEco;

                if (udCommand.equals(OnOffType.ON)) {

                    logger.debug("Thermostat ECO switch ON {}", channel);
                    bridge.setDataPoint(channel, "1");

                }
                if (udCommand.equals(OnOffType.OFF)) {

                    logger.debug("Thermostat ECO switch OFF {}", channel);
                    bridge.setDataPoint(channel, "0");

                }
            }
        }
    }

    @Override
    public void tearDown() {

    }

    @Override
    public void setUp() {
        m_Configuration = getConfigAs(FreeAtHomeThermostatConfig.class);

        // Fetch bridge on initialization to get proper state
        FreeAtHomeBridgeHandler bridge = getFreeAtHomeBridge();
        if (bridge != null) {
            // Target temperature update
            class TargetTempDecimalTypeConverter implements StateConverter {
                @Override
                public State convert(String value) {
                    if (value.equals("35")) // reported if thermostat is switched off
                    {
                        return new DecimalType(Double.NaN);
                    } else {
                        return new DecimalType(value);
                    }
                }

            }

            m_UpdateChannels.add(
                    new FreeAtHomeUpdateChannel(this, FreeAtHomeBindingConstants.THERMOSTAT_TARGET_TEMP_THING_CHANNEL,
                            new TargetTempDecimalTypeConverter(), m_Configuration.deviceId, m_Configuration.channelId,
                            m_Configuration.dataPointIdTargetUpdate));

            // Room temperature update
            m_UpdateChannels.add(new FreeAtHomeUpdateChannel(this,
                    FreeAtHomeBindingConstants.THERMOSTAT_ROOM_TEMP_THING_CHANNEL, new DefaultDecimalTypeConverter(),
                    m_Configuration.deviceId, m_Configuration.channelId, m_Configuration.dataPointIdRoomUpdate));

            // Switch on/off update
            m_UpdateChannels.add(new FreeAtHomeUpdateChannel(this,
                    FreeAtHomeBindingConstants.THERMOSTAT_SWITCH_THING_CHANNEL, new DefaultOnOffTypeConverter(),
                    m_Configuration.deviceId, m_Configuration.channelId, m_Configuration.dataPointIdSwitchUpdate));

            // Switch eco on/off
            // Nested class for eco switch
            class EcoModeOnOffTypeConverter implements StateConverter {
                @Override
                public State convert(String value) {
                    if (value.equals("68")) {
                        return OnOffType.ON;
                    } else { // expected "65"
                        return OnOffType.OFF;
                    }
                }
            }
            m_UpdateChannels.add(new FreeAtHomeUpdateChannel(this,
                    FreeAtHomeBindingConstants.THERMOSTAT_ECO_THING_CHANNEL, new EcoModeOnOffTypeConverter(),
                    m_Configuration.deviceId, m_Configuration.channelId, m_Configuration.dataPointIdEcoUpdate));

        } // dummy call to avoid optimization
    }

}