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
 * Configuration of bridge
 *
 * @author kjoglum - Initial contribution
 *
 */
public class FreeAtHomeBridgeConfig {
    /*
     * IP adress of gateway
     */
    public String host;

    public Integer port;

    public String login;

    public String password;

    public String log_dir;

    public Boolean log_enabled;

    public Boolean dummy_things_enabled;
}