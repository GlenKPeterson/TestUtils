package org.organicdesign.testUtils.string

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test


internal class DiffResultTest {
    @Test
    fun testBasics() {
        assertEquals(DiffResult.IDENTICAL,
                     DiffResult("", ""))
        assertEquals(DiffResult("", ""),
                     DiffResult.IDENTICAL)
        assertNotEquals(DiffResult("a", ""),
                        DiffResult("b", ""))
        assertNotEquals(DiffResult("a", ""),
                        DiffResult("", "a"))
        assertNotEquals(DiffResult("", "b"),
                        DiffResult("", "a"))

        assertEquals(DiffResult("a", ""),
                     DiffResult("a", ""))
        assertEquals(DiffResult("", "b"),
                     DiffResult("", "b"))
        assertEquals(DiffResult("a", "b"),
                     DiffResult("a", "b"))

        assertEquals("a",
                     DiffResult("a", "b").first)
        assertEquals("b",
                     DiffResult("a", "b").second)

        assertEquals("DiffResult(\"Hello\", \"World\")",
                     DiffResult("Hello", "World").toString())
        assertEquals("IDENTICAL",
                     DiffResult("", "").toString())
    }
}