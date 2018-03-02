/**
 * Copyright (c) 2014-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.freeathome.config;

/**
 * Configuration of bridge
 *
 * @author ruebox
 *
 */
public class FreeAtHomeBridgeConfig {
    /*
     * IP adress of gateway
     */
    public String ipAddress;

    public Integer port;

    public String login;

    public String password;

    public String log_dir;

    public Boolean log_enabled;
}