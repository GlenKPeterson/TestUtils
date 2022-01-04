package org.organicdesign.testUtils.http

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode

class KvTest {
    @Test
    fun testBasics() {
        assertEquals("a", Kv("a", "b").key)
        assertEquals("b", Kv("a", "b").value)

        assertEquals("Kv(\"a\", \"b\")",
                     Kv("a", "b").toString())

        equalsDistinctHashCode(Kv("a", "b"),
                               Kv("a", "b"),
                               Kv("a", "b"),
                               Kv("a", "c"))

        equalsDistinctHashCode(Kv("a", "b"),
                               Kv("a", "b"),
                               Kv("a", "b"),
                               Kv("c", "b"))
    }
}