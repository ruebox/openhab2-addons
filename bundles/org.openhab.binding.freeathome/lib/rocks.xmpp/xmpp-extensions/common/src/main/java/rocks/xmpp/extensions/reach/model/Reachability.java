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

package rocks.xmpp.extensions.reach.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The implementation of the {@code <reach/>} element in the {@code urn:xmpp:reach:0} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0152.html">XEP-0152: Reachability Addresses</a>
 * @see <a href="https://xmpp.org/extensions/xep-0152.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "reach")
public final class Reachability {

    public static final String NAMESPACE = "urn:xmpp:reach:0";

    private final List<Address> addr = new ArrayList<>();

    public Reachability(Collection<Address> addresses) {
        this.addr.addAll(addresses);
    }

    public Reachability() {
    }

    /**
     * Gets the addresses.
     *
     * @return The addresses.
     */
    public final List<Address> getAddresses() {
        return Collections.unmodifiableList(addr);
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Reachability)) {
            return false;
        }
        Reachability other = (Reachability) o;

        return Objects.equals(addr, other.addr);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(addr);
    }

    @Override
    public final String toString() {
        return addr.toString();
    }
}
