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
package org.organicdesign.testUtils;

/**
 * Do Not Use Outside This Project.
 * It tests for some things that Java's type system lets you do.
 * Kotlin's type system doesn't allow this test because it would be nonsensical in Kotlin.
 * We have to keep this in Java (for now) to test for bugs in non-Kotlin code.
 */
enum BreakNullSafety {
    INSTANCE;
    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    void compareToNull(Comparable<?> comp) {
        comp.compareTo(null);
    }
}
