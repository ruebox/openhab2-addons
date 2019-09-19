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

import org.openhab.binding.freeathome.internal.handler.FreeAtHomeBaseHandler;
import org.openhab.binding.freeathome.internal.internal.stateconvert.StateConverter;

/**
 * Class to register pubsub events
 *
 * @author kjoglum - Initial contribution
 *
 */
public class FreeAtHomeUpdateChannel {
    public FreeAtHomeBaseHandler m_Thing;
    public String m_OhThingChanneId;
    public StateConverter m_OhThingStateConverter;
    public String m_FhSerial;
    public String m_FhChannel;
    public String m_FhDataPoint;

    public FreeAtHomeUpdateChannel(FreeAtHomeBaseHandler thing, String channelId, StateConverter stateConverter,
            String serial, String channel, String dataPoint) {
        m_Thing = thing;
        m_OhThingChanneId = channelId;
        m_OhThingStateConverter = stateConverter;
        m_FhSerial = serial;
        m_FhChannel = channel;
        m_FhDataPoint = dataPoint;
    }

    @Override
    public String toString() {
        return m_Thing.toString() + "_" + m_OhThingChanneId + "_" + m_OhThingStateConverter.toString() + "_"
                + m_FhSerial + "_" + m_FhChannel + "_" + m_FhDataPoint;
    }
}
