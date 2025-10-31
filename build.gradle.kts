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
    signing
    `maven-publish`
    jacoco
    id("pl.allegro.tech.build.axion-release") version "1.18.18"
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
    id("ru.vyarus.quality") version "4.8.0"
    kotlin("jvm") version "1.7.10"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withJavadocJar()
    withSourcesJar()
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

val ossUser: String? = System.getenv("OSS_USER")
val ossPass: String? = System.getenv("OSS_PASS")

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])

            pom {
                name.set(project.name)
                url.set("https://github.com/Smarteon/loxone-java")
                description.set("Java implementation of the Loxone&trade; communication protocol (Web Socket)")
                organization {
                    name.set("Smarteon Systems s.r.o")
                    url.set("https://smarteon.cz")
                }
                licenses {
                    license {
                        name.set("3-Clause BSD License")
                        url.set("https://opensource.org/licenses/BSD-3-Clause")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        name.set("Jiří Mikulášek")
                        email.set("jiri.mikulasek@smarteon.cz")
                    }
                    developer {
                        name.set("Vojtěch Zavřel")
                        email.set("vojtech.zavrel@smarteon.cz")
                    }
                    developer {
                        name.set("Tomáš Knotek")
                        email.set("tomas.knotek@smarteon.cz")
                    }
                }
                contributors {
                    contributor {
                        name.set("Petr Žufan")
                    }
                }
                scm {
                    url.set("git@github.com:Smarteon/loxone-java.git")
                    connection.set("scm:git:git@github.com:Smarteon/loxone-java.git")
                    tag.set(project.version.toString())
                }
            }
        }
    }
}

if (ossUser != null && ossPass != null) {
    nexusPublishing {
        repositories {
            sonatype {
                username.set(ossUser)
                password.set(ossPass)
            }
        }
    }
}

val signingKey: String? = System.getenv("SIGNING_KEY")
val signingPassword: String? = System.getenv("SIGNING_PASS")
if (signingKey != null && signingPassword != null) {
    signing {
        setRequired({
            !project.version.toString().endsWith("-SNAPSHOT")
        })
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["library"])
    }
} else if (hasProperty("signing.keyId")) {
    signing {
        setRequired({
            !project.version.toString().endsWith("-SNAPSHOT")
        })
        sign(publishing.publications["library"])
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
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    check {
        dependsOn(jacocoTestReport)
    }
}
