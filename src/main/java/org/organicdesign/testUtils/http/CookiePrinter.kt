package org.organicdesign.testUtils.http

import jakarta.servlet.http.Cookie
import org.organicdesign.indented.IndentedStringable
import org.organicdesign.indented.StringUtils.fieldsOnOneLineK
import org.organicdesign.indented.nullWhen

class CookiePrinter(private val cookie: Cookie): IndentedStringable {

    override fun indentedStr(indent: Int, singleLine: Boolean): String =
        fieldsOnOneLineK(indent, "Cookie",
                         listOf(
                             "" to cookie.name,
                             "" to cookie.value,
                             "domain" to cookie.domain,
                             "maxAge" to nullWhen(cookie.maxAge, -1),
                             "path" to cookie.path,
                             "secure" to cookie.secure,
                             "version" to nullWhen(cookie.version, 0),
                             "httpOnly" to cookie.isHttpOnly
                         ).filter { it.second != null &&
                                    it.second != false })

    override fun toString(): String = indentedStr(0)
}