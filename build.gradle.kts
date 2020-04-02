import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.dokka.gradle.DokkaTask

// Deploying to OSSRH with Gradle
// https://central.sonatype.org/pages/gradle.html

// To upload to sonatype (have to deploy manually)
// ./gradlew clean assemble javadocJar publish --info

// I think if you can see it here, then it's ready to be "Closed" and deployed manually:
// https://oss.sonatype.org/content/groups/staging/org/organicdesign/testUtils/TestUtils/
// Here once released:
// https://repo1.maven.org/maven2/org/organicdesign/testUtils/TestUtils/

// This takes these values from ~/gradle.properties which should have valid values for each of these names in it.
// https://docs.gradle.org/current/userguide/build_environment.html
val ossrhUsername: String by project
val ossrhPassword: String by project

plugins {
    `java-library`
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "0.10.1"
    id("com.github.ben-manes.versions") version "0.28.0"
    kotlin("jvm") version "1.3.71"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.organicdesign.indented:Indented:0.0.11")
    implementation("javax.servlet:javax.servlet-api:4.0.1")
    implementation(kotlin("test-junit"))
    testImplementation(kotlin("test"))
}

group = "org.organicdesign.testUtils"
version = "0.0.14"
description = "Utilities for testing common Java contracts: equals(), hashCode(), and compareTo()"

java {
//    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            afterEvaluate {
                artifactId = tasks.jar.get().archiveBaseName.get()
            }
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
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
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

tasks {
    val dokka by getting(DokkaTask::class) {
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"
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
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/kotlin/dokka") }
    maven { url = uri("https://jitpack.io") }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}