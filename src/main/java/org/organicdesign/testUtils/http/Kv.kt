package org.organicdesign.testUtils.http

import org.organicdesign.indented.IndentedStringable
import org.organicdesign.indented.StringUtils

/**
 * A Key-value pair.  Use this to briefly pass the headers.
 */
class Kv(override val key: String,
         override val value: String) : Map.Entry<String, String>, IndentedStringable {

    override fun indentedStr(indent: Int): String =
            "Kv(${StringUtils.stringify(key)}, ${StringUtils.stringify(value)})"

    override fun toString(): String = indentedStr(0)

    override fun equals(other: Any?): Boolean =
            if (this === other) {
                true
            } else {
                other is Map.Entry<*,*> &&
                key == other.key &&
                value == other.value
            }

    // This is specified in java.util.Map as part of the map contract
    override fun hashCode(): Int = key.hashCode() xor value.hashCode()
}