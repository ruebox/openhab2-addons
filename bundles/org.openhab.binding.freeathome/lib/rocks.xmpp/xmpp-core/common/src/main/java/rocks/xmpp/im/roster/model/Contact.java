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

import rocks.xmpp.addr.Jid;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A contact in the user's roster.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/rfcs/rfc6121.html#roster-syntax-items">2.1.2.  Roster Items</a></cite></p>
 * <p>The {@code <query/>} element inside a roster set contains one {@code <item/>} child, and a roster result typically contains multiple {@code <item/>} children. Each {@code <item/>} element describes a unique "roster item" (sometimes also called a "contact").</p>
 * </blockquote>
 * <p>
 * This class is immutable.
 */
public final class Contact implements RosterItem, Comparable<Contact> {

    @XmlAttribute
    private final Boolean approved;

    @XmlJavaTypeAdapter(PendingAdapter.class)
    @XmlAttribute
    private final Boolean ask;

    private final List<String> group = new ArrayList<>();

    @XmlAttribute
    private final Jid jid;

    @XmlAttribute
    private final String name;

    @XmlAttribute
    private final Subscription subscription;

    /**
     * Private default constructor for unmarshalling.
     */
    @SuppressWarnings("unused")
    private Contact() {
        this.jid = null;
        this.name = null;
        this.ask = null;
        this.subscription = null;
        this.approved = null;
    }

    /**
     * Creates a new contact associated with the given JID.
     *
     * @param jid The JID.
     */
    public Contact(Jid jid) {
        this(jid, null);
    }

    /**
     * Creates a new contact associated with the given JID and name.
     *
     * @param jid  The JID.
     * @param name The name.
     */
    public Contact(Jid jid, String name) {
        this(jid, name, Collections.emptyList());
    }

    /**
     * Creates a new contact associated with the given JID, name and groups.
     *
     * @param jid    The JID.
     * @param name   The name.
     * @param groups The groups for this contact.
     */
    public Contact(Jid jid, String name, String... groups) {
        this(jid, name, Arrays.asList(groups));
    }

    /**
     * Creates a new contact associated with the given JID, name and groups.
     *
     * @param jid    The JID.
     * @param name   The name.
     * @param groups The groups for this contact.
     */
    public Contact(Jid jid, String name, Collection<String> groups) {
        this(jid, name, null, null, null, groups);
    }

    /**
     * Creates a new contact from a roster item.
     *
     * @param rosterItem The roster item.
     */
    public Contact(RosterItem rosterItem) {
        this(rosterItem.getJid(), rosterItem.getName(), rosterItem.isPendingOut(), rosterItem.isApproved(), rosterItem.getSubscription(), rosterItem.getGroups());
    }

    /**
     * Creates a new contact associated with the given JID, name and groups.
     *
     * @param jid          The JID.
     * @param name         The name.
     * @param isPending    Indicates, whether the contact's subscription approval is pending.
     * @param approved     Indicates, whether the contact is pre-approved.
     * @param subscription The subscription.
     * @param groups       The groups for this contact.
     */
    public Contact(Jid jid, String name, Boolean isPending, Boolean approved, Subscription subscription, Collection<String> groups) {
        this.jid = Objects.requireNonNull(jid);
        this.name = name;
        this.ask = isPending;
        this.subscription = subscription;
        if (groups != null) {
            this.group.addAll(groups);
        }
        this.approved = approved;
    }

    /**
     * Creates a new contact to send to the server for removal.
     *
     * @param jid Jid of the contact to be removed.
     * @return a contact with only the Jid and {@link SubscriptionState.Subscription#REMOVE} set.
     */
    public static Contact removeContact(Jid jid) {
        return new Contact(jid, null, null, null, Subscription.REMOVE, Collections.emptyList());
    }

    /**
     * Creates an identical contact with only the name changed.
     *
     * @param name The name.
     * @return A new contact object with only the name changed.
     */
    public Contact withName(String name) {
        return new Contact(jid, name, ask, approved, subscription, group);
    }

    /**
     * Creates an identical contact without name.
     *
     * @return A new contact object with only the name changed.
     */
    public Contact withoutName() {
        return new Contact(jid, null, ask, approved, subscription, group);
    }

    /**
     * Creates an identical contact with only the groups changed.
     *
     * @param groups the new groups.
     * @return a new contact with only the groups changed.
     */
    public Contact withGroups(Collection<String> groups) {
        return new Contact(jid, name, ask, approved, subscription, groups);
    }

    /**
     * Creates an identical contact with only the groups changed.
     *
     * @param groups the new groups.
     * @return a new contact with only the groups changed.
     */
    public Contact withGroups(String... groups) {
        return withGroups(groups != null ? Arrays.asList(groups) : null);
    }

    /**
     * Creates an identical contact without groups.
     *
     * @return a new contact with no groups.
     */
    public Contact withoutGroups() {
        return withGroups();
    }

    /**
     * Gets the JID of the contact.
     *
     * @return The JID.
     */
    @Override
    public final Jid getJid() {
        return jid;
    }

    /**
     * Gets the name of the contact.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6121.html#roster-syntax-items-name">2.1.2.4.  Name Attribute</a></cite></p>
     * <p>The 'name' attribute of the {@code <item/>} element specifies the "handle" to be associated with the JID, as determined by the user (not the contact). Although the value of the 'name' attribute MAY have meaning to a human user, it is opaque to the server. However, the 'name' attribute MAY be used by the server for matching purposes within the context of various XMPP extensions (one possible comparison method is that described for XMPP resourceparts in [XMPP-ADDR]).</p>
     * <p>It is OPTIONAL for a client to include the 'name' attribute when adding or updating a roster item.</p>
     * </blockquote>
     *
     * @return The name.
     */
    @Override
    public final String getName() {
        return name;
    }

