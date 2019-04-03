package org.organicdesign.testUtils.http

import org.junit.Test

import java.util.Locale.TRADITIONAL_CHINESE
import org.junit.Assert.*
import org.organicdesign.testUtils.http.FakeHttpServletResponse.Companion.httpServletResponse
import java.util.*
import javax.servlet.http.Cookie

class FakeHttpServletResponseTest {
    @Test
    fun testBasics() {
        val hsr = httpServletResponse()
        hsr.status = 9
        assertEquals(9, hsr.status.toLong())
        hsr.sendRedirect("somewhere")
        assertEquals("somewhere", hsr.redirect)

        assertFalse(hsr.containsHeader("Hello"))
        hsr.setHeader("Hello", "World")
        assertTrue(hsr.containsHeader("Hello"))
        assertEquals("World", hsr.getHeader("Hello"))

        hsr.addHeader("Hello", "Pumpkin")
        assertTrue(hsr.containsHeader("Hello"))
        assertEquals(listOf("World", "Pumpkin"),
                     hsr.getHeaders("Hello"))
        assertEquals("World", hsr.getHeader("Hello"))

        assertFalse(hsr.containsHeader("Buddy"))
        assertNull(hsr.getHeader("Buddy"))
        hsr.setHeader("Buddy", "Rich")

        assertEquals(setOf("Hello", "Buddy"),
                     hsr.headerNames)

        // Cupcake should replace World (the first value for Hello).
        hsr.setHeader("Hello", "Cupcake")
        assertEquals(listOf("Cupcake", "Pumpkin"),
                     hsr.getHeaders("Hello"))
        assertEquals("Cupcake", hsr.getHeader("Hello"))

        val timeL = Date().time
        hsr.setDateHeader("One", timeL)
        assertEquals(timeL.toString(), hsr.getHeader("One"))

        hsr.addDateHeader("One", timeL + 1)
        assertEquals(listOf(timeL.toString(), (timeL + 1).toString()), hsr.getHeaders("One"))

        hsr.setIntHeader("Two", 2)
        assertEquals(2.toString(), hsr.getHeader("Two"))

        hsr.addIntHeader("Two", 3)
        assertEquals(listOf(2.toString(), 3.toString()), hsr.getHeaders("Two"))

        hsr.setLocale(TRADITIONAL_CHINESE)
        assertEquals(TRADITIONAL_CHINESE,
                     hsr.locale)

        hsr.outputStream.write("hi".toByteArray())
        assertEquals("hi",
                     (hsr.outputStream as FakeServletOutputStream).stringBuilder.toString())

        hsr.setContentType("cranberry")
        assertEquals("cranberry", hsr.contentType)

        assertFalse(hsr.isCommitted)

        hsr.sendError(404)
        assertEquals(404, hsr.status)
        assertEquals("text/html", hsr.contentType)
        assertTrue(hsr.isCommitted)

        assertNull(hsr.characterEncoding)
        hsr.setContentType("text/html;charset=UTF-8")
        assertEquals("UTF-8", hsr.characterEncoding)
        hsr.characterEncoding = "ISO-8859-1"
        assertEquals("ISO-8859-1", hsr.characterEncoding)
        hsr.setContentType("text/html;charset=UTF-8")
        assertEquals("UTF-8", hsr.characterEncoding)

        hsr.addCookie(Cookie("cName", "cValue"))

        assertEquals("FakeHttpServletResponse(\n" +
                     "        status=404,\n" +
                     "        committed=true,\n" +
                     "        redirect=somewhere,\n" +
                     "        contentType=text/html;charset=UTF-8,\n" +
                     "        encoding=UTF-8,\n" +
                     "        locale=zh_TW,\n" +
                     "        cookies=listOf(Cookie(\"cName\", \"cValue\")),\n" +
                     "        outputStream=FakeServletOutputStream(\"hi\"),\n" +
                     "        headers=listOf(Kv(\"Hello\", \"Cupcake\"),\n" +
                     "                       Kv(\"Hello\", \"Pumpkin\"),\n" +
                     "                       Kv(\"Buddy\", \"Rich\"),\n" +
                     "                       Kv(\"One\", \"$timeL\"),\n" +
                     "                       Kv(\"One\", \"${timeL + 1}\"),\n" +
                     "                       Kv(\"Two\", \"2\"),\n" +
                     "                       Kv(\"Two\", \"3\")),\n" +
                     ")",
                     hsr.toString())
    }

    @Test(expected = IllegalStateException::class)
    fun testEx01() {
        val hsr = httpServletResponse()
        hsr.sendError(404)
        hsr.sendError(404)
    }

}