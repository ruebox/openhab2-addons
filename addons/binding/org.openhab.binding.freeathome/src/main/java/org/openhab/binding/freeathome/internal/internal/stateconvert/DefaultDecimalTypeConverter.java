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
package org.openhab.binding.freeathome.internal.internal.stateconvert;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.types.State;

/**
 * Convert values of update events to openhab states
 *
 * Default conversion for decimal types
 *
 * @author kjoglum - Initial contribution
 *
 */
public class DefaultDecimalTypeConverter implements StateConverter {
    @Override
    public State convert(String value) {
        return new DecimalType(value);
    }

}