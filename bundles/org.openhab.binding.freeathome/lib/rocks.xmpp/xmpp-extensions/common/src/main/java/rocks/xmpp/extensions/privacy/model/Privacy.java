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

package rocks.xmpp.extensions.privacy.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The implementation of the the {@code <query/>} element in the {@code jabber:iq:privacy} namespace.
 * <p>
 * This class contains information about the active and default list and holds the privacy lists.
 * </p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0016.html">XEP-0016: Privacy Lists</a>
 * @see <a href="https://xmpp.org/extensions/xep-0016.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "query")
public final class Privacy {

    /**
     * jabber:iq:privacy
     */
    public static final String NAMESPACE = "jabber:iq:privacy";

    private final List<PrivacyList> list = new ArrayList<>();

    @XmlJavaTypeAdapter(ActiveNameAdapter.class)
    @XmlElement(name = "active")
    private final String activeName;

    @XmlJavaTypeAdapter(DefaultNameAdapter.class)
    @XmlElement(name = "default")
    private final String defaultName;

    /**
     * Creates an empty privacy element.
     */
    public Privacy() {
        this(null, null, null);
    }

    /**
     * Creates a privacy element with one or more privacy lists.
     *
     * @param privacyLists The privacy list(s).
     */
    public Privacy(PrivacyList... privacyLists) {
        this(null, null, Arrays.asList(privacyLists));
    }

    /**
     * Creates a privacy element with one or more privacy lists and an active and default name.
     *
     * @param activeName   The active name.
     * @param defaultName  The default name.
     * @param privacyLists The privacy list(s).
     */
    public Privacy(String activeName, String defaultName, Collection<PrivacyList> privacyLists) {
        this.activeName = activeName;
        this.defaultName = defaultName;
        if (privacyLists != null) {
            this.list.addAll(privacyLists);
        }
    }

    /**
     * Creates a privacy element with an active list.
     *
     * @param active The active list name. Pass an empty string if you want to decline the use of an active list.
     * @return The privacy element.
     */
    public static Privacy withActive(String active) {
        return new Privacy(Objects.requireNonNull(active), null, null);
    }

    /**
     * Creates a privacy element with a default list.
     *
     * @param defaultName The default list name. Pass an empty string if you want to decline the use of a default list.
     * @return The privacy element.
     */
    public static Privacy withDefault(String defaultName) {
        return new Privacy(null, Objects.requireNonNull(defaultName), null);
    }

    /**
     * Gets the active list name.
     *
     * @return The active list name.
     */
    public final String getActiveName() {
        return activeName;
    }

    /**
     * Sets the default list name.
     *
     * @return The default list name.
     */
    public final String getDefaultName() {
        return defaultName;
    }

    /**
     * Gets the privacy lists.
     *
     * @return The privacy lists.
     */
    public final List<PrivacyList> getPrivacyLists() {
        return Collections.unmodifiableList(list);
    }

    @Override
    public final String toString() {
        return "Privacy lists: " + list;
    }

    private static final class Active {
        @XmlAttribute
        private String name;
    }

    private static final class Default {
        @XmlAttribute
        private String name;
    }

    private static final class ActiveNameAdapter extends XmlAdapter<Active, String> {

        @Override
        public final String unmarshal(Active v) {
            if (v != null) {
                return v.name;
            }
            return null;
        }

        @Override
        public final Active marshal(String v) {
            if (v != null) {
                Active active = new Active();
                active.name = v.isEmpty() ? null : v;
                return active;
            }
            return null;
        }
    }

    private static final class DefaultNameAdapter extends XmlAdapter<Default, String> {

        @Override
        public final String unmarshal(Default v) {
            if (v != null) {
                return v.name;
            }
            return null;
        }

        @Override
        public final Default marshal(String v) {
            if (v != null) {
                Default def = new Default();
                def.name = v.isEmpty() ? null : v;
                return def;
            }
            return null;
        }
    }
}
