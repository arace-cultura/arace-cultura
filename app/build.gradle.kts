plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize") // Vem da app-dev
}

android {
    namespace = "com.aracecultura.arace"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.aracecultura.arace"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }


    viewBinding {
        enable = true
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.coil)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.blurview)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.compose.animation) // Use a versão compatível com seu Compose
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.recyclerview)

    implementation(platform("io.github.jan-tennert.supabase:bom:3.1.4"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:storage-kt")

    // engine HTTP (obrigatório)
    implementation("io.ktor:ktor-client-android:3.1.2")

    // Geralmente, o bundle padrão de Compose já resolve isso:
    implementation(libs.ui)
    implementation(libs.androidx.compose.material3)
}