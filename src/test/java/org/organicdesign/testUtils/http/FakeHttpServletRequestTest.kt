package org.organicdesign.testUtils.http

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.Part
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.*

class FakeHttpServletRequestTest {
    @Test
    fun testBasics() {
        val timeL = Date().time

        val headers = listOf(Kv("First", "Primero"),
                             Kv("MyDate", timeL.toString()),
                             Kv("Third", "3"))

        val stuff = arrayOf("a", "b", "c")
        val thing = arrayOf("JustOne")

        val params = TreeMap<String, List<String>>()
        params["stuff"] = Arrays.asList(*stuff)
        params["thing"] = Arrays.asList(*thing)

        val hsr = ReqB().baseUrl("https://sub.example.com:8443")
                .uri("/path/file.html")
                .headers(headers)
                .params(params)
                .locale(Locale.TRADITIONAL_CHINESE)
                .requestedSessionId("MyInsecureSessId")
                .toReq()
        assertNull(hsr.getHeaders(null))

        assertEquals("Primero", hsr.getHeader("First"))

        assertEquals(listOf("First", "MyDate", "Third", "Host"),
                hsr.headerNames.toList())

        assertEquals(-1, hsr.getIntHeader("First"))
        assertEquals(3, hsr.getIntHeader("Third"))
        assertEquals(timeL, hsr.getDateHeader("MyDate"))

        assertEquals("stuff=a&stuff=b&stuff=c&thing=JustOne", hsr.queryString)
        val pNmEn = hsr.parameterNames
        assertEquals("stuff", pNmEn.nextElement())
        assertEquals("thing", pNmEn.nextElement())
        assertFalse(pNmEn.hasMoreElements())

        assertArrayEquals(stuff, hsr.getParameterValues("stuff"))
        assertEquals("a", hsr.getParameter("stuff"))
        assertArrayEquals(thing, hsr.getParameterValues("thing"))


        val testMap = hsr.parameterMap
        assertEquals(params.size.toLong(), testMap.size.toLong())
        for (k in params.keys) {
            assertTrue(testMap.containsKey(k))
            assertArrayEquals(params[k]!!.toTypedArray(), testMap[k],
                              "Param values equal for $k")
        }

        assertEquals("/path/file.html", hsr.pathInfo)
        assertEquals("/path/file.html", hsr.requestURI)
        assertEquals("/path/file.html", hsr.servletPath)
        assertEquals("https://sub.example.com:8443/path/file.html", hsr.requestURL.toString())

        assertEquals(Locale.TRADITIONAL_CHINESE, ReqB.funDefaults().toReq().locale)
        assertEquals(Locale.US, ReqB().locale(Locale.US).toReq().locale)

        assertNull(hsr.getAttribute("attr1"))
        assertFalse(hsr.attributeNames.hasMoreElements())

        hsr.setAttribute("attr1", "val1")
        assertEquals("val1", hsr.getAttribute("attr1"))

        val attrNameEn = hsr.attributeNames
        assertEquals("attr1", attrNameEn.nextElement())
        assertFalse(attrNameEn.hasMoreElements())

        hsr.characterEncoding = "WinAnsi"
        assertEquals("WinAnsi", hsr.characterEncoding)

        hsr.setAttribute("attr2", "val2")
        assertEquals("val2", hsr.getAttribute("attr2"))

        assertEquals(Locale.TRADITIONAL_CHINESE,
                     hsr.locale)

        assertEquals("MyInsecureSessId", hsr.requestedSessionId)

        assertNull(hsr.cookies)

        assertEquals("https", hsr.scheme)
        assertEquals("sub.example.com", hsr.serverName)
        assertEquals(8443, hsr.serverPort)

        assertEquals("FakeHttpServletRequest(url=\"https://sub.example.com:8443/path/file.html\",\n" +
                     "                       remoteAddr=\"0:0:0:0:0:0:0:1\",\n" +
                     "                       method=\"GET\",\n" +
                     "                       encoding=\"WinAnsi\",\n" +
                     "                       locale=zh_TW,\n" +
                     "                       requestedSessionId=\"MyInsecureSessId\",\n" +
                     "                       attributes=mapOf(\"attr1\"=\"val1\",\n" +
                     "                                        \"attr2\"=\"val2\"),\n" +
                     "                       cookies=listOf(),\n" +
                     "                       params=mapOf(\"stuff\"=listOf(\"a\",\n" +
                     "                                                   \"b\",\n" +
                     "                                                   \"c\"),\n" +
                     "                                    \"thing\"=listOf(\"JustOne\")),\n" +
                     "                       headers=arrayOf(\"First\"=\"Primero\",\n" +
                     "                                       \"MyDate\"=\"$timeL\",\n" +
                     "                                       \"Third\"=\"3\",\n" +
                     "                                       \"Host\"=\"sub.example.com:8443\"))",
                     hsr.toString())

        hsr.removeAttribute("attr1")
        assertNull(hsr.getAttribute("attr1"))

        // Cookie doesn't have .equals() implemented.
        val myCookie = Cookie("a", "b")
        myCookie.isHttpOnly = true
        myCookie.secure = true
        assertArrayEquals(arrayOf(myCookie),
                          ReqB().cookies(listOf(myCookie)).toReq().cookies)

        assertEquals("FakeHttpServletRequest(url=\"https://domain.com\",\n" +
                     "                       remoteAddr=\"0:0:0:0:0:0:0:1\",\n" +
                     "                       method=\"GET\",\n" +
                     "                       attributes=mapOf(),\n" +
                     "                       cookies=listOf(Cookie(\"a\", \"b\", secure=true, httpOnly=true)),\n" +
                     "                       params=mapOf(),\n" +
                     "                       headers=arrayOf(\"Host\"=\"domain.com\"))",
                     ReqB().cookies(listOf(myCookie)).toReq().toString())

        assertEquals("https", ReqB().toReq().scheme)
        assertEquals("domain.com", ReqB().toReq().serverName)
        assertEquals(443, ReqB().toReq().serverPort)

        assertEquals("http", ReqB().baseUrl("http://organicdesign.org").toReq().scheme)
        assertEquals("organicdesign.org", ReqB().baseUrl("http://organicdesign.org").toReq().serverName)
        assertEquals(80, ReqB().baseUrl("http://organicdesign.org").toReq().serverPort)

        assertEquals("google.com",
                     ReqB()
                             .headers(listOf(Kv(FakeHttpServletRequest.HTTP_HEAD_HOST,
                                                "google.com:99")))
                             .toReq()
                             .serverName)
        assertEquals(99,
                     ReqB()
                             .headers(listOf(Kv(FakeHttpServletRequest.HTTP_HEAD_HOST,
                                                "google.com:99")))
                             .toReq()
                             .serverPort)

        assertEquals("GET", ReqB.funDefaults().toReq().method)
        assertEquals("POST", ReqB().method("POST").toReq().method)

        assertEquals("2FCF6F9AA75782B8B783308DE74BC557", ReqB.funDefaults().toReq().requestedSessionId)
        assertEquals("Hello", ReqB().requestedSessionId("Hello").toReq().requestedSessionId)

        assertEquals("0:0:0:0:0:0:0:1", ReqB.funDefaults().toReq().remoteAddr)
        assertEquals("Dumpling", ReqB().remoteAddr("Dumpling").toReq().remoteAddr)

        assertEquals("v1", ReqB().attributes(mutableMapOf("k1" to "v1")).toReq()
                .getAttribute("k1"))

        assertNull(ReqB().toReq().contentType)

        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Type
        // Content-Type: text/html; charset=utf-8
        // Content-Type: multipart/form-data; boundary=something
        assertEquals("text/html; charset=utf-8",
                     ReqB().headers(listOf(Kv("Content-Type", "text/html; charset=utf-8"))).toReq().contentType)

        assertEquals(-1, ReqB().inStream(ByteArrayInputStream(byteArrayOf()), Int.MAX_VALUE + 1L).toReq()
                .contentLength)

    }

