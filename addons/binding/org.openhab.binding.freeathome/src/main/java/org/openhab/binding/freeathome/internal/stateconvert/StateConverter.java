package org.openhab.binding.freeathome.internal.stateconvert;

import org.eclipse.smarthome.core.types.State;

public interface StateConverter {
    public State convert(String value);

}
