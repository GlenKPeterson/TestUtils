package org.organicdesign.testUtils.http

import junit.framework.TestCase.assertEquals
import org.junit.Test
import javax.servlet.http.Cookie


class CookiePrinterTest {
    @Test
    fun testIndent() {
        // C is for cookie, that's good enough for me
        // https://www.youtube.com/watch?v=BovQyphS8kA
        val c = Cookie("n", "v")
        assertEquals("Cookie(\"n\", \"v\")",
                     CookiePrinter(c).toString())

        c.domain = "www.domain.com"
        assertEquals("Cookie(\"n\", \"v\",\n" +
                     "       domain=\"www.domain.com\")",
                     CookiePrinter(c).toString())

        c.maxAge = 1729
        assertEquals("Cookie(\"n\", \"v\",\n" +
                     "       domain=\"www.domain.com\",\n" +
                     "       maxAge=1729)",
                     CookiePrinter(c).toString())

        c.path = "/catalog"
        assertEquals("Cookie(\"n\", \"v\",\n" +
                     "       domain=\"www.domain.com\",\n" +
                     "       maxAge=1729,\n" +
                     "       path=\"/catalog\")",
                     CookiePrinter(c).toString())

        c.secure = true
        assertEquals("Cookie(\"n\", \"v\",\n" +
                     "       domain=\"www.domain.com\",\n" +
                     "       maxAge=1729,\n" +
                     "       path=\"/catalog\",\n" +
                     "       secure)",
                     CookiePrinter(c).toString())

        c.version = 3
        assertEquals("Cookie(\"n\", \"v\",\n" +
                     "       domain=\"www.domain.com\",\n" +
                     "       maxAge=1729,\n" +
                     "       path=\"/catalog\",\n" +
                     "       secure,\n" +
                     "       version=3)",
                     CookiePrinter(c).toString())

        c.isHttpOnly = true
        assertEquals("Cookie(\"n\", \"v\",\n" +
                     "       domain=\"www.domain.com\",\n" +
                     "       maxAge=1729,\n" +
                     "       path=\"/catalog\",\n" +
                     "       secure,\n" +
                     "       version=3,\n" +
                     "       httpOnly)",
                     CookiePrinter(c).toString())
    }
}