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
package org.openhab.binding.ambientweather.internal;

import static org.openhab.binding.ambientweather.internal.AmbientWeatherBindingConstants.*;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.i18n.TimeZoneProvider;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.ambientweather.internal.handler.AmbientWeatherBridgeHandler;
import org.openhab.binding.ambientweather.internal.handler.AmbientWeatherStationHandler;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link AmbientWeatherHandlerFactory} is responsible for creating the
 * Ambient Weather bridge and station thing handlers.
 *
 * @author Mark Hilbush - Initial contribution
 */
@Component(configurationPid = "binding.ambientweather", service = ThingHandlerFactory.class)
public class AmbientWeatherHandlerFactory extends BaseThingHandlerFactory {
    private final Logger logger = LoggerFactory.getLogger(AmbientWeatherHandlerFactory.class);

    // Needed for converting UTC time to local time
    private TimeZoneProvider timeZoneProvider;

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (SUPPORTED_STATION_THING_TYPES_UIDS.contains(thingTypeUID)) {
            return new AmbientWeatherStationHandler(thing, timeZoneProvider);
        }
        if (SUPPORTED_BRIDGE_THING_TYPES_UIDS.contains(thingTypeUID)) {
            return new AmbientWeatherBridgeHandler((Bridge) thing);
        }
        return null;
    }

    @Reference
    protected void setTimeZoneProvider(TimeZoneProvider timeZoneProvider) {
        this.timeZoneProvider = timeZoneProvider;
    }

    protected void unsetTimeZoneProvider(TimeZoneProvider timeZone) {
        this.timeZoneProvider = null;
    }

}
