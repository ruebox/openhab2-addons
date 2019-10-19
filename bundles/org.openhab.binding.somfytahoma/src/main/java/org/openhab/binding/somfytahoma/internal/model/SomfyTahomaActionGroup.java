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
package org.openhab.binding.somfytahoma.internal.model;

import org.eclipse.jdt.annotation.NonNullByDefault;

import java.util.ArrayList;

/**
 * The {@link SomfyTahomaActionGroup} holds information about a action
 * group and current actions.
 *
 * @author Ondrej Pecta - Initial contribution
 */
@NonNullByDefault
public class SomfyTahomaActionGroup {
    private String oid = "";
    private String label = "";
    private ArrayList<SomfyTahomaAction> actions = new ArrayList<>();

    public String getOid() {
        return oid;
    }

    public String getLabel() {
        return label;
    }

    public ArrayList<SomfyTahomaAction> getActions() {
        return actions;
    }
}
