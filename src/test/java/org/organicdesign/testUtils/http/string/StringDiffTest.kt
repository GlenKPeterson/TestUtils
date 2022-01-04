package org.organicdesign.testUtils.http.string

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.organicdesign.testUtils.string.DiffResult
import org.organicdesign.testUtils.string.StringDiff.differentMiddle

class StringDiffTest {
    @Test
    fun testShortDiff() {
        assertEquals(
            DiffResult.IDENTICAL,
            differentMiddle("", ""))
        assertEquals(DiffResult.IDENTICAL,
                     differentMiddle("Hello", "Hello"))

        assertEquals(DiffResult("i", ""),
                     differentMiddle("Hi", "H"))

        assertEquals(DiffResult("", "o"),
                     differentMiddle("Hell", "Hello"))

        assertEquals(DiffResult("Media", "Hello"),
                     differentMiddle("Media", "Hello"))

        assertEquals(DiffResult("M", "H"),
                     differentMiddle("Mello", "Hello"))

        assertEquals(DiffResult("Mellow", "Hello"),
                     differentMiddle("Mellow", "Hello"))

        assertEquals(DiffResult("od", "an"),
                     differentMiddle("coddle", "candle"))

        assertEquals(DiffResult("nd", "ck"),
                     differentMiddle("kind", "kick"))

        assertEquals(DiffResult("d", "n"),
                     differentMiddle("paid", "pain"))

        assertEquals(DiffResult("n", ""),
                     differentMiddle("dinner", "diner"))

        assertEquals(DiffResult("", "n"),
                     differentMiddle("diner", "dinner"))

        assertEquals(DiffResult("abc", "def"),
                     differentMiddle("abc", "def"))

        assertEquals(DiffResult("def", ""),
                     differentMiddle("abcdefghi", "abcghi"))

    }
}