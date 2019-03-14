package org.organicdesign.testUtils

import java.io.BufferedReader
import java.security.Principal
import java.util.*
import javax.servlet.*
import javax.servlet.http.*

/**
 * This mocks an HttpServletRequest - EXPERIMENTAL
 */
class FakeHttpServletRequest
/**
 * Mocks a fake HTTP servlet request for testing.
 *
 * @param baseUrl http://localhost:8080
 * @param uri /Goodbye/cruel/world
 * @param hs the HTTP headers as a map of keys/values
 * Technically this maps keys to a list of values, but it's simpler to just map to a
 * single value.
 * @param params the request parameters as a map of keys and lists of values.
 */
private constructor(
        private val baseUrl: String,
        private val uri: String,
        hs: List<Map.Entry<String, String>>,
        private val params: Map<String, List<String>>
) : HttpServletRequest {

    // HTTP headers are case-insensitive.
    // https://stackoverflow.com/questions/8236945/case-insensitive-string-as-hashmap-key
    // For case insensitive hack:
    // https://stackoverflow.com/questions/8236945/case-insensitive-string-as-hashmap-key
    // Actual implementation in Jetty uses an *array*.
    private val heads: Array<Map.Entry<String, String>> = hs.toTypedArray()

    private val locale: Locale = Locale.US
    private val attributes: MutableMap<String, Any> = mutableMapOf()
    private var characterEncoding: String = "UTF-8"

    var fakeMethod: String = "GET"
    var fakeRequestedSessionId = "2FCF6F9AA75782B8B783308DE74BC557"
    var fakeRemoteAddr = "0:0:0:0:0:0:0:1"

    override fun getAuthType(): String {
        throw UnsupportedOperationException("Not Implemented")
    }

    override fun getCookies(): Array<Cookie> {
        throw UnsupportedOperationException("Not Implemented")
    }

    override fun getDateHeader(p0: String?): Long {
        throw UnsupportedOperationException("Not Implemented")
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

    override fun getIntHeader(s: String): Int {
        val v = getHeader(s)
        return if (v == null) -1 else Integer.parseInt(v)
    }

    override fun getMethod(): String = fakeMethod

    // 2018-03-02: Tomcat 8 can return null here.  Jetty does not.
    override fun getPathInfo(): String? = uri

    override fun getPathTranslated(): String { throw UnsupportedOperationException("Not implemented") }

    override fun getContextPath(): String {
        throw UnsupportedOperationException("Not implemented")
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

    override fun getRemoteUser(): String {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun isUserInRole(s: String): Boolean {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getUserPrincipal(): Principal {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getRequestedSessionId(): String = fakeRequestedSessionId

    override fun getRequestURI(): String = uri

    override fun getRequestURL(): StringBuffer = StringBuffer(baseUrl).append(uri)

    override fun getServletPath(): String = uri

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

    override fun login(s: String, s1: String) {}
    override fun logout() {}
    override fun getParts(): Collection<Part> {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getPart(s: String): Part {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun <T : HttpUpgradeHandler> upgrade(aClass: Class<T>): T {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getAttribute(s: String): Any? {
        return attributes[s]
    }

    override fun getAttributeNames(): Enumeration<String> {
        return enumeration(attributes.keys)
    }

    override fun getCharacterEncoding(): String {
        return characterEncoding
    }

    override fun setCharacterEncoding(s: String) {
        characterEncoding = s
    }

    override fun getContentLength(): Int {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getContentLengthLong(): Long {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getContentType(): String {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getInputStream(): ServletInputStream {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getParameter(s: String): String {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getParameterNames(): Enumeration<String> {
        return enumeration(params.keys)
    }

    override fun getParameterValues(s: String): Array<String>? {
        return params[s]?.toTypedArray()
    }

    override fun getParameterMap(): Map<String, Array<String>> {
        val ret = HashMap<String, Array<String>>()
        for (entry in params.entries) {
            ret[entry.key] = entry.value.toTypedArray()
        }
        return ret
    }

    override fun getProtocol(): String {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getScheme(): String {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getServerName(): String {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getServerPort(): Int {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getReader(): BufferedReader {
        throw UnsupportedOperationException("Not implemented")
    }

    // Looks like an IP address...
    override fun getRemoteAddr(): String = fakeRemoteAddr

    override fun getRemoteHost(): String {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun setAttribute(s: String, o: Any) {
        attributes[s] = o
    }

    override fun removeAttribute(s: String) {
        attributes.remove(s)
    }

    override fun getLocale(): Locale = locale

    override fun getLocales(): Enumeration<Locale> = enumeration(listOf(locale))

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

    // Use this to pass the headers.
    class HttpField(override val key: String,
                    override val value: String) : Map.Entry<String, String>

    companion object {

        // GET params come from getQueryString()
        // POST params come from

        /**
         * Mocks a fake HTTP servlet request for testing.
         *
         * @param baseUrl http://localhost:8080
         * @param uri /Goodbye/cruel/world
         * @param headers the HTTP headers as a map of keys/values
         * Technically this maps keys to a list of values, but it's simpler to just map to a
         * single value.
         * @param params the request parameters as a map of keys and lists of values.
         * @return a fake HTTP servlet request.
         */
        @JvmStatic
        fun fakeReq(
                baseUrl: String,
                uri: String,
                headers: List<Map.Entry<String, String>>,
                params: Map<String, List<String>>): FakeHttpServletRequest {
            return FakeHttpServletRequest(baseUrl, uri, headers, params)
        }

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
    }
}
