import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Deploying to OSSRH with Gradle
// https://central.sonatype.org/pages/gradle.html
// https://github.com/gradle-nexus/publish-plugin

// Did you update version number here AND in the README?
// This is different from other projects because it is TEST SCOPED.

// To find out if any dependencies need upgrades:
// gradle --refresh-dependencies dependencyUpdates

// To publish to maven local:
// gradle --warning-mode all clean assemble dokkaJar publishToMavenLocal

// To publish to Sonatype (do the maven local above first):
// gradle --warning-mode all clean assemble dokkaJar publishToSonatype closeAndReleaseSonatypeStagingRepository

// If half-deployed, sign in here:
// https://oss.sonatype.org
// Click on "Staging Repositories"
// Open the "Content" for the latest one you uploaded.
// If it looks good, "Close" it and wait.
// When it's really "closed" with no errors, "Release" (and automatically drop) it.
//
// Alternatively, if you can see it here, then it's ready to be "Closed" and deployed manually:
// https://oss.sonatype.org/content/groups/staging/org/organicdesign/TestUtils/
// Here once released:
// https://repo1.maven.org/maven2/org/organicdesign/TestUtils/

// https://docs.gradle.org/current/userguide/build_environment.html
// You must have the following set in ~/.gradle/gradle.properties
// sonatypeUsername=
// sonatypePassword=
//
// At least while dokka crashes the gradle daemon you also want:
// org.gradle.daemon=false
// Or run with --no-daemon
plugins {
    `maven-publish`
    signing
    id("com.github.ben-manes.versions") version "0.42.0"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("org.jetbrains.dokka") version "1.6.10"
    kotlin("jvm") version "1.6.10"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.organicdesign:Indented:0.1.1")
    implementation("jakarta.servlet:jakarta.servlet-api:5.0.0")

    // This was so tied together there was no reasonable way to unpick it.
    // Copied 2 classes from jetty-server.
    // Next step down is to copy maybe 5 classes from jetty-http and include only jetty-util?
    // Jetty-util has some nice UTF8 StringBuilder stuff.
    implementation("org.eclipse.jetty:jetty-http:11.0.8")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.slf4j:slf4j-simple:2.0.0-alpha5")
}

group = "org.organicdesign"
version = "2.0.2"
description = "Utilities for testing common Java contracts: equals(), hashCode(), and compareTo()"

java {
//    withJavadocJar()
    withSourcesJar()
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Jar>("dokkaJar") {
    archiveClassifier.set("javadoc")
    dependsOn("dokkaJavadoc")
    from("$buildDir/dokka/javadoc")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            afterEvaluate {
                artifactId = tasks.jar.get().archiveBaseName.get()
            }
            artifact(tasks["dokkaJar"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
//            artifact(tasks["dokkaJar"])
            pom {
                name.set(rootProject.name)
                packaging = "jar"
                description.set(project.description)
                url.set("https://github.com/GlenKPeterson/TestUtils")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://apache.org/licenses/LICENSE-2.0.txt")
                    }
                    license {
                        name.set("The Eclipse Public License v. 2.0")
                        url.set("https://eclipse.org/legal/epl-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("GlenKPeterson")
                        name.set("Glen K. Peterson")
                        email.set("glen@organicdesign.org")
                        organization.set("PlanBase Inc.")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/GlenKPeterson/TestUtils.git")
                    developerConnection.set("scm:git:https://github.com/GlenKPeterson/TestUtils.git")
                    url.set("https://github.com/GlenKPeterson/TestUtils.git")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype()
    }
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}

tasks.compileJava {
    options.encoding = "UTF-8"
}
repositories {
    mavenLocal()
    mavenCentral()
    maven(url="https://jitpack.io")
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "11"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "11"
}