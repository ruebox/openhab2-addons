/**
 * Copyright (c) 2014-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.freeathome.internal.stateconvert;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.types.State;

/**
 * Convert values of update events to openhab states
 *
 * Default conversion for decimal types
 *
 * @author ruebox
 *
 */
public class DefaultDecimalTypeConverter implements StateConverter {
    @Override
    public State convert(String value) {
        return new DecimalType(value);
    }

}