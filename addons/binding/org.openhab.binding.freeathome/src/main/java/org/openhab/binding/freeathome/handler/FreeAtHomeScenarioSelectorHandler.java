/**
 * Copyright (c) 2014-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.freeathome.handler;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FreeAtHomeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author ruebox - Initial contribution
 */
public class FreeAtHomeScenarioSelectorHandler extends FreeAtHomeBaseHandler {

    private Logger logger = LoggerFactory.getLogger(FreeAtHomeScenarioSelectorHandler.class);

    public FreeAtHomeScenarioSelectorHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

    }

    @Override
    public void tearDown() {

    }

    @Override
    public void setUp() {
        // Fetch bridge on initialization to get proper state
        FreeAtHomeBridgeHandler bridge = getFreeAtHomeBridge();
        if (bridge != null) {
            bridge.dummyThingsEnabled();
        } // dummy call to avoid optimization
    }
}
