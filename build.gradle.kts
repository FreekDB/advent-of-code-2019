// Gradle build script (written in Kotlin) for yet another Advent of Code project.
// https://github.com/FreekDB/advent-of-code-2019

group = "nl.xs4all.home.freekdb.aoc"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.3.61"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