    /**
     * Gets the subscription attribute.
     *
     * @return The subscription attribute.
     */
    @Override
    public final Subscription getSubscription() {
        return subscription;
    }

    /**
     * Gets the pending state of the contact.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6121.html#roster-syntax-items-ask">2.1.2.2.  Ask Attribute</a></cite></p>
     * <p>The 'ask' attribute of the {@code <item/>} element with a value of "subscribe" is used to signal various subscription sub-states that include a "Pending Out" aspect as described under Section 3.1.2.</p>
     * <p>A server SHOULD include the 'ask' attribute to inform the client of "Pending Out" sub-states. A client MUST NOT include the 'ask' attribute in the roster sets it sends to the server, but instead MUST use presence stanzas of type "subscribe" and "unsubscribe" to manage such sub-states as described under Section 3.1.2. </p>
     * </blockquote>
     *
     * @return True, if a subscription request for the contact is pending, i.e. the contact has not yet approved or denied a subscription request.
     * @deprecated Use {@link #isPendingOut()}
     */
    @Deprecated
    public final boolean isPending() {
        return isPendingOut();
    }

    /**
     * Gets the pending state of the contact.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6121.html#roster-syntax-items-ask">2.1.2.2.  Ask Attribute</a></cite></p>
     * <p>The 'ask' attribute of the {@code <item/>} element with a value of "subscribe" is used to signal various subscription sub-states that include a "Pending Out" aspect as described under Section 3.1.2.</p>
     * <p>A server SHOULD include the 'ask' attribute to inform the client of "Pending Out" sub-states. A client MUST NOT include the 'ask' attribute in the roster sets it sends to the server, but instead MUST use presence stanzas of type "subscribe" and "unsubscribe" to manage such sub-states as described under Section 3.1.2. </p>
     * </blockquote>
     *
     * @return True, if a subscription request for the contact is pending, i.e. the contact has not yet approved or denied a subscription request.
     */
    @Override
    public final boolean isPendingOut() {
        return ask != null && ask;
    }

    /**
     * Roster items on the XMPP layer don't have a notion of a "Pending In" subscription state, therefore this methods always returns false.
     * Pending-in subscription states are not represented in the user's roster at all.
     *
     * @return Always <code>false</code>.
     */
    @Override
    public final boolean isPendingIn() {
        return false;
    }

    /**
     * Gets the subscription pre-approval status.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6121.html#roster-syntax-items-approved">2.1.2.1.  Approved Attribute</a></cite></p>
     * <p>The boolean 'approved' attribute with a value of "true" is used to signal subscription pre-approval as described under Section 3.4</p>
     * </blockquote>
     *
     * @return True, if the contact is pre approved.
     */
    @Override
    public final boolean isApproved() {
        return approved != null && approved;
    }

    /**
     * Gets the groups of the contact.
     *
     * @return The groups.
     */
    @Override
    public final List<String> getGroups() {
        return Collections.unmodifiableList(group);
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Contact)) {
            return false;
        }
        Contact other = (Contact) o;

        return Objects.equals(jid, other.jid)
                && Objects.equals(name, other.name)
                && Objects.equals(subscription, other.subscription)
                && Objects.equals(approved, other.approved)
                && Objects.equals(ask, other.ask)
                && group.size() == other.group.size() && group.containsAll(other.group);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(jid, name, subscription, approved, ask, group);
    }

    /**
     * Compares two contacts with each other by first comparing their names, then their subscription states and eventually their pending states.
     *
     * @param o The other contact.
     * @return The result of the comparison.
     */
    @Override
    public final int compareTo(Contact o) {
        if (this == o) {
            return 0;
        }

        if (o != null) {
            int result;
            if (name != null) {
                if (o.name != null) {
                    result = Collator.getInstance().compare(name, o.name);
                } else {
                    result = -1;
                }
            } else {
                if (o.name != null) {
                    result = 1;
                } else {
                    result = 0;
                }
            }
            if (result == 0) {
                if (subscription != null) {
                    if (o.subscription != null) {
                        result = subscription.compareTo(o.subscription);
                    } else {
                        result = -1;
                    }
                } else {
                    if (o.subscription != null) {
                        result = 1;
                    } else {
                        result = 0;
                    }
                }
            }
            if (result == 0) {
                return Boolean.compare(isPendingOut(), o.isPendingOut());
            }
            return result;
        } else {
            return -1;
        }
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder(jid.toString());
        if (name != null) {
            sb.append(" (").append(name).append(')');
        }
        return sb.toString();
    }

    /**
     * The implementation of the 'subscription' attribute.
     */

    @XmlEnum
    private enum Ask {
        @XmlEnumValue("subscribe")
        SUBSCRIBE
    }

    /**
     * Converts the "ask" attribute, which can only have a single value ("subscribe") to a boolean for convenience.
     */
    private static final class PendingAdapter extends XmlAdapter<Ask, Boolean> {

        @Override
        public Boolean unmarshal(Ask v) {
            return v == Ask.SUBSCRIBE;
        }

        @Override
        public Ask marshal(Boolean v) {
            return v != null && v ? Ask.SUBSCRIBE : null;
        }
    }
}
