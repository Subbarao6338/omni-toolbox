plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

fun getGitCommitCount(): Int {
    return try {
        val process = ProcessBuilder("git", "rev-list", "--count", "HEAD").start()
        val count = process.inputStream.bufferedReader().readText().trim().toInt()
        process.waitFor()
        count
    } catch (e: Exception) {
        1
    }
}

android {
    namespace = "com.nature.docs"
    compileSdk = 34

    val commitCount = getGitCommitCount()

    defaultConfig {
        applicationId = "com.nature.docs"
        minSdk = 26
        targetSdk = 34
        versionCode = System.getenv("APP_VERSION_CODE")?.toIntOrNull() ?: commitCount
        versionName = System.getenv("APP_VERSION_NAME") ?: "1.0.$commitCount"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("KEYSTORE_FILE_PATH")
            if (keystorePath != null) {
                storeFile = file(keystorePath)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
            enableV1Signing = true
            enableV2Signing = true
        }
        create("debug_stable") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            val isSigningConfigured = System.getenv("KEYSTORE_FILE_PATH") != null
            signingConfig = if (isSigningConfigured) {
                signingConfigs.getByName("release")
            } else {
                // Fallback to debug key for non-production builds so they are installable
                signingConfigs.getByName("debug_stable")
            }
            
            // Aggressive optimization
            ndk {
                debugSymbolLevel = "none"
            }
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug_stable")
        }
    }
    
    packaging {
        jniLibs {
            useLegacyPackaging = true
            keepDebugSymbols += "**/libmlkit_google_ocr_pipeline.so"
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/*.kotlin_module"
            excludes += "/*.txt"
            excludes += "/com/tom_roush/pdfbox/resources/version.properties"
            excludes += "/*.properties"
            excludes += "/META-INF/*.version"
            excludes += "/META-INF/proguard/*"
            excludes += "/com/google/android/material/**"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures { compose = true }
    lint { abortOnError = false }
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.googlefonts)
    api(libs.pdfbox.android)
    implementation(libs.poi.ooxml)
    implementation(libs.mlkit.text.recognition)
    implementation(libs.mlkit.segmentation.selfie)
    implementation(libs.mlkit.barcode.scanning)
    implementation(libs.mlkit.digital.ink.recognition)
    implementation(libs.mlkit.translate)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.coil.compose)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.transformer)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.palette)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}
