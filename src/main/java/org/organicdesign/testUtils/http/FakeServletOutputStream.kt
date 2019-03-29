package org.organicdesign.testUtils.http

import java.lang.StringBuilder
import javax.servlet.ServletOutputStream
import javax.servlet.WriteListener

class FakeServletOutputStream(): ServletOutputStream() {

    val stringBuilder = StringBuilder()

    override fun isReady(): Boolean = true

    override fun setWriteListener(writeListener: WriteListener?) {
        throw UnsupportedOperationException("Not Implemented")
    }

    override fun write(b: Int) {
        stringBuilder.append(b.toChar())
    }
}