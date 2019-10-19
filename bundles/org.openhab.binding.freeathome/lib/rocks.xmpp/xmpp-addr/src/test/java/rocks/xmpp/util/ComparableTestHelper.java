/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 Christian Schudt
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

package rocks.xmpp.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Helper class for testing {@link Comparable} implementations.
 *
 * @author Christian Schudt
 */
public final class ComparableTestHelper {

    private ComparableTestHelper() {
    }

    /**
     * Checks for the contract of a {@link Comparable} implementation.
     * <p>
     * The passed collection should contain test objects, which are compared against each other.
     * <p>
     * The three requirements of the {@link Comparable#compareTo(Object)} method are checked.
     * <p>
     * If the contract is violated, this method throws an {@link AssertionError}.
     *
     * @param collection The collection of test objects.
     * @param <T>        The type.
     * @param <C>        The comparable.
     * @throws AssertionError If the Comparable contract is violated.
     * @see Comparable#compareTo(Object)
     */
    public static <T extends C, C extends Comparable<C>> void checkCompareToContract(final Collection<T> collection) {

        final List<C> list = new ArrayList<>(collection);
        for (int i = 0; i < list.size(); i++) {
            for (int j = i; j < list.size(); j++) {
                final C x = list.get(i);
                final C y = list.get(j);
                final int xCompareToY = x.compareTo(y);
                final int yCompareToX = y.compareTo(x);
                //System.out.println("Comparing item " + i + " with item " + j + ": " + xCompareToY + ", " + yCompareToX);
                // The implementor must ensure sgn(x.compareTo(y)) == -sgn(y.compareTo(x)) for all x and y. (This implies that x.compareTo(y) must throw an exception iff y.compareTo(x) throws an exception.)
                assert Math.signum(xCompareToY) == -Math.signum(yCompareToX);

                for (final C z : list) {
                    // The implementor must also ensure that the relation is transitive: (x.compareTo(y)>0 && y.compareTo(z)>0) implies x.compareTo(z)>0.
                    assert xCompareToY <= 0 || y.compareTo(z) <= 0 || x.compareTo(z) > 0;
                    // Finally, the implementor must ensure that x.compareTo(y)==0 implies that sgn(x.compareTo(z)) == sgn(y.compareTo(z)), for all z.
                    assert xCompareToY != 0 || Math.signum(x.compareTo(z)) == Math.signum(y.compareTo(z));
                }
            }
        }
    }

    /**
     * Checks if the {@code Comparable}s in the collection are all consistent with {@code equals()}, i.e. {@code (x.compareTo(y)==0) == (x.equals(y))}.
     *
     * @param collection The collection of {@code Comparable}s.
     * @param <T>        The type.
     * @param <C>        The comparable.
     * @return true, if all items are consistens with equals; false otherwise.
     * @see Comparable#compareTo(Object)
     */
    public static <T extends C, C extends Comparable<C>> boolean isConsistentWithEquals(final Collection<T> collection) {
        final List<C> list = new ArrayList<>(collection);
        for (int i = 0; i < list.size(); i++) {
            for (int j = i; j < list.size(); j++) {
                final C x = list.get(i);
                final C y = list.get(j);
                if (x.compareTo(y) == 0 != x.equals(y)) {
                    return false;
                }
            }
        }
        return true;
    }
}
