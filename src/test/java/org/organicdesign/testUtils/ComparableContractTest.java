package org.organicdesign.testUtils;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Objects;

import static org.organicdesign.indented.StringUtils.stringify;
import static org.organicdesign.testUtils.ComparableContract.testCompareTo;

public class ComparableContractTest {

    /**
     * Returns a unique object for the given primitive integer.
     * Normally this is a really bad idea (the list of suppressed warnings is longer than the entire method),
     * but when trying to test comparisons, it's handy.
     */
    @SuppressWarnings({"CachedNumberConstructorCall", "UnnecessaryBoxing", "SameParameterValue", "deprecation"})
    private Integer uniqueInteger(int i) { return new Integer(i); }

    @Test
    public void testTestCompareTo() {
        testCompareTo(Integer.MIN_VALUE, Integer.MIN_VALUE,
                      uniqueInteger(0), uniqueInteger(0),
                      Integer.MAX_VALUE, Integer.MAX_VALUE);
        testCompareTo(0.1, 0.1,
                      0.2, 0.2,
                      0.3, 0.3);
        testCompareTo(Double.MIN_VALUE, Double.MIN_VALUE,
                      0.1, 0.1,
                      Double.MAX_VALUE, Double.MAX_VALUE);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testDuplicateEx() {
        Double dotOne = 0.1;
        testCompareTo(dotOne, dotOne,
                      0.2, 0.2,
                      0.3, 0.3);
    }

    @Test (expected = AssertionError.class)
    public void testEqualObjsEx() {
        testCompareTo(0.2, 0.2,
                      0.2, 0.2,
                      0.2, 0.2);
    }

    @Test (expected = AssertionError.class)
    public void testMissingException() {
        testCompareTo(new Dumbo(1, "1"), new Dumbo(1, "1"),
                      new Dumbo(1, "2"), new Dumbo(1, "2"),
                      new Dumbo(2, "1"), new Dumbo(2, "1"));
    }

    static class Dumbo implements Comparable<Dumbo> {
        final int i;
        final @NotNull String s;
        Dumbo(int i1, @NotNull String s1) { i = i1; s = s1; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if ( !( o instanceof Dumbo)) {
                return false;
            }
            Dumbo dumbo = (Dumbo) o;
            return i == dumbo.i &&
                   Objects.equals(s, dumbo.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, s);
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(Dumbo dumbo) {
            // Comparing to null is evil, bad, and wrong because it's one-sided.
            // Comparing dumbo to null may seem sensible because you can call dumbo.compareTo(null)
            // But you can't call null.compareTo(dumbo).  Never do this.  It's here for test-coverage only.
            if (dumbo == null) {
                return -1;
            }
            int ret = Integer.compare(i, dumbo.i);
            if (ret != 0)  {
                return ret;
            }
            return s.compareTo(dumbo.s);
        }

        @Override
        public String toString() {
            return "Dumbo(" + i + ", " + stringify(s) + ")";
        }
    }
}