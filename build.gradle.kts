group = "cz.smarteon"

plugins {
    java
    groovy
    signing
    maven
    id("net.researchgate.release") version "2.6.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}


repositories {
    mavenCentral()
}

val jacksonVersion = "2.9.5"

dependencies {
    implementation("org.java-websocket:Java-WebSocket:1.3.8")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("org.slf4j:slf4j-api:1.7.25")

    testImplementation("org.spockframework:spock-core:1.1-groovy-2.4")
    testImplementation("org.codehaus.groovy:groovy-all:2.4.10")
    testRuntimeOnly("cglib:cglib-nodep:3.2.5")
    testRuntimeOnly("org.objenesis:objenesis:2.5.1")
    testImplementation("nl.jqno.equalsverifier:equalsverifier:2.4")
    testImplementation("net.javacrumbs.json-unit:json-unit:1.23.0")
    testImplementation("net.javacrumbs.json-unit:json-unit-core:1.23.0")
    testRuntimeOnly("org.slf4j:slf4j-simple:1.7.25")
    testImplementation("org.bouncycastle:bcprov-jdk15on:1.59")
}

val sourcesJar by tasks.creating(Jar::class) {
    classifier = "sources"
    from(java.sourceSets["main"].allSource)
}

val javadocJar by tasks.creating(Jar::class) {
    dependsOn(tasks["javadoc"])
    classifier = "javadoc"
    from(java.docsDir)
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
        sign(configurations.archives)
    }
}

val ossUser by project
val ossPass by project

tasks {
    "afterReleaseBuild" {
        dependsOn(tasks["uploadArchives"])
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
