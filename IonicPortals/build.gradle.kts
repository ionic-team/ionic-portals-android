plugins {
    id("com.android.library")
    id("maven-publish")
    id("org.jetbrains.kotlin.android")
}

if (System.getenv("PORTALS_PUBLISH") == "true") {
    apply(from = file("./scripts/publish-module.gradle"))
}

android {
    namespace = "io.ionic.portals"
    compileSdk = 35

    defaultConfig {
        minSdk = 23
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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    publishing {
        singleVariant("release")
    }
}

dependencies {
    implementation(kotlin("reflect"))

    api("com.capacitorjs:core:[7.0.0,7.1.0)")
    compileOnly("io.ionic:liveupdates:0.5.5")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
