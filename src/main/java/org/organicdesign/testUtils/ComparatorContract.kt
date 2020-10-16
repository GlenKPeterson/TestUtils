package org.organicdesign.testUtils

import org.junit.Assert
import org.organicdesign.testUtils.EqualsContract.permutations
import java.util.Comparator

/**
 * Created by Glen K. Peterson on 3/28/17.
 */
object ComparatorContract {
    private fun <T> pairComp(
            first: Named<T>,
            comp: CompToZero,
            second: Named<T>,
            comparator: Comparator<T>
    ) {
        Assert.assertTrue("The " + first.name + " item must be " + comp.english() +
                          " the " + second.name,
                          comp.vsZero(comparator.compare(first.a, second.a)))
    }

    /**
     * Tests the various properties the Comparable contract is supposed to uphold.  Also tests that
     * the behavior of compareTo() is compatible with equals() and hashCode() which is strongly
     * suggested, but not actually required, but keeps things much simpler (your class will behave the same
     * way in a sorted and unsorted set or map).
     *
     * It's safest to also make your comparator serializable.  Instead of a static class, use an enum singleton.
     * If your comparator must be instantiated with parameters, ensure that it is serializable, for instance
     * with [Serialization.serializeDeserialize] before passing it to this method.
     *
     * Expects three unique objects.
     * The first must be less than the second, which in turn is less than the third.
     *
     * See note in class documentation.
     */
    // Many of the comments in this method are paraphrases or direct quotes from the Javadocs for
    // the Comparable interface.  That is where this contract is specified.
    // https://docs.oracle.com/javase/8/docs/api/
    @JvmStatic
    fun <T> testComparator(
            least1: T,
            middle1: T,
            greatest1: T,
            comparator: Comparator<T>
    ) {

        // TODO: Do we want to ensure that comparators are serializable?
        var anySame = false
        permutations(listOf(least1, middle1, greatest1)
        ) { a: T, b: T ->
            if (a === b) {
                anySame = true
            }
        }
        require(!anySame) { "You must provide three pair of different objects in order" }
        var i = 0
        for (item in listOf(least1, middle1, greatest1)) {
            i++
            // null is not an instance of any class, and e.compareTo(null) should throw a
            // NullPointerException
            try {
                comparator.compare(item, null)
                Assert.fail("comparator.compare(item, null) should throw some kind of RuntimeException" +
                            " (NullPointer/IllegalArgument/IllegalState, etc.)" +
                            " even though e.equals(null) returns false." +
                            " Item " + i + " threw no exception.")
            } catch (ignore: RuntimeException) {
                // Previously we had allowed NullPointerException and IllegalArgumentException.
                // Kotlin throws IllegalStateException, so we now expect any RuntimeException
                // to be thrown.
                //
                // This reports no-test-coverage, but if you uncomment this, you can
                // prove that it *is* covered.
//                System.out.println("Pass: " + comparator + " " + item + " " + ignore);
            }
            try {
                comparator.compare(null, item)
                Assert.fail("comparator.compare(null, item) should throw some kind of RuntimeException" +
                            " (NullPointer/IllegalArgument/IllegalState, etc.)" +
                            " even though e.equals(null) returns false." +
                            " Item " + i + " threw no exception.")
            } catch (ignore: RuntimeException) {
                // Previously we had allowed NullPointerException and IllegalArgumentException.
                // Kotlin throws IllegalStateException, so we now expect any RuntimeException
                // to be thrown.
                //
                // This reports no-test-coverage, but if you uncomment this, you can
                // prove that it *is* covered.
//                System.out.println("Pass2: " + comparator + " " + item);
            }
        }
        val least = Named(least1, "Least")
        val middle = Named(middle1, "Middle")
        val greatest = Named(greatest1, "Greatest")
        for (pair in listOf(least, middle, greatest)) {
            // Consistent with equals: (e1.compareTo(e2) == 0) if and only if e1.equals(e2)
            pairComp(pair, CompToZero.EQZ, pair, comparator)
        }
        pairComp(least, CompToZero.LTZ, middle, comparator)
        pairComp(least, CompToZero.LTZ, greatest, comparator)
        pairComp(middle, CompToZero.LTZ, greatest, comparator)
        pairComp(greatest, CompToZero.GTZ, middle, comparator)
        pairComp(greatest, CompToZero.GTZ, least, comparator)
        pairComp(middle, CompToZero.GTZ, least, comparator)
    }

    internal enum class CompToZero {
        LTZ {
            override fun english(): String = "less than"
            override fun vsZero(i: Int): Boolean = i < 0
        },
        GTZ {
            override fun english(): String = "greater than"
            override fun vsZero(i: Int): Boolean = i > 0
        },
        EQZ {
            override fun english(): String = "equal to"
            override fun vsZero(i: Int): Boolean = i == 0
        };

        abstract fun english(): String
        abstract fun vsZero(i: Int): Boolean
    }

    private class Named<T>(
            val a: T,
            val name: String
    )
}