import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android) version "2.0.0"
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.kapt") version "2.0.0"
}

android {
    namespace = "com.example.travelog"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.travelog"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // Load properties from local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { inputStream ->
                localProperties.load(inputStream)
            }
        }

        // Inject secret into BuildConfig
        val cloudinarityApiKey = localProperties.getProperty("CLOUDINARY_API_KEY", "")
        buildConfigField("String", "CLOUDINARY_API_KEY", "\"$cloudinarityApiKey\"")
        val cloudinaritySecretKey = localProperties.getProperty("CLOUDINARY_API_SECRET", "")
        buildConfigField("String", "CLOUDINARY_API_SECRET", "\"$cloudinaritySecretKey\"")



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
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.databinding.runtime)
    implementation(libs.gson)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler) // Room annotation processor

    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform(libs.firebase.bom))

    // Glide for image loading and caching
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    // Cloudinary Android SDK for uploading images
    implementation("com.cloudinary:cloudinary-android:2.0.0")

//    // (Optional) Kotlin coroutines for asynchronous work
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
}
