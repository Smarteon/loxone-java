group = "cz.smarteon"

plugins {
    `java-library`
    groovy
    signing
    `maven-publish`
    jacoco
    id("net.researchgate.release") version "2.6.0"
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
    implementation("org.java-websocket:Java-WebSocket:1.5.1")

    val jacksonVersion = "2.12.2"
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")

    val slf4jVersion = "1.7.30"
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    compileOnly("org.jetbrains:annotations:17.0.0")

    testImplementation("org.spockframework:spock-core:2.0-M5-groovy-3.0")

    val jsonUnitVersion = "2.21.0"
    testImplementation("net.javacrumbs.json-unit:json-unit:$jsonUnitVersion")
    testImplementation("net.javacrumbs.json-unit:json-unit-core:$jsonUnitVersion")

    testImplementation("nl.jqno.equalsverifier:equalsverifier:3.5")
    testRuntimeOnly("org.slf4j:slf4j-simple:$slf4jVersion")
    testImplementation("org.bouncycastle:bcprov-jdk15on:1.67")

    val jadlerVersion = "1.3.0"
    testImplementation("net.jadler:jadler-core:$jadlerVersion")
    testImplementation("net.jadler:jadler-jdk:$jadlerVersion")
}

val ossUser: String? by project
val ossPass: String? by project

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])

            pom {
                name.set(project.name)
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
    jacocoTestReport {
        reports {
            xml.isEnabled = true
            html.isEnabled = true
        }
    }

    check {
        dependsOn(jacocoTestReport)
    }
}
