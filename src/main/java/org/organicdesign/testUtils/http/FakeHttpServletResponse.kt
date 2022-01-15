package org.organicdesign.testUtils.http

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.organicdesign.indented.IndentedStringable
import org.organicdesign.indented.StringUtils.classFieldsK
import java.io.PrintWriter
import java.util.*

/**
 * This mocks an HttpServletResponse.  It is a very raw and early version.
 */
class FakeHttpServletResponse : HttpServletResponse, IndentedStringable {

    override fun indentedStr(indent: Int, singleLine: Boolean): String =
        classFieldsK(indent, "FakeHttpServletResponse",
                     listOf("status" to status,
                            "committed" to committed,
                            "redirect" to redirect,
                            "contentType" to contentType,
                            "encoding" to encoding,
                            "locale" to locale,
                            "cookies" to cookies.map{ CookiePrinter(it) },
                            "headers" to headers,
                            "outputStream" to outputStream
                     ).filter { it.second != null },
                     singleLine)

    override fun toString(): String = indentedStr(0)

    private var status = 0
    override fun setStatus(i: Int) { status = i }
    @Deprecated("")
    override fun setStatus(i: Int, s: String) { status = i }
    override fun getStatus(): Int = status

    var redirect: String? = null

    private var locale: Locale? = null
    override fun getLocale(): Locale? = locale
    override fun setLocale(locale: Locale) { this.locale = locale }

    private var contentType: String? = null
    override fun getContentType(): String? = contentType
    override fun setContentType(s: String) {
        contentType = s
        val charsetIdx = s.indexOf("charset=")
        if (charsetIdx > -1) {
            encoding = s.substringAfter("charset=").trim()
        }
    }

    private var committed = false
    override fun isCommitted(): Boolean = committed

    private var encoding: String? = null
    override fun getCharacterEncoding(): String? = encoding
    override fun setCharacterEncoding(s: String?) { encoding = s }

    val cookies: MutableList<Cookie> = mutableListOf()
    override fun addCookie(cookie: Cookie) { cookies.add(cookie) }

    private val outputStream = FakeServletOutputStream()
    override fun getOutputStream(): FakeServletOutputStream = outputStream
    override fun getWriter(): PrintWriter = PrintWriter(outputStream.stringWriter)
    private var bufferSize: Int = DEFAULT_BUFFER_SIZE
    override fun getBufferSize(): Int = bufferSize
    override fun setBufferSize(i: Int) { bufferSize = i }
    override fun flushBuffer() { }
    override fun resetBuffer() { }
    override fun reset() { }

    private val headers: MutableList<Map.Entry<String, String>> = mutableListOf()

    override fun getHeaderNames(): Collection<String> = headers.map{ it.key }.toSet()

    override fun getHeader(s: String): String? {
        val iter = getHeaders(s).iterator()
        return if (iter.hasNext())
            iter.next()
        else
            null
    }

    override fun getHeaders(s: String?): Collection<String> {
        val values: MutableList<String> = mutableListOf()
        if (s != null) {
            for (head in headers) {
                if (s.equals(head.key, ignoreCase = true)) {
                    values.add(head.value)
                }
            }
        }
        return values
    }

    override fun containsHeader(s: String?): Boolean {
        if (s != null) {
            for (head in headers) {
                if (s.equals(head.key, ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }

    override fun setHeader(s: String?, s1: String) {
        if (s != null) {
            for (i in headers.indices) {
                val head = headers[i]
                if (s.equals(head.key, ignoreCase = true)) {
                    headers[i] = Kv(s, s1)
                    return
                }
            }
            addHeader(s, s1)
        }
    }

    override fun addHeader(s: String, s1: String) {
        headers.add(Kv(s, s1))
    }

    override fun setDateHeader(s: String, l: Long) {
        setHeader(s, l.toString())
    }

    override fun addDateHeader(s: String, l: Long) {
        addHeader(s, l.toString())
    }

    override fun setIntHeader(s: String, i: Int) {
        setHeader(s, i.toString())
    }

    override fun addIntHeader(s: String, i: Int) {
        addHeader(s, i.toString())
    }

    override fun encodeRedirectURL(s: String): String? {
        redirect = encodeURL(s)
        return redirect
    }
    @Deprecated(replaceWith = ReplaceWith("encodeRedirectURL(s)"),
                message = "replaced with encodeRedirectURL(String url)")
    override fun encodeRedirectUrl(s: String): String? = encodeRedirectURL(s)

    /**
     * This is only a correct implementation if we assume that the browser supports cookies, or session
     * tracking is turned off
     */
    override fun encodeURL(s: String): String = s

    @Deprecated(replaceWith = ReplaceWith("encodeURL(s)"),
                message = "encodeURL(s)")
    override fun encodeUrl(s: String): String = encodeURL(s)

    override fun sendError(i: Int, s: String) {
        if (committed) {
            throw IllegalStateException("Response already committed.")
        }
        status = i
        contentType = "text/html"
        committed = true
        // TODO: "clear the buffer"
        // TODO: Where does the string parameter go?
    }

    override fun sendError(i: Int) = sendError(i, "")

    override fun sendRedirect(s: String) { redirect = s }

    override fun setContentLength(i: Int) {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun setContentLengthLong(l: Long) {
        throw UnsupportedOperationException("Not implemented")
    }

    companion object {
        @JvmStatic
        fun httpServletResponse(): FakeHttpServletResponse {
            return FakeHttpServletResponse()
        }
    }
}
