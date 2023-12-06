plugins {

    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("C:\\MyKeyStore\\mykeystore.jks")
            storePassword = "aceh2712"
            keyAlias = "Dizky_Store"
            keyPassword = "aceh2712"
        }
    }
    namespace = "com.example.evnote"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.evnote"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("debug")
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
        viewBinding = true

    }


}

dependencies {



    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //      Firebase
    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    implementation("com.google.firebase:firebase-analytics")

    //    Dependencies Glide
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    //    Navigation View
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    //  Fragment
    implementation("androidx.fragment:fragment-ktx:1.5.5")

    //  Lottie Files & Coroutines
    implementation ("com.airbnb.android:lottie:3.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")


    //    Google Maps API

    implementation ("com.google.maps.android:android-maps-utils:2.3.0")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.maps.android:maps-utils-ktx:3.4.0")

    //  Drawer Layout
    implementation ("androidx.drawerlayout:drawerlayout:1.1.1")

    // ViewModel and LiveData
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")

    //    Dependencies Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // RecyclerView
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.recyclerview:recyclerview-selection:1.1.0")

    // Circle Image View
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Kotlin Coroutine
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")


    // ViewModel SavedState
    implementation ("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.3.1")


}