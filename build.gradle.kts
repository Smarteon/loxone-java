group = "cz.smarteon"

plugins {
    java
    groovy
    signing
    maven
    jacoco
    id("net.researchgate.release") version "2.6.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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

    // spock and dependencies - 1.3 is probably latest version ever, do not upgrade the deps versions to ensure compatibility
    testImplementation("org.spockframework:spock-core:1.3-groovy-2.5")
    testImplementation("org.codehaus.groovy:groovy-all:2.5.7")
    testRuntimeOnly("cglib:cglib-nodep:3.2.5")
    testRuntimeOnly("org.objenesis:objenesis:2.5.1")

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

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

val javadocJar by tasks.creating(Jar::class) {
    dependsOn("javadoc")
    archiveClassifier.set("javadoc")
    from(tasks["javadoc"])
}

artifacts {
    add("archives", sourcesJar)
    add("archives", javadocJar)
}

if (hasProperty("signing.keyId")) {
    signing {
        setRequired({
            !project.version.toString().endsWith("-SNAPSHOT")
                    && gradle.taskGraph.hasTask("uploadArchives")
        })
        sign(configurations.archives.get())
    }
}

val ossUser: String? by project
val ossPass: String? by project

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

    "afterReleaseBuild" {
        dependsOn("uploadArchives")
    }

    "uploadArchives"(Upload::class) {
        repositories {
            withConvention(MavenRepositoryHandlerConvention::class) {
                mavenDeployer {
                    beforeDeployment { let(signing::signPom) }
                    withGroovyBuilder {
                        "repository"("url" to uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")) {
                            "authentication"("userName" to ossUser, "password" to ossPass)
                        }
                        "snapshotRepository"("url" to uri("https://oss.sonatype.org/content/repositories/snapshots/")) {
                            "authentication"("userName" to ossUser, "password" to ossPass)
                        }
                    }

                    pom.project {
                        withGroovyBuilder {
                            "packaging"("jar")
                            "name"(name)
                            "url"("https://github.com/Smarteon/loxone-java")
                            "description"("Java implementation of the Loxone&trade; communication protocol (Web Socket)")
                            "organization" {
                                "name"("Smarteon Systems s.r.o")
                                "url"("https://smarteon.cz")
                            }
                            "licenses" {
                                "license" {
                                    "name"("3-Clause BSD License")
                                    "url"("http://opensource.org/licenses/BSD-3-Clause")
                                    "distribution"("repo")
                                }
                            }
                            "developers" {
                                "developer" {
                                    "name"("Jiří Mikulášek")
                                    "email"("jiri.mikulasek@smarteon.cz")
                                }
                                "developer" {
                                    "name"("Vojtěch Zavřel")
                                    "email"("vojtech.zavrel@smarteon.cz")
                                }
                                "developer" {
                                    "name"("Petr Žufan")
                                    "email"("petr.zufan@smarteon.cz")
                                }
                            }
                            "scm" {
                                "url"("git@github.com:Smarteon/loxone-java.git")
                                "connection"("scm:git:git@github.com:Smarteon/loxone-java.git")
                                "tag"("HEAD")
                            }
                        }
                    }
                }
            }
        }
    }
}
