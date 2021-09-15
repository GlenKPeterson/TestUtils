import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Deploying to OSSRH with Gradle
// https://central.sonatype.org/pages/gradle.html

// Did you update version number here AND in the README?
// This is different from other projects because it is TEST SCOPED.

// gradle --refresh-dependencies dependencyUpdates

// To upload to sonatype (have to deploy manually)
// I'm using --no-daemon because dokka crashes the daemon too often.
// gradle --no-daemon clean assemble dokkaJar publish

// To work for compiling other stuff WITH MAVEN, ON THIS MACHINE:
// gradle --no-daemon publishToMavenLocal

// Log in here:
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

// This takes these values from ~/gradle.properties which should have valid values for each of these names in it.
// https://docs.gradle.org/current/userguide/build_environment.html
val ossrhUsername: String by project
val ossrhPassword: String by project

plugins {
    `java-library`
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "1.5.0"
    id("com.github.ben-manes.versions") version "0.39.0"
//    id("de.marcphilipp.nexus-publish") version "0.3.0"
//    id("io.codearte.nexus-staging") version "0.22.0"
    kotlin("jvm") version "1.5.30"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.organicdesign:Indented:0.0.18")
    implementation("javax.servlet:javax.servlet-api:4.0.1")
    implementation("org.jetbrains.kotlin:kotlin-test-junit:1.5.21")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.21")
}

group = "org.organicdesign"
version = "1.0.3"
description = "Utilities for testing common Java contracts: equals(), hashCode(), and compareTo()"

java {
//    withJavadocJar()
    withSourcesJar()
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
            pom {
                name.set(rootProject.name)
                packaging = "jar"
                description.set(project.description)
                url.set("https://github.com/GlenKPeterson/TestUtils")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
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
    repositories {
        maven {
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
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
// Publication 'org.organicdesign:TestUtils:1.0.2-SNAPSHOT' is published multiple times to the same location. It is likely that repository 'myNexus' is duplicated.
//nexusPublishing {
//    repositories {
//        create("myNexus") {
//            nexusUrl.set(uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/"))
//            snapshotRepositoryUrl.set(uri("https://oss.sonatype.org/content/repositories/snapshots"))
//            username.set(ossrhUsername)
//            password.set(ossrhPassword)
//        }
//    }
//}
//nexusStaging {
//    packageGroup = "org.organicdesign" //optional if packageGroup == project.getGroup()
//    stagingProfileId = "org.organicdesign" //when not defined will be got from server using "packageGroup"
////    username = ossrhUsername
////    password = ossrhPassword
//}
