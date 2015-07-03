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

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 Tests the various properties the Comparable contract is supposed to uphold.  If you think this is
 confusing, realize that like equals(), it is often not possible to implement a one-sided
 compareTo() correctly with inheritance - it's a broken concept, but it's still used so often that
 you have to do your best with it.

 I got the idea of contract-based testing from watching Bill Venners:
 https://www.youtube.com/watch?v=bCTZQi2dpl8
 */
public class CompareToContract {
    // Many of the comments in this method are paraphrases or direct quotes from the Javadocs for
    // the Comparable interface.  That is where this contract is specified.
    // https://docs.oracle.com/javase/8/docs/api/
    public static <S extends Comparable<S>, T1 extends S, T2 extends S, T3 extends S>
    void testCompareTo(T1 least, T2 middle, T3 greatest) {
        if ( (least == middle) ||
             (least == greatest) ||
             (middle == greatest) ) {
            throw new IllegalArgumentException("You must provide three different objects in order");
        }
        List<S> comps = Arrays.asList(least, middle, greatest);

        int i = 0;
        for (S comp : comps) {
            i++;
            // Consistent with equals: (e1.compareTo(e2) == 0) if and only if e1.equals(e2)
            assertTrue("item.compareTo(itself) should have returned 0 for item " + i,
                       comp.compareTo(comp) == 0);
            //noinspection EqualsWithItself
            assertTrue("item.equals(itself) should have return true for item " + i,
                       comp.equals(comp));

            // It is strongly recommended (though not required) that natural orderings be consistent
            // with equals.

            // One exception is java.math.BigDecimal, whose natural ordering equates BigDecimal
            // objects with equal values and different precisions (such as 4.0 and 4.00).

            // null is not an instance of any class, and e.compareTo(null) should throw a
            // NullPointerException even though e.equals(null) returns false.
            try {
                //noinspection ConstantConditions
                comp.compareTo(null);
                assertFalse("e.compareTo(null) should throw a NullPointerException even though e.equals(null)" +
                                    " returns false, but item " + i + "did not.",
                            true);
            } catch (NullPointerException ignore) {
            }
            //noinspection ConstantConditions,ObjectEqualsNull
            assertFalse("item.equals(null) should always be false.  Item " + i + " failed",
                        comp.equals(null));
        }

        assertTrue("The first item must be less than the second.",
                   least.compareTo(middle) < 0);
        assertTrue("The first item must be less than the third.",
                   least.compareTo(greatest) < 0);
        assertTrue("The second item must be less than the third.",
                   middle.compareTo(greatest) < 0);
        assertTrue("The third item must be greater than the second.",
                   greatest.compareTo(middle) > 0);
        assertTrue("The third item must be greater than the first.",
                   greatest.compareTo(least) > 0);
        assertTrue("The second item must be greater than the first.",
                   middle.compareTo(least) > 0);
    }

}
