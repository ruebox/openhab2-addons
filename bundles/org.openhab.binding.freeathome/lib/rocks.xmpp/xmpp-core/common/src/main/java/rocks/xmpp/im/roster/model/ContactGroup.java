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

import java.text.Collator;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a contact group in the user's roster.
 * A group consists of contacts and may contain nested sub-groups.
 * <p>
 * This class is conditionally thread-safe.
 *
 * @author Christian Schudt
 */
public final class ContactGroup implements Comparable<ContactGroup> {

    private final Set<Contact> contacts;

    private final Set<ContactGroup> groups;

    private final ContactGroup parentGroup;

    private final String name;

    private final String fullName;

    public ContactGroup(String name, String fullName, ContactGroup parentGroup) {
        this.name = name;
        this.fullName = fullName;
        this.contacts = new TreeSet<>();
        this.groups = new TreeSet<>();
        this.parentGroup = parentGroup;
    }

    /**
     * Gets the name of the group.
     *
     * @return The name.
     */
    public final String getName() {
        return name;
    }

    /**
     * Gets the full name of the group.
     *
     * @return The full name.
     */
    public final String getFullName() {
        return fullName;
    }

    /**
     * Gets the nested sub-groups of this group. The returned collection is mutable and not thread-safe.
     *
     * @return The nested groups.
     */
    public final Collection<ContactGroup> getGroups() {
        return groups;
    }

    /**
     * Gets the contacts in this group. The returned collection is mutable and not thread-safe.
     *
     * @return The contacts.
     */
    public final Collection<Contact> getContacts() {
        return contacts;
    }

    /**
     * Gets the parent group.
     *
     * @return The parent group or null, if it has no parent.
     */
    public final ContactGroup getParentGroup() {
        return parentGroup;
    }

    @Override
    public final int compareTo(ContactGroup o) {
        if (this == o) {
            return 0;
        }

        if (o != null) {
            if (fullName != null) {
                if (o.fullName != null) {
                    return Collator.getInstance().compare(fullName, o.fullName);
                } else {
                    return -1;
                }
            } else {
                if (o.fullName != null) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } else {
            return -1;
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ContactGroup)) {
            return false;
        }
        ContactGroup other = (ContactGroup) o;

        return Objects.equals(fullName, other.fullName);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(fullName);
    }

    @Override
    public final String toString() {
        return name;
    }
}
