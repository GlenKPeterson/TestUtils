package org.organicdesign.testUtils

object StringDiff {

    // TODO: Make this work with actual Unicode characters instead of the weird way Strings work today.
    @JvmStatic
    fun singleShortestDiffSubstring(
            s1: String,
            s2: String
    ): Pair<String,String> {
        var firstDiffIdx = 0
        while (
                (firstDiffIdx < s1.length) &&
                (firstDiffIdx < s2.length)
        ) {
            println("firstDiffIdx: $firstDiffIdx")

            // If this char is different:
            if (s1[firstDiffIdx] != s2[firstDiffIdx]) {
                println("Different! ${s1[firstDiffIdx]} != ${s2[firstDiffIdx]}")
                var lastDiffIdxS1 = s1.length - 1
                var lastDiffIdxS2 = s2.length - 1
                // Find similar part at end of string
                while (
                        (lastDiffIdxS1 >= firstDiffIdx) &&
                        (lastDiffIdxS2 >= firstDiffIdx)
                ) {

                    println("lastDiffIdxS1 = $lastDiffIdxS1 lastDiffIdxS2 = $lastDiffIdxS2")
                    println("Reverse diff: ${s1[lastDiffIdxS1]} vs. ${s2[lastDiffIdxS2]}")

                    // Found similar part at end of string - return different middle
                    if (s1[lastDiffIdxS1] != s2[lastDiffIdxS2]) {
                        println("Only middle was different")
                        return s1.substring(firstDiffIdx, lastDiffIdxS1 + 1) to
                                s2.substring(firstDiffIdx, lastDiffIdxS2 + 1)
                    }

                    lastDiffIdxS1 -= 1
                    lastDiffIdxS2 -= 1
                }

                // Whole end of string was similar
                println("Whole end of string was similar")
                return  s1.substring(firstDiffIdx, lastDiffIdxS1 + 1) to s2.substring(firstDiffIdx, lastDiffIdxS2 + 1)
            }
            firstDiffIdx += 1
        }
        return if (s1.length == s2.length) {
            println("no diff")
            "" to ""
        } else {
            println("returning")
            return s1.substring(firstDiffIdx, s1.length) to s2.substring(firstDiffIdx, s2.length)
        }
    }

}