/**
 * Copyright (c) 2014-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.freeathome;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link FreeAtHomeBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author ruebox - Initial contribution
 */
public class FreeAtHomeBindingConstants {

    public static final String BINDING_ID = "freeathome";

    public static final String BRIDGE_ID = "bridge";
    public static final String RAFFSTORE_ID = "raffstore";
    public static final String SCENE_ID = "scene";
    public static final String SCENARIO_SELECTOR_ID = "scenario_selector";

    // List of all Thing Type UIDs
    public final static ThingTypeUID RAFFSTORE_THING_TYPEUID = new ThingTypeUID(BINDING_ID, RAFFSTORE_ID);
    public final static ThingTypeUID BRIDGE_THING_TYPEUID = new ThingTypeUID(BINDING_ID, BRIDGE_ID);
    public final static ThingTypeUID SCENE_THING_TYPEUID = new ThingTypeUID(BINDING_ID, SCENE_ID);
    public final static ThingTypeUID SCENARIO_SELECTOR_THING_TYPEUID = new ThingTypeUID(BINDING_ID,
            SCENARIO_SELECTOR_ID);

    // List of all Channel ids
    public final static String RAFFSTORE_THING_CHANNEL_STEPWISE = "raffstoreshutter_stepwise";
    public final static String RAFFSTORE_THING_CHANNEL_COMPLETE = "raffstoreshutter_complete";

    public final static String SCENE_THING_CHANNEL_ACTIVATE = "scene_activate";

    public final static String SCENARIO_SELECTOR_THING_CHANNEL = "scenario_selector_channel";

}