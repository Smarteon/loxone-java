group = "cz.smarteon"
version = "0.0.1-SNAPSHOT"

plugins {
    java
    groovy
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

repositories {
    mavenCentral()
}


dependencies {
    implementation("org.java-websocket:Java-WebSocket:1.3.8")
    implementation("com.fasterxml.jackson.core:jackson-core:2.9.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.9.2")
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

publishing {
    (publications) {
        "mavenJava"(MavenPublication::class) {
            from(components["java"])
        }
    }
}
