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
import org.eclipse.smarthome.core.library.types.StopMoveType;
import org.eclipse.smarthome.core.library.types.UpDownType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.freeathome.internal.FreeAtHomeBindingConstants;
import org.openhab.binding.freeathome.internal.FreeAtHomeUpdateChannel;
import org.openhab.binding.freeathome.internal.config.FreeAtHomeDimmerConfig;
import org.openhab.binding.freeathome.internal.internal.stateconvert.DefaultOnOffTypeConverter;
import org.openhab.binding.freeathome.internal.internal.stateconvert.DefaultPercentTypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FreeAtHomeDimmerHandler} represents a dimmer
 *
 * @author kjoglum - Initial contribution
 */

public class FreeAtHomeDimmerHandler extends FreeAtHomeBaseHandler {

    public FreeAtHomeDimmerHandler(Thing thing) {
        super(thing);

    }

    private Logger logger = LoggerFactory.getLogger(FreeAtHomeDimmerHandler.class);

    private FreeAtHomeDimmerConfig m_Configuration;

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        FreeAtHomeBridgeHandler bridge = getFreeAtHomeBridge();

        if (bridge == null) {
            logger.error("No bridge connected");
            return;
        }

        /*
         * UpDownCommand
         */
        if (command instanceof OnOffType) {

            if (channelUID.getId().equals(FreeAtHomeBindingConstants.DIMMER_SWITCH_THING_CHANNEL)) {
                OnOffType udCommand = (OnOffType) command;
                String channel = m_Configuration.deviceId + "/" + m_Configuration.channelId + "/"
                        + m_Configuration.dataPointIdSwitch;

                if (udCommand.equals(OnOffType.ON)) {

                    logger.debug("Switch ON {}", channel);
                    bridge.setDataPoint(channel, "1");

                }
                if (udCommand.equals(OnOffType.OFF)) {

                    logger.debug("Switch OFF {}", channel);
                    bridge.setDataPoint(channel, "0");

                }
            }
        }

        /*
         * Stop Move
         */
        if (command instanceof StopMoveType
                && channelUID.getId().equals(FreeAtHomeBindingConstants.DIMMER_FADING_THING_CHANNEL)) {
            StopMoveType udCommand = (StopMoveType) command;
            if (udCommand.equals(StopMoveType.STOP)) {
                String channel = m_Configuration.deviceId + "/" + m_Configuration.channelId + "/"
                        + m_Configuration.dataPointIdFade;
                String raffstoreSwitch = "Fading STOP " + channel + " 0";
                logger.debug("Event {}", raffstoreSwitch);
                bridge.setDataPoint(channel, "0");
            }
        }

        /*
         * UpDownCommand
         */
        if (command instanceof UpDownType) {
            UpDownType udCommand = (UpDownType) command;

            if (channelUID.getId().equals(FreeAtHomeBindingConstants.DIMMER_FADING_THING_CHANNEL)) {
                String channel = m_Configuration.deviceId + "/" + m_Configuration.channelId + "/"
                        + m_Configuration.dataPointIdFade;

                logger.info("Called channel fading {}", channel);

                if (udCommand.equals(UpDownType.UP)) {
                    logger.debug("Fading UP");
                    bridge.setDataPoint(channel, "9");
                } else {
                    logger.debug("Fading DOWN");
                    bridge.setDataPoint(channel, "1");
                }
            } // stepwise
        }

        /*
         * Value
         */
        if (command instanceof DecimalType) {

            // Switch on/off
            if (channelUID.getId().equals(FreeAtHomeBindingConstants.DIMMER_VALUE_THING_CHANNEL)) {
                DecimalType udCommand = (DecimalType) command;
                String channel = m_Configuration.deviceId + "/" + m_Configuration.channelId + "/"
                        + m_Configuration.dataPointIdValue;

                String dimmerSwitch = "Set target value: " + channel + " value(" + udCommand.toString() + ")";
                logger.debug("Target Value {}", dimmerSwitch);
                bridge.setDataPoint(channel, udCommand.toString());
            }
        }
    }

    @Override
    public void tearDown() {

    }

    @Override
    public void setUp() {
        m_Configuration = getConfigAs(FreeAtHomeDimmerConfig.class);

        // Fetch bridge on initialization to get proper state
        FreeAtHomeBridgeHandler bridge = getFreeAtHomeBridge();
        if (bridge != null) {

            m_UpdateChannels.add(new FreeAtHomeUpdateChannel(this,
                    FreeAtHomeBindingConstants.DIMMER_SWITCH_THING_CHANNEL, new DefaultOnOffTypeConverter(),
                    m_Configuration.deviceId, m_Configuration.channelId, m_Configuration.dataPointIdSwitchUpdate));

            m_UpdateChannels.add(new FreeAtHomeUpdateChannel(this,
                    FreeAtHomeBindingConstants.DIMMER_VALUE_THING_CHANNEL, new DefaultPercentTypeConverter(),
                    m_Configuration.deviceId, m_Configuration.channelId, m_Configuration.dataPointIdValueUpdate));
        } // dummy call to avoid optimization
    }

}
