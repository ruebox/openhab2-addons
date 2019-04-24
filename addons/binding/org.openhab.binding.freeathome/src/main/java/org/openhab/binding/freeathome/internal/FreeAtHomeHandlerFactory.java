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
package org.openhab.binding.freeathome.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.freeathome.internal.handler.FreeAtHomeBridgeHandler;
import org.openhab.binding.freeathome.internal.handler.FreeAtHomeDimmerHandler;
import org.openhab.binding.freeathome.internal.handler.FreeAtHomeDummyHandler;
import org.openhab.binding.freeathome.internal.handler.FreeAtHomeRaffStoreHandler;
import org.openhab.binding.freeathome.internal.handler.FreeAtHomeScenarioSelectorHandler;
import org.openhab.binding.freeathome.internal.handler.FreeAtHomeSceneHandler;
import org.openhab.binding.freeathome.internal.handler.FreeAtHomeSwitchHandler;
import org.openhab.binding.freeathome.internal.handler.FreeAtHomeThermostatHandler;
import org.openhab.binding.freeathome.internal.handler.FreeAtHomeTouchHandler;
import org.openhab.binding.freeathome.internal.handler.FreeAtHomeWeatherHandler;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;

/**
 * The {@link FreeAtHomeHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 *
 * @author kjoglum - Initial contribution
 */
@NonNullByDefault
@Component(service = ThingHandlerFactory.class)
public class FreeAtHomeHandlerFactory extends BaseThingHandlerFactory {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(FreeAtHomeHandlerFactory.class);

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
            logger.debug("Create BridgeHandler");
            return new FreeAtHomeBridgeHandler((Bridge) thing);
        }

        if (thingTypeUID.equals(FreeAtHomeBindingConstants.SCENE_THING_TYPEUID)) {
            logger.debug("Create Scene Handler");
            return new FreeAtHomeSceneHandler(thing);
        }

        if (thingTypeUID.equals(FreeAtHomeBindingConstants.SCENARIO_SELECTOR_THING_TYPEUID)) {
            logger.debug("Create Scenario Selector Handler");
            return new FreeAtHomeScenarioSelectorHandler(thing);
        }

        if (thingTypeUID.equals(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID)) {
            logger.debug("Create Switch Handler");
            return new FreeAtHomeSwitchHandler(thing);
        }

        if (thingTypeUID.equals(FreeAtHomeBindingConstants.THERMOSTAT_THING_TYPEUID)) {
            logger.debug("Create thermostat Handler");
            return new FreeAtHomeThermostatHandler(thing);
        }

        if (thingTypeUID.equals(FreeAtHomeBindingConstants.DIMMER_THING_TYPEUID)) {
            logger.debug("Create dimmer Handler");
            return new FreeAtHomeDimmerHandler(thing);
        }

        if (thingTypeUID.equals(FreeAtHomeBindingConstants.TOUCH_THING_TYPEUID)) {
            logger.debug("Create touch Handler");
            return new FreeAtHomeTouchHandler(thing);
        }

        if (thingTypeUID.equals(FreeAtHomeBindingConstants.WEATHER_THING_TYPEUID)) {
            logger.debug("Create weather station Handler");
            return new FreeAtHomeWeatherHandler(thing);
        }

        if (thingTypeUID.equals(FreeAtHomeBindingConstants.DUMMY_THING_TYPEUID)) {
            logger.debug("Create dummy Handler");
            return new FreeAtHomeDummyHandler(thing);
        }

        return null;
    }
}