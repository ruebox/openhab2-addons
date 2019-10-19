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

import rocks.xmpp.addr.Jid;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.text.Collator;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The implementation of a privacy list.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/extensions/xep-0016.html#protocol">2. Protocol</a></cite></p>
 * <p>Most instant messaging systems have found it necessary to implement some method for users to block communications from particular other users (this is also required by sections 5.1.5, 5.1.15, 5.3.2, and 5.4.10 of RFC 2779 [3]. In XMPP this is done by managing one's privacy lists using the 'jabber:iq:privacy' namespace.</p>
 * </blockquote>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0016.html">XEP-0016: Privacy Lists</a>
 */
public final class PrivacyList implements Comparable<PrivacyList> {

    private final List<PrivacyRule> item = new ArrayList<>();

    @XmlAttribute
    private final String name;

    @XmlTransient
    private boolean isActive;

    @XmlTransient
    private boolean isDefault;

    private PrivacyList() {
        this.name = null;
    }

    /**
     * Creates a privacy list with rules.
     *
     * @param name  The privacy list's name.
     * @param items The privacy rules.
     */
    public PrivacyList(String name, Collection<PrivacyRule> items) {
        this.name = Objects.requireNonNull(name);
        if (items != null) {
            this.item.addAll(items);
        }
    }

    /**
     * Creates a privacy list.
     *
     * @param name The privacy list's name.
     */
    public PrivacyList(String name) {
        this.name = name;
    }

    /**
     * Creates a global invisibility list.
     *
     * @return The invisibility list.
     */
    public static PrivacyList createInvisibilityList() {
        return createInvisibilityListExceptForUsers("invisible");
    }

    /**
     * Creates a global invisibility list, where you are still visible to some contacts.
     *
     * @param listName The list name. See <a href="https://xmpp.org/extensions/xep-0126.html#impl">4. Implementation Notes</a> for recommended list names.
     * @param jids     The JIDs to which you are still visible.
     * @return The invisibility list.
     * @see <a href="https://xmpp.org/extensions/xep-0126.html">XEP-0126: Invisibility</a>
     * @see <a href="https://xmpp.org/extensions/xep-0126.html#vis-select-jid">3.2.1 Becoming Visible by JID</a>
     */
    public static PrivacyList createInvisibilityListExceptForUsers(String listName, Jid... jids) {
        Collection<PrivacyRule> rules = new ArrayDeque<>();
        long order = 1;
        for (Jid jid : jids) {
            rules.add(PrivacyRule.of(jid, PrivacyRule.Action.ALLOW, order++).appliedToOutboundPresence());
        }
        rules.add(PrivacyRule.blockOutboundPresence(order));
        return new PrivacyList(listName, rules);
    }

    /**
     * Creates a global invisibility list, where you are still visible to some contacts.
     *
     * @param listName The list name. See <a href="https://xmpp.org/extensions/xep-0126.html#impl">4. Implementation Notes</a> for recommended list names.
     * @param groups   The roster groups to which you are still visible.
     * @return The invisibility list.
     * @see <a href="https://xmpp.org/extensions/xep-0126.html">XEP-0126: Invisibility</a>
     * @see <a href="https://xmpp.org/extensions/xep-0126.html#vis-select-roster">3.2.2 Becoming Visible by Roster Group</a>
     */
    public static PrivacyList createInvisibilityListExceptForGroups(String listName, String... groups) {
        Collection<PrivacyRule> rules = new ArrayDeque<>();
        long order = 1;
        for (String group : groups) {
            rules.add(PrivacyRule.of(group, PrivacyRule.Action.ALLOW, order++).appliedToOutboundPresence());
        }
        rules.add(PrivacyRule.blockOutboundPresence(order));
        return new PrivacyList(listName, rules);
    }

    /**
     * Creates a selective invisibility list. You are only invisible to the provided JIDs. You are visible to everyone else.
     *
     * @param listName The list name. See <a href="https://xmpp.org/extensions/xep-0126.html#impl">4. Implementation Notes</a> for recommended list names.
     * @param jids     The JIDs to which you appear invisible.
     * @return The invisibility list.
     * @see <a href="https://xmpp.org/extensions/xep-0126.html">XEP-0126: Invisibility</a>
     * @see <a href="https://xmpp.org/extensions/xep-0126.html#invis-select-jid">3.4.1 Becoming Invisible by JID</a>
     */
    public static PrivacyList createInvisibilityListForUsers(String listName, Jid... jids) {
        Collection<PrivacyRule> rules = new ArrayDeque<>();
        long order = 1;
        for (Jid jid : jids) {
            rules.add(PrivacyRule.blockPresenceTo(jid, order++));
        }
        rules.add(PrivacyRule.of(PrivacyRule.Action.ALLOW, order).appliedToOutboundPresence());
        return new PrivacyList(listName, rules);
    }

    /**
     * Creates a selective invisibility list. You are only invisible to the provided JIDs. You are visible to everyone else.
     *
     * @param listName The list name. See <a href="https://xmpp.org/extensions/xep-0126.html#impl">4. Implementation Notes</a> for recommended list names.
     * @param groups   The roster groups to which you appear invisible.
     * @return The invisibility list.
     * @see <a href="https://xmpp.org/extensions/xep-0126.html">XEP-0126: Invisibility</a>
     * @see <a href="https://xmpp.org/extensions/xep-0126.html#invis-select-roster">3.4.2 Becoming Invisible by Roster Group</a>
     */
    public static PrivacyList createInvisibilityListForGroups(String listName, String... groups) {
        Collection<PrivacyRule> rules = new ArrayDeque<>();
        long order = 1;
        for (String group : groups) {
            rules.add(PrivacyRule.blockPresenceToRosterGroup(group, order++));
        }
        rules.add(PrivacyRule.of(PrivacyRule.Action.ALLOW, order).appliedToOutboundPresence());
        return new PrivacyList(listName, rules);
    }

    /**
     * Gets the privacy rules.
     *
     * @return The privacy rules.
     */
    public final List<PrivacyRule> getPrivacyRules() {
        return Collections.unmodifiableList(item);
    }

    /**
     * Gets the name of the privacy list.
     *
     * @return The name.
     */
    public final String getName() {
        return name;
    }

    /**
     * Indicates whether this is the default list.
     *
     * @return True, if this is the default list.
     */
    public final boolean isDefault() {
        return isDefault;
    }

    /**
     * Indicates whether this is the active list.
     *
     * @return True, if this is the active list.
     */
    public final boolean isActive() {
        return isActive;
    }

    /**
     * Gets the privacy list marked as active list.
     *
     * @return The list as active list.
     * @see #isActive()
     */
    public final PrivacyList asActive() {
        PrivacyList privacyList = new PrivacyList(name, item);
        privacyList.isActive = true;
        return privacyList;
    }

    /**
     * Gets the privacy list marked as default list.
     *
     * @return The privacy list as default list.
     * @see #isDefault()
     */
    public final PrivacyList asDefault() {
        PrivacyList privacyList = new PrivacyList(name, item);
        privacyList.isDefault = true;
        return privacyList;
    }

    /**
     * Compares this privacy list with another list. When sorted, default lists are listed first, then active lists, then lists are sorted by their name.
     *
     * @param o The other list.
     * @return The comparison result.
     */
    @Override
    public final int compareTo(PrivacyList o) {
        if (this == o) {
            return 0;
        }
        if (o != null) {
            if (isDefault) {
                if (o.isDefault) {
                    return name != null ? Collator.getInstance().compare(name, o.name) : 1;
                } else {
                    return -1;
                }
            } else if (isActive) {
                if (o.isDefault) {
                    return 1;
                } else if (o.isActive) {
                    return name != null ? Collator.getInstance().compare(name, o.name) : 1;
                } else {
                    return -1;
                }
            } else {
                if (o.isDefault || o.isActive) {
                    return 1;
                } else {
                    return name != null ? Collator.getInstance().compare(name, o.name) : 1;
                }
            }
        }
        return -1;
    }

    @Override
    public final String toString() {
        return name;
    }
}