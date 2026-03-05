import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import com.android.build.api.variant.BuildConfigField

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.ionic.portals.testapp"
    compileSdk = 36

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "io.ionic.portals.testapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

    kotlinOptions {
        jvmTarget = "21"
    }
}

androidComponents {
    onVariants {
        it.buildConfigFields?.put("PORTALS_KEY", BuildConfigField("String", getPortalsKey(), "portals registration key"))
    }
}

dependencies {
    implementation("io.ionic:live-updates-provider:LOCAL-SNAPSHOT")

    implementation(project(":IonicPortals"))
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-web:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
}

fun getPortalsKey(): String {
    val propFile = rootProject.file("local.properties")
    val properties = Properties()
    properties.load(FileInputStream(propFile))
<<<<<<< HEAD
    val raw = properties.getProperty("portals_key") ?: ""
    val normalized = if (raw.length >= 2 && raw.first() == '"' && raw.last() == '"') {
        raw.substring(1, raw.length - 1)
    } else {
        raw
    }
    val escaped = normalized.replace("\\", "\\\\").replace("\"", "\\\"")
    return "\"$escaped\""
=======
    return properties.getProperty("portals_key") ?: ""
>>>>>>> e9d0f1b (feat: add third-party LiveUpdates provider support and upgrade dependencies)
}
