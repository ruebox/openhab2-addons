package org.openhab.binding.freeathome.internal.stateconvert;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.State;

public class DefaultOnOffTypeConverter implements StateConverter {

    @Override
    public State convert(String value) {

        if (value.equals("1")) {
            return OnOffType.ON;
        } else {
            return OnOffType.OFF;
        }
    }

}
