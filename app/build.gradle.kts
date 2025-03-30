plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.googleService)
}

android {
    namespace = "com.sigfred.kitchwa_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sigfred.kitchwa_app"
        minSdk = 26
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.core.splashscreen)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.firebaseDatabase)
    implementation(libs.firebaseDatabase)
    implementation(libs.firebaseAnalytics)
    implementation (libs.firebase.auth)
    implementation (libs.firebase.auth.v2200)
    implementation (libs.play.services.auth)
    implementation (libs.androidx.constraintlayout.v214)
    implementation (libs.kotlin.stdlib)
    implementation (libs.play.services.auth.v2111)
    implementation (libs.play.services.safetynet)
    implementation (libs.integrity)
    implementation (libs.firebase.auth.ktx)
    implementation ("com.google.android.play:integrity:1.4.0") // Última versión
    implementation ("com.google.android.gms:play-services-base:18.4.0")
    implementation ("com.google.firebase:firebase-auth-ktx:23.0.0")
    implementation ("com.google.android.play:integrity:1.4.0")
    implementation ("androidx.core:core-ktx:1.12.0")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.android.gms:play-services-auth:21.1.1")
    implementation ("com.google.firebase:firebase-firestore-ktx:25.0.0")
    implementation ("com.google.android.material:material:1.12.0")
    implementation (libs.play.services.auth.v2070)
}