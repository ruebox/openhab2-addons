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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.freeathome.internal.FreeAtHomeUpdateChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FreeAtHomeBaseHandler} is responsible for handling common commands, which are
 * relevant for all things
 *
 * @author kjoglum - Initial contribution
 */
public abstract class FreeAtHomeBaseHandler extends BaseThingHandler {

    protected List<FreeAtHomeUpdateChannel> m_UpdateChannels;
    private Logger logger = LoggerFactory.getLogger(FreeAtHomeBaseHandler.class);

    public FreeAtHomeBaseHandler(Thing thing) {
        super(thing);
        // TODO Auto-generated constructor stub

        m_UpdateChannels = new ArrayList<FreeAtHomeUpdateChannel>();
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {

        if (bridgeStatusInfo.getStatus() == ThingStatus.ONLINE) {
            updateStatus(bridgeStatusInfo.getStatus());
        } else {
            updateStatus(bridgeStatusInfo.getStatus(), ThingStatusDetail.BRIDGE_OFFLINE,
                    bridgeStatusInfo.getDescription());
        }

    }

    protected FreeAtHomeBridgeHandler getFreeAtHomeBridge() {
        Bridge bridge = this.getBridge();
        if (bridge == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "No bridge available");
            return null;
        }

        ThingHandler handler = bridge.getHandler();
        if (handler instanceof FreeAtHomeBridgeHandler) {
            FreeAtHomeBridgeHandler h = (FreeAtHomeBridgeHandler) handler;
            updateStatus(h.getThing().getStatus(), h.getThing().getStatusInfo().getStatusDetail());
            return h;
        }
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Bridge of incorrect type");
        return null;
    }

    public void notifyUpdate(ChannelUID cUID, State state) {
        updateState(cUID, state);
    }

    public void registerUpdate() {

        FreeAtHomeBridgeHandler bridge = getFreeAtHomeBridge();

        if (bridge == null) {
            logger.error("No bridge found -> nothing to register");
            return;
        }

        for (int i = 0; i < m_UpdateChannels.size(); i++) {
            logger.debug("Register {}", m_UpdateChannels.get(i).toString());
            bridge.m_UpdateHandler.Register(m_UpdateChannels.get(i));
        }

    }

    public void unregisterUpdate() {
        FreeAtHomeBridgeHandler bridge = getFreeAtHomeBridge();

        if (bridge == null) {
            logger.error("No bridge found -> nothing to register");
            return;
        }

        for (int i = 0; i < m_UpdateChannels.size(); i++) {
            logger.debug("Register {}", m_UpdateChannels.get(i).toString());
            bridge.m_UpdateHandler.Unregister(m_UpdateChannels.get(i));
        }

    }

    /*
     * Called during initialize
     */
    public abstract void setUp();

    /*
     * Called during dispose
     */
    public abstract void tearDown();

    @Override
    public void initialize() {
        this.setUp();
        this.registerUpdate();
    }

    @Override
    public void dispose() {
        this.tearDown();
        this.unregisterUpdate();
    }

}
