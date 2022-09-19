rootProject.name = "loxone-java"

pluginManagement {
    repositories {
        // Prefer Central to the Gradle Plugin Portal as the latter redirects to the unreliable JCenter for most things actually coming from Central.
        // see https://github.com/gradle/gradle/issues/15406
        mavenCentral()
        gradlePluginPortal()
    }
}
