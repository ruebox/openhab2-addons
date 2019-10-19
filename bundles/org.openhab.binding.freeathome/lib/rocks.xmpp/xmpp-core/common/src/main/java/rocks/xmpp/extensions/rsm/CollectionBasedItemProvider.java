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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

final class CollectionBasedItemProvider<T extends ResultSetItem> implements ResultSetProvider<T> {

    private final Collection<T> items;

    CollectionBasedItemProvider(final Collection<T> items) {
        this.items = Objects.requireNonNull(items);
    }

    @Override
    public final List<T> getItems() {
        return Collections.unmodifiableList(new ArrayList<>(items));
    }

    @Override
    public final int getItemCount() {
        return items.size();
    }

    @Override
    public final List<T> getItems(final int index, final int maxSize) {
        final int toIndex = index + maxSize;
        final List<T> list = new ArrayList<>(items);
        if (index < 0 || toIndex > list.size() || index > toIndex) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list.subList(index, toIndex));
    }

    @Override
    public final List<T> getItemsAfter(final String itemId, final int maxSize) {
        return getItems(indexOf(itemId) + 1, maxSize);
    }

    @Override
    public final List<T> getItemsBefore(final String itemId, final int maxSize) {
        return getItems(indexOf(itemId) - maxSize, maxSize);
    }

    @Override
    public final int indexOf(final String itemId) {
        Objects.requireNonNull(itemId);
        final ListIterator<T> itemIterator = new ArrayList<>(items).listIterator();
        while (itemIterator.hasNext()) {
            final T item = itemIterator.next();
            if (item != null && itemId.equals(item.getId())) {
                return itemIterator.previousIndex();
            }
        }
        return -1;
    }
}
