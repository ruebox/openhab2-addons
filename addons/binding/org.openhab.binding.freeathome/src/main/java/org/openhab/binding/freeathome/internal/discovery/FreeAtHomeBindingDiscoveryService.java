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
package org.openhab.binding.freeathome.internal.discovery;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.freeathome.internal.FreeAtHomeBindingConstants;
import org.openhab.binding.freeathome.internal.handler.FreeAtHomeBridgeHandler;
import org.openhab.binding.freeathome.internal.handler.FreeAtHomeRaffStoreHandler;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;

/**
 * Implements manually triggered discovery
 *
 * @author kjoglum - Initial contribution
 *
 */
@Component(service = DiscoveryService.class)
public class FreeAtHomeBindingDiscoveryService extends AbstractDiscoveryService {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(FreeAtHomeRaffStoreHandler.class);

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

        this.removeOlderResults(getTimestampOfLastScan());

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
                                            + deviceTypeId + "_" + device.Serial + "_" + channelId)
                                    .withBridge(bridgeUID).withProperties(properties).build();
                            thingDiscovered(discoveryResult);
                        }
                        break;
                    // Jalousieaktor 1-fach, REG
                    case "9013":
                    case "1013":
                    // 1013 provides 1 channel, but it runs on channel ch0003
                    {
                        String channelId = "ch0003";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.RAFFSTORE_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("DeviceId", device.Serial);
                        properties.put("ChannelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                    }
                        break;

                    // Sensor/Schaltaktor 2/1-fach
                    // http://www.busch-jaeger-katalog.de/artikel.php?bereich=1013622&programm=1013638&gruppe=1013642&produkt=1013643
                    case "1015":
                    case "9015":
                    // 9015 provides 1 channel, but it runs on channel ch0006
                    // Jalousieaktor 1-fach, REG ?2-fac
                    {
                        String channelId = "ch0006";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.RAFFSTORE_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("DeviceId", device.Serial);
                        properties.put("ChannelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                    }
                        break;
                    // // Hue Schaltaktor
                    case "10C4": {
                        String channelId = "ch0000";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);
                        properties.put("channelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                    }
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
                                            + deviceTypeId + "_" + device.Serial + "_" + channelId)
                                    .withBridge(bridgeUID).withProperties(properties).build();
                            thingDiscovered(discoveryResult);
                        }
                        break;
                    // Schaltaktor 8 fach
                    case "B008":
                        for (int i = 12; i < 12 + 8; i++) // B008 provides 8 different channels
                        {
                            String n = Integer.toHexString(i).toUpperCase();

                            String result = ("0000" + n).substring(n.length());
                            String channelId = "ch" + result;
                            ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID,
                                    device.Serial + "_" + channelId);
                            Map<String, Object> properties = new HashMap<>(1);
                            properties.put("deviceId", device.Serial);
                            properties.put("channelId", channelId);

                            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                    .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                            + deviceTypeId + "_" + device.Serial + "_" + channelId)
                                    .withBridge(bridgeUID).withProperties(properties).build();
                            thingDiscovered(discoveryResult);
                        }
                        break;
                    // Schaltaktor 1 fach
                    case "900C": // Switch actuator 1/1
                    {
                        String channelId = "ch0003";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);
                        properties.put("channelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                    }
                        break;
                    // Motion sensor with switch 1 fach
                    case "900A": // Switch actuator 1/1
                    {
                        String channelId = "ch0001";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);
                        properties.put("channelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                    }
                        break;
                    // Schaltaktor 2 fach
                    case "9010": // Switch actuator 2/2
                        for (int i = 6; i < 8; i++) // 2 channel
                        {
                            String channelId = "ch000" + i;
                            ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID,
                                    device.Serial + "_" + channelId);
                            Map<String, Object> properties = new HashMap<>(1);
                            properties.put("deviceId", device.Serial);
                            properties.put("channelId", channelId);

                            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                    .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                            + deviceTypeId + "_" + device.Serial + "_" + channelId)
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

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
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

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder
                                .create(uid).withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                        + deviceTypeId + "_" + device.Serial)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }
                    // thermostat
                    case "1004":
                    case "9004": {
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.THERMOSTAT_THING_TYPEUID, device.Serial);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder
                                .create(uid).withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                        + deviceTypeId + "_" + device.Serial)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }
                    // touch
                    case "1020": {
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.TOUCH_THING_TYPEUID, device.Serial);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder
                                .create(uid).withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                        + deviceTypeId + "_" + device.Serial)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }
                    // neue Szene
                    case "4800": {
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SCENE_THING_TYPEUID, device.Serial);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("SceneId", device.Serial);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder
                                .create(uid).withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                        + deviceTypeId + "_" + device.Serial)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }
                    // Dimmer 4 channel
                    case "101C":
                    case "1021":
                    case "901C":
                        for (int i = 0; i < 4; i++) {
                            String channelId = "ch000" + i;
                            ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.DIMMER_THING_TYPEUID,
                                    device.Serial + "_" + channelId);
                            Map<String, Object> properties = new HashMap<>(1);
                            properties.put("deviceId", device.Serial);
                            properties.put("channelId", channelId);

                            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                    .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                            + deviceTypeId + "_" + device.Serial + "_" + channelId)
                                    .withBridge(bridgeUID).withProperties(properties).build();
                            thingDiscovered(discoveryResult);
                        }
                        break;
                    case "9017": // 1/1 dim actuator
                    {
                        String channelId = "ch0003";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.DIMMER_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);
                        properties.put("channelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                    }
                        break;
                    case "9019": // 2/1 dim actuator
                    {
                        String channelId = "ch0006";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.DIMMER_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);
                        properties.put("channelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                    }
                        break;
                    case "10C0": // Hue dimmer
                    {
                        String channelId = "ch0000";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.DIMMER_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);
                        properties.put("channelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                    }
                        break;

                    case "101D": // weather station
                    {
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.WEATHER_THING_TYPEUID, device.Serial);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder
                                .create(uid).withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                        + deviceTypeId + "_" + device.Serial)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                    }
                        break;
                    default: {
                        if (fh.dummyThingsEnabled()) {

                            ThingUID uid1 = new ThingUID(FreeAtHomeBindingConstants.DUMMY_THING_TYPEUID, device.Serial);

                            DiscoveryResult discoveryResult1 = DiscoveryResultBuilder
                                    .create(uid1).withLabel("Dummy_" + device.DeviceDisplayName + "_"
                                            + device.DeviceTypeName + "_" + deviceTypeId + "_" + device.Serial)
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
        logger.debug("Stop freeathome background discovery");

    }

    @Override
    public boolean isBackgroundDiscoveryEnabled() {
        return false;
    }

}
