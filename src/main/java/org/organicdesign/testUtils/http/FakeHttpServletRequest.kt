package org.organicdesign.testUtils.http

import org.organicdesign.indented.IndentedStringable
import org.organicdesign.indented.StringUtils.spaces
import org.organicdesign.indented.StringUtils.stringify
import org.organicdesign.indented.StringUtils.indent
import java.io.BufferedReader
import java.security.Principal
import java.util.*
import javax.servlet.*
import javax.servlet.http.*
import java.io.IOException
import java.io.InputStream
import java.lang.Exception


/**
 * This mocks an HttpServletRequest - EXPERIMENTAL
 */
class FakeHttpServletRequest
internal constructor(
        reqB: ReqB
) : HttpServletRequest, IndentedStringable {
    override fun indentedStr(indent:Int):String {
        val sB = java.lang.StringBuilder("FakeHttpServletRequest(\n")
                .append("${spaces(indent + 8)}url=${stringify(requestURL.toString())},\n")
                .append("${spaces(indent + 8)}remoteAddr=${stringify(remoteAddr)},\n")
                .append("${spaces(indent + 8)}method=${stringify(method)},\n")
        if (characterEncoding != null) {
            sB.append("${spaces(indent + 8)}encoding=${stringify(characterEncoding)},\n")
        }
        if (locale != null) {
            sB.append("${spaces(indent + 8)}locale=$locale,\n")
        }
        if (requestedSessionId != null) {
            sB.append("${spaces(indent + 8)}requestedSessionId=${stringify(requestedSessionId)},\n")
        }

        sB.append("${spaces(indent + 8)}inputStream=$inStream,\n" +
                  "${spaces(indent + 8)}attributes=${indent(indent + 19, attributes)},\n" +
                  "${spaces(indent + 8)}cookies=${indent(indent + 16, cookies.map { CookiePrinter(it) })},\n" +
                  "${spaces(indent + 8)}params=${indent(indent + 15, params)},\n" +
                  "${spaces(indent + 8)}headers=${indent(indent + 16, heads)},\n" +
                  "${spaces(indent)})")
        return sB.toString()
    }

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

    override fun getQueryString(): String? {
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
    override fun getParts(): Collection<Part> {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getPart(s: String): Part {
        throw UnsupportedOperationException("Not implemented")
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
