plugins {
    java
}

group = "cz.smarteon"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("cz.smarteon:loxone-java:0.1.1-SNAPSHOT")
    implementation("org.bouncycastle:bcprov-jdk15on:1.59")
    runtimeOnly("org.slf4j:slf4j-simple:1.7.25")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}