plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.cobacapstone"
    compileSdk = 35
    val baseUrl: String = project.findProperty("BASE_URL")
            as String? ?: "https://backend-api-v1-0-458350978327.asia-southeast2.run.app/"


    defaultConfig {
        applicationId = "com.example.cobacapstone"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

}

dependencies {
    // AndroidX Core Libraries
    implementation(libs.androidx.core.ktx)              // Core KTX Extensions
    implementation(libs.androidx.appcompat)             // Backward-compatible UI elements
    implementation(libs.androidx.cardview)              // CardView UI component
    implementation(libs.androidx.constraintlayout)      // ConstraintLayout for flexible UI layouts
    implementation(libs.androidx.activity)              // Activity support with lifecycle awareness

    // AndroidX Lifecycle & Navigation Components
    implementation(libs.androidx.lifecycle.livedata.ktx) // LiveData for observing UI changes
    implementation(libs.androidx.lifecycle.viewmodel.ktx) // ViewModel to handle UI-related data
    implementation(libs.androidx.navigation.fragment.ktx) // Navigation component for fragment management
    implementation(libs.androidx.navigation.ui.ktx)       // Navigation UI component
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Material Design Components
    implementation(libs.material)                        // Material Design components and styles

    // Dependency Injection
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.work)                    // Hilt for dependency injection
    ksp(libs.hilt.compiler)                              // Hilt compiler for code generation

    // Networking & Serialization
    implementation(libs.retrofit)                        // Retrofit for API integration
    implementation(libs.converter.gson)                  // Gson converter for JSON parsing
    implementation(libs.logging.interceptor)             // OkHttp logging for network requests

    implementation(libs.gson)

    // Image Loading
    implementation(libs.glide)                           // Glide for image loading and caching

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)      // Coroutines for asynchronous tasks on Android

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    implementation (libs.androidx.datastore.preferences)

    // Queue prediksi
    implementation (libs.androidx.work.runtime.ktx)

    // Testing Libraries
    testImplementation(libs.junit)                       // JUnit for unit testing
    androidTestImplementation(libs.androidx.junit)       // AndroidX JUnit extensions for Android testing
    androidTestImplementation(libs.androidx.espresso.core) // Espresso for UI testing

    implementation(libs.dotsindicator)

    implementation ("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.1")

}