    /**
     * This is needed for file uploading.
     */
    @Test
    fun testInputStream() {
        assertNull(ReqB().toReq().inputStream)

        // Specific example text under Apache license taken from:
        // https://commons.apache.org/proper/commons-fileupload/xref-test/org/apache/commons/fileupload/servlet/ServletFileUploadTest.html
        //
        // Yes, the spec really says CRLF.
        // https://tools.ietf.org/html/rfc7578
        val text = "-----1234\r\n" +
                "Content-Disposition: form-data; name=\"utf8Html\"\r\n" +
                "\r\n" +
                "Thís ís the coñteñt of the fíle\n" +
                "\r\n" +
                "-----1234--\r\n"
        val bytes = text.byteInputStream(Charset.forName("UTF-8"))

        val req = ReqB().inStream(bytes, text.length.toLong()).toReq()

        val inStream = req.inputStream

        assertEquals(text, InputStreamReader(inStream as InputStream).readText())
        assertEquals(text.length.toLong(), req.contentLengthLong)
        assertEquals(text.length, req.contentLength)
    }

    private val fileContent = "This is the content of the file\n"
    private val postText = "-----1234\r\n" +
                           "Content-Disposition: form-data; name=\"file\"; filename=\"foo.tab\"\r\n" +
                           "Content-Type: multipart/form-data\r\n" +
                           "\r\n" +
                           fileContent +
                           "\r\n" +
                           "-----1234\r\n" +
                           "Content-Disposition: form-data; name=\"field\"\r\n" +
                           "\r\n" +
                           "fieldValue\r\n" +
                           "-----1234\r\n" +
                           "Content-Disposition: form-data; name=\"multi\"\r\n" +
                           "\r\n" +
                           "value1\r\n" +
                           "-----1234\r\n" +
                           "Content-Disposition: form-data; name=\"multi\"\r\n" +
                           "\r\n" +
                           "value2\r\n" +
                           "-----1234--\r\n"

