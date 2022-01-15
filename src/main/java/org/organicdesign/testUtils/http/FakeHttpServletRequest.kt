package org.organicdesign.testUtils.http

import jakarta.servlet.*
import jakarta.servlet.http.*
import org.eclipse.jetty.http.HttpField
import org.eclipse.jetty.http.MimeTypes
import org.eclipse.jetty.util.IO
import org.eclipse.jetty.util.MultiMap
import org.organicdesign.indented.IndentedStringable
import org.organicdesign.indented.StringUtils.classFieldsK
import java.io.*
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.security.Principal
import java.util.*

/**
 * This mocks an HttpServletRequest - EXPERIMENTAL
 */
class FakeHttpServletRequest
internal constructor(
        reqB: ReqB
) : HttpServletRequest, IndentedStringable {
    override fun indentedStr(indent: Int, singleLine: Boolean): String =
        classFieldsK(
            indent, "FakeHttpServletRequest",
            listOf("url" to requestURL.toString(),
                   "remoteAddr" to remoteAddr,
                   "method" to method,
                   "encoding" to characterEncoding,
                   "locale" to locale,
                   "requestedSessionId" to requestedSessionId,
                   "inputStream" to inStream,
                   "attributes" to attributes,
                   "cookies" to cookies.map{ CookiePrinter(it) },
                   "params" to params,
                   "headers" to heads
            ).filter { it.second != null },
            singleLine)

    override fun toString(): String = indentedStr(0)

    private val baseUrl: String = reqB.baseUrl
    // TODO: What about nulls?  Also, Needs to return a Protocol, server name, port num, and server path (but no query string)
    override fun getRequestURL(): StringBuffer {
        val sB = StringBuffer(baseUrl)
        if (uri != null) {
            sB.append(uri)
        }
        return sB
    }

    private val uri: String? = reqB.uri
    // 2018-03-02: Tomcat 8 can return null here.  Jetty does not.
    override fun getPathInfo(): String? = uri
    override fun getRequestURI(): String? = uri
    // TODO: Not sure if this always starts with a slash or not, but maybe can't be null?
    override fun getServletPath(): String? = uri

    // Looks like an IP address
    // My code elsewhere assumes that this can never be null, so I'm going with that, at least for Jetty.
    private val remoteAddr = reqB.remoteAddr
    override fun getRemoteAddr(): String = remoteAddr

    private val method: String = reqB.method
    override fun getMethod(): String = method

    private var characterEncoding: String? = reqB.characterEncoding
    override fun getCharacterEncoding(): String? = characterEncoding
    override fun setCharacterEncoding(s: String?) { characterEncoding = s }

    private val locale: Locale? = reqB.locale
    // TODO: Can this be null?
    override fun getLocale(): Locale? = locale

    // TODO: locales that are acceptable to the client based on the Accept-Language header.
    override fun getLocales(): Enumeration<Locale?> =
            enumeration(listOf(locale))

    private val requestedSessionId: String? = reqB.requestedSessionId
    override fun getRequestedSessionId(): String? = requestedSessionId

    private val inStream = reqB.inStream
    private val inStreamSize = reqB.inStreamSize
    override fun getInputStream(): ServletInputStream? =
            if (inStream == null) {
                null
            } else {
                FakeServletInputStream(inStream)
            }

    override fun getContentLength(): Int =
            if (inStreamSize > Int.MAX_VALUE) {
                -1
            } else {
                inStreamSize.toInt()
            }

    override fun getContentLengthLong(): Long = inStreamSize

    private var _multiParts: MultiPartFormInputStream? = null
    private var _contentParameters: MultiMap<String>? = null

    private val attributes: MutableMap<String, Any> = reqB.attributes
    override fun getAttribute(s: String): Any? = attributes[s]
    override fun getAttributeNames(): Enumeration<String> = enumeration(attributes.keys)
    override fun setAttribute(s: String, o: Any) { attributes[s] = o }
    override fun removeAttribute(s: String) { attributes.remove(s) }

    private val params: Map<String, List<String?>> = reqB.params.toMap()
    override fun getParameterNames(): Enumeration<String> = enumeration(params.keys)
    override fun getParameterValues(s: String): Array<String?>? = params[s]?.toTypedArray()
    override fun getParameter(s: String): String? = getParameterValues(s)?.get(0)
    override fun getParameterMap(): Map<String, Array<String?>> {
        val ret: MutableMap<String, Array<String?>> = mutableMapOf()
        for (entry in params.entries) {
            ret[entry.key] = entry.value.toTypedArray()
        }
        return ret
    }

    override fun getQueryString(): String {
        val sB = StringBuilder()
        for ((key, value) in params) {
            for (v in value) {
                sB.append(if (sB.isNotEmpty()) "&" else "")
                        .append(key).append("=")
                        .append(v)
            }
        }
        return sB.toString()
    }

    // HTTP headers are case-insensitive.
    // https://stackoverflow.com/questions/8236945/case-insensitive-string-as-hashmap-key
    // For case insensitive hack:
    // https://stackoverflow.com/questions/8236945/case-insensitive-string-as-hashmap-key
    // Actual implementation in Jetty uses an *array*.
    private val heads: Array<Map.Entry<String, String>> = when {
        reqB.headers.any{ HTTP_HEAD_HOST.equals(it.key, ignoreCase = true) } -> reqB.headers.toTypedArray()
        else -> {
            val hs = reqB.headers.toMutableList()
            hs.add(Kv(HTTP_HEAD_HOST, reqB.baseUrl.substringAfter("//")))
            hs.toTypedArray()
        }
    }

    override fun getHeader(p0: String?): String? {
        if (p0 != null) {
            for (head in heads) {
                if (p0.equals(head.key, ignoreCase = true)) {
                    return head.value
                }
            }
        }
        return null
    }

    override fun getHeaders(s: String?): Enumeration<String>? {
        val header = getHeader(s)
        return if (header == null) null else enumeration(listOf(header))
    }

    override fun getHeaderNames(): Enumeration<String> {
        return enumeration(heads.map { it.key }.toList())
    }

    override fun getIntHeader(s: String): Int =
            try {
                Integer.parseInt(getHeader(s))
            } catch (_: Exception) {
                -1
            }

    override fun getDateHeader(p0: String?): Long {
        return getHeader(p0)?.toLong() ?: -1
    }

    override fun getContentType(): String? = getHeader("Content-Type")

    private val cookies: MutableList<Cookie> = reqB.cookies
    override fun getCookies(): Array<Cookie>? =
            when {
                cookies.isEmpty() -> null
                else              -> cookies.toTypedArray()
            }

    override fun getAuthType(): String {
        throw UnsupportedOperationException("Not Implemented")
    }

    override fun getPathTranslated(): String? {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getContextPath(): String {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getRemoteUser(): String {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun isUserInRole(s: String): Boolean {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getUserPrincipal(): Principal {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getSession(b: Boolean): HttpSession {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getSession(): HttpSession {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun changeSessionId(): String {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun isRequestedSessionIdValid(): Boolean {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun isRequestedSessionIdFromCookie(): Boolean {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun isRequestedSessionIdFromURL(): Boolean {
        throw UnsupportedOperationException("Not implemented")
    }

    @Deprecated("")
    override fun isRequestedSessionIdFromUrl(): Boolean {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun authenticate(httpServletResponse: HttpServletResponse): Boolean {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun login(s: String, s1: String) {
        throw UnsupportedOperationException("Not implemented")
    }
    override fun logout() {
        throw UnsupportedOperationException("Not implemented")
    }

    @Throws(IOException::class, ServletException::class)
    override fun getPart(name: String?): Part? {
        this.parts
        return _multiParts!!.getPart(name)
    }

    @Throws(IOException::class, ServletException::class)
    override fun getParts(): Collection<Part?>? {
        val contentType = contentType
        if (contentType == null || !MimeTypes.Type.MULTIPART_FORM_DATA.`is`(
                HttpField.valueParameters(
                    contentType,
                    null
                )
            )
        ) throw ServletException(
            "Unsupported Content-Type [$contentType], expected [multipart/form-data]"
        )
        return getParts(null)
    }

    // Copied from Jetty server.Request
    @Throws(IOException::class)
    private fun getParts(params: MultiMap<String>?): Collection<Part?>?
    {
        if (_multiParts == null) {
            val tmpDir = Files.createTempDirectory("testUtilTestTemp").toFile()
            val config = MultipartConfigElement(tmpDir.absolutePath, 100000, 100000, 0)
            _multiParts = newMultiParts(config)
            println("_multiParts = $_multiParts")
            val parts = _multiParts!!.parts
            println("parts=$parts")
            var formCharset: String? = null
            val charsetPart = _multiParts!!.getPart("_charset_")
            println("charsetPart=$charsetPart")
            charsetPart?.inputStream?.use { `is` ->
                val os = ByteArrayOutputStream()
                IO.copy(`is`, os)
                formCharset = String(os.toByteArray(), StandardCharsets.UTF_8)
            }

            /*
            Select Charset to use for this part. (NOTE: charset behavior is for the part value only and not the part header/field names)
                1. Use the part specific charset as provided in that part's Content-Type header; else
                2. Use the overall default charset. Determined by:
                    a. if part name _charset_ exists, use that part's value.
                    b. if the request.getCharacterEncoding() returns a value, use that.
                        (note, this can be either from the charset field on the request Content-Type
                        header, or from a manual call to request.setCharacterEncoding())
                    c. use utf-8.
             */
            val defaultCharset: Charset = if (formCharset != null) Charset.forName(formCharset)
            else if (getCharacterEncoding() != null) Charset.forName(getCharacterEncoding())
            else StandardCharsets.UTF_8

            var os: ByteArrayOutputStream? = null
            for (p in parts) {
                println("Part=$p")
                if (p.submittedFileName == null) {
                    // Servlet Spec 3.0 pg 23, parts without filename must be put into params.
                    var charset: String? = null
                    if (p.contentType != null) charset = MimeTypes.getCharsetFromContentType(p.contentType)
                    p.inputStream.use { `is` ->
                        if (os == null) os = ByteArrayOutputStream()
                        IO.copy(`is`, os)
                        val content =
                            String(os!!.toByteArray(), if (charset == null) defaultCharset else Charset.forName(charset))
                        if (_contentParameters == null) _contentParameters =
                            params ?: MultiMap()
                        _contentParameters!!.add(p.name, content)
                    }
                    os!!.reset()
                }
            }
        }

        return _multiParts!!.parts
    }

    // Copied from Jetty server.Request
    @Throws(IOException::class)
    private fun newMultiParts(config: MultipartConfigElement?): MultiPartFormInputStream {
        return MultiPartFormInputStream(
            inputStream, contentType, config,
            null
        )
    }

    override fun <T : HttpUpgradeHandler> upgrade(aClass: Class<T>): T {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getProtocol(): String? {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getScheme(): String = baseUrl.substringBefore("://")

    override fun getServerName(): String = getHeader(HTTP_HEAD_HOST)!!.substringBefore(":")

    override fun getServerPort(): Int {
        val host: String = getHeader(HTTP_HEAD_HOST)!!
        return when {
            host.contains(":") -> host.substringAfter(":").toInt()
            else               -> schemePorts.getOrDefault(scheme, 0)
        }
    }

    override fun getReader(): BufferedReader {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getRemoteHost(): String {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun isSecure(): Boolean {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getRequestDispatcher(s: String): RequestDispatcher {
        throw UnsupportedOperationException("Not implemented")
    }

    @Deprecated("")
    override fun getRealPath(s: String): String {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getRemotePort(): Int {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getLocalName(): String {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getLocalAddr(): String {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getLocalPort(): Int {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getServletContext(): ServletContext {
        throw UnsupportedOperationException("Not implemented")
    }

    @Throws(IllegalStateException::class)
    override fun startAsync(): AsyncContext {
        throw UnsupportedOperationException("Not implemented")
    }

    @Throws(IllegalStateException::class)
    override fun startAsync(servletRequest: ServletRequest, servletResponse: ServletResponse): AsyncContext {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun isAsyncStarted(): Boolean {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun isAsyncSupported(): Boolean {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getAsyncContext(): AsyncContext {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getDispatcherType(): DispatcherType {
        throw UnsupportedOperationException("Not implemented")
    }

    companion object {

        const val HTTP_HEAD_HOST = "Host"

        private val schemePorts: Map<String,Int> = mapOf("https" to 443,
                                                         "http" to 80,
                                                         "ftp" to 21)

        fun <E> enumeration(iterable: Iterable<E>): Enumeration<E> {
            return object : Enumeration<E> {
                var iter = iterable.iterator()
                override fun hasMoreElements(): Boolean {
                    return iter.hasNext()
                }

                override fun nextElement(): E {
                    return iter.next()
                }
            }
        }

        private class FakeServletInputStream(
                private val inStream: InputStream
        ) : ServletInputStream() {

            private var eosReached = false

            override fun isReady(): Boolean = !eosReached

            override fun isFinished(): Boolean = eosReached

            override fun setReadListener(p0: ReadListener?) {
                throw UnsupportedOperationException("Not Implemented")
            }

            @Throws(IOException::class)
            override fun read(): Int {
                val ret = inStream.read()
                if (ret == -1) {
                    eosReached = true
                }
                return ret
            }

            @Throws(IOException::class)
            override fun read(b: ByteArray, off: Int, len: Int): Int {
                val ret = inStream.read(b, off, len)
                if (ret == -1) {
                    eosReached = true
                }
                return ret
            }

        }
    }
}
