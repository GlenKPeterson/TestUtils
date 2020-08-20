import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Deploying to OSSRH with Gradle
// https://central.sonatype.org/pages/gradle.html

// To upload to sonatype (have to deploy manually)
// ./gradlew clean assemble dokkaJar publish

// Sign in here:
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
    id("org.jetbrains.dokka") version "1.4.0-rc"
    id("com.github.ben-manes.versions") version "0.28.0"
    kotlin("jvm") version "1.4.0"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.organicdesign:Indented:0.0.12")
    implementation("javax.servlet:javax.servlet-api:4.0.1")
    implementation(kotlin("test-junit"))
    testImplementation(kotlin("test"))
}

group = "org.organicdesign"
version = "0.0.17"
description = "Utilities for testing common Java contracts: equals(), hashCode(), and compareTo()"

java {
//    withJavadocJar()
    withSourcesJar()
}

tasks.register<Jar>("dokkaJar") {
    archiveClassifier.set("javadoc")
    dependsOn("dokkaJavadoc")
    from("$buildDir/dokka")
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

signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}

tasks.compileJava {
    options.encoding = "UTF-8"
}
repositories {
    jcenter()
    mavenCentral()
    maven(url="https://jitpack.io")
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}