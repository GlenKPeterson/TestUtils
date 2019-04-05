package org.organicdesign.testUtils.http

import org.organicdesign.indented.IndentedStringable
import org.organicdesign.indented.StringUtils.spaces
import org.organicdesign.indented.StringUtils.stringify
import javax.servlet.http.Cookie

class CookiePrinter(private val cookie: Cookie): IndentedStringable {
    override fun indentedStr(indent: Int): String {
        var ret = "Cookie(${stringify(cookie.name)}, ${stringify(cookie.value)}"
        if (cookie.domain != null) {
            ret = "$ret,\n" +
                  "${spaces(indent + 7)}domain=${stringify(cookie.domain)}"
        }
        if (cookie.maxAge != -1) {
            ret = "$ret,\n" +
                  "${spaces(indent + 7)}maxAge=${cookie.maxAge}"
        }
        if (cookie.path != null) {
            ret = "$ret,\n" +
                  "${spaces(indent + 7)}path=${stringify(cookie.path)}"
        }
        if (cookie.secure) {
            ret = "$ret,\n" +
                  "${spaces(indent + 7)}secure"
        }
        if (cookie.version != 0) {
            ret = "$ret,\n" +
                  "${spaces(indent + 7)}version=${cookie.version}"
        }
        if (cookie.isHttpOnly) {
            ret = "$ret,\n" +
                  "${spaces(indent + 7)}httpOnly"
        }
        return "$ret)"
    }

    override fun toString(): String = indentedStr(0)
}