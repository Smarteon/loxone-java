import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "cz.smarteon"

scmVersion {
    tag {
        prefix.set("")
        versionSeparator.set("")
    }
}

project.version = scmVersion.version

plugins {
    `java-library`
    jacoco
    id("pl.allegro.tech.build.axion-release") version "1.18.18"
    id("com.vanniktech.maven.publish") version "0.34.0"
    id("ru.vyarus.quality") version "6.0.1"
    kotlin("jvm") version "2.2.20"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    // javadoc and sources configured in publishing plugin
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.java-websocket:Java-WebSocket:1.5.2")

    val jacksonVersion = "2.13.3"
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")

    implementation("com.sun.xml.bind:jaxb-impl:3.0.2")

    val slf4jVersion = "1.7.32"
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    compileOnly("org.jetbrains:annotations:22.0.0")

    val lombokVersion = "1.18.24"
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    val junitVersion = "5.8.2"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    val striktVersion = "0.34.1"
    testImplementation("io.strikt:strikt-core:$striktVersion")
    testImplementation("io.strikt:strikt-jvm:$striktVersion")
    testImplementation("io.mockk:mockk:1.12.2")

    val jsonUnitVersion = "2.28.0"
    testImplementation("net.javacrumbs.json-unit:json-unit:$jsonUnitVersion")
    testImplementation("net.javacrumbs.json-unit:json-unit-core:$jsonUnitVersion")


    testImplementation("nl.jqno.equalsverifier:equalsverifier:3.7.2")
    testRuntimeOnly("org.slf4j:slf4j-simple:$slf4jVersion")
    testImplementation("org.bouncycastle:bcprov-jdk15on:1.69")

    val jadlerVersion = "1.3.1"
    testImplementation("net.jadler:jadler-core:$jadlerVersion")
    testImplementation("net.jadler:jadler-jdk:$jadlerVersion")

    val ktorVersion = "2.0.3"
    testImplementation("io.ktor:ktor-server-core:$ktorVersion")
    testImplementation("io.ktor:ktor-server-netty:$ktorVersion")
    testImplementation("io.ktor:ktor-server-websockets:$ktorVersion")
    testImplementation("io.ktor:ktor-network:$ktorVersion")
    testImplementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    testImplementation("io.ktor:ktor-serialization-jackson:$ktorVersion")

    testImplementation("org.awaitility:awaitility-kotlin:4.1.1")
}

// see https://vanniktech.github.io/gradle-maven-publish-plugin/central/#secrets for how to set up credentials and signing
mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    configure(
        JavaLibrary(
            javadocJar = JavadocJar.Javadoc(),
            sourcesJar = true,
        )
    )

    coordinates(project.group.toString(), project.name, project.version.toString())

    pom {
        name = project.name
        url = "https://github.com/Smarteon/loxone-java"
        description = "Java implementation of the Loxone&trade; communication protocol (Web Socket)"
        organization {
            name = "Smarteon Systems s.r.o"
            url = "https://smarteon.cz"
        }
        licenses {
            license {
                name = "3-Clause BSD License"
                url = "https://opensource.org/licenses/BSD-3-Clause"
                distribution = "repo"
            }
        }
        developers {
            developer {
                name = "Jiří Mikulášek"
                email = "jiri.mikulasek@smarteon.cz"
            }
            developer {
                name = "Vojtěch Zavřel"
                email = "vojtech.zavrel@smarteon.cz"
            }
            developer {
                name = "Tomáš Knotek"
                email = "tomas.knotek@smarteon.cz"
            }
        }
        contributors {
            contributor {
                name = "Petr Žufan"
            }
        }
        scm {
            url = "git@github.com:Smarteon/loxone-java.git"
            connection = "scm:git:git@github.com:Smarteon/loxone-java.git"
            tag = project.version.toString()
        }
    }
}

quality {
    strict = false
}

tasks {
    withType<JacocoReport> {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    withType<Test> {
        useJUnitPlatform()
        jvmArgs(
            "--add-opens",
            "java.base/jdk.internal.misc=ALL-UNNAMED",
            "-Dio.netty.tryReflectionSetAccessible=true"
        )
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    check {
        dependsOn(jacocoTestReport)
    }
}
