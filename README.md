# TestUtils
Utilities for testing common Java contracts.  Currently equals(), hashCode(), and compareTo().
I find a bug almost every time I apply these tests to some old piece of code.  Usage is defined in the Javadocs.

The idea of contract-based testing was from watching Bill Venners:
https://www.youtube.com/watch?v=bCTZQi2dpl8
Any bugs are my own.

#Maven Dependency

Note that this project is just for testing, so add it only to the `test` scope of your project:
```xml
<dependency>
	<groupId>org.organicdesign.testUtils</groupId>
	<artifactId>TestUtils</artifactId>
	<version>0.0.2-SNAPSHOT</version>
	<scope>test</scope>
</dependency>
```
This project has not been submitted to Sonatype yet, so you have to build it and add it to your local repository before you can use it.  It's easy, just do this in an appropriate directory:
```bash
git clone https://github.com/GlenKPeterson/TestUtils.git
mvn clean install
```

#Change Log
0.0.3-SNAPSHOT Added/updated JavaDocs.

#License
Apache 2.0 Copyright 2015 Glen Peterson and PlanBase Inc.
