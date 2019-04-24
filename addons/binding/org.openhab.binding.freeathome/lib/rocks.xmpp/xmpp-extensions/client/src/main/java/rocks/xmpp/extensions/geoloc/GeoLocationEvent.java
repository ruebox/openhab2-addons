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

package rocks.xmpp.extensions.geoloc;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.extensions.geoloc.model.GeoLocation;

import java.util.EventObject;
import java.util.function.Consumer;

/**
 * This event notifies listeners, when a geo location has been updated.
 *
 * @author Christian Schudt
 * @see GeoLocationManager#addGeoLocationListener(Consumer)
 */
public final class GeoLocationEvent extends EventObject {
    private final transient GeoLocation geoLocation;

    private final Jid publisher;

    /**
     * Constructs a message delivered event.
     *
     * @param source      The object on which the event initially occurred.
     * @param geoLocation The geo location.
     * @param publisher   The publisher.
     * @throws IllegalArgumentException if source is null.
     */
    GeoLocationEvent(Object source, GeoLocation geoLocation, Jid publisher) {
        super(source);
        this.geoLocation = geoLocation;
        this.publisher = publisher;
    }

    /**
     * Gets the geo location.
     *
     * @return The geo location.
     */
    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    /**
     * Gets the publisher, who published his or her new geo location.
     *
     * @return The publisher.
     */
    public Jid getPublisher() {
        return publisher;
    }
}
