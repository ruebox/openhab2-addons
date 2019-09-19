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

package rocks.xmpp.core.sasl.model;

import rocks.xmpp.core.stream.model.StreamFeature;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents the {@code <mechanisms/>} element as described in <a href="https://xmpp.org/rfcs/rfc6120.html#sasl-process-stream">Exchange of Stream Headers and Stream Features</a>
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 */
@XmlRootElement
@XmlSeeAlso({Abort.class, Auth.class, Challenge.class, Failure.class, Response.class, Success.class})
public final class Mechanisms extends StreamFeature {

    private final List<String> mechanism = new ArrayList<>();

    private Mechanisms() {
    }

    public Mechanisms(Collection<String> mechanisms) {
        this.mechanism.addAll(mechanisms);
    }

    /**
     * Gets the list of mechanisms supported by the server.
     *
     * @return The list of mechanisms.s
     */
    public final List<String> getMechanisms() {
        return Collections.unmodifiableList(mechanism);
    }

    /**
     * Returns always true, because SASL is mandatory to negotiate.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#sasl-rules-mtn">6.3.1.  Mandatory-to-Negotiate</a></cite></p>
     * <p>The parties to a stream MUST consider SASL as mandatory-to-negotiate.</p>
     * </blockquote>
     *
     * @return True. This feature is always mandatory to negotiate.
     */
    @Override
    public final boolean isMandatory() {
        return true;
    }

    @Override
    public final int getPriority() {
        return 1;
    }

    @Override
    public final boolean requiresRestart() {
        return true;
    }

    @Override
    public final String toString() {
        return "SASL mechanisms: " + mechanism;
    }
}
