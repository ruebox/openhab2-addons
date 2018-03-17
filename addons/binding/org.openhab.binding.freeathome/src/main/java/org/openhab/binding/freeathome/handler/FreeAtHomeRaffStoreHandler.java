/**
 * Copyright (c) 2014-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.freeathome.handler;

import static org.openhab.binding.freeathome.FreeAtHomeBindingConstants.*;

import org.eclipse.smarthome.core.library.types.StopMoveType;
import org.eclipse.smarthome.core.library.types.UpDownType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.freeathome.config.FreeAtHomeRaffStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FreeAtHomeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author ruebox - Initial contribution
 */
public class FreeAtHomeRaffStoreHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(FreeAtHomeRaffStoreHandler.class);

    private FreeAtHomeRaffStoreConfig m_Configuration;

    public FreeAtHomeRaffStoreHandler(Thing thing) {
        super(thing);
    }

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

        if (command instanceof StopMoveType && channelUID.getId().equals(RAFFSTORE_THING_CHANNEL_COMPLETE)) {
            StopMoveType udCommand = (StopMoveType) command;
            if (udCommand.equals(StopMoveType.STOP)) {
                String channel = m_Configuration.DeviceId + "/" + m_Configuration.ChannelId + "/"
                        + m_Configuration.InputIdStepwise;

                logger.debug("Stop complete run" + channel);
                bridge.setDataPoint(channel, "0");
            }
        }

        /*
         * UpDownCommand
         */
        if (command instanceof UpDownType) {
            UpDownType udCommand = (UpDownType) command;

            if (channelUID.getId().equals(RAFFSTORE_THING_CHANNEL_STEPWISE)) {
                String channel = m_Configuration.DeviceId + "/" + m_Configuration.ChannelId + "/"
                        + m_Configuration.InputIdStepwise;

                logger.info("Called channel STEPWISE: " + channel);

                if (udCommand.equals(UpDownType.UP)) {
                    logger.debug("STEPWISE UP");
                    bridge.setDataPoint(channel, "0");
                } else {
                    logger.debug("STEPWISE DOWN");
                    bridge.setDataPoint(channel, "1");
                }
            } // stepwise

            if (channelUID.getId().equals(RAFFSTORE_THING_CHANNEL_COMPLETE)) {
                String channel = m_Configuration.DeviceId + "/" + m_Configuration.ChannelId + "/"
                        + m_Configuration.InputIdComplete;

                logger.debug("Called channel COMPLETE: " + channel);

                if (udCommand.equals(UpDownType.UP)) {
                    logger.debug("COMPLETE UP");
                    bridge.setDataPoint(channel, "0");
                } else {
                    logger.debug("COMPLETE DOWN");
                    bridge.setDataPoint(channel, "1");
                }
            } // complete
        }
    }

    @Override
    public void initialize() {
        m_Configuration = getConfigAs(FreeAtHomeRaffStoreConfig.class);

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