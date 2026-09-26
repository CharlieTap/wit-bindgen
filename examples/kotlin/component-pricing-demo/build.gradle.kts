@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform") version "2.5.0-Beta1"
    id("dev.zacsweers.metro") version "1.4.5"
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
