# TestUtils
Utilities for testing common Java contracts.  Currently equals(), hashCode(), and compareTo().
I find a bug almost every time I apply these tests to some old piece of code.  Usage is defined in the Javadocs.

The idea of contract-based testing was from watching Bill Venners:
https://www.youtube.com/watch?v=bCTZQi2dpl8
Any bugs are my own.

## Maven Dependency
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.organicdesign/TestUtils/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.organicdesign/TestUtils)
[![javadoc](https://javadoc.io/badge2/org.organicdesign/TestUtils/javadoc.svg)](https://javadoc.io/doc/org.organicdesign/TestUtils)

Note that this project is just for testing, so add it only to the `test` scope of your project:
```xml
<dependency>
	<groupId>org.organicdesign</groupId>
	<artifactId>TestUtils</artifactId>
	<version>0.0.18</version>
	<scope>test</scope>
</dependency>
```

## Usage
```java
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;
import static org.organicdesign.testUtils.EqualsContract.equalsSameHashCode;

public class PaddingTest {
    @Test public void equalHashTest() {
        // Test first item different
        equalsDistinctHashCode(Padding.of(1), Padding.of(1,1,1,1), Padding.of(1),
                               Padding.of(2,1,1,1));

        // Test transposed middle items are different (but have same hashcode)
        equalsSameHashCode(Padding.of(3, 5, 7, 1.1f), Padding.of(3, 5, 7, 1.1f),
                           Padding.of(3, 5, 7, 1.1f),
                           Padding.of(3, 7, 5, 1.1f));

        // Padding values that differ by less than 0.1f have the same hashcode
        // but are not equal.  Prove it (also tests last item is different):
        equalsSameHashCode(Padding.of(1), Padding.of(1, 1, 1, 1), Padding.of(1),
                           Padding.of(1, 1, 1, 1.0001f));
    }
}
```

The above is a suitable test for the class [com.planbase.pdf.layoutmanager.Padding](https://github.com/GlenKPeterson/PdfLayoutManager/blob/master/src/main/java/com/planbase/pdf/layoutmanager/Padding.java)

* All four arguments must be distinct objects (not pointers to the same object in memory)
* The first three arguments must equal each other (and therefore must have the same hashCode), but must not equal the fourth argument.
* When possible/practical, use a fourth object with a different hashCode
* When practical, it's a good idea to also find and test an unequal fourth object with the same hashCode
* Think about the most different ways you can construct objects for the first three arguments.  The above example is a little weak in that regard because there just aren't many legal ways to construct Padding (good for Padding!).

## Contributions
To build locally (in an appropriate folder), you need Java 8, maven, and git installed.  Then:
```bash
git clone https://github.com/GlenKPeterson/TestUtils.git
mvn clean install
```

## Change Log

## 0.0.20 2020-10-07 "CompareToContract Signature"
 - This fixes a longstanding bug (and/or maybe a new Kotlin incompatibility)
 in the generic type signature of `CompareToContract.testCompareTo()`.
 It hasn't caused a problem (yet) in Java, but Kotlin 1.4.10 likes
 the new version *much* better.

### 0.0.19 2020-10-06
 - Bumped dependency versions

### 0.0.18 2020-08-20
 - Updated to Kotlin 1.4.0 and Dokka 1.4.0-rc which gives us real javadoc now.
 - Changed repository name to remove redundant '.testUtils'

**Note:** Version 0.0.17 lacked Javadoc

#### 0.0.16 2020-05-18
 - Allow any RuntimeException to be thrown by `comparator.compare(item, null)`.
   Previously we had allowed NullPointerException and IllegalArgumentException.
   Kotlin throws IllegalStateException.  Really any kind of RuntimeException
   is adequate.
 - Upgraded Kotlin

**Note:** Version 0.0.15 was a fluke - failed strangely in the Sonatype release process, so just re-released as .16.

#### 0.0.14 2020-04-02
 - Updated all dependencies.
 - Upgraded Gradle to 6.3 and switched to .kts gradle file format.

#### 0.0.13 2019-05-24
Updated all dependencies.

#### 0.0.12 2019-04-29
Implemented FakeHttpServletResponse.bufferSize

0.0.11 2019-04-05
Added CookiePrinter class for showing HTTP cookies.

0.0.10
 - Moved Kv out to top-level class (was inside FakeHttpServletRequest)
 - Added .indentedStr() and .toString() implementations
 - Upgraded Indented dependency.
 - Reorganized FakeHttp... classes to put accessor methods next to the fields they are related to.
 This also puts unimplemented methods at the bottom.
 - Test coverage at 85% by line

0.0.9
 - Implemented more methods in FakeHttpServletResponse

0.0.8
 - Renamed TestHttpServletRequest/Response to FakeHttpServlet... and put them in an http sub-package.
 - Added ReqB as a FakeHttpServletRequestBuilder.
 - Made FakeHttpServletRequest.getRemoteAddr() not null and used "0:0:0:0:0:0:0:1" (IP-V8 localhost) as the default value.
 Implemented .locale.  Might have implemented or fixed implementations of other methods.
 - Added tests for CompareToContract and Serialization
 - Updated dependencies.

0.0.7 Added HttpServletRequest mock

0.0.6 Added ComparatorContract test and Serialization helper function.

0.0.5 Allow compareTo(null) to throw *either* a NullPointerException or an IllegalArgumentException

0.0.4 Fixed variance on CompareToContract.testCompareTo()

0.0.3-SNAPSHOT Added/updated JavaDocs.

## License
Apache 2.0 Copyright 2015 Glen Peterson and PlanBase Inc.
