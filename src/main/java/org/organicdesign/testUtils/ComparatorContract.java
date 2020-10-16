package org.organicdesign.testUtils;

import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
import static org.organicdesign.testUtils.ComparatorContract.CompToZero.*;
/**
 Created by Glen K. Peterson on 3/28/17.
 */
public class ComparatorContract {
    public enum CompToZero {
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
        final @NotNull T a;
        final @NotNull String name;
        Named(
                @NotNull T theA,
                @NotNull String nm
        ) {
            a = theA;
            name = nm;
        }
    }

    private static <T>
    void pairComp(
            @NotNull Named<T> first,
            @NotNull CompToZero comp,
            @NotNull Named<T> second,
            @NotNull Comparator<T> comparator
    ) {
        assertTrue("The " + first.name + " item must be " + comp.english() +
                   " the " + second.name,
                   comp.vsZero(comparator.compare(first.a, second.a)));
    }

    /**
     Tests the various properties the Comparable contract is supposed to uphold.  Also tests that
     the behavior of compareTo() is compatible with equals() and hashCode() which is strongly
     suggested, but not actually required, but keeps things much simpler (your class will behave the same
     way in a sorted and unsorted set or map).

     It's safest to also make your comparator serializable.  Instead of a static class, use an enum singleton.
     If your comparator must be instantiated with parameters, ensure that it is serializable, for instance
     with {@link Serialization#serializeDeserialize(Object)} before passing it to this method.

     Expects three unique objects.
     The first must be less than the second, which in turn is less than the third.

     See note in class documentation.
     */
    // Many of the comments in this method are paraphrases or direct quotes from the Javadocs for
    // the Comparable interface.  That is where this contract is specified.
    // https://docs.oracle.com/javase/8/docs/api/
    public static <T>
    void testComparator(
            @NotNull T least1,
            @NotNull T middle1,
            @NotNull T greatest1,
            @NotNull Comparator<T> comparator
    ) {

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
                //noinspection ResultOfMethodCallIgnored
                comparator.compare(item, null);
                fail("comparator.compare(item, null) should throw some kind of RuntimeException" +
                     " (NullPointer/IllegalArgument/IllegalState, etc.)" +
                     " even though e.equals(null) returns false." +
                     " Item " + i + " threw no exception.");
            } catch (RuntimeException ignore) {
                // Previously we had allowed NullPointerException and IllegalArgumentException.
                // Kotlin throws IllegalStateException, so we now expect any RuntimeException
                // to be thrown.
                //
                // This reports no-test-coverage, but if you uncomment this, you can
                // prove that it *is* covered.
//                System.out.println("Pass: " + comparator + " " + item + " " + ignore);
            }

            try {
                //noinspection ResultOfMethodCallIgnored
                comparator.compare(null, item);
                fail("comparator.compare(null, item) should throw some kind of RuntimeException" +
                     " (NullPointer/IllegalArgument/IllegalState, etc.)" +
                     " even though e.equals(null) returns false." +
                     " Item " + i + " threw no exception.");
            } catch (RuntimeException ignore) {
                // Previously we had allowed NullPointerException and IllegalArgumentException.
                // Kotlin throws IllegalStateException, so we now expect any RuntimeException
                // to be thrown.
                //
                // This reports no-test-coverage, but if you uncomment this, you can
                // prove that it *is* covered.
//                System.out.println("Pass2: " + comparator + " " + item);
            }
        }

        Named<T> least = new Named<>(least1, "Least");
        Named<T> middle = new Named<>(middle1, "Middle");
        Named<T> greatest = new Named<>(greatest1, "Greatest");

        for (Named<T> pair : Arrays.asList(least, middle, greatest)) {
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
