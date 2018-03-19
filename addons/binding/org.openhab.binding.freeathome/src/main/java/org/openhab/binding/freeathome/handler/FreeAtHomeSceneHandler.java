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

import java.util.Timer;
import java.util.TimerTask;

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

    private Timer m_Timer = new Timer(true);

    public FreeAtHomeSceneHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        m_Configuration = getConfigAs(FreeAtHomeSceneConfig.class);

        logger.debug("Reset Timeout            {}.", m_Configuration.resetTimeout);
        // Fetch bridge on initialization to get proper state
        FreeAtHomeBridgeHandler bridge = getFreeAtHomeBridge();
        if (bridge != null) {
            bridge.dummyThingsEnabled();
        } // dummy call to avoid optimization
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command, boolean update) {
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

                    if (m_Configuration.resetTimeout > 0) {
                        m_Timer.schedule(new ResetTask(this), (long) (m_Configuration.resetTimeout * 1000));
                        logger.debug("Reset task scheduled");
                    }
                }
            }
        }

    }

    public void ResetSwitch() {
        logger.debug("Reset scene switch to OFF");
        updateState(SCENE_THING_CHANNEL_ACTIVATE, OnOffType.OFF);
    }

    /*
     * Class to reset Scene switch asynchronously
     */
    class ResetTask extends TimerTask {
        FreeAtHomeSceneHandler m_Handler = null;

        ResetTask(FreeAtHomeSceneHandler handler) {
            m_Handler = handler;
        }

        @Override
        public void run() {
            m_Handler.ResetSwitch();
        }
    }

}