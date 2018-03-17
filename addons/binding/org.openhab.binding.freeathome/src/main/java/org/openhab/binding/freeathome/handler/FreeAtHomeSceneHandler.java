/**
 * Copyright (c) 2014-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.freeathome.handler;

import static org.openhab.binding.freeathome.FreeAtHomeBindingConstants.SCENE_THING_CHANNEL_ACTIVATE;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.freeathome.config.FreeAtHomeSceneConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FreeAtHomeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author ruebox - Initial contribution
 */
public class FreeAtHomeSceneHandler extends FreeAtHomeBaseHandler {

    private Logger logger = LoggerFactory.getLogger(FreeAtHomeSceneHandler.class);

    private FreeAtHomeSceneConfig m_Configuration;

    public FreeAtHomeSceneHandler(Thing thing) {
        super(thing);
    }

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

            if (channelUID.getId().equals(SCENE_THING_CHANNEL_ACTIVATE)) {
                OnOffType udCommand = (OnOffType) command;

                if (udCommand.equals(OnOffType.ON)) {
                    String channel = m_Configuration.SceneId + "/" + m_Configuration.ChannelId + "/"
                            + m_Configuration.OutputId;

                    logger.debug("Called channel Scence: " + channel);
                    bridge.setDataPoint(channel, m_Configuration.DataPoint);

                    // Reset scene switch to off after activation.
                    // logger.debug("Reset scene switch to off after activation");
                    // updateState(SCENE_THING_CHANNEL_ACTIVATE, OnOffType.OFF);
                    // Seems not to work properly -> done by rule as workaround
                }
            }
        }
    }

    @Override
    public void initialize() {
        m_Configuration = getConfigAs(FreeAtHomeSceneConfig.class);

        // Fetch bridge on initialization to get proper state
        FreeAtHomeBridgeHandler bridge = getFreeAtHomeBridge();
        bridge.dummyThingsEnabled();
    }

}