    @Test
    fun testFileUploadNeeds() {

        val bytes = postText.byteInputStream(Charset.forName("UTF-8"))

        val req = ReqB.post(
            mapOf("Content-Type" to "multipart/form-data; boundary=---1234").entries.toList(),
            bytes,
            postText.length
        ).toReq()

        assertEquals(
            "POST",
            req.method
        )

        assertEquals(postText, InputStreamReader(req.inputStream as InputStream).readText())
        assertEquals(postText.length.toLong(), req.contentLengthLong)
        assertEquals(postText.length, req.contentLength)
    }

    @Test
    fun testParts() {
        val bytes = postText.byteInputStream(Charset.forName("UTF-8"))

        val req = ReqB.post(mapOf("Content-Type" to "multipart/form-data; boundary=---1234").entries.toList(),
                            bytes,
                            postText.length).toReq()

        assertEquals("POST",
                     req.method)

        val filePart: Part? = req.getPart("file")
        assertNotNull(filePart)
        assertEquals("file", filePart!!.name)
        assertEquals("foo.tab", filePart.submittedFileName)
        assertEquals("multipart/form-data", filePart.contentType)
        assertEquals(fileContent.length.toLong(), filePart.size)
        assertEquals(setOf("content-disposition", "content-type"), filePart.headerNames)
        assertEquals("form-data; name=\"file\"; filename=\"foo.tab\"", filePart.getHeader("content-disposition"))
        assertEquals(listOf("form-data; name=\"file\"; filename=\"foo.tab\""), filePart.getHeaders("content-disposition"))
        assertInstanceOf(List::class.java, filePart.getHeaders("content-disposition"))
        assertEquals("multipart/form-data", filePart.getHeader("content-type"))
        assertEquals(listOf("multipart/form-data"), filePart.getHeaders("content-type"))
        assertEquals(fileContent, InputStreamReader(filePart.inputStream).readText())

        val parts: Collection<Part?>? = req.parts
        assertNotNull(parts)
        assertEquals(4, parts!!.size)
        assertInstanceOf(List::class.java, parts)

        @Suppress("UNCHECKED_CAST")
        val partList: List<Part> = parts as List<Part>
        assertEquals("file", partList[0].name)
        assertEquals("foo.tab", partList[0].submittedFileName)
        assertEquals("multipart/form-data", partList[0].contentType)
        assertEquals(fileContent.length.toLong(), partList[0].size)

        assertEquals("field", partList[1].name)
        assertEquals("fieldValue".length.toLong(), partList[1].size)
        assertEquals("fieldValue", InputStreamReader(partList[1].inputStream).readText())

        assertEquals("multi", partList[2].name)
        assertEquals("value1".length.toLong(), partList[2].size)
        assertEquals("value1", InputStreamReader(partList[2].inputStream).readText())

        assertEquals("multi", partList[3].name)
        assertEquals("value2".length.toLong(), partList[3].size)
        assertEquals("value2", InputStreamReader(partList[3].inputStream).readText())
    }
}