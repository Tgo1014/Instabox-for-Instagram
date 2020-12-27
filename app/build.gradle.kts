import com.android.build.gradle.internal.dsl.BaseFlavor

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(Versions.Sdk.compileSdk)
    defaultConfig {
        minSdkVersion(Versions.Sdk.minSdk)
        targetSdkVersion(Versions.Sdk.targetSdk)
        applicationId = Config.applicationId
        versionCode = Versions.Build.code
        versionName = Versions.Build.name
        testInstrumentationRunner = Config.testInstrumentationRunner
        buildConfigString("BASE_CLARIFAI_URL", "https://api.clarifai.com/v2/")
        buildConfigString("BASE_INSTAGRAM_URL", "https://i.instagram.com/api/v1/")
        buildConfigString("KEY", "Key")
    }
    flavorDimensions("version")
    productFlavors {
        create("dev") {
            dimension = "version"
            resConfigs(listOf("en", "xxhdpi"))
        }
        create("prod") {
            dimension = "version"
        }
    }
    signingConfigs { }
    buildTypes {
        getByName("debug") {
            extra.set("alwaysUpdateBuildId", false)
            extra.set("enableCrashlytics", false)
            extra.set("enableStability", false)
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions { jvmTarget = JavaVersion.VERSION_1_8.toString() }
    sourceSets {
        getByName("test").resources.srcDirs("src/test/resources")
        getByName("androidTest").resources.srcDirs("src/androidTest/resources")
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.21")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Tests
    testImplementation("junit:junit:4.13.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.0")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("org.mockito:mockito-core:3.5.13")
    androidTestImplementation("androidx.test.ext:junit:1.1.3-alpha02")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0-alpha02")

    // Android
    implementation("androidx.appcompat:appcompat:${Versions.Android.appCompat}")
    implementation("androidx.constraintlayout:constraintlayout:${Versions.Android.constrationLayout}")
    implementation("androidx.security:security-crypto:${Versions.Android.crypto}")

    // Android KTX
    implementation("androidx.core:core-ktx:${Versions.Ktx.core}")
    implementation("androidx.preference:preference-ktx:${Versions.Ktx.prefs}")
    implementation("androidx.fragment:fragment-ktx:${Versions.Ktx.fragment}")
    implementation("androidx.activity:activity-ktx:${Versions.Ktx.activity}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.Ktx.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.Ktx.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Versions.Ktx.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-common-java8:${Versions.Ktx.lifecycle}")
    kapt("androidx.lifecycle:lifecycle-common-java8:${Versions.Ktx.lifecycle}") // use kapt for Kotlin)

    // Material Design
    implementation("com.google.android.material:material:${Versions.material}")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Kotlin.coroutines}")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:${Versions.Retrofit.core}")
    implementation("com.squareup.retrofit2:converter-moshi:${Versions.Retrofit.core}")
    implementation("com.squareup.okhttp3:logging-interceptor:${Versions.Retrofit.interceptor}")

    // Hilt
    implementation("com.google.dagger:hilt-android:${Versions.Hilt.android}")
    kapt("com.google.dagger:hilt-android-compiler:${Versions.Hilt.android}")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:${Versions.Hilt.viewmodel}")
    kapt("androidx.hilt:hilt-compiler:${Versions.Hilt.viewmodel}")

    // Moshi
    implementation("com.squareup.moshi:moshi-kotlin:${Versions.moshi}")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}")

    // Timber
    implementation("com.jakewharton.timber:timber:${Versions.timber}")

    // Glide
    implementation("com.github.bumptech.glide:glide:${Versions.glide}")

    // PeakAndPop
    implementation("com.github.shalskar:PeekAndPop:${Versions.peekAndPop}")

    // Lottie
    implementation("com.airbnb.android:lottie:${Versions.lottie}")

    // Compression
    implementation("id.zelory:compressor:${Versions.compressor}")

}

// For flavors usage
fun BaseFlavor.buildConfigBoolean(name: String, value: Boolean) =
    buildConfigField("Boolean", name, value.toString())

fun BaseFlavor.buildConfigString(name: String, value: String) =
    buildConfigField("String", name, "\"$value\"")