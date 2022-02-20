group = "cz.smarteon"

plugins {
    `java-library`
    groovy
    signing
    `maven-publish`
    jacoco
    id("net.researchgate.release") version "2.6.0"
    kotlin("jvm") version "1.6.10"
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

    val jacksonVersion = "2.13.0"
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")

    implementation("com.sun.xml.bind:jaxb-impl:3.0.2")

    val slf4jVersion = "1.7.32"
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    compileOnly("org.jetbrains:annotations:22.0.0")

    testImplementation("org.spockframework:spock-core:2.0-groovy-3.0")

    testImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    val junitVersion = "5.8.2"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("io.strikt:strikt-core:0.33.0")
    testImplementation("io.mockk:mockk:1.12.2")

    val jsonUnitVersion = "2.28.0"
    testImplementation("net.javacrumbs.json-unit:json-unit:$jsonUnitVersion")
    testImplementation("net.javacrumbs.json-unit:json-unit-core:$jsonUnitVersion")


    testImplementation("nl.jqno.equalsverifier:equalsverifier:3.7.2")
    testRuntimeOnly("org.slf4j:slf4j-simple:$slf4jVersion")
    testImplementation("org.bouncycastle:bcprov-jdk15on:1.69")

    val jadlerVersion = "1.3.0"
    testImplementation("net.jadler:jadler-core:$jadlerVersion")
    testImplementation("net.jadler:jadler-jdk:$jadlerVersion")

    val ktorVersion = "2.0.0-beta-1"
    testImplementation("io.ktor:ktor-server-core:$ktorVersion")
    testImplementation("io.ktor:ktor-server-netty:$ktorVersion")
    testImplementation("io.ktor:ktor-server-websockets:$ktorVersion")
    testImplementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    testImplementation("io.ktor:ktor-serialization-jackson:$ktorVersion")

    testImplementation("org.awaitility:awaitility-kotlin:4.1.1")
}

val ossUser: String? by project
val ossPass: String? by project

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
                        url.set("http://opensource.org/licenses/BSD-3-Clause")
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
                        name.set("Petr Žufan")
                        email.set("petr.zufan@smarteon.cz")
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

    repositories {
        maven {
            name = "oss"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            authentication {
                credentials {
                    username = ossUser
                    password = ossPass
                }
            }
        }
    }
}

if (hasProperty("signing.keyId")) {
    signing {
        setRequired({
            !project.version.toString().endsWith("-SNAPSHOT")
        })
        sign(publishing.publications["library"])
    }
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

    check {
        dependsOn(jacocoTestReport)
    }

    afterReleaseBuild {
        dependsOn(getByName("publishLibraryPublicationToOssRepository"))
    }
}
