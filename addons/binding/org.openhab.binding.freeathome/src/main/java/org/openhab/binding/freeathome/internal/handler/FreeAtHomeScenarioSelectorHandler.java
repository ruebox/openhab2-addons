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

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;

/**
 * The {@link FreeAtHomeScenarioSelectorHandler} represents scenario
 *
 * @author kjoglum - Initial contribution
 */
public class FreeAtHomeScenarioSelectorHandler extends FreeAtHomeBaseHandler {

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
