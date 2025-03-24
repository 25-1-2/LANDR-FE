plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.capston"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.capston"
        minSdk = 24
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":presentation"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // glide
    implementation(libs.glide)
    implementation(libs.androidx.paging.common.android)
    annotationProcessor (libs.compiler)

    // viewpager
    implementation(libs.androidx.viewpager2)

    // indicator
    implementation (libs.relex.circleindicator)

    // kakao
//    implementation (libs.com.kakao.sdk.v2.all)

    // datastore
    implementation (libs.androidx.datastore.preferences)

    // okHttp
    implementation(libs.okhttp)
    implementation(platform(libs.okhttp.bom))
    implementation (libs.logging.interceptor)
    implementation (libs.okhttp.urlconnection)

    // retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // gson
    implementation(libs.gson)

    // splash
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // drag and drop
    implementation(libs.androidx.draganddrop)

    // coroutines
    implementation(libs.kotlinx.coroutines.android)

    //lottie
    implementation (libs.lottie)

    //room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    // bottom navigation
    implementation(libs.androidx.material)
    implementation(libs.navigation.compose)

    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.identity.googleid)
    implementation(libs.gms.play.services.auth)


}