package org.organicdesign.testUtils.http

import org.organicdesign.testUtils.http.FakeHttpServletRequest.Companion.Kv
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import java.util.*

/**
 * This is a FakeHttpServletRequestBuilder.
 * Since it's meant to make construction easy, I shortened that to ReqB, for "Request-Builder".
 */
class ReqB {
    // HTTP headers are case-insensitive.
    internal var headers: List<Map.Entry<String, String>> = listOf()
    internal var attributes: MutableMap<String, Any> = mutableMapOf()

    // TODO: can the list itself be null?  Or just the values in the list?
    internal var params: Map<String, List<String?>> = mutableMapOf()
    internal var locale: Locale? = null

    internal var method: String? = null // GET
    internal var baseUrl: String? = null // "https://domain.com"
    internal var uri: String? = null // "/somePath/file.html"
    internal var characterEncoding: String? = null // "UTF-8"
    internal var requestedSessionId: String? = null // "2FCF6F9AA75782B8B783308DE74BC557"

    // I think this can never be null.  It's so rare that we care that I don't want to make it
    // a required constructor param.  I'll settle for the "localhost" IP-V8 address as a default.
    internal var remoteAddr: String = "0:0:0:0:0:0:0:1"

    internal var inStream: ByteArrayInputStream? = null //ByteArrayInputStream(byteArrayOf())
    internal var inStreamSize: Long = -1

    fun attributes(m: MutableMap<String, Any>): ReqB {
        attributes = m
        return this
    }

    fun headers(l: List<Map.Entry<String, String>>): ReqB {
        headers = l
        return this
    }

    fun params(m: Map<String, List<String?>>): ReqB {
        params = m
        return this
    }

    fun method(s: String): ReqB {
        method = s
        return this
    }

    fun baseUrl(s: String): ReqB {
        baseUrl = s
        return this
    }

    fun uri(s: String): ReqB {
        uri = s
        return this
    }

    fun locale(l: Locale): ReqB {
        locale = l
        return this
    }

    fun characterEncoding(s: String): ReqB {
        characterEncoding = s
        return this
    }

    fun requestedSessionId(s: String): ReqB {
        requestedSessionId = s
        return this
    }

    fun remoteAddr(s: String): ReqB {
        remoteAddr = s
        return this
    }

    fun inStream(stream: ByteArrayInputStream, size: Long): ReqB {
        inStream = stream
        inStreamSize = size
        return this
    }

    fun toReq(): FakeHttpServletRequest = FakeHttpServletRequest(this)

    companion object {
        // Input under Apache license, taken from
        // https://commons.apache.org/proper/commons-fileupload/xref-test/org/apache/commons/fileupload/servlet/ServletFileUploadTest.html
        private const val text: String =
                "-----1234\r\n" +
                "Content-Disposition: form-data; name=\"utf8Html\"\r\n" +
                "\r\n" +
                "Thís ís the coñteñt of the fíle\n" +
                "\r\n" +
                "-----1234--\r\n"

        private val testStream: ByteArrayInputStream =
                text.byteInputStream(Charset.forName("UTF-8"))


        @JvmStatic
        fun funDefaults() = ReqB()
                .method("GET")
                .baseUrl("https://www.domain.com")
                .uri("/somePath/file.html")
                .headers(mutableListOf(
                        Kv("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"),
                        Kv("Accept-Encoding", "gzip,deflate,sdch"),
                        Kv("Accept-Language", "en-US,en;q=0.8"),
                        Kv("Connection", "keep-alive"),
                        Kv("Cookie", "__utma=114994118.358976500.1339520096.1375206419.1375875584.30;" +
                                " __utmz=114994118.1375206419.29.12.utmcsr=linkedin.com|" +
                                "utmccn=(referral)|" +
                                "utmcmd=referral|" +
                                "utmcct=/profile/view;" +
                                " JSESSIONID=2FCF6F9AA75782B8B783308DE74BC557"),
                        Kv("Host", "www.domain.com"),
                        Kv("Referer", "https://www.domain.com/somePath/file.html"),
                        Kv("user-agent", "Mozilla/5.0 (X11; Linux x86_64)" +
                                " AppleWebKit/537.36 (KHTML, like Gecko)" +
                                " Ubuntu" +
                                " Chromium/28.0.1500.71" +
                                " Chrome/28.0.1500.71" +
                                " Safari/537.36")))

                .params(mutableMapOf(
                        "k1" to listOf("v1"),
                        "k2" to listOf("v2a", "v2b", "v2c")))
//                "k3" to listOf(null),
//                "k4" to listOf("v4a", null, "v4c"))

                .locale(Locale.TRADITIONAL_CHINESE)

                .characterEncoding("UTF-8")
                .requestedSessionId("2FCF6F9AA75782B8B783308DE74BC557")
                .remoteAddr("0:0:0:0:0:0:0:1")
                .inStream(testStream, text.length.toLong())
    }
}