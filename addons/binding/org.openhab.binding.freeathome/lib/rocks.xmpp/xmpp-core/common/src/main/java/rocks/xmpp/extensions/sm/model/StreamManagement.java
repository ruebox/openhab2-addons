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

package rocks.xmpp.extensions.sm.model;

import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.core.stream.model.StreamElement;
import rocks.xmpp.core.stream.model.StreamFeature;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * The implementation of the {@code <sm/>} element in the {@code urn:xmpp:sm:3} namespace.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0198.html">XEP-0198: Stream Management</a>
 * @see <a href="https://xmpp.org/extensions/xep-0198.html#schemas">XML Schema</a>
 */
@XmlRootElement(name = "sm")
@XmlSeeAlso({StreamManagement.Answer.class, StreamManagement.Enable.class, StreamManagement.Enabled.class, StreamManagement.Failed.class, StreamManagement.Request.class, StreamManagement.Resume.class, StreamManagement.Resumed.class})
public final class StreamManagement extends StreamFeature {

    /**
     * The {@code <r/>} element in the {@code urn:xmpp:sm:3} namespace.
     */
    public static final Request REQUEST = new Request();

    /**
     * urn:xmpp:sm:3
     */
    public static final String NAMESPACE = "urn:xmpp:sm:3";

    @XmlElement(name = "required")
    private final String required;

    public StreamManagement() {
        this.required = null;
    }

    public StreamManagement(Boolean required) {
        this.required = required != null && required ? "" : null;
    }

    @Override
    public final boolean isMandatory() {
        return required != null;
    }

    @Override
    public final int getPriority() {
        return 4;
    }

    @Override
    public final String toString() {
        return "Stream Management";
    }

    /**
     * The implementation of the {@code <resumed/>} element in the {@code urn:xmpp:sm:3} namespace.
     */
    @XmlRootElement(name = "resumed")
    public static final class Resumed extends AbstractResume {

        private Resumed() {
            super(0, null);
        }

        /**
         * @param lastHandledStanza Identifies the sequence number of the last handled stanza sent over the former stream from the server to the client.
         * @param prevId            The stream id of the former stream.
         */
        public Resumed(long lastHandledStanza, String prevId) {
            super(lastHandledStanza, prevId);
        }
    }

    /**
     * The implementation of the {@code <resume/>} element in the {@code urn:xmpp:sm:3} namespace.
     */
    @XmlRootElement(name = "resume")
    public static final class Resume extends AbstractResume {

        private Resume() {
            super(0, null);
        }

        /**
         * @param lastHandledStanza Identifies the sequence number of the last handled stanza sent over the former stream from the server to the client.
         * @param prevId            The stream id of the former stream.
         */
        public Resume(long lastHandledStanza, String prevId) {
            super(lastHandledStanza, prevId);
        }
    }

    /**
     * The implementation of the {@code <r/>} element in the {@code urn:xmpp:sm:3} namespace.
     * <p>
     * This class is a singleton.
     *
     * @see #REQUEST
     */
    @XmlRootElement(name = "r")
    @XmlType(factoryMethod = "create")
    public static final class Request implements StreamElement {

        private Request() {
        }

        private static Request create() {
            return REQUEST;
        }

        @Override
        public final String toString(){
            return "Stream Management Request";
        }
    }

    /**
     * The implementation of the {@code <failed/>} element in the {@code urn:xmpp:sm:3} namespace.
     */
    @XmlRootElement(name = "failed")
    public static final class Failed extends LastHandledStanza {

        @XmlElementRef
        private final Condition stanzaError;

        public Failed() {
            this(null);
        }

        public Failed(Condition stanzaError) {
            this(stanzaError, null);
        }

        public Failed(Condition stanzaError, Long h) {
            super(h);
            this.stanzaError = stanzaError;
        }

        /**
         * Gets the stanza error.
         *
         * @return The stanza error. Maybe null.
         */
        public final Condition getError() {
            return stanzaError;
        }
    }

