// Copyright 2015-07-03 PlanBase Inc. & Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.organicdesign.testUtils

import org.organicdesign.testUtils.ComparatorContract.CompToZero
import org.organicdesign.testUtils.EqualsContract.permutations
import java.lang.IllegalArgumentException

/**
 * Tests the various properties the Comparable contract is supposed to uphold.  If you think this is
 * confusing, realize that like equals(), it is often not possible to implement a one-sided
 * compareTo() correctly with inheritance - it's a broken concept, but it's still used so often that
 * you have to do your best with it.
 *
 * I got the idea of contract-based testing from watching Bill Venners:
 * https://www.youtube.com/watch?v=bCTZQi2dpl8
 */
object ComparableContract {
    private fun <S : Comparable<S>> pairComp(
            first: NamedPair<S>,
            comp: CompToZero,
            second: NamedPair<S>
    ) {
        if (!comp.vsZero(first.a.compareTo(second.a))) {
            throw AssertionError("Item A in the " + first.name +
                                 " pair must be " + comp.english() +
                                 " item A in the " + second.name + " pair")
        }
        if (!comp.vsZero(first.a.compareTo(second.b))) {
            throw AssertionError("Item A in the " + first.name +
                                 " pair must be " + comp.english() +
                                 " item B in the " + second.name + " pair")
        }
        if (!comp.vsZero(first.b.compareTo(second.a))) {
            throw AssertionError("Item B in the " + first.name +
                                 " pair must be " + comp.english() +
                                 " item A in the " + second.name + " pair")
        }
        if (!comp.vsZero(first.b.compareTo(second.b))) {
            throw AssertionError("Item B in the " + first.name +
                                 " pair must be " + comp.english() +
                                 " item B in the " + second.name + " pair")
        }
    }

    /**
     * Tests the various properties the Comparable contract is supposed to uphold.  Also tests that
     * the behavior of compareTo() is compatible with equals() and hashCode() which is strongly
     * suggested, but not actually required.  Write your own test if you don't want that.
     *
     * Expects three pair of unique objects.
     * Within a pair, the two objects should be equal and return the same hashcode.
     * Both objects in the first pair are less than the ones in the second pair,
     * which in turn are less than the objects in the third pair.
     *
     * See note in class documentation.
     */
    // Because the Comparable interface is only comparable to itself, I think all the objects have to
    // extend the same comparable class.  Thus, the type signature is different from
    // EqualsContract.equalsHashCode()
    //
    // The old (BAD/WRONG) signature tried to let you compare against other comparables, which is neither sensible
    // nor possible.  It also makes the Kotlin compiler go haywire:
    //
    //    public static <S extends Comparable<? super S>, T1 extends S, T2 extends S, T3 extends S>
    //    void testCompareTo(T1 least1, T1 least2, T2 middle1, T2 middle2, T3 greatest1, T3 greatest2)
    //
    @JvmStatic
    fun <S : Comparable<S>> testCompareTo(
            least1: S,
            least2: S,
            middle1: S,
            middle2: S,
            greatest1: S,
            greatest2: S
    ) {
        // Many of the comments in this method are paraphrases or direct quotes from the Javadocs for
        // the Comparable interface.  That is where this contract is specified.
        // https://docs.oracle.com/javase/8/docs/api/
        var anySame = false
        permutations(listOf(least1, least2, middle1, middle2, greatest1, greatest2)
        ) { a: S, b: S ->
            if (a === b) {
                anySame = true
            }
        }
        if (anySame) {
            throw IllegalArgumentException("You must provide three pair of different objects in order")
        }
        val least = NamedPair(least1, least2, "Least")
        val middle = NamedPair(middle1, middle2, "Middle")
        val greatest = NamedPair(greatest1, greatest2, "Greatest")
        for (comp in listOf(least, middle, greatest)) {
            // Consistent with equals: (e1.compareTo(e2) == 0) if and only if e1.equals(e2)
            pairComp(comp, CompToZero.EQZ, comp)
            if (!comp.a.equals(comp.b)) {
                throw AssertionError(comp.name + " A must be compatibly equal to its paired B element")
            }
            if (!comp.b.equals(comp.a)) {
                throw AssertionError(comp.name + " B must be compatibly equal to its paired A element")
            }
            if (comp.a.hashCode() != comp.b.hashCode()) {
                throw AssertionError(comp.name + " A should have the same hashcode as its paired B element")
            }
        }
        var i = 0
        for (comp in listOf(least1, least2, middle1, middle2, greatest1, greatest2)) {
            i++
            if (!comp.equals(comp)) {
                throw AssertionError("a.equals(a) should have returned true for item $i " +
                                     "(should equal itself)")
            }

            // It is strongly recommended (though not required) that natural orderings be consistent
            // with equals.

            // One exception is java.math.BigDecimal, whose natural ordering equates BigDecimal
            // objects with equal values and different precisions (such as 4.0 and 4.00).

            // null is not an instance of any class, and e.compareTo(null) should throw a
            // NullPointerException even though e.equals(null) returns false.
            // This is because e.compareTo(null) is not reflexive - you can't call null.compareTo(e).
            try {
                BreakNullSafety.INSTANCE.compareToNull(comp)
                throw AssertionError("e.compareTo(null) should throw some kind of RuntimeException" +
                                     " (NullPointer/IllegalArgument/IllegalState, etc.)" +
                                     " even though e.equals(null) returns false." +
                                     " Item " + i + " threw no exception!")
            } catch (ignore: RuntimeException) {
                // Previously we had allowed NullPointerException and IllegalArgumentException.
                // Kotlin throws IllegalStateException, so we now expect any RuntimeException
                // to be thrown.
            }
            if (comp.equals(null)) {
                throw AssertionError("item.equals(null) should always be false.  Item $i failed")
            }
        }
        pairComp(least, CompToZero.LTZ, middle)
        pairComp(least, CompToZero.LTZ, greatest)
        pairComp(middle, CompToZero.LTZ, greatest)
        pairComp(greatest, CompToZero.GTZ, middle)
        pairComp(greatest, CompToZero.GTZ, least)
        pairComp(middle, CompToZero.GTZ, least)
    }

    private class NamedPair<S : Comparable<S>>(
            val a: S,
            val b: S,
            val name: String
    )
}