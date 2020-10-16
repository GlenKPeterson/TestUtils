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

package org.organicdesign.testUtils;

import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
import static org.organicdesign.testUtils.ComparatorContract.*;
import static org.organicdesign.testUtils.ComparatorContract.CompToZero.*;

/**
 Tests the various properties the Comparable contract is supposed to uphold.  If you think this is
 confusing, realize that like equals(), it is often not possible to implement a one-sided
 compareTo() correctly with inheritance - it's a broken concept, but it's still used so often that
 you have to do your best with it.

 I got the idea of contract-based testing from watching Bill Venners:
 https://www.youtube.com/watch?v=bCTZQi2dpl8
 */
public class CompareToContract {

    private static class NamedPair<S extends Comparable<? super S>> {
        final @NotNull S a;
        final @NotNull S b;
        final @NotNull String name;
        NamedPair(
                @NotNull S theA,
                @NotNull S theB,
                @NotNull String nm
        ) {
            a = theA; b = theB; name = nm;
        }
    }

    private static <S extends Comparable<? super S>> void pairComp(
            @NotNull NamedPair<S> first,
            @NotNull CompToZero comp,
            @NotNull NamedPair<S> second
    ) {
        assertTrue("Item A in the " + first.name + " pair must be " + comp.english() +
                   " item A in the " + second.name + " pair",
                   comp.vsZero(first.a.compareTo(second.a)));
        assertTrue("Item A in the " + first.name + " pair must be " + comp.english() +
                   " item B in the " + second.name + " pair",
                   comp.vsZero(first.a.compareTo(second.b)));
        assertTrue("Item B in the " + first.name + " pair must be " + comp.english() +
                   " item A in the " + second.name + " pair",
                   comp.vsZero(first.b.compareTo(second.a)));
        assertTrue("Item B in the " + first.name + " pair must be " + comp.english() +
                   " item B in the " + second.name + " pair",
                   comp.vsZero(first.b.compareTo(second.b)));
    }

    /**
     Tests the various properties the Comparable contract is supposed to uphold.  Also tests that
     the behavior of compareTo() is compatible with equals() and hashCode() which is strongly
     suggested, but not actually required.  Write your own test if you don't want that.  Expects
     three pair of unique objects.  Within a pair, the two objects should be equal.  Both objects in
     the first pair are less than the ones in the second pair, which in turn are less than the
     objects in the third pair.

     See note in class documentation.
     */
    // Because the Comparable interface is only comparable to itself, I think all the objects have to
    // extend the same comparable class.  Thus the type signature is different from
    // EqualsContract.equalsHashCode()
    //
    // The old (BAD/WRONG) signature tried to let you compare against other comparables, which is neither sensible
    // nor possible.  It also makes the Kotlin compiler go haywire:
    //
    //    public static <S extends Comparable<? super S>, T1 extends S, T2 extends S, T3 extends S>
    //    void testCompareTo(T1 least1, T1 least2, T2 middle1, T2 middle2, T3 greatest1, T3 greatest2)
    //
    public static <S extends Comparable<? super S>>
    void testCompareTo(
            @NotNull S least1,
            @NotNull S least2,
            @NotNull S middle1,
            @NotNull S middle2,
            @NotNull S greatest1,
            @NotNull S greatest2
    ) {
        // Many of the comments in this method are paraphrases or direct quotes from the Javadocs for
        // the Comparable interface.  That is where this contract is specified.
        // https://docs.oracle.com/javase/8/docs/api/
        AtomicBoolean anySame = new AtomicBoolean();
        EqualsContract.permutations(Arrays.asList(least1, least2, middle1, middle2, greatest1, greatest2),
                                    (S a, S b) -> {
                                        if (a == b) {
                                            anySame.set(true);
                                        }
                                        return null;
                                    });
        if (anySame.get()) {
            throw new IllegalArgumentException("You must provide three pair of different objects in order");
        }

        NamedPair<S> least = new NamedPair<>(least1, least2, "Least");
        NamedPair<S> middle = new NamedPair<>(middle1, middle2, "Middle");
        NamedPair<S> greatest = new NamedPair<>(greatest1, greatest2, "Greatest");

        for (NamedPair<S> comp : Arrays.asList(least, middle, greatest)) {
            // Consistent with equals: (e1.compareTo(e2) == 0) if and only if e1.equals(e2)
            pairComp(comp, EQZ, comp);
            assertEquals(comp.name + " A must be compatibly equal to its paired B element", comp.a, comp.b);
            assertEquals(comp.name + " B must be compatibly equal to its paired A element", comp.b, comp.a);
        }

        int i = 0;
        for (S comp : Arrays.asList(least1, least2, middle1, middle2, greatest1, greatest2)) {
            i++;
            assertEquals("item.equals(itself) should have returned true for item " + i, comp, comp);

            // It is strongly recommended (though not required) that natural orderings be consistent
            // with equals.

            // One exception is java.math.BigDecimal, whose natural ordering equates BigDecimal
            // objects with equal values and different precisions (such as 4.0 and 4.00).

            // null is not an instance of any class, and e.compareTo(null) should throw a
            // NullPointerException even though e.equals(null) returns false.
            // This is because e.compareTo(null) is not reflexive - you can't call null.compareTo(e).
            try {
                //noinspection ConstantConditions,ResultOfMethodCallIgnored
                comp.compareTo(null);
                fail("e.compareTo(null) should throw some kind of RuntimeException" +
                     " (NullPointer/IllegalArgument/IllegalState, etc.)" +
                     " even though e.equals(null) returns false." +
                     " Item " + i + " threw no exception!");
            } catch (RuntimeException ignore) {
                // Previously we had allowed NullPointerException and IllegalArgumentException.
                // Kotlin throws IllegalStateException, so we now expect any RuntimeException
                // to be thrown.
                //
                // This reports no-test-coverage, but if you uncomment this, you can
                // prove that it *is* covered.
//                System.out.println("Pass: " + comp + " " + ignore);
            }
            assertNotEquals("item.equals(null) should always be false.  Item " + i + " failed", null, comp);
        }

        pairComp(least, LTZ, middle);
        pairComp(least, LTZ, greatest);
        pairComp(middle, LTZ, greatest);

        pairComp(greatest, GTZ, middle);
        pairComp(greatest, GTZ, least);
        pairComp(middle, GTZ, least);
    }
}
