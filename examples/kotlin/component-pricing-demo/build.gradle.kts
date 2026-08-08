@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform") version "2.4.0"
    id("dev.zacsweers.metro") version "1.3.2"
}

repositories {
    mavenCentral()
}

kotlin {
    wasmWasi {
        binaries.executable()
        nodejs()
    }

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        wasmWasiMain {
            kotlin.srcDir("generated/wit-bindgen")
        }
    }
}
