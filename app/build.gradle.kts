plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    alias(libs.plugins.google.gms.google.services)
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.example.a1"
    compileSdk = 35 // Changed from 35 to 34

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    packaging {
        resources {
            excludes.add("META-INF/DEPENDENCIES")
            excludes.add("META-INF/io.netty.versions.properties")
            excludes.add("META-INF/INDEX.LIST")

        }
    }

    defaultConfig {
        applicationId = "com.example.a1"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.volley)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lottie)
    implementation(libs.ccp)

    // Google Play Services
    implementation(libs.play.services.location.v2101)
    implementation(libs.play.services.maps.v1910)
    implementation(libs.play.services.auth)

    // Permission handling
    implementation(libs.pub.easypermissions)

    // Lifecycle components
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    // Navigation components - use consistent versions from libs catalog
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    //implementation(libs.navigation.ui.ktx)
    implementation(libs.firebase.appdistribution.gradle)
    // Remove the direct implementation with specific version

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson)

    // Data binding
    annotationProcessor(libs.compiler)

    // OpenTok
    implementation(libs.opentok.android.sdk.v2261)

    // UI components
    implementation(libs.squareup.picasso)
    implementation(libs.drawerlayout)
    implementation(libs.viewpager2)
    implementation(libs.exoplayer)
    implementation(libs.com.github.bumptech.glide.glide)

    // ZegoCloud
    implementation ("com.github.ZEGOCLOUD:zego_uikit_prebuilt_call_android:+")
}