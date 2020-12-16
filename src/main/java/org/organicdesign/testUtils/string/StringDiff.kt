package org.organicdesign.testUtils.string

object StringDiff {

    // TODO: Make this work with actual Unicode characters instead of the weird way Strings work today.
    /**
     * Takes two strings and returns the different middle section which could be
     *  - Two empty strings (no difference)
     *  - an empty string and a non-empty string (a deletion or addition)
     *  - Two strings (the changed part).
     *
     *  This works by finding exact matches from the beginning and the end of the string, then returning
     *  everything inbetween.
     */
    @JvmStatic
    fun differentMiddle(
            s1: String,
            s2: String
    ): DiffResult {
        var firstDiffIdx = 0
        val s1Len = s1.length
        val s2Len = s2.length
        while (
                (firstDiffIdx < s1Len) &&
                (firstDiffIdx < s2Len)
        ) {
//            println("firstDiffIdx: $firstDiffIdx")

            // If this char is different:
            if (s1[firstDiffIdx] != s2[firstDiffIdx]) {
//                println("Different! ${s1[firstDiffIdx]} != ${s2[firstDiffIdx]}")
                var lastDiffIdxS1 = s1Len - 1
                var lastDiffIdxS2 = s2Len - 1
                // Find similar part at end of string
                while (
                        (lastDiffIdxS1 >= firstDiffIdx) &&
                        (lastDiffIdxS2 >= firstDiffIdx)
                ) {

//                    println("lastDiffIdxS1 = $lastDiffIdxS1 lastDiffIdxS2 = $lastDiffIdxS2")
//                    println("Reverse diff: ${s1[lastDiffIdxS1]} vs. ${s2[lastDiffIdxS2]}")

                    // Found similar part at end of string - return different middle
                    if (s1[lastDiffIdxS1] != s2[lastDiffIdxS2]) {
//                        println("Only middle was different")
                        return DiffResult(s1.substring(firstDiffIdx, lastDiffIdxS1 + 1),
                                          s2.substring(firstDiffIdx, lastDiffIdxS2 + 1))
                    }

                    lastDiffIdxS1 -= 1
                    lastDiffIdxS2 -= 1
                }

                // Whole end of string was similar
//                println("Whole end of string was similar")
                return  DiffResult(s1.substring(firstDiffIdx, lastDiffIdxS1 + 1),
                                   s2.substring(firstDiffIdx, lastDiffIdxS2 + 1))
            }
            firstDiffIdx += 1
        }
        return if (s1Len == s2Len) {
//            println("no diff")
            DiffResult.IDENTICAL
        } else {
//            println("returning")
            return DiffResult(s1.substring(firstDiffIdx),
                              s2.substring(firstDiffIdx))
        }
    }

}