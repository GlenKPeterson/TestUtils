# TestUtils
Utilities for testing common Java/Kotlin contracts.  Currently equals(), hashCode(), and compareTo().
I find a bug almost every time I apply these tests to old code.  Usage is defined in the Javadocs.

The idea of contract-based testing was from watching Bill Venners:
https://www.youtube.com/watch?v=bCTZQi2dpl8
Any bugs are my own.

This project also includes fake Http servlet requests/responses useful for end-to-end testing java servlets. 

## Maven Dependency
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.organicdesign/TestUtils/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.organicdesign/TestUtils)
[![javadoc](https://javadoc.io/badge2/org.organicdesign/TestUtils/javadoc.svg)](https://javadoc.io/doc/org.organicdesign/TestUtils)

Note that this project is just for testing, so add it only to the `test` scope of your project:
```xml
<dependency>
	<groupId>org.organicdesign</groupId>
	<artifactId>TestUtils</artifactId>
	<version>2.0.0</version>
	<scope>test</scope>
</dependency>
```

## Usage: Equality
```java
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;
import static org.organicdesign.testUtils.EqualsContract.equalsSameHashCode;

public class PaddingTest {
    @Test public void equalHashTest() {
        // Test padding-top different
        equalsDistinctHashCode(Padding.of(1),
                               Padding.of(1,1,1,1),
                               Padding.of(1),
                               Padding.of(2,1,1,1));

        // Test transposed padding-right and pdding-bottom are different (but have same hashcode)
        equalsSameHashCode(Padding.of(3, 5, 7, 1.1f),
                           Padding.of(3, 5, 7, 1.1f),
                           Padding.of(3, 5, 7, 1.1f),
                           Padding.of(3, 7, 5, 1.1f));

        // Padding values that differ by less than 0.1f have the same hashcode
        // but are not equal.  Prove it (also tests when padding-left is different):
        equalsSameHashCode(Padding.of(1),
                           Padding.of(1, 1, 1, 1),
                           Padding.of(1),
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

## Usage: FakeHttpServletRequest/Response

```kotlin
import com.goalqpc.memJogLib.servlet.MjlServletHandler.handle
import com.nhaarman.mockitokotlin2.mock
import org.eclipse.jetty.server.Request
import org.organicdesign.testUtils.http.FakeHttpServletResponse
import org.organicdesign.testUtils.http.ReqB
import javax.servlet.http.HttpServletResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ServletHandlerTest {

    @Test
    fun testFavicon() {
        // ReqB uses a fluent interface to build FakeHttpServletRequests
        val httpReq = ReqB().uri("/favicon.ico").toReq()
        // FakeHttpServletResponse generally needs no initialization
        val resp = FakeHttpServletResponse()
        // Using mokito for the request we don't need to inspect later
        val mockBaseReq: Request = mock { }

        // Send fake request and response to servlet handler
        handle("", mockBaseReq, httpReq, resp)

        // After handling a request, first check for proper response code
        assertEquals(HttpServletResponse.SC_OK, resp.status)
        // No redirect
        assertNull(resp.redirect)
        // Correct content type
        assertEquals("image/x-icon", resp.contentType)
        // Query headers
        val headers: Collection<String> = resp.getHeaders("Cache-Control")
        assertTrue(headers.contains("public"))
        assertTrue(headers.contains("max-age=28800"))

        // Great debugging printout if you want that.
        // println(httpReq.indentedStr(0))
        // println(resp.indentedStr(0))
        // println((resp.outputStream as FakeServletOutputStream)
        //         .indentedStr(0))
    }
}
```

It's easy to test the response body too:
```kotlin
assertTrue(resp.outputStream.toString().contains("@media only screen"))
```

## Contributions
To build locally (in an appropriate folder), you need Java 8+, gradle, and git installed.  Then:
```bash
git clone https://github.com/GlenKPeterson/TestUtils.git
gradle clean assemble publishToMavenLocal
```

## Change Log

### 2.0.0 2022-01-03 "jakarta.servlet-api:5.0.0"
- Updated from javax.servlet api 4 to jakarta 5.
- No longer includes Junit in build.  Throws `AssertionError`s instead of calling JUnit.assertEqual().
- Internally uses Junit 5.

### 1.0.6 2021-12-27 "Bumped versions"
- Updated dependencies

### 1.0.5 2021-09-20 "Bumped versions"
- Updated dependencies

### 1.0.4 2021-09-17 "Fix CVE-2020-15250"
- Added dependency to junit 4.13.2 because the one packaged with kotlin-test is currently vulnerable to CVE-2020-15250

### 1.0.3 2021-09-14 "bump"
- Bumped dependency versions.

### 1.0.2 2020-12-16 "differentMiddle"
 - Added StringDiff.differentMiddle()
 - Made FakeHttpServletResponse.getOutputStream() return a FakeServletOutputStream
   instead of OutputStream so you can access .stringWriter to call .toString() on it.
 - Bumped dependency versions.

### 1.0.1 2020-10-16 "Kotlin type signatures"
 - Converted the various contract tests (Equals, Comparator, and Comparable) to Kotlin
 in order to simplify the type signatures.
 - Renamed CompareToContract to ComparableContract

***Upgrade Instructions:***
Replace all words: `CompareToContract` with `ComparableContract`

#### 1.0.0 2020-10-08 "1.0"
 - Added more @NotNull annotations and did a tiny bit of cleanup.
 - I've been using this for years.  It's 1.0 quality, so I'm calling it 1.0.

#### 0.0.20 2020-10-07 "CompareToContract Signature"
 - This fixes a longstanding bug (and/or maybe a new Kotlin incompatibility)
 in the generic type signature of `CompareToContract.testCompareTo()`.
 It hasn't caused a problem (yet) in Java, but Kotlin 1.4.10 likes
 the new version *much* better.

#### 0.0.19 2020-10-06
 - Bumped dependency versions

#### 0.0.18 2020-08-20
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

##### 0.0.14 2020-04-02
 - Updated all dependencies.
 - Upgraded Gradle to 6.3 and switched to .kts gradle file format.

##### 0.0.13 2019-05-24
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
