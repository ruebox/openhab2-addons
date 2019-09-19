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

/**
 * XML scheme extension for XMPP update events: abb.com.protocol.update
 * Implementation of update element in the http://abb.com/protocol/update namespace
 * as XEP-0163: Personal Eventing Protocol
 * based on https://sco0ter.bitbucket.io/babbler/xep/pep.html
 *
 * <update xmlns='http://abb.com/protocol/update'>
 * <data>
 * DATA
 * </data>
 * </update>
 *
 * @author kjoglum - Initial contribution
 * @see <a href="http://xmpp.org/extensions/xep-0163.html">XEP-0163: Personal Eventing Protocol</a>
 */
package org.openhab.binding.freeathome.internal.xmpp.rocks.extensions.abb.com.protocol.data;