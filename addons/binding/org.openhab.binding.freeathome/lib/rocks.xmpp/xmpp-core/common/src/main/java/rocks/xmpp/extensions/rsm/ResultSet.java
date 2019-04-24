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

package rocks.xmpp.extensions.rsm;

import rocks.xmpp.extensions.rsm.model.ResultSetItem;
import rocks.xmpp.extensions.rsm.model.ResultSetManagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A result set usually consists of a list of items and additional result set information (e.g. to mark first and last items).
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 */
public final class ResultSet<T extends ResultSetItem> {

    private final List<T> items = new ArrayList<>();

    private final ResultSetManagement resultSetManagement;

    private ResultSet(Collection<T> items, ResultSetManagement resultSetManagement) {
        if (items != null) {
            this.items.addAll(items);
        }
        this.resultSetManagement = resultSetManagement;
    }

    /**
     * Gets the items.
     *
     * @return The items.
     */
    public final List<T> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Gets the result set info.
     *
     * @return The result set info.
     */
    public final ResultSetManagement getResultSetManagement() {
        return resultSetManagement;
    }

    /**
     * Creates a result set from a result set provider and a result set management.
     * <p>
     * If the result set management asks for a limited result set (e.g. starting at a specific index and a maximum size),
     * this method takes care, that the result set provider returns the appropriate results.
     * <p>
     * The returned result set contains the limited result set as well as a result set management,
     * which contains the total item count and which can be used by the requester to navigate through the results, e.g. by asking for the previous or next page.
     *
     * @param resultSetProvider   The result set provider.
     * @param resultSetManagement The result set management.
     * @param <T>                 The type of the result set.
     * @return The result set.
     */
    public static <T extends ResultSetItem> ResultSet<T> create(ResultSetProvider<T> resultSetProvider, ResultSetManagement resultSetManagement) {

        // If the query has a RSM extension, use it and return a limited result set.
        if (resultSetManagement != null && resultSetManagement.getMaxSize() != null) {
            // If the max size == 0, it means the requester wants to know the total item count only.
            if (resultSetManagement.getMaxSize() != 0) {
                if (resultSetManagement.getAfter() != null) {
                    // 2.2 Paging Forwards Through a Result Set
                    return createResponse(resultSetProvider.getItemsAfter(resultSetManagement.getAfter(), resultSetManagement.getMaxSize()), resultSetProvider);
                } else if (resultSetManagement.getBefore() != null) {
                    if (!resultSetManagement.getBefore().isEmpty()) {
                        // 2.3 Paging Backwards Through a Result Set
                        return createResponse(resultSetProvider.getItemsBefore(resultSetManagement.getBefore(), resultSetManagement.getMaxSize()), resultSetProvider);
                    } else {
                        // 2.5 Requesting the Last Page in a Result Set
                        return createResponse(resultSetProvider.getItems(resultSetProvider.getItemCount() - resultSetManagement.getItemCount(), resultSetManagement.getMaxSize()), resultSetProvider);
                    }
                } else {
                    // 2.6 Retrieving a Page Out of Order
                    int fromIndex = resultSetManagement.getIndex() != null ? resultSetManagement.getIndex() : 0;
                    return createResponse(resultSetProvider.getItems(fromIndex, resultSetManagement.getMaxSize()), resultSetProvider);
                }
            } else {
                // 2.7 Getting the Item Count
                return new ResultSet<>(null, ResultSetManagement.forCountResponse(resultSetProvider.getItemCount()));
            }
        } else {
            // If there's no RSM in the query, return all items, without any result set info.
            return new ResultSet<>(resultSetProvider.getItems(), null);
        }
    }

    private static <T extends ResultSetItem> ResultSet<T> createResponse(List<T> result, final ResultSetProvider<T> resultSetProvider) {
        ResultSetManagement resultSetManagement;
        if (!result.isEmpty()) {
            //  the responding entity MUST include <first/> and <last/> elements that specify the unique ID (UID) for the first and last items in the page.
            resultSetManagement = ResultSetManagement.forResponse(resultSetProvider.getItemCount(), resultSetProvider.indexOf(result.get(0).getId()), result.get(0).getId(), result.get(result.size() - 1).getId());
        } else {
            // If there are no items in the page, then the <first/> and <last/> elements MUST NOT be included.
            // Only return the <count/> in this case.
            resultSetManagement = ResultSetManagement.forCountResponse(resultSetProvider.getItemCount());
        }
        return new ResultSet<>(result, resultSetManagement);
    }
}
