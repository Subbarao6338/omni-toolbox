plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.9.22-1.0.17"
}

android {
    namespace = "com.nature.files"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nature.files"
        minSdk = 26
        targetSdk = 34

        val major = project.findProperty("VERSION_MAJOR")?.toString()?.toInt() ?: 1
        val minor = project.findProperty("VERSION_MINOR")?.toString()?.toInt() ?: 0
        val patch = project.findProperty("VERSION_PATCH")?.toString()?.toInt() ?: 0

        versionCode = project.findProperty("VERSION_CODE")?.toString()?.toInt() ?: (major * 10000 + minor * 100 + patch)
        versionName = project.findProperty("VERSION_NAME")?.toString() ?: "$major.$minor.$patch"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("release") {
            val storeFilePath = project.findProperty("RELEASE_STORE_FILE")?.toString() ?: "release.keystore"
            val storeFileObj = file(storeFilePath)
            val storePasswordStr = project.findProperty("RELEASE_STORE_PASSWORD")?.toString()
            val keyAliasStr = project.findProperty("RELEASE_KEY_ALIAS")?.toString()
            val keyPasswordStr = project.findProperty("RELEASE_KEY_PASSWORD")?.toString()

            val isSigningConfigComplete = storeFileObj.exists() &&
                    !storePasswordStr.isNullOrBlank() &&
                    !keyAliasStr.isNullOrBlank() &&
                    !keyPasswordStr.isNullOrBlank()

            if (isSigningConfigComplete) {
                storeFile = storeFileObj
                storePassword = storePasswordStr
                keyAlias = keyAliasStr
                keyPassword = keyPasswordStr
            } else {
                // If the file is missing or empty, Gradle will throw a much better error if we don't set it,
                // but let's provide some context if we can.
                println("Warning: Release keystore file is missing or empty at ${storeFileObj.absolutePath}")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            val releaseSigningConfig = signingConfigs.getByName("release")
            if (releaseSigningConfig.storeFile != null) {
                signingConfig = releaseSigningConfig
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/NOTICE"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.documentfile:documentfile:1.0.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Coil
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("io.coil-kt:coil-video:2.5.0")

    // Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.biometric:biometric:1.1.0")

    // Archives
    implementation("org.apache.commons:commons-compress:1.26.1")

    // Network
    implementation("com.github.mwiede:jsch:0.2.17")
    implementation("com.hierynomus:smbj:0.13.0")
    implementation("org.apache.ftpserver:ftpserver-core:1.2.0")

    // Media
    val media3Version = "1.2.1"
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-ui:$media3Version")

    // PDF
    implementation("com.tom-roush:pdfbox-android:2.0.27.0") {
        exclude(group = "org.bouncycastle", module = "bcprov-jdk15to18")
    }

    // Dual-pane / WindowManager
    implementation("androidx.window:window:1.2.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:core-3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
