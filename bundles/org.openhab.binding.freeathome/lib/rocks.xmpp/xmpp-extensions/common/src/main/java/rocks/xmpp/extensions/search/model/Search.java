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

package rocks.xmpp.extensions.search.model;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.rsm.model.ResultSetManagement;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The implementation of the {@code <query/>} element in the {@code jabber:iq:search} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0055.html">XEP-0055: Jabber Search</a>
 * @see <a href="https://xmpp.org/extensions/xep-0055.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "query")
public final class Search {

    /**
     * jabber:iq:search
     */
    public static final String NAMESPACE = "jabber:iq:search";

    private final List<Item> item = new ArrayList<>();

    private final String instructions;

    private final String first;

    private final String last;

    private final String nick;

    private final String email;

    @XmlElementRef
    private final DataForm form;

    @XmlElementRef
    private final ResultSetManagement resultSet;

    /**
     * Creates an empty search request.
     */
    public Search() {
        this(null, null, null, null);
    }

    /**
     * Creates a search request, consisting of multiple search parameters.
     *
     * @param first The first name.
     * @param last  The last name.
     * @param nick  The nick name.
     * @param email The email.
     */
    public Search(String first, String last, String nick, String email) {
        this(first, last, nick, email, null, null, null);
    }

    /**
     * Creates a search request, consisting of multiple search parameters.
     *
     * @param first        The first name.
     * @param last         The last name.
     * @param nick         The nick name.
     * @param email        The email.
     * @param resultSet    The result set information.
     * @param instructions The instructions.
     * @param dataForm     The data form.
     */
    public Search(String first, String last, String nick, String email, ResultSetManagement resultSet, String instructions, DataForm dataForm) {
        this.first = first;
        this.last = last;
        this.nick = nick;
        this.email = email;
        this.resultSet = resultSet;
        this.instructions = instructions;
        this.form = dataForm;

    }

    /**
     * Gets the search instructions.
     *
     * @return The search instructions.
     */
    public final String getInstructions() {
        return instructions;
    }

    /**
     * Gets the first name.
     *
     * @return The first name.
     */
    public final String getFirst() {
        return first;
    }

    /**
     * Gets the last name.
     *
     * @return The last name.
     */
    public final String getLast() {
        return last;
    }

    /**
     * Gets the nick name.
     *
     * @return The nick name.
     */
    public final String getNick() {
        return nick;
    }

    /**
     * Gets the email address.
     *
     * @return The email address.
     */
    public final String getEmail() {
        return email;
    }

    /**
     * Gets the items of a search result.
     *
     * @return The items.
     */
    public final List<Item> getItems() {
        return Collections.unmodifiableList(item);
    }

    /**
     * Gets additional information for the search, e.g. for gender.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/extensions/xep-0055.html#extensibility">3. Extensibility</a></cite></p>
     * <p>The fields defined in the 'jabber:iq:search' namespace are strictly limited to those specified in the schema. If a host needs to gather additional information, Data Forms SHOULD be used; a host MUST NOT add new fields to the 'jabber:iq:search' namespace. Support for extensibility via Data Forms is RECOMMENDED, but is not required for compliance with this document.</p>
     * </blockquote>
     *
     * @return The data form, which contains additional information.
     */
    public final DataForm getAdditionalInformation() {
        return form;
    }

    /**
     * Gets the result set information.
     *
     * @return The result set.
     * @see <a href="https://xmpp.org/extensions/xep-0059.html">XEP-0059: Result Set Management</a>
     */
    public final ResultSetManagement getResultSet() {
        return resultSet;
    }

    /**
     * The implementation of a search result item.
     * <p>
     * This class is immutable.
     */
    public static final class Item {

        @XmlAttribute
        private final Jid jid;

        private final String first;

        private final String last;

        private final String nick;

        private final String email;

        private Item() {
            this.jid = null;
            this.first = null;
            this.last = null;
            this.nick = null;
            this.email = null;
        }

        public Item(Jid jid, String first, String last, String nick, String email) {
            this.jid = Objects.requireNonNull(jid);
            this.first = first;
            this.last = last;
            this.nick = nick;
            this.email = email;
        }

        /**
         * Gets the first name.
         *
         * @return The first name.
         */
        public final String getFirst() {
            return first;
        }

        /**
         * Gets the last name.
         *
         * @return The last name.
         */
        public final String getLast() {
            return last;
        }

        /**
         * Gets the nick name.
         *
         * @return The nick name.
         */
        public final String getNick() {
            return nick;
        }

        /**
         * Gets the email address.
         *
         * @return The email address.
         */
        public final String getEmail() {
            return email;
        }

        /**
         * Gets the JID.
         *
         * @return The JID.
         */
        public final Jid getJid() {
            return jid;
        }
    }
}
