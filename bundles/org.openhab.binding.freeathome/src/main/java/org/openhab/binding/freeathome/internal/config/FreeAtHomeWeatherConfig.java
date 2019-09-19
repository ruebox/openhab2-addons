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
package org.openhab.binding.freeathome.internal.config;

/**
 * Configuration of a weather station
 *
 * @author kjoglum - Initial contribution
 *
 */
public class FreeAtHomeWeatherConfig {
    public String deviceId;
    public String channelIdIllumination;
    public String dataPointIdIllumination;
    public String channelIdRain;
    public String dataPointIdRain;
    public String channelIdTemp;
    public String dataPointIdTemp;
    public String channelIdWind;
    public String dataPointIdWind;

}
