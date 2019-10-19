/**
<<<<<<< HEAD
 * Copyright (c) 2014-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.freeathome.handler;
=======
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
>>>>>>> 27b57273dd980306b76ae97cf96e434f3c45d934

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
<<<<<<< HEAD
import org.openhab.binding.freeathome.FreeAtHomeBindingConstants;
import org.openhab.binding.freeathome.config.FreeAtHomeWeatherConfig;
import org.openhab.binding.freeathome.internal.FreeAtHomeUpdateChannel;
import org.openhab.binding.freeathome.internal.stateconvert.DefaultDecimalTypeConverter;
import org.openhab.binding.freeathome.internal.stateconvert.DefaultOnOffTypeConverter;
=======
import org.openhab.binding.freeathome.internal.FreeAtHomeBindingConstants;
import org.openhab.binding.freeathome.internal.FreeAtHomeUpdateChannel;
import org.openhab.binding.freeathome.internal.config.FreeAtHomeWeatherConfig;
import org.openhab.binding.freeathome.internal.internal.stateconvert.DefaultDecimalTypeConverter;
import org.openhab.binding.freeathome.internal.internal.stateconvert.DefaultOnOffTypeConverter;
>>>>>>> 27b57273dd980306b76ae97cf96e434f3c45d934
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FreeAtHomeWeatherHandler} represents weather station
 *
<<<<<<< HEAD
 * @author ruebox - Initial contribution
=======
 * @author kjoglum - Initial contribution
>>>>>>> 27b57273dd980306b76ae97cf96e434f3c45d934
 */

public class FreeAtHomeWeatherHandler extends FreeAtHomeBaseHandler {

    public FreeAtHomeWeatherHandler(Thing thing) {
        super(thing);

    }

    private Logger logger = LoggerFactory.getLogger(FreeAtHomeWeatherHandler.class);

    private FreeAtHomeWeatherConfig m_Configuration;

    @Override
    /*
     * No commands need to be handled
     */
    public void handleCommand(ChannelUID channelUID, Command command) {
        FreeAtHomeBridgeHandler bridge = getFreeAtHomeBridge();

        if (bridge == null) {
            logger.error("No bridge connected");
            return;
        }

    }

    @Override
    public void tearDown() {

    }

    @Override
    public void setUp() {
        m_Configuration = getConfigAs(FreeAtHomeWeatherConfig.class);

        // Fetch bridge on initialization to get proper state
        FreeAtHomeBridgeHandler bridge = getFreeAtHomeBridge();
        if (bridge != null) {
            m_UpdateChannels.add(new FreeAtHomeUpdateChannel(this,
                    FreeAtHomeBindingConstants.WEATHER_TEMP_THING_CHANNEL, new DefaultDecimalTypeConverter(),
                    m_Configuration.deviceId, m_Configuration.channelIdTemp, m_Configuration.dataPointIdTemp));

            m_UpdateChannels.add(new FreeAtHomeUpdateChannel(this,
                    FreeAtHomeBindingConstants.WEATHER_WIND_THING_CHANNEL, new DefaultDecimalTypeConverter(),
                    m_Configuration.deviceId, m_Configuration.channelIdWind, m_Configuration.dataPointIdWind));

            m_UpdateChannels.add(
                    new FreeAtHomeUpdateChannel(this, FreeAtHomeBindingConstants.WEATHER_ILLUMINATION_THING_CHANNEL,
                            new DefaultDecimalTypeConverter(), m_Configuration.deviceId,
                            m_Configuration.channelIdIllumination, m_Configuration.dataPointIdIllumination));

            m_UpdateChannels.add(new FreeAtHomeUpdateChannel(this,
                    FreeAtHomeBindingConstants.WEATHER_RAIN_THING_CHANNEL, new DefaultOnOffTypeConverter(),
                    m_Configuration.deviceId, m_Configuration.channelIdRain, m_Configuration.dataPointIdRain));

        } // dummy call to avoid optimization
    }

}