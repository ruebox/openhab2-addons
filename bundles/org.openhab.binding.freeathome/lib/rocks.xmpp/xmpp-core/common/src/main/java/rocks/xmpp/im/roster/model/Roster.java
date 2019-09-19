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

package rocks.xmpp.im.roster.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The implementation of the roster.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/rfcs/rfc6121.html#roster-syntax">2.1.  Syntax and Semantics</a></cite></p>
 * <p>Rosters are managed using {@code <iq/>} stanzas (see Section 8.2.3 of [XMPP-CORE]), specifically by means of a {@code <query/>} child element qualified by the 'jabber:iq:roster' namespace. The detailed syntax and semantics are defined in the following sections.</p>
 * </blockquote>
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 */
@XmlRootElement(name = "query")
public final class Roster {

    /**
     * jabber:iq:roster
     */
    public static final String NAMESPACE = "jabber:iq:roster";

    private final List<Contact> item = new ArrayList<>();

    @XmlAttribute
    private final String ver;

    public Roster() {
        this(null, null);
    }

    public Roster(String ver) {
        this(null, ver);
    }

    public Roster(Collection<Contact> contacts) {
        this(contacts, null);
    }

    public Roster(Collection<Contact> contacts, String version) {
        if (contacts != null) {
            item.addAll(contacts);
        }
        this.ver = version;
    }

    public Roster(Contact... contacts) {
        this(Arrays.asList(contacts));
    }

    /**
     * Gets the roster version.
     *
     * @return The roster version.
     */
    public final String getVersion() {
        return ver;
    }

    /**
     * Gets the contacts.
     *
     * @return The contacts.
     */
    public final List<Contact> getContacts() {
        return Collections.unmodifiableList(item);
    }

    @Override
    public final String toString() {
        return "Roster: " + item.toString();
    }
}
