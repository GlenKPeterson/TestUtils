package org.organicdesign.testUtils.http

import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class CookiePrinterTest {
    @Test
    fun testIndent() {
        // C is for cookie, that's good enough for me
        // https://www.youtube.com/watch?v=BovQyphS8kA
        val c = Cookie("n", "v")
        assertEquals("Cookie(\"n\", \"v\")",
                     CookiePrinter(c).toString())

        c.domain = "www.domain.com"
        assertEquals("Cookie(\"n\", \"v\", domain=\"www.domain.com\")",
                     CookiePrinter(c).toString())

        c.maxAge = 1729
        assertEquals("Cookie(\"n\", \"v\", domain=\"www.domain.com\", maxAge=1729)",
                     CookiePrinter(c).toString())

        c.path = "/catalog"
        assertEquals("Cookie(\"n\", \"v\", domain=\"www.domain.com\", maxAge=1729, path=\"/catalog\")",
                     CookiePrinter(c).toString())

        c.secure = true
        assertEquals("Cookie(\"n\", \"v\", domain=\"www.domain.com\", maxAge=1729, path=\"/catalog\", secure=true)",
                     CookiePrinter(c).toString())

        c.version = 3
        assertEquals("Cookie(\"n\", \"v\", domain=\"www.domain.com\", maxAge=1729, path=\"/catalog\", secure=true, version=3)",
                     CookiePrinter(c).toString())

        c.isHttpOnly = true
        assertEquals("Cookie(\"n\", \"v\", domain=\"www.domain.com\", maxAge=1729, path=\"/catalog\", secure=true, version=3, httpOnly=true)",
                     CookiePrinter(c).toString())
    }
}