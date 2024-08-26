import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.kunzhut.audioplayer"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.kunzhut.audioplayer"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName =if (gradle.startParameter.taskNames.any{it.lowercase().contains("debug")}) "1.0-debug" else "1.0-release"
        archivesName="AudioPlayer"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug{
            isMinifyEnabled = true
            isShrinkResources = true
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
}

dependencies {

    implementation(libs.androidx.appcompat)
    implementation("androidx.recyclerview:recyclerview:1.3.2")

}