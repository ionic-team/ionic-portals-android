plugins {
    id("org.jetbrains.dokka") version "2.3.0"
}

buildscript {
    val kotlinVersion = "2.2.20"

    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }

    dependencies {
        if (System.getenv("PORTALS_PUBLISH") == "true") {
            classpath("io.github.gradle-nexus:publish-plugin:2.0.0")
        }

        classpath("com.android.tools.build:gradle:9.4.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

if (System.getenv("PORTALS_PUBLISH") == "true") {
    apply(plugin = "io.github.gradle-nexus.publish-plugin")
    apply(from = file("./IonicPortals/scripts/publish-root.gradle"))
}

allprojects {
    repositories {
        google()
        mavenCentral()
        // Capacitor 9 beta is not on Maven Central yet; publish it to mavenLocal from @capacitor/android
        mavenLocal()
    }
}
