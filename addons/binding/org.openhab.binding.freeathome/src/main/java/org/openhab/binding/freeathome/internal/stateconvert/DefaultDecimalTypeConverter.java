package org.openhab.binding.freeathome.internal.stateconvert;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.types.State;

public class DefaultDecimalTypeConverter implements StateConverter {
    @Override
    public State convert(String value) {
        return new DecimalType(value);
    }

}