    /**
     * The implementation of the {@code <enabled/>} element in the {@code urn:xmpp:sm:3} namespace.
     */
    @XmlRootElement(name = "enabled")
    public static final class Enabled implements StreamElement {

        @XmlAttribute(name = "id")
        private final String id;

        @XmlAttribute(name = "resume")
        private final Boolean resume;

        @XmlAttribute(name = "max")
        private final Integer max;

        @XmlAttribute(name = "location")
        private final String location;

        public Enabled() {
            this(null, null);
        }

        /**
         * @param id     The stream id.
         * @param resume If the server allows session resumption.
         */
        public Enabled(String id, Boolean resume) {
            this(id, resume, null, null);
        }

        /**
         * @param id       The stream id.
         * @param resume   If the server allows session resumption.
         * @param max      The server's preferred maximum resumption time.
         * @param location The server's preferred IP address or hostname (optionally with a port) for reconnection.
         */
        public Enabled(String id, Boolean resume, Integer max, String location) {
            this.id = id;
            this.resume = resume;
            this.max = max;
            this.location = location;
        }

        /**
         * The stream id.
         *
         * @return The stream id.
         */
        public final String getId() {
            return id;
        }

        /**
         * If the server allows session resumption.
         *
         * @return If the server allows session resumption.
         */
        public final boolean isResume() {
            return resume != null && resume;
        }

        /**
         * The server's preferred IP address or hostname (optionally with a port) for reconnection.
         *
         * @return The server's preferred IP address or hostname (optionally with a port) for reconnection
         */
        public final String getLocation() {
            return location;
        }

        /**
         * Gets the server's preferred maximum resumption time.
         *
         * @return The server's preferred maximum resumption time.
         */
        public final Integer getMax() {
            return max;
        }
    }

    /**
     * The implementation of the {@code <enable/>} element in the {@code urn:xmpp:sm:3} namespace.
     */
    @XmlRootElement(name = "enable")
    public static final class Enable implements StreamElement {

        @XmlAttribute(name = "resume")
        private final Boolean resume;

        @XmlAttribute(name = "max")
        private final Integer max;

        public Enable() {
            this(null, null);
        }

        /**
         * @param resume To request that the stream will be resumable.
         */
        public Enable(Boolean resume) {
            this(resume, null);
        }

        /**
         * @param resume To request that the stream will be resumable.
         * @param max    The client's preferred maximum resumption time in seconds.
         */
        public Enable(Boolean resume, Integer max) {
            this.resume = resume;
            this.max = max;
        }
    }

    /**
     * The implementation of the {@code <a/>} element in the {@code urn:xmpp:sm:3} namespace.
     */
    @XmlRootElement(name = "a")
    public static final class Answer extends LastHandledStanza {

        private Answer() {
            super(null);
        }

        public Answer(long h) {
            super(h);
        }

        @Override
        public final String toString() {
            return "Stream Management Answer: " + getLastHandledStanza();
        }
    }

    @XmlTransient
    private abstract static class LastHandledStanza implements StreamElement {

        @XmlAttribute(name = "h")
        private final Long lastHandledStanza;

        private LastHandledStanza(Long lastHandledStanza) {
            this.lastHandledStanza = lastHandledStanza;
        }

        /**
         * Gets the last handled stanza.
         *
         * @return The last handled stanza.
         */
        public final Long getLastHandledStanza() {
            return lastHandledStanza;
        }
    }

    @XmlTransient
    private abstract static class AbstractResume extends LastHandledStanza {

        @XmlAttribute(name = "previd")
        private final String previd;

        private AbstractResume(long lastHandledStanza, String previd) {
            super(lastHandledStanza);
            this.previd = previd;
        }

        /**
         * Gets the stream id of the former stream.
         *
         * @return The stream id of the former stream.
         */
        public final String getPreviousId() {
            return previd;
        }
    }
}
