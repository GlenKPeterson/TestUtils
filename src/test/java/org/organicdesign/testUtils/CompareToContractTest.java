package org.organicdesign.testUtils;

import org.junit.Test;

import static org.organicdesign.testUtils.CompareToContract.testCompareTo;

public class CompareToContractTest {

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
}