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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DataPoint {

    @XmlAttribute
    private String state;

    @XmlAttribute
    private String i;

    @XmlElement
    private String value;

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public void setI(String i) {
        this.i = i;
    }

    public String getI() {
        return this.i;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}