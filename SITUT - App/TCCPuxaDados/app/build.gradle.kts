plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // Para serviços do Google, como Firebase
}

android {
    namespace = "com.example.tccpuxxadados"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tccpuxxadados"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Configuração para excluir recursos conflitantes
        packagingOptions {
            resources {
                excludes += listOf("META-INF/versions/9/OSGI-INF/MANIFEST.MF")
            }
        }
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
}

dependencies {
    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database:20.0.5")
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-firestore")

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth:21.1.0")
    implementation("com.google.android.gms:play-services-auth:20.5.0")

    // AndroidX Libraries
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-analytics:21.0.0")

    // PDF Generation Library
    implementation("com.itextpdf:itext7-core:7.2.3")

    // Other Libraries
    implementation("com.koushikdutta.ion:ion:3.1.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.activity:activity:1.8.0")

    // Testing Libraries
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.firebase:firebase-client-android:2.5.2")

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    implementation ("com.google.android.material:material:1.12.0")

    implementation ("androidx.work:work-runtime:2.8.1")
    implementation("androidx.core:core:1.9.0")
    implementation("com.firebase:firebase-client-android:2.5.2")
    implementation("com.google.firebase:firebase-messaging:23.0.0")
    implementation ("androidx.concurrent:concurrent-futures:1.1.0")
    implementation ("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("com.google.firebase:firebase-storage:20.0.1")
    implementation ("com.github.bumptech.glide:glide:4.12.0")

}

// This block is unnecessary inside the 'android' block and is removed from it.
