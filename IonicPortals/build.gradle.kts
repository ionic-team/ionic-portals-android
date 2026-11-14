import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
    id("maven-publish")
}

if (System.getenv("PORTALS_PUBLISH") == "true") {
    apply(from = file("./scripts/publish-module.gradle"))
}

android {
    namespace = "io.ionic.portals"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("test").java.srcDirs("src/test/kotlin")
        getByName("androidTest").java.srcDirs("src/androidTest/kotlin")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    publishing {
        singleVariant("release")
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

dependencies {
    implementation(kotlin("reflect"))

    api("com.capacitorjs:core:[8.0.0,9.0.0)")
    api("io.ionic:liveupdateprovider:1.0.0")
    compileOnly("io.ionic:liveupdates:0.5.5")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("androidx.appcompat:appcompat:1.7.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

// AGP 9 built-in Kotlin does not register the Kotlin Gradle plugin extension Dokka discovers source sets from.
dokka {
    dokkaSourceSets.register("main") {
        sourceRoots.from("src/main/kotlin")
    }
}
