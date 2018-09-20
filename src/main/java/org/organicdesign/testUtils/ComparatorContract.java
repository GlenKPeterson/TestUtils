package org.organicdesign.testUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.organicdesign.testUtils.ComparatorContract.CompToZero.*;
/**
 Created by gpeterso on 3/28/17.
 */
public class ComparatorContract {
    enum CompToZero {
        LTZ {
            @Override public String english() { return "less than"; }
            @Override public boolean vsZero(int i) { return i < 0; }
        },
        GTZ {
            @Override public String english() { return "greater than"; }
            @Override public boolean vsZero(int i) { return i > 0; }
        },
        EQZ {
            @Override public String english() { return "equal to"; }
            @Override public boolean vsZero(int i) { return i == 0; }
        };
        public abstract String english();
        public abstract boolean vsZero(int i);
    }

    private static class Named<T> {
        final T a;
        final String name;
        Named(T theA, String nm) {a = theA;name = nm; }
    }

    private static <T> Named<T> t2(T a, String c) {
        return new Named<>(a, c);
    }

    @SuppressWarnings("unchecked")
    private static void pairComp(Named first, CompToZero comp, Named second,
                                 Comparator comparator) {
        assertTrue("The " + first.name + " item must be " + comp.english() +
                   " the " + second.name,
                   comp.vsZero(comparator.compare(first.a, second.a)));
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
    // Many of the comments in this method are paraphrases or direct quotes from the Javadocs for
    // the Comparable interface.  That is where this contract is specified.
    // https://docs.oracle.com/javase/8/docs/api/
//    @SuppressWarnings("unchecked")
    public static <T>
    void testComparator(T least1, T middle1, T greatest1, Comparator<T> comparator) {

        // TODO: Do we want to ensure that comparators are serializable?

        AtomicBoolean anySame = new AtomicBoolean();
        EqualsContract.permutations(Arrays.asList(least1, middle1, greatest1),
                                    (T a, T b) -> {
                                        if (a == b) {
                                            anySame.set(true);
                                        }
                                        return null;
                                    });
        if (anySame.get()) {
            throw new IllegalArgumentException("You must provide three pair of different objects in order");
        }

        int i = 0;
        for (T item : Arrays.asList(least1, middle1, greatest1)) {
            i++;
            // null is not an instance of any class, and e.compareTo(null) should throw a
            // NullPointerException
            try {
                comparator.compare(item, null);
                //noinspection ConstantConditions
                assertFalse("comparator.compare(item, null) should throw a NullPointerException" +
                            "even though e.equals(null) returns false, but item " + i + "did not.",
                            true);
            } catch (NullPointerException | IllegalArgumentException ignore) {
            }

            try {
                comparator.compare(null, item);
                //noinspection ConstantConditions
                assertFalse("comparator.compare(null, item) should throw a NullPointerException" +
                            "even though e.equals(null) returns false, but item " + i + "did not.",
                            true);
            } catch (NullPointerException | IllegalArgumentException ignore) {
            }
        }

        Named<T> least = t2(least1, "Least");
        Named<T> middle = t2(middle1, "Middle");
        Named<T> greatest = t2(greatest1, "Greatest");

        for (Named pair : Arrays.asList(least, middle, greatest)) {
            // Consistent with equals: (e1.compareTo(e2) == 0) if and only if e1.equals(e2)
            pairComp(pair, EQZ, pair, comparator);
        }

        pairComp(least, LTZ, middle, comparator);
        pairComp(least, LTZ, greatest, comparator);
        pairComp(middle, LTZ, greatest, comparator);

        pairComp(greatest, GTZ, middle, comparator);
        pairComp(greatest, GTZ, least, comparator);
        pairComp(middle, GTZ, least, comparator);
    }
}