plugins {
    kotlin("jvm") version "1.9.23"
    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "io.dpopkov.apatheiafx"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
javafx {
    version = "19"
    modules("javafx.controls", "javafx.graphics", "javafx.fxml", "javafx.media")
}
