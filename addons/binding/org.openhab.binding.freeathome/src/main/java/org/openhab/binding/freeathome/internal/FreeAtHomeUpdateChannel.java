/**
 *
 */
package org.openhab.binding.freeathome.internal;

import org.openhab.binding.freeathome.handler.FreeAtHomeBaseHandler;
import org.openhab.binding.freeathome.internal.stateconvert.StateConverter;

/**
 * @author rue
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
