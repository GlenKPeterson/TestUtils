package org.organicdesign.testUtils

import org.organicdesign.testUtils.StringDiff.differentMiddle
import kotlin.test.Test
import kotlin.test.assertEquals

class StringDiffTest {
    @Test
    fun testShortDiff() {
        assertEquals("" to "",
                     differentMiddle("", ""))
        assertEquals("" to "",
                     differentMiddle("Hello", "Hello"))

        assertEquals("i" to "",
                     differentMiddle("Hi", "H"))

        assertEquals("" to "o",
                     differentMiddle("Hell", "Hello"))

        assertEquals("Media" to "Hello",
                     differentMiddle("Media", "Hello"))

        assertEquals("M" to "H",
                     differentMiddle("Mello", "Hello"))

        assertEquals("Mellow" to "Hello",
                     differentMiddle("Mellow", "Hello"))

        assertEquals("od" to "an",
                     differentMiddle("coddle", "candle"))

        assertEquals("nd" to "ck",
                     differentMiddle("kind", "kick"))

        assertEquals("d" to "n",
                     differentMiddle("paid", "pain"))

        assertEquals("n" to "",
                     differentMiddle("dinner", "diner"))

        assertEquals("" to "n",
                     differentMiddle("diner", "dinner"))

        assertEquals("abc" to "def",
                     differentMiddle("abc", "def"))

        assertEquals("def" to "",
                     differentMiddle("abcdefghi", "abcghi"))

    }
}