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

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.freeathome.internal.FreeAtHomeBindingConstants;
import org.openhab.binding.freeathome.internal.FreeAtHomeUpdateChannel;
import org.openhab.binding.freeathome.internal.config.FreeAtHomeSwitchConfig;
import org.openhab.binding.freeathome.internal.internal.stateconvert.DefaultOnOffTypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FreeAtHomeSwitchHandler} represents binary switch or group
 *
 * @author kjoglum - Initial contribution
 */

public class FreeAtHomeSwitchHandler extends FreeAtHomeBaseHandler {

    public FreeAtHomeSwitchHandler(Thing thing) {
        super(thing);

    }

    private Logger logger = LoggerFactory.getLogger(FreeAtHomeSwitchHandler.class);

    private FreeAtHomeSwitchConfig m_Configuration;

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

            if (channelUID.getId().equals(FreeAtHomeBindingConstants.SWITCH_THING_CHANNEL)) {
                OnOffType udCommand = (OnOffType) command;
                String channel = m_Configuration.deviceId + "/" + m_Configuration.channelId + "/"
                        + m_Configuration.dataPointId;

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
    }

    @Override
    public void tearDown() {

    }

    @Override
    public void setUp() {
        m_Configuration = getConfigAs(FreeAtHomeSwitchConfig.class);

        // Fetch bridge on initialization to get proper state
        FreeAtHomeBridgeHandler bridge = getFreeAtHomeBridge();
        if (bridge != null) {
            m_UpdateChannels.add(new FreeAtHomeUpdateChannel(this, FreeAtHomeBindingConstants.SWITCH_THING_CHANNEL,
                    new DefaultOnOffTypeConverter(), m_Configuration.deviceId, m_Configuration.channelId,
                    m_Configuration.dataPointIdUpdate));
        } // dummy call to avoid optimization
    }

}