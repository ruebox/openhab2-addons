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

package rocks.xmpp.dns;

import java.nio.ByteBuffer;

/**
 * A DNS SRV resource record.
 *
 * @author Christian Schudt
 * @see <a href="http://tools.ietf.org/html/rfc2782">A DNS RR for specifying the location of services (DNS SRV)</a>
 */
public final class SrvRecord implements Comparable<SrvRecord> {

    /**
     * The priority of this target host.  A client MUST attempt to
     * contact the target host with the lowest-numbered priority it can
     * reach; target hosts with the same priority SHOULD be tried in an
     * order defined by the weight field.  The range is 0-65535.  This
     * is a 16 bit unsigned integer in network byte order.
     */
    private final int priority;

    /**
     * A server selection mechanism.  The weight field specifies a
     * relative weight for entries with the same priority. Larger
     * weights SHOULD be given a proportionately higher probability of
     * being selected. The range of this number is 0-65535.  This is a
     * 16 bit unsigned integer in network byte order.  Domain
     * administrators SHOULD use Weight 0 when there isn't any server
     * selection to do, to make the RR easier to read for humans (less
     * noisy).  In the presence of records containing weights greater
     * than 0, records with weight 0 should have a very small chance of
     * being selected.
     */
    private final int weight;

    /**
     * The port on this target host of this service.
     */
    private final int port;

    /**
     * The domain name of the target host.
     */
    private final String target;

    SrvRecord(final ByteBuffer data) {
        this.priority = data.getShort() & 0xFFFF;
        this.weight = data.getShort() & 0xFFFF;
        this.port = data.getShort() & 0xFFFF;
        this.target = ResourceRecord.parse(data);
    }

    /**
     * Gets the priority of this target host.
     *
     * @return The priority of this target host.
     */
    public final int getPriority() {
        return priority;
    }

    /**
     * Gets the weight of this target host. The weight field specifies a
     * relative weight for entries with the same priority.
     *
     * @return The weight of this target host.
     */
    public final int getWeight() {
        return weight;
    }

    /**
     * Gets the port on this target host of this service.
     *
     * @return The port on this target host of this service.
     */
    public final int getPort() {
        return port;
    }

    /**
     * Gets the domain name of the target host.
     *
     * @return The domain name of the target host.
     */
    public final String getTarget() {
        return target;
    }

    @Override
    public final int compareTo(final SrvRecord o) {
        if (o == null) {
            return -1;
        }
        final int result = Integer.compare(this.priority, o.priority);
        if (result == 0) {
            return Integer.compare(this.weight, o.weight);
        }
        return result;
    }

    @Override
    public final String toString() {
        return "SRV " + priority + ' ' + weight + ' ' + port + ' ' + target;
    }
}
