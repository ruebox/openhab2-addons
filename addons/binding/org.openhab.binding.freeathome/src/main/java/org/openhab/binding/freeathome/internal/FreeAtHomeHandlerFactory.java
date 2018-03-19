/**
 * Copyright (c) 2014-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.freeathome.internal;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.freeathome.FreeAtHomeBindingConstants;
import org.openhab.binding.freeathome.handler.FreeAtHomeBridgeHandler;
import org.openhab.binding.freeathome.handler.FreeAtHomeDimmerHandler;
import org.openhab.binding.freeathome.handler.FreeAtHomeDummyHandler;
import org.openhab.binding.freeathome.handler.FreeAtHomeRaffStoreHandler;
import org.openhab.binding.freeathome.handler.FreeAtHomeScenarioSelectorHandler;
import org.openhab.binding.freeathome.handler.FreeAtHomeSceneHandler;
import org.openhab.binding.freeathome.handler.FreeAtHomeSwitchHandler;
import org.openhab.binding.freeathome.handler.FreeAtHomeThermostatHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FreeAtHomeHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 *
 * @author ruebox - Initial contribution
 */
public class FreeAtHomeHandlerFactory extends BaseThingHandlerFactory {

    private Logger logger = LoggerFactory.getLogger(FreeAtHomeHandlerFactory.class);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return FreeAtHomeBindingConstants.SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(FreeAtHomeBindingConstants.RAFFSTORE_THING_TYPEUID)) {
            return new FreeAtHomeRaffStoreHandler(thing);
        }

        if (thingTypeUID.equals(FreeAtHomeBindingConstants.BRIDGE_THING_TYPEUID)) {
            logger.debug("create BridgeHandler");
            FreeAtHomeBridgeHandler handler = new FreeAtHomeBridgeHandler((Bridge) thing);
            return handler;
        }

        if (thingTypeUID.equals(FreeAtHomeBindingConstants.SCENE_THING_TYPEUID)) {
            logger.debug("create Scene Handler");
            FreeAtHomeSceneHandler handler = new FreeAtHomeSceneHandler(thing);
            return handler;
        }

        if (thingTypeUID.equals(FreeAtHomeBindingConstants.SCENARIO_SELECTOR_THING_TYPEUID)) {
            logger.debug("create Scenario Selector Handler");
            FreeAtHomeScenarioSelectorHandler handler = new FreeAtHomeScenarioSelectorHandler(thing);
            return handler;
        }

        if (thingTypeUID.equals(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID)) {
            logger.debug("create Switch Handler");
            FreeAtHomeSwitchHandler handler = new FreeAtHomeSwitchHandler(thing);
            return handler;
        }

        if (thingTypeUID.equals(FreeAtHomeBindingConstants.THERMOSTAT_THING_TYPEUID)) {
            logger.debug("create thermostat Handler");
            FreeAtHomeThermostatHandler handler = new FreeAtHomeThermostatHandler(thing);
            return handler;
        }

        if (thingTypeUID.equals(FreeAtHomeBindingConstants.DIMMER_THING_TYPEUID)) {
            logger.debug("create dimmer Handler");
            FreeAtHomeDimmerHandler handler = new FreeAtHomeDimmerHandler(thing);
            return handler;
        }

        if (thingTypeUID.equals(FreeAtHomeBindingConstants.DUMMY_THING_TYPEUID)) {
            logger.debug("create dummy Handler");
            FreeAtHomeDummyHandler handler = new FreeAtHomeDummyHandler(thing);
            return handler;
        }

        return null;
    }
}