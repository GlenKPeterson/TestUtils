package org.organicdesign.testUtils.string

import org.organicdesign.indented.IndentedStringable
import org.organicdesign.indented.StringUtils.fieldsOnOneLineK

data class DiffResult(
    val first: String,
    val second: String
) : IndentedStringable {

    override fun indentedStr(indent: Int, singleLine: Boolean): String =
        if ( first == "" &&
            second == "" ) {
            "IDENTICAL"
        } else {
            fieldsOnOneLineK(indent, "DiffResult",
                             listOf("" to first,
                                    "" to second))
        }

    override fun toString(): String = indentedStr(0)

    companion object {
        @JvmField
        val IDENTICAL = DiffResult("", "")
    }
}