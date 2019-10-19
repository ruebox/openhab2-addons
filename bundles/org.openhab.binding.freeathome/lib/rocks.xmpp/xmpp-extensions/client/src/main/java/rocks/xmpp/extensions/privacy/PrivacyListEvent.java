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

package rocks.xmpp.extensions.privacy;

import java.util.EventObject;
import java.util.function.Consumer;

/**
 * This event notifies listeners, when a privacy list has been created or updated.
 *
 * @author Christian Schudt
 * @see PrivacyListManager#addPrivacyListListener(Consumer)
 */
public final class PrivacyListEvent extends EventObject {
    private final String listName;

    /**
     * Constructs a privacy list event.
     *
     * @param source   The object on which the event initially occurred.
     * @param listName The name of the list, which was updated.
     * @throws IllegalArgumentException if source is null.
     */
    PrivacyListEvent(Object source, String listName) {
        super(source);
        this.listName = listName;
    }

    /**
     * Gets the created or updated list name.
     *
     * @return The list name.
     */
    public String getListName() {
        return listName;
    }
}
