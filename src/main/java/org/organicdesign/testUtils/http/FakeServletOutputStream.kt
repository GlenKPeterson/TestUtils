package org.organicdesign.testUtils.http

import jakarta.servlet.ServletOutputStream
import jakarta.servlet.WriteListener
import org.organicdesign.indented.IndentedStringable
import org.organicdesign.indented.StringUtils.stringify
import java.io.StringWriter

class FakeServletOutputStream: ServletOutputStream(), IndentedStringable {
    override fun indentedStr(indent: Int, singleLine: Boolean): String =
            "FakeServletOutputStream(${stringify(stringWriter.toString())})"

    override fun toString(): String = indentedStr(0)

    val stringWriter = StringWriter()

    override fun isReady(): Boolean = true

    override fun setWriteListener(writeListener: WriteListener?) {
        throw UnsupportedOperationException("Not Implemented")
    }

    override fun write(b: Int) {
        stringWriter.append(b.toChar())
    }
}