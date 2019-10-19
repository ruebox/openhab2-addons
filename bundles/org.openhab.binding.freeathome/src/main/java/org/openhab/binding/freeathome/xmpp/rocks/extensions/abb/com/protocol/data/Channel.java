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
public class Channel {
    @XmlAttribute
    private String state;

    @XmlAttribute
    private String i;

    @XmlAttribute
    private String cid;

    @XmlElementWrapper(name = "inputs")
    @XmlElement(name = "dataPoint")
    private List<DataPoint> inputs;

    @XmlElementWrapper(name = "outputs")
    @XmlElement(name = "dataPoint")
    private List<DataPoint> outputs;

    @XmlElementWrapper(name = "parameters")
    @XmlElement(name = "parameter")
    private List<String> parameters;

    public Channel() {
        this.inputs = new ArrayList<DataPoint>();
        this.outputs = new ArrayList<DataPoint>();
        this.parameters = new ArrayList<String>();
    }

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

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCid() {
        return this.cid;
    }

    public void setInputs(List<DataPoint> inputs) {
        this.inputs = inputs;
    }

    public List<DataPoint> getInputs() {
        return this.outputs;
    }

    public void setOutputs(List<DataPoint> outputs) {
        this.outputs = outputs;
    }

    public List<DataPoint> getOutputs() {
        return this.outputs;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public List<String> getParameters() {
        return this.parameters;
    }

}