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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link FreeAtHomeBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author ruebox - Initial contribution
 * @author kjoglum - Update copyright header / package / added touch thing
 */
public class FreeAtHomeBindingConstants {

    public static final String BINDING_ID = "freeathome";

    public static final String BRIDGE_ID = "bridge";
    public static final String RAFFSTORE_ID = "raffstore";
    public static final String SCENE_ID = "scene";
    public static final String SCENARIO_SELECTOR_ID = "scenario_selector";
    public static final String DUMMY_ID = "dummy";
    public static final String SWITCH_ID = "switch";
    public static final String THERMOSTAT_ID = "thermostat";
    public static final String DIMMER_ID = "dimmer";
    public static final String TOUCH_ID = "touch";
    public static final String WEATHER_ID = "weather";

    // List of all Thing Type UIDs
    public final static ThingTypeUID RAFFSTORE_THING_TYPEUID = new ThingTypeUID(BINDING_ID, RAFFSTORE_ID);
    public final static ThingTypeUID BRIDGE_THING_TYPEUID = new ThingTypeUID(BINDING_ID, BRIDGE_ID);
    public final static ThingTypeUID SCENE_THING_TYPEUID = new ThingTypeUID(BINDING_ID, SCENE_ID);
    public final static ThingTypeUID SCENARIO_SELECTOR_THING_TYPEUID = new ThingTypeUID(BINDING_ID,
            SCENARIO_SELECTOR_ID);
    public final static ThingTypeUID DUMMY_THING_TYPEUID = new ThingTypeUID(BINDING_ID, DUMMY_ID);
    public final static ThingTypeUID SWITCH_THING_TYPEUID = new ThingTypeUID(BINDING_ID, SWITCH_ID);
    public final static ThingTypeUID THERMOSTAT_THING_TYPEUID = new ThingTypeUID(BINDING_ID, THERMOSTAT_ID);
    public final static ThingTypeUID DIMMER_THING_TYPEUID = new ThingTypeUID(BINDING_ID, DIMMER_ID);
    public final static ThingTypeUID TOUCH_THING_TYPEUID = new ThingTypeUID(BINDING_ID, TOUCH_ID);
    public final static ThingTypeUID WEATHER_THING_TYPEUID = new ThingTypeUID(BINDING_ID, WEATHER_ID);

    // List of all Channel ids
    public final static String RAFFSTORE_THING_CHANNEL_STEPWISE = "stepwise";
    public final static String RAFFSTORE_THING_CHANNEL_COMPLETE = "complete";
    public final static String RAFFSTORE_THING_CHANNEL_PERCENTAGE = "percentage";

    public final static String SCENE_THING_CHANNEL_ACTIVATE = "activate";

    public final static String SCENARIO_SELECTOR_THING_CHANNEL = "selector";

    public final static String DUMMY_THING_CHANNEL = "dummy_channel";

    public final static String SWITCH_THING_CHANNEL = "fh_switch_channel";

    public final static String THERMOSTAT_TARGET_TEMP_THING_CHANNEL = "therm_target_temp";
    public final static String THERMOSTAT_ROOM_TEMP_THING_CHANNEL = "therm_room_temp";
    public final static String THERMOSTAT_SWITCH_THING_CHANNEL = "therm_switch";
    public final static String THERMOSTAT_ECO_THING_CHANNEL = "therm_eco_switch";

    public final static String DIMMER_SWITCH_THING_CHANNEL = "dimmer_switch";
    public final static String DIMMER_FADING_THING_CHANNEL = "dimmer_fading";
    public final static String DIMMER_VALUE_THING_CHANNEL = "dimmer_value";

    public final static String TOUCH_TARGET_TEMP_THING_CHANNEL = "therm_target_temp";
    public final static String TOUCH_ROOM_TEMP_THING_CHANNEL = "therm_room_temp";
    public final static String TOUCH_SWITCH_THING_CHANNEL = "therm_switch";
    public final static String TOUCH_ECO_THING_CHANNEL = "therm_eco_switch";

    public final static String WEATHER_TEMP_THING_CHANNEL = "weather_temperature";
    public final static String WEATHER_WIND_THING_CHANNEL = "weather_wind";
    public final static String WEATHER_ILLUMINATION_THING_CHANNEL = "weather_illumination";
    public final static String WEATHER_RAIN_THING_CHANNEL = "weather_rain";

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = new HashSet<>(
            Arrays.asList(RAFFSTORE_THING_TYPEUID, BRIDGE_THING_TYPEUID, SCENE_THING_TYPEUID,
                    SCENARIO_SELECTOR_THING_TYPEUID, DUMMY_THING_TYPEUID, SWITCH_THING_TYPEUID,
                    THERMOSTAT_THING_TYPEUID, DIMMER_THING_TYPEUID, TOUCH_THING_TYPEUID, WEATHER_THING_TYPEUID));

}