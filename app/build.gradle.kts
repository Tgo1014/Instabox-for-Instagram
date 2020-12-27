import com.android.build.gradle.internal.dsl.BaseFlavor

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        applicationId = "tgo1014.instabox"
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    implementation("androidx.appcompat:appcompat:1.3.0-alpha02")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0-alpha2")
    implementation("androidx.security:security-crypto:1.1.0-alpha03")

    // Android KTX
    implementation("androidx.core:core-ktx:1.5.0-alpha05")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0-rc01")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.0-rc01")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.0-rc01")
    implementation("androidx.fragment:fragment-ktx:1.3.0-rc01")
    implementation("androidx.activity:activity-ktx:1.2.0-rc01")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.3.0-rc01")
    kapt("androidx.lifecycle:lifecycle-common-java8:2.3.0-rc01") // use kapt for Kotlin)

    // Material Design
    implementation("com.google.android.material:material:1.3.0-beta01")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.2")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.30.1-alpha")
    kapt("com.google.dagger:hilt-android-compiler:2.30.1-alpha")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha02")
    kapt("androidx.hilt:hilt-compiler:1.0.0-alpha02")

    // Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.11.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.11.0")

    // Timber
    implementation("com.jakewharton.timber:timber:4.7.1")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.11.0")

    // PeakAndPop
    implementation("com.github.shalskar:PeekAndPop:1.1.0")

    // Lottie
    implementation("com.airbnb.android:lottie:3.5.0")

    // Compression
    implementation("id.zelory:compressor:3.0.0")

}

// For flavors usage
fun BaseFlavor.buildConfigBoolean(name: String, value: Boolean) =
    buildConfigField("Boolean", name, value.toString())

fun BaseFlavor.buildConfigString(name: String, value: String) =
    buildConfigField("String", name, "\"$value\"")