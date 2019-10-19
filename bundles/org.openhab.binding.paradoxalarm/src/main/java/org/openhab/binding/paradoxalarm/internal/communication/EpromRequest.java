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
package org.openhab.binding.paradoxalarm.internal.communication;

import org.openhab.binding.paradoxalarm.internal.communication.messages.IPPacketPayload;
import org.openhab.binding.paradoxalarm.internal.model.EntityType;

/**
 * The {@link EpromRequest}. Request for retrieving EPROM data from Paradox system.
 *
 * @author Konstantin Polihronov - Initial contribution
 */
public class EpromRequest extends Request {

    private EntityType entityType;
    private int entityId;

    public EpromRequest(int entityId, EntityType entityType, IPPacketPayload payload) {
        super(RequestType.EPROM, payload);
        this.entityId = entityId;
        this.entityType = entityType;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public int getEntityId() {
        return entityId;
    }

}
