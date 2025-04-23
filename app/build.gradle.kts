plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.google.gms.google.services) // تأكد من وجوده هنا
}

android {
    namespace = "com.example.capital_taxi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.capital_taxi"
        minSdk = 24
        targetSdk = 34
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

    implementation(libs.androidx.core.ktx)
    implementation (libs.accompanist.pager.indicators)
    implementation("io.coil-kt:coil-compose:2.3.0")
    implementation ("io.ktor:ktor-client-core:2.3.2")
    implementation ("io.ktor:ktor-client-okhttp:2.3.2")
    implementation ("io.ktor:ktor-client-android:2.0.0")
    implementation ("io.ktor:ktor-client-serialization:2.0.0")
    implementation ("io.ktor:ktor-client-json:2.0.0")
    implementation ("io.ktor:ktor-client-logging:2.3.3")
    implementation ("io.ktor:ktor-client-content-negotiation:2.3.3")
    implementation ("io.ktor:ktor-serialization-gson:2.3.3")
    implementation ("io.ktor:ktor-client-content-negotiation:2.3.2")
    implementation ("com.cloudinary:cloudinary-android:2.3.1")
    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation ("com.google.firebase:firebase-messaging:24.1.0")
    implementation ("com.googlecode.libphonenumber:libphonenumber:8.13.52")
    implementation ("com.hbb20:ccp:2.7.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // لتحويل JSON إلى كائنات
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3") // لعرض الـ Logs
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.0")
    implementation ("com.google.android.gms:play-services-auth:")
    implementation ("com.airbnb.android:lottie-compose:6.6.0")
    implementation ("androidx.compose.animation:animation:")
    implementation ("androidx.camera:camera-core:1.3.1")
    implementation ("org.osmdroid:osmdroid-android:6.1.10")
    implementation ("com.google.firebase:firebase-storage-ktx")
    implementation ("androidx.camera:camera-view:1.3.1")
    implementation ("androidx.camera:camera-lifecycle:1.3.1")
    implementation("com.google.maps.android:maps-compose:4.3.0")
    implementation("com.google.maps.android:maps-compose-utils:4.3.0")
    implementation("com.google.maps.android:maps-compose-widgets:4.3.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation ("androidx.compose.material3:material3:")
    implementation("com.google.firebase:firebase-auth")
    implementation ("androidx.compose.material3:material3:")
    implementation ("androidx.compose.foundation:foundation:")
    implementation ("androidx.navigation:navigation-compose:")
    implementation ("androidx.media3:media3-exoplayer:1.2.1")
    implementation ("androidx.media3:media3-ui:1.2.1")


    implementation("io.github.jan-tennert.supabase:storage-kt:1.4.0") // لـ Storage

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.firebase.auth)
    implementation(libs.transportation.consumer)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.volley)
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.firebase.database.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.media3.exoplayer)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}