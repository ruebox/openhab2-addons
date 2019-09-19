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

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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
 *
 *         <update xmlns='http://abb.com/protocol/update'>
 *         <data>
 *         DATA
 *         </data>
 *         </update>
 *
 * @see <a href="http://xmpp.org/extensions/xep-0163.html">XEP-0163: Personal Eventing Protocol</a>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "project")
public class Project {

    @XmlAttribute
    private String timeStamp;

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String mrhaVersion;

    @XmlElement
    private String descriptions;

    @XmlElementWrapper(name = "devices")
    @XmlElement(name = "device")
    private List<Device> devices;

    @XmlElementWrapper(name = "timerPrograms")
    @XmlElement(name = "timerProgram")
    private List<String> timerPrograms;

    public Project() {
        devices = new ArrayList<Device>();
        timerPrograms = new ArrayList<String>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public void setDefault() {
        this.timeStamp = "time";
        this.mrhaVersion = "mrha";
        this.type = "type";
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setMrhaVersion(String mrhaVersion) {
        this.mrhaVersion = mrhaVersion;
    }

    public String getMrhaVersion() {
        return this.mrhaVersion;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public List<Device> getDevices() {
        return this.devices;
    }

    public void setTimerPrograms(List<String> timerPrograms) {
        this.timerPrograms = timerPrograms;
    }

    public List<String> getTimerPrograms() {
        return this.timerPrograms;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getDescriptions() {
        return this.descriptions;
    }

    public static final class Builder {

        private Builder() {
        }

        public Project build(String xmlString) throws JAXBException {
            Project p = null;
            JAXBContext context;

            context = JAXBContext.newInstance(
                    org.openhab.binding.freeathome.internal.xmpp.rocks.extensions.abb.com.protocol.data.Project.class);

            /*
             * Marshaller m = context.createMarshaller();
             * // for pretty-print XML in JAXB
             * m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
             * org.openhab.binding.freeathome.xmpp.rocks.extensions.abb.com.protocol.data.Project u1 = new
             * org.openhab.binding.freeathome.xmpp.rocks.extensions.abb.com.protocol.data.Project();
             * u1.setDefault();
             * m.marshal(u1, new File("/home/rue/test.xml"));
             */

            Unmarshaller u = context.createUnmarshaller();

            byte[] bytes = xmlString.getBytes();
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            InputStreamReader isr = new InputStreamReader(bais);

            p = (org.openhab.binding.freeathome.internal.xmpp.rocks.extensions.abb.com.protocol.data.Project) u
                    .unmarshal(isr);

            return p;
        }
    }
}
