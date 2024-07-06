
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")
}

kapt {
    correctErrorTypes = true
}

android {
    namespace = "com.example.longdogtracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.longdogtracker"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        buildConfigField(
            "String",
            "THE_TV_DB_API_KEY",
            "\"${project.findProperty("THE_TV_DB_API_KEY")}\""
        )
        buildConfigField(
            "String",
            "GOOGLE_BOOKS_API_KEY",
            "\"${project.findProperty("GOOGLE_BOOKS_API_KEY")}\""
        )

        buildFeatures {
            buildConfig = true
        }

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
        kotlinOptions.freeCompilerArgs += "-Xinline-classes"
        kotlinOptions.freeCompilerArgs += "-Xjvm-default=all-compatibility"
        kotlinOptions.freeCompilerArgs += "-Xcontext-receivers"
    }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.8")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.activity:activity-compose")
    implementation("androidx.compose.material3:material3:1.2.1")

    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation("androidx.core:core-ktx:1.13.1")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.moshi:moshi:1.15.1")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.1")
    implementation("io.coil-kt:coil-compose:2.6.0")

    annotationProcessor("androidx.room:room-compiler:2.6.1")
    kapt("androidx.room:room-compiler:2.5.2")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    kapt("com.google.dagger:hilt-compiler:2.48.1")
    kapt("com.google.dagger:dagger-compiler:2.48.1")
    implementation("com.google.dagger:hilt-android:2.48.1")

}