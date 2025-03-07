import java.util.Properties

plugins {
    id("com.google.devtools.ksp")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.pc.genzwardrobe"
    compileSdk = 35

    val localProperties = Properties().apply {
        load(project.rootProject.file("local.properties").inputStream())
    }

    defaultConfig {
        applicationId = "com.pc.genzwardrobe"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField(
            type = "String",
            name = "RAZORPAY_API_KEY",
            value = "\"${localProperties.getProperty("razorpay.api.key")}\""
        )
        buildConfigField(
            type = "String",
            name = "GEOCODING_API_KEY",
            value = "\"${localProperties.getProperty("geocoding.api.key")}\""
        )
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Lottie animation
    implementation(libs.android.lottie.compose)

    // Extended Material icons
    implementation (libs.androidx.material.icons.extended)

    // Compose constraint layout
    implementation(libs.androidx.constraintlayout.compose)

    // Navigation Animation
    implementation(libs.accompanist.navigation.animation)

    // Coil
    implementation(libs.coil.compose)

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-messaging")

    // Hilt Dependency
    implementation("com.google.dagger:hilt-android:2.48.1")
    ksp("com.google.dagger:hilt-android-compiler:2.48.1")
    ksp("androidx.hilt:hilt-compiler:1.2.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Paging
    implementation(libs.androidx.paging.compose.android)
    implementation("androidx.paging:paging-runtime:3.3.6")

    // Room Database
    implementation("androidx.room:room-runtime:2.5.1")
    ksp("androidx.room:room-compiler:2.5.1")
    implementation("androidx.room:room-ktx:2.5.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Fused Location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Preference Datastore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Razorpay Payment
//    implementation("com.razorpay:razorpay-android:1.6.22")
    implementation("com.razorpay:checkout:1.6.40")

    implementation("com.google.accompanist:accompanist-pager-indicators:0.32.0")
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}