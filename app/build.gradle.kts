plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.medicinereminder"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.medicinereminder"
        minSdk = 28
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Core dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Firebase (using BOM for consistent versioning)
    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-database")
    implementation ("com.google.firebase:firebase-auth:21.0.5") // or latest version
    implementation ("com.google.firebase:firebase-storage:20.0.0") // or latest version
    implementation ("com.google.firebase:firebase-storage:21.0.1")
    implementation ("androidx.work:work-runtime:2.7.1")


    // Android components
    implementation(libs.activity)

    // Third-party libraries
    implementation("com.braintreepayments:card-form:3.1.1") // Credit card input form
    implementation("com.google.android.gms:play-services-vision:20.1.3") // Vision API
    implementation("com.google.android.gms:play-services-location:21.0.1") // Location services
    implementation("androidx.work:work-runtime:2.8.1") // WorkManager for background tasks

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("androidx.work:work-testing:2.8.1") // WorkManager testing
}
