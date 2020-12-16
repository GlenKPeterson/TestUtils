package org.organicdesign.testUtils

import org.organicdesign.testUtils.StringDiff.singleShortestDiffSubstring
import kotlin.test.Test
import kotlin.test.assertEquals

class StringDiffTest {
    @Test
    fun testShortDiff() {
        assertEquals("" to "",
                     singleShortestDiffSubstring("", ""))
        assertEquals("" to "",
                     singleShortestDiffSubstring("Hello", "Hello"))

        assertEquals("i" to "",
                     singleShortestDiffSubstring("Hi", "H"))

        assertEquals("" to "o",
                     singleShortestDiffSubstring("Hell", "Hello"))

        assertEquals("Media" to "Hello",
                     singleShortestDiffSubstring("Media", "Hello"))

        assertEquals("M" to "H",
                     singleShortestDiffSubstring("Mello", "Hello"))

        assertEquals("Mellow" to "Hello",
                     singleShortestDiffSubstring("Mellow", "Hello"))

        assertEquals("od" to "an",
                     singleShortestDiffSubstring("coddle", "candle"))

        assertEquals("nd" to "ck",
                     singleShortestDiffSubstring("kind", "kick"))

        assertEquals("d" to "n",
                     singleShortestDiffSubstring("paid", "pain"))

        assertEquals("n" to "",
                     singleShortestDiffSubstring("dinner", "diner"))

        assertEquals("" to "n",
                     singleShortestDiffSubstring("diner", "dinner"))

        assertEquals("abc" to "def",
                     singleShortestDiffSubstring("abc", "def"))

        assertEquals("def" to "",
                     singleShortestDiffSubstring("abcdefghi", "abcghi"))

    }
}