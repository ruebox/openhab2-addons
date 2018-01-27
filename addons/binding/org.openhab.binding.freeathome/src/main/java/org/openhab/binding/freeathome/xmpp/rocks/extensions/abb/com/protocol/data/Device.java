/**
 * Copyright (c) 2014-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.freeathome.xmpp.rocks.extensions.abb.com.protocol.data;

/**
 * XML scheme extension for XMPP update events: abb.com.protocol.update
 * Implementation of update element in the http://abb.com/protocol/update namespace
 * as XEP-0163: Personal Eventing Protocol
 * based on https://sco0ter.bitbucket.io/babbler/xep/pep.html
 *
 *
 *         <update xmlns='http://abb.com/protocol/update'>
 *         <data>
 *         DATA
 *         </data>
 *         </update>
 *
 * @author ruebox
 * @see <a href="http://xmpp.org/extensions/xep-0163.html">XEP-0163: Personal Eventing Protocol</a>
 */
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

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

    void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    String getSoftwareVersion() {
        return this.softwareVersion;
    }

    void setDomainAddress(String domainAddress) {
        this.domainAddress = domainAddress;
    }

    String getDomainAddress() {
        return this.domainAddress;
    }

    void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    String getSerialNumber() {
        return this.serialNumber;
    }

    void setState(String state) {
        this.state = state;
    }

    String getState() {
        return this.state;
    }

    void setCommissioningState(String commissioningState) {
        this.commissioningState = commissioningState;
    }

    String getCommissioningState() {
        return this.commissioningState;
    }

}