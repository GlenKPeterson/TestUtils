// Copyright 2015-07-03 PlanBase Inc. & Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.organicdesign.testUtils

import org.junit.Assert

/**
 * Tests Reflexive, Symmetric, Transitive, Consistent, and non-nullity properties of the equals()
 * contract.  If you think this is confusing, realize that there is no way to implement a
 * one-sided equals() correctly with inheritance - it's a broken concept, but it's still used so
 * often that you have to do your best with it.
 *
 * I got the idea of contract-based testing from watching Bill Venners:
 * https://www.youtube.com/watch?v=bCTZQi2dpl8
 */
object EqualsContract {
    /**
     * Apply the given function against all unique pairings of items in the list.  Does this belong on Function2 instead
     * of List?
     */
    @JvmStatic
    fun <T> permutations(
            items: List<T>,
            f: (T, T) -> Any?) {
        for (i in items.indices) {
            for (j in i + 1 until items.size) {
                f.invoke(items[i], items[j])
            }
        }
    }

    /**
     * Tests Reflexive, Symmetric, Transitive, Consistent, and non-nullity properties of the equals()
     * contract.  See note in class documentation.
     *
     * @param equiv1 First equivalent (but unique) object
     * @param equiv2 Second equivalent (but unique) object (could be a different class)
     * @param equiv3 Third equivalent (but unique) object (could be a different class)
     * @param different Non-equivalent object with a (maybe) different hashCode (should be an otherwise compatible class)
     * @param requireDistinctHashes if true, require that the fourth object have a different hashCode.  Otherwise,
     * require that it have the same hashCode.
     * @param <S> The super-class of all these objects - an interface or super-class within which they should be equal.
    </S> */
    @JvmStatic
    fun equalsHashCode(
            equiv1: Any,
            equiv2: Any,
            equiv3: Any,
            different: Any,
            requireDistinctHashes: Boolean
    ) {
        require(!(equiv1 === equiv2 ||
                  equiv1 === equiv3 ||
                  equiv1 === different ||
                  equiv2 === equiv3 ||
                  equiv2 === different ||
                  equiv3 === different)) { "You must provide four different (having different memory locations) but 3 equivalent objects" }
        val equivs: List<Any> = listOf(equiv1, equiv2, equiv3)
        Assert.assertFalse("The different param should not allow itself to equal null", different as Any? == null)
        Assert.assertEquals("The different param must have the same hashCode as itself",
                            different.hashCode().toLong(), different.hashCode().toLong())
        Assert.assertTrue("The different param must equal itself", different == different)
        var i = 0
        // Reflexive
        for (equiv in equivs) {
            i++
            Assert.assertEquals("Param $i must have the same hashCode as itself",
                                equiv.hashCode().toLong(), equiv.hashCode().toLong())
            if (requireDistinctHashes) {
                Assert.assertNotEquals("The hashCode of param " + i + " must not equal the" +
                                       " hashCode of the different param.  If you meant to do that, use equalsSameHashCode()" +
                                       " instead.",
                                       equiv.hashCode().toLong(), different.hashCode().toLong())
            } else {
                Assert.assertEquals("The hashCode of param " + i + " must equal the" +
                                    " hashCode of the different param  If you meant to do that, use equalsDistinctHashCode()" +
                                    " instead.",
                                    equiv.hashCode().toLong(), different.hashCode().toLong())
            }
            Assert.assertTrue("Param $i must be equal to itself", equiv == equiv)
            Assert.assertFalse("Param $i cannot be equal to the different param", equiv == different)
            Assert.assertFalse("The different param cannot be equal to param $i", different == equiv)

            // Check null
            Assert.assertFalse("Param $i cannot allow itself to equal null", equiv as Any? == null)
        }

        // Symmetric (effectively covers Transitive as well)
        permutations(equivs) { a: Any, b: Any ->
            Assert.assertEquals("Found an unequal hashCode while inspecting permutations: a=$a b=$b",
                                a.hashCode().toLong(), b.hashCode().toLong())
            Assert.assertTrue("Failed equals while inspecting permutations: a=$a b=$b", a == b)
            Assert.assertTrue("Failed reflexive equals while inspecting permutations", b == a)
        }
    }

    /**
     * Tests Reflexive, Symmetric, Transitive, Consistent, and non-nullity properties of the equals()
     * contract.  See note in class documentation.
     *
     * @param equiv1 First equivalent (but unique) object
     * @param equiv2 Second equivalent (but unique) object (could be a different class)
     * @param equiv3 Third equivalent (but unique) object (could be a different class)
     * @param different Non-equivalent object with the same hashCode as the previous three
     * @param <S> The super-class of all these objects - an interface or super-class within which they should be equal.
    </S> */
    @JvmStatic
    fun equalsSameHashCode(
            equiv1: Any,
            equiv2: Any,
            equiv3: Any,
            different: Any
    ) {
        equalsHashCode(equiv1, equiv2, equiv3, different, false)
    }

    /**
     * Tests Reflexive, Symmetric, Transitive, Consistent, and non-nullity properties of the equals()
     * contract.  See note in class documentation.
     *
     * @param equiv1 First equivalent (but unique) object
     * @param equiv2 Second equivalent (but unique) object (could be a different class)
     * @param equiv3 Third equivalent (but unique) object (could be a different class)
     * @param different Non-equivalent object with a different hashCode (should be an otherwise compatible class)
     * @param <S> The super-class of all these objects - an interface or super-class within which they should be equal.
    </S> */
    @JvmStatic
    fun equalsDistinctHashCode(
            equiv1: Any,
            equiv2: Any,
            equiv3: Any,
            different: Any
    ) {
        equalsHashCode(equiv1, equiv2, equiv3, different, true)
    }
}