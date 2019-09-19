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
package org.openhab.binding.freeathome.internal.xmpp.rocks.extension.abb.com.protocol.update;

import javax.xml.bind.annotation.XmlElement;
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
 * @author kjoglum - Initial contribution
 * @see <a href="http://xmpp.org/extensions/xep-0163.html">XEP-0163: Personal Eventing Protocol</a>
 */
@XmlRootElement(name = "update")
public final class Update {

    /**
     * http://abb.com/protocol/update
     */

    public static final String NAMESPACE = "http://abb.com/protocol/update";

    @XmlElement(required = true)
    private final String data;

    /**
     * Creates an empty geolocation element.
     */
    private Update() {
        this.data = null;
    }

    private Update(Builder builder) {
        this.data = builder.data;
    }

    /**
     * Creates the builder to build a geo location.
     *
     * @return The builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets the horizontal GPS error in meters.
     *
     * @return The accuracy.
     */
    public final String getData() {
        return data;
    }

    /**
     * A builder class to which builds geo location objects.
     */
    public static final class Builder {

        private String data;

        private Builder() {
        }

        /**
         * Sets the natural language of location data.
         *
         * @param language The language.
         * @return The builder.
         */
        public Builder data(String data) {
            this.data = data;
            return this;
        }

        /**
         * Builds the geo location.
         *
         * @return The geo location.
         */
        public Update build() {
            return new Update(this);
        }
    }
}