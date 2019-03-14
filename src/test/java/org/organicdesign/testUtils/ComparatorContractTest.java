package org.organicdesign.testUtils;

import org.junit.Test;

import java.util.Comparator;

/**
 Created by gpeterso on 3/28/17.
 */
public class ComparatorContractTest {
    @Test
    public void testTestComparator() {
        ComparatorContract.testComparator("a", "b", "c", String::compareTo);
        ComparatorContract.testComparator(Integer.MIN_VALUE, new Integer(0), Integer.MAX_VALUE, Integer::compareTo);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testDuplicateEx() {
        String a = "a";
        ComparatorContract.testComparator(a, a, "c", String::compareTo);
    }

    @Test (expected = AssertionError.class)
    public void testEqualObjsEx() {
        ComparatorContract.testComparator(new Integer(375), new Integer(375), new Integer(375), Integer::compareTo);
    }

    @Test (expected = AssertionError.class)
    public void testNullProblemEx1() {
        Comparator<Integer> badComp = new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                if (a == null) {
                    return 0;
                }
                return a - b;
            }
        };
        ComparatorContract.testComparator(new Integer(27), new Integer(100), new Integer(375), badComp);
    }

    @Test (expected = AssertionError.class)
    public void testNullProblemEx2() {
        Comparator<Integer> badComp = new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                if (b == null) {
                    return 0;
                }
                return a - b;
            }
        };
        ComparatorContract.testComparator(new Integer(27), new Integer(100), new Integer(375), badComp);
    }

}