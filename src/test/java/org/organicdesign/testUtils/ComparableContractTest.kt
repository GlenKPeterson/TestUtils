package org.organicdesign.testUtils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.organicdesign.indented.StringUtils.stringify
import org.organicdesign.testUtils.ComparableContract.testCompareTo
import java.util.*

class ComparableContractTest {

    @Test
    fun testTestCompareTo() {
        assertNotSame(Integer.MIN_VALUE, Integer.MIN_VALUE)
        // Kotlin keeps a cache of small integers.
        // As of 1.6.10, every time an UNCACHED primitive appears it's boxed as a new object.
        assertNotSame(270, 270)
        testCompareTo(
            Integer.MIN_VALUE, Integer.MIN_VALUE,
            270, 270,
            Integer.MAX_VALUE, Integer.MAX_VALUE
        )
        testCompareTo(
            0.1, 0.1,
            0.2, 0.2,
            0.3, 0.3
        )
        testCompareTo(
            java.lang.Double.MIN_VALUE, java.lang.Double.MIN_VALUE,
            0.1, 0.1,
            java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE
        )
    }

    @Test
    fun testDuplicateEx() {
        // Kotlin 1.6.10 unboxes most primitives,
        // unless you use a nullable type.
        @Suppress("RedundantNullableReturnType")
        val dotOne: Double? = 0.1
        assertSame(dotOne, dotOne)
        assertThrowsExactly(
            IllegalArgumentException::class.java
        ) {
            testCompareTo(
                dotOne!!, dotOne,
                0.2, 0.2,
                0.3, 0.3
            )
        }
    }

    @Test
    fun testEqualObjsEx() {
        assertThrowsExactly(
            AssertionError::class.java
        ) {
            testCompareTo(
                0.2, 0.2,
                0.2, 0.2,
                0.2, 0.2
            )
        }
    }

    @Test
    fun testMissingException() {
        val ae: AssertionError = assertThrows {
            testCompareTo(
                Dumbo(1, "1"), Dumbo(1, "1"),
                Dumbo(1, "2"), Dumbo(1, "2"),
                Dumbo(2, "1"), Dumbo(2, "1")
            )
        }
        assertEquals("e.compareTo(null) should throw some kind of " +
                     "RuntimeException (NullPointer/IllegalArgument/" +
                     "IllegalState, etc.) even though e.equals(null) returns " +
                     "false. Item 1 threw no exception!",
                     ae.message)
    }

    internal class Dumbo(val i: Int, val s: String) : Comparable<Dumbo?> {

        override fun equals(other: Any?): Boolean =
            (other is Dumbo) &&
            i == other.i &&
            s == other.s

        override fun hashCode(): Int = Objects.hash(i, s)

        override fun compareTo(other: Dumbo?): Int {
            // Comparing to null is evil, bad, and wrong because it's one-sided.
            // Comparing dumbo to null may seem sensible because you can call dumbo.compareTo(null)
            // But you can't call null.compareTo(dumbo).  Never do this.  It's here for test-coverage only.
            if (other == null) {
                return -1
            }
            val ret = Integer.compare(i, other.i)
            return if (ret != 0) {
                ret
            } else s.compareTo(other.s)
        }

        override fun toString(): String = "Dumbo(" + i + ", " + stringify(s) + ")"
    }

    internal class SortSameEqDiff(
        private val i: Int
    ) : Comparable<SortSameEqDiff> {

        override fun equals(other: Any?): Boolean = other is SortSameEqDiff &&
                                                    i == other.i + 1

        override fun hashCode(): Int = i.hashCode()

        override fun compareTo(other: SortSameEqDiff): Int = i.compareTo(other.i)

        override fun toString(): String = "SortSameEqDiff($i)"
    }

    @Test
    fun testMustBeCompatiblyEqualToPairedElement() {
        val ae: AssertionError = assertThrows {
            testCompareTo(
                SortSameEqDiff(1), SortSameEqDiff(1),
                SortSameEqDiff(2), SortSameEqDiff(2),
                SortSameEqDiff(3), SortSameEqDiff(3)
            )
        }
        assertTrue(ae.message!!.contains("must be compatibly equal to its paired"))
    }

    internal class SortSameNeSelf(
        private val i: Int
    ) : Comparable<SortSameNeSelf> {

        override fun equals(other: Any?): Boolean =
            (other is SortSameNeSelf) &&
            this !== other && // Can't equal itself (an error)
            i == other.i

        override fun hashCode(): Int = i.hashCode()

        override fun compareTo(other: SortSameNeSelf): Int = i.compareTo(other.i)

        override fun toString(): String = "SortSameNeSelf($i)"
    }

    @Test
    fun testNotEqualToItself() {
        val ae: AssertionError = assertThrows {
            testCompareTo(
                SortSameNeSelf(1), SortSameNeSelf(1),
                SortSameNeSelf(2), SortSameNeSelf(2),
                SortSameNeSelf(3), SortSameNeSelf(3)
            )
        }
        assertEquals("a.equals(a) should have returned true for item 1 (should equal itself)", ae.message)
    }

    class SortSameEqNull(
        private val i: Int
    ) : Comparable<SortSameEqNull> {

        override fun equals(other: Any?): Boolean {
            if (other == null) {
                return true // An error for any object to equal null.
            }
            if (other !is SortSameEqNull) {
                return false
            }
            return i == other.i
        }

        override fun hashCode(): Int = i.hashCode()

        override fun compareTo(other: SortSameEqNull): Int = i.compareTo(other.i)

        override fun toString(): String = "SortSameEqNull($i)"
    }

    @Test
    fun testEqualNull() {
        val ae: AssertionError = assertThrows {
            testCompareTo(
                SortSameEqNull(1), SortSameEqNull(1),
                SortSameEqNull(2), SortSameEqNull(2),
                SortSameEqNull(3), SortSameEqNull(3)
            )
        }
        assertEquals("item.equals(null) should always be false.  Item 1 failed", ae.message)
    }

    internal class SortSameBadHash(
        private val i: Int
    ) : Comparable<SortSameBadHash> {

        override fun equals(other: Any?): Boolean = (other is SortSameBadHash) &&
                                                    i == other.i

        override fun hashCode(): Int = System.identityHashCode(this) // error - inconsistent hashcode!

        override fun compareTo(other: SortSameBadHash): Int = i.compareTo(other.i)

        override fun toString(): String = "SortSameBadHash($i)"
    }

    @Test
    fun testBadHash() {
        val ae: AssertionError = assertThrows {
            testCompareTo(
                SortSameBadHash(1), SortSameBadHash(1),
                SortSameBadHash(2), SortSameBadHash(2),
                SortSameBadHash(3), SortSameBadHash(3)
            )
        }
        assertEquals("Least A should have the same hashcode as its paired B element",
                     ae.message)
    }
}