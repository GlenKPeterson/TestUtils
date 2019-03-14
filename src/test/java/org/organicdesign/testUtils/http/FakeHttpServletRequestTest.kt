package org.organicdesign.testUtils.http

import org.junit.Test

import java.util.Arrays
import java.util.Locale
import java.util.TreeMap

import org.junit.Assert.*
import org.organicdesign.testUtils.http.FakeHttpServletRequest.Companion.Kv

class FakeHttpServletRequestTest {
    @Test
    fun testBasics() {
        val headers = listOf(Kv("First", "Primero"),
                Kv("Second", "Secundo"),
                Kv("Third", "Tercero"))

        val stuff = arrayOf("a", "b", "c")
        val thing = arrayOf("JustOne")

        val params = TreeMap<String, List<String>>()
        params["stuff"] = Arrays.asList(*stuff)
        params["thing"] = Arrays.asList(*thing)

        val hsr = ReqB().baseUrl("https://sub.example.com")
                .uri("/path/file.html")
                .headers(headers)
                .params(params)
                .toReq()
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

        assertEquals(Locale.TRADITIONAL_CHINESE, ReqB.funDefaults().toReq().locale)
        assertEquals(Locale.US, ReqB().locale(Locale.US).toReq().locale)

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

        assertEquals("GET", ReqB.funDefaults().toReq().method)
        assertEquals("POST", ReqB().method("POST").toReq().method)

        assertEquals("2FCF6F9AA75782B8B783308DE74BC557", ReqB.funDefaults().toReq().requestedSessionId)
        assertEquals("Hello", ReqB().requestedSessionId("Hello").toReq().requestedSessionId)

        assertEquals("0:0:0:0:0:0:0:1", ReqB.funDefaults().toReq().remoteAddr)
        assertEquals("Dumpling", ReqB().remoteAddr("Dumpling").toReq().remoteAddr)

    }

}