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

import rocks.xmpp.extensions.jingle.transports.model.TransportMethod;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Schudt
 */
@XmlRootElement(name = "transport")
public final class IceUdpTransportMethod extends TransportMethod {

    /**
     * urn:xmpp:jingle:transports:ice-udp:1
     */
    public static final String NAMESPACE = "urn:xmpp:jingle:transports:ice-udp:1";

    private final List<Candidate> candidate = new ArrayList<>();

    public IceUdpTransportMethod() {
    }

    public IceUdpTransportMethod(String pPwd, String pUfrag, List<Candidate> pCandidates) {
    	super();
    	pwd = pPwd;
    	ufrag = pUfrag;
    	candidate.addAll(pCandidates);
    }

    @XmlAttribute
    private String pwd;

    @XmlAttribute
    private String ufrag;

    @XmlElement(name = "remote-candidate")
    private RemoteCandidate remoteCandidate;

    public List<Candidate> getCandidates() {
        return candidate;
    }

    public String getPassword() {
        return pwd;
    }

    public String getUserFragment() {
        return ufrag;
    }
}
