/*
 * Copyright (c) 2016-2018 Projekt Substratum
 * This file is part of Substratum.
 *
 * SPDX-License-Identifier: GPL-3.0-Or-Later
 */

import java.io.FileInputStream
import java.io.IOException
import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val buildTypeRelease = "release"

fun gitHash(): String {
    try {
        return Runtime.getRuntime().exec("git describe --tags").inputStream.reader().use { it.readText() }.trim()
    } catch (ignored: IOException) {
    }
    return ""
}

android {
    compileSdkVersion(28)
    dataBinding.isEnabled = true
    defaultConfig {
        applicationId = "projekt.substratum"
        minSdkVersion(24)
        targetSdkVersion(28)
        versionCode = 1011
        versionName = "one thousand eleven"
        buildConfigField("java.util.Date", "buildTime", "new java.util.Date(${System.currentTimeMillis()}L)")
        buildConfigField("String", "GIT_HASH", "\"${gitHash()}\"")
        buildConfigField("boolean", "ENHANCED_LOGGING", "false")
        setProperty("archivesBaseName", "substratum_${gitHash()}")
    }
    compileOptions {
        setSourceCompatibility(JavaVersion.VERSION_1_8)
        setTargetCompatibility(JavaVersion.VERSION_1_8)
    }
    // If the keystore file exists
    if (keystorePropertiesFile.exists()) {
        // Initialize a new Properties() object called keystoreProperties.
        val keystoreProperties = Properties()

        // Load your keystore.properties file into the keystoreProperties object.
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))

        signingConfigs {
            create(buildTypeRelease) {
                keyAlias = keystoreProperties["keyAlias"].toString()
                keyPassword = keystoreProperties["keyPassword"].toString()
                storeFile = rootProject.file(keystoreProperties["storeFile"].toString())
                storePassword = keystoreProperties["storePassword"].toString()
            }
        }
    }
    buildTypes {
        getByName(buildTypeRelease) {
            if (keystorePropertiesFile.exists()) signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }
    dexOptions {
        setJavaMaxHeapSize("2048m")
    }
    lintOptions.isAbortOnError = false
}

dependencies {
    val aboutVersion = "6.2.0"
    val androidXVersion = "1.0.0"
    val apkSigVersion = "3.2.1"
    val caocVersion = "2.2.0"
    // Apparently v2.6 relies on the framework
    // having java.nio.File which Android 7 does not.
    val commonsIoVersion = "2.5"
    val crashlyticsVersion = "2.9.7"
    val databindingVersion = "3.2.1"
    val expandableLayoutVersion = "2.9.2"
    val fabSheetVersion = "1.2.1"
    val firebaseVersion = "16.0.6"
    val floatingHeadVersion = "2.4.0"
    val gestureRecyclerVersion = "1.7.0"
    val glideVersion = "4.8.0"
    val imageCropperVersion = "2.8.0"
    val svgViewVersion = "1.0.6"
    val welcomeVersion = "1.4.1"
    val ztZipVersion = "1.13"

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Android Support Libraries
    implementation("androidx.databinding:databinding-runtime:$databindingVersion")
    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("androidx.cardview:cardview:$androidXVersion")
    implementation("androidx.recyclerview:recyclerview:$androidXVersion")
    implementation("com.google.android.material:material:$androidXVersion")
    implementation("androidx.palette:palette:$androidXVersion")

    // Firebase
    implementation("com.google.firebase:firebase-core:$firebaseVersion")

    // Crashlytics
    implementation("com.crashlytics.sdk.android:crashlytics:$crashlyticsVersion")

    // Image Downloading and Caching
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    annotationProcessor("com.github.bumptech.glide:compiler:$glideVersion")

    // Commons IO
    implementation("commons-io:commons-io:$commonsIoVersion")

    // APK Signer
    implementation("com.android.tools.build:apksig:$apkSigVersion")

    // App Intro
    implementation("com.stephentuso:welcome:$welcomeVersion")

    // Floating Action Buttons
    implementation("com.gordonwong:material-sheet-fab:$fabSheetVersion")

    // About Libraries
    implementation("com.mikepenz:aboutlibraries:$aboutVersion@aar") {
        isTransitive = true
    }

    // Gesture RecyclerView
    implementation("com.thesurix.gesturerecycler:gesture-recycler:$gestureRecyclerVersion")

    // ZT Zip
    implementation("org.zeroturnaround:zt-zip:$ztZipVersion")

    // Image Processing
    implementation("com.theartofdev.edmodo:android-image-cropper:$imageCropperVersion")

    // Expandable Layout
    implementation("net.cachapa.expandablelayout:expandablelayout:$expandableLayoutVersion")

    // Floating App Head
    implementation("com.github.recruit-lifestyle:FloatingView:$floatingHeadVersion")

    // Crash Activity
    implementation("cat.ereza:customactivityoncrash:$caocVersion")

    // Splash screen svg animation
    implementation("com.jaredrummler:animated-svg-view:$svgViewVersion")
}
