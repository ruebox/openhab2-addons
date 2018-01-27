/**
 * Copyright (c) 2014-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.freeathome.xmpp.rocks.extension.abb.com.protocol.update;

import java.util.EventObject;

import rocks.xmpp.addr.Jid;

/**
 * XML scheme extension for XMPP update events: abb.com.protocol.update
 * Implementation of update element in the http://abb.com/protocol/update namespace
 * as XEP-0163: Personal Eventing Protocol
 * based on https://sco0ter.bitbucket.io/babbler/xep/pep.html
 *
 *
 * <update xmlns='http://abb.com/protocol/update'>
 * <data>
 * DATA
 * </data>
 * </update>
 *
 * @author ruebox
 * @see <a href="http://xmpp.org/extensions/xep-0163.html">XEP-0163: Personal Eventing Protocol</a>
 */

public final class UpdateEvent extends EventObject {
    private final Update update;

    private final Jid publisher;

    UpdateEvent(Object source, Update update, Jid publisher) {
        super(source);
        this.update = update;
        this.publisher = publisher;
    }

    public Update getUpdate() {
        return update;
    }

    public Jid getPublisher() {
        return publisher;
    }
}