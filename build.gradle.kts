buildscript {
    val kotlinVersion = "1.6.21"
    extra.apply {
        set("kotlinVersion", kotlinVersion)
    }

    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        if (System.getenv("PORTALS_PUBLISH") == "true") {
            classpath("io.github.gradle-nexus:publish-plugin:1.1.0")
        }

        classpath("com.android.tools.build:gradle:7.2.2")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath(kotlin("serialization", version = kotlinVersion))
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
    }
}

// register Clean task
tasks.register("clean").configure {
    delete("build")
}
