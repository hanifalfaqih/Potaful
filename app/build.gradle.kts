plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "id.hanifalfaqih.potaful"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "id.hanifalfaqih.potaful"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "OPEN_WEATHER_API_KEY", "\"3e4571c54ced693c23c3124dd408040e\"")
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
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.splashscreen)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    // Retrofit for API calls
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // OkHttp for networking
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Gson for JSON parsing
    implementation(libs.gson)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    // SwipeRefreshLayout for pull-to-refresh (explicit dependency because catalog alias unresolved)
    implementation(libs.androidx.swiperefreshlayout)

    // Glide for image loading
    implementation(libs.glide)
}