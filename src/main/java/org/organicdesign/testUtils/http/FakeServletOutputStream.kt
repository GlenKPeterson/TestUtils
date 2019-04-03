package org.organicdesign.testUtils.http

import org.organicdesign.indented.IndentedStringable
import org.organicdesign.indented.StringUtils.stringify
import java.lang.StringBuilder
import javax.servlet.ServletOutputStream
import javax.servlet.WriteListener

class FakeServletOutputStream: ServletOutputStream(), IndentedStringable {
    override fun indentedStr(indent: Int): String =
            "FakeServletOutputStream(${stringify(stringBuilder.toString())})"

    override fun toString(): String = indentedStr(0)

    val stringBuilder = StringBuilder()

    override fun isReady(): Boolean = true

    override fun setWriteListener(writeListener: WriteListener?) {
        throw UnsupportedOperationException("Not Implemented")
    }

    override fun write(b: Int) {
        stringBuilder.append(b.toChar())
    }
}