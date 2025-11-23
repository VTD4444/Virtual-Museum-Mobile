plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.virtualmuseum"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.virtualmuseum"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.webkit:webkit:1.11.0")
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.mlkit.barcode.scanning)
    implementation(libs.guava)
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Thư viện này giúp tạo animation dễ dàng hơn (tùy chọn nhưng khuyến khích)
    implementation("com.google.accompanist:accompanist-navigation-animation:0.32.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.6")
    implementation("io.coil-kt:coil-compose:2.6.0")
    // Retrofit cho việc gọi API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
// Gson converter để parse JSON
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
// OkHttp logging interceptor để debug (rất hữu ích)
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation(libs.androidx.appcompat)
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.compose.ui:ui-viewbinding:1.6.7")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.animation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}