package org.openhab.binding.freeathome.internal.stateconvert;

import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.types.State;

public class DefaultPercentTypeConverter implements StateConverter {

    @Override
    public State convert(String value) {
        // TODO Auto-generated method stub
        return new PercentType(value);
    }

}
