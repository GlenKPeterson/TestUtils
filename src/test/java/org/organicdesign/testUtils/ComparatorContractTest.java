package org.organicdesign.testUtils;

import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 Created by Glen K. Peterson on 3/28/17.
 */
public class ComparatorContractTest {
    @Test
    public void testTestComparator() {
        ComparatorContract.testComparator("a", "b", "c", String::compareTo);
        ComparatorContract.testComparator(Integer.MIN_VALUE, 0, Integer.MAX_VALUE, Integer::compareTo);
    }

    @Test
    public void testDuplicateEx() {
        String a = "a";
        assertThrowsExactly(IllegalArgumentException.class,
                            () -> ComparatorContract.testComparator(a, a, "c", String::compareTo));
    }

    @Test
    public void testEqualObjsEx() {
        assertThrowsExactly(AssertionError.class,
                            () -> ComparatorContract.testComparator(375, 375, 375,
                                                                    Integer::compareTo));
    }

    @Test
    public void testNullProblemEx1() {
        Comparator<Integer> badComp = (a, b) -> {
            if (a == null) {
                return 0;
            }
            return a - b;
        };
        assertThrowsExactly(AssertionError.class,
                            () -> ComparatorContract.testComparator(27, 100, 375, badComp));
    }

    @Test
    public void testNullProblemEx2() {
        Comparator<Integer> badComp = (a, b) -> {
            if (b == null) {
                return 0;
            }
            return a - b;
        };
        assertThrowsExactly(AssertionError.class,
                            () -> ComparatorContract.testComparator(27, 100, 375, badComp));
    }
}