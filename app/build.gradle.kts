plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    alias(libs.plugins.google.gms.google.services)
    id ("androidx.navigation.safeargs")
}



android {

    namespace = "com.example.a1"
    compileSdk = 35

    buildFeatures{
        viewBinding = true
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
    implementation (libs.ccp)

    implementation(libs.play.services.location.v2101) // Use the latest version
    implementation(libs.play.services.maps.v1910) // Use the latest version
    implementation(libs.play.services.auth) // Use the latest version


    implementation (libs.pub.easypermissions)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui) // To take the permissions
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.retrofit) // HTTP requests
    implementation (libs.retrofit2.converter.gson) // for

    //noinspection UseTomlInstead
    implementation ("androidx.navigation:navigation-fragment-ktx:2.8.9") // Use the latest version
    implementation (libs.navigation.ui.ktx) // Use the latest version
    
    implementation (libs.drawerlayout)

}



