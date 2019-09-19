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

package rocks.xmpp.extensions.rsm.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The implementation of the {@code <set/>} element in the {@code http://jabber.org/protocol/rsm} namespace.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0059.html">XEP-0059: Result Set Management</a>
 * @see <a href="https://xmpp.org/extensions/xep-0059.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "set")
public final class ResultSetManagement {

    /**
     * http://jabber.org/protocol/rsm
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/rsm";

    private final Integer max;

    private final String after;

    private final String before;

    private final Integer count;

    private final First first;

    private final Integer index;

    private final String last;

    private ResultSetManagement() {
        this(null, null, null, null, null, null, null);
    }

    private ResultSetManagement(Integer max, String after, String before, Integer count, First first, Integer index, String last) {
        this.max = max;
        this.after = after;
        this.before = before;
        this.count = count;
        this.first = first;
        this.index = index;
        this.last = last;
    }

    /**
     * Creates the result set request for the next page. It requires that this result set management has a "last item" set, otherwise the last page is requested.
     *
     * @param max The number of items in the page.
     * @return The result set management for requesting the next page.
     */
    public final ResultSetManagement nextPage(int max) {
        if (last != null) {
            return ResultSetManagement.forNextPage(max, last);
        }
        return ResultSetManagement.forLastPage(max);
    }

    /**
     * Creates the result set request for the previous page. It requires that this result set management has a "first item" set, otherwise the first page is requested.
     *
     * @param max The number of items in the page.
     * @return The result set management for requesting the previous page.
     */
    public final ResultSetManagement previousPage(int max) {
        if (first != null) {
            return ResultSetManagement.forPreviousPage(max, first.value);
        }
        return ResultSetManagement.forFirstPage(max);
    }

    /**
     * Gets a result set, which limits the number of items of a result to be returned.
     *
     * @param limit The limit, i.e. the maximum number of items.
     * @return The result set.
     * @see <a href="https://xmpp.org/extensions/xep-0059.html#limit">2.1 Limiting the Number of Items</a>
     * @deprecated Use {@link #forFirstPage(int)}
     */
    @Deprecated
    public static ResultSetManagement forLimit(int limit) {
        return new ResultSetManagement(limit, null, null, null, null, null, null);
    }

    /**
     * Gets a result set, which requests the first page.
     *
     * @param max The item count per page.
     * @return The result set.
     * @see <a href="https://xmpp.org/extensions/xep-0059.html#limit">2.1 Limiting the Number of Items</a>
     */
    public static ResultSetManagement forFirstPage(int max) {
        return new ResultSetManagement(max, null, null, null, null, null, null);
    }

    /**
     * Gets a result set, which requests the next page after a specified item.
     *
     * @param max The item count per page.
     * @param id  The id of the last item of the previous page. This should be the {@link #getLastItem()} ()} of the previous page.
     * @return The result set.
     * @see <a href="https://xmpp.org/extensions/xep-0059.html#forwards">2.2 Paging Forwards Through a Result Set</a>
     */
    public static ResultSetManagement forNextPage(int max, String id) {
        return new ResultSetManagement(max, id, null, null, null, null, null);
    }

    /**
     * Gets a result set, which requests the previous page before a specified item.
     *
     * @param max The item count per page.
     * @param id  The id of the first item of the next page. This should be the {@link #getFirstItem()} of the next page.
     * @return The result set.
     * @see <a href="https://xmpp.org/extensions/xep-0059.html#backwards">2.3 Paging Backwards Through a Result Set</a>
     */
    public static ResultSetManagement forPreviousPage(int max, String id) {
        return new ResultSetManagement(max, null, id, null, null, null, null);
    }

    /**
     * Gets a result set, which requests the last page.
     *
     * @param max The item count per page.
     * @return The result set.
     * @see <a href="https://xmpp.org/extensions/xep-0059.html#last">2.5 Requesting the Last Page in a Result Set</a>
     */
    public static ResultSetManagement forLastPage(int max) {
        return new ResultSetManagement(max, null, "", null, null, null, null);
    }

    /**
     * Gets a result set, which starts at a particular index.
     *
     * @param limit The item count per page.
     * @param index The index to start from.
     * @return The result set.
     * @see <a href="https://xmpp.org/extensions/xep-0059.html#jump">2.6 Retrieving a Page Out of Order</a>
     */
    public static ResultSetManagement forLimit(int limit, int index) {
        return new ResultSetManagement(limit, null, null, null, null, index, null);
    }

    /**
     * Gets a result set, which has a count information.
     *
     * @param count The item count per page.
     * @return The result set.
     * @see <a href="https://xmpp.org/extensions/xep-0059.html#jump">2.6 Retrieving a Page Out of Order</a>
     * @deprecated Use {@link #forCountResponse(int)}
     */
    @Deprecated
    public static ResultSetManagement forCount(int count) {
        return new ResultSetManagement(null, null, null, count, null, null, null);
    }

    /**
     * Gets a result set, which has a count information. The result set is intended for the response.
     *
     * @param count The item count per page.
     * @return The result set.
     * @see <a href="https://xmpp.org/extensions/xep-0059.html#jump">2.6 Retrieving a Page Out of Order</a>
     */
    public static ResultSetManagement forCountResponse(int count) {
        return new ResultSetManagement(null, null, null, count, null, null, null);
    }

    /**
     * Gets a result set, which has a count information, including first and last item.
     *
     * @param count The item count per page.
     * @param index The index of the first item.
     * @param first The first item.
     * @param last  The last item.
     * @return The result set.
     * @see <a href="https://xmpp.org/extensions/xep-0059.html#forwards">2.2 Paging Forwards Through a Result Set</a>
     * @deprecated Use {@link #forResponse(Integer, Integer, String, String)}
     */
    @Deprecated
    public static ResultSetManagement forCount(Integer count, Integer index, String first, String last) {
        return new ResultSetManagement(null, null, null, count, new First(index, first), null, last);
    }

    /**
     * Gets a result set, which has a count information, including first and last item. The result set is intended for the response.
     *
     * @param count The item count per page.
     * @param index The index of the first item.
     * @param first The first item.
     * @param last  The last item.
     * @return The result set.
     * @see <a href="https://xmpp.org/extensions/xep-0059.html#forwards">2.2 Paging Forwards Through a Result Set</a>
     */
    public static ResultSetManagement forResponse(Integer count, Integer index, String first, String last) {
        return new ResultSetManagement(null, null, null, count, new First(index, first), null, last);
    }

    /**
     * Gets a result set, which gets the item count of a result without retrieving the items themselves.
     *
     * @return The result set.
     * @see <a href="https://xmpp.org/extensions/xep-0059.html#count">2.7 Getting the Item Count</a>
     */
    public static ResultSetManagement forItemCount() {
        return forFirstPage(0);
    }

    /**
     * Gets the item count of a result set.
     *
     * @return The item count.
     */
    public final Integer getItemCount() {
        return count;
    }

    /**
     * Gets the first item.
     *
     * @return The first item.
     */
    public final String getFirstItem() {
        return first != null ? first.value : null;
    }

    /**
     * Gets the first item's index.
     *
     * @return The first item's index.
     */
    public final Integer getFirstItemIndex() {
        return first != null ? first.index : null;
    }

    /**
     * Gets the last item.
     *
     * @return The last item.
     */
    public final String getLastItem() {
        return last;
    }

    /**
     * Gets the max size.
     *
     * @return The max size.
     */
    public final Integer getMaxSize() {
        return max;
    }

    /**
     * Gets the 'after' element.
     *
     * @return The 'after' element.
     */
    public final String getAfter() {
        return after;
    }

    /**
     * Gets the 'before' element.
     *
     * @return The 'before' element.
     */
    public final String getBefore() {
        return before;
    }

    /**
     * Gets the index.
     *
     * @return The index.
     */
    public final Integer getIndex() {
        return index;
    }

    /**
     * Indicates, whether this RSM is used for requesting the item count.
     *
     * @return True, if this RSM is used for requesting the item count.
     */
    public final boolean isRequestingCount() {
        return max != null && max == 0;
    }
}
