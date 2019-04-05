package org.organicdesign.testUtils.http

import org.organicdesign.indented.IndentedStringable
import org.organicdesign.indented.StringUtils.iterableToStr
import org.organicdesign.indented.StringUtils.spaces
import org.organicdesign.indented.StringUtils.stringify
import javax.servlet.ServletOutputStream
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import java.io.PrintWriter
import java.lang.StringBuilder
import java.util.Locale

/**
 * This mocks an HttpServletResponse.  It is a very raw and early version.
 */
class FakeHttpServletResponse : HttpServletResponse, IndentedStringable {

    override fun indentedStr(indent:Int):String {
        class CookiePrinter(val cookie: Cookie) {
            override fun toString(): String =
                    "Cookie(${stringify(cookie.name)}, ${stringify(cookie.value)})"
        }
        val sB = StringBuilder("FakeHttpServletResponse(\n")
                .append("${spaces(indent + 8)}status=$status,\n")
                .append("${spaces(indent + 8)}committed=$committed,\n")
        if (redirect != null) {
            sB.append("${spaces(indent + 8)}redirect=$redirect,\n")
        }
        if (contentType != null) {
            sB.append("${spaces(indent + 8)}contentType=$contentType,\n")
        }
        if (encoding != null) {
            sB.append("${spaces(indent + 8)}encoding=$encoding,\n")
        }
        if (locale != null) {
            sB.append("${spaces(indent + 8)}locale=$locale,\n")
        }
        sB.append("${spaces(indent + 8)}cookies=${iterableToStr(indent + 16, "listOf", cookies.map{ CookiePrinter(it) })},\n" +
                  "${spaces(indent + 8)}headers=${iterableToStr(indent + 16, "listOf", headers)},\n" +
                  "${spaces(indent + 8)}outputStream=$outputStream,\n" +
                  "${spaces(indent)})")
        return sB.toString()
    }

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
    override fun getOutputStream(): ServletOutputStream = outputStream

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

    override fun encodeURL(s: String): String {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun encodeRedirectURL(s: String): String {
        throw UnsupportedOperationException("Not implemented")
    }

    @Deprecated("")
    override fun encodeUrl(s: String): String {
        throw UnsupportedOperationException("Not implemented")
    }

    @Deprecated("")
    override fun encodeRedirectUrl(s: String): String {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun sendError(i: Int, s: String) {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun sendError(i: Int) {
        if (committed) {
            throw IllegalStateException("Response already committed.")
        }
        status = i
        contentType = "text/html"
        committed = true
        // TODO: "clear the buffer"
    }

    override fun sendRedirect(s: String) { redirect = s }

    override fun getWriter(): PrintWriter = PrintWriter(outputStream.stringWriter)

    override fun setContentLength(i: Int) {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun setContentLengthLong(l: Long) {
        throw UnsupportedOperationException("Not implemented")
    }


    override fun setBufferSize(i: Int) {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun getBufferSize(): Int {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun flushBuffer() {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun resetBuffer() {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun reset() {
        throw UnsupportedOperationException("Not implemented")
    }

    companion object {
        @JvmStatic
        fun httpServletResponse(): FakeHttpServletResponse {
            return FakeHttpServletResponse()
        }
    }
}
