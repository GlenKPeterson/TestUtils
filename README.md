# TestUtils
Utilities for testing common Java contracts.  Currently equals(), hashCode(), and compareTo().
I find a bug almost every time I apply these tests to some old piece of code.  Usage is defined in the Javadocs.

The idea of contract-based testing was from watching Bill Venners:
https://www.youtube.com/watch?v=bCTZQi2dpl8
Any bugs are my own.

## Maven Dependency
Note that this project is just for testing, so add it only to the `test` scope of your project:
```xml
<dependency>
	<groupId>org.organicdesign.testUtils</groupId>
	<artifactId>TestUtils</artifactId>
	<version>0.0.7</version>
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

#Change Log
0.0.7 Added HttpServletRequest mock
0.0.6 Added ComparatorContract test and Serialization helper function.
0.0.5 Allow compareTo(null) to throw *either* a NullPointerException or an IllegalArgumentException
0.0.4 Fixed variance on CompareToContract.testCompareTo()
0.0.3-SNAPSHOT Added/updated JavaDocs.

## License
Apache 2.0 Copyright 2015 Glen Peterson and PlanBase Inc.
