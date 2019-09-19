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
package org.openhab.binding.freeathome.internal.xmpp.rocks.extensions.abb.com.protocol.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML scheme extension for XMPP update events: abb.com.protocol.update
 * Implementation of update element in the http://abb.com/protocol/update namespace
 * as XEP-0163: Personal Eventing Protocol
 * based on https://sco0ter.bitbucket.io/babbler/xep/pep.html
 *
 * @author kjoglum - Initial contribution
 *         <update xmlns='http://abb.com/protocol/update'>
 *         <data>
 *         DATA
 *         </data>
 *         </update>
 *
 * @see <a href="http://xmpp.org/extensions/xep-0163.html">XEP-0163: Personal Eventing Protocol</a>
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Device {
    @XmlAttribute
    private String softwareVersion;

    @XmlAttribute
    private String domainAddress;

    @XmlAttribute
    private String serialNumber;

    @XmlAttribute
    private String state;

    @XmlAttribute
    private String commissioningState;

    @XmlElementWrapper(name = "channels")
    @XmlElement(name = "channel")
    private List<Channel> channels;

    public Device() {
        this.channels = new ArrayList<Channel>();
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getSoftwareVersion() {
        return this.softwareVersion;
    }

    public void setDomainAddress(String domainAddress) {
        this.domainAddress = domainAddress;
    }

    public String getDomainAddress() {
        return this.domainAddress;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public void setCommissioningState(String commissioningState) {
        this.commissioningState = commissioningState;
    }

    public String getCommissioningState() {
        return this.commissioningState;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public List<Channel> getChannels() {
        return this.channels;
    }

}