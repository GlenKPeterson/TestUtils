package org.organicdesign.testUtils

import org.junit.Test

import java.util.Arrays
import java.util.Locale
import java.util.TreeMap

import org.junit.Assert.*
import org.organicdesign.testUtils.FakeHttpServletRequest.*
import org.organicdesign.testUtils.FakeHttpServletRequest.Companion.fakeReq

class FakeHttpServletRequestTest {
    @Test
    fun testBasics() {
        val headers = Arrays.asList<Map.Entry<String, String>>(HttpField("First", "Primero"),
                HttpField("Second", "Secundo"),
                HttpField("Third", "Tercero"))

        val stuff = arrayOf("a", "b", "c")
        val thing = arrayOf("JustOne")

        val params = TreeMap<String, List<String>>()
        params["stuff"] = Arrays.asList(*stuff)
        params["thing"] = Arrays.asList(*thing)

        val hsr = fakeReq("https://sub.example.com", "/path/file.html", headers, params)
        assertNull(hsr.getHeaders(null))

        assertEquals("Primero", hsr.getHeader("First"))

        assertEquals("stuff=a&stuff=b&stuff=c&thing=JustOne", hsr.queryString)
        val pNmEn = hsr.parameterNames
        assertEquals("stuff", pNmEn.nextElement())
        assertEquals("thing", pNmEn.nextElement())
        assertFalse(pNmEn.hasMoreElements())

        assertArrayEquals(stuff, hsr.getParameterValues("stuff"))
        assertArrayEquals(thing, hsr.getParameterValues("thing"))


        val testMap = hsr.parameterMap
        assertEquals(params.size.toLong(), testMap.size.toLong())
        for (k in params.keys) {
            assertTrue(testMap.containsKey(k))
            assertArrayEquals("Param values equal for $k",
                    params[k]!!.toTypedArray(), testMap[k])
        }

        assertEquals("/path/file.html", hsr.pathInfo)
        assertEquals("/path/file.html", hsr.requestURI)
        assertEquals("/path/file.html", hsr.servletPath)
        assertEquals("https://sub.example.com/path/file.html", hsr.requestURL.toString())

        assertEquals(Locale.US, hsr.locale)

        assertNull(hsr.getAttribute("attr1"))
        assertFalse(hsr.attributeNames.hasMoreElements())

        hsr.setAttribute("attr1", "val1")
        assertEquals("val1", hsr.getAttribute("attr1"))

        val attrNameEn = hsr.attributeNames
        assertEquals("attr1", attrNameEn.nextElement())
        assertFalse(attrNameEn.hasMoreElements())

        hsr.removeAttribute("attr1")
        assertNull(hsr.getAttribute("attr1"))

        hsr.characterEncoding = "WinAnsi"
        assertEquals("WinAnsi", hsr.characterEncoding)

        assertEquals("GET", hsr.method)
        hsr.fakeMethod = "POST"
        assertEquals("POST", hsr.method)

        assertEquals("2FCF6F9AA75782B8B783308DE74BC557", hsr.requestedSessionId)
        hsr.fakeRequestedSessionId = "Hello"
        assertEquals("Hello", hsr.requestedSessionId)

        assertEquals("0:0:0:0:0:0:0:1", hsr.remoteAddr)
        hsr.fakeRemoteAddr = "Dumpling"
        assertEquals("Dumpling", hsr.remoteAddr)

    }

}