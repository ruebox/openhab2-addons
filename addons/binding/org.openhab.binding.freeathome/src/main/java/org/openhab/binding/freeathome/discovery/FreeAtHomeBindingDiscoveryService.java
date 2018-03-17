/**
 * Copyright (c) 2014-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.freeathome.discovery;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.freeathome.FreeAtHomeBindingConstants;
import org.openhab.binding.freeathome.handler.FreeAtHomeBridgeHandler;
import org.openhab.binding.freeathome.handler.FreeAtHomeRaffStoreHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Component(service = DiscoveryService.class, immediate = true, configurationPid = "discovery.freeathome")

/**
 * Implements manually triggered discovery
 *
 * @author ruebox
 *
 */
public class FreeAtHomeBindingDiscoveryService extends AbstractDiscoveryService {

    private Logger logger = LoggerFactory.getLogger(FreeAtHomeRaffStoreHandler.class);

    public FreeAtHomeBindingDiscoveryService(int timeout) throws IllegalArgumentException {
        super(FreeAtHomeBindingConstants.SUPPORTED_THING_TYPES_UIDS, 90, false);
        // TODO Auto-generated constructor stub
    }

    public FreeAtHomeBindingDiscoveryService() {
        super(FreeAtHomeBindingConstants.SUPPORTED_THING_TYPES_UIDS, 90, false);
    }

    @Override
    public void startScan() {
        // TODO Auto-generated method stub
        FreeAtHomeBridgeHandler fh = FreeAtHomeBridgeHandler.g_freeAtHomeBridgeHandler;

        if (fh != null) {
            logger.debug("start scan");
            ThingUID bridgeUID = fh.getThing().getUID();

            String xmlTree = fh.getAll();
            FreeAtHomeBindingInventoryIterator iter = new FreeAtHomeBindingInventoryIterator(xmlTree);

            while (iter.iter_hasnext()) {
                FreeAtHomeBindingInventoryIterator.DeviceDescription device = iter.iter_get();
                logger.debug(device.toString());

                String deviceTypeId = device.DeviceTypeId;

                switch (deviceTypeId) {
                    // Jalousieaktor 4-fach, REG
                    case "B001":
                        for (int i = 0; i < 4; i++) // B001 provides 4 different channels
                        {
                            String channelId = "ch000" + i;
                            ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.RAFFSTORE_THING_TYPEUID,
                                    device.Serial + "_" + channelId);
                            Map<String, Object> properties = new HashMap<>(1);
                            properties.put("DeviceId", device.Serial);
                            properties.put("ChannelId", channelId);

                            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                    .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                            + device.Serial + "_" + channelId)
                                    .withBridge(bridgeUID).withProperties(properties).build();
                            thingDiscovered(discoveryResult);
                        }
                        break;
                    // Jalousieaktor 1-fach, REG
                    case "1013":
                        for (int i = 0; i < 1; i++) // 1013 provides 4 different channels
                        {
                            String channelId = "ch000" + i;
                            ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.RAFFSTORE_THING_TYPEUID,
                                    device.Serial + "_" + channelId);
                            Map<String, Object> properties = new HashMap<>(1);
                            properties.put("DeviceId", device.Serial);
                            properties.put("ChannelId", channelId);

                            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                    .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                            + device.Serial + "_" + channelId)
                                    .withBridge(bridgeUID).withProperties(properties).build();
                            thingDiscovered(discoveryResult);
                        }
                        break;
                    // // Schaltaktor 4-fach, 16A, REG
                    case "B002":
                        for (int i = 0; i < 4; i++) // B002 provides 4 different channels
                        {
                            String channelId = "ch000" + i;
                            ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID,
                                    device.Serial + "_" + channelId);
                            Map<String, Object> properties = new HashMap<>(1);
                            properties.put("deviceId", device.Serial);
                            properties.put("channelId", channelId);

                            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                    .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                            + device.Serial + "_" + channelId)
                                    .withBridge(bridgeUID).withProperties(properties).build();
                            thingDiscovered(discoveryResult);
                        }
                        break;
                    case "B008":
                        for (int i = 0; i < 8; i++) // B008 provides 8 different channels
                        {
                            String channelId = "ch000" + i;
                            ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID,
                                    device.Serial + "_" + channelId);
                            Map<String, Object> properties = new HashMap<>(1);
                            properties.put("deviceId", device.Serial);
                            properties.put("channelId", channelId);

                            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                    .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                            + device.Serial + "_" + channelId)
                                    .withBridge(bridgeUID).withProperties(properties).build();
                            thingDiscovered(discoveryResult);
                        }
                        break;
                    // // Switch group
                    case "4000": {
                        String channelId = "ch0000";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID, device.Serial);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);
                        properties.put("channelId", channelId);
                        properties.put("dataPointId", "odp0002");

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder
                                .create(uid).withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                        + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }
                    // Jalousiegruppe
                    case "4001": {
                        String channelId = "ch0000";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.RAFFSTORE_THING_TYPEUID, device.Serial);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("DeviceId", device.Serial);
                        properties.put("ChannelId", channelId);
                        properties.put("InputIdComplete", "odp0003");
                        properties.put("InputIdStepwise", "odp0004");

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + device.Serial)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }
                    // thermostat
                    case "9004": {
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.THERMOSTAT_THING_TYPEUID, device.Serial);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + device.Serial)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }
                    // neue Szene
                    case "4800":
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SCENE_THING_TYPEUID, device.Serial);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("SceneId", device.Serial);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + device.Serial)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;

                    default: {
                        if (fh.dummyThingsEnabled()) {

                            ThingUID uid1 = new ThingUID(FreeAtHomeBindingConstants.DUMMY_THING_TYPEUID, device.Serial);

                            DiscoveryResult discoveryResult1 = DiscoveryResultBuilder
                                    .create(uid1).withLabel("Dummy_" + device.DeviceDisplayName + "_"
                                            + device.DeviceTypeName + "_" + device.Serial)
                                    .withBridge(bridgeUID).build();
                            thingDiscovered(discoveryResult1);
                        }
                    }
                }

                iter.iter_next();
            }

        }

    }

    @Override
    protected void startBackgroundDiscovery() {
        logger.debug("Start freeathome  background discovery");
        startScan();

    }

    @Override
    protected void stopBackgroundDiscovery() {
        logger.debug("Stop free at home background discovery");

    }

    @Override
    public boolean isBackgroundDiscoveryEnabled() {
        return false;
    }

}
