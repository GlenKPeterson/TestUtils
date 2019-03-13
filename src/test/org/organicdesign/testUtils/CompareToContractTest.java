package org.organicdesign.testUtils;

import org.junit.Test;

import static org.junit.Assert.*;

public class CompareToContractTest {
    @Test
    public void testTestCompareTo() {
//        CompareToContract.testCompareTo("a", "a",
//                                        "b", "b",
//                                        "c", "c");
        CompareToContract.testCompareTo(Integer.MIN_VALUE, Integer.MIN_VALUE,
                                        new Integer(0), new Integer(0),
                                        Integer.MAX_VALUE, Integer.MAX_VALUE);
        CompareToContract.testCompareTo(0.1, 0.1,
                                        0.2, 0.2,
                                        0.3, 0.3);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testDuplicateEx() {
        Double dotOne = 0.1;
        CompareToContract.testCompareTo(dotOne, dotOne,
                                        0.2, 0.2,
                                        0.3, 0.3);
    }

    @Test (expected = AssertionError.class)
    public void testEqualObjsEx() {
        CompareToContract.testCompareTo(0.2, 0.2,
                                        0.2, 0.2,
                                        0.2, 0.2);
    }
}