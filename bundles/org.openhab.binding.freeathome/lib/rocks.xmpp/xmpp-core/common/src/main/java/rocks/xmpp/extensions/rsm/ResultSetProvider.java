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

import java.util.Collection;
import java.util.List;

/**
 * Provides methods which are needed for result set management.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0059.html">XEP-0059: Result Set Management</a>
 */
public interface ResultSetProvider<T extends ResultSetItem> {

    /**
     * Gets all items. This method is called, if no result set management was included in the query.
     *
     * @return The items.
     */
    List<T> getItems();

    /**
     * Gets the total item count.
     *
     * @return The item count.
     */
    int getItemCount();

    /**
     * Gets the items in a specific range (by index).
     *
     * @param index   The index.
     * @param maxSize The maximal size of items.
     * @return The items.
     */
    List<T> getItems(int index, int maxSize);

    /**
     * Gets the items after a specific item, which is determined by an item id.
     *
     * @param itemId  The item id.
     * @param maxSize The maximal size of items.
     * @return The items.
     */
    List<T> getItemsAfter(String itemId, int maxSize);

    /**
     * Gets the items before a specific item, which is determined by an item id.
     *
     * @param itemId  The item id.
     * @param maxSize The maximal size of items.
     * @return The items.
     */
    List<T> getItemsBefore(String itemId, int maxSize);

    /**
     * Gets the index of a specific item.
     *
     * @param itemId The item id.
     * @return The index or -1, if no index could be determined.
     */
    int indexOf(String itemId);

    /**
     * Creates a {@link Collection}-based result set provider.
     * <p>
     * It is highly recommended that the provided list is thread-safe, e.g. by using {@link java.util.Collections#synchronizedList(List)}} or a concurrent collection.
     * Otherwise modifications on the list, while reading a sub list ({@link #getItems(int, int)}) may produce {@link java.util.ConcurrentModificationException}.
     *
     * @param items The items.
     * @param <T>   The result set item.
     * @return The result set provider.
     * @since 0.8.0
     */
    static <T extends ResultSetItem> ResultSetProvider<T> forItems(Collection<T> items) {
        return new CollectionBasedItemProvider<>(items);
    }
}
