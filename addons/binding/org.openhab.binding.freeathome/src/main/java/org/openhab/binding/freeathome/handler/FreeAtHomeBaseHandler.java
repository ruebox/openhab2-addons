/**
 * Copyright (c) 2014-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.freeathome.handler;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;

/**
 * The {@link FreeAtHomeBaseHandler} is responsible for handling common commands, which are
 * relevant for all things
 *
 * @author ruebox - Initial contribution
 */
public abstract class FreeAtHomeBaseHandler extends BaseThingHandler {

    public FreeAtHomeBaseHandler(Thing thing) {
        super(thing);
        // TODO Auto-generated constructor stub
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
        Bridge bridge = getBridge();
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

}
