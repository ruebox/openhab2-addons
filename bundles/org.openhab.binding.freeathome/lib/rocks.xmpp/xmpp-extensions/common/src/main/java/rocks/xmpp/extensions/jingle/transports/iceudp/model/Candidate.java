/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 Christian Schudt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package rocks.xmpp.extensions.jingle.transports.iceudp.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * @author Christian Schudt
 */
public final class Candidate extends RemoteCandidate {


    @XmlAttribute
    private Short foundation;

    @XmlAttribute
    private Short generation;

    @XmlAttribute
    private String id;

    @XmlAttribute
    private Short network;

    @XmlAttribute
    private Integer priority;

    @XmlAttribute
    private String protocol;

    @XmlAttribute(name = "rel-address")
    private String relatedAddress;

    @XmlAttribute(name = "rel-port")
    private Integer relatedPort;

    @XmlAttribute
    private Type type;

    public Candidate() {
    }

    public Candidate(Integer pComponent, String pIp, Integer pPort, Short pFoundation, Short pGeneration, String pId,
        Short pNetwork, Integer pPriority, String pProtocol, String pRelatedAddress, Integer pRelatedPort, Type pType) {
        super(pComponent, pIp, pPort);
        foundation = pFoundation;
        generation = pGeneration;
        id = pId;
        network = pNetwork;
        priority = pPriority;
        protocol = pProtocol;
        relatedAddress = pRelatedAddress;
        relatedPort = pRelatedPort;
        type = pType;
    }

    public Short getFoundation() {
        return foundation;
    }

    public Short getGeneration() {
        return generation;
    }

    public String getId() {
        return id;
    }

    public Short getNetwork() {
        return network;
    }

    public Integer getPriority() {
        return priority;
    }

    public String getProtocol() {
        return protocol;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        @XmlEnumValue("host")
        HOST,
        @XmlEnumValue("prflx")
        PRFLX,
        @XmlEnumValue("relay")
        RELAY,
        @XmlEnumValue("srflx")
        SRFLX
    }
}
