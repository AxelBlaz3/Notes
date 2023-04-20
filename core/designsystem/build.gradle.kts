plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.codex.designsystem"
    compileSdk = 33

    defaultConfig {
        minSdk = 28
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)


    // Compose
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.activity.compose)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.lifecycle.runtime.compose)

    // Material3
    api(libs.androidx.compose.material3)

    implementation(libs.kotlinx.datetime)

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}