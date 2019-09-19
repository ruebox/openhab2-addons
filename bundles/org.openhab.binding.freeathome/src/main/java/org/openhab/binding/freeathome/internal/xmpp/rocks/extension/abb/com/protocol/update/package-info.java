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
 * @author kjoglum - Initial contribution
 *
 *         <update xmlns='http://abb.com/protocol/update'>
 *         <data>
 *         DATA
 *         </data>
 *         </update>
 *
 * @author kjoglum - Initial contribution
 * @see <a href="http://xmpp.org/extensions/xep-0163.html">XEP-0163: Personal Eventing Protocol</a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSchema(namespace = Update.NAMESPACE, elementFormDefault = XmlNsForm.QUALIFIED)

package org.openhab.binding.freeathome.internal.xmpp.rocks.extension.abb.com.protocol.update;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
