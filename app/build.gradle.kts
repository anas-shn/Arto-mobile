import java.util.Properties

val properties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    properties.load(localPropertiesFile.inputStream())
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.arto"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.arto"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        buildConfigField(
            "String",
            "SUPABASE_URL",
            "\"${properties.getProperty("SUPABASE_URL") ?: ""}\""
        )
        buildConfigField(
            "String",
            "SUPABASE_KEY",
            "\"${properties.getProperty("SUPABASE_KEY") ?: ""}\""
        )

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
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.recyclerview)

//    retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("io.github.mckrpk:AnimatedProgressBar:1.0.0")

    // Supabase - Gunakan versi yang sama
//    implementation("io.github.jan-tennert.supabase:postgrest-kt:3.2.4")
//    implementation("io.github.jan-tennert.supabase:storage-kt:3.2.4")
//    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.6.1")
//    implementation("io.ktor:ktor-client-android:3.3.0")
//    implementation("io.ktor:ktor-client-core:3.3.0")
//    implementation("io.ktor:ktor-utils:3.3.0")
//    implementation("io.github.jan-tennert.supabase:serializer-moshi:3.2.4")
//
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
//
//    // Coroutines
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
}