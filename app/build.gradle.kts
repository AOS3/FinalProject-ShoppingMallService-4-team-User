import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // ksp
    id("com.google.devtools.ksp")
    // fireBase
    id("com.google.gms.google-services")
    // Serialization
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0"
}

// api_key를 숨기기 위해 작성
val properties = Properties()
properties.load(FileInputStream(rootProject.file("local.properties")))

android {
    namespace = "com.aladin.finalproject_shoppingmallservice_4_team"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aladin.finalproject_shoppingmallservice_4_team"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // BuildConfig에 API Key 추가
        buildConfigField("String", "API_KEY", properties.getProperty("API_KEY"))
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
        buildConfig = true  // BuildConfig 기능을 활성화 하기 위해 작성
    }
    viewBinding {
        enable = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // fragment
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    // FireBase
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore:25.1.1")
    implementation("com.google.firebase:firebase-storage:21.0.1")
    implementation("com.google.firebase:firebase-auth:23.1.0")
    // CameraX
    implementation ("androidx.camera:camera-core:1.2.2")
    implementation ("androidx.camera:camera-lifecycle:1.2.2")
    implementation ("androidx.camera:camera-view:1.2.2")
    implementation ("androidx.camera:camera-camera2:1.2.2")
    // Retrofit을 사용하여 HTTP API 호출 관리.
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    // Gson을 사용하여 Retrofit의 JSON 데이터 변환.
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
    // Kotlinx Serialization을 사용하여 Retrofit의 JSON 데이터 변환.
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    // HTTP 요청/응답 로그를 출력하여 디버깅 지원.
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    // Kotlinx Serialization을 사용하여 JSON 직렬화 및 역직렬화.
    // 카카오 지도 검색 api 사용을 위한 WebView
    implementation("androidx.webkit:webkit:1.12.1")
    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")
    // MLKit
    implementation ("com.google.mlkit:barcode-scanning:17.0.3")